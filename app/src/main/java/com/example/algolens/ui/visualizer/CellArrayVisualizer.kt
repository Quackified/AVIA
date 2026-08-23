package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
data class OffscreenPointerTarget(
    val index: Int,
    val value: Int,
    val label: String,
    val state: ElementState,
    val badgeBg: Color,
    val badgeTextColor: Color,
    val isRight: Boolean
)

/**
 * Alternative Box/Cell Array Visualizer mode with top & bottom labeled pointer badges,
 * comparison expression callout, embedded synchronized multi-language CodeTracePane,
 * and animated pop-up indicator cells for off-screen pointers.
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
    val coroutineScope = rememberCoroutineScope()

    // Smoothly keep the active elements / pointers in view when stepping
    val activeIndices = step.elementStates.filter { it.value != ElementState.IDLE }.keys
    val pointerIndices = step.topPointers.values + step.bottomPointers.values
    val targetIndex = (activeIndices + pointerIndices).minOrNull()
    LaunchedEffect(step.stepIndex, targetIndex) {
        if (targetIndex != null && step.array.isNotEmpty()) {
            val safeIndex = targetIndex.coerceIn(0, step.array.size - 1)
            lazyListState.animateScrollToItem(safeIndex)
        }
    }

    // Determine currently visible item indices in LazyRow
    val visibleItemIndices by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
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

                // 1. Check Top Pointers (pivot, target, L, R)
                step.topPointers.forEach { (label, idx) ->
                    if (idx in step.array.indices && idx !in visibleItemIndices) {
                        val isPivot = label.equals("pivot", ignoreCase = true)
                        val badgeBg = if (isPivot) AccentPink else SecondaryPurple
                        val badgeText = if (isPivot) DarkBackground else Color.White
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

                // 2. Check Bottom Pointers (i, j, mid, low, high)
                step.bottomPointers.forEach { (label, idx) ->
                    if (idx in step.array.indices && idx !in visibleItemIndices && list.none { it.index == idx }) {
                        val badgeBg = when (label.lowercase()) {
                            "i", "low" -> PrimaryCyan
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── 1. Array Cells & Pointers Canvas with Pop-up Indicators ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Responsive LazyRow for horizontal scrolling across array elements
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(step.array, key = { index, _ -> index }) { index, value ->
                        val state = step.elementStates[index] ?: ElementState.IDLE
                        val isSelectedForChallenge = selectedCellIndices.contains(index)

                        // Find any top pointers for this index (e.g. L, R, pivot, target)
                        val topPointerEntry = step.topPointers.entries.find { it.value == index }
                        // Find any bottom pointers for this index (e.g. i, j, mid, low, high)
                        val bottomPointerEntry = step.bottomPointers.entries.find { it.value == index }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
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
                                    val (badgeBg, badgeText) = when {
                                        isPivot -> Pair(AccentPink, DarkBackground)
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
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = badgeText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 7.5.sp
                                        )
                                    }
                                }
                            }

                            // ── Cell Box (Fixed: Crisp White Text for Pivot & High-Contrast Styling) ──
                            val (targetBorder, targetBg, targetText) = when {
                                isSelectedForChallenge -> Triple(PrimaryCyan, CyanSubtle, PrimaryCyan)
                                state == ElementState.PIVOT -> Triple(AccentPink, Color(0x33FF3366), Color.White) // High-contrast crisp white text
                                state == ElementState.COMPARING -> Triple(AccentYellow, YellowSubtle, AccentYellow)
                                state == ElementState.SWAPPING -> Triple(AccentRed, Color(0x33FF4B4B), Color.White) // High-contrast crisp white text
                                state == ElementState.ACTIVE || state == ElementState.FOUND -> Triple(PrimaryCyan, CyanSubtle, PrimaryCyan)
                                state == ElementState.SORTED -> Triple(AccentGreen, GreenSubtle, AccentGreen)
                                state == ElementState.TARGET -> Triple(SecondaryPurple, PurpleSubtle, Color.White)
                                else -> Triple(BorderMedium, CardBackground, TextPrimary)
                            }

                            val animatedBorder by animateColorAsState(targetBorder, tween(200), label = "cellBorder_$index")
                            val animatedBg by animateColorAsState(targetBg, tween(200), label = "cellBg_$index")
                            val animatedText by animateColorAsState(targetText, tween(200), label = "cellText_$index")

                            Box(
                                modifier = Modifier
                                    .size(width = 36.dp, height = 40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(animatedBg)
                                    .border(
                                        width = if (isSelectedForChallenge || state != ElementState.IDLE) 2.dp else 1.dp,
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
                                        "i", "low" -> Pair(PrimaryCyan, DarkBackground)
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

                // ── Animated Off-Screen Pointer Pop-Up Cell Indicators ──
                if (leftOffscreen.isNotEmpty() || rightOffscreen.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Off-screen Pop-up Cell
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

                        // Right Off-screen Pop-up Cell
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

        // ── 2. Embedded Synchronized Multi-Language Code Trace & Variable Inspector ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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
 * Animated Pop-up Cell displaying the off-screen selected/pointer element.
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

