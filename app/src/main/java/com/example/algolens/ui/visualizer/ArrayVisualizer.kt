package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import com.example.algolens.ui.theme.AlgoTokens
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BarUnsorted
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Animated Vertical Bar Visualizer powered by unified VisualizerStep state.
 *
 * Features:
 * - Phase banner header (reuses PhaseBanner from CellArrayVisualizer)
 * - Active range dimming: bars outside `step.activeRange` render at 35% opacity
 * - Swap pair scale-up: bars in `step.swappedIndices` spring-scale to 1.12x
 * - Enhanced pointer badges: pivot (pink), min (yellow), key (purple)
 * - Sorted boundary gradient divider
 * - Smooth bar height & color transitions
 */
@Composable
fun ArrayVisualizer(
    step: VisualizerStep,
    algorithmName: String = "",
    maxVal: Int = (step.array.maxOrNull() ?: 100),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Phase Banner (shown when phaseLabel is present) ──
        if (step.phaseLabel.isNotBlank()) {
            PhaseBanner(
                step = step,
                algorithmName = algorithmName,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ── Bar Chart Container ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackgroundElevated)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 48.dp)
        ) {
            // ── Shared swap-flight map: bars physically cross slots with the
            //    lift → glide → settle arc (same primitive as cells). ──
            var rowWidthPx by remember { mutableStateOf(0) }
            val previousArray = remember { mutableStateOf(step.array) }
            val flightMap = rememberSlotFlightMap(
                current = step.array,
                previous = previousArray.value,
                swappedIndices = step.swappedIndices,
                slotPitchPx = if (step.array.isEmpty()) 0f else rowWidthPx.toFloat() / step.array.size,
                key = step.stepIndex,
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { rowWidthPx = it.width },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                step.array.forEachIndexed { index, value ->
                    val state = step.elementStates[index] ?: ElementState.IDLE
                    val topPointer = step.topPointers.entries.find { it.value == index }
                    val bottomPointer = step.bottomPointers.entries.find { it.value == index }

                    // ── Active Range Dimming ──
                    val isInActiveRange = step.activeRange == null || index in step.activeRange
                    val targetAlpha = if (isInActiveRange) 1f else 0.35f
                    val animatedAlpha by animateFloatAsState(
                        targetValue = targetAlpha,
                        animationSpec = tween(durationMillis = 250),
                        label = "barAlpha_$index"
                    )

                    // ── Swap Pair Flag (used for border glow; physical scale is
                    //    handled by the shared flight graphicsLayer below) ──
                    val isSwapped = step.swappedIndices != null && (step.swappedIndices.first == index || step.swappedIndices.second == index)

                    val targetFraction = (value.toFloat() / maxVal.coerceAtLeast(1)).coerceIn(0.06f, 1f)
                    val animatedHeightFraction by animateFloatAsState(
                        targetValue = targetFraction,
                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                        label = "barHeight_$index"
                    )

                    val targetColor = when (state) {
                        ElementState.PIVOT -> AccentPink
                        ElementState.COMPARING -> AccentYellow
                        ElementState.SWAPPING -> AccentRed
                        ElementState.ACTIVE, ElementState.FOUND -> PrimaryCyan
                        ElementState.SORTED -> AccentGreen
                        ElementState.TARGET -> SecondaryPurple
                        else -> BarUnsorted
                    }

                    val animatedColor by animateColorAsState(
                        targetValue = targetColor,
                        animationSpec = tween(durationMillis = 180),
                        label = "barColor_$index"
                    )

                    // ── Bar border glow for special states ──
                    val barBorderColor = when {
                        isSwapped -> AccentRed.copy(alpha = 0.7f)
                        state == ElementState.PIVOT -> AccentPink.copy(alpha = 0.5f)
                        index == step.pivotIndex -> AccentPink.copy(alpha = 0.5f)
                        index == step.minIndex -> AccentYellow.copy(alpha = 0.5f)
                        state == ElementState.SORTED -> AccentGreen.copy(alpha = 0.3f)
                        else -> Color.Transparent
                    }
                    val animatedBorderColor by animateColorAsState(
                        targetValue = barBorderColor,
                        animationSpec = tween(durationMillis = 200),
                        label = "barBorder_$index"
                    )

                    // ── Physical swap flight (render-phase reads only) ──
                    val flight = flightMap.transform(index)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .graphicsLayer {
                                translationX = flight.offsetX.value
                                translationY = flight.offsetY.value
                                val s = flight.scale.value
                                scaleX = s
                                scaleY = s
                                shadowElevation =
                                    if (s > 1.01f || kotlin.math.abs(flight.offsetY.value) > 0.5f) {
                                        AlgoTokens.elevationTraveling.toPx()
                                    } else 0f
                            }
                            .alpha(animatedAlpha),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // ── Top Pointer Indicator ──
                        Box(
                            modifier = Modifier.height(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (topPointer != null) {
                                val label = topPointer.key
                                val badgeBg = when (label.uppercase()) {
                                    "PIVOT" -> AccentPink
                                    "MIN" -> AccentYellow
                                    "KEY" -> SecondaryPurple
                                    else -> Color(0xFF475569)
                                }
                                val badgeText = when (label.uppercase()) {
                                    "PIVOT" -> DarkBackground
                                    "MIN" -> DarkBackground
                                    "KEY" -> Color.White
                                    else -> Color.White
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = label.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = badgeText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 7.sp
                                    )
                                }
                            }
                        }

                        // ── Bar Fill (with scale for swap pairs) ──
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedHeightFraction)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .then(
                                        if (animatedBorderColor != Color.Transparent) {
                                            Modifier.border(
                                                1.dp,
                                                animatedBorderColor,
                                                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                            )
                                        } else Modifier
                                    )
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                animatedColor,
                                                animatedColor.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // ── Value Label ──
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = when (state) {
                                ElementState.PIVOT, ElementState.SWAPPING -> Color.White
                                ElementState.IDLE -> TextMuted
                                else -> animatedColor
                            },
                            fontWeight = if (state != ElementState.IDLE) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 8.5.sp,
                            maxLines = 1
                        )

                        // ── Index or Bottom Pointer Label ──
                        Box(
                            modifier = Modifier.height(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (bottomPointer != null) {
                                val label = bottomPointer.key
                                val badgeBg = when (label.lowercase()) {
                                    "i", "low" -> PrimaryCyan
                                    "j", "mid" -> AccentYellow
                                    "min" -> AccentYellow
                                    "key" -> SecondaryPurple
                                    else -> SecondaryPurple
                                }
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(badgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkBackground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 7.5.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = index.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark,
                                    fontSize = 7.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun ArrayVisualizerPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(340.dp).padding(16.dp)) {
            ArrayVisualizer(
                step = VisualizerStep(
                    stepIndex = 3,
                    description = "Partition step comparing 34 and pivot 7",
                    array = listOf(64, 34, 25, 12, 22, 11, 90),
                    elementStates = mapOf(
                        1 to ElementState.COMPARING,
                        6 to ElementState.PIVOT,
                        0 to ElementState.SORTED
                    ),
                    topPointers = mapOf("pivot" to 6),
                    bottomPointers = mapOf("j" to 1),
                    phaseLabel = "PARTITIONING",
                    activeRange = 1..5,
                    pivotIndex = 6,
                    swappedIndices = Pair(1, 3)
                ),
                algorithmName = "Quick Sort"
            )
        }
    }
}
