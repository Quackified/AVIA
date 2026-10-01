package com.avia.data.firebase

import android.util.Log
import com.avia.data.AppSettings
import com.avia.data.auth.GuestDataMigrationPolicy
import com.avia.data.auth.UserProfile
import com.avia.model.AlgorithmId
import com.avia.model.chat.ChatConversation
import com.avia.model.chat.ChatMessage
import com.avia.model.chat.ChatSender
import com.avia.model.chat.ChatStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Observable synchronization state of cloud Firestore connection.
 */
enum class SyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE,
    ERROR
}

/**
 * Practice & quiz progress data model stored in Firestore.
 */
data class PracticeProgressRecord(
    val completedQuizzes: Int = 0,
    val highestStreak: Int = 0,
    val totalScore: Int = 0,
    val solvedQuestionIds: List<String> = emptyList(),
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

/**
 * Predict Step Arena & challenge telemetry record stored in Firestore.
 */
data class ChallengeStatsRecord(
    val highestStreak: Int = 0,
    val totalPredictions: Int = 0,
    val correctPredictions: Int = 0,
    val totalPoints: Int = 0,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

/**
 * Production Cloud Firestore sync engine providing offline-first persistence,
 * automatic local caching, and guest-to-account data migration for AVIA.
 */
class FirestoreSyncEngine(
    private val firestoreProvider: () -> FirebaseFirestore? = {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Firestore uninitialized; offline mode active", e)
            null
        }
    },
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _practiceProgress = MutableStateFlow(PracticeProgressRecord())
    val practiceProgress: StateFlow<PracticeProgressRecord> = _practiceProgress.asStateFlow()

    private val _challengeStats = MutableStateFlow(ChallengeStatsRecord())
    val challengeStats: StateFlow<ChallengeStatsRecord> = _challengeStats.asStateFlow()

    fun syncBookmarksAsync(userId: String, bookmarkIds: Set<String>) {
        scope.launch {
            syncBookmarks(userId, bookmarkIds)
        }
    }

    suspend fun syncBookmarks(userId: String, bookmarkIds: Set<String>): Result<Unit> {
        val firestore = firestoreProvider()
        if (firestore == null) {
            _syncStatus.value = SyncStatus.OFFLINE
            return Result.failure(IllegalStateException("Firestore unavailable"))
        }
        _syncStatus.value = SyncStatus.SYNCING
        return try {
            val data = mapOf(
                "algorithmIds" to bookmarkIds.toList(),
                "lastUpdatedEpochMs" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .collection("meta")
                .document("bookmarks")
                .set(data, SetOptions.merge())
                .await()
            _syncStatus.value = SyncStatus.SYNCED
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to sync bookmarks to Firestore (cached offline)", e)
            _syncStatus.value = SyncStatus.OFFLINE
            Result.failure(e)
        }
    }

    suspend fun fetchBookmarks(userId: String): Result<Set<String>> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("meta")
                .document("bookmarks")
                .get()
                .await()
            if (!doc.exists()) {
                return Result.success(emptySet())
            }
            @Suppress("UNCHECKED_CAST")
            val ids = (doc.get("algorithmIds") as? List<String>)?.toSet() ?: emptySet()
            Result.success(ids)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to fetch bookmarks (offline fallback)", e)
            Result.failure(e)
        }
    }

    suspend fun syncProfile(userId: String, profile: UserProfile): Result<Unit> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val data = mapOf(
                "uid" to userId,
                "displayName" to profile.displayName,
                "handle" to profile.handle,
                "roleTitle" to profile.roleTitle,
                "email" to profile.email,
                "avatarUri" to profile.avatarUri,
                "lastSyncEpochMs" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to sync profile to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun syncPracticeStats(userId: String, record: PracticeProgressRecord): Result<Unit> {
        _practiceProgress.value = record
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val data = mapOf(
                "completedQuizzes" to record.completedQuizzes,
                "highestStreak" to record.highestStreak,
                "totalScore" to record.totalScore,
                "solvedQuestionIds" to record.solvedQuestionIds,
                "lastUpdatedEpochMs" to record.lastUpdatedEpochMs
            )
            firestore.collection("users")
                .document(userId)
                .collection("meta")
                .document("practice_stats")
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to sync practice stats to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun syncChallengeStats(userId: String, record: ChallengeStatsRecord): Result<Unit> {
        _challengeStats.value = record
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val data = mapOf(
                "highestStreak" to record.highestStreak,
                "totalPredictions" to record.totalPredictions,
                "correctPredictions" to record.correctPredictions,
                "totalPoints" to record.totalPoints,
                "lastUpdatedEpochMs" to record.lastUpdatedEpochMs
            )
            firestore.collection("users")
                .document(userId)
                .collection("meta")
                .document("challenge_stats")
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to sync challenge stats to Firestore", e)
            Result.failure(e)
        }
    }

    fun syncChatConversationAsync(userId: String, conversation: ChatConversation) {
        scope.launch {
            syncChatConversation(userId, conversation)
        }
    }

    fun deleteChatConversationAsync(userId: String, conversationId: String) {
        scope.launch {
            deleteChatConversation(userId, conversationId)
        }
    }

    suspend fun syncChatConversation(userId: String, conversation: ChatConversation): Result<Unit> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val messageList = conversation.messages.map { msg ->
                val map = mutableMapOf<String, Any>(
                    "id" to msg.id,
                    "sender" to msg.sender.name,
                    "content" to msg.content,
                    "timestamp" to msg.timestamp,
                    "status" to msg.status.name,
                    "suggestedFollowUps" to msg.suggestedFollowUps
                )
                msg.referencedAlgorithm?.let { map["referencedAlgorithm"] = it.name }
                map
            }

            val data = mapOf(
                "id" to conversation.id,
                "ownerId" to userId,
                "title" to conversation.title,
                "preview" to conversation.preview,
                "createdAt" to conversation.createdAt,
                "updatedAt" to conversation.updatedAt,
                "isPinned" to conversation.isPinned,
                "messages" to messageList,
                "lastSyncEpochMs" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("chat_conversations")
                .document(conversation.id)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to sync chat conversation to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun deleteChatConversation(userId: String, conversationId: String): Result<Unit> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("chat_conversations")
                .document(conversationId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to delete chat conversation from Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun fetchChatConversations(userId: String): Result<List<ChatConversation>> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("chat_conversations")
                .get()
                .await()
            val list = mutableListOf<ChatConversation>()
            for (doc in snapshot.documents) {
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: "Conversation"
                val preview = doc.getString("preview") ?: ""
                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                val isPinned = doc.getBoolean("isPinned") ?: false

                @Suppress("UNCHECKED_CAST")
                val rawMessages = doc.get("messages") as? List<Map<String, Any>> ?: emptyList()
                val messages = rawMessages.mapNotNull { m ->
                    val mId = m["id"] as? String ?: UUID.randomUUID().toString()
                    val senderStr = m["sender"] as? String ?: return@mapNotNull null
                    val sender = runCatching { ChatSender.valueOf(senderStr) }.getOrDefault(ChatSender.ASSISTANT)
                    val content = m["content"] as? String ?: ""
                    val timestamp = (m["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                    val statusStr = m["status"] as? String ?: ChatStatus.COMPLETE.name
                    val status = runCatching { ChatStatus.valueOf(statusStr) }.getOrDefault(ChatStatus.COMPLETE)
                    val refAlgoStr = m["referencedAlgorithm"] as? String
                    val refAlgo = refAlgoStr?.let { runCatching { AlgorithmId.valueOf(it) }.getOrNull() }
                    @Suppress("UNCHECKED_CAST")
                    val followUps = (m["suggestedFollowUps"] as? List<String>) ?: emptyList()

                    ChatMessage(
                        id = mId,
                        sender = sender,
                        content = content,
                        timestamp = timestamp,
                        status = status,
                        referencedAlgorithm = refAlgo,
                        suggestedFollowUps = followUps
                    )
                }

                list.add(
                    ChatConversation(
                        id = id,
                        ownerId = userId,
                        title = title,
                        preview = preview,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        isPinned = isPinned,
                        messages = messages
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Failed to fetch chat conversations", e)
            Result.failure(e)
        }
    }

    suspend fun performInitialSync(
        userId: String,
        profile: UserProfile,
        migrationPolicy: GuestDataMigrationPolicy
    ) {
        val firestore = firestoreProvider()
        if (firestore == null) {
            _syncStatus.value = SyncStatus.OFFLINE
            return
        }
        _syncStatus.value = SyncStatus.SYNCING
        try {
            // 1. Sync Profile
            syncProfile(userId, profile)

            // 2. Fetch and merge bookmarks
            val remoteBookmarksResult = fetchBookmarks(userId)
            val remoteBookmarks = remoteBookmarksResult.getOrDefault(emptySet())
            val localBookmarks = AppSettings.bookmarkedAlgorithmIds

            val finalBookmarks = if (migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT) {
                localBookmarks + remoteBookmarks
            } else {
                remoteBookmarks
            }

            AppSettings.bookmarkedAlgorithmIds = finalBookmarks
            if (finalBookmarks != remoteBookmarks) {
                syncBookmarks(userId, finalBookmarks)
            }

            // 3. Fetch practice and challenge stats
            runCatching {
                val pDoc = firestore.collection("users")
                    .document(userId)
                    .collection("meta")
                    .document("practice_stats")
                    .get()
                    .await()
                if (pDoc.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val pRecord = PracticeProgressRecord(
                        completedQuizzes = (pDoc.getLong("completedQuizzes") ?: 0L).toInt(),
                        highestStreak = (pDoc.getLong("highestStreak") ?: 0L).toInt(),
                        totalScore = (pDoc.getLong("totalScore") ?: 0L).toInt(),
                        solvedQuestionIds = (pDoc.get("solvedQuestionIds") as? List<String>) ?: emptyList(),
                        lastUpdatedEpochMs = pDoc.getLong("lastUpdatedEpochMs") ?: System.currentTimeMillis()
                    )
                    _practiceProgress.value = pRecord
                }

                val cDoc = firestore.collection("users")
                    .document(userId)
                    .collection("meta")
                    .document("challenge_stats")
                    .get()
                    .await()
                if (cDoc.exists()) {
                    val cRecord = ChallengeStatsRecord(
                        highestStreak = (cDoc.getLong("highestStreak") ?: 0L).toInt(),
                        totalPredictions = (cDoc.getLong("totalPredictions") ?: 0L).toInt(),
                        correctPredictions = (cDoc.getLong("correctPredictions") ?: 0L).toInt(),
                        totalPoints = (cDoc.getLong("totalPoints") ?: 0L).toInt(),
                        lastUpdatedEpochMs = cDoc.getLong("lastUpdatedEpochMs") ?: System.currentTimeMillis()
                    )
                    _challengeStats.value = cRecord
                }
            }

            _syncStatus.value = SyncStatus.SYNCED
        } catch (e: Exception) {
            Log.w("FirestoreSyncEngine", "Initial sync error (offline fallback)", e)
            _syncStatus.value = SyncStatus.OFFLINE
        }
    }
}
