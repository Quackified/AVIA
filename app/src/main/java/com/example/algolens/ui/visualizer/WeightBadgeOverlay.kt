package com.example.algolens.ui.visualizer

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.GraphEdgeDefault
import com.example.algolens.ui.theme.TextMuted

/**
 * Graph-edge weight badge overlay.
 *
 * Renders a small weight label at the midpoint of every graph edge
 * whose `weight` is non-null. For the current 13 algorithms every
 * `GraphEdgeState.weight` is `null` (BFS / DFS / BST / Heap all
 * construct unweighted edges), so this overlay is a no-op today;
 * it exists so that a future weighted graph algorithm (Dijkstra, MST)
 * can declare the overlay on its spec and pick up the rendering for
 * free, without touching the graph renderer.
 *
 * The coordinate transform mirrors [GraphTreeVisualizer]'s
 * `toCanvasOffset` (padding 36f, normalized input space with a +15f
 * pad on each axis). When the renderer's transform changes, this
 * overlay must be updated in lockstep — the constants live at the
 * top of this file so a grep for `PADDING` / `AXIS_INSET` surfaces
 * the synchronization point.
 *
 * Conventions:
 *  - No-op when no edge has a weight. A weighted-only overlay that
 *    renders an empty box on the unweighted graphs would be visual
 *    noise.
 *  - Tokenised: badge background is `CanvasBackground` (the well
 *    surface, so the badge reads as on-canvas), border uses
 *    `AccentGreen` when the edge is highlighted, otherwise the same
 *    color the renderer uses (`#1E293B` slate). Text uses
 *    `AccentGreen` / `TextMuted` for the same highlighted / default
 *    distinction.
 *  - The host fills the canvas with this overlay using
 *    `Modifier.matchParentSize()`; the overlay's [modifier] is the
 *    root it receives.
 */
object WeightBadgeOverlay : VisualizerOverlay {

    /** Padding (px) around the drawable area. Mirrors the renderer. */
    private const val PADDING = 36f

    /** Inset (px) added to min/max on each axis. Mirrors the renderer. */
    private const val AXIS_INSET = 15f

    @Composable
    override fun Content(
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) {
        if (step.edges.isEmpty() || step.edges.none { it.weight != null }) return
        if (step.nodes.isEmpty()) return

        val minX = step.nodes.minOf { it.x }
        val minY = step.nodes.minOf { it.y }
        val spanX = (step.nodes.maxOf { it.x } - minX).coerceAtLeast(1f)
        val spanY = (step.nodes.maxOf { it.y } - minY).coerceAtLeast(1f)

        Box(modifier = modifier.fillMaxSize()) {
            Canvas(modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
            ) {
                val drawWidth = size.width - (PADDING * 2)
                val drawHeight = size.height - (PADDING * 2)

                fun toCanvasOffset(nx: Float, ny: Float): Offset {
                    val cx = PADDING + ((nx - minX + AXIS_INSET) / spanX) * drawWidth
                    val cy = PADDING + ((ny - minY + AXIS_INSET) / spanY) * drawHeight
                    return Offset(cx, cy)
                }

                for (edge in step.edges) {
                    val weight = edge.weight ?: continue
                    val fromNode = step.nodes.find { it.id == edge.from } ?: continue
                    val toNode = step.nodes.find { it.id == edge.to } ?: continue
                    val start = toCanvasOffset(fromNode.x, fromNode.y)
                    val end = toCanvasOffset(toNode.x, toNode.y)
                    val midX = (start.x + end.x) / 2f
                    val midY = (start.y + end.y) / 2f

                    // Badge background
                    val bgColor = CanvasBackground
                    val borderColor = if (edge.isHighlighted) AccentGreen else GraphEdgeDefault
                    val textColor = if (edge.isHighlighted) AccentGreen else TextMuted

                    drawRect(
                        color = bgColor,
                        topLeft = Offset(midX - 14f, midY - 10f),
                        size = Size(28f, 20f),
                    )
                    drawRect(
                        color = borderColor,
                        topLeft = Offset(midX - 14f, midY - 10f),
                        size = Size(28f, 20f),
                        style = Stroke(width = 1f),
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            isAntiAlias = true
                            color = textColor.toArgb()
                            textSize = 18f
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.MONOSPACE
                        }
                        drawText(weight.toString(), midX, midY + 6f, paint)
                    }
                }
            }
        }
    }
}
