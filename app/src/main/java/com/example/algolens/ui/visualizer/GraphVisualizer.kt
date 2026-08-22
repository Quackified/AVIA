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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.algolens.model.GraphEdge
import com.example.algolens.model.GraphNode
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

@Composable
fun GraphVisualizer(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    visitedNodes: Set<String>,
    activeNode: String?,
    pathEdges: Set<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .border(1.dp, AccentGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
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

                val isPath = pathEdges.contains("${edge.from}-${edge.to}") ||
                        pathEdges.contains("${edge.to}-${edge.from}")

                val edgeColor = if (isPath) AccentGreen else Color(0xFF1E293B)
                val strokeWidth = if (isPath) 5f else 2.5f

                drawLine(
                    color = edgeColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth
                )

                // Edge weight badge
                val midX = (start.x + end.x) / 2
                val midY = (start.y + end.y) / 2

                drawRect(
                    color = Color(0xFF060A14),
                    topLeft = Offset(midX - 16f, midY - 12f),
                    size = Size(32f, 24f)
                )
                drawRect(
                    color = if (isPath) AccentGreen else Color(0xFF1E293B),
                    topLeft = Offset(midX - 16f, midY - 12f),
                    size = Size(32f, 24f),
                    style = Stroke(width = 1f)
                )

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        isAntiAlias = true
                        color = (if (isPath) AccentGreen else TextMuted).toArgb()
                        textSize = 20f
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.MONOSPACE
                    }
                    drawText(edge.weight.toString(), midX, midY + 7f, paint)
                }
            }

            // 2. Draw Nodes
            for (node in nodes) {
                val center = Offset(node.x * scaleX, node.y * scaleY)
                val isVisited = visitedNodes.contains(node.id)
                val isActive = activeNode == node.id

                val fillColor = when {
                    isActive -> AccentGreen.copy(alpha = 0.25f)
                    isVisited -> PrimaryCyan.copy(alpha = 0.18f)
                    else -> CardBackground
                }

                val strokeColor = when {
                    isActive -> AccentGreen
                    isVisited -> PrimaryCyan
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
                    style = Stroke(width = if (isActive) 3f else 1.8f)
                )

                // Label Text
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = Paint().apply {
                        isAntiAlias = true
                        color = (when {
                            isActive -> AccentGreen
                            isVisited -> TextPrimary
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
    com.example.algolens.ui.theme.AlgoLensTheme {
        Box(
            modifier = Modifier
                .height(280.dp)
                .padding(16.dp)
        ) {
            GraphVisualizer(
                nodes = listOf(
                    GraphNode("A", 45f, 35f, "A"),
                    GraphNode("B", 130f, 30f, "B"),
                    GraphNode("C", 45f, 100f, "C"),
                    GraphNode("D", 130f, 105f, "D"),
                    GraphNode("E", 215f, 65f, "E")
                ),
                edges = listOf(
                    GraphEdge("A", "B", 4),
                    GraphEdge("A", "C", 2),
                    GraphEdge("B", "C", 1),
                    GraphEdge("B", "D", 5),
                    GraphEdge("C", "D", 8),
                    GraphEdge("C", "E", 10),
                    GraphEdge("D", "E", 2)
                ),
                visitedNodes = setOf("A", "C", "B"),
                activeNode = "B",
                pathEdges = setOf("A-C", "C-B")
            )
        }
    }
}
