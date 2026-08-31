package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AlgoTokens

/**
 * Sliding bouncy active-line indicator for the code trace pane.
 *
 * Tracks the first visible code line whose number is in [activeLines],
 * animates the pill's Y and H via [AlgoTokens.lineTrackSpring] (Y) and a
 * 160ms tween (H). Renders as a translucent pill with the line's semantic
 * [accent] border, layered behind the LazyColumn content (z-stacked by
 * caller's Box order).
 *
 * Shares the same [LazyListState] as the [CodeListing] LazyColumn — both
 * must be created by the public shell so the pill reads the same layout
 * info the listing scrolls.
 */
@Composable
fun ActiveLinePill(
    activeLines: List<Int>,
    syncPulse: State<Float>,
    accent: Color,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    var pillTargetY by remember { mutableFloatStateOf(0f) }
    var pillTargetH by remember { mutableFloatStateOf(0f) }
    val pillPaddingPx = with(LocalDensity.current) { 4.dp.toPx() }

    LaunchedEffect(activeLines, lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                (it.index + 1) in activeLines
            }
        }.collect { info ->
            if (info != null) {
                pillTargetY = info.offset.toFloat()
                pillTargetH = info.size.toFloat()
            }
        }
    }

    val animatedPillY by animateFloatAsState(
        targetValue = pillTargetY,
        animationSpec = AlgoTokens.lineTrackSpring,
        label = "activeLinePillY"
    )
    val animatedPillH by animateFloatAsState(
        targetValue = pillTargetH,
        animationSpec = tween(160),
        label = "activeLinePillH"
    )

    if (animatedPillH > 0f) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { animatedPillH.toDp() })
                .graphicsLayer { translationY = animatedPillY + pillPaddingPx }
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .drawBehind {
                    // Pulse read in the render phase — no recomposition.
                    drawRect(accent.copy(alpha = 0.10f + 0.12f * syncPulse.value))
                }
                .border(AlgoTokens.strokeThin, accent.copy(alpha = 0.28f), RoundedCornerShape(AlgoTokens.radiusXs))
        )
    }
}
