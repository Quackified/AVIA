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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Original 3-line Peek height (126.dp) and Expanded height (285.dp) of the
 * docked terminal frame, plus the height of the attached window tabs row (~26.dp).
 */
internal val TERMINAL_PEEK_HEIGHT = 126.dp
internal val TERMINAL_EXPANDED_HEIGHT = 228.dp
internal val ATTACHED_TAB_VISUAL_HEIGHT = 26.dp
internal val ATTACHED_TABS_HEIGHT = ATTACHED_TAB_VISUAL_HEIGHT + AlgoTokens.space1
internal val DOCK_PEEK_TOTAL_HEIGHT = TERMINAL_PEEK_HEIGHT + ATTACHED_TABS_HEIGHT + 8.dp
internal val DOCK_EXPANDED_TOTAL_HEIGHT = TERMINAL_EXPANDED_HEIGHT + ATTACHED_TABS_HEIGHT + 8.dp

/**
 * Docked 3-Line Peek / Expand Terminal UI with attached top window tabs
 * (`Trace` | `State`).
 *
 * - **Always docked**: rests at [TERMINAL_PEEK_HEIGHT] (126.dp, showing the
 *   terminal titlebar + 3 lines of code or live state) and expands to
 *   [TERMINAL_EXPANDED_HEIGHT] (228.dp) when `state.deckExpanded` is true.
 * - **Attached window tabs**: `Trace` and `State` sit directly on top of the
 *   terminal frame (`26.dp` visual height inside a `44.dp` touch target) and
 *   serve as the sole explicit expand/collapse affordance.
 * - **Measured stage reservation**: reports its actual rendered height via
 *   [onMeasuredHeightChanged] so `VisualizerScreen` adapts accurately to font scaling.
 */
@Composable
fun InstrumentDeck(
    state: VisualizerScreenState,
    algorithm: Algorithm,
    currentStep: VisualizerStep,
    syncPulse: State<Float>,
    modifier: Modifier = Modifier,
    onMeasuredHeightChanged: ((androidx.compose.ui.unit.Dp) -> Unit)? = null
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
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
            .onSizeChanged { size ->
                if (size.height > 0 && onMeasuredHeightChanged != null) {
                    with(density) {
                        onMeasuredHeightChanged(size.height.toDp())
                    }
                }
            }
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space1)
    ) {
        // Attached window tabs sitting flush on top of the terminal frame,
        // with unified vertical heights and a matching tab-style Expand/Collapse button.
        AttachedDeckTabs(
            selected = state.deckPage,
            isExpanded = state.deckExpanded,
            onToggleExpand = { state.deckExpanded = !state.deckExpanded },
            onSelect = { page ->
                if (state.deckPage == page) {
                    state.deckExpanded = !state.deckExpanded
                } else {
                    state.deckPage = page
                }
            }
        )

        // Unified Terminal UI Frame (126.dp 3-line peek <-> 228.dp expanded)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(terminalHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = AlgoTokens.radiusMd,
                        bottomEnd = AlgoTokens.radiusMd
                    )
                )
                .background(CanvasBackground)
                .border(
                    AlgoTokens.strokeThin,
                    if (state.deckExpanded) PrimaryCyan.copy(alpha = 0.35f) else BorderSubtle,
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = AlgoTokens.radiusMd,
                        bottomEnd = AlgoTokens.radiusMd
                    )
                )
        ) {
            when (state.deckPage) {
                DeckPage.TRACE -> CodeTracePane(
                    step = currentStep,
                    algorithmName = algorithm.name,
                    algorithmId = algorithm.id,
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
                    timeComplexity = algorithm.timeComplexity,
                    spaceComplexity = algorithm.spaceComplexity,
                    onComplexityClick = { state.showTheorySheet = true },
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
 * the terminal frame, plus a matching tab-style `Expand` / `Collapse` button
 * on the top-right edge. All window tabs share an identical [ATTACHED_TAB_VISUAL_HEIGHT]
 * (`26.dp`) visual height inside a `44.dp` accessible hit box.
 */
@Composable
private fun AttachedDeckTabs(
    selected: DeckPage,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelect: (DeckPage) -> Unit
) {
    val tabShape = RoundedCornerShape(
        topStart = AlgoTokens.radiusSm,
        topEnd = AlgoTokens.radiusSm
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Left: Trace & State Window Tabs (identical 26dp height)
        Row(
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1),
            verticalAlignment = Alignment.Bottom
        ) {
            for (page in DeckPage.entries) {
                val isSelected = page == selected

                Box(
                    modifier = Modifier
                        .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                        .clickable { onSelect(page) },
                    contentAlignment = Alignment.BottomStart
                ) {
                    Row(
                        modifier = Modifier
                            .height(ATTACHED_TAB_VISUAL_HEIGHT)
                            .pressPhysics(shape = tabShape, accent = PrimaryCyan)
                            .clip(tabShape)
                            .background(if (isSelected) CardBackgroundElevated else CanvasBackground.copy(alpha = 0.85f))
                            .border(
                                width = AlgoTokens.strokeThin,
                                color = if (isSelected) PrimaryCyan.copy(alpha = 0.35f) else BorderSubtle,
                                shape = tabShape
                            )
                            .padding(horizontal = AlgoTokens.space4),
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
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Right: Expand / Collapse Window Tab Button (same tab style & 26dp height)
        Box(
            modifier = Modifier
                .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                .clickable { onToggleExpand() },
            contentAlignment = Alignment.BottomEnd
        ) {
            Row(
                modifier = Modifier
                    .height(ATTACHED_TAB_VISUAL_HEIGHT)
                    .pressPhysics(shape = tabShape, accent = PrimaryCyan)
                    .clip(tabShape)
                    .background(if (isExpanded) CyanSubtle else CardBackgroundElevated)
                    .border(
                        width = AlgoTokens.strokeThin,
                        color = if (isExpanded) PrimaryCyan.copy(alpha = 0.45f) else BorderSubtle,
                        shape = tabShape
                    )
                    .padding(horizontal = AlgoTokens.space4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Text(
                    text = if (isExpanded) "Collapse" else "Expand",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) AlgoGlyphs.ChevronDown else AlgoGlyphs.ChevronUp,
                    contentDescription = if (isExpanded) "Collapse deck" else "Expand deck",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
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
