package com.example.algolens.ui.visualizer

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interactive 2D Graph & Tree Canvas Visualizer and Builder.
 * Supports:
 * - Real-time state visualization of algorithm steps (BFS, DFS, BST, Heap)
 * - User touch gestures (`detectTapGestures` to spawn nodes at tapped coordinates)
 * - User drag gestures (`detectDragGestures` to drag connecting edges between nodes)
 * - Glowing halos (`AccentPink`, `AccentGreen`, `PrimaryCyan`) rendered underneath interactive touch targets.
 */
@Composable
fun GraphTreeVisualizer(
    step: VisualizerStep,
    modifier: Modifier = Modifier,
    onGraphModified: ((List<GraphNodeState>, List<GraphEdgeState>) -> Unit)? = null
) {
    // Dynamic interactive node & edge states
    var dynamicNodes by remember(step) { mutableStateOf(step.nodes) }
    var dynamicEdges by remember(step) { mutableStateOf(step.edges) }

    // Gesture builder state
    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var dragStartNode by remember { mutableStateOf<GraphNodeState?>(null) }
    var currentDragPos by remember { mutableStateOf<Offset?>(null) }
    var hoveredTargetNodeId by remember { mutableStateOf<String?>(null) }
    var isBuilderActive by remember { mutableStateOf(false) }

    // Sync when step changes
    LaunchedEffect(step.nodes, step.edges) {
        dynamicNodes = step.nodes
        dynamicEdges = step.edges
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .border(
                1.dp,
                if (isBuilderActive) BorderCyan else BorderSubtle,
                RoundedCornerShape(12.dp)
            )
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Interactive Builder Toolbar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isBuilderActive) CyanSubtle else CanvasBackground)
                            .border(
                                1.dp,
                                if (isBuilderActive) PrimaryCyan else BorderSubtle,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { isBuilderActive = !isBuilderActive }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gesture,
                                contentDescription = null,
                                tint = if (isBuilderActive) PrimaryCyan else TextMuted,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = if (isBuilderActive) "Builder: Active" else "Interactive Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isBuilderActive) PrimaryCyan else TextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "${dynamicNodes.size} nodes • ${dynamicEdges.size} edges",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDark,
                        fontSize = 7.5.sp
                    )
                }

                // Reset / Clear buttons
                if (dynamicNodes.size != step.nodes.size || dynamicEdges.size != step.edges.size) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CanvasBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                            .clickable {
                                dynamicNodes = step.nodes
                                dynamicEdges = step.edges
                                selectedNodeId = null
                                dragStartNode = null
                                currentDragPos = null
                                onGraphModified?.invoke(step.nodes, step.edges)
                            }
                            .padding(horizontal = 5.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = AccentPink,
                            modifier = Modifier.size(9.dp)
                        )
                        Text(
                            text = "Reset Graph",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentPink,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Main Canvas with Gesture Detectors ──
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CanvasBackground)
            ) {
                // Render-phase pulse for the active node's halo (draw-read only).
                val activeHaloPulse = rememberInfiniteTransition(label = "activeNodeHalo")
                    .animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1100),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "activeNodeHaloValue"
                    )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(dynamicNodes, dynamicEdges, isBuilderActive) {
                            // Tap gesture: Spawn new node or select existing node
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
                                    dynamicNodes = updatedNodes
                                    onGraphModified?.invoke(updatedNodes, dynamicEdges)
                                    return@detectTapGestures
                                }

                                // Coordinate bounds mapping
                                val maxX = (nodes.maxOfOrNull { it.x } ?: 100f).coerceAtLeast(100f)
                                val maxY = (nodes.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)
                                val minX = (nodes.minOfOrNull { it.x } ?: 0f).coerceAtMost(0f)
                                val minY = (nodes.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)

                                val spanX = (maxX - minX + 30f).coerceAtLeast(1f)
                                val spanY = (maxY - minY + 30f).coerceAtLeast(1f)
                                val padding = 36f
                                val drawWidth = size.width - (padding * 2)
                                val drawHeight = size.height - (padding * 2)

                                fun toOffset(nx: Float, ny: Float): Offset {
                                    val cx = padding + ((nx - minX + 15f) / spanX) * drawWidth
                                    val cy = padding + ((ny - minY + 15f) / spanY) * drawHeight
                                    return Offset(cx, cy)
                                }

                                // Check if tapped on an existing node (radius threshold = 28px)
                                val hitNode = nodes.find { node ->
                                    val c = toOffset(node.x, node.y)
                                    val dx = tapOffset.x - c.x
                                    val dy = tapOffset.y - c.y
                                    sqrt(dx * dx + dy * dy) <= 30f
                                }

                                if (hitNode != null) {
                                    // Tapped on existing node -> toggle selection or connect if another node was selected
                                    if (selectedNodeId != null && selectedNodeId != hitNode.id) {
                                        // Connect selected node to this node
                                        val fromId = selectedNodeId!!
                                        val toId = hitNode.id
                                        val edgeExists = dynamicEdges.any { (it.from == fromId && it.to == toId) || (it.from == toId && it.to == fromId) }
                                        if (!edgeExists) {
                                            val newEdge = GraphEdgeState(from = fromId, to = toId, isHighlighted = true)
                                            val updatedEdges = dynamicEdges + newEdge
                                            dynamicEdges = updatedEdges
                                            onGraphModified?.invoke(dynamicNodes, updatedEdges)
                                        }
                                        selectedNodeId = null
                                    } else {
                                        selectedNodeId = if (selectedNodeId == hitNode.id) null else hitNode.id
                                    }
                                } else {
                                    // Tapped on empty canvas -> Spawn new node at coordinate
                                    val nx = ((tapOffset.x - padding) / drawWidth.coerceAtLeast(1f)) * spanX + minX - 15f
                                    val ny = ((tapOffset.y - padding) / drawHeight.coerceAtLeast(1f)) * spanY + minY - 15f

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
                                        val fromId = selectedNodeId!!
                                        updatedEdges = updatedEdges + GraphEdgeState(from = fromId, to = nextLabel, isHighlighted = true)
                                    }

                                    val updatedNodes = nodes + newNode
                                    dynamicNodes = updatedNodes
                                    dynamicEdges = updatedEdges
                                    selectedNodeId = nextLabel
                                    onGraphModified?.invoke(updatedNodes, updatedEdges)
                                }
                            }
                        }
                        .pointerInput(dynamicNodes, dynamicEdges) {
                            // Drag gesture: Drag from a node to connect to another node
                            detectDragGestures(
                                onDragStart = { startOffset ->
                                    val nodes = dynamicNodes
                                    if (nodes.isEmpty()) return@detectDragGestures

                                    val maxX = (nodes.maxOfOrNull { it.x } ?: 100f).coerceAtLeast(100f)
                                    val maxY = (nodes.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)
                                    val minX = (nodes.minOfOrNull { it.x } ?: 0f).coerceAtMost(0f)
                                    val minY = (nodes.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)

                                    val spanX = (maxX - minX + 30f).coerceAtLeast(1f)
                                    val spanY = (maxY - minY + 30f).coerceAtLeast(1f)
                                    val padding = 36f
                                    val drawWidth = size.width - (padding * 2)
                                    val drawHeight = size.height - (padding * 2)

                                    fun toOffset(nx: Float, ny: Float): Offset {
                                        val cx = padding + ((nx - minX + 15f) / spanX) * drawWidth
                                        val cy = padding + ((ny - minY + 15f) / spanY) * drawHeight
                                        return Offset(cx, cy)
                                    }

                                    val hit = nodes.find { node ->
                                        val c = toOffset(node.x, node.y)
                                        val dx = startOffset.x - c.x
                                        val dy = startOffset.y - c.y
                                        sqrt(dx * dx + dy * dy) <= 30f
                                    }

                                    if (hit != null) {
                                        dragStartNode = hit
                                        currentDragPos = startOffset
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (dragStartNode != null) {
                                        val newPos = (currentDragPos ?: change.position) + dragAmount
                                        currentDragPos = newPos

                                        // Find if hovering over a target node
                                        val nodes = dynamicNodes
                                        val maxX = (nodes.maxOfOrNull { it.x } ?: 100f).coerceAtLeast(100f)
                                        val maxY = (nodes.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)
                                        val minX = (nodes.minOfOrNull { it.x } ?: 0f).coerceAtMost(0f)
                                        val minY = (nodes.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)
                                        val spanX = (maxX - minX + 30f).coerceAtLeast(1f)
                                        val spanY = (maxY - minY + 30f).coerceAtLeast(1f)
                                        val padding = 36f
                                        val drawWidth = size.width - (padding * 2)
                                        val drawHeight = size.height - (padding * 2)

                                        fun toOffset(nx: Float, ny: Float): Offset {
                                            val cx = padding + ((nx - minX + 15f) / spanX) * drawWidth
                                            val cy = padding + ((ny - minY + 15f) / spanY) * drawHeight
                                            return Offset(cx, cy)
                                        }

                                        val hovered = nodes.find { node ->
                                            if (node.id == dragStartNode?.id) return@find false
                                            val c = toOffset(node.x, node.y)
                                            val dx = newPos.x - c.x
                                            val dy = newPos.y - c.y
                                            sqrt(dx * dx + dy * dy) <= 30f
                                        }
                                        hoveredTargetNodeId = hovered?.id
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
                                            dynamicEdges = updatedEdges
                                            onGraphModified?.invoke(dynamicNodes, updatedEdges)
                                        }
                                    }
                                    dragStartNode = null
                                    currentDragPos = null
                                    hoveredTargetNodeId = null
                                },
                                onDragCancel = {
                                    dragStartNode = null
                                    currentDragPos = null
                                    hoveredTargetNodeId = null
                                }
                            )
                        }
                ) {
                    val nodes = dynamicNodes
                    val edges = dynamicEdges
                    if (nodes.isEmpty()) return@Canvas

                    // Compute coordinate bounds
                    val maxX = (nodes.maxOfOrNull { it.x } ?: 100f).coerceAtLeast(100f)
                    val maxY = (nodes.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)
                    val minX = (nodes.minOfOrNull { it.x } ?: 0f).coerceAtMost(0f)
                    val minY = (nodes.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)

                    val spanX = (maxX - minX + 30f).coerceAtLeast(1f)
                    val spanY = (maxY - minY + 30f).coerceAtLeast(1f)

                    val padding = 36f
                    val drawWidth = size.width - (padding * 2)
                    val drawHeight = size.height - (padding * 2)

                    fun toCanvasOffset(nx: Float, ny: Float): Offset {
                        val cx = padding + ((nx - minX + 15f) / spanX) * drawWidth
                        val cy = padding + ((ny - minY + 15f) / spanY) * drawHeight
                        return Offset(cx, cy)
                    }

                    // ── 1. Draw Existing Edges ──
                    for (edge in edges) {
                        val fromNode = nodes.find { it.id == edge.from } ?: continue
                        val toNode = nodes.find { it.id == edge.to } ?: continue

                        val start = toCanvasOffset(fromNode.x, fromNode.y)
                        val end = toCanvasOffset(toNode.x, toNode.y)

                        val isHighlighted = edge.isHighlighted
                        val edgeColor = if (isHighlighted) AccentGreen else Color(0xFF1E293B)
                        val strokeWidth = if (isHighlighted) 4.5f else 2f

                        // Draw path highlight glow
                        if (isHighlighted) {
                            drawLine(
                                color = AccentGreen.copy(alpha = 0.25f),
                                start = start,
                                end = end,
                                strokeWidth = 9f
                            )
                        }

                        drawLine(
                            color = edgeColor,
                            start = start,
                            end = end,
                            strokeWidth = strokeWidth
                        )

                        // Draw arrow head if directed
                        if (edge.isDirected) {
                            val angle = atan2(end.y - start.y, end.x - start.x)
                            val nodeRadius = 22f
                            val arrowTip = Offset(
                                end.x - nodeRadius * cos(angle).toFloat(),
                                end.y - nodeRadius * sin(angle).toFloat()
                            )
                            val arrowSize = 10f
                            val leftWing = Offset(
                                arrowTip.x - arrowSize * cos(angle - 0.45f).toFloat(),
                                arrowTip.y - arrowSize * sin(angle - 0.45f).toFloat()
                            )
                            val rightWing = Offset(
                                arrowTip.x - arrowSize * cos(angle + 0.45f).toFloat(),
                                arrowTip.y - arrowSize * sin(angle + 0.45f).toFloat()
                            )
                            drawLine(color = edgeColor, start = arrowTip, end = leftWing, strokeWidth = strokeWidth)
                            drawLine(color = edgeColor, start = arrowTip, end = rightWing, strokeWidth = strokeWidth)
                        }

                        // Draw edge weight badge if present
                        if (edge.weight != null) {
                            val midX = (start.x + end.x) / 2
                            val midY = (start.y + end.y) / 2

                            drawRect(
                                color = Color(0xFF060A14),
                                topLeft = Offset(midX - 14f, midY - 10f),
                                size = Size(28f, 20f)
                            )
                            drawRect(
                                color = if (isHighlighted) AccentGreen else Color(0xFF1E293B),
                                topLeft = Offset(midX - 14f, midY - 10f),
                                size = Size(28f, 20f),
                                style = Stroke(width = 1f)
                            )

                            drawContext.canvas.nativeCanvas.apply {
                                val paint = Paint().apply {
                                    isAntiAlias = true
                                    color = (if (isHighlighted) AccentGreen else TextMuted).toArgb()
                                    textSize = 18f
                                    textAlign = Paint.Align.CENTER
                                    typeface = Typeface.MONOSPACE
                                }
                                drawText(edge.weight.toString(), midX, midY + 6f, paint)
                            }
                        }
                    }

                    // ── 2. Draw Interactive Drag Preview Line (Rubber-Band Edge) ──
                    val dragFrom = dragStartNode
                    val dragTo = currentDragPos
                    if (dragFrom != null && dragTo != null) {
                        val startPos = toCanvasOffset(dragFrom.x, dragFrom.y)
                        // Glowing drag line
                        drawLine(
                            color = PrimaryCyan.copy(alpha = 0.35f),
                            start = startPos,
                            end = dragTo,
                            strokeWidth = 8f
                        )
                        drawLine(
                            color = PrimaryCyan,
                            start = startPos,
                            end = dragTo,
                            strokeWidth = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                        // Endpoint pulse
                        drawCircle(
                            color = PrimaryCyan,
                            radius = 6f,
                            center = dragTo
                        )
                    }

                    // ── 3. Draw Nodes with Glowing Halos ──
                    for (node in nodes) {
                        val center = toCanvasOffset(node.x, node.y)
                        val isVisited = step.visitedNodeIds.contains(node.id) || node.state == ElementState.VISITED
                        val isActive = step.activeNodeId == node.id || node.state == ElementState.ACTIVE || selectedNodeId == node.id
                        val isHovered = hoveredTargetNodeId == node.id
                        val isDragSource = dragStartNode?.id == node.id

                        val (fillColor, strokeColor, textColor) = when {
                            isHovered -> Triple(PrimaryCyan.copy(alpha = 0.40f), PrimaryCyan, PrimaryCyan)
                            isDragSource -> Triple(SecondaryPurple.copy(alpha = 0.40f), SecondaryPurple, PurpleGlow)
                            isActive -> Triple(AccentGreen.copy(alpha = 0.28f), AccentGreen, AccentGreen)
                            node.state == ElementState.COMPARING -> Triple(AccentYellow.copy(alpha = 0.25f), AccentYellow, AccentYellow)
                            node.state == ElementState.SWAPPING -> Triple(AccentRed.copy(alpha = 0.25f), AccentRed, Color.White)
                            node.state == ElementState.FOUND || node.state == ElementState.TARGET -> Triple(PrimaryCyan.copy(alpha = 0.25f), PrimaryCyan, PrimaryCyan)
                            node.state == ElementState.PIVOT -> Triple(AccentPink.copy(alpha = 0.25f), AccentPink, Color.White)
                            isVisited || node.state == ElementState.SORTED -> Triple(SecondaryPurple.copy(alpha = 0.20f), SecondaryPurple, TextPrimary)
                            else -> Triple(CardBackground, PrimaryCyan.copy(alpha = 0.5f), TextSecondary)
                        }

                        // ── Halo Glow Rendering Underneath Touch Targets ──
                        if (isActive || isHovered || isDragSource) {
                            val haloColor = if (isHovered || isDragSource) PrimaryCyan else AccentGreen
                            // Outer ambient halo (breathes when node is the active one)
                            val breathe = if (isActive) activeHaloPulse.value else 0f
                            drawCircle(
                                color = haloColor.copy(alpha = 0.14f + 0.10f * breathe),
                                radius = 34f + 4f * breathe,
                                center = center
                            )
                            // Inner sharp halo
                            drawCircle(
                                color = haloColor.copy(alpha = 0.28f + 0.14f * breathe),
                                radius = 26f + 2f * breathe,
                                center = center
                            )
                        } else if (node.state == ElementState.PIVOT) {
                            drawCircle(
                                color = AccentPink.copy(alpha = 0.20f),
                                radius = 28f,
                                center = center
                            )
                        }

                        // Node background circle
                        drawCircle(
                            color = fillColor,
                            radius = 20f,
                            center = center
                        )

                        // Node border stroke
                        drawCircle(
                            color = strokeColor,
                            radius = 20f,
                            center = center,
                            style = Stroke(width = if (isActive || isHovered || isDragSource) 3f else 1.8f)
                        )

                        // Node label text
                        drawContext.canvas.nativeCanvas.apply {
                            val textPaint = Paint().apply {
                                isAntiAlias = true
                                color = textColor.toArgb()
                                textSize = 20f
                                textAlign = Paint.Align.CENTER
                                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                            }
                            drawText(node.label, center.x, center.y + 7f, textPaint)
                        }
                    }
                }

                // Builder instruction banner overlay
                if (isBuilderActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground.copy(alpha = 0.85f))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "💡 Tap empty space to add node • Drag between nodes to connect",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = 7.5.sp
                        )
                    }
                }
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

