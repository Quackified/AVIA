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
 * Standalone peek height. This is the pane's own **frame geometry**, not
 * spacing, so it is a named constant rather than a `space*` token — the spacing
 * grid governs padding between elements, not a component's frame size.
 */
private val PEEK_HEIGHT = 126.dp

/** Standalone expanded height — see [PEEK_HEIGHT]. */
private val EXPANDED_HEIGHT = 285.dp

/**
 * Enhanced Multi-Language Code Trace Pane with syntax highlighting,
 * segmented tab row, dynamic active-line mapping, and scroll state preservation.
 *
 * Thin public shell — composes the following private modules in the same package:
 *  - [CodeListing]    — language tabs + syntax-highlighted code body
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
    modifier: Modifier = Modifier,
    fillsAvailableHeight: Boolean = false,
    isExpandedOverride: Boolean? = null,
    showHeader: Boolean = true,
    onToggleExpand: (() -> Unit)? = null
) {
    val selectedLanguage = com.example.algolens.data.AppSettings.preferredLanguage
    var isExpanded by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    val expanded = isExpandedOverride ?: isExpanded

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

    // Keep the active line centered in the 3-line Peek window (activeLine - 1 at top),
    // or smoothly scroll into view when expanded.
    LaunchedEffect(activeLinesInCurrentLang, expanded) {
        val firstActiveLine = activeLinesInCurrentLang.minOrNull()
        if (firstActiveLine != null && codeData.lines.isNotEmpty()) {
            val anchorIdx = if (!expanded) {
                (firstActiveLine - 2).coerceIn(0, (codeData.lines.size - 1).coerceAtLeast(0))
            } else {
                (firstActiveLine - 3).coerceIn(0, (codeData.lines.size - 1).coerceAtLeast(0))
            }
            lazyListState.animateScrollToItem(anchorIdx)
        }
    }

    val terminalHeight by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (expanded) EXPANDED_HEIGHT else PEEK_HEIGHT,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
        ),
        label = "terminalPeekExpandHeight"
    )

    val paneModifier = if (fillsAvailableHeight) {
        modifier.fillMaxWidth()
    } else {
        modifier
            .fillMaxWidth()
            .height(terminalHeight)
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2)
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(com.example.algolens.ui.theme.CanvasBackground)
            .border(
                AlgoTokens.strokeThin,
                if (expanded) com.example.algolens.ui.theme.PrimaryCyan.copy(alpha = 0.35f)
                else com.example.algolens.ui.theme.BorderSubtle,
                RoundedCornerShape(AlgoTokens.radiusMd)
            )
    }

    // ── Docked Peek / Expand Terminal UI Frame ──
    Box(modifier = paneModifier) {
        CodeListing(
            algorithmName = algorithmName,
            codeData = codeData,
            highlightedLines = highlightedLines as List<AnnotatedString>,
            activeLines = activeLinesInCurrentLang,
            variables = step.variables,
            syncPulse = syncPulse,
            selectedLanguage = selectedLanguage,
            isExpanded = expanded,
            showHeader = showHeader,
            onToggleExpand = onToggleExpand ?: { isExpanded = !isExpanded },
            lazyListState = lazyListState,
            modifier = Modifier.fillMaxSize()
        )
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
