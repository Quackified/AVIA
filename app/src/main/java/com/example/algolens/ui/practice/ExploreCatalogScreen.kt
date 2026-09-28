package com.example.algolens.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.practice.PracticeQuestionRepository
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Data representation of a practice track catalog entry.
 */
data class PracticeTrack(
    val category: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accent: Color,
    val accentSubtle: Color,
    val questionCount: Int,
    val topics: String
)

/**
 * Explore Catalog Screen: The study hub hosted on [NavTab.EXPLORE].
 *
 * Provides categorized challenge tracks (All, Sorting, Searching,
 * Data Structures, Graph Traversal). Tapping "Start Practice" on any
 * track launches a full-screen, focused Practice session.
 */
@Composable
fun ExploreCatalogScreen(
    onStartPractice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val allTracks = remember {
        listOf(
            PracticeTrack(
                category = "All",
                title = "All Challenges",
                description = "Comprehensive challenge track covering all 14 algorithms, pointer mechanics, data structures, and traversals.",
                icon = AlgoGlyphs.Spark,
                accent = PrimaryCyan,
                accentSubtle = CyanSubtle,
                questionCount = PracticeQuestionRepository.totalCount,
                topics = "All Families · Easy · Medium · Hard"
            ),
            PracticeTrack(
                category = "Sorting",
                title = "Sorting & Partitioning",
                description = "Master Bubble, Selection, Insertion, Merge, and Quick Sort invariants, partition pointers, and comparisons.",
                icon = AlgoGlyphs.Code,
                accent = PrimaryCyan,
                accentSubtle = CyanSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Sorting").size,
                topics = "Bubble · Selection · Insertion · Merge · Quick"
            ),
            PracticeTrack(
                category = "Searching",
                title = "Searching & Bounds",
                description = "Test your understanding of Linear Search and Binary Search interval contraction, midpoints, and search invariants.",
                icon = AlgoGlyphs.Search,
                accent = AccentGreen,
                accentSubtle = GreenSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Searching").size,
                topics = "Linear Search · Binary Search"
            ),
            PracticeTrack(
                category = "Data Structures",
                title = "Buffers, Trees & Heaps",
                description = "Explore Stack (LIFO), Queue (FIFO), Binary Search Tree invariants, and Binary Heap array representations.",
                icon = AlgoGlyphs.Layers,
                accent = PurpleGlow,
                accentSubtle = PurpleSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Data Structures").size,
                topics = "Stack · Queue · BST · Min/Max Heap"
            ),
            PracticeTrack(
                category = "Graph Traversal",
                title = "Frontiers & Traversal",
                description = "Differentiate BFS queue frontiers and DFS recursion stacks, edge visitation orders, and cycle exploration.",
                icon = AlgoGlyphs.Nodes,
                accent = AccentYellow,
                accentSubtle = YellowSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Graph Traversal").size,
                topics = "BFS (Queue) · DFS (Recursion)"
            )
        )
    }

    val displayedTracks = remember(selectedFilter, allTracks) {
        if (selectedFilter.equals("All", ignoreCase = true)) {
            allTracks
        } else {
            allTracks.filter { it.category.equals(selectedFilter, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Top Explore Header Bar ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Explore & Practice",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Curated challenge catalogs for algorithmic mastery",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Global Total Pill
                Box(
                    modifier = Modifier
                                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CyanSubtle)
                        .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                ) {
                    Text(
                        text = "${PracticeQuestionRepository.totalCount} CHALLENGES",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.microSize,
                        maxLines = 1
                    )
                }
            }

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                PracticeQuestionRepository.categories.forEach { cat ->
                    val isSelected = selectedFilter.equals(cat, ignoreCase = true)
                    val chipBg = if (isSelected) CyanSubtle else CardBackground
                    val chipBorder = if (isSelected) PrimaryCyan else BorderSubtle
                    val chipText = if (isSelected) PrimaryCyan else TextSecondary

                    Box(
                        modifier = Modifier
                                                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                            .background(chipBg)
                            .border(AlgoTokens.bezelInset, chipBorder, RoundedCornerShape(AlgoTokens.radiusXxs))
                            .clickable { selectedFilter = cat }
                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall,
                            color = chipText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = AlgoType.microSize,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // ── 2. Track Catalogs List ──
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            items(displayedTracks, key = { it.category }) { track ->
                TrackCatalogCard(
                    track = track,
                    onStartPractice = { onStartPractice(track.category) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(AlgoTokens.space6))
            }
        }
    }
}

/**
 * Catalog track card rendered with subtle double-bezel styling.
 */
@Composable
private fun TrackCatalogCard(
    track: PracticeTrack,
    onStartPractice: () -> Unit
) {
    DoubleBezelShell(
        modifier = Modifier
            .fillMaxWidth()
            .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusMd), accent = track.accent)
            .clickable { onStartPractice() },
        contentPadding = PaddingValues(AlgoTokens.space5)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
            // Header Row: Icon + Title & Category + Badge
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(track.accentSubtle)
                            .border(AlgoTokens.strokeThin, track.accent.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = track.icon,
                            contentDescription = null,
                            tint = track.accent,
                            modifier = Modifier.size(AlgoTokens.inlineIconMd)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.topics,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Question count badge
                Box(
                    modifier = Modifier
                                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(track.accentSubtle)
                        .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                ) {
                    Text(
                        text = "${track.questionCount} Questions",
                        style = MaterialTheme.typography.labelSmall,
                        color = track.accent,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // Description
            Text(
                text = track.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = AlgoType.leadingBodyDefault,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Start Practice CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(track.accent)
                    .clickable { onStartPractice() }
                    .padding(vertical = AlgoTokens.space3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Text(
                        text = "Start Practice",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = AlgoGlyphs.ChevronRight,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
@Composable
fun ExploreCatalogScreenPreview() {
    AlgoLensTheme {
        ExploreCatalogScreen(onStartPractice = {})
    }
}
