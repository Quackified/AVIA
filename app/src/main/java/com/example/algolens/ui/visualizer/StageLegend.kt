package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.TextSecondary

/**
 * The stage key — an unobtrusive chip row that names the five element states
 * the canvas paints, in the order the workspace introduces them.
 *
 * **Why it exists.** UI_GUIDELINES §14 is the single source of the
 * `ElementState → colour` mapping, but until now that mapping lived only in
 * documentation — a first-time user saw a yellow cell and had no way to learn
 * that yellow means *comparing*. §13 requires that colour is never the *only*
 * channel; a printed key is that second channel.
 *
 * **Costs zero vertical budget.** It overlays the bottom-start corner of the
 * canvas rather than occupying a row in the column, so it does not compete with
 * the stage for space. It is intentionally **not** dismissible: a hide control
 * with no re-entry path would be a dead end, and the legibility it buys is
 * worth more than the pixels it covers.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun StageLegend(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(AlgoTokens.surfaceSunken.copy(alpha = 0.85f))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        // Order mirrors UI_GUIDELINES §14: inspect → mutate → anchor → lock → travel.
        LegendEntry(color = AlgoTokens.accentYellow, label = "compare")
        LegendEntry(color = AlgoTokens.accentPink, label = "swap")
        LegendEntry(color = AlgoTokens.accentPurple, label = "pivot")
        LegendEntry(color = AlgoTokens.accentGreen, label = "sorted")
        LegendEntry(color = AlgoTokens.accentCyan, label = "active")
    }
}

@Composable
private fun LegendEntry(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        Box(
            modifier = Modifier
                .size(AlgoTokens.space3)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
@Composable
fun StageLegendPreview() {
    AlgoLensTheme {
        StageLegend(modifier = Modifier.padding(AlgoTokens.space4))
    }
}
