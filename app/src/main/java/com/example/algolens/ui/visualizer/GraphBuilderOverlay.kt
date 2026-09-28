package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algolens.model.GraphCapabilityProfile
import com.example.algolens.model.GraphTool
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted

/**
 * Top interactive builder toolbar providing mode toggle, node/edge counts,
 * and quick viewport/fullscreen toggles.
 */
@Composable
fun GraphBuilderToolbar(
    isBuilderActive: Boolean,
    onToggleBuilder: () -> Unit,
    dynamicNodeCount: Int,
    dynamicEdgeCount: Int,
    canReset: Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    isPanned: Boolean = false,
    onResetPan: () -> Unit = {},
    onAddNode: () -> Unit = {},
    activeTool: GraphTool = GraphTool.MOVE,
    onToolSelected: (GraphTool) -> Unit = {},
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(if (isBuilderActive) CyanSubtle else CanvasBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        if (isBuilderActive) PrimaryCyan else BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusXs)
                    )
                    .clickable { onToggleBuilder() }
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Tap,
                        contentDescription = null,
                        tint = if (isBuilderActive) PrimaryCyan else TextMuted,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = if (isBuilderActive) "Builder: Active" else "Interactive Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isBuilderActive) PrimaryCyan else TextMuted,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            if (isBuilderActive) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CyanSubtle)
                        .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.45f), RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onAddNode() }
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Plus,
                        contentDescription = "Add Node",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = "Add Node",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = "$dynamicNodeCount nodes • $dynamicEdgeCount edges",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontSize = AlgoType.microSize,
                maxLines = 1
            )
        }

        // Right actions: Center View (when panned) + Reset Graph (when modified) + Fullscreen + Done
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            if (isPanned) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onResetPan() }
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Text(
                        text = "Center View",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (canReset) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable { onReset() }
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Refresh,
                        contentDescription = "Reset",
                        tint = AccentPink,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = "Reset Graph",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentPink,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Fullscreen toggle button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(if (isFullscreen) PrimaryCyan else CanvasBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        if (isFullscreen) PrimaryCyan else BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusXs)
                    )
                    .clickable { onToggleFullscreen() }
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                Icon(
                    imageVector = if (isFullscreen) AlgoGlyphs.Close else AlgoGlyphs.Expand,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
                    tint = if (isFullscreen) DarkBackground else PrimaryCyan,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                )
                Text(
                    text = if (isFullscreen) "Exit" else "Fullscreen",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFullscreen) DarkBackground else PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isBuilderActive) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(PrimaryCyan)
                        .clickable { onToggleBuilder() }
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkBackground,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Builder instruction banner overlay. Appears only when [isBuilderActive]
 * is true. Always BottomCenter aligned.
 */
@Composable
fun GraphBuilderBanner(
    isBuilderActive: Boolean,
    modifier: Modifier = Modifier,
    activeTool: GraphTool = GraphTool.MOVE,
    selectedNodeId: String? = null,
    profile: GraphCapabilityProfile? = null
) {
    if (!isBuilderActive) return
    Box(
        modifier = modifier
            .padding(bottom = AlgoTokens.space3)
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(DarkBackground.copy(alpha = 0.90f))
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
    ) {
        val hintText = profile?.hintProvider?.invoke(activeTool, selectedNodeId) ?: when (activeTool) {
            GraphTool.MOVE -> if (selectedNodeId != null) "Selected Node $selectedNodeId • Drag to reposition • Tap empty space to deselect" else "Drag any node to move • Drag background to pan viewport"
            GraphTool.ADD -> if (selectedNodeId != null) "Tap canvas to add node and auto-connect to $selectedNodeId" else "Tap anywhere on empty canvas to plant a new node"
            GraphTool.LINK -> if (selectedNodeId != null) "Selected Node $selectedNodeId: Tap target node to connect" else "Tap or drag from one node to another to create an edge"
            GraphTool.WEIGHT -> "Tap any edge weight pill to cycle weight (1..9)"
            GraphTool.ENDPOINTS -> "Tap node to set START (green) • Tap another to set TARGET (pink) • Tap to clear"
            GraphTool.DELETE -> "Tap any node or edge to delete it"
        }
        Text(
            text = hintText,
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Medium
        )
    }
}
