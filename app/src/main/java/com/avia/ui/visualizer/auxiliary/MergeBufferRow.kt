package com.avia.ui.visualizer.auxiliary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avia.model.AuxiliarySlot
import com.avia.model.RegionAuxiliary
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.visualizer.VisualizerScreenState
import com.avia.ui.visualizer.VisualizerStep
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.avia.ui.theme.AlgoType

/**
 * Merge target buffer row — the bottom auxiliary of the
 * "exquisite" Merge Sort layout.
 *
 * Reads [VisualizerStep.auxiliaryArray] and
 * [VisualizerStep.auxiliaryIndices]. When the algorithm is in
 * the merge phase, `auxiliaryArray` is non-null; the row shows
 * the buffer as a row of cells, with the `i` / `j` / `k`
 * pointer badges at the corresponding positions. Cells that
 * haven't been filled yet are dimmed.
 *
 * During the divide phase, `auxiliaryArray` is null; the row
 * shows an empty placeholder.
 *
 * Cells render at ~60% of the main row's cell height so the
 * main array stays the dominant element.
 *
 * Usage: declared on [com.avia.model.AlgorithmSpec.auxiliaryComponents]
 * with [AuxiliarySlot.BOTTOM] and a weight of ~0.34f.
 */
object MergeBufferRow {
    const val KEY: String = "merge-buffer-row"
    const val DEFAULT_WEIGHT: Float = 0.34f

    val Composable: @Composable (
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) -> Unit = { step, _, modifier ->
        BufferContent(step = step, modifier = modifier)
    }

    fun auxiliary(weight: Float = DEFAULT_WEIGHT): RegionAuxiliary =
        RegionAuxiliary(
            slot = AuxiliarySlot.BOTTOM,
            weight = weight,
            key = KEY,
            content = Composable,
        )

    @Composable
    private fun BufferContent(step: VisualizerStep, modifier: Modifier) {
        val n = step.array.size.coerceAtLeast(1)
        val aux = step.auxiliaryArray
        val auxIndices = step.auxiliaryIndices

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (aux == null) {
                    EmptyPlaceholder()
                } else {
                    for (i in 0 until n) {
                        val value = aux.getOrNull(i)
                        val isI = auxIndices["i"] == i
                        val isJ = auxIndices["j"] == i
                        val isK = auxIndices["k"] == i
                        BufferCell(
                            index = i,
                            value = value,
                            isI = isI,
                            isJ = isJ,
                            isK = isK,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmptyPlaceholder() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                .padding(AlgoTokens.space3),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AUX BUFFER (empty during divide phase)",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    @Composable
    private fun BufferCell(
        index: Int,
        value: Int?,
        isI: Boolean,
        isJ: Boolean,
        isK: Boolean,
        modifier: Modifier
    ) {
        val filled = value != null
        val container = when {
            isI || isJ || isK -> AlgoTokens.cyanFill
            filled -> AlgoTokens.glassFill
            else -> AlgoTokens.glassFill
        }
        val border = when {
            isI -> PrimaryCyan.copy(alpha = 0.6f)
            isJ -> AccentYellow.copy(alpha = 0.6f)
            isK -> AccentGreen.copy(alpha = 0.6f)
            else -> BorderSubtle
        }
        val textColor = if (filled) TextPrimary else TextMuted
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(container)
                .border(AlgoTokens.strokeThin, border, RoundedCornerShape(AlgoTokens.radiusXs)),
            contentAlignment = Alignment.Center
        ) {
            if (filled) {
                Box(
                    modifier = Modifier
                        .padding(AlgoTokens.space1),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else if (isI || isJ || isK) {
                Text(
                    text = if (isI) "i" else if (isJ) "j" else "k",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isI) PrimaryCyan else if (isJ) AccentYellow else AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.microSize
                )
            }
        }
    }
}
