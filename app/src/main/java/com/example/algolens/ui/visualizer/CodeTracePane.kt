package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.data.AlgorithmCodeRegistry
import com.example.algolens.data.TraceLanguage
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.SecondaryPurple

/**
 * Enhanced Multi-Language Code Trace Pane with syntax highlighting,
 * segmented tab row, dynamic active-line mapping, and scroll state preservation.
 *
 * Thin public shell — composes the following private modules in the same package:
 *  - [CodeListing]    — language tabs + syntax-highlighted code body
 *  - [ActiveLinePill] — sliding active-line pill driven by [lineTrackSpring]
 *  - `SyntaxHighlighter` — keyword/type/comment/number highlighter (in-package helper)
 *  - `AlgorithmCodeRegistry` / `TraceLanguage` (in `data/`) — multi-language code data
 *
 * Owns: the single shared [LazyListState], the selected-language state, the
 * `codeData` lookup, and the `activeLinesInCurrentLang` mapping. The pill and
 * the listing both receive the same [LazyListState] so the pill reads the
 * same layout info that the listing scrolls.
 */
@Composable
fun CodeTracePane(
    step: VisualizerStep,
    algorithmName: String = "Bubble Sort",
    syncPulse: State<Float> = mutableStateOf(0f),
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf(TraceLanguage.KOTLIN) }
    val lazyListState = rememberLazyListState()

    val codeData = remember(algorithmName, selectedLanguage) {
        AlgorithmCodeRegistry.getCode(algorithmName, selectedLanguage)
    }

    // Map active code lines from VisualizerStep to the current selected language
    val activeLinesInCurrentLang = remember(step.activeCodeLines, codeData) {
        if (step.activeCodeLines.isEmpty()) {
            emptyList()
        } else {
            step.activeCodeLines.flatMap { pyLine ->
                codeData.lineMapping[pyLine] ?: listOf(pyLine.coerceIn(1, codeData.lines.size.coerceAtLeast(1)))
            }.distinct()
        }
    }

    val highlightedLines = remember(codeData, selectedLanguage) {
        codeData.lines.map { line -> SyntaxHighlighter.highlight(line, selectedLanguage) }
    }

    // The pill takes the highest-precedence accent across ALL active lines,
    // so a single step that activates e.g. `[5, 8]` (one of which is a swap)
    // correctly renders the pill pink even though another active line is
    // a plain assignment. See [CodeLineAccent] for the family ordering.
    val pillAccent = CodeLineAccent.resolveForActiveSet(codeData.lines, activeLinesInCurrentLang)

    // Smoothly keep the active lines in view only when out of viewport
    LaunchedEffect(activeLinesInCurrentLang) {
        val firstActiveLine = activeLinesInCurrentLang.minOrNull()
        if (firstActiveLine != null && codeData.lines.isNotEmpty()) {
            val targetIdx = (firstActiveLine - 1).coerceIn(0, codeData.lines.size - 1)
            val visibleIndices = lazyListState.layoutInfo.visibleItemsInfo.map { it.index }
            if (targetIdx !in visibleIndices) {
                lazyListState.animateScrollToItem(targetIdx)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        // ── Code Container ──
        // The variable inspector and memory call stack moved into
        // the kebab popover in [VisualizerHeader]; the code-trace
        // pane now contains only the code container, which fills
        // the full available height.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.25f), RoundedCornerShape(AlgoTokens.radiusMd))
                .padding(AlgoTokens.space4)
        ) {
            CodeListing(
                codeData = codeData,
                highlightedLines = highlightedLines as List<AnnotatedString>,
                activeLines = activeLinesInCurrentLang,
                variables = step.variables,
                syncPulse = syncPulse,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it },
                lazyListState = lazyListState,
                modifier = Modifier.fillMaxSize()
            )

            // The active-line pill is layered ON TOP of the listing so the
            // bouncy spring tracker visually highlights the line even as
            // the LazyColumn is mid-scroll.
            ActiveLinePill(
                activeLines = activeLinesInCurrentLang,
                syncPulse = syncPulse,
                accent = pillAccent,
                lazyListState = lazyListState
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CodeTracePanePreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(450.dp).padding(16.dp)) {
            CodeTracePane(
                step = VisualizerStep(
                    stepIndex = 3,
                    description = "Comparing elements at index 1 and 2",
                    array = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7),
                    activeCodeLines = listOf(5),
                    variables = mapOf("i" to "0", "j" to "1", "arr[j]" to "8", "arr[j+1]" to "9")
                ),
                algorithmName = "Bubble Sort"
            )
        }
    }
}
