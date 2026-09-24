package com.example.algolens.ui.dashboard

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.AlgoCard
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.EntryCascadeProvider
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

private enum class CatalogueSortMode(val label: String) {
    DEFAULT("Sort: Default"),
    COMPLEXITY("Sort: Complexity"),
    DIFFICULTY("Sort: Difficulty"),
    NAME("Sort: A–Z");

    fun next(): CatalogueSortMode {
        val entries = values()
        return entries[(ordinal + 1) % entries.size]
    }
}

private data class CategoryChipSpec(
    val id: String,
    val displayLabel: String,
    val glyph: ImageVector?,
    val accent: Color
)

private data class CatalogueSection(
    val title: String,
    val subtitle: String? = null,
    val accent: Color,
    val glyph: ImageVector? = null,
    val algorithms: List<Algorithm>
)

private fun complexityRank(timeComplexity: String): Int = when {
    timeComplexity.startsWith("O(1)") -> 1
    timeComplexity.startsWith("O(log n)") -> 2
    timeComplexity.startsWith("O(n)") && !timeComplexity.startsWith("O(n log") && !timeComplexity.startsWith("O(n²)") -> 3
    timeComplexity.startsWith("O(V+E)") -> 3
    timeComplexity.startsWith("O(n log n)") -> 4
    timeComplexity.startsWith("O(n²)") -> 5
    else -> 6
}

private fun complexityTierTitle(rank: Int): Pair<String, Color> = when (rank) {
    1 -> "O(1) · Constant Time" to AccentGreen
    2 -> "O(log n) · Logarithmic" to PrimaryCyan
    3 -> "O(n) / O(V+E) · Linear" to SecondaryPurple
    4 -> "O(n log n) · Linearithmic" to AccentYellow
    5 -> "O(n²) · Quadratic" to AccentOrange
    else -> "Other Complexity" to TextMuted
}

private fun difficultyRank(difficulty: String): Int = when (difficulty.lowercase()) {
    "easy" -> 1
    "medium" -> 2
    "hard" -> 3
    else -> 4
}

/**
 * The instrument's front panel. A unified scrollable workspace with the
 * AVIA header, search well, compact horizontal category chip strip,
 * interactive no-outline sort button, and grouped/sorted algorithm catalogue.
 */
@Composable
fun DashboardScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var sortMode by remember { mutableStateOf(CatalogueSortMode.DEFAULT) }

    val categoryChips = remember {
        listOf(
            CategoryChipSpec("All", "All", null, PrimaryCyan),
            CategoryChipSpec("Sorting", "Sorting", AlgoGlyphs.SwapVert, PrimaryCyan),
            CategoryChipSpec("Searching", "Searching", AlgoGlyphs.Search, SecondaryPurple),
            CategoryChipSpec("Data Structures", "Structures", AlgoGlyphs.Stack, AccentOrange),
            CategoryChipSpec("Graph Traversal", "Graphs", AlgoGlyphs.Tree, AccentGreen),
        )
    }

    val filteredAlgorithms = remember(searchQuery, selectedCategory, sortMode) {
        val base = SampleData.algorithms.filter { algo ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                else -> algo.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isEmpty() ||
                    algo.name.contains(searchQuery, ignoreCase = true) ||
                    algo.category.contains(searchQuery, ignoreCase = true) ||
                    algo.timeComplexity.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }

        when (sortMode) {
            CatalogueSortMode.DEFAULT -> base
            CatalogueSortMode.COMPLEXITY -> base.sortedWith(
                compareBy<Algorithm> { complexityRank(it.timeComplexity) }
                    .thenBy { difficultyRank(it.difficulty) }
                    .thenBy { it.name }
            )
            CatalogueSortMode.DIFFICULTY -> base.sortedWith(
                compareBy<Algorithm> { difficultyRank(it.difficulty) }
                    .thenBy { complexityRank(it.timeComplexity) }
                    .thenBy { it.name }
            )
            CatalogueSortMode.NAME -> base.sortedBy { it.name }
        }
    }

    val groupedSections = remember(filteredAlgorithms, selectedCategory, sortMode) {
        if (filteredAlgorithms.isEmpty()) {
            emptyList()
        } else {
            when (sortMode) {
                CatalogueSortMode.DEFAULT -> {
                    if (selectedCategory != "All") {
                        val spec = categoryChips.firstOrNull { it.id == selectedCategory }
                        listOf(
                            CatalogueSection(
                                title = selectedCategory,
                                subtitle = "${filteredAlgorithms.size}",
                                accent = spec?.accent ?: PrimaryCyan,
                                glyph = spec?.glyph,
                                algorithms = filteredAlgorithms
                            )
                        )
                    } else {
                        categoryChips.drop(1).mapNotNull { chip ->
                            val items = filteredAlgorithms.filter {
                                it.category.equals(chip.id, ignoreCase = true)
                            }
                            if (items.isEmpty()) null
                            else CatalogueSection(
                                title = chip.id,
                                subtitle = "${items.size}",
                                accent = chip.accent,
                                glyph = chip.glyph,
                                algorithms = items
                            )
                        }
                    }
                }
                CatalogueSortMode.COMPLEXITY -> {
                    filteredAlgorithms
                        .groupBy { complexityRank(it.timeComplexity) }
                        .toSortedMap()
                        .map { (rank, items) ->
                            val (tierLabel, tierColor) = complexityTierTitle(rank)
                            CatalogueSection(
                                title = tierLabel,
                                subtitle = "${items.size}",
                                accent = tierColor,
                                algorithms = items
                            )
                        }
                }
                CatalogueSortMode.DIFFICULTY -> {
                    filteredAlgorithms
                        .groupBy { difficultyRank(it.difficulty) }
                        .toSortedMap()
                        .map { (_, items) ->
                            val diff = items.first().difficulty
                            val diffAccent = when (diff.lowercase()) {
                                "easy" -> AccentGreen
                                "medium" -> AccentYellow
                                else -> AccentOrange
                            }
                            CatalogueSection(
                                title = "$diff Difficulty",
                                subtitle = "${items.size}",
                                accent = diffAccent,
                                algorithms = items
                            )
                        }
                }
                CatalogueSortMode.NAME -> {
                    listOf(
                        CatalogueSection(
                            title = "Alphabetical (A–Z)",
                            subtitle = "${filteredAlgorithms.size}",
                            accent = PrimaryCyan,
                            algorithms = filteredAlgorithms
                        )
                    )
                }
            }
        }
    }

    EntryCascadeProvider {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(CanvasBackground)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = AlgoTokens.space6)
        ) {
            // ── 1. Top header: mark, wordmark, catalogue count (position preserved) ──
            item(key = "dashboard_header") {
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
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .clickable { sortMode = sortMode.next() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.TrendingUp,
                            contentDescription = "Cycle sort order",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ── 2. Search well (sunken, hairline, one glyph) ──
            item(key = "dashboard_search") {
                val searchShape = RoundedCornerShape(AlgoTokens.radiusMd)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4)
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
                                    text = "Search algorithms...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            // ── 3. Compact Horizontal Category Chip Strip (replacing 2×2 grid) ──
            item(key = "dashboard_category_chips") {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AlgoTokens.space2, bottom = AlgoTokens.space2),
                    contentPadding = PaddingValues(horizontal = AlgoTokens.space6),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categoryChips, key = { it.id }) { chip ->
                        val isSelected = selectedCategory == chip.id
                        CategoryChip(
                            spec = chip,
                            selected = isSelected,
                            onClick = {
                                selectedCategory = if (chip.id == "All") {
                                    "All"
                                } else if (selectedCategory == chip.id) {
                                    "All"
                                } else {
                                    chip.id
                                }
                            }
                        )
                    }
                }
            }

            // ── 4. Results line + No-outline Interactive Sort Text Button ──
            item(key = "dashboard_results_bar") {
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
                    // No-outline text button that cycles the sort mode
                    Text(
                        text = sortMode.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (sortMode == CatalogueSortMode.DEFAULT) TextMuted else PrimaryCyan,
                        fontWeight = if (sortMode == CatalogueSortMode.DEFAULT) FontWeight.Normal else FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { sortMode = sortMode.next() }
                            .padding(vertical = AlgoTokens.space1)
                    )
                }
            }

            // ── 5. Grouped or Sorted Algorithm Catalogue ──
            if (filteredAlgorithms.isEmpty()) {
                item(key = "dashboard_empty") {
                    EmptyState(
                        query = searchQuery,
                        onClear = {
                            searchQuery = ""
                            selectedCategory = "All"
                        }
                    )
                }
            } else {
                var runningIndex = 0
                groupedSections.forEach { section ->
                    item(key = "section_${section.title}") {
                        CatalogueGroupHeader(
                            title = section.title,
                            countText = section.subtitle,
                            accent = section.accent,
                            glyph = section.glyph,
                            modifier = Modifier.padding(
                                start = AlgoTokens.space6,
                                end = AlgoTokens.space6,
                                top = AlgoTokens.space4,
                                bottom = AlgoTokens.space3
                            )
                        )
                    }

                    itemsIndexed(
                        items = section.algorithms,
                        key = { _, algo -> algo.id }
                    ) { localIdx, algo ->
                        val cardIndex = runningIndex + localIdx
                        Box(
                            modifier = Modifier.padding(
                                start = AlgoTokens.space6,
                                end = AlgoTokens.space6,
                                bottom = AlgoTokens.space4
                            )
                        ) {
                            AlgoCard(
                                algo = algo,
                                onClick = { onAlgorithmClick(algo) },
                                index = cardIndex
                            )
                        }
                    }
                    runningIndex += section.algorithms.size
                }
            }
        }
    }
}

/**
 * Compact horizontal category chip matching the Figma Make reference strip.
 */
@Composable
private fun CategoryChip(
    spec: CategoryChipSpec,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AlgoTokens.radiusSm)
    val backgroundColor = if (selected) PrimaryCyan else AlgoTokens.surfaceCard
    val contentColor = if (selected) CanvasBackground else TextMuted

    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(
                        width = AlgoTokens.strokeThin,
                        color = AlgoTokens.strokeBorderSubtle,
                        shape = shape
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        if (spec.glyph != null) {
            Icon(
                imageVector = spec.glyph,
                contentDescription = null,
                tint = if (selected) CanvasBackground else spec.accent.copy(alpha = 0.8f),
                modifier = Modifier.size(AlgoTokens.inlineIconSm)
            )
        }
        Text(
            text = spec.displayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Compact group section header for family / complexity / difficulty grouping.
 */
@Composable
private fun CatalogueGroupHeader(
    title: String,
    countText: String?,
    accent: Color,
    glyph: ImageVector?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            if (glyph != null) {
                Icon(
                    imageVector = glyph,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(AlgoTokens.space3)
                        .clip(CircleShape)
                        .background(accent)
                )
            }
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = AlgoType.trackSection
            )
        }
        if (countText != null) {
            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall,
                color = TextDark,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.SemiBold
            )
        }
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

