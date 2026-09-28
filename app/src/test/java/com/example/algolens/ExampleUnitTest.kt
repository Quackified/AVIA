package com.example.algolens

import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.SampleData
import com.example.algolens.model.AlgorithmId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun bubbleSort_sortsCorrectly() {
        val input = listOf(64, 34, 25, 12, 22, 11, 90)
        val steps = AlgorithmStepRepository.generateStepsForId(AlgorithmId.BUBBLE_SORT, input)

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertEquals(input.sorted(), finalStep.array)
    }

    @Test
    fun sampleData_containsExpectedAlgorithms() {
        assertEquals(14, SampleData.algorithms.size)
        assertTrue(SampleData.categories.contains("Sorting"))
        assertTrue(SampleData.categories.contains("Searching"))
        assertTrue(SampleData.categories.contains("Data Structures"))
        assertTrue(SampleData.categories.contains("Graph Traversal"))
        assertTrue(SampleData.categories.contains("Shortest Path"))
    }

    @Test
    fun sampleData_categoryCounts() {
        val grouped = SampleData.algorithms.groupBy { it.category }
        assertEquals(5, grouped["Sorting"]?.size)
        assertEquals(2, grouped["Searching"]?.size)
        assertEquals(4, grouped["Data Structures"]?.size)
        assertEquals(2, grouped["Graph Traversal"]?.size)
        assertEquals(1, grouped["Shortest Path"]?.size)
    }

    @Test
    fun sampleData_noPurgedEntries() {
        val names = SampleData.algorithms.map { it.name }
        assertTrue("Knapsack 0/1 should be purged", "Knapsack 0/1" !in names)
        assertTrue("Longest CS should be purged", "Longest CS" !in names)

        val categories = SampleData.algorithms.map { it.category }.toSet()
        assertTrue("Dynamic Programming should be purged", "Dynamic Programming" !in categories)
    }
}