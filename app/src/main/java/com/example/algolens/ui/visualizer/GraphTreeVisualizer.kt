package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackgroundElevated

/**
 * Interactive 2D Graph & Tree Canvas Visualizer and Builder.
 * Supports:
 * - Real-time state visualization of algorithm steps (BFS, DFS, BST, Heap)
 * - User touch gestures (`detectTapGestures` to spawn nodes at tapped coordinates)
 * - User drag gestures (`detectDragGestures` to drag connecting edges between nodes)
 * - Glowing halos (AccentPink, AccentGreen, PrimaryCyan) rendered underneath interactive touch targets.
 *
 * Thin public shell — composes the following private modules in the same package:
 *  - [GraphTreeRenderer]       — pure Canvas draw (edges, drag preview, nodes)
 *  - [GraphBuilderOverlay]     — toolbar, gesture detectors, instruction banner
 *  - [GraphCanvasGeometry]     — shared coordinate transform (was duplicated 4×)
 */
@Composable
fun GraphTreeVisualizer(
    step: VisualizerStep,
    modifier: Modifier = Modifier,
    algorithmKey: String = "",
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)? = null
) {
    // Dynamic interactive node & edge states. Keyed on [algorithmKey]
    // (not [step]) so the user's custom-built nodes/edges survive
    // across step transitions and play/pause. The state is only
    // re-initialized when the user navigates to a different algorithm
    // (which gives a different `algorithmKey`), or when they tap the
    // existing "Reset Graph" button.
    var dynamicNodes by remember(algorithmKey) { mutableStateOf(step.nodes) }
    var dynamicEdges by remember(algorithmKey) { mutableStateOf(step.edges) }

    // Whether the user has diverged from the step via the builder.
    // When true, the sync effect below stops writing to dynamic*
    // so local edits are preserved across step transitions.
    // Reset to false by the "Reset Graph" toolbar action below.
    var hasLocalEdits by remember(algorithmKey) { mutableStateOf(false) }

    // Sync dynamic state from the step whenever the step pushes a
    // new graph layout (e.g. on first composition after steps are
    // generated, or when the user customizes input / scrubs to a
    // different step). The user must NOT have made any local
    // builder edits, otherwise their graph is preserved.
    LaunchedEffect(step.nodes, step.edges, hasLocalEdits) {
        if (!hasLocalEdits) {
            dynamicNodes = step.nodes
            dynamicEdges = step.edges
        }
    }

    // Gesture builder state
    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var dragStartNode by remember { mutableStateOf<GraphNodeState?>(null) }
    var currentDragPos by remember { mutableStateOf<Offset?>(null) }
    var hoveredTargetNodeId by remember { mutableStateOf<String?>(null) }
    var isBuilderActive by remember { mutableStateOf(false) }

    // ── Per-node "first visit" / "just became active" scale pop ──
    val nodeScales = remember { mutableMapOf<String, Animatable<Float, *>>() }
    val poppedIds = remember { mutableStateOf(setOf<String>()) }
    LaunchedEffect(dynamicNodes, step.activeNodeId) {
        val candidates = (dynamicNodes.map { it.id } + listOfNotNull(step.activeNodeId)).toSet()
        candidates.forEach { id ->
            if (id !in poppedIds.value) {
                val anim = nodeScales.getOrPut(id) { Animatable(1f) }
                anim.snapTo(0.55f)
                anim.animateTo(1f, tween(durationMillis = 280))
                poppedIds.value = poppedIds.value + id
            }
        }
        val stale = nodeScales.keys - candidates
        if (stale.isNotEmpty()) {
            stale.forEach { nodeScales.remove(it) }
            poppedIds.value = poppedIds.value - stale
        }
    }

    // Helper to generate next available letter label
    fun getNextNodeLabel(existing: List<GraphNodeState>): String {
        val usedLabels = existing.map { it.label }.toSet()
        for (ch in 'A'..'Z') {
            if (!usedLabels.contains(ch.toString())) {
                return ch.toString()
            }
        }
        return "N${existing.size + 1}"
    }

    // Snapshot the per-node scale (read each frame by the renderer).
    val nodeScalesSnapshot = remember(dynamicNodes, poppedIds.value) {
        dynamicNodes.associate { it.id to (nodeScales[it.id]?.value ?: 1f) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(AlgoTokens.space2)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Interactive Builder Toolbar ──
            GraphBuilderToolbar(
                isBuilderActive = isBuilderActive,
                onToggleBuilder = { isBuilderActive = !isBuilderActive },
                dynamicNodeCount = dynamicNodes.size,
                dynamicEdgeCount = dynamicEdges.size,
                canReset = dynamicNodes.size != step.nodes.size || dynamicEdges.size != step.edges.size,
                onReset = {
                    dynamicNodes = step.nodes
                    dynamicEdges = step.edges
                    selectedNodeId = null
                    dragStartNode = null
                    currentDragPos = null
                    hasLocalEdits = false
                    onGraphModified?.invoke(step.nodes, step.edges)
                }
            )

            // ── Main Canvas with Gesture Detectors + Renderer ──
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CanvasBackground)
            ) {
                GraphBuilderGestures(
                    dynamicNodes = dynamicNodes,
                    dynamicEdges = dynamicEdges,
                    isBuilderActive = isBuilderActive,
                    selectedNodeId = selectedNodeId,
                    dragStartNode = dragStartNode,
                    hoveredTargetNodeId = hoveredTargetNodeId,
                    onNodesChanged = { dynamicNodes = it; hasLocalEdits = true },
                    onEdgesChanged = { dynamicEdges = it; hasLocalEdits = true },
                    onSelectedNodeIdChanged = { selectedNodeId = it },
                    onDragStartNodeChanged = { dragStartNode = it },
                    onCurrentDragPosChanged = { currentDragPos = it },
                    onHoveredTargetNodeIdChanged = { hoveredTargetNodeId = it },
                    onGraphModified = onGraphModified,
                    getNextNodeLabel = ::getNextNodeLabel,
                    modifier = Modifier.fillMaxSize()
                )

                GraphTreeRenderer(
                    nodes = dynamicNodes,
                    edges = dynamicEdges,
                    visitedNodeIds = step.visitedNodeIds,
                    activeNodeId = step.activeNodeId,
                    selectedNodeId = selectedNodeId,
                    hoveredTargetNodeId = hoveredTargetNodeId,
                    dragStartNode = dragStartNode,
                    currentDragPos = currentDragPos,
                    nodeScales = nodeScalesSnapshot,
                    modifier = Modifier.fillMaxSize()
                )

                // Builder instruction banner overlay
                GraphBuilderBanner(
                    isBuilderActive = isBuilderActive,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun GraphTreeVisualizerPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(300.dp).padding(16.dp)) {
            GraphTreeVisualizer(
                step = VisualizerStep(
                    stepIndex = 2,
                    description = "BFS: Visiting node B from queue",
                    nodes = listOf(
                        GraphNodeState("A", "A", 30f, 30f, ElementState.VISITED),
                        GraphNodeState("B", "B", 100f, 20f, ElementState.ACTIVE),
                        GraphNodeState("C", "C", 30f, 90f, ElementState.VISITED),
                        GraphNodeState("D", "D", 100f, 90f, ElementState.IDLE),
                        GraphNodeState("E", "E", 170f, 55f, ElementState.IDLE)
                    ),
                    edges = listOf(
                        GraphEdgeState("A", "B", isHighlighted = true),
                        GraphEdgeState("A", "C", isHighlighted = true),
                        GraphEdgeState("B", "D"),
                        GraphEdgeState("C", "D"),
                        GraphEdgeState("D", "E")
                    ),
                    activeNodeId = "B",
                    visitedNodeIds = setOf("A", "C")
                )
            )
        }
    }
}
