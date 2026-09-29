package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.algolens.data.AlgorithmCodeRegistry
import com.example.algolens.data.AppSettings
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.ChipBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextSecondary

/**
 * The resting representation of the code trace: **one line**, the line the
 * announcement banner is describing, docked directly above [PlaybackRail].
 *
 * **Why replace the 3-line terminal with this.** The old `CodeTracePane` peek
 * occupied 126dp of the column permanently — the single largest fixed cost below
 * the canvas — to show three lines of source, two of which were usually context
 * the user had already read. Worse, its expand animation re-measured the stage,
 * which re-ran the responsive cell-sizing maths in `CellArrayVisualizer` and
 * physically resized the cells mid-interaction.
 *
 * This strip keeps the lockstep promise `PRODUCT.md` calls the product ("watch
 * the canvas and the code-trace line move together") at 30dp, and the full
 * scrollable listing moves into [InstrumentDeck]'s Trace page, which overlays
 * the stage instead of competing with it for height.
 *
 * Height is [AlgoTokens.iconButtonSm] rather than the smaller figure sketched in
 * the brief: this is a tap target the user hits repeatedly, and 30dp is the
 * documented `UI_GUIDELINES` §13 exception for secondary rail controls. A 20dp
 * strip would sit under that floor with no touch headroom.
 *
 * The active-line mapping is the same contract `CodeTracePane` uses — the
 * generator emits Python-keyed line numbers and `lineMapping` translates them
 * into the user's **preferred language** from Settings.
 */
@Composable
fun TraceStrip(
    step: VisualizerStep,
    algorithmName: String,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = AppSettings.preferredLanguage
    val bstMode = step.variables["bstMode"]?.let { runCatching { com.example.algolens.model.BstMode.valueOf(it) }.getOrNull() } ?: com.example.algolens.model.BstMode.SEARCH
    val queueVariant = step.variables["queueVariant"]?.let { runCatching { com.example.algolens.model.QueueVariant.valueOf(it) }.getOrNull() } ?: com.example.algolens.model.QueueVariant.LINEAR_FIFO

    val codeData = remember(algorithmName, language, bstMode, queueVariant) {
        AlgorithmCodeRegistry.getCode(algorithmName, language, bstMode, queueVariant)
    }

    val activeLineNumber = remember(step.activeCodeLines, codeData) {
        if (step.activeCodeLines.isEmpty()) {
            null
        } else {
            step.activeCodeLines
                .flatMap { pyLine ->
                    codeData.lineMapping[pyLine] ?: listOf(pyLine)
                }
                .minOrNull()
                ?.coerceIn(1, codeData.lines.size.coerceAtLeast(1))
        }
    }

    val lineNumber = activeLineNumber ?: 1
    val rawLine = codeData.lines.getOrNull(lineNumber - 1).orEmpty()
    val highlightedLine = remember(rawLine, language) {
        SyntaxHighlighter.highlight(rawLine, language)
    }

    val variableCount = step.variables.size

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AlgoTokens.iconButtonSm)
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackground)
            .border(
                AlgoTokens.strokeThin,
                SecondaryPurple.copy(alpha = 0.30f),
                RoundedCornerShape(AlgoTokens.radiusSm)
            )
            .pressPhysics(
                shape = RoundedCornerShape(AlgoTokens.radiusSm),
                accent = SecondaryPurple
            )
            .clickable(onClickLabel = "Open code trace", onClick = onExpand)
            .padding(horizontal = AlgoTokens.space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Icon(
            imageVector = AlgoGlyphs.Terminal,
            contentDescription = null,
            tint = PurpleGlow,
            modifier = Modifier.size(AlgoTokens.inlineIconSm)
        )

        // Line number — the anchor that ties the strip to the listing.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(ChipBackground)
                .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = lineNumber.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Text(
            text = highlightedLine,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (variableCount > 0) {
            Text(
                text = if (variableCount == 1) "1 var" else "$variableCount vars",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        // Chevron points up: the strip opens a surface that grows upward.
        Icon(
            imageVector = AlgoGlyphs.ChevronDown,
            contentDescription = "Open code trace",
            tint = PrimaryCyan,
            modifier = Modifier
                .size(AlgoTokens.inlineIconSm)
                .graphicsLayer { rotationZ = 180f }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
@Composable
fun TraceStripPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(AlgoTokens.space4)) {
            TraceStrip(
                step = VisualizerStep(
                    stepIndex = 41,
                    description = "Comparing element at index 2 (9) with its neighbour (2)",
                    array = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7),
                    activeCodeLines = listOf(6),
                    variables = mapOf("i" to "2", "j" to "3")
                ),
                algorithmName = "Bubble Sort",
                onExpand = {}
            )
        }
    }
}
