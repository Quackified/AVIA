package com.avia.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.TextMuted

/**
 * Obsidian-style workspace divider and handle bar positioned between
 * the Canvas Visualizer and Deck Instruments.
 *
 * Tapping expands the canvas to full height (Focus Mode) by shifting the
 * widget deck off-screen, or restores the widget deck when in Focus Mode.
 */
@Composable
fun WorkspaceHandleBar(
    isFocusMode: Boolean,
    onToggleFocusMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val grabColor by animateColorAsState(
        targetValue = if (isFocusMode) PrimaryCyan else BorderSubtle,
        label = "grabColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AlgoTokens.iconButtonXs)
            .background(DarkBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onToggleFocusMode
            )
            .semantics {
                this.role = Role.Button
                this.contentDescription = if (isFocusMode) {
                    "Collapse focus mode, restore instrument deck"
                } else {
                    "Expand canvas, enter focus mode"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Divider hairline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlgoTokens.strokeHairline)
                .background(BorderSubtle.copy(alpha = 0.5f))
                .align(Alignment.Center)
        )

        // Centered grab pill + icon chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(CardBackground)
                .border(
                    AlgoTokens.strokeThin,
                    grabColor.copy(alpha = 0.6f),
                    RoundedCornerShape(percent = 50)
                )
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space1)
        ) {
            // Grab pill
            Box(
                modifier = Modifier
                    .width(AlgoTokens.space8)
                    .height(AlgoTokens.strokeActive)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(grabColor)
            )

            // Directional chevron
            Icon(
                imageVector = if (isFocusMode) AlgoGlyphs.ChevronUp else AlgoGlyphs.ChevronDown,
                contentDescription = null,
                tint = if (isFocusMode) PrimaryCyan else TextMuted,
                modifier = Modifier.size(AlgoTokens.inlineIconSm)
            )

            Text(
                text = if (isFocusMode) "Restore Deck" else "Focus Mode",
                style = MaterialTheme.typography.labelSmall,
                color = if (isFocusMode) PrimaryCyan else TextMuted,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
