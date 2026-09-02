package com.example.algolens.ui.boot

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextSecondary

/**
 * In-Compose boot overlay: the dark workspace background, a 1dp `PrimaryCyan`
 * progress bar along the top edge, and the `ALGOLENS` wordmark in JetBrains
 * Mono underneath the same geometric boot mark used by the OS splash.
 *
 * Animation:
 *  - Progress bar width animates `0f → 1f` over [holdDurationMs] using a linear
 *    tick (this is a progress indicator, not a "decorative" transition — see
 *    UI_GUIDELINES.md §14 exception for status indicators).
 *  - Overall overlay alpha fades from 1f → 0f once `controller.ready == true`
 *    using [AlgoTokens.panelSpring] (600ms ceiling), so the dashboard mounts
 *    into the same workspace the splash revealed.
 *
 * Tokens only: `PrimaryCyan`, `DarkBackground`, `TextSecondary`, `AlgoTokens`.
 * No raw `Color(0xFF…)`. No linear decorative tweens.
 */
@Composable
fun BootOverlay(
    controller: BootController,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = if (controller.ready) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = controller.holdDurationMs.toInt(),
            easing = androidx.compose.animation.core.LinearEasing,
        ),
        label = "BootProgress",
    )

    val alpha by animateFloatAsState(
        targetValue = if (controller.ready) 0f else 1f,
        animationSpec = AlgoTokens.panelFadeSpring,
        label = "BootFadeOut",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space6),
        ) {
            BootMarkCanvas(
                modifier = Modifier.size(AlgoTokens.bootMarkSize),
                tint = PrimaryCyan,
            )
            Text(
                text = "ALGOLENS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                ),
                color = TextSecondary,
            )
            Text(
                text = "loading workspace",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = TextSecondary.copy(alpha = 0.6f),
            )
        }

        // 1dp PrimaryCyan progress bar pinned to the top edge.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBackground),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(1.dp)
                    .background(PrimaryCyan),
            )
        }
    }
}

/**
 * Mirrors the OS-splash `ic_boot_mark` vector but drawn in Compose so the
 * in-Compose overlay matches the splash pixel-for-pixel without needing a
 * drawable lookup at runtime. Material "Bolt" glyph + small accent dot —
 * the same mark the dashboard uses for its logo.
 */
@Composable
private fun BootMarkCanvas(
    modifier: Modifier = Modifier,
    tint: Color,
) {
    Canvas(modifier = modifier) {
        val stroke = 1.5.dp.toPx()
        // The vector lives in a 108×108 viewport. Scale every coordinate
        // proportionally to the actual canvas so the bolt stays centred
        // inside whatever size the modifier requests.
        val unit = size.minDimension / 108f
        fun px(x: Float, y: Float) = Offset(x * unit, y * unit)

        // Bolt path — same coordinates as ic_boot_mark.xml, scaled to canvas.
        val bolt = androidx.compose.ui.graphics.Path().apply {
            moveTo(px(55.35f, 40.5f).x,  px(55.35f, 40.5f).y)  // top tip
            lineTo(px(41.85f, 56.7f).x,  px(41.85f, 56.7f).y)  // bottom-left tip
            lineTo(px(54f,    56.7f).x,  px(54f,    56.7f).y)  // lower notch
            lineTo(px(52.65f, 67.5f).x,  px(52.65f, 67.5f).y)  // bottom tip
            lineTo(px(66.15f, 51.3f).x,  px(66.15f, 51.3f).y)  // top-right tip
            lineTo(px(54f,    51.3f).x,  px(54f,    51.3f).y)  // upper notch
            close()
        }
        drawPath(
            path = bolt,
            color = tint,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // Accent dot — sits just outside the upper-right tip of the bolt.
        // 1dp radius at (68, 44) of the 108dp viewport, scaled to canvas.
        drawCircle(
            color = tint,
            radius = unit * 1f,
            center = px(68f, 44f),
        )
    }
}