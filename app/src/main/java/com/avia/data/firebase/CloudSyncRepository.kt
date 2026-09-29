package com.avia.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Cloud sync data representation for user progress.
 */
data class UserProgressRecord(
    val completedQuizzes: Int = 0,
    val highestStreak: Int = 0,
    val totalScore: Int = 0,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

/**
 * Cloud sync repository interface for syncing progress and custom authored data.
 */
interface CloudSyncRepository {
    suspend fun syncProgress(userId: String, record: UserProgressRecord): Result<Unit>
    suspend fun fetchProgress(userId: String): Result<UserProgressRecord?>
}

/**
 * Production Firebase Firestore synchronization manager with graceful offline fallback.
 */
class FirebaseSyncManager(
    private val firestoreProvider: () -> FirebaseFirestore? = {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseSyncManager", "Firestore uninitialized; offline mode active", e)
            null
        }
    }
) : CloudSyncRepository {

    override suspend fun syncProgress(userId: String, record: UserProgressRecord): Result<Unit> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val data = mapOf(
                "completedQuizzes" to record.completedQuizzes,
                "highestStreak" to record.highestStreak,
                "totalScore" to record.totalScore,
                "lastUpdatedEpochMs" to record.lastUpdatedEpochMs
            )
            firestore.collection("users")
                .document(userId)
                .collection("meta")
                .document("progress")
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseSyncManager", "Sync progress failed", e)
            Result.failure(e)
        }
    }

    override suspend fun fetchProgress(userId: String): Result<UserProgressRecord?> {
        val firestore = firestoreProvider()
            ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("meta")
                .document("progress")
                .get()
                .await()
            if (!doc.exists()) {
                return Result.success(null)
            }
            val record = UserProgressRecord(
                completedQuizzes = (doc.getLong("completedQuizzes") ?: 0L).toInt(),
                highestStreak = (doc.getLong("highestStreak") ?: 0L).toInt(),
                totalScore = (doc.getLong("totalScore") ?: 0L).toInt(),
                lastUpdatedEpochMs = doc.getLong("lastUpdatedEpochMs") ?: 0L
            )
            Result.success(record)
        } catch (e: Exception) {
            Log.e("FirebaseSyncManager", "Fetch progress failed", e)
            Result.failure(e)
        }
    }
}
