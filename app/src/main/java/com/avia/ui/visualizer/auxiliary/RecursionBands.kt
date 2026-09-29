package com.avia.ui.visualizer.auxiliary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.avia.model.RegionAuxiliary
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.visualizer.VisualizerScreenState
import com.avia.ui.visualizer.VisualizerStep

/**
 * Recursion depth band strip — the top auxiliary of the
 * "exquisite" Merge Sort visualizer layout.
 *
 * Reads [VisualizerStep.ancestorRanges] (the path from the root
 * to the current range) and [VisualizerStep.mergeBlocks] (the
 * current depth's children). Renders one row per depth in the
 * path:
 *  - Ancestor depths: a single muted band covering the
 *    ancestor's range.
 *  - The current depth: one vivid band per [mergeBlocks] entry.
 *    The band whose range matches the current [activeRange]
 *    glows (full alpha + cyan border).
 *
 * Empty / not Merge Sort: renders nothing. The strip is a Canvas
 * so we can lay out the bands to the cell-row's column count
 * without paying for individual Composables per cell.
 *
 * Usage: declared on [com.avia.model.AlgorithmSpec.auxiliaryComponents]
 * with [com.avia.model.AuxiliarySlot.TOP] and a
 * weight of ~0.10f.
 */
object RecursionBands {
    const val KEY: String = "recursion-bands"
    const val DEFAULT_WEIGHT: Float = 0.10f

    /**
     * The composable content. The host applies the slot/weight;
     * we just draw into the supplied [Modifier].
     */
    val Composable: @Composable (
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) -> Unit = { step, _, modifier ->
        BandsContent(step = step, modifier = modifier)
    }

    /**
     * The pre-bound [RegionAuxiliary] entry for use in
     * [com.avia.data.AlgorithmRegistry]. Pass
     * [weight] to override the default 0.10f.
     */
    fun auxiliary(weight: Float = DEFAULT_WEIGHT): RegionAuxiliary =
        RegionAuxiliary(
            slot = com.avia.model.AuxiliarySlot.TOP,
            weight = weight,
            key = KEY,
            content = Composable,
        )

    @Composable
    private fun BandsContent(step: VisualizerStep, modifier: Modifier) {
        if (step.ancestorRanges.isEmpty() || step.array.isEmpty()) return
        val n = step.array.size
        val ancestorRanges = step.ancestorRanges
        val currentDepth = step.recursionDepth.coerceAtLeast(0)
        val maxDepth = (ancestorRanges.size - 1).coerceAtLeast(currentDepth)

        val ancestorColor = remember { AlgoTokens.accentPurple }
        val currentColor = remember { AlgoTokens.accentPurple }
        val activeColor = remember { AlgoTokens.accentCyan }

        Canvas(modifier = modifier.fillMaxSize()) {
            val depthCount = maxDepth + 1
            val rowHeight = size.height / depthCount.coerceAtLeast(1)
            val gap = rowHeight * 0.18f
            val bandHeight = (rowHeight - gap).coerceAtLeast(1f)

            for (depth in 0..maxDepth) {
                val rowTop = depth * rowHeight + gap / 2f
                if (depth < currentDepth) {
                    // Ancestor depth: a single muted band over the
                    // ancestor's range.
                    val range = ancestorRanges.getOrNull(depth) ?: continue
                    val (l, r) = range.first.toFloat() to range.last.toFloat()
                    val left = (l / n) * size.width
                    val right = ((r + 1) / n) * size.width
                    drawRect(
                        color = ancestorColor.copy(alpha = 0.18f),
                        topLeft = Offset(left, rowTop),
                        size = Size(right - left, bandHeight),
                    )
                } else if (depth == currentDepth) {
                    // Current depth: one band per mergeBlocks entry.
                    // The one matching `activeRange` glows.
                    val blocks = step.mergeBlocks
                    if (blocks.isEmpty()) {
                        // No partitions this step (initial state or
                        // post-merge); fall back to a single band.
                        val range = ancestorRanges.getOrNull(depth) ?: continue
                        val (l, r) = range.first.toFloat() to range.last.toFloat()
                        val left = (l / n) * size.width
                        val right = ((r + 1) / n) * size.width
                        drawRect(
                            color = currentColor.copy(alpha = 0.55f),
                            topLeft = Offset(left, rowTop),
                            size = Size(right - left, bandHeight),
                        )
                    } else {
                        for (block in blocks) {
                            val (l, r) = block.first.toFloat() to block.last.toFloat()
                            val left = (l / n) * size.width
                            val right = ((r + 1) / n) * size.width
                            val isActive = step.activeRange == block
                            drawRect(
                                color = if (isActive) currentColor.copy(alpha = 0.95f)
                                        else currentColor.copy(alpha = 0.45f),
                                topLeft = Offset(left, rowTop),
                                size = Size(right - left, bandHeight),
                            )
                            if (isActive) {
                                drawRect(
                                    color = activeColor,
                                    topLeft = Offset(left, rowTop),
                                    size = Size(right - left, bandHeight),
                                    style = Stroke(width = 1.5f),
                                )
                            }
                        }
                    }
                } else {
                    // Future depth (deeper than current): render
                    // empty / very faint baseline. The merge
                    // recursion hasn't reached this level yet.
                    val range = ancestorRanges.getOrNull(depth) ?: continue
                    val (l, r) = range.first.toFloat() to range.last.toFloat()
                    val left = (l / n) * size.width
                    val right = ((r + 1) / n) * size.width
                    drawRect(
                        color = BorderSubtle.copy(alpha = 0.25f),
                        topLeft = Offset(left, rowTop),
                        size = Size(right - left, bandHeight),
                    )
                }
            }
        }
    }
}
