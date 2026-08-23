package com.example.algolens.ui.visualizer

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
data class Pointer(
    val label: String,
    val index: Int,
    val isTop: Boolean = false,
    val colorHex: String? = null
)

/**
 * Node for Graph & Tree visualizers.
 */
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
data class GraphEdgeState(
    val from: String,
    val to: String,
    val weight: Int? = null,
    val isHighlighted: Boolean = false,
    val isDirected: Boolean = false
)

/**
 * Item in a Buffer (Stack/Queue).
 */
data class BufferItem(
    val id: String,
    val value: String,
    val state: ElementState = ElementState.IDLE
)

/**
 * Represents a single state snapshot / step during algorithm execution.
 */
data class VisualizerStep(
    val stepIndex: Int = 0,
    val description: String = "",
    val comparisonExpr: String? = null, // e.g. "COMPARE: 9 <= 7?"
    val renderMode: VisualizerRenderMode = VisualizerRenderMode.CELLS,
    
    // Array Data
    val array: List<Int> = emptyList(),
    val elementStates: Map<Int, ElementState> = emptyMap(),
    val topPointers: Map<String, Int> = emptyMap(),    // e.g. {"L": 0, "pivot": 8}
    val bottomPointers: Map<String, Int> = emptyMap(), // e.g. {"i": 0, "j": 2}
    
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
    val variables: Map<String, String> = emptyMap() // live inspector e.g. {"i": "0", "j": "2", "pivot": "7"}
)
