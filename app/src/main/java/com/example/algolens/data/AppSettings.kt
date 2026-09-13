package com.example.algolens.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
     * Experimental: When enabled, array visualizer dynamically sizes cells
     * including gap subtraction so all elements fit cleanly on screen without
     * clipping towards the right or causing camera jitter during comparisons/swaps.
     */
    var useAdaptiveCellVisualizer by mutableStateOf(true)

    /**
     * Default cell scale across visualizers.
     * 0.7f corresponds to "Small" (S), preventing horizontal clipping for
     * standard arrays.
     */
    var defaultCellScale by mutableFloatStateOf(0.7f)
}
