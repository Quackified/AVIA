package com.avia

import com.avia.data.AppSettings
import com.avia.data.SampleData
import com.avia.data.auth.GuestDataMigrationPolicy
import com.avia.data.auth.UserProfile
import com.avia.data.firebase.ChallengeStatsRecord
import com.avia.data.firebase.FirestoreSyncEngine
import com.avia.data.firebase.PracticeProgressRecord
import com.avia.data.firebase.SyncStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirestoreSyncEngineTest {

    private lateinit var syncEngine: FirestoreSyncEngine

    @Before
    fun setUp() {
        AppSettings.clearAllSavedData()
        syncEngine = FirestoreSyncEngine(firestoreProvider = { null })
    }

    @Test
    fun defaultSyncStatusIsIdle() {
        assertEquals(SyncStatus.IDLE, syncEngine.syncStatus.value)
    }

    @Test
    fun syncBookmarksFailsGracefullyWhenUninitialized() = runBlocking {
        val result = syncEngine.syncBookmarks("user_123", setOf("DIJKSTRA", "BINARY_SEARCH"))
        assertTrue("Sync should return failure when Firestore is null", result.isFailure)
        assertEquals(SyncStatus.OFFLINE, syncEngine.syncStatus.value)
    }

    @Test
    fun fetchBookmarksFailsGracefullyWhenUninitialized() = runBlocking {
        val result = syncEngine.fetchBookmarks("user_123")
        assertTrue("Fetch should return failure when Firestore is null", result.isFailure)
    }

    @Test
    fun syncPracticeAndChallengeRecordsUpdatesLocalState() = runBlocking {
        val practiceRecord = PracticeProgressRecord(
            completedQuizzes = 8,
            highestStreak = 5,
            totalScore = 650,
            solvedQuestionIds = listOf("sort_1", "graph_1")
        )
        val pResult = syncEngine.syncPracticeStats("user_123", practiceRecord)
        assertTrue(pResult.isFailure)
        assertEquals(8, syncEngine.practiceProgress.value.completedQuizzes)
        assertEquals(650, syncEngine.practiceProgress.value.totalScore)

        val challengeRecord = ChallengeStatsRecord(
            highestStreak = 7,
            totalPredictions = 20,
            correctPredictions = 18,
            totalPoints = 1800
        )
        val cResult = syncEngine.syncChallengeStats("user_123", challengeRecord)
        assertTrue(cResult.isFailure)
        assertEquals(7, syncEngine.challengeStats.value.highestStreak)
        assertEquals(1800, syncEngine.challengeStats.value.totalPoints)
    }

    @Test
    fun performInitialSyncHandlesOfflineGracefully() = runBlocking {
        val profile = UserProfile(
            uid = "user_456",
            displayName = "Offline Duck",
            email = "duck@avia.app"
        )
        syncEngine.performInitialSync(
            userId = "user_456",
            profile = profile,
            migrationPolicy = GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT
        )
        assertEquals(SyncStatus.OFFLINE, syncEngine.syncStatus.value)
    }

    @Test
    fun syncChatConversationHandlesOfflineGracefully() = runBlocking {
        val conv = com.avia.model.chat.ChatConversation(
            id = "conv_1",
            ownerId = "user_123",
            title = "Dijkstra Chat",
            messages = listOf(
                com.avia.model.chat.ChatMessage(
                    sender = com.avia.model.chat.ChatSender.USER,
                    content = "Explain Dijkstra algorithm"
                )
            )
        )
        val result = syncEngine.syncChatConversation("user_123", conv)
        assertTrue("Sync should return failure when Firestore is null", result.isFailure)
    }

    @Test
    fun deleteChatConversationHandlesOfflineGracefully() = runBlocking {
        val result = syncEngine.deleteChatConversation("user_123", "conv_1")
        assertTrue("Delete should return failure when Firestore is null", result.isFailure)
    }

    @Test
    fun fetchChatConversationsHandlesOfflineGracefully() = runBlocking {
        val result = syncEngine.fetchChatConversations("user_123")
        assertTrue("Fetch should return failure when Firestore is null", result.isFailure)
    }

    @Test
    fun chatHistoryRepository_triggersOnConversationSavedAndDeleted() {
        val repo = com.avia.data.chat.ChatHistoryRepository()
        var savedOwner: String? = null
        var savedConvId: String? = null
        var deletedOwner: String? = null
        var deletedConvId: String? = null

        repo.onConversationSaved = { owner, conv ->
            savedOwner = owner
            savedConvId = conv.id
        }
        repo.onConversationDeleted = { owner, id ->
            deletedOwner = owner
            deletedConvId = id
        }

        val conv = repo.createNewConversation("user_abc", "Test Save")
        assertEquals("user_abc", savedOwner)
        assertEquals(conv.id, savedConvId)

        repo.deleteConversation("user_abc", conv.id)
        assertEquals("user_abc", deletedOwner)
        assertEquals(conv.id, deletedConvId)
    }

    @Test
    fun chatHistoryRepository_mergesRemoteConversationsCorrectly() {
        val repo = com.avia.data.chat.ChatHistoryRepository()
        val localList = repo.loadConversations("user_xyz")
        val localId = localList.first().id

        val remoteConvNew = com.avia.model.chat.ChatConversation(
            id = "remote_1",
            ownerId = "user_xyz",
            title = "Remote Title",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val remoteUpdatedLocal = localList.first().copy(
            title = "Updated Local Title From Cloud",
            updatedAt = System.currentTimeMillis() + 10000L
        )

        val merged = repo.mergeRemoteConversations("user_xyz", listOf(remoteConvNew, remoteUpdatedLocal))
        assertTrue("Must contain newly added remote conversation", merged.any { it.id == "remote_1" })
        val updatedTarget = merged.firstOrNull { it.id == localId }
        assertEquals("Updated Local Title From Cloud", updatedTarget?.title)
    }

    @Test
    fun dijkstra_belongsToGraphTraversalAndAppearsInSampleData() {
        val dijkstra = com.avia.model.AlgorithmId.DIJKSTRA
        assertEquals("Graph Traversal", dijkstra.categoryLabel)

        val graphAlgos = SampleData.algorithms.filter { it.category.equals("Graph Traversal", ignoreCase = true) }
        assertTrue("Dijkstra must be present under Graph Traversal category", graphAlgos.any { it.id == dijkstra })
        assertEquals(3, graphAlgos.size) // BFS, DFS, DIJKSTRA
    }
}
