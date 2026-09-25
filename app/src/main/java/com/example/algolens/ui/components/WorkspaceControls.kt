package com.example.algolens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.AlgoType

/**
 * Compact circular icon-only control. Used throughout the visualizer
 * header and the bottom playback rail (back, play, step, challenge,
 * tutor, reset, etc.).
 *
 * Sized for *icon* use, not full accessibility. When the control is a
 * primary action (play / pause, scrub step) prefer
 * [AlgoTokens.iconButtonMd] (32dp). When it's a secondary
 * affordance (help, theory) prefer [AlgoTokens.iconButtonSm] (30dp).
 *
 * The component owns its own `disabledAlpha` (via [enabled]) so the
 * rail can drive locked-state from the [com.example.algolens.ui.visualizer.VisualizerScreenState]
 * without wrapping each button in a `Modifier.alpha(...)`.
 */
@Composable
fun RailIconButton(
    icon: ImageVector,
    contentDescription: String,
    boxSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    tint: Color,
    container: Color,
    borderColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .alpha(if (enabled) 1f else AlgoTokens.disabledAlpha)
            .size(boxSize)
            .clip(CircleShape)
            .background(container)
            .border(AlgoTokens.strokeThin, borderColor, CircleShape)
            .pressPhysics(shape = CircleShape, accent = borderColor, enabled = enabled)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Compact toolbar/header icon button that separates the 44dp accessible hit area
 * ([touchSize]) from the compact visible button geometry ([visualSize], default 32dp).
 *
 * Prevents oversized 44dp filled boxes in screen toolbars while preserving full
 * touch target compliance and TalkBack semantics.
 */
@Composable
fun CompactIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    visualSize: androidx.compose.ui.unit.Dp = AlgoTokens.iconButtonMd,
    touchSize: androidx.compose.ui.unit.Dp = AlgoTokens.Spacing.minTouchTarget,
    iconSize: androidx.compose.ui.unit.Dp = AlgoTokens.inlineIconMd,
    tint: Color = com.example.algolens.ui.theme.TextSecondary,
    container: Color = com.example.algolens.ui.theme.CardBackgroundElevated,
    borderColor: Color = BorderSubtle,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(AlgoTokens.radiusSm),
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else AlgoTokens.disabledAlpha)
            .size(touchSize)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(visualSize)
                .clip(shape)
                .background(container)
                .border(AlgoTokens.strokeThin, borderColor, shape)
                .pressPhysics(shape = shape, accent = borderColor, enabled = enabled),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * Inline pill combining a small label and an optional leading icon
 * (e.g. `TIME O(n log n)`, `SPACE O(1)`, `CUSTOMIZE`). Tokenized
 * surface; the *meaning* of the chip is set by [accent] and the
 * [borderColor] is derived from it for consistency.
 */
@Composable
fun IconPillButton(
    label: String,
    accent: Color,
    accentContainer: Color,
    borderColor: Color,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(accentContainer)
            .border(AlgoTokens.strokeThin, borderColor, RoundedCornerShape(AlgoTokens.radiusXs))
            .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusXs), accent = accent)
            .clickable(onClick = onClick)
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(AlgoTokens.inlineIconSm - 1.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Segmented two-or-more-option switch. Tokenized so it can be reused
 * for any 2+ state "view" toggles (cells vs bars, tree vs array for
 * heaps, directed vs undirected graphs, etc.).
 */
@Composable
fun SegmentedToggle(
    options: List<Pair<String, Any>>,
    selectedKey: Any,
    accent: Color = PrimaryCyan,
    onContainer: Color = DarkBackground,
    onSelect: (Any) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusSm - AlgoTokens.space1))
            .background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm - AlgoTokens.space1))
            .padding(AlgoTokens.space1),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        for ((label, key) in options) {
            val isSelected = key == selectedKey
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs - AlgoTokens.space1))
                    .background(if (isSelected) accent else Color.Transparent)
                    .pressPhysics(
                        shape = RoundedCornerShape(AlgoTokens.radiusXs - AlgoTokens.space1),
                        accent = accent,
                        enabled = !isSelected
                    )
                    .clickable { onSelect(key) }
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) onContainer else TextMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = AlgoType.microSize
                )
            }
        }
    }
}
