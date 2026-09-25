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
    /** Mid-weight border for buffer/rail surfaces (between hairline and active). */
    val strokeMedium: Dp = 1.5.dp
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

    object Spacing {
        val minTouchTarget: Dp get() = AlgoTokens.minTouchTarget
    }
    /** Extra-small icon button (cell key-card glyphs, smaller than the 30dp rail icons). */
    val iconButtonXs: Dp = 26.dp
    /** Compact icon button used in rails / headers. */
    val iconButtonSm: Dp = 30.dp
    val iconButtonMd: Dp = 32.dp
    val iconButtonLg: Dp = 36.dp
    /** Inline icon next to a label (e.g. header / chip). */
    val inlineIconSm: Dp = 12.dp
    val inlineIconMd: Dp = 14.dp
    val inlineIconLg: Dp = 18.dp

    /** Cold-start boot mark canvas (used by `BootOverlay` to mirror the OS splash drawable). */
    val bootMarkSize: Dp = 32.dp
    /** Profile hero avatar container size. */
    val avatarHeroSize: Dp = 56.dp

    // ── Corner Radii ──
    /** Tightest radius for chips / small badges (between 4dp xs and 8dp sm). */
    val radiusXxs: Dp = 6.dp
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

    /**
     * Float-typed twin of [panelSpring], used wherever a panel-shaped motion
     * needs to drive a `Float` (e.g. fade alpha, progress tween) rather than
     * an `IntSize`. Same damping + stiffness profile — same 600ms ceiling.
     */
    val panelFadeSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Spring for the canvas cell "pop up then shift to slot" travel. */
    val cellTravelSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
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

    // ────────────────────────────────────────────────────────────────────────
    //  M2 — Elevation ladder (restored) + the one permitted texture
    // ────────────────────────────────────────────────────────────────────────
    /** Every screen's base layer. Never the well. */
    val surfaceBase: Color = DarkBackground
    /** Cards, list rows, prompt panels. */
    val surfaceCard: Color = CardBackground
    /** Floating rails, sheets, overlays, bottom nav. */
    val surfaceFloat: Color = CardBackgroundHover
    /** Pressed / inverted panels. */
    val surfaceSunken: Color = CardBackgroundElevated
    /** The visualizer canvas well — the ONLY place this surface may sit. */
    val surfaceWell: Color = CanvasBackground

    /** Static scanline texture: 3% opacity, 2dp period, drawn once. */
    val scanlineAlpha: Float = 0.03f
    val scanlinePeriod: Dp = 2.dp

    // ────────────────────────────────────────────────────────────────────────
    //  M4 — Double-bezel instrument frame + ruler ticks + meter
    // ────────────────────────────────────────────────────────────────────────
    /** Inset of the inner hairline from the outer shell edge. */
    val bezelInset: Dp = 1.dp
    /** Default bezel radius (the outer shell). */
    val bezelRadius: Dp = radiusMd
    /** Minor ruler tick length (4dp rhythm). */
    val tickMinor: Dp = 4.dp
    /** Major ruler tick length (every 20dp). */
    val tickMajor: Dp = 20.dp
    /** Ruler-tick / meter track alpha. */
    val tickAlpha: Float = 0.4f
    /** Single-meter-track height. */
    val meterHeight: Dp = 2.dp
    /** Tick pitch on a progress meter. */
    val meterTickCount: Int = 24

    // ────────────────────────────────────────────────────────────────────────
    //  M5 — Pressed physics + entry choreography
    // ────────────────────────────────────────────────────────────────────────
    /** Resting→pressed scale for every tappable. */
    val pressScale: Float = 0.96f
    /** Accent border bloom at rest. */
    val pressBloomFrom: Float = 0.3f
    /** Accent border bloom while pressed. */
    val pressBloomTo: Float = 0.6f
    /** Vertical offset of the first item in a list cascade. */
    val entryRiseDistance: Dp = 12.dp
    /** Per-item stagger of the one-shot entry cascade. */
    val entryStaggerMs: Int = 40
    /**
     * Ceiling on the entry stagger, in steps. A list entrance is read in its
     * first few rows; clamping here keeps a row deep in a scrolling list from
     * sitting blank through a delay nobody is watching.
     */
    val entryStaggerMax: Int = 5
    /** Duration of a single item's rise+fade. */
    val entryDurationMs: Int = 220
    /**
     * Ceiling on *which* rows are choreographed at all, by list position. Only
     * the rows a freshly mounted screen can actually show are allowed to enter;
     * every deeper row is composed by scrolling, and a scroll-composed row must
     * land at rest rather than fade up from zero under the user's thumb. Rows
     * past this budget take the no-op fast path permanently.
     */
    val entryMaxItems: Int = 6
    /**
     * Total length of the mount entry cascade: the last stagger step, one full
     * item entry, and a small margin. A row composed after this window has
     * closed appears at rest — see [entryMaxItems] for the position budget.
     *
     * Declared *after* the three values it derives from on purpose: this is an
     * `object`, so properties initialise in declaration order, and an up-front
     * `entryWindowMs` would have been computed from three zeroes.
     */
    val entryWindowMs: Int = entryStaggerMax * entryStaggerMs + entryDurationMs + 120
    /** Press response spring — medium stiffness, no bounce. */
    val pressSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /**
     * The full instrument frame used by [com.example.algolens.ui.components
     * .DoubleBezelShell]: outer 1dp shell radius, inner hairline radius and
     * the gap between them.
     */
    val bezelInnerRadius: Dp = radiusMd - bezelInset
    val bezelGap: Dp = 2.dp
}

/**
 * Uniform expanding/collapsing behaviour for all workspace panels.
 * Applies [animateContentSize] with the shared low-stiffness spring so
 * resizing feels natural rather than snappy (per design system motion spec).
 */
fun Modifier.smoothPanelExpansion(): Modifier = composed {
    animateContentSize(animationSpec = AlgoTokens.panelSpring)
}
