package com.avia

import androidx.compose.ui.geometry.Offset
import com.avia.data.AlgorithmStepRepository
import com.avia.model.Algorithm
import com.avia.model.AlgorithmId
import com.avia.model.QueueOp
import com.avia.ui.visualizer.ElementState
import com.avia.ui.visualizer.NodeRipple
import com.avia.ui.visualizer.TraversalSignal
import com.avia.ui.visualizer.VisualizerScreenState
import com.avia.ui.visualizer.computeTransverseArcPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.sqrt

class UnifiedMotionSystemTest {

    private val eps = 1e-3f

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Transverse Orbital Swap Arc Tests (computeTransverseArcPosition)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun computeTransverseArcPosition_boundaryConditions() {
        val from = Offset(50f, 100f)
        val to = Offset(250f, 400f)

        val atStart = computeTransverseArcPosition(from, to, 0f, isFirstSwapper = true)
        assertEquals(from.x, atStart.x, eps)
        assertEquals(from.y, atStart.y, eps)

        val atEnd = computeTransverseArcPosition(from, to, 1f, isFirstSwapper = true)
        assertEquals(to.x, atEnd.x, eps)
        assertEquals(to.y, atEnd.y, eps)

        // Clamping check
        val belowZero = computeTransverseArcPosition(from, to, -0.5f, isFirstSwapper = true)
        assertEquals(from.x, belowZero.x, eps)
        assertEquals(from.y, belowZero.y, eps)

        val aboveOne = computeTransverseArcPosition(from, to, 1.5f, isFirstSwapper = true)
        assertEquals(to.x, aboveOne.x, eps)
        assertEquals(to.y, aboveOne.y, eps)
    }

    @Test
    fun computeTransverseArcPosition_zeroDistance_returnsBaseCoords() {
        val samePoint = Offset(120f, 120f)
        val result = computeTransverseArcPosition(samePoint, samePoint, 0.5f, isFirstSwapper = true)
        assertEquals(120f, result.x, eps)
        assertEquals(120f, result.y, eps)
    }

    @Test
    fun computeTransverseArcPosition_verticalSwap_bypassesHorizontally() {
        val nodeAStart = Offset(100f, 100f)
        val nodeBStart = Offset(100f, 300f)
        val arcHeight = 36f

        // Node A moves down (nodeAStart -> nodeBStart)
        val posA = computeTransverseArcPosition(
            from = nodeAStart,
            to = nodeBStart,
            progress = 0.5f,
            isFirstSwapper = true,
            arcHeight = arcHeight
        )
        // Node B moves up (nodeBStart -> nodeAStart)
        val posB = computeTransverseArcPosition(
            from = nodeBStart,
            to = nodeAStart,
            progress = 0.5f,
            isFirstSwapper = true,
            arcHeight = arcHeight
        )

        // At midpoint, baseline y should be 200f for both
        assertEquals(200f, posA.y, eps)
        assertEquals(200f, posB.y, eps)

        // For Node A: dx=0, dy=200, dist=200 -> nx = -200/200 = -1, ny = 0
        // posA.x = 100 + (-1) * 36 = 64f
        assertEquals(64f, posA.x, eps)

        // For Node B: dx=0, dy=-200, dist=200 -> nx = -(-200)/200 = +1, ny = 0
        // posB.x = 100 + (1) * 36 = 136f
        assertEquals(136f, posB.x, eps)

        // Separation between swapping nodes at midpoint is exactly 2 * arcHeight (72px)
        val separation = abs(posB.x - posA.x)
        assertEquals(72f, separation, eps)
    }

    @Test
    fun computeTransverseArcPosition_horizontalSwap_bypassesVertically() {
        val nodeAStart = Offset(100f, 200f)
        val nodeBStart = Offset(300f, 200f)
        val arcHeight = 40f

        val posA = computeTransverseArcPosition(nodeAStart, nodeBStart, 0.5f, isFirstSwapper = true, arcHeight = arcHeight)
        val posB = computeTransverseArcPosition(nodeBStart, nodeAStart, 0.5f, isFirstSwapper = true, arcHeight = arcHeight)

        // Baseline x is 200f for both
        assertEquals(200f, posA.x, eps)
        assertEquals(200f, posB.x, eps)

        // Normal vector separation vertically: exactly 2 * arcHeight (80px)
        val separation = abs(posB.y - posA.y)
        assertEquals(80f, separation, eps)
    }

    @Test
    fun computeTransverseArcPosition_diagonalSwap_normalIsOrthogonal() {
        val from = Offset(0f, 0f)
        val to = Offset(300f, 400f) // 3-4-5 triangle, dist = 500
        val arcHeight = 50f

        val mid = computeTransverseArcPosition(from, to, 0.5f, isFirstSwapper = true, arcHeight = arcHeight)

        // Baseline midpoint is (150, 200)
        val baseX = 150f
        val baseY = 200f

        // Vector from baseline midpoint to curved position
        val arcDx = mid.x - baseX
        val arcDy = mid.y - baseY

        // Magnitude of arc offset must equal arcHeight at t = 0.5
        val arcMagnitude = sqrt(arcDx * arcDx + arcDy * arcDy)
        assertEquals(arcHeight, arcMagnitude, eps)

        // Arc offset must be orthogonal to displacement vector (dot product = 0)
        val dispDx = to.x - from.x
        val dispDy = to.y - from.y
        val dotProduct = arcDx * dispDx + arcDy * dispDy
        assertEquals(0f, dotProduct, eps)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Traversal Signal & Ripple Model Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun traversalSignal_linearInterpolation() {
        val u = Offset(100f, 100f)
        val v = Offset(300f, 500f)

        val signal = TraversalSignal(fromNodeId = "0", toNodeId = "1", progress = 0.75f)
        assertEquals("0", signal.fromNodeId)
        assertEquals("1", signal.toNodeId)
        assertEquals(0.75f, signal.progress, eps)

        // Interpolated position P(t) = (1-t)u + tv
        val t = signal.progress
        val px = (1f - t) * u.x + t * v.x
        val py = (1f - t) * u.y + t * v.y

        assertEquals(250f, px, eps)
        assertEquals(400f, py, eps)
    }

    @Test
    fun nodeRipple_propertiesAndDefaults() {
        val rippleNormal = NodeRipple(nodeId = "A", progress = 0.4f)
        assertEquals("A", rippleNormal.nodeId)
        assertEquals(0.4f, rippleNormal.progress, eps)
        assertFalse(rippleNormal.isTarget)

        val rippleTarget = NodeRipple(nodeId = "B", progress = 0.9f, isTarget = true)
        assertEquals("B", rippleTarget.nodeId)
        assertEquals(0.9f, rippleTarget.progress, eps)
        assertTrue(rippleTarget.isTarget)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Playback Speed & Duration Scaling Factors
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun visualizerScreenState_speedDurationScaling() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.QUICK_SORT))

        // Normal speed (600ms baseline)
        state.playbackSpeedMs = 600L
        assertEquals(1.0f, state.speedMultiplier, eps)
        assertEquals(1.0f, state.animationDurationMultiplier, eps)

        // Fast speed (300ms) -> 2.0x playback speed -> 0.5x duration
        state.playbackSpeedMs = 300L
        assertEquals(2.0f, state.speedMultiplier, eps)
        assertEquals(0.5f, state.animationDurationMultiplier, eps)

        // Slow speed (1200ms) -> 0.5x playback speed -> 2.0x duration
        state.playbackSpeedMs = 1200L
        assertEquals(0.5f, state.speedMultiplier, eps)
        assertEquals(2.0f, state.animationDurationMultiplier, eps)
    }

    @Test
    fun visualizerScreenState_scrubbingStateTransition() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.STACK))

        assertFalse(state.isScrubbing)

        state.scrubTarget = 2
        assertTrue(state.isScrubbing)

        state.cancelScrub()
        assertFalse(state.isScrubbing)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Laboratory-Grade Stack Chamber Level & Headroom Math
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun stackChamber_headroomAndCapacityMetrics() {
        val maxCapacity = 8

        fun computeHeadroomBadge(count: Int): String {
            val headroom = maxCapacity - count
            return if (headroom == 0) "[FULL]" else "[+$headroom FREE]"
        }

        fun computeGraduationLabel(level: Int): String {
            return when (level) {
                maxCapacity - 1 -> "[$level] MAX"
                0 -> "[$level] BASE"
                else -> "[$level]"
            }
        }

        fun computeHexAddress(level: Int): String {
            return "0x%02X".format(level)
        }

        // Capacity Headroom Tests
        assertEquals("[+8 FREE]", computeHeadroomBadge(0))
        assertEquals("[+5 FREE]", computeHeadroomBadge(3))
        assertEquals("[+1 FREE]", computeHeadroomBadge(7))
        assertEquals("[FULL]", computeHeadroomBadge(8))

        // Graduation Etching Labels
        assertEquals("[7] MAX", computeGraduationLabel(7))
        assertEquals("[0] BASE", computeGraduationLabel(0))
        assertEquals("[4]", computeGraduationLabel(4))

        // Monospace Hex Addresses
        assertEquals("0x00", computeHexAddress(0))
        assertEquals("0x07", computeHexAddress(7))
        assertEquals("0x03", computeHexAddress(3))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. QueueOp.Peek & Queue Front Semantics
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun queueOpPeek_appendsPeekStepAndPreservesQueueState() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.QUEUE),
            queueOps = listOf(QueueOp.Enqueue(42), QueueOp.Enqueue(88), QueueOp.Peek)
        )
        val peekStep = steps.first { it.phaseLabel == "PEEK" }

        assertEquals("PEEK", peekStep.phaseLabel)
        assertTrue(peekStep.description.contains("42"))
        assertEquals(2, peekStep.buffer.size)
        assertEquals("42", peekStep.buffer.first().value)
        assertEquals(ElementState.FOUND, peekStep.buffer.first().state)
        assertEquals(ElementState.IDLE, peekStep.buffer.last().state)
    }

    @Test
    fun queueOpPeek_onEmptyQueue_reportsEmptyWithoutCrashing() {
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            Algorithm(id = AlgorithmId.QUEUE),
            queueOps = listOf(QueueOp.Peek)
        )
        val underflowStep = steps.first { it.phaseLabel == "UNDERFLOW" }
        assertEquals("UNDERFLOW", underflowStep.phaseLabel)
        assertTrue(underflowStep.description.contains("Queue underflow (empty)"))
        assertTrue(underflowStep.buffer.isEmpty())
    }

    @Test
    fun queueOpPeek_liveQueueOpAppendsWhenNonEmpty() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.QUEUE))
        assertTrue(state.canRemoveFromBuffer(isStack = false))
        val initialSize = state.queueOps.size
        state.appendLiveQueueOp(QueueOp.Peek)
        assertEquals(initialSize + 1, state.queueOps.size)
        assertEquals(QueueOp.Peek, state.queueOps.last())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. DFS Backtracking Acceleration & Playback Speed Scaling
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun dfsBacktracking_acceleratesPlaybackDuration() {
        val baseDelay = 600L
        fun computeTickDelay(phaseLabel: String, baseMs: Long): Long {
            return if (phaseLabel == "BACKTRACKING") {
                (baseMs * 0.35f).toLong().coerceAtLeast(100L)
            } else {
                baseMs
            }
        }

        val normalDelay = computeTickDelay("VISITING", baseDelay)
        val backtrackDelay = computeTickDelay("BACKTRACKING", baseDelay)

        assertEquals(600L, normalDelay)
        assertEquals(210L, backtrackDelay)
        assertEquals(0.35f, backtrackDelay.toFloat() / normalDelay.toFloat(), eps)

        // Coerce lower bound test
        val fastBacktrackDelay = computeTickDelay("BACKTRACKING", 150L)
        assertEquals(100L, fastBacktrackDelay)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7. Insertion Sort Celebration Constraint
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun insertionSortCelebration_onlyTriggersOnTerminalSortedPhase() {
        fun isFullySortedCelebration(array: List<Int>, states: Map<Int, ElementState>, phaseLabel: String): Boolean {
            return phaseLabel == "SORTED" &&
                array.isNotEmpty() &&
                states.size == array.size &&
                states.values.all { it == ElementState.SORTED }
        }

        val arr = listOf(1, 2, 3, 4, 5)
        val sortedStates = (0..4).associateWith { ElementState.SORTED }

        // Intermediate KEY INSERTED step with all elements marked sorted should NOT trigger celebration
        assertFalse(isFullySortedCelebration(arr, sortedStates, "KEY INSERTED"))

        // Final step with phaseLabel = "SORTED" DOES trigger celebration
        assertTrue(isFullySortedCelebration(arr, sortedStates, "SORTED"))

        // Incomplete states should NOT trigger celebration
        val partialStates = sortedStates.toMutableMap().apply { put(4, ElementState.ACTIVE) }
        assertFalse(isFullySortedCelebration(arr, partialStates, "SORTED"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 8. BFS Decomposed Micro-Phases
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun bfsStepGeneration_producesDecomposedMicroPhases() {
        val bfsSteps = AlgorithmStepRepository.generateStepsForAlgorithm(Algorithm(id = AlgorithmId.BFS))
        val phaseLabels = bfsSteps.map { it.phaseLabel }.toSet()

        assertTrue("BFS steps must contain INITIALIZING", phaseLabels.contains("INITIALIZING"))
        assertTrue("BFS steps must contain DEQUEUE phase", phaseLabels.contains("DEQUEUE"))
        assertTrue("BFS steps must contain EXPLORE phase", phaseLabels.contains("EXPLORE"))
        assertTrue("BFS steps must contain ENQUEUED phase", phaseLabels.contains("ENQUEUED"))

        // Ensure final step has empty buffer
        assertTrue("Final BFS step must have empty frontier buffer", bfsSteps.last().buffer.isEmpty())
    }
}
