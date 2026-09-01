package com.example.algolens

import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.visualizer.BufferItem
import com.example.algolens.ui.visualizer.ChallengeFeedback
import com.example.algolens.ui.visualizer.ChallengeQuestionType
import com.example.algolens.ui.visualizer.ChallengeState
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphNodeState
import com.example.algolens.ui.visualizer.PredictionAnswer
import com.example.algolens.ui.visualizer.PredictionKind
import com.example.algolens.ui.visualizer.PredictionQuestion
import com.example.algolens.ui.visualizer.StepToken
import com.example.algolens.ui.visualizer.VisualizerStep
import com.example.algolens.ui.visualizer.buildPredictionQuestion
import com.example.algolens.ui.visualizer.highlightedCellPair
import com.example.algolens.ui.visualizer.parseStepToken
import com.example.algolens.ui.visualizer.predictionAnswerFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the "Predict Next Step" engine in `ChallengeModeManager.kt`.
 * Pure functions: [buildPredictionQuestion], [predictionAnswerFor],
 * [parseStepToken], [highlightedCellPair]. Locks the per-algorithm contract.
 */
class ChallengeModeManagerTest {

    // ─── parseStepToken ──────────────────────────────────────────────────────

    @Test
    fun parseStepToken_recognisesAllCanonicalPrefixes() {
        assertEquals(StepToken.SWAP, parseStepToken("SWAP: arr[0] <-> arr[1]"))
        assertEquals(StepToken.COMPARE, parseStepToken("COMPARE: 5 > 3?"))
        assertEquals(StepToken.SHIFT, parseStepToken("SHIFT: 4 < 7"))
        assertEquals(StepToken.ELEVATE, parseStepToken("ELEVATED KEY: 9 at [3]"))
        assertEquals(StepToken.PLACE, parseStepToken("PLACED: key 5 at [2]"))
        assertEquals(StepToken.PIVOT, parseStepToken("PIVOT: arr[4]=6"))
        assertEquals(StepToken.FOUND, parseStepToken("FOUND: arr[5]=7"))
        assertEquals(StepToken.MISS, parseStepToken("NOT FOUND"))
        assertEquals(StepToken.COMPLETE, parseStepToken("SORT COMPLETE"))
        assertEquals(StepToken.COMPLETE, parseStepToken("TRAVERSAL COMPLETE"))
        assertEquals(StepToken.PUSH, parseStepToken("PUSH: 5"))
        assertEquals(StepToken.POP, parseStepToken("POP: 5"))
        assertEquals(StepToken.ENQUEUE, parseStepToken("ENQUEUE: 5"))
        assertEquals(StepToken.DEQUEUE, parseStepToken("DEQUEUE: 5"))
        assertEquals(StepToken.VISIT, parseStepToken("VISIT: A"))
        assertEquals(StepToken.TRAVERSE, parseStepToken("TRAVERSE: A->B"))
    }

    @Test
    fun parseStepToken_handlesNullAndUnknown() {
        assertEquals(StepToken.UNKNOWN, parseStepToken(null))
        assertEquals(StepToken.UNKNOWN, parseStepToken(""))
        assertEquals(StepToken.UNKNOWN, parseStepToken("random prose without colon"))
        assertEquals(StepToken.UNKNOWN, parseStepToken("??? unknown token: stuff"))
    }

    // ─── highlightedCellPair ─────────────────────────────────────────────────

    @Test
    fun highlightedCellPair_usesComparingFirst() {
        val step = VisualizerStep(
            array = listOf(5, 3, 8, 1, 9, 2, 7, 4, 6),
            elementStates = mapOf(2 to ElementState.COMPARING, 7 to ElementState.COMPARING),
        )
        assertEquals(2 to 7, highlightedCellPair(step))
    }

    @Test
    fun highlightedCellPair_fallsBackToPointers() {
        val step = VisualizerStep(
            array = listOf(5, 3, 8, 1, 9),
            topPointers = mapOf("L" to 0),
            bottomPointers = mapOf("R" to 4),
        )
        assertEquals(0 to 4, highlightedCellPair(step))
    }

    @Test
    fun highlightedCellPair_returnsNullWhenArrayTooSmall() {
        val step = VisualizerStep(array = listOf(1), elementStates = emptyMap())
        assertNull(highlightedCellPair(step))
    }

    // ─── predictionAnswerFor ─────────────────────────────────────────────────

    @Test
    fun predictionAnswerFor_yesNo_evaluatesCorrectly() {
        val q = PredictionQuestion(
            kind = PredictionKind.SWAP_DECISION,
            promptText = "",
            contextLine = "",
            eligibleIndices = setOf(0, 1),
            eligibleNodeIds = emptySet(),
            yesLabel = "YES",
            noLabel = "NO",
            answer = PredictionAnswer.YesNo(isYes = true),
        )
        assertTrue(predictionAnswerFor(q, userSaidYes = true))
        assertFalse(predictionAnswerFor(q, userSaidYes = false))
    }

    @Test
    fun predictionAnswerFor_indices_evaluatesSetEquality() {
        val q = PredictionQuestion(
            kind = PredictionKind.SELECT_COMPARE_PAIR,
            promptText = "",
            contextLine = "",
            eligibleIndices = setOf(0, 1),
            eligibleNodeIds = emptySet(),
            yesLabel = "",
            noLabel = "",
            answer = PredictionAnswer.Indices(indices = setOf(0, 1)),
        )
        assertTrue(predictionAnswerFor(q, userSelectedIndices = setOf(1, 0)))
        assertFalse(predictionAnswerFor(q, userSelectedIndices = setOf(0, 2)))
    }

    @Test
    fun predictionAnswerFor_nodeId_evaluatesStringEquality() {
        val q = PredictionQuestion(
            kind = PredictionKind.SELECT_VISIT_NODE,
            promptText = "",
            contextLine = "",
            eligibleIndices = emptySet(),
            eligibleNodeIds = setOf("A", "B", "C"),
            yesLabel = "",
            noLabel = "",
            answer = PredictionAnswer.NodeId(id = "B"),
        )
        assertTrue(predictionAnswerFor(q, userSelectedNodeId = "B"))
        assertFalse(predictionAnswerFor(q, userSelectedNodeId = "A"))
    }

    @Test
    fun predictionAnswerFor_shapeMismatchReturnsFalse() {
        // YesNo question, user supplies indices — should be false (shape mismatch).
        val q = PredictionQuestion(
            kind = PredictionKind.SWAP_DECISION,
            promptText = "",
            contextLine = "",
            eligibleIndices = emptySet(),
            eligibleNodeIds = emptySet(),
            yesLabel = "",
            noLabel = "",
            answer = PredictionAnswer.YesNo(isYes = true),
        )
        assertFalse(predictionAnswerFor(q, userSelectedIndices = setOf(0)))
    }

    // ─── buildPredictionQuestion: Sorting ────────────────────────────────────

    @Test
    fun buildPredictionQuestion_quickSort_returnsSelectPivot() {
        val current = VisualizerStep(array = listOf(5, 3, 8, 1, 9, 2, 7, 4, 6), activeRange = 0..8)
        val next = VisualizerStep(
            array = listOf(5, 3, 8, 1, 9, 2, 7, 4, 6),
            comparisonExpr = "PIVOT: arr[4]",
            pivotIndex = 4,
        )
        val q = buildPredictionQuestion(AlgorithmId.QUICK_SORT, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_PIVOT, q!!.kind)
        assertEquals(setOf(4), q.eligibleIndices)
        assertTrue(predictionAnswerFor(q, userSelectedIndices = setOf(4)))
        assertFalse(predictionAnswerFor(q, userSelectedIndices = setOf(2)))
    }

    @Test
    fun buildPredictionQuestion_bubbleSort_swapStep_returnsSwapDecision() {
        val current = VisualizerStep(
            array = listOf(5, 3, 8, 1),
            elementStates = mapOf(0 to ElementState.COMPARING, 1 to ElementState.COMPARING),
        )
        val next = VisualizerStep(
            array = listOf(5, 3, 8, 1),
            comparisonExpr = "SWAP: arr[0] <-> arr[1]",
        )
        val q = buildPredictionQuestion(AlgorithmId.BUBBLE_SORT, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SWAP_DECISION, q!!.kind)
        assertTrue(predictionAnswerFor(q, userSaidYes = true))
        assertFalse(predictionAnswerFor(q, userSaidYes = false))
    }

    @Test
    fun buildPredictionQuestion_bubbleSort_compareStep_returnsComparePair() {
        val current = VisualizerStep(array = listOf(5, 3, 8, 1))
        val next = VisualizerStep(
            array = listOf(5, 3, 8, 1),
            comparisonExpr = "COMPARE: 8 > 1?",
            elementStates = mapOf(2 to ElementState.COMPARING, 3 to ElementState.COMPARING),
        )
        val q = buildPredictionQuestion(AlgorithmId.BUBBLE_SORT, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_COMPARE_PAIR, q!!.kind)
        assertEquals(setOf(2, 3), q.eligibleIndices)
        assertTrue(predictionAnswerFor(q, userSelectedIndices = setOf(2, 3)))
    }

    @Test
    fun buildPredictionQuestion_insertionSort_shiftStep_returnsComparePair() {
        // The "compare" step is the *next* step; the step generator
        // emits the highlighted (j, key) cells on that step.
        val current = VisualizerStep(array = listOf(2, 5, 7, 9, 4))
        val next = VisualizerStep(
            array = listOf(2, 5, 7, 9, 4),
            comparisonExpr = "SHIFT: 7 < 4",
            elementStates = mapOf(2 to ElementState.COMPARING, 4 to ElementState.COMPARING),
        )
        val q = buildPredictionQuestion(AlgorithmId.INSERTION_SORT, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_COMPARE_PAIR, q!!.kind)
        assertEquals(setOf(2, 4), q.eligibleIndices)
    }

    @Test
    fun buildPredictionQuestion_mergeSort_compareAndPlace() {
        val current = VisualizerStep(array = listOf(2, 4, 6, 1, 3, 5), recursionDepth = 1)
        val compareNext = VisualizerStep(
            array = listOf(2, 4, 6, 1, 3, 5),
            comparisonExpr = "COMPARE: 4 < 1?",
            elementStates = mapOf(1 to ElementState.COMPARING, 3 to ElementState.COMPARING),
        )
        val q1 = buildPredictionQuestion(AlgorithmId.MERGE_SORT, current, compareNext)
        assertNotNull(q1)
        assertEquals(PredictionKind.SELECT_COMPARE_PAIR, q1!!.kind)
        assertEquals(setOf(1, 3), q1.eligibleIndices)

        val placeNext = VisualizerStep(
            array = listOf(2, 4, 6, 1, 3, 5),
            comparisonExpr = "PLACED: 1",
            phaseLabel = "KEY INSERTED",
        )
        val q2 = buildPredictionQuestion(AlgorithmId.MERGE_SORT, current, placeNext)
        assertNotNull(q2)
        assertEquals(PredictionKind.WILL_DEQUEUE, q2!!.kind)
    }

    // ─── buildPredictionQuestion: Searching ──────────────────────────────────

    @Test
    fun buildPredictionQuestion_linearSearch_foundStep_returnsFoundDecision() {
        val current = VisualizerStep(array = listOf(3, 1, 4, 1, 5, 9))
        val next = VisualizerStep(
            array = listOf(3, 1, 4, 1, 5, 9),
            comparisonExpr = "FOUND: arr[4]=5",
        )
        val q = buildPredictionQuestion(AlgorithmId.LINEAR_SEARCH, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.FOUND_DECISION, q!!.kind)
        assertTrue(predictionAnswerFor(q, userSaidYes = true))
        assertFalse(predictionAnswerFor(q, userSaidYes = false))
    }

    @Test
    fun buildPredictionQuestion_binarySearch_compareStep_returnsComparePair() {
        val current = VisualizerStep(array = listOf(1, 3, 5, 7, 9, 11))
        val next = VisualizerStep(
            array = listOf(1, 3, 5, 7, 9, 11),
            comparisonExpr = "COMPARE: arr[2]=5 vs target=7",
            elementStates = mapOf(1 to ElementState.COMPARING, 3 to ElementState.COMPARING),
        )
        val q = buildPredictionQuestion(AlgorithmId.BINARY_SEARCH, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_COMPARE_PAIR, q!!.kind)
        assertEquals(setOf(1, 3), q.eligibleIndices)
    }

    // ─── buildPredictionQuestion: Buffer / Graph / Heap ──────────────────────

    @Test
    fun buildPredictionQuestion_stack_push_returnsWillPush() {
        val current = VisualizerStep(
            buffer = listOf(BufferItem("1", "1"), BufferItem("2", "2")),
            bufferCapacity = 4,
        )
        val next = VisualizerStep(
            buffer = listOf(BufferItem("1", "1"), BufferItem("2", "2"), BufferItem("3", "3")),
            bufferCapacity = 4,
            comparisonExpr = "PUSH: 3",
        )
        val q = buildPredictionQuestion(AlgorithmId.STACK, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.WILL_PUSH, q!!.kind)
        assertTrue(predictionAnswerFor(q, userSaidYes = true))
    }

    @Test
    fun buildPredictionQuestion_stack_pop_returnsWillPop() {
        val current = VisualizerStep(
            buffer = listOf(BufferItem("1", "1"), BufferItem("2", "2"), BufferItem("3", "3")),
            bufferCapacity = 4,
        )
        val next = VisualizerStep(
            buffer = listOf(BufferItem("1", "1"), BufferItem("2", "2")),
            bufferCapacity = 4,
            comparisonExpr = "POP: 3",
        )
        val q = buildPredictionQuestion(AlgorithmId.STACK, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.WILL_POP, q!!.kind)
        assertTrue(predictionAnswerFor(q, userSaidYes = true))
    }

    @Test
    fun buildPredictionQuestion_queue_enqueue_returnsWillEnqueue() {
        val current = VisualizerStep(
            buffer = listOf(BufferItem("1", "1"), BufferItem("2", "2")),
            bufferCapacity = 4,
        )
        val next = VisualizerStep(
            buffer = listOf(BufferItem("1", "1"), BufferItem("2", "2"), BufferItem("3", "3")),
            bufferCapacity = 4,
            comparisonExpr = "ENQUEUE: 3",
        )
        val q = buildPredictionQuestion(AlgorithmId.QUEUE, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.WILL_ENQUEUE, q!!.kind)
    }

    @Test
    fun buildPredictionQuestion_bfs_returnsSelectVisitNode() {
        val current = VisualizerStep()
        val next = VisualizerStep(
            nodes = listOf(
                GraphNodeState("A", "A", 0f, 0f, ElementState.VISITED),
                GraphNodeState("B", "B", 10f, 10f, ElementState.IDLE),
            ),
            comparisonExpr = "VISIT: B",
            activeNodeId = "B",
            phaseLabel = "BFS LEVEL 1",
        )
        val q = buildPredictionQuestion(AlgorithmId.BFS, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_VISIT_NODE, q!!.kind)
        assertEquals(setOf("A", "B"), q.eligibleNodeIds)
        assertTrue(predictionAnswerFor(q, userSelectedNodeId = "B"))
        assertFalse(predictionAnswerFor(q, userSelectedNodeId = "A"))
    }

    @Test
    fun buildPredictionQuestion_dfs_returnsSelectVisitNode() {
        val current = VisualizerStep()
        val next = VisualizerStep(
            nodes = listOf(
                GraphNodeState("A", "A", 0f, 0f, ElementState.VISITED),
                GraphNodeState("B", "B", 10f, 10f, ElementState.IDLE),
                GraphNodeState("C", "C", 20f, 20f, ElementState.ACTIVE),
            ),
            comparisonExpr = "TRAVERSE: B->C",
            activeNodeId = "C",
        )
        val q = buildPredictionQuestion(AlgorithmId.DFS, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_VISIT_NODE, q!!.kind)
        assertEquals("C", (q.answer as PredictionAnswer.NodeId).id)
    }

    @Test
    fun buildPredictionQuestion_bst_returnsSelectVisitNode() {
        val current = VisualizerStep()
        val next = VisualizerStep(
            nodes = listOf(GraphNodeState("root", "50", 0f, 0f)),
            comparisonExpr = "VISIT: root",
            activeNodeId = "root",
        )
        val q = buildPredictionQuestion(AlgorithmId.BINARY_SEARCH_TREE, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_VISIT_NODE, q!!.kind)
    }

    @Test
    fun buildPredictionQuestion_heap_returnsComparePairOnElevate() {
        val current = VisualizerStep(array = listOf(50, 30, 20, 10, 40))
        val next = VisualizerStep(
            array = listOf(50, 30, 20, 10, 40),
            comparisonExpr = "ELEVATED KEY: 40 at [4]",
            elementStates = mapOf(0 to ElementState.COMPARING, 4 to ElementState.COMPARING),
        )
        val q = buildPredictionQuestion(AlgorithmId.HEAP, current, next)
        assertNotNull(q)
        assertEquals(PredictionKind.SELECT_COMPARE_PAIR, q!!.kind)
    }

    // ─── buildPredictionQuestion: Terminal / null guards ─────────────────────

    @Test
    fun buildPredictionQuestion_returnsNullWhenNextStepIsNull() {
        val current = VisualizerStep(array = listOf(5, 3))
        assertNull(buildPredictionQuestion(AlgorithmId.BUBBLE_SORT, current, null))
        assertNull(buildPredictionQuestion(AlgorithmId.QUICK_SORT, current, null))
    }

    @Test
    fun buildPredictionQuestion_returnsNullOnTerminalStep() {
        val current = VisualizerStep(array = listOf(1, 2, 3))
        val next = VisualizerStep(
            array = listOf(1, 2, 3),
            comparisonExpr = "SORT COMPLETE",
        )
        assertNull(buildPredictionQuestion(AlgorithmId.BUBBLE_SORT, current, next))
        assertNull(buildPredictionQuestion(AlgorithmId.QUICK_SORT, current, next))
        assertNull(buildPredictionQuestion(AlgorithmId.LINEAR_SEARCH, current, next))
    }

    @Test
    fun buildPredictionQuestion_returnsNullForUnknownToken() {
        val current = VisualizerStep(array = listOf(5, 3))
        val next = VisualizerStep(
            array = listOf(5, 3),
            comparisonExpr = "garbage that isn't a token",
        )
        assertNull(buildPredictionQuestion(AlgorithmId.BUBBLE_SORT, current, next))
    }

    @Test
    fun buildPredictionQuestion_quickSort_withoutPivot_returnsNull() {
        val current = VisualizerStep(array = listOf(5, 3, 8))
        val next = VisualizerStep(
            array = listOf(5, 3, 8),
            comparisonExpr = "SWAP: arr[0] <-> arr[1]",
        )
        assertNull(buildPredictionQuestion(AlgorithmId.QUICK_SORT, current, next))
    }

    // ─── ChallengeState wiring ───────────────────────────────────────────────

    @Test
    fun challengeState_defaultQuestionTypeIsSwapDecision() {
        // The engine produces a fresh PredictionQuestion per step; the
        // state.questionType field is preserved for back-compat with
        // existing test fixtures and the canvas glow wiring.
        val state = ChallengeState(isActive = true)
        assertEquals(ChallengeQuestionType.SWAP_DECISION, state.questionType)
        assertNull(state.feedback)
    }

    @Test
    fun challengeFeedback_carriesPointsAndMessage() {
        val fb = ChallengeFeedback(isCorrect = true, message = "ok", pointsAwarded = 140)
        assertTrue(fb.isCorrect)
        assertEquals(140, fb.pointsAwarded)
        assertEquals("ok", fb.message)
    }
}