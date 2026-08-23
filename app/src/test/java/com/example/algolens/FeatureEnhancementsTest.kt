package com.example.algolens

import com.example.algolens.data.SampleData
import com.example.algolens.ui.visualizer.AlgorithmCodeRegistry
import com.example.algolens.ui.visualizer.AlgorithmTheoryRepository
import com.example.algolens.ui.visualizer.ChallengeState
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState
import com.example.algolens.ui.visualizer.PRESET_OPTIONS
import com.example.algolens.ui.visualizer.SyntaxHighlighter
import com.example.algolens.ui.visualizer.TOUR_STEPS
import com.example.algolens.ui.visualizer.TraceLanguage
import com.example.algolens.ui.visualizer.VisualizerStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureEnhancementsTest {

    @Test
    fun edgeCasePresets_containAllRequiredOptions() {
        val presetIds = PRESET_OPTIONS.map { it.id }
        assertTrue("Presets should contain 'Random'", presetIds.contains("Random"))
        assertTrue("Presets should contain 'Already Sorted'", presetIds.contains("Already Sorted"))
        assertTrue("Presets should contain 'Reverse Sorted'", presetIds.contains("Reverse Sorted"))
        assertTrue("Presets should contain 'All Equal'", presetIds.contains("All Equal"))
    }

    @Test
    fun multiLanguageRegistry_supportsAll4LanguagesForAlgorithms() {
        val algorithms = listOf(
            "Bubble Sort",
            "Quick Sort",
            "Binary Search",
            "Insertion Sort",
            "Selection Sort",
            "Merge Sort",
            "Linear Search"
        )
        val languages = listOf(
            TraceLanguage.KOTLIN,
            TraceLanguage.JAVA,
            TraceLanguage.PYTHON,
            TraceLanguage.CPP
        )

        for (algo in algorithms) {
            for (lang in languages) {
                val codeData = AlgorithmCodeRegistry.getCode(algo, lang)
                assertTrue("Code for $algo in ${lang.label} should not be empty", codeData.lines.isNotEmpty())
                assertTrue("Line mapping for $algo in ${lang.label} should exist", codeData.lineMapping.isNotEmpty())
            }
        }
    }

    @Test
    fun syntaxHighlighter_producesFormattedAnnotatedStrings() {
        val snippet = "fun bubbleSort(arr: IntArray) { val n = 10 }"
        val highlighted = SyntaxHighlighter.highlight(snippet, TraceLanguage.KOTLIN)
        assertEquals(snippet, highlighted.text)
        assertTrue("Syntax spans should be applied", highlighted.spanStyles.isNotEmpty())
    }

    @Test
    fun graphTreeVisualizer_stateCalculations() {
        val step = VisualizerStep(
            nodes = listOf(
                GraphNodeState("A", "A", 10f, 20f, ElementState.ACTIVE),
                GraphNodeState("B", "B", 80f, 60f, ElementState.IDLE)
            ),
            edges = listOf(
                GraphEdgeState(from = "A", to = "B", isHighlighted = true)
            )
        )

        assertEquals(2, step.nodes.size)
        assertEquals(1, step.edges.size)
        assertTrue(step.edges.first().isHighlighted)
    }

    @Test
    fun algorithmTheoryRepository_containsDataForAll13Algorithms() {
        for (algo in SampleData.algorithms) {
            val theory = AlgorithmTheoryRepository.getTheory(algo.name)
            assertNotNull("Theory for ${algo.name} should not be null", theory)
            assertTrue("Theory overview for ${algo.name} should not be blank", theory.overview.isNotBlank())
            assertTrue("Theory howItWorks for ${algo.name} should not be empty", theory.howItWorks.isNotEmpty())
            assertTrue("Theory whenToUse for ${algo.name} should not be empty", theory.whenToUse.isNotEmpty())
            assertTrue("Theory bestCase for ${algo.name} should not be blank", theory.bestCase.isNotBlank())
            assertTrue("Theory worstCase for ${algo.name} should not be blank", theory.worstCase.isNotBlank())
        }
    }

    @Test
    fun guidedTour_containsAll6InteractiveSteps() {
        assertEquals(6, TOUR_STEPS.size)
        assertTrue(TOUR_STEPS.any { it.title.contains("Visualizer Canvas") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Multi-Language Code Trace") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Variable State") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Playback Controls") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Predict Next Step") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Theory Deep Dive") })
    }

    @Test
    fun challengeState_initializesAndUpdatesCorrectly() {
        var state = ChallengeState(isActive = true, score = 100, streak = 2)
        assertEquals(100, state.score)
        assertEquals(2, state.streak)
        assertTrue(state.isActive)

        state = state.copy(score = state.score + 140, streak = state.streak + 1)
        assertEquals(240, state.score)
        assertEquals(3, state.streak)
    }

    @Test
    fun offscreenPointerTarget_correctlyFlagsDirection() {
        val targetRight = com.example.algolens.ui.visualizer.OffscreenPointerTarget(
            index = 8,
            value = 7,
            label = "pivot",
            state = ElementState.PIVOT,
            badgeBg = androidx.compose.ui.graphics.Color.Red,
            badgeTextColor = androidx.compose.ui.graphics.Color.White,
            isRight = true
        )
        assertTrue(targetRight.isRight)
        assertEquals("pivot", targetRight.label)
        assertEquals(7, targetRight.value)
        assertEquals(8, targetRight.index)

        val targetLeft = com.example.algolens.ui.visualizer.OffscreenPointerTarget(
            index = 0,
            value = 3,
            label = "i",
            state = ElementState.ACTIVE,
            badgeBg = androidx.compose.ui.graphics.Color.Cyan,
            badgeTextColor = androidx.compose.ui.graphics.Color.Black,
            isRight = false
        )
        assertFalse(targetLeft.isRight)
    }
}


