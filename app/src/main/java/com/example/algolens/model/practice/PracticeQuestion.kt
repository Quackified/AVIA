package com.example.algolens.model.practice

import com.example.algolens.model.AlgorithmId

/**
 * Difficulty tier for practice questions with point weights.
 */
enum class PracticeDifficulty(
    val label: String,
    val basePoints: Int
) {
    EASY("Easy", 100),
    MEDIUM("Medium", 150),
    HARD("Hard", 200)
}

/**
 * An individual multiple-choice option for a practice question.
 */
data class PracticeOption(
    val id: Int,
    val label: String,
    val isCorrect: Boolean
)

/**
 * A practice challenge question covering algorithm fundamentals, step predictions,
 * or operational invariants.
 */
data class PracticeQuestion(
    val id: String,
    val algorithmId: AlgorithmId,
    val category: String,
    val difficulty: PracticeDifficulty,
    val stepTitle: String,
    val promptText: String,
    val explanation: String,
    val options: List<PracticeOption>,
    val snapshot: PracticeSnapshot? = null
)
