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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CardBackgroundHover
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
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Buffer Header Tag — plain ASCII, no special characters
            // (the `|` separator was rendering as a corrupted glyph on
            // some devices; the user opted for clean text).
            val label = step.bufferLabel ?: if (isStack) "STACK - LIFO (Last In, First Out)" else "QUEUE - FIFO (First In, First Out)"
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
                    letterSpacing = 0.6.sp
                )
                Text(
                    text = "Size: ${step.buffer.size} / ${step.bufferCapacity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.5.sp
                )
            }

            if (isStack) {
                StackCanvas(step)
            } else {
                QueueCanvas(step)
            }
        }
    }
}

/**
 * Stack canvas — vertical stack with a TOP arrow, a base platform
 * under the bottom block, and a capacity indicator on the right side.
 *
 * The TOP arrow + base together give the stack a clear "physical
 * container" metaphor: items slide in from above (the slideEnter
 * direction is `Top` on the leaf cells) and rest on the base. The
 * capacity indicator on the right tells the user at a glance how
 * much of the buffer's max size is used.
 */
@Composable
private fun StackCanvas(step: VisualizerStep) {
    val capacity = step.bufferCapacity.coerceAtLeast(1)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // Stack column with TOP arrow above and base below
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
            modifier = Modifier.width(200.dp)
        ) {
            // ── TOP arrow indicator ──
            TopArrowIndicator(isActive = step.buffer.isNotEmpty())

            // ── Stack container ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = AlgoTokens.radiusXxs, bottomEnd = AlgoTokens.radiusXxs))
                    .background(CardBackground)
                    .border(
                        width = 1.5.dp,
                        color = PrimaryCyan.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = AlgoTokens.radiusXxs, bottomEnd = AlgoTokens.radiusXxs)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (step.buffer.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = "EMPTY",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "push() to add",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontSize = 7.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
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
                            // Keyed by item.id so the slide-in entry
                            // animation runs on every push.
                            key(item.id) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .slideEnter(visible = true, direction = SlideDirection.Top)
                                        .nodePop(item.state == ElementState.ACTIVE)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(bgCol)
                                        .border(
                                            width = if (isTop) 1.5.dp else 1.dp,
                                            color = if (isTop) SecondaryPurple else BorderMedium,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
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
                                                fontSize = 6.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Base platform ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                PrimaryCyan.copy(alpha = 0.5f),
                                SecondaryPurple.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }

        // ── Capacity indicator on the right side ──
        Spacer(modifier = Modifier.width(12.dp))
        CapacityIndicator(size = step.buffer.size, capacity = capacity)
    }
}

/**
 * Stack TOP arrow — a down-arrow + "TOP" label, sits above the stack
 * container. Shown in muted colors when the stack is empty.
 */
@Composable
private fun TopArrowIndicator(isActive: Boolean) {
    val accent = if (isActive) SecondaryPurple else TextMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = "TOP",
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = "Top of stack",
            tint = accent,
            modifier = Modifier.size(10.dp)
        )
    }
}

/**
 * Vertical capacity indicator — "n / cap" rotated 90° (read bottom-up
 * via `rotation = 270f`). Sits to the right of the stack and shows
 * how many of the maximum slots are filled.
 */
@Composable
private fun CapacityIndicator(size: Int, capacity: Int) {
    val fraction = (size.toFloat() / capacity.toFloat()).coerceIn(0f, 1f)
    Column(
        modifier = Modifier.height(180.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar (vertical fill)
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CanvasBackground)
                .border(AlgoTokens.strokeHairline, BorderSubtle, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .background(
                        Brush.verticalGradient(
                            listOf(AccentGreen, PrimaryCyan)
                        )
                    )
                    .align(Alignment.BottomStart)
            )
        }
        Spacer(modifier = Modifier.height(AlgoTokens.space2))
        Text(
            text = "$size/$capacity",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Queue canvas — horizontal conveyor with a "FRONT" gate on the left
 * and a "REAR" gate on the right. Blocks slide in from the right
 * (slideEnter direction = `Right`).
 */
@Composable
private fun QueueCanvas(step: VisualizerStep) {
    val capacity = step.bufferCapacity.coerceAtLeast(1)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        // Conveyor row with FRONT/REAR gates
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackground)
                .border(width = AlgoTokens.strokeMedium, color = PrimaryCyan.copy(alpha = 0.4f), shape = RoundedCornerShape(AlgoTokens.radiusSm))
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
            contentAlignment = Alignment.Center
        ) {
            if (step.buffer.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Text(
                        text = "EMPTY QUEUE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "enqueue() to add",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDark,
                        fontSize = 7.sp
                    )
                }
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

                        key(item.id) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                            ) {
                                // FRONT / REAR gate labels
                                if (isFront) {
                                    QueueGate(label = "FRONT", color = AccentGreen)
                                } else if (isRear) {
                                    QueueGate(label = "REAR", color = PurpleGlow)
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Block
                                Box(
                                    modifier = Modifier
                                        .size(width = 44.dp, height = 48.dp)
                                        .slideEnter(visible = true, direction = SlideDirection.Right)
                                        .nodePop(item.state == ElementState.ACTIVE)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                        .background(bgCol)
                                        .border(
                                            width = if (isFront || isRear) 1.5.dp else 1.dp,
                                            color = if (isFront) AccentGreen else if (isRear) SecondaryPurple else BorderMedium,
                                            shape = RoundedCornerShape(AlgoTokens.radiusXxs)
                                        ),
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

                                // Slot index
                                Text(
                                    text = "#$index",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark,
                                    fontSize = 7.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom legend / capacity
        Text(
            text = "FRONT ── ${step.buffer.size} / $capacity ── REAR",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * "FRONT" / "REAR" gate pill — a small text label that visually
 * marks the ends of the queue conveyor. Plain text, no icons
 * (per user preference).
 */
@Composable
private fun QueueGate(label: String, color: Color) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontSize = 6.5.sp,
        fontWeight = FontWeight.Bold
    )
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
