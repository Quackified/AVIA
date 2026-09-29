package com.avia.ui.visualizer

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.avia.model.GraphCapabilityProfile
import com.avia.model.GraphTool
import com.avia.model.NodePlacementMode
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Unified gesture detector for 2D Graph & Tree canvas.
 * Supports:
 * - Multi-pointer pinch-zoom (0.5x .. 2.5x) anchored at pinch centroid
 * - Single-pointer viewport pan
 * - Stable single-node drag without triggering step regeneration
 * - Structure-aware tap routing (Freeform Add, BST Keyed Insert, Heap Push, Delete, Endpoints, Weight)
 * - Rubber-band drag and tap-tap edge linking with duplicate/self-link rejection
 * - Midpoint pill and full segment hit-testing for edges
 * - Long-press on edge for direct weight dialog
 */
@Composable
fun GraphBuilderGestures(
    nodes: List<GraphNodeState>,
    edges: List<GraphEdgeState>,
    profile: GraphCapabilityProfile,
    isBuilderActive: Boolean,
    activeTool: GraphTool,
    selectedNodeId: String?,
    onPanAndZoomChanged: (Offset, Float) -> Unit,
    onNodeMoved: (nodeId: String, newWorldCoords: Offset) -> Unit,
    onNodeSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    startNodeId: String? = null,
    targetNodeId: String? = null,
    panOffset: Offset = Offset.Zero,
    zoom: Float = 1f,
    referenceHeight: Float = 0f,
    dragStartNode: GraphNodeState? = null,
    hoveredTargetNodeId: String? = null,
    onNodeClicked: (String) -> Unit = {},
    onAddNodeFreeform: (worldCoords: Offset) -> Unit = {},
    onRequestBstInsert: (worldCoords: Offset) -> Unit = {},
    onRequestHeapPush: () -> Unit = {},
    onLinkCreated: (fromId: String, toId: String) -> Unit = { _, _ -> },
    onCycleWeight: (edgeIndex: Int) -> Unit = {},
    onRequestEditWeight: (edgeIndex: Int) -> Unit = {},
    onEndpointsChanged: (start: String?, target: String?) -> Unit = { _, _ -> },
    onDeleteNode: (nodeId: String) -> Unit = {},
    onDeleteEdge: (edgeIndex: Int) -> Unit = {},
    onDragStartNodeChanged: (GraphNodeState?) -> Unit = {},
    onCurrentDragPosChanged: (Offset?) -> Unit = {},
    onHoveredTargetNodeIdChanged: (String?) -> Unit = {},
    onEdgeSelected: (Int?) -> Unit = {},
    onNodeDragStarted: () -> Unit = {},
    onNodeDragFinished: () -> Unit = {}
) {
    val density = LocalDensity.current.density

    val currentNodes = rememberUpdatedState(nodes)
    val currentEdges = rememberUpdatedState(edges)
    val currentProfile = rememberUpdatedState(profile)
    val currentBuilderActive = rememberUpdatedState(isBuilderActive)
    val currentTool = rememberUpdatedState(activeTool)
    val currentSelectedNodeId = rememberUpdatedState(selectedNodeId)
    val currentStartNodeId = rememberUpdatedState(startNodeId)
    val currentTargetNodeId = rememberUpdatedState(targetNodeId)
    val currentPan = rememberUpdatedState(panOffset)
    val currentZoom = rememberUpdatedState(zoom)
    val currentRefHeight = rememberUpdatedState(referenceHeight)
    val currentOnPanAndZoomChanged = rememberUpdatedState(onPanAndZoomChanged)
    val currentOnNodeMoved = rememberUpdatedState(onNodeMoved)
    val currentOnNodeSelected = rememberUpdatedState(onNodeSelected)
    val currentOnNodeClicked = rememberUpdatedState(onNodeClicked)
    val currentOnAddNodeFreeform = rememberUpdatedState(onAddNodeFreeform)
    val currentOnRequestBstInsert = rememberUpdatedState(onRequestBstInsert)
    val currentOnRequestHeapPush = rememberUpdatedState(onRequestHeapPush)
    val currentOnLinkCreated = rememberUpdatedState(onLinkCreated)
    val currentOnCycleWeight = rememberUpdatedState(onCycleWeight)
    val currentOnRequestEditWeight = rememberUpdatedState(onRequestEditWeight)
    val currentOnEndpointsChanged = rememberUpdatedState(onEndpointsChanged)
    val currentOnDeleteNode = rememberUpdatedState(onDeleteNode)
    val currentOnDeleteEdge = rememberUpdatedState(onDeleteEdge)
    val currentOnDragStartNodeChanged = rememberUpdatedState(onDragStartNodeChanged)
    val currentOnCurrentDragPosChanged = rememberUpdatedState(onCurrentDragPosChanged)
    val currentOnHoveredTargetNodeIdChanged = rememberUpdatedState(onHoveredTargetNodeIdChanged)
    val currentOnEdgeSelected = rememberUpdatedState(onEdgeSelected)
    val currentOnNodeDragStarted = rememberUpdatedState(onNodeDragStarted)
    val currentOnNodeDragFinished = rememberUpdatedState(onNodeDragFinished)

    Box(
        modifier = modifier.pointerInput(Unit) {
            val touchSlop = viewConfiguration.touchSlop

            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val startOffset = down.position
                val downTime = System.currentTimeMillis()

                val liveNodes = currentNodes.value
                val liveEdges = currentEdges.value
                val prof = currentProfile.value
                val builderActive = currentBuilderActive.value
                val tool = currentTool.value
                val selectedId = currentSelectedNodeId.value
                val livePan = currentPan.value
                val liveZoom = currentZoom.value
                val refHeight = currentRefHeight.value

                val geom = GraphCanvasGeometry.from(
                    nodes = liveNodes,
                    drawSize = size.toSize(),
                    panOffset = livePan,
                    zoom = liveZoom,
                    referenceHeight = refHeight
                )
                // Screen-space hit radius: max(28dp, visible node radius + 12dp)
                val hitThreshold = max(28f * density, (geom.nodeRadius * geom.zoom) + 12f * density)

                // 1. Node Hit Testing
                val hitNode = liveNodes.minByOrNull { node ->
                    val c = geom.toCanvasOffset(node.x, node.y)
                    val dx = startOffset.x - c.x
                    val dy = startOffset.y - c.y
                    sqrt(dx * dx + dy * dy)
                }?.takeIf { node ->
                    val c = geom.toCanvasOffset(node.x, node.y)
                    val dx = startOffset.x - c.x
                    val dy = startOffset.y - c.y
                    sqrt(dx * dx + dy * dy) <= hitThreshold
                }

                // 2. Edge Hit Testing (Pill midpoint + full segment)
                val hitEdgeIndex = liveEdges.indexOfFirst { edge ->
                    val fromNode = liveNodes.find { it.id == edge.from } ?: return@indexOfFirst false
                    val toNode = liveNodes.find { it.id == edge.to } ?: return@indexOfFirst false
                    val p1 = geom.toCanvasOffset(fromNode.x, fromNode.y)
                    val p2 = geom.toCanvasOffset(toNode.x, toNode.y)

                    // Test midpoint badge
                    val mid = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                    val midDist = (startOffset - mid).getDistance()
                    if (midDist <= 24f * density) return@indexOfFirst true

                    // Test full segment distance
                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val lenSq = dx * dx + dy * dy
                    if (lenSq < 1e-4f) {
                        return@indexOfFirst (startOffset - p1).getDistance() <= 16f * density
                    }
                    val t = (((startOffset.x - p1.x) * dx + (startOffset.y - p1.y) * dy) / lenSq).coerceIn(0f, 1f)
                    val proj = Offset(p1.x + t * dx, p1.y + t * dy)
                    (startOffset - proj).getDistance() <= 18f * density
                }

                var dragStarted = false
                var currentPos = startOffset
                var isPanningViewport = false
                var isMovingNode = false
                var dragStartNodeWorldPos = Offset.Zero
                var dragStartBaseScale = geom.baseScale
                var isLinking = false
                var isLongPressTriggered = false
                var lastHoveredTargetId: String? = null

                var prevPinchDist: Float? = null
                var prevPinchCentroid: Offset? = null

                while (true) {
                    val event = awaitPointerEvent()
                    val pressedChanges = event.changes.filter { it.pressed }

                    if (pressedChanges.isEmpty()) {
                        break
                    }

                    // ── Multi-Pointer Transform Handling (Pinch-Zoom & Pan) ──
                    if (pressedChanges.size >= 2) {
                        // Cancel single-finger editing gestures immediately to prevent accidental mutations
                        if (isMovingNode || isLinking) {
                            isMovingNode = false
                            isLinking = false
                            currentOnDragStartNodeChanged.value(null)
                            currentOnCurrentDragPosChanged.value(null)
                            currentOnHoveredTargetNodeIdChanged.value(null)
                        }

                        val p0 = pressedChanges[0].position
                        val p1 = pressedChanges[1].position
                        val currentPinchDist = (p0 - p1).getDistance()
                        val currentPinchCentroid = Offset((p0.x + p1.x) / 2f, (p0.y + p1.y) / 2f)

                        val prevDist = prevPinchDist
                        val prevCentroid = prevPinchCentroid

                        if (prevDist != null && prevCentroid != null && prevDist > 10f) {
                            val scaleFactor = currentPinchDist / prevDist
                            val centroidDelta = currentPinchCentroid - prevCentroid

                            val curZoom = currentZoom.value
                            val curPan = currentPan.value
                            val newZoom = (curZoom * scaleFactor).coerceIn(
                                GraphCanvasGeometry.MIN_ZOOM,
                                GraphCanvasGeometry.MAX_ZOOM
                            )
                            val zoomRatio = newZoom / curZoom
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val panMultiplier = 1.0f
                            val newPan = Offset(
                                x = (prevCentroid.x - cx) * (1f - zoomRatio) + curPan.x * zoomRatio + centroidDelta.x * panMultiplier,
                                y = (prevCentroid.y - cy) * (1f - zoomRatio) + curPan.y * zoomRatio + centroidDelta.y * panMultiplier
                            )
                            currentOnPanAndZoomChanged.value(newPan, newZoom)
                        }

                        prevPinchDist = currentPinchDist
                        prevPinchCentroid = currentPinchCentroid

                        pressedChanges.forEach { it.consume() }
                        continue
                    }

                    // If returning from a 2-finger pinch, reset tracking so single-pointer drag doesn't jump
                    if (prevPinchDist != null || prevPinchCentroid != null) {
                        prevPinchDist = null
                        prevPinchCentroid = null
                        val survivingPos = pressedChanges.firstOrNull { it.id == down.id }?.position ?: pressedChanges.first().position
                        currentPos = survivingPos
                    }

                    // ── Single-Pointer Handling ──
                    val change = pressedChanges.firstOrNull { it.id == down.id } ?: pressedChanges.first()
                    val newPos = change.position
                    val distFromStart = (newPos - startOffset).getDistance()

                    // Check for Long-press on edge for direct weight entry
                    if (!dragStarted && !isLongPressTriggered && tool == GraphTool.WEIGHT && hitEdgeIndex >= 0 && prof.canEditEdgeWeights) {
                        if (System.currentTimeMillis() - downTime >= 500L && distFromStart <= touchSlop) {
                            isLongPressTriggered = true
                            currentOnRequestEditWeight.value(hitEdgeIndex)
                        }
                    }

                    if (!dragStarted && distFromStart > touchSlop) {
                        dragStarted = true
                        if (builderActive && hitNode != null && tool == GraphTool.MOVE && prof.canMoveNodes) {
                            // Selection-gated move: ONLY move if this node is already selected (lit green)
                            if (hitNode.id == selectedId) {
                                currentOnNodeDragStarted.value()
                                isMovingNode = true
                                dragStartNodeWorldPos = Offset(hitNode.x, hitNode.y)
                                dragStartBaseScale = geom.baseScale
                            } else {
                                isPanningViewport = true
                            }
                        } else if (builderActive && hitNode != null && tool == GraphTool.LINK && prof.canConnectNodes) {
                            isLinking = true
                            currentOnDragStartNodeChanged.value(hitNode)
                            currentOnCurrentDragPosChanged.value(newPos)
                        } else {
                            isPanningViewport = true
                        }
                    }

                    if (dragStarted) {
                        change.consume()
                        val delta = newPos - currentPos
                        currentPos = newPos

                        val liveGeom = GraphCanvasGeometry.from(
                            nodes = currentNodes.value,
                            drawSize = size.toSize(),
                            panOffset = currentPan.value,
                            zoom = currentZoom.value,
                            referenceHeight = currentRefHeight.value
                        )

                        if (isMovingNode && hitNode != null) {
                            // Decoupled 1:1 world delta prevents runaway scale explosions
                            val deltaScreen = newPos - startOffset
                            val currentZ = currentZoom.value.coerceAtLeast(0.1f)
                            val scale = (dragStartBaseScale * currentZ).coerceAtLeast(0.001f)
                            val deltaWorldX = deltaScreen.x / scale
                            val deltaWorldY = deltaScreen.y / scale
                            val newWorldX = (dragStartNodeWorldPos.x + deltaWorldX).coerceIn(5f, 400f)
                            val newWorldY = (dragStartNodeWorldPos.y + deltaWorldY).coerceIn(5f, 300f)
                            currentOnNodeMoved.value(
                                hitNode.id,
                                Offset(newWorldX, newWorldY)
                            )
                        } else if (isLinking && hitNode != null) {
                            currentOnCurrentDragPosChanged.value(newPos)
                            val hovered = currentNodes.value
                                .filter { it.id != hitNode.id }
                                .minByOrNull { node ->
                                    val c = liveGeom.toCanvasOffset(node.x, node.y)
                                    val dx = newPos.x - c.x
                                    val dy = newPos.y - c.y
                                    sqrt(dx * dx + dy * dy)
                                }?.takeIf { node ->
                                    val c = liveGeom.toCanvasOffset(node.x, node.y)
                                    val dx = newPos.x - c.x
                                    val dy = newPos.y - c.y
                                    sqrt(dx * dx + dy * dy) <= hitThreshold
                                }
                            lastHoveredTargetId = hovered?.id
                            currentOnHoveredTargetNodeIdChanged.value(hovered?.id)
                        } else if (isPanningViewport) {
                            val panMultiplier = 1.0f
                            val maxPan = 1500f
                            val curPan = currentPan.value
                            val nextPan = Offset(
                                x = (curPan.x + delta.x * panMultiplier).coerceIn(-maxPan, maxPan),
                                y = (curPan.y + delta.y * panMultiplier).coerceIn(-maxPan, maxPan)
                            )
                            currentOnPanAndZoomChanged.value(nextPan, currentZoom.value)
                        }
                    }
                }

                // ── Gesture Finalization ──
                if (dragStarted) {
                    if (isMovingNode) {
                        currentOnNodeDragFinished.value()
                    }
                    if (isLinking && hitNode != null) {
                        val targetId = lastHoveredTargetId
                        if (targetId != null && targetId != hitNode.id) {
                            currentOnLinkCreated.value(hitNode.id, targetId)
                        }
                        currentOnDragStartNodeChanged.value(null)
                        currentOnCurrentDragPosChanged.value(null)
                        currentOnHoveredTargetNodeIdChanged.value(null)
                    }
                } else if (!isLongPressTriggered) {
                    // Tap Detected
                    if (hitEdgeIndex >= 0) {
                        currentOnEdgeSelected.value(hitEdgeIndex)
                    } else if (tool != GraphTool.WEIGHT) {
                        currentOnEdgeSelected.value(null)
                    }
                    if (!builderActive) {
                        if (hitNode != null) {
                            currentOnNodeClicked.value(hitNode.id)
                            currentOnNodeSelected.value(if (selectedId == hitNode.id) null else hitNode.id)
                        }
                    } else {
                        when (tool) {
                            GraphTool.MOVE -> {
                                if (hitNode != null) {
                                    currentOnNodeClicked.value(hitNode.id)
                                    currentOnNodeSelected.value(if (selectedId == hitNode.id) null else hitNode.id)
                                } else {
                                    currentOnNodeSelected.value(null)
                                }
                            }
                            GraphTool.ADD -> {
                                when (prof.nodePlacementMode) {
                                    NodePlacementMode.FREEFORM -> {
                                        if (hitNode != null) {
                                            currentOnNodeSelected.value(if (selectedId == hitNode.id) null else hitNode.id)
                                        } else if (prof.canAddNode) {
                                            val worldCoord = geom.toWorldCoords(startOffset)
                                            currentOnAddNodeFreeform.value(worldCoord)
                                        }
                                    }
                                    NodePlacementMode.KEYED_BST_INSERT -> {
                                        val worldCoord = geom.toWorldCoords(startOffset)
                                        currentOnRequestBstInsert.value(worldCoord)
                                    }
                                    NodePlacementMode.HEAP_ARRAY_PUSH -> {
                                        currentOnRequestHeapPush.value()
                                    }
                                }
                            }
                            GraphTool.LINK -> {
                                if (hitNode != null && prof.canConnectNodes) {
                                    if (selectedId != null && selectedId != hitNode.id) {
                                        currentOnLinkCreated.value(selectedId, hitNode.id)
                                        currentOnNodeSelected.value(null)
                                    } else {
                                        currentOnNodeSelected.value(if (selectedId == hitNode.id) null else hitNode.id)
                                    }
                                }
                            }
                            GraphTool.WEIGHT -> {
                                if (hitEdgeIndex >= 0 && prof.canEditEdgeWeights) {
                                    currentOnEdgeSelected.value(hitEdgeIndex)
                                } else if (hitNode != null) {
                                    val nextSelectedId = if (selectedId == hitNode.id) null else hitNode.id
                                    currentOnNodeSelected.value(nextSelectedId)
                                    if (nextSelectedId != null && prof.canEditEdgeWeights) {
                                        val incidentEdgeIdx = liveEdges.indexOfFirst { it.from == nextSelectedId || it.to == nextSelectedId }
                                        if (incidentEdgeIdx >= 0) {
                                            currentOnEdgeSelected.value(incidentEdgeIdx)
                                        }
                                    }
                                }
                            }
                            GraphTool.ENDPOINTS -> {
                                if (hitNode != null && prof.canSetEndpoints) {
                                    val curStart = currentStartNodeId.value
                                    val curTarget = currentTargetNodeId.value
                                    val id = hitNode.id
                                    val (newStart, newTarget) = when {
                                        id == curStart -> null to curTarget
                                        id == curTarget -> curStart to null
                                        curStart == null -> id to curTarget
                                        curTarget == null -> curStart to id
                                        else -> id to null
                                    }
                                    currentOnEndpointsChanged.value(newStart, newTarget)
                                }
                            }
                            GraphTool.DELETE -> {
                                if (hitNode != null && prof.canDeleteElements) {
                                    currentOnDeleteNode.value(hitNode.id)
                                } else if (hitEdgeIndex >= 0 && prof.canDeleteElements) {
                                    currentOnDeleteEdge.value(hitEdgeIndex)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

/**
 * Backward-compatible overload for [GraphBuilderGestures].
 */
@Composable
fun GraphBuilderGestures(
    dynamicNodes: List<GraphNodeState>,
    dynamicEdges: List<GraphEdgeState>,
    isBuilderActive: Boolean,
    selectedNodeId: String?,
    dragStartNode: GraphNodeState?,
    hoveredTargetNodeId: String?,
    onNodesChanged: (List<GraphNodeState>) -> Unit,
    onEdgesChanged: (List<GraphEdgeState>) -> Unit,
    onSelectedNodeIdChanged: (String?) -> Unit,
    onDragStartNodeChanged: (GraphNodeState?) -> Unit,
    onCurrentDragPosChanged: (Offset?) -> Unit,
    onHoveredTargetNodeIdChanged: (String?) -> Unit,
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)?,
    getNextNodeLabel: (List<GraphNodeState>) -> String,
    modifier: Modifier = Modifier,
    activeTool: GraphTool = GraphTool.MOVE,
    startNodeId: String? = null,
    targetNodeId: String? = null,
    panOffset: Offset = Offset.Zero,
    referenceHeight: Float = 0f,
    onPanChanged: (Offset) -> Unit = {},
    onEndpointsChanged: ((start: String?, target: String?) -> Unit)? = null,
    onNodeClick: ((String) -> Unit)? = null
) {
    GraphBuilderGestures(
        nodes = dynamicNodes,
        edges = dynamicEdges,
        profile = GraphCapabilityProfile.dijkstraProfile(),
        isBuilderActive = isBuilderActive,
        activeTool = activeTool,
        selectedNodeId = selectedNodeId,
        startNodeId = startNodeId,
        targetNodeId = targetNodeId,
        panOffset = panOffset,
        zoom = 1f,
        referenceHeight = referenceHeight,
        dragStartNode = dragStartNode,
        hoveredTargetNodeId = hoveredTargetNodeId,
        onPanAndZoomChanged = { pan, _ -> onPanChanged(pan) },
        onNodeMoved = { id, coords ->
            val updated = dynamicNodes.map { if (it.id == id) it.copy(x = coords.x, y = coords.y) else it }
            onNodesChanged(updated)
            onGraphModified?.invoke(updated, dynamicEdges)
        },
        onNodeSelected = onSelectedNodeIdChanged,
        onNodeClicked = { onNodeClick?.invoke(it) },
        onAddNodeFreeform = { coords ->
            val nextLabel = getNextNodeLabel(dynamicNodes)
            val newNode = GraphNodeState(
                id = nextLabel,
                label = nextLabel,
                x = coords.x,
                y = coords.y,
                state = ElementState.ACTIVE
            )
            var updatedEdges = dynamicEdges
            if (selectedNodeId != null) {
                val defaultWeight = ((dynamicEdges.size * 2) % 9) + 1
                updatedEdges = updatedEdges + GraphEdgeState(
                    from = selectedNodeId,
                    to = nextLabel,
                    weight = defaultWeight,
                    isHighlighted = true
                )
            }
            val updatedNodes = dynamicNodes + newNode
            onNodesChanged(updatedNodes)
            onEdgesChanged(updatedEdges)
            onSelectedNodeIdChanged(nextLabel)
            onGraphModified?.invoke(updatedNodes, updatedEdges)
        },
        onLinkCreated = { fromId, toId ->
            val edgeExists = dynamicEdges.any {
                (it.from == fromId && it.to == toId) || (it.from == toId && it.to == fromId)
            }
            if (!edgeExists) {
                val defaultWeight = ((dynamicEdges.size * 2) % 9) + 1
                val newEdge = GraphEdgeState(
                    from = fromId,
                    to = toId,
                    weight = defaultWeight,
                    isHighlighted = true
                )
                val updatedEdges = dynamicEdges + newEdge
                onEdgesChanged(updatedEdges)
                onGraphModified?.invoke(dynamicNodes, updatedEdges)
            }
        },
        onCycleWeight = { idx ->
            val edge = dynamicEdges.getOrNull(idx) ?: return@GraphBuilderGestures
            val nextWeight = ((edge.weight ?: 1) % 9) + 1
            val updatedEdges = dynamicEdges.toMutableList().apply {
                this[idx] = edge.copy(weight = nextWeight, isHighlighted = true)
            }
            onEdgesChanged(updatedEdges)
            onGraphModified?.invoke(dynamicNodes, updatedEdges)
        },
        onEndpointsChanged = { s, t -> onEndpointsChanged?.invoke(s, t) },
        onDeleteNode = { id ->
            val updatedNodes = dynamicNodes.filter { it.id != id }
            val updatedEdges = dynamicEdges.filter { it.from != id && it.to != id }
            if (id == startNodeId || id == targetNodeId) {
                val s = if (id == startNodeId) null else startNodeId
                val t = if (id == targetNodeId) null else targetNodeId
                onEndpointsChanged?.invoke(s, t)
            }
            onNodesChanged(updatedNodes)
            onEdgesChanged(updatedEdges)
            onSelectedNodeIdChanged(null)
            onGraphModified?.invoke(updatedNodes, updatedEdges)
        },
        onDeleteEdge = { idx ->
            val updatedEdges = dynamicEdges.filterIndexed { i, _ -> i != idx }
            onEdgesChanged(updatedEdges)
            onGraphModified?.invoke(dynamicNodes, updatedEdges)
        },
        onDragStartNodeChanged = onDragStartNodeChanged,
        onCurrentDragPosChanged = onCurrentDragPosChanged,
        onHoveredTargetNodeIdChanged = onHoveredTargetNodeIdChanged,
        modifier = modifier
    )
}
