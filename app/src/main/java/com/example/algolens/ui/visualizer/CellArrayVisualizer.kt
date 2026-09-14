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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Responsive tactile-canvas sizing: card dimensions, type scale and
        // slot pitch derive from the available width and total node count.
        val nodeCount = step.array.size.coerceAtLeast(1)
        val availableWidth = maxWidth - 36.dp // canvas well padding + gutters
        // Adaptive cell sizing: cells grow to fill the available
        // width so the row always spans the canvas. The header's S/M/L
        // toggle (cellScale = 0.7..1.25) scales the cell width so
        // users actually see the cells shrink/grow, not just the
        // digits inside them. The 14dp floor and 44dp cap still
        // protect readability at the extremes:
        //  - 14dp floor: a 20-element array at S scale would otherwise
        //    collapse cells below the minimum touch-target width.
        //  - 44dp cap: a 3-element array at L scale would otherwise
        //    balloon cells past the cap the rest of the UI assumes.
        val slotGap = AlgoTokens.space3
        val totalGaps = slotGap * (nodeCount - 1).coerceAtLeast(0)
        val usableWidth = (availableWidth - totalGaps).coerceAtLeast(0.dp)
        val cellWidth = ((usableWidth / nodeCount) * cellScale).coerceIn(14.dp, 44.dp)
        val cellHeight = cellWidth * 1.12f
        // Font size scales with the cell width (it was the cellScale
        // multiplier before — that math now lives in the cellWidth
        // calc above). 0.36 × cellWidth gives a digit size that
        // reads cleanly across the S/M/L range.
        val cellTextSize = (cellWidth.value * 0.36f).coerceIn(7f, 16f).sp

        // Read-then-remember: cache the static text merges once per
        // composition so the per-step callout reuses a stable TextStyle.
        // The *string and color* stay dynamic — only the typography merge
        // (weight/size/spacing) is baked here.
        val exprStyleBase = MaterialTheme.typography.labelSmall
        val exprStyle = remember(exprStyleBase) {
            exprStyleBase.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                fontSize = AlgoType.microSize
            )
        }

        // Mirror the computed sizing into the state that the
        // adaptive-scroll LaunchedEffect above reads. We pass by
        // value (not assignment) so the effect re-fires whenever
        // the box re-measures (orientation change, resize, etc).
        LaunchedEffect(cellWidth, slotGap, maxWidth) {
            // Inner params shadow the outer vars. Use the
            // outer \x27cellWidthField\x27 delegate (the var by
            // remember inside the function) by reading the
            // outer scope.
            outerCellWidth = cellWidth
            outerCellGap = slotGap
            outerViewportWidthDp = maxWidth - 36.dp
        }

        LaunchedEffect(cellWidth) {
            slotPitchPx = with(density) { (cellWidth + slotGap + AlgoTokens.space2).toPx() }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // ── 1. Floating Glassmorphic Phase Banner ──
        PhaseBanner(
            step = step,
            algorithmName = algorithmName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space1)
        )

        // ── 2. Array Cells & Visual Gimmicks Canvas ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
                .padding(horizontal = AlgoTokens.space4, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                // ── A. Insertion Sort: Elevated Key Inspection Header ──
                if (isInsertionSort && step.floatingElement != null) {
                    val (keyVal, origIdx) = step.floatingElement
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(PurpleSubtle)
                            .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(horizontal = 10.dp, vertical = AlgoTokens.space3),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(SecondaryPurple)
                                    .padding(horizontal = 5.dp, vertical = AlgoTokens.space1)
                            ) {
                                Text(
                                    text = "ELEVATED KEY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AlgoType.microSize
                                )
                            }
                            Text(
                                text = "Lifting arr[$origIdx] = $keyVal above array to find slot",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = AlgoType.microSize
                            )
                        }

                        // Floating Key Card with Glow & Arrow
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AlgoTokens.iconButtonXs)
                                    .shadow(4.dp, RoundedCornerShape(AlgoTokens.radiusXxs), spotColor = PurpleGlow)
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                    .background(SecondaryPurple)
                                    .border(AlgoTokens.strokeMedium, Color.White, RoundedCornerShape(AlgoTokens.radiusXxs)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = keyVal.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = AlgoType.bodySize
                                )
                            }
                            Icon(
                                imageVector = AlgoGlyphs.ArrowDown,
                                contentDescription = "Insert",
                                tint = PurpleGlow,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // ── B. Bubble Sort: Swapping / Connecting Arc Tag ──
                if (isBubbleSort && step.leftPointer != null && step.rightPointer != null) {
                    val isSwapping = step.phaseLabel == "SWAPPING"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AlgoTokens.space2),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Neutral glass readout:the state is signalled by the pink accent,
                        // accent only, never by flooding the whole bar red,
                        // (cells already carry the mutation colour).
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                .background(AlgoTokens.glassFill)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXxs))
                                .padding(horizontal = 10.dp, vertical = AlgoTokens.space1)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    if (isSwapping) {
                                        withStyle(SpanStyle(color = AlgoTokens.accentPink)) {
                                            append("\u26A1 ")
                                        }
                                        append("BUBBLE UP SWAP: arr[${step.leftPointer}] \u2194 arr[${step.rightPointer}]")
                                    } else {
                                        withStyle(SpanStyle(color = AlgoTokens.accentCyan)) {
                                            append("\uD83D\uDD0D ")
                                        }
                                        append("ADJACENT COMPARE: arr[${step.leftPointer}] vs arr[${step.rightPointer}]")
                                    }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                        }
                    }
                }

                // ── C. Selection Sort: Region Split Curtain Indicator ──
                if (isSelectionSort && step.sortedBoundary != null && step.sortedBoundary > 0 && step.sortedBoundary < step.array.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AlgoTokens.space2),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\u25C4 SORTED REGION (0..${step.sortedBoundary - 1})",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                        Text(
                            text = "UNSORTED CANDIDATES (${step.sortedBoundary}..${step.array.size - 1}) \u25BA",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }
                }

                // ── D. Merge Sort: Recursion Level & Sub-Blocks Info ──
                if (isMergeSort && step.mergeBlocks.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AlgoTokens.space2),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(PurpleSubtle)
                                    .padding(horizontal = AlgoTokens.space3, vertical = 1.5.dp)
                            ) {
                                Text(
                                    text = "DEPTH ${step.recursionDepth}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AlgoType.microSize
                                )
                            }
                            Text(
                                text = "Blocks: ${step.mergeBlocks.joinToString(" + ") { "[${it.first}..${it.last}]" }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = AlgoType.microSize
                            )
                        }
                    }
                }


                // ── Main Cells LazyRow with Bridge Overlay ──
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
                    lazyListState = lazyListState
                )

                // ── Animated Off-Screen Pointer Pop-Up Cell Indicators ──
                OffscreenPointerBanner(
                    step = step,
                    lazyListState = lazyListState
                )

                // ── Comparison / Step Expression Callout ──
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
                            // Evaluation readouts stay yellow (threshold semantics);
                            // only explicit mutation (SWAP:) lines take the pink accent.
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
