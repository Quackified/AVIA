package com.avia.ui.practice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.avia.model.practice.PracticeSnapshot
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BarUnsorted
import com.avia.ui.theme.BorderCyan
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.GreenSubtle
import com.avia.ui.theme.OrangeSubtle
import com.avia.ui.theme.PinkSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.YellowSubtle
import com.avia.ui.visualizer.ElementState

/**
 * Adaptive frozen visualizer canvas that renders the appropriate data structure
 * snapshot (1D linear bars, buffers, or 2D graphs) for practice questions.
 */
@Composable
fun PracticeVisualCanvas(
    stepTitle: String,
    snapshot: PracticeSnapshot?,
    modifier: Modifier = Modifier
) {
    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AlgoTokens.space4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
            // Header Row: Step Title & Frozen Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stepTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize,
                    letterSpacing = AlgoType.trackSection
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(PurpleSubtle)
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = "Frozen Canvas",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleGlow,
                        fontSize = AlgoType.microSize
                    )
                }
            }

            // Snapshot Content Dispatcher
            when (snapshot) {
                is PracticeSnapshot.LinearSnapshot -> {
                    LinearSnapshotView(snapshot)
                }
                is PracticeSnapshot.BufferSnapshot -> {
                    BufferSnapshotView(snapshot)
                }
                is PracticeSnapshot.GraphSnapshot -> {
                    GraphSnapshotView(snapshot)
                }
                null -> {
                    // Default Conceptual Snapshot
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AlgoTokens.minTouchTarget * 2)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CardBackground)
                            .border(AlgoTokens.bezelInset, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(AlgoTokens.space4),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Conceptual Question · Visual Invariant Analysis",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }
        }
    }
}

/**
 * 1D Array / Bar Chart renderer for Sorting and Searching.
 */
@Composable
private fun LinearSnapshotView(
    snapshot: PracticeSnapshot.LinearSnapshot
) {
    val hasTopPointers = snapshot.topPointers.isNotEmpty()
    val hasBottomPointers = snapshot.bottomPointers.isNotEmpty()
    val pointerBadgeHeight = AlgoTokens.space5 + AlgoTokens.space4 // 20.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AlgoTokens.minTouchTarget * 3.4f)
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
        verticalAlignment = Alignment.Bottom
    ) {
        val maxVal = snapshot.maxValue.coerceAtLeast(1)

        snapshot.array.forEachIndexed { idx, valNum ->
            val state = snapshot.elementStates[idx] ?: ElementState.IDLE
            val topPointer = snapshot.topPointers.entries.find { it.value == idx }?.key
            val bottomPointer = snapshot.bottomPointers.entries.find { it.value == idx }?.key
            val isInActiveRange = snapshot.activeRange == null || idx in snapshot.activeRange

            val barColor = when {
                state == ElementState.PIVOT -> SecondaryPurple
                state == ElementState.COMPARING -> AccentYellow
                state == ElementState.SWAPPING -> AccentPink
                state == ElementState.SORTED || state == ElementState.FOUND -> AccentGreen
                state == ElementState.TARGET -> AccentPink
                state == ElementState.ACTIVE -> PrimaryCyan
                topPointer != null -> when (topPointer.uppercase()) {
                    "L" -> PrimaryCyan
                    "R" -> SecondaryPurple
                    "PIVOT", "MIN" -> AccentYellow
                    "TARGET" -> AccentPink
                    else -> SecondaryPurple
                }
                else -> BarUnsorted
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(if (isInActiveRange) 1f else 0.35f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Pointer Indicator
                if (hasTopPointers) {
                    Box(
                        modifier = Modifier.height(pointerBadgeHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        if (topPointer != null) {
                            val (badgeBg, badgeColor) = when (topPointer.uppercase()) {
                                "PIVOT", "MIN" -> YellowSubtle to AccentYellow
                                "TARGET" -> PinkSubtle to AccentPink
                                "KEY", "HIGH" -> PurpleSubtle to SecondaryPurple
                                "L", "LOW", "MID", "I" -> CyanSubtle to PrimaryCyan
                                "R", "J" -> PurpleSubtle to SecondaryPurple
                                else -> CyanSubtle to PrimaryCyan
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(badgeBg)
                                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = topPointer.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = badgeColor,
                                    fontSize = AlgoType.microSize,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(AlgoTokens.space1))
                }

                // Bar Fill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = AlgoTokens.space1),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight((valNum.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f))
                            .clip(RoundedCornerShape(topStart = AlgoTokens.radiusXs, topEnd = AlgoTokens.radiusXs))
                            .background(barColor)
                    )
                }

                Spacer(modifier = Modifier.height(AlgoTokens.space1))

                // Value Label
                Text(
                    text = valNum.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state != ElementState.IDLE) barColor else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.microSize,
                    maxLines = 1
                )

                // Index Label
                Text(
                    text = idx.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDark,
                    fontSize = AlgoType.microSize,
                    maxLines = 1
                )

                // Bottom Pointer Indicator
                if (hasBottomPointers) {
                    Spacer(modifier = Modifier.height(AlgoTokens.space1))
                    Box(
                        modifier = Modifier.height(pointerBadgeHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bottomPointer != null) {
                            val (badgeBg, badgeColor) = when (bottomPointer.uppercase()) {
                                "L", "LOW", "I", "MID" -> CyanSubtle to PrimaryCyan
                                "R", "HIGH", "J" -> PurpleSubtle to SecondaryPurple
                                "KEY" -> PurpleSubtle to SecondaryPurple
                                "TARGET" -> PinkSubtle to AccentPink
                                "FOUND" -> GreenSubtle to AccentGreen
                                "PIVOT", "MIN" -> YellowSubtle to AccentYellow
                                else -> CyanSubtle to PrimaryCyan
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(badgeBg)
                                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bottomPointer.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AlgoType.microSize,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Buffer container renderer for Stacks and Queues.
 */
@Composable
private fun BufferSnapshotView(
    snapshot: PracticeSnapshot.BufferSnapshot
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AlgoTokens.space3),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (snapshot.isStack) "LIFO Container (Left -> Top)" else "FIFO Queue (Front -> Rear)",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = AlgoType.microSize
            )
            if (snapshot.operationLabel.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(OrangeSubtle)
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = snapshot.operationLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackground)
                .border(AlgoTokens.bezelInset, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                .padding(AlgoTokens.space3),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            snapshot.items.forEachIndexed { idx, itemVal ->
                val isHighlighted = idx == snapshot.highlightIndex
                val borderColor = if (isHighlighted) BorderCyan else BorderSubtle

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(if (isHighlighted) CyanSubtle else CardBackground)
                        .border(AlgoTokens.bezelInset, borderColor, RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(vertical = AlgoTokens.space4),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                    ) {
                        Text(
                            text = itemVal.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isHighlighted) PrimaryCyan else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.labelSize
                        )
                        Text(
                            text = "[$idx]",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2D Canvas renderer for Graphs (BFS, DFS) and Trees (BST, Heap).
 */
@Composable
private fun GraphSnapshotView(
    snapshot: PracticeSnapshot.GraphSnapshot
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackground)
            .border(AlgoTokens.bezelInset, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
    ) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        val nodeSizeDp = 36.dp
        val nodeSizePx = with(density) { nodeSizeDp.toPx() }
        val padXPx = with(density) { 32.dp.toPx() }
        val padYPx = with(density) { 24.dp.toPx() }

        val usableWidth = (widthPx - 2 * padXPx).coerceAtLeast(1f)
        val usableHeight = (heightPx - 2 * padYPx).coerceAtLeast(1f)

        fun getNodeCenter(node: com.avia.model.practice.PracticeGraphNode): Offset {
            return Offset(
                x = padXPx + node.xRatio * usableWidth,
                y = padYPx + node.yRatio * usableHeight
            )
        }

        // Draw Edges First
        Canvas(modifier = Modifier.fillMaxSize()) {
            snapshot.edges.forEach { edge ->
                val fromNode = snapshot.nodes.find { it.id == edge.from }
                val toNode = snapshot.nodes.find { it.id == edge.to }
                if (fromNode != null && toNode != null) {
                    val startOffset = getNodeCenter(fromNode)
                    val endOffset = getNodeCenter(toNode)
                    drawLine(
                        color = if (edge.isHighlighted) PrimaryCyan else BorderSubtle,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = if (edge.isHighlighted) 2.5f else 1.5f
                    )
                }
            }
        }

        // Edge weights overlay
        snapshot.edges.forEach { edge ->
            if (edge.weight != null) {
                val fromNode = snapshot.nodes.find { it.id == edge.from }
                val toNode = snapshot.nodes.find { it.id == edge.to }
                if (fromNode != null && toNode != null) {
                    val startOffset = getNodeCenter(fromNode)
                    val endOffset = getNodeCenter(toNode)
                    val midX = (startOffset.x + endOffset.x) / 2f
                    val midY = (startOffset.y + endOffset.y) / 2f
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (midX - with(density) { 12.dp.toPx() }).roundToInt(),
                                    y = (midY - with(density) { 9.dp.toPx() }).roundToInt()
                                )
                            }
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(CardBackground)
                            .border(
                                1.dp,
                                if (edge.isHighlighted) PrimaryCyan else BorderSubtle,
                                RoundedCornerShape(AlgoTokens.radiusXs)
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${edge.weight}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (edge.isHighlighted) PrimaryCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Draw Nodes
        snapshot.nodes.forEach { node ->
            val isTarget = node.id == snapshot.activeNodeId
            val nodeBg = when {
                node.state == ElementState.SORTED -> GreenSubtle
                node.state == ElementState.ACTIVE || isTarget -> CyanSubtle
                node.state == ElementState.TARGET -> PinkSubtle
                node.state == ElementState.VISITED -> PurpleSubtle
                else -> CardBackground
            }
            val nodeBorder = when {
                node.state == ElementState.SORTED -> AccentGreen
                node.state == ElementState.ACTIVE || isTarget -> PrimaryCyan
                node.state == ElementState.TARGET -> AccentPink
                node.state == ElementState.VISITED -> SecondaryPurple
                else -> BorderSubtle
            }
            val textColor = when {
                node.state == ElementState.SORTED -> AccentGreen
                node.state == ElementState.ACTIVE || isTarget -> PrimaryCyan
                node.state == ElementState.TARGET -> AccentPink
                node.state == ElementState.VISITED -> SecondaryPurple
                else -> TextPrimary
            }

            val center = getNodeCenter(node)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (center.x - nodeSizePx / 2f).roundToInt(),
                            y = (center.y - nodeSizePx / 2f).roundToInt()
                        )
                    }
                    .size(nodeSizeDp)
                    .clip(CircleShape)
                    .background(nodeBg)
                    .border(AlgoTokens.bezelInset * 1.5f, nodeBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = node.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.labelSize
                )
            }
        }
    }
}
