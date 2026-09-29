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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.avia.data.AlgorithmRegistry
import com.avia.data.SampleData
import com.avia.model.AlgorithmId
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.AudioHaptics
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentOrange
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AccentRed
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
import com.avia.ui.theme.OrangeSubtle
import com.avia.ui.theme.PinkSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.RedSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.YellowSubtle
import com.avia.ui.visualizer.ChallengeFeedback
import com.avia.ui.visualizer.PredictionAnswer
import com.avia.ui.visualizer.PredictionKind
import com.avia.ui.visualizer.PredictionQuestion
import com.avia.ui.visualizer.VisualizerHost
import com.avia.ui.visualizer.buildPredictionQuestion
import com.avia.ui.visualizer.predictionAnswerFor
import com.avia.ui.visualizer.rememberVisualizerScreenState

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
    AlgorithmId.DFS,
    AlgorithmId.DIJKSTRA
)

/**
 * Predict the Step Screen.
 *
 * How it works:
 *  - Opens and simulates automatically (no "Start Simulation" button).
 *  - Automatically pauses at ~5 decision checkpoints.
 *  - The question card appears ONLY when the simulator is paused at a checkpoint.
 *  - Zero floating overlays inside the canvas stage (no text clipping with PhaseBanner or telemetry).
 *  - High-contrast YES/NO buttons with clean, readable text.
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
    var isPlaying by remember { mutableStateOf(true) }
    var answeredCheckpoints by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val algorithm = remember(selectedAlgoId) {
        SampleData.algorithms.find { it.id == selectedAlgoId }
            ?: SampleData.algorithms.first()
    }
    val state = rememberVisualizerScreenState(algorithm)
    val spec = remember(selectedAlgoId) { AlgorithmRegistry.specFor(selectedAlgoId) }

    // ── Checkpoint Scheduler: 5–8 distributed stops ──
    val allDecisionIndices = remember(selectedAlgoId, state.steps) {
        state.steps.indices.filter { idx ->
            idx < state.steps.lastIndex && buildPredictionQuestion(selectedAlgoId, state.steps[idx], state.steps[idx + 1]) != null
        }
    }

    val scheduledCheckpoints = remember(allDecisionIndices) {
        if (allDecisionIndices.isEmpty()) emptyList()
        else {
            val targetStops = when {
                allDecisionIndices.size <= 4 -> allDecisionIndices.size
                allDecisionIndices.size <= 10 -> minOf(5, allDecisionIndices.size)
                allDecisionIndices.size <= 20 -> 6
                else -> 8
            }
            val stepSize = (allDecisionIndices.size - 1).toFloat() / (targetStops - 1).coerceAtLeast(1)
            (0 until targetStops).map { i ->
                allDecisionIndices[(i * stepSize).toInt().coerceIn(0, allDecisionIndices.lastIndex)]
            }.distinct()
        }
    }

    // Auto-play immediately when switching algorithms
    androidx.compose.runtime.LaunchedEffect(selectedAlgoId) {
        feedback = null
        userSelectedIndices = emptySet()
        answeredCheckpoints = emptySet()
        state.currentStepIdx = 0
        isPlaying = true
    }

    val currentStep = state.currentStep
    val nextStep = state.steps.getOrNull(state.currentStepIdx + 1)
    val currentQuestion = remember(selectedAlgoId, state.currentStepIdx, state.steps) {
        buildPredictionQuestion(selectedAlgoId, currentStep, nextStep)
    }

    val isCurrentStepCheckpoint = state.currentStepIdx in scheduledCheckpoints
    val checkpointOrder = scheduledCheckpoints.indexOf(state.currentStepIdx)
    val checkpointNumber = if (checkpointOrder >= 0) checkpointOrder + 1 else null
    val isPausedForQuestion = !isPlaying && isCurrentStepCheckpoint && state.currentStepIdx !in answeredCheckpoints && currentQuestion != null

    // ── Simulation Loop: advances automatically, stops at checkpoints ──
    androidx.compose.runtime.LaunchedEffect(isPlaying, state.currentStepIdx, feedback) {
        if (!isPlaying || feedback != null) return@LaunchedEffect

        if (state.currentStepIdx >= state.steps.lastIndex) {
            isPlaying = false
            return@LaunchedEffect
        }

        // If currently on an unanswered checkpoint, halt immediately for question
        if (state.currentStepIdx in scheduledCheckpoints && state.currentStepIdx !in answeredCheckpoints) {
            val q = buildPredictionQuestion(selectedAlgoId, state.currentStep, state.steps.getOrNull(state.currentStepIdx + 1))
            if (q != null) {
                isPlaying = false
                feedback = null
                userSelectedIndices = emptySet()
                return@LaunchedEffect
            }
        }

        delay(480L)

        // Advance
        val prevStep = state.currentStep
        state.stepForward()
        if (com.avia.data.AppSettings.soundEnabled) {
            com.avia.ui.audio.AlgorithmAudioEngine.playStep(state.currentStep, prevStep)
        }

        // Check if newly landed step is a checkpoint
        if (state.currentStepIdx in scheduledCheckpoints && state.currentStepIdx !in answeredCheckpoints) {
            val q = buildPredictionQuestion(selectedAlgoId, state.currentStep, state.steps.getOrNull(state.currentStepIdx + 1))
            if (q != null) {
                isPlaying = false
                feedback = null
                userSelectedIndices = emptySet()
            }
        }
    }

    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

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
        answeredCheckpoints = answeredCheckpoints + state.currentStepIdx
        if (isCorrect) {
            score += pts
            streak++
            correctAnswers++
            AudioHaptics.performCorrect(view, haptic)
        } else {
            streak = 0
            AudioHaptics.performIncorrect(view, haptic)
        }
        feedback = ChallengeFeedback(
            isCorrect = isCorrect,
            message = if (isCorrect) "Correct! ${q.contextLine}" else "Incorrect. ${q.contextLine}",
            pointsAwarded = pts
        )
    }

    fun continueSimulation() {
        AudioHaptics.performClick(view, haptic)
        feedback = null
        userSelectedIndices = emptySet()
        if (state.currentStepIdx < state.steps.lastIndex) {
            state.stepForward()
        }
        isPlaying = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        // ── 1. Clean Header Strip ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Predict the Step",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Algorithm: ${algorithm.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
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
                            .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${streak}x Streak",
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
                        .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                ) {
                    Text(
                        text = "$score pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = AlgoType.microSize
                    )
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
                        .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
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

        // ── 3. Visualizer Stage Card (Zero floating overlays inside to avoid clipping) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
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
        }

        // ── 4. Status Strip & Minimal Transport ──
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
            val isFinished = state.currentStepIdx >= state.steps.lastIndex

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                // Pause / Resume Toggle
                if (!isFinished) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(if (isPlaying) PurpleSubtle else CyanSubtle)
                            .border(AlgoTokens.strokeThin, if (isPlaying) SecondaryPurple else PrimaryCyan, RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { isPlaying = !isPlaying }
                            .padding(horizontal = AlgoTokens.space3, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) AlgoGlyphs.Pause else AlgoGlyphs.Play,
                                contentDescription = null,
                                tint = if (isPlaying) SecondaryPurple else PrimaryCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isPlaying) "Pause" else "Resume",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPlaying) SecondaryPurple else PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                        }
                    }
                }

                Text(
                    text = "Step ${state.displayStepIdx + 1}/${state.totalSteps}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize
                )

                if (scheduledCheckpoints.isNotEmpty()) {
                    Text(
                        text = "· Questions: ${answeredCheckpoints.size}/${scheduledCheckpoints.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
            }

            // Restart Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .clickable {
                        feedback = null
                        userSelectedIndices = emptySet()
                        answeredCheckpoints = emptySet()
                        state.currentStepIdx = 0
                        isPlaying = true
                    }
                    .padding(horizontal = AlgoTokens.space2, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Reset,
                        contentDescription = "Restart",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Restart",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = AlgoType.microSize
                    )
                }
            }
        }

        // ── 5. Question Card (ONLY visible when stopped at a checkpoint or finished) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                .background(CardBackgroundElevated)
                .border(
                    width = 1.dp,
                    color = when {
                        feedback?.isCorrect == true -> AccentGreen.copy(alpha = 0.6f)
                        feedback?.isCorrect == false -> AccentRed.copy(alpha = 0.6f)
                        isPausedForQuestion -> AccentYellow.copy(alpha = 0.5f)
                        else -> BorderSubtle
                    },
                    shape = RoundedCornerShape(AlgoTokens.radiusMd)
                )
                .padding(AlgoTokens.space5)
        ) {
            when {
                // ── Case A: Feedback State ──
                feedback != null -> {
                    val fb = feedback!!
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(if (fb.isCorrect) GreenSubtle else RedSubtle)
                                    .border(1.5.dp, if (fb.isCorrect) AccentGreen else AccentRed, CircleShape),
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
                                    text = if (fb.isCorrect) "Correct! +${fb.pointsAwarded} pts" else "Incorrect",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (fb.isCorrect) AccentGreen else AccentRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = fb.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(PrimaryCyan)
                                .clickable { continueSimulation() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Continue →",
                                style = MaterialTheme.typography.labelMedium,
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // ── Case B: Paused at a Checkpoint Question ──
                isPausedForQuestion -> {
                    val q = currentQuestion!!
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${checkpointNumber ?: 1} of ${scheduledCheckpoints.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )

                            Text(
                                text = "+${q.pointsAvailable} pts",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                        }

                        Text(
                            text = q.promptText,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )

                        // High-contrast decision buttons
                        when (q.kind) {
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
                                    // High-Contrast YES Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                            .background(GreenSubtle)
                                            .border(1.5.dp, AccentGreen, RoundedCornerShape(AlgoTokens.radiusSm))
                                            .clickable {
                                                AudioHaptics.performSelect(view, haptic)
                                                submitAnswer(userSaidYes = true)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(AccentGreen),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = AlgoGlyphs.Check,
                                                    contentDescription = null,
                                                    tint = DarkBackground,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                            Text(
                                                text = q.yesLabel,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    // High-Contrast NO Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                            .background(RedSubtle)
                                            .border(1.5.dp, AccentRed, RoundedCornerShape(AlgoTokens.radiusSm))
                                            .clickable {
                                                AudioHaptics.performSelect(view, haptic)
                                                submitAnswer(userSaidYes = false)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(AccentRed),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = AlgoGlyphs.Close,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                            Text(
                                                text = q.noLabel,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            PredictionKind.SELECT_COMPARE_PAIR,
                            PredictionKind.SELECT_PIVOT -> {
                                val targetCount = (q.answer as? PredictionAnswer.Indices)?.indices?.size ?: 1
                                val canSubmit = userSelectedIndices.size == targetCount

                                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                                    Text(
                                        text = "Select ${if (targetCount == 1) "the target element" else "$targetCount target elements"}:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                                    ) {
                                        currentStep.array.forEachIndexed { idx, value ->
                                            val isSelected = idx in userSelectedIndices

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                                    .background(if (isSelected) CyanSubtle else CardBackground)
                                                    .border(
                                                        width = if (isSelected) 1.5.dp else 1.dp,
                                                        color = if (isSelected) PrimaryCyan else BorderSubtle,
                                                        shape = RoundedCornerShape(AlgoTokens.radiusXs)
                                                    )
                                                    .clickable {
                                                        AudioHaptics.performSelect(view, haptic)
                                                        userSelectedIndices = if (isSelected) {
                                                            userSelectedIndices - idx
                                                        } else {
                                                            if (userSelectedIndices.size < targetCount) userSelectedIndices + idx
                                                            else setOf(idx)
                                                        }
                                                    }
                                                    .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "$value",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = if (isSelected) PrimaryCyan else TextPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "[$idx]",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isSelected) PrimaryCyan else TextDark,
                                                        fontSize = 9.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                            .background(if (canSubmit) PrimaryCyan else CardBackground)
                                            .border(
                                                1.dp,
                                                if (canSubmit) PrimaryCyan else BorderSubtle,
                                                RoundedCornerShape(AlgoTokens.radiusSm)
                                            )
                                            .clickable(enabled = canSubmit) {
                                                AudioHaptics.performClick(view, haptic)
                                                submitAnswer(indices = userSelectedIndices)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (canSubmit) "Submit →" else "Select $targetCount element(s)",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (canSubmit) DarkBackground else TextMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            PredictionKind.SELECT_VISIT_NODE -> {
                                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                                    Text(
                                        text = "Which node is visited next?",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
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
                                                    .border(1.dp, SecondaryPurple, RoundedCornerShape(AlgoTokens.radiusXs))
                                                    .clickable {
                                                        AudioHaptics.performSelect(view, haptic)
                                                        submitAnswer(nodeId = node.id)
                                                    }
                                                    .padding(horizontal = AlgoTokens.space4, vertical = 7.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = node.label,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Case C: Simulation Completed ──
                state.currentStepIdx >= state.steps.lastIndex -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                    ) {
                        Text(
                            text = "Simulation Complete",
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "You answered $correctAnswers of ${scheduledCheckpoints.size} questions correctly ($score pts).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(PrimaryCyan)
                                .clickable {
                                    AudioHaptics.performClick(view, haptic)
                                    state.currentStepIdx = 0
                                    answeredCheckpoints = emptySet()
                                    feedback = null
                                    userSelectedIndices = emptySet()
                                    isPlaying = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Play Again ↺",
                                style = MaterialTheme.typography.labelMedium,
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // ── Case D: Actively Running (No premature question!) ──
                else -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
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
                                text = if (isPlaying) "SIMULATING..." else "PAUSED",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPlaying) PrimaryCyan else AccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = currentStep.description.ifBlank { "Watching ${algorithm.name} execute..." },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
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
