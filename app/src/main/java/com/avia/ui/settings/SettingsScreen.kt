package com.avia.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avia.data.AppSettings
import com.avia.ui.audio.AlgorithmAudioEngine
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.CustomSwitch
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.components.SectionLabel
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary

/**
 * General Settings Screen.
 *
 * Dedicated to non-categorized / general workspace configurations:
 * - Real-time algorithm step audio synthesis toggle
 * - Replay onboarding walkthrough
 * - Application version & runtime environment info
 *
 * Specific category options reside in their respective Profile sub-settings:
 * - Study Preferences: Trace language, playback speed, cell scaling, deck auto-open, complexity pills
 * - Accessibility: High-contrast outlines, tactile haptic feedback
 * - Data & Storage: Bookmark management, clear temporary buffers, reset all saved data
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onReplayOnboarding: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Header ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                com.avia.ui.components.CompactIconButton(
                    icon = AlgoGlyphs.Back,
                    contentDescription = "Back",
                    onClick = onBack,
                    container = CardBackground
                )

                Text(
                    text = "General Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(AlgoTokens.iconButtonLg)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanSubtle)
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Tune,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ── 1. Audio Feedback ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Spark, text = "Audio Feedback")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = AlgoTokens.space3)) {
                    Text(
                        text = "Algorithm Sound Effects",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Synthesize real-time audio tones on element comparisons, swaps, and completion sweeps",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(
                    checked = AppSettings.soundEnabled,
                    onCheckedChange = { AppSettings.soundEnabled = it }
                )
            }

            if (AppSettings.soundEnabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AlgoTokens.space2),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sound Volume",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "${(AppSettings.soundVolume * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = AppSettings.soundVolume,
                        onValueChange = { AppSettings.soundVolume = it },
                        onValueChangeFinished = { AlgorithmAudioEngine.playCompare(60, 100) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryCyan,
                            activeTrackColor = PrimaryCyan,
                            inactiveTrackColor = CardBackgroundElevated
                        )
                    )
                }
            }

            Text(
                text = "Offline PCM audio synthesis running on device hardware. Zero audio asset downloads or network traffic.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = AlgoType.microSize
            )
        }

        // ── 2. Guidance & Workspace Tour ──
        if (onReplayOnboarding != null) {
            SettingsCard {
                SectionLabel(icon = AlgoGlyphs.Spark, text = "Workspace Tour")
                val tourShape = RoundedCornerShape(AlgoTokens.radiusSm)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                        .pressPhysics(shape = tourShape, accent = PrimaryCyan)
                        .clip(tourShape)
                        .background(CyanSubtle)
                        .border(
                            AlgoTokens.strokeThin,
                            PrimaryCyan.copy(alpha = 0.35f),
                            tourShape
                        )
                        .clickable { onReplayOnboarding() }
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Spark,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(AlgoTokens.inlineIconMd)
                        )
                        Text(
                            text = "Replay Onboarding Tour",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "3 Slides",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
            }
        }

        // ── 3. Application Info ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Info, text = "Application Info")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackgroundElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(AlgoTokens.space3),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Version", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = AlgoType.microSize)
                    Text("AlgoLens v2.4.1", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = AlgoType.microSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Build", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = AlgoType.microSize)
                    Text("Build 204", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = AlgoType.microSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Architecture", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = AlgoType.microSize)
                    Text("100% Offline Runtime", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("License", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = AlgoType.microSize)
                    Text("MIT Open Source", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontSize = AlgoType.microSize)
                }
            }
        }

        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AlgoLens · Visual Algorithm Engineering",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = AlgoType.microSize
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space5)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4),
            content = content
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun SettingsScreenPreview() {
    com.avia.ui.theme.AlgoLensTheme {
        SettingsScreen(onBack = {})
    }
}
