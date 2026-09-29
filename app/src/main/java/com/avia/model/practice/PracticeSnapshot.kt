package com.avia.model.practice

import com.avia.ui.visualizer.ElementState

/**
 * Node in a practice graph or tree snapshot.
 */
data class PracticeGraphNode(
    val id: String,
    val label: String,
    val state: ElementState = ElementState.IDLE,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.5f
)

/**
 * Directed/undirected edge in a practice graph snapshot.
 */
data class PracticeGraphEdge(
    val from: String,
    val to: String,
    val isHighlighted: Boolean = false,
    val weight: Int? = null
)

/**
 * Represents the frozen visual state of an algorithm's data structure
 * accompanying a practice question.
 */
sealed interface PracticeSnapshot {

    /**
     * 1D array snapshot used for Sorting and Searching algorithms.
     */
    data class LinearSnapshot(
        val array: List<Int>,
        val elementStates: Map<Int, ElementState> = emptyMap(),
        val topPointers: Map<String, Int> = emptyMap(),
        val bottomPointers: Map<String, Int> = emptyMap(),
        val activeRange: IntRange? = null,
        val maxValue: Int = 100
    ) : PracticeSnapshot

    /**
     * Buffer snapshot used for Stack (LIFO) and Queue (FIFO) data structures.
     */
    data class BufferSnapshot(
        val items: List<Int>,
        val isStack: Boolean = true,
        val highlightIndex: Int? = null,
        val operationLabel: String = ""
    ) : PracticeSnapshot

    /**
     * 2D node/edge snapshot used for Graph Traversals (BFS, DFS) and Trees (BST, Heap).
     */
    data class GraphSnapshot(
        val nodes: List<PracticeGraphNode>,
        val edges: List<PracticeGraphEdge> = emptyList(),
        val activeNodeId: String? = null
    ) : PracticeSnapshot
}
