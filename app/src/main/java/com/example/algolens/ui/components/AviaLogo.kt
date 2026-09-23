package com.example.algolens.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.algolens.R
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.SecondaryPurple

/**
 * AVIA brand mark component rendered from `app/src/main/logo.svg` (`@drawable/ic_avia_logo`).
 * Renders the clean vector glyph without any circular/rounded background fill.
 */
@Composable
fun AviaLogo(
    modifier: Modifier = Modifier,
    size: Dp = AlgoTokens.bootMarkSize,
    tint: Color = Color(0xFF31E1ED),
    secondaryTint: Color = SecondaryPurple,
    enableGlow: Boolean = false,
    pulseSpeed: Int = 2400
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_avia_logo),
            contentDescription = "AVIA Logo",
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier.fillMaxSize()
        )
    }
}
