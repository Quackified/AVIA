package com.avia.ui.visualizer

/**
 * Toggle for the 1D cell-renderer that controls whether to draw the
 * algorithm as a [CELLS] grid (with top/bottom pointers, code-trace
 * sync) or as a [BARS] chart (the swap-arc primitive).
 *
 * This is a per-algorithm view choice *within* the `LINEAR_1D`
 * visualizer family. The family dispatch in [VisualizerHost] is
 * independent of this choice.
 */
enum class ArrayViewMode {
    /** Box / cell mode with top & bottom pointers and code-trace sync. */
    CELLS,
    /** Vertical animated bar chart. */
    BARS
}
