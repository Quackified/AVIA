package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
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

/**
 * Alternative Box/Cell Array Visualizer mode with top & bottom labeled pointer badges,
 * comparison expression callout, and embedded synchronized live code trace matching the design specification.
 */
@Composable
fun CellArrayVisualizer(
    step: VisualizerStep,
    codeLines: List<String>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── 1. Array Cells & Pointers Canvas ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
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

                        // Find any top pointers for this index (e.g. L, R, pivot, target)
                        val topPointerEntry = step.topPointers.entries.find { it.value == index }
                        // Find any bottom pointers for this index (e.g. i, j, mid, low, high)
                        val bottomPointerEntry = step.bottomPointers.entries.find { it.value == index }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 2.dp)
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

                            // ── Cell Box ──
                            val (targetBorder, targetBg, targetText) = when (state) {
                                ElementState.PIVOT -> Triple(AccentPink, PinkSubtle, AccentPink)
                                ElementState.COMPARING -> Triple(AccentYellow, YellowSubtle, AccentYellow)
                                ElementState.SWAPPING -> Triple(AccentRed, RedSubtle, AccentRed)
                                ElementState.ACTIVE, ElementState.FOUND -> Triple(PrimaryCyan, CyanSubtle, PrimaryCyan)
                                ElementState.SORTED -> Triple(AccentGreen, GreenSubtle, AccentGreen)
                                ElementState.TARGET -> Triple(SecondaryPurple, PurpleSubtle, PurpleGlow)
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
                                        width = if (state != ElementState.IDLE) 1.5.dp else 1.dp,
                                        color = animatedBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = value.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = animatedText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            // ── Index Label ──
                            Text(
                                text = index.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
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

                // ── Comparison / Step Expression Callout ──
                val expr = step.comparisonExpr ?: step.description
                if (expr.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
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

        // ── 2. Embedded Synchronized Code Trace ──
        if (codeLines.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackgroundElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Code Trace Title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SYNCHRONIZED CODE TRACE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (step.activeCodeLines.isNotEmpty()) {
                            Text(
                                text = "Line ${step.activeCodeLines.joinToString(", ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Code lines
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CanvasBackground)
                            .padding(vertical = 4.dp)
                    ) {
                        itemsIndexed(codeLines) { index, line ->
                            val lineNum = index + 1
                            val isActive = lineNum in step.activeCodeLines

                            val lineBg by animateColorAsState(
                                targetValue = if (isActive) Color(0x2200E676) else Color.Transparent,
                                label = "lineBg_$lineNum"
                            )
                            val lineTextCol by animateColorAsState(
                                targetValue = if (isActive) AccentGreen else TextPrimary,
                                label = "lineCol_$lineNum"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(lineBg)
                                    .padding(horizontal = 8.dp, vertical = 2.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lineNum.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isActive) AccentGreen else TextDark,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 9.sp,
                                    modifier = Modifier.width(22.dp)
                                )
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = lineTextCol,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
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
                    activeCodeLines = listOf(11, 12)
                ),
                codeLines = listOf(
                    "def quick_sort(a, low, high):",
                    "    if low < high:",
                    "        pi = partition(a, low, high)",
                    "        quick_sort(a, low, pi - 1)",
                    "        quick_sort(a, pi + 1, high)",
                    "",
                    "def partition(a, low, high):",
                    "    pivot = a[high]",
                    "    i = low - 1",
                    "    for j in range(low, high):",
                    "        if a[j] <= pivot:",
                    "            i += 1",
                    "            a[i], a[j] = a[j], a[i]"
                )
            )
        }
    }
}
