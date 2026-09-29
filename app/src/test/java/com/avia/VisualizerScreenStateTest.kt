package com.avia

import com.avia.model.Algorithm
import com.avia.model.AlgorithmId
import com.avia.ui.visualizer.DeckPage
import com.avia.ui.visualizer.VisualizerStep
import com.avia.ui.visualizer.VisualizerScreenState
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
    fun currentStep_emptyStream_showsNeutralStandby_notProcessing() {
        // The pre-generation window must not flash a "PROCESSING" phase pill —
        // nothing is being processed yet, and the fabricated default step made
        // the header claim work was happening on an empty canvas.
        val state = VisualizerScreenState(makeAlgorithm())
        assertTrue(state.steps.isEmpty())
        assertEquals("STANDBY", state.currentStep.phaseLabel)
    }

    @Test
    fun togglePlay_atLastStep_restartsFromBeginning() {
        // Pressing play while parked on the final step used to start a tick
        // the playback loop immediately cancelled — a dead-feeling button.
        val state = makeState(3)
        state.currentStepIdx = 2
        state.togglePlay()
        assertTrue(state.isPlaying)
        assertEquals(0, state.currentStepIdx)
    }

    @Test
    fun togglePlay_midStream_keepsPlayhead() {
        val state = makeState(5)
        state.currentStepIdx = 2
        state.togglePlay()
        assertTrue(state.isPlaying)
        assertEquals(2, state.currentStepIdx)
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
        assertFalse("the deck starts collapsed", state.deckExpanded)
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

    @Test
    fun stackAndQueue_initializeWithDefaultOperationsAndPreRemovalHighlights() {
        val state = makeState(1)
        assertTrue(state.bufferOps.isNotEmpty())
        assertTrue(state.queueOps.isNotEmpty())

        val stackSteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.STACK),
            bufferOps = state.bufferOps
        )
        assertTrue("Stack should have more than 2 steps", stackSteps.size > 2)
        assertTrue(
            "Stack Pop should emit a pre-removal SWAPPING step",
            stackSteps.any { step -> step.buffer.any { it.state == com.avia.ui.visualizer.ElementState.SWAPPING } }
        )
        assertTrue("Stack steps should populate variables", stackSteps.all { it.variables.isNotEmpty() })

        val queueSteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.QUEUE),
            queueOps = state.queueOps
        )
        assertTrue("Queue should have more than 2 steps", queueSteps.size > 2)
        assertTrue(
            "Queue Dequeue should emit a pre-removal SWAPPING step",
            queueSteps.any { step -> step.buffer.any { it.state == com.avia.ui.visualizer.ElementState.SWAPPING } }
        )
    }

    @Test
    fun bstHeapBfsDfs_populateTelemetryVariablesAndRespectCustomInput() {
        val bstValues = listOf(50, 30, 70, 20, 40)
        val bstSteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.BINARY_SEARCH_TREE),
            bstValues = bstValues,
            bstSearchKey = 40
        )
        // BST is now pre-built: no INSERTING steps — tree is fully visible from step 0.
        val insertingSteps = bstSteps.filter { it.phaseLabel == "INSERTING" }
        assertEquals("BST pre-built: no INSERTING steps expected", 0, insertingSteps.size)

        // Step 0 must be INITIALIZING with ALL input nodes already present.
        val step0 = bstSteps.first()
        assertEquals("Step 0 phaseLabel should be INITIALIZING", "INITIALIZING", step0.phaseLabel)
        assertEquals("Step 0 must contain all ${bstValues.size} nodes", bstValues.size, step0.nodes.size)

        // Exactly one ACTIVE node (root) in step 0.
        val activeInStep0 = step0.nodes.filter { it.state == com.avia.ui.visualizer.ElementState.ACTIVE }
        assertEquals("Step 0 must have exactly 1 active node (root)", 1, activeInStep0.size)
        assertEquals("Root should be 50", "50", activeInStep0.first().label)

        // Search traversal: must find target key 40.
        assertTrue("BST must reach FOUND step", bstSteps.any { it.phaseLabel == "FOUND" })
        assertTrue("BST should populate visitedNodeIds", bstSteps.last().visitedNodeIds.isNotEmpty())
        assertTrue("BST should populate variables", bstSteps.last().variables.isNotEmpty())

        // Default Heap input should be the 7-element DEFAULT_HEAP_INPUT
        val defaultHeapSteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.HEAP)
        )
        assertEquals("Default Heap should have 7 nodes", 7, defaultHeapSteps.first().nodes.size)

        // Explicit DEFAULT_INPUT [3, 8, 9, 2, 6, 1, 5, 4, 7] must NOT be coerced to 7 elements
        val explicitDefaultArraySteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.HEAP),
            inputArray = com.avia.data.AlgorithmStepRepository.DEFAULT_INPUT
        )
        assertEquals("Explicit 9-element array must preserve all 9 nodes", 9, explicitDefaultArraySteps.first().nodes.size)

        // Check Heap sizes 1, 2, and 15 for complete heap establishment and bounded pointers
        for (size in listOf(1, 2, 15)) {
            val input = (1..size).map { it * 10 }
            val steps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
                com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.HEAP),
                inputArray = input
            )
            val finalStep = steps.last()
            assertEquals("Final heapSize for n=$size", size.toString(), finalStep.variables["heapSize"])
            assertEquals(
                "Root node at index 0 must be FOUND (heap maximum established)",
                com.avia.ui.visualizer.ElementState.FOUND,
                finalStep.nodes[0].state
            )
            for (step in steps) {
                val bound = step.variables["heapSize"]?.toIntOrNull() ?: size
                step.bottomPointers.forEach { (label, idx) ->
                    if (label == "L" || label == "R") {
                        assertTrue("Child pointer $label at $idx must be < heapBound $bound", idx < bound)
                    }
                }
            }
        }
    }

    @Test
    fun bfsAndDfsSteps_separateNodeIdFromFormattedBufferValueAndClearCompletedFrontier() {
        val bfsSteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.BFS)
        )
        val validNodeIds = bfsSteps.first().nodes.map { it.id }.toSet()
        val nonEmptyBfsBuffers = bfsSteps.filter { it.buffer.isNotEmpty() }
        assertTrue("BFS should have steps with non-empty queue frontier", nonEmptyBfsBuffers.isNotEmpty())
        for (step in nonEmptyBfsBuffers) {
            for (item in step.buffer) {
                assertTrue("BFS BufferItem.nodeId (${item.nodeId}) must be a valid node ID", item.nodeId in validNodeIds)
                assertTrue("BFS BufferItem.value (${item.value}) should include distance", item.value.contains("(d="))
            }
        }
        assertTrue("Final BFS step must have an empty frontier buffer", bfsSteps.last().buffer.isEmpty())

        val dfsSteps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.DFS)
        )
        val nonEmptyDfsBuffers = dfsSteps.filter { it.buffer.isNotEmpty() }
        assertTrue("DFS should have steps with non-empty call stack frontier", nonEmptyDfsBuffers.isNotEmpty())
        for (step in nonEmptyDfsBuffers) {
            for (item in step.buffer) {
                assertTrue("DFS BufferItem.nodeId (${item.nodeId}) must be a valid node ID", item.nodeId in validNodeIds)
                assertTrue("DFS BufferItem.value (${item.value}) should include dfs(...) frame", item.value.startsWith("dfs("))
            }
        }
        assertTrue("Final DFS step must have an empty frontier buffer", dfsSteps.last().buffer.isEmpty())
    }

    @Test
    fun liveStackAndQueueActions_validateAgainstTailStateRegardlessOfScrubbedStep() {
        val stackState = VisualizerScreenState(Algorithm(id = AlgorithmId.STACK))
        stackState.scrubTo(0) // Scrub to step 0 where currentStep.buffer is smaller
        val initialTailSize = stackState.tailBufferSize(isStack = true)
        assertEquals("Default stack ops leave 3 items at tail", 3, initialTailSize)
        assertTrue(stackState.canAppendToBuffer(isStack = true))
        assertTrue(stackState.canRemoveFromBuffer(isStack = true))

        // Pop 3 times until tail is empty, even while scrubbed earlier
        repeat(3) {
            stackState.scrubTo(0)
            stackState.appendLiveStackOp(com.avia.model.BufferOp.Pop)
        }
        assertEquals(0, stackState.tailBufferSize(isStack = true))
        assertFalse("Cannot pop when tail stack is empty", stackState.canRemoveFromBuffer(isStack = true))
        val opsCountBeforeRejectedPop = stackState.bufferOps.size
        stackState.appendLiveStackOp(com.avia.model.BufferOp.Pop)
        assertEquals("Rejected pop should not append an op", opsCountBeforeRejectedPop, stackState.bufferOps.size)

        // Push 8 times to reach capacity
        repeat(8) { idx ->
            stackState.appendLiveStackOp(com.avia.model.BufferOp.Push(idx + 1))
        }
        assertEquals(8, stackState.tailBufferSize(isStack = true))
        assertFalse("Cannot push when tail stack reaches capacity 8", stackState.canAppendToBuffer(isStack = true))
    }

    @Test
    fun traversalStartIds_reflectCustomGraphEditsAndFallbackWhenStartDeleted() {
        val bfsState = VisualizerScreenState(Algorithm(id = AlgorithmId.BFS))
        assertEquals(listOf("A", "B", "C", "D", "E", "F"), bfsState.effectiveTraversalNodeIds)

        val baseGraph = com.avia.data.AlgorithmStepRepository.canonicalWeightedGraph()
        val withNodeG = Pair(
            baseGraph.first + com.avia.ui.visualizer.GraphNodeState("G", "G", 85f, 85f),
            baseGraph.second
        )
        bfsState.customGraph = withNodeG
        assertTrue("effectiveTraversalNodeIds should include added node G", "G" in bfsState.effectiveTraversalNodeIds)

        bfsState.graphConfig = com.avia.model.GraphCustomization.ForTraversal(startNodeId = "G")
        assertEquals("G", bfsState.effectiveTraversalStartNodeId)

        // Now remove G from customGraph; effectiveTraversalStartNodeId must fall back to "A"
        bfsState.customGraph = baseGraph
        assertFalse("G" in bfsState.effectiveTraversalNodeIds)
        assertEquals("A", bfsState.effectiveTraversalStartNodeId)
    }

    @Test
    fun bstLayoutAndGraphCanvasGeometry_preventCircleOverlapOn15NodeSkewedAndDuplicateTrees() {
        val skewedInput = (1..15).toList()
        val duplicateInput = List(15) { 42 }

        for (input in listOf(skewedInput, duplicateInput)) {
            val steps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
                com.avia.model.Algorithm(id = com.avia.model.AlgorithmId.BINARY_SEARCH_TREE),
                bstValues = input,
                bstSearchKey = input.last()
            )
            val finalNodes = steps.last().nodes
            assertEquals(15, finalNodes.size)
            val geom = com.avia.ui.visualizer.GraphCanvasGeometry.from(
                nodes = finalNodes,
                drawSize = androidx.compose.ui.geometry.Size(1000f, 680f)
            )
            for (i in finalNodes.indices) {
                for (j in i + 1 until finalNodes.size) {
                    val c1 = geom.toCanvasOffset(finalNodes[i].x, finalNodes[i].y)
                    val c2 = geom.toCanvasOffset(finalNodes[j].x, finalNodes[j].y)
                    val dist = kotlin.math.hypot((c2.x - c1.x).toDouble(), (c2.y - c1.y).toDouble()).toFloat()
                    assertTrue(
                        "Nodes ${finalNodes[i].id} and ${finalNodes[j].id} overlap: dist=$dist < 2*radius=${2f * geom.nodeRadius}",
                        dist >= 2f * geom.nodeRadius - 0.5f
                    )
                }
            }
        }
    }

    @Test
    fun appSettings_toggleBookmark_addsAndRemovesAlgorithmId() {
        com.avia.data.AppSettings.clearAllSavedData()
        assertFalse(com.avia.data.AppSettings.isBookmarked("MERGE_SORT"))
        com.avia.data.AppSettings.toggleBookmark("MERGE_SORT")
        assertTrue(com.avia.data.AppSettings.isBookmarked("MERGE_SORT"))
        com.avia.data.AppSettings.toggleBookmark("MERGE_SORT")
        assertFalse(com.avia.data.AppSettings.isBookmarked("MERGE_SORT"))
    }

    @Test
    fun liveBufferOps_branchFromVisibleStep_andDisablePopAtBoot() {
        val stackState = VisualizerScreenState(Algorithm(id = AlgorithmId.STACK))
        stackState.steps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.STACK)
        )
        // At boot (step 0, INITIALIZING), buffer is empty
        stackState.scrubTo(0)
        assertEquals("0", stackState.currentStep.variables["opCount"])
        assertEquals(0, stackState.currentBufferCount(isStack = true))
        assertFalse("Cannot pop or peek on empty chamber at boot", stackState.canRemoveFromBuffer(isStack = true))
        assertTrue("Can push into empty chamber at boot", stackState.canAppendToBuffer(isStack = true))

        // Pushing from step 0 branches from empty state (does not append to default 6 ops)
        stackState.appendLiveStackOp(com.avia.model.BufferOp.Push(42))
        assertEquals(1, stackState.bufferOps.size)
        assertEquals(com.avia.model.BufferOp.Push(42), stackState.bufferOps.first())

        // Same for Circular Queue
        val cqState = VisualizerScreenState(Algorithm(id = AlgorithmId.QUEUE))
        cqState.selectQueueVariant(com.avia.model.QueueVariant.CIRCULAR_RING)
        cqState.steps = com.avia.data.AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.QUEUE),
            queueVariant = com.avia.model.QueueVariant.CIRCULAR_RING
        )
        cqState.scrubTo(0)
        assertEquals("0", cqState.currentStep.variables["opCount"])
        assertEquals(0, cqState.currentBufferCount(isStack = false))
        assertFalse("Cannot dequeue or peek at boot", cqState.canRemoveFromBuffer(isStack = false))
        assertTrue("Can enqueue into empty circular queue at boot", cqState.canAppendToBuffer(isStack = false))

        // Enqueue from step 0 branches from empty state (does not append to default 12 ops)
        cqState.appendLiveCircularQueueOp(com.avia.model.QueueOp.Enqueue(99))
        assertEquals(1, cqState.circularQueueOps.size)
        assertEquals(com.avia.model.QueueOp.Enqueue(99), cqState.circularQueueOps.first())
    }
}
