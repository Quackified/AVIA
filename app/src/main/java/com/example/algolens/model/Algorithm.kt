package com.example.algolens.model

/**
 * Data class representing an Algorithm in the system.
 * Matches the structure found in the Figma reference App.tsx.
 */
data class Algorithm(
    val id: Int,
    val name: String,
    val category: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val colorHex: String
)
