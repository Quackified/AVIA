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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WifiOff
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

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("Kotlin") }
    var speedSlider by remember { mutableFloatStateOf(50f) }
    var offlineEnabled by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }
    var highContrast by remember { mutableStateOf(false) }

    val speedLabel = when {
        speedSlider < 34f -> "Slow"
        speedSlider < 67f -> "Normal"
        else -> "Fast"
    }
    val speedMs = when {
        speedSlider < 34f -> "800ms"
        speedSlider < 67f -> "480ms"
        else -> "220ms"
    }

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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBackground)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Back,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanSubtle)
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Tune,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // ── 1. Language Preference ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Code, text = "Preferred Language")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Kotlin", "Java", "Python", "C++").forEach { lang ->
                    val isSel = selectedLanguage == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) PrimaryCyan else CardBackgroundElevated)
                            .border(
                                1.dp,
                                if (isSel) PrimaryCyan else BorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedLanguage = lang }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) DarkBackground else TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = AlgoType.labelSize
                        )
                    }
                }
            }
            Text(
                text = "Code snippets use $selectedLanguage syntax in step-by-step explanations.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDark,
                fontSize = AlgoType.microSize,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ── 2. Playback Speed ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Speed, text = "Animation Playback Speed")
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
                onValueChange = { speedSlider = it },
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
                listOf("Slow", "Normal", "Fast").forEach {
                    Text(text = it, style = MaterialTheme.typography.labelSmall, color = TextNavy, fontSize = AlgoType.microSize)
                }
            }
        }

        // ── 3. Offline Mode ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Offline, text = "Offline Mode")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Enable Offline Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Cache algorithms for use without internet",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(
                    checked = offlineEnabled,
                    onCheckedChange = { offlineEnabled = it }
                )
            }

            // Cache progress
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
                            text = "Download progress",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize
                        )
                        Text(
                            text = "68%",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }

                    InstrumentMeter(
                        progress = { 0.68f },
                        accent = PrimaryCyan,
                        modifier = Modifier.fillMaxWidth(),
                        height = AlgoTokens.space4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "5 of 8 algorithms cached",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize
                        )
                        Text(
                            text = "12.4 MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }
        }

        // ── 4. Display Preferences ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Sliders, text = "Display")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Push Notifications",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Step completions & study reminders",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(checked = pushNotifications, onCheckedChange = { pushNotifications = it })
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "High Contrast Labels",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Increase label visibility on bars",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                CustomSwitch(checked = highContrast, onCheckedChange = { highContrast = it })
            }
        }

        // ── 5. Visualizer Preferences ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Tune, text = "Visualizer Preferences")

            Text(
                text = "Default Cell Size Preset",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Controls the default cell scaling when launching visualizers (can also be adjusted live in the visualizer header).",
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
                    "Small (0.7x)" to 0.7f,
                    "Normal (1.0x)" to 1.0f,
                    "Large (1.25x)" to 1.25f
                ).forEach { (label, scale) ->
                    val isSel = AppSettings.defaultCellScale == scale
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(if (isSel) PrimaryCyan else CardBackgroundElevated)
                            .border(
                                AlgoTokens.strokeThin,
                                if (isSel) PrimaryCyan else BorderSubtle,
                                RoundedCornerShape(AlgoTokens.radiusSm)
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
                Column {
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

        // ── 6. Data Management (Danger Zone) ──
        SettingsCard {
            SectionLabel(icon = AlgoGlyphs.Storage, text = "Data Management")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(RedSubtle)
                    .border(1.dp, AccentRed.copy(alpha = 0.25f), RoundedCornerShape(9.dp))
                    .clickable { }
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
                        text = "Clear Saved Data",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Bookmarks · History",
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
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            content()
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun SettingsScreenPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        SettingsScreen(onBack = {})
    }
}
