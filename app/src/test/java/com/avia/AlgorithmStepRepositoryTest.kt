package com.avia

import com.avia.data.AlgorithmCodeRegistry
import com.avia.data.AlgorithmStepRepository
import com.avia.data.SampleData
import com.avia.data.TraceLanguage
import com.avia.ui.visualizer.ElementState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlgorithmStepRepositoryTest {

    @Test
    fun all14Algorithms_generateNonEmptySteps() {
        val testArray = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
        for (algo in SampleData.algorithms) {
            val steps = AlgorithmStepRepository.generateStepsForAlgorithm(algo, testArray)
            assertTrue("Algorithm ${algo.name} generated empty steps", steps.isNotEmpty())

            // The Python-only `getCodeLinesForAlgorithm` was removed in
            // favour of `AlgorithmCodeRegistry`, which is the single
            // source of truth used by the Code Trace pane.
            val codeData = AlgorithmCodeRegistry.getCode(algo.name, TraceLanguage.PYTHON)
            assertTrue(
                "Algorithm ${algo.name} Python codeLines should not be empty",
                codeData.lines.isNotEmpty()
            )
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
    fun linearSearch_findsCustomTarget() {
        val testArray = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
        val lSearch = SampleData.algorithms.first { it.name == "Linear Search" }
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(lSearch, testArray, searchTarget = 9)

        val found = steps.firstOrNull { it.bottomPointers.containsKey("found") }
        assertNotNull("Linear Search should reach a FOUND step for custom target 9", found)
        // The dedicated FOUND step names the found element's index via the "found" bottom pointer.
        assertEquals(2, found!!.bottomPointers["found"])
    }

    @Test
    fun linearSearch_reportsNotFoundForMissingTarget() {
        val testArray = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
        val lSearch = SampleData.algorithms.first { it.name == "Linear Search" }
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(lSearch, testArray, searchTarget = 100)

        assertFalse("Linear Search must not FOUND for a missing target", steps.any { it.elementStates.values.contains(ElementState.FOUND) })
        assertTrue("Last linear step should report not found", steps.last().description.contains("not found"))
    }

    @Test
    fun binarySearch_findsCustomTarget() {
        val testArray = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val bSearch = SampleData.algorithms.first { it.name == "Binary Search" }
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(bSearch, testArray, searchTarget = 1)

        val found = steps.find { it.elementStates.values.contains(ElementState.FOUND) }
        assertNotNull("Binary Search should reach FOUND for custom target 1", found)
    }

    @Test
    fun binarySearch_reportsNotFoundForMissingTarget() {
        val testArray = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val bSearch = SampleData.algorithms.first { it.name == "Binary Search" }
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(bSearch, testArray, searchTarget = 100)

        assertFalse("Binary Search must not FOUND for a missing target", steps.any { it.elementStates.values.contains(ElementState.FOUND) })
        assertTrue("Last binary step should report not found", steps.last().description.contains("not found"))
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
