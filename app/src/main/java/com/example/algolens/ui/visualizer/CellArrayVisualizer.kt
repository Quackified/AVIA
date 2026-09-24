package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.ChipBackground
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * Data structure describing an active off-screen pointer target.
 * Public so the test suite (`FeatureEnhancementsTest`) can construct it directly.
 */
@Immutable
data class OffscreenPointerTarget(
    val index: Int,
    val value: Int,
    val label: String,
    val state: ElementState = ElementState.IDLE,
    val badgeBg: Color = SecondaryPurple,
    val badgeTextColor: Color = Color.White,
    val isRight: Boolean = true
)

/**
 * Alternative Box/Cell Array Visualizer mode with top & bottom labeled pointer badges,
 * comparison expression callout, animated "pop up & shift" swap travel animations,
 * glowing Challenge-Mode target cells, and animated pop-up pill indicator cells
 * for off-screen pointers. The synchronized CodeTracePane is hosted by the
 * VisualizerScreen coordinator (bottom workspace region), not embedded here.
 *
 * Thin public shell — composes the following private modules in the same package:
 *  - [CellGrid]         — LazyRow + per-cell paint (badges, box, index label)
 *  - [CellPointerBadges] — [TopPointerBadge] / [BottomPointerBadge]
 *  - [OffscreenPointerBanner] — animated off-screen pop-up pills
 *  - [CellGlowPainter]   — three-band outer stroke glow
 *  - [ChallengeGlowTargets] — shared pulse state for Challenge Mode halos
 */
@Composable
fun CellArrayVisualizer(
    step: VisualizerStep,
    spec: com.example.algolens.model.AlgorithmSpec? = null,
    selectedCellIndices: Set<Int> = emptySet(),
    challengeTargetIndices: Set<Int> = emptySet(),
    syncPulse: State<Float> = mutableStateOf(0f),
    onCellClick: ((Int) -> Unit)? = null,
    cellScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val algorithmName = spec?.id?.displayName ?: "Bubble Sort"
    val isMergeSort = spec?.id == com.example.algolens.model.AlgorithmId.MERGE_SORT
    val dimOutOfRange = spec?.dimOutOfRangeCells == true

    val lazyListState = rememberLazyListState()

    // ─────────────────────────────────────────────────────────────────────
    // Swap Travel Animation: cells "pop up", then shift and settle into
    // their respective new slots with an elevated shadow while in flight.
    // ─────────────────────────────────────────────────────────────────────
    val density = LocalDensity.current

    // Slot pitch (cell width + gap + item padding) in px. Baseline here;
    // refined responsively by BoxWithConstraints further below.
    var slotPitchPx by remember { mutableFloatStateOf(with(density) { 42.dp.toPx() }) }

    val previousArray = remember { mutableStateOf(step.array) }
    val slotFlight = rememberSlotFlightMap(
        current = step.array,
        previous = previousArray.value,
        swappedIndices = step.swappedIndices,
        slotPitchPx = slotPitchPx,
        key = step.stepIndex,
    )
    LaunchedEffect(step.stepIndex, step.array) {
        previousArray.value = step.array
    }

    // Challenge Mode halo pulse (shared infinite transition for all target
    // cells). Held as State and read inside draw lambdas only, so the
    // infinite animation never triggers per-frame recomposition.
    val challengePulseState = rememberChallengePulseState()

    // Determine currently visible item indices in LazyRow
    val visibleItemIndices by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
        }
    }

    // Smoothly keep the active elements / pointers in view only when outside viewport
    val activeIndices = remember(step.elementStates) {
        step.elementStates.filter { it.value != ElementState.IDLE }.keys
    }
    val pointerIndices = remember(step.topPointers, step.bottomPointers) {
        step.topPointers.values + step.bottomPointers.values
    }
    val targetIndex = remember(activeIndices, pointerIndices) {
        (activeIndices + pointerIndices).minOrNull()
    }

    // Adaptive scroll strategy (3 cases):
    //   1. Array fits in viewport  -> center it.
    //   2. Array overflows, no active target -> pin index 0 to the
    //      left edge (UX brief: 'left index shifts towards the
    //      left, bounding area + current margin').
    //   3. Array overflows, active target present -> scroll so the
    //      leftmost active target is the leftmost visible item
    //      (preserves the original 'keep active elements in
    //      view' behavior). The centering math uses the viewport
    //      width measured inside BoxWithConstraints; we hoist the
    //      computed sizing into state so the effect can read it.
    var outerCellWidth by remember { mutableStateOf(0.dp) }
    var outerCellGap by remember { mutableStateOf(0.dp) }
    var outerViewportWidthDp by remember { mutableStateOf(0.dp) }

    LaunchedEffect(step.stepIndex, step.array.size, outerCellWidth, outerCellGap, outerViewportWidthDp) {
        if (step.array.isEmpty()) return@LaunchedEffect
        if (outerViewportWidthDp == 0.dp) return@LaunchedEffect
        if (outerCellWidth == 0.dp) return@LaunchedEffect
        val nodeCount = step.array.size

        val totalRowWidth = (outerCellWidth * nodeCount) + (outerCellGap * (nodeCount - 1).coerceAtLeast(0))
        if (totalRowWidth <= outerViewportWidthDp) {
            // Adaptive zero-clip mode: array fits completely within the canvas well.
            // Reset scroll to 0 if displaced so the array stays statically centered.
            if (lazyListState.firstVisibleItemIndex != 0 || lazyListState.firstVisibleItemScrollOffset != 0) {
                lazyListState.scrollToItem(0, 0)
            }
        } else {
            val anchor = (targetIndex ?: 0).coerceIn(0, nodeCount - 1)
            if (anchor !in visibleItemIndices) {
                lazyListState.animateScrollToItem(anchor)
            }
        }
    }

    val isBubbleSort = algorithmName.contains("bubble", ignoreCase = true)
    val isSelectionSort = algorithmName.contains("selection", ignoreCase = true)
    val isInsertionSort = algorithmName.contains("insertion", ignoreCase = true)

    val exprStyle = MaterialTheme.typography.labelMedium.copy(
        fontSize = AlgoType.microSize,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = AlgoType.trackTight
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val nodeCount = step.array.size.coerceAtLeast(1)
        val availableWidth = (maxWidth - 16.dp).coerceAtLeast(240.dp)

        // Normalized scale: S (0.7f) -> 0.0f, M (1.0f) -> 0.55f, L (1.25f) -> 1.0f
        val normalizedScale = ((cellScale - 0.7f) / (1.25f - 0.7f)).coerceIn(0f, 1f)

        // Target horizontal budget fill ratio so the cell array is ALWAYS the main visual focus:
        // S fills 90% of the stage width, M fills 96%, L fills 100%.
        val budgetFillRatio = 0.90f + (0.10f * normalizedScale)
        val targetRowBudget = availableWidth * budgetFillRatio

        // Adaptive base banding (slotGap): tightens automatically on larger arrays so budget
        // goes to cell width, and expands on smaller arrays so cells + banding span the stage.
        val initialGap = when {
            nodeCount >= 11 -> 3.dp
            nodeCount >= 9 -> 4.dp
            nodeCount >= 7 -> 5.5.dp
            nodeCount >= 5 -> 7.dp
            else -> 9.dp
        } * (0.85f + 0.15f * normalizedScale)

        val initialTotalGaps = initialGap * (nodeCount - 1).coerceAtLeast(0)
        val rawCellWidth = (targetRowBudget - initialTotalGaps) / nodeCount

        // No tiny hard cap! Allow cells on low array counts to expand up to hero sizes (54dp..68dp)
        // so S scaling on low array counts fills the horizontal budget as the centerpiece.
        val minReadableWidth = (22f + 8f * normalizedScale).dp
        val maxHeroCellWidth = (54f + 14f * normalizedScale).dp
        val cellWidth = rawCellWidth.coerceIn(minReadableWidth, maxHeroCellWidth)

        // When nodeCount is very low (e.g. 3-5 items) and cellWidth reaches maxHeroCellWidth,
        // adaptively widen the banding (slotGap) so the array still fills the horizontal budget.
        val slotGap = if (nodeCount > 1) {
            val remainingBudgetForGaps = (targetRowBudget - (cellWidth * nodeCount)).coerceAtLeast(initialTotalGaps)
            (remainingBudgetForGaps / (nodeCount - 1)).coerceIn(2.5.dp, 16.dp)
        } else {
            0.dp
        }

        // Keep cellHeight sleek and balanced (capped at 56.dp) so wide cells on low array counts
        // never overflow vertically or collide with top pointers / bottom info bar.
        val cellHeight = (cellWidth * 1.08f).coerceIn(34.dp, 56.dp)
        val cellTextSize = (cellWidth.value * 0.36f).coerceIn(10f, 18f).sp

        LaunchedEffect(cellWidth, slotGap, availableWidth) {
            outerCellWidth = cellWidth
            outerCellGap = slotGap
            outerViewportWidthDp = availableWidth
        }

        LaunchedEffect(cellWidth, slotGap) {
            slotPitchPx = with(density) { (cellWidth + slotGap).toPx() }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── 1. Compact Phase Header ──
            PhaseBanner(
                step = step,
                algorithmName = algorithmName,
                modifier = Modifier.fillMaxWidth()
            )

            // ── 2. Centered Visual Stage (Top info bar removed; Bottom info bar retained) ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CellGrid(
                        step = step,
                        selectedCellIndices = selectedCellIndices,
                        challengeTargetIndices = challengeTargetIndices,
                        syncPulse = syncPulse,
                        onCellClick = onCellClick,
                        isSelectionSort = isSelectionSort,
                        isBubbleSort = isBubbleSort,
                        dimOutOfRange = dimOutOfRange,
                        slotFlight = slotFlight,
                        challengePulseState = challengePulseState,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        cellTextSize = cellTextSize,
                        cellGap = slotGap,
                        viewportWidthDp = availableWidth,
                        cellScale = cellScale,
                        lazyListState = lazyListState
                    )

                    OffscreenPointerBanner(
                        step = step,
                        lazyListState = lazyListState
                    )

                    // ── Comparison / Step Expression Callout (Bottom Info Bar) ──
                    val expr = step.comparisonExpr ?: step.description
                    if (expr.isNotBlank()) {
                        Spacer(modifier = Modifier.height(AlgoTokens.space1))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                .background(ChipBackground)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXxs))
                                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = expr.uppercase(),
                                style = exprStyle,
                                color = when {
                                    step.comparisonExpr == null -> AlgoTokens.accentCyan
                                    expr.startsWith("SWAP", ignoreCase = true) -> AlgoTokens.accentPink
                                    else -> AccentYellow
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CellArrayVisualizerPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            CellArrayVisualizer(
                step = VisualizerStep(
                    stepIndex = 4,
                    description = "Comparing element at index 2 (9) with pivot (7)",
                    comparisonExpr = "COMPARE: 9 <= 7?",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7),
                    elementStates = mapOf(
                        0 to ElementState.ACTIVE,
                        2 to ElementState.COMPARING,
                        8 to ElementState.PIVOT
                    ),
                    topPointers = mapOf(
                        "L" to 0,
                        "pivot" to 8
                    ),
                    bottomPointers = mapOf(
                        "i" to 0,
                        "j" to 2
                    ),
                    activeCodeLines = listOf(5)
                ),
                spec = com.example.algolens.data.AlgorithmRegistry.specFor(com.example.algolens.model.AlgorithmId.QUICK_SORT)
            )
        }
    }
}
