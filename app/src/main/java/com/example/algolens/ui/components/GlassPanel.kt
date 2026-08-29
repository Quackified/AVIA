               package com.example.algolens.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple

/**
 * Continuous glassmorphic workspace background — the unified IDE canvas
 * container for the whole VisualizerScreen. Paints the primary #0B0F19
 * surface with faint ambient radial glows so every region (canvas, divider,
 * code trace) visually sits inside one cohesive workspace instead of
 * floating on isolated cards.
 */
@Composable
fun AlgoWorkspaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Base surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        )

        // Ambient glow field (soft cyan top-left, soft purple bottom-right)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AlgoTokens.accentCyan.copy(alpha = 0.07f),
                        Color.Transparent
                    ),
                    center = Offset(canvasWidth * 0.12f, canvasHeight * 0.08f),
                    radius = canvasWidth * 0.85f
                )
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SecondaryPurple.copy(alpha = 0.055f),
                        Color.Transparent
                    ),
                    center = Offset(canvasWidth * 0.92f, canvasHeight * 0.96f),
                    radius = canvasWidth * 0.9f
                )
            )

            // Faint horizon wash to separate mental "canvas" vs "trace" zones
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        AlgoTokens.canvasWell.copy(alpha = 0.55f)
                    ),
                    startY = canvasHeight * 0.45f,
                    endY = canvasHeight
                )
            )
        }

        content()
    }
}

/**
 * Reusable glassmorphic surface: translucent fill, hairline stroke and
 * tokenized radius/elevation. The building block for rails, prompts and
 * floating chips inside the workspace.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(AlgoTokens.radiusMd),
    containerColor: Color = AlgoTokens.glassFill.copy(alpha = 0.72f),
    borderColor: Color = AlgoTokens.strokeBorderMedium,
    borderWidth: Dp = AlgoTokens.strokeThin,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .border(borderWidth, borderColor, shape),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

/**
 * Seamless visual divider between the Algorithm Canvas and the Code Trace
 * pane. A thin ambient glow line in #00E5FF at 20% alpha that pulses gently
 * when a state transition occurs ([pulse] spikes 0→1 on each step change).
 */
@Composable
fun AmbientGlowDivider(
    pulseProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    // Both animated inputs are read inside the Canvas draw lambda, so the
    // breathing and pulse animations only redraw this 2dp line and never
    // recompose anything further up the tree.
    val breathingState = rememberInfiniteTransition(label = "dividerBreath").animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "dividerBreathAlpha"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        val intensity = (AlgoTokens.dividerGlowAlpha * breathingState.value) +
            (pulseProvider().coerceIn(0f, 1f) * (1f - AlgoTokens.dividerGlowAlpha))
        val y = size.height / 2f

        // Outer ambient halo
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Transparent,
                    AlgoTokens.accentCyan.copy(alpha = intensity * 0.35f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = size.height * 2.4f
        )

        // Core glow line
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(
                    AlgoTokens.accentCyan.copy(alpha = intensity * 0.45f),
                    AlgoTokens.accentCyan.copy(alpha = intensity),
                    AlgoTokens.accentCyan.copy(alpha = intensity * 0.45f)
                )
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = size.height * 0.9f
        )
    }
}