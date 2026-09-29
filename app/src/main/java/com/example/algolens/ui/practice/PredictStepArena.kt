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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
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
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import com.example.algolens.ui.visualizer.ChallengeFeedback
import com.example.algolens.ui.visualizer.PredictionAnswer
import com.example.algolens.ui.visualizer.PredictionKind
import com.example.algolens.ui.visualizer.PredictionQuestion
import com.example.algolens.ui.visualizer.VisualizerHost
import com.example.algolens.ui.visualizer.VisualizerStep
import com.example.algolens.ui.visualizer.buildPredictionQuestion
import com.example.algolens.ui.visualizer.challengeEligibleIndices
import com.example.algolens.ui.visualizer.predictionAnswerFor
import com.example.algolens.ui.visualizer.rememberVisualizerScreenState

/** Supported algorithms for interactive step prediction. */
private val PREDICT_ALGORITHMS = listOf(
    AlgorithmId.BUBBLE_SORT,
    AlgorithmId.SELECTION_SORT,
    AlgorithmId.INSERTION_SORT,
    AlgorithmId.MERGE_SORT,
    AlgorithmId.QUICK_SORT,
    AlgorithmId.BINARY_SEARCH,
    AlgorithmId.LINEAR_SEARCH,
    AlgorithmId.STACK,
    AlgorithmId.QUEUE,
    AlgorithmId.BINARY_SEARCH_TREE,
    AlgorithmId.HEAP,
    AlgorithmId.BFS,
    AlgorithmId.DFS
)

/**
 * Dedicated Interactive Arena for "Predict the Next Step" in Explore Mode.
 *
 * Provides:
 *  - Algorithm selector chips across supported algorithmic families.
 *  - Live Visualizer Stage rendering the algorithm snapshot.
 *  - Dedicated Prediction Challenge Card with clear typography, breathable spacing,
 *    and specialized answer controls (Yes/No, Element Tap, Node Selection).
 *  - Scoreboard, streak tracking, accuracy metric, and educational feedback.
 */
@Composable
fun PredictStepArena(
    modifier: Modifier = Modifier
) {
    var selectedAlgoId by remember { mutableStateOf(AlgorithmId.BUBBLE_SORT) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var totalQuestions by remember { mutableIntStateOf(0) }
    var correctAnswers by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf<ChallengeFeedback?>(null) }
    var userSelectedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var isPlaying by remember { mutableStateOf(false) }
    var skipCountUntilHalt by remember { mutableIntStateOf(0) }

    val algorithm = remember(selectedAlgoId) {
        SampleData.algorithms.find { it.id == selectedAlgoId }
            ?: SampleData.algorithms.first()
    }
    val state = rememberVisualizerScreenState(algorithm)
    val spec = remember(selectedAlgoId) { AlgorithmRegistry.specFor(selectedAlgoId) }

    // Reset when changing algorithms
    androidx.compose.runtime.LaunchedEffect(selectedAlgoId) {
        feedback = null
        userSelectedIndices = emptySet()
        isPlaying = false
        skipCountUntilHalt = 0
        state.currentStepIdx = 0
    }

    val currentStep = state.currentStep
    val nextStep = state.steps.getOrNull(state.currentStepIdx + 1)
    val currentQuestion = remember(selectedAlgoId, state.currentStepIdx, state.steps) {
        buildPredictionQuestion(selectedAlgoId, currentStep, nextStep)
    }

    // ── Auto-Play Simulation Engine ──
    androidx.compose.runtime.LaunchedEffect(isPlaying, state.currentStepIdx, feedback) {
        if (!isPlaying || feedback != null) return@LaunchedEffect

        if (state.currentStepIdx >= state.steps.lastIndex) {
            isPlaying = false
            return@LaunchedEffect
        }

        delay(550L)

        // Advance to next step
        state.stepForward()

        // Check if the new step is a decision point
        val cur = state.currentStep
        val nxt = state.steps.getOrNull(state.currentStepIdx + 1)
        val q = if (nxt != null) buildPredictionQuestion(selectedAlgoId, cur, nxt) else null

        if (q != null) {
            if (skipCountUntilHalt > 0) {
                // Let this step animate through so the user sees progression
                skipCountUntilHalt--
            } else {
                // HALT: Freeze simulation and challenge user!
                isPlaying = false
                feedback = null
                userSelectedIndices = emptySet()
                skipCountUntilHalt = Random.nextInt(1, 4)
            }
        }
    }

    val challengeTargets = remember(currentQuestion) {
        currentQuestion?.eligibleIndices ?: emptySet()
    }

    val syncPulse = remember { mutableStateOf(0f) }

    fun submitAnswer(
        userSaidYes: Boolean? = null,
        indices: Set<Int> = emptySet(),
        nodeId: String? = null
    ) {
        val q = currentQuestion ?: return
        val isCorrect = predictionAnswerFor(
            question = q,
            userSaidYes = userSaidYes,
            userSelectedIndices = indices,
            userSelectedNodeId = nodeId
        )
        val pts = if (isCorrect) q.pointsAvailable + (streak * 25) else 0
        totalQuestions++
        if (isCorrect) {
            score += pts
            streak++
            correctAnswers++
        } else {
            streak = 0
        }
        feedback = ChallengeFeedback(
            isCorrect = isCorrect,
            message = if (isCorrect) "Spot on! ${q.contextLine}" else "Not quite. ${q.contextLine}",
            pointsAwarded = pts
        )
    }

    fun jumpToNextDecision() {
        feedback = null
        userSelectedIndices = emptySet()
        isPlaying = false
        var searchIdx = state.currentStepIdx + 1
        var found = false
        while (searchIdx < state.steps.lastIndex) {
            val q = buildPredictionQuestion(selectedAlgoId, state.steps[searchIdx], state.steps.getOrNull(searchIdx + 1))
            if (q != null) {
                state.currentStepIdx = searchIdx
                found = true
                break
            }
            searchIdx++
        }
        if (!found) {
            if (state.currentStepIdx < state.steps.lastIndex) {
                state.stepForward()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        // ── 1. Arena Header & Score Strip ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PREDICT NEXT STEP",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection,
                    fontSize = AlgoType.microSize
                )
                Text(
                    text = "Interactive Foresight Arena",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Streak & Score Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (streak > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(OrangeSubtle)
                            .border(AlgoTokens.strokeThin, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Bolt,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${streak}x STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CyanSubtle)
                        .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Target,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "$score PTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }
        }

        // ── 2. Algorithm Selector Chips ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            PREDICT_ALGORITHMS.forEach { algoId ->
                val isSelected = selectedAlgoId == algoId
                val chipBg = if (isSelected) CyanSubtle else CardBackgroundElevated
                val chipBorder = if (isSelected) PrimaryCyan else BorderSubtle
                val chipText = if (isSelected) PrimaryCyan else TextSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(chipBg)
                        .border(AlgoTokens.strokeThin, chipBorder, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable {
                            if (selectedAlgoId != algoId) {
                                selectedAlgoId = algoId
                            }
                        }
                        .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                ) {
                    Text(
                        text = algoId.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = chipText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = AlgoType.microSize
                    )
                }
            }
        }

        // ── 3. Visualizer Stage Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CanvasBackground)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
        ) {
            if (spec != null) {
                VisualizerHost(
                    spec = spec,
                    currentStep = currentStep,
                    arrayViewMode = state.arrayViewMode,
                    selectedCellIndices = userSelectedIndices,
                    challengeTargetIndices = challengeTargets,
                    syncPulse = syncPulse,
                    onCellClick = { idx ->
                        userSelectedIndices = if (idx in userSelectedIndices) {
                            userSelectedIndices - idx
                        } else {
                            userSelectedIndices + idx
                        }
                    },
                    state = state
                )
            }

            // Step counter badge in stage
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AlgoTokens.space3)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(DarkBackground.copy(alpha = 0.75f))
                    .border(AlgoTokens.strokeHairline, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                    .padding(horizontal = AlgoTokens.space2, vertical = 2.dp)
            ) {
                Text(
                    text = "Step ${state.displayStepIdx + 1} of ${state.totalSteps}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize
                )
            }
        }

        // ── 3.5. Simulation Transport Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                .padding(horizontal = AlgoTokens.space3, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                // Play / Pause Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (isPlaying) PurpleSubtle else CyanSubtle)
                        .border(
                            AlgoTokens.strokeThin,
                            if (isPlaying) SecondaryPurple else PrimaryCyan,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .clickable {
                            if (isPlaying) {
                                isPlaying = false
                            } else {
                                feedback = null
                                userSelectedIndices = emptySet()
                                if (state.currentStepIdx >= state.steps.lastIndex) {
                                    state.currentStepIdx = 0
                                }
                                isPlaying = true
                            }
                        }
                        .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) AlgoGlyphs.Pause else AlgoGlyphs.Play,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (isPlaying) SecondaryPurple else PrimaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isPlaying) "PAUSE RUN" else "AUTO-PLAY",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPlaying) SecondaryPurple else PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }
                }

                // Step Forward Manual Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CardBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable(enabled = !isPlaying && state.currentStepIdx < state.steps.lastIndex) {
                            feedback = null
                            userSelectedIndices = emptySet()
                            state.stepForward()
                        }
                        .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.ChevronRight,
                            contentDescription = "Step",
                            tint = if (!isPlaying && state.currentStepIdx < state.steps.lastIndex) TextPrimary else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "STEP",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (!isPlaying && state.currentStepIdx < state.steps.lastIndex) TextPrimary else TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = AlgoType.microSize
                        )
                    }
                }

                // Reset Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(CardBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                        .clickable {
                            isPlaying = false
                            feedback = null
                            userSelectedIndices = emptySet()
                            state.currentStepIdx = 0
                        }
                        .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Reset,
                            contentDescription = "Reset",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "RESET",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }

            // Seek Decision Shortcut
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .clickable {
                        isPlaying = false
                        jumpToNextDecision()
                    }
                    .padding(horizontal = AlgoTokens.space2, vertical = 4.dp)
            ) {
                Text(
                    text = "Seek Decision ⏭",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── 4. Dedicated Prediction Challenge Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
                .padding(AlgoTokens.space5)
        ) {
            if (feedback != null) {
                // Feedback State
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
                    val fb = feedback!!
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (fb.isCorrect) GreenSubtle else RedSubtle)
                                .border(1.dp, if (fb.isCorrect) AccentGreen else AccentRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (fb.isCorrect) AlgoGlyphs.Check else AlgoGlyphs.Close,
                                contentDescription = null,
                                tint = if (fb.isCorrect) AccentGreen else AccentRed,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (fb.isCorrect) "CORRECT! +${fb.pointsAwarded} PTS" else "INCORRECT",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (fb.isCorrect) AccentGreen else AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = fb.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = AlgoType.microSize,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Continue Simulation / Seek Next Decision
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(PrimaryCyan)
                                .clickable {
                                    feedback = null
                                    userSelectedIndices = emptySet()
                                    if (state.currentStepIdx < state.steps.lastIndex) {
                                        state.stepForward()
                                    }
                                    isPlaying = true
                                }
                                .padding(vertical = AlgoTokens.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                            ) {
                                Icon(
                                    imageVector = AlgoGlyphs.Play,
                                    contentDescription = null,
                                    tint = DarkBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Continue Run ▶",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CardBackground)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                .clickable { jumpToNextDecision() }
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Next Decision ⏭",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else if (currentQuestion != null) {
                // Active Question State
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
                    // Question Header & Prompt
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
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
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                        .background(YellowSubtle)
                                        .border(AlgoTokens.strokeThin, AccentYellow.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusXxs))
                                        .padding(horizontal = AlgoTokens.space2, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "⚡ HALT: DECISION POINT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentYellow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = AlgoType.microSize
                                    )
                                }
                                Text(
                                    text = "· ${currentStep.phaseLabel.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = AlgoType.microSize,
                                    letterSpacing = AlgoType.trackSection
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                                    .background(PurpleSubtle)
                                    .padding(horizontal = AlgoTokens.space2, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${currentQuestion.pointsAvailable} PTS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AlgoType.microSize
                                )
                            }
                        }

                        Text(
                            text = currentQuestion.promptText,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        )

                        if (currentQuestion.contextLine.isNotBlank()) {
                            Text(
                                text = currentQuestion.contextLine,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = AlgoType.microSize,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Interactive Input Form
                    when (currentQuestion.kind) {
                        PredictionKind.SWAP_DECISION,
                        PredictionKind.FOUND_DECISION,
                        PredictionKind.WILL_PUSH,
                        PredictionKind.WILL_POP,
                        PredictionKind.WILL_ENQUEUE,
                        PredictionKind.WILL_DEQUEUE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                        .background(GreenSubtle)
                                        .border(1.dp, AccentGreen.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                                        .clickable { submitAnswer(userSaidYes = true) }
                                        .padding(vertical = AlgoTokens.space3),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentQuestion.yesLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = AccentGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                        .background(PinkSubtle)
                                        .border(1.dp, AccentPink.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusSm))
                                        .clickable { submitAnswer(userSaidYes = false) }
                                        .padding(vertical = AlgoTokens.space3),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentQuestion.noLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = AccentPink,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        PredictionKind.SELECT_COMPARE_PAIR,
                        PredictionKind.SELECT_PIVOT -> {
                            val targetCount = (currentQuestion.answer as? PredictionAnswer.Indices)?.indices?.size ?: 1
                            val canSubmit = userSelectedIndices.size == targetCount

                            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                                Text(
                                    text = "Tap ${if (targetCount == 1) "the target element" else "$targetCount elements"} directly on the visualizer canvas above:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = AlgoType.microSize
                                )

                                if (userSelectedIndices.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Selected on canvas:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            fontSize = AlgoType.microSize
                                        )
                                        userSelectedIndices.sorted().forEach { idx ->
                                            val valStr = currentStep.array.getOrNull(idx)?.toString() ?: "?"
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                                    .background(CyanSubtle)
                                                    .border(AlgoTokens.strokeThin, PrimaryCyan, RoundedCornerShape(AlgoTokens.radiusXs))
                                                    .clickable { userSelectedIndices = userSelectedIndices - idx }
                                                    .padding(horizontal = AlgoTokens.space2, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "$valStr [$idx] ✕",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = PrimaryCyan,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = AlgoType.microSize
                                                )
                                            }
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                        .background(if (canSubmit) PrimaryCyan else CardBackground)
                                        .border(
                                            1.dp,
                                            if (canSubmit) PrimaryCyan else BorderSubtle,
                                            RoundedCornerShape(AlgoTokens.radiusSm)
                                        )
                                        .clickable(enabled = canSubmit) {
                                            submitAnswer(indices = userSelectedIndices)
                                        }
                                        .padding(vertical = AlgoTokens.space3),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (canSubmit) "SUBMIT PREDICTION" else "TAP $targetCount ELEMENT(S) ON CANVAS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (canSubmit) DarkBackground else TextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        PredictionKind.SELECT_VISIT_NODE -> {
                            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                                Text(
                                    text = "Which node will be visited next?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = AlgoType.microSize
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                                ) {
                                    currentStep.nodes.take(6).forEach { node ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                                .background(PurpleSubtle)
                                                .border(1.dp, SecondaryPurple.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusXs))
                                                .clickable { submitAnswer(nodeId = node.id) }
                                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = node.label,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = PurpleGlow,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Non-decision step state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    val isComplete = state.currentStepIdx >= state.steps.lastIndex
                    Text(
                        text = if (isComplete) "ALGORITHM RUN COMPLETE" else "TRANSITION STEP",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isComplete) AccentGreen else TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                    Text(
                        text = if (isComplete) "All steps completed for ${algorithm.name}." else currentStep.description.ifBlank { "Setting up elements for next decision..." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(PrimaryCyan)
                                .clickable {
                                    if (isComplete) {
                                        state.currentStepIdx = 0
                                    }
                                    feedback = null
                                    userSelectedIndices = emptySet()
                                    isPlaying = true
                                }
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                            ) {
                                Icon(
                                    imageVector = if (isComplete) AlgoGlyphs.Reset else AlgoGlyphs.Play,
                                    contentDescription = null,
                                    tint = DarkBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isComplete) "Restart Simulation" else "Auto-Play Simulation ▶",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!isComplete) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CardBackground)
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                    .clickable { jumpToNextDecision() }
                                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Seek Decision ⏭",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AlgoTokens.space6))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A14)
@Composable
fun PredictStepArenaPreview() {
    AlgoLensTheme {
        PredictStepArena()
    }
}
