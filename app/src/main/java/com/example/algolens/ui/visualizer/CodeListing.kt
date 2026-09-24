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
    codeData: AlgorithmCodeRegistry.MultiLangCode,
    highlightedLines: List<AnnotatedString>,
    activeLines: List<Int>,
    variables: Map<String, String>,
    syncPulse: androidx.compose.runtime.State<Float>,
    selectedLanguage: TraceLanguage,
    onLanguageSelected: (TraceLanguage) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Header with Multi-Language Tabs (no border)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Terminal,
                    contentDescription = null,
                    tint = PurpleGlow,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Code Trace",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.microSize
                )
            }

            // ── Borderless Segmented Language Tabs (Kotlin, Java, Python, C++) ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (lang in TraceLanguage.entries) {
                    val isSelected = selectedLanguage == lang
                    val animatedBg by animateColorAsState(
                        targetValue = if (isSelected) SecondaryPurple.copy(alpha = 0.25f) else Color.Transparent,
                        animationSpec = tween(120),
                        label = "langTabBg_${lang.name}"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(animatedBg)
                            .clickable { onLanguageSelected(lang) }
                            .padding(horizontal = 7.dp, vertical = 2.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lang.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) PurpleGlow else TextMuted,
                            fontSize = AlgoType.microSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ── Centered, Borderless Syntax-Highlighted Code Stack ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .width(316.dp)
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.Start
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
                                    .padding(start = 26.dp, top = 2.dp),
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
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isActive) lineAccent.copy(alpha = 0.12f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 8.dp, vertical = 2.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lineNum.toString().padStart(2, ' '),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isActive) lineAccent else TextDark,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                fontSize = AlgoType.microSize,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(22.dp)
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
