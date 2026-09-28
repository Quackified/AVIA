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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

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
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.algolens.model.BufferOp
import com.example.algolens.model.QueueOp
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.pressPhysics

/**
 * Animated Buffer Memory Visualizer for Stack (LIFO) and Queue (FIFO) data structures.
 * Features:
 * - DoubleBezelShell instrument framing & subtle grid-dot chamber background
 * - Inline stage controls (`Push(val)`, `Pop()`, `Peek()`, `Enqueue(val)`, `Dequeue()`)
 */
@Composable
fun BufferVisualizer(
    step: VisualizerStep,
    isStack: Boolean = true,
    tailSize: Int = step.buffer.size,
    canAppend: Boolean = tailSize < step.bufferCapacity,
    canRemove: Boolean = tailSize > 0,
    onStackOp: ((BufferOp) -> Unit)? = null,
    onQueueOp: ((QueueOp) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var nextInputValue by remember(isStack, tailSize) {
        val seed = ((tailSize + 1) * 14 + 18) % 89 + 10
        mutableIntStateOf(seed)
    }

    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        shellBorder = PrimaryCyan.copy(alpha = 0.20f),
        coreColor = CanvasBackground,
        contentPadding = PaddingValues(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Top Buffer Telemetry Header ──
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
                    fontSize = AlgoType.microSize,
                    letterSpacing = AlgoType.trackTight
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step.buffer.isNotEmpty()) {
                        val headText = if (isStack) "TOP=${step.buffer.last().value}" else "FRONT=${step.buffer.first().value}"
                        Text(
                            text = headText,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Frame: ${step.buffer.size}/${step.bufferCapacity} · Tail: $tailSize/${step.bufferCapacity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
            }

            // ── Main Stack / Queue Chamber ──
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isStack) {
                    StackCanvas(step)
                } else {
                    QueueCanvas(step)
                }
            }

            // ── Inline Interactive Stage Operations Bar (Appends to Sequence Tail) ──
            if (onStackOp != null || onQueueOp != null) {
                BufferStageControls(
                    isStack = isStack,
                    nextValue = nextInputValue,
                    tailSize = tailSize,
                    capacity = step.bufferCapacity,
                    onCycleValue = { nextInputValue = ((nextInputValue + 13) % 89) + 10 },
                    canRemove = canRemove,
                    isFull = !canAppend,
                    onPushOrEnqueue = {
                        if (isStack) {
                            onStackOp?.invoke(BufferOp.Push(nextInputValue))
                        } else {
                            onQueueOp?.invoke(QueueOp.Enqueue(nextInputValue))
                        }
                    },
                    onPopOrDequeue = {
                        if (isStack) {
                            onStackOp?.invoke(BufferOp.Pop)
                        } else {
                            onQueueOp?.invoke(QueueOp.Dequeue)
                        }
                    },
                    onPeek = if (isStack) {
                        { onStackOp?.invoke(BufferOp.Peek) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun BufferStageControls(
    isStack: Boolean,
    nextValue: Int,
    tailSize: Int,
    capacity: Int,
    onCycleValue: () -> Unit,
    canRemove: Boolean,
    isFull: Boolean,
    onPushOrEnqueue: () -> Unit,
    onPopOrDequeue: () -> Unit,
    onPeek: (() -> Unit)?
) {
    val pillShape = RoundedCornerShape(AlgoTokens.radiusXxs)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space1, vertical = AlgoTokens.space1),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Value chip (tap to cycle next value)
        Row(
            modifier = Modifier
                .heightIn(min = 28.dp)
                .clip(pillShape)
                .background(DarkBackground)
                .border(AlgoTokens.strokeHairline, BorderSubtle, pillShape)
                .pressPhysics(shape = pillShape, accent = PrimaryCyan)
                .clickable { onCycleValue() }
                .padding(horizontal = AlgoTokens.space2, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
        ) {
            Text(
                text = "TAIL($tailSize/$capacity) VAL:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = AlgoType.microSize
            )
            Text(
                text = "$nextValue ↻",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold
            )
        }

        // Action pills (compact, shorter vertically)
        Row(
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // + PUSH / + ENQUEUE
            Box(
                modifier = Modifier
                    .heightIn(min = 28.dp)
                    .clip(pillShape)
                    .background(if (!isFull) CyanSubtle else DarkBackground)
                    .border(
                        AlgoTokens.strokeHairline,
                        if (!isFull) PrimaryCyan.copy(alpha = 0.4f) else BorderSubtle,
                        pillShape
                    )
                    .pressPhysics(shape = pillShape, accent = PrimaryCyan, enabled = !isFull)
                    .clickable(enabled = !isFull) { onPushOrEnqueue() }
                    .padding(horizontal = AlgoTokens.space2, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isStack) "+ PUSH($nextValue)" else "+ ENQ($nextValue)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (!isFull) PrimaryCyan else TextDark,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold
                )
            }

            // - POP / - DEQUEUE
            Box(
                modifier = Modifier
                    .heightIn(min = 28.dp)
                    .clip(pillShape)
                    .background(if (canRemove) RedSubtle else DarkBackground)
                    .border(
                        AlgoTokens.strokeHairline,
                        if (canRemove) AccentRed.copy(alpha = 0.4f) else BorderSubtle,
                        pillShape
                    )
                    .pressPhysics(shape = pillShape, accent = AccentRed, enabled = canRemove)
                    .clickable(enabled = canRemove) { onPopOrDequeue() }
                    .padding(horizontal = AlgoTokens.space2, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isStack) "− POP()" else "− DEQ()",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (canRemove) AccentRed else TextDark,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold
                )
            }

            // PEEK() (Stack only)
            if (onPeek != null) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 28.dp)
                        .clip(pillShape)
                        .background(if (canRemove) PurpleSubtle else DarkBackground)
                        .border(
                            AlgoTokens.strokeHairline,
                            if (canRemove) SecondaryPurple.copy(alpha = 0.4f) else BorderSubtle,
                            pillShape
                        )
                        .pressPhysics(shape = pillShape, accent = SecondaryPurple, enabled = canRemove)
                        .clickable(enabled = canRemove) { onPeek() }
                        .padding(horizontal = AlgoTokens.space2, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PEEK()",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (canRemove) PurpleGlow else TextDark,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                }
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
/**
 * Redesigned Stack canvas — an open-top vertical chamber (beaker / U-tube)
 * with discrete slot guides (#0 to #7), a dynamic TOP pointer that tracks
 * the active topmost element, an open entrance mouth, and a grounded pedestal.
 *
 * Items enter from above (slideEnter Top) and stack onto the grounded base.
 * Ghost slot trays visually communicate the full capacity and remaining slots.
 */
@Composable
private fun StackCanvas(step: VisualizerStep) {
    val capacity = step.bufferCapacity.coerceIn(1, 8)
    val currentSize = step.buffer.size
    val topIndex = if (currentSize > 0) currentSize - 1 else -1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // Dynamic TOP Pointer Column (on the left of the beaker)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .width(48.dp)
                .padding(bottom = 8.dp) // align above base platform
        ) {
            // Space above container matching the top mouth label height
            Spacer(modifier = Modifier.height(18.dp))

            for (slotIdx in (capacity - 1) downTo 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (slotIdx == topIndex) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "TOP",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                            Text(
                                text = "➔",
                                color = SecondaryPurple,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (currentSize == 0 && slotIdx == 0) {
                        Text(
                            text = "BASE ➔",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Center Stack Receptacle: Mouth Header + Open-Top Beaker + Grounded Platform
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.width(200.dp)
        ) {
            // ── Open Top Guidance Header ──
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(16.dp)
            ) {
                if (currentSize == capacity) {
                    Text(
                        text = "STACK FULL",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentRed,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackTight
                    )
                }
            }

            // ── Open-Top Chamber Container (Walls on Left, Bottom, Right; Open at Top with Notches) ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val stroke = 1.5.dp.toPx()
                        val wallColor = SecondaryPurple.copy(alpha = 0.50f)
                        val notch = 8.dp.toPx()

                        // Left entry lip notch (outward tick)
                        drawLine(
                            color = wallColor,
                            start = Offset(0f, 0f),
                            end = Offset(notch, 0f),
                            strokeWidth = stroke
                        )
                        // Left wall
                        drawLine(
                            color = wallColor,
                            start = Offset(notch, 0f),
                            end = Offset(notch, size.height),
                            strokeWidth = stroke
                        )
                        // Bottom floor
                        drawLine(
                            color = wallColor,
                            start = Offset(notch, size.height),
                            end = Offset(size.width - notch, size.height),
                            strokeWidth = stroke
                        )
                        // Right wall
                        drawLine(
                            color = wallColor,
                            start = Offset(size.width - notch, size.height),
                            end = Offset(size.width - notch, 0f),
                            strokeWidth = stroke
                        )
                        // Right entry lip notch (outward tick)
                        drawLine(
                            color = wallColor,
                            start = Offset(size.width - notch, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = stroke
                        )
                    }
                    .background(CardBackground.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // 8 Discrete Slot Guides (Rendered #7 down to #0)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (slotIdx in (capacity - 1) downTo 0) {
                        val item = step.buffer.getOrNull(slotIdx)
                        val isTop = slotIdx == topIndex

                        if (item != null) {
                            // Filled Slot Cell
                            val (bgCol, textCol) = when (item.state) {
                                ElementState.ACTIVE, ElementState.FOUND -> Pair(PrimaryCyan, DarkBackground)
                                ElementState.SWAPPING -> Pair(AccentRed, Color.White)
                                ElementState.COMPARING -> Pair(AccentYellow, DarkBackground)
                                else -> Pair(
                                    if (isTop) PurpleSubtle else CardBackgroundElevated,
                                    if (isTop) PurpleGlow else TextPrimary
                                )
                            }

                            key(item.id) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .slideEnter(visible = true, direction = SlideDirection.Top)
                                        .nodePop(item.state == ElementState.ACTIVE)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(bgCol)
                                        .border(
                                            width = if (isTop) 1.2.dp else 1.dp,
                                            color = if (isTop) SecondaryPurple else BorderMedium,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Slot index indicator
                                    Text(
                                        text = "#$slotIdx",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (item.state == ElementState.ACTIVE || item.state == ElementState.FOUND) TextDark else TextMuted,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Item value
                                    Text(
                                        text = item.value,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textCol,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )

                                    // State tag
                                    if (isTop) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(SecondaryPurple)
                                                .padding(horizontal = 3.dp, vertical = 0.5.dp)
                                        ) {
                                            Text(
                                                text = "TOP",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(18.dp))
                                    }
                                }
                            }
                        } else {
                            // Empty Ghost Slot Guide
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.Transparent)
                                    .border(
                                        AlgoTokens.strokeHairline,
                                        BorderSubtle.copy(alpha = 0.35f),
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#$slotIdx",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontSize = 8.sp
                                )
                                Text(
                                    text = "· · ·",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark.copy(alpha = 0.6f),
                                    fontSize = 8.sp
                                )
                                Spacer(modifier = Modifier.width(18.dp))
                            }
                        }
                    }
                }
            }

            // ── Grounded Base Platform ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                SecondaryPurple.copy(alpha = 0.7f),
                                PrimaryCyan.copy(alpha = 0.5f),
                                SecondaryPurple.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }

        // ── Capacity Indicator on Right Side ──
        Spacer(modifier = Modifier.width(10.dp))
        CapacityIndicator(size = currentSize, capacity = capacity)
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
            fontSize = AlgoType.microSize,
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
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "enqueue() to add",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDark,
                        fontSize = AlgoType.microSize
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
                                        fontSize = AlgoType.bodySize
                                    )
                                }

                                // Slot index
                                Text(
                                    text = "#$index",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark,
                                    fontSize = AlgoType.microSize
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
            fontSize = AlgoType.microSize,
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
        fontSize = AlgoType.microSize,
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
