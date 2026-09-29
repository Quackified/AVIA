package com.avia

import com.avia.data.practice.PracticeQuestionRepository
import com.avia.model.practice.PracticeDifficulty
import com.avia.ui.practice.PracticeSessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite for the Explore (Practice Mode) backend:
 *  - [PracticeQuestionRepository] data integrity and category queries.
 *  - [PracticeSessionManager] state machine, scoring, streaks, and navigation.
 */
class PracticeBackendTest {

    // ── Repository Data Integrity ──────────────────────────────────────────

    @Test
    fun repository_allQuestions_haveValidOptionsAndExactlyOneCorrect() {
        val questions = PracticeQuestionRepository.getQuestions("All")
        assertTrue("Repository should have at least 15 questions", questions.size >= 15)

        questions.forEach { q ->
            assertTrue("Question ${q.id} must have at least 2 options", q.options.size >= 2)
            val correctCount = q.options.count { it.isCorrect }
            assertEquals("Question ${q.id} must have exactly one correct option", 1, correctCount)
            assertTrue("Question ${q.id} promptText must not be blank", q.promptText.isNotBlank())
            assertTrue("Question ${q.id} explanation must not be blank", q.explanation.isNotBlank())
            assertTrue("Question ${q.id} stepTitle must not be blank", q.stepTitle.isNotBlank())
        }
    }

    @Test
    fun repository_coversAllFourCoreCategories() {
        val sortingQuestions = PracticeQuestionRepository.getQuestions("Sorting")
        val searchingQuestions = PracticeQuestionRepository.getQuestions("Searching")
        val dataStructureQuestions = PracticeQuestionRepository.getQuestions("Data Structures")
        val graphQuestions = PracticeQuestionRepository.getQuestions("Graph Traversal")

        assertTrue("Sorting category must have questions", sortingQuestions.isNotEmpty())
        assertTrue("Searching category must have questions", searchingQuestions.isNotEmpty())
        assertTrue("Data Structures category must have questions", dataStructureQuestions.isNotEmpty())
        assertTrue("Graph Traversal category must have questions", graphQuestions.isNotEmpty())

        val totalFromCategories = sortingQuestions.size + searchingQuestions.size +
            dataStructureQuestions.size + graphQuestions.size
        assertEquals(
            "Sum of category questions must equal 'All'",
            PracticeQuestionRepository.getQuestions("All").size,
            totalFromCategories
        )
    }

    @Test
    fun repository_filtersByDifficultyTier() {
        val easyQuestions = PracticeQuestionRepository.getQuestions("All", PracticeDifficulty.EASY)
        val mediumQuestions = PracticeQuestionRepository.getQuestions("All", PracticeDifficulty.MEDIUM)

        assertTrue("Must have Easy questions", easyQuestions.isNotEmpty())
        assertTrue("Must have Medium questions", mediumQuestions.isNotEmpty())

        easyQuestions.forEach {
            assertEquals(PracticeDifficulty.EASY, it.difficulty)
        }
        mediumQuestions.forEach {
            assertEquals(PracticeDifficulty.MEDIUM, it.difficulty)
        }
    }

    @Test
    fun repository_containsFundamentalsQuestions() {
        val allQuestions = PracticeQuestionRepository.getQuestions("All")
        val complexityQuestions = allQuestions.filter {
            it.promptText.contains("complexity", ignoreCase = true) ||
                it.stepTitle.contains("COMPLEXITY", ignoreCase = true)
        }
        assertTrue(
            "Repository should contain fundamental complexity questions as requested by user",
            complexityQuestions.size >= 4
        )
    }

    // ── Session Manager State Machine ──────────────────────────────────────

    @Test
    fun sessionManager_initialState() {
        val manager = PracticeSessionManager("Sorting")
        assertEquals("Sorting", manager.selectedCategory)
        assertTrue(manager.questions.isNotEmpty())
        assertEquals(0, manager.currentIndex)
        assertNull(manager.selectedOptionId)
        assertFalse(manager.isSubmitted)
        assertNull(manager.isCorrect)
        assertEquals(0, manager.score)
        assertEquals(0, manager.streak)
        assertEquals(0, manager.correctCount)
        assertFalse(manager.isSessionCompleted)
    }

    @Test
    fun sessionManager_selectOptionAndSubmitCorrectAnswer() {
        val manager = PracticeSessionManager("Sorting")
        val q = manager.currentQuestion!!
        val correctOption = q.options.first { it.isCorrect }

        // Select correct option
        manager.selectOption(correctOption.id)
        assertEquals(correctOption.id, manager.selectedOptionId)
        assertFalse(manager.isSubmitted)

        // Submit
        manager.submitAnswer()
        assertTrue(manager.isSubmitted)
        assertEquals(true, manager.isCorrect)
        assertEquals(1, manager.streak)
        assertEquals(1, manager.bestStreak)
        assertEquals(1, manager.correctCount)
        assertTrue("Score should increase by base points", manager.score >= q.difficulty.basePoints)
    }

    @Test
    fun sessionManager_streakBonusCalculation() {
        val manager = PracticeSessionManager("All")

        // Answer 1st correctly
        val q1 = manager.currentQuestion!!
        val opt1 = q1.options.first { it.isCorrect }
        manager.selectOption(opt1.id)
        manager.submitAnswer()
        val scoreAfterFirst = manager.score
        assertEquals(1, manager.streak)
        assertEquals(q1.difficulty.basePoints, scoreAfterFirst)

        // Advance to 2nd question
        manager.nextQuestion()
        val q2 = manager.currentQuestion!!
        val opt2 = q2.options.first { it.isCorrect }
        manager.selectOption(opt2.id)
        manager.submitAnswer()

        assertEquals(2, manager.streak)
        // 2nd consecutive answer earns basePoints + 20 streak bonus
        val expectedScore = scoreAfterFirst + q2.difficulty.basePoints + 20
        assertEquals(expectedScore, manager.score)
    }

    @Test
    fun sessionManager_incorrectAnswerResetsStreak() {
        val manager = PracticeSessionManager("All")

        // Answer 1st correctly
        val q1 = manager.currentQuestion!!
        manager.selectOption(q1.options.first { it.isCorrect }.id)
        manager.submitAnswer()
        assertEquals(1, manager.streak)

        // Answer 2nd incorrectly
        manager.nextQuestion()
        val q2 = manager.currentQuestion!!
        val wrongOption = q2.options.first { !it.isCorrect }
        manager.selectOption(wrongOption.id)
        manager.submitAnswer()

        assertEquals(false, manager.isCorrect)
        assertEquals(0, manager.streak)
        assertEquals(1, manager.bestStreak) // best streak preserved
    }

    @Test
    fun sessionManager_switchingCategoryResetsState() {
        val manager = PracticeSessionManager("Sorting")
        manager.selectOption(manager.currentQuestion!!.options.first { it.isCorrect }.id)
        manager.submitAnswer()
        assertTrue(manager.score > 0)

        // Switch to Searching
        manager.selectCategory("Searching")
        assertEquals("Searching", manager.selectedCategory)
        assertEquals(0, manager.currentIndex)
        assertEquals(0, manager.score)
        assertEquals(0, manager.streak)
        assertNull(manager.selectedOptionId)
        assertFalse(manager.isSubmitted)
    }

    @Test
    fun sessionManager_advancesUntilSessionCompleted() {
        val manager = PracticeSessionManager("Searching")
        val count = manager.totalQuestions

        for (i in 0 until count) {
            assertFalse(manager.isSessionCompleted)
            manager.selectOption(manager.currentQuestion!!.options.first().id)
            manager.submitAnswer()
            manager.nextQuestion()
        }

        assertTrue(manager.isSessionCompleted)
        assertEquals(1.0f, manager.progressFraction, 0.01f)
    }
}
