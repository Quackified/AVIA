package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import kotlin.math.sqrt
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * 6-Tool modes for interactive graph authoring and goal-directed endpoints.
 */
enum class GraphTool(val label: String) {
    MOVE("Move"),
    ADD("Add Node"),
    LINK("Link"),
    WEIGHT("Weight"),
    ENDPOINTS("Endpoints"),
    DELETE("Delete")
}

/**
 * Toolbar + tap/drag gesture detectors + instruction banner for the graph
 * builder. The public [GraphTreeVisualizer] shell owns all the mutable
 * builder state and passes read+write closures down so this composable can
 * stay stateless with respect to the data layer.
 */
@Composable
fun GraphBuilderToolbar(
    isBuilderActive: Boolean,
    onToggleBuilder: () -> Unit,
    dynamicNodeCount: Int,
    dynamicEdgeCount: Int,
    canReset: Boolean,
    onReset: () -> Unit,
    isPanned: Boolean = false,
    onResetPan: () -> Unit = {},
    onAddNode: () -> Unit = {},
    activeTool: GraphTool = GraphTool.MOVE,
    onToolSelected: (GraphTool) -> Unit = {},
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
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
                    .clickable { onToggleBuilder() }
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Tap,
                        contentDescription = null,
                        tint = if (isBuilderActive) PrimaryCyan else TextMuted,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = if (isBuilderActive) "Builder: Active" else "Interactive Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isBuilderActive) PrimaryCyan else TextMuted,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            if (isBuilderActive) {
                Row(
                    modifier = Modifier
                                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CyanSubtle)
                        .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.45f), RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onAddNode() }
                        .padding(horizontal = 6.dp, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Plus,
                        contentDescription = "Add Node",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(9.dp)
                    )
                    Text(
                        text = "Add Node",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = "$dynamicNodeCount nodes • $dynamicEdgeCount edges",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontSize = AlgoType.microSize,
                maxLines = 1,
                modifier = Modifier            )
        }

        // Right actions: Center View (when panned) + Reset Graph (when modified) + Done (when active)
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            if (isPanned) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onResetPan() }
                        .padding(horizontal = 5.dp, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Text(
                        text = "Center View",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (canReset) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onReset() }
                        .padding(horizontal = 5.dp, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Refresh,
                        contentDescription = "Reset",
                        tint = AccentPink,
                        modifier = Modifier.size(9.dp)
                    )
                    Text(
                        text = "Reset Graph",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentPink,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Fullscreen toggle button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(if (isFullscreen) PrimaryCyan else CanvasBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        if (isFullscreen) PrimaryCyan else BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusXs)
                    )
                    .clickable { onToggleFullscreen() }
                    .padding(horizontal = 6.dp, vertical = AlgoTokens.space1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = if (isFullscreen) AlgoGlyphs.Close else AlgoGlyphs.Expand,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
                    tint = if (isFullscreen) DarkBackground else PrimaryCyan,
                    modifier = Modifier.size(9.dp)
                )
                Text(
                    text = if (isFullscreen) "Exit" else "Fullscreen",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFullscreen) DarkBackground else PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isBuilderActive) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(PrimaryCyan)
                        .clickable { onToggleBuilder() }
                        .padding(horizontal = 8.dp, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkBackground,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Tap/drag gesture detector that mutates the live graph AND supports
 * viewport panning via [panOffset] shared with [GraphTreeRenderer].
 */
@Composable
fun GraphBuilderGestures(
    dynamicNodes: List<GraphNodeState>,
    dynamicEdges: List<GraphEdgeState>,
    isBuilderActive: Boolean,
    selectedNodeId: String?,
    dragStartNode: GraphNodeState?,
    hoveredTargetNodeId: String?,
    activeTool: GraphTool = GraphTool.MOVE,
    startNodeId: String? = null,
    targetNodeId: String? = null,
    panOffset: Offset = Offset.Zero,
    referenceHeight: Float = 0f,
    onPanChanged: (Offset) -> Unit = {},
    onNodesChanged: (List<GraphNodeState>) -> Unit,
    onEdgesChanged: (List<GraphEdgeState>) -> Unit,
    onSelectedNodeIdChanged: (String?) -> Unit,
    onDragStartNodeChanged: (GraphNodeState?) -> Unit,
    onCurrentDragPosChanged: (Offset?) -> Unit,
    onHoveredTargetNodeIdChanged: (String?) -> Unit,
    onEndpointsChanged: ((start: String?, target: String?) -> Unit)? = null,
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)?,
    getNextNodeLabel: (List<GraphNodeState>) -> String,
    onNodeClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentNodesState = androidx.compose.runtime.rememberUpdatedState(dynamicNodes)
    val currentEdgesState = androidx.compose.runtime.rememberUpdatedState(dynamicEdges)
    val currentBuilderActiveState = androidx.compose.runtime.rememberUpdatedState(isBuilderActive)
    val currentSelectedNodeIdState = androidx.compose.runtime.rememberUpdatedState(selectedNodeId)
    val currentActiveToolState = androidx.compose.runtime.rememberUpdatedState(activeTool)
    val currentStartNodeIdState = androidx.compose.runtime.rememberUpdatedState(startNodeId)
    val currentTargetNodeIdState = androidx.compose.runtime.rememberUpdatedState(targetNodeId)
    val currentOnEndpointsChanged = androidx.compose.runtime.rememberUpdatedState(onEndpointsChanged)
    val currentPanOffsetState = androidx.compose.runtime.rememberUpdatedState(panOffset)
    val currentReferenceHeightState = androidx.compose.runtime.rememberUpdatedState(referenceHeight)
    val currentOnPanChanged = androidx.compose.runtime.rememberUpdatedState(onPanChanged)
    val currentOnNodesChanged = androidx.compose.runtime.rememberUpdatedState(onNodesChanged)
    val currentOnEdgesChanged = androidx.compose.runtime.rememberUpdatedState(onEdgesChanged)
    val currentOnSelectedNodeIdChanged = androidx.compose.runtime.rememberUpdatedState(onSelectedNodeIdChanged)
    val currentOnDragStartNodeChanged = androidx.compose.runtime.rememberUpdatedState(onDragStartNodeChanged)
    val currentOnCurrentDragPosChanged = androidx.compose.runtime.rememberUpdatedState(onCurrentDragPosChanged)
    val currentOnHoveredTargetNodeIdChanged = androidx.compose.runtime.rememberUpdatedState(onHoveredTargetNodeIdChanged)
    val currentOnGraphModified = androidx.compose.runtime.rememberUpdatedState(onGraphModified)
    val currentGetNextNodeLabel = androidx.compose.runtime.rememberUpdatedState(getNextNodeLabel)
    val currentOnNodeClick = androidx.compose.runtime.rememberUpdatedState(onNodeClick)

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                val touchSlop = viewConfiguration.touchSlop

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startOffset = down.position

                    val nodes = currentNodesState.value
                    val edges = currentEdgesState.value
                    val builderActive = currentBuilderActiveState.value
                    val selectedId = currentSelectedNodeIdState.value
                    val tool = currentActiveToolState.value
                    val currentPan = currentPanOffsetState.value
                    val refHeight = currentReferenceHeightState.value

                    if (nodes.isEmpty()) {
                        var releasedWithoutDrag = true
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                change.consume()
                                break
                            }
                            if ((change.position - startOffset).getDistance() > touchSlop) {
                                releasedWithoutDrag = false
                            }
                        }
                        if (releasedWithoutDrag && builderActive) {
                            val newNode = GraphNodeState(
                                id = "A",
                                label = "A",
                                x = 50f,
                                y = 50f,
                                state = ElementState.ACTIVE
                            )
                            val updatedNodes = listOf(newNode)
                            currentOnNodesChanged.value(updatedNodes)
                            currentOnGraphModified.value?.invoke(updatedNodes, edges)
                        }
                        return@awaitEachGesture
                    }

                    val geom = GraphCanvasGeometry.from(
                        nodes = nodes,
                        drawSize = size.toSize(),
                        panOffset = currentPan,
                        referenceHeight = refHeight
                    )
                    val hitThreshold = maxOf(24f, geom.nodeRadius + 10f)

                    // Check if hit on an existing node
                    val hitNode = nodes.minByOrNull { node ->
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

                    // Check if hit on an existing edge weight pill to cycle its weight (1..9)
                    val hitEdgeIndex = edges.indexOfFirst { edge ->
                        val fromNode = nodes.find { it.id == edge.from } ?: return@indexOfFirst false
                        val toNode = nodes.find { it.id == edge.to } ?: return@indexOfFirst false
                        val start = geom.toCanvasOffset(fromNode.x, fromNode.y)
                        val end = geom.toCanvasOffset(toNode.x, toNode.y)
                        val midX = (start.x + end.x) / 2f
                        val midY = (start.y + end.y) / 2f
                        val dx = startOffset.x - midX
                        val dy = startOffset.y - midY
                        sqrt(dx * dx + dy * dy) <= 22f
                    }

                    var dragStarted = false
                    var currentPos = startOffset
                    var isPanningViewport = false
                    var isMovingNode = false
                    var lastHoveredTargetId: String? = null

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }

                        val newPos = change.position
                        val dist = (newPos - startOffset).getDistance()

                        if (!dragStarted && dist > touchSlop) {
                            dragStarted = true
                            if (builderActive && hitNode != null && tool == GraphTool.MOVE) {
                                isMovingNode = true
                            } else if (builderActive && hitNode != null && tool == GraphTool.LINK) {
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
                                nodes = currentNodesState.value,
                                drawSize = size.toSize(),
                                panOffset = currentPanOffsetState.value,
                                referenceHeight = currentReferenceHeightState.value
                            )

                            if (isMovingNode && hitNode != null) {
                                val worldCoord = liveGeom.toWorldCoords(newPos)
                                val movedNodes = currentNodesState.value.map {
                                    if (it.id == hitNode.id) it.copy(x = worldCoord.x.coerceIn(5f, 250f), y = worldCoord.y.coerceIn(5f, 180f)) else it
                                }
                                currentOnNodesChanged.value(movedNodes)
                            } else if (isPanningViewport) {
                                val maxPan = 600f
                                val pan = currentPanOffsetState.value
                                val nextPan = Offset(
                                    x = (pan.x + delta.x).coerceIn(-maxPan, maxPan),
                                    y = (pan.y + delta.y).coerceIn(-maxPan, maxPan)
                                )
                                currentOnPanChanged.value(nextPan)
                            } else if (tool == GraphTool.LINK && hitNode != null) {
                                currentOnCurrentDragPosChanged.value(newPos)
                                val hovered = currentNodesState.value.filter { it.id != hitNode.id }.minByOrNull { node ->
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
                            }
                        }
                    }

                    // Gesture completed
                    if (dragStarted) {
                        if (isMovingNode && hitNode != null) {
                            currentOnGraphModified.value?.invoke(currentNodesState.value, edges)
                        } else if (tool == GraphTool.LINK && hitNode != null) {
                            val targetId = lastHoveredTargetId
                            if (targetId != null && targetId != hitNode.id) {
                                val edgeExists = edges.any {
                                    (it.from == hitNode.id && it.to == targetId) ||
                                    (it.from == targetId && it.to == hitNode.id)
                                }
                                if (!edgeExists) {
                                    val defaultWeight = ((edges.size * 2) % 9) + 1
                                    val newEdge = GraphEdgeState(
                                        from = hitNode.id,
                                        to = targetId,
                                        weight = defaultWeight,
                                        isHighlighted = true
                                    )
                                    val updatedEdges = edges + newEdge
                                    currentOnEdgesChanged.value(updatedEdges)
                                    currentOnGraphModified.value?.invoke(currentNodesState.value, updatedEdges)
                                }
                            }
                            currentOnDragStartNodeChanged.value(null)
                            currentOnCurrentDragPosChanged.value(null)
                            currentOnHoveredTargetNodeIdChanged.value(null)
                        }
                    } else {
                        // Tap detected
                        if (!builderActive) {
                            if (hitNode != null) {
                                currentOnNodeClick.value?.invoke(hitNode.id)
                                currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                            }
                        } else {
                            when (tool) {
                                GraphTool.MOVE -> {
                                    if (hitNode != null) {
                                        currentOnNodeClick.value?.invoke(hitNode.id)
                                        currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                                    } else {
                                        currentOnSelectedNodeIdChanged.value(null)
                                    }
                                }
                                GraphTool.ADD -> {
                                    if (hitNode != null) {
                                        currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                                    } else {
                                        val worldCoord = geom.toWorldCoords(startOffset)
                                        val nextLabel = currentGetNextNodeLabel.value(nodes)
                                        val newNode = GraphNodeState(
                                            id = nextLabel,
                                            label = nextLabel,
                                            x = worldCoord.x,
                                            y = worldCoord.y,
                                            state = ElementState.ACTIVE
                                        )
                                        var updatedEdges = edges
                                        if (selectedId != null) {
                                            val defaultWeight = ((edges.size * 2) % 9) + 1
                                            updatedEdges = updatedEdges + GraphEdgeState(
                                                from = selectedId,
                                                to = nextLabel,
                                                weight = defaultWeight,
                                                isHighlighted = true
                                            )
                                        }
                                        val updatedNodes = nodes + newNode
                                        currentOnNodesChanged.value(updatedNodes)
                                        currentOnEdgesChanged.value(updatedEdges)
                                        currentOnSelectedNodeIdChanged.value(nextLabel)
                                        currentOnGraphModified.value?.invoke(updatedNodes, updatedEdges)
                                    }
                                }
                                GraphTool.LINK -> {
                                    if (hitNode != null) {
                                        if (selectedId != null && selectedId != hitNode.id) {
                                            val fromId = selectedId
                                            val toId = hitNode.id
                                            val edgeExists = edges.any {
                                                (it.from == fromId && it.to == toId) ||
                                                (it.from == toId && it.to == fromId)
                                            }
                                            if (!edgeExists) {
                                                val defaultWeight = ((edges.size * 2) % 9) + 1
                                                val newEdge = GraphEdgeState(
                                                    from = fromId,
                                                    to = toId,
                                                    weight = defaultWeight,
                                                    isHighlighted = true
                                                )
                                                val updatedEdges = edges + newEdge
                                                currentOnEdgesChanged.value(updatedEdges)
                                                currentOnGraphModified.value?.invoke(nodes, updatedEdges)
                                            }
                                            currentOnSelectedNodeIdChanged.value(null)
                                        } else {
                                            currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                                        }
                                    }
                                }
                                GraphTool.WEIGHT -> {
                                    if (hitEdgeIndex >= 0) {
                                        val targetEdge = edges[hitEdgeIndex]
                                        val nextWeight = ((targetEdge.weight ?: 1) % 9) + 1
                                        val updatedEdges = edges.toMutableList().apply {
                                            this[hitEdgeIndex] = targetEdge.copy(weight = nextWeight, isHighlighted = true)
                                        }
                                        currentOnEdgesChanged.value(updatedEdges)
                                        currentOnGraphModified.value?.invoke(nodes, updatedEdges)
                                    } else if (hitNode != null) {
                                        currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                                    }
                                }
                                GraphTool.ENDPOINTS -> {
                                    if (hitNode != null) {
                                        val curStart = currentStartNodeIdState.value
                                        val curTarget = currentTargetNodeIdState.value
                                        val id = hitNode.id
                                        val (newStart, newTarget) = when {
                                            id == curStart -> null to curTarget
                                            id == curTarget -> curStart to null
                                            curStart == null -> id to curTarget
                                            curTarget == null -> curStart to id
                                            else -> id to null
                                        }
                                        currentOnEndpointsChanged.value?.invoke(newStart, newTarget)
                                    }
                                }
                                GraphTool.DELETE -> {
                                    if (hitNode != null) {
                                        val id = hitNode.id
                                        val updatedNodes = nodes.filter { it.id != id }
                                        val updatedEdges = edges.filter { it.from != id && it.to != id }
                                        if (id == currentStartNodeIdState.value || id == currentTargetNodeIdState.value) {
                                            val s = if (id == currentStartNodeIdState.value) null else currentStartNodeIdState.value
                                            val t = if (id == currentTargetNodeIdState.value) null else currentTargetNodeIdState.value
                                            currentOnEndpointsChanged.value?.invoke(s, t)
                                        }
                                        currentOnNodesChanged.value(updatedNodes)
                                        currentOnEdgesChanged.value(updatedEdges)
                                        currentOnSelectedNodeIdChanged.value(null)
                                        currentOnGraphModified.value?.invoke(updatedNodes, updatedEdges)
                                    } else if (hitEdgeIndex >= 0) {
                                        val updatedEdges = edges.filterIndexed { idx, _ -> idx != hitEdgeIndex }
                                        currentOnEdgesChanged.value(updatedEdges)
                                        currentOnGraphModified.value?.invoke(nodes, updatedEdges)
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
 * Builder instruction banner overlay. Appears only when [isBuilderActive]
 * is true. Always BottomCenter aligned.
 */
@Composable
fun GraphBuilderBanner(
    isBuilderActive: Boolean,
    activeTool: GraphTool = GraphTool.MOVE,
    selectedNodeId: String? = null,
    modifier: Modifier = Modifier
) {
    if (!isBuilderActive) return
    Box(
        modifier = modifier
            .padding(bottom = AlgoTokens.space3)
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(DarkBackground.copy(alpha = 0.90f))
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
    ) {
        val hintText = when (activeTool) {
            GraphTool.MOVE -> if (selectedNodeId != null) "Selected Node $selectedNodeId • Drag to reposition • Tap empty space to deselect" else "Drag any node to move • Drag background to pan viewport"
            GraphTool.ADD -> if (selectedNodeId != null) "Tap canvas to add node and auto-connect to $selectedNodeId" else "Tap anywhere on empty canvas to plant a new node"
            GraphTool.LINK -> if (selectedNodeId != null) "Selected Node $selectedNodeId: Tap target node to connect" else "Tap or drag from one node to another to create an edge"
            GraphTool.WEIGHT -> "Tap any edge weight pill to cycle weight (1..9)"
            GraphTool.ENDPOINTS -> "Tap node to set START (green) • Tap another to set TARGET (pink) • Tap to clear"
            GraphTool.DELETE -> "Tap any node or edge to delete it"
        }
        Text(
            text = hintText,
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Floating bottom glass toolbar for the node graph editor and fullscreen mode.
 */
@Composable
fun GraphFloatingToolbar(
    activeTool: GraphTool,
    onToolSelected: (GraphTool) -> Unit,
    onCenterView: () -> Unit,
    onResetGraph: () -> Unit,
    onToggleFullscreen: () -> Unit,
    isFullscreen: Boolean,
    canReset: Boolean,
    isPanned: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(AlgoTokens.space2)
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(DarkBackground.copy(alpha = 0.94f))
            .border(
                AlgoTokens.strokeThin,
                PrimaryCyan.copy(alpha = 0.35f),
                RoundedCornerShape(AlgoTokens.radiusMd)
            )
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            // 6 Tools
            GraphTool.entries.forEach { tool ->
                val isSelected = (tool == activeTool)
                val glyph = when (tool) {
                    GraphTool.MOVE -> AlgoGlyphs.Tap
                    GraphTool.ADD -> AlgoGlyphs.Plus
                    GraphTool.LINK -> AlgoGlyphs.Nodes
                    GraphTool.WEIGHT -> AlgoGlyphs.Speed
                    GraphTool.ENDPOINTS -> AlgoGlyphs.Target
                    GraphTool.DELETE -> AlgoGlyphs.Trash
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (isSelected) CyanSubtle else CanvasBackground)
                        .border(
                            AlgoTokens.strokeThin,
                            if (isSelected) PrimaryCyan else BorderSubtle,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .clickable { onToolSelected(tool) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = glyph,
                            contentDescription = tool.label,
                            tint = if (isSelected) PrimaryCyan else TextMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = tool.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) PrimaryCyan else TextMuted,
                            fontSize = AlgoType.microSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 18.dp)
                    .background(BorderSubtle)
            )

            // Center View (when panned)
            if (isPanned) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onCenterView() }
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Center",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            // Reset Graph (when modified)
            if (canReset) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onResetGraph() }
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Refresh,
                            contentDescription = "Reset",
                            tint = AccentPink,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentPink,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            // Fullscreen toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(if (isFullscreen) PrimaryCyan else CanvasBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        if (isFullscreen) PrimaryCyan else BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusXs)
                    )
                    .clickable { onToggleFullscreen() }
                    .padding(horizontal = 7.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (isFullscreen) AlgoGlyphs.Close else AlgoGlyphs.Expand,
                        contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
                        tint = if (isFullscreen) DarkBackground else PrimaryCyan,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = if (isFullscreen) "Exit" else "Fullscreen",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isFullscreen) DarkBackground else PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
