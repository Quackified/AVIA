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
 * Thin public shell \u25C4€” composes the following private modules in the same package:
 *  - [CellGrid]         \u25C4€” LazyRow + per-cell paint (badges, box, index label)
 *  - [CellPointerBadges] \u25C4€” [TopPointerBadge] / [BottomPointerBadge]
 *  - [OffscreenPointerBanner] \u25C4€” animated off-screen pop-up pills
 *  - [CellGlowPainter]   \u25C4€” three-band outer stroke glow
 *  - [ChallengeGlowTargets] \u25C4€” shared pulse state for Challenge Mode halos
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

    // \u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€
    // Swap Travel Animation: cells "pop up", then shift and settle into
    // their respective new slots with an elevated shadow while in flight.
    // \u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€\u25C4”€
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
    LaunchedEffect(step.stepIndex, targetIndex) {
        if (targetIndex != null && step.array.isNotEmpty()) {
            val safeIndex = targetIndex.coerceIn(0, step.array.size - 1)
            if (safeIndex !in visibleItemIndices) {
                lazyListState.animateScrollToItem(safeIndex)
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
        // `cellScale` is the user's S/M/L header preset (0.6x..1.4x).
        val cellWidth = (((availableWidth / nodeCount) * cellScale).coerceAtLeast(18.dp)).coerceAtMost(44.dp)
        val cellHeight = cellWidth * 1.12f
        val cellTextSize = (cellWidth.value * 0.36f).coerceIn(10f, 16f).sp
        val slotGap = AlgoTokens.space3

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
        // \u25C4”€\u25C4”€ 1. Floating Glassmorphic Phase Banner \u25C4”€\u25C4”€
        PhaseBanner(
            step = step,
            algorithmName = algorithmName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space1)
        )

        // \u25C4”€\u25C4”€ 2. Array Cells & Visual Gimmicks Canvas \u25C4”€\u25C4”€
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
                // \u25C4”€\u25C4”€ A. Insertion Sort: Elevated Key Inspection Header \u25C4”€\u25C4”€
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
                                    fontSize = 7.5.sp
                                )
                            }
                            Text(
                                text = "Lifting arr[$origIdx] = $keyVal above array to find slot",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 8.5.sp
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
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Insert",
                                tint = PurpleGlow,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // \u25C4”€\u25C4”€ B. Bubble Sort: Swapping / Connecting Arc Tag \u25C4”€\u25C4”€
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
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                // \u25C4”€\u25C4”€ C. Selection Sort: Region Split Curtain Indicator \u25C4”€\u25C4”€
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
                            fontSize = 7.5.sp
                        )
                        Text(
                            text = "UNSORTED CANDIDATES (${step.sortedBoundary}..${step.array.size - 1}) \u25BA",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.5.sp
                        )
                    }
                }

                // \u25C4”€\u25C4”€ D. Merge Sort: Recursion Level & Sub-Blocks Info \u25C4”€\u25C4”€
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
                                    fontSize = 7.5.sp
                                )
                            }
                            Text(
                                text = "Blocks: ${step.mergeBlocks.joinToString(" + ") { "[${it.first}..${it.last}]" }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                // \u25C4”€\u25C4”€ Main Cells LazyRow with Smooth Animations \u25C4”€\u25C4”€
                CellGrid(
                    step = step,
                    selectedCellIndices = selectedCellIndices,
                    challengeTargetIndices = challengeTargetIndices,
                    syncPulse = syncPulse,
                    onCellClick = onCellClick,
                    isSelectionSort = isSelectionSort,
                    dimOutOfRange = dimOutOfRange,
                    slotFlight = slotFlight,
                    challengePulseState = challengePulseState,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    cellTextSize = cellTextSize,
                    lazyListState = lazyListState
                )

                // \u25C4”€\u25C4”€ Animated Off-Screen Pointer Pop-Up Cell Indicators \u25C4”€\u25C4”€
                OffscreenPointerBanner(
                    step = step,
                    lazyListState = lazyListState
                )

                // \u25C4”€\u25C4”€ Comparison / Step Expression Callout \u25C4”€\u25C4”€
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
                            style = MaterialTheme.typography.labelSmall,
                            // Evaluation readouts stay yellow (threshold semantics);
                            // only explicit mutation (SWAP:) lines take the pink accent.
                            color = when {
                                step.comparisonExpr == null -> AlgoTokens.accentCyan
                                expr.startsWith("SWAP", ignoreCase = true) -> AlgoTokens.accentPink
                                else -> AccentYellow
                            },
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 8.5.sp
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
