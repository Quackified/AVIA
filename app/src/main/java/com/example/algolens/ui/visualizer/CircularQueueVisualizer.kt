package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.QueueOp
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * High-craft Radial Ring Buffer Visualizer for Circular Queue.
 *
 * Features:
 * - 8 radial slots arranged in a circle with rotating FRONT (Cyan) and REAR (Orange) pointer needles.
 * - Center HUD instrument with capacity gauge, live modular arithmetic formula, and state indicator.
 * - Inline stage controls for Enqueue, Dequeue, and Peek.
 */
@Composable
fun CircularQueueVisualizer(
    step: VisualizerStep,
    onQueueOp: ((QueueOp) -> Unit)? = null,
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val capacity = step.bufferCapacity.takeIf { it > 0 } ?: 8
    val bufferItems = step.buffer
    val frontIdx = step.topPointers["F"] ?: -1
    val rearIdx = step.topPointers["R"] ?: -1

    val occupiedCount = bufferItems.count { it.value != "—" && it.value.isNotBlank() }
    val isFull = occupiedCount == capacity
    val isEmpty = occupiedCount == 0

    var nextInputValue by remember(occupiedCount) {
        val seed = ((occupiedCount + 1) * 17 + 11) % 89 + 10
        mutableIntStateOf(seed)
    }

    var peekPulseTrigger by remember { mutableIntStateOf(0) }
    val peekPulseAnim = remember { Animatable(0f) }

    LaunchedEffect(peekPulseTrigger) {
        if (peekPulseTrigger > 0) {
            peekPulseAnim.snapTo(1f)
            peekPulseAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
    }

    LaunchedEffect(step.stepIndex, step.phaseLabel) {
        if (step.phaseLabel == "PEEK" || step.buffer.any { it.state == ElementState.FOUND }) {
            peekPulseAnim.snapTo(1f)
            peekPulseAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
    }

    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        glow = AccentOrange,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AlgoTokens.space3),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Telemetry Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CIRCULAR RING BUFFER",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentOrange,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• (i + 1) % $capacity",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentYellow.copy(alpha = 0.9f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = AlgoType.microSize
                    )
                }

                // Status Pill
                val statusColor = when {
                    isFull -> AccentOrange
                    isEmpty -> TextMuted
                    else -> PrimaryCyan
                }
                val statusLabel = when {
                    isFull -> "FULL"
                    isEmpty -> "EMPTY"
                    else -> "$occupiedCount / $capacity OCCUPIED"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(statusColor.copy(alpha = 0.16f))
                        .border(AlgoTokens.strokeThin, statusColor.copy(alpha = 0.55f), RoundedCornerShape(AlgoTokens.radiusXxs))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.microSize
                    )
                }
            }

            // Radial Ring Buffer Arena
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = AlgoTokens.space2),
                contentAlignment = Alignment.Center
            ) {
                val availableWidth = maxWidth.value
                val availableHeight = maxHeight.value
                val minDim = minOf(availableWidth, availableHeight)
                val ringRadius = (minDim * 0.27f).coerceIn(68f, 82f).dp

                // Background ring tracks & needle indicators
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val rPx = ringRadius.toPx()

                    // Outer orbit guide line
                    drawCircle(
                        color = BorderSubtle.copy(alpha = 0.45f),
                        radius = rPx,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    )

                    // Draw FRONT pointer needle
                    if (frontIdx in 0 until capacity && !isEmpty) {
                        val angle = (-PI / 2f + frontIdx * (2 * PI / capacity)).toFloat()
                        val needleStart = Offset(
                            center.x + (rPx * 0.32f) * cos(angle),
                            center.y + (rPx * 0.32f) * sin(angle)
                        )
                        val needleEnd = Offset(
                            center.x + (rPx * 0.72f) * cos(angle),
                            center.y + (rPx * 0.72f) * sin(angle)
                        )
                        drawLine(
                            color = PrimaryCyan.copy(alpha = 0.85f),
                            start = needleStart,
                            end = needleEnd,
                            strokeWidth = 3.dp.toPx()
                        )
                        drawCircle(
                            color = PrimaryCyan,
                            radius = 3.5.dp.toPx(),
                            center = needleEnd
                        )
                    }

                    // Draw REAR pointer needle
                    if (rearIdx in 0 until capacity && !isEmpty) {
                        val angle = (-PI / 2f + rearIdx * (2 * PI / capacity)).toFloat()
                        val needleStart = Offset(
                            center.x + (rPx * 0.32f) * cos(angle),
                            center.y + (rPx * 0.32f) * sin(angle)
                        )
                        val needleEnd = Offset(
                            center.x + (rPx * 0.72f) * cos(angle),
                            center.y + (rPx * 0.72f) * sin(angle)
                        )
                        drawLine(
                            color = AccentOrange.copy(alpha = 0.85f),
                            start = needleStart,
                            end = needleEnd,
                            strokeWidth = 3.dp.toPx()
                        )
                        drawCircle(
                            color = AccentOrange,
                            radius = 3.5.dp.toPx(),
                            center = needleEnd
                        )
                    }
                }

                // Center Instrumentation Hub (Clean, minimalist ring buffer gauge)
                Box(
                    modifier = Modifier
                        .size((ringRadius.value * 0.68f).coerceIn(56f, 78f).dp)
                        .clip(CircleShape)
                        .background(DarkBackground.copy(alpha = 0.94f))
                        .border(AlgoTokens.strokeThin, BorderMedium, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "QUEUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 7.5.sp,
                            letterSpacing = AlgoType.trackTight
                        )
                        Text(
                            text = "$occupiedCount / $capacity",
                            style = MaterialTheme.typography.titleMedium,
                            color = when {
                                isFull -> AccentOrange
                                isEmpty -> TextMuted
                                else -> PrimaryCyan
                            },
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = when {
                                isFull -> "[FULL]"
                                isEmpty -> "[EMPTY]"
                                else -> "SLOTS"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                isFull -> AccentOrange
                                isEmpty -> TextMuted
                                else -> PrimaryCyan.copy(alpha = 0.75f)
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 8 Radial Slots & Directional Orbit Pointer Badges
                for (i in 0 until capacity) {
                    val angle = -PI / 2f + i * (2 * PI / capacity)
                    val item = bufferItems.getOrNull(i)
                    val isFront = i == frontIdx && !isEmpty
                    val isRear = i == rearIdx && !isEmpty

                    key("slot_$i") {
                        CircularSlotItem(
                            slotIdx = i,
                            item = item,
                            angle = angle,
                            ringRadius = ringRadius,
                            frontIdx = frontIdx,
                            rearIdx = rearIdx,
                            isEmpty = isEmpty,
                            peekPulse = peekPulseAnim.value,
                            isScrubbing = isScrubbing,
                            playbackSpeedMs = playbackSpeedMs,
                            phaseLabel = step.phaseLabel
                        )
                    }

                    if (isFront || isRear) {
                        key("ptr_$i") {
                            CircularPointerBadge(
                                slotIdx = i,
                                angle = angle,
                                ringRadius = ringRadius,
                                isFront = isFront,
                                isRear = isRear,
                                peekPulse = peekPulseAnim.value,
                                isFound = item?.state == ElementState.FOUND || step.phaseLabel == "PEEK"
                            )
                        }
                    }
                }
            }

            // Bottom Pointer Legend Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryCyan))
                        Text(
                            text = "FRONT: ${if (frontIdx >= 0) "[$frontIdx]" else "NONE"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentOrange))
                        Text(
                            text = "REAR: ${if (rearIdx >= 0) "[$rearIdx]" else "NONE"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentOrange,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "SIZE: $occupiedCount / $capacity",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            // Interactive Stage Controls (Enqueue, Dequeue, Peek)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AlgoTokens.space2),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enqueue Button
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (!isFull) AccentOrange.copy(alpha = 0.22f) else DarkBackground)
                        .border(
                            AlgoTokens.strokeThin,
                            if (!isFull) AccentOrange.copy(alpha = 0.7f) else BorderSubtle,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .clickable(enabled = !isFull && onQueueOp != null) {
                            onQueueOp?.invoke(QueueOp.Enqueue(nextInputValue))
                        }
                        .pressPhysics()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (!isFull) "ENQUEUE($nextInputValue)" else "QUEUE FULL",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!isFull) AccentOrange else TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Dequeue Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (!isEmpty) PrimaryCyan.copy(alpha = 0.22f) else DarkBackground)
                        .border(
                            AlgoTokens.strokeThin,
                            if (!isEmpty) PrimaryCyan.copy(alpha = 0.7f) else BorderSubtle,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .clickable(enabled = !isEmpty && onQueueOp != null) {
                            onQueueOp?.invoke(QueueOp.Dequeue)
                        }
                        .pressPhysics()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DEQUEUE()",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!isEmpty) PrimaryCyan else TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Peek Button with green pulse
                val peekPulse = peekPulseAnim.value
                val isPulsing = peekPulse > 0.05f
                val peekBg = if (isPulsing) {
                    AccentGreen.copy(alpha = 0.22f + 0.35f * peekPulse)
                } else if (!isEmpty) {
                    AccentGreen.copy(alpha = 0.20f)
                } else {
                    DarkBackground
                }
                val peekBorder = if (isPulsing) {
                    AccentGreen
                } else if (!isEmpty) {
                    AccentGreen.copy(alpha = 0.7f)
                } else {
                    BorderSubtle
                }

                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(peekBg)
                        .border(
                            if (isPulsing) 1.5.dp else AlgoTokens.strokeThin,
                            peekBorder,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .clickable(enabled = !isEmpty && onQueueOp != null) {
                            peekPulseTrigger++
                            onQueueOp?.invoke(QueueOp.Peek)
                        }
                        .pressPhysics()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PEEK()",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!isEmpty || isPulsing) AccentGreen else TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Animated Radial Slot Item for Circular Queue.
 * Dequeuing items smoothly drift outward, scale down, and fade alpha.
 * Peeking items scale bounce and glow with AccentGreen.
 */
@Composable
private fun CircularSlotItem(
    slotIdx: Int,
    item: BufferItem?,
    angle: Double,
    ringRadius: Dp,
    frontIdx: Int,
    rearIdx: Int,
    isEmpty: Boolean,
    peekPulse: Float,
    isScrubbing: Boolean,
    playbackSpeedMs: Long,
    phaseLabel: String
) {
    val value = item?.value ?: "—"
    val isOccupied = value != "—" && value.isNotBlank()
    val isFront = slotIdx == frontIdx && !isEmpty
    val isRear = slotIdx == rearIdx && !isEmpty
    val isSlotActive = item?.state == ElementState.ACTIVE
    val isSlotSwapping = item?.state == ElementState.SWAPPING || (phaseLabel == "DEQUEUING" && isFront)
    val isSlotFound = item?.state == ElementState.FOUND || (phaseLabel == "PEEK" && isFront)
    val isPeekingFront = isFront && (peekPulse > 0.01f || isSlotFound)

    val driftAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(1f) }
    val scaleAnim = remember { Animatable(1f) }

    val exitSpring = remember(playbackSpeedMs) {
        val stiffness = when (playbackSpeedMs) {
            300L -> Spring.StiffnessHigh
            1000L -> Spring.StiffnessLow
            else -> Spring.StiffnessMedium
        }
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness)
    }

    LaunchedEffect(isSlotSwapping, isScrubbing) {
        if (isScrubbing) {
            driftAnim.snapTo(0f)
            alphaAnim.snapTo(1f)
            scaleAnim.snapTo(1f)
            return@LaunchedEffect
        }
        if (isSlotSwapping) {
            launch { scaleAnim.animateTo(0.72f, exitSpring) }
            launch { alphaAnim.animateTo(0f, exitSpring) }
            driftAnim.animateTo(22f, exitSpring)
        } else {
            driftAnim.snapTo(0f)
            alphaAnim.snapTo(1f)
            scaleAnim.snapTo(1f)
        }
    }

    val effectiveRadius = ringRadius.value + driftAnim.value
    val offsetX = effectiveRadius * cos(angle).toFloat()
    val offsetY = effectiveRadius * sin(angle).toFloat()

    val borderColor = when {
        isSlotSwapping -> AccentRed
        isPeekingFront -> AccentGreen
        isSlotActive -> AccentGreen
        isFront && isRear -> AccentYellow
        isFront -> PrimaryCyan
        isRear -> AccentOrange
        isOccupied -> BorderMedium
        else -> BorderSubtle.copy(alpha = 0.35f)
    }

    val peekScale = if (isPeekingFront) 1f + 0.12f * peekPulse else 1f

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.dp.roundToPx(), offsetY.dp.roundToPx()) }
            .graphicsLayer {
                this.alpha = alphaAnim.value
                this.scaleX = scaleAnim.value * peekScale
                this.scaleY = scaleAnim.value * peekScale
            }
            .size(42.dp)
            .drawBehind {
                val corner = CornerRadius(8.dp.toPx())
                if (isPeekingFront) {
                    drawCellGlow(AccentGreen, intensity = 0.90f + 0.35f * peekPulse, cornerRadius = corner)
                } else if (isSlotActive) {
                    drawCellGlow(PrimaryCyan, intensity = 0.80f, cornerRadius = corner)
                } else if (isSlotSwapping) {
                    drawCellGlow(AccentRed, intensity = 0.85f, cornerRadius = corner)
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isPeekingFront -> GreenSubtle
                    isOccupied -> CardBackgroundElevated
                    else -> DarkBackground.copy(alpha = 0.6f)
                }
            )
            .border(
                width = if (isFront || isRear || isSlotActive || isSlotSwapping || isPeekingFront) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = when {
                    isSlotSwapping -> AccentRed
                    isPeekingFront -> AccentGreen
                    isSlotActive -> PrimaryCyan
                    isOccupied -> TextPrimary
                    else -> TextMuted
                },
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
            Text(
                text = "[$slotIdx]",
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    isPeekingFront -> AccentGreen
                    isFront && isRear -> AccentYellow
                    isFront -> PrimaryCyan
                    isRear -> AccentOrange
                    else -> TextMuted
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )
        }
    }
}

/**
 * Directional pointer badge placed adjacent to the radial slot around the ring.
 * Uses arrow glyphs pointing directly towards the cell:
 * - Slot 0 (Top): ▼ FRONT
 * - Slot 1 (Top-Right): ↙ FRONT
 * - Slot 2 (Right): ◀ FRONT
 * - Slot 3 (Bottom-Right): ↖ FRONT
 * - Slot 4 (Bottom): ▲ FRONT
 * - Slot 5 (Bottom-Left): FRONT ↗
 * - Slot 6 (Left): FRONT ▶
 * - Slot 7 (Top-Left): FRONT ↘
 */
@Composable
private fun CircularPointerBadge(
    slotIdx: Int,
    angle: Double,
    ringRadius: Dp,
    isFront: Boolean,
    isRear: Boolean,
    peekPulse: Float,
    isFound: Boolean
) {
    val badgeInfo = getPointerBadgeInfo(slotIdx, isFront, isRear) ?: return
    val (badgeText, badgeBaseColor) = badgeInfo
    val isPeekingFront = isFront && (peekPulse > 0.01f || isFound)
    val badgeColor = if (isPeekingFront) AccentGreen else badgeBaseColor

    val badgeRadius = calculatePointerRadius(slotIdx, ringRadius)
    val badgeX = badgeRadius.value * cos(angle).toFloat()
    val badgeY = badgeRadius.value * sin(angle).toFloat()

    val badgeScale = if (isPeekingFront) 1f + 0.08f * peekPulse else 1f

    Box(
        modifier = Modifier
            .offset { IntOffset(badgeX.dp.roundToPx(), badgeY.dp.roundToPx()) }
            .graphicsLayer {
                this.scaleX = badgeScale
                this.scaleY = badgeScale
            }
            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
            .background(badgeColor.copy(alpha = if (isPeekingFront) 0.32f else 0.18f))
            .border(
                if (isPeekingFront) 1.5.dp else AlgoTokens.strokeThin,
                badgeColor.copy(alpha = if (isPeekingFront) 1f else 0.85f),
                RoundedCornerShape(AlgoTokens.radiusXxs)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = badgeText,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Calculates radial clearance from the center to ensure the pointer badge
 * never clips into the 42dp slot cell at any orientation (top/bottom, lateral, diagonal).
 */
private fun calculatePointerRadius(slotIdx: Int, ringRadius: Dp): Dp {
    val radialOffset = when (slotIdx) {
        0, 4 -> 36.dp
        2, 6 -> 55.dp
        else -> 63.dp
    }
    return ringRadius + radialOffset
}

/**
 * Returns the directional label and arrow for the pointer badge at slot [slotIdx].
 */
private fun getPointerBadgeInfo(
    slotIdx: Int,
    isFront: Boolean,
    isRear: Boolean
): Pair<String, Color>? {
    if (!isFront && !isRear) return null
    val label = when {
        isFront && isRear -> "F • R"
        isFront -> "FRONT"
        else -> "REAR"
    }
    val color = when {
        isFront && isRear -> AccentYellow
        isFront -> PrimaryCyan
        else -> AccentOrange
    }
    val textWithArrow = when (slotIdx) {
        0 -> "▼ $label"
        1 -> "↙ $label"
        2 -> "◀ $label"
        3 -> "↖ $label"
        4 -> "▲ $label"
        5 -> "$label ↗"
        6 -> "$label ▶"
        7 -> "$label ↘"
        else -> label
    }
    return Pair(textWithArrow, color)
}
