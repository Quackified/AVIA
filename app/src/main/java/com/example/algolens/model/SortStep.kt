package com.example.algolens.model

/**
 * Represents a single frame or step in a sorting algorithm animation.
 */
data class SortStep(
    val array: List<Int>,
    val type: StepType,
    val indices: List<Int>,
    val description: String
)

enum class StepType {
    COMPARE,
    SWAP,
    DONE
}
