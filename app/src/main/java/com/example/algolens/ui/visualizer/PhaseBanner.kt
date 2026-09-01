package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Floating glassmorphic Phase Banner shared by every visualizer family.
 * Reflects the current step's [VisualizerStep.phaseLabel] (coloured pill),
 * the human-readable [VisualizerStep.description], and a contextual quick
 * badge when the step carries a pivot / min / key / active range / sorted
 * boundary.
 *
 * Originally lived inside [CellArrayVisualizer] (where it is still used for
 * the 1D sorting / searching cases). It is now its own composable so the
 * BUFFER (Stack / Queue) and GRAPH_2D (BST / Heap / BFS / DFS) families
 * can show the same banner above their canvas via [VisualizerHost].
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
        // Defaults that work for BUFFER and GRAPH_2D family labels.
        "PUSH", "POP", "ENQUEUE", "DEQUEUE" -> Pair(SecondaryPurple, PurpleSubtle)
        "VISITING", "ENQUEUED", "DEQUEUED" -> Pair(PrimaryCyan, CyanSubtle)
        "INSERTED", "DELETED", "EXTRACTED", "FOUND" -> Pair(AccentGreen, GreenSubtle)
        "SIFT UP", "SIFT DOWN", "HEAPIFY" -> Pair(AccentYellow, YellowSubtle)
        else -> Pair(PrimaryCyan, CyanSubtle)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackgroundElevated)
            .border(1.dp, phaseColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
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
                    .clip(RoundedCornerShape(5.dp))
                    .background(phaseBg)
                    .border(1.dp, phaseColor.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                    .padding(horizontal = 7.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = step.phaseLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = phaseColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.sp,
                    letterSpacing = 0.6.sp
                )
            }

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                fontSize = 8.5.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Contextual Quick Badge (e.g. Range, Depth, Key, Pivot).
        // Falls back to the algorithm name for BUFFER / GRAPH_2D steps that
        // don't carry the 1D-specific step fields below.
        val contextTag = when {
            step.pivotIndex != null -> "PIVOT [${step.pivotIndex}]"
            step.minIndex != null -> "MIN [${step.minIndex}]"
            step.floatingElement != null -> "KEY ${step.floatingElement.first}"
            step.activeRange != null -> "[${step.activeRange.first}..${step.activeRange.last}]"
            step.sortedBoundary != null -> "SORTED: ${step.sortedBoundary}"
            step.activeNodeId != null -> "NODE ${step.activeNodeId}"
            step.buffer.isNotEmpty() -> "SIZE ${step.buffer.size}/${step.bufferCapacity ?: step.buffer.size}"
            else -> null
        }

        if (contextTag != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ChipBackground)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 1.5.dp)
            ) {
                Text(
                    text = contextTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.5.sp
                )
            }
        }
    }
}
