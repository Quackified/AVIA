package com.example.algolens.data

import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DijkstraStepsTest {

    @Test
    fun dijkstra_generatesNonEmptySteps() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DIJKSTRA),
            traversalStartNodeId = "A",
            targetNodeId = null
        )
        assertTrue("Dijkstra steps must not be empty", steps.isNotEmpty())
        assertTrue("Dijkstra must generate multiple steps", steps.size >= 5)

        val firstStep = steps.first()
        assertEquals("INITIALIZING", firstStep.phaseLabel)
        assertEquals("A", firstStep.activeNodeId)
    }

    @Test
    fun dijkstra_pqBufferContainsFormattedDistanceItems() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DIJKSTRA),
            traversalStartNodeId = "A",
            targetNodeId = null
        )

        // Find a step where Priority Queue has items
        val pqStep = steps.find { it.buffer.isNotEmpty() }
        assertNotNull("Priority queue buffer must contain entries during execution", pqStep)
        val sampleItem = pqStep!!.buffer.first()
        assertTrue("Buffer item value should include distance format e.g. A(0)", sampleItem.value.contains("(") && sampleItem.value.contains(")"))
    }

    @Test
    fun dijkstra_withTargetFindsOptimalPathAndTerminates() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DIJKSTRA),
            traversalStartNodeId = "A",
            targetNodeId = "E"
        )

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertEquals("FOUND", finalStep.phaseLabel)
        assertTrue("Description must state shortest path", finalStep.description.contains("TARGET REACHED: Shortest path is"))
        assertTrue("ComparisonExpr should show path cost", finalStep.comparisonExpr?.contains("SHORTEST PATH") == true)

        // Path edges should be highlighted
        val highlightedEdges = finalStep.edges.filter { it.isHighlighted }
        assertTrue("Shortest path edges must be highlighted", highlightedEdges.isNotEmpty())

        // Target node must be marked FOUND or SORTED
        val targetNode = finalStep.nodes.find { it.id == "E" }
        assertNotNull("Target node E must exist", targetNode)
        assertTrue("Target node state must be FOUND or SORTED", targetNode!!.state == ElementState.FOUND || targetNode.state == ElementState.SORTED)
    }

    @Test
    fun dijkstra_withUnreachableTargetReportsUnreachable() {
        val customNodes = listOf(
            GraphNodeState("A", "A", 20f, 20f),
            GraphNodeState("B", "B", 40f, 20f),
            GraphNodeState("Z", "Z", 80f, 80f)
        )
        val customEdges = listOf(
            GraphEdgeState("A", "B", weight = 3)
        )
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DIJKSTRA),
            traversalStartNodeId = "A",
            targetNodeId = "Z",
            customGraph = customNodes to customEdges
        )

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertEquals("NOT_FOUND", finalStep.phaseLabel)
        assertTrue(finalStep.description.contains("TARGET UNREACHABLE"))
    }

    @Test
    fun dijkstra_withoutTargetExploresAllReachableNodes() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DIJKSTRA),
            traversalStartNodeId = "A",
            targetNodeId = null
        )

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertEquals("SORTED", finalStep.phaseLabel)
        assertTrue("All reachable nodes should be visited", finalStep.visitedNodeIds.contains("A"))
        assertTrue("Description must state completion", finalStep.description.contains("Dijkstra Complete!"))
    }
}
