package com.avia.ui.practice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.avia.data.practice.PracticeQuestionRepository
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.SegmentedToggle
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderCyan
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.GreenSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.YellowSubtle

/**
 * Explore Mode sub-destinations.
 */
enum class ExploreMode {
    PREDICT_STEP,
    PRACTICE_QUIZ
}

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
 * Explore Hub Screen hosted on [com.avia.ui.components.NavTab.EXPLORE].
 *
 * Features:
 *  - Top Mode Switcher: [ Predict Step | Practice Quiz ]
 *    1. Predict Step: Dedicated interactive algorithmic foresight arena with live visualizer.
 *    2. Practice Quiz: Clean, airy catalog of conceptual question tracks.
 */
@Composable
fun ExploreCatalogScreen(
    onStartPractice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var exploreMode by remember { mutableStateOf(ExploreMode.PREDICT_STEP) }
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
                title = "Frontiers & Shortest Path",
                description = "Master BFS queue frontiers, DFS recursion stacks, cycle exploration, and Dijkstra's Min-Priority Queue shortest path calculations.",
                icon = AlgoGlyphs.Nodes,
                accent = AccentYellow,
                accentSubtle = YellowSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Graph Traversal").size,
                topics = "BFS (Queue) · DFS (Recursion) · Dijkstra (Min-PQ)"
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
        // ── 1. Top Explore Header Bar with Mode Switcher ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Explore & Practice",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (exploreMode == ExploreMode.PREDICT_STEP) {
                            "Step-by-step interactive algorithmic prediction"
                        } else {
                            "Curated conceptual question tracks"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Global Count Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CyanSubtle)
                        .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                ) {
                    Text(
                        text = if (exploreMode == ExploreMode.PREDICT_STEP) "13 ALGORITHMS" else "${PracticeQuestionRepository.totalCount} QUIZZES",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.microSize,
                        maxLines = 1
                    )
                }
            }

            // Mode Switcher Segmented Toggle
            SegmentedToggle(
                options = listOf(
                    "Predict Step" to ExploreMode.PREDICT_STEP,
                    "Practice Quiz" to ExploreMode.PRACTICE_QUIZ
                ),
                selectedKey = exploreMode,
                onSelect = { if (it is ExploreMode) exploreMode = it }
            )
        }

        // ── 2. Content Body Switcher ──
        AnimatedContent(
            targetState = exploreMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "ExploreModeContent"
        ) { mode ->
            when (mode) {
                ExploreMode.PREDICT_STEP -> {
                    PredictStepArena(modifier = Modifier.fillMaxSize())
                }
                ExploreMode.PRACTICE_QUIZ -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Category Filter Pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            PracticeQuestionRepository.categories.forEach { cat ->
                                val isSelected = selectedFilter.equals(cat, ignoreCase = true)
                                val chipBg = if (isSelected) CyanSubtle else CardBackgroundElevated
                                val chipBorder = if (isSelected) PrimaryCyan else BorderSubtle
                                val chipText = if (isSelected) PrimaryCyan else TextSecondary

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                        .background(chipBg)
                                        .border(AlgoTokens.strokeThin, chipBorder, RoundedCornerShape(AlgoTokens.radiusXs))
                                        .clickable { selectedFilter = cat }
                                        .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
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

                        // Track Catalogs List (Redesigned Breathable Cards)
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
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
            }
        }
    }
}

/**
 * Redesigned Track Catalog Card:
 * Airy, user-friendly, and readable with comfortable whitespace, clean typography hierarchy,
 * and an effortless tap surface.
 */
@Composable
private fun TrackCatalogCard(
    track: PracticeTrack,
    onStartPractice: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle.copy(alpha = 0.65f), RoundedCornerShape(AlgoTokens.radiusMd))
            .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusMd), accent = track.accent)
            .clickable { onStartPractice() }
            .padding(AlgoTokens.space5)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
            // Overline: Category + Question Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(track.accentSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = track.icon,
                            contentDescription = null,
                            tint = track.accent,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Text(
                        text = track.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = track.accent,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                }

                Text(
                    text = "${track.questionCount} Questions",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Medium
                )
            }

            // Title & Topics Summary
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                )
                Text(
                    text = track.topics,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Description with relaxed line height for readability
            Text(
                text = track.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Sleek Footer Action Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AlgoTokens.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Multiple-choice track",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Start Track",
                        style = MaterialTheme.typography.labelMedium,
                        color = track.accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = AlgoGlyphs.ChevronRight,
                        contentDescription = null,
                        tint = track.accent,
                        modifier = Modifier.size(14.dp)
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
