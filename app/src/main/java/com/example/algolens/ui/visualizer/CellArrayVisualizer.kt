package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CardBackgroundHover
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import kotlinx.coroutines.launch
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/**
 * Data structure describing an active off-screen pointer target.
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
 */
@Composable
fun CellArrayVisualizer(
    step: VisualizerStep,
    algorithmName: String = "Bubble Sort",
    selectedCellIndices: Set<Int> = emptySet(),
    challengeTargetIndices: Set<Int> = emptySet(),
    syncPulse: State<Float> = mutableStateOf(0f),
    onCellClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val auxLazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // ─────────────────────────────────────────────────────────────────────
    // Swap Travel Animation: cells "pop up", then shift and settle into
    // their respective new slots with an elevated shadow while in flight.
    // ─────────────────────────────────────────────────────────────────────
    val density = LocalDensity.current

    // Slot pitch (cell width + gap + item padding) in px. Baseline here;
    // refined responsively by BoxWithConstraints further below.
    var slotPitchPx by remember { mutableFloatStateOf(with(density) { 42.dp.toPx() }) }

    // Snapshot of the previous step's array used to diff value permutations.
    var previousArray by remember { mutableStateOf(step.array) }

    // Stable element identities (each element's origin slot). Keying the
    // LazyRow by these lets Compose reuse cell state for elements in motion
    // instead of treating a swapped element as a brand-new item.
    var elementIds by remember(step.array.size) {
        mutableStateOf(List(step.array.size) { it })
    }

    // Per-slot flight animations (3-phase swap motion):
    //   1. LIFT  — cell pops straight UP off its slot.
    //   2. SHIFT — glides horizontally toward the destination slot, mid-air.
    //   3. LAND  — descends into the new slot and locks with a small settle bounce.
    val travelOffsetsX = remember { mutableMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val travelOffsetsY = remember { mutableMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val travelScales = remember { mutableMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val liftHeightPx = with(density) { 16.dp.toPx() }

    // ── Composition-time flight plan ──
    // Step data arrives with the swap ALREADY applied. To avoid a one-frame
    // flash of the final arrangement, the diff and the displaced start poses
    // are computed synchronously during composition: the very first frame
    // renders the two cells at their PRE-swap locations, and the
    // LaunchedEffect below only plays the lift/shift/land choreography.
    var lastPlannedStep by remember { mutableStateOf(-1) }
    // The plan lives in remembered STATE (not a local val): the composition
    // pass that computes it also writes other state, which restarts that
    // pass — and the restarted pass must still hand the SAME plan to the
    // LaunchedEffect below, otherwise cells would sit displaced with no
    // flight ever animating them home.
    val flightPlanState = remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    if (lastPlannedStep != step.stepIndex) {
        lastPlannedStep = step.stepIndex
        val prev = previousArray
        val curr = step.array

        val movedPairs: List<Pair<Int, Int>> = when {
            step.swappedIndices != null &&
                step.swappedIndices.first in curr.indices &&
                step.swappedIndices.second in curr.indices -> {
                listOf(step.swappedIndices.first to step.swappedIndices.second)
            }
            prev.size == curr.size -> {
                val changed = curr.indices.filter { prev[it] != curr[it] }
                // A swap means exactly two slots changed and their values crossed.
                if (changed.size == 2) {
                    val (a, b) = changed
                    if (prev[a] == curr[b] && prev[b] == curr[a]) listOf(a to b) else emptyList()
                } else {
                    emptyList()
                }
            }
            else -> emptyList()
        }

        // Sync stable element identities with the new permutation (stable keys).
        if (prev.size == curr.size) {
            val prevIds = elementIds
            val newIds = MutableList(curr.size) { it }
            val used = BooleanArray(prev.size)
            curr.forEachIndexed { i, v ->
                val j = prev.indices.firstOrNull { !used[it] && prev[it] == v }
                if (j != null) {
                    newIds[i] = prevIds[j]
                    used[j] = true
                }
            }
            elementIds = newIds
        }

        // Cancel-safe reset: any slot NOT traveling in this step gets a fresh
        // rest Animatable, so flights cancelled mid-air by fast playback or
        // scrubbing can never leave a cell stranded lifted/shifted/enlarged.
        val planSlots = movedPairs.flatMap { (a, b) -> listOf(a, b) }.toSet()
        travelOffsetsX.keys.toList().forEach { slot ->
            if (slot !in planSlots) {
                travelOffsetsX[slot] = Animatable(0f)
                travelOffsetsY[slot] = Animatable(0f)
                travelScales[slot] = Animatable(1f)
            }
        }
        previousArray = curr

        // Place traveling cells at their PRE-swap locations. Post-swap, the
        // element at slot `from` came from `to` and vice versa — the two
        // cells therefore start displaced toward each other's old slots and
        // CROSS through each other into their new homes.
        movedPairs.onEach { (from, to) ->
            val distancePx = (to - from) * slotPitchPx
            travelOffsetsX[from] = Animatable(distancePx)
            travelOffsetsY[from] = Animatable(0f)
            travelScales[from] = Animatable(1f)
            travelOffsetsX[to] = Animatable(-distancePx)
            travelOffsetsY[to] = Animatable(0f)
            travelScales[to] = Animatable(1f)
        }
        flightPlanState.value = movedPairs
    }

    LaunchedEffect(step.stepIndex) {
        // Defense-in-depth reset: any slot NOT traveling in this step is
        // snapped back to rest before new flights launch. Covers every
        // path where a previous flight was cut short (fast playback,
        // scrubbing, new input) so no cell can remain lifted, shifted
        // or enlarged.
        val planSlots = flightPlanState.value.flatMap { (a, b) -> listOf(a, b) }.toSet()
        travelOffsetsX.forEach { (slot, anim) -> if (slot !in planSlots) anim.snapTo(0f) }
        travelOffsetsY.forEach { (slot, anim) -> if (slot !in planSlots) anim.snapTo(0f) }
        travelScales.forEach { (slot, anim) -> if (slot !in planSlots) anim.snapTo(1f) }

        // Play the choreography for the plan built above (cells are already
        // rendered displaced at their old slots — animate them home). The
        // flight runs under NonCancellable: a newer step replaces these
        // Animatable objects in the maps, so letting the coroutine finish on
        // its detached objects is harmless — while cancelling it mid-air is
        // exactly what used to strand cells in a lifted/shifted pose.
        flightPlanState.value.forEach { (from, to) ->
            listOf(from, to).forEach { slot ->
                val offsetX = travelOffsetsX[slot] ?: return@forEach
                val offsetY = travelOffsetsY[slot] ?: return@forEach
                val scale = travelScales[slot] ?: return@forEach
                launch {
                    withContext(NonCancellable) {
                        // 1) LIFT — pop straight up off the slot (scale swells in parallel).
                        launch {
                            scale.animateTo(
                                targetValue = 1.18f,
                                animationSpec = spring(
                                    dampingRatio = 0.5f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                        offsetY.animateTo(
                            targetValue = -liftHeightPx,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )

                        // 2) SHIFT — horizontal glide into the destination slot, mid-air.
                        offsetX.animateTo(0f, AlgoTokens.cellTravelSpring)

                        // 3) LAND & LOCK — descend into the slot with a slight settle bounce.
                        offsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = 0.55f,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        scale.animateTo(1f, AlgoTokens.pointerSpring)

                        // Guarantee an exact landing pose, even if a spring
                        // is interrupted by the Animatable being replaced.
                        offsetX.snapTo(0f)
                        offsetY.snapTo(0f)
                        scale.snapTo(1f)
                    }
                }
            }
        }
    }

    // Challenge Mode halo pulse (shared infinite transition for all target
    // cells). Held as State and read inside draw lambdas only, so the
    // infinite animation never triggers per-frame recomposition.
    val challengePulseState = rememberInfiniteTransition(label = "challengeHalo").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "challengeHaloAlpha"
    )

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

    // Compute off-screen pointers to left and right
    val offscreenPointers by remember(step, visibleItemIndices) {
        derivedStateOf {
            if (visibleItemIndices.isEmpty() || step.array.isEmpty()) {
                emptyList()
            } else {
                val minVisible = visibleItemIndices.minOrNull() ?: 0
                val maxVisible = visibleItemIndices.maxOrNull() ?: (step.array.size - 1)
                val list = mutableListOf<OffscreenPointerTarget>()

                // 1. Check Top Pointers (pivot, target, L, R, min, key)
                step.topPointers.forEach { (label, idx) ->
                    if (idx in step.array.indices && idx !in visibleItemIndices) {
                        val isPivot = label.equals("pivot", ignoreCase = true)
                        val isMin = label.equals("min", ignoreCase = true)
                        val badgeBg = when {
                            isPivot -> AlgoTokens.accentYellow
                            isMin -> AccentYellow
                            else -> SecondaryPurple
                        }
                        val badgeText = when {
                            isPivot || isMin -> DarkBackground
                            else -> Color.White
                        }
                        list.add(
                            OffscreenPointerTarget(
                                index = idx,
                                value = step.array[idx],
                                label = label,
                                state = step.elementStates[idx] ?: ElementState.IDLE,
                                badgeBg = badgeBg,
                                badgeTextColor = badgeText,
                                isRight = idx > maxVisible
                            )
                        )
                    }
                }

                // 2. Check Bottom Pointers (i, j, mid, low, high, k)
                step.bottomPointers.forEach { (label, idx) ->
                    if (idx in step.array.indices && idx !in visibleItemIndices && list.none { it.index == idx }) {
                        val badgeBg = when (label.lowercase()) {
                            "i", "low", "l" -> PrimaryCyan
                            "j", "mid" -> AccentYellow
                            "high", "r" -> SecondaryPurple
                            "k" -> AccentGreen
                            else -> PrimaryCyan
                        }
                        val badgeText = when (label.lowercase()) {
                            "high", "r" -> Color.White
                            else -> DarkBackground
                        }
                        list.add(
                            OffscreenPointerTarget(
                                index = idx,
                                value = step.array[idx],
                                label = label,
                                state = step.elementStates[idx] ?: ElementState.IDLE,
                                badgeBg = badgeBg,
                                badgeTextColor = badgeText,
                                isRight = idx > maxVisible
                            )
                        )
                    }
                }

                // 3. Check active states (COMPARING, SWAPPING, PIVOT, TARGET)
                step.elementStates.forEach { (idx, st) ->
                    if (st != ElementState.IDLE && idx in step.array.indices && idx !in visibleItemIndices && list.none { it.index == idx }) {
                        val (label, bg, fg) = when (st) {
                            ElementState.PIVOT -> Triple("pivot", AccentPink, DarkBackground)
                            ElementState.COMPARING -> Triple("compare", AccentYellow, DarkBackground)
                            ElementState.SWAPPING -> Triple("swap", AccentRed, Color.White)
                            ElementState.FOUND -> Triple("found", PrimaryCyan, DarkBackground)
                            ElementState.TARGET -> Triple("target", SecondaryPurple, Color.White)
                            else -> Triple("active", PrimaryCyan, DarkBackground)
                        }
                        list.add(
                            OffscreenPointerTarget(
                                index = idx,
                                value = step.array[idx],
                                label = label,
                                state = st,
                                badgeBg = bg,
                                badgeTextColor = fg,
                                isRight = idx > maxVisible
                            )
                        )
                    }
                }

                list
            }
        }
    }

    val leftOffscreen = offscreenPointers.filter { !it.isRight }
    val rightOffscreen = offscreenPointers.filter { it.isRight }

    val isBubbleSort = algorithmName.contains("bubble", ignoreCase = true)
    val isSelectionSort = algorithmName.contains("selection", ignoreCase = true)
    val isInsertionSort = algorithmName.contains("insertion", ignoreCase = true)
    val isQuickSort = algorithmName.contains("quick", ignoreCase = true)
    val isMergeSort = algorithmName.contains("merge", ignoreCase = true)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Responsive tactile-canvas sizing: card dimensions, type scale and
        // slot pitch derive from the available width and total node count.
        val nodeCount = step.array.size.coerceAtLeast(1)
        val availableWidth = maxWidth - 36.dp // canvas well padding + gutters
        val cellWidth = ((availableWidth / nodeCount).coerceAtLeast(26.dp)).coerceAtMost(44.dp)
        val cellHeight = cellWidth * 1.12f
        val cellTextSize = (cellWidth.value * 0.36f).coerceIn(10f, 16f).sp
        val slotGap = 6.dp

        LaunchedEffect(cellWidth) {
            slotPitchPx = with(density) { (cellWidth + slotGap + 4.dp).toPx() }
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
                .padding(horizontal = 2.dp)
        )

        // ── 2. Array Cells & Visual Gimmicks Canvas ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackgroundElevated)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ── A. Insertion Sort: Elevated Key Inspection Header ──
                if (isInsertionSort && step.floatingElement != null) {
                    val (keyVal, origIdx) = step.floatingElement
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PurpleSubtle)
                            .border(1.dp, SecondaryPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SecondaryPurple)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .shadow(4.dp, RoundedCornerShape(6.dp), spotColor = PurpleGlow)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SecondaryPurple)
                                    .border(1.5.dp, Color.White, RoundedCornerShape(6.dp)),
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

                // ── B. Bubble Sort: Swapping / Connecting Arc Tag ──
                if (isBubbleSort && step.leftPointer != null && step.rightPointer != null) {
                    val isSwapping = step.phaseLabel == "SWAPPING"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Neutral glass readout — the state is signalled by the pink
                        // ⚡ accent only, never by flooding the whole bar red
                        // (cells already carry the mutation colour).
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AlgoTokens.glassFill)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    if (isSwapping) {
                                        withStyle(SpanStyle(color = AlgoTokens.accentPink)) {
                                            append("⚡ ")
                                        }
                                        append("BUBBLE UP SWAP: arr[${step.leftPointer}] ⇄ arr[${step.rightPointer}]")
                                    } else {
                                        withStyle(SpanStyle(color = AlgoTokens.accentCyan)) {
                                            append("🔍 ")
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

                // ── C. Selection Sort: Region Split Curtain Indicator ──
                if (isSelectionSort && step.sortedBoundary != null && step.sortedBoundary > 0 && step.sortedBoundary < step.array.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◄ SORTED REGION (0..${step.sortedBoundary - 1})",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.5.sp
                        )
                        Text(
                            text = "UNSORTED CANDIDATES (${step.sortedBoundary}..${step.array.size - 1}) ►",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.5.sp
                        )
                    }
                }

                // ── D. Merge Sort: Recursion Level & Sub-Blocks Info ──
                if (isMergeSort && step.mergeBlocks.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PurpleSubtle)
                                    .padding(horizontal = 6.dp, vertical = 1.5.dp)
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
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                // ── Main Cells LazyRow with Smooth Animations ──
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(step.array, key = { index, _ -> elementIds.getOrElse(index) { index } }) { index, value ->
                        val state = step.elementStates[index] ?: ElementState.IDLE
                        val isSelectedForChallenge = selectedCellIndices.contains(index)
                        val isChallengeTarget = index in challengeTargetIndices

                        // Quick Sort Partition Dimming (Dim elements outside low..high)
                        val isInActiveRange = step.activeRange == null || index in step.activeRange
                        val cellAlphaState = animateFloatAsState(
                            targetValue = if (isQuickSort && !isInActiveRange) 0.35f else 1f,
                            animationSpec = tween(150),
                            label = "cellAlpha_$index"
                        )

                        // Top pointer (L, R, pivot, target, min, key)
                        val topPointerEntry = step.topPointers.entries.find { it.value == index }
                        // Bottom pointer (i, j, mid, low, high, k)
                        val bottomPointerEntry = step.bottomPointers.entries.find { it.value == index }

                        // Bubble Sort / Swap Scale & Glow Gimmick
                        val isSwappingCell = step.swappedIndices?.let { it.first == index || it.second == index } ?: false

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

                        // Selection Sort boundary curtain divider check
                        val isAtSortedBoundary = isSelectionSort && step.sortedBoundary == index && index > 0

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
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .graphicsLayer {
                                        // All per-frame animated values are read HERE, in the
                                        // render phase — animation frames only re-record this
                                        // layer and never recompose the cell subtree.
                                        val ox = travelOffsetsX[index]?.value ?: 0f
                                        val oy = travelOffsetsY[index]?.value ?: 0f
                                        val sc = travelScales[index]?.value ?: 1f
                                        val ev = evalScaleState.value
                                        translationX = ox
                                        translationY = oy
                                        scaleX = sc * ev
                                        scaleY = sc * ev
                                        alpha = cellAlphaState.value
                                        shadowElevation =
                                            if (sc > 1.01f || kotlin.math.abs(oy) > 0.5f) {
                                                AlgoTokens.elevationTraveling.toPx()
                                            } else {
                                                0f
                                            }
                                        shape = RoundedCornerShape(6.dp)
                                        cameraDistance = 12f * density.density
                                    }
                                    .clickable(enabled = onCellClick != null) {
                                        onCellClick?.invoke(index)
                                    }
                            ) {
                                // ── Top Pointer Badge ──
                                Box(
                                    modifier = Modifier.height(18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val topEntry = topPointerEntry
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = topEntry != null,
                                        enter = scaleIn(AlgoTokens.evalSpring) + fadeIn(tween(90)),
                                        exit = scaleOut(tween(110)) + fadeOut(tween(110))
                                    ) {
                                        if (topEntry != null) {
                                        val (label, _) = topEntry
                                        val isPivot = label.equals("pivot", ignoreCase = true)
                                        val isMin = label.equals("min", ignoreCase = true)
                                        val (badgeBg, badgeText) = when {
                                            isPivot -> Pair(AlgoTokens.accentYellow, DarkBackground)
                                            isMin -> Pair(AlgoTokens.accentYellow, DarkBackground)
                                            label.equals("key", ignoreCase = true) -> Pair(SecondaryPurple, Color.White)
                                            label.equals("target", ignoreCase = true) -> Pair(SecondaryPurple, Color.White)
                                            else -> Pair(Color(0xFF475569), Color.White)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(badgeBg)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = label.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = badgeText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 7.5.sp
                                            )
                                        }
                                    }
                                }
                                }

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
                                    isChallengeTarget -> Triple(
                                        AlgoTokens.accentYellow, AlgoTokens.yellowFill, AlgoTokens.accentYellow
                                    )
                                    state == ElementState.PIVOT -> Triple(
                                        AlgoTokens.accentYellow, AlgoTokens.yellowFill, Color.White
                                    )
                                    // Compare flash: pink frame + pink tint, but the value
                                    // stays WHITE — a red digit on a red tint is unreadable.
                                    state == ElementState.COMPARING -> Triple(
                                        AlgoTokens.accentPink, AlgoTokens.pinkFill, Color.White
                                    )
                                    state == ElementState.SWAPPING -> Triple(
                                        AlgoTokens.accentPink, Color(0x44FF3366), Color.White
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
                                            val corner = CornerRadius(6.dp.toPx())
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
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(animatedBg)
                                        .border(
                                            width = if (isSelectedForChallenge || isChallengeTarget ||
                                                state != ElementState.IDLE || isSwappingCell
                                            ) 2.dp else 1.dp,
                                            color = when {
                                                isSelectedForChallenge -> AlgoTokens.accentCyan
                                                isChallengeTarget -> AlgoTokens.accentYellow
                                                else -> animatedBorder
                                            },
                                            shape = RoundedCornerShape(6.dp)
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
                                    fontSize = 8.5.sp
                                )

                                // ── Bottom Pointer Badge ──
                                Box(
                                    modifier = Modifier.height(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val bottomEntry = bottomPointerEntry
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = bottomEntry != null,
                                        enter = scaleIn(AlgoTokens.evalSpring) + fadeIn(tween(90)),
                                        exit = scaleOut(tween(110)) + fadeOut(tween(110))
                                    ) {
                                        if (bottomEntry != null) {
                                        val (label, _) = bottomEntry
                                        val (badgeBg, badgeText) = when (label.lowercase()) {
                                            "i", "low", "l", "j", "mid" -> Pair(AlgoTokens.accentCyan, DarkBackground)
                                            "high", "r" -> Pair(AlgoTokens.accentPurple, Color.White)
                                            "k" -> Pair(AlgoTokens.accentPink, Color.White)
                                            else -> Pair(AlgoTokens.accentCyan, DarkBackground)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(width = 18.dp, height = 18.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(badgeBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = badgeText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                                }
                            }
                        }
                    }
                }

                // ── E. Merge Sort: Tier 2 (Auxiliary Merge Buffer & Flow Indicator) ──
                if (isMergeSort && step.auxiliaryArray != null && step.auxiliaryArray.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B14))
                            .border(1.dp, PurpleSubtle, RoundedCornerShape(8.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = "Auxiliary Array",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "AUXILIARY MERGE BUFFER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 7.5.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Merge into Main Array",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "Merging to Main Array",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentGreen,
                                    fontSize = 7.sp
                                )
                            }
                        }

                        // Aux Array Cells
                        LazyRow(
                            state = auxLazyListState,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemsIndexed(step.auxiliaryArray, key = { index, _ -> "aux_$index" }) { auxIdx, auxVal ->
                                val isAuxPointerI = step.auxiliaryIndices["i"] == auxIdx
                                val isAuxPointerJ = step.auxiliaryIndices["j"] == auxIdx
                                val isAuxActive = isAuxPointerI || isAuxPointerJ

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    // Pointer indicator above aux cell
                                    Box(modifier = Modifier.height(12.dp), contentAlignment = Alignment.Center) {
                                        if (isAuxPointerI) {
                                            Text(text = "i", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 7.5.sp)
                                        } else if (isAuxPointerJ) {
                                            Text(text = "j", style = MaterialTheme.typography.labelSmall, color = AccentYellow, fontWeight = FontWeight.Bold, fontSize = 7.5.sp)
                                        }
                                    }

                                    // Aux Cell Box
                                    Box(
                                        modifier = Modifier
                                            .size(width = 28.dp, height = 30.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(if (isAuxActive) CyanSubtle else CardBackgroundHover)
                                            .border(
                                                1.dp,
                                                if (isAuxActive) PrimaryCyan else BorderSubtle,
                                                RoundedCornerShape(5.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = auxVal.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isAuxActive) PrimaryCyan else TextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Aux index
                                    Text(
                                        text = auxIdx.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontSize = 7.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Animated Off-Screen Pointer Pop-Up Cell Indicators ──
                if (leftOffscreen.isNotEmpty() || rightOffscreen.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(
                            visible = leftOffscreen.isNotEmpty(),
                            modifier = Modifier.weight(1f, fill = false),
                            enter = fadeIn() + slideInHorizontally { -it / 2 } + expandHorizontally(),
                            exit = fadeOut() + slideOutHorizontally { -it / 2 } + shrinkHorizontally()
                        ) {
                            val target = leftOffscreen.firstOrNull()
                            if (target != null) {
                                OffscreenCellPopup(
                                    target = target,
                                    isRight = false,
                                    onClick = {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(target.index)
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        AnimatedVisibility(
                            visible = rightOffscreen.isNotEmpty(),
                            modifier = Modifier.weight(1f, fill = false),
                            enter = fadeIn() + slideInHorizontally { it / 2 } + expandHorizontally(),
                            exit = fadeOut() + slideOutHorizontally { it / 2 } + shrinkHorizontally()
                        ) {
                            val target = rightOffscreen.firstOrNull()
                            if (target != null) {
                                OffscreenCellPopup(
                                    target = target,
                                    isRight = true,
                                    onClick = {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(target.index)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // ── Comparison / Step Expression Callout ──
                val expr = step.comparisonExpr ?: step.description
                if (expr.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
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

/**
 * Layered outer-stroke glow around a cell. Deliberately paints ONLY the band
 * OUTSIDE the cell bounds (three nested rounded-rect strokes with decreasing
 * width / increasing intensity) so the translucent cell fill never picks up
 * the halo — the element value inside stays fully legible, and the thin band
 * (~7dp) cannot bleed into neighbouring cells.
 */
private fun DrawScope.drawCellGlow(
    accent: Color,
    intensity: Float,
    cornerRadius: CornerRadius
) {
    val soft = 6.dp.toPx()
    val mid = 3.dp.toPx()
    val tight = 1.dp.toPx()

    // Wide soft outer band
    drawRoundRect(
        color = accent.copy(alpha = 0.14f * intensity),
        topLeft = Offset(-soft, -soft),
        size = Size(size.width + soft * 2f, size.height + soft * 2f),
        cornerRadius = cornerRadius,
        style = Stroke(width = 4.dp.toPx())
    )
    // Mid glow band
    drawRoundRect(
        color = accent.copy(alpha = 0.30f * intensity),
        topLeft = Offset(-mid, -mid),
        size = Size(size.width + mid * 2f, size.height + mid * 2f),
        cornerRadius = cornerRadius,
        style = Stroke(width = 2.dp.toPx())
    )
    // Tight bright rim hugging the border
    drawRoundRect(
        color = accent.copy(alpha = 0.85f * intensity),
        topLeft = Offset(-tight, -tight),
        size = Size(size.width + tight * 2f, size.height + tight * 2f),
        cornerRadius = cornerRadius,
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Floating glassmorphic Phase Banner reflecting current step phase and contextual metadata.
 */
@Composable
fun PhaseBanner(
    step: VisualizerStep,
    algorithmName: String,
    modifier: Modifier = Modifier
) {
    val (phaseColor, phaseBg) = when (step.phaseLabel.uppercase()) {
        "PARTITIONING", "PIVOT PLACED" -> Pair(AccentPink, PinkSubtle)
        "MERGING", "DIVIDING", "MERGE COMPLETE" -> Pair(PurpleGlow, PurpleSubtle)
        "MIN SEARCH", "NEW MIN FOUND", "SWAPPING MIN" -> Pair(AccentYellow, YellowSubtle)
        "KEY ELEVATED", "SHIFTING", "KEY INSERTED" -> Pair(SecondaryPurple, PurpleSubtle)
        "SWAPPING", "COMPARING" -> Pair(PrimaryCyan, CyanSubtle)
        "SORTED", "PASS COMPLETE", "LOCKED IN TAIL" -> Pair(AccentGreen, GreenSubtle)
        else -> Pair(PrimaryCyan, CyanSubtle)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackgroundElevated)
            .border(1.dp, phaseColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Phase Tag Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(phaseBg)
                    .border(1.dp, phaseColor.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                    .padding(horizontal = 7.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = step.phaseLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = phaseColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.sp,
                    letterSpacing = 0.6.sp
                )
            }

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                fontSize = 8.5.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Contextual Quick Badge (e.g. Range, Depth, Key, Pivot)
        val contextTag = when {
            step.pivotIndex != null -> "PIVOT [${step.pivotIndex}]"
            step.minIndex != null -> "MIN [${step.minIndex}]"
            step.floatingElement != null -> "KEY ${step.floatingElement.first}"
            step.activeRange != null -> "[${step.activeRange.first}..${step.activeRange.last}]"
            step.sortedBoundary != null -> "SORTED: ${step.sortedBoundary}"
            else -> null
        }

        if (contextTag != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 1.5.dp)
            ) {
                Text(
                    text = contextTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.5.sp
                )
            }
        }
    }
}

/**
 * Animated Pop-up Pill displaying the off-screen selected/pointer element.
 */
@Composable
private fun OffscreenCellPopup(
    target: OffscreenPointerTarget,
    isRight: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackgroundHover)
            .border(
                1.dp,
                target.badgeBg.copy(alpha = 0.6f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (!isRight) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Scroll Left to Element",
                tint = target.badgeBg,
                modifier = Modifier.size(11.dp)
            )
        }

        // Pointer badge (e.g. PIVOT, J, MID)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(target.badgeBg)
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = target.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = target.badgeTextColor,
                fontWeight = FontWeight.Bold,
                fontSize = 7.5.sp
            )
        }

        // Cell Value Box preview
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(CardBackground)
                .border(1.dp, target.badgeBg.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(
                text = "arr[${target.index}] = ${target.value}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 8.5.sp
            )
        }

        if (isRight) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Scroll Right to Element",
                tint = target.badgeBg,
                modifier = Modifier.size(11.dp)
            )
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
                algorithmName = "Quick Sort"
            )
        }
    }
}



