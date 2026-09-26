package com.example.algolens.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.algolens.data.TraceLanguage
import com.example.algolens.ui.visualizer.SyntaxHighlighter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.JetBrainsMono
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Callout alert types supported in the AI Chatbox (GitHub-style).
 */
enum class CalloutType(
    val label: String,
    val accent: Color,
    val fill: Color,
    val glyph: ImageVector
) {
    NOTE("NOTE", PrimaryCyan, CyanSubtle, AlgoGlyphs.Info),
    TIP("TIP", AccentGreen, GreenSubtle, AlgoGlyphs.Lightbulb),
    WARNING("WARNING", AccentYellow, YellowSubtle, AlgoGlyphs.Warning),
    CAUTION("CAUTION", AccentRed, RedSubtle, AlgoGlyphs.Alert),
    IMPORTANT("IMPORTANT", SecondaryPurple, PurpleSubtle, AlgoGlyphs.Spark)
}

/**
 * Parses markdown inline formatting (**bold**, `inline code`, *italic*)
 * into an [AnnotatedString] conforming to the AlgoLens design tokens.
 */
fun parseMarkdownInline(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val n = text.length

        while (i < n) {
            // Bold: **text**
            if (i + 1 < n && text[i] == '*' && text[i + 1] == '*') {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold))
                    append(text.substring(i + 2, end))
                    pop()
                    i = end + 2
                    continue
                }
            }

            // Inline code: `code`
            if (text[i] == '`') {
                val end = text.indexOf('`', i + 1)
                if (end != -1) {
                    pushStyle(
                        SpanStyle(
                            color = PrimaryCyan,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.SemiBold,
                            background = CyanSubtle
                        )
                    )
                    append(text.substring(i + 1, end))
                    pop()
                    i = end + 1
                    continue
                }
            }

            // Italic: *text*
            if (text[i] == '*' && (i + 1 >= n || text[i + 1] != '*')) {
                val end = text.indexOf('*', i + 1)
                if (end != -1 && (end + 1 >= n || text[end + 1] != '*')) {
                    pushStyle(SpanStyle(color = TextSecondary, fontStyle = FontStyle.Italic))
                    append(text.substring(i + 1, end))
                    pop()
                    i = end + 1
                    continue
                }
            }

            append(text[i])
            i++
        }
    }
}

/**
 * High-craft tech-noir block markdown renderer for AI assistant messages.
 * Handles:
 *  - Headers (###, ##)
 *  - Tables (| Col 1 | Col 2 |)
 *  - Flowcharts & DAGs (```flowchart, ```mermaid, ```graph)
 *  - Callout alerts (> [!NOTE], > [!TIP], > [!WARNING], > [!IMPORTANT], > [!CAUTION])
 *  - Bullet lists (•, -)
 *  - Embedded code blocks
 *  - Inline spans (**bold**, `code`, *italic*)
 */
@Composable
fun ChatMarkdownMessage(
    content: String,
    modifier: Modifier = Modifier
) {
    val rawLines = content.lines()
    val total = rawLines.size
    var lineIdx = 0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        while (lineIdx < total) {
            val line = rawLines[lineIdx]
            val trimmed = line.trim()

            // ── 1. Code Block Fence (```) ──
            if (trimmed.startsWith("```")) {
                val fenceDirective = trimmed.removePrefix("```").trim().lowercase()
                val codeLines = mutableListOf<String>()
                lineIdx++
                while (lineIdx < total && !rawLines[lineIdx].trim().startsWith("```")) {
                    codeLines.add(rawLines[lineIdx])
                    lineIdx++
                }
                if (lineIdx < total) lineIdx++ // skip closing ```

                val blockText = codeLines.joinToString("\n")
                val isFlowchartDirective = fenceDirective in listOf("flowchart", "mermaid", "graph")
                val parsedFlowchart = if (isFlowchartDirective) parseFlowchart(blockText) else null

                if (parsedFlowchart != null) {
                    ChatFlowchartBlock(flowchart = parsedFlowchart)
                } else {
                    ChatEmbeddedCodeBlock(code = blockText, language = fenceDirective)
                }
                continue
            }

            // ── 2. Markdown Table (| ... |) ──
            if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
                val tableLines = mutableListOf<String>()
                while (lineIdx < total && rawLines[lineIdx].trim().startsWith("|") && rawLines[lineIdx].trim().endsWith("|")) {
                    tableLines.add(rawLines[lineIdx].trim())
                    lineIdx++
                }

                val table = parseMarkdownTable(tableLines)
                if (table != null) {
                    ChatTableBlock(table = table)
                } else {
                    // Fallback to plain lines if malformed table
                    tableLines.forEach { tLine ->
                        Text(
                            text = parseMarkdownInline(tLine),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = AlgoType.bodySize,
                            lineHeight = AlgoType.leadingBodyRelaxed
                        )
                    }
                }
                continue
            }

            // ── 3. Callout Alerts (> [!TIP], > [!WARNING], etc.) ──
            if (trimmed.startsWith("> [!") && trimmed.contains("]")) {
                val tag = trimmed.substringAfter("> [!").substringBefore("]").uppercase()
                val calloutType = when (tag) {
                    "NOTE" -> CalloutType.NOTE
                    "TIP" -> CalloutType.TIP
                    "WARNING" -> CalloutType.WARNING
                    "CAUTION" -> CalloutType.CAUTION
                    "IMPORTANT" -> CalloutType.IMPORTANT
                    else -> CalloutType.NOTE
                }

                val calloutLines = mutableListOf<String>()
                // Add remainder of first line if any
                val firstLineRest = trimmed.substringAfter("]").trim()
                if (firstLineRest.isNotEmpty()) calloutLines.add(firstLineRest)
                lineIdx++

                while (lineIdx < total && rawLines[lineIdx].trim().startsWith(">")) {
                    val cLine = rawLines[lineIdx].trim().removePrefix(">").trim()
                    calloutLines.add(cLine)
                    lineIdx++
                }

                ChatCalloutBlock(
                    type = calloutType,
                    body = calloutLines.joinToString("\n")
                )
                continue
            }

            // ── 4. Blank Lines ──
            if (trimmed.isEmpty()) {
                Spacer(modifier = Modifier.height(AlgoTokens.space1))
                lineIdx++
                continue
            }

            // ── 5. Section Headers (### or ##) ──
            if (trimmed.startsWith("### ")) {
                val headerText = trimmed.removePrefix("### ").trim()
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.labelSize,
                    letterSpacing = AlgoType.trackSection,
                    modifier = Modifier.padding(top = AlgoTokens.space2, bottom = AlgoTokens.space1)
                )
                lineIdx++
                continue
            }

            if (trimmed.startsWith("## ")) {
                val headerText = trimmed.removePrefix("## ").trim()
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.bodySize,
                    letterSpacing = AlgoType.trackSection,
                    modifier = Modifier.padding(top = AlgoTokens.space2, bottom = AlgoTokens.space1)
                )
                lineIdx++
                continue
            }

            // ── 6. Bullet Items (• or -) ──
            if (trimmed.startsWith("• ") || trimmed.startsWith("- ")) {
                val bulletContent = trimmed.substring(2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.bodySize
                    )
                    Text(
                        text = parseMarkdownInline(bulletContent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = AlgoType.bodySize,
                        lineHeight = AlgoType.leadingBodyRelaxed,
                        modifier = Modifier.weight(1f)
                    )
                }
                lineIdx++
                continue
            }

            // ── 7. Normal Prose Paragraphs ──
            Text(
                text = parseMarkdownInline(line),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = AlgoType.bodySize,
                lineHeight = AlgoType.leadingBodyRelaxed
            )
            lineIdx++
        }
    }
}

/**
 * Callout alert card for tips, notes, warnings, and caveats.
 */
@Composable
private fun ChatCalloutBlock(
    type: CalloutType,
    body: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(type.fill)
            .border(AlgoTokens.strokeThin, type.accent.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusSm))
            .padding(AlgoTokens.space4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Icon(
                    imageVector = type.glyph,
                    contentDescription = null,
                    tint = type.accent,
                    modifier = Modifier.size(AlgoTokens.inlineIconMd)
                )
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = type.accent,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection
                )
            }

            if (body.isNotBlank()) {
                Text(
                    text = parseMarkdownInline(body),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    fontSize = AlgoType.labelSize,
                    lineHeight = AlgoType.leadingLabel
                )
            }
        }
    }
}

@Composable
private fun ChatEmbeddedCodeBlock(
    code: String,
    language: String = "",
    modifier: Modifier = Modifier
) {
    val traceLang = when (language.lowercase().trim()) {
        "kotlin", "kt" -> TraceLanguage.KOTLIN
        "python", "py" -> TraceLanguage.PYTHON
        "java" -> TraceLanguage.JAVA
        "c++", "cpp" -> TraceLanguage.CPP
        else -> TraceLanguage.KOTLIN
    }
    val highlighted = remember(code, traceLang) {
        SyntaxHighlighter.highlight(code, traceLang)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
            .padding(AlgoTokens.space3)
    ) {
        Text(
            text = highlighted,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontFamily = JetBrainsMono,
            fontSize = AlgoType.microSize,
            lineHeight = AlgoType.leadingMicroRelaxed,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        )
    }
}
