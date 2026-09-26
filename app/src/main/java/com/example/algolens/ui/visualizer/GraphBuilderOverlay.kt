package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
                        fontWeight = FontWeight.Bold
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
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "$dynamicNodeCount nodes • $dynamicEdgeCount edges",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontSize = AlgoType.microSize
            )
        }

        // Right actions: Center View (when panned) + Reset Graph (when modified) + Done (when active)
        Row(
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
    panOffset: Offset = Offset.Zero,
    referenceHeight: Float = 0f,
    onPanChanged: (Offset) -> Unit = {},
    onNodesChanged: (List<GraphNodeState>) -> Unit,
    onEdgesChanged: (List<GraphEdgeState>) -> Unit,
    onSelectedNodeIdChanged: (String?) -> Unit,
    onDragStartNodeChanged: (GraphNodeState?) -> Unit,
    onCurrentDragPosChanged: (Offset?) -> Unit,
    onHoveredTargetNodeIdChanged: (String?) -> Unit,
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)?,
    getNextNodeLabel: (List<GraphNodeState>) -> String,
    onNodeClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentNodesState = androidx.compose.runtime.rememberUpdatedState(dynamicNodes)
    val currentEdgesState = androidx.compose.runtime.rememberUpdatedState(dynamicEdges)
    val currentBuilderActiveState = androidx.compose.runtime.rememberUpdatedState(isBuilderActive)
    val currentSelectedNodeIdState = androidx.compose.runtime.rememberUpdatedState(selectedNodeId)
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
                    val activeDragSource = if (builderActive && hitNode != null) hitNode else null
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
                            if (activeDragSource != null) {
                                currentOnDragStartNodeChanged.value(activeDragSource)
                                currentOnCurrentDragPosChanged.value(newPos)
                            } else {
                                isPanningViewport = true
                            }
                        }

                        if (dragStarted) {
                            change.consume()
                            val delta = newPos - currentPos
                            currentPos = newPos

                            if (isPanningViewport) {
                                val maxPan = 600f
                                val pan = currentPanOffsetState.value
                                val nextPan = Offset(
                                    x = (pan.x + delta.x).coerceIn(-maxPan, maxPan),
                                    y = (pan.y + delta.y).coerceIn(-maxPan, maxPan)
                                )
                                currentOnPanChanged.value(nextPan)
                            } else if (activeDragSource != null) {
                                currentOnCurrentDragPosChanged.value(newPos)
                                val liveGeom = GraphCanvasGeometry.from(
                                    nodes = nodes,
                                    drawSize = size.toSize(),
                                    panOffset = currentPanOffsetState.value,
                                    referenceHeight = currentReferenceHeightState.value
                                )
                                val hovered = nodes.filter { it.id != activeDragSource.id }.minByOrNull { node ->
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
                        if (!isPanningViewport && activeDragSource != null) {
                            val targetId = lastHoveredTargetId
                            if (targetId != null && targetId != activeDragSource.id) {
                                val edgeExists = edges.any {
                                    (it.from == activeDragSource.id && it.to == targetId) ||
                                    (it.from == targetId && it.to == activeDragSource.id)
                                }
                                if (!edgeExists) {
                                    val defaultWeight = ((edges.size * 2) % 9) + 1
                                    val newEdge = GraphEdgeState(
                                        from = activeDragSource.id,
                                        to = targetId,
                                        weight = defaultWeight,
                                        isHighlighted = true
                                    )
                                    val updatedEdges = edges + newEdge
                                    currentOnEdgesChanged.value(updatedEdges)
                                    currentOnGraphModified.value?.invoke(nodes, updatedEdges)
                                }
                            }
                        }
                        currentOnDragStartNodeChanged.value(null)
                        currentOnCurrentDragPosChanged.value(null)
                        currentOnHoveredTargetNodeIdChanged.value(null)
                    } else {
                        // Tap detected
                        if (!builderActive) {
                            if (hitNode != null) {
                                currentOnNodeClick.value?.invoke(hitNode.id)
                                currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                            }
                        } else {
                            if (hitEdgeIndex >= 0 && hitNode == null) {
                                val targetEdge = edges[hitEdgeIndex]
                                val nextWeight = ((targetEdge.weight ?: 1) % 9) + 1
                                val updatedEdges = edges.toMutableList().apply {
                                    this[hitEdgeIndex] = targetEdge.copy(weight = nextWeight, isHighlighted = true)
                                }
                                currentOnEdgesChanged.value(updatedEdges)
                                currentOnGraphModified.value?.invoke(nodes, updatedEdges)
                            } else if (hitNode != null) {
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
        val hintText = if (selectedNodeId != null) {
            "Selected Node $selectedNodeId: Tap another node to connect • Tap empty space to add & connect"
        } else {
            "Tap node to select • Drag between nodes to connect • Tap empty space to add node"
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
