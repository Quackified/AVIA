package com.example.algolens.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.algolens.R

// JetBrains Mono Monospace Font Family
val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_variable, FontWeight.Normal),
    Font(R.font.jetbrains_mono_variable, FontWeight.Medium),
    Font(R.font.jetbrains_mono_variable, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono_variable, FontWeight.Bold)
)

val AlgoLensTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.03).sp
    ),
    displayMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.03).sp
    ),
    displaySmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.03).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.01).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.01).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.01).sp
    ),
    titleLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.01).sp
    ),
    titleMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.01).sp
    ),
    titleSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.08.sp
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.08.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.08.sp
    )
)

/**
 * The instrument's five type steps as addressable styles, plus the raw sizes a
 * `Canvas` / `TextMeasurer` needs (a `drawText` call cannot inherit a slot).
 * Canvas internals address [microSize]; persistent text addresses a style.
 */
object AlgoType {
    /** 28sp — screen titles, hero numerals. */
    val display: TextStyle = AlgoLensTypography.displayLarge
    /** 16sp — region headers. */
    val title: TextStyle = AlgoLensTypography.headlineMedium
    /** 12sp — prose, complexity values. */
    val body: TextStyle = AlgoLensTypography.bodyLarge
    /** 10sp — section labels, pills, meta. */
    val label: TextStyle = AlgoLensTypography.labelMedium
    /** 9.5sp — canvas-internal numerals only. Never persistent chrome. */
    val micro: TextStyle = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 9.5.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp
    )

    // ── Raw sizes for Canvas / TextMeasurer call sites ──
    val displaySize: TextUnit = 28.sp
    val titleSize: TextUnit = 16.sp
    val bodySize: TextUnit = 12.sp
    val labelSize: TextUnit = 10.sp
    /** The persistent-text floor. Nothing renders below this. */
    val microSize: TextUnit = 9.5.sp

    /** Section-label tracking (0.08em) for tracking call sites. */
    val labelTracking: TextUnit = 0.08.sp
    /** Display tracking for large numerals / wordmarks. */
    val displayTracking: TextUnit = (-0.03).sp

    // ── Leading (lineHeight) scale ──
    val leadingMicroTight: TextUnit = 11.sp
    val leadingMicro: TextUnit = 12.sp
    val leadingMicroRelaxed: TextUnit = 13.sp
    val leadingLabel: TextUnit = 14.sp
    val leadingBodyTight: TextUnit = 15.sp
    val leadingBody: TextUnit = 16.sp
    val leadingBodyDefault: TextUnit = 17.sp
    val leadingBodyRelaxed: TextUnit = 18.sp
    val leadingTitle: TextUnit = 21.sp
    val leadingDisplay: TextUnit = 34.sp

    // ── Tracking (letterSpacing) scale ──
    val trackTight: TextUnit = 0.6.sp
    val trackSection: TextUnit = 0.8.sp
    val trackHeader: TextUnit = 1.sp
    val trackBrand: TextUnit = 4.sp
}

