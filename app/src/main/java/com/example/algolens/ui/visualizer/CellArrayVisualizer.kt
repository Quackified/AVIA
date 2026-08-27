package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
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
 * comparison expression callout, embedded synchronized multi-language CodeTracePane,
 * and animated pop-up pill indicator cells for off-screen pointers.
 */
@Composable
fun CellArrayVisualizer(
    step: VisualizerStep,
    algorithmName: String = "Bubble Sort",
    codeLines: List<String> = emptyList(),
    selectedCellIndices: Set<Int> = emptySet(),
    onCellClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val auxLazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                            isPivot -> AccentPink
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

    Column(
        modifier = modifier
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSwapping) PinkSubtle else CyanSubtle)
                                .border(1.dp, if (isSwapping) AccentPink.copy(alpha = 0.5f) else PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isSwapping) "⚡ BUBBLE UP SWAP: arr[${step.leftPointer}] ⇄ arr[${step.rightPointer}]"
                                       else "🔍 ADJACENT COMPARE: arr[${step.leftPointer}] vs arr[${step.rightPointer}]",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSwapping) AccentPink else PrimaryCyan,
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
                    itemsIndexed(step.array, key = { index, _ -> index }) { index, value ->
                        val state = step.elementStates[index] ?: ElementState.IDLE
                        val isSelectedForChallenge = selectedCellIndices.contains(index)

                        // Quick Sort Partition Dimming (Dim elements outside low..high)
                        val isInActiveRange = step.activeRange == null || index in step.activeRange
                        val cellAlpha by animateFloatAsState(
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
                        val scaleFactor by animateFloatAsState(
                            targetValue = if (isSwappingCell && state == ElementState.SWAPPING) 1.15f else 1f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                            label = "cellScale_$index"
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
                                    .scale(scaleFactor)
                                    .alpha(cellAlpha)
                                    .clickable(enabled = onCellClick != null) {
                                        onCellClick?.invoke(index)
                                    }
                            ) {
                                // ── Top Pointer Badge ──
                                Box(
                                    modifier = Modifier.height(18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (topPointerEntry != null) {
                                        val (label, _) = topPointerEntry
                                        val isPivot = label.equals("pivot", ignoreCase = true)
                                        val isMin = label.equals("min", ignoreCase = true)
                                        val (badgeBg, badgeText) = when {
                                            isPivot -> Pair(AccentPink, DarkBackground)
                                            isMin -> Pair(AccentYellow, DarkBackground)
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

                                // ── Cell Box ──
                                val (targetBorder, targetBg, targetText) = when {
                                    isSelectedForChallenge -> Triple(PrimaryCyan, CyanSubtle, PrimaryCyan)
                                    state == ElementState.PIVOT -> Triple(AccentPink, Color(0x33FF3366), Color.White)
                                    state == ElementState.COMPARING -> Triple(AccentYellow, YellowSubtle, AccentYellow)
                                    state == ElementState.SWAPPING -> Triple(AccentRed, Color(0x44FF4B4B), Color.White)
                                    state == ElementState.ACTIVE || state == ElementState.FOUND -> Triple(PrimaryCyan, CyanSubtle, PrimaryCyan)
                                    state == ElementState.SORTED -> Triple(AccentGreen, GreenSubtle, AccentGreen)
                                    state == ElementState.TARGET -> Triple(SecondaryPurple, PurpleSubtle, Color.White)
                                    else -> Triple(BorderMedium, CardBackground, TextPrimary)
                                }

                                val animatedBorder by animateColorAsState(targetBorder, tween(120), label = "cellBorder_$index")
                                val animatedBg by animateColorAsState(targetBg, tween(120), label = "cellBg_$index")
                                val animatedText by animateColorAsState(targetText, tween(120), label = "cellText_$index")

                                Box(
                                    modifier = Modifier
                                        .size(width = 36.dp, height = 40.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(animatedBg)
                                        .border(
                                            width = if (isSelectedForChallenge || state != ElementState.IDLE || isSwappingCell) 2.dp else 1.dp,
                                            color = if (isSelectedForChallenge) PrimaryCyan else animatedBorder,
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = value.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = animatedText,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }

                                // ── Index Label ──
                                Text(
                                    text = index.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelectedForChallenge) PrimaryCyan else TextMuted,
                                    fontWeight = if (isSelectedForChallenge) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 8.5.sp
                                )

                                // ── Bottom Pointer Badge ──
                                Box(
                                    modifier = Modifier.height(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (bottomPointerEntry != null) {
                                        val (label, _) = bottomPointerEntry
                                        val (badgeBg, badgeText) = when (label.lowercase()) {
                                            "i", "low", "l" -> Pair(PrimaryCyan, DarkBackground)
                                            "j", "mid" -> Pair(AccentYellow, DarkBackground)
                                            "high", "r" -> Pair(SecondaryPurple, Color.White)
                                            "k" -> Pair(AccentGreen, DarkBackground)
                                            else -> Pair(PrimaryCyan, DarkBackground)
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
                            color = if (step.comparisonExpr != null) AccentYellow else PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 8.5.sp
                        )
                    }
                }
            }
        }

        // ── 3. Embedded Synchronized Multi-Language Code Trace & Variable Inspector ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        ) {
            CodeTracePane(
                step = step,
                algorithmName = algorithmName,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
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



