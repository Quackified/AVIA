package com.example.algolens

import com.example.algolens.data.AlgorithmCodeRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.TraceLanguage
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.BstMode
import com.example.algolens.model.QueueOp
import com.example.algolens.model.QueueVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for BST Tree Traversals (In-Order, Pre-Order, Post-Order)
 * and the Circular Queue (Ring Buffer) with modular arithmetic wrap-around.
 */
class TreeTraversalAndCircularQueueTest {

    // ─────────────────────────────────────────────────────────────
    // 1. Binary Search Tree Traversals
    // ─────────────────────────────────────────────────────────────

    @Test
    fun testBstInOrderTraversalEmitsAscendingSortedSequence() {
        val bstValues = listOf(50, 30, 70, 20, 40, 60, 80)
        val steps = AlgorithmStepRepository.generateBSTSteps(
            values = bstValues,
            mode = BstMode.IN_ORDER
        )

        assertTrue("Expected non-empty traversal steps", steps.isNotEmpty())
        assertEquals("First step must be INITIALIZING", "INITIALIZING", steps.first().phaseLabel)
        assertEquals("Last step must be DONE", "DONE", steps.last().phaseLabel)

        // Fundamental BST Invariant: In-Order traversal MUST produce an ascending sorted sequence
        val finalBuffer = steps.last().buffer.map { it.value }
        val expectedSorted = listOf("20", "30", "40", "50", "60", "70", "80")
        assertEquals(
            "BST In-Order traversal must produce strictly ascending sorted elements",
            expectedSorted,
            finalBuffer
        )

        // Verify visit node steps
        val visitSteps = steps.filter { it.phaseLabel == "VISIT_NODE" }
        assertEquals(7, visitSteps.size)
        val emittedSequence = visitSteps.mapNotNull { it.variables["current"] }
        assertEquals(expectedSorted, emittedSequence)
    }

    @Test
    fun testBstPreOrderTraversalVisitsRootBeforeChildren() {
        val bstValues = listOf(50, 30, 70, 20, 40, 60, 80)
        val steps = AlgorithmStepRepository.generateBSTSteps(
            values = bstValues,
            mode = BstMode.PRE_ORDER
        )

        assertTrue(steps.isNotEmpty())
        val finalBuffer = steps.last().buffer.map { it.value }
        // Pre-Order: Root (50), Left Subtree (30, 20, 40), Right Subtree (70, 60, 80)
        val expectedPreOrder = listOf("50", "30", "20", "40", "70", "60", "80")
        assertEquals(
            "BST Pre-Order traversal must visit root before children",
            expectedPreOrder,
            finalBuffer
        )
    }

    @Test
    fun testBstPostOrderTraversalVisitsChildrenBeforeRoot() {
        val bstValues = listOf(50, 30, 70, 20, 40, 60, 80)
        val steps = AlgorithmStepRepository.generateBSTSteps(
            values = bstValues,
            mode = BstMode.POST_ORDER
        )

        assertTrue(steps.isNotEmpty())
        val finalBuffer = steps.last().buffer.map { it.value }
        // Post-Order: Left Subtree (20, 40, 30), Right Subtree (60, 80, 70), Root (50)
        val expectedPostOrder = listOf("20", "40", "30", "60", "80", "70", "50")
        assertEquals(
            "BST Post-Order traversal must visit children before root",
            expectedPostOrder,
            finalBuffer
        )
    }

    @Test
    fun testBstModeDispatcherIntegration() {
        val algo = Algorithm(id = AlgorithmId.BINARY_SEARCH_TREE)
        val inOrderSteps = AlgorithmStepRepository.generateStepsForAlgorithm(
            algorithm = algo,
            bstMode = BstMode.IN_ORDER
        )
        val lastStep = inOrderSteps.last()
        assertEquals("DONE", lastStep.phaseLabel)
        assertEquals("In-Order", lastStep.variables["mode"])
        assertEquals("BstMode.IN_ORDER", "IN_ORDER", lastStep.variables["bstMode"])
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Circular Queue (Radial Ring Buffer)
    // ─────────────────────────────────────────────────────────────

    @Test
    fun testCircularQueueWrapAroundModuloArithmetic() {
        val ops = AlgorithmStepRepository.defaultCircularQueueOps()
        val steps = AlgorithmStepRepository.generateCircularQueueSteps(ops, capacity = 8)

        assertTrue(steps.isNotEmpty())
        assertEquals("INITIALIZING", steps.first().phaseLabel)
        assertEquals("DONE", steps.last().phaseLabel)

        // Find the step where 90 is enqueued
        val wrapStep = steps.firstOrNull { it.description.contains("enqueue(90)") }
        assertNotNull("Should contain step enqueuing 90", wrapStep)

        // 90 should have wrapped around to slot 0 because previous dequeues freed slots 0 and 1
        assertEquals("Rear pointer must wrap to slot 0", 0, wrapStep!!.topPointers["R"])
        assertEquals("Slot 0 value must be 90", "90", wrapStep.buffer[0].value)

        // Check peek step
        val peekStep = steps.firstOrNull { it.phaseLabel == "PEEK" }
        assertNotNull("Should contain PEEK step", peekStep)
        assertTrue(peekStep!!.comparisonExpr?.contains("PEEK()") == true)
    }

    @Test
    fun testCircularQueueOverflowAndUnderflow() {
        // Enqueue 8 items to fill, then try 9th -> OVERFLOW
        val ops = listOf(
            QueueOp.Enqueue(1),
            QueueOp.Enqueue(2),
            QueueOp.Enqueue(3),
            QueueOp.Enqueue(4),
            QueueOp.Enqueue(5),
            QueueOp.Enqueue(6),
            QueueOp.Enqueue(7),
            QueueOp.Enqueue(8),
            QueueOp.Enqueue(999), // Overflow!
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue,
            QueueOp.Dequeue // Underflow!
        )
        val steps = AlgorithmStepRepository.generateCircularQueueSteps(ops, capacity = 8)

        val overflowStep = steps.firstOrNull { it.phaseLabel == "OVERFLOW" }
        assertNotNull("Must detect queue overflow when full", overflowStep)
        assertTrue(overflowStep!!.description.contains("Overflow"))

        val underflowStep = steps.firstOrNull { it.phaseLabel == "UNDERFLOW" }
        assertNotNull("Must detect queue underflow when empty", underflowStep)
        assertTrue(underflowStep!!.description.contains("Underflow"))
    }

    @Test
    fun testQueueVariantDispatcherIntegration() {
        val algo = Algorithm(id = AlgorithmId.QUEUE)
        val circularSteps = AlgorithmStepRepository.generateStepsForAlgorithm(
            algorithm = algo,
            queueVariant = QueueVariant.CIRCULAR_RING
        )
        val firstStep = circularSteps.first()
        assertEquals("CIRCULAR_RING", firstStep.variables["queueVariant"])
        assertEquals(8, firstStep.bufferCapacity)
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Multi-Language Code Trace Registry
    // ─────────────────────────────────────────────────────────────

    @Test
    fun testCodeRegistrySupportsTraversalsAndCircularQueue() {
        for (lang in TraceLanguage.values()) {
            val inOrderCode = AlgorithmCodeRegistry.getCode(
                id = AlgorithmId.BINARY_SEARCH_TREE,
                language = lang,
                bstMode = BstMode.IN_ORDER
            )
            assertTrue("In-Order code for $lang must not be empty", inOrderCode.lines.isNotEmpty())
            assertTrue("In-Order line mapping for $lang must not be empty", inOrderCode.lineMapping.isNotEmpty())

            val circularQueueCode = AlgorithmCodeRegistry.getCode(
                id = AlgorithmId.QUEUE,
                language = lang,
                queueVariant = QueueVariant.CIRCULAR_RING
            )
            assertTrue("Circular Queue code for $lang must not be empty", circularQueueCode.lines.isNotEmpty())
            assertTrue("Circular Queue line mapping for $lang must not be empty", circularQueueCode.lineMapping.isNotEmpty())
        }
    }
}
