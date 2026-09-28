package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.launch

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
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false,
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
                    StackCanvas(step, playbackSpeedMs, isScrubbing)
                } else {
                    QueueCanvas(step, playbackSpeedMs, isScrubbing)
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
 * Directional motion physics for items inside the vertical Stack beaker.
 * - PUSH: Item drops from above the chamfered mouth, accelerating down and settling into slot with AlgoTokens.settleSpring (bounce).
 * - POP: Item lifts vertically upward past the mouth aperture and dissolves (alpha: 1f -> 0f, scale: 1f -> 0.9f).
 * - PEEK: Item scales subtly to 1.05x with inspection glow.
 * - SCRUB: Running transitions snap to rest state immediately with zero latency.
 */
@Composable
private fun Modifier.stackItemMotion(
    slotIdx: Int,
    capacity: Int,
    isTop: Boolean,
    state: ElementState,
    phaseLabel: String,
    isScrubbing: Boolean,
    playbackSpeedMs: Long
): Modifier = composed {
    val density = LocalDensity.current
    val slotHeightPx = with(density) { 22.dp.toPx() }
    val slotGapPx = with(density) { 3.dp.toPx() }
    val mouthOffsetPx = with(density) { 40.dp.toPx() }
    val totalSlotsAbove = (capacity - 1 - slotIdx)
    val dropDistancePx = (totalSlotsAbove * (slotHeightPx + slotGapPx)) + mouthOffsetPx

    val translationY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(1f) }

    val settleSpring = remember(playbackSpeedMs) {
        val stiffness = when (playbackSpeedMs) {
            300L -> Spring.StiffnessHigh
            1000L -> Spring.StiffnessLow
            else -> Spring.StiffnessMedium
        }
        spring<Float>(dampingRatio = 0.55f, stiffness = stiffness)
    }

    val liftSpring = remember(playbackSpeedMs) {
        val stiffness = when (playbackSpeedMs) {
            300L -> Spring.StiffnessHigh
            1000L -> Spring.StiffnessLow
            else -> Spring.StiffnessMedium
        }
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness)
    }

    val isPushing = (phaseLabel == "PUSH" && isTop) || (state == ElementState.ACTIVE && isTop)
    val isPopping = state == ElementState.SWAPPING || phaseLabel == "POPPING"
    val isPeeking = phaseLabel == "PEEK" || state == ElementState.FOUND

    LaunchedEffect(isPushing, isPopping, isPeeking, isScrubbing) {
        if (isScrubbing) {
            translationY.snapTo(0f)
            alpha.snapTo(1f)
            scale.snapTo(1f)
            return@LaunchedEffect
        }

        when {
            isPopping -> {
                launch { scale.animateTo(0.90f, liftSpring) }
                launch { alpha.animateTo(0f, liftSpring) }
                translationY.animateTo(-dropDistancePx, liftSpring)
            }
            isPushing -> {
                translationY.snapTo(-dropDistancePx)
                alpha.snapTo(1f)
                scale.snapTo(1f)
                translationY.animateTo(0f, settleSpring)
            }
            isPeeking -> {
                translationY.snapTo(0f)
                alpha.snapTo(1f)
                scale.animateTo(1.05f, AlgoTokens.evalSpring)
            }
            else -> {
                translationY.snapTo(0f)
                alpha.snapTo(1f)
                scale.animateTo(1f, AlgoTokens.evalSpring)
            }
        }
    }

    this.graphicsLayer {
        this.translationY = translationY.value
        this.alpha = alpha.value
        this.scaleX = scale.value
        this.scaleY = scale.value
    }
}

/**
 * Directional transit physics for items on the horizontal Queue pipeline conveyor.
 * - ENQUEUE: Item enters from the right airlock and glides into slot with AlgoTokens.cellTravelSpring.
 * - DEQUEUE: Item exits leftward through the front airlock and dissolves.
 */
@Composable
private fun Modifier.queueItemMotion(
    isRear: Boolean,
    isFront: Boolean,
    state: ElementState,
    phaseLabel: String,
    isScrubbing: Boolean,
    playbackSpeedMs: Long
): Modifier = composed {
    val density = LocalDensity.current
    val transitDistancePx = with(density) { 80.dp.toPx() }

    val translationX = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(1f) }

    val travelSpring = remember(playbackSpeedMs) {
        val stiffness = when (playbackSpeedMs) {
            300L -> Spring.StiffnessHigh
            1000L -> Spring.StiffnessLow
            else -> Spring.StiffnessMedium
        }
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness)
    }

    val isEnqueuing = (phaseLabel == "ENQUEUE" && isRear) || (state == ElementState.ACTIVE && isRear)
    val isDequeuing = (phaseLabel == "DEQUEUING" && isFront) || (state == ElementState.SWAPPING && isFront)

    LaunchedEffect(isEnqueuing, isDequeuing, isScrubbing) {
        if (isScrubbing) {
            translationX.snapTo(0f)
            alpha.snapTo(1f)
            scale.snapTo(1f)
            return@LaunchedEffect
        }

        when {
            isDequeuing -> {
                launch { scale.animateTo(0.90f, travelSpring) }
                launch { alpha.animateTo(0f, travelSpring) }
                translationX.animateTo(-transitDistancePx, travelSpring)
            }
            isEnqueuing -> {
                translationX.snapTo(transitDistancePx)
                alpha.snapTo(1f)
                scale.snapTo(1f)
                translationX.animateTo(0f, travelSpring)
            }
            else -> {
                translationX.snapTo(0f)
                alpha.snapTo(1f)
                scale.animateTo(1f, AlgoTokens.evalSpring)
            }
        }
    }

    this.graphicsLayer {
        this.translationX = translationX.value
        this.alpha = alpha.value
        this.scaleX = scale.value
        this.scaleY = scale.value
    }
}

/**
 * Redesigned Stack Canvas — A precision laboratory-grade instrument chamber:
 * - Double-bezel glass tube with laser-etched graduation markers (`[7] MAX` down to `[0] BASE`)
 * - Chamfered entry mouth aperture with visual funnel guides
 * - Dynamic capacity headroom telemetry badge (`COUNT: N / 8`) and vertical fluid gauge
 * - High-legibility monospace memory addresses (`0x07`..`0x00`) and values
 * - Solid beveled grounded base platform
 */
@Composable
private fun StackCanvas(
    step: VisualizerStep,
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false
) {
    val capacity = step.bufferCapacity.coerceIn(1, 8)
    val currentSize = step.buffer.size
    val topIndex = if (currentSize > 0) currentSize - 1 else -1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // ── 1. Laser-Etched Graduation Column + Dynamic TOP Pointer (Left) ──
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .width(68.dp)
                .padding(bottom = 10.dp)
        ) {
            // Space above matching the chamfered mouth aperture
            Spacer(modifier = Modifier.height(26.dp))

            for (slotIdx in (capacity - 1) downTo 0) {
                val isFilled = slotIdx < currentSize
                val isTop = slotIdx == topIndex
                val graduationText = when (slotIdx) {
                    capacity - 1 -> "[$slotIdx] MAX"
                    0 -> "[0] BASE"
                    else -> "[$slotIdx]"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTop) {
                        Text(
                            text = "TOP ➔",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    } else if (currentSize == 0 && slotIdx == 0) {
                        Text(
                            text = "BASE ➔",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.5.sp
                        )
                    }

                    Text(
                        text = graduationText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isTop) PurpleGlow else if (isFilled) PrimaryCyan.copy(alpha = 0.85f) else TextDark.copy(alpha = 0.65f),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isTop || isFilled) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // ── 2. Center Instrument Chamber: Chamfered Mouth + Double-Bezel Glass Tube + Grounded Platform ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.width(214.dp)
        ) {
            // ── Chamfered Entry Mouth Aperture ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .drawBehind {
                        val stroke = 1.5.dp.toPx()
                        val wallColor = SecondaryPurple.copy(alpha = 0.55f)
                        val leftWallX = 10.dp.toPx()
                        val rightWallX = size.width - 10.dp.toPx()

                        // Chamfered entry mouth: funnels in from edges towards tube walls
                        drawLine(
                            color = wallColor,
                            start = Offset(0f, 0f),
                            end = Offset(leftWallX, size.height),
                            strokeWidth = stroke
                        )
                        drawLine(
                            color = wallColor,
                            start = Offset(size.width, 0f),
                            end = Offset(rightWallX, size.height),
                            strokeWidth = stroke
                        )
                        // Mouth guide ticks
                        drawLine(
                            color = PrimaryCyan.copy(alpha = 0.6f),
                            start = Offset(0f, 0f),
                            end = Offset(6.dp.toPx(), 0f),
                            strokeWidth = stroke
                        )
                        drawLine(
                            color = PrimaryCyan.copy(alpha = 0.6f),
                            start = Offset(size.width, 0f),
                            end = Offset(size.width - 6.dp.toPx(), 0f),
                            strokeWidth = stroke
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (currentSize == capacity) {
                    Text(
                        text = "CAPACITY MAXIMUM",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackTight
                    )
                } else {
                    Text(
                        text = "▼ CHAMBER MOUTH ▼",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDark.copy(alpha = 0.55f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackTight
                    )
                }
            }

            // ── Double-Bezel Glass Instrument Tube ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val stroke = 1.5.dp.toPx()
                        val wallColor = SecondaryPurple.copy(alpha = 0.55f)
                        val leftWallX = 10.dp.toPx()
                        val rightWallX = size.width - 10.dp.toPx()

                        // Left vertical glass wall
                        drawLine(
                            color = wallColor,
                            start = Offset(leftWallX, 0f),
                            end = Offset(leftWallX, size.height),
                            strokeWidth = stroke
                        )
                        // Right vertical glass wall
                        drawLine(
                            color = wallColor,
                            start = Offset(rightWallX, 0f),
                            end = Offset(rightWallX, size.height),
                            strokeWidth = stroke
                        )
                        // Bottom floor
                        drawLine(
                            color = wallColor,
                            start = Offset(leftWallX, size.height),
                            end = Offset(rightWallX, size.height),
                            strokeWidth = stroke
                        )

                        // Laser-etched horizontal slot ticks along both walls
                        val rowHeightPx = (size.height - 8.dp.toPx()) / capacity.toFloat()
                        val tickLen = 6.dp.toPx()
                        for (i in 0..capacity) {
                            val tickY = size.height - 4.dp.toPx() - (i * rowHeightPx)
                            drawLine(
                                color = PrimaryCyan.copy(alpha = 0.35f),
                                start = Offset(leftWallX, tickY),
                                end = Offset(leftWallX + tickLen, tickY),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = PrimaryCyan.copy(alpha = 0.35f),
                                start = Offset(rightWallX - tickLen, tickY),
                                end = Offset(rightWallX, tickY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                CardBackground.copy(alpha = 0.30f),
                                CardBackground.copy(alpha = 0.55f),
                                DarkBackground.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // 8 Discrete Slot Trays (#7 down to #0)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (slotIdx in (capacity - 1) downTo 0) {
                        val item = step.buffer.getOrNull(slotIdx)
                        val isTop = slotIdx == topIndex
                        val hexAddress = "0x0$slotIdx"

                        if (item != null) {
                            val isPeek = (step.phaseLabel == "PEEK" || item.state == ElementState.FOUND) && isTop
                            val (bgCol, textCol, borderCol) = when {
                                item.state == ElementState.SWAPPING -> Triple(AccentRed, Color.White, AccentRed)
                                item.state == ElementState.COMPARING -> Triple(AccentYellow, DarkBackground, AccentYellow)
                                isPeek -> Triple(PurpleSubtle, PurpleGlow, SecondaryPurple)
                                item.state == ElementState.ACTIVE || item.state == ElementState.FOUND -> Triple(PrimaryCyan, DarkBackground, PrimaryCyan)
                                isTop -> Triple(PurpleSubtle, PurpleGlow, SecondaryPurple)
                                else -> Triple(CardBackgroundElevated, TextPrimary, BorderMedium)
                            }

                            key(item.id) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(22.dp)
                                        .stackItemMotion(
                                            slotIdx = slotIdx,
                                            capacity = capacity,
                                            isTop = isTop,
                                            state = item.state,
                                            phaseLabel = step.phaseLabel,
                                            isScrubbing = isScrubbing,
                                            playbackSpeedMs = playbackSpeedMs
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(bgCol)
                                        .border(
                                            width = if (isTop || isPeek) 1.5.dp else 1.dp,
                                            color = borderCol,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Memory Address Offset + Slot Index
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = hexAddress,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (item.state == ElementState.ACTIVE || item.state == ElementState.FOUND) TextDark.copy(alpha = 0.7f) else TextMuted,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "#$slotIdx",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (item.state == ElementState.ACTIVE || item.state == ElementState.FOUND) TextDark else TextMuted,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Item Value
                                    Text(
                                        text = item.value,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textCol,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )

                                    // Active State Tag
                                    if (isPeek) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(SecondaryPurple)
                                                .padding(horizontal = 4.dp, vertical = 0.5.dp)
                                        ) {
                                            Text(
                                                text = "PEEK",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontSize = 7.5.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else if (isTop) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(SecondaryPurple)
                                                .padding(horizontal = 4.dp, vertical = 0.5.dp)
                                        ) {
                                            Text(
                                                text = "TOP",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontSize = 7.5.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(22.dp))
                                    }
                                }
                            }
                        } else {
                            // Empty Ghost Slot Guide
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(22.dp)
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
                                    text = hexAddress,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark.copy(alpha = 0.5f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 7.5.sp
                                )
                                Text(
                                    text = "· · · EMPTY SLOT · · ·",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark.copy(alpha = 0.45f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 7.5.sp
                                )
                                Text(
                                    text = "#$slotIdx",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark.copy(alpha = 0.5f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 7.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── Grounded Beveled Base Platform ──
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                SecondaryPurple.copy(alpha = 0.85f),
                                PrimaryCyan.copy(alpha = 0.90f),
                                SecondaryPurple.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ── 3. Dynamic Headroom & Capacity Gauge Column (Right) ──
        StackHeadroomGauge(
            currentSize = currentSize,
            capacity = capacity
        )
    }
}

/**
 * Dynamic Headroom Telemetry Badge & Precision Vertical Gauge.
 * Displays real-time memory headroom: COUNT: N / 8, plus fluid capacity bar.
 */
@Composable
private fun StackHeadroomGauge(
    currentSize: Int,
    capacity: Int
) {
    val fraction = (currentSize.toFloat() / capacity.toFloat()).coerceIn(0f, 1f)
    val headroom = (capacity - currentSize).coerceAtLeast(0)
    val isFull = currentSize >= capacity
    val isNearFull = currentSize >= capacity - 1

    val badgeColor = when {
        isFull -> AccentRed
        isNearFull -> AccentOrange
        else -> PrimaryCyan
    }

    Column(
        modifier = Modifier
            .height(230.dp)
            .padding(bottom = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dynamic Headroom Badge Pill
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                .background(DarkBackground)
                .border(AlgoTokens.strokeHairline, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusXxs))
                .padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "COUNT",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontFamily = FontFamily.Monospace,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$currentSize / $capacity",
                style = MaterialTheme.typography.labelSmall,
                color = badgeColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isFull) "[FULL]" else "[+$headroom FREE]",
                style = MaterialTheme.typography.labelSmall,
                color = if (isFull) AccentRed else TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 6.5.sp
            )
        }

        // Vertical Fluid Gauge Bar with Level Markers
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CanvasBackground)
                .border(AlgoTokens.strokeHairline, BorderSubtle, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .background(
                        Brush.verticalGradient(
                            if (isFull) listOf(AccentRed, AccentOrange)
                            else listOf(AccentGreen, PrimaryCyan)
                        )
                    )
                    .align(Alignment.BottomStart)
            )
        }
    }
}

/**
 * Queue Canvas — Horizontal Pipeline Conveyor Transit.
 * Items enter from the rear airlock (right) and slide horizontally to exit at the front airlock (left).
 */
@Composable
private fun QueueCanvas(
    step: VisualizerStep,
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false
) {
    val capacity = step.bufferCapacity.coerceAtLeast(1)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        // Horizontal Conveyor Pipeline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackground)
                .border(
                    width = AlgoTokens.strokeMedium,
                    color = PrimaryCyan.copy(alpha = 0.40f),
                    shape = RoundedCornerShape(AlgoTokens.radiusSm)
                )
                .drawBehind {
                    // Top and bottom conveyor guide rails
                    val railW = 1.dp.toPx()
                    val railCol = BorderSubtle.copy(alpha = 0.5f)
                    drawLine(railCol, Offset(0f, 10.dp.toPx()), Offset(size.width, 10.dp.toPx()), railW)
                    drawLine(railCol, Offset(0f, size.height - 10.dp.toPx()), Offset(size.width, size.height - 10.dp.toPx()), railW)
                }
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
            contentAlignment = Alignment.Center
        ) {
            if (step.buffer.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Text(
                        text = "EMPTY QUEUE PIPELINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "enqueue() to add to rear airlock",
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                // Front / Rear Airlock Gate Indicators
                                if (isFront) {
                                    QueueGate(label = "◀ HEAD (DEQ)", color = AccentGreen)
                                } else if (isRear) {
                                    QueueGate(label = "TAIL (ENQ) ◀", color = PurpleGlow)
                                } else {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Queue Item Box with Transit Physics
                                Box(
                                    modifier = Modifier
                                        .size(width = 46.dp, height = 50.dp)
                                        .queueItemMotion(
                                            isRear = isRear,
                                            isFront = isFront,
                                            state = item.state,
                                            phaseLabel = step.phaseLabel,
                                            isScrubbing = isScrubbing,
                                            playbackSpeedMs = playbackSpeedMs
                                        )
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
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = AlgoType.bodySize
                                    )
                                }

                                // Slot Index
                                Text(
                                    text = "#$index",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = AlgoType.microSize
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Conveyor Pipeline Telemetry
        val headroom = (capacity - step.buffer.size).coerceAtLeast(0)
        Text(
            text = "FIFO PIPELINE · ${step.buffer.size} / $capacity IN TRANSIT · HEADROOM: $headroom",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontFamily = FontFamily.Monospace,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Gate label pill marking the pipeline ends.
 */
@Composable
private fun QueueGate(label: String, color: Color) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontFamily = FontFamily.Monospace,
        fontSize = 8.sp,
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
