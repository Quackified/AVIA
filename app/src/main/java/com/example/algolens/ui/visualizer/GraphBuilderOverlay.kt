package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

            Text(
                text = "$dynamicNodeCount nodes • $dynamicEdgeCount edges",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontSize = AlgoType.microSize
            )
        }

        // Right actions: Center View (when panned) + Reset Graph (when modified)
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
                detectTapGestures { tapOffset ->
                    val nodes = currentNodesState.value
                    val edges = currentEdgesState.value
                    val builderActive = currentBuilderActiveState.value
                    val selectedId = currentSelectedNodeIdState.value
                    val currentPan = currentPanOffsetState.value
                    val refHeight = currentReferenceHeightState.value

                    if (nodes.isEmpty()) {
                        if (!builderActive) return@detectTapGestures
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
                        return@detectTapGestures
                    }

                    val geom = GraphCanvasGeometry.from(
                        nodes = nodes,
                        drawSize = size.toSize(),
                        panOffset = currentPan,
                        referenceHeight = refHeight
                    )
                    val hitThreshold = maxOf(24f, geom.nodeRadius + 10f)

                    // Check if tapped on an existing node
                    val hitNode = nodes.minByOrNull { node ->
                        val c = geom.toCanvasOffset(node.x, node.y)
                        val dx = tapOffset.x - c.x
                        val dy = tapOffset.y - c.y
                        sqrt(dx * dx + dy * dy)
                    }?.takeIf { node ->
                        val c = geom.toCanvasOffset(node.x, node.y)
                        val dx = tapOffset.x - c.x
                        val dy = tapOffset.y - c.y
                        sqrt(dx * dx + dy * dy) <= hitThreshold
                    }

                    // When builder mode is OFF, tapping a node invokes onNodeClick (for Challenge Mode / inspection)
                    if (!builderActive) {
                        if (hitNode != null) {
                            currentOnNodeClick.value?.invoke(hitNode.id)
                            currentOnSelectedNodeIdChanged.value(if (selectedId == hitNode.id) null else hitNode.id)
                        }
                        return@detectTapGestures
                    }

                    // Check if tapped on an existing edge weight pill to cycle its weight (1..9)
                    val hitEdgeIndex = edges.indexOfFirst { edge ->
                        val fromNode = nodes.find { it.id == edge.from } ?: return@indexOfFirst false
                        val toNode = nodes.find { it.id == edge.to } ?: return@indexOfFirst false
                        val start = geom.toCanvasOffset(fromNode.x, fromNode.y)
                        val end = geom.toCanvasOffset(toNode.x, toNode.y)
                        val midX = (start.x + end.x) / 2f
                        val midY = (start.y + end.y) / 2f
                        val dx = tapOffset.x - midX
                        val dy = tapOffset.y - midY
                        sqrt(dx * dx + dy * dy) <= 20f
                    }

                    if (hitEdgeIndex >= 0 && hitNode == null) {
                        val targetEdge = edges[hitEdgeIndex]
                        val nextWeight = ((targetEdge.weight ?: 1) % 9) + 1
                        val updatedEdges = edges.toMutableList().apply {
                            this[hitEdgeIndex] = targetEdge.copy(weight = nextWeight, isHighlighted = true)
                        }
                        currentOnEdgesChanged.value(updatedEdges)
                        currentOnGraphModified.value?.invoke(nodes, updatedEdges)
                        return@detectTapGestures
                    }

                    if (hitNode != null) {
                        // Tapped on existing node -> toggle selection or connect if another node was selected
                        if (selectedId != null && selectedId != hitNode.id) {
                            val fromId = selectedId
                            val toId = hitNode.id
                            val edgeExists = edges.any {
                                (it.from == fromId && it.to == toId) || (it.from == toId && it.to == fromId)
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
                        // Tapped on empty canvas -> Spawn new node at world coordinate accounting for panOffset
                        val worldCoord = geom.toWorldCoords(tapOffset)

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
            .pointerInput(Unit) {
                var activeDragSource: GraphNodeState? = null
                var activeHoverTargetId: String? = null
                var isPanningViewport = false

                detectDragGestures(
                    onDragStart = { startOffset ->
                        val nodes = currentNodesState.value
                        val builderActive = currentBuilderActiveState.value
                        val currentPan = currentPanOffsetState.value
                        val refHeight = currentReferenceHeightState.value

                        if (!builderActive || nodes.isEmpty()) {
                            activeDragSource = null
                            activeHoverTargetId = null
                            isPanningViewport = true
                            return@detectDragGestures
                        }

                        val geom = GraphCanvasGeometry.from(
                            nodes = nodes,
                            drawSize = size.toSize(),
                            panOffset = currentPan,
                            referenceHeight = refHeight
                        )
                        val hitThreshold = maxOf(24f, geom.nodeRadius + 10f)
                        val hit = nodes.minByOrNull { node ->
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

                        activeDragSource = hit
                        activeHoverTargetId = null
                        isPanningViewport = (hit == null)
                        if (hit != null) {
                            currentOnDragStartNodeChanged.value(hit)
                            currentOnCurrentDragPosChanged.value(startOffset)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (isPanningViewport) {
                            val currentPan = currentPanOffsetState.value
                            val maxPan = 600f
                            val nextPan = Offset(
                                x = (currentPan.x + dragAmount.x).coerceIn(-maxPan, maxPan),
                                y = (currentPan.y + dragAmount.y).coerceIn(-maxPan, maxPan)
                            )
                            currentOnPanChanged.value(nextPan)
                            return@detectDragGestures
                        }

                        val source = activeDragSource ?: return@detectDragGestures
                        val newPos = change.position
                        currentOnCurrentDragPosChanged.value(newPos)

                        val nodes = currentNodesState.value
                        val geom = GraphCanvasGeometry.from(
                            nodes = nodes,
                            drawSize = size.toSize(),
                            panOffset = currentPanOffsetState.value,
                            referenceHeight = currentReferenceHeightState.value
                        )
                        val hitThreshold = maxOf(24f, geom.nodeRadius + 10f)
                        val hovered = nodes.filter { it.id != source.id }.minByOrNull { node ->
                            val c = geom.toCanvasOffset(node.x, node.y)
                            val dx = newPos.x - c.x
                            val dy = newPos.y - c.y
                            sqrt(dx * dx + dy * dy)
                        }?.takeIf { node ->
                            val c = geom.toCanvasOffset(node.x, node.y)
                            val dx = newPos.x - c.x
                            val dy = newPos.y - c.y
                            sqrt(dx * dx + dy * dy) <= hitThreshold
                        }
                        activeHoverTargetId = hovered?.id
                        currentOnHoveredTargetNodeIdChanged.value(hovered?.id)
                    },
                    onDragEnd = {
                        if (!isPanningViewport) {
                            val startNode = activeDragSource
                            val targetId = activeHoverTargetId
                            val nodes = currentNodesState.value
                            val edges = currentEdgesState.value
                            if (startNode != null && targetId != null && startNode.id != targetId) {
                                val edgeExists = edges.any {
                                    (it.from == startNode.id && it.to == targetId) || (it.from == targetId && it.to == startNode.id)
                                }
                                if (!edgeExists) {
                                    val defaultWeight = ((edges.size * 2) % 9) + 1
                                    val newEdge = GraphEdgeState(
                                        from = startNode.id,
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
                        isPanningViewport = false
                        activeDragSource = null
                        activeHoverTargetId = null
                        currentOnDragStartNodeChanged.value(null)
                        currentOnCurrentDragPosChanged.value(null)
                        currentOnHoveredTargetNodeIdChanged.value(null)
                    },
                    onDragCancel = {
                        isPanningViewport = false
                        activeDragSource = null
                        activeHoverTargetId = null
                        currentOnDragStartNodeChanged.value(null)
                        currentOnCurrentDragPosChanged.value(null)
                        currentOnHoveredTargetNodeIdChanged.value(null)
                    }
                )
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
    modifier: Modifier = Modifier
) {
    if (!isBuilderActive) return
    Box(
        modifier = modifier
            .padding(bottom = AlgoTokens.space3)
            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
            .background(DarkBackground.copy(alpha = 0.85f))
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXxs))
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
    ) {
        Text(
            text = "💡 Tap empty space to add node • Drag between nodes to connect",
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan,
            fontSize = AlgoType.microSize
        )
    }
}
