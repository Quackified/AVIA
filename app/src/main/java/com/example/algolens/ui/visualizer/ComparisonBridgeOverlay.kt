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

/**
 * Luminous comparison & swap bridge connecting active elements.
 *
 * Draws a refined, lightweight staple-bracket `|—|` between two cells. Placed
 * as a `matchParentSize()` overlay inside a `Box` that also hosts the `LazyRow`,
 * sharing the exact same coordinate space.
 *
 * [cellCenterXA] and [cellCenterXB] are Box-local horizontal center pixels
 * of the two cells, computed directly via `gridCoordinates.localPositionOf(cellCoords)`.
 *
 * [cellTopY] is the Box-local Y pixel position of the cell number box top edge.
 *
 * [hasTopPill] indicates whether either active cell has a top pointer badge
 * (e.g. PIVOT, MIN, L, R). When true, the bridge floats above the pill with
 * clearance; when false, it floats above the cell with clearance.
 *
 * [transformA] and [transformB] provide live in-flight transforms (offsetX, offsetY)
 * for the two slots. Reading them inside the Canvas draw scope ensures the
 * entire bridge (legs, crossbar, pins) animates seamlessly with cell flights
 * without recomposing the grid.
 */
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
    useStraightBracket: Boolean = true
) {
    // Determine the pair of indices in active comparison or swap
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

    // Animate opacity for smooth enter/exit
    val targetAlpha = if (activePair != null && cellCenterXA != null && cellCenterXB != null
        && step.array.isNotEmpty()
    ) 1f else 0f
    val alpha = animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 150),
        label = "bridgeAlpha"
    )

    if (alpha.value < 0.01f) return

    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val x1 = cellCenterXA ?: return@Canvas
        val x2 = cellCenterXB ?: return@Canvas

        // Vertical lift animation: scaled to a gentle 0.45x intensity so the bridge
        // elegantly accommodates the cells' jump without launching too high
        val oyA = transformA?.offsetY?.value ?: 0f
        val oyB = transformB?.offsetY?.value ?: 0f
        val liftY = minOf(oyA, oyB) * 0f

        // Clearance & hierarchy allotment:
        // TopPointerBadge is 18dp + 4dp space2 above cellTopY = 22dp
        val topBadgeClearancePx = with(density) { (18.dp + AlgoTokens.space2 + 6.dp).toPx() }
        val noBadgeClearancePx = with(density) { 8.dp.toPx() }
        val baseClearancePx = if (hasTopPill) topBadgeClearancePx else noBadgeClearancePx

        // The entire bridge gently lifts up together with the cushioned liftY
        val currentBottomY = cellTopY - baseClearancePx + liftY
        val bridgeHeightPx = with(density) { (if (hasTopPill) 15.dp else 12.dp).toPx() }
        val currentTopY = currentBottomY - bridgeHeightPx

        // Endpoints stay solidly anchored to the two cells' horizontal centers (no horizontal squashing)
        val leftX = minOf(x1, x2)
        val rightX = maxOf(x1, x2)
        val spanX = rightX - leftX
        if (spanX < 1f) return@Canvas

        val path = Path().apply {
            if (useStraightBracket) {
                val maxCorner = (spanX / 2f).coerceAtLeast(0f)
                val cornerRadius = with(density) { AlgoTokens.radiusXxs.toPx() }.coerceAtMost(maxCorner)
                moveTo(leftX, currentBottomY)
                lineTo(leftX, currentTopY + cornerRadius)
                if (cornerRadius > 0f) {
                    quadraticTo(leftX, currentTopY, leftX + cornerRadius, currentTopY)
                }
                lineTo(rightX - cornerRadius, currentTopY)
                if (cornerRadius > 0f) {
                    quadraticTo(rightX, currentTopY, rightX, currentTopY + cornerRadius)
                }
                lineTo(rightX, currentBottomY)
            } else {
                val midX = (leftX + rightX) / 2f
                moveTo(leftX, currentBottomY)
                quadraticTo(midX, currentTopY, rightX, currentBottomY)
            }
        }

        // Ambient luminous aura (refined, less weight)
        drawPath(
            path = path,
            color = accentColor.copy(alpha = 0.15f * alpha.value),
            style = Stroke(
                width = with(density) { 2.dp.toPx() },
                cap = StrokeCap.Round
            )
        )

        // Crisp primary HUD bridge stroke
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.6f * alpha.value),
                    accentColor.copy(alpha = alpha.value),
                    accentColor.copy(alpha = 0.6f * alpha.value)
                ),
                startX = leftX,
                endX = rightX
            ),
            style = Stroke(
                width = with(density) { 1.25.dp.toPx() },
                cap = StrokeCap.Round
            )
        )

        // Precise micro anchor pins
        val dotRadius = with(density) { 1.5.dp.toPx() }
        drawCircle(
            color = accentColor.copy(alpha = alpha.value),
            radius = dotRadius,
            center = Offset(leftX, currentBottomY)
        )
        drawCircle(
            color = accentColor.copy(alpha = alpha.value),
            radius = dotRadius,
            center = Offset(rightX, currentBottomY)
        )
    }
}

