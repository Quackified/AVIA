package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Original 3-line Peek height (126.dp) and Expanded height (285.dp) of the
 * docked terminal frame, plus the height of the attached window tabs row (~26.dp).
 */
internal val TERMINAL_PEEK_HEIGHT = 126.dp
internal val TERMINAL_EXPANDED_HEIGHT = 285.dp
internal val ATTACHED_TABS_HEIGHT = 26.dp
internal val DOCK_PEEK_TOTAL_HEIGHT = TERMINAL_PEEK_HEIGHT + ATTACHED_TABS_HEIGHT + 8.dp
internal val DOCK_EXPANDED_TOTAL_HEIGHT = TERMINAL_EXPANDED_HEIGHT + ATTACHED_TABS_HEIGHT + 8.dp

/**
 * Docked 3-Line Peek / Expand Terminal UI with attached top window tabs
 * (`Trace` | `State`).
 *
 * - **Always docked**: rests at [TERMINAL_PEEK_HEIGHT] (126.dp, showing the
 *   terminal titlebar + 3 lines of code or live state) and expands to
 *   [TERMINAL_EXPANDED_HEIGHT] (285.dp) when `state.deckExpanded` is true.
 * - **Attached window tabs**: `Trace` and `State` sit directly on top of the
 *   terminal frame with no background bar behind them.
 * - **Unified Terminal UI**: both [DeckPage.TRACE] and [DeckPage.STATE] retain
 *   the terminal titlebar (`Traffic-Light Dots | filename | Language/State badge | Expand ^ / Collapse v`)
 *   and share the [CanvasBackground] terminal body.
 */
@Composable
fun InstrumentDeck(
    state: VisualizerScreenState,
    algorithm: Algorithm,
    currentStep: VisualizerStep,
    syncPulse: State<Float>,
    modifier: Modifier = Modifier
) {
    val terminalHeight by animateDpAsState(
        targetValue = if (state.deckExpanded) TERMINAL_EXPANDED_HEIGHT else TERMINAL_PEEK_HEIGHT,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "instrumentDeckTerminalHeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space1)
    ) {
        // Attached window tabs sitting flush on top of the terminal frame
        AttachedDeckTabs(
            selected = state.deckPage,
            onSelect = { page ->
                if (state.deckPage == page) {
                    state.deckExpanded = !state.deckExpanded
                } else {
                    state.deckPage = page
                }
            }
        )

        // Unified Terminal UI Frame (126.dp 3-line peek <-> 285.dp expanded)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(terminalHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = AlgoTokens.radiusXs,
                        topEnd = AlgoTokens.radiusMd,
                        bottomStart = AlgoTokens.radiusMd,
                        bottomEnd = AlgoTokens.radiusMd
                    )
                )
                .background(CanvasBackground)
                .border(
                    AlgoTokens.strokeThin,
                    if (state.deckExpanded) PrimaryCyan.copy(alpha = 0.35f) else BorderSubtle,
                    RoundedCornerShape(
                        topStart = AlgoTokens.radiusXs,
                        topEnd = AlgoTokens.radiusMd,
                        bottomStart = AlgoTokens.radiusMd,
                        bottomEnd = AlgoTokens.radiusMd
                    )
                )
        ) {
            when (state.deckPage) {
                DeckPage.TRACE -> CodeTracePane(
                    step = currentStep,
                    algorithmName = algorithm.name,
                    syncPulse = syncPulse,
                    fillsAvailableHeight = true,
                    isExpandedOverride = state.deckExpanded,
                    showHeader = true,
                    onToggleExpand = { state.deckExpanded = !state.deckExpanded },
                    modifier = Modifier.fillMaxSize()
                )

                DeckPage.STATE -> StateDeckPage(
                    algorithmName = algorithm.name,
                    step = currentStep,
                    isExpanded = state.deckExpanded,
                    onToggleExpand = { state.deckExpanded = !state.deckExpanded },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Window tabs (`Trace` | `State`) attached directly to the top-left edge of
 * the terminal frame, with no background bar behind them.
 *
 * The active tab uses [CardBackgroundElevated] so it connects directly into the
 * terminal titlebar directly below it.
 */
@Composable
private fun AttachedDeckTabs(
    selected: DeckPage,
    onSelect: (DeckPage) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AlgoTokens.space2),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
        verticalAlignment = Alignment.Bottom
    ) {
        for (page in DeckPage.entries) {
            val isSelected = page == selected
            val shape = RoundedCornerShape(
                topStart = AlgoTokens.radiusSm,
                topEnd = AlgoTokens.radiusSm
            )

            Row(
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) CardBackgroundElevated else CanvasBackground.copy(alpha = 0.85f))
                    .border(
                        width = AlgoTokens.strokeThin,
                        color = if (isSelected) PrimaryCyan.copy(alpha = 0.35f) else BorderSubtle,
                        shape = shape
                    )
                    .clickable { onSelect(page) }
                    .padding(
                        horizontal = AlgoTokens.space4,
                        vertical = AlgoTokens.space1
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Box(
                    modifier = Modifier
                        .size(AlgoTokens.space3)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) PrimaryCyan else TextMuted.copy(alpha = 0.45f)
                        )
                )
                Text(
                    text = page.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSelected) TextPrimary else TextMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
@Composable
fun InstrumentDeckPreview() {
    AlgoLensTheme {
        val algorithm = Algorithm(id = AlgorithmId.QUICK_SORT)
        val deckState = remember(algorithm) {
            VisualizerScreenState(algorithm)
        }
        val pulse = remember { mutableStateOf(0f) }

        Box(modifier = Modifier.fillMaxWidth()) {
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
                syncPulse = pulse
            )
        }
    }
}
