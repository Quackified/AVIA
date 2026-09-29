package com.avia.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avia.model.BstMode
import com.avia.model.QueueVariant
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentOrange
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary

/**
 * Contextual mode selector for Binary Search Tree:
 * [ 🔍 Search | 🌲 In-Order | 🌿 Pre-Order | 🍂 Post-Order ]
 * Occupies the reclaimed under-header slot (formerly StageLegend).
 */
@Composable
fun BstModeSelector(
    currentMode: BstMode,
    onSelectMode: (BstMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(AlgoTokens.surfaceSunken.copy(alpha = 0.85f))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BstMode.values().forEach { mode ->
            val isSelected = currentMode == mode
            val animatedBg by animateColorAsState(
                targetValue = if (isSelected) PrimaryCyan.copy(alpha = 0.22f) else Color.Transparent,
                animationSpec = tween(200),
                label = "bstModeBg"
            )
            val animatedBorder by animateColorAsState(
                targetValue = if (isSelected) PrimaryCyan.copy(alpha = 0.75f) else Color.Transparent,
                animationSpec = tween(200),
                label = "bstModeBorder"
            )
            val animatedText by animateColorAsState(
                targetValue = if (isSelected) PrimaryCyan else TextSecondary,
                animationSpec = tween(200),
                label = "bstModeText"
            )

            val icon = when (mode) {
                BstMode.SEARCH -> "🔍"
                BstMode.IN_ORDER -> "🌲"
                BstMode.PRE_ORDER -> "🌿"
                BstMode.POST_ORDER -> "🍂"
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(animatedBg)
                    .border(AlgoTokens.strokeThin, animatedBorder, RoundedCornerShape(AlgoTokens.radiusXxs))
                    .clickable { onSelectMode(mode) }
                    .pressPhysics()
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = animatedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

/**
 * Contextual architecture variant selector for Queue:
 * [ ━ Linear FIFO | ⭕ Circular Ring Buffer ]
 * Occupies the reclaimed under-header slot (formerly StageLegend).
 */
@Composable
fun QueueModeSelector(
    currentVariant: QueueVariant,
    onSelectVariant: (QueueVariant) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(AlgoTokens.surfaceSunken.copy(alpha = 0.85f))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QueueVariant.values().forEach { variant ->
            val isSelected = currentVariant == variant
            val accentColor = if (variant == QueueVariant.CIRCULAR_RING) AccentOrange else PrimaryCyan
            val animatedBg by animateColorAsState(
                targetValue = if (isSelected) accentColor.copy(alpha = 0.22f) else Color.Transparent,
                animationSpec = tween(200),
                label = "queueVariantBg"
            )
            val animatedBorder by animateColorAsState(
                targetValue = if (isSelected) accentColor.copy(alpha = 0.75f) else Color.Transparent,
                animationSpec = tween(200),
                label = "queueVariantBorder"
            )
            val animatedText by animateColorAsState(
                targetValue = if (isSelected) accentColor else TextSecondary,
                animationSpec = tween(200),
                label = "queueVariantText"
            )

            val icon = when (variant) {
                QueueVariant.LINEAR_FIFO -> "━"
                QueueVariant.CIRCULAR_RING -> "⭕"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(animatedBg)
                    .border(AlgoTokens.strokeThin, animatedBorder, RoundedCornerShape(AlgoTokens.radiusXxs))
                    .clickable { onSelectVariant(variant) }
                    .pressPhysics()
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 11.sp, color = animatedText)
                    Text(
                        text = variant.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = animatedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
