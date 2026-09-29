package com.avia.ui.visualizer

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
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.GraphEdgeDefault
import com.avia.ui.theme.CanvasBackground

/**
 * Unified graph visualizer — talks to the [VisualizerStep] model so the
 * tree, BFS and DFS animations all share the same node / edge state
 * representation as the rest of the workspace.
 *
 * Each node's [ElementState] (IDLE / ACTIVE / VISITED / COMPARING /
 * FOUND) drives the fill, stroke and label colour, and any edge with
 * `isHighlighted = true` is rendered as the active path. Tokens come
 * from [AlgoTokens] so the surface radius / stroke align with the rest
 * of the design system.
 *
 * Note: the builder overlay (tap-to-add-node) is implemented separately
 * in [GraphTreeVisualizer] which is still used for BFS / DFS / BST / Heap
 * rendering. This [GraphVisualizer] is a thin, deterministic, model-only
 * graph renderer that the visualizer host can fall back to when no
 * builder is needed (e.g. theory previews, lightweight graph tests).
 */
@Composable
fun GraphVisualizer(
    step: VisualizerStep,
    modifier: Modifier = Modifier
) {
    val nodes = step.nodes
    val edges = step.edges
    val activeId = step.activeNodeId
    val visited = step.visitedNodeIds

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, AccentGreen.copy(alpha = 0.2f), RoundedCornerShape(AlgoTokens.radiusMd))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (nodes.isEmpty()) return@Canvas

            val scaleX = size.width / 255f
            val scaleY = size.height / 155f

            // 1. Draw Edges
            for (edge in edges) {
                val from = nodes.find { it.id == edge.from } ?: continue
                val to = nodes.find { it.id == edge.to } ?: continue

                val start = Offset(from.x * scaleX, from.y * scaleY)
                val end = Offset(to.x * scaleX, to.y * scaleY)

                val isHighlighted = edge.isHighlighted

                val edgeColor = if (isHighlighted) AccentGreen else GraphEdgeDefault
                val strokeWidth = if (isHighlighted) 5f else 2.5f

                drawLine(
                    color = edgeColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth
                )

                // Edge weight badge (only for weighted edges)
                if (edge.weight != null) {
                    val midX = (start.x + end.x) / 2
                    val midY = (start.y + end.y) / 2

                    drawRect(
                        color = CanvasBackground,
                        topLeft = Offset(midX - 16f, midY - 12f),
                        size = Size(32f, 24f)
                    )
                    drawRect(
                        color = if (isHighlighted) AccentGreen else GraphEdgeDefault,
                        topLeft = Offset(midX - 16f, midY - 12f),
                        size = Size(32f, 24f),
                        style = Stroke(width = 1f)
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            isAntiAlias = true
                            color = (if (isHighlighted) AccentGreen else TextMuted).toArgb()
                            textSize = 20f
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.MONOSPACE
                        }
                        drawText(edge.weight.toString(), midX, midY + 7f, paint)
                    }
                }
            }

            // 2. Draw Nodes — ElementState drives fill, stroke and label colour.
            for (node in nodes) {
                val center = Offset(node.x * scaleX, node.y * scaleY)
                val isActive = activeId == node.id || node.state == ElementState.ACTIVE
                val isVisited = node.id in visited || node.state == ElementState.VISITED
                val isFound = node.state == ElementState.FOUND
                val isComparing = node.state == ElementState.COMPARING

                val fillColor = when {
                    isActive -> AccentGreen.copy(alpha = 0.25f)
                    isFound -> AccentGreen.copy(alpha = 0.2f)
                    isVisited -> PrimaryCyan.copy(alpha = 0.18f)
                    isComparing -> com.avia.ui.theme.AccentYellow.copy(alpha = 0.18f)
                    else -> CardBackground
                }

                val strokeColor = when {
                    isActive -> AccentGreen
                    isFound -> AccentGreen
                    isVisited -> PrimaryCyan
                    isComparing -> com.avia.ui.theme.AccentYellow
                    else -> PrimaryCyan.copy(alpha = 0.6f)
                }

                if (isActive) {
                    drawCircle(
                        color = AccentGreen.copy(alpha = 0.15f),
                        radius = 28f,
                        center = center
                    )
                }

                drawCircle(
                    color = fillColor,
                    radius = 20f,
                    center = center
                )

                drawCircle(
                    color = strokeColor,
                    radius = 20f,
                    center = center,
                    style = Stroke(width = if (isActive || isFound) 3f else 1.8f)
                )

                // Label Text
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = Paint().apply {
                        isAntiAlias = true
                        color = (when {
                            isActive -> AccentGreen
                            isFound -> AccentGreen
                            isVisited -> TextPrimary
                            isComparing -> com.avia.ui.theme.AccentYellow
                            else -> TextSecondary
                        }).toArgb()
                        textSize = 22f
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    }
                    drawText(node.label, center.x, center.y + 7.5f, textPaint)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun GraphVisualizerPreview() {
    com.avia.ui.theme.AlgoLensTheme {
        Box(
            modifier = Modifier
                .height(280.dp)
                .padding(16.dp)
        ) {
            GraphVisualizer(
                step = VisualizerStep(
                    stepIndex = 2,
                    description = "BFS: Visiting node B from queue",
                    nodes = listOf(
                        GraphNodeState("A", "A", 45f, 35f, ElementState.VISITED),
                        GraphNodeState("B", "B", 130f, 30f, ElementState.ACTIVE),
                        GraphNodeState("C", "C", 45f, 100f, ElementState.VISITED),
                        GraphNodeState("D", "D", 130f, 105f, ElementState.IDLE),
                        GraphNodeState("E", "E", 215f, 65f, ElementState.IDLE)
                    ),
                    edges = listOf(
                        GraphEdgeState("A", "B", 4, isHighlighted = true),
                        GraphEdgeState("A", "C", 2, isHighlighted = true),
                        GraphEdgeState("B", "C", 1),
                        GraphEdgeState("B", "D", 5),
                        GraphEdgeState("C", "D", 8),
                        GraphEdgeState("C", "E", 10),
                        GraphEdgeState("D", "E", 2)
                    ),
                    activeNodeId = "B",
                    visitedNodeIds = setOf("A", "C")
                )
            )
        }
    }
}
