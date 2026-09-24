package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.components.RailIconButton
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.pressPhysics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import com.example.algolens.ui.theme.DarkBackground

/**
 * Two-Tier Bottom Transport Bar:
 *  - Tier 1 (Top): Full-width interactive step timeline scrubber with step counter & progress percentage.
 *  - Tier 2 (Bottom): Un-crushed transport controls —
 *      Left: [ Reset ] [ Speed ]
 *      Center: [ Step-Back ] [ Hero Play / Pause ] [ Step-Forward ]
 *      Right: [ Challenge ] [ AI Tutor ]
 */
@Composable
fun PlaybackRail(
    state: VisualizerScreenState,
    modifier: Modifier = Modifier
) {
    val challengeLocked = state.challengeInFlight

    val refreshIcon = remember { AlgoGlyphs.Refresh }
    val skipBackIcon = remember { AlgoGlyphs.StepBack }
    val skipForwardIcon = remember { AlgoGlyphs.StepForward }
    val emojiEventsIcon = remember { AlgoGlyphs.Target }
    val autoAwesomeIcon = remember { AlgoGlyphs.Spark }

    val speedStyleBase = MaterialTheme.typography.labelSmall
    val speedChipStyle = remember(speedStyleBase) {
        speedStyleBase.copy(
            color = PrimaryCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground.copy(alpha = 0.94f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // ── TIER 1: Full-Width Timeline Scrubber ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${state.currentStepIdx + 1}/${state.totalSteps}",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )

            Slider(
                value = state.currentStepIdx.toFloat(),
                onValueChange = { state.scrubTo(it.toInt()) },
                valueRange = 0f..(state.totalSteps - 1).coerceAtLeast(1).toFloat(),
                steps = (state.totalSteps - 2).coerceAtLeast(0),
                enabled = !challengeLocked,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryCyan,
                    activeTrackColor = PrimaryCyan,
                    inactiveTrackColor = CardBackground
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
                    .alpha(if (challengeLocked) AlgoTokens.disabledAlpha else 1f)
            )

            val pct = (((state.currentStepIdx + 1).toFloat() / state.totalSteps.coerceAtLeast(1)) * 100).toInt()
            Text(
                text = "$pct%",
                color = PrimaryCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ── TIER 2: Un-crushed Transport Controls ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Group: Reset + Speed Chip
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = refreshIcon,
                    contentDescription = "Reset",
                    boxSize = 34.dp,
                    iconSize = 15.dp,
                    tint = TextMuted,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    onClick = { state.reset() }
                )

                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .alpha(if (challengeLocked) AlgoTokens.disabledAlpha else 1f)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CardBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                        .pressPhysics(
                            shape = RoundedCornerShape(AlgoTokens.radiusSm),
                            accent = PrimaryCyan,
                            enabled = !challengeLocked
                        )
                        .clickable(enabled = !challengeLocked) { state.cycleSpeed() }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.speedLabel,
                        style = speedChipStyle
                    )
                }
            }

            // Center Group: Step Back | Hero Play/Pause | Step Forward
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = skipBackIcon,
                    contentDescription = "Step Back",
                    boxSize = 36.dp,
                    iconSize = 16.dp,
                    tint = TextPrimary,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    enabled = !challengeLocked,
                    onClick = { state.stepBackward() }
                )

                // Hero Play / Pause button (Filled Cyan button)
                RailIconButton(
                    icon = if (state.isPlaying) AlgoGlyphs.Pause else AlgoGlyphs.Play,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    boxSize = 42.dp,
                    iconSize = 18.dp,
                    tint = DarkBackground,
                    container = PrimaryCyan,
                    borderColor = PrimaryCyan,
                    enabled = !challengeLocked || !state.isPlaying,
                    onClick = { state.togglePlay() }
                )

                RailIconButton(
                    icon = skipForwardIcon,
                    contentDescription = "Step Forward",
                    boxSize = 36.dp,
                    iconSize = 16.dp,
                    tint = TextPrimary,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    enabled = !challengeLocked,
                    onClick = { state.stepForward() }
                )
            }

            // Right Group: Challenge Mode + AI Tutor
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = emojiEventsIcon,
                    contentDescription = "Challenge Mode",
                    boxSize = 34.dp,
                    iconSize = 15.dp,
                    tint = if (state.challengeState.isActive) AccentPink else TextMuted,
                    container = if (state.challengeState.isActive) PinkSubtle else CardBackground,
                    borderColor = if (state.challengeState.isActive) AccentPink else BorderSubtle,
                    onClick = { state.toggleChallenge() }
                )

                RailIconButton(
                    icon = autoAwesomeIcon,
                    contentDescription = "AI Tutor",
                    boxSize = 34.dp,
                    iconSize = 15.dp,
                    tint = PurpleGlow,
                    container = PurpleSubtle,
                    borderColor = SecondaryPurple.copy(alpha = 0.4f),
                    onClick = { state.showTutorSheet = true }
                )
            }
        }
    }
}

