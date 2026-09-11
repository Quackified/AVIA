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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
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

/**
 * Immutable, per-algorithm card chrome so a `LazyColumn` rebuild never
 * re-derives icon / color / difficulty styling on the render thread. Keyed
 * on the stable algorithm id, so identical cards (e.g. a re-filtered list)
 * share a single cached instance.
 */
@Immutable
private data class AlgoCardChrome(
    val icon: ImageVector,
    val tint: Color,
    val diffColor: Color,
    val diffBg: Color,
)

@Composable
fun AlgoCard(
    algo: Algorithm,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // All card chrome is a pure function of `algo.id`. Cache it so a LazyColumn
    // rebuild (every card recomposes as it scrolls into the viewport) never
    // re-runs color parsing or string lookups on the render thread.
    val chrome = remember(algo.id) {
        val (diffColor, diffBg) = when (algo.difficulty.lowercase()) {
            "easy" -> Pair(AccentGreen, GreenSubtle)
            "medium" -> Pair(AccentYellow, YellowSubtle)
            else -> Pair(AccentRed, RedSubtle)
        }
        AlgoCardChrome(
            icon = when (algo.category.lowercase()) {
                "sorting" -> Icons.Default.SwapVert
                "searching" -> Icons.Default.Search
                "data structures" -> Icons.Default.Layers
                "graph traversal" -> Icons.Default.AccountTree
                else -> Icons.Default.GridView
            },
            tint = try {
                Color(algo.colorHex.toColorInt())
            } catch (_: Exception) {
                PrimaryCyan
            },
            diffColor = diffColor,
            diffBg = diffBg,
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
            .clickable { onClick() }
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space5 - AlgoTokens.space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(AlgoTokens.iconButtonLg)
                .clip(RoundedCornerShape(AlgoTokens.radiusSm + AlgoTokens.space1))
                .background(chrome.tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = chrome.icon,
                contentDescription = algo.category,
                tint = chrome.tint,
                modifier = Modifier.size(AlgoTokens.space6)
            )
        }

        // Details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = algo.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(AlgoTokens.space5 - AlgoTokens.space1)
                )
            }

            Row(
                modifier = Modifier.padding(top = AlgoTokens.space1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                Text(
                    text = algo.timeComplexity,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextNavy
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                        .background(chrome.diffBg)
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = algo.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = chrome.diffColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
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
