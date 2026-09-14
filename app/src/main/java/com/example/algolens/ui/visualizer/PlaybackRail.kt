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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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

/**
 * Bottom-edge playback control surface. The transport row contains:
 *
 *  [ Reset ] [ Step-Back ] [ Play / Pause ] [ Step-Forward ] [ ── scrubber ── ] [ Speed ] [ Challenge ] [ AI Tutor ]
 *
 *  - **Step-back / scrubber / step-forward / speed** are all
 *    [AlgoTokens.disabledAlpha]-dimmed when the challenge is in
 *    flight, because the user is mid-prompt and shouldn't be jumping
 *    the algorithm.
 *  - **Challenge and AI Tutor** stay enabled even during a prompt
 *    because they don't mutate the playback position.
 *
 * The rail reads *only* from [VisualizerScreenState] and invokes
 * its mutators — no local state, no nested LaunchedEffects.
 */
@Composable
fun PlaybackRail(
    state: VisualizerScreenState,
    modifier: Modifier = Modifier
) {
    val challengeLocked = state.challengeInFlight
    // ── Static icon + size caches ─────────────────────────────────────────────
    // Every icon except the play/pause glyph is fully static for the lifetime
    // of this algorithm run. Caching them with `remember` (no keys) means the
    // same object reference is reused across all recompositions, so the
    // surrounding Modifier.size(...) / tint / container wrappers are not rebuilt
    // each step. `remember` without keys is stable across recomposition as long
    // as the composable's position in the tree is unchanged — which holds for
    // the rail that stays mounted during playback.
    val refreshIcon = remember { AlgoGlyphs.Refresh }
    val refreshButtonSize = remember { AlgoTokens.iconButtonSm }
    val refreshIconSize = remember { AlgoTokens.inlineIconMd - 1.dp }

    val skipBackIcon = remember { AlgoGlyphs.StepBack }
    val skipForwardIcon = remember { AlgoGlyphs.StepForward }
    val skipButtonSize = remember { AlgoTokens.iconButtonMd }
    val skipIconSize = remember { AlgoTokens.inlineIconMd }

    val emojiEventsIcon = remember { AlgoGlyphs.Target }
    val autoAwesomeIcon = remember { AlgoGlyphs.Spark }
    val chipButtonSize = remember { AlgoTokens.iconButtonMd }
    val chipIconSize = remember { AlgoTokens.inlineIconMd }

    // Play/Pause glyph is dynamic (driven by state.isPlaying) — keep inline.
    // But its container SIZE and ICON SIZE are static; cache them so the
    // Modifier.size() and the button-size wrapper are not rebuilt each step.
    val playButtonSize = remember { AlgoTokens.iconButtonLg }
    val playIconSize = remember { AlgoTokens.inlineIconLg }

    // Read-then-remember: speed-chip text is fully static chrome (labelSmall
    // + cyan/bold/8.5sp); only the speed *string* changes per tap.
    val speedStyleBase = MaterialTheme.typography.labelSmall
    val speedChipStyle = remember(speedStyleBase) {
        speedStyleBase.copy(
            color = PrimaryCyan,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AlgoTokens.glassElevated.copy(alpha = 0.92f))
            .border(AlgoTokens.strokeThin, BorderSubtle)
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RailIconButton(
            icon = refreshIcon,
            contentDescription = "Reset",
            boxSize = refreshButtonSize,
            iconSize = refreshIconSize,
            tint = TextMuted,
            container = CardBackground,
            borderColor = BorderSubtle,
            onClick = { state.reset() }
        )

        RailIconButton(
            icon = skipBackIcon,
            contentDescription = "Step Back",
            boxSize = skipButtonSize,
            iconSize = skipIconSize,
            tint = TextPrimary,
            container = CardBackground,
            borderColor = BorderSubtle,
            enabled = !challengeLocked,
            onClick = { state.stepBackward() }
        )

        RailIconButton(
            icon = if (state.isPlaying) AlgoGlyphs.Pause else AlgoGlyphs.Play,
            contentDescription = if (state.isPlaying) "Pause" else "Play",
            boxSize = playButtonSize,
            iconSize = playIconSize,
            tint = PrimaryCyan,
            container = CardBackground,
            borderColor = PrimaryCyan.copy(alpha = 0.4f),
            enabled = !challengeLocked || !state.isPlaying,
            onClick = { state.togglePlay() }
        )

        RailIconButton(
            icon = skipForwardIcon,
            contentDescription = "Step Forward",
            boxSize = skipButtonSize,
            iconSize = skipIconSize,
            tint = TextPrimary,
            container = CardBackground,
            borderColor = BorderSubtle,
            enabled = !challengeLocked,
            onClick = { state.stepForward() }
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

        // Speed chip
        Box(
            modifier = Modifier
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
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.speedLabel,
                style = speedChipStyle
            )
        }

        // Challenge toggle
        RailIconButton(
            icon = emojiEventsIcon,
            contentDescription = "Challenge Mode",
            boxSize = chipButtonSize,
            iconSize = chipIconSize,
            tint = if (state.challengeState.isActive) AccentPink else TextMuted,
            container = if (state.challengeState.isActive) PinkSubtle else CardBackground,
            borderColor = if (state.challengeState.isActive) AccentPink else BorderSubtle,
            onClick = { state.toggleChallenge() }
        )

        // AI Tutor
        RailIconButton(
            icon = autoAwesomeIcon,
            contentDescription = "AI Tutor",
            boxSize = chipButtonSize,
            iconSize = chipIconSize,
            tint = PurpleGlow,
            container = PurpleSubtle,
            borderColor = SecondaryPurple.copy(alpha = 0.4f),
            onClick = { state.showTutorSheet = true }
        )
    }
}
