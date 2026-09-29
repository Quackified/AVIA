package com.avia.ui.visualizer

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.avia.data.GraphSearch
import com.avia.model.GraphCapabilityProfile
import com.avia.model.GraphTelemetryMode
import com.avia.model.GraphTool
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.GreenSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary

/**
 * Interactive 2D Graph & Tree Canvas Visualizer and Builder.
 * Delegates canvas operations to [GraphCanvasEngine] shared between embedded
 * and fullscreen edge-to-edge Dialog surfaces.
 */
@Composable
fun GraphTreeVisualizer(
    step: VisualizerStep,
    modifier: Modifier = Modifier,
    algorithmKey: String = "",
    builderEnabled: Boolean = false,
    profile: GraphCapabilityProfile? = null,
    telemetryMode: GraphTelemetryMode = GraphTelemetryMode.NONE,
    isCustomGraph: Boolean = false,
    userCustomCoordinates: Map<String, Offset> = emptyMap(),
    onNodeMoved: ((String, Offset) -> Unit)? = null,
    challengeTargetNodeIds: Set<String> = emptySet(),
    startNodeId: String? = null,
    targetNodeId: String? = null,
    onEndpointsChanged: ((String?, String?) -> Unit)? = null,
    onNodeClick: ((String) -> Unit)? = null,
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)? = null,
    onBstInsertKey: ((Int) -> Unit)? = null,
    onBstDeleteNode: ((String) -> Unit)? = null,
    onHeapPushValue: ((Int) -> Unit)? = null,
    onHeapExtractRoot: (() -> Unit)? = null,
    onHeapRemoveTail: (() -> Unit)? = null,
    onResetGraph: (() -> Unit)? = null,
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onNodeDragStarted: () -> Unit = {},
    onNodeDragFinished: () -> Unit = {},
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false
) {
    val resolvedProfile = remember(profile, algorithmKey, builderEnabled) {
        if (profile != null) return@remember profile
        when {
            algorithmKey.contains("DIJKSTRA", ignoreCase = true) ->
                GraphCapabilityProfile.dijkstraProfile()
            algorithmKey.contains("BFS", ignoreCase = true) || algorithmKey.contains("DFS", ignoreCase = true) ->
                GraphCapabilityProfile.bfsDfsProfile()
            algorithmKey.contains("BST", ignoreCase = true) || algorithmKey.contains("BINARY_SEARCH_TREE", ignoreCase = true) ->
                GraphCapabilityProfile.bstProfile()
            algorithmKey.contains("HEAP", ignoreCase = true) ->
                GraphCapabilityProfile.heapProfile()
            builderEnabled ->
                GraphCapabilityProfile.dijkstraProfile()
            else ->
                GraphCapabilityProfile.readOnlyProfile()
        }
    }

    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var isBuilderActive by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var activeTool by remember { mutableStateOf(GraphTool.MOVE) }

    val resolvedMode = remember(telemetryMode, algorithmKey) {
        if (telemetryMode != GraphTelemetryMode.NONE) {
            telemetryMode
        } else {
            com.avia.model.AlgorithmId.entries
                .firstOrNull { it.name.equals(algorithmKey, ignoreCase = true) || it.displayName.equals(algorithmKey, ignoreCase = true) }
                ?.let { com.avia.data.AlgorithmRegistry.specFor(it)?.graphTelemetryMode }
                ?: GraphTelemetryMode.BFS_QUEUE
        }
    }

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
            // ── Top Status Header (only if editing tools are available) ──
            if (resolvedProfile.allowedTools.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(if (isBuilderActive) CyanSubtle else CanvasBackground)
                            .border(
                                AlgoTokens.strokeThin,
                                if (isBuilderActive) PrimaryCyan else BorderSubtle,
                                RoundedCornerShape(AlgoTokens.radiusXs)
                            )
                            .clickable { isBuilderActive = !isBuilderActive }
                            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Tap,
                                contentDescription = null,
                                tint = if (isBuilderActive) PrimaryCyan else TextMuted,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                            Text(
                                text = "Interactive Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isBuilderActive) PrimaryCyan else TextMuted,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Text(
                        text = "${step.nodes.size} nodes • ${step.edges.size} edges",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDark,
                        fontSize = AlgoType.microSize,
                        maxLines = 1
                    )
                }
            }

            // ── Embedded Stage Canvas ──
            GraphCanvasEngine(
                nodes = step.nodes,
                edges = step.edges,
                profile = resolvedProfile,
                isBuilderActive = isBuilderActive,
                activeTool = activeTool,
                onToolSelected = { activeTool = it },
                selectedNodeId = selectedNodeId,
                onSelectedNodeIdChanged = { selectedNodeId = it },
                userCustomCoordinates = userCustomCoordinates,
                onNodeMoved = { id, coords -> onNodeMoved?.invoke(id, coords) },
                visitedNodeIds = step.visitedNodeIds,
                activeNodeId = step.activeNodeId,
                challengeTargetNodeIds = challengeTargetNodeIds,
                startNodeId = startNodeId,
                targetNodeId = targetNodeId,
                onEndpointsChanged = onEndpointsChanged,
                onNodeClicked = onNodeClick,
                onGraphModified = onGraphModified,
                onBstInsertKey = onBstInsertKey,
                onBstDeleteNode = onBstDeleteNode,
                onHeapPushValue = onHeapPushValue,
                onHeapExtractRoot = onHeapExtractRoot,
                onHeapRemoveTail = onHeapRemoveTail,
                canReset = isCustomGraph || userCustomCoordinates.isNotEmpty(),
                onResetGraph = {
                    selectedNodeId = null
                    onResetGraph?.invoke()
                },
                canClear = isBuilderActive && resolvedProfile.canDeleteElements && step.nodes.isNotEmpty(),
                onClearCanvas = {
                    selectedNodeId = null
                    onEndpointsChanged?.invoke(null, null)
                    onGraphModified?.invoke(emptyList(), emptyList())
                },
                canUndo = canUndo,
                canRedo = canRedo,
                onUndo = onUndo,
                onRedo = onRedo,
                onNodeDragStarted = onNodeDragStarted,
                onNodeDragFinished = onNodeDragFinished,
                isFullscreen = isFullscreen,
                onToggleFullscreen = { isFullscreen = !isFullscreen },
                currentStep = step,
                playbackSpeedMs = playbackSpeedMs,
                isScrubbing = isScrubbing,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // ── Synchronized Bottom Stage Telemetry ──
            if (resolvedMode == GraphTelemetryMode.HEAP_ARRAY) {
                HeapSynchronizedArrayStrip(
                    step = step,
                    nodes = step.nodes,
                    onCellClick = { idx -> onNodeClick?.invoke(idx.toString()) }
                )
            } else {
                GraphFrontierTelemetryStrip(
                    step = step,
                    edges = step.edges,
                    telemetryMode = resolvedMode,
                    startNodeId = startNodeId,
                    targetNodeId = targetNodeId,
                    onNodeClick = onNodeClick
                )
            }
        }
    }

    // ── Fullscreen Interactive Canvas Overlay ──
    if (isFullscreen && resolvedProfile.supportsFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val view = LocalView.current
            DisposableEffect(view) {
                var parent = view.parent
                var dialogWindow: android.view.Window? = null
                while (parent != null) {
                    if (parent is DialogWindowProvider) {
                        dialogWindow = parent.window
                        break
                    }
                    parent = parent.parent
                }
                dialogWindow?.let { win ->
                    win.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    win.setBackgroundDrawableResource(android.R.color.transparent)
                }
                onDispose {}
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AlgoTokens.space3),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    // Fullscreen Header (2-Line layout: Title on top, Interactive Mode on new line below)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        // Line 1: Title & node/edge count
                        Text(
                            text = if (algorithmKey.isNotEmpty()) "$algorithmKey Full Screen Canvas - ${step.nodes.size} nodes - ${step.edges.size} edges" else "Graph Canvas - ${step.nodes.size} nodes - ${step.edges.size} edges",
                            style = MaterialTheme.typography.titleSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )

                        // Line 2: Interactive Mode toggle button (at the bottom on a new line)
                        if (resolvedProfile.allowedTools.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(if (isBuilderActive) CyanSubtle else CanvasBackground)
                                    .border(
                                        AlgoTokens.strokeThin,
                                        if (isBuilderActive) PrimaryCyan else BorderSubtle,
                                        RoundedCornerShape(AlgoTokens.radiusXs)
                                    )
                                    .clickable { isBuilderActive = !isBuilderActive }
                                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                                ) {
                                    Icon(
                                        imageVector = AlgoGlyphs.Tap,
                                        contentDescription = null,
                                        tint = if (isBuilderActive) PrimaryCyan else TextMuted,
                                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                    )
                                    Text(
                                        text = "Interactive Mode",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isBuilderActive) PrimaryCyan else TextMuted,
                                        fontSize = AlgoType.microSize,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Stage in Fullscreen
                    GraphCanvasEngine(
                        nodes = step.nodes,
                        edges = step.edges,
                        profile = resolvedProfile,
                        isBuilderActive = isBuilderActive,
                        activeTool = activeTool,
                        onToolSelected = { activeTool = it },
                        selectedNodeId = selectedNodeId,
                        onSelectedNodeIdChanged = { selectedNodeId = it },
                        userCustomCoordinates = userCustomCoordinates,
                        onNodeMoved = { id, coords -> onNodeMoved?.invoke(id, coords) },
                        visitedNodeIds = step.visitedNodeIds,
                        activeNodeId = step.activeNodeId,
                        challengeTargetNodeIds = challengeTargetNodeIds,
                        startNodeId = startNodeId,
                        targetNodeId = targetNodeId,
                        onEndpointsChanged = onEndpointsChanged,
                        onNodeClicked = onNodeClick,
                        onGraphModified = onGraphModified,
                        onBstInsertKey = onBstInsertKey,
                        onBstDeleteNode = onBstDeleteNode,
                        onHeapPushValue = onHeapPushValue,
                        onHeapExtractRoot = onHeapExtractRoot,
                        onHeapRemoveTail = onHeapRemoveTail,
                        canReset = isCustomGraph || userCustomCoordinates.isNotEmpty(),
                        onResetGraph = {
                            selectedNodeId = null
                            onResetGraph?.invoke()
                        },
                        canClear = isBuilderActive && resolvedProfile.canDeleteElements && step.nodes.isNotEmpty(),
                        onClearCanvas = {
                            selectedNodeId = null
                            onEndpointsChanged?.invoke(null, null)
                            onGraphModified?.invoke(emptyList(), emptyList())
                        },
                        canUndo = canUndo,
                        canRedo = canRedo,
                        onUndo = onUndo,
                        onRedo = onRedo,
                        onNodeDragStarted = onNodeDragStarted,
                        onNodeDragFinished = onNodeDragFinished,
                        isFullscreen = true,
                        onToggleFullscreen = { isFullscreen = false },
                        currentStep = step,
                        playbackSpeedMs = playbackSpeedMs,
                        isScrubbing = isScrubbing,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    // Bottom telemetry in Fullscreen
                    if (resolvedMode == GraphTelemetryMode.HEAP_ARRAY) {
                        HeapSynchronizedArrayStrip(
                            step = step,
                            nodes = step.nodes,
                            onCellClick = { idx -> onNodeClick?.invoke(idx.toString()) }
                        )
                    } else {
                        GraphFrontierTelemetryStrip(
                            step = step,
                            edges = step.edges,
                            telemetryMode = resolvedMode,
                            startNodeId = startNodeId,
                            targetNodeId = targetNodeId,
                            onNodeClick = onNodeClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Synchronized 1D Array Strip rendered directly beneath the 2D Binary Heap Tree.
 * Safely handles empty heap without showing misleading [0..0] range.
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
        nodes.mapNotNull { it.label.toIntOrNull() }
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
            val titleText = if (arrayValues.isNotEmpty()) {
                "HEAP ARRAY [0..${arrayValues.size - 1}]"
            } else {
                "HEAP ARRAY (EMPTY)"
            }
            Text(
                text = titleText,
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold
            )
            val mappingText = if (arrayValues.isEmpty()) {
                "Heap is empty"
            } else if (activeIdx != null) {
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

        if (arrayValues.isEmpty()) {
            Text(
                text = "No elements in heap (use Add Node to push value)",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontSize = AlgoType.microSize,
                modifier = Modifier.padding(vertical = AlgoTokens.space1)
            )
        } else {
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
}

/**
 * Live Frontier (Queue / Stack / Target), Visited Order, and Path Weight Strip.
 * Path weight is ONLY shown for Dijkstra's confirmed path; never shown for BFS/DFS or trees.
 */
@Composable
private fun GraphFrontierTelemetryStrip(
    step: VisualizerStep,
    edges: List<GraphEdgeState>,
    telemetryMode: GraphTelemetryMode,
    startNodeId: String? = null,
    targetNodeId: String? = null,
    onNodeClick: ((String) -> Unit)?
) {
    val frontierLabel = telemetryMode.frontierLabel
    val frontierItems: List<BufferItem> = step.buffer

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
        // 1. Frontier Pill Group (Queue / Stack / Target)
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
                    text = "EMPTY (0)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDark,
                    fontSize = AlgoType.microSize
                )
            } else {
                frontierItems.forEach { item ->
                    val targetId = item.nodeId ?: item.value
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                            .background(CyanSubtle)
                            .border(AlgoTokens.strokeHairline, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusXxs))
                            .clickable(enabled = onNodeClick != null) { onNodeClick?.invoke(targetId) }
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.strokeThin)
                    ) {
                        Text(
                            text = item.value,
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

        // 3. Weighted Path Cost Badge (only shown for Dijkstra)
        if (telemetryMode == GraphTelemetryMode.DIJKSTRA_PQ) {
            val confirmedCost = when {
                step.phaseLabel in listOf("FOUND", "TARGET REACHED", "DONE", "SORTED") -> {
                    step.variables["cost"]?.toIntOrNull()
                        ?: step.variables["dist[${targetNodeId ?: step.activeNodeId}]"]?.toIntOrNull()
                        ?: if (startNodeId != null && targetNodeId != null) {
                            val adj = GraphSearch.buildAdjacency(edges)
                            val (_, cost) = GraphSearch.dijkstraShortestPath(adj, startNodeId, targetNodeId)
                            cost.takeIf { it >= 0 }
                        } else null
                }
                step.phaseLabel in listOf("SETTLED", "RELAXING", "UPDATING") && step.activeNodeId != null -> {
                    step.variables["dist[${step.activeNodeId}]"]?.toIntOrNull()
                }
                else -> null
            }

            if (confirmedCost != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(GreenSubtle)
                        .border(AlgoTokens.strokeHairline, AccentGreen.copy(alpha = 0.45f), RoundedCornerShape(AlgoTokens.radiusXxs))
                        .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = "Cost: $confirmedCost",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentGreen,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun GraphTreeVisualizerPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(300.dp).padding(AlgoTokens.space4)) {
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
