package com.example.algolens.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

/**
 * Observable global application settings.
 *
 * Holds user preferences and experimental flags, including the adaptive
 * zero-clipping cell visualizer and default cell scaling.
 */
@Stable
object AppSettings {
    /**
     * Default cell scale across visualizers.
     * 0.7f corresponds to "Small" (S), preventing horizontal clipping for
     * standard arrays.
     */
    var defaultCellScale by mutableFloatStateOf(0.7f)

    /**
     * Whether tactile haptic feedback is enabled for step ticks, swaps,
     * and the completion celebration wave.
     */
    var hapticsEnabled by androidx.compose.runtime.mutableStateOf(true)
}
