package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.AccentPinkGlow
import kotlin.math.abs

/**
 * Horizontal scrolling grid of cell tiles with swap-travel animations, pointer
 * badges, and challenge-target halos. Each cell renders a top pointer pill
 * (PIVOT, L, R, target, etc.), the cell box (with the value digit), an index
 * label, and a bottom pointer pill (i, j, k, mid, etc.). Cells mid-flight
 * during a swap are translated / scaled via [slotFlight] inside the
 * `graphicsLayer` (no per-frame recomposition).
 *
 * Sizing: [cellWidth] / [cellHeight] / [cellTextSize] are computed by the
 * public shell from the available width and the cell-scale preset; this
 * composable just consumes them.
 *
 * Selection sort boundary curtain (the greenâ†’yellow vertical divider) and
 * challenge-target halos (yellow pulse) live here because they are tightly
 * coupled to the cell paint.
 */
@Composable
fun CellGrid(
    step: VisualizerStep,
    selectedCellIndices: Set<Int>,
    challengeTargetIndices: Set<Int>,
    syncPulse: State<Float>,
    onCellClick: ((Int) -> Unit)?,
    isSelectionSort: Boolean,
    dimOutOfRange: Boolean,
    slotFlight: SlotFlightMap,
    challengePulseState: State<Float>,
    cellWidth: Dp,
    cellHeight: Dp,
    cellTextSize: TextUnit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = AlgoTokens.space1),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(step.array, key = { index, _ -> "cell_$index" }) { index, value ->
            CellItem(
                index = index,
                value = value,
                state = step.elementStates[index] ?: ElementState.IDLE,
                isSelectedForChallenge = selectedCellIndices.contains(index),
                isChallengeTarget = index in challengeTargetIndices,
                isInActiveRange = step.activeRange == null || index in step.activeRange,
                isSelectionSort = isSelectionSort,
                sortedBoundary = step.sortedBoundary,
                swappedIndices = step.swappedIndices,
                topPointerEntry = step.topPointers.entries.find { it.value == index },
                bottomPointerEntry = step.bottomPointers.entries.find { it.value == index },
                dimOutOfRange = dimOutOfRange,
                slotFlight = slotFlight,
                challengePulseState = challengePulseState,
                syncPulse = syncPulse,
                onCellClick = onCellClick,
                cellWidth = cellWidth,
                cellHeight = cellHeight,
                cellTextSize = cellTextSize
            )
        }
    }
}

/**
 * Single cell tile: top pointer badge, cell box with value digit + border
 * + optional challenge-target halo, index label, bottom pointer badge.
 * The optional left-side selection-sort boundary curtain is rendered by the
 * caller's outer [Row] in [CellGrid] when the cell is at the sorted
 * boundary index.
 */
@Composable
private fun CellItem(
    index: Int,
    value: Int,
    state: ElementState,
    isSelectedForChallenge: Boolean,
    isChallengeTarget: Boolean,
    isInActiveRange: Boolean,
    isSelectionSort: Boolean,
    sortedBoundary: Int?,
    swappedIndices: Pair<Int, Int>?,
    topPointerEntry: Map.Entry<String, Int>?,
    bottomPointerEntry: Map.Entry<String, Int>?,
    dimOutOfRange: Boolean,
    slotFlight: SlotFlightMap,
    challengePulseState: State<Float>,
    syncPulse: State<Float>,
    onCellClick: ((Int) -> Unit)?,
    cellWidth: Dp,
    cellHeight: Dp,
    cellTextSize: TextUnit
) {
    // Divide-and-conquer active-range dimming (Binary Search, Quick Sort,
    // Merge Sort during divide). The spec opt-in replaces a string match
    // on `algorithmName.contains("quick")` so all three algorithms share
    // one check.
    val cellAlphaState = animateFloatAsState(
        targetValue = if (dimOutOfRange && !isInActiveRange) 0.35f else 1f,
        animationSpec = tween(150),
        label = "cellAlpha_$index"
    )

    // Tactile evaluation pop (bouncy 1.1xâ€“1.2x) for cells being scanned.
    // Held as State â€” read in the graphicsLayer below, not in composition.
    val isEvaluated = state == ElementState.COMPARING ||
        state == ElementState.ACTIVE ||
        state == ElementState.FOUND
    val evalScaleState = animateFloatAsState(
        targetValue = if (isEvaluated) 1.12f else 1f,
        animationSpec = AlgoTokens.evalSpring,
        label = "evalScale_$index"
    )

    // Bubble Sort / Swap Scale & Glow Gimmick
    val isSwappingCell = swappedIndices?.let { it.first == index || it.second == index } ?: false

    // Selection Sort boundary curtain divider check
    val isAtSortedBoundary = isSelectionSort && sortedBoundary == index && index > 0

    val cellDensity = LocalDensity.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isAtSortedBoundary) {
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .width(2.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(AccentGreen, AccentYellow)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(AlgoTokens.space3))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            modifier = Modifier
                .padding(horizontal = AlgoTokens.space1)
                .graphicsLayer {
                    // All per-frame animated values are read HERE, in the
                    // render phase â€” animation frames only re-record this
                    // layer and never recompose the cell subtree.
                    val ox = slotFlight.transform(index).offsetX.value
                    val oy = slotFlight.transform(index).offsetY.value
                    val sc = slotFlight.transform(index).scale.value
                    val ev = evalScaleState.value
                    translationX = ox
                    translationY = oy
                    scaleX = sc * ev
                    scaleY = sc * ev
                    alpha = cellAlphaState.value
                    shadowElevation =
                        if (sc > 1.01f || abs(oy) > 0.5f) {
                            AlgoTokens.elevationTraveling.toPx()
                        } else {
                            0f
                        }
                    shape = RoundedCornerShape(AlgoTokens.radiusXxs)
                    cameraDistance = 12f * cellDensity.density
                }
                .clickable(enabled = onCellClick != null) {
                    onCellClick?.invoke(index)
                }
        ) {
            // â”€â”€ Top Pointer Badge â”€â”€
            TopPointerBadge(entry = topPointerEntry)

            // â”€â”€ Cell Box â”€â”€
            // Contrast rule: the semantic accent lives on the BORDER and
            // FILL TINT only â€” the element VALUE digit must always render
            // in a high-contrast colour (white / bright accent). Never map
            // a low-luminance accent (e.g. pink #FF3366) to the value text,
            // or the digit vanishes into its own tinted fill.
            val (targetBorder, targetBg, targetText) = when {
                isSelectedForChallenge -> Triple(
                    AlgoTokens.accentCyan, AlgoTokens.cyanFill, AlgoTokens.accentCyan
                )
                isChallengeTarget -> challengeTargetColorTriple()
                state == ElementState.PIVOT -> Triple(
                    AlgoTokens.accentYellow, AlgoTokens.yellowFill, Color.White
                )
                // Compare flash: pink frame + pink tint, but the value
                // stays WHITE â€” a red digit on a red tint is unreadable.
                state == ElementState.COMPARING -> Triple(
                    AlgoTokens.accentPink, AlgoTokens.pinkFill, Color.White
                )
                state == ElementState.SWAPPING -> Triple(
                    AlgoTokens.accentPink, AccentPinkGlow, Color.White
                )
                state == ElementState.ACTIVE ||
                    state == ElementState.VISITED ||
                    state == ElementState.FOUND -> Triple(
                    AlgoTokens.accentCyan, AlgoTokens.cyanFill, AlgoTokens.accentCyan
                )
                state == ElementState.SORTED -> Triple(
                    AlgoTokens.accentGreen, AlgoTokens.greenFill, AlgoTokens.accentGreen
                )
                state == ElementState.TARGET -> Triple(
                    AlgoTokens.accentPurple, AlgoTokens.purpleFill, Color.White
                )
                else -> Triple(
                    AlgoTokens.strokeBorderMedium, AlgoTokens.glassFill, TextPrimary
                )
            }

            val animatedBorder by animateColorAsState(targetBorder, tween(120), label = "cellBorder_$index")
            val animatedBg by animateColorAsState(targetBg, tween(120), label = "cellBg_$index")
            val animatedText by animateColorAsState(targetText, tween(120), label = "cellText_$index")

            Box(
                modifier = Modifier
                    .size(width = cellWidth, height = cellHeight)
                    .drawBehind {
                        val corner = CornerRadius(AlgoTokens.radiusXxs.toPx())
                        // Challenge Mode glowing target ring
                        if (isChallengeTarget) {
                            drawCellGlow(
                                accent = AlgoTokens.accentYellow,
                                intensity = challengePulseState.value,
                                cornerRadius = corner
                            )
                        }
                        // Cross-feature mirror glow: cells flash in
                        // sync with the highlighted code trace line
                        if (state != ElementState.IDLE && syncPulse.value > 0.01f) {
                            drawCellGlow(
                                accent = animatedBorder,
                                intensity = syncPulse.value,
                                cornerRadius = corner
                            )
                        }
                    }
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(animatedBg)
                    .border(
                        width = if (isSelectedForChallenge || isChallengeTarget ||
                            state != ElementState.IDLE || isSwappingCell
                        ) AlgoTokens.strokeActive else AlgoTokens.strokeThin,
                        color = when {
                            isSelectedForChallenge -> AlgoTokens.accentCyan
                            isChallengeTarget -> AlgoTokens.accentYellow
                            else -> animatedBorder
                        },
                        shape = RoundedCornerShape(AlgoTokens.radiusXxs)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = animatedText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = cellTextSize
                )
            }

            // â”€â”€ Index Label â”€â”€
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    isSelectedForChallenge -> AlgoTokens.accentCyan
                    isChallengeTarget -> AlgoTokens.accentYellow
                    else -> TextMuted
                },
                fontWeight = if (isSelectedForChallenge) FontWeight.Bold else FontWeight.Normal,
                fontSize = 8.5.sp
            )

            // â”€â”€ Bottom Pointer Badge â”€â”€
            BottomPointerBadge(entry = bottomPointerEntry)
        }
    }
}
