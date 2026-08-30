package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BarUnsorted
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted

/**
 * Unified 1D bar visualizer — talks to the [VisualizerStep] model so it
 * stays in lockstep with the cells, graph and buffer renderers. Active
 * range, swap pair, sorted tail and per-element [ElementState] are all
 * read from the same step object that the code trace / challenge mode
 * observe.
 *
 * Tokenized surfaces ([AlgoTokens.radiusMd], [AlgoTokens.strokeThin],
 * [AlgoTokens.disabledAlpha]) keep this composable aligned with the rest
 * of the workspace.
 */
@Composable
fun BarVisualizer(
    step: VisualizerStep,
    spec: com.example.algolens.model.AlgorithmSpec? = null,
    cellScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val dimOutOfRange = spec?.dimOutOfRangeCells == true
    val maxVal = step.array.maxOrNull()?.coerceAtLeast(1) ?: 1
    val sortedBoundary = step.sortedBoundary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
            .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            // Bar-to-bar gap scales with the header's S/M/L cellSize
            // preset. Bars themselves use `weight(1f)` so the gap
            // change is the only visible delta.
            horizontalArrangement = Arrangement.spacedBy((4f * cellScale).coerceIn(2f, 12f).dp),
            verticalAlignment = Alignment.Bottom
        ) {
            step.array.forEachIndexed { index, value ->
                val state = step.elementStates[index] ?: ElementState.IDLE
                val isInActiveRange = step.activeRange?.contains(index) ?: true
                val isSwapped = step.swappedIndices?.let { it.first == index || it.second == index } ?: false
                val isSorted = sortedBoundary?.let { index >= it } ?: false
                val isPivot = step.pivotIndex == index

                val targetFraction = (value.toFloat() / maxVal.coerceAtLeast(1)).coerceIn(0.06f, 1f)
                val animatedHeightFraction by animateFloatAsState(
                    targetValue = targetFraction,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "barHeight_$index"
                )

                val targetColor = when (state) {
                    ElementState.SORTED, ElementState.FOUND -> PrimaryCyan
                    ElementState.SWAPPING -> AccentRed
                    ElementState.COMPARING -> AccentYellow
                    ElementState.PIVOT, ElementState.TARGET -> SecondaryPurple
                    else -> if (isSorted) PrimaryCyan else BarUnsorted
                }

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 180),
                    label = "barColor_$index"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .alpha(
                            if (!isInActiveRange && dimOutOfRange) AlgoTokens.disabledAlpha + 0.2f
                            else 1f
                        ),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bar Fill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(
                                    if (isSwapped) (animatedHeightFraction * 1.06f).coerceAtMost(1f)
                                    else animatedHeightFraction
                                )
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(animatedColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Value Label Below Bar
                    val isActive = state != ElementState.IDLE || isPivot
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isActive) animatedColor else TextMuted,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        fontSize = (8.5f * cellScale).coerceIn(7f, 12f).sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun BarVisualizerPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        Box(modifier = Modifier.height(260.dp).padding(16.dp)) {
            BarVisualizer(
                step = VisualizerStep(
                    stepIndex = 1,
                    description = "Comparing 34 and 25",
                    array = listOf(64, 34, 25, 12, 22, 11, 90),
                    elementStates = mapOf(
                        1 to ElementState.COMPARING,
                        2 to ElementState.COMPARING
                    ),
                    swappedIndices = null,
                    activeRange = 0..6
                )
            )
        }
    }
}
