package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.model.AuxiliarySlot
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.AlgorithmSpec
import com.example.algolens.model.VisualizerFamily
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.TextSecondary

/**
 * Dispatcher that owns the *visualizer family* → *renderer* mapping and
 * the shared chrome (description banner + height + padding) that wraps
 * every algorithm canvas. The visualizer screen no longer branches on
 * `currentStep.renderMode` directly — it composes a [VisualizerHost]
 * with the algorithm's [AlgorithmSpec] and the current [VisualizerStep].
 *
 * Why a host? Three reasons:
 *   1. **Single source of truth for the chrome.** Stack/Queue, Graph/Tree
 *      and 1D bars all need an identical description banner. Today the
 *      screen duplicates that block per branch; the host makes it a
 *      single Box.
 *   2. **Per-family renderer selection.** Each [VisualizerFamily] maps
 *      to one renderer — and that mapping is the only place we have to
 *      touch when a new visualizer joins the family.
 *   3. **Per-family state on the spec.** Stack vs Queue, Cells vs
 *      Bars — these toggles live on the [AlgorithmSpec] (e.g.
 *      `isStack`) and the screen no longer re-derives them from the
 *      algorithm name.
 *
 * Per-algorithm gimmicks (recursion tree overlay for Merge Sort,
 * weight badges for Dijkstra, etc.) attach via the [AlgorithmSpec]'s
 * `overlays` list (Phase 4) without the host knowing about them.
 */
@Composable
fun VisualizerHost(
    spec: AlgorithmSpec,
    currentStep: VisualizerStep,
    arrayViewMode: ArrayViewMode,
    selectedCellIndices: Set<Int>,
    challengeTargetIndices: Set<Int>,
    syncPulse: State<Float>,
    onCellClick: (Int) -> Unit,
    state: VisualizerScreenState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (spec.id.family) {
            VisualizerFamily.LINEAR_1D -> Linear1DCanvas(
                spec = spec,
                currentStep = currentStep,
                arrayViewMode = arrayViewMode,
                selectedCellIndices = selectedCellIndices,
                challengeTargetIndices = challengeTargetIndices,
                syncPulse = syncPulse,
                onCellClick = onCellClick,
                state = state
            )

            VisualizerFamily.GRAPH_2D -> GraphTreeCanvas(
                currentStep = currentStep,
                algorithmId = spec.id
            )

            VisualizerFamily.BUFFER -> BufferCanvas(
                currentStep = currentStep,
                spec = spec
            )

            VisualizerFamily.MERGE_SORT -> MergeSortVisualizer(
                spec = spec,
                currentStep = currentStep,
                state = state
            )
        }

        // Per-algorithm overlays (recursion tree, weight badges, pointer
        // banner, …) float above the family renderer. They are layered
        // here so they can use `Modifier.align(...)` or
        // `Modifier.matchParentSize()` without competing with the
        // renderer for layout weight.
        spec.overlays.forEach { overlay ->
            overlay.Content(
                step = currentStep,
                state = state,
                modifier = Modifier
            )
        }
    }
}

/**
 * 1D linear cells or bars. Composes the family renderer (cells
 * or bars) with any per-algorithm structural auxiliaries declared
 * on the [spec]. Auxiliaries are composed around the family
 * renderer in a single `Column`, with `weight(1f)` reserved for
 * the renderer; the spec controls the layout, not the host.
 *
 * Defaults to CELLS; user can toggle to BARS via the
 * screen-level [ArrayViewMode] chip.
 */
@Composable
private fun Linear1DCanvas(
    spec: AlgorithmSpec,
    currentStep: VisualizerStep,
    arrayViewMode: ArrayViewMode,
    selectedCellIndices: Set<Int>,
    challengeTargetIndices: Set<Int>,
    syncPulse: State<Float>,
    onCellClick: (Int) -> Unit,
    state: VisualizerScreenState,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top auxiliaries (recursion bands, phase strip, …).
        spec.auxiliaryComponents
            .filter { it.slot == AuxiliarySlot.TOP }
            .forEach { aux ->
                key(aux.key) {
                    aux.content(
                        currentStep,
                        state,
                        Modifier.fillMaxWidth().weight(aux.weight)
                    )
                }
            }

        // Family renderer (cells or bars) — takes the remaining space.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (arrayViewMode == ArrayViewMode.CELLS) {
                CellArrayVisualizer(
                    step = currentStep,
                    spec = spec,
                    selectedCellIndices = selectedCellIndices,
                    challengeTargetIndices = challengeTargetIndices,
                    syncPulse = syncPulse,
                    onCellClick = onCellClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                BarVisualizer(
                    step = currentStep,
                    spec = spec,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Bottom auxiliaries (merge buffer row, …).
        spec.auxiliaryComponents
            .filter { it.slot == AuxiliarySlot.BOTTOM }
            .forEach { aux ->
                key(aux.key) {
                    aux.content(
                        currentStep,
                        state,
                        Modifier.fillMaxWidth().weight(aux.weight)
                    )
                }
            }
    }
}

/**
 * 2D graph / tree canvas. [GraphTreeVisualizer] still owns the
 * tap-to-build-node builder for graph algorithms.
 */
@Composable
private fun GraphTreeCanvas(
    currentStep: VisualizerStep,
    algorithmId: AlgorithmId
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DescriptionBanner(currentStep.description)
        GraphTreeVisualizer(
            step = currentStep,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

/**
 * Buffer (Stack / Queue) canvas.
 */
@Composable
private fun BufferCanvas(
    currentStep: VisualizerStep,
    spec: AlgorithmSpec
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DescriptionBanner(currentStep.description)
        BufferVisualizer(
            step = currentStep,
            isStack = spec.isStack,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )
    }
}

/**
 * Shared description banner  single source of truth for the chrome
 * that wraps every graph / buffer / bars canvas. Cell renderers own
 * their own banner inside the cell grid, so this isn't used for CELLS.
 */
@Composable
private fun DescriptionBanner(description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 13.sp,
            fontSize = 8.5.sp
        )
    }
}
