package com.example.algolens.data

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Persistent application-level user preferences backed by SharedPreferences.
 *
 * Tracks whether the first-run onboarding tour has been completed.
 * State updates are observable by Jetpack Compose.
 */
@Stable
object UserPreferences {
    private const val PREFS_NAME = "avia_user_preferences"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    /**
     * Whether the user has completed or skipped the first-run onboarding tour.
     * Backed by [mutableStateOf] so Composables recompose on state mutation.
     */
    var hasCompletedOnboarding by mutableStateOf(false)
        internal set

    /**
     * Initialize preferences from disk on app cold start.
     */
    fun init(context: Context) {
        init(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
    }

    /**
     * Initialize preferences directly from [SharedPreferences].
     */
    fun init(prefs: android.content.SharedPreferences) {
        hasCompletedOnboarding = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Update the onboarding completion flag and persist to disk.
     */
    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        setOnboardingCompleted(
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE),
            completed
        )
    }

    /**
     * Update the onboarding completion flag on the provided [SharedPreferences].
     */
    fun setOnboardingCompleted(prefs: android.content.SharedPreferences, completed: Boolean) {
        hasCompletedOnboarding = completed
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
}
