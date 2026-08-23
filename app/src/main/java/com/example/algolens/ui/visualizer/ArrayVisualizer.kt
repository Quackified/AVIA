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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Animated Vertical Bar Visualizer powered by unified VisualizerStep state.
 * Features top pointer arrows, index labels, and smooth state-driven animations.
 */
@Composable
fun ArrayVisualizer(
    step: VisualizerStep,
    maxVal: Int = (step.array.maxOrNull() ?: 100),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            step.array.forEachIndexed { index, value ->
                val state = step.elementStates[index] ?: ElementState.IDLE
                val topPointer = step.topPointers.entries.find { it.value == index }
                val bottomPointer = step.bottomPointers.entries.find { it.value == index }

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

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
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
                            val isPivot = label.equals("pivot", ignoreCase = true)
                            val badgeBg = if (isPivot) AccentPink else Color(0xFF475569)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isPivot) DarkBackground else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 7.sp
                                )
                            }
                        }
                    }

                    // ── Bar Fill ──
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
                                .background(animatedColor)
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
                                    text = label,
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

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun ArrayVisualizerPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(260.dp).padding(16.dp)) {
            ArrayVisualizer(
                step = VisualizerStep(
                    stepIndex = 3,
                    description = "Partition step comparing 34 and pivot 7",
                    array = listOf(64, 34, 25, 12, 22, 11, 90),
                    elementStates = mapOf(
                        1 to ElementState.COMPARING,
                        6 to ElementState.PIVOT
                    ),
                    topPointers = mapOf("pivot" to 6),
                    bottomPointers = mapOf("j" to 1)
                )
            )
        }
    }
}
