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
 * Draws the engineered CAD / oscilloscope grid-like background on the 2D Graph & Tree stage:
 * - Subtle radial emerald/cyan ambient glow
 * - Minor Cartesian grid lines (24dp pitch)
 * - Major Cartesian grid lines (96dp pitch) with intersection dot markers
 */
private fun DrawScope.drawGraphInstrumentGrid() {
    val minorStep = 24.dp.toPx().coerceAtLeast(16f)
    val majorEvery = 4

    // Subtle ambient radial glow in the stage center
    val maxDim = size.maxDimension.coerceAtLeast(1f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                AccentGreen.copy(alpha = 0.05f),
                PrimaryCyan.copy(alpha = 0.025f),
                Color.Transparent
            ),
            center = Offset(size.width * 0.5f, size.height * 0.5f),
            radius = maxDim * 0.62f
        ),
        radius = maxDim * 0.62f,
        center = Offset(size.width * 0.5f, size.height * 0.5f)
    )

    val minorColor = BorderSubtle.copy(alpha = 0.30f)
    val majorColor = PrimaryCyan.copy(alpha = 0.11f)
    val dotColor = PrimaryCyan.copy(alpha = 0.28f)

    var colIndex = 0
    var x = 0f
    while (x <= size.width) {
        val isMajor = colIndex % majorEvery == 0
        drawLine(
            color = if (isMajor) majorColor else minorColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = if (isMajor) 1.2f else 0.8f
        )
        x += minorStep
        colIndex++
    }

    var rowIndex = 0
    var y = 0f
    while (y <= size.height) {
        val isMajor = rowIndex % majorEvery == 0
        drawLine(
            color = if (isMajor) majorColor else minorColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = if (isMajor) 1.2f else 0.8f
        )
        if (isMajor) {
            var ix = 0f
            var cIdx = 0
            while (ix <= size.width) {
                if (cIdx % majorEvery == 0) {
                    drawCircle(
                        color = dotColor,
                        radius = 1.6f,
                        center = Offset(ix, y)
                    )
                }
                ix += minorStep
                cIdx++
            }
        }
        y += minorStep
        rowIndex++
    }
}

/**
 * Pure rendering of the graph/tree canvas — grid background, edges, weight pills,
 * drag preview, and nodes with halos. Owns the `activeHaloPulse` infinite transition
 * that breathes the active node's halo.
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
        // ── 0. Precision Instrument Grid Background ──
        drawGraphInstrumentGrid()

        if (nodes.isEmpty()) return@Canvas

        val geom = GraphCanvasGeometry.from(nodes, size)

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

            // Per-node "first visit" pop scale. Defaults to 1f
            // (no effect) so nodes that have already been popped
            // look identical to the previous Canvas.
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
                // Outer ambient halo (breathes when node is the active one)
                val breathe = if (isActive) activeHaloPulse.value else 0f
                drawCircle(
                    color = haloColor.copy(alpha = 0.14f + 0.10f * breathe),
                    radius = (34f + 4f * breathe) * popScale,
                    center = center
                )
                // Inner sharp halo
                drawCircle(
                    color = haloColor.copy(alpha = 0.28f + 0.14f * breathe),
                    radius = (26f + 2f * breathe) * popScale,
                    center = center
                )
            } else if (node.state == ElementState.PIVOT) {
                drawCircle(
                    color = AccentPink.copy(alpha = 0.20f),
                    radius = 28f * popScale,
                    center = center
                )
            }

            if (node.id in challengeTargetNodeIds) {
                val breathe = activeHaloPulse.value
                drawCircle(
                    color = SecondaryPurple.copy(alpha = 0.22f + 0.16f * breathe),
                    radius = (25f + 3f * breathe) * popScale,
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
                radius = 20f * popScale,
                center = center
            )

            // Node border stroke
            val baseStroke = if (isActive || isHovered || isDragSource) 3f else 1.8f
            val strokeW = (if (highContrast) baseStroke + 1.2f else baseStroke) * popScale
            drawCircle(
                color = strokeColor,
                radius = 20f * popScale,
                center = center,
                style = Stroke(width = strokeW)
            )

            // Node label text — also scaled so the glyph grows
            // in lockstep with the node's first-visit pop.
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = Paint().apply {
                    isAntiAlias = true
                    color = textColor.toArgb()
                    textSize = 20f * popScale
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                drawText(node.label, center.x, center.y + 7f * popScale, textPaint)
            }
        }
    }
}

/**
 * Geometry helper for the graph canvas. The original file duplicated the
 * `toCanvasOffset` mapping 4 times (once in the tap detector, twice in the
 * drag detector, once in the render Canvas). This consolidates it into a
 * single value type that both [GraphTreeRenderer] and [GraphBuilderOverlay]
 * can construct from the live node set.
 */
internal data class GraphCanvasGeometry(
    val minX: Float,
    val minY: Float,
    val spanX: Float,
    val spanY: Float,
    val padding: Float,
    val drawWidth: Float,
    val drawHeight: Float
) {
    fun toCanvasOffset(nx: Float, ny: Float): Offset {
        val cx = padding + ((nx - minX + 15f) / spanX) * drawWidth
        val cy = padding + ((ny - minY + 15f) / spanY) * drawHeight
        return Offset(cx, cy)
    }

    companion object {
        fun from(nodes: List<GraphNodeState>, drawSize: Size): GraphCanvasGeometry {
            val maxX = (nodes.maxOfOrNull { it.x } ?: 100f).coerceAtLeast(100f)
            val maxY = (nodes.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)
            val minX = (nodes.minOfOrNull { it.x } ?: 0f).coerceAtMost(0f)
            val minY = (nodes.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)
            val spanX = (maxX - minX + 30f).coerceAtLeast(1f)
            val spanY = (maxY - minY + 30f).coerceAtLeast(1f)
            val padding = 36f
            val drawWidth = drawSize.width - (padding * 2)
            val drawHeight = drawSize.height - (padding * 2)
            return GraphCanvasGeometry(minX, minY, spanX, spanY, padding, drawWidth, drawHeight)
        }
    }
}
