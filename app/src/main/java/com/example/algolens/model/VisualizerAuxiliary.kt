package com.example.algolens.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.algolens.ui.visualizer.VisualizerScreenState
import com.example.algolens.ui.visualizer.VisualizerStep

/**
 * Structural decoration that the visualizer host renders *around*
 * the family renderer. Distinct from [com.example.algolens.ui.visualizer.VisualizerOverlay]
 * (popover-style add-on stacked on top of the canvas): an
 * auxiliary reserves a slice of the canvas in the host's `Column`,
 * so it can hold dedicated rows, strips, or sub-canvases that
 * need layout weight.
 *
 * Use cases:
 *  - **Top strip** for recursion bands (Merge Sort).
 *  - **Bottom row** for the merge-target buffer (Merge Sort).
 *  - **Top row** for a phase chip strip (Merge Sort).
 *
 * Not a use case for this concept:
 *  - Tooltips, popovers, or transient decorations (use
 *    [com.example.algolens.ui.visualizer.VisualizerOverlay]).
 *  - Per-cell modifications like dimming out-of-range cells
 *    (use [AlgorithmSpec.dimOutOfRangeCells] — a flag, not a
 *    list).
 */
enum class AuxiliarySlot { TOP, BOTTOM }

/**
 * A structural auxiliary the host composes inside its `Column`
 * scaffold. Each auxiliary declares where it goes ([slot]) and
 * how much of the available height it takes ([weight], passed to
 * `Modifier.weight(...)`). The host composes them in the order
 * they appear in [AlgorithmSpec.auxiliaryComponents], so the spec
 * controls the layout, not the host.
 *
 * [key] is a stable identity used for Compose's `key(...)`
 * modifier on the inner `remember { ... }` scope, so the
 * auxiliary's internal state survives recompositions. Two
 * auxiliaries with the same key in one spec is a programming
 * error; use different keys.
 */
data class RegionAuxiliary(
    val slot: AuxiliarySlot,
    val weight: Float,
    val key: String,
    val content: @Composable (
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) -> Unit,
)
