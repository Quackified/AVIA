package com.example.algolens

import com.example.algolens.data.BubbleSort
import com.example.algolens.data.SampleData
import com.example.algolens.model.StepType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun bubbleSort_sortsCorrectly() {
        val input = listOf(64, 34, 25, 12, 22, 11, 90)
        val steps = BubbleSort.generateSteps(input)

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertEquals(StepType.DONE, finalStep.type)
        assertEquals(input.sorted(), finalStep.array)
    }

    @Test
    fun sampleData_containsExpectedAlgorithms() {
        assertEquals(8, SampleData.algorithms.size)
        assertTrue(SampleData.categories.contains("Sorting"))
        assertTrue(SampleData.categories.contains("Graphs"))
    }
}