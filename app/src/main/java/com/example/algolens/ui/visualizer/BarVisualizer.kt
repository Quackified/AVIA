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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.SortStep
import com.example.algolens.model.StepType
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.BarUnsorted
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextMuted

/**
 * Custom Compose algorithm bar chart visualizer.
 * Built with Compose Layout and dynamic animations.
 */
@Composable
fun BarVisualizer(
    step: SortStep,
    maxVal: Int,
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
                val targetFraction = (value.toFloat() / maxVal.coerceAtLeast(1)).coerceIn(0.06f, 1f)
                val animatedHeightFraction by animateFloatAsState(
                    targetValue = targetFraction,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "barHeight_$index"
                )

                // Determine bar color based on step type and indices
                val targetColor = when {
                    step.type == StepType.DONE -> PrimaryCyan
                    index in step.indices -> {
                        if (step.type == StepType.SWAP) AccentRed else AccentYellow
                    }
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
                                .fillMaxHeight(animatedHeightFraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(animatedColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Value Label Below Bar
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index in step.indices) animatedColor else TextMuted,
                        fontWeight = if (index in step.indices) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 8.5.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun BarVisualizerPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        Box(modifier = Modifier.height(260.dp).padding(16.dp)) {
            BarVisualizer(
                step = SortStep(
                    array = listOf(64, 34, 25, 12, 22, 11, 90),
                    type = StepType.COMPARE,
                    indices = listOf(1, 2),
                    description = "Comparing 34 and 25"
                ),
                maxVal = 90
            )
        }
    }
}
