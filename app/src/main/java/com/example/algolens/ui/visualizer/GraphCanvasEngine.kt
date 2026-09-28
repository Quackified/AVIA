package com.example.algolens.ui.visualizer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import com.example.algolens.data.GraphSearch
import com.example.algolens.data.GraphTreeMutations
import com.example.algolens.model.GraphCapabilityProfile
import com.example.algolens.model.GraphTool
import com.example.algolens.model.NodePlacementMode
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Reusable dialog for integer entry (BST key, Heap value, edge weight)
 * with inline validation, range constraints, and side-effect-free dismissal.
 */
@Composable
fun GraphIntegerEntryDialog(
    title: String,
    hint: String,
    range: IntRange,
    initialValue: String = "",
    confirmLabel: String = "Confirm",
    canConfirm: Boolean = true,
    capacityError: String? = null,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var textValue by remember { mutableStateOf(initialValue) }
    val parsedInt = textValue.trim().toIntOrNull()
    val errorText = when {
        capacityError != null -> capacityError
        textValue.trim().isEmpty() -> "Value cannot be empty"
        parsedInt == null -> "Must be an integer"
        parsedInt !in range -> "Must be between ${range.first} and ${range.last}"
        else -> null
    }
    val isValid = (errorText == null) && canConfirm

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CardBackground)
                .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusMd))
                .padding(AlgoTokens.space4)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { onDismiss() }
                            .padding(AlgoTokens.space2)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(AlgoTokens.inlineIconMd)
                        )
                    }
                }

                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize
                )

                // Input field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CanvasBackground)
                        .border(
                            AlgoTokens.strokeThin,
                            if (errorText != null) AccentRed.copy(alpha = 0.8f) else BorderCyan,
                            RoundedCornerShape(AlgoTokens.radiusSm)
                        )
                        .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4)
                ) {
                    BasicTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryCyan,
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(PrimaryCyan),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Error / ready feedback
                if (errorText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Alert,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                        )
                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentRed,
                            fontSize = AlgoType.microSize
                        )
                    }
                }

                // Actions: Cancel & Confirm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(CanvasBackground)
                            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { onDismiss() }
                            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(if (isValid) PrimaryCyan else PrimaryCyan.copy(alpha = 0.35f))
                            .clickable(enabled = isValid) {
                                if (isValid && parsedInt != null) {
                                    onConfirm(parsedInt)
                                }
                            }
                            .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4)
                    ) {
                        Text(
                            text = confirmLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isValid) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Unified 2D Graph and Tree Canvas Engine shared identically between
 * embedded and fullscreen surfaces.
 *
 * Responsibilities:
 * - Viewport geometry (pan & zoom 0.5x..2.5x, center of mass, fit with 24dp margins)
 * - Persistent coordinate overrides via [userCustomCoordinates]
 * - Capability-driven tool arbitration and structure-aware mutations
 * - Integer entry dialogs for BST key, Heap value, and direct edge weight
 * - Synchronized preview path (Dijkstra least-cost or BFS fewest hops)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GraphCanvasEngine(
    nodes: List<GraphNodeState>,
    edges: List<GraphEdgeState>,
    profile: GraphCapabilityProfile,
    isBuilderActive: Boolean,
    activeTool: GraphTool,
    onToolSelected: (GraphTool) -> Unit,
    selectedNodeId: String?,
    onSelectedNodeIdChanged: (String?) -> Unit,
    userCustomCoordinates: Map<String, Offset>,
    onNodeMoved: (nodeId: String, newWorldCoords: Offset) -> Unit,
    canReset: Boolean,
    onResetGraph: () -> Unit,
    canClear: Boolean = false,
    onClearCanvas: (() -> Unit)? = null,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier,
    visitedNodeIds: Set<String> = emptySet(),
    activeNodeId: String? = null,
    challengeTargetNodeIds: Set<String> = emptySet(),
    startNodeId: String? = null,
    targetNodeId: String? = null,
    onEndpointsChanged: ((start: String?, target: String?) -> Unit)? = null,
    onNodeClicked: ((String) -> Unit)? = null,
    onGraphModified: (((nodes: List<GraphNodeState>, edges: List<GraphEdgeState>) -> Unit))? = null,
    onBstInsertKey: ((key: Int) -> Unit)? = null,
    onBstDeleteNode: ((nodeId: String) -> Unit)? = null,
    onHeapPushValue: ((value: Int) -> Unit)? = null,
    onHeapExtractRoot: (() -> Unit)? = null,
    onHeapRemoveTail: (() -> Unit)? = null,
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onNodeDragStarted: () -> Unit = {},
    onNodeDragFinished: () -> Unit = {},
    currentStep: VisualizerStep? = null,
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false
) {
    val density = LocalDensity.current.density

    // Viewport state
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var maxObservedCanvasHeightPx by remember { mutableFloatStateOf(0f) }

    // Transient interaction state
    var dragStartNode by remember { mutableStateOf<GraphNodeState?>(null) }
    var currentDragPos by remember { mutableStateOf<Offset?>(null) }
    var hoveredTargetNodeId by remember { mutableStateOf<String?>(null) }
    var selectedEdgeIndex by remember { mutableStateOf<Int?>(null) }

    // Dialogs state
    var showBstInsertDialog by remember { mutableStateOf(false) }
    var showHeapPushDialog by remember { mutableStateOf(false) }
    var editingEdgeIndex by remember { mutableStateOf<Int?>(null) }

    // Decorate current step nodes with user coordinate overrides
    val effectiveNodes = remember(nodes, userCustomCoordinates) {
        nodes.map { node ->
            userCustomCoordinates[node.id]?.let { node.copy(x = it.x, y = it.y) } ?: node
        }
    }

    // Motion State tracking for swaps, signals, ripples, and path neon trace
    val stepForMotion = currentStep ?: remember(effectiveNodes, edges, activeNodeId, visitedNodeIds) {
        VisualizerStep(
            nodes = effectiveNodes,
            edges = edges,
            activeNodeId = activeNodeId,
            visitedNodeIds = visitedNodeIds
        )
    }
    val previousStepState = remember { mutableStateOf<VisualizerStep?>(null) }
    val motionState = rememberGraphMotionState(
        currentStep = stepForMotion,
        previousStep = previousStepState.value,
        startNodeId = startNodeId,
        targetNodeId = targetNodeId,
        playbackSpeedMs = playbackSpeedMs,
        isScrubbing = isScrubbing
    )
    LaunchedEffect(stepForMotion.stepIndex, effectiveNodes.size, activeNodeId) {
        previousStepState.value = stepForMotion
    }

    // Live preview path
    val livePreviewPath = remember(edges, startNodeId, targetNodeId, profile) {
        if (startNodeId != null && targetNodeId != null) {
            val adj = GraphSearch.buildAdjacency(edges)
            if (profile.canEditEdgeWeights) {
                GraphSearch.dijkstraShortestPath(adj, startNodeId, targetNodeId).first
            } else {
                GraphSearch.shortestPath(adj, startNodeId, targetNodeId)
            }
        } else {
            emptyList()
        }
    }

    val isPannedOrZoomed = (panOffset != Offset.Zero || zoom != 1f)

    Box(
        modifier = modifier
            .onSizeChanged { size ->
                canvasSize = size.toSize()
                if (size.height.toFloat() > maxObservedCanvasHeightPx) {
                    maxObservedCanvasHeightPx = size.height.toFloat()
                }
            }
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CanvasBackground)
            .border(
                width = AlgoTokens.strokeThin,
                color = AccentGreen.copy(alpha = 0.16f),
                shape = RoundedCornerShape(AlgoTokens.radiusSm)
            )
    ) {
        // 1. Gesture Detector Layer
        GraphBuilderGestures(
            nodes = effectiveNodes,
            edges = edges,
            profile = profile,
            isBuilderActive = isBuilderActive && profile.allowedTools.isNotEmpty(),
            activeTool = activeTool,
            selectedNodeId = selectedNodeId,
            startNodeId = startNodeId,
            targetNodeId = targetNodeId,
            panOffset = panOffset,
            zoom = zoom,
            referenceHeight = maxObservedCanvasHeightPx,
            dragStartNode = dragStartNode,
            hoveredTargetNodeId = hoveredTargetNodeId,
            onPanAndZoomChanged = { newPan, newZoom ->
                panOffset = newPan
                zoom = newZoom
            },
            onNodeMoved = { id, coords ->
                onNodeMoved(id, coords)
            },
            onNodeSelected = { id ->
                if (id != selectedNodeId) {
                    selectedEdgeIndex = null
                }
                onSelectedNodeIdChanged(id)
            },
            onNodeClicked = { id ->
                onNodeClicked?.invoke(id)
            },
            onAddNodeFreeform = { coords ->
                if (profile.canAddNode) {
                    val (updatedNodes, updatedEdges) = GraphTreeMutations.addGeneralGraphNode(
                        nodes = effectiveNodes,
                        edges = edges,
                        x = coords.x,
                        y = coords.y,
                        autoLinkFromNodeId = selectedNodeId
                    )
                    val nextLabel = updatedNodes.last().label
                    onSelectedNodeIdChanged(nextLabel)
                    onGraphModified?.invoke(updatedNodes, updatedEdges)
                }
            },
            onRequestBstInsert = {
                showBstInsertDialog = true
            },
            onRequestHeapPush = {
                showHeapPushDialog = true
            },
            onLinkCreated = { fromId, toId ->
                if (profile.canConnectNodes) {
                    val updatedEdges = GraphTreeMutations.linkGeneralGraphNodes(
                        edges = edges,
                        fromId = fromId,
                        toId = toId,
                        isDirected = profile.supportsDirectedEdges,
                        weight = GraphTreeMutations.getNextAvailableWeight(edges)
                    )
                    if (updatedEdges != null) {
                        onGraphModified?.invoke(effectiveNodes, updatedEdges)
                    }
                }
            },
            onCycleWeight = { idx ->
                if (profile.canEditEdgeWeights) {
                    val edge = edges.getOrNull(idx)
                    if (edge != null) {
                        val updatedEdges = GraphTreeMutations.cycleEdgeWeight(edges, edge.from, edge.to)
                        onGraphModified?.invoke(effectiveNodes, updatedEdges)
                    }
                }
            },
            onRequestEditWeight = { idx ->
                if (profile.canEditEdgeWeights) {
                    editingEdgeIndex = idx
                }
            },
            onEndpointsChanged = { s, t ->
                if (profile.canSetEndpoints) {
                    onEndpointsChanged?.invoke(s, t)
                }
            },
            onDeleteNode = { id ->
                if (profile.canDeleteElements) {
                    when (profile.nodePlacementMode) {
                        NodePlacementMode.FREEFORM -> {
                            val (updatedNodes, updatedEdges) = GraphTreeMutations.deleteGeneralGraphNode(
                                nodes = effectiveNodes,
                                edges = edges,
                                nodeId = id
                            )
                            if (id == startNodeId || id == targetNodeId) {
                                val s = if (id == startNodeId) null else startNodeId
                                val t = if (id == targetNodeId) null else targetNodeId
                                onEndpointsChanged?.invoke(s, t)
                            }
                            onSelectedNodeIdChanged(null)
                            onGraphModified?.invoke(updatedNodes, updatedEdges)
                        }
                        NodePlacementMode.KEYED_BST_INSERT -> {
                            onSelectedNodeIdChanged(null)
                            onBstDeleteNode?.invoke(id)
                        }
                        NodePlacementMode.HEAP_ARRAY_PUSH -> {
                            val slotIdx = id.toIntOrNull()
                            if (slotIdx != null && GraphTreeMutations.isSupportedHeapDeletionSlot(slotIdx, effectiveNodes.size)) {
                                onSelectedNodeIdChanged(null)
                                if (slotIdx == 0) {
                                    onHeapExtractRoot?.invoke()
                                } else {
                                    onHeapRemoveTail?.invoke()
                                }
                            }
                        }
                    }
                }
            },
            onDeleteEdge = { idx ->
                if (profile.canDeleteElements && profile.nodePlacementMode == NodePlacementMode.FREEFORM) {
                    val edge = edges.getOrNull(idx)
                    if (edge != null) {
                        val updatedEdges = GraphTreeMutations.deleteGeneralGraphEdge(edges, edge.from, edge.to)
                        onGraphModified?.invoke(effectiveNodes, updatedEdges)
                    }
                }
            },
            onDragStartNodeChanged = { dragStartNode = it },
            onCurrentDragPosChanged = { currentDragPos = it },
            onHoveredTargetNodeIdChanged = { hoveredTargetNodeId = it },
            onEdgeSelected = { selectedEdgeIndex = it },
            onNodeDragStarted = onNodeDragStarted,
            onNodeDragFinished = onNodeDragFinished,
            modifier = Modifier.fillMaxSize()
        )

        val activeEdgeIndex: Int? = selectedEdgeIndex
            ?: selectedNodeId?.let { id ->
                val idx = edges.indexOfFirst { it.from == id || it.to == id }
                if (idx >= 0) idx else null
            }
            ?: if (profile.canEditEdgeWeights && activeTool == GraphTool.WEIGHT) {
                val firstWeighted = edges.indexOfFirst { it.weight != null }
                if (firstWeighted >= 0) firstWeighted else edges.indices.firstOrNull()
            } else null
        val activeEdge = activeEdgeIndex?.let { edges.getOrNull(it) }

        // 2. Rendering Layer
        GraphTreeRenderer(
            nodes = effectiveNodes,
            edges = edges,
            visitedNodeIds = visitedNodeIds,
            activeNodeId = activeNodeId,
            selectedNodeId = selectedNodeId,
            hoveredTargetNodeId = hoveredTargetNodeId,
            dragStartNode = dragStartNode,
            currentDragPos = currentDragPos,
            nodeScales = emptyMap(),
            challengeTargetNodeIds = challengeTargetNodeIds,
            startNodeId = startNodeId,
            targetNodeId = targetNodeId,
            previewPath = livePreviewPath,
            selectedEdgeIndex = activeEdgeIndex,
            panOffset = panOffset,
            zoom = zoom,
            referenceHeight = maxObservedCanvasHeightPx,
            motionState = motionState,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Floating Bottom Chrome: Banner / Counter Stepper + Floating Toolbar (only when Interactive Mode is active)
        if (isBuilderActive && profile.allowedTools.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AlgoTokens.space1),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                // 3a. Tooltip Info Banner
                val bannerText = profile.hintProvider(activeTool, selectedNodeId)
                Box(
                    modifier = Modifier
                        .padding(bottom = AlgoTokens.space1)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(DarkBackground.copy(alpha = 0.90f))
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = bannerText,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 3b. Compact Edge Weight Counter Stepper on top of the Floating Toolbar
                val curEdge = activeEdge
                val isStepperVisible = profile.canEditEdgeWeights &&
                    (selectedEdgeIndex != null || selectedNodeId != null)

                if (isStepperVisible && curEdge != null) {
                    val curWeight = curEdge.weight ?: 1
                    val incidentEdges = if (selectedNodeId != null) {
                        edges.indices.filter { edges[it].from == selectedNodeId || edges[it].to == selectedNodeId }
                    } else {
                        edges.indices.toList()
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                        modifier = Modifier
                            .padding(bottom = AlgoTokens.space1)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(DarkBackground.copy(alpha = 0.95f))
                            .border(
                                AlgoTokens.strokeThin,
                                PrimaryCyan.copy(alpha = 0.60f),
                                RoundedCornerShape(percent = 50)
                            )
                            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                    ) {
                        // Minus (-1)
                        Box(
                            modifier = Modifier
                                .size(AlgoTokens.iconButtonXs)
                                .clip(CircleShape)
                                .background(CanvasBackground)
                                .clickable {
                                    val nextWeight = (curWeight - 1).coerceAtLeast(1)
                                    val updated = GraphTreeMutations.setEdgeWeight(edges, curEdge.from, curEdge.to, nextWeight)
                                    onGraphModified?.invoke(effectiveNodes, updated)
                                }
                                .semantics {
                                    this.role = Role.Button
                                    this.contentDescription = "Decrease edge weight"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Minus,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                        }

                        // Compact Weight display: "A ➔ B: 5"
                        // Tapping cycles through incident edges (if multiple) or opens numeric entry (if single).
                        // Long-press always opens direct numeric entry dialog.
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .combinedClickable(
                                    onClick = {
                                        if (incidentEdges.size > 1) {
                                            val currentPos = incidentEdges.indexOf(activeEdgeIndex)
                                            val nextIdx = incidentEdges[(currentPos + 1).coerceAtLeast(0) % incidentEdges.size]
                                            selectedEdgeIndex = nextIdx
                                        } else {
                                            editingEdgeIndex = activeEdgeIndex
                                        }
                                    },
                                    onLongClick = {
                                        editingEdgeIndex = activeEdgeIndex
                                    }
                                )
                                .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                        ) {
                            Text(
                                text = "${curEdge.from} ➔ ${curEdge.to}:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$curWeight",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.Bold
                            )
                            if (incidentEdges.size > 1) {
                                Text(
                                    text = "↻",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan.copy(alpha = 0.7f),
                                    fontSize = AlgoType.microSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Plus (+1)
                        Box(
                            modifier = Modifier
                                .size(AlgoTokens.iconButtonXs)
                                .clip(CircleShape)
                                .background(CanvasBackground)
                                .clickable {
                                    val nextWeight = (curWeight + 1).coerceAtMost(99)
                                    val updated = GraphTreeMutations.setEdgeWeight(edges, curEdge.from, curEdge.to, nextWeight)
                                    onGraphModified?.invoke(effectiveNodes, updated)
                                }
                                .semantics {
                                    this.role = Role.Button
                                    this.contentDescription = "Increase edge weight"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Plus,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                        }
                    }
                }

                GraphFloatingToolbar(
                    profile = profile,
                    activeTool = activeTool,
                    onToolSelected = { tool ->
                        onToolSelected(tool)
                        if (tool == GraphTool.WEIGHT && selectedEdgeIndex == null && profile.canEditEdgeWeights) {
                            val defaultIdx = edges.indexOfFirst { it.weight != null }
                                .takeIf { it >= 0 } ?: edges.indices.firstOrNull()
                            selectedEdgeIndex = defaultIdx
                        }
                    },
                    onCenterView = {
                        if (canvasSize.width > 0f && canvasSize.height > 0f) {
                            panOffset = GraphCanvasGeometry.computeCenterPan(
                                nodes = effectiveNodes,
                                drawSize = canvasSize,
                                zoom = zoom,
                                referenceHeight = maxObservedCanvasHeightPx
                            )
                        } else {
                            panOffset = Offset.Zero
                        }
                    },
                    onFitToScreen = {
                        if (canvasSize.width > 0f && canvasSize.height > 0f) {
                            val (fitZoom, fitPan) = GraphCanvasGeometry.computeFitZoomAndPan(
                                nodes = effectiveNodes,
                                drawSize = canvasSize,
                                marginPx = 24f * density,
                                referenceHeight = maxObservedCanvasHeightPx
                            )
                            zoom = fitZoom
                            panOffset = fitPan
                        } else {
                            zoom = 1f
                            panOffset = Offset.Zero
                        }
                    },
                    onResetGraph = {
                        panOffset = Offset.Zero
                        zoom = 1f
                        onResetGraph()
                    },
                    canReset = canReset,
                    canClear = canClear || (effectiveNodes.isNotEmpty() && profile.canDeleteElements),
                    onClearCanvas = {
                        selectedEdgeIndex = null
                        onSelectedNodeIdChanged(null)
                        onEndpointsChanged?.invoke(null, null)
                        onClearCanvas?.invoke() ?: onGraphModified?.invoke(emptyList(), emptyList())
                    },
                    isPannedOrZoomed = isPannedOrZoomed
                )
            }
        }

        // 4a. Top-Left Canvas Controls: Undo & Redo (only when interactive builder is active)
        if (isBuilderActive && profile.allowedTools.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(AlgoTokens.space2),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo
                Box(
                    modifier = Modifier
                        .size(AlgoTokens.iconButtonSm)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(DarkBackground.copy(alpha = 0.75f))
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .semantics {
                            this.role = Role.Button
                            this.contentDescription = "Undo graph edit"
                        }
                        .clickable(enabled = canUndo) { onUndo() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Undo,
                        contentDescription = null,
                        tint = if (canUndo) PrimaryCyan else TextMuted.copy(alpha = 0.35f),
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                }

                // Redo
                Box(
                    modifier = Modifier
                        .size(AlgoTokens.iconButtonSm)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(DarkBackground.copy(alpha = 0.75f))
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .semantics {
                            this.role = Role.Button
                            this.contentDescription = "Redo graph edit"
                        }
                        .clickable(enabled = canRedo) { onRedo() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Redo,
                        contentDescription = null,
                        tint = if (canRedo) PrimaryCyan else TextMuted.copy(alpha = 0.35f),
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                }
            }
        }

        // 4b. Top-Right Canvas Icon: Fullscreen Toggle (only when fullscreen mode is supported)
        if (profile.supportsFullscreen) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AlgoTokens.space2)
                    .size(AlgoTokens.iconButtonSm)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(DarkBackground.copy(alpha = 0.75f))
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                    .semantics {
                        this.role = Role.Button
                        this.contentDescription = if (isFullscreen) "Exit Fullscreen" else "Open Fullscreen"
                    }
                    .clickable { onToggleFullscreen() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFullscreen) AlgoGlyphs.Close else AlgoGlyphs.Expand,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                )
            }
        }

        // 5. Modals / Entry Dialogs
        if (showBstInsertDialog) {
            val capacityErr = if (effectiveNodes.size >= 15) "Maximum capacity reached (15 nodes)" else null
            GraphIntegerEntryDialog(
                title = "Insert BST Key",
                hint = "Enter an integer key (1..999) to insert into the Binary Search Tree.",
                range = 1..999,
                confirmLabel = "Insert",
                canConfirm = capacityErr == null,
                capacityError = capacityErr,
                onConfirm = { key ->
                    showBstInsertDialog = false
                    onBstInsertKey?.invoke(key)
                },
                onDismiss = { showBstInsertDialog = false }
            )
        }

        if (showHeapPushDialog) {
            val capacityErr = if (effectiveNodes.size >= 15) "Maximum capacity reached (15 elements)" else null
            GraphIntegerEntryDialog(
                title = "Push Heap Value",
                hint = "Enter an integer value (1..999) to append to the Binary Heap array.",
                range = 1..999,
                confirmLabel = "Push",
                canConfirm = capacityErr == null,
                capacityError = capacityErr,
                onConfirm = { value ->
                    showHeapPushDialog = false
                    onHeapPushValue?.invoke(value)
                },
                onDismiss = { showHeapPushDialog = false }
            )
        }

        editingEdgeIndex?.let { edgeIdx ->
            val edge = edges.getOrNull(edgeIdx)
            if (edge != null) {
                GraphIntegerEntryDialog(
                    title = "Set Edge Weight",
                    hint = "Enter an integer weight (1..99) for edge (${edge.from} ↔ ${edge.to}).",
                    range = 1..99,
                    initialValue = (edge.weight ?: 1).toString(),
                    confirmLabel = "Set Weight",
                    onConfirm = { newWeight ->
                        val updatedEdges = GraphTreeMutations.setEdgeWeight(edges, edge.from, edge.to, newWeight)
                        editingEdgeIndex = null
                        onGraphModified?.invoke(effectiveNodes, updatedEdges)
                    },
                    onDismiss = { editingEdgeIndex = null }
                )
            }
        }
    }
}
