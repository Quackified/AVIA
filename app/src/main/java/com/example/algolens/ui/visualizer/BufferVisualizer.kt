package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Animated Buffer Memory Visualizer for Stack (LIFO) and Queue (FIFO) data structures.
 */
@Composable
fun BufferVisualizer(
    step: VisualizerStep,
    isStack: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Buffer Header Tag
            val label = step.bufferLabel ?: if (isStack) "Stack · LIFO (Last In First Out)" else "Queue · FIFO (First In First Out)"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Size: ${step.buffer.size} / ${step.bufferCapacity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.5.sp
                )
            }

            if (isStack) {
                // ── Vertical Stack Container ──
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(CardBackground)
                        .border(
                            width = 2.dp,
                            color = PrimaryCyan.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (step.buffer.isEmpty()) {
                        Text(
                            text = "EMPTY STACK",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(bottom = 80.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            step.buffer.reversed().forEachIndexed { indexFromTop, item ->
                                val isTop = indexFromTop == 0
                                val (bgCol, textCol) = when (item.state) {
                                    ElementState.ACTIVE, ElementState.FOUND -> Pair(PrimaryCyan, DarkBackground)
                                    ElementState.SWAPPING -> Pair(AccentRed, Color.White)
                                    ElementState.COMPARING -> Pair(AccentYellow, DarkBackground)
                                    else -> Pair(if (isTop) PurpleSubtle else CardBackgroundElevated, if (isTop) PurpleGlow else TextPrimary)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .nodePop(item.state == ElementState.ACTIVE)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(bgCol)
                                        .border(1.dp, if (isTop) SecondaryPurple else BorderMedium, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.value,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textCol,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    if (isTop) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(SecondaryPurple)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "TOP",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ── Horizontal Queue Conveyor ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBackground)
                        .border(width = 1.5.dp, color = PrimaryCyan.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (step.buffer.isEmpty()) {
                        Text(
                            text = "EMPTY QUEUE (FRONT → REAR)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontSize = 9.sp
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            step.buffer.forEachIndexed { index, item ->
                                val isFront = index == 0
                                val isRear = index == step.buffer.size - 1

                                val (bgCol, textCol) = when (item.state) {
                                    ElementState.ACTIVE, ElementState.FOUND -> Pair(PrimaryCyan, DarkBackground)
                                    ElementState.SWAPPING -> Pair(AccentRed, Color.White)
                                    ElementState.COMPARING -> Pair(AccentYellow, DarkBackground)
                                    else -> Pair(if (isFront) GreenSubtle else if (isRear) PurpleSubtle else CardBackgroundElevated, TextPrimary)
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    // Pointer pill
                                    Box(modifier = Modifier.height(14.dp), contentAlignment = Alignment.Center) {
                                        if (isFront) {
                                            Text("FRONT", style = MaterialTheme.typography.labelSmall, color = AccentGreen, fontSize = 6.5.sp, fontWeight = FontWeight.Bold)
                                        } else if (isRear) {
                                            Text("REAR", style = MaterialTheme.typography.labelSmall, color = PurpleGlow, fontSize = 6.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Block
                                    Box(
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 48.dp)
                                            .nodePop(item.state == ElementState.ACTIVE)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(bgCol)
                                            .border(1.dp, if (isFront) AccentGreen else if (isRear) SecondaryPurple else BorderMedium, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.value,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = textCol,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Text(
                                        text = "#$index",
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
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun BufferVisualizerStackPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BufferVisualizer(
                step = VisualizerStep(
                    stepIndex = 3,
                    description = "Stack: Pushed 42 onto top",
                    buffer = listOf(
                        BufferItem("1", "10"),
                        BufferItem("2", "25"),
                        BufferItem("3", "42", ElementState.ACTIVE)
                    ),
                    bufferLabel = "Stack (Top -> Bottom)"
                ),
                isStack = true
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun BufferVisualizerQueuePreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BufferVisualizer(
                step = VisualizerStep(
                    stepIndex = 3,
                    description = "Queue: Enqueued 60 to rear",
                    buffer = listOf(
                        BufferItem("1", "15"),
                        BufferItem("2", "30"),
                        BufferItem("3", "45"),
                        BufferItem("4", "60", ElementState.ACTIVE)
                    ),
                    bufferLabel = "Queue (Front -> Rear)"
                ),
                isStack = false
            )
        }
    }
}
