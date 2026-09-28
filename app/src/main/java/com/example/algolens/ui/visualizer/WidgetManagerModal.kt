package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Modal dialog for configuring workspace deck instruments.
 * Enables/disables widgets and reorders them in the deck horizontal carousel.
 */
@Composable
fun WidgetManagerModal(
    state: VisualizerScreenState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CardBackgroundElevated)
                .border(
                    AlgoTokens.strokeThin,
                    BorderSubtle,
                    RoundedCornerShape(AlgoTokens.radiusMd)
                )
                .padding(AlgoTokens.space5)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                        Text(
                            text = "Workspace Instruments",
                            style = MaterialTheme.typography.titleSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Toggle and reorder deck widgets",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize
                        )
                    }

                    // Close button
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.iconButtonSm)
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(CanvasBackground)
                            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Close,
                            contentDescription = "Close widget manager",
                            tint = TextMuted,
                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                        )
                    }
                }

                // Widget List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    for (type in WidgetType.entries) {
                        val isEnabled = state.enabledWidgets.contains(type)
                        val orderIndex = state.enabledWidgets.indexOf(type)
                        val canMoveUp = isEnabled && orderIndex > 0
                        val canMoveDown = isEnabled && orderIndex in 0 until (state.enabledWidgets.size - 1)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CanvasBackground)
                                .border(
                                    AlgoTokens.strokeThin,
                                    if (isEnabled) PrimaryCyan.copy(alpha = 0.35f) else BorderSubtle,
                                    RoundedCornerShape(AlgoTokens.radiusSm)
                                )
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            // Widget Info
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                                ) {
                                    Text(
                                        text = type.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isEnabled) TextPrimary else TextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isEnabled) {
                                        Text(
                                            text = "#${orderIndex + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryCyan,
                                            fontSize = AlgoType.microSize,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Text(
                                    text = type.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextDark,
                                    fontSize = AlgoType.microSize
                                )
                            }

                            // Reorder buttons (only if enabled)
                            if (isEnabled) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Move Up
                                    Box(
                                        modifier = Modifier
                                            .size(AlgoTokens.iconButtonXs)
                                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                            .background(DarkBackground)
                                            .border(
                                                AlgoTokens.strokeThin,
                                                if (canMoveUp) BorderSubtle else BorderSubtle.copy(alpha = 0.3f),
                                                RoundedCornerShape(AlgoTokens.radiusXs)
                                            )
                                            .clickable(enabled = canMoveUp) {
                                                state.moveWidget(orderIndex, orderIndex - 1)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = AlgoGlyphs.ArrowUp,
                                            contentDescription = "Move widget up",
                                            tint = if (canMoveUp) PrimaryCyan else TextMuted.copy(alpha = 0.3f),
                                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                        )
                                    }

                                    // Move Down
                                    Box(
                                        modifier = Modifier
                                            .size(AlgoTokens.iconButtonXs)
                                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                            .background(DarkBackground)
                                            .border(
                                                AlgoTokens.strokeThin,
                                                if (canMoveDown) BorderSubtle else BorderSubtle.copy(alpha = 0.3f),
                                                RoundedCornerShape(AlgoTokens.radiusXs)
                                            )
                                            .clickable(enabled = canMoveDown) {
                                                state.moveWidget(orderIndex, orderIndex + 1)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = AlgoGlyphs.ArrowDown,
                                            contentDescription = "Move widget down",
                                            tint = if (canMoveDown) PrimaryCyan else TextMuted.copy(alpha = 0.3f),
                                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                        )
                                    }
                                }
                            }

                            // Toggle Pill
                            val toggleEnabled = isEnabled || state.enabledWidgets.size > 1
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(if (isEnabled) GreenSubtle else DarkBackground)
                                    .border(
                                        AlgoTokens.strokeThin,
                                        if (isEnabled) AccentGreen.copy(alpha = 0.5f) else BorderSubtle,
                                        RoundedCornerShape(AlgoTokens.radiusXs)
                                    )
                                    .semantics {
                                        this.role = Role.Switch
                                        this.contentDescription = "Toggle ${type.title}"
                                    }
                                    .clickable(enabled = toggleEnabled) {
                                        state.toggleWidget(type)
                                    }
                                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isEnabled) "Active" else "Hidden",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isEnabled) AccentGreen else TextMuted,
                                    fontSize = AlgoType.microSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(PrimaryCyan)
                        .clickable { onDismiss() }
                        .padding(vertical = AlgoTokens.space3),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
