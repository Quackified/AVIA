package com.example.algolens.ui.visualizer.auxiliary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.algolens.model.AuxiliarySlot
import com.example.algolens.model.RegionAuxiliary
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.visualizer.VisualizerScreenState
import com.example.algolens.ui.visualizer.VisualizerStep
import com.example.algolens.ui.components.IconPillButton

/**
 * Phase chip strip — the second top auxiliary of the "exquisite"
 * Merge Sort layout.
 *
 * Reads the current step's `phaseLabel`, `activeRange`, and
 * (during merging) `mergeBlocks` to render a small chip row:
 *
 *  - `PHASE` chip (color-coded by phase: cyan for DIVIDING,
 *    purple for MERGING, green for MERGE COMPLETE / SORTED).
 *  - `RANGE [l..r]` chip showing the active sub-range.
 *  - `BLOCKS [a..b] + [c..d]` chip during merge.
 *  - `DEPTH n` chip showing the current recursion depth.
 *
 * The chips reuse the `IconPillButton` shape so the strip feels
 * like the rest of the header / popover chrome.
 *
 * Usage: declared on [com.example.algolens.model.AlgorithmSpec.auxiliaryComponents]
 * with [AuxiliarySlot.TOP] and a weight of ~0.06f.
 */
object PhaseStrip {
    const val KEY: String = "phase-strip"
    const val DEFAULT_WEIGHT: Float = 0.06f

    val Composable: @Composable (
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) -> Unit = { step, _, modifier ->
        StripContent(step = step, modifier = modifier)
    }

    fun auxiliary(weight: Float = DEFAULT_WEIGHT): RegionAuxiliary =
        RegionAuxiliary(
            slot = AuxiliarySlot.TOP,
            weight = weight,
            key = KEY,
            content = Composable,
        )

    @Composable
    private fun StripContent(step: VisualizerStep, modifier: Modifier) {
        val (accent, container, border) = phaseColors(step.phaseLabel)

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconPillButton(
                    label = step.phaseLabel,
                    accent = accent,
                    accentContainer = container,
                    borderColor = border
                )

                step.activeRange?.let { range ->
                    IconPillButton(
                        label = "[${range.first}..${range.last}]",
                        accent = accent,
                        accentContainer = container,
                        borderColor = border
                    )
                }

                if (step.mergeBlocks.size >= 2) {
                    val parts = step.mergeBlocks.joinToString(" + ") { "[${it.first}..${it.last}]" }
                    IconPillButton(
                        label = parts,
                        accent = accent,
                        accentContainer = container,
                        borderColor = border
                    )
                }

                if (step.recursionDepth > 0 || step.phaseLabel.equals("INITIALIZING", ignoreCase = true)) {
                    IconPillButton(
                        label = "DEPTH ${step.recursionDepth}",
                        accent = accent,
                        accentContainer = container,
                        borderColor = border
                    )
                }
            }
        }
    }

    /**
     * Phase → (accent, container, border) color triple. The
     * three colors track the same accent, just at different
     * alphas, so the chip is visually consistent regardless of
     * phase.
     */
    private fun phaseColors(phaseLabel: String): Triple<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
        val upper = phaseLabel.uppercase()
        return when {
            upper.startsWith("DIVID") || upper.startsWith("INIT") -> Triple(
                PrimaryCyan, AlgoTokens.cyanFill.copy(alpha = 0.18f), AlgoTokens.accentCyan.copy(alpha = 0.3f)
            )
            upper.startsWith("MERGE COMPLETE") || upper.startsWith("SORTED") -> Triple(
                AccentGreen, AlgoTokens.greenFill.copy(alpha = 0.18f), AlgoTokens.accentGreen.copy(alpha = 0.3f)
            )
            upper.startsWith("MERG") || upper.startsWith("FLUSH") -> Triple(
                PurpleGlow, PurpleSubtle, SecondaryPurple.copy(alpha = 0.3f)
            )
            else -> Triple(
                PrimaryCyan, AlgoTokens.cyanFill.copy(alpha = 0.18f), AlgoTokens.accentCyan.copy(alpha = 0.3f)
            )
        }
    }
}
