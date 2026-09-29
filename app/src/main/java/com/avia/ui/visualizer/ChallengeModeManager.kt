package com.avia.ui.visualizer

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
import com.avia.model.AlgorithmId
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.BorderCyan
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.CyanGlow
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.GreenSubtle
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
import com.avia.ui.theme.AlgoType
import com.avia.ui.components.AlgoGlyphs

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



/** What kind of prediction question to ask. */
enum class PredictionKind {
    SWAP_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT,
    FOUND_DECISION, WILL_ENQUEUE, WILL_DEQUEUE, WILL_PUSH, WILL_POP,
    SELECT_VISIT_NODE,
}

/** A single prediction question the user must answer. */
data class PredictionQuestion(
    val kind: PredictionKind,
    val promptText: String,
    val contextLine: String,
    val eligibleIndices: Set<Int>,
    val eligibleNodeIds: Set<String>,
    val yesLabel: String,
    val noLabel: String,
    val answer: PredictionAnswer,
    val pointsAvailable: Int = 100,
)

/** Typed answer to a [PredictionQuestion]. */
sealed class PredictionAnswer {
    data class YesNo(val isYes: Boolean) : PredictionAnswer()
    data class Indices(val indices: Set<Int>) : PredictionAnswer()
    data class NodeId(val id: String) : PredictionAnswer()
}

/** Typed predicate tokens emitted by the step repository. */
internal enum class StepToken {
    SWAP, COMPARE, SHIFT, ELEVATE, PLACE,
    PIVOT, FOUND, MISS, COMPLETE,
    PUSH, POP, ENQUEUE, DEQUEUE, VISIT, TRAVERSE, UNKNOWN,
}

internal fun parseStepToken(comparisonExpr: String?): StepToken {
    if (comparisonExpr.isNullOrBlank()) return StepToken.UNKNOWN
    val head = comparisonExpr.substringBefore(':').trim().uppercase()
    return when (head) {
        "SWAP" -> StepToken.SWAP
        "COMPARE" -> StepToken.COMPARE
        "SHIFT" -> StepToken.SHIFT
        "ELEVATED KEY" -> StepToken.ELEVATE
        "PLACED" -> StepToken.PLACE
        "PIVOT", "INITIAL PIVOT", "INITIAL MIN", "INITIAL MAX" -> StepToken.PIVOT
        "FOUND" -> StepToken.FOUND
        "MISS", "NOT FOUND" -> StepToken.MISS
        "SORT COMPLETE", "SEARCH COMPLETE", "TRAVERSAL COMPLETE" -> StepToken.COMPLETE
        "PUSH" -> StepToken.PUSH
        "POP" -> StepToken.POP
        "ENQUEUE" -> StepToken.ENQUEUE
        "DEQUEUE" -> StepToken.DEQUEUE
        "VISIT", "VISITED" -> StepToken.VISIT
        "TRAVERSE", "TRAVERSED" -> StepToken.TRAVERSE
        else -> StepToken.UNKNOWN
    }
}

/** Score the user's prediction against the answer baked into the question. */
fun predictionAnswerFor(
    question: PredictionQuestion,
    userSaidYes: Boolean? = null,
    userSelectedIndices: Set<Int> = emptySet(),
    userSelectedNodeId: String? = null,
): Boolean = when (val expected = question.answer) {
    is PredictionAnswer.YesNo -> when (question.kind) {
        PredictionKind.SWAP_DECISION,
        PredictionKind.FOUND_DECISION,
        PredictionKind.WILL_ENQUEUE,
        PredictionKind.WILL_DEQUEUE,
        PredictionKind.WILL_PUSH,
        PredictionKind.WILL_POP -> userSaidYes == expected.isYes
        else -> false
    }
    is PredictionAnswer.Indices -> when (question.kind) {
        PredictionKind.SELECT_COMPARE_PAIR,
        PredictionKind.SELECT_PIVOT -> userSelectedIndices == expected.indices
        else -> false
    }
    is PredictionAnswer.NodeId -> when (question.kind) {
        PredictionKind.SELECT_VISIT_NODE -> userSelectedNodeId == expected.id
        else -> false
    }
}

/**
 * Public dispatch for the prediction engine. Picks the right per-family
 * builder based on [algorithm]. Returns null for terminal steps and
 * for any step where the engine can't read a meaningful next-action.
 */
fun buildPredictionQuestion(
    algorithm: AlgorithmId,
    currentStep: VisualizerStep,
    nextStep: VisualizerStep?,
): PredictionQuestion? {
    if (nextStep == null) return null
    if (parseStepToken(nextStep.comparisonExpr) == StepToken.COMPLETE) return null
    return when (algorithm) {
        AlgorithmId.BUBBLE_SORT -> swapOrComparePairQuestion(currentStep, nextStep)
        AlgorithmId.SELECTION_SORT -> swapOrComparePairQuestion(currentStep, nextStep)
        AlgorithmId.INSERTION_SORT -> insertionSortQuestion(currentStep, nextStep)
        AlgorithmId.MERGE_SORT -> mergeSortQuestion(currentStep, nextStep)
        AlgorithmId.QUICK_SORT -> quickSortQuestion(currentStep, nextStep)
        AlgorithmId.LINEAR_SEARCH -> foundOrCompareQuestion(currentStep, nextStep)
        AlgorithmId.BINARY_SEARCH -> foundOrCompareQuestion(currentStep, nextStep)
        AlgorithmId.STACK -> bufferPushPopQuestion(currentStep, nextStep, isStack = true)
        AlgorithmId.QUEUE -> bufferPushPopQuestion(currentStep, nextStep, isStack = false)
        AlgorithmId.BINARY_SEARCH_TREE -> graphVisitQuestion(currentStep, nextStep)
        AlgorithmId.HEAP -> heapQuestion(currentStep, nextStep)
        AlgorithmId.BFS -> graphVisitQuestion(currentStep, nextStep)
        AlgorithmId.DFS -> graphVisitQuestion(currentStep, nextStep)
        AlgorithmId.DIJKSTRA -> dijkstraPredictionQuestion(currentStep, nextStep)
    }
}

/** The two array indices highlighted by COMPARING/SWAPPING. */
internal fun highlightedCellPair(step: VisualizerStep): Pair<Int, Int>? {
    if (step.array.size < 2) return null
    val highlighted = step.elementStates
        .filter { it.value == ElementState.COMPARING || it.value == ElementState.SWAPPING }
        .keys
        .filter { it in step.array.indices }
        .sorted()
    if (highlighted.size >= 2) {
        return highlighted.first() to highlighted.last()
    }
    val fromPointers = (step.bottomPointers.values + step.topPointers.values)
        .filter { it in step.array.indices }
        .distinct()
        .sorted()
    return if (fromPointers.size >= 2) {
        fromPointers.first() to fromPointers.last()
    } else null
}


private fun swapOrComparePairQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? {
    val currentPair = highlightedCellPair(currentStep)
    if (currentPair != null) {
        val willSwap = parseStepToken(nextStep.comparisonExpr) == StepToken.SWAP
        val valA = currentStep.array.getOrNull(currentPair.first)?.toString() ?: "?"
        val valB = currentStep.array.getOrNull(currentPair.second)?.toString() ?: "?"
        return PredictionQuestion(
            kind = PredictionKind.SWAP_DECISION,
            promptText = "Will elements $valA (at [${currentPair.first}]) and $valB (at [${currentPair.second}]) swap next?",
            contextLine = "Comparing: $valA vs $valB (${if (currentStep.phaseLabel.isNotBlank()) currentStep.phaseLabel else "comparison"})",
            eligibleIndices = setOf(currentPair.first, currentPair.second),
            eligibleNodeIds = emptySet(),
            yesLabel = "YES (SWAP)",
            noLabel = "NO (KEEP)",
            answer = PredictionAnswer.YesNo(isYes = willSwap),
        )
    }
    return when (parseStepToken(nextStep.comparisonExpr)) {
        StepToken.SWAP -> swapQuestion(currentStep, nextStep)
        StepToken.COMPARE -> comparePairQuestion(currentStep, nextStep)
        else -> null
    }
}

private fun swapQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? {
    val pair = highlightedCellPair(currentStep) ?: highlightedCellPair(nextStep) ?: return null
    val valA = currentStep.array.getOrNull(pair.first)?.toString() ?: "?"
    val valB = currentStep.array.getOrNull(pair.second)?.toString() ?: "?"
    return PredictionQuestion(
        kind = PredictionKind.SWAP_DECISION,
        promptText = "Will elements $valA (at [${pair.first}]) and $valB (at [${pair.second}]) swap next?",
        contextLine = "Highlighted: arr[${pair.first}] ($valA) & arr[${pair.second}] ($valB)",
        eligibleIndices = setOf(pair.first, pair.second),
        eligibleNodeIds = emptySet(),
        yesLabel = "YES (SWAP)",
        noLabel = "NO (KEEP)",
        answer = PredictionAnswer.YesNo(isYes = true),
    )
}

private fun comparePairQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? {
    val pair = highlightedCellPair(nextStep) ?: return null
    return PredictionQuestion(
        kind = PredictionKind.SELECT_COMPARE_PAIR,
        promptText = "Which two cells get compared next?",
        contextLine = "Phase: ${nextStep.phaseLabel}",
        eligibleIndices = setOf(pair.first, pair.second),
        eligibleNodeIds = emptySet(),
        yesLabel = "TAP CELLS",
        noLabel = "",
        answer = PredictionAnswer.Indices(indices = setOf(pair.first, pair.second)),
    )
}

private fun quickSortQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? {
    val pivotIdx = nextStep.pivotIndex
        ?: nextStep.topPointers.entries
            .firstOrNull { it.key.equals("pivot", ignoreCase = true) }?.value
        ?: return null
    if (pivotIdx !in currentStep.array.indices) return null
    return PredictionQuestion(
        kind = PredictionKind.SELECT_PIVOT,
        promptText = "Which cell will be the pivot next?",
        contextLine = "Active range: ${currentStep.activeRange ?: "full array"}",
        eligibleIndices = setOf(pivotIdx),
        eligibleNodeIds = emptySet(),
        yesLabel = "TAP PIVOT",
        noLabel = "",
        answer = PredictionAnswer.Indices(indices = setOf(pivotIdx)),
    )
}


private fun yesNoQuestion(
    currentStep: VisualizerStep,
    promptText: String,
    contextLine: String,
    isYes: Boolean,
    kind: PredictionKind,
): PredictionQuestion = PredictionQuestion(
    kind = kind,
    promptText = promptText,
    contextLine = contextLine,
    eligibleIndices = currentStep.array.indices.toSet(),
    eligibleNodeIds = emptySet(),
    yesLabel = "YES",
    noLabel = "NO",
    answer = PredictionAnswer.YesNo(isYes = isYes),
)

private fun foundOrCompareQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? = when (parseStepToken(nextStep.comparisonExpr)) {
    StepToken.FOUND -> yesNoQuestion(
        currentStep = currentStep,
        promptText = "Will the search find the target next?",
        contextLine = "Phase: ${nextStep.phaseLabel}",
        isYes = true,
        kind = PredictionKind.FOUND_DECISION,
    )
    StepToken.MISS -> yesNoQuestion(
        currentStep = currentStep,
        promptText = "Will the search conclude 'not found' next?",
        contextLine = "Phase: ${currentStep.phaseLabel}",
        isYes = true,
        kind = PredictionKind.FOUND_DECISION,
    )
    StepToken.COMPARE -> comparePairQuestion(currentStep, nextStep)
    else -> null
}

private fun insertionSortQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? = when (parseStepToken(nextStep.comparisonExpr)) {
    StepToken.SHIFT, StepToken.COMPARE -> comparePairQuestion(currentStep, nextStep)
    StepToken.PLACE -> yesNoQuestion(
        currentStep = currentStep,
        promptText = "Will the elevated key be placed next?",
        contextLine = "Phase: KEY ELEVATED",
        isYes = true,
        kind = PredictionKind.WILL_DEQUEUE,
    )
    StepToken.ELEVATE -> yesNoQuestion(
        currentStep = currentStep,
        promptText = "Will a new key be elevated next?",
        contextLine = "Phase: ${currentStep.phaseLabel}",
        isYes = true,
        kind = PredictionKind.WILL_PUSH,
    )
    else -> null
}

private fun mergeSortQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? = when (parseStepToken(nextStep.comparisonExpr)) {
    StepToken.COMPARE -> comparePairQuestion(currentStep, nextStep)
    StepToken.PLACE -> yesNoQuestion(
        currentStep = currentStep,
        promptText = "Will these two cells merge next?",
        contextLine = "Depth ${currentStep.recursionDepth}",
        isYes = true,
        kind = PredictionKind.WILL_DEQUEUE,
    )
    else -> null
}


private fun bufferPushPopQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
    isStack: Boolean,
): PredictionQuestion? {
    val currentSize = currentStep.buffer.size
    val nextSize = nextStep.buffer.size
    val nextToken = parseStepToken(nextStep.comparisonExpr)
    val (kind, promptText, isYes) = when {
        nextSize > currentSize && nextToken == StepToken.PUSH ->
            Triple(PredictionKind.WILL_PUSH, "Will a value be pushed next?", true)
        nextSize > currentSize && nextToken == StepToken.ENQUEUE ->
            Triple(PredictionKind.WILL_ENQUEUE, "Will a value be enqueued next?", true)
        nextSize < currentSize && nextToken == StepToken.POP && isStack ->
            Triple(PredictionKind.WILL_POP, "Will the top be popped next?", true)
        nextSize < currentSize && nextToken == StepToken.DEQUEUE && !isStack ->
            Triple(PredictionKind.WILL_DEQUEUE, "Will the front be dequeued next?", true)
        else -> return null
    }
    return yesNoQuestion(
        currentStep = currentStep,
        promptText = promptText,
        contextLine = "Size: $currentSize to $nextSize",
        isYes = isYes,
        kind = kind,
    )
}

private fun heapQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? = when (parseStepToken(nextStep.comparisonExpr)) {
    StepToken.ELEVATE, StepToken.COMPARE -> comparePairQuestion(currentStep, nextStep)
    else -> null
}

private fun graphVisitQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? {
    val nextActive = nextStep.activeNodeId ?: return null
    val eligibleIds = nextStep.nodes.map { it.id }.toSet()
    if (nextActive !in eligibleIds) return null
    return PredictionQuestion(
        kind = PredictionKind.SELECT_VISIT_NODE,
        promptText = "Which node will be visited next?",
        contextLine = "Phase: ${nextStep.phaseLabel}",
        eligibleIndices = emptySet(),
        eligibleNodeIds = eligibleIds,
        yesLabel = "TAP NODE",
        noLabel = "",
        answer = PredictionAnswer.NodeId(id = nextActive),
    )
}

private fun dijkstraPredictionQuestion(
    currentStep: VisualizerStep,
    nextStep: VisualizerStep,
): PredictionQuestion? {
    // 1. If next step is EXTRACT-MIN / SETTLED / FOUND
    val isExtractMin = nextStep.comparisonExpr?.startsWith("EXTRACT-MIN") == true ||
        nextStep.phaseLabel in listOf("SETTLED", "FOUND")
    if (isExtractMin) {
        val nextActive = nextStep.activeNodeId ?: return null
        val pqNodeIds = currentStep.buffer.mapNotNull { it.nodeId }.toSet()
        val eligibleIds = if (pqNodeIds.isNotEmpty()) pqNodeIds else nextStep.nodes.map { it.id }.toSet()
        if (nextActive !in eligibleIds) return null
        return PredictionQuestion(
            kind = PredictionKind.SELECT_VISIT_NODE,
            promptText = "Which node has minimum distance in Priority Queue and will be settled next?",
            contextLine = "Phase: ${nextStep.phaseLabel} · Min-PQ (${currentStep.buffer.size} entries)",
            eligibleIndices = emptySet(),
            eligibleNodeIds = eligibleIds,
            yesLabel = "TAP NODE",
            noLabel = "",
            answer = PredictionAnswer.NodeId(id = nextActive),
        )
    }

    // 2. If current step is RELAXING edge (u -> v)
    if (currentStep.phaseLabel == "RELAXING") {
        val u = currentStep.variables["u"] ?: ""
        val v = currentStep.variables["v"] ?: ""
        val newDist = currentStep.variables["newDist"] ?: ""
        val oldDist = currentStep.variables["oldDist"] ?: ""
        val willRelax = nextStep.phaseLabel == "UPDATING"
        return PredictionQuestion(
            kind = PredictionKind.SWAP_DECISION,
            promptText = "Will edge ($u → $v) relax the shortest distance to $v?",
            contextLine = "Candidate dist = $newDist vs current dist = $oldDist",
            eligibleIndices = emptySet(),
            eligibleNodeIds = emptySet(),
            yesLabel = "RELAX",
            noLabel = "KEEP",
            answer = PredictionAnswer.YesNo(isYes = willRelax),
        )
    }

    return null
}


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
): Set<Int> {
    // Hard safety bound: every returned index must actually exist in the
    // current array, otherwise the CellArrayVisualizer can IndexOutOfBounds
    // while drawing challenge halos or accepting taps on these cells.
    val validRange = step.array.indices
    fun bound(indices: Set<Int>): Set<Int> = indices.filter { it in validRange }.toSet()

    return when (type) {
        ChallengeQuestionType.SWAP_DECISION -> {
            val active = step.elementStates.filter {
                it.value == ElementState.COMPARING || it.value == ElementState.SWAPPING
            }.keys
            if (active.size >= 2) {
                bound(active)
            } else {
                val pointers = (step.bottomPointers.values + step.topPointers.values)
                    .distinct()
                    .sorted()
                when {
                    pointers.size >= 2 -> bound(pointers.take(2).toSet())
                    // Fall back to the first two valid indices of the array
                    // (never 0,1 unconditionally — that would crash on an
                    // empty or single-cell array).
                    step.array.size >= 2 -> setOf(0, 1).filter { it in validRange }.toSet()
                    else -> emptySet()
                }
            }
        }
        ChallengeQuestionType.SELECT_PIVOT -> {
            val pivot = step.pivotIndex
                ?: step.topPointers.entries
                    .firstOrNull { it.key.equals("pivot", ignoreCase = true) }?.value
            if (pivot != null && pivot in validRange) {
                setOf(pivot)
            } else {
                step.activeRange?.let { range ->
                    val mid = (range.first + range.last) / 2
                    if (mid in validRange) setOf(mid) else emptySet()
                } ?: emptySet()
            }
        }
        ChallengeQuestionType.SELECT_COMPARE_PAIR -> {
            val comparing = step.elementStates
                .filter { it.value == ElementState.COMPARING }.keys
            if (comparing.size >= 2) {
                bound(comparing)
            } else {
                val pointers = step.bottomPointers.values
                    .distinct()
                    .sorted()
                    .take(2)
                    .toSet()
                bound(pointers)
            }
        }
    }
}

/**
 * Compact Challenge Mode prompt rendered as a slim horizontal strip
 * between the canvas and the code trace (not on top of the canvas).
 * Driven by the typed prediction engine ([buildPredictionQuestion],
 * [predictionAnswerFor]).
 */
@Composable
fun CanvasChallengePrompt(
    algorithm: AlgorithmId,
    step: VisualizerStep,
    nextStep: VisualizerStep?,
    state: ChallengeState,
    onStateChange: (ChallengeState) -> Unit,
    onContinueNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val question = remember(algorithm, step.stepIndex, nextStep?.stepIndex) {
        buildPredictionQuestion(algorithm, step, nextStep)
    }
    val borderColor = when {
        state.feedback?.isCorrect == true -> AccentGreen
        state.feedback?.isCorrect == false -> AccentRed
        else -> SecondaryPurple.copy(alpha = 0.5f)
    }
    if (question == null && state.feedback == null) return

    fun onAnswered(
        userSaidYes: Boolean? = null,
        userSelectedIndices: Set<Int> = emptySet(),
        userSelectedNodeId: String? = null
    ) {
        val q = question ?: return
        val isCorrect = predictionAnswerFor(
            question = q,
            userSaidYes = userSaidYes,
            userSelectedIndices = userSelectedIndices,
            userSelectedNodeId = userSelectedNodeId,
        )
        val pts = if (isCorrect) q.pointsAvailable + (state.streak * 20) else 0
        onStateChange(
            state.copy(
                score = state.score + pts,
                streak = if (isCorrect) state.streak + 1 else 0,
                totalQuestions = state.totalQuestions + 1,
                correctAnswers = state.correctAnswers + (if (isCorrect) 1 else 0),
                selectedIndices = userSelectedIndices,
                feedback = ChallengeFeedback(
                    isCorrect = isCorrect,
                    message = if (isCorrect) "Spot on! ${q.contextLine}" else "Incorrect. ${q.contextLine}",
                    pointsAwarded = pts,
                ),
            )
        )
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
            PromptHeader(state)
            val fb = state.feedback
            if (fb == null && question != null) {
                PredictionPromptBody(
                    question = question,
                    step = step,
                    onAnswerYes = { onAnswered(userSaidYes = true) },
                    onAnswerNo = { onAnswered(userSaidYes = false) },
                    onAnswerIndices = { onAnswered(userSelectedIndices = it) },
                    onAnswerNodeId = { onAnswered(userSelectedNodeId = it) }
                )
            } else if (fb != null) {
                FeedbackRow(
                    feedback = fb,
                    onContinue = {
                        onStateChange(state.copy(feedback = null))
                        onContinueNext()
                    },
                )
            }
        }
    }
}


/** Header row: streak / score badges + mode label. */
@Composable
private fun PromptHeader(state: ChallengeState) {
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(AlgoGlyphs.Bolt, contentDescription = null, tint = AccentPink, modifier = Modifier.size(10.dp))
                    Text("${state.streak}", style = MaterialTheme.typography.labelSmall, color = AccentPink, fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(YellowSubtle)
                    .border(1.dp, AccentYellow.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(AlgoGlyphs.Target, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(10.dp))
                    Text("${state.score} PTS", style = MaterialTheme.typography.labelSmall, color = AccentYellow, fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize)
                }
            }
        }
        Text(
            text = "PREDICT NEXT STEP",
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize,
            letterSpacing = AlgoType.trackSection
        )
    }
}

/**
 * Body of [CanvasChallengePrompt] -- renders the typed prompt text and
 * the right kind of answer surface.
 */
@Composable
private fun PredictionPromptBody(
    question: PredictionQuestion,
    step: VisualizerStep,
    onAnswerYes: () -> Unit,
    onAnswerNo: () -> Unit,
    onAnswerIndices: (Set<Int>) -> Unit,
    onAnswerNodeId: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = question.promptText,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = AlgoType.labelSize, lineHeight = AlgoType.leadingMicroRelaxed, maxLines = 2,
            )
            Text(
                text = question.contextLine,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = AlgoType.microSize, lineHeight = AlgoType.leadingMicroTight, maxLines = 1,
            )
        }
        when (question.kind) {
            PredictionKind.SWAP_DECISION,
            PredictionKind.FOUND_DECISION,
            PredictionKind.WILL_PUSH,
            PredictionKind.WILL_POP,
            PredictionKind.WILL_ENQUEUE,
            PredictionKind.WILL_DEQUEUE -> YesNoButtonRow(
                yesLabel = question.yesLabel,
                noLabel = question.noLabel,
                onYes = onAnswerYes, onNo = onAnswerNo,
            )
            PredictionKind.SELECT_COMPARE_PAIR,
            PredictionKind.SELECT_PIVOT -> TapIndicesChip(
                question = question, step = step, onSubmit = onAnswerIndices,
            )
            PredictionKind.SELECT_VISIT_NODE -> Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                step.nodes.take(6).forEach { node ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PurpleSubtle)
                            .border(1.dp, SecondaryPurple.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                            .clickable { onAnswerNodeId(node.id) }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = node.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }
        }
    }
}


/** Two side-by-side pill buttons used by every Yes/No question kind. */
@Composable
private fun YesNoButtonRow(
    yesLabel: String,
    noLabel: String,
    onYes: () -> Unit,
    onNo: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(GreenSubtle)
            .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(7.dp))
            .clickable(onClick = onYes)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(yesLabel, style = MaterialTheme.typography.labelSmall, color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(PinkSubtle)
            .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(7.dp))
            .clickable(onClick = onNo)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(noLabel, style = MaterialTheme.typography.labelSmall, color = AccentPink, fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize)
    }
}

/**
 * Tap-to-pick pill chips -- one chip per eligible index. Each chip shows
 * the cell VALUE (so the user reasons about the algorithm, not about
 * positions). Tapping toggles selection; a SUBMIT button scores the
 * answer when the user has picked the expected number of cells.
 *
 * This is the fix for "It doesn't tap": previously the first chip tap
 * was scored immediately, which was always wrong for compare-pair (needs
 * two cells). Now the user can pick both before submitting.
 */
@Composable
private fun TapIndicesChip(
    question: PredictionQuestion,
    step: VisualizerStep,
    onSubmit: (Set<Int>) -> Unit,
) {
    val targetSize = (question.answer as? PredictionAnswer.Indices)?.indices?.size ?: 1
    var selected by remember(question, step.stepIndex) { mutableStateOf(setOf<Int>()) }
    val canSubmit = selected.size == targetSize

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        question.eligibleIndices.toSortedSet().forEach { idx ->
            val value = step.array.getOrNull(idx)
            val isSelected = idx in selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) PrimaryCyan.copy(alpha = 0.18f) else CyanSubtle)
                    .border(
                        1.dp,
                        if (isSelected) PrimaryCyan else PrimaryCyan.copy(alpha = 0.4f),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable {
                        selected = if (isSelected) selected - idx else selected + idx
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(value?.toString() ?: "?", style = MaterialTheme.typography.labelSmall, color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = AlgoType.labelSize)
                    Text("[$idx]", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Normal, fontSize = AlgoType.microSize)
                }
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(7.dp))
                .background(if (canSubmit) PrimaryCyan else CardBackground)
                .border(
                    1.dp,
                    if (canSubmit) PrimaryCyan else PrimaryCyan.copy(alpha = 0.4f),
                    RoundedCornerShape(7.dp)
                )
                .clickable(enabled = canSubmit) {
                    onSubmit(selected)
                }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (canSubmit) "SUBMIT" else "PICK $targetSize",
                style = MaterialTheme.typography.labelSmall,
                color = if (canSubmit) DarkBackground else TextSecondary,
                fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize,
            )
        }
    }
}


/** Bottom row of [CanvasChallengePrompt] after the user answers. */
@Composable
private fun FeedbackRow(
    feedback: ChallengeFeedback,
    onContinue: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (if (feedback.isCorrect) "CORRECT (+${feedback.pointsAwarded} PTS) " else "INCORRECT ") + feedback.message,
            style = MaterialTheme.typography.bodySmall,
            color = if (feedback.isCorrect) AccentGreen else AccentRed,
            fontSize = AlgoType.microSize, lineHeight = AlgoType.leadingMicro,
            modifier = Modifier.weight(1f), maxLines = 2,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(7.dp))
                .background(if (feedback.isCorrect) PrimaryCyan else SecondaryPurple)
                .clickable(onClick = onContinue)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("NEXT", style = MaterialTheme.typography.labelSmall, color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = AlgoType.microSize)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
private fun CanvasChallengePromptPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CanvasChallengePrompt(
                algorithm = AlgorithmId.QUICK_SORT,
                step = VisualizerStep(
                    description = "Comparing arr[1]=8 and arr[2]=9",
                    comparisonExpr = "COMPARE: 8 > 9?",
                    array = listOf(3, 8, 9, 2, 6),
                    elementStates = mapOf(1 to ElementState.COMPARING, 2 to ElementState.COMPARING)
                ),
                nextStep = VisualizerStep(
                    description = "Pivot partition step",
                    comparisonExpr = "PIVOT: arr[4]",
                    pivotIndex = 4,
                    array = listOf(3, 8, 9, 2, 6),
                    elementStates = mapOf(4 to ElementState.PIVOT)
                ),
                state = ChallengeState(isActive = true, score = 240, streak = 2),
                onStateChange = {}, onContinueNext = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
private fun CanvasChallengePromptFoundPreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CanvasChallengePrompt(
                algorithm = AlgorithmId.LINEAR_SEARCH,
                step = VisualizerStep(
                    description = "Inspecting index 5",
                    comparisonExpr = "COMPARE: arr[5]=7 vs target=7",
                    array = listOf(3, 8, 9, 2, 6, 7, 5)
                ),
                nextStep = VisualizerStep(
                    description = "Found 7 at index 5",
                    comparisonExpr = "FOUND: arr[5]=7"
                ),
                state = ChallengeState(isActive = true, score = 80, streak = 1),
                onStateChange = {}, onContinueNext = {}
            )
        }
    }
}
