package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Challenge state and scoring model.
 */
data class ChallengeState(
    val isActive: Boolean = false,
    val score: Int = 0,
    val streak: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val selectedIndices: Set<Int> = emptySet(),
    val questionType: ChallengeQuestionType = ChallengeQuestionType.SWAP_DECISION,
    val feedback: ChallengeFeedback? = null
)

enum class ChallengeQuestionType {
    SWAP_DECISION,       // Will these two elements swap? (Yes / No)
    SELECT_COMPARE_PAIR, // Tap the two elements that will be compared next
    SELECT_PIVOT         // Tap the index that serves as the pivot
}

data class ChallengeFeedback(
    val isCorrect: Boolean,
    val message: String,
    val pointsAwarded: Int
)

/**
 * Computes the set of array indices that are valid answers for the current
 * challenge question. The VisualizerScreen feeds these to the canvas so the
 * eligible cells render with pulsing glowing target rings — turning the
 * main visualizer itself into the interactive answer surface.
 */
fun challengeEligibleIndices(
    step: VisualizerStep,
    nextStep: VisualizerStep?,
    type: ChallengeQuestionType
): Set<Int> = when (type) {
    ChallengeQuestionType.SWAP_DECISION -> {
        val active = step.elementStates.filter {
            it.value == ElementState.COMPARING || it.value == ElementState.SWAPPING
        }.keys
        if (active.size >= 2) {
            active
        } else {
            val pointers = (step.bottomPointers.values + step.topPointers.values)
                .distinct()
                .sorted()
            if (pointers.size >= 2) pointers.take(2).toSet() else setOf(0, 1)
        }
    }
    ChallengeQuestionType.SELECT_PIVOT -> {
        val pivot = step.pivotIndex
            ?: step.topPointers.entries
                .firstOrNull { it.key.equals("pivot", ignoreCase = true) }?.value
        if (pivot != null) {
            setOf(pivot)
        } else {
            step.activeRange?.let { setOf((it.first + it.last) / 2) } ?: emptySet()
        }
    }
    ChallengeQuestionType.SELECT_COMPARE_PAIR -> {
        val comparing = step.elementStates
            .filter { it.value == ElementState.COMPARING }.keys
        if (comparing.size >= 2) {
            comparing
        } else {
            step.bottomPointers.values.distinct().sorted().take(2).toSet()
        }
    }
}

/**
 * Compact Challenge Mode prompt rendered as a floating glass overlay on top
 * of the visualizer canvas (replaces the old detached top banner). Playback
 * controls are locked by the host screen until the question is answered.
 */
@Composable
fun CanvasChallengePrompt(
    step: VisualizerStep,
    nextStep: VisualizerStep?,
    state: ChallengeState,
    onStateChange: (ChallengeState) -> Unit,
    onContinueNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSwapLikely = nextStep?.comparisonExpr?.contains("SWAP", ignoreCase = true) == true ||
        nextStep?.elementStates?.values?.any { it == ElementState.SWAPPING } == true

    val borderColor = when {
        state.feedback?.isCorrect == true -> AccentGreen
        state.feedback?.isCorrect == false -> AccentRed
        else -> SecondaryPurple.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated.copy(alpha = 0.94f))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PinkSubtle)
                            .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AccentPink,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "${state.streak}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(YellowSubtle)
                            .border(1.dp, AccentYellow.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "${state.score} PTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                Text(
                    text = "🎯 PREDICT NEXT STEP",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp
                )
            }

                if (state.feedback == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Will these highlighted cells swap? " +
                                "(${step.comparisonExpr ?: step.description})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 2
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(GreenSubtle)
                                .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(7.dp))
                                .clickable {
                                    val isCorrect = isSwapLikely
                                    val pts = if (isCorrect) 100 + (state.streak * 20) else 0
                                    onStateChange(
                                        state.copy(
                                            score = state.score + pts,
                                            streak = if (isCorrect) state.streak + 1 else 0,
                                            totalQuestions = state.totalQuestions + 1,
                                            correctAnswers = state.correctAnswers + (if (isCorrect) 1 else 0),
                                            feedback = ChallengeFeedback(
                                                isCorrect = isCorrect,
                                                message = if (isCorrect) {
                                                    "Spot on! The elements satisfy the swap condition. 🎯"
                                                } else {
                                                    "Incorrect: elements were already in order."
                                                },
                                                pointsAwarded = pts
                                            )
                                        )
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "YES (SWAP)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(PinkSubtle)
                                .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(7.dp))
                                .clickable {
                                    val isCorrect = !isSwapLikely
                                    val pts = if (isCorrect) 100 + (state.streak * 20) else 0
                                    onStateChange(
                                        state.copy(
                                            score = state.score + pts,
                                            streak = if (isCorrect) state.streak + 1 else 0,
                                            totalQuestions = state.totalQuestions + 1,
                                            correctAnswers = state.correctAnswers + (if (isCorrect) 1 else 0),
                                            feedback = ChallengeFeedback(
                                                isCorrect = isCorrect,
                                                message = if (isCorrect) {
                                                    "Correct! No swap was necessary. 🌟"
                                                } else {
                                                    "Oops! A swap occurs next."
                                                },
                                                pointsAwarded = pts
                                            )
                                        )
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NO (KEEP)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
                } else {
                    val fb = state.feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = (if (fb.isCorrect) "🎉 CORRECT (+${fb.pointsAwarded} PTS) " else "❌ INCORRECT ") + fb.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (fb.isCorrect) AccentGreen else AccentRed,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 2
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (fb.isCorrect) PrimaryCyan else SecondaryPurple)
                                .clickable {
                                    onStateChange(state.copy(feedback = null))
                                    onContinueNext()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NEXT ➔",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                }
        }
    }
}

/**
 * Challenge Mode interactive question card & score bar displayed above the visualizer.
 */
@Composable
fun ChallengeCard(
    step: VisualizerStep,
    nextStep: VisualizerStep?,
    state: ChallengeState,
    onStateChange: (ChallengeState) -> Unit,
    onContinueNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate prediction question from current step vs next step
    val isSwapLikely = nextStep?.comparisonExpr?.contains("SWAP", ignoreCase = true) == true ||
            nextStep?.elementStates?.values?.any { it == ElementState.SWAPPING } == true

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .border(
                1.dp,
                when {
                    state.feedback?.isCorrect == true -> AccentGreen
                    state.feedback?.isCorrect == false -> AccentRed
                    else -> SecondaryPurple.copy(alpha = 0.5f)
                },
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header with Streak & Score Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PinkSubtle)
                            .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AccentPink,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "STREAK ${state.streak}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(YellowSubtle)
                            .border(1.dp, AccentYellow.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "${state.score} PTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                Text(
                    text = "🎯 PREDICT NEXT STEP",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp
                )
            }

            // Question Prompt
            if (state.feedback == null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Question: Based on the current comparison, will these elements swap?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Inspect the comparison expression: ${step.comparisonExpr ?: step.description}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentYellow,
                        fontSize = 8.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Binary Choice Buttons: Yes (Swap) or No (Keep)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Yes (Swap)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenSubtle)
                                .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    val isCorrect = isSwapLikely
                                    val pts = if (isCorrect) 100 + (state.streak * 20) else 0
                                    val newStreak = if (isCorrect) state.streak + 1 else 0
                                    onStateChange(
                                        state.copy(
                                            score = state.score + pts,
                                            streak = newStreak,
                                            totalQuestions = state.totalQuestions + 1,
                                            correctAnswers = state.correctAnswers + (if (isCorrect) 1 else 0),
                                            feedback = ChallengeFeedback(
                                                isCorrect = isCorrect,
                                                message = if (isCorrect) "Spot on! The elements satisfy swap condition. 🎯" else "Incorrect: Elements were already in order or didn't need swapping.",
                                                pointsAwarded = pts
                                            )
                                        )
                                    )
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "YES (SWAP)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        }

                        // No (Keep)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PinkSubtle)
                                .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    val isCorrect = !isSwapLikely
                                    val pts = if (isCorrect) 100 + (state.streak * 20) else 0
                                    val newStreak = if (isCorrect) state.streak + 1 else 0
                                    onStateChange(
                                        state.copy(
                                            score = state.score + pts,
                                            streak = newStreak,
                                            totalQuestions = state.totalQuestions + 1,
                                            correctAnswers = state.correctAnswers + (if (isCorrect) 1 else 0),
                                            feedback = ChallengeFeedback(
                                                isCorrect = isCorrect,
                                                message = if (isCorrect) "Correct! No swap was necessary at this step. 🌟" else "Oops! The condition was met so a swap occurs next.",
                                                pointsAwarded = pts
                                            )
                                        )
                                    )
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NO (KEEP)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        }
                    }
                }
            } else {
                // Feedback Result Banner
                val fb = state.feedback
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (fb.isCorrect) "🎉 CORRECT PREDICTION (+${fb.pointsAwarded} PTS)" else "❌ INCORRECT GUESS",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (fb.isCorrect) AccentGreen else AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )

                        // Continue button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (fb.isCorrect) PrimaryCyan else SecondaryPurple)
                                .clickable {
                                    onStateChange(state.copy(feedback = null))
                                    onContinueNext()
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Next Step ➔",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        }
                    }

                    Text(
                        text = fb.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun ChallengeCardPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ChallengeCard(
                step = VisualizerStep(
                    description = "Comparing arr[1]=8 and arr[2]=9",
                    comparisonExpr = "COMPARE: 8 > 9?"
                ),
                nextStep = VisualizerStep(
                    description = "No swap needed"
                ),
                state = ChallengeState(
                    isActive = true,
                    score = 240,
                    streak = 2
                ),
                onStateChange = {},
                onContinueNext = {}
            )
        }
    }
}
