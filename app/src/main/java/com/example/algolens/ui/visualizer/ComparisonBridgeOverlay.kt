package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens

/**
 * Luminous comparison & swap bridge connecting active elements.
 * Supports straight staple-bracket `|—|` and bezier arc geometry,
 * precisely aligned to cells via [lazyListState] layout measurements.
 * All distracting pill badges have been eliminated for minimal, high-tech clarity.
 */
@Composable
fun ComparisonBridgeOverlay(
    step: VisualizerStep,
    cellWidth: Dp,
    slotGap: Dp,
    lazyListState: LazyListState,
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

    AnimatedVisibility(
        visible = activePair != null && step.array.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (activePair == null) return@AnimatedVisibility

        val (idxA, idxB) = activePair
        val minIdx = minOf(idxA, idxB).coerceIn(0, step.array.lastIndex)
        val maxIdx = maxOf(idxA, idxB).coerceIn(0, step.array.lastIndex)

        if (minIdx == maxIdx) return@AnimatedVisibility

        val density = LocalDensity.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlgoTokens.space5)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AlgoTokens.space5)
            ) {
                val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
                val itemA = visibleItems.find { it.index == minIdx }
                val itemB = visibleItems.find { it.index == maxIdx }

                // Calculate exact pixel centers of target cells, offset by left padding
                val leftPaddingOffset = with(density) { AlgoTokens.space1.toPx() }
                val curtainShiftA = if (step.sortedBoundary == minIdx && minIdx > 0) with(density) { AlgoTokens.space2.toPx() } else 0f
                val curtainShiftB = if (step.sortedBoundary == maxIdx && maxIdx > 0) with(density) { AlgoTokens.space2.toPx() } else 0f

                val (x1Px, x2Px) = if (itemA != null && itemB != null) {
                    Pair(
                        itemA.offset + (itemA.size / 2f) + leftPaddingOffset + curtainShiftA,
                        itemB.offset + (itemB.size / 2f) + leftPaddingOffset + curtainShiftB
                    )
                } else {
                    // Precise fallback accounting for item horizontal padding and start offset
                    val itemWidthPx = with(density) { (cellWidth + AlgoTokens.space1 * 2).toPx() }
                    val slotGapPx = with(density) { slotGap.toPx() }
                    val totalWidthPx = (itemWidthPx * step.array.size) + (slotGapPx * (step.array.size - 1).coerceAtLeast(0))
                    val startOffsetPx = ((size.width - totalWidthPx) / 2f).coerceAtLeast(with(density) { AlgoTokens.space1.toPx() })
                    Pair(
                        startOffsetPx + (itemWidthPx + slotGapPx) * minIdx + (itemWidthPx / 2f) + leftPaddingOffset + curtainShiftA,
                        startOffsetPx + (itemWidthPx + slotGapPx) * maxIdx + (itemWidthPx / 2f) + leftPaddingOffset + curtainShiftB
                    )
                }

                val bottomY = size.height - with(density) { AlgoTokens.space1.toPx() }
                val topY = with(density) { AlgoTokens.space1.toPx() }

                val path = Path().apply {
                    if (useStraightBracket) {
                        // Straight staple bracket |—| with sleek micro-rounded corners
                        val maxCorner = ((x2Px - x1Px) / 2f).coerceAtLeast(0f)
                        val cornerRadius = with(density) { AlgoTokens.radiusXxs.toPx() }.coerceAtMost(maxCorner)
                        moveTo(x1Px, bottomY)
                        lineTo(x1Px, topY + cornerRadius)
                        if (cornerRadius > 0f) {
                            quadraticTo(x1Px, topY, x1Px + cornerRadius, topY)
                        }
                        lineTo(x2Px - cornerRadius, topY)
                        if (cornerRadius > 0f) {
                            quadraticTo(x2Px, topY, x2Px, topY + cornerRadius)
                        }
                        lineTo(x2Px, bottomY)
                    } else {
                        // Classical bezier arc
                        val midXPx = (x1Px + x2Px) / 2f
                        moveTo(x1Px, bottomY)
                        quadraticTo(midXPx, topY, x2Px, bottomY)
                    }
                }

                // Ambient luminous glow
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.22f),
                    style = Stroke(
                        width = with(density) { AlgoTokens.space2.toPx() },
                        cap = StrokeCap.Round
                    )
                )

                // Crisp primary bridge stroke
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.5f),
                            accentColor,
                            accentColor.copy(alpha = 0.5f)
                        ),
                        startX = x1Px,
                        endX = x2Px
                    ),
                    style = Stroke(
                        width = with(density) { AlgoTokens.strokeThin.toPx() },
                        cap = StrokeCap.Round
                    )
                )

                // Precise anchor pins at cell connection points
                val dotRadius = with(density) { AlgoTokens.space1.toPx() }
                drawCircle(
                    color = accentColor,
                    radius = dotRadius,
                    center = Offset(x1Px, bottomY)
                )
                drawCircle(
                    color = accentColor,
                    radius = dotRadius,
                    center = Offset(x2Px, bottomY)
                )
            }
        }
    }
}
