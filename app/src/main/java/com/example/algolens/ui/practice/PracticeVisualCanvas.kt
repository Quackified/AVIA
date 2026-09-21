package com.example.algolens.ui.practice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.algolens.model.practice.PracticeSnapshot
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BarUnsorted
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import com.example.algolens.ui.visualizer.ElementState

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AlgoTokens.minTouchTarget * 2.5f)
            .padding(vertical = AlgoTokens.space2),
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
                topPointer != null -> SecondaryPurple
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
                Box(
                    modifier = Modifier.height(AlgoTokens.space5),
                    contentAlignment = Alignment.Center
                ) {
                    if (topPointer != null) {
                        val badgeBg = when (topPointer.uppercase()) {
                            "PIVOT" -> PurpleSubtle
                            "MIN" -> YellowSubtle
                            "TARGET" -> PinkSubtle
                            "KEY" -> PurpleSubtle
                            else -> CyanSubtle
                        }
                        val badgeColor = when (topPointer.uppercase()) {
                            "PIVOT" -> PurpleGlow
                            "MIN" -> AccentYellow
                            "TARGET" -> AccentPink
                            "KEY" -> SecondaryPurple
                            else -> PrimaryCyan
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(badgeBg)
                                .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                        ) {
                            Text(
                                text = topPointer,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bar Fill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
                    fontSize = AlgoType.microSize
                )

                // Bottom Pointer / Index
                Box(
                    modifier = Modifier.height(AlgoTokens.space5),
                    contentAlignment = Alignment.Center
                ) {
                    if (bottomPointer != null) {
                        Text(
                            text = bottomPointer,
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    } else {
                        Text(
                            text = idx.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize
                        )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AlgoTokens.minTouchTarget * 3)
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackground)
            .border(AlgoTokens.bezelInset, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
    ) {
        // Draw Edges First
        Canvas(modifier = Modifier.fillMaxSize()) {
            snapshot.edges.forEach { edge ->
                val fromNode = snapshot.nodes.find { it.id == edge.from }
                val toNode = snapshot.nodes.find { it.id == edge.to }
                if (fromNode != null && toNode != null) {
                    val startOffset = Offset(fromNode.xRatio * size.width, fromNode.yRatio * size.height)
                    val endOffset = Offset(toNode.xRatio * size.width, toNode.yRatio * size.height)
                    drawLine(
                        color = if (edge.isHighlighted) PrimaryCyan else BorderSubtle,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = if (edge.isHighlighted) 2.5f else 1.5f
                    )
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
                else -> CardBackground
            }
            val nodeBorder = when {
                node.state == ElementState.SORTED -> AccentGreen
                node.state == ElementState.ACTIVE || isTarget -> PrimaryCyan
                node.state == ElementState.TARGET -> AccentPink
                else -> BorderSubtle
            }
            val textColor = when {
                node.state == ElementState.SORTED -> AccentGreen
                node.state == ElementState.ACTIVE || isTarget -> PrimaryCyan
                node.state == ElementState.TARGET -> AccentPink
                else -> TextPrimary
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = AlgoTokens.space4,
                        top = AlgoTokens.space2,
                        end = AlgoTokens.space4,
                        bottom = AlgoTokens.space2
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(
                            androidx.compose.ui.BiasAlignment(
                                (node.xRatio * 2f) - 1f,
                                (node.yRatio * 2f) - 1f
                            )
                        )
                        .clip(CircleShape)
                        .background(nodeBg)
                        .border(AlgoTokens.bezelInset * 1.5f, nodeBorder, CircleShape)
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
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
}
