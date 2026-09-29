package com.avia.ui.visualizer

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Layered outer-stroke glow around a cell. Deliberately paints ONLY the band
 * OUTSIDE the cell bounds (three nested rounded-rect strokes with decreasing
 * width / increasing intensity) so the translucent cell fill never picks up
 * the halo — the element value inside stays fully legible, and the thin band
 * (~7dp) cannot bleed into neighbouring cells.
 *
 * Used by the Challenge Mode "TARGET" cells (yellow halo) and the cross-feature
 * code-trace sync pulse (mirrored colour). Lives in its own file so it can be
 * invoked from both [CellGrid] and the challenge-target composable without a
 * cycle.
 */
internal fun DrawScope.drawCellGlow(
    accent: Color,
    intensity: Float,
    cornerRadius: CornerRadius
) {
    val soft = 6.dp.toPx()
    val mid = 3.dp.toPx()
    val tight = 1.dp.toPx()

    // Wide soft outer band
    drawRoundRect(
        color = accent.copy(alpha = 0.14f * intensity),
        topLeft = Offset(-soft, -soft),
        size = Size(size.width + soft * 2f, size.height + soft * 2f),
        cornerRadius = cornerRadius,
        style = Stroke(width = 4.dp.toPx())
    )
    // Mid glow band
    drawRoundRect(
        color = accent.copy(alpha = 0.30f * intensity),
        topLeft = Offset(-mid, -mid),
        size = Size(size.width + mid * 2f, size.height + mid * 2f),
        cornerRadius = cornerRadius,
        style = Stroke(width = 2.dp.toPx())
    )
    // Tight bright rim hugging the border
    drawRoundRect(
        color = accent.copy(alpha = 0.85f * intensity),
        topLeft = Offset(-tight, -tight),
        size = Size(size.width + tight * 2f, size.height + tight * 2f),
        cornerRadius = cornerRadius,
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Concentric outer-stroke glow around circular nodes (Graph & Tree visualizers).
 * Paints outside the node circle with decreasing width / increasing intensity.
 */
internal fun DrawScope.drawCircleGlow(
    center: Offset,
    radius: Float,
    accent: Color,
    intensity: Float = 1f
) {
    val soft = 6.dp.toPx()
    val mid = 3.dp.toPx()
    val tight = 1.2.dp.toPx()

    // Wide soft outer band
    drawCircle(
        color = accent.copy(alpha = 0.14f * intensity),
        radius = radius + soft,
        center = center,
        style = Stroke(width = 4.dp.toPx())
    )
    // Mid glow band
    drawCircle(
        color = accent.copy(alpha = 0.30f * intensity),
        radius = radius + mid,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    // Tight bright rim hugging the border
    drawCircle(
        color = accent.copy(alpha = 0.85f * intensity),
        radius = radius + tight,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )
}

