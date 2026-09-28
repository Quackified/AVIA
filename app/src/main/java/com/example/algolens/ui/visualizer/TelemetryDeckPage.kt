package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.AlgoHairline
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Deck page displaying live telemetry metrics, variable scopes,
 * execution phase, and visited nodes for the current visualizer step.
 */
@Composable
fun TelemetryDeckPage(
    step: VisualizerStep,
    algorithmName: String,
    modifier: Modifier = Modifier
) {
    val verticalScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .verticalScroll(verticalScroll)
            .padding(AlgoTokens.space4),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        // Step & Phase Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Box(
                    modifier = Modifier
                        .size(AlgoTokens.space3)
                        .clip(CircleShape)
                        .background(PrimaryCyan)
                )
                Text(
                    text = "STEP ${step.stepIndex + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.microSize
                )
            }

            if (step.phaseLabel.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(PurpleSubtle)
                        .border(
                            AlgoTokens.strokeHairline,
                            SecondaryPurple.copy(alpha = 0.5f),
                            RoundedCornerShape(AlgoTokens.radiusXxs)
                        )
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.strokeThin)
                ) {
                    Text(
                        text = step.phaseLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.microSize
                    )
                }
            }
        }

        // Narrative Description
        Text(
            text = step.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )

        AlgoHairline()

        // Variables Section
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Terminal,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                )
                Text(
                    text = "LIVE VARIABLES",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold
                )
            }

            val displayVariables = if (step.variables.isNotEmpty()) {
                step.variables
            } else {
                buildMap {
                    for ((label, index) in step.bottomPointers) {
                        val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                        put(label, "$index ($arrVal)")
                    }
                    for ((label, index) in step.topPointers) {
                        val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                        put(label, "$index ($arrVal)")
                    }
                }
            }

            if (displayVariables.isEmpty()) {
                Text(
                    text = "No active variables at this step",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDark,
                    fontSize = AlgoType.microSize
                )
            } else {
                val horizScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizScroll),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    for ((k, v) in displayVariables) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(CardBackground)
                                .border(
                                    AlgoTokens.strokeThin,
                                    PrimaryCyan.copy(alpha = 0.35f),
                                    RoundedCornerShape(AlgoTokens.radiusXs)
                                )
                                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Text(
                                text = k,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "=",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextDark,
                                fontSize = AlgoType.microSize
                            )
                            Text(
                                text = v,
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Visited / Active Graph Nodes (if present)
        if (step.visitedNodeIds.isNotEmpty() || step.activeNodeId != null) {
            AlgoHairline()
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                if (step.activeNodeId != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Text(
                            text = "ACTIVE NODE:",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = step.activeNodeId,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (step.visitedNodeIds.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Text(
                            text = "VISITED (${step.visitedNodeIds.size}):",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = step.visitedNodeIds.joinToString(" → "),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontSize = AlgoType.microSize,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
