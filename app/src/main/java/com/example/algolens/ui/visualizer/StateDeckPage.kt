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
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stateFileName = remember(algorithmName) {
        algorithmName.lowercase().replace(" ", "_").replace("-", "_") + ".state"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // ── Terminal Header Bar (matches CodeListing's header bar) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackgroundElevated)
                .clickable { onToggleExpand() }
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Terminal Window Traffic-Light Dots + State File Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space3)
                            .clip(CircleShape)
                            .background(AccentRed.copy(alpha = 0.85f))
                    )
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space3)
                            .clip(CircleShape)
                            .background(AccentYellow.copy(alpha = 0.85f))
                    )
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space3)
                            .clip(CircleShape)
                            .background(AccentGreen.copy(alpha = 0.85f))
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Terminal,
                        contentDescription = null,
                        tint = PurpleGlow,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = stateFileName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = AlgoType.microSize
                    )
                }
            }

            // Right: STATE Badge + Peek/Expand Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(SecondaryPurple.copy(alpha = 0.18f))
                        .border(
                            AlgoTokens.strokeHairline,
                            SecondaryPurple.copy(alpha = 0.45f),
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "STATE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        color = PurpleGlow,
                        fontSize = AlgoType.microSize,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CyanSubtle)
                        .border(
                            AlgoTokens.strokeHairline,
                            PrimaryCyan.copy(alpha = 0.4f),
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isExpanded) "Collapse v" else "Expand ^",
                        style = MaterialTheme.typography.labelSmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Terminal Header Separator Hairline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlgoTokens.strokeThin)
                .background(BorderSubtle)
        )

        // ── Scrollable Terminal State Body ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
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
            fontFamily = FontFamily.Monospace,
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
    val base = MaterialTheme.typography.bodySmall
    val badgeStyle = remember(base) {
        base.copy(
            color = PrimaryCyan,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize
        )
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CyanSubtle)
            .border(
                AlgoTokens.strokeThin,
                PrimaryCyan.copy(alpha = 0.35f),
                RoundedCornerShape(AlgoTokens.radiusXs)
            )
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
