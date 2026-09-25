package com.example.algolens.ui.settings

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Icon
import com.example.algolens.data.AppSettings
import com.example.algolens.ui.components.InstrumentMeter
import com.example.algolens.ui.theme.AlgoTokens
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.components.CustomSwitch
import com.example.algolens.ui.components.SectionLabel
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.CardBackgroundHover
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.sizeIn
import com.example.algolens.ui.components.pressPhysics

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onReplayOnboarding: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val speedSlider = AppSettings.speedSliderValue
    val highContrast = AppSettings.highContrastNodeOutlines
    val autoOpenDeck = AppSettings.autoOpenDeckOnPlay
    val showComplexity = AppSettings.showComplexityBadges
    var dataClearedBanner by remember { mutableStateOf(false) }

    val speedLabel = when {
        speedSlider < 34f -> "Slow"
        speedSlider < 67f -> "Normal"
        else -> "Fast"
    }
    val speedMs = "${AppSettings.defaultPlaybackSpeedMs}ms"
    val backShape = RoundedCornerShape(AlgoTokens.radiusSm)

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
                com.example.algolens.ui.components.CompactIconButton(
                    icon = AlgoGlyphs.Back,
                    contentDescription = "Back",
                    onClick = onBack,
                    container = CardBackground
                )

                Text(
                    text = "Settings",
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

        // ── 1. Default Trace Language ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Code, text = "Default Trace Language")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                com.example.algolens.data.TraceLanguage.entries.forEach { lang ->
                    val isSel = AppSettings.preferredLanguage == lang
                    val chipShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                            .pressPhysics(shape = chipShape, accent = PrimaryCyan)
                            .clip(chipShape)
                            .background(if (isSel) PrimaryCyan else CardBackgroundElevated)
                            .border(
                                1.dp,
                                if (isSel) PrimaryCyan else BorderSubtle,
                                chipShape
                            )
                            .clickable { AppSettings.preferredLanguage = lang }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lang.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) DarkBackground else TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = AlgoType.labelSize
                        )
                    }
                }
            }
            Text(
                text = "Code stack in the visualizer renders in ${AppSettings.preferredLanguage.label} syntax.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDark,
                fontSize = AlgoType.microSize,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ── 2. Default Playback Speed ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Speed, text = "Default Playback Speed")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = speedLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$speedMs / step",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize
                )
            }

            Slider(
                value = speedSlider,
                onValueChange = { AppSettings.speedSliderValue = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryCyan,
                    activeTrackColor = PrimaryCyan,
                    inactiveTrackColor = CardBackgroundElevated
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Slow (1000ms)", "Normal (600ms)", "Fast (300ms)").forEach {
                    Text(text = it, style = MaterialTheme.typography.labelSmall, color = TextNavy, fontSize = AlgoType.microSize)
                }
            }
        }

        // ── 3. Offline Engine ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Offline, text = "Offline Engine")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Embedded Architecture",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "100% offline runtime · Zero network requests",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CyanSubtle)
                        .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = "OFFLINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackSection
                    )
                }
            }

            // Engine status meter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardBackgroundElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Embedded catalogue",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize
                        )
                        Text(
                            text = "100%",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }

                    InstrumentMeter(
                        progress = { 1.0f },
                        accent = PrimaryCyan,
                        modifier = Modifier.fillMaxWidth(),
                        height = AlgoTokens.space4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "13 of 13 algorithms verified",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize
                        )
                        Text(
                            text = "52 code traces",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }
        }

        // ── 4. Display & Deck Behaviour ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Sliders, text = "Workspace Display & Deck")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = AlgoTokens.space3)) {
                    Text(
                        text = "High-Contrast Node Outlines",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Increase border stroke weight and label contrast on nodes and cells",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(
                    checked = highContrast,
                    onCheckedChange = { AppSettings.highContrastNodeOutlines = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = AlgoTokens.space3)) {
                    Text(
                        text = "Auto-Open Deck on Play",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Automatically expand the Focus Deck trace terminal when playback starts",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(
                    checked = autoOpenDeck,
                    onCheckedChange = { AppSettings.autoOpenDeckOnPlay = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = AlgoTokens.space3)) {
                    Text(
                        text = "Inline Complexity Readouts",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Display TIME and SPACE Big-O pills in the Visualizer header",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(
                    checked = showComplexity,
                    onCheckedChange = { AppSettings.showComplexityBadges = it }
                )
            }
        }

        // ── 5. Visualizer Preferences ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Tune, text = "Visualizer Preferences")

            Text(
                text = "Cell Scaling (S / M / L)",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Controls the visual canvas cell scaling across all visualizers (defaults to Small 0.7x).",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = AlgoType.microSize
            )

            Spacer(modifier = Modifier.height(AlgoTokens.space2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                listOf(
                    "S (0.7x)" to 0.7f,
                    "M (1.0x)" to 1.0f,
                    "L (1.25x)" to 1.25f
                ).forEach { (label, scale) ->
                    val isSel = AppSettings.defaultCellScale == scale
                    val scaleShape = RoundedCornerShape(AlgoTokens.radiusSm)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                            .pressPhysics(shape = scaleShape, accent = PrimaryCyan)
                            .clip(scaleShape)
                            .background(if (isSel) PrimaryCyan else CardBackgroundElevated)
                            .border(
                                AlgoTokens.strokeThin,
                                if (isSel) PrimaryCyan else BorderSubtle,
                                scaleShape
                            )
                            .clickable { AppSettings.defaultCellScale = scale }
                            .padding(vertical = AlgoTokens.space2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) DarkBackground else TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space3))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = AlgoTokens.space3)) {
                    Text(
                        text = "Tactile Haptic Feedback",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Vibrations on cell swaps, step ticks, and completion wave",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(
                    checked = AppSettings.hapticsEnabled,
                    onCheckedChange = { AppSettings.hapticsEnabled = it }
                )
            }
        }

        // ── 6. Guidance & Workspace Tour ──
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

        // ── 7. Data Management (Danger Zone) ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Storage, text = "Data Management")
            val clearShape = RoundedCornerShape(9.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                    .pressPhysics(shape = clearShape, accent = AccentRed)
                    .clip(clearShape)
                    .background(RedSubtle)
                    .border(1.dp, AccentRed.copy(alpha = 0.25f), clearShape)
                    .clickable {
                        AppSettings.clearAllSavedData()
                        dataClearedBanner = true
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Trash,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (dataClearedBanner) "Saved Data Reset to Defaults" else "Clear Saved Data",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${AppSettings.bookmarkedAlgorithmIds.size} Bookmarks · Prefs",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize
                )
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
                text = "AVIA v2.4.1 · Build 204 · MIT License",
                style = MaterialTheme.typography.bodySmall,
                color = TextNavy,
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
    com.example.algolens.ui.components.DoubleBezelShell(
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
    com.example.algolens.ui.theme.AlgoLensTheme {
        SettingsScreen(onBack = {})
    }
}
