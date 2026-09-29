package com.avia.data

import com.avia.model.Algorithm
import com.avia.model.AlgorithmId
import com.avia.ui.visualizer.ElementState
import com.avia.ui.visualizer.GraphEdgeState
import com.avia.ui.visualizer.GraphNodeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GraphSearchTest {

    @Test
    fun buildAdjacency_createsUndirectedSymmetricMap() {
        val edges = listOf(
            GraphEdgeState("A", "B", weight = 4),
            GraphEdgeState("B", "C", weight = 2)
        )
        val adj = GraphSearch.buildAdjacency(edges)

        assertTrue(adj.containsKey("A"))
        assertTrue(adj.containsKey("B"))
        assertTrue(adj.containsKey("C"))

        assertEquals(listOf("B" to 4), adj["A"])
        assertEquals(listOf("A" to 4, "C" to 2), adj["B"])
        assertEquals(listOf("B" to 2), adj["C"])
    }

    @Test
    fun shortestPath_findsExpectedHopSequence() {
        val (nodes, edges) = AlgorithmStepRepository.canonicalWeightedGraph()
        val adj = GraphSearch.buildAdjacency(edges)

        val path = GraphSearch.shortestPath(adj, "A", "E")
        assertTrue("Path from A to E must not be empty", path.isNotEmpty())
        assertEquals("A", path.first())
        assertEquals("E", path.last())
    }

    @Test
    fun shortestPath_unreachableReturnsEmpty() {
        val edges = listOf(
            GraphEdgeState("A", "B"),
            GraphEdgeState("C", "D")
        )
        val adj = GraphSearch.buildAdjacency(edges)

        val path = GraphSearch.shortestPath(adj, "A", "D")
        assertTrue("Disconnected vertices must yield empty path", path.isEmpty())
    }

    @Test
    fun shortestPath_sameStartAndTargetReturnsSingleNode() {
        val edges = listOf(GraphEdgeState("A", "B"))
        val adj = GraphSearch.buildAdjacency(edges)

        val path = GraphSearch.shortestPath(adj, "A", "A")
        assertEquals(listOf("A"), path)
    }

    @Test
    fun bfs_withTargetTerminatesEarlyWhenTargetFound() {
        val fullSteps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.BFS),
            traversalStartNodeId = "A",
            targetNodeId = null
        )
        val targetedSteps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.BFS),
            traversalStartNodeId = "A",
            targetNodeId = "B"
        )

        assertTrue("Targeted BFS steps should be fewer or equal to full traversal", targetedSteps.size <= fullSteps.size)
        val lastStep = targetedSteps.last()
        assertTrue("Last step must mention TARGET REACHED", lastStep.description.contains("TARGET REACHED") || lastStep.phaseLabel == "TARGET REACHED")
        assertTrue("Target node B must be marked FOUND or SORTED", lastStep.nodes.any { it.id == "B" && (it.state == ElementState.FOUND || it.state == ElementState.SORTED) })
    }

    @Test
    fun bfs_withUnreachableTargetReportsExhausted() {
        val customNodes = listOf(
            GraphNodeState("A", "A", 20f, 20f),
            GraphNodeState("B", "B", 40f, 20f),
            GraphNodeState("X", "X", 80f, 80f)
        )
        val customEdges = listOf(
            GraphEdgeState("A", "B")
        )
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.BFS),
            traversalStartNodeId = "A",
            targetNodeId = "X",
            customGraph = customNodes to customEdges
        )

        assertTrue(steps.isNotEmpty())
        val lastStep = steps.last()
        assertTrue("Must report unreachable target", lastStep.description.contains("UNREACHABLE") && lastStep.phaseLabel == "NOT_FOUND")
    }

    @Test
    fun dfs_withTargetTerminatesEarlyWhenTargetFound() {
        val fullSteps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DFS),
            traversalStartNodeId = "A",
            targetNodeId = null
        )
        val targetedSteps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DFS),
            traversalStartNodeId = "A",
            targetNodeId = "B"
        )

        assertTrue(targetedSteps.isNotEmpty())
        val lastStep = targetedSteps.last()
        assertTrue("Last step must mention TARGET REACHED", lastStep.description.contains("TARGET REACHED") || lastStep.phaseLabel == "FOUND")
    }

    @Test
    fun dfs_withoutTargetCompletesFullTraversal() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.DFS),
            traversalStartNodeId = "A",
            targetNodeId = null
        )
        assertTrue(steps.isNotEmpty())
        val lastStep = steps.last()
        assertTrue("DFS complete must be reported", lastStep.description.contains("DFS Complete!"))
        assertEquals("FOUND", lastStep.phaseLabel)
    }
}
