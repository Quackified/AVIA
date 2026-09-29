package com.avia.data.firebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.avia.model.AlgorithmId
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * High-level analytics tracking contract for user engagement and telemetry.
 */
interface AnalyticsTracker {
    fun logAlgorithmViewed(algorithmId: AlgorithmId)
    fun logPredictionCompleted(algorithmId: AlgorithmId, isCorrect: Boolean, points: Int)
    fun logQuizCompleted(category: String, score: Int, total: Int)
}

/**
 * Production Firebase Analytics implementation with safe no-op fallback.
 */
class FirebaseAnalyticsManager(
    private val analyticsProvider: () -> FirebaseAnalytics?
) : AnalyticsTracker {

    constructor(context: Context) : this({
        try {
            FirebaseAnalytics.getInstance(context)
        } catch (e: Exception) {
            Log.w("FirebaseAnalyticsManager", "Firebase Analytics uninitialized", e)
            null
        }
    })

    override fun logAlgorithmViewed(algorithmId: AlgorithmId) {
        val analytics = analyticsProvider() ?: return
        val bundle = Bundle().apply {
            putString("algorithm_id", algorithmId.name)
            putString("algorithm_family", algorithmId.family.name)
        }
        analytics.logEvent("algorithm_viewed", bundle)
    }

    override fun logPredictionCompleted(algorithmId: AlgorithmId, isCorrect: Boolean, points: Int) {
        val analytics = analyticsProvider() ?: return
        val bundle = Bundle().apply {
            putString("algorithm_id", algorithmId.name)
            putBoolean("is_correct", isCorrect)
            putInt("points_awarded", points)
        }
        analytics.logEvent("prediction_completed", bundle)
    }

    override fun logQuizCompleted(category: String, score: Int, total: Int) {
        val analytics = analyticsProvider() ?: return
        val bundle = Bundle().apply {
            putString("category", category)
            putInt("score", score)
            putInt("total_questions", total)
        }
        analytics.logEvent("quiz_completed", bundle)
    }
}
