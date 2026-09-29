package com.avia.ui.visualizer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Contract for a per-algorithm visual that floats above the family
 * renderer (cells / bars / graph / buffer) without forking it.
 *
 * The visualizer screen composes a [VisualizerHost] for each algorithm.
 * The host dispatches the family renderer (the only `when (family)`
 * block in the codebase) and then iterates the [com.avia.model.AlgorithmSpec]'s
 * `overlays` list. Each overlay's [Content] is composed on top of the
 * canvas, so the overlay is free to use `Modifier.align(...)` or
 * `Modifier.matchParentSize()` to claim space inside the host.
 *
 * Why a single-method `interface` (rather than a `fun interface`
 * SAM)? Each overlay is a singleton `object` that the registry
 * references by name (`RecursionTreeOverlay`, `WeightBadgeOverlay`).
 * The `object` provides identity, stable equality, and a free
 * `KClass.simpleName` for diagnostics. A SAM would force a lambda
 * allocation per spec, and the registry would lose the named
 * reference. Three reasons this is the right shape:
 *  1. **Cheap to add.** A new overlay is one file, one `object`. No
 *     sealed type, no registration ceremony, no `when` block.
 *  2. **Cheap to attach.** An overlay attaches via
 *     `AlgorithmSpec(overlays = listOf(MyOverlay))`. The registry
 *     and the host pick it up without further code changes.
 *  3. **Cheap to test.** The overlay is a `Composable`, so it's
 *     previewable and testable in isolation — no `VisualizerScreen`
 *     scaffolding required.
 *
 * Conventions:
 *  - **No-op when irrelevant.** An overlay that has nothing to say on
 *    the current step must render nothing (early-return), not an
 *    empty box. The recursion-tree overlay early-returns when
 *    `step.mergeBlocks.isEmpty()`; the weight-badge overlay
 *    early-returns when no edge has a weight.
 *  - **Use the supplied [modifier] as the outer modifier of the
 *    overlay's root Composable.** The host passes `Modifier` (root)
 *    or a positioned `Modifier.align(Alignment.TopEnd)` (corner
 *    overlays). The overlay should not wrap with a different shape.
 *  - **No `when (algorithm.id)` inside the overlay.** Per-algorithm
 *    branching lives in the registry. The overlay reads the step and
 *    decides what to draw from there.
 *  - **Tokenised.** Every color, dp, and animation comes from
 *    `AlgoTokens` or the existing `ui/theme/Color.kt`. No new
 *    `Color(0xFF…)` literals, no raw `*.dp` in `Modifier.padding`.
 *
 * The [state] parameter is provided for future overlays that need to
 * mutate playback (e.g. a "click the recursion-tree node to jump to
 * that step" gimmick). The current three overlays (recursion tree,
 * weight badges, pointer banner) don't read it.
 *
 * The interface lives in `ui/visualizer/` (not `model/`) because the
 * `step` parameter type ([VisualizerStep]) is defined here, alongside
 * it. `model/AlgorithmSpec.overlays` imports the interface upward —
 * a strictly additive dependency, no cycles introduced.
 */
interface VisualizerOverlay {
    @Composable
    fun Content(
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    )
}
