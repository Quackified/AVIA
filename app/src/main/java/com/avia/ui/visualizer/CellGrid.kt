package com.avia.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avia.data.AppSettings
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.AccentPinkGlow
import kotlinx.coroutines.delay
import kotlin.math.abs
import com.avia.ui.theme.AlgoType

/**
 * Horizontal scrolling grid of cell tiles with swap-travel animations, pointer
 * badges, and challenge-target halos. Each cell renders a top pointer pill
 * (PIVOT, L, R, target, etc.), the cell box (with the value digit), an index
 * label, and a bottom pointer pill (i, j, k, mid, etc.). Cells mid-flight
 * during a swap are translated / scaled via [slotFlight] inside the
 * `graphicsLayer` (no per-frame recomposition).
 *
 * The comparison bridge overlay is drawn **inside** this composable as a
 * `matchParentSize()` Canvas overlay sharing the same coordinate space as
 * the LazyRow. Cell center positions are tracked via `positionInWindow()`
 * on each cell box, then converted to Box-relative coordinates using the
 * wrapper Box's own `positionInWindow()`. This eliminates all manual
 * padding/curtain compensation.
 *
 * Sizing: [cellWidth] / [cellHeight] / [cellTextSize] are computed by the
 * public shell from the available width and the cell-scale preset; this
 * composable just consumes them.
 *
 * Selection sort boundary curtain (the green→yellow vertical divider) and
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
    isBubbleSort: Boolean,
    dimOutOfRange: Boolean,
    slotFlight: SlotFlightMap,
    challengePulseState: State<Float>,
    cellWidth: Dp,
    cellHeight: Dp,
    cellTextSize: TextUnit,
    cellGap: Dp = 4.dp,
    viewportWidthDp: Dp = 360.dp,
    cellScale: Float = 1f,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // ── Coordinate tracking for pointer bracket overlay ──
    var gridCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val cellCoordsMap = remember { mutableMapOf<Int, LayoutCoordinates>() }
    val cellCentersX = remember { mutableStateMapOf<Int, Float>() }
    val cellTopsY = remember { mutableStateMapOf<Int, Float>() }

    // Determine active pointer pair
    val bridgePair = remember(step.elementStates, step.swappedIndices, step.leftPointer, step.rightPointer) {
        when {
            step.swappedIndices != null -> step.swappedIndices
            step.leftPointer != null && step.rightPointer != null && step.leftPointer != step.rightPointer ->
                Pair(step.leftPointer, step.rightPointer)
            else -> {
                val comparing = step.elementStates.filter { it.value == ElementState.COMPARING }.keys.toList()
                if (comparing.size >= 2) {
                    Pair(comparing[0], comparing[1])
                } else if (comparing.size == 1) {
                    val pivot = step.elementStates.filter { it.value == ElementState.PIVOT }.keys.firstOrNull()
                    if (pivot != null && pivot != comparing[0]) Pair(comparing[0], pivot) else null
                } else null
            }
        }
    }

    val hasTopPill = remember(bridgePair, step.topPointers) {
        bridgePair != null && (
            step.topPointers.containsValue(bridgePair.first) ||
            step.topPointers.containsValue(bridgePair.second)
        )
    }

    val topAllotment = if (hasTopPill) 44.dp else 26.dp

    // Synchronized centering math across S / M / L scales:
    // Calculate exact horizontal padding so the row is centered whenever it fits,
    // and uses minimal edge padding when scrolling large arrays.
    val nodeCount = step.array.size.coerceAtLeast(1)
    val totalRowWidth = (cellWidth * nodeCount) + (cellGap * (nodeCount - 1).coerceAtLeast(0))
    val centeredSidePadding = ((viewportWidthDp - totalRowWidth) / 2f).coerceAtLeast(2.dp)

    // ── Completion Celebration Wave State & Haptics ──
    val haptic = LocalHapticFeedback.current
    var waveActiveIndex by remember { mutableIntStateOf(-1) }
    val celebrationPulse = remember { Animatable(0f) }
    val celebrationPulseState = remember { derivedStateOf { celebrationPulse.value } }

    val isFullySorted = remember(step.elementStates, step.array.size, step.phaseLabel) {
        step.phaseLabel == "SORTED" &&
            step.array.isNotEmpty() &&
            step.elementStates.size == step.array.size &&
            step.elementStates.values.all { it == ElementState.SORTED }
    }

    LaunchedEffect(isFullySorted, step.stepIndex) {
        if (isFullySorted) {
            waveActiveIndex = -1
            celebrationPulse.snapTo(0f)
            for (i in step.array.indices) {
                waveActiveIndex = i
                if (AppSettings.hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                delay(65L)
            }
            waveActiveIndex = -1
            if (AppSettings.hapticsEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            celebrationPulse.snapTo(1f)
            celebrationPulse.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        } else {
            waveActiveIndex = -1
            celebrationPulse.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                gridCoordinates = coords
                cellCoordsMap.forEach { (idx, cellCoords) ->
                    if (cellCoords.isAttached && coords.isAttached) {
                        val center = coords.localPositionOf(cellCoords, Offset(cellCoords.size.width / 2f, 0f))
                        val top = coords.localPositionOf(cellCoords, Offset.Zero)
                        cellCentersX[idx] = center.x
                        cellTopsY[idx] = top.y
                    }
                }
            }
    ) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = centeredSidePadding,
                end = centeredSidePadding,
                top = topAllotment,
                bottom = AlgoTokens.space1
            ),
            horizontalArrangement = Arrangement.spacedBy(cellGap, Alignment.CenterHorizontally),
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
                    isWaveActive = waveActiveIndex == index,
                    celebrationPulseState = celebrationPulseState,
                    onCellClick = onCellClick,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    cellTextSize = cellTextSize,
                    onCellBoxPositioned = { idx, cellCoords ->
                        cellCoordsMap[idx] = cellCoords
                        val grid = gridCoordinates
                        if (grid != null && grid.isAttached && cellCoords.isAttached) {
                            val center = grid.localPositionOf(cellCoords, Offset(cellCoords.size.width / 2f, 0f))
                            val top = grid.localPositionOf(cellCoords, Offset.Zero)
                            cellCentersX[idx] = center.x
                            cellTopsY[idx] = top.y
                        }
                    }
                )
            }
        }

        // ── ActivePairPointerBracket — drawn in the same Box coordinate space ──
        if (bridgePair != null) {
            val idxA = bridgePair.first
            val idxB = bridgePair.second

            val centerXA = cellCentersX[idxA]
            val centerXB = cellCentersX[idxB]
            val topYA = cellTopsY[idxA]
            val topYB = cellTopsY[idxB]
            val cellTopY = if (topYA != null && topYB != null) {
                minOf(topYA, topYB)
            } else topYA ?: topYB

            val transformA = slotFlight.transform(idxA)
            val transformB = slotFlight.transform(idxB)

            ActivePairPointerBracket(
                step = step,
                cellCenterXA = centerXA,
                cellCenterXB = centerXB,
                cellTopY = cellTopY ?: with(density) { (topAllotment + 22.dp).toPx() },
                hasTopPill = hasTopPill,
                cellScale = cellScale,
                transformA = transformA,
                transformB = transformB,
                modifier = Modifier.matchParentSize()
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
 *
 * [onCellBoxPositioned] fires with (index, windowCenterX, windowTopY) —
 * the cell box's center X and top Y in window coordinates. The host Box
 * subtracts its own window position to get Box-relative coordinates for
 * the bridge overlay Canvas.
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
    isWaveActive: Boolean,
    celebrationPulseState: State<Float>,
    onCellClick: ((Int) -> Unit)?,
    cellWidth: Dp,
    cellHeight: Dp,
    cellTextSize: TextUnit,
    onCellBoxPositioned: (Int, LayoutCoordinates) -> Unit = { _, _ -> }
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

    // Tactile evaluation pop (bouncy 1.1x–1.2x) for cells being scanned.
    // Held as State — read in the graphicsLayer below, not in composition.
    val isEvaluated = state == ElementState.COMPARING ||
        state == ElementState.ACTIVE ||
        state == ElementState.FOUND
    val evalScaleState = animateFloatAsState(
        targetValue = if (isEvaluated) 1.12f else 1f,
        animationSpec = AlgoTokens.evalSpring,
        label = "evalScale_$index"
    )

    // Completion cascade wave pop scale
    val waveScaleState = animateFloatAsState(
        targetValue = if (isWaveActive) 1.15f else 1f,
        animationSpec = tween(120),
        label = "waveScale_$index"
    )

    // Bubble Sort / Swap Scale & Glow Gimmick
    val isSwappingCell = swappedIndices?.let { it.first == index || it.second == index } ?: false

    // Selection Sort boundary curtain divider check
    val isAtSortedBoundary = isSelectionSort && sortedBoundary == index && index > 0

    val cellDensity = LocalDensity.current

    Row(verticalAlignment = Alignment.Top) {
        if (isAtSortedBoundary) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Vertical space matching TopPointerBadge (18dp) + space2 (4dp)
                Spacer(modifier = Modifier.height(18.dp + AlgoTokens.space2))
                Box(
                    modifier = Modifier
                        .height(cellHeight)
                        .width(2.dp)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(AccentGreen)
                )
            }
            Spacer(modifier = Modifier.width(AlgoTokens.space3))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            modifier = Modifier
                .width(cellWidth)
                .clickable(enabled = onCellClick != null) {
                    onCellClick?.invoke(index)
                }
        ) {
            // ── Top Pointer Badge ──
            TopPointerBadge(entry = topPointerEntry)

            // ── Cell Box ──
            // Contrast rule: the semantic accent lives on the BORDER and
            // FILL TINT only — the element VALUE digit must always render
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
                // stays WHITE — a red digit on a red tint is unreadable.
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
                    .graphicsLayer {
                        // All per-frame animated values are read HERE, in the
                        // render phase — animation frames only re-record this
                        // layer and never recompose the cell subtree.
                        // Only the cell tile travels during swap; pointers and
                        // index labels remain stationary at their slot positions.
                        val ox = slotFlight.transform(index).offsetX.value
                        val oy = slotFlight.transform(index).offsetY.value
                        val sc = slotFlight.transform(index).scale.value
                        val ev = evalScaleState.value
                        val ws = waveScaleState.value
                        val cp = celebrationPulseState.value
                        val celebrationScale = 1f + cp * 0.05f
                        translationX = ox
                        translationY = oy
                        scaleX = sc * ev * ws * celebrationScale
                        scaleY = sc * ev * ws * celebrationScale
                        alpha = cellAlphaState.value
                        shadowElevation =
                            if (sc > 1.01f || abs(oy) > 0.5f || ws > 1.05f) {
                                AlgoTokens.elevationTraveling.toPx()
                            } else {
                                0f
                            }
                        shape = RoundedCornerShape(AlgoTokens.radiusXxs)
                        cameraDistance = 12f * cellDensity.density
                    }
                    .onGloballyPositioned { coords ->
                        onCellBoxPositioned(index, coords)
                    }
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
                        // Completion wave ripple glow
                        if (isWaveActive) {
                            drawCellGlow(
                                accent = AccentGreen,
                                intensity = 0.85f,
                                cornerRadius = corner
                            )
                        }
                        // Completion celebration synchronized shimmer
                        if (celebrationPulseState.value > 0.01f) {
                            drawCellGlow(
                                accent = AccentGreen,
                                intensity = celebrationPulseState.value * 0.5f,
                                cornerRadius = corner
                            )
                        }
                        // Cross-feature mirror glow: only on SWAPPING or FOUND semantic events with damped intensity
                        if ((state == ElementState.SWAPPING || state == ElementState.FOUND) && syncPulse.value > 0.01f) {
                            drawCellGlow(
                                accent = animatedBorder,
                                intensity = syncPulse.value * 0.4f,
                                cornerRadius = corner
                            )
                        }
                    }
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(animatedBg)
                    .border(
                        width = if (isSelectedForChallenge || isChallengeTarget || isWaveActive ||
                            state != ElementState.IDLE || isSwappingCell
                        ) AlgoTokens.strokeActive else AlgoTokens.strokeThin,
                        color = when {
                            isSelectedForChallenge -> AlgoTokens.accentCyan
                            isChallengeTarget -> AlgoTokens.accentYellow
                            isWaveActive -> AccentGreen
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

            // ── Index Label ──
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    isSelectedForChallenge -> AlgoTokens.accentCyan
                    isChallengeTarget -> AlgoTokens.accentYellow
                    else -> TextMuted
                },
                fontWeight = if (isSelectedForChallenge) FontWeight.Bold else FontWeight.Normal,
                fontSize = AlgoType.microSize
            )

            // ── Bottom Pointer Badge ──
            BottomPointerBadge(entry = bottomPointerEntry)
        }
    }
}
