package com.example.algolens

import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.visualizer.DeckPage
import com.example.algolens.ui.visualizer.VisualizerStep
import com.example.algolens.ui.visualizer.VisualizerScreenState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VisualizerScreenStateTest {

    private fun makeAlgorithm(): Algorithm = Algorithm(id = AlgorithmId.QUICK_SORT)

    /**
     * Build a state that *already* has [steps] populated (bypassing the
     * `LaunchedEffect` that normally generates them). This lets us
     * unit-test the state machine in isolation.
     */
    private fun makeState(stepCount: Int = 5): VisualizerScreenState {
        val state = VisualizerScreenState(makeAlgorithm())
        // Use reflection-free path: the `steps` setter is `internal set`
        // from within the package. The class is in `ui.visualizer` and
        // the test is in the same module so internal access works.
        state.steps = (0 until stepCount).map {
            VisualizerStep(stepIndex = it, array = listOf(0))
        }
        return state
    }

    @Test
    fun currentStepIdx_clampsToStepRange() {
        val state = makeState(3)
        // 0..2 are valid
        state.scrubTo(99)
        assertEquals(2, state.currentStepIdx)
        state.scrubTo(-1)
        assertEquals(0, state.currentStepIdx)
    }

    @Test
    fun stepForward_advancesAndStopsPlaying() {
        val state = makeState(3)
        state.isPlaying = true
        state.stepForward()
        assertEquals(1, state.currentStepIdx)
        assertFalse("stepForward should stop playback", state.isPlaying)
    }

    @Test
    fun stepForward_atLastStep_isNoOp() {
        val state = makeState(3)
        state.currentStepIdx = 2
        state.stepForward()
        assertEquals(2, state.currentStepIdx)
    }

    @Test
    fun stepBackward_atZero_isNoOp() {
        val state = makeState(3)
        state.stepBackward()
        assertEquals(0, state.currentStepIdx)
    }

    @Test
    fun cycleSpeed_rotatesThroughThreeValues() {
        val state = makeState(1)
        assertEquals(1_000L, state.playbackSpeedMs)
        state.cycleSpeed()
        assertEquals(600L, state.playbackSpeedMs)
        state.cycleSpeed()
        assertEquals(300L, state.playbackSpeedMs)
        state.cycleSpeed()
        assertEquals(1_000L, state.playbackSpeedMs)
    }

    @Test
    fun speedLabel_matchesPlaybackSpeed() {
        val state = makeState(1)
        assertEquals("0.5x", state.speedLabel)
        state.cycleSpeed()
        assertEquals("1.0x", state.speedLabel)
        state.cycleSpeed()
        assertEquals("2.0x", state.speedLabel)
    }

    @Test
    fun reset_returnsToStartAndStops() {
        val state = makeState(5)
        state.currentStepIdx = 3
        state.isPlaying = true
        state.reset()
        assertEquals(0, state.currentStepIdx)
        assertFalse(state.isPlaying)
    }

    @Test
    fun toggleChallenge_flipsActiveAndStopsPlayback() {
        val state = makeState(1)
        state.isPlaying = true
        state.toggleChallenge()
        assertTrue(state.challengeState.isActive)
        assertFalse(state.isPlaying)
        state.toggleChallenge()
        assertFalse(state.challengeState.isActive)
    }

    @Test
    fun challengeInFlight_isTrueOnlyWhenActiveAndNoFeedback() {
        val state = makeState(1)
        assertFalse(state.challengeInFlight)
        state.toggleChallenge()
        assertTrue(state.challengeInFlight)
    }

    @Test
    fun setCellSelected_togglesMembership() {
        val state = makeState(1)
        state.setCellSelected(3)
        assertTrue(3 in state.challengeState.selectedIndices)
        state.setCellSelected(3)
        assertFalse(3 in state.challengeState.selectedIndices)
        state.setCellSelected(7)
        state.setCellSelected(9)
        assertTrue(setOf(7, 9) == state.challengeState.selectedIndices)
    }

    @Test
    fun totalSteps_isAlwaysAtLeastOne() {
        val empty = VisualizerScreenState(makeAlgorithm())
        assertEquals(1, empty.totalSteps)
        val populated = makeState(4)
        assertEquals(4, populated.totalSteps)
    }

    @Test
    fun currentStep_isSafeWhenStepsEmpty() {
        val state = VisualizerScreenState(makeAlgorithm())
        // Should not throw — currentStep returns a default VisualizerStep
        // when the step list is empty.
        val step = state.currentStep
        assertEquals(0, step.array.size)
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Phase 5A — Focus Deck + scrub preview
    // ─────────────────────────────────────────────────────────────────────

    @Test
    fun deck_startsCollapsedOnTheTracePage() {
        val state = makeState(3)
        assertEquals(DeckPage.TRACE, state.deckPage)
        assertFalse("the deck must not cover the stage on open", state.deckExpanded)
    }

    @Test
    fun openDeck_selectsPageAndExpands() {
        val state = makeState(3)
        state.openDeck(DeckPage.STATE)
        assertEquals(DeckPage.STATE, state.deckPage)
        assertTrue(state.deckExpanded)
    }

    @Test
    fun toggleDeckPage_collapsesWhenReselectingTheOpenPage() {
        val state = makeState(3)

        state.toggleDeckPage(DeckPage.STATE)
        assertEquals(DeckPage.STATE, state.deckPage)
        assertTrue(state.deckExpanded)

        // Re-selecting the page already showing is the collapse gesture.
        state.toggleDeckPage(DeckPage.STATE)
        assertEquals(DeckPage.STATE, state.deckPage)
        assertFalse(state.deckExpanded)
    }

    @Test
    fun collapseDeck_leavesTheSelectedPageAlone() {
        val state = makeState(3)
        state.openDeck(DeckPage.STATE)
        state.collapseDeck()
        assertFalse(state.deckExpanded)
        assertEquals(DeckPage.STATE, state.deckPage)
    }

    @Test
    fun previewScrub_movesThePreviewButNotThePlayhead() {
        val state = makeState(5)
        state.previewScrub(3)

        assertEquals(3, state.displayStepIdx)
        assertEquals("the playhead must not commit mid-drag", 0, state.currentStepIdx)
        assertEquals(3, state.scrubTarget)
    }

    @Test
    fun previewScrub_clampsToTheStepRange() {
        val state = makeState(3)
        state.previewScrub(99)
        assertEquals(2, state.scrubTarget)
        state.previewScrub(-4)
        assertEquals(0, state.scrubTarget)
    }

    @Test
    fun previewScrub_stopsPlayback() {
        val state = makeState(5)
        state.isPlaying = true
        state.previewScrub(2)
        assertFalse("a drag must never fight the playback tick", state.isPlaying)
    }

    @Test
    fun commitScrub_promotesThePreviewToThePlayhead() {
        val state = makeState(5)
        state.previewScrub(4)
        state.commitScrub()

        assertEquals(4, state.currentStepIdx)
        assertNull("the preview is consumed by the commit", state.scrubTarget)
        assertEquals(4, state.displayStepIdx)
    }

    @Test
    fun commitScrub_withNoPreview_isANoOp() {
        val state = makeState(5)
        state.currentStepIdx = 2
        state.commitScrub()
        assertEquals(2, state.currentStepIdx)
    }

    @Test
    fun cancelScrub_dropsThePreviewWithoutMovingThePlayhead() {
        val state = makeState(5)
        state.currentStepIdx = 2
        state.previewScrub(4)
        state.cancelScrub()

        assertNull(state.scrubTarget)
        assertEquals(2, state.currentStepIdx)
        assertEquals(2, state.displayStepIdx)
    }

    @Test
    fun reset_clearsAnInFlightScrubPreview() {
        val state = makeState(5)
        state.currentStepIdx = 2
        state.previewScrub(4)
        state.reset()

        assertNull(state.scrubTarget)
        assertEquals(0, state.currentStepIdx)
        assertEquals(0, state.displayStepIdx)
    }

    @Test
    fun stepForward_clearsAnInFlightScrubPreview() {
        val state = makeState(5)
        state.previewScrub(4)
        state.stepForward()

        assertNull(state.scrubTarget)
        assertEquals(1, state.currentStepIdx)
    }

    @Test
    fun stepProgress_runsZeroToOneAcrossTheTimeline() {
        val state = makeState(5)
        assertEquals(0f, state.stepProgress, 0.0001f)

        state.currentStepIdx = 4
        assertEquals(1f, state.stepProgress, 0.0001f)

        state.currentStepIdx = 2
        assertEquals(0.5f, state.stepProgress, 0.0001f)
    }

    @Test
    fun stepProgress_isSafeWithASingleStep() {
        val state = makeState(1)
        assertEquals(0f, state.stepProgress, 0.0001f)
    }

    @Test
    fun stepProgress_followsTheScrubPreview() {
        val state = makeState(5)
        state.previewScrub(4)
        assertEquals(1f, state.stepProgress, 0.0001f)
    }
}
