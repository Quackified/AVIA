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
