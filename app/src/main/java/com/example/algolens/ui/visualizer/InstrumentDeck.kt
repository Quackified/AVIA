package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.AlgoHairline
import com.example.algolens.ui.components.RailIconButton
import com.example.algolens.ui.components.SegmentedToggle
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.TextMuted

/**
 * Height of the expanded deck, as a fraction of the stage it overlays. Kept as a
 * **fraction, not a dp** on purpose: the deck must not introduce a second
 * absolute height budget that fights the responsive canvas for space. At 46% the
 * deck shows a comfortable 6–7 code lines while leaving the majority of the
 * stage visible behind it.
 */
private const val DECK_HEIGHT_FRACTION = 0.46f

/**
 * The Focus Deck — the single bottom-anchored surface that replaced the
 * permanently-docked 3-line terminal.
 *
 * **What it fixes.** The visualizer column used to hold three stacked bottom
 * bands: a 126dp code peek, a timeline-scrubber tier and a transport tier. The
 * deck collapses that to one surface the user opens on demand, and because it
 * **overlays the stage** instead of joining the column, opening it cannot
 * re-measure the canvas — which is what used to re-run the responsive
 * cell-sizing maths in `CellArrayVisualizer` mid-interaction.
 *
 * **Pages.** [DeckPage.TRACE] hosts the existing `CodeTracePane` (terminal
 * frame, syntax highlighting, active-line anchoring, shared `LazyListState` —
 * all unchanged) and [DeckPage.STATE] hosts [StateDeckPage]. Adding a page is
 * one enum entry plus one branch here.
 *
 * Pages are selected with the catalogue's own [SegmentedToggle] rather than a
 * bespoke chip row, and the collapse affordance is the rail's own
 * [RailIconButton] — no new control vocabulary was introduced for this surface.
 */
@Composable
fun InstrumentDeck(
    state: VisualizerScreenState,
    algorithm: Algorithm,
    currentStep: VisualizerStep,
    syncPulse: State<Float>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.deckExpanded,
        modifier = modifier.fillMaxWidth(),
        enter = fadeIn(tween(200)) + expandVertically(expandFrom = Alignment.Bottom),
        exit = fadeOut(tween(160)) + shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(DECK_HEIGHT_FRACTION)
                .clip(RoundedCornerShape(AlgoTokens.radiusLg))
                .background(AlgoTokens.surfaceFloat)
                .border(
                    AlgoTokens.strokeThin,
                    BorderSubtle,
                    RoundedCornerShape(AlgoTokens.radiusLg)
                )
        ) {
            DeckHeaderRow(state = state)

            AlgoHairline()

            when (state.deckPage) {
                DeckPage.TRACE -> CodeTracePane(
                    step = currentStep,
                    algorithmName = algorithm.name,
                    syncPulse = syncPulse,
                    fillsAvailableHeight = true,
                    // The terminal titlebar becomes the collapse affordance,
                    // so it stops being a dead tap target in this context.
                    onToggleExpand = { state.deckExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                DeckPage.STATE -> StateDeckPage(
                    algorithmName = algorithm.name,
                    step = currentStep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

/**
 * Deck chrome: page selector on the left, collapse on the right.
 */
@Composable
private fun DeckHeaderRow(state: VisualizerScreenState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SegmentedToggle(
            options = listOf(
                DeckPage.TRACE.label to DeckPage.TRACE,
                DeckPage.STATE.label to DeckPage.STATE
            ),
            selectedKey = state.deckPage,
            onContainer = AlgoTokens.surfaceSunken,
            onSelect = { key ->
                if (key is DeckPage) state.deckPage = key
            }
        )

        RailIconButton(
            icon = AlgoGlyphs.ChevronDown,
            contentDescription = "Collapse deck",
            boxSize = AlgoTokens.iconButtonSm,
            iconSize = AlgoTokens.inlineIconSm,
            tint = TextMuted,
            container = Color.Transparent,
            borderColor = BorderSubtle,
            onClick = { state.deckExpanded = false }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
@Composable
fun InstrumentDeckPreview() {
    AlgoLensTheme {
        val algorithm = Algorithm(id = AlgorithmId.QUICK_SORT)
        // Preview-only: the deck is a conditional surface, so open it at
        // construction rather than mutating state during composition.
        val deckState = remember(algorithm) {
            VisualizerScreenState(algorithm).also { it.deckExpanded = true }
        }
        val pulse = remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        ) {
            InstrumentDeck(
                state = deckState,
                algorithm = algorithm,
                currentStep = VisualizerStep(
                    stepIndex = 4,
                    description = "Comparing element at index 2 (9) with pivot (7)",
                    array = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7),
                    activeCodeLines = listOf(5),
                    variables = mapOf("i" to "0", "j" to "2", "pivot" to "7")
                ),
                syncPulse = pulse,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
