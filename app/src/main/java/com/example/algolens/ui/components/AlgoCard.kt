package com.example.algolens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.YellowSubtle
import com.example.algolens.ui.theme.AlgoType

/**
 * Immutable, per-algorithm card chrome so a `LazyColumn` rebuild never
 * re-derives icon / color / difficulty styling on the render thread. Keyed
 * on the stable algorithm id, so identical cards (e.g. a re-filtered list)
 * share a single cached instance.
 */
@Immutable
private data class AlgoCardChrome(
    val glyph: ImageVector,
    val accent: Color,
    val diffColor: Color,
    val diffSubtle: Color,
)

/**
 * Algorithm row (M6). A plate in a tray, not a Material list item:
 *
 *  - **Double bezel** — outer shell + inset core plate, hairline strokes only.
 *    No elevation, no fill, no icon chip.
 *  - **Family rail** — a 2dp accent strip on the leading edge carries the
 *    family colour; the glyph is a stroke-1.5 [AlgoGlyphs] mark at 16dp.
 *  - **Name** title step (12sp), complexity + difficulty in label step (10sp).
 *  - **One affordance** — a chevron. The row is the tap target; press physics
 *    is the only feedback (4% scale + accent bloom).
 */
@Composable
fun AlgoCard(
    algo: Algorithm,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    // All card chrome is a pure function of `algo.id`. Cache it so a LazyColumn
    // rebuild (every card recomposes as it scrolls into the viewport) never
    // re-runs color parsing or string lookups on the render thread.
    val chrome = remember(algo.id) {
        val (diffColor, diffSubtle) = when (algo.difficulty.lowercase()) {
            "easy" -> Pair(AccentGreen, GreenSubtle)
            "medium" -> Pair(AccentYellow, YellowSubtle)
            else -> Pair(AccentRed, RedSubtle)
        }
        AlgoCardChrome(
            glyph = when (algo.category.lowercase()) {
                "sorting" -> AlgoGlyphs.SwapVert
                "searching" -> AlgoGlyphs.Search
                "data structures" -> AlgoGlyphs.Stack
                "graph traversal" -> AlgoGlyphs.Tree
                else -> AlgoGlyphs.Grid
            },
            accent = try {
                Color(algo.colorHex.toColorInt())
            } catch (_: Exception) {
                PrimaryCyan
            },
            diffColor = diffColor,
            diffSubtle = diffSubtle,
        )
    }

    val shape = RoundedCornerShape(AlgoTokens.bezelRadius)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .entryCascade(index = index)
            .clip(shape)
            .background(AlgoTokens.surfaceSunken)
            .pressPhysics(shape = shape, accent = chrome.accent)
            .clickable { onClick() }
            .padding(AlgoTokens.bezelGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Core plate ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.bezelInnerRadius))
                .background(AlgoTokens.surfaceCard)
                .padding(
                    start = AlgoTokens.space5,
                    end = AlgoTokens.space5,
                    top = AlgoTokens.space4,
                    bottom = AlgoTokens.space4
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = chrome.glyph,
                contentDescription = algo.category,
                tint = chrome.accent.copy(alpha = 0.75f),
                modifier = Modifier.size(AlgoTokens.inlineIconLg)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = AlgoTokens.space5)
            ) {
                Text(
                    text = algo.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.padding(top = AlgoTokens.space1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    Text(
                        text = algo.timeComplexity,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextNavy
                    )
                    // Difficulty = 6dp dot + label. Never a filled badge.
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space3)
                            .clip(CircleShape)
                            .background(chrome.diffSubtle)
                            .border(AlgoTokens.strokeThin, chrome.diffColor.copy(alpha = 0.7f), CircleShape)
                    )
                    Text(
                        text = algo.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = chrome.diffColor
                    )
                }
            }

            Icon(
                imageVector = AlgoGlyphs.ChevronRight,
                contentDescription = null,
                tint = TextDark,
                modifier = Modifier.size(AlgoTokens.inlineIconMd)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun AlgoCardPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        Box(modifier = Modifier.padding(AlgoTokens.space6)) {
            AlgoCard(
                algo = com.example.algolens.data.SampleData.algorithms.first(),
                onClick = {}
            )
        }
    }
}
