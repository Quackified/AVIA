package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import kotlin.math.roundToInt

/**
 * Luminous comparison & swap bridge connecting active elements.
 * Renders a glowing bezier arc directly between two compared or swapping
 * array cells, visually establishing their relationship without clipping.
 */
@Composable
fun ComparisonBridgeOverlay(
    step: VisualizerStep,
    cellWidth: Dp,
    slotGap: Dp,
    modifier: Modifier = Modifier
) {
    // Determine the pair of indices in active comparison or swap
    val activePair = remember(step.elementStates, step.swappedIndices, step.leftPointer, step.rightPointer) {
        when {
            step.swappedIndices != null -> step.swappedIndices
            step.leftPointer != null && step.rightPointer != null && step.leftPointer != step.rightPointer ->
                Pair(step.leftPointer, step.rightPointer)
            else -> {
                val comparing = step.elementStates.filter { it.value == ElementState.COMPARING }.keys.toList()
                if (comparing.size >= 2) Pair(comparing[0], comparing[1]) else null
            }
        }
    }

    val isSwapping = step.swappedIndices != null || step.phaseLabel.contains("SWAP", ignoreCase = true)
    val accentColor = if (isSwapping) AlgoTokens.accentPink else AccentYellow
    val badgeLabel = when {
        isSwapping -> "⇄ SWAP"
        step.comparisonExpr != null -> {
            val expr = step.comparisonExpr.removePrefix("COMPARE:").trim()
            if (expr.length <= 14) expr else "COMPARE"
        }
        else -> "COMPARE"
    }

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

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
        ) {
            val availableWidth = maxWidth
            val nodeCount = step.array.size
            val totalRowWidth = (cellWidth * nodeCount) + (slotGap * (nodeCount - 1).coerceAtLeast(0))
            val startOffset = if (totalRowWidth < availableWidth) {
                (availableWidth - totalRowWidth) / 2
            } else {
                AlgoTokens.space1
            }

            val x1Dp = startOffset + (cellWidth + slotGap) * minIdx + (cellWidth / 2)
            val x2Dp = startOffset + (cellWidth + slotGap) * maxIdx + (cellWidth / 2)
            val midXDp = (x1Dp + x2Dp) / 2

            val x1Px = with(density) { x1Dp.toPx() }
            val x2Px = with(density) { x2Dp.toPx() }
            val midXPx = with(density) { midXDp.toPx() }

            Canvas(modifier = Modifier.fillMaxWidth().height(26.dp)) {
                val bottomY = size.height - 2.dp.toPx()
                val peakY = 8.dp.toPx()

                val path = Path().apply {
                    moveTo(x1Px, bottomY)
                    quadraticTo(midXPx, peakY, x2Px, bottomY)
                }

                // Outer ambient glow
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.25f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Core crisp stroke
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.4f),
                            accentColor,
                            accentColor.copy(alpha = 0.4f)
                        ),
                        startX = x1Px,
                        endX = x2Px
                    ),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Anchor dots at cell connection points
                drawCircle(
                    color = accentColor,
                    radius = 2.5.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x1Px, bottomY)
                )
                drawCircle(
                    color = accentColor,
                    radius = 2.5.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x2Px, bottomY)
                )
            }

            // Central relationship pill badge
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (midXPx - with(density) { 24.dp.toPx() }).roundToInt(),
                            y = with(density) { 0.dp.toPx() }.roundToInt()
                        )
                    }
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(AlgoTokens.glassFill)
                    .border(AlgoTokens.strokeThin, accentColor.copy(alpha = 0.6f), RoundedCornerShape(AlgoTokens.radiusXxs))
                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.5.sp
                )
            }
        }
    }
}
