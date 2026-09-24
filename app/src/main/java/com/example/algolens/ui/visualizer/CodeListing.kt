package com.example.algolens.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.AlgorithmCodeRegistry
import com.example.algolens.data.TraceLanguage
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * Code listing body: header (terminal icon + title) on top, segmented
 * language tabs (Kotlin / Java / Python / C++) on the right, then a
 * `LazyColumn` of syntax-highlighted code lines.
 *
 * The listing shares its [LazyListState] withthe public [CodeTracePane] shell.
 *
 * Per-line `InlineVarChip` badges are rendered inline above each line; they
 * are an internal helper because the legacy `disclosure { … }` block was
 * already moved into the kebab popover in `VisualizerHeader.kt`.
 */
@Composable
fun CodeListing(
    algorithmName: String = "Bubble Sort",
    codeData: AlgorithmCodeRegistry.MultiLangCode,
    highlightedLines: List<AnnotatedString>,
    activeLines: List<Int>,
    variables: Map<String, String>,
    syncPulse: androidx.compose.runtime.State<Float>,
    selectedLanguage: TraceLanguage,
    isExpanded: Boolean = false,
    showHeader: Boolean = true,
    onToggleExpand: () -> Unit = {},
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val fileExtension = remember(selectedLanguage) {
        when (selectedLanguage) {
            TraceLanguage.KOTLIN -> "kt"
            TraceLanguage.JAVA -> "java"
            TraceLanguage.PYTHON -> "py"
            TraceLanguage.CPP -> "cpp"
        }
    }
    val fileName = remember(algorithmName, fileExtension) {
        algorithmName.lowercase().replace(" ", "_").replace("-", "_") + "." + fileExtension
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // Hidden inside the Focus Deck: its attached tabs replace this titlebar.
        if (showHeader) {
            Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(com.example.algolens.ui.theme.CardBackgroundElevated)
                .clickable { onToggleExpand() }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Terminal Window Traffic-Light Dots + File Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.85f))
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.85f))
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.85f))
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Terminal,
                        contentDescription = null,
                        tint = PurpleGlow,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                }
            }

            // Right: Read-Only Preferred Language Badge + Peek/Expand Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SecondaryPurple.copy(alpha = 0.18f))
                        .border(
                            AlgoTokens.strokeHairline,
                            SecondaryPurple.copy(alpha = 0.45f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedLanguage.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        color = PurpleGlow,
                        fontSize = 8.5.sp,
                        lineHeight = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(com.example.algolens.ui.theme.CyanSubtle)
                        .border(
                            AlgoTokens.strokeHairline,
                            com.example.algolens.ui.theme.PrimaryCyan.copy(alpha = 0.4f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 2.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isExpanded) "Collapse v" else "Expand ^",
                        style = MaterialTheme.typography.labelSmall.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        color = com.example.algolens.ui.theme.PrimaryCyan,
                        fontSize = 8.5.sp,
                        lineHeight = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        } // end if (showHeader)

        // Terminal Header Separator Hairline
        if (showHeader) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderSubtle)
            )
        }

        // ── Terminal Code Body ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
            ) {
                itemsIndexed(highlightedLines) { index, lineAnnotated ->
                    val lineNum = index + 1
                    val isActive = lineNum in activeLines
                    val rawLine = codeData.lines.getOrElse(index) { "" }

                    val lineAccent = CodeLineAccent.resolve(rawLine)

                    // Inline variable chips: only variables referenced by this line
                    val lineVars = remember(rawLine, variables) {
                        if (!isActive || variables.isEmpty()) {
                            emptyMap()
                        } else {
                            variables.filterKeys { key ->
                                Regex("(?<![A-Za-z0-9_])${Regex.escape(key)}(?![A-Za-z0-9_])")
                                    .containsMatchIn(rawLine)
                            }.entries.take(4).associate { it.key to it.value }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // ── Inline Variable Chips (mirrored inspector, above line) ──
                        if (lineVars.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 34.dp, top = 2.dp, bottom = 1.dp),
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                            ) {
                                for ((label, value) in lineVars) {
                                    InlineVarChip(
                                        label = label,
                                        value = value,
                                        accent = lineAccent
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isActive) lineAccent.copy(alpha = 0.13f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 10.dp, vertical = 2.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lineNum.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isActive) lineAccent else TextDark,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                fontSize = AlgoType.microSize,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(24.dp)
                            )

                            Text(
                                text = lineAnnotated,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = AlgoType.microSize,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline variable chip badge mirrored directly above active code lines
 * (e.g. `i = 0`, `pivot = 7`). Uses the line's semantic accent so the
 * chip, rail and canvas glow all speak the same functional color.
 */
@Composable
private fun InlineVarChip(
    label: String,
    value: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(accent.copy(alpha = 0.14f))
            .border(AlgoTokens.strokeThin, accent.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = 5.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize,
            fontFamily = FontFamily.Monospace
        )
    }
}
