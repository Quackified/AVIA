package com.example.algolens.ui.visualizer

import androidx.compose.runtime.Immutable

/**
 * Visual highlight and activity state of an algorithm element (array bar/box, graph node, tree node, buffer slot).
 */
enum class ElementState {
    IDLE,
    COMPARING,
    SWAPPING,
    SORTED,
    ACTIVE,
    VISITED,
    FOUND,
    PIVOT,
    TARGET
}

/**
 * Visualizer render modes.
 */
enum class VisualizerRenderMode {
    CELLS,      // Horizontal cell/box array with dual pointers & code trace (screenshot mode)
    BARS,       // Animated vertical bar chart
    GRAPH_TREE, // 2D Graph / Hierarchical Tree Canvas
    BUFFER      // Stack container (LIFO) & Queue conveyor (FIFO)
}

/**
 * Represents a pointer indicator (e.g., L, R, pivot, i, j, low, mid, high, target, front, rear, top).
 */
@Immutable
data class Pointer(
    val label: String,
    val index: Int,
    val isTop: Boolean = false,
    val colorHex: String? = null
)

/**
 * Node for Graph & Tree visualizers.
 */
@Immutable
data class GraphNodeState(
    val id: String,
    val label: String,
    val x: Float, // Normalized (e.g. 0-100 or 0-250)
    val y: Float,
    val state: ElementState = ElementState.IDLE,
    val value: String = label
)

/**
 * Edge for Graph & Tree visualizers.
 */
@Immutable
data class GraphEdgeState(
    val from: String,
    val to: String,
    val weight: Int? = null,
    val isHighlighted: Boolean = false,
    val isDirected: Boolean = false
)

/**
 * Item in a Buffer (Stack/Queue/Frontier).
 * [nodeId] carries the underlying graph/tree node ID separately from the
 * formatted [value] label (e.g. `nodeId = "B"` while `value = "B(d=4)"` or `"dfs(B)"`).
 */
@Immutable
data class BufferItem(
    val id: String,
    val value: String,
    val state: ElementState = ElementState.IDLE,
    val nodeId: String? = null
)

/**
 * Represents a single state snapshot / step during algorithm execution.
 */
@Immutable
data class VisualizerStep(
    val stepIndex: Int = 0,
    val description: String = "",
    val comparisonExpr: String? = null, // e.g. "COMPARE: 9 <= 7?"
    val phaseLabel: String = "PROCESSING", // e.g. "PARTITIONING", "MERGING", "KEY ELEVATED", "SWAPPING"
    val renderMode: VisualizerRenderMode = VisualizerRenderMode.CELLS,
    
    // Array Data
    val array: List<Int> = emptyList(),
    val elementStates: Map<Int, ElementState> = emptyMap(),
    val topPointers: Map<String, Int> = emptyMap(),    // e.g. {"L": 0, "pivot": 8}
    val bottomPointers: Map<String, Int> = emptyMap(), // e.g. {"i": 0, "j": 2}
    
    // Structural / Sorting Visualizer Extensions
    val activeRange: IntRange? = null, // e.g. low..high for Quick Sort / Merge Sort
    val sortedBoundary: Int? = null,   // Left sorted region for Selection Sort / Right sorted tail for Bubble Sort
    val auxiliaryArray: List<Int>? = null, // 2-tier temporary buffer for Merge Sort
    val auxiliaryIndices: Map<String, Int> = emptyMap(), // Pointers in aux buffer (e.g. "k" to 2)
    val recursionDepth: Int = 0, // Recursion tree level for Merge Sort
    val mergeBlocks: List<IntRange> = emptyList(), // Color-coded block partitions for Merge Sort
    /**
     * The recursion path from the root to the current range. For
     * Merge Sort, the step generator walks `sort(l, r, depth)`
     * recursively and emits `[root, ..., current]` so the bands
     * auxiliary can render the tree. Index 0 is the root
     * (always `0 until array.size`); the last element is the
     * currently-active sub-range. Empty for non-recursive
     * algorithms.
     */
    val ancestorRanges: List<IntRange> = emptyList(),
    val floatingElement: Pair<Int, Int>? = null, // (value, originalIndex) elevated above slot during Insertion Sort
    val pivotIndex: Int? = null,
    val minIndex: Int? = null,
    val leftPointer: Int? = null,
    val rightPointer: Int? = null,
    val swappedIndices: Pair<Int, Int>? = null, // Indices pair being swapped
    
    // Graph / Tree Data
    val nodes: List<GraphNodeState> = emptyList(),
    val edges: List<GraphEdgeState> = emptyList(),
    val activeNodeId: String? = null,
    val visitedNodeIds: Set<String> = emptySet(),
    
    // Buffer Data (Stack / Queue)
    val buffer: List<BufferItem> = emptyList(),
    val bufferLabel: String? = null, // e.g. "Stack (Top -> Bottom)"
    val bufferCapacity: Int = 8,
    
    // Code Trace Sync
    val activeCodeLines: List<Int> = emptyList(), // 1-indexed lines in code pane
    val variables: Map<String, String> = emptyMap(), // live inspector e.g. {"i": "0", "j": "2", "pivot": "7"}
    val callStack: List<String> = emptyList() // live call stack frames e.g. ["dfs(A)", "dfs(B)"]
)
