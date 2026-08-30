package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.TextMuted

/**
 * Merge Sort recursion-tree overlay.
 *
 * Renders a small ancestor-path strip in the top-right corner of the
 * canvas, one row per recursion level, where each row shows the
 * sibling block(s) at that depth. The current depth is highlighted
 * with the `Tracking Purple` accent; ancestor depths are muted.
 *
 * Data source: [VisualizerStep.recursionDepth] (Int) +
 * [VisualizerStep.mergeBlocks] (List<IntRange>). At each emit, the
 * step repository writes the *current* level's active blocks, not the
 * full path, so this overlay reconstructs the visible path by
 * rendering level-by-level with the current step's depth as the
 * "lit" row. This matches what the user sees in the inline
 * `CellArrayVisualizer` recursion block (`DEPTH n`, `Blocks: [...]`),
 * but as a floating overlay rather than embedded chrome.
 *
 * Conventions:
 *  - No-op when `step.mergeBlocks` is empty (algorithms that don't
 *    carry recursion metadata render nothing).
 *  - Tokenised: every color, dp, and radius comes from `AlgoTokens`
 *    or the existing `ui/theme/Color.kt`. No new hex literals.
 *  - The host positions this overlay via `Modifier.align(...)`; the
 *    overlay itself takes the supplied [modifier] as its root.
 */
object RecursionTreeOverlay : VisualizerOverlay {
    private const val MAX_LEVELS = 4 // 9 elements -> log2 = 3.x; reserve 4 for headroom
    private const val PANEL_WIDTH_DP = 140

    @Composable
    override fun Content(
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) {
        if (step.mergeBlocks.isEmpty()) return
        val depth = step.recursionDepth.coerceIn(0, MAX_LEVELS - 1)

        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AlgoTokens.space3)
                    .width(PANEL_WIDTH_DP.dp)
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CardBackground)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space3),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            ) {
                DepthHeader(depth = depth)
                // Render (depth + 1) ancestor levels, the deepest lit.
                // Each level shows the same `mergeBlocks` list; the
                // ancestor rows use a muted style, the active row uses
                // the tracking-purple accent. We do not have per-level
                // block data in `VisualizerStep`, so the same block
                // list is shown at every rendered level; the active
                // row's pill color is the meaningful signal.
                for (level in 0..depth) {
                    LevelRow(
                        level = level,
                        isActive = level == depth,
                        blocks = step.mergeBlocks,
                    )
                }
            }
        }
    }

    @Composable
    private fun DepthHeader(depth: Int) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        ) {
            DepthPill(depth = depth)
            Text(
                text = "RECURSION",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 7.5.sp,
            )
        }
    }

    @Composable
    private fun DepthPill(depth: Int) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(PurpleSubtle)
                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        ) {
            Text(
                text = "DEPTH $depth",
                style = MaterialTheme.typography.labelSmall,
                color = PurpleGlow,
                fontWeight = FontWeight.Bold,
                fontSize = 7.5.sp,
            )
        }
    }

    @Composable
    private fun LevelRow(
        level: Int,
        isActive: Boolean,
        blocks: List<IntRange>,
    ) {
        val containerColor: Color = if (isActive) PurpleSubtle else CardBackground
        val borderColor: Color = if (isActive) {
            AlgoTokens.accentPurple.copy(alpha = 0.45f)
        } else {
            BorderSubtle
        }
        val labelColor: Color = if (isActive) PurpleGlow else TextMuted

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(containerColor)
                .border(AlgoTokens.strokeThin, borderColor, RoundedCornerShape(AlgoTokens.radiusXs))
                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        ) {
            Text(
                text = "L$level",
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
                fontWeight = FontWeight.Bold,
                fontSize = 7.5.sp,
                modifier = Modifier.width(14.dp),
            )
            Text(
                text = blocks.joinToString(separator = " ") { "[${it.first}..${it.last}]" },
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
                fontSize = 7.5.sp,
            )
        }
    }
}
