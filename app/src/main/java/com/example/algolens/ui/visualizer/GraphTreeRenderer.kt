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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure rendering of the graph/tree canvas — edges, drag preview, nodes with
 * halos. Owns the `activeHaloPulse` infinite transition that breathes the
 * active node's halo. Has no interactive state of its own; the public
 * [GraphTreeVisualizer] shell passes in the live state values to read each
 * frame.
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

        val geom = GraphCanvasGeometry.from(nodes, size)

        // ── 1. Draw Existing Edges ──
        for (edge in edges) {
            val fromNode = nodes.find { it.id == edge.from } ?: continue
            val toNode = nodes.find { it.id == edge.to } ?: continue

            val start = geom.toCanvasOffset(fromNode.x, fromNode.y)
            val end = geom.toCanvasOffset(toNode.x, toNode.y)

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

            // Node background circle
            drawCircle(
                color = fillColor,
                radius = 20f * popScale,
                center = center
            )

            // Node border stroke
            drawCircle(
                color = strokeColor,
                radius = 20f * popScale,
                center = center,
                style = Stroke(width = (if (isActive || isHovered || isDragSource) 3f else 1.8f) * popScale)
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
