package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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
import com.example.algolens.ui.theme.TextMuted

/**
 * Floating glass toolbar for the node graph/tree editor and fullscreen mode.
 * Filters tools deterministically using [profile.allowedTools].
 * Keeps viewport utilities (Center, Fit, Reset, Fullscreen) accessible even in read-only mode.
 */
@Composable
fun GraphFloatingToolbar(
    profile: GraphCapabilityProfile,
    activeTool: GraphTool,
    onToolSelected: (GraphTool) -> Unit,
    onCenterView: () -> Unit,
    onFitToScreen: () -> Unit,
    onResetGraph: () -> Unit,
    canReset: Boolean,
    modifier: Modifier = Modifier,
    isPannedOrZoomed: Boolean = false
) {
    val allowedTools = GraphTool.entries.filter { it in profile.allowedTools }

    Box(
        modifier = modifier
            .padding(AlgoTokens.space2)
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(DarkBackground.copy(alpha = 0.94f))
            .border(
                AlgoTokens.strokeThin,
                PrimaryCyan.copy(alpha = 0.35f),
                RoundedCornerShape(AlgoTokens.radiusMd)
            )
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            // Profile-filtered tools
            allowedTools.forEach { tool ->
                val isSelected = (tool == activeTool)
                val glyph = when (tool) {
                    GraphTool.MOVE -> AlgoGlyphs.Tap
                    GraphTool.ADD -> AlgoGlyphs.Plus
                    GraphTool.LINK -> AlgoGlyphs.Nodes
                    GraphTool.WEIGHT -> AlgoGlyphs.Speed
                    GraphTool.ENDPOINTS -> AlgoGlyphs.Target
                    GraphTool.DELETE -> AlgoGlyphs.Trash
                }
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = AlgoTokens.minTouchTarget, minHeight = AlgoTokens.minTouchTarget)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (isSelected) CyanSubtle else CanvasBackground)
                        .border(
                            AlgoTokens.strokeThin,
                            if (isSelected) PrimaryCyan else BorderSubtle,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .semantics {
                            this.role = Role.Tab
                            this.selected = isSelected
                            this.contentDescription = "${tool.label}. ${tool.tooltip}"
                        }
                        .clickable { onToolSelected(tool) }
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Icon(
                            imageVector = glyph,
                            contentDescription = null,
                            tint = if (isSelected) PrimaryCyan else TextMuted,
                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                        )
                        Text(
                            text = tool.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) PrimaryCyan else TextMuted,
                            fontSize = AlgoType.microSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            // Divider between tools and viewport actions (shown if any edit tools are visible)
            if (allowedTools.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(width = AlgoTokens.strokeThin, height = AlgoTokens.space7 - AlgoTokens.space2)
                        .background(BorderSubtle)
                )
            }

            // Center View
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = AlgoTokens.minTouchTarget, minHeight = AlgoTokens.minTouchTarget)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(CanvasBackground)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                    .semantics {
                        this.role = Role.Button
                        this.contentDescription = "Center graph in viewport"
                    }
                    .clickable { onCenterView() }
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Target,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = "Center",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            // Fit to Screen
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = AlgoTokens.minTouchTarget, minHeight = AlgoTokens.minTouchTarget)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(CanvasBackground)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                    .semantics {
                        this.role = Role.Button
                        this.contentDescription = "Fit graph to screen with margins"
                    }
                    .clickable { onFitToScreen() }
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Expand,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = "Fit",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            // Reset Graph (visible when modifications exist)
            if (canReset) {
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = AlgoTokens.minTouchTarget, minHeight = AlgoTokens.minTouchTarget)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .semantics {
                            this.role = Role.Button
                            this.contentDescription = "Reset graph and tree modifications"
                        }
                        .clickable { onResetGraph() }
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Refresh,
                            contentDescription = null,
                            tint = AccentPink,
                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                        )
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentPink,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Backward-compatible overload for [GraphFloatingToolbar].
 */
@Composable
fun GraphFloatingToolbar(
    activeTool: GraphTool,
    onToolSelected: (GraphTool) -> Unit,
    onCenterView: () -> Unit,
    onResetGraph: () -> Unit,
    canReset: Boolean,
    isPanned: Boolean,
    modifier: Modifier = Modifier
) {
    GraphFloatingToolbar(
        profile = GraphCapabilityProfile.dijkstraProfile(),
        activeTool = activeTool,
        onToolSelected = onToolSelected,
        onCenterView = onCenterView,
        onFitToScreen = onCenterView,
        onResetGraph = onResetGraph,
        canReset = canReset,
        isPannedOrZoomed = isPanned,
        modifier = modifier
    )
}
