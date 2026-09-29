package com.avia.data.chat

import android.content.Context
import com.avia.model.AlgorithmId
import com.avia.model.chat.ChatAction
import com.avia.model.chat.ChatCodeSnippet
import com.avia.model.chat.ChatComplexitySnapshot
import com.avia.model.chat.ChatConversation
import com.avia.model.chat.ChatMessage
import com.avia.model.chat.ChatSender
import com.avia.model.chat.ChatStatus
import com.avia.model.chat.ProjectRecommendationPayload
import com.avia.model.chat.RecommendedAlgorithmCandidate
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * Contract for chat response providers.
 *
 * Separates the deterministic offline rule & catalog engine from any future
 * server-proxied LLM integration. Never embeds provider secrets on the client.
 */
interface ChatResponseProvider {
    val providerId: String
    val displayName: String
    val isOfflineDeterministic: Boolean

    suspend fun generateResponse(query: String, conversationId: String): ChatMessage
}

/**
 * Deterministic offline rule & catalog engine backed by [AiChatEngine] and
 * [ProjectAlgorithmRecommender]. Clearly labeled as an offline deterministic
 * knowledge engine rather than a general AI model.
 */
object OfflineCatalogChatProvider : ChatResponseProvider {
    override val providerId: String = "offline_catalog_engine"
    override val displayName: String = "OFFLINE RULE & CATALOG ENGINE"
    override val isOfflineDeterministic: Boolean = true

    override suspend fun generateResponse(query: String, conversationId: String): ChatMessage {
        return AiChatEngine.processQuery(query)
    }
}

/**
 * Structured persistence store for guest and account conversations, stored
 * separately from `AppSettings` in private app files (`avia_chat_history_<ownerId>.json`)
 * with an in-memory backing store for JVM tests and instant session access.
 */
class ChatHistoryRepository(
    private val context: Context? = null,
    val maxConversations: Int = MAX_CONVERSATIONS
) {
    private val memoryStore = mutableMapOf<String, MutableList<ChatConversation>>()

    var onConversationSaved: ((ownerId: String, conversation: ChatConversation) -> Unit)? = null
    var onConversationDeleted: ((ownerId: String, conversationId: String) -> Unit)? = null

    @Synchronized
    fun loadConversations(ownerId: String = DEFAULT_GUEST_OWNER): List<ChatConversation> {
        val normalizedOwner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val existing = memoryStore[normalizedOwner]
        if (existing != null) {
            return existing.sortedWith(
                compareByDescending<ChatConversation> { it.isPinned }
                    .thenByDescending { it.updatedAt }
            )
        }

        val loadedFromDisk = readFromDisk(normalizedOwner)
        val initialList = if (loadedFromDisk.isNotEmpty()) {
            loadedFromDisk.sortedWith(
                compareByDescending<ChatConversation> { it.isPinned }
                    .thenByDescending { it.updatedAt }
            ).take(maxConversations).toMutableList()
        } else {
            val defaultConv = createDefaultConversation(normalizedOwner)
            mutableListOf(defaultConv)
        }
        memoryStore[normalizedOwner] = initialList
        writeToDisk(normalizedOwner, initialList)
        return initialList.toList()
    }

    @Synchronized
    fun saveConversation(conversation: ChatConversation) {
        val owner = conversation.ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val list = memoryStore.getOrPut(owner) {
            readFromDisk(owner).toMutableList()
        }
        val index = list.indexOfFirst { it.id == conversation.id }
        if (index >= 0) {
            list[index] = conversation
        } else {
            list.add(0, conversation)
        }
        val trimmed = list
            .sortedWith(
                compareByDescending<ChatConversation> { it.isPinned }
                    .thenByDescending { it.updatedAt }
            )
            .take(maxConversations)
            .toMutableList()
        memoryStore[owner] = trimmed
        writeToDisk(owner, trimmed)
        onConversationSaved?.invoke(owner, conversation)
    }

    @Synchronized
    fun createNewConversation(
        ownerId: String = DEFAULT_GUEST_OWNER,
        title: String = "New Conversation"
    ): ChatConversation {
        val owner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val now = System.currentTimeMillis()
        val greeting = createDefaultGreetingMessage(now)
        val newConv = ChatConversation(
            id = UUID.randomUUID().toString(),
            ownerId = owner,
            title = title,
            preview = "Offline rule & catalog engine ready",
            createdAt = now,
            updatedAt = now,
            messages = listOf(greeting),
            draftText = ""
        )
        saveConversation(newConv)
        return newConv
    }

    @Synchronized
    fun renameConversation(
        ownerId: String = DEFAULT_GUEST_OWNER,
        conversationId: String,
        newTitle: String
    ): ChatConversation? {
        val cleanedTitle = newTitle.trim().takeIf { it.isNotEmpty() } ?: return null
        val owner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val list = loadConversations(owner).toMutableList()
        val idx = list.indexOfFirst { it.id == conversationId }
        if (idx < 0) return null
        val updated = list[idx].copy(
            title = cleanedTitle.take(60),
            updatedAt = System.currentTimeMillis()
        )
        list[idx] = updated
        memoryStore[owner] = list.sortedWith(
            compareByDescending<ChatConversation> { it.isPinned }
                .thenByDescending { it.updatedAt }
        ).toMutableList()
        writeToDisk(owner, memoryStore[owner]!!)
        onConversationSaved?.invoke(owner, updated)
        return updated
    }

    @Synchronized
    fun togglePinConversation(
        ownerId: String = DEFAULT_GUEST_OWNER,
        conversationId: String
    ): ChatConversation? {
        val owner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val list = loadConversations(owner).toMutableList()
        val idx = list.indexOfFirst { it.id == conversationId }
        if (idx < 0) return null
        val target = list[idx]
        val updated = target.copy(
            isPinned = !target.isPinned,
            updatedAt = System.currentTimeMillis()
        )
        list[idx] = updated
        val sorted = list.sortedWith(
            compareByDescending<ChatConversation> { it.isPinned }
                .thenByDescending { it.updatedAt }
        ).toMutableList()
        memoryStore[owner] = sorted
        writeToDisk(owner, sorted)
        onConversationSaved?.invoke(owner, updated)
        return updated
    }

    @Synchronized
    fun forkConversation(
        ownerId: String = DEFAULT_GUEST_OWNER,
        sourceConversationId: String,
        upToMessageId: String,
        branchTitle: String? = null
    ): ChatConversation? {
        val owner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val list = loadConversations(owner).toMutableList()
        val source = list.firstOrNull { it.id == sourceConversationId } ?: return null
        val msgIdx = source.messages.indexOfFirst { it.id == upToMessageId }
        if (msgIdx < 0) return null

        val subMessages = source.messages.take(msgIdx + 1)
        val now = System.currentTimeMillis()
        val derivedTitle = branchTitle ?: run {
            val base = source.title.removePrefix("Branch: ").take(40)
            "Branch: $base"
        }
        val forked = ChatConversation(
            id = UUID.randomUUID().toString(),
            ownerId = owner,
            title = derivedTitle,
            preview = subMessages.lastOrNull()?.content?.take(90) ?: source.preview,
            createdAt = now,
            updatedAt = now,
            messages = subMessages,
            draftText = "",
            isPinned = false
        )
        list.add(0, forked)
        val sorted = list.sortedWith(
            compareByDescending<ChatConversation> { it.isPinned }
                .thenByDescending { it.updatedAt }
        ).take(maxConversations).toMutableList()
        memoryStore[owner] = sorted
        writeToDisk(owner, sorted)
        onConversationSaved?.invoke(owner, forked)
        return forked
    }

    @Synchronized
    fun deleteConversation(
        ownerId: String = DEFAULT_GUEST_OWNER,
        conversationId: String
    ): List<ChatConversation> {
        val owner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val list = loadConversations(owner).filterNot { it.id == conversationId }.toMutableList()
        if (list.isEmpty()) {
            val defaultConv = createDefaultConversation(owner)
            list.add(defaultConv)
            onConversationSaved?.invoke(owner, defaultConv)
        }
        memoryStore[owner] = list
        writeToDisk(owner, list)
        onConversationDeleted?.invoke(owner, conversationId)
        return list.toList()
    }

    @Synchronized
    fun clearAllConversations(ownerId: String = DEFAULT_GUEST_OWNER): List<ChatConversation> {
        val owner = ownerId.ifBlank { DEFAULT_GUEST_OWNER }
        val fresh = mutableListOf(createDefaultConversation(owner))
        memoryStore[owner] = fresh
        writeToDisk(owner, fresh)
        return fresh.toList()
    }

    /**
     * Migrates guest conversations into [targetAccountId] when the user explicitly
     * consents during guest-to-account sign-in, without exposing one account's
     * history to another account.
     */
    @Synchronized
    fun migrateGuestConversationsToAccount(targetAccountId: String) {
        if (targetAccountId.isBlank() || targetAccountId == DEFAULT_GUEST_OWNER) return
        val guestConvs = loadConversations(DEFAULT_GUEST_OWNER)
        val accountConvs = loadConversations(targetAccountId).toMutableList()
        val existingIds = accountConvs.map { it.id }.toSet()

        guestConvs.forEach { guestConv ->
            val hasUserMessages = guestConv.messages.any { it.sender == ChatSender.USER }
            if (hasUserMessages && guestConv.id !in existingIds) {
                val migrated = guestConv.copy(ownerId = targetAccountId)
                accountConvs.add(migrated)
                onConversationSaved?.invoke(targetAccountId, migrated)
            }
        }
        val merged = accountConvs
            .sortedByDescending { it.updatedAt }
            .take(maxConversations)
            .toMutableList()
        memoryStore[targetAccountId] = merged
        writeToDisk(targetAccountId, merged)
    }

    /**
     * Merges conversations fetched from Firestore into the local store for [ownerId].
     * If a conversation exists locally, the version with the newer [ChatConversation.updatedAt]
     * timestamp wins.
     */
    @Synchronized
    fun mergeRemoteConversations(
        ownerId: String,
        remoteConversations: List<ChatConversation>
    ): List<ChatConversation> {
        if (ownerId.isBlank() || ownerId == DEFAULT_GUEST_OWNER || remoteConversations.isEmpty()) {
            return loadConversations(ownerId)
        }
        val localList = loadConversations(ownerId).toMutableList()
        var changed = false
        for (remote in remoteConversations) {
            val idx = localList.indexOfFirst { it.id == remote.id }
            if (idx < 0) {
                localList.add(remote.copy(ownerId = ownerId))
                changed = true
            } else {
                val local = localList[idx]
                if (remote.updatedAt > local.updatedAt) {
                    localList[idx] = remote.copy(ownerId = ownerId)
                    changed = true
                }
            }
        }
        if (changed) {
            val sorted = localList
                .sortedWith(
                    compareByDescending<ChatConversation> { it.isPinned }
                        .thenByDescending { it.updatedAt }
                )
                .take(maxConversations)
                .toMutableList()
            memoryStore[ownerId] = sorted
            writeToDisk(ownerId, sorted)
        }
        return memoryStore[ownerId] ?: localList
    }

    private fun createDefaultConversation(ownerId: String): ChatConversation {
        val now = System.currentTimeMillis()
        return ChatConversation(
            id = UUID.randomUUID().toString(),
            ownerId = ownerId,
            title = "Algorithm Workspace Chat",
            preview = "Offline rule & catalog engine ready",
            createdAt = now,
            updatedAt = now,
            messages = listOf(createDefaultGreetingMessage(now)),
            draftText = ""
        )
    }

    private fun storageFile(ownerId: String): File? {
        val safeOwner = ownerId.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val dir = context?.filesDir ?: return null
        return File(dir, "avia_chat_history_$safeOwner.json")
    }

    private fun writeToDisk(ownerId: String, conversations: List<ChatConversation>) {
        val file = storageFile(ownerId) ?: return
        runCatching {
            val root = JSONObject()
            val array = JSONArray()
            conversations.forEach { conv ->
                array.put(serializeConversation(conv))
            }
            root.put("conversations", array)
            file.writeText(root.toString())
        }
    }

    private fun readFromDisk(ownerId: String): List<ChatConversation> {
        val file = storageFile(ownerId) ?: return emptyList()
        if (!file.exists()) return emptyList()
        return runCatching {
            val root = JSONObject(file.readText())
            val array = root.optJSONArray("conversations") ?: return emptyList()
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    add(deserializeConversation(obj, ownerId))
                }
            }
        }.getOrDefault(emptyList())
    }

    companion object {
        const val DEFAULT_GUEST_OWNER = "guest"
        const val MAX_CONVERSATIONS = 50

        fun createDefaultGreetingMessage(timestamp: Long = System.currentTimeMillis()): ChatMessage {
            return ChatMessage(
                sender = ChatSender.ASSISTANT,
                timestamp = timestamp,
                content = "Greetings, Operator. I am AVIA's **Offline Rule & Catalog Engine** (100% deterministic local knowledge base — no cloud LLM or network required).\n\n" +
                    "I can analyze Big-O complexity matrices, compare algorithms from our 13-algorithm catalog, generate 4-language code traces, **recommend suitable algorithms for your project ideas**, and deep-link directly into interactive visualizers.",
                suggestedFollowUps = listOf(
                    "Plan an algorithm for my project",
                    "Compare Quick Sort vs Merge Sort",
                    "When to use BFS vs DFS?"
                )
            )
        }

        internal fun serializeConversation(conv: ChatConversation): JSONObject {
            val obj = JSONObject()
            obj.put("id", conv.id)
            obj.put("ownerId", conv.ownerId)
            obj.put("title", conv.title)
            obj.put("preview", conv.preview)
            obj.put("createdAt", conv.createdAt)
            obj.put("updatedAt", conv.updatedAt)
            obj.put("draftText", conv.draftText)
            obj.put("isPinned", conv.isPinned)

            val msgsArray = JSONArray()
            conv.messages.forEach { msg ->
                val m = JSONObject()
                m.put("id", msg.id)
                m.put("sender", msg.sender.name)
                m.put("content", msg.content)
                m.put("timestamp", msg.timestamp)
                m.put("status", msg.status.name)
                msg.referencedAlgorithm?.let { m.put("referencedAlgorithm", it.name) }

                msg.complexity?.let { c ->
                    val cObj = JSONObject()
                    cObj.put("bestCase", c.bestCase)
                    cObj.put("averageCase", c.averageCase)
                    cObj.put("worstCase", c.worstCase)
                    cObj.put("spaceComplexity", c.spaceComplexity)
                    c.isStable?.let { cObj.put("isStable", it) }
                    c.isInPlace?.let { cObj.put("isInPlace", it) }
                    m.put("complexity", cObj)
                }

                msg.codeSnippet?.let { s ->
                    val sObj = JSONObject()
                    sObj.put("language", s.language)
                    sObj.put("code", s.code)
                    m.put("codeSnippet", sObj)
                }

                msg.projectRecommendation?.let { rec ->
                    val rObj = JSONObject()
                    rObj.put("projectSummary", rec.projectSummary)
                    rec.outOfCatalogNotice?.let { rObj.put("outOfCatalogNotice", it) }
                    val candArr = JSONArray()
                    rec.candidates.forEach { cand ->
                        val candObj = JSONObject()
                        candObj.put("algorithmId", cand.algorithmId.name)
                        candObj.put("displayName", cand.displayName)
                        candObj.put("fitReason", cand.fitReason)
                        candObj.put("bestCase", cand.bestCase)
                        candObj.put("averageCase", cand.averageCase)
                        candObj.put("worstCase", cand.worstCase)
                        candObj.put("spaceComplexity", cand.spaceComplexity)
                        candObj.put("isStable", cand.isStable)
                        candObj.put("isInPlace", cand.isInPlace)
                        candObj.put("tradeOffs", cand.tradeOffs)
                        candArr.put(candObj)
                    }
                    rObj.put("candidates", candArr)
                    rObj.put("assumptions", JSONArray(rec.assumptions))
                    rObj.put("alternatives", JSONArray(rec.alternatives))
                    m.put("projectRecommendation", rObj)
                }

                val actArr = JSONArray()
                msg.actions.forEach { act ->
                    val aObj = JSONObject()
                    when (act) {
                        is ChatAction.LaunchVisualizer -> {
                            aObj.put("type", "LAUNCH")
                            aObj.put("algorithmId", act.algorithmId.name)
                            aObj.put("displayName", act.displayName)
                        }
                        is ChatAction.QueryFollowUp -> {
                            aObj.put("type", "FOLLOW_UP")
                            aObj.put("prompt", act.prompt)
                        }
                    }
                    actArr.put(aObj)
                }
                m.put("actions", actArr)
                m.put("suggestedFollowUps", JSONArray(msg.suggestedFollowUps))
                msgsArray.put(m)
            }
            obj.put("messages", msgsArray)
            return obj
        }

        internal fun deserializeConversation(obj: JSONObject, fallbackOwner: String): ChatConversation {
            val msgs = mutableListOf<ChatMessage>()
            val msgsArray = obj.optJSONArray("messages")
            if (msgsArray != null) {
                for (i in 0 until msgsArray.length()) {
                    val m = msgsArray.optJSONObject(i) ?: continue
                    val sender = runCatching { ChatSender.valueOf(m.optString("sender", "ASSISTANT")) }
                        .getOrDefault(ChatSender.ASSISTANT)
                    val status = runCatching { ChatStatus.valueOf(m.optString("status", "COMPLETE")) }
                        .getOrDefault(ChatStatus.COMPLETE)
                    val refAlgo = m.optString("referencedAlgorithm", "")
                        .takeIf { it.isNotEmpty() }
                        ?.let { name -> AlgorithmId.entries.firstOrNull { it.name == name } }

                    val complexity = m.optJSONObject("complexity")?.let { cObj ->
                        ChatComplexitySnapshot(
                            bestCase = cObj.optString("bestCase"),
                            averageCase = cObj.optString("averageCase"),
                            worstCase = cObj.optString("worstCase"),
                            spaceComplexity = cObj.optString("spaceComplexity"),
                            isStable = if (cObj.has("isStable")) cObj.optBoolean("isStable") else null,
                            isInPlace = if (cObj.has("isInPlace")) cObj.optBoolean("isInPlace") else null
                        )
                    }

                    val codeSnippet = m.optJSONObject("codeSnippet")?.let { sObj ->
                        ChatCodeSnippet(
                            language = sObj.optString("language"),
                            code = sObj.optString("code")
                        )
                    }

                    val projectRec = m.optJSONObject("projectRecommendation")?.let { rObj ->
                        val candList = mutableListOf<RecommendedAlgorithmCandidate>()
                        val candArr = rObj.optJSONArray("candidates")
                        if (candArr != null) {
                            for (cIdx in 0 until candArr.length()) {
                                val cObj = candArr.optJSONObject(cIdx) ?: continue
                                val algoId = AlgorithmId.entries.firstOrNull {
                                    it.name == cObj.optString("algorithmId")
                                } ?: continue
                                // Always re-validate against registry on load
                                val grounded = ProjectAlgorithmRecommender.validateAndGroundCandidate(
                                    id = algoId,
                                    fitReason = cObj.optString("fitReason"),
                                    tradeOffs = cObj.optString("tradeOffs")
                                )
                                if (grounded != null) candList.add(grounded)
                            }
                        }
                        val assumptions = mutableListOf<String>()
                        rObj.optJSONArray("assumptions")?.let { arr ->
                            for (k in 0 until arr.length()) assumptions.add(arr.optString(k))
                        }
                        val alternatives = mutableListOf<String>()
                        rObj.optJSONArray("alternatives")?.let { arr ->
                            for (k in 0 until arr.length()) alternatives.add(arr.optString(k))
                        }
                        ProjectRecommendationPayload(
                            projectSummary = rObj.optString("projectSummary"),
                            candidates = candList,
                            assumptions = assumptions,
                            alternatives = alternatives,
                            outOfCatalogNotice = rObj.optString("outOfCatalogNotice", "").takeIf { it.isNotEmpty() }
                        )
                    }

                    val actions = mutableListOf<ChatAction>()
                    m.optJSONArray("actions")?.let { actArr ->
                        for (aIdx in 0 until actArr.length()) {
                            val aObj = actArr.optJSONObject(aIdx) ?: continue
                            when (aObj.optString("type")) {
                                "LAUNCH" -> {
                                    val algoId = AlgorithmId.entries.firstOrNull {
                                        it.name == aObj.optString("algorithmId")
                                    }
                                    if (algoId != null) {
                                        actions.add(
                                            ChatAction.LaunchVisualizer(
                                                algorithmId = algoId,
                                                displayName = aObj.optString("displayName", algoId.displayName)
                                            )
                                        )
                                    }
                                }
                                "FOLLOW_UP" -> {
                                    actions.add(ChatAction.QueryFollowUp(aObj.optString("prompt")))
                                }
                            }
                        }
                    }

                    val followUps = mutableListOf<String>()
                    m.optJSONArray("suggestedFollowUps")?.let { fArr ->
                        for (fIdx in 0 until fArr.length()) {
                            followUps.add(fArr.optString(fIdx))
                        }
                    }

                    msgs.add(
                        ChatMessage(
                            id = m.optString("id", UUID.randomUUID().toString()),
                            sender = sender,
                            content = m.optString("content"),
                            timestamp = m.optLong("timestamp", System.currentTimeMillis()),
                            status = status,
                            referencedAlgorithm = refAlgo,
                            complexity = complexity,
                            codeSnippet = codeSnippet,
                            projectRecommendation = projectRec,
                            actions = actions,
                            suggestedFollowUps = followUps
                        )
                    )
                }
            }

            return ChatConversation(
                id = obj.optString("id", UUID.randomUUID().toString()),
                ownerId = obj.optString("ownerId", fallbackOwner),
                title = obj.optString("title", "New Conversation"),
                preview = obj.optString("preview", ""),
                createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                messages = msgs,
                draftText = obj.optString("draftText", ""),
                isPinned = obj.optBoolean("isPinned", false)
            )
        }
    }
}
