package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.BadgeMuted

/**
 * Top-of-cell pointer pill (e.g. PIVOT, L, R, target, min, key).
 *
 * Owns the badge sizing, the labelâ†’colour mapping for the canonical sort
 * pointer names, and the scale+fade enter/exit animation. Wrapped in an
 * [AnimatedVisibility] keyed on the entry's nullness so it appears/disappears
 * smoothly as pointers land on and leave a cell.
 */
@Composable
fun TopPointerBadge(
    entry: Map.Entry<String, Int>?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(18.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = entry != null,
            enter = scaleIn(AlgoTokens.evalSpring) + fadeIn(tween(90)),
            exit = scaleOut(tween(110)) + fadeOut(tween(110))
        ) {
            if (entry != null) {
                val label = entry.key
                val isPivot = label.equals("pivot", ignoreCase = true)
                val isMin = label.equals("min", ignoreCase = true)
                val (badgeBg, badgeText) = when {
                    isPivot -> AlgoTokens.accentYellow to DarkBackground
                    isMin -> AlgoTokens.accentYellow to DarkBackground
                    label.equals("key", ignoreCase = true) -> SecondaryPurple to Color.White
                    label.equals("target", ignoreCase = true) -> SecondaryPurple to Color.White
                    else -> BadgeMuted to Color.White
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(badgeBg)
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Bottom-of-cell pointer pill (e.g. i, j, mid, low, high, k).
 *
 * A 18Ã—18dp square with a single letter and a labelâ†’colour mapping for the
 * canonical sort pointer names.
 */
@Composable
fun BottomPointerBadge(
    entry: Map.Entry<String, Int>?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(20.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = entry != null,
            enter = scaleIn(AlgoTokens.evalSpring) + fadeIn(tween(90)),
            exit = scaleOut(tween(110)) + fadeOut(tween(110))
        ) {
            if (entry != null) {
                val label = entry.key
                val (badgeBg, badgeText) = when (label.lowercase()) {
                    "i", "low", "l", "j", "mid" -> AlgoTokens.accentCyan to DarkBackground
                    "high", "r" -> AlgoTokens.accentPurple to Color.White
                    "k" -> AlgoTokens.accentPink to Color.White
                    else -> AlgoTokens.accentCyan to DarkBackground
                }

                Box(
                    modifier = Modifier
                        .size(width = 18.dp, height = 18.dp)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
