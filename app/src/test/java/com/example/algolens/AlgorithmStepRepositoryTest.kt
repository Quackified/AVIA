package com.example.algolens

import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.SampleData
import com.example.algolens.ui.visualizer.ElementState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlgorithmStepRepositoryTest {

    @Test
    fun all13Algorithms_generateNonEmptySteps() {
        val testArray = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
        for (algo in SampleData.algorithms) {
            val steps = AlgorithmStepRepository.generateStepsForAlgorithm(algo, testArray)
            assertTrue("Algorithm ${algo.name} generated empty steps", steps.isNotEmpty())

            val codeLines = AlgorithmStepRepository.getCodeLinesForAlgorithm(algo)
            assertTrue("Algorithm ${algo.name} codeLines should not be empty", codeLines.isNotEmpty())
        }
    }

    @Test
    fun quickSort_generatesCorrectStepsAndPivotPointers() {
        val testArray = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
        val qSort = SampleData.algorithms.first { it.name == "Quick Sort" }
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(qSort, testArray)

        assertTrue(steps.size > 5)
        // Check pivot presence in steps
        val pivotStep = steps.find { it.topPointers.containsKey("pivot") }
        assertNotNull("QuickSort should have pivot top pointer", pivotStep)

        // Final step should be sorted
        val finalStep = steps.last()
        assertEquals(testArray.sorted(), finalStep.array)
    }

    @Test
    fun binarySearch_findsTarget() {
        val testArray = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val bSearch = SampleData.algorithms.first { it.name == "Binary Search" }
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(bSearch, testArray)

        val foundStep = steps.find { it.elementStates.values.contains(ElementState.FOUND) }
        assertNotNull("Binary Search should reach FOUND step", foundStep)
    }

    @Test
    fun stackAndQueue_generateBufferItems() {
        val stack = SampleData.algorithms.first { it.name == "Stack" }
        val stackSteps = AlgorithmStepRepository.generateStepsForAlgorithm(stack)
        assertTrue(stackSteps.any { it.buffer.isNotEmpty() })

        val queue = SampleData.algorithms.first { it.name == "Queue" }
        val queueSteps = AlgorithmStepRepository.generateStepsForAlgorithm(queue)
        assertTrue(queueSteps.any { it.buffer.isNotEmpty() })
    }

    @Test
    fun graphAlgorithms_generateNodesAndEdges() {
        val bfs = SampleData.algorithms.first { it.name.contains("BFS") }
        val bfsSteps = AlgorithmStepRepository.generateStepsForAlgorithm(bfs)
        assertTrue(bfsSteps.all { it.nodes.isNotEmpty() && it.edges.isNotEmpty() })

        val dfs = SampleData.algorithms.first { it.name.contains("DFS") }
        val dfsSteps = AlgorithmStepRepository.generateStepsForAlgorithm(dfs)
        assertTrue(dfsSteps.all { it.nodes.isNotEmpty() && it.edges.isNotEmpty() })
    }
}
