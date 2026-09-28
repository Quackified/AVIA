package com.example.algolens.ui.visualizer

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.GraphEdgeDefault
import com.example.algolens.ui.theme.CanvasBackground
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure rendering of the graph/tree canvas — edges, weight pills,
 * drag preview, and nodes with halos. Owns the `activeHaloPulse` infinite transition
 * that breathes the active node's halo.
 *
 * Adheres to `DESIGN.md`: no extra Cartesian/dot canvas textures are drawn here
 * because `AlgoWorkspaceBackground` owns the single workspace texture.
 */
@Composable
fun GraphTreeRenderer(
    nodes: List<GraphNodeState>,
    edges: List<GraphEdgeState>,
    visitedNodeIds: Set<String>,
    activeNodeId: String?,
    selectedNodeId: String?,
    hoveredTargetNodeId: String?,
    dragStartNode: GraphNodeState?,
    currentDragPos: Offset?,
    nodeScales: Map<String, Float>,
    modifier: Modifier = Modifier,
    challengeTargetNodeIds: Set<String> = emptySet(),
    startNodeId: String? = null,
    targetNodeId: String? = null,
    previewPath: List<String> = emptyList(),
    selectedEdgeIndex: Int? = null,
    panOffset: Offset = Offset.Zero,
    zoom: Float = 1f,
    referenceHeight: Float = 0f
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

    Canvas(modifier = modifier) {
        if (nodes.isEmpty()) return@Canvas

        val geom = GraphCanvasGeometry.from(
            nodes = nodes,
            drawSize = size,
            panOffset = panOffset,
            zoom = zoom,
            referenceHeight = referenceHeight
        )
        val r = geom.nodeRadius * geom.zoom

        // ── 1. Draw Existing Edges ──
        for ((edgeIdx, edge) in edges.withIndex()) {
            val fromNode = nodes.find { it.id == edge.from } ?: continue
            val toNode = nodes.find { it.id == edge.to } ?: continue

            val start = geom.toCanvasOffset(fromNode.x, fromNode.y)
            val end = geom.toCanvasOffset(toNode.x, toNode.y)

            val isSelectedEdge = (selectedEdgeIndex == edgeIdx)
            val isHighlighted = edge.isHighlighted || isSelectedEdge
            val edgeColor = when {
                isSelectedEdge -> PrimaryCyan
                edge.isHighlighted -> AccentGreen
                else -> GraphEdgeDefault
            }
            val strokeWidth = when {
                isSelectedEdge -> 5f
                edge.isHighlighted -> 4.5f
                else -> 2.2f
            }

            // Draw path highlight glow
            if (isHighlighted) {
                drawLine(
                    color = AccentGreen.copy(alpha = 0.25f),
                    start = start,
                    end = end,
                    strokeWidth = 9.5f
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
                val tipOffset = r + 2f
                val arrowTip = Offset(
                    end.x - tipOffset * cos(angle).toFloat(),
                    end.y - tipOffset * sin(angle).toFloat()
                )
                val arrowSize = (r * 0.5f).coerceIn(6f, 10f)
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

            // Draw rounded pill edge weight badge (figma-make-ref parity)
            if (edge.weight != null) {
                val midX = (start.x + end.x) / 2f
                val midY = (start.y + end.y) / 2f
                val pillW = 28f
                val pillH = 20f
                val corner = CornerRadius(6f, 6f)

                drawRoundRect(
                    color = DarkBackground,
                    topLeft = Offset(midX - pillW / 2f, midY - pillH / 2f),
                    size = Size(pillW, pillH),
                    cornerRadius = corner
                )
                drawRoundRect(
                    color = if (isHighlighted) AccentGreen else PrimaryCyan.copy(alpha = 0.45f),
                    topLeft = Offset(midX - pillW / 2f, midY - pillH / 2f),
                    size = Size(pillW, pillH),
                    cornerRadius = corner,
                    style = Stroke(width = if (isHighlighted) 1.6f else 1.1f)
                )

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        isAntiAlias = true
                        color = (if (isHighlighted) AccentGreen else TextSecondary).toArgb()
                        textSize = 18f
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    }
                    drawText(edge.weight.toString(), midX, midY + 6f, paint)
                }
            }
        }

        // ── 2. Draw Interactive Drag Preview Line (Rubber-Band Edge) ──
        val dragFrom = dragStartNode
        val dragTo = currentDragPos
        if (dragFrom != null && dragTo != null) {
            val startPos = geom.toCanvasOffset(dragFrom.x, dragFrom.y)
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

        // ── 2B. Draw Live Goal Path Preview ──
        if (previewPath.size >= 2) {
            for (i in 0 until previewPath.size - 1) {
                val u = previewPath[i]
                val v = previewPath[i + 1]
                val fromNode = nodes.find { it.id == u } ?: continue
                val toNode = nodes.find { it.id == v } ?: continue
                val pStart = geom.toCanvasOffset(fromNode.x, fromNode.y)
                val pEnd = geom.toCanvasOffset(toNode.x, toNode.y)
                drawLine(
                    color = AccentGreen.copy(alpha = 0.25f),
                    start = pStart,
                    end = pEnd,
                    strokeWidth = 9.5f
                )
                drawLine(
                    color = AccentGreen,
                    start = pStart,
                    end = pEnd,
                    strokeWidth = 3.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            }
        }

        // ── 3. Draw Nodes with Glowing Halos ──
        for (node in nodes) {
            val center = geom.toCanvasOffset(node.x, node.y)
            val isVisited = visitedNodeIds.contains(node.id) || node.state == ElementState.VISITED
            val isActive = activeNodeId == node.id || node.state == ElementState.ACTIVE || selectedNodeId == node.id
            val isHovered = hoveredTargetNodeId == node.id
            val isDragSource = dragStartNode?.id == node.id

            // Per-node "first visit" pop scale.
            val popScale = nodeScales[node.id] ?: 1f

            val highContrast = com.example.algolens.data.AppSettings.highContrastNodeOutlines
            val (fillColor, strokeColor, textColor) = when {
                isHovered -> Triple(PrimaryCyan.copy(alpha = 0.40f), PrimaryCyan, PrimaryCyan)
                isDragSource -> Triple(SecondaryPurple.copy(alpha = 0.40f), SecondaryPurple, PurpleGlow)
                isActive -> Triple(AccentGreen.copy(alpha = 0.28f), AccentGreen, AccentGreen)
                node.state == ElementState.COMPARING -> Triple(AccentYellow.copy(alpha = 0.25f), AccentYellow, AccentYellow)
                node.state == ElementState.SWAPPING -> Triple(AccentRed.copy(alpha = 0.25f), AccentRed, Color.White)
                node.state == ElementState.FOUND || node.state == ElementState.TARGET -> Triple(PrimaryCyan.copy(alpha = 0.25f), PrimaryCyan, PrimaryCyan)
                node.state == ElementState.PIVOT -> Triple(AccentPink.copy(alpha = 0.25f), AccentPink, Color.White)
                isVisited || node.state == ElementState.SORTED -> Triple(SecondaryPurple.copy(alpha = 0.20f), SecondaryPurple, TextPrimary)
                else -> Triple(
                    CardBackground,
                    if (highContrast) PrimaryCyan.copy(alpha = 0.90f) else PrimaryCyan.copy(alpha = 0.5f),
                    if (highContrast) TextPrimary else TextSecondary
                )
            }

            // ── Halo Glow Rendering Underneath Touch Targets ──
            if (isActive || isHovered || isDragSource) {
                val haloColor = if (isHovered || isDragSource) PrimaryCyan else AccentGreen
                val breathe = if (isActive) activeHaloPulse.value else 0f
                drawCircle(
                    color = haloColor.copy(alpha = 0.14f + 0.10f * breathe),
                    radius = (r * 1.65f + 3f * breathe) * popScale,
                    center = center
                )
                drawCircle(
                    color = haloColor.copy(alpha = 0.28f + 0.14f * breathe),
                    radius = (r * 1.28f + 1.5f * breathe) * popScale,
                    center = center
                )
            } else if (node.state == ElementState.PIVOT) {
                drawCircle(
                    color = AccentPink.copy(alpha = 0.20f),
                    radius = (r * 1.35f) * popScale,
                    center = center
                )
            }

            if (node.id in challengeTargetNodeIds) {
                val breathe = activeHaloPulse.value
                drawCircle(
                    color = SecondaryPurple.copy(alpha = 0.22f + 0.16f * breathe),
                    radius = (r * 1.25f + 2f * breathe) * popScale,
                    center = center,
                    style = Stroke(
                        width = 1.8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                    )
                )
            }

            // Endpoint Halos (START = AccentGreen, TARGET = AccentPink)
            if (node.id == startNodeId) {
                val breathe = activeHaloPulse.value
                drawCircle(
                    color = AccentGreen.copy(alpha = 0.38f + 0.16f * breathe),
                    radius = (r * 1.38f + 2.5f * breathe) * popScale,
                    center = center,
                    style = Stroke(width = 2.4f)
                )
            } else if (node.id == targetNodeId) {
                val breathe = activeHaloPulse.value
                drawCircle(
                    color = AccentPink.copy(alpha = 0.38f + 0.16f * breathe),
                    radius = (r * 1.38f + 2.5f * breathe) * popScale,
                    center = center,
                    style = Stroke(width = 2.4f)
                )
            }

            // Node background circle
            drawCircle(
                color = fillColor,
                radius = r * popScale,
                center = center
            )

            // Node border stroke
            val baseStroke = if (isActive || isHovered || isDragSource) 2.8f else 1.7f
            val strokeW = (if (highContrast) baseStroke + 1.2f else baseStroke) * popScale
            drawCircle(
                color = strokeColor,
                radius = r * popScale,
                center = center,
                style = Stroke(width = strokeW)
            )

            // Node label text
            drawContext.canvas.nativeCanvas.apply {
                val fontSize = (r * 0.95f).coerceIn(12f, 20f) * popScale
                val textPaint = Paint().apply {
                    isAntiAlias = true
                    color = textColor.toArgb()
                    textSize = fontSize
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                drawText(node.label, center.x, center.y + (fontSize * 0.35f), textPaint)

                // Endpoint Tag Caption above node
                if (node.id == startNodeId) {
                    val badgePaint = Paint().apply {
                        isAntiAlias = true
                        color = AccentGreen.toArgb()
                        textSize = (r * 0.55f).coerceIn(9f, 13f) * popScale
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    }
                    drawText("START", center.x, center.y - (r * 1.45f * popScale), badgePaint)
                } else if (node.id == targetNodeId) {
                    val badgePaint = Paint().apply {
                        isAntiAlias = true
                        color = AccentPink.toArgb()
                        textSize = (r * 0.55f).coerceIn(9f, 13f) * popScale
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    }
                    drawText("TARGET", center.x, center.y - (r * 1.45f * popScale), badgePaint)
                }
            }
        }
    }
}

/**
 * Geometry helper for the graph canvas.
 *
 * Uses a **stable world scale** ([worldWidth] x [worldHeight]) derived from
 * [referenceHeight] (the un-expanded stage height) and expands the virtual world
 * when dense/skewed 15-node trees require more room, rather than shrinking
 * [nodeRadius] below `16f`. Also applies [panOffset] identically in
 * [toCanvasOffset] and [toWorldCoords] so renderer and gesture hit-testing
 * never drift.
 */
data class GraphCanvasGeometry(
    val nodeCenterX: Float,
    val nodeCenterY: Float,
    val baseScale: Float,
    val nodeRadius: Float = 20f,
    val panOffset: Offset = Offset.Zero,
    val zoom: Float = 1f,
    val viewportWidth: Float,
    val viewportHeight: Float,
    val minX: Float = 0f,
    val minY: Float = 0f,
    val spanX: Float = 100f,
    val spanY: Float = 100f,
    val padding: Float = 24f,
    val drawWidth: Float = viewportWidth,
    val drawHeight: Float = viewportHeight
) {
    fun toCanvasOffset(nx: Float, ny: Float): Offset {
        val cx = viewportWidth / 2f
        val cy = viewportHeight / 2f
        val sx = cx + (nx - nodeCenterX) * baseScale * zoom + panOffset.x
        val sy = cy + (ny - nodeCenterY) * baseScale * zoom + panOffset.y
        return Offset(sx, sy)
    }

    fun toWorldCoords(canvasOffset: Offset): Offset {
        val cx = viewportWidth / 2f
        val cy = viewportHeight / 2f
        val safeScale = (baseScale * zoom).coerceAtLeast(0.0001f)
        val wx = nodeCenterX + (canvasOffset.x - panOffset.x - cx) / safeScale
        val wy = nodeCenterY + (canvasOffset.y - panOffset.y - cy) / safeScale
        return Offset(wx, wy)
    }

    companion object {
        const val MIN_READABLE_RADIUS = 16f
        const val MAX_READABLE_RADIUS = 20f
        const val MIN_CENTER_SPACING = 38f
        const val MIN_ZOOM = 0.5f
        const val MAX_ZOOM = 2.5f

        fun from(
            nodes: List<GraphNodeState>,
            drawSize: Size,
            panOffset: Offset = Offset.Zero,
            zoom: Float = 1f,
            referenceHeight: Float = 0f
        ): GraphCanvasGeometry {
            val minX = nodes.minOfOrNull { it.x } ?: 0f
            val maxX = nodes.maxOfOrNull { it.x } ?: 100f
            val minY = nodes.minOfOrNull { it.y } ?: 0f
            val maxY = nodes.maxOfOrNull { it.y } ?: 100f

            val nodeCenterX = (minX + maxX) / 2f
            val nodeCenterY = (minY + maxY) / 2f

            val rawSpanX = (maxX - minX + 50f).coerceAtLeast(10f)
            val rawSpanY = (maxY - minY + 50f).coerceAtLeast(10f)

            // Soft-damped span expansion prevents runaway zoom-out when moving nodes further away
            val baseSpanX = 220f
            val baseSpanY = 180f
            val spanX = if (rawSpanX > baseSpanX) {
                baseSpanX + Math.pow((rawSpanX - baseSpanX).toDouble(), 0.75).toFloat()
            } else {
                rawSpanX
            }
            val spanY = if (rawSpanY > baseSpanY) {
                baseSpanY + Math.pow((rawSpanY - baseSpanY).toDouble(), 0.75).toFloat()
            } else {
                rawSpanY
            }

            val padding = if (nodes.size > 10) 24f else 32f

            val availW = (drawSize.width - (padding * 2f)).coerceAtLeast(10f)
            val effectiveHeight = if (referenceHeight > 0f) referenceHeight else drawSize.height
            val availH = (effectiveHeight - (padding * 2f)).coerceAtLeast(10f)

            // Isotropic uniform scale: X and Y scale identically to prevent distortion
            val baseScale = minOf(availW / spanX, availH / spanY).coerceIn(0.2f, 15f)

            // Measure pairwise distance in pixels at baseScale
            var minDistPx = Float.MAX_VALUE
            for (i in nodes.indices) {
                for (j in i + 1 until nodes.size) {
                    val dx = (nodes[i].x - nodes[j].x) * baseScale
                    val dy = (nodes[i].y - nodes[j].y) * baseScale
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                    if (dist > 0.5f && dist < minDistPx) {
                        minDistPx = dist
                    }
                }
            }

            val adaptiveRadius = if (minDistPx < Float.MAX_VALUE) {
                (minDistPx * 0.42f).coerceIn(MIN_READABLE_RADIUS, MAX_READABLE_RADIUS)
            } else {
                MAX_READABLE_RADIUS
            }

            return GraphCanvasGeometry(
                nodeCenterX = nodeCenterX,
                nodeCenterY = nodeCenterY,
                baseScale = baseScale,
                nodeRadius = adaptiveRadius,
                panOffset = panOffset,
                zoom = zoom,
                viewportWidth = drawSize.width,
                viewportHeight = drawSize.height,
                minX = minX,
                minY = minY,
                spanX = spanX,
                spanY = spanY,
                padding = padding,
                drawWidth = availW,
                drawHeight = availH
            )
        }

        fun computeCenterPan(
            nodes: List<GraphNodeState>,
            drawSize: Size,
            zoom: Float = 1f,
            referenceHeight: Float = 0f
        ): Offset {
            if (nodes.isEmpty()) return Offset.Zero
            val geom = from(nodes, drawSize, panOffset = Offset.Zero, zoom = 1f, referenceHeight = referenceHeight)
            val avgX = nodes.map { it.x }.average().toFloat()
            val avgY = nodes.map { it.y }.average().toFloat()
            val offsetX = -(avgX - geom.nodeCenterX) * geom.baseScale * zoom
            val offsetY = -(avgY - geom.nodeCenterY) * geom.baseScale * zoom
            return Offset(offsetX, offsetY)
        }

        fun computeFitZoomAndPan(
            nodes: List<GraphNodeState>,
            drawSize: Size,
            marginPx: Float = 48f,
            referenceHeight: Float = 0f
        ): Pair<Float, Offset> {
            if (nodes.isEmpty()) return 1f to Offset.Zero
            val geom = from(nodes, drawSize, panOffset = Offset.Zero, zoom = 1f, referenceHeight = referenceHeight)
            val minX = nodes.minOf { it.x }
            val maxX = nodes.maxOf { it.x }
            val minY = nodes.minOf { it.y }
            val maxY = nodes.maxOf { it.y }

            val r = geom.nodeRadius
            val contentW = ((maxX - minX) * geom.baseScale + 2f * r + 2f * marginPx).coerceAtLeast(1f)
            val contentH = ((maxY - minY) * geom.baseScale + 2f * r + 2f * marginPx).coerceAtLeast(1f)

            val scaleX = drawSize.width / contentW
            val scaleY = drawSize.height / contentH
            val fitZoom = minOf(scaleX, scaleY).coerceIn(MIN_ZOOM, MAX_ZOOM)

            val bboxCenterX = (minX + maxX) / 2f
            val bboxCenterY = (minY + maxY) / 2f
            val pan = Offset(
                x = -(bboxCenterX - geom.nodeCenterX) * geom.baseScale * fitZoom,
                y = -(bboxCenterY - geom.nodeCenterY) * geom.baseScale * fitZoom
            )
            return fitZoom to pan
        }
    }
}
