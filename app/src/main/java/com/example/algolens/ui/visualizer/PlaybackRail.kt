package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.InstrumentMeter
import com.example.algolens.ui.components.RailIconButton
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import kotlin.math.roundToInt

/**
 * Two-Tier Bottom Transport Bar — the only bottom-of-screen chrome.
 *
 *  - **Tier 1** — the timeline: step counter, the bespoke [InstrumentMeter]
 *    scrub track (tap to jump, drag to scrub with live canvas preview) and the
 *    playback-speed chip.
 *  - **Tier 2** — the transport itself, in the thumb zone:
 *      Left `[ Restart ]` · Center `[ Step back ] [ Play / Pause ] [ Step forward ]`
 *      · Right `[ Challenge ] [ AI Tutor ]`
 *
 * **Hardening pass (Phase 5A).** Three pre-existing violations were fixed here
 * without changing what the rail does or how it is called:
 *  1. the timeline was a stock `Material3.Slider`; `UI_GUIDELINES` §17.6 requires
 *     the bespoke `InstrumentMeter` tick meter for progress;
 *  2. the bar painted `DarkBackground` (the screen base) instead of
 *     `AlgoTokens.surfaceFloat` (§17.2 — floating rails, sheets and the bottom
 *     nav sit one rung up);
 *  3. every geometry literal in the file was a raw `.dp` / `.sp`, against red
 *     line 6 and the `AlgolensRawDpSpacing` lint rule.
 *
 * The scrub track now drives [VisualizerScreenState.previewScrub] while a finger
 * is down and commits on release, so dragging updates the canvas, the narrative
 * strip and the code trace in lockstep under the finger. Nothing is committed if
 * the gesture is cancelled.
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
    val challengeIcon = remember { AlgoGlyphs.Target }
    val tutorIcon = remember { AlgoGlyphs.Spark }

    val speedBase = MaterialTheme.typography.labelSmall
    val speedChipStyle = remember(speedBase) {
        speedBase.copy(
            color = PrimaryCyan,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.labelSize
        )
    }
    val counterBase = MaterialTheme.typography.bodySmall
    val counterStyle = remember(counterBase) {
        counterBase.copy(
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.labelSize
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AlgoTokens.surfaceFloat)
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        // ── TIER 1: Timeline (counter · InstrumentMeter · speed) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                Text(
                    text = "${state.displayStepIdx + 1}",
                    style = counterStyle,
                    color = PrimaryCyan
                )
                Text(
                    text = "/ ${state.totalSteps}",
                    style = counterStyle,
                    color = TextMuted
                )
            }

            ScrubTrack(
                state = state,
                enabled = !challengeLocked,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .height(AlgoTokens.iconButtonXs)
                    .alpha(if (challengeLocked) AlgoTokens.disabledAlpha else 1f)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(CardBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusXs)
                    )
                    .pressPhysics(
                        shape = RoundedCornerShape(AlgoTokens.radiusXs),
                        accent = PrimaryCyan,
                        enabled = !challengeLocked
                    )
                    .clickable(enabled = !challengeLocked) { state.cycleSpeed() }
                    .padding(horizontal = AlgoTokens.space3),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.speedLabel,
                    style = speedChipStyle
                )
            }
        }

        // ── TIER 2: Un-crushed Transport Controls ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Restart
            RailIconButton(
                icon = refreshIcon,
                contentDescription = "Restart",
                boxSize = AlgoTokens.iconButtonLg,
                iconSize = AlgoTokens.inlineIconMd,
                tint = TextMuted,
                container = CardBackground,
                borderColor = BorderSubtle,
                onClick = { state.reset() }
            )

            // Center: Step Back | Hero Play/Pause | Step Forward
            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = skipBackIcon,
                    contentDescription = "Step back",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconLg,
                    tint = TextPrimary,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    enabled = !challengeLocked,
                    onClick = { state.stepBackward() }
                )

                // Hero play / pause: the one filled-cyan control on the screen.
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
                    contentDescription = "Step forward",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconLg,
                    tint = TextPrimary,
                    container = CardBackground,
                    borderColor = BorderSubtle,
                    enabled = !challengeLocked,
                    onClick = { state.stepForward() }
                )
            }

            // Right: Challenge | AI Tutor
            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailIconButton(
                    icon = challengeIcon,
                    contentDescription = "Challenge mode",
                    boxSize = AlgoTokens.iconButtonLg,
                    iconSize = AlgoTokens.inlineIconMd,
                    tint = if (state.challengeState.isActive) AccentPink else TextMuted,
                    container = if (state.challengeState.isActive) PinkSubtle else CardBackground,
                    borderColor = if (state.challengeState.isActive) AccentPink else BorderSubtle,
                    onClick = { state.toggleChallenge() }
                )

                RailIconButton(
                    icon = tutorIcon,
                    contentDescription = "AI tutor",
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

/**
 * The timeline track: a bespoke [InstrumentMeter] wrapped in the gesture that
 * makes it interactive.
 *
 * `InstrumentMeter` draws its ticks from a `progress` **lambda**, so advancing
 * the playhead never recomposes a text node — the reason §17.6 mandates it over
 * a stock Material indicator. The gesture layer is kept separate because the
 * meter itself is a pure canvas.
 *
 * A finger-down parks a *preview* playhead (see
 * [VisualizerScreenState.previewScrub]); release commits it. Tap and drag share
 * one code path, so there is no gesture ambiguity between them and the parent
 * never steals the touch.
 *
 * The 30dp band is the touch target; the drawn meter inside it stays at
 * [AlgoTokens.meterHeight] (2dp) per the instrument spec.
 */
@Composable
private fun ScrubTrack(
    state: VisualizerScreenState,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(AlgoTokens.iconButtonSm)
            .alpha(if (enabled) 1f else AlgoTokens.disabledAlpha)
            .pointerInput(enabled, state.totalSteps) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        down.consume()
                        state.previewScrub(stepAt(down.position.x, size.width, state.totalSteps))

                        var pressed = true
                        while (pressed) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id }
                            if (change == null || !change.pressed) {
                                pressed = false
                            } else if (change.positionChanged()) {
                                change.consume()
                                state.previewScrub(
                                    stepAt(change.position.x, size.width, state.totalSteps)
                                )
                            }
                        }
                        state.commitScrub()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        InstrumentMeter(
            progress = { state.stepProgress },
            accent = PrimaryCyan,
            height = AlgoTokens.meterHeight
        )
    }
}

/**
 * Maps a horizontal touch position to a step index, clamped at both ends so a
 * drag past either edge parks on the first / last step rather than overshooting.
 */
private fun stepAt(x: Float, widthPx: Int, totalSteps: Int): Int {
    if (widthPx <= 0 || totalSteps <= 1) return 0
    val ratio = (x / widthPx.toFloat()).coerceIn(0f, 1f)
    return (ratio * (totalSteps - 1)).roundToInt().coerceIn(0, totalSteps - 1)
}

@Preview(showBackground = true, backgroundColor = 0xFF111D30)
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

