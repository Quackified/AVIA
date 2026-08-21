package com.example.algolens.data

import com.example.algolens.model.SortStep
import com.example.algolens.model.StepType

object BubbleSort {
    /**
     * Generates a sequence of steps for the Bubble Sort algorithm.
     */
    fun generateSteps(input: List<Int>): List<SortStep> {
        val steps = mutableListOf<SortStep>()
        val arr = input.toMutableList()
        val n = arr.size

        // Initial state
        steps.add(SortStep(arr.toList(), StepType.DONE, emptyList(), "Starting Bubble Sort"))

        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                // Comparison step
                steps.add(
                    SortStep(
                        arr.toList(),
                        StepType.COMPARE,
                        listOf(j, j + 1),
                        "Comparing ${arr[j]} and ${arr[j + 1]}"
                    )
                )

                if (arr[j] > arr[j + 1]) {
                    // Swap
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp

                    // Swap step
                    steps.add(
                        SortStep(
                            arr.toList(),
                            StepType.SWAP,
                            listOf(j, j + 1),
                            "Swapping ${arr[j]} and ${arr[j + 1]}"
                        )
                    )
                }
            }
        }

        // Final state
        steps.add(SortStep(arr.toList(), StepType.DONE, emptyList(), "Sort complete!"))
        return steps
    }
}
