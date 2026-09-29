package com.avia

import com.avia.data.AlgorithmStepRepository
import com.avia.model.AlgorithmId
import com.avia.ui.visualizer.PredictionAnswer
import com.avia.ui.visualizer.PredictionKind
import com.avia.ui.visualizer.buildPredictionQuestion
import com.avia.ui.visualizer.predictionAnswerFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DijkstraPredictionTest {

    @Test
    fun dijkstraGeneratesExtractMinPrediction() {
        val steps = AlgorithmStepRepository.generateDijkstraSteps(startNodeId = "A")
        assertTrue("Dijkstra must generate multiple steps", steps.size > 2)

        // Find a transition into a SETTLED step (extract-min)
        var extractMinIndex = -1
        for (i in 0 until steps.size - 1) {
            val next = steps[i + 1]
            if (next.phaseLabel in listOf("SETTLED", "FOUND") || next.comparisonExpr?.startsWith("EXTRACT-MIN") == true) {
                extractMinIndex = i
                break
            }
        }
        assertTrue("Dijkstra must contain an extract-min transition", extractMinIndex >= 0)

        val currentStep = steps[extractMinIndex]
        val nextStep = steps[extractMinIndex + 1]
        val question = buildPredictionQuestion(AlgorithmId.DIJKSTRA, currentStep, nextStep)

        assertNotNull("Prediction question must not be null for extract-min", question)
        assertEquals(PredictionKind.SELECT_VISIT_NODE, question?.kind)
        assertTrue(question?.answer is PredictionAnswer.NodeId)

        val expectedNodeId = (question?.answer as PredictionAnswer.NodeId).id
        assertEquals(nextStep.activeNodeId, expectedNodeId)

        // Verify predictionAnswerFor
        assertTrue(
            "Correct node ID must be scored as true",
            predictionAnswerFor(question, userSelectedNodeId = expectedNodeId)
        )
        assertFalse(
            "Incorrect node ID must be scored as false",
            predictionAnswerFor(question, userSelectedNodeId = "NON_EXISTENT_NODE")
        )
    }

    @Test
    fun dijkstraGeneratesRelaxationPrediction() {
        val steps = AlgorithmStepRepository.generateDijkstraSteps(startNodeId = "A")

        // Find a RELAXING step
        val relaxIndex = steps.indexOfFirst { it.phaseLabel == "RELAXING" }
        assertTrue("Dijkstra must contain at least one RELAXING step", relaxIndex >= 0)

        val currentStep = steps[relaxIndex]
        val nextStep = steps[relaxIndex + 1]
        val question = buildPredictionQuestion(AlgorithmId.DIJKSTRA, currentStep, nextStep)

        assertNotNull("Prediction question must not be null for RELAXING step", question)
        assertEquals(PredictionKind.SWAP_DECISION, question?.kind)
        assertTrue(question?.answer is PredictionAnswer.YesNo)

        val expectedYes = (question?.answer as PredictionAnswer.YesNo).isYes
        assertEquals(nextStep.phaseLabel == "UPDATING", expectedYes)

        // Verify predictionAnswerFor with Yes/No
        assertTrue(
            "Correct answer must evaluate to true",
            predictionAnswerFor(question, userSaidYes = expectedYes)
        )
        assertFalse(
            "Opposite answer must evaluate to false",
            predictionAnswerFor(question, userSaidYes = !expectedYes)
        )
    }
}
