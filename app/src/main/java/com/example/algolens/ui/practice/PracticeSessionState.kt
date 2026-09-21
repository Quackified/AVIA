package com.example.algolens.ui.practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.algolens.data.practice.PracticeQuestionRepository
import com.example.algolens.model.practice.PracticeDifficulty
import com.example.algolens.model.practice.PracticeQuestion

/**
 * State manager owning the reactive session flow for Explore / Practice Mode.
 */
@Stable
class PracticeSessionManager(
    initialCategory: String = "All"
) {
    var selectedCategory by mutableStateOf(initialCategory)
        private set

    var selectedDifficulty by mutableStateOf<PracticeDifficulty?>(null)
        private set

    var questions by mutableStateOf<List<PracticeQuestion>>(emptyList())
        private set

    var currentIndex by mutableIntStateOf(0)
        private set

    var selectedOptionId by mutableStateOf<Int?>(null)
        private set

    var isSubmitted by mutableStateOf(false)
        private set

    var isCorrect by mutableStateOf<Boolean?>(null)
        private set

    var score by mutableIntStateOf(0)
        private set

    var streak by mutableIntStateOf(0)
        private set

    var bestStreak by mutableIntStateOf(0)
        private set

    var correctCount by mutableIntStateOf(0)
        private set

    var isSessionCompleted by mutableStateOf(false)
        private set

    init {
        loadQuestions()
    }

    val currentQuestion: PracticeQuestion?
        get() = questions.getOrNull(currentIndex)

    val totalQuestions: Int
        get() = questions.size

    val progressFraction: Float
        get() {
            if (questions.isEmpty()) return 0f
            if (isSessionCompleted) return 1f
            val base = currentIndex.toFloat() / questions.size.toFloat()
            val stepBoost = if (isSubmitted) (1f / questions.size.toFloat()) else if (selectedOptionId != null) (0.5f / questions.size.toFloat()) else 0f
            return (base + stepBoost).coerceIn(0f, 1f)
        }

    fun selectCategory(category: String) {
        if (selectedCategory == category) return
        selectedCategory = category
        restartSession()
    }

    fun selectDifficulty(difficulty: PracticeDifficulty?) {
        if (selectedDifficulty == difficulty) return
        selectedDifficulty = difficulty
        restartSession()
    }

    fun selectOption(optionId: Int) {
        if (isSubmitted) return
        selectedOptionId = optionId
    }

    fun submitAnswer() {
        val q = currentQuestion ?: return
        val optId = selectedOptionId ?: return
        if (isSubmitted) return

        val opt = q.options.find { it.id == optId }
        val correct = opt?.isCorrect == true

        isCorrect = correct
        isSubmitted = true

        if (correct) {
            correctCount++
            streak++
            if (streak > bestStreak) {
                bestStreak = streak
            }
            // Base points + streak bonus (20 pts per consecutive answer)
            val streakBonus = (streak - 1).coerceAtLeast(0) * 20
            score += q.difficulty.basePoints + streakBonus
        } else {
            streak = 0
        }
    }

    fun nextQuestion() {
        if (currentIndex + 1 < questions.size) {
            currentIndex++
            selectedOptionId = null
            isSubmitted = false
            isCorrect = null
        } else {
            isSessionCompleted = true
        }
    }

    fun restartSession() {
        loadQuestions()
        currentIndex = 0
        selectedOptionId = null
        isSubmitted = false
        isCorrect = null
        score = 0
        streak = 0
        bestStreak = 0
        correctCount = 0
        isSessionCompleted = false
    }

    private fun loadQuestions() {
        questions = PracticeQuestionRepository.getQuestions(
            category = selectedCategory,
            difficulty = selectedDifficulty
        )
    }
}

/**
 * Remember a [PracticeSessionManager] scoped to the Explore / Practice screen.
 */
@Composable
fun rememberPracticeSessionManager(
    initialCategory: String = "All"
): PracticeSessionManager = remember {
    PracticeSessionManager(initialCategory)
}
