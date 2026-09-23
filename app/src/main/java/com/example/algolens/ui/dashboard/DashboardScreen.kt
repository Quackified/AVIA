package com.example.algolens.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.AlgoCard
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.EntryCascadeProvider
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * The instrument's front panel. Not a marketing landing page: a dense,
 * quiet catalogue with a live count, a search well, a 2×2 family selector
 * and the algorithm rows. Nothing here exists to persuade — it exists to
 * get the user into a visualizer in one tap (PRODUCT.md §Principles).
 */
@Composable
fun DashboardScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredAlgorithms = remember(searchQuery, selectedCategory) {
        SampleData.algorithms.filter { algo ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                else -> algo.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isEmpty() ||
                    algo.name.contains(searchQuery, ignoreCase = true) ||
                    algo.category.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Top header: mark, wordmark, catalogue count ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    com.example.algolens.ui.components.AviaLogo(
                        size = AlgoTokens.inlineIconLg
                    )
                    Text(
                        text = "AVIA",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }
                Text(
                    text = "${filteredAlgorithms.size} algorithms available",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyanSubtle)
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AlgoGlyphs.TrendingUp,
                    contentDescription = "Trending",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ── 2. Search well (sunken, hairline, one glyph) ──
        val searchShape = RoundedCornerShape(AlgoTokens.radiusMd)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space5)
                .clip(searchShape)
                .background(AlgoTokens.surfaceSunken)
                .border(AlgoTokens.strokeHairline, AlgoTokens.strokeBorderSubtle, searchShape)
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            Icon(
                imageVector = AlgoGlyphs.Search,
                contentDescription = "Search",
                tint = TextMuted,
                modifier = Modifier.size(AlgoTokens.inlineIconMd)
            )
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(PrimaryCyan),
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search algorithms",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    innerTextField()
                }
            )
        }

        // ── 3. Family selector: 2×2 instrument tiles (not chips) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space6)
        ) {
            val families = listOf(
                Triple("Sorting", AlgoGlyphs.SwapVert, PrimaryCyan),
                Triple("Searching", AlgoGlyphs.Search, AlgoTokens.accentPurple),
                Triple("Data Structures", AlgoGlyphs.Stack, AccentOrange),
                Triple("Graph Traversal", AlgoGlyphs.Tree, AlgoTokens.accentGreen),
            )
            families.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                ) {
                    pair.forEach { (label, glyph, accent) ->
                        FamilyTile(
                            label = label,
                            glyph = glyph,
                            accent = accent,
                            count = SampleData.algorithms.count {
                                it.category.equals(label, ignoreCase = true)
                            },
                            selected = selectedCategory == label,
                            onClick = {
                                selectedCategory = if (selectedCategory == label) "All" else label
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Box(modifier = Modifier.height(AlgoTokens.space4))
            }
        }

        // ── 4. Results line ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredAlgorithms.size} ${if (filteredAlgorithms.size == 1) "result" else "results"}",
                style = MaterialTheme.typography.labelSmall,
                color = TextDark
            )
            Text(
                text = if (selectedCategory == "All") "All Types" else selectedCategory,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }


        // ── 5. Algorithm rows ──
        //
        // M5 gate: the rows rise-and-fade as a single one-shot cascade when the
        // screen mounts. EntryCascadeProvider closes that window after
        // AlgoTokens.entryWindowMs, after which `entryCascade` hands the modifier
        // straight back — so scrolling a row into the viewport is a plain layout
        // pass with no Animatable, no coroutine and no render layer. Without this
        // wrapper every row still mounted at alpha 0 and animated, which is what
        // made a flick through the list pop and shift.
        EntryCascadeProvider {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = AlgoTokens.space6,
                    end = AlgoTokens.space6,
                    top = AlgoTokens.space4,
                    bottom = AlgoTokens.space6
                ),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                if (filteredAlgorithms.isEmpty()) {
                    item {
                        EmptyState(
                            query = searchQuery,
                            onClear = {
                                searchQuery = ""
                                selectedCategory = "All"
                            }
                        )
                    }
                } else {
                    itemsIndexed(filteredAlgorithms, key = { _, algo -> algo.id }) { index, algo ->
                        AlgoCard(
                            algo = algo,
                            onClick = { onAlgorithmClick(algo) },
                            index = index
                        )
                    }
                }
            }
        }
    }
}

/**
 * Family tile (M6): a double-bezel instrument key. Selected = accent-tinted
 * plate, accent glyph and a 2dp accent underline. Never a pill chip.
 */
@Composable
private fun FamilyTile(
    label: String,
    glyph: ImageVector,
    accent: Color,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AlgoTokens.bezelRadius)
    Column(
        modifier = modifier
            .height(AlgoTokens.space8 * 2)
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.12f) else AlgoTokens.surfaceSunken)
            .pressPhysics(shape = shape, accent = accent)
            .clickable { onClick() }
            .padding(AlgoTokens.bezelGap)
            .clip(RoundedCornerShape(AlgoTokens.bezelInnerRadius))
            .background(if (selected) accent.copy(alpha = 0.08f) else AlgoTokens.surfaceCard)
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            Icon(
                imageVector = glyph,
                contentDescription = null,
                tint = if (selected) accent else TextMuted,
                modifier = Modifier.size(AlgoTokens.inlineIconLg)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) accent else TextDark
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) TextPrimary else TextMuted,
            maxLines = 2
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlgoTokens.strokeActive)
                .background(if (selected) accent else Color.Transparent)
        )
    }
}

/**
 * Empty state. Never a dead end — it names what failed and offers the way
 * back.
 */
@Composable
private fun EmptyState(query: String, onClear: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AlgoTokens.space8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        Icon(
            imageVector = AlgoGlyphs.Search,
            contentDescription = null,
            tint = TextDark,
            modifier = Modifier.size(AlgoTokens.space7)
        )
        Text(
            text = if (query.isEmpty()) "No algorithms in this family" else "No match for \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Text(
            text = "Clear filters",
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan,
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .clickable(onClick = onClear)
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    AlgoLensTheme {
        DashboardScreen(
            onAlgorithmClick = {}
        )
    }
}

