package com.example.algolens

import android.content.SharedPreferences
import com.example.algolens.data.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Pure-JVM unit tests for [UserPreferences].
 * Verifies Compose state observable delegate, default values, and persistence contracts.
 */
class UserPreferencesTest {

    private val storage = mutableMapOf<String, Any?>()

    private fun createMockPreferences(): SharedPreferences {
        var editorProxy: SharedPreferences.Editor? = null

        editorProxy = Proxy.newProxyInstance(
            SharedPreferences.Editor::class.java.classLoader,
            arrayOf(SharedPreferences.Editor::class.java)
        ) { proxy, method, args ->
            when (method.name) {
                "putBoolean" -> {
                    val key = args[0] as String
                    val value = args[1] as Boolean
                    storage[key] = value
                    proxy // return Editor instance for fluent chaining
                }
                "apply", "commit" -> null
                else -> null
            }
        } as SharedPreferences.Editor

        return Proxy.newProxyInstance(
            SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)
        ) { _, method, args ->
            when (method.name) {
                "getBoolean" -> {
                    val key = args[0] as String
                    val default = args[1] as Boolean
                    storage[key] as? Boolean ?: default
                }
                "edit" -> editorProxy
                else -> null
            }
        } as SharedPreferences
    }

    @Before
    fun setUp() {
        storage.clear()
        UserPreferences.hasCompletedOnboarding = false
    }

    @Test
    fun hasCompletedOnboarding_isBackedByMutableState() {
        val delegateField = UserPreferences::class.java.getDeclaredField("hasCompletedOnboarding\$delegate")
        assertNotNull(
            "UserPreferences.hasCompletedOnboarding must be delegated through mutableStateOf for Compose reactivity",
            delegateField
        )
        assertEquals(
            "androidx.compose.runtime.MutableState",
            delegateField.type.name
        )
    }

    @Test
    fun setOnboardingCompleted_updatesStateAndPersists() {
        val mockPrefs = createMockPreferences()
        assertFalse(UserPreferences.hasCompletedOnboarding)

        UserPreferences.setOnboardingCompleted(mockPrefs, true)

        assertTrue(UserPreferences.hasCompletedOnboarding)
        assertEquals(true, storage["onboarding_completed"])
    }

    @Test
    fun init_loadsPersistedPreference() {
        val mockPrefs = createMockPreferences()
        storage["onboarding_completed"] = true

        assertFalse(UserPreferences.hasCompletedOnboarding)
        UserPreferences.init(mockPrefs)

        assertTrue(UserPreferences.hasCompletedOnboarding)
    }
}
