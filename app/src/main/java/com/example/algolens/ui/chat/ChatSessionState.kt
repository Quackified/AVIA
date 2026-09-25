package com.example.algolens.ui.chat

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.algolens.data.chat.AiChatEngine
import com.example.algolens.data.chat.ChatHistoryRepository
import com.example.algolens.data.chat.ChatResponseProvider
import com.example.algolens.data.chat.OfflineCatalogChatProvider
import com.example.algolens.data.chat.ProjectAlgorithmRecommender
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatConversation
import com.example.algolens.model.chat.ChatMessage
import com.example.algolens.model.chat.ChatPromptStarter
import com.example.algolens.model.chat.ChatSender
import com.example.algolens.model.chat.ChatStatus
import com.example.algolens.model.chat.ProjectConstraintBrief
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Multi-conversation state manager owning persistent history, conversation switching,
 * per-conversation in-flight coroutine cancellation, and project algorithm recommendations.
 */
@Stable
class ChatSessionManager(
    private val scope: CoroutineScope,
    private val repository: ChatHistoryRepository = ChatHistoryRepository(),
    private val provider: ChatResponseProvider = OfflineCatalogChatProvider,
    private val responseDelayMs: Long = 350L,
    initialOwnerId: String = ChatHistoryRepository.DEFAULT_GUEST_OWNER
) {
    val starters: List<ChatPromptStarter> = AiChatEngine.promptStarters
    val providerLabel: String = provider.displayName
    val isOfflineProvider: Boolean = provider.isOfflineDeterministic

    var currentOwnerId by mutableStateOf(initialOwnerId)
        private set

    var conversations by mutableStateOf<List<ChatConversation>>(emptyList())
        private set

    var activeConversationId by mutableStateOf("")
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isSidebarOpen by mutableStateOf(false)
        private set

    var showProjectPlanner by mutableStateOf(false)
        private set

    private var thinkingConversationIds by mutableStateOf<Set<String>>(emptySet())
    private val inFlightJobs = mutableMapOf<String, Job>()

    init {
        loadOwnerConversations(currentOwnerId)
    }

    val activeConversation: ChatConversation
        get() = conversations.firstOrNull { it.id == activeConversationId }
            ?: conversations.firstOrNull()
            ?: createDefaultConversation(currentOwnerId)

    val messages: List<ChatMessage>
        get() = activeConversation.messages

    val inputText: String
        get() = activeConversation.draftText

    val isThinking: Boolean
        get() = thinkingConversationIds.contains(activeConversationId)

    val filteredConversations: List<ChatConversation>
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) return conversations
            return conversations.filter { conv ->
                conv.title.lowercase().contains(q) ||
                    conv.preview.lowercase().contains(q) ||
                    conv.messages.any { it.content.lowercase().contains(q) }
            }
        }

    private fun loadOwnerConversations(ownerId: String) {
        val loaded = repository.loadConversations(ownerId)
        if (loaded.isNotEmpty()) {
            conversations = loaded
            activeConversationId = loaded.first().id
        } else {
            val fresh = createDefaultConversation(ownerId)
            conversations = listOf(fresh)
            activeConversationId = fresh.id
            repository.saveConversation(fresh)
        }
    }

    fun onInputChange(newText: String) {
        updateConversation(activeConversationId, bumpTimestamp = false) { conv ->
            conv.copy(draftText = newText)
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun toggleSidebar(open: Boolean = !isSidebarOpen) {
        isSidebarOpen = open
    }

    fun toggleProjectPlanner(open: Boolean = !showProjectPlanner) {
        showProjectPlanner = open
    }

    fun createNewChat(): String {
        val existingEmpty = conversations.firstOrNull { conv ->
            conv.messages.none { it.sender == ChatSender.USER } && conv.draftText.isBlank()
        }
        if (existingEmpty != null) {
            activeConversationId = existingEmpty.id
            isSidebarOpen = false
            return existingEmpty.id
        }
        val fresh = repository.createNewConversation(currentOwnerId, "New Conversation")
        conversations = repository.loadConversations(currentOwnerId)
        activeConversationId = fresh.id
        isSidebarOpen = false
        return fresh.id
    }

    fun selectConversation(conversationId: String) {
        if (conversations.any { it.id == conversationId }) {
            activeConversationId = conversationId
            isSidebarOpen = false
        }
    }

    fun renameConversation(conversationId: String, newTitle: String) {
        val updated = repository.renameConversation(currentOwnerId, conversationId, newTitle) ?: return
        conversations = conversations.map { if (it.id == conversationId) updated else it }
    }

    fun deleteConversation(conversationId: String) {
        inFlightJobs.remove(conversationId)?.cancel()
        thinkingConversationIds = thinkingConversationIds - conversationId

        val remaining = repository.deleteConversation(currentOwnerId, conversationId)
        conversations = remaining
        if (activeConversationId == conversationId || conversations.none { it.id == activeConversationId }) {
            activeConversationId = remaining.first().id
        }
    }

    fun clearAllHistory() {
        inFlightJobs.values.forEach { it.cancel() }
        inFlightJobs.clear()
        thinkingConversationIds = emptySet()

        val freshList = repository.clearAllConversations(currentOwnerId)
        conversations = freshList
        activeConversationId = freshList.first().id
    }

    fun clearChat() {
        val targetId = activeConversationId
        inFlightJobs.remove(targetId)?.cancel()
        thinkingConversationIds = thinkingConversationIds - targetId
        updateConversation(targetId, bumpTimestamp = true) { conv ->
            conv.copy(
                title = "New Conversation",
                preview = "Offline rule & catalog engine ready",
                messages = listOf(createInitialGreeting()),
                draftText = "",
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun sendMessage(text: String = inputText) {
        val trimmed = text.trim()
        val targetId = activeConversationId
        if (trimmed.isEmpty() || thinkingConversationIds.contains(targetId)) return

        val userMessage = ChatMessage(
            sender = ChatSender.USER,
            content = trimmed,
            status = ChatStatus.COMPLETE
        )

        updateConversation(targetId, bumpTimestamp = true) { conv ->
            val updatedMessages = conv.messages + userMessage
            val isDefaultTitle = conv.title == "New Conversation" || conv.title == "Algorithm Workspace Chat"
            val updatedTitle = if (isDefaultTitle) deriveConversationTitle(trimmed) else conv.title
            conv.copy(
                title = updatedTitle,
                preview = trimmed.take(90),
                messages = updatedMessages,
                draftText = "",
                updatedAt = System.currentTimeMillis()
            )
        }

        thinkingConversationIds = thinkingConversationIds + targetId

        val job = scope.launch {
            if (responseDelayMs > 0L) {
                delay(responseDelayMs)
            }
            val assistantResponse = provider.generateResponse(trimmed, targetId)
            // Guard against stale response leaking into a deleted or cleared conversation
            if (conversations.any { it.id == targetId }) {
                updateConversation(targetId, bumpTimestamp = true) { conv ->
                    conv.copy(
                        preview = assistantResponse.content.lineSequence().firstOrNull { it.isNotBlank() }?.take(90)
                            ?: conv.preview,
                        messages = conv.messages + assistantResponse,
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }
            thinkingConversationIds = thinkingConversationIds - targetId
            inFlightJobs.remove(targetId)
        }
        inFlightJobs[targetId] = job
    }

    fun submitProjectBrief(brief: ProjectConstraintBrief) {
        val targetId = activeConversationId
        if (thinkingConversationIds.contains(targetId)) return
        showProjectPlanner = false

        val summaryLines = buildString {
            append("Project Plan Request: ${brief.projectGoal.ifBlank { brief.inputOrder.label }}")
            append("\n• Scale: ${brief.dataScale.label}")
            append("\n• Input Shape: ${brief.inputOrder.label}")
            append("\n• Graph Goal: ${brief.graphGoal.label}")
        }

        val userMessage = ChatMessage(
            sender = ChatSender.USER,
            content = summaryLines,
            status = ChatStatus.COMPLETE
        )

        updateConversation(targetId, bumpTimestamp = true) { conv ->
            val isDefaultTitle = conv.title == "New Conversation" || conv.title == "Algorithm Workspace Chat"
            val updatedTitle = if (isDefaultTitle) {
                deriveConversationTitle("Project: ${brief.projectGoal.ifBlank { brief.inputOrder.label }}")
            } else {
                conv.title
            }
            conv.copy(
                title = updatedTitle,
                preview = summaryLines.lineSequence().first().take(90),
                messages = conv.messages + userMessage,
                draftText = "",
                updatedAt = System.currentTimeMillis()
            )
        }

        thinkingConversationIds = thinkingConversationIds + targetId
        val job = scope.launch {
            if (responseDelayMs > 0L) {
                delay(responseDelayMs)
            }
            val payload = ProjectAlgorithmRecommender.recommend(brief)
            val assistantResponse = ProjectAlgorithmRecommender.toChatMessage(payload)
            if (conversations.any { it.id == targetId }) {
                updateConversation(targetId, bumpTimestamp = true) { conv ->
                    conv.copy(
                        preview = payload.projectSummary.take(90),
                        messages = conv.messages + assistantResponse,
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }
            thinkingConversationIds = thinkingConversationIds - targetId
            inFlightJobs.remove(targetId)
        }
        inFlightJobs[targetId] = job
    }

    fun sendStarter(starter: ChatPromptStarter) {
        sendMessage(starter.prompt)
    }

    fun executeAction(action: ChatAction, onLaunchVisualizer: (AlgorithmId) -> Unit) {
        when (action) {
            is ChatAction.LaunchVisualizer -> {
                onLaunchVisualizer(action.algorithmId)
            }
            is ChatAction.QueryFollowUp -> {
                sendMessage(action.prompt)
            }
        }
    }

    fun switchOwner(newOwnerId: String, migrateGuestData: Boolean = false) {
        if (newOwnerId == currentOwnerId) return
        inFlightJobs.values.forEach { it.cancel() }
        inFlightJobs.clear()
        thinkingConversationIds = emptySet()

        if (migrateGuestData && currentOwnerId == ChatHistoryRepository.DEFAULT_GUEST_OWNER &&
            newOwnerId != ChatHistoryRepository.DEFAULT_GUEST_OWNER
        ) {
            repository.migrateGuestConversationsToAccount(newOwnerId)
        }
        currentOwnerId = newOwnerId
        loadOwnerConversations(newOwnerId)
    }

    private fun updateConversation(
        conversationId: String,
        bumpTimestamp: Boolean,
        transform: (ChatConversation) -> ChatConversation
    ) {
        var updatedConv: ChatConversation? = null
        val updatedList = conversations.map { conv ->
            if (conv.id == conversationId) {
                transform(conv).also { updatedConv = it }
            } else {
                conv
            }
        }
        conversations = if (bumpTimestamp) {
            updatedList.sortedByDescending { it.updatedAt }
        } else {
            updatedList
        }
        updatedConv?.let { repository.saveConversation(it) }
    }

    companion object {
        fun deriveConversationTitle(rawQuery: String): String {
            val cleaned = rawQuery.trim().replace(Regex("\\s+"), " ")
            return if (cleaned.length <= 42) cleaned else "${cleaned.take(39)}..."
        }

        fun createDefaultConversation(
            ownerId: String = ChatHistoryRepository.DEFAULT_GUEST_OWNER
        ): ChatConversation {
            return ChatConversation(
                title = "New Conversation",
                preview = "Offline rule & catalog engine ready",
                messages = listOf(createInitialGreeting()),
                ownerId = ownerId
            )
        }

        fun createInitialGreeting(): ChatMessage {
            return ChatMessage(
                sender = ChatSender.ASSISTANT,
                content = "Greetings, Operator. I am AVIA's Offline Catalog Assistant. I operate 100% locally against AlgoLens's verified 13-algorithm engine with zero network latency.\n\n" +
                    "I can analyze Big-O complexity matrices, compare sorting and graph algorithms, recommend algorithms for your software project, generate multi-language traces (Kotlin, Python, Java, C++), and deep-link directly into interactive visualizers.\n\n" +
                    "Select a prompt starter below, open Project Planner, or query me directly.",
                suggestedFollowUps = listOf(
                    "Compare Quick Sort vs Merge Sort",
                    "Plan an algorithm for my project",
                    "Why is Binary Search O(log n)?",
                    "When to use BFS vs DFS?"
                )
            )
        }
    }
}

@Composable
fun rememberChatSessionManager(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    ownerId: String = ChatHistoryRepository.DEFAULT_GUEST_OWNER
): ChatSessionManager {
    val appContext = context.applicationContext
    return remember(appContext, scope) {
        ChatSessionManager(
            scope = scope,
            repository = ChatHistoryRepository(appContext),
            provider = OfflineCatalogChatProvider,
            initialOwnerId = ownerId
        )
    }
}
