package com.example.algolens.data

import android.content.Context
import android.content.SharedPreferences
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
 *  - [defaultPlaybackSpeedMs]: Default Playback Speed (`800L` Slow, `480L` Normal, `220L` Fast)
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

    private var _bookmarkedAlgorithmIds by mutableStateOf<Set<String>>(emptySet())
    var bookmarkedAlgorithmIds: Set<String>
        get() = _bookmarkedAlgorithmIds
        set(value) {
            _bookmarkedAlgorithmIds = value
            prefs?.edit()?.putStringSet(KEY_BOOKMARKS, value.toSet())?.apply()
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

    fun init(context: Context) {
        init(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
    }

    fun init(sharedPrefs: SharedPreferences) {
        prefs = sharedPrefs
        val langName = sharedPrefs.getString(KEY_LANGUAGE, TraceLanguage.KOTLIN.name)
        _preferredLanguage = TraceLanguage.entries.find { it.name == langName } ?: TraceLanguage.KOTLIN
        _defaultCellScale = sharedPrefs.getFloat(KEY_CELL_SCALE, 0.7f)
        _hapticsEnabled = sharedPrefs.getBoolean(KEY_HAPTICS, true)
        _speedSliderValue = sharedPrefs.getFloat(KEY_SPEED_SLIDER, 15f)
        _defaultPlaybackSpeedMs = sharedPrefs.getLong(KEY_SPEED_MS, 1_000L)
        _highContrastNodeOutlines = sharedPrefs.getBoolean(KEY_HIGH_CONTRAST, false)
        _autoOpenDeckOnPlay = sharedPrefs.getBoolean(KEY_AUTO_OPEN_DECK, false)
        _showComplexityBadges = sharedPrefs.getBoolean(KEY_SHOW_COMPLEXITY, true)
        _bookmarkedAlgorithmIds = sharedPrefs.getStringSet(KEY_BOOKMARKS, emptySet())?.toSet() ?: emptySet()
    }

    fun clearAllSavedData() {
        prefs?.edit()?.clear()?.apply()
        _preferredLanguage = TraceLanguage.KOTLIN
        _defaultCellScale = 0.7f
        _hapticsEnabled = true
        _speedSliderValue = 15f
        _defaultPlaybackSpeedMs = 1_000L
        _highContrastNodeOutlines = false
        _autoOpenDeckOnPlay = false
        _showComplexityBadges = true
        _bookmarkedAlgorithmIds = emptySet()
    }
}

