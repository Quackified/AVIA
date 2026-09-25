package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

/**
 * Interactive 2D Graph & Tree Canvas Visualizer and Builder.
 * Supports:
 * - Precision CAD / oscilloscope grid-like stage background
 * - Weighted edge pill badges (`figma-make-ref` parity)
 * - Synchronized Heap Array Strip (`2i+1` / `2i+2`) for Heap & Frontier Queue/Stack strip for BFS/DFS
 * - Direct node tap callbacks (`onNodeClick`) for Graph Challenge Mode & inspection
 */
@Composable
fun GraphTreeVisualizer(
    step: VisualizerStep,
    modifier: Modifier = Modifier,
    algorithmKey: String = "",
    challengeTargetNodeIds: Set<String> = emptySet(),
    onNodeClick: ((String) -> Unit)? = null,
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)? = null
) {
    var dynamicNodes by remember(algorithmKey) { mutableStateOf(step.nodes) }
    var dynamicEdges by remember(algorithmKey) { mutableStateOf(step.edges) }
    var hasLocalEdits by remember(algorithmKey) { mutableStateOf(false) }

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

    fun getNextNodeLabel(existing: List<GraphNodeState>): String {
        val usedLabels = existing.map { it.label }.toSet()
        for (ch in 'A'..'Z') {
            if (!usedLabels.contains(ch.toString())) {
                return ch.toString()
            }
        }
        return "N${existing.size + 1}"
    }

    val nodeScalesSnapshot = remember(dynamicNodes, poppedIds.value) {
        dynamicNodes.associate { it.id to (nodeScales[it.id]?.value ?: 1f) }
    }

    val isHeap = algorithmKey.contains("HEAP", ignoreCase = true)

    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        shellBorder = AccentGreen.copy(alpha = 0.20f),
        coreColor = CanvasBackground,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space2)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            // ── Top Interactive Builder Toolbar ──
            GraphBuilderToolbar(
                isBuilderActive = isBuilderActive,
                onToggleBuilder = { isBuilderActive = !isBuilderActive },
                dynamicNodeCount = dynamicNodes.size,
                dynamicEdgeCount = dynamicEdges.size,
                canReset = dynamicNodes.size != step.nodes.size || dynamicEdges.size != step.edges.size || hasLocalEdits,
                onReset = {
                    selectedNodeId = null
                    dragStartNode = null
                    currentDragPos = null
                    hasLocalEdits = false
                    if (onGraphModified != null) {
                        onGraphModified(emptyList(), emptyList())
                    } else {
                        dynamicNodes = step.nodes
                        dynamicEdges = step.edges
                    }
                }
            )

            // ── Main Canvas with Precision Grid + Gesture Detectors + Renderer ──
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CanvasBackground)
                    .border(
                        width = AlgoTokens.strokeThin,
                        color = AccentGreen.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(AlgoTokens.radiusSm)
                    )
            ) {
                GraphBuilderGestures(
                    dynamicNodes = dynamicNodes,
                    dynamicEdges = dynamicEdges,
                    isBuilderActive = isBuilderActive,
                    selectedNodeId = selectedNodeId,
                    dragStartNode = dragStartNode,
                    hoveredTargetNodeId = hoveredTargetNodeId,
                    onNodesChanged = { dynamicNodes = it; if (onGraphModified == null) hasLocalEdits = true },
                    onEdgesChanged = { dynamicEdges = it; if (onGraphModified == null) hasLocalEdits = true },
                    onSelectedNodeIdChanged = { selectedNodeId = it },
                    onDragStartNodeChanged = { dragStartNode = it },
                    onCurrentDragPosChanged = { currentDragPos = it },
                    onHoveredTargetNodeIdChanged = { hoveredTargetNodeId = it },
                    onGraphModified = onGraphModified,
                    getNextNodeLabel = ::getNextNodeLabel,
                    onNodeClick = onNodeClick,
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
                    challengeTargetNodeIds = challengeTargetNodeIds,
                    modifier = Modifier.fillMaxSize()
                )

                // Builder instruction banner overlay
                GraphBuilderBanner(
                    isBuilderActive = isBuilderActive,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // ── Synchronized Bottom Stage Telemetry (Heap Array Strip OR Graph Frontier Strip) ──
            if (isHeap) {
                HeapSynchronizedArrayStrip(
                    step = step,
                    nodes = dynamicNodes,
                    onCellClick = { idx -> onNodeClick?.invoke(idx.toString()) }
                )
            } else {
                GraphFrontierTelemetryStrip(
                    step = step,
                    edges = dynamicEdges,
                    algorithmKey = algorithmKey,
                    onNodeClick = onNodeClick
                )
            }
        }
    }
}

/**
 * Synchronized 1D Array Strip rendered directly beneath the 2D Binary Heap Tree.
 * Shows the exact 0-indexed array layout (`parent = (i-1)/2`, `left = 2i+1`, `right = 2i+2`)
 * in lockstep with the tree node states.
 */
@Composable
private fun HeapSynchronizedArrayStrip(
    step: VisualizerStep,
    nodes: List<GraphNodeState>,
    onCellClick: (Int) -> Unit
) {
    val arrayValues = if (step.array.isNotEmpty()) {
        step.array
    } else {
        nodes.map { it.label.toIntOrNull() ?: 0 }
    }
    val activeIdx = step.activeNodeId?.toIntOrNull()
    val parentIdx = if (activeIdx != null && activeIdx > 0) (activeIdx - 1) / 2 else null
    val leftChildIdx = if (activeIdx != null) 2 * activeIdx + 1 else null
    val rightChildIdx = if (activeIdx != null) 2 * activeIdx + 2 else null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HEAP ARRAY [0..${(arrayValues.size - 1).coerceAtLeast(0)}]",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold
            )
            val mappingText = if (activeIdx != null) {
                val lStr = if (leftChildIdx != null && leftChildIdx < arrayValues.size) "2i+1=$leftChildIdx" else "L=null"
                val rStr = if (rightChildIdx != null && rightChildIdx < arrayValues.size) "2i+2=$rightChildIdx" else "R=null"
                "i=$activeIdx • $lStr • $rStr"
            } else {
                "L = 2i+1 • R = 2i+2"
            }
            Text(
                text = mappingText,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = AlgoType.microSize
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            arrayValues.forEachIndexed { idx, value ->
                val nodeState = nodes.getOrNull(idx)?.state ?: step.elementStates[idx] ?: ElementState.IDLE
                val isCurrent = idx == activeIdx || nodeState == ElementState.ACTIVE
                val isChild = idx == leftChildIdx || idx == rightChildIdx || nodeState == ElementState.COMPARING
                val isSwapping = nodeState == ElementState.SWAPPING
                val isSorted = nodeState == ElementState.SORTED || nodeState == ElementState.VISITED

                val (bg, border, fg) = when {
                    isSwapping -> Triple(AccentRed.copy(alpha = 0.25f), AccentRed, Color.White)
                    isCurrent -> Triple(AccentGreen.copy(alpha = 0.22f), AccentGreen, AccentGreen)
                    isChild -> Triple(AccentYellow.copy(alpha = 0.18f), AccentYellow, AccentYellow)
                    isSorted -> Triple(PurpleSubtle, SecondaryPurple, PurpleGlow)
                    else -> Triple(DarkBackground, BorderSubtle, TextSecondary)
                }

                val roleBadge = when (idx) {
                    activeIdx -> "P"
                    leftChildIdx -> "L"
                    rightChildIdx -> "R"
                    parentIdx -> "↑P"
                    else -> "#$idx"
                }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(bg)
                        .border(AlgoTokens.strokeThin, border, RoundedCornerShape(AlgoTokens.radiusXxs))
                        .clickable { onCellClick(idx) }
                        .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = fg,
                        fontSize = AlgoType.labelSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = roleBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (idx == activeIdx || idx == leftChildIdx || idx == rightChildIdx) fg else TextDark,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Live Frontier (Queue / Stack / Search Path), Visited Order, and Path Weight Strip
 * rendered below the Graph/BST Canvas for `figma-make-ref` parity.
 */
@Composable
private fun GraphFrontierTelemetryStrip(
    step: VisualizerStep,
    edges: List<GraphEdgeState>,
    algorithmKey: String,
    onNodeClick: ((String) -> Unit)?
) {
    val isDfs = algorithmKey.contains("DFS", ignoreCase = true)
    val isBst = algorithmKey.contains("BST", ignoreCase = true) || algorithmKey.contains("TREE", ignoreCase = true)
    val frontierLabel = when {
        isDfs -> "STACK"
        isBst -> "TARGET"
        else -> "QUEUE"
    }

    val frontierItems: List<String> = when {
        step.buffer.isNotEmpty() -> step.buffer.map { it.value }
        else -> step.nodes
            .filter { it.state == ElementState.COMPARING || it.state == ElementState.ACTIVE || it.state == ElementState.FOUND }
            .map { it.label }
    }

    val highlightedWeightSum = edges
        .filter { it.isHighlighted && it.weight != null }
        .sumOf { it.weight ?: 0 }
    val hasWeights = edges.any { it.weight != null }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Frontier Pill Group (Queue / Stack)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
        ) {
            Text(
                text = "$frontierLabel:",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold
            )
            if (frontierItems.isEmpty()) {
                Text(
                    text = "∅",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDark,
                    fontSize = AlgoType.microSize
                )
            } else {
                frontierItems.forEach { itemLabel ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                            .background(CyanSubtle)
                            .border(AlgoTokens.strokeHairline, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusXxs))
                            .clickable(enabled = onNodeClick != null) { onNodeClick?.invoke(itemLabel) }
                            .padding(horizontal = AlgoTokens.space2, vertical = 1.dp)
                    ) {
                        Text(
                            text = itemLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Visited Order Sequence
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
        ) {
            Text(
                text = "VISITED:",
                style = MaterialTheme.typography.labelSmall,
                color = AccentGreen,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold
            )
            if (step.visitedNodeIds.isEmpty()) {
                Text(
                    text = "none",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDark,
                    fontSize = AlgoType.microSize
                )
            } else {
                Text(
                    text = step.visitedNodeIds.joinToString(" → "),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 3. Weighted Path Cost Badge (when graph has edge weights)
        if (hasWeights) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(GreenSubtle)
                    .border(AlgoTokens.strokeHairline, AccentGreen.copy(alpha = 0.45f), RoundedCornerShape(AlgoTokens.radiusXxs))
                    .padding(horizontal = AlgoTokens.space2, vertical = 1.dp)
            ) {
                Text(
                    text = "∑w = $highlightedWeightSum",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentGreen,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold
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
