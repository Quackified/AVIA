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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundHover
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import kotlinx.coroutines.launch
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * Row containing the animated pop-up pill indicators for off-screen pointers.
 *
 * Renders one pill on the left side and one on the right side, each animating
 * in/out when the corresponding offscreen-pointer list becomes empty. Tapping
 * either pill animates the [LazyListState] so the indicated cell scrolls into
 * view.
 *
 * The inline `derivedStateOf` for [offscreenPointers] is the canonical
 * implementation matched by `PointerBannerOverlay.buildOffscreenTargets` (see
 * the test `VisualizerOverlayTest.pointerBannerOverlay_buildOffscreenTargets_matchesInlineSemantics`).
 * The two stay in lockstep until Phase 4 consolidates them; do not edit one
 * without updating the other.
 */
@Composable
fun OffscreenPointerBanner(
    step: VisualizerStep,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val visibleItemIndices by remember {
        derivedStateOf { lazyListState.layoutInfo.visibleItemsInfo.map { it.index }.toSet() }
    }

    val offscreenPointers by remember(step, visibleItemIndices) {
        derivedStateOf {
            if (visibleItemIndices.isEmpty() || step.array.isEmpty()) {
                emptyList()
            } else {
                val minVisible = visibleItemIndices.minOrNull() ?: 0
                val maxVisible = visibleItemIndices.maxOrNull() ?: (step.array.size - 1)
                val list = mutableListOf<OffscreenPointerTarget>()

                // 1. Check Top Pointers (pivot, target, L, R, min, key)
                step.topPointers.forEach { (label, idx) ->
                    if (idx in step.array.indices && idx !in visibleItemIndices) {
                        val isPivot = label.equals("pivot", ignoreCase = true)
                        val isMin = label.equals("min", ignoreCase = true)
                        val badgeBg = when {
                            isPivot -> AlgoTokens.accentYellow
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

                // 2. Check Bottom Pointers (i, j, mid, low, high, k)
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

                // 3. Check active states (COMPARING, SWAPPING, PIVOT, TARGET)
                step.elementStates.forEach { (idx, st) ->
                    if (st != ElementState.IDLE && idx in step.array.indices && idx !in visibleItemIndices && list.none { it.index == idx }) {
                        val (label, bg, fg) = when (st) {
                            ElementState.PIVOT -> Triple("pivot", AccentPink, DarkBackground)
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

                list
            }
        }
    }

    val leftOffscreen = offscreenPointers.filter { !it.isRight }
    val rightOffscreen = offscreenPointers.filter { it.isRight }

    if (leftOffscreen.isEmpty() && rightOffscreen.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
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
                OffscreenCellPopup(
                    target = target,
                    isRight = false,
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(target.index)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(AlgoTokens.space4))

        AnimatedVisibility(
            visible = rightOffscreen.isNotEmpty(),
            modifier = Modifier.weight(1f, fill = false),
            enter = fadeIn() + slideInHorizontally { it / 2 } + expandHorizontally(),
            exit = fadeOut() + slideOutHorizontally { it / 2 } + shrinkHorizontally()
        ) {
            val target = rightOffscreen.firstOrNull()
            if (target != null) {
                OffscreenCellPopup(
                    target = target,
                    isRight = true,
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(target.index)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Animated Pop-up Pill displaying the off-screen selected/pointer element.
 * Internal because it is rendered exclusively by [OffscreenPointerBanner];
 * external callers should go through that composable.
 */
@Composable
private fun OffscreenCellPopup(
    target: OffscreenPointerTarget,
    isRight: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundHover)
            .border(
                AlgoTokens.strokeThin,
                target.badgeBg.copy(alpha = 0.6f),
                RoundedCornerShape(AlgoTokens.radiusSm)
            )
            .clickable { onClick() }
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
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

        // Pointer badge (e.g. PIVOT, J, MID)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(target.badgeBg)
                .padding(horizontal = AlgoTokens.space2, vertical = 1.dp)
        ) {
            Text(
                text = target.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = target.badgeTextColor,
                fontWeight = FontWeight.Bold,
                fontSize = AlgoType.microSize
            )
        }

        // Cell Value Box preview
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(CardBackground)
                .border(AlgoTokens.strokeThin, target.badgeBg.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusXs))
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
