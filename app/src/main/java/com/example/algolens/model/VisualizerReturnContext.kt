package com.example.algolens.model

import androidx.compose.runtime.Immutable

/**
 * Contextual handle allowing bidirectional return from AVIA Chat back to the live Visualizer canvas.
 * Preserves the exact algorithm and playhead step index where the user consulted the AI Tutor.
 */
@Immutable
data class VisualizerReturnContext(
    val algorithm: Algorithm,
    val stepIndex: Int
)
