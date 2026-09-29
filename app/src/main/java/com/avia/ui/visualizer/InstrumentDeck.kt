package com.avia.ui.visualizer

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avia.model.Algorithm
import com.avia.model.AlgorithmId
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextSecondary
import kotlinx.coroutines.launch

internal val TERMINAL_PEEK_HEIGHT = 126.dp
internal val TERMINAL_EXPANDED_HEIGHT = 228.dp
internal val ATTACHED_TAB_VISUAL_HEIGHT = 26.dp
internal val ATTACHED_TABS_HEIGHT = ATTACHED_TAB_VISUAL_HEIGHT + AlgoTokens.space1
internal val DOCK_PEEK_TOTAL_HEIGHT = TERMINAL_PEEK_HEIGHT + ATTACHED_TABS_HEIGHT + 8.dp
internal val DOCK_EXPANDED_TOTAL_HEIGHT = TERMINAL_EXPANDED_HEIGHT + ATTACHED_TABS_HEIGHT + 8.dp

/**
 * Obsidian-style Workspace Deck Widget.
 *
 * Terminal-styled card frame wrapping enabled instruments ([WidgetType.TRACE],
 * [WidgetType.STATE], [WidgetType.TELEMETRY]) in a horizontally swipeable pager with
 * infinite wrap-around scroll, bottom-overlay dot indicators, and a More Options button.
 *
 * Titlebar: `(ooo | Icon | Widget Name |                         : |)`
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
    val enabledWidgets = state.enabledWidgets
    val coroutineScope = rememberCoroutineScope()
    var showWidgetManagerModal by remember { mutableStateOf(false) }

    val pageCount = if (enabledWidgets.size > 1) 1000 * enabledWidgets.size else 1
    val initialPage = if (enabledWidgets.size > 1) {
        500 * enabledWidgets.size + state.activeWidgetIndex.coerceIn(0, enabledWidgets.size - 1)
    } else 0

    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    val currentWidgetIndex = if (enabledWidgets.isNotEmpty()) {
        ((pagerState.currentPage % enabledWidgets.size) + enabledWidgets.size) % enabledWidgets.size
    } else 0

    val currentWidget = enabledWidgets.getOrNull(currentWidgetIndex) ?: WidgetType.TRACE

    LaunchedEffect(pagerState.currentPage, enabledWidgets.size) {
        if (enabledWidgets.isNotEmpty()) {
            state.activeWidgetIndex = currentWidgetIndex
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TERMINAL_EXPANDED_HEIGHT)
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space1)
    ) {
        val cardShape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )

        // Outer Card Frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(CanvasBackground)
                .border(
                    AlgoTokens.strokeThin,
                    SecondaryPurple.copy(alpha = 0.35f),
                    cardShape
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Terminal UI Style Titlebar Header: [ ooo | Icon | Widget Name |                  : ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AlgoTokens.iconButtonSm)
                        .background(CardBackgroundElevated)
                        .padding(horizontal = AlgoTokens.space4),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Traffic Light Dots (ooo) + Icon + Widget Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                    ) {
                        // Traffic light dots (red, yellow, green)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AlgoTokens.space3)
                                    .clip(CircleShape)
                                    .background(AccentRed.copy(alpha = 0.85f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(AlgoTokens.space3)
                                    .clip(CircleShape)
                                    .background(AccentYellow.copy(alpha = 0.85f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(AlgoTokens.space3)
                                    .clip(CircleShape)
                                    .background(AccentGreen.copy(alpha = 0.85f))
                            )
                        }

                        // Widget Icon + Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            val widgetGlyph = when (currentWidget) {
                                WidgetType.TRACE -> AlgoGlyphs.Code
                                WidgetType.STATE -> AlgoGlyphs.Terminal
                                WidgetType.TELEMETRY -> AlgoGlyphs.Nodes
                            }
                            Icon(
                                imageVector = widgetGlyph,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                            val formattedName = when (currentWidget) {
                                WidgetType.TRACE -> "Code Trace"
                                WidgetType.STATE -> "State"
                                WidgetType.TELEMETRY -> "Telemetry"
                            }
                            Text(
                                text = formattedName,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = AlgoType.microSize,
                                maxLines = 1
                            )
                        }
                    }

                    // Right: More Options button (opens widget manager modal)
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.iconButtonXs)
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { showWidgetManagerModal = true }
                            .semantics {
                                this.role = Role.Button
                                this.contentDescription = "Manage workspace widgets"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.More,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                        )
                    }
                }

                // Titlebar bottom hairline separator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AlgoTokens.strokeHairline)
                        .background(SecondaryPurple.copy(alpha = 0.35f))
                )

                // Pager Content Area
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val actualWidget = if (enabledWidgets.isNotEmpty()) {
                        enabledWidgets[((page % enabledWidgets.size) + enabledWidgets.size) % enabledWidgets.size]
                    } else WidgetType.TRACE

                    when (actualWidget) {
                        WidgetType.TRACE -> CodeTracePane(
                            step = currentStep,
                            algorithmName = algorithm.name,
                            algorithmId = algorithm.id,
                            syncPulse = syncPulse,
                            fillsAvailableHeight = true,
                            isExpandedOverride = true,
                            showHeader = false,
                            modifier = Modifier.fillMaxSize()
                        )

                        WidgetType.STATE -> StateDeckPage(
                            algorithmName = algorithm.name,
                            step = currentStep,
                            timeComplexity = algorithm.timeComplexity,
                            spaceComplexity = algorithm.spaceComplexity,
                            onComplexityClick = { state.showTheorySheet = true },
                            isExpanded = true,
                            onToggleExpand = {},
                            modifier = Modifier.fillMaxSize()
                        )

                        WidgetType.TELEMETRY -> TelemetryDeckPage(
                            step = currentStep,
                            algorithmName = algorithm.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Bottom-Center Floating Ellipses (...) Overlay
            if (enabledWidgets.size > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = AlgoTokens.space2)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(DarkBackground.copy(alpha = 0.85f))
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        for (idx in enabledWidgets.indices) {
                            val isActive = idx == currentWidgetIndex
                            Box(
                                modifier = Modifier
                                    .height(AlgoTokens.space1)
                                    .width(if (isActive) AlgoTokens.space4 else AlgoTokens.space1)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(if (isActive) PrimaryCyan else TextDark)
                                    .clickable {
                                        val diff = idx - currentWidgetIndex
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + diff)
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Widget Manager Modal
        if (showWidgetManagerModal) {
            WidgetManagerModal(
                state = state,
                onDismiss = { showWidgetManagerModal = false }
            )
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
