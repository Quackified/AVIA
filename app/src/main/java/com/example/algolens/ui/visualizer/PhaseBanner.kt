package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import com.example.algolens.ui.theme.ChipBackground
import com.example.algolens.ui.theme.AlgoType

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Concept A — Live Narrative Stage Header:
 *  - Top Row: Styled Conversational Step Narrative (15.5sp) with semantic keyword/number
 *    highlighting and a glowing left accent bar, prefixed by the compact Phase Pill
 *    (no meaningless [0..8] range tag).
 *  - Bottom Row: Live Variable Pills ([i = 1]  [j = 3]  [pivot = 5]) positioned
 *    directly under the narrative text.
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
        "PUSH", "POP", "ENQUEUE", "DEQUEUE" -> Pair(SecondaryPurple, PurpleSubtle)
        "VISITING", "ENQUEUED", "DEQUEUED" -> Pair(PrimaryCyan, CyanSubtle)
        "INSERTED", "DELETED", "EXTRACTED", "FOUND" -> Pair(AccentGreen, GreenSubtle)
        "SIFT UP", "SIFT DOWN", "HEAPIFY" -> Pair(AccentYellow, YellowSubtle)
        else -> Pair(PrimaryCyan, CyanSubtle)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2),
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
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(phaseBg)
                    .border(
                        AlgoTokens.strokeThin,
                        phaseColor.copy(alpha = 0.5f),
                        RoundedCornerShape(AlgoTokens.radiusXxs)
                    )
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
            ) {
                Text(
                    text = step.phaseLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = phaseColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = AlgoType.labelSize,
                    letterSpacing = AlgoType.trackTight
                )
            }

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(AlgoTokens.space3))

        // Contextual Quick Badge (e.g. Pivot, Min, Key) — activeRange ([0..8]) omitted
        val isBubbleSort = algorithmName.contains("bubble", ignoreCase = true)
        val contextTag = when {
            step.pivotIndex != null -> "PIVOT [${step.pivotIndex}]"
            step.minIndex != null -> "MIN [${step.minIndex}]"
            step.floatingElement != null -> "KEY ${step.floatingElement.first}"
            step.sortedBoundary != null -> {
                val unsortedCount = if (isBubbleSort) {
                    step.sortedBoundary
                } else {
                    (step.array.size - step.sortedBoundary).coerceAtLeast(0)
                }
                if (unsortedCount > 0) "UNSORTED: $unsortedCount" else "SORTED: ${step.array.size}"
            }
            step.activeNodeId != null -> "NODE ${step.activeNodeId}"
            step.buffer.isNotEmpty() -> "SIZE ${step.buffer.size}/${step.bufferCapacity ?: step.buffer.size}"
            else -> null
        }

        if (contextTag != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(ChipBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusXs)
                    )
                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
            ) {
                Text(
                    text = contextTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.labelSize
                )
            }
        }
    }
}

