package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens

import androidx.compose.ui.graphics.StrokeJoin

/**
 * Sharp-edged pointer bracket (`|—|`) connecting the two active cells being
 * compared or swapped.
 *
 * Replaces the old rounded `ComparisonBridgeOverlay` with:
 *  - Crisp 90-degree sharp corners (`StrokeJoin.Miter`, `StrokeCap.Square`, straight `lineTo` legs)
 *  - Stroke thickness and anchor tips that scale adaptively with [cellScale] (S / M / L)
 */
@Composable
fun ActivePairPointerBracket(
    step: VisualizerStep,
    cellCenterXA: Float?,
    cellCenterXB: Float?,
    cellTopY: Float,
    hasTopPill: Boolean,
    cellScale: Float = 1f,
    transformA: SlotTransform? = null,
    transformB: SlotTransform? = null,
    modifier: Modifier = Modifier
) {
    val activePair = remember(step.elementStates, step.swappedIndices, step.leftPointer, step.rightPointer) {
        when {
            step.swappedIndices != null -> step.swappedIndices
            step.leftPointer != null && step.rightPointer != null && step.leftPointer != step.rightPointer ->
                Pair(step.leftPointer, step.rightPointer)
            else -> {
                val comparing = step.elementStates.filter { it.value == ElementState.COMPARING }.keys.toList()
                if (comparing.size >= 2) {
                    Pair(comparing[0], comparing[1])
                } else if (comparing.size == 1) {
                    val pivot = step.elementStates.filter { it.value == ElementState.PIVOT }.keys.firstOrNull()
                    if (pivot != null && pivot != comparing[0]) Pair(comparing[0], pivot) else null
                } else null
            }
        }
    }

    val isSwapping = step.swappedIndices != null || step.phaseLabel.contains("SWAP", ignoreCase = true)
    val accentColor = if (isSwapping) AlgoTokens.accentPink else AccentYellow

    val targetAlpha = if (activePair != null && cellCenterXA != null && cellCenterXB != null
        && step.array.isNotEmpty()
    ) 1f else 0f
    val alpha = animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 150),
        label = "pointerBracketAlpha"
    )

    if (alpha.value < 0.01f) return

    val density = LocalDensity.current
    val scaleFactor = cellScale.coerceIn(0.7f, 1.25f)

    Canvas(modifier = modifier) {
        val x1 = cellCenterXA ?: return@Canvas
        val x2 = cellCenterXB ?: return@Canvas

        // Clearance above cell box or top pointer badge
        val topBadgeClearancePx = with(density) { (18.dp + AlgoTokens.space2 + 5.dp).toPx() }
        val noBadgeClearancePx = with(density) { 6.dp.toPx() }
        val baseClearancePx = if (hasTopPill) topBadgeClearancePx else noBadgeClearancePx

        val currentBottomY = cellTopY - baseClearancePx
        val bracketHeightPx = with(density) { ((if (hasTopPill) 13f else 10f) * scaleFactor).dp.toPx() }
        val currentTopY = currentBottomY - bracketHeightPx

        val leftX = minOf(x1, x2)
        val rightX = maxOf(x1, x2)
        val spanX = rightX - leftX
        if (spanX < 1f) return@Canvas

        // Sharp 90-degree pointer bracket path (no corner rounding)
        val path = Path().apply {
            moveTo(leftX, currentBottomY)
            lineTo(leftX, currentTopY)
            lineTo(rightX, currentTopY)
            lineTo(rightX, currentBottomY)
        }

        val primaryStrokePx = with(density) { (1.35f * scaleFactor).dp.toPx() }
        val glowStrokePx = with(density) { (2.6f * scaleFactor).dp.toPx() }

        // Subtle ambient aura with sharp miter joins
        drawPath(
            path = path,
            color = accentColor.copy(alpha = 0.16f * alpha.value),
            style = Stroke(
                width = glowStrokePx,
                cap = StrokeCap.Square,
                join = StrokeJoin.Miter
            )
        )

        // Crisp sharp-edged pointer bracket stroke
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.8f * alpha.value),
                    accentColor.copy(alpha = alpha.value),
                    accentColor.copy(alpha = 0.8f * alpha.value)
                ),
                startX = leftX,
                endX = rightX
            ),
            style = Stroke(
                width = primaryStrokePx,
                cap = StrokeCap.Square,
                join = StrokeJoin.Miter
            )
        )

        // Sharp square pointer tips at the bottom of each vertical leg
        val tipHalfPx = with(density) { (1.6f * scaleFactor).dp.toPx() }
        drawRect(
            color = accentColor.copy(alpha = alpha.value),
            topLeft = Offset(leftX - tipHalfPx, currentBottomY - tipHalfPx),
            size = androidx.compose.ui.geometry.Size(tipHalfPx * 2f, tipHalfPx * 2f)
        )
        drawRect(
            color = accentColor.copy(alpha = alpha.value),
            topLeft = Offset(rightX - tipHalfPx, currentBottomY - tipHalfPx),
            size = androidx.compose.ui.geometry.Size(tipHalfPx * 2f, tipHalfPx * 2f)
        )
    }
}

@Composable
fun ComparisonBridgeOverlay(
    step: VisualizerStep,
    cellCenterXA: Float?,
    cellCenterXB: Float?,
    cellTopY: Float,
    hasTopPill: Boolean,
    transformA: SlotTransform? = null,
    transformB: SlotTransform? = null,
    modifier: Modifier = Modifier,
    cellScale: Float = 1f
) {
    ActivePairPointerBracket(
        step = step,
        cellCenterXA = cellCenterXA,
        cellCenterXB = cellCenterXB,
        cellTopY = cellTopY,
        hasTopPill = hasTopPill,
        cellScale = cellScale,
        transformA = transformA,
        transformB = transformB,
        modifier = modifier
    )
}


