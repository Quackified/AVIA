package com.example.algolens.ui.boot

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.components.AviaLogo
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.sin

/**
 * In-Compose boot screen ("AVIA Logo and Text Splash"):
 * Renders the AVIA logo inside a smooth orbital sweep halo, the brand wordmark,
 * and an algorithmic harmonic wave loader.
 *
 * Tap anywhere during the sequence to immediately fast-forward into the workspace.
 */
@Composable
fun BootOverlay(
    controller: BootController,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = if (controller.ready) 1f else 0f,
        animationSpec = tween(
            durationMillis = controller.holdDurationMs.toInt(),
            easing = LinearEasing,
        ),
        label = "BootProgress",
    )

    val alpha by animateFloatAsState(
        targetValue = if (controller.ready) 0f else 1f,
        animationSpec = AlgoTokens.panelFadeSpring,
        label = "BootFadeOut",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "BootLoadingMechanic")

    // Smooth 360-degree orbital rotation for the logo ring
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitAngle"
    )

    // Continuous phase for the algorithmic bar wave loader
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScale"
    )

    val statusText = when {
        progress < 0.4f -> "INITIALIZING INVARIANT CORE"
        progress < 0.8f -> "CALIBRATING ALGORITHMIC MODULES"
        else -> "WORKSPACE READY"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                controller.markReady()
            }
            .graphicsLayer { this.alpha = alpha }
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5),
            modifier = Modifier.padding(horizontal = AlgoTokens.space7)
        ) {
            // Hero AVIA Logo inside an orbital precision spinner ring
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokePx = 2.dp.toPx()
                    // Subtle static track ring
                    drawCircle(
                        color = PrimaryCyan.copy(alpha = 0.10f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    // Rotating sweep gradient arc
                    rotate(degrees = orbitAngle) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to Color.Transparent,
                                0.55f to SecondaryPurple.copy(alpha = 0.35f),
                                0.85f to PrimaryCyan.copy(alpha = 0.9f),
                                1.0f to PrimaryCyan
                            ),
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }
                }

                AviaLogo(
                    size = 48.dp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    }
                )
            }

            // Brand Wordmark
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AVIA",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackBrand,
                    ),
                    color = PrimaryCyan,
                )
                Spacer(modifier = Modifier.height(AlgoTokens.space1))
                Text(
                    text = "ALGORITHM VISUALIZER & INTELLIGENCE ASSISTANT",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackSection,
                    ),
                    color = TextMuted,
                )
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space3))

            // Algorithmic Bar Wave Loading Mechanic + Status Readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                AlgorithmicWaveLoader(phase = wavePhase)

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = AlgoType.trackSection,
                    ),
                    color = TextSecondary,
                )
            }
        }

        // Tap to skip hint at bottom edge
        Text(
            text = "TAP ANYWHERE TO SKIP",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = AlgoType.microSize,
                letterSpacing = AlgoType.trackSection,
            ),
            color = TextDark,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = AlgoTokens.space7)
        )
    }
}

/**
 * 5-bar staggered harmonic wave loader inspired by AVIA's sorting bar visualizer.
 */
@Composable
private fun AlgorithmicWaveLoader(
    phase: Float,
    modifier: Modifier = Modifier
) {
    val barCount = 5
    Row(
        modifier = modifier.height(22.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val offset = i * 0.65f
            val normalized = ((sin(phase - offset) + 1f) / 2f).coerceIn(0f, 1f)
            val barHeight = 6.dp + (14.dp * normalized)
            val barColor = if (normalized > 0.6f) {
                PrimaryCyan.copy(alpha = 0.55f + 0.45f * normalized)
            } else {
                SecondaryPurple.copy(alpha = 0.35f + 0.45f * normalized)
            }

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}