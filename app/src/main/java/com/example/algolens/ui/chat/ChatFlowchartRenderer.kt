package com.example.algolens.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.YellowSubtle

enum class FlowchartShape {
    PROCESS,
    DECISION,
    TERMINAL
}

@Immutable
data class FlowchartNode(
    val id: String,
    val label: String,
    val shape: FlowchartShape = FlowchartShape.PROCESS
)

@Immutable
data class FlowchartEdge(
    val fromId: String,
    val toId: String,
    val label: String? = null
)

@Immutable
data class ParsedFlowchart(
    val title: String = "ALGORITHM CONTROL FLOW",
    val nodes: List<FlowchartNode>,
    val edges: List<FlowchartEdge>
)

/**
 * Parses Mermaid / Flowchart text blocks into [ParsedFlowchart].
 */
fun parseFlowchart(text: String): ParsedFlowchart? {
    val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
    if (lines.isEmpty()) return null

    val nodeMap = linkedMapOf<String, FlowchartNode>()
    val edges = mutableListOf<FlowchartEdge>()

    fun parseNodeDef(token: String): FlowchartNode? {
        val t = token.trim()
        // Process: A[Label]
        if (t.contains("[") && t.endsWith("]")) {
            val id = t.substringBefore("[").trim()
            val label = t.substringAfter("[").removeSuffix("]").trim()
            return FlowchartNode(id.ifEmpty { label }, label, FlowchartShape.PROCESS)
        }
        // Decision: A{Label}
        if (t.contains("{") && t.endsWith("}")) {
            val id = t.substringBefore("{").trim()
            val label = t.substringAfter("{").removeSuffix("}").trim()
            return FlowchartNode(id.ifEmpty { label }, label, FlowchartShape.DECISION)
        }
        // Terminal: A(Label)
        if (t.contains("(") && t.endsWith(")")) {
            val id = t.substringBefore("(").trim()
            val label = t.substringAfter("(").removeSuffix(")").trim()
            return FlowchartNode(id.ifEmpty { label }, label, FlowchartShape.TERMINAL)
        }
        if (t.isNotEmpty() && !t.contains(" ") && t.all { it.isLetterOrDigit() || it == '_' }) {
            return FlowchartNode(t, t, FlowchartShape.PROCESS)
        }
        return null
    }

    fun mergeNode(existing: FlowchartNode?, incoming: FlowchartNode): FlowchartNode {
        if (existing == null) return incoming
        val incomingIsRich = incoming.label != incoming.id || incoming.shape != FlowchartShape.PROCESS
        val existingIsRich = existing.label != existing.id || existing.shape != FlowchartShape.PROCESS
        return if (incomingIsRich || !existingIsRich) incoming else existing
    }

    for (line in lines) {
        // Skip directives like "graph TD" or "flowchart TD"
        if (line.startsWith("graph") || line.startsWith("flowchart")) continue

        // Connection line: A --> B or A -->|Label| B or A -> B
        val arrowPattern = Regex("(-->|->)")
        if (arrowPattern.containsMatchIn(line)) {
            val parts = line.split(arrowPattern)
            if (parts.size >= 2) {
                var currentFrom = parts[0].trim()
                for (stepIdx in 1 until parts.size) {
                    val rawTarget = parts[stepIdx].trim()
                    var edgeLabel: String? = null
                    var actualTarget = rawTarget

                    // Extract edge label |condition|
                    if (rawTarget.startsWith("|") && rawTarget.indexOf("|", 1) != -1) {
                        val endPipe = rawTarget.indexOf("|", 1)
                        edgeLabel = rawTarget.substring(1, endPipe).trim()
                        actualTarget = rawTarget.substring(endPipe + 1).trim()
                    }

                    val fromNode = parseNodeDef(currentFrom) ?: FlowchartNode(currentFrom, currentFrom)
                    val toNode = parseNodeDef(actualTarget) ?: FlowchartNode(actualTarget, actualTarget)

                    nodeMap[fromNode.id] = mergeNode(nodeMap[fromNode.id], fromNode)
                    nodeMap[toNode.id] = mergeNode(nodeMap[toNode.id], toNode)

                    edges.add(FlowchartEdge(fromNode.id, toNode.id, edgeLabel))
                    currentFrom = actualTarget
                }
                continue
            }
        }

        // Standalone node definition
        val singleNode = parseNodeDef(line)
        if (singleNode != null) {
            nodeMap[singleNode.id] = mergeNode(nodeMap[singleNode.id], singleNode)
        }
    }

    if (nodeMap.isEmpty()) return null

    return ParsedFlowchart(
        nodes = nodeMap.values.toList(),
        edges = edges
    )
}

/**
 * Native Jetpack Compose Flowchart diagram renderer.
 * Visualizes algorithmic workflows, branching conditions, and execution pipelines.
 */
@Composable
fun ChatFlowchartBlock(
    flowchart: ParsedFlowchart,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
            .padding(AlgoTokens.space4)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Nodes,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = flowchart.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                }

                Text(
                    text = "${flowchart.nodes.size} steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark,
                    fontSize = AlgoType.microSize
                )
            }

            // Flow Diagram Sequence
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                flowchart.nodes.forEachIndexed { index, node ->
                    FlowchartNodeCard(node = node)

                    // Find outgoing edge if present
                    val outgoingEdge = flowchart.edges.firstOrNull { it.fromId == node.id }
                    if (index < flowchart.nodes.size - 1 || outgoingEdge != null) {
                        FlowchartEdgeConnector(label = outgoingEdge?.label)
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowchartNodeCard(
    node: FlowchartNode,
    modifier: Modifier = Modifier
) {
    val shapeRadius = when (node.shape) {
        FlowchartShape.TERMINAL -> AlgoTokens.radiusLg
        FlowchartShape.DECISION -> AlgoTokens.radiusXs
        FlowchartShape.PROCESS -> AlgoTokens.radiusSm
    }

    val borderTint = when (node.shape) {
        FlowchartShape.TERMINAL -> PrimaryCyan.copy(alpha = 0.5f)
        FlowchartShape.DECISION -> AccentYellow.copy(alpha = 0.5f)
        FlowchartShape.PROCESS -> BorderSubtle
    }

    val bgTint = when (node.shape) {
        FlowchartShape.TERMINAL -> CyanSubtle
        FlowchartShape.DECISION -> YellowSubtle
        FlowchartShape.PROCESS -> CardBackground
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(shapeRadius))
            .background(bgTint)
            .border(AlgoTokens.strokeThin, borderTint, RoundedCornerShape(shapeRadius))
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                // Type Badge / Glyph
                val badgeIcon = when (node.shape) {
                    FlowchartShape.TERMINAL -> AlgoGlyphs.Spark
                    FlowchartShape.DECISION -> AlgoGlyphs.Help
                    FlowchartShape.PROCESS -> AlgoGlyphs.Terminal
                }
                val iconTint = when (node.shape) {
                    FlowchartShape.TERMINAL -> PrimaryCyan
                    FlowchartShape.DECISION -> AccentYellow
                    FlowchartShape.PROCESS -> SecondaryPurple
                }

                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(AlgoTokens.inlineIconMd)
                )

                Text(
                    text = node.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    fontSize = AlgoType.labelSize,
                    fontWeight = if (node.shape == FlowchartShape.DECISION) FontWeight.Bold else FontWeight.Medium,
                    lineHeight = AlgoType.leadingLabel
                )
            }

            if (node.shape == FlowchartShape.DECISION) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(YellowSubtle)
                        .border(AlgoTokens.strokeThin, AccentYellow.copy(alpha = 0.3f), RoundedCornerShape(AlgoTokens.radiusXxs))
                        .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = "BRANCH",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentYellow,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FlowchartEdgeConnector(
    label: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = AlgoTokens.space1),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        if (!label.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .background(PurpleSubtle)
                    .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusXxs))
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleGlow,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Icon(
            imageVector = AlgoGlyphs.ArrowDown,
            contentDescription = null,
            tint = BorderCyan,
            modifier = Modifier.size(AlgoTokens.inlineIconMd)
        )
    }
}
