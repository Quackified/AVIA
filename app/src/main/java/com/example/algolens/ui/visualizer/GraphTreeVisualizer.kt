package com.example.algolens.ui.visualizer

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modular Canvas Renderer for 2D Graphs (BFS, DFS) and Hierarchical Trees (BST, Heap).
 * State-driven from VisualizerStep.
 */
@Composable
fun GraphTreeVisualizer(
    step: VisualizerStep,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nodes = step.nodes
            val edges = step.edges
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

            // ── 1. Draw Edges ──
            for (edge in edges) {
                val fromNode = nodes.find { it.id == edge.from } ?: continue
                val toNode = nodes.find { it.id == edge.to } ?: continue

                val start = toCanvasOffset(fromNode.x, fromNode.y)
                val end = toCanvasOffset(toNode.x, toNode.y)

                val isHighlighted = edge.isHighlighted
                val edgeColor = if (isHighlighted) AccentGreen else Color(0xFF1E293B)
                val strokeWidth = if (isHighlighted) 4.5f else 2f

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

            // ── 2. Draw Nodes ──
            for (node in nodes) {
                val center = toCanvasOffset(node.x, node.y)
                val isVisited = step.visitedNodeIds.contains(node.id) || node.state == ElementState.VISITED
                val isActive = step.activeNodeId == node.id || node.state == ElementState.ACTIVE

                val (fillColor, strokeColor, textColor) = when {
                    isActive -> Triple(AccentGreen.copy(alpha = 0.28f), AccentGreen, AccentGreen)
                    node.state == ElementState.COMPARING -> Triple(AccentYellow.copy(alpha = 0.25f), AccentYellow, AccentYellow)
                    node.state == ElementState.SWAPPING -> Triple(AccentRed.copy(alpha = 0.25f), AccentRed, AccentRed)
                    node.state == ElementState.FOUND || node.state == ElementState.TARGET -> Triple(PrimaryCyan.copy(alpha = 0.25f), PrimaryCyan, PrimaryCyan)
                    node.state == ElementState.PIVOT -> Triple(AccentPink.copy(alpha = 0.25f), AccentPink, AccentPink)
                    isVisited || node.state == ElementState.SORTED -> Triple(SecondaryPurple.copy(alpha = 0.20f), SecondaryPurple, TextPrimary)
                    else -> Triple(CardBackground, PrimaryCyan.copy(alpha = 0.5f), TextSecondary)
                }

                // Halo glow for active node
                if (isActive) {
                    drawCircle(
                        color = AccentGreen.copy(alpha = 0.16f),
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
                    style = Stroke(width = if (isActive) 3f else 1.8f)
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
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun GraphTreeVisualizerPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(280.dp).padding(16.dp)) {
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
