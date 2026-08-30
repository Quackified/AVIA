package com.example.algolens.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = CanvasBackground,
    primaryContainer = CyanSubtle,
    onPrimaryContainer = PrimaryCyan,
    secondary = SecondaryPurple,
    onSecondary = Color.White,
    secondaryContainer = PurpleSubtle,
    onSecondaryContainer = PurpleGlow,
    tertiary = AccentGreen,
    onTertiary = CanvasBackground,
    tertiaryContainer = GreenSubtle,
    onTertiaryContainer = AccentGreen,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardBackgroundElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = CardBackground,
    surfaceContainerHigh = CardBackgroundElevated,
    surfaceContainerHighest = CardBackgroundHover,
    error = AccentRed,
    onError = Color.White,
    errorContainer = RedSubtle,
    onErrorContainer = AccentRed,
    outline = BorderMedium,
    outlineVariant = BorderSubtle
)

@Composable
fun AlgoLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // AlgoLens is designed as a specialized dark-tech aesthetic theme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CanvasBackground.toArgb()
                window.navigationBarColor = CardBackgroundElevated.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AlgoLensTypography,
        content = content
    )
}

/**
 * ============================================================================
 *  AlgoLens Design System Tokens — Unified IDE Workspace Language
 * ============================================================================
 *  Single source of truth for surfaces, functional accents, strokes, radii,
 *  elevation, blur and motion. Every visualizer surface, divider, sheet and
 *  highlight must derive from these tokens so the Canvas, Controls and Code
 *  Trace read as one continuous workspace rather than isolated cards.
 *
 *  Functional Accent Mapping (semantic, not decorative):
 *    - AccentCyan    (#00E5FF) → Active traversal / read pointers (i, j, scanning)
 *    - AccentPurple  (#8B5CF6) → Secondary tracking / auxiliary structures (key, target)
 *    - AccentPink    (#FF3366) → Comparisons, swaps and active mutations
 *    - AccentGreen   (#00E676) → Verified sorted states & correct challenge answers
 *    - AccentYellow  (#FFB800) → Pivot points, boundary thresholds & edge alerts
 * ============================================================================
 */
object AlgoTokens {

    // ── Workspace Surfaces ──
    /** Primary IDE workspace surface (#0B0F19). */
    val workspaceSurface: Color = DarkBackground
    /** Sunken canvas well beneath visualizer content. */
    val canvasWell: Color = CanvasBackground
    /** Glass panel fill (translucent card layer). */
    val glassFill: Color = CardBackground
    /** Elevated glass layer for floating rails / prompts. */
    val glassElevated: Color = CardBackgroundHover
    /** Uniform page-level padding of the workspace frame. */
    val framePadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 6.dp)

    // ── Functional Accents (semantic mapping) ──
    val accentCyan: Color = PrimaryCyan       // traversal / read pointers
    val accentPurple: Color = SecondaryPurple // secondary tracking / auxiliary
    val accentPink: Color = AccentPink        // comparisons / swaps / mutations
    val accentGreen: Color = AccentGreen      // verified sorted / correct answer
    val accentYellow: Color = AccentYellow    // pivot / threshold / edge alerts

    // Translucent fills paired with the accents above.
    val cyanFill: Color = CyanSubtle
    val purpleFill: Color = PurpleSubtle
    val pinkFill: Color = PinkSubtle
    val greenFill: Color = GreenSubtle
    val yellowFill: Color = YellowSubtle

    // ── Strokes ──
    val strokeHairline: Dp = 0.5.dp
    val strokeThin: Dp = 1.dp
    val strokeActive: Dp = 2.dp
    val strokeBorderSubtle: Color = BorderSubtle
    val strokeBorderMedium: Color = BorderMedium

    // ── Spacing (4dp grid) ──
    val space1: Dp = 2.dp
    val space2: Dp = 4.dp
    val space3: Dp = 6.dp
    val space4: Dp = 8.dp
    val space5: Dp = 12.dp
    val space6: Dp = 16.dp
    val space7: Dp = 24.dp
    val space8: Dp = 32.dp

    // ── Component Sizing ──
    /** Minimum recommended touch target for accessibility (WCAG-aligned). */
    val minTouchTarget: Dp = 44.dp
    /** Compact icon button used in rails / headers. */
    val iconButtonSm: Dp = 30.dp
    val iconButtonMd: Dp = 32.dp
    val iconButtonLg: Dp = 36.dp
    /** Inline icon next to a label (e.g. header / chip). */
    val inlineIconSm: Dp = 12.dp
    val inlineIconMd: Dp = 14.dp
    val inlineIconLg: Dp = 18.dp

    // ── Corner Radii ──
    val radiusXs: Dp = 4.dp
    val radiusSm: Dp = 8.dp
    val radiusMd: Dp = 12.dp
    val radiusLg: Dp = 16.dp
    val radiusXl: Dp = 24.dp

    // ── Elevation ──
    val elevationFlat: Dp = 0.dp
    val elevationRaised: Dp = 2.dp
    val elevationFloating: Dp = 8.dp
    val elevationTraveling: Dp = 18.dp   // cells mid "pop-up & shift" flight

    // ── Glow & Blur ──
    /** Ambient divider glow baseline alpha (#00E5FF @ 20%). */
    val dividerGlowAlpha: Float = 0.20f
    /** Backdrop blur radius for overlay sheets / side drawers. */
    val backdropBlur: Dp = 16.dp
    /** Cell glow halo radius multiplier used while mirroring code highlights. */
    val syncGlowRadius: Dp = 14.dp

    // ── Motion ──
    /** Uniform spring for all expanding / collapsing panels (never snappy). */
    val panelSpring = spring<IntSize>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Spring for the canvas cell "pop up then shift to slot" travel. */
    val cellTravelSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    /** Spring for pointer badge / chip hopping between slots. */
    val pointerSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Bouncy spring for cell "being evaluated" pops (1.1x–1.2x scale). */
    val evalSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Spring tracking the code trace's sliding active-line pill. */
    val lineTrackSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Lift phase of the swap arc: pop straight up off the slot. */
    val liftSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Settle phase of the swap arc: descend into the destination slot. */
    val settleSpring = spring<Float>(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMedium
    )

    /** Standard opacity of inactive/disabled workspace controls. */
    val disabledAlpha: Float = 0.38f
}

/**
 * Uniform expanding/collapsing behaviour for all workspace panels.
 * Applies [animateContentSize] with the shared low-stiffness spring so
 * resizing feels natural rather than snappy (per design system motion spec).
 */
fun Modifier.smoothPanelExpansion(): Modifier = composed {
    animateContentSize(animationSpec = AlgoTokens.panelSpring)
}
