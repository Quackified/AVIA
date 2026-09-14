package com.example.algolens.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AlgoTokens
import kotlinx.coroutines.delay

/*
 * Instrument primitives (M4 + M5):
 *   DoubleBezelShell, InstrumentMeter, InstrumentRule, pressPhysics,
 *   entryCascade. Every premium surface in the app is built from these.
 */

/**
 * The double-bezel instrument frame: an outer shell with its own radius and
 * hairline, an inset gap, then a core plate inside. Nothing floats flat on the
 * background — every card is a plate in a tray.
 *
 * @param glow accent border for state (playing / challenge / selected).
 */
@Composable
fun DoubleBezelShell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(AlgoTokens.bezelRadius),
    shellColor: Color = AlgoTokens.surfaceSunken,
    shellBorder: Color = AlgoTokens.strokeBorderSubtle,
    coreColor: Color = AlgoTokens.surfaceCard,
    glow: Color? = null,
    contentPadding: PaddingValues = PaddingValues(AlgoTokens.space5),
    content: @Composable ColumnScope.() -> Unit
) {
    val innerShape = RoundedCornerShape(AlgoTokens.bezelInnerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(shellColor)
            .border(
                width = if (glow != null) AlgoTokens.strokeThin else AlgoTokens.strokeHairline,
                color = glow?.copy(alpha = 0.45f) ?: shellBorder,
                shape = shape
            )
            .padding(AlgoTokens.bezelGap)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(innerShape)
                .background(coreColor)
                .border(AlgoTokens.strokeHairline, AlgoTokens.strokeBorderSubtle, innerShape)
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Tick-based progress meter. Reads [progress] inside the draw lambda so the
 * transport advances it every frame without recomposing any text.
 */
@Composable
fun InstrumentMeter(
    progress: () -> Float,
    accent: Color,
    modifier: Modifier = Modifier,
    tickCount: Int = AlgoTokens.meterTickCount,
    height: Dp = AlgoTokens.space5
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val ticks = tickCount.coerceAtLeast(2)
        val pitch = size.width / ticks
        val filled = progress().coerceIn(0f, 1f) * ticks
        val activeWidth = (pitch * 0.42f).coerceAtLeast(1f)

        for (i in 0 until ticks) {
            val x = i * pitch + pitch / 2f
            drawLine(
                color = if (i < filled) accent else AlgoTokens.strokeBorderMedium,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = activeWidth
            )
        }
    }
}

/**
 * Ruler-ticked rule used along canvas headers and toolbar edges: a hairline
 * with 4dp minor ticks and a 20dp major tick every fifth mark.
 */
@Composable
fun InstrumentRule(
    modifier: Modifier = Modifier,
    accent: Color = AlgoTokens.accentCyan,
    tickColor: Color = AlgoTokens.strokeBorderMedium
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(AlgoTokens.space5)
    ) {
        val minorPx = AlgoTokens.tickMinor.toPx()
        val majorPx = AlgoTokens.tickMajor.toPx()
        val pitch = AlgoTokens.space4.toPx() * 2f
        val baseline = size.height

        drawLine(
            color = accent.copy(alpha = 0.22f),
            start = Offset(0f, baseline),
            end = Offset(size.width, baseline),
            strokeWidth = 1f
        )

        var i = 0
        var x = 0f
        while (x <= size.width) {
            val isMajor = i % 5 == 0
            drawLine(
                color = if (isMajor) tickColor.copy(alpha = 0.9f) else tickColor.copy(alpha = 0.45f),
                start = Offset(x, baseline),
                end = Offset(x, baseline - if (isMajor) majorPx else minorPx),
                strokeWidth = 1f
            )
            i++
            x += pitch
        }
    }
}

/**
 * M5 press contract: resting → pressed is a 4% scale-down plus an accent
 * border bloom (0.30 → 0.60 alpha), both on [AlgoTokens.pressSpring]. Append
 * **after** the background + clip in a modifier chain.
 */
/**
 * Tokenised 1dp hairline — the only separator allowed between regions or
 * menu groups. Reads as a ruled edge rather than a Material divider: no
 * baked-in insets, explicit token colour, never elevated, never padded.
 */
@Composable
fun AlgoHairline(
    modifier: Modifier = Modifier,
    color: Color = AlgoTokens.strokeBorderSubtle
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AlgoTokens.strokeThin)
            .background(color)
    )
}


@Composable
fun Modifier.pressPhysics(
    shape: Shape,
    accent: Color,
    enabled: Boolean = true
): Modifier {
    var isDown by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isDown && enabled) AlgoTokens.pressScale else 1f,
        animationSpec = AlgoTokens.pressSpring,
        label = "pressScale"
    )
    val bloom by animateFloatAsState(
        targetValue = if (isDown && enabled) AlgoTokens.pressBloomTo else AlgoTokens.pressBloomFrom,
        animationSpec = AlgoTokens.pressSpring,
        label = "pressBloom"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin.Center
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            awaitPointerEventScope {
                while (true) {
                    // Never consume: the sibling `clickable` further down the
                    // chain still owns the actual tap.
                    awaitFirstDown(requireUnconsumed = false)
                    isDown = true
                    waitForUpOrCancellation()
                    isDown = false
                }
            }
        }
        .border(
            border = BorderStroke(AlgoTokens.strokeThin, accent.copy(alpha = bloom)),
            shape = shape
        )
}

/**
 * M5 entry choreography: content never mounts statically. Each item rises
 * [AlgoTokens.entryRiseDistance] and fades in, staggered by [index] × 40ms.
 * Transform + opacity only — no layout-triggering properties.
 */
@Composable
fun Modifier.entryCascade(
    index: Int,
    enabled: Boolean = true
): Modifier {
    val rise = with(LocalDensity.current) { AlgoTokens.entryRiseDistance.toPx() }
    val progress = remember { Animatable(if (enabled) 0f else 1f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            delay((index.coerceAtLeast(0) * AlgoTokens.entryStaggerMs).toLong())
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = AlgoTokens.entryDurationMs,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    return this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * rise
    }
}
