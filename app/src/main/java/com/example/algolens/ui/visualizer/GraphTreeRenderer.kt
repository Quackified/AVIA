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
    challengeTargetNodeIds: Set<String> = emptySet(),
    panOffset: Offset = Offset.Zero,
    referenceHeight: Float = 0f,
    modifier: Modifier = Modifier
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
            referenceHeight = referenceHeight
        )
        val r = geom.nodeRadius

        // ── 1. Draw Existing Edges ──
        for (edge in edges) {
            val fromNode = nodes.find { it.id == edge.from } ?: continue
            val toNode = nodes.find { it.id == edge.to } ?: continue

            val start = geom.toCanvasOffset(fromNode.x, fromNode.y)
            val end = geom.toCanvasOffset(toNode.x, toNode.y)

            val isHighlighted = edge.isHighlighted
            val edgeColor = if (isHighlighted) AccentGreen else GraphEdgeDefault
            val strokeWidth = if (isHighlighted) 4.5f else 2.2f

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
internal data class GraphCanvasGeometry(
    val minX: Float,
    val minY: Float,
    val spanX: Float,
    val spanY: Float,
    val padding: Float,
    val drawWidth: Float,
    val drawHeight: Float,
    val nodeRadius: Float = 20f,
    val panOffset: Offset = Offset.Zero
) {
    fun toCanvasOffset(nx: Float, ny: Float): Offset {
        val cx = padding + ((nx - minX + 15f) / spanX) * drawWidth + panOffset.x
        val cy = padding + ((ny - minY + 15f) / spanY) * drawHeight + panOffset.y
        return Offset(cx, cy)
    }

    fun toWorldCoords(canvasOffset: Offset): Offset {
        val wx = ((canvasOffset.x - panOffset.x - padding) / drawWidth.coerceAtLeast(1f)) * spanX + minX - 15f
        val wy = ((canvasOffset.y - panOffset.y - padding) / drawHeight.coerceAtLeast(1f)) * spanY + minY - 15f
        return Offset(wx, wy)
    }

    companion object {
        private const val MIN_READABLE_RADIUS = 16f
        private const val MAX_READABLE_RADIUS = 20f
        private const val MIN_CENTER_SPACING = 38f

        fun from(
            nodes: List<GraphNodeState>,
            drawSize: Size,
            panOffset: Offset = Offset.Zero,
            referenceHeight: Float = 0f
        ): GraphCanvasGeometry {
            val maxX = (nodes.maxOfOrNull { it.x } ?: 100f).coerceAtLeast(100f)
            val maxY = (nodes.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)
            val minX = (nodes.minOfOrNull { it.x } ?: 0f).coerceAtMost(0f)
            val minY = (nodes.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)
            val spanX = (maxX - minX + 30f).coerceAtLeast(1f)
            val spanY = (maxY - minY + 30f).coerceAtLeast(1f)
            val padding = if (nodes.size > 10) 24f else 32f

            // Stable world height: never shrinks when the dock expands.
            val stableCanvasHeight = maxOf(drawSize.height, referenceHeight, drawSize.width * 0.72f)
            val baseDrawWidth = (drawSize.width - (padding * 2)).coerceAtLeast(1f)
            val baseDrawHeight = (stableCanvasHeight - (padding * 2)).coerceAtLeast(1f)

            val initial = GraphCanvasGeometry(
                minX = minX,
                minY = minY,
                spanX = spanX,
                spanY = spanY,
                padding = padding,
                drawWidth = baseDrawWidth,
                drawHeight = baseDrawHeight,
                nodeRadius = MAX_READABLE_RADIUS,
                panOffset = panOffset
            )
            if (nodes.size <= 1) return initial

            // Measure pairwise distance in the base world
            val baseOffsets = nodes.map { initial.copy(panOffset = Offset.Zero).toCanvasOffset(it.x, it.y) }
            var minDist = Float.MAX_VALUE
            for (i in baseOffsets.indices) {
                for (j in i + 1 until baseOffsets.size) {
                    val dx = baseOffsets[i].x - baseOffsets[j].x
                    val dy = baseOffsets[i].y - baseOffsets[j].y
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                    if (dist > 0.5f && dist < minDist) {
                        minDist = dist
                    }
                }
            }

            // If dense/skewed nodes are closer than MIN_CENTER_SPACING, expand the world
            // dimensions so nodes stay at full readable radius (>= 16f) and can be panned.
            val worldExpansion = if (minDist < MIN_CENTER_SPACING && minDist < Float.MAX_VALUE) {
                (MIN_CENTER_SPACING / minDist).coerceIn(1f, 3.0f)
            } else {
                1f
            }

            val expandedWidth = baseDrawWidth * worldExpansion
            val expandedHeight = baseDrawHeight * worldExpansion
            val effectiveMinDist = if (minDist < Float.MAX_VALUE) minDist * worldExpansion else Float.MAX_VALUE
            val adaptiveRadius = if (effectiveMinDist < Float.MAX_VALUE) {
                (effectiveMinDist * 0.42f).coerceIn(MIN_READABLE_RADIUS, MAX_READABLE_RADIUS)
            } else {
                MAX_READABLE_RADIUS
            }

            // Center expanded horizontal world by default when panOffset == Zero
            val autoCenterOffsetX = if (worldExpansion > 1f) -(expandedWidth - baseDrawWidth) * 0.5f else 0f

            return initial.copy(
                drawWidth = expandedWidth,
                drawHeight = expandedHeight,
                nodeRadius = adaptiveRadius,
                panOffset = Offset(panOffset.x + autoCenterOffsetX, panOffset.y)
            )
        }
    }
}
