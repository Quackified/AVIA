package com.avia

import com.avia.data.firebase.AuthState
import com.avia.data.firebase.FirebaseAuthManager
import com.avia.data.firebase.FirebaseAnalyticsManager
import com.avia.data.firebase.FirebaseSyncManager
import com.avia.data.firebase.UserProgressRecord
import com.avia.model.AlgorithmId
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseServiceTest {

    @Test
    fun authManagerDefaultsToGuestWhenUninitialized() = runBlocking {
        val authManager = FirebaseAuthManager(authProvider = { null })
        assertEquals(AuthState.Guest, authManager.authState.value)
        assertNull(authManager.currentUserId)

        val result = authManager.signInAnonymously()
        assertTrue("Sign in should fail gracefully when uninitialized", result.isFailure)

        val signOutResult = authManager.signOut()
        assertTrue("Sign out should succeed as no-op when uninitialized", signOutResult.isSuccess)
    }

    @Test
    fun syncManagerHandlesUninitializedGracefully() = runBlocking {
        val syncManager = FirebaseSyncManager(firestoreProvider = { null })
        val syncResult = syncManager.syncProgress("test-user", UserProgressRecord(completedQuizzes = 5))
        assertTrue("Sync should fail gracefully when Firestore is uninitialized", syncResult.isFailure)

        val fetchResult = syncManager.fetchProgress("test-user")
        assertTrue("Fetch should fail gracefully when Firestore is uninitialized", fetchResult.isFailure)
    }

    @Test
    fun analyticsManagerNoOpsWhenUninitialized() {
        val analyticsManager = FirebaseAnalyticsManager(analyticsProvider = { null })
        // None of these should throw
        analyticsManager.logAlgorithmViewed(AlgorithmId.DIJKSTRA)
        analyticsManager.logPredictionCompleted(AlgorithmId.DIJKSTRA, isCorrect = true, points = 100)
        analyticsManager.logQuizCompleted("Graph Traversal", score = 3, total = 3)
    }
}
