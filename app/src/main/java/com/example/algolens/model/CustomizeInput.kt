package com.example.algolens.model

/**
 * Family-aware customize-input payload for GRAPH_2D algorithms.
 *
 * The `CustomizeGraphSheet` collects user input and emits one of these
 * variants. The screen-layer dispatcher hands the value to the right
 * step generator:
 *
 *   - [ForHeap]     → `generateHeapSteps(values, sortOrder)`
 *   - [ForBst]      → `generateBSTSteps(values, searchKey)`
 *   - [ForTraversal]→ `generateBFSSteps(startNodeId)` / `generateDFSSteps(startNodeId)`
 */
sealed class GraphCustomization {
    /** Heap: array of leaf values. */
    data class ForHeap(val values: List<Int>) : GraphCustomization()

    /** BST: values to insert (in order) and the search key. */
    data class ForBst(val values: List<Int>, val searchKey: Int) : GraphCustomization()

    /** BFS / DFS: start node id from the existing graph topology. */
    data class ForTraversal(val startNodeId: String) : GraphCustomization()
}

/**
 * Validation result for a manual text input field. Lifted out of
 * [com.example.algolens.ui.visualizer.CustomizeInputSheet] so the new
 * buffer / graph sheets can reuse the same shape.
 */
sealed class InputValidationResult {
    data class Valid(val parsed: List<Int>) : InputValidationResult()
    data class Error(val message: String) : InputValidationResult()
}
