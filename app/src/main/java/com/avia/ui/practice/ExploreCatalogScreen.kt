package com.avia.ui.practice

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import com.avia.data.practice.PracticeQuestionRepository
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.AudioHaptics
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
import com.avia.ui.theme.CardBackgroundHover
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.GreenSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.YellowSubtle

/**
 * Explore sub-destinations.
 */
enum class ExploreView {
    HUB,
    PREDICT_ARENA,
    QUIZ_CATALOG
}

/**
 * Legacy enum maintained for compatibility with existing tests/callsites.
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
 * Unified Explore Screen hosted on [com.avia.ui.components.NavTab.EXPLORE].
 *
 * Clean, direct, and unbloated:
 *  - 2 simple cards: "Predict the Step" and "Practice Quiz".
 *  - No marketing fluff, no buzzword spam, no overcrowded badges.
 */
@Composable
fun ExploreCatalogScreen(
    onStartPractice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentView by remember { mutableStateOf(ExploreView.HUB) }
    var selectedFilter by remember { mutableStateOf("All") }

    BackHandler(enabled = currentView != ExploreView.HUB) {
        currentView = ExploreView.HUB
    }

    val allTracks = remember {
        listOf(
            PracticeTrack(
                category = "All",
                title = "All Challenges",
                description = "Comprehensive track covering all 14 algorithms, pointer mechanics, and data structures.",
                icon = AlgoGlyphs.Spark,
                accent = PrimaryCyan,
                accentSubtle = CyanSubtle,
                questionCount = PracticeQuestionRepository.totalCount,
                topics = "All Families · Easy · Medium · Hard"
            ),
            PracticeTrack(
                category = "Sorting",
                title = "Sorting & Partitioning",
                description = "Bubble, Selection, Insertion, Merge, and Quick Sort invariants and comparisons.",
                icon = AlgoGlyphs.Code,
                accent = PrimaryCyan,
                accentSubtle = CyanSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Sorting").size,
                topics = "Bubble · Selection · Insertion · Merge · Quick"
            ),
            PracticeTrack(
                category = "Searching",
                title = "Searching & Bounds",
                description = "Linear Search and Binary Search interval contraction, midpoints, and search invariants.",
                icon = AlgoGlyphs.Search,
                accent = AccentGreen,
                accentSubtle = GreenSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Searching").size,
                topics = "Linear Search · Binary Search"
            ),
            PracticeTrack(
                category = "Data Structures",
                title = "Buffers, Trees & Heaps",
                description = "Stack (LIFO), Queue (FIFO), Binary Search Tree invariants, and Binary Heap array representations.",
                icon = AlgoGlyphs.Layers,
                accent = PurpleGlow,
                accentSubtle = PurpleSubtle,
                questionCount = PracticeQuestionRepository.getQuestions("Data Structures").size,
                topics = "Stack · Queue · BST · Min/Max Heap"
            ),
            PracticeTrack(
                category = "Graph Traversal",
                title = "Frontiers & Shortest Path",
                description = "BFS queue frontiers, DFS recursion stacks, cycle exploration, and Dijkstra's algorithm.",
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
        AnimatedContent(
            targetState = currentView,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize(),
            label = "ExploreViewSwitcher"
        ) { view ->
            when (view) {
                // ═════════════════════════════════════════════════════════════
                // VIEW 1: UNIFIED EXPLORE HUB (2 BIG VERTICAL CARDS)
                // ═════════════════════════════════════════════════════════════
                ExploreView.HUB -> {
                    val view = LocalView.current
                    val haptic = LocalHapticFeedback.current

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                    ) {
                        // ── Top Header ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryCyan)
                                    )
                                    Text(
                                        text = "EXPLORE & PRACTICE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimaryCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = AlgoType.trackSection
                                    )
                                }
                                Text(
                                    text = "Choose a practice mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CyanSubtle)
                                    .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusSm))
                                    .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "2 MODES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AlgoType.microSize
                                )
                            }
                        }

                        // ── Two Big Vertical Mode Buttons That Fill Screen Space ──
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                        ) {
                            // ── CARD 1: PREDICT THE STEP ──
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                                    .background(CardBackgroundElevated)
                                    .border(
                                        width = 1.dp,
                                        color = BorderCyan.copy(alpha = 0.45f),
                                        shape = RoundedCornerShape(AlgoTokens.radiusMd)
                                    )
                                    .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusMd), accent = PrimaryCyan)
                                    .clickable {
                                        AudioHaptics.performClick(view, haptic)
                                        currentView = ExploreView.PREDICT_ARENA
                                    }
                                    .padding(AlgoTokens.space6)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                                .background(CyanSubtle)
                                                .border(1.dp, BorderCyan.copy(alpha = 0.6f), RoundedCornerShape(AlgoTokens.radiusSm)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = AlgoGlyphs.Bolt,
                                                contentDescription = null,
                                                tint = PrimaryCyan,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                                .background(CyanSubtle)
                                                .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "INTERACTIVE",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PrimaryCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = AlgoType.microSize,
                                                letterSpacing = AlgoType.trackSection
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                                        Text(
                                            text = "Predict the Step",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                        Text(
                                            text = "The algorithm runs on the visualizer and pauses at key decision points. Predict what operation executes next.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "13 Algorithms · Step-by-step",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            fontSize = AlgoType.microSize
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Start Simulation",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = PrimaryCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Icon(
                                                imageVector = AlgoGlyphs.ChevronRight,
                                                contentDescription = null,
                                                tint = PrimaryCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // ── CARD 2: PRACTICE QUIZ ──
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                                    .background(CardBackgroundElevated)
                                    .border(
                                        width = 1.dp,
                                        color = AccentGreen.copy(alpha = 0.40f),
                                        shape = RoundedCornerShape(AlgoTokens.radiusMd)
                                    )
                                    .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusMd), accent = AccentGreen)
                                    .clickable {
                                        AudioHaptics.performClick(view, haptic)
                                        currentView = ExploreView.QUIZ_CATALOG
                                    }
                                    .padding(AlgoTokens.space6)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                                .background(GreenSubtle)
                                                .border(1.dp, AccentGreen.copy(alpha = 0.6f), RoundedCornerShape(AlgoTokens.radiusSm)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = AlgoGlyphs.Target,
                                                contentDescription = null,
                                                tint = AccentGreen,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                                .background(GreenSubtle)
                                                .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "QUIZZES",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AccentGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = AlgoType.microSize,
                                                letterSpacing = AlgoType.trackSection
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                                        Text(
                                            text = "Practice Quiz",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                        Text(
                                            text = "Curated multiple-choice questions on sorting invariants, binary search intervals, buffer operations, and graphs.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${PracticeQuestionRepository.totalCount} Questions · 5 Tracks",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            fontSize = AlgoType.microSize
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Browse Tracks",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = AccentGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Icon(
                                                imageVector = AlgoGlyphs.ChevronRight,
                                                contentDescription = null,
                                                tint = AccentGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ═════════════════════════════════════════════════════════════
                // VIEW 2: PREDICT THE STEP
                // ═════════════════════════════════════════════════════════════
                ExploreView.PREDICT_ARENA -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CardBackgroundElevated)
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                    .clickable { currentView = ExploreView.HUB }
                                    .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = AlgoGlyphs.Back,
                                        contentDescription = "Back",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Back",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryCyan)
                                )
                                Text(
                                    text = "PREDICT THE STEP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = AlgoType.trackSection
                                )
                            }
                        }

                        PredictStepArena(modifier = Modifier.weight(1f).fillMaxWidth())
                    }
                }

                // ═════════════════════════════════════════════════════════════
                // VIEW 3: PRACTICE QUIZ CATALOG
                // ═════════════════════════════════════════════════════════════
                ExploreView.QUIZ_CATALOG -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CardBackgroundElevated)
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                    .clickable { currentView = ExploreView.HUB }
                                    .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = AlgoGlyphs.Back,
                                        contentDescription = "Back",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Back",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                )
                                Text(
                                    text = "PRACTICE QUIZ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = AlgoType.trackSection
                                )
                            }
                        }

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
                                val chipBg = if (isSelected) GreenSubtle else CardBackgroundElevated
                                val chipBorder = if (isSelected) AccentGreen else BorderSubtle
                                val chipText = if (isSelected) AccentGreen else TextSecondary

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                        .background(chipBg)
                                        .border(AlgoTokens.strokeThin, chipBorder, RoundedCornerShape(AlgoTokens.radiusXs))
                                        .clickable { selectedFilter = cat }
                                        .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
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

                        // Track Catalogs List
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
 * Clean, breathable track card without marketing fluff.
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
            .border(
                width = 1.dp,
                color = track.accent.copy(alpha = 0.28f),
                shape = RoundedCornerShape(AlgoTokens.radiusMd)
            )
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

            // Title & Topics
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
                Text(
                    text = track.topics,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = track.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Start →",
                        style = MaterialTheme.typography.labelMedium,
                        color = track.accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
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
