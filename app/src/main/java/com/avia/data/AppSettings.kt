package com.avia.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Observable global application settings backed by [SharedPreferences].
 *
 * Holds user preferences and workspace flags across cold launches:
 *  - [preferredLanguage]: Default Trace Language (`Kotlin`, `Java`, `Python`, `C++`)
 *  - [defaultPlaybackSpeedMs]: Default Playback Speed (`1_000L` Slow, `600L` Normal, `300L` Fast —
 *    the same trio the header speed toggle cycles through)
 *  - [highContrastNodeOutlines]: High-Contrast Node Outlines & Bar Labels
 *  - [autoOpenDeckOnPlay]: Auto-Open Instrument Deck when playback starts
 *  - [showComplexityBadges]: Display inline TIME / SPACE complexity pills in VisualizerHeader
 *  - [defaultCellScale]: Default Cell Scaling (`0.7f` S, `1.0f` M, `1.25f` L)
 *  - [hapticsEnabled]: Tactile Haptic Feedback on steps/swaps
 *  - [bookmarkedAlgorithmIds]: User's saved algorithm IDs (`Set<String>`)
 */
@Stable
object AppSettings {
    private const val PREFS_NAME = "avia_app_settings"
    private const val KEY_LANGUAGE = "preferred_language"
    private const val KEY_SPEED_MS = "default_playback_speed_ms"
    private const val KEY_SPEED_SLIDER = "default_speed_slider"
    private const val KEY_HIGH_CONTRAST = "high_contrast_node_outlines"
    private const val KEY_AUTO_OPEN_DECK = "auto_open_deck_on_play"
    private const val KEY_SHOW_COMPLEXITY = "show_complexity_badges"
    private const val KEY_CELL_SCALE = "default_cell_scale"
    private const val KEY_HAPTICS = "haptics_enabled"
    private const val KEY_SOUNDS = "sounds_enabled"
    private const val KEY_SOUND_VOLUME = "sounds_volume"
    private const val KEY_BOOKMARKS = "bookmarked_algorithm_ids"

    private var prefs: SharedPreferences? = null

    private var _defaultCellScale by mutableFloatStateOf(0.7f)
    var defaultCellScale: Float
        get() = _defaultCellScale
        set(value) {
            _defaultCellScale = value
            prefs?.edit()?.putFloat(KEY_CELL_SCALE, value)?.apply()
        }

    private var _hapticsEnabled by mutableStateOf(true)
    var hapticsEnabled: Boolean
        get() = _hapticsEnabled
        set(value) {
            _hapticsEnabled = value
            prefs?.edit()?.putBoolean(KEY_HAPTICS, value)?.apply()
        }

    private var _soundEnabled by mutableStateOf(true)
    var soundEnabled: Boolean
        get() = _soundEnabled
        set(value) {
            _soundEnabled = value
            prefs?.edit()?.putBoolean(KEY_SOUNDS, value)?.apply()
        }

    private var _soundVolume by mutableFloatStateOf(1.0f)
    var soundVolume: Float
        get() = _soundVolume
        set(value) {
            val clamped = value.coerceIn(0f, 1f)
            _soundVolume = clamped
            prefs?.edit()?.putFloat(KEY_SOUND_VOLUME, clamped)?.apply()
        }

    private var _preferredLanguage by mutableStateOf(TraceLanguage.KOTLIN)
    var preferredLanguage: TraceLanguage
        get() = _preferredLanguage
        set(value) {
            _preferredLanguage = value
            prefs?.edit()?.putString(KEY_LANGUAGE, value.name)?.apply()
        }

    private var _speedSliderValue by mutableFloatStateOf(15f)
    var speedSliderValue: Float
        get() = _speedSliderValue
        set(value) {
            val clamped = value.coerceIn(0f, 100f)
            _speedSliderValue = clamped
            val mappedMs = when {
                clamped < 34f -> 1_000L
                clamped < 67f -> 600L
                else -> 300L
            }
            _defaultPlaybackSpeedMs = mappedMs
            prefs?.edit()
                ?.putFloat(KEY_SPEED_SLIDER, clamped)
                ?.putLong(KEY_SPEED_MS, mappedMs)
                ?.apply()
        }

    private var _defaultPlaybackSpeedMs by mutableLongStateOf(1_000L)
    var defaultPlaybackSpeedMs: Long
        get() = _defaultPlaybackSpeedMs
        set(value) {
            _defaultPlaybackSpeedMs = value
            _speedSliderValue = when {
                value >= 800L -> 15f
                value >= 450L -> 50f
                else -> 85f
            }
            prefs?.edit()
                ?.putLong(KEY_SPEED_MS, value)
                ?.putFloat(KEY_SPEED_SLIDER, _speedSliderValue)
                ?.apply()
        }

    private var _highContrastNodeOutlines by mutableStateOf(false)
    var highContrastNodeOutlines: Boolean
        get() = _highContrastNodeOutlines
        set(value) {
            _highContrastNodeOutlines = value
            prefs?.edit()?.putBoolean(KEY_HIGH_CONTRAST, value)?.apply()
        }

    private var _autoOpenDeckOnPlay by mutableStateOf(false)
    var autoOpenDeckOnPlay: Boolean
        get() = _autoOpenDeckOnPlay
        set(value) {
            _autoOpenDeckOnPlay = value
            prefs?.edit()?.putBoolean(KEY_AUTO_OPEN_DECK, value)?.apply()
        }

    private var _showComplexityBadges by mutableStateOf(true)
    var showComplexityBadges: Boolean
        get() = _showComplexityBadges
        set(value) {
            _showComplexityBadges = value
            prefs?.edit()?.putBoolean(KEY_SHOW_COMPLEXITY, value)?.apply()
        }

    private var currentOwnerId: String = "guest"
    private val inMemoryOwnerBookmarks = mutableMapOf<String, Set<String>>()

    private fun bookmarkKeyFor(ownerId: String): String {
        return if (ownerId == "guest") KEY_BOOKMARKS else "${KEY_BOOKMARKS}_$ownerId"
    }

    var onBookmarksChanged: ((ownerId: String, bookmarks: Set<String>) -> Unit)? = null

    private var _bookmarkedAlgorithmIds by mutableStateOf<Set<String>>(emptySet())
    var bookmarkedAlgorithmIds: Set<String>
        get() = _bookmarkedAlgorithmIds
        set(value) {
            _bookmarkedAlgorithmIds = value
            inMemoryOwnerBookmarks[currentOwnerId] = value.toSet()
            prefs?.edit()?.putStringSet(bookmarkKeyFor(currentOwnerId), value.toSet())?.apply()
            if (currentOwnerId != "guest") {
                onBookmarksChanged?.invoke(currentOwnerId, value.toSet())
            }
        }

    fun isBookmarked(algorithmId: String): Boolean =
        algorithmId in _bookmarkedAlgorithmIds

    fun toggleBookmark(algorithmId: String) {
        val updated = if (algorithmId in _bookmarkedAlgorithmIds) {
            _bookmarkedAlgorithmIds - algorithmId
        } else {
            _bookmarkedAlgorithmIds + algorithmId
        }
        bookmarkedAlgorithmIds = updated
    }

    fun switchOwner(ownerId: String, migrateGuestBookmarks: Boolean = false) {
        val normalized = ownerId.ifBlank { "guest" }
        val guestBookmarks = inMemoryOwnerBookmarks["guest"]
            ?: prefs?.getStringSet(KEY_BOOKMARKS, emptySet())?.toSet()
            ?: emptySet()

        currentOwnerId = normalized
        val existingForOwner = inMemoryOwnerBookmarks[normalized]
            ?: prefs?.getStringSet(bookmarkKeyFor(normalized), emptySet())?.toSet()
            ?: emptySet()

        val merged = if (migrateGuestBookmarks && normalized != "guest") {
            existingForOwner + guestBookmarks
        } else {
            existingForOwner
        }
        bookmarkedAlgorithmIds = merged
    }

    private const val KEY_GUEST_NAME = "guest_display_name"
    private const val KEY_GUEST_HANDLE = "guest_handle"
    private const val KEY_GUEST_ROLE = "guest_role_title"
    private const val KEY_GUEST_AVATAR_URI = "guest_avatar_uri"

    private var _guestDisplayName by mutableStateOf("Duke Ducky")
    var guestDisplayName: String
        get() = _guestDisplayName
        set(value) {
            _guestDisplayName = value
            prefs?.edit()?.putString(KEY_GUEST_NAME, value)?.apply()
        }

    private var _guestHandle by mutableStateOf("@quacky")
    var guestHandle: String
        get() = _guestHandle
        set(value) {
            _guestHandle = value
            prefs?.edit()?.putString(KEY_GUEST_HANDLE, value)?.apply()
        }

    private var _guestRoleTitle by mutableStateOf("CS Student · AVIA Workspace")
    var guestRoleTitle: String
        get() = _guestRoleTitle
        set(value) {
            _guestRoleTitle = value
            prefs?.edit { putString(KEY_GUEST_ROLE, value) }
        }

    private var _guestAvatarUri by mutableStateOf<String?>(null)
    var guestAvatarUri: String?
        get() = _guestAvatarUri
        set(value) {
            _guestAvatarUri = value
            prefs?.edit { putString(KEY_GUEST_AVATAR_URI, value) }
        }

    fun init(context: Context) {
        init(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
    }

    fun init(sharedPrefs: SharedPreferences) {
        prefs = sharedPrefs
        currentOwnerId = "guest"
        val langName = sharedPrefs.getString(KEY_LANGUAGE, TraceLanguage.KOTLIN.name)
        _preferredLanguage = TraceLanguage.entries.find { it.name == langName } ?: TraceLanguage.KOTLIN
        _defaultCellScale = sharedPrefs.getFloat(KEY_CELL_SCALE, 0.7f)
        _hapticsEnabled = sharedPrefs.getBoolean(KEY_HAPTICS, true)
        _soundEnabled = sharedPrefs.getBoolean(KEY_SOUNDS, true)
        _soundVolume = sharedPrefs.getFloat(KEY_SOUND_VOLUME, 1.0f)
        _speedSliderValue = sharedPrefs.getFloat(KEY_SPEED_SLIDER, 15f)
        _defaultPlaybackSpeedMs = sharedPrefs.getLong(KEY_SPEED_MS, 1_000L)
        _highContrastNodeOutlines = sharedPrefs.getBoolean(KEY_HIGH_CONTRAST, false)
        _autoOpenDeckOnPlay = sharedPrefs.getBoolean(KEY_AUTO_OPEN_DECK, false)
        _showComplexityBadges = sharedPrefs.getBoolean(KEY_SHOW_COMPLEXITY, true)
        _guestDisplayName = sharedPrefs.getString(KEY_GUEST_NAME, "Duke Ducky") ?: "Duke Ducky"
        _guestHandle = sharedPrefs.getString(KEY_GUEST_HANDLE, "@quacky") ?: "@quacky"
        _guestRoleTitle = sharedPrefs.getString(KEY_GUEST_ROLE, "CS Student · AVIA Workspace") ?: "CS Student · AVIA Workspace"
        _guestAvatarUri = sharedPrefs.getString(KEY_GUEST_AVATAR_URI, null)
        val loaded = sharedPrefs.getStringSet(KEY_BOOKMARKS, emptySet())?.toSet() ?: emptySet()
        inMemoryOwnerBookmarks["guest"] = loaded
        _bookmarkedAlgorithmIds = loaded
    }

    fun clearAllSavedData() {
        prefs?.edit { clear() }
        inMemoryOwnerBookmarks.clear()
        currentOwnerId = "guest"
        _preferredLanguage = TraceLanguage.KOTLIN
        _defaultCellScale = 0.7f
        _hapticsEnabled = true
        _soundEnabled = true
        _soundVolume = 1.0f
        _speedSliderValue = 15f
        _defaultPlaybackSpeedMs = 1_000L
        _highContrastNodeOutlines = false
        _autoOpenDeckOnPlay = false
        _showComplexityBadges = true
        _guestDisplayName = "Duke Ducky"
        _guestHandle = "@quacky"
        _guestRoleTitle = "CS Student · AVIA Workspace"
        _guestAvatarUri = null
        _bookmarkedAlgorithmIds = emptySet()
    }
}

