package com.avia.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.avia.ui.components.AlgoHairline
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.TextSecondary

enum class TableAlignment {
    LEFT,
    CENTER,
    RIGHT
}

@Immutable
data class ParsedTable(
    val headers: List<String>,
    val rows: List<List<String>>,
    val alignments: List<TableAlignment>
)

/**
 * Parses markdown table text into [ParsedTable].
 */
fun parseMarkdownTable(lines: List<String>): ParsedTable? {
    if (lines.size < 2) return null

    val headerLine = lines[0].trim()
    val delimiterLine = lines[1].trim()

    if (!headerLine.startsWith("|") || !headerLine.endsWith("|")) return null
    if (!delimiterLine.startsWith("|") || !delimiterLine.endsWith("|")) return null

    val rawHeaders = headerLine
        .removePrefix("|")
        .removeSuffix("|")
        .split("|")
        .map { it.trim() }

    if (rawHeaders.isEmpty()) return null

    val rawDelimiters = delimiterLine
        .removePrefix("|")
        .removeSuffix("|")
        .split("|")
        .map { it.trim() }

    val alignments = rawDelimiters.map { d ->
        when {
            d.startsWith(":") && d.endsWith(":") -> TableAlignment.CENTER
            d.endsWith(":") -> TableAlignment.RIGHT
            else -> TableAlignment.LEFT
        }
    }

    val rows = mutableListOf<List<String>>()
    for (i in 2 until lines.size) {
        val rowLine = lines[i].trim()
        if (!rowLine.startsWith("|") || !rowLine.endsWith("|")) continue

        val cells = rowLine
            .removePrefix("|")
            .removeSuffix("|")
            .split("|")
            .map { it.trim() }

        // Pad with empty cells if row has fewer columns
        val paddedRow = List(rawHeaders.size) { colIdx ->
            cells.getOrElse(colIdx) { "" }
        }
        rows.add(paddedRow)
    }

    return ParsedTable(
        headers = rawHeaders,
        rows = rows,
        alignments = alignments
    )
}

/**
 * Native Jetpack Compose Markdown table renderer styled with AlgoLens tech-noir tokens.
 */
@Composable
fun ChatTableBlock(
    table: ParsedTable,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
            .horizontalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(vertical = AlgoTokens.space2),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                table.headers.forEachIndexed { idx, header ->
                    val alignment = table.alignments.getOrElse(idx) { TableAlignment.LEFT }
                    Box(
                        modifier = Modifier
                            .widthIn(min = AlgoTokens.minTouchTarget * 2)
                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                        contentAlignment = when (alignment) {
                            TableAlignment.LEFT -> Alignment.CenterStart
                            TableAlignment.CENTER -> Alignment.Center
                            TableAlignment.RIGHT -> Alignment.CenterEnd
                        }
                    ) {
                        Text(
                            text = header.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = AlgoType.trackSection,
                            textAlign = when (alignment) {
                                TableAlignment.LEFT -> TextAlign.Start
                                TableAlignment.CENTER -> TextAlign.Center
                                TableAlignment.RIGHT -> TextAlign.End
                            }
                        )
                    }
                }
            }

            AlgoHairline(color = BorderSubtle)

            // Data Rows
            table.rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AlgoTokens.space2),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEachIndexed { colIdx, cell ->
                        val alignment = table.alignments.getOrElse(colIdx) { TableAlignment.LEFT }
                        Box(
                            modifier = Modifier
                                .widthIn(min = AlgoTokens.minTouchTarget * 2)
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                            contentAlignment = when (alignment) {
                                TableAlignment.LEFT -> Alignment.CenterStart
                                TableAlignment.CENTER -> Alignment.Center
                                TableAlignment.RIGHT -> Alignment.CenterEnd
                            }
                        ) {
                            Text(
                                text = parseMarkdownInline(cell),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = AlgoType.labelSize,
                                lineHeight = AlgoType.leadingLabel,
                                textAlign = when (alignment) {
                                TableAlignment.LEFT -> TextAlign.Start
                                TableAlignment.CENTER -> TextAlign.Center
                                TableAlignment.RIGHT -> TextAlign.End
                            }
                            )
                        }
                    }
                }

                if (rowIndex < table.rows.size - 1) {
                    AlgoHairline(color = BorderSubtle.copy(alpha = 0.5f))
                }
            }
        }
    }
}
