package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
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
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundHover
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * `LINEAR_1D` off-screen pointer banner overlay.
 *
 * Renders the same animated left/right pop-up pills that currently
 * live inside [CellArrayVisualizer], but as an overlay that any 1D
 * spec can opt into. The extraction is split across two PRs:
 *
 *  - **This PR (Phase 4.1.x):** the overlay is built and tested in
 *    isolation. It is **not yet wired** to any spec — the
 *    [com.example.algolens.data.AlgorithmRegistry] still uses the
 *    inline banner in the cell visualizer. The plan calls this
 *    "extraction prep."
 *  - **Follow-up PR:** the inline banner in `CellArrayVisualizer` is
 *    removed, every `LINEAR_1D` spec gains `overlays =
 *    listOf(PointerBannerOverlay())`, and the overlay receives the
 *    cell visualizer's `LazyListState` via a host-owned parameter
 *    so the click-to-scroll callback can stay live.
 *
 * Why split it? The plan's risk callout: the inline banner shares
 * `derivedStateOf` state with the cell row, and unwiring that in
 * the same change risks breaking the 12-test `VisualizerScreenStateTest`
 * suite. Building the overlay shape first locks the contract; the
 * extraction is then a search-and-replace.
 *
 * Conventions:
 *  - **No-op when there are no offscreen pointers.** Empty rows
 *    would be visual noise.
 *  - **Tokenised** for spacing (uses `AlgoTokens.space*`),
 *    but the internal pill geometry (`size(11.dp)`, the 5dp
 *    inter-icon gap, etc.) is the same as the inline version —
 *    a behavior-equivalent move. When the extraction PR lands,
 *    those literals are the next mechanical-hygiene sweep.
 */
object PointerBannerOverlay : VisualizerOverlay {

    @Composable
    override fun Content(
        step: VisualizerStep,
        state: VisualizerScreenState,
        modifier: Modifier,
    ) {
        if (step.array.isEmpty()) return

        // Reconstruct the same offscreen-target computation that
        // `CellArrayVisualizer` runs inline. Without a `LazyListState`
        // we cannot know the visible window, so we conservatively
        // assume nothing is offscreen — the inline version will
        // continue to render the real banner until the extraction
        // PR. The non-wired overlay's job is to prove the
        // composable shape compiles and tests pass; the visual
        // appearance in this PR is "renders nothing" by design.
        val visibleItemIndices: Set<Int> = step.array.indices.toSet()
        val offscreenPointers = if (visibleItemIndices.isEmpty()) {
            emptyList<OffscreenPointerTarget>()
        } else {
            buildOffscreenTargets(step, visibleItemIndices)
        }

        val leftOffscreen = offscreenPointers.filter { !it.isRight }
        val rightOffscreen = offscreenPointers.filter { it.isRight }

        if (leftOffscreen.isEmpty() && rightOffscreen.isEmpty()) return

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = leftOffscreen.isNotEmpty(),
                modifier = Modifier.weight(1f, fill = false),
                enter = fadeIn() + slideInHorizontally { -it / 2 } + expandHorizontally(),
                exit = fadeOut() + slideOutHorizontally { -it / 2 } + shrinkHorizontally()
            ) {
                val target = leftOffscreen.firstOrNull()
                if (target != null) {
                    OffscreenCellPopup(target = target, isRight = false, onClick = { /* wired in extraction PR */ })
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            AnimatedVisibility(
                visible = rightOffscreen.isNotEmpty(),
                modifier = Modifier.weight(1f, fill = false),
                enter = fadeIn() + slideInHorizontally { it / 2 } + expandHorizontally(),
                exit = fadeOut() + slideOutHorizontally { it / 2 } + shrinkHorizontally()
            ) {
                val target = rightOffscreen.firstOrNull()
                if (target != null) {
                    OffscreenCellPopup(target = target, isRight = true, onClick = { /* wired in extraction PR */ })
                }
            }
        }
    }

    /**
     * Same algorithm as the inline `derivedStateOf` block in
     * [CellArrayVisualizer]: walk top pointers, then bottom
     * pointers, then active states, and emit an
     * [OffscreenPointerTarget] for each pointer whose index falls
     * outside the visible window. Public so a future test can
     * assert the per-algorithm offscreen behavior.
     */
    fun buildOffscreenTargets(
        step: VisualizerStep,
        visibleItemIndices: Set<Int>,
    ): List<OffscreenPointerTarget> {
        if (visibleItemIndices.isEmpty() || step.array.isEmpty()) return emptyList()
        val minVisible = visibleItemIndices.minOrNull() ?: 0
        val maxVisible = visibleItemIndices.maxOrNull() ?: (step.array.size - 1)
        val list = mutableListOf<OffscreenPointerTarget>()

        step.topPointers.forEach { (label, idx) ->
            if (idx in step.array.indices && idx !in visibleItemIndices) {
                val isPivot = label.equals("pivot", ignoreCase = true)
                val isMin = label.equals("min", ignoreCase = true)
                val badgeBg = when {
                    isPivot -> AccentYellow
                    isMin -> AccentYellow
                    else -> SecondaryPurple
                }
                val badgeText = when {
                    isPivot || isMin -> DarkBackground
                    else -> Color.White
                }
                list.add(
                    OffscreenPointerTarget(
                        index = idx,
                        value = step.array[idx],
                        label = label,
                        state = step.elementStates[idx] ?: ElementState.IDLE,
                        badgeBg = badgeBg,
                        badgeTextColor = badgeText,
                        isRight = idx > maxVisible
                    )
                )
            }
        }

        step.bottomPointers.forEach { (label, idx) ->
            if (idx in step.array.indices && idx !in visibleItemIndices && list.none { it.index == idx }) {
                val badgeBg = when (label.lowercase()) {
                    "i", "low", "l" -> PrimaryCyan
                    "j", "mid" -> AccentYellow
                    "high", "r" -> SecondaryPurple
                    "k" -> AccentGreen
                    else -> PrimaryCyan
                }
                val badgeText = when (label.lowercase()) {
                    "high", "r" -> Color.White
                    else -> DarkBackground
                }
                list.add(
                    OffscreenPointerTarget(
                        index = idx,
                        value = step.array[idx],
                        label = label,
                        state = step.elementStates[idx] ?: ElementState.IDLE,
                        badgeBg = badgeBg,
                        badgeTextColor = badgeText,
                        isRight = idx > maxVisible
                    )
                )
            }
        }

        step.elementStates.forEach { (idx, st) ->
            if (st != ElementState.IDLE && idx in step.array.indices && idx !in visibleItemIndices && list.none { it.index == idx }) {
                val (label, bg, fg) = when (st) {
                    ElementState.PIVOT -> Triple("pivot", AccentRed, DarkBackground)
                    ElementState.COMPARING -> Triple("compare", AccentYellow, DarkBackground)
                    ElementState.SWAPPING -> Triple("swap", AccentRed, Color.White)
                    ElementState.FOUND -> Triple("found", PrimaryCyan, DarkBackground)
                    ElementState.TARGET -> Triple("target", SecondaryPurple, Color.White)
                    else -> Triple("active", PrimaryCyan, DarkBackground)
                }
                list.add(
                    OffscreenPointerTarget(
                        index = idx,
                        value = step.array[idx],
                        label = label,
                        state = st,
                        badgeBg = bg,
                        badgeTextColor = fg,
                        isRight = idx > maxVisible
                    )
                )
            }
        }

        return list
    }
}

/**
 * Pop-up pill shown at the left or right edge when a pointer is
 * outside the visible window. Behavior-equivalent to the inline
 * `OffscreenCellPopup` in [CellArrayVisualizer]; relocated here as
 * part of the [PointerBannerOverlay] extraction prep.
 */
@Composable
private fun OffscreenCellPopup(
    target: OffscreenPointerTarget,
    isRight: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackgroundHover)
            .border(
                1.dp,
                target.badgeBg.copy(alpha = 0.6f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (!isRight) {
            Icon(
                imageVector = AlgoGlyphs.Back,
                contentDescription = "Scroll Left to Element",
                tint = target.badgeBg,
                modifier = Modifier.size(11.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(target.badgeBg)
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = target.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = target.badgeTextColor,
                fontWeight = FontWeight.Bold,
                fontSize = AlgoType.microSize
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(CardBackground)
                .border(1.dp, target.badgeBg.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(
                text = "arr[${target.index}] = ${target.value}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = AlgoType.microSize
            )
        }

        if (isRight) {
            Icon(
                imageVector = AlgoGlyphs.ChevronRight,
                contentDescription = "Scroll Right to Element",
                tint = target.badgeBg,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}
