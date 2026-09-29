package com.avia.ui.practice

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avia.data.practice.PracticeQuestionRepository
import com.avia.model.practice.PracticeDifficulty
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.AudioHaptics
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.components.InstrumentMeter
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentOrange
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
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
import com.avia.ui.theme.OrangeSubtle
import com.avia.ui.theme.PinkSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.RedSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.YellowSubtle

private data class OptionStyle(
    val bg: Color,
    val border: Color,
    val text: Color,
    val badgeBg: Color,
    val badgeText: Color
)

/**
 * Explore / Practice Mode Screen in AlgoLens.
 *
 * Connected to [PracticeSessionManager] and [PracticeQuestionRepository].
 * Features:
 *  - Category filter pills (All, Sorting, Searching, Data Structures, Graph Traversal).
 *  - Adaptive multi-family visual snapshots via [PracticeVisualCanvas].
 *  - Interactive multiple-choice selection with educational feedback.
 *  - In-session scoring, consecutive streak tracking, and completion summary.
 *  - Upgraded option design with colored borders, option index badges ([A], [B], [C], [D]),
 *    and consistent AVIA dark-tech typography.
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
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Root Destination Header ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space3),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
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
                    if (onBack != null) {
                        com.avia.ui.components.RailIconButton(
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

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryCyan)
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
                        Text(
                            text = "${manager.selectedCategory} Track",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Question ${manager.currentIndex + 1} of ${manager.totalQuestions} · ${manager.questions.size} available",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Score & Streak Badges
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (manager.streak > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(OrangeSubtle)
                                .border(AlgoTokens.strokeThin, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                                .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${manager.streak}x STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize,
                                maxLines = 1
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CyanSubtle)
                            .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${manager.score} PTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize,
                            maxLines = 1
                        )
                    }
                }
            }

            // Progress Meter
            val progressLabel = if (manager.isSessionCompleted) {
                "Session Completed (${manager.correctCount}/${manager.totalQuestions} Correct)"
            } else {
                "Track Progress (${manager.currentIndex + 1}/${manager.totalQuestions})"
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
                    fontSize = 11.sp
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
                height = 5.dp
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
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
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
                            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                        ) {
                            Text(
                                text = "Quiz Complete",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Category: ${manager.selectedCategory}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
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
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                        .background(CardBackgroundElevated)
                                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                        .clickable { onBack() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Back to Catalog",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(PrimaryCyan)
                                    .clickable { manager.restartSession() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Practice Again",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                        .background(CardBackgroundElevated)
                        .border(1.dp, BorderCyan.copy(alpha = 0.22f), RoundedCornerShape(AlgoTokens.radiusMd))
                        .padding(AlgoTokens.space5)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
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
                                    .padding(horizontal = AlgoTokens.space2, vertical = 2.dp)
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        )
                    }
                }

                // ── Multiple-Choice Options with Branded Borders and Badges ──
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                    q.options.forEachIndexed { optIdx, opt ->
                        val isSelected = manager.selectedOptionId == opt.id
                        val isSubmitted = manager.isSubmitted

                        val style = when {
                            isSubmitted && opt.isCorrect -> OptionStyle(
                                bg = GreenSubtle,
                                border = AccentGreen,
                                text = AccentGreen,
                                badgeBg = GreenSubtle,
                                badgeText = AccentGreen
                            )
                            isSubmitted && isSelected && !opt.isCorrect -> OptionStyle(
                                bg = RedSubtle,
                                border = AccentRed,
                                text = AccentRed,
                                badgeBg = RedSubtle,
                                badgeText = AccentRed
                            )
                            isSelected -> OptionStyle(
                                bg = CyanSubtle.copy(alpha = 0.45f),
                                border = PrimaryCyan,
                                text = PrimaryCyan,
                                badgeBg = PrimaryCyan,
                                badgeText = DarkBackground
                            )
                            else -> OptionStyle(
                                bg = CardBackgroundElevated,
                                border = BorderCyan.copy(alpha = 0.22f),
                                text = TextPrimary,
                                badgeBg = CyanSubtle.copy(alpha = 0.6f),
                                badgeText = PrimaryCyan
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(style.bg)
                                .border(
                                    width = if (isSelected || (isSubmitted && (opt.isCorrect || isSelected))) 1.5.dp else 1.dp,
                                    color = style.border,
                                    shape = RoundedCornerShape(AlgoTokens.radiusSm)
                                )
                                .pressPhysics(
                                    shape = RoundedCornerShape(AlgoTokens.radiusSm),
                                    accent = if (isSelected) PrimaryCyan else BorderCyan,
                                    enabled = !isSubmitted
                                )
                                .clickable(enabled = !isSubmitted) {
                                    AudioHaptics.performSelect(view, haptic)
                                    manager.selectOption(opt.id)
                                }
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Option letter pill [A], [B], [C], [D]
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                        .background(style.badgeBg)
                                        .border(1.dp, style.border.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusXxs)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${('A' + optIdx)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = style.badgeText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = opt.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = style.text,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = if (isSelected || (isSubmitted && opt.isCorrect)) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                            // Radio ring or status icon
                            Box(
                                modifier = Modifier
                                    .padding(start = AlgoTokens.space2)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(DarkBackground)
                                    .border(
                                        width = if (isSelected || (isSubmitted && (opt.isCorrect || isSelected))) 1.5.dp else 1.dp,
                                        color = style.border,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSubmitted && opt.isCorrect) {
                                    Icon(
                                        imageVector = AlgoGlyphs.Check,
                                        contentDescription = null,
                                        tint = AccentGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else if (isSubmitted && isSelected && !opt.isCorrect) {
                                    Icon(
                                        imageVector = AlgoGlyphs.Close,
                                        contentDescription = null,
                                        tint = AccentRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryCyan)
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
                            .height(44.dp)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(if (canSubmit) PrimaryCyan else CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, if (canSubmit) PrimaryCyan else BorderSubtle.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .clickable(enabled = canSubmit) {
                                val isCorrect = q.options.find { it.id == manager.selectedOptionId }?.isCorrect == true
                                if (isCorrect) AudioHaptics.performCorrect(view, haptic) else AudioHaptics.performIncorrect(view, haptic)
                                manager.submitAnswer()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Submit Answer ▶",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (canSubmit) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
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
                    val cardTitle = if (correct) "Correct! +${q.difficulty.basePoints} PTS" else "Review Concept"

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                            .background(cardBg)
                            .border(1.dp, cardBorder.copy(alpha = 0.6f), RoundedCornerShape(AlgoTokens.radiusMd))
                            .padding(AlgoTokens.space5)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(cardBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (correct) AlgoGlyphs.Check else AlgoGlyphs.Close,
                                        contentDescription = null,
                                        tint = DarkBackground,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = cardTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = cardBorder,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            val isLast = manager.currentIndex + 1 >= manager.totalQuestions
                            val nextLabel = if (isLast) "Finish Session →" else "Next Question →"

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(if (correct) AccentGreen else PrimaryCyan)
                                    .clickable {
                                        AudioHaptics.performClick(view, haptic)
                                        manager.nextQuestion()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nextLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
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
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space6))
        }
    }
}
