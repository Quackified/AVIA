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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
 * Progress meter supporting continuous rounded track rendering (default)
 * or legacy tick-based rendering when [tickCount] is provided.
 * Reads [progress] inside the draw lambda so the transport advances it every frame
 * without recomposing any text.
 */
@Composable
fun InstrumentMeter(
    progress: () -> Float,
    accent: Color,
    modifier: Modifier = Modifier,
    tickCount: Int? = null,
    trackColor: Color = Color(0xFF131D2E),
    height: Dp = 6.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val fraction = progress().coerceIn(0f, 1f)
        val cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)

        if (tickCount != null && tickCount > 0) {
            val ticks = tickCount.coerceAtLeast(2)
            val pitch = size.width / ticks
            val filled = fraction * ticks
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
        } else {
            // Draw continuous rounded track background
            drawRoundRect(
                color = trackColor,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = cornerRadius
            )
            // Draw filled progress capsule
            if (fraction > 0f) {
                val progressWidth = (size.width * fraction).coerceIn(size.height, size.width)
                drawRoundRect(
                    color = accent,
                    topLeft = Offset.Zero,
                    size = Size(progressWidth, size.height),
                    cornerRadius = cornerRadius
                )
            }
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
    shape: Shape = RoundedCornerShape(AlgoTokens.radiusSm),
    accent: Color = AlgoTokens.accentCyan,
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

    // At rest `bloom` is the constant `pressBloomFrom`, so this `remember`
    // short-circuits and a scrolling list allocates no stroke at all. Without
    // it, every row rebuilt a `Color.copy()` + `BorderStroke` on every
    // recomposition the list produced while moving.
    val stroke = remember(accent, bloom) {
        BorderStroke(AlgoTokens.strokeThin, accent.copy(alpha = bloom))
    }

    // Attach a render layer only while the press is actually displacing the
    // surface. `graphicsLayer` always allocates one, so applying it
    // unconditionally parked a permanent offscreen layer on every row of a
    // scrolling list — at rest, where it bought nothing and cost composition
    // time on every frame the list moved.
    val displaced = if (scale == 1f) this else this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        transformOrigin = TransformOrigin.Center
    }

    return displaced
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
        .border(border = stroke, shape = shape)
}

/**
 * Screen-scoped, one-shot entry cascade — the M5 choreography gate.
 *
 * Wrap a screen's content in [EntryCascadeProvider] and items that opt in via
 * [entryCascade] rise by [AlgoTokens.entryRiseDistance] and fade in once,
 * staggered by index. The window then closes after [AlgoTokens.entryWindowMs]
 * and from that moment [entryCascade] is a **no-op that hands back the modifier
 * unchanged** — no `Animatable`, no coroutine, no `graphicsLayer`, no per-item
 * offscreen render layer.
 *
 * **Why the gate exists.** The previous design decided *per item instance*
 * whether to animate. That meant every row a `LazyColumn` composed after the
 * initial mount — which is to say every row you scroll into — still started at
 * `alpha = 0f`, held its layout space while invisible, waited out its stagger,
 * then slid up. On a scrolling list that reads as content popping in and out and
 * shifting, and it kept a render layer alive for every row in the list for as
 * long as the list existed.
 *
 * A screen that never provides the gate silently takes the fast path, so
 * omitting the provider degrades to "items appear at rest" — never to "items
 * animate forever".
 */
@Stable
class EntryCascade internal constructor() {
    internal var isOpen by mutableStateOf(true)
        private set

    internal fun closeWindow() {
        isOpen = false
    }
}

private val LocalEntryCascade = staticCompositionLocalOf<EntryCascade?> { null }

/** Opens the one-shot entry window for [content]; see [EntryCascade]. */
@Composable
fun EntryCascadeProvider(content: @Composable () -> Unit) {
    val cascade = remember { EntryCascade() }
    LaunchedEffect(cascade) {
        delay(AlgoTokens.entryWindowMs.toLong())
        cascade.closeWindow()
    }
    CompositionLocalProvider(LocalEntryCascade provides cascade, content = content)
}

/**
 * Rise-and-fade entrance for row [index] of a freshly mounted screen. Returns
 * `this` untouched once the cascade window has closed.
 */
@Composable
fun Modifier.entryCascade(index: Int): Modifier {
    val cascade = LocalEntryCascade.current ?: return this
    if (!cascade.isOpen) return this
    // Position budget — the half of [AlgoTokens.entryMaxItems] that the stagger
    // clamp alone did not enforce. Clamping only the *delay* still left every
    // deeper row mounted at `alpha = 0f` for the length of the window, so a row
    // scrolled into the viewport inside those ~520ms faded up from nothing
    // under the user's thumb: content popping in and out, then shifting.
    // Rows past the budget now take the no-op fast path from the first frame.
    if (index >= AlgoTokens.entryMaxItems) return this
    return entryCascadeInWindow(index)
}

@Composable
private fun Modifier.entryCascadeInWindow(index: Int): Modifier {
    val rise = with(LocalDensity.current) { AlgoTokens.entryRiseDistance.toPx() }
    // Saveable so an item disposed and re-composed *inside* the short window
    // resumes at rest instead of restarting its entrance mid-flight.
    var hasEntered by rememberSaveable { mutableStateOf(false) }
    val progress = remember { Animatable(if (hasEntered) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (hasEntered) {
            if (progress.value != 1f) progress.snapTo(1f)
        } else {
            val steps = index.coerceIn(0, AlgoTokens.entryStaggerMax)
            if (steps > 0) delay((steps * AlgoTokens.entryStaggerMs).toLong())
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = AlgoTokens.entryDurationMs,
                    easing = LinearOutSlowInEasing
                )
            )
            hasEntered = true
        }
    }

    return this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * rise
    }
}
