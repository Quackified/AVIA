package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.AlgoHairline
import com.example.algolens.ui.components.SectionLabel
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.TextMuted

/**
 * The Focus Deck's **State** page: the live variable readout and the recursion /
 * call-stack depth, stacked in one scrollable column.
 *
 * **Provenance.** Both blocks were extracted verbatim from
 * `VisualizerHeader.kt`, where they lived as inline disclosure bodies inside the
 * kebab popover — two taps deep, behind a `DropdownMenuItem`. They are the only
 * surfaces in the app that report per-step variable state, so they belong next
 * to the transport the user is already holding, not inside a menu.
 *
 * Nothing about the readouts changed: same badges, same tokens, same
 * stable-`TextStyle` bake. Only their home moved, and the `ColumnScope`
 * receiver was dropped so they compose from any scope.
 *
 * Per the phase brief ("one affordance, one home"), the header rows that used to
 * open these blocks are **deleted**, not aliased.
 */
@Composable
fun StateDeckPage(
    algorithmName: String,
    step: VisualizerStep,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        Column {
            SectionLabel(
                icon = AlgoGlyphs.Code,
                text = "Live variables",
                iconColor = PrimaryCyan
            )
            VariableInspectorReadout(step = step)
        }

        AlgoHairline()

        Column {
            SectionLabel(
                icon = AlgoGlyphs.Terminal,
                text = "Call stack",
                iconColor = PurpleGlow
            )
            MemoryCallStackReadout(
                algorithmName = algorithmName,
                step = step
            )
        }
    }
}

/**
 * Readout body for the variable block: one badge per live variable, falling
 * back to the top / bottom pointer maps (or a bare step counter) for algorithms
 * whose steps carry pointers rather than named variables.
 */
@Composable
private fun VariableInspectorReadout(step: VisualizerStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (step.variables.isNotEmpty()) {
            for ((k, v) in step.variables) {
                VarBadge(label = k, value = v)
            }
        } else {
            for ((label, index) in step.bottomPointers) {
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            for ((label, index) in step.topPointers) {
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            if (step.bottomPointers.isEmpty() && step.topPointers.isEmpty()) {
                VarBadge(label = "step", value = "${step.stepIndex + 1}")
            }
        }
    }
}

/**
 * Readout body for the call-stack block: the pseudo-frame header plus the
 * depth / render-mode / active-code-line badges.
 */
@Composable
private fun MemoryCallStackReadout(
    algorithmName: String,
    step: VisualizerStep
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        Text(
            text = "$algorithmName(size=${step.array.size})",
            style = MaterialTheme.typography.labelSmall,
            color = PurpleGlow,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StackInfoBadge(label = "DEPTH", value = "${step.recursionDepth + 1}")
            StackInfoBadge(label = "MODE", value = step.renderMode.name.lowercase())
            StackInfoBadge(
                label = "LINE",
                value = if (step.activeCodeLines.isEmpty()) "—" else step.activeCodeLines.joinToString(",")
            )
        }
    }
}

@Composable
private fun VarBadge(label: String, value: String) {
    // Read-then-remember: color/weight/size are static badge chrome, so bake
    // them into one stable TextStyle. Per-step value changes then only
    // remeasure the string — Text's internal style.merge() sees a stable
    // style instead of rebuilding overrides every recomposition.
    val base = MaterialTheme.typography.bodySmall
    val badgeStyle = remember(base) {
        base.copy(
            color = PrimaryCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize
        )
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CyanSubtle)
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
    ) {
        Text(
            text = "$label = $value",
            style = badgeStyle
        )
    }
}

@Composable
private fun StackInfoBadge(label: String, value: String) {
    // Read-then-remember: same static-chrome bake as VarBadge — the live
    // value string stays dynamic, the TextStyle refs stay stable.
    val badgeBase = MaterialTheme.typography.labelSmall
    val labelStyle = remember(badgeBase) {
        badgeBase.copy(
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
    }
    val valueStyle = remember(badgeBase) {
        badgeBase.copy(
            color = PurpleGlow,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize
        )
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(PurpleSubtle)
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Text(
            text = label,
            style = labelStyle
        )
        Text(
            text = value,
            style = valueStyle
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111D30)
@Composable
fun StateDeckPagePreview() {
    AlgoLensTheme {
        StateDeckPage(
            algorithmName = "Quick Sort",
            step = VisualizerStep(
                stepIndex = 4,
                description = "Comparing element at index 2 (9) with pivot (7)",
                array = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7),
                activeCodeLines = listOf(5),
                recursionDepth = 2,
                variables = mapOf("i" to "0", "j" to "2", "pivot" to "7")
            )
        )
    }
}
