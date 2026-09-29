package com.avia.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.avia.model.Algorithm
import com.avia.model.AlgorithmId
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.RailIconButton
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.PinkSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary

/**
 * Two-Tier Bottom Transport Bar:
 *  - Tier 1 (Top): Full-width interactive step timeline [Slider] with zero-padded
 *    step counter (`01/65`) on the left and progress percentage (`$pct%`) on the right.
 *  - Tier 2 (Bottom): Transport controls inside a full-width [Box] so the center
 *    cluster stays at the exact horizontal midpoint:
 *      Left (`Alignment.CenterStart`): `[ Reset ]` + `[ Speed ("1.0x") ]`
 *      Center (`Alignment.Center`): `[ Step-Back ] [ Hero Play / Pause ] [ Step-Forward ]`
 *      Right (`Alignment.CenterEnd`): `[ Challenge ]` + `[ AI Tutor ]`
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
            fontSize = AlgoType.labelSize
        )
    }
    val counterBase = MaterialTheme.typography.bodySmall
    val counterStyle = remember(counterBase) {
        counterBase.copy(
            color = TextMuted,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.labelSize,
            fontFeatureSettings = "tnum"
        )
    }
    val pctStyle = remember(counterBase) {
        counterBase.copy(
            color = PrimaryCyan,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.labelSize,
            fontFeatureSettings = "tnum"
        )
    }

    val padWidth = state.totalSteps.toString().length.coerceAtLeast(2)
    val stepCounterText = "${(state.displayStepIdx + 1).toString().padStart(padWidth, '0')}/${state.totalSteps.toString().padStart(padWidth, '0')}"
    val pct = (((state.displayStepIdx + 1).toFloat() / state.totalSteps.coerceAtLeast(1)) * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground.copy(alpha = 0.94f))
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        // ── TIER 1: Full-Width Timeline Scrubber (01/65 · Slider · %) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            Text(
                text = stepCounterText,
                style = counterStyle
            )

            Slider(
                value = state.displayStepIdx.toFloat(),
                onValueChange = { state.previewScrub(it.toInt()) },
                onValueChangeFinished = { state.commitScrub() },
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
                    .height(AlgoTokens.iconButtonXs)
                    .alpha(if (challengeLocked) AlgoTokens.disabledAlpha else 1f)
            )

            Text(
                text = "$pct%",
                style = pctStyle
            )
        }

        // ── TIER 2: Transport Controls (Left: Reset + Speed | Center: Prev/Play/Next | Right: Challenge + Tutor) ──
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left Group: Reset + Speed Chip beside it
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = refreshIcon,
                    contentDescription = "Reset",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconMd,
                    tint = TextMuted,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    onClick = { state.reset() }
                )

                Box(
                    modifier = Modifier
                        .height(AlgoTokens.iconButtonLg)
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
                        .padding(horizontal = AlgoTokens.space4),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.speedLabel,
                        style = speedChipStyle
                    )
                }
            }

            // Center Group: Step Back | Hero Play/Pause | Step Forward (pinned to true center)
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = skipBackIcon,
                    contentDescription = "Step Back",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconLg,
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
                    boxSize = AlgoTokens.iconButtonLg + AlgoTokens.space3,
                    iconSize = AlgoTokens.inlineIconLg,
                    tint = DarkBackground,
                    container = PrimaryCyan,
                    borderColor = PrimaryCyan,
                    enabled = !challengeLocked || !state.isPlaying,
                    onClick = { state.togglePlay() }
                )

                RailIconButton(
                    icon = skipForwardIcon,
                    contentDescription = "Step Forward",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconLg,
                    tint = TextPrimary,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    enabled = !challengeLocked,
                    onClick = { state.stepForward() }
                )
            }

            // Right Group: AI Tutor Trigger
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = autoAwesomeIcon,
                    contentDescription = "AI Tutor",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconMd,
                    tint = PurpleGlow,
                    container = PurpleSubtle,
                    borderColor = SecondaryPurple.copy(alpha = 0.4f),
                    onClick = { state.showTutorSheet = true }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun PlaybackRailPreview() {
    AlgoLensTheme {
        val algorithm = remember { Algorithm(id = AlgorithmId.QUICK_SORT) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AlgoTokens.space4)
        ) {
            PlaybackRail(
                state = rememberVisualizerScreenState(algorithm)
            )
        }
    }
}

