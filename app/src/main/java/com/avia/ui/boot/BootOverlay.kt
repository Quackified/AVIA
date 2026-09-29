package com.avia.ui.boot

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avia.ui.components.AviaLogo
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.PrimaryCyan

/**
 * In-Compose boot screen — single-focal minimalist composition.
 *
 * One centered AVIA mark settles in (fade + micro-scale over ~520ms) above
 * its wordmark, with a single hairline progress line sweeping across the
 * hold duration beneath. The previous iteration stacked three simultaneous
 * motion systems (orbital halo ring, logo scale pulse, 5-bar harmonic wave)
 * plus a status readout and a skip caption — busy for a sub-two-second
 * screen nobody watches twice.
 *
 * Tap anywhere during the sequence to fast-forward into the workspace. The
 * gesture stays intentionally unsigned: skip-by-habit needs no signage.
 */
@Composable
fun BootOverlay(
    controller: BootController,
    modifier: Modifier = Modifier,
) {
    // Hairline sweep: one continuous 0→1 pass across the hold window.
    val progress by animateFloatAsState(
        targetValue = if (controller.ready) 1f else 0f,
        animationSpec = tween(
            durationMillis = controller.holdDurationMs.toInt(),
            easing = LinearEasing,
        ),
        label = "BootProgress",
    )

    // Exit fade handled by the shared panel spring so the handoff into the
    // workspace matches every other panel transition in the app.
    val exitAlpha by animateFloatAsState(
        targetValue = if (controller.ready) 0f else 1f,
        animationSpec = AlgoTokens.panelFadeSpring,
        label = "BootFadeOut",
    )

    // Entry settle: flipped on the first frame; alpha + micro-scale follow it.
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { settled = true }
    val settle by animateFloatAsState(
        targetValue = if (settled) 1f else 0f,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "BootSettle",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .graphicsLayer { alpha = exitAlpha }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { controller.markReady() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.graphicsLayer {
                alpha = settle
                scaleX = 0.94f + 0.06f * settle
                scaleY = 0.94f + 0.06f * settle
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5),
        ) {
            AviaLogo()

            Text(
                text = "AVIA",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackBrand,
                ),
                color = PrimaryCyan,
            )

            // Hairline progress line — the only other element on screen.
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .background(BorderSubtle)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(2.dp)
                        .background(PrimaryCyan)
                )
            }
        }
    }
}
