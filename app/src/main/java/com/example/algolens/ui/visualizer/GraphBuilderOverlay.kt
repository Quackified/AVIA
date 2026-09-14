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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Refresh
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

        // Reset / Clear buttons
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

/**
 * Tap/drag gesture detector that mutates the live graph. Owns the logic
 * for: spawning the first node on an empty canvas, spawning subsequent nodes
 * at tap coordinates, tapping an existing node to select/connect, and
 * dragging from one node to another to create an edge.
 *
 * The caller passes state readers and setter callbacks because the source
 * of truth lives in the public [GraphTreeVisualizer] shell.
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.pointerInput(dynamicNodes, dynamicEdges, isBuilderActive) {
            detectTapGestures { tapOffset ->
                val nodes = dynamicNodes
                if (nodes.isEmpty()) {
                    // First node
                    val newNode = GraphNodeState(
                        id = "A",
                        label = "A",
                        x = 50f,
                        y = 50f,
                        state = ElementState.ACTIVE
                    )
                    val updatedNodes = listOf(newNode)
                    onNodesChanged(updatedNodes)
                    onGraphModified?.invoke(updatedNodes, dynamicEdges)
                    return@detectTapGestures
                }

                val geom = GraphCanvasGeometry.from(nodes, size.toSize())

                // Check if tapped on an existing node (radius threshold = 28px)
                val hitNode = nodes.find { node ->
                    val c = geom.toCanvasOffset(node.x, node.y)
                    val dx = tapOffset.x - c.x
                    val dy = tapOffset.y - c.y
                    sqrt(dx * dx + dy * dy) <= 30f
                }

                if (hitNode != null) {
                    // Tapped on existing node -> toggle selection or connect if another node was selected
                    if (selectedNodeId != null && selectedNodeId != hitNode.id) {
                        // Connect selected node to this node
                        val fromId = selectedNodeId
                        val toId = hitNode.id
                        val edgeExists = dynamicEdges.any { (it.from == fromId && it.to == toId) || (it.from == toId && it.to == fromId) }
                        if (!edgeExists) {
                            val newEdge = GraphEdgeState(from = fromId, to = toId, isHighlighted = true)
                            val updatedEdges = dynamicEdges + newEdge
                            onEdgesChanged(updatedEdges)
                            onGraphModified?.invoke(dynamicNodes, updatedEdges)
                        }
                        onSelectedNodeIdChanged(null)
                    } else {
                        onSelectedNodeIdChanged(if (selectedNodeId == hitNode.id) null else hitNode.id)
                    }
                } else {
                    // Tapped on empty canvas -> Spawn new node at coordinate
                    val nx = ((tapOffset.x - geom.padding) / geom.drawWidth.coerceAtLeast(1f)) * geom.spanX + geom.minX - 15f
                    val ny = ((tapOffset.y - geom.padding) / geom.drawHeight.coerceAtLeast(1f)) * geom.spanY + geom.minY - 15f

                    val nextLabel = getNextNodeLabel(nodes)
                    val newNode = GraphNodeState(
                        id = nextLabel,
                        label = nextLabel,
                        x = nx,
                        y = ny,
                        state = ElementState.ACTIVE
                    )

                    var updatedEdges = dynamicEdges
                    // If a node was selected, auto-connect to new node
                    if (selectedNodeId != null) {
                        val fromId = selectedNodeId
                        updatedEdges = updatedEdges + GraphEdgeState(from = fromId, to = nextLabel, isHighlighted = true)
                    }

                    val updatedNodes = nodes + newNode
                    onNodesChanged(updatedNodes)
                    onEdgesChanged(updatedEdges)
                    onSelectedNodeIdChanged(nextLabel)
                    onGraphModified?.invoke(updatedNodes, updatedEdges)
                }
            }
        }.pointerInput(dynamicNodes, dynamicEdges) {
            detectDragGestures(
                onDragStart = { startOffset ->
                    val nodes = dynamicNodes
                    if (nodes.isEmpty()) return@detectDragGestures

                    val geom = GraphCanvasGeometry.from(nodes, size.toSize())
                    val hit = nodes.find { node ->
                        val c = geom.toCanvasOffset(node.x, node.y)
                        val dx = startOffset.x - c.x
                        val dy = startOffset.y - c.y
                        sqrt(dx * dx + dy * dy) <= 30f
                    }

                    if (hit != null) {
                        onDragStartNodeChanged(hit)
                        onCurrentDragPosChanged(startOffset)
                    }
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    if (dragStartNode != null) {
                        val newPos = (change.position) + dragAmount
                        onCurrentDragPosChanged(newPos)

                        // Find if hovering over a target node
                        val nodes = dynamicNodes
                        val geom = GraphCanvasGeometry.from(nodes, size.toSize())
                        val hovered = nodes.find { node ->
                            if (node.id == dragStartNode.id) return@find false
                            val c = geom.toCanvasOffset(node.x, node.y)
                            val dx = newPos.x - c.x
                            val dy = newPos.y - c.y
                            sqrt(dx * dx + dy * dy) <= 30f
                        }
                        onHoveredTargetNodeIdChanged(hovered?.id)
                    }
                },
                onDragEnd = {
                    val startNode = dragStartNode
                    val targetId = hoveredTargetNodeId
                    if (startNode != null && targetId != null && startNode.id != targetId) {
                        val edgeExists = dynamicEdges.any {
                            (it.from == startNode.id && it.to == targetId) || (it.from == targetId && it.to == startNode.id)
                        }
                        if (!edgeExists) {
                            val newEdge = GraphEdgeState(
                                from = startNode.id,
                                to = targetId,
                                isHighlighted = true
                            )
                            val updatedEdges = dynamicEdges + newEdge
                            onEdgesChanged(updatedEdges)
                            onGraphModified?.invoke(dynamicNodes, updatedEdges)
                        }
                    }
                    onDragStartNodeChanged(null)
                    onCurrentDragPosChanged(null)
                    onHoveredTargetNodeIdChanged(null)
                },
                onDragCancel = {
                    onDragStartNodeChanged(null)
                    onCurrentDragPosChanged(null)
                    onHoveredTargetNodeIdChanged(null)
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
            .padding(horizontal = AlgoTokens.space4, vertical = 3.dp)
    ) {
        Text(
            text = "💡 Tap empty space to add node • Drag between nodes to connect",
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan,
            fontSize = AlgoType.microSize
        )
    }
}
