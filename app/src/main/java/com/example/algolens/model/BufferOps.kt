package com.example.algolens.model

/**
 * User-facing operations for the Stack customize sheet. The step
 * generator walks a `List<BufferOp>` and emits a [com.example.algolens.ui.visualizer.VisualizerStep]
 * for each event (PUSH adds a [com.example.algolens.ui.visualizer.BufferItem],
 * POP removes the top, PEEK reads the top without mutating).
 */
sealed class BufferOp {
    /** Push `value` onto the top of the stack. */
    data class Push(val value: Int) : BufferOp()

    /** Pop the top of the stack. No-op if the stack is empty. */
    data object Pop : BufferOp()

    /** Peek the top of the stack. No mutation. */
    data object Peek : BufferOp()
}

/**
 * User-facing operations for the Queue customize sheet. The step
 * generator walks a `List<QueueOp>` and emits a step for each event
 * (ENQUEUE appends to the rear, DEQUEUE removes from the front).
 */
sealed class QueueOp {
    /** Append `value` to the rear of the queue. */
    data class Enqueue(val value: Int) : QueueOp()

    /** Remove the front of the queue. No-op if the queue is empty. */
    data object Dequeue : QueueOp()
}
