package com.example.algolens.ui.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.practice.PracticeQuestionRepository
import com.example.algolens.model.practice.PracticeDifficulty
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.InstrumentMeter
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
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
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Explore / Practice Mode Screen in AlgoLens.
 *
 * Connected to [PracticeSessionManager] and [PracticeQuestionRepository].
 * Features:
 *  - Category filter pills (All, Sorting, Searching, Data Structures, Graph Traversal).
 *  - Adaptive multi-family visual snapshots via [PracticeVisualCanvas].
 *  - Interactive multiple-choice selection with educational feedback.
 *  - In-session scoring, consecutive streak tracking, and completion summary.
 */
@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier,
    initialCategory: String? = null,
    onBack: (() -> Unit)? = null,
    manager: PracticeSessionManager = rememberPracticeSessionManager()
) {
    androidx.compose.runtime.LaunchedEffect(initialCategory) {
        if (!initialCategory.isNullOrBlank()) {
            manager.selectCategory(initialCategory)
        }
    }

    val q = manager.currentQuestion

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Root Destination Header & Category Filter ──
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    if (onBack != null) {
                        com.example.algolens.ui.components.RailIconButton(
                            icon = AlgoGlyphs.Back,
                            contentDescription = "Back to Catalog",
                            boxSize = AlgoTokens.iconButtonSm,
                            iconSize = AlgoTokens.inlineIconMd,
                            tint = TextSecondary,
                            container = CardBackground,
                            borderColor = BorderSubtle,
                            onClick = onBack
                        )
                    }

                    Column {
                        Text(
                            text = "Explore: Practice",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Interactive challenge mode · ${manager.questions.size} questions available",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Score & Streak Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (manager.streak > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(OrangeSubtle)
                                .border(AlgoTokens.strokeThin, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                                .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${manager.streak}x STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CyanSubtle)
                            .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${manager.score} PTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }
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
                    val isSelected = manager.selectedCategory.equals(cat, ignoreCase = true)
                    val chipBg = if (isSelected) CyanSubtle else CardBackground
                    val chipBorder = if (isSelected) PrimaryCyan else BorderSubtle
                    val chipText = if (isSelected) PrimaryCyan else TextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                            .background(chipBg)
                            .border(AlgoTokens.bezelInset, chipBorder, RoundedCornerShape(AlgoTokens.radiusXxs))
                            .clickable { manager.selectCategory(cat) }
                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall,
                            color = chipText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }

            // Progress Meter
            val progressLabel = if (manager.isSessionCompleted) {
                "Session Completed (${manager.correctCount}/${manager.totalQuestions} Correct)"
            } else {
                "Question ${manager.currentIndex + 1} of ${manager.totalQuestions}"
            }
            val percentText = "${(manager.progressFraction * 100).toInt()}%"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progressLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = AlgoType.microSize
                )
                Text(
                    text = percentText,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.microSize
                )
            }

            InstrumentMeter(
                progress = { manager.progressFraction },
                accent = PrimaryCyan,
                modifier = Modifier.fillMaxWidth(),
                height = 6.dp
            )
        }

        // ── 2. Main Question / Summary Body ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space3),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            if (manager.isSessionCompleted) {
                // ── Session Completion Summary Card ──
                DoubleBezelShell(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(AlgoTokens.space6)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AlgoTokens.minTouchTarget)
                                .clip(CircleShape)
                                .background(GreenSubtle)
                                .border(AlgoTokens.bezelInset, AccentGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.CheckCircle,
                                contentDescription = "Completed",
                                tint = AccentGreen,
                                modifier = Modifier.size(AlgoTokens.inlineIconLg)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Text(
                                text = "PRACTICE COMPLETE",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = AlgoType.trackHeader
                            )
                            Text(
                                text = "Category: ${manager.selectedCategory}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = AlgoType.microSize
                            )
                        }

                        // Metrics Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CardBackgroundElevated)
                                .border(AlgoTokens.bezelInset, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                .padding(AlgoTokens.space4),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCORE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = AlgoType.microSize
                                )
                                Text(
                                    text = "${manager.score}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "ACCURACY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = AlgoType.microSize
                                )
                                val accuracy = if (manager.totalQuestions > 0) {
                                    (manager.correctCount * 100) / manager.totalQuestions
                                } else 0
                                Text(
                                    text = "$accuracy%",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "BEST STREAK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = AlgoType.microSize
                                )
                                Text(
                                    text = "${manager.bestStreak}x",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AccentOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            if (onBack != null) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                        .background(CardBackgroundElevated)
                                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                        .clickable { onBack() }
                                        .padding(vertical = AlgoTokens.space4),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Back to Catalog",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(PrimaryCyan)
                                    .clickable { manager.restartSession() }
                                    .padding(vertical = AlgoTokens.space4),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Practice Again",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else if (q != null) {
                // ── Active Question Canvas ──
                PracticeVisualCanvas(
                    stepTitle = q.stepTitle,
                    snapshot = q.snapshot
                )

                // ── Question Text Card ──
                DoubleBezelShell(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = AlgoTokens.space5,
                        vertical = AlgoTokens.space4
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${q.category.uppercase()} · ${q.algorithmId.displayName.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = AlgoType.microSize,
                                letterSpacing = AlgoType.trackSection
                            )

                            // Difficulty Pill
                            val (diffColor, diffBg) = when (q.difficulty) {
                                PracticeDifficulty.EASY -> Pair(AccentGreen, GreenSubtle)
                                PracticeDifficulty.MEDIUM -> Pair(AccentOrange, OrangeSubtle)
                                PracticeDifficulty.HARD -> Pair(AccentRed, RedSubtle)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(diffBg)
                                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                            ) {
                                Text(
                                    text = q.difficulty.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = diffColor,
                                    fontSize = AlgoType.microSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = q.promptText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = AlgoType.leadingBodyDefault
                        )
                    }
                }

                // ── Multiple-Choice Options ──
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                    q.options.forEach { opt ->
                        val isSelected = manager.selectedOptionId == opt.id
                        val isSubmitted = manager.isSubmitted

                        val (optBg, optBorder, optText) = when {
                            isSubmitted && opt.isCorrect -> Triple(GreenSubtle, AccentGreen, AccentGreen)
                            isSubmitted && isSelected && !opt.isCorrect -> Triple(RedSubtle, AccentRed, AccentRed)
                            isSelected -> Triple(CyanSubtle, PrimaryCyan, PrimaryCyan)
                            else -> Triple(CardBackground, BorderSubtle, TextPrimary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                                .background(optBg)
                                .border(
                                    width = if (isSelected || (isSubmitted && opt.isCorrect)) AlgoTokens.bezelInset * 1.5f else AlgoTokens.bezelInset,
                                    color = optBorder,
                                    shape = RoundedCornerShape(AlgoTokens.radiusMd)
                                )
                                .clickable(enabled = !isSubmitted) {
                                    manager.selectOption(opt.id)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opt.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = optText,
                                fontWeight = if (isSelected || (isSubmitted && opt.isCorrect)) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .padding(start = AlgoTokens.space3)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(DarkBackground)
                                    .border(
                                        width = if (isSelected || (isSubmitted && opt.isCorrect)) 2.dp else 1.dp,
                                        color = optBorder,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected || (isSubmitted && opt.isCorrect)) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(optBorder)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Submit Answer Button ──
                if (!manager.isSubmitted) {
                    val canSubmit = manager.selectedOptionId != null
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                            .background(if (canSubmit) PrimaryCyan else CardBackgroundElevated)
                            .clickable(enabled = canSubmit) { manager.submitAnswer() }
                            .padding(vertical = AlgoTokens.space4),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Submit Answer",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (canSubmit) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ── Feedback Overlay Card ──
                AnimatedVisibility(
                    visible = manager.isSubmitted,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val correct = manager.isCorrect == true
                    val cardBg = if (correct) GreenSubtle else RedSubtle
                    val cardBorder = if (correct) AccentGreen else AccentRed
                    val cardTitle = if (correct) "Correct! +${q.difficulty.basePoints} PTS" else "Incorrect Choice"

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                            .background(cardBg)
                            .border(AlgoTokens.bezelInset, cardBorder, RoundedCornerShape(AlgoTokens.radiusMd))
                            .padding(AlgoTokens.space4)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(AlgoTokens.space4)
                                        .clip(CircleShape)
                                        .background(cardBorder)
                                )
                                Text(
                                    text = cardTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = cardBorder,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = AlgoType.leadingBodyTight
                            )

                            val isLast = manager.currentIndex + 1 >= manager.totalQuestions
                            val nextLabel = if (isLast) "Finish Session →" else "Next Question →"

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(if (correct) AccentGreen else PrimaryCyan)
                                    .clickable { manager.nextQuestion() }
                                    .padding(vertical = AlgoTokens.space3),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nextLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty state for category with no questions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AlgoTokens.space8),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No questions found for this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space6))
        }
    }
}
