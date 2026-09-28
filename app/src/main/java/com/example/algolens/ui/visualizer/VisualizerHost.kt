package com.example.algolens.ui.visualizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.algolens.model.AuxiliarySlot
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.AlgorithmSpec
import com.example.algolens.model.VisualizerFamily

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

            VisualizerFamily.GRAPH_2D -> {
                if (spec.graphTelemetryMode == com.example.algolens.model.GraphTelemetryMode.HEAP_ARRAY && arrayViewMode == ArrayViewMode.BARS) {
                    Linear1DCanvas(
                        spec = spec,
                        currentStep = currentStep,
                        arrayViewMode = ArrayViewMode.CELLS,
                        selectedCellIndices = selectedCellIndices,
                        challengeTargetIndices = challengeTargetIndices,
                        syncPulse = syncPulse,
                        onCellClick = onCellClick,
                        state = state
                    )
                } else {
                    GraphTreeCanvas(
                        currentStep = currentStep,
                        spec = spec,
                        state = state
                    )
                }
            }

            VisualizerFamily.BUFFER -> BufferCanvas(
                currentStep = currentStep,
                spec = spec,
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
                    cellScale = state.cellScale,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                BarVisualizer(
                    step = currentStep,
                    spec = spec,
                    cellScale = state.cellScale,
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
    spec: AlgorithmSpec,
    state: VisualizerScreenState
) {
    val algorithmId = spec.id
    val challengeNodeIds = if (state.challengeInFlight) {
        currentStep.nodes.map { it.id }.toSet()
    } else {
        emptySet()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PhaseBanner(
            step = currentStep,
            algorithmName = algorithmId.displayName,
            modifier = Modifier.fillMaxWidth()
        )
        GraphTreeVisualizer(
            step = currentStep,
            algorithmKey = algorithmId.name,
            builderEnabled = spec.builderEnabled,
            telemetryMode = spec.graphTelemetryMode,
            isCustomGraph = state.customGraph != null,
            challengeTargetNodeIds = challengeNodeIds,
            startNodeId = state.graphStartNodeId ?: state.effectiveTraversalStartNodeId,
            targetNodeId = state.graphTargetNodeId,
            onEndpointsChanged = { newStart, newTarget ->
                state.graphStartNodeId = newStart
                state.graphTargetNodeId = newTarget
            },
            onNodeClick = { nodeId ->
                state.submitNodePrediction(nodeId, algorithmId)
            },
            onGraphModified = if (spec.builderEnabled) {
                { nodes, edges ->
                    state.customGraph = if (nodes.isEmpty()) null else (nodes to edges)
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

/**
 * Buffer (Stack / Queue) canvas.
 */
@Composable
private fun BufferCanvas(
    currentStep: VisualizerStep,
    spec: AlgorithmSpec,
    state: VisualizerScreenState
) {
    val isStack = spec.isStack
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PhaseBanner(
            step = currentStep,
            algorithmName = spec.id.displayName,
            modifier = Modifier.fillMaxWidth()
        )
        BufferVisualizer(
            step = currentStep,
            isStack = isStack,
            tailSize = state.tailBufferSize(isStack),
            canAppend = state.canAppendToBuffer(isStack, currentStep.bufferCapacity),
            canRemove = state.canRemoveFromBuffer(isStack),
            onStackOp = { op -> state.appendLiveStackOp(op) },
            onQueueOp = { op -> state.appendLiveQueueOp(op) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
