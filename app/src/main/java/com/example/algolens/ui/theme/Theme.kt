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
