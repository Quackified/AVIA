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

import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontFamily
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.text.PlatformTextStyle
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextSecondary

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.sizeIn
import com.example.algolens.ui.components.pressPhysics

/**
 * The Focus Deck's **State** page: uses the exact same Terminal UI titlebar
 * (`Traffic-Light Dots | >_ state_inspector | STATE | Expand ^ / Collapse v`)
 * and [CanvasBackground] body as [CodeListing], followed by the live variable
 * readout and call-stack depth.
 */
@Composable
fun StateDeckPage(
    algorithmName: String,
    step: VisualizerStep,
    timeComplexity: String = "",
    spaceComplexity: String = "",
    onComplexityClick: (() -> Unit)? = null,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // ── Scrollable Terminal State Body ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            // ── Invariant Complexity Badges (Time & Space) ──
            if (timeComplexity.isNotBlank() || spaceComplexity.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AlgoTokens.space1),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timeComplexity.isNotBlank()) {
                        ComplexityStatePill(
                            label = "TIME",
                            value = timeComplexity,
                            accent = PrimaryCyan,
                            container = CyanSubtle,
                            borderColor = PrimaryCyan.copy(alpha = 0.35f),
                            onClick = onComplexityClick
                        )
                    }
                    if (spaceComplexity.isNotBlank()) {
                        ComplexityStatePill(
                            label = "SPACE",
                            value = spaceComplexity,
                            accent = PurpleGlow,
                            container = PurpleSubtle,
                            borderColor = SecondaryPurple.copy(alpha = 0.35f),
                            onClick = onComplexityClick
                        )
                    }
                }

                AlgoHairline()
            }

            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                SectionLabel(
                    icon = AlgoGlyphs.Code,
                    text = "Live variables",
                    iconColor = PrimaryCyan
                )
                VariableInspectorReadout(step = step)
            }

            AlgoHairline()

            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
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
}

/**
 * Readout body for the variable block: wraps variable badges across lines via
 * [androidx.compose.foundation.layout.FlowRow] so telemetry never overflows
 * horizontally offscreen.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun VariableInspectorReadout(step: VisualizerStep) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space1, vertical = AlgoTokens.space1),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
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
 * Readout body for the call-stack block: the pseudo-frame header plus
 * wrapping depth / render-mode / active-code-line badges.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun MemoryCallStackReadout(
    algorithmName: String,
    step: VisualizerStep
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space1, vertical = AlgoTokens.space1),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        val frameSummary = if (step.callStack.isNotEmpty()) {
            step.callStack.joinToString(" → ")
        } else {
            val count = step.array.size.takeIf { it > 0 } ?: step.nodes.size.takeIf { it > 0 } ?: step.buffer.size
            "$algorithmName(size=$count)"
        }
        Text(
            text = frameSummary,
            style = MaterialTheme.typography.labelSmall,
            color = PurpleGlow,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            val depthVal = if (step.callStack.isNotEmpty()) step.callStack.size else (step.recursionDepth + 1)
            StackInfoBadge(label = "DEPTH", value = "$depthVal")
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
    val isFormula = label.equals("formula", ignoreCase = true)
    val containerBg = if (isFormula) AccentYellow.copy(alpha = 0.14f) else CyanSubtle
    val borderColor = if (isFormula) AccentYellow.copy(alpha = 0.45f) else PrimaryCyan.copy(alpha = 0.35f)
    val textColor = if (isFormula) AccentYellow else PrimaryCyan
    val base = MaterialTheme.typography.bodySmall
    val badgeStyle = remember(base, textColor) {
        base.copy(
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize
        )
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(containerBg)
            .border(
                AlgoTokens.strokeThin,
                borderColor,
                RoundedCornerShape(AlgoTokens.radiusXs)
            )
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
    ) {
        Text(
            text = if (isFormula) "formula: $value" else "$label = $value",
            style = badgeStyle
        )
    }
}

@Composable
private fun StackInfoBadge(label: String, value: String) {
    val badgeBase = MaterialTheme.typography.labelSmall
    val labelStyle = remember(badgeBase) {
        badgeBase.copy(
            color = TextMuted,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
    }
    val valueStyle = remember(badgeBase) {
        badgeBase.copy(
            color = PurpleGlow,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize
        )
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(PurpleSubtle)
            .border(
                AlgoTokens.strokeThin,
                BorderSubtle,
                RoundedCornerShape(AlgoTokens.radiusXs)
            )
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

@Composable
private fun ComplexityStatePill(
    label: String,
    value: String,
    accent: androidx.compose.ui.graphics.Color,
    container: androidx.compose.ui.graphics.Color,
    borderColor: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AlgoTokens.radiusXs)
    Row(
        modifier = modifier
            .clip(shape)
            .background(container)
            .border(AlgoTokens.strokeThin, borderColor, shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
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
