package com.example.algolens.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.visualizer.VisualizerOverlay

/**
 * Single source of truth for every algorithm that ships in the app.
 *
 * Adding a new algorithm = adding one entry here + one entry in
 * [com.example.algolens.data.AlgorithmRegistry]. No other site in the
 * codebase should branch on raw algorithm name strings.
 */
enum class AlgorithmId(
    val displayName: String,
    val family: VisualizerFamily,
    val inputKind: InputKind,
    val difficulty: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val accent: Color,
    /** Free-form category label preserved for the Dashboard / Profile screens. */
    val categoryLabel: String,
) {
    BUBBLE_SORT("Bubble Sort", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Easy", "O(n²)", "O(1)", PrimaryCyan, "Sorting"),
    SELECTION_SORT("Selection Sort", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Easy", "O(n²)", "O(1)", PrimaryCyan, "Sorting"),
    INSERTION_SORT("Insertion Sort", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Easy", "O(n²)", "O(1)", PrimaryCyan, "Sorting"),
    MERGE_SORT("Merge Sort", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Medium", "O(n log n)", "O(n)", PrimaryCyan, "Sorting"),
    QUICK_SORT("Quick Sort", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Medium", "O(n log n)", "O(log n)", PrimaryCyan, "Sorting"),
    LINEAR_SEARCH("Linear Search", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Easy", "O(n)", "O(1)", SecondaryPurple, "Searching"),
    BINARY_SEARCH("Binary Search", VisualizerFamily.LINEAR_1D, InputKind.ARRAY, "Easy", "O(log n)", "O(1)", SecondaryPurple, "Searching"),
    STACK("Stack", VisualizerFamily.BUFFER, InputKind.NONE, "Easy", "O(1) push/pop", "O(n)", AccentOrange, "Data Structures"),
    QUEUE("Queue", VisualizerFamily.BUFFER, InputKind.NONE, "Easy", "O(1) enq/deq", "O(n)", AccentOrange, "Data Structures"),
    BINARY_SEARCH_TREE("Binary Search Tree", VisualizerFamily.GRAPH_2D, InputKind.NONE, "Medium", "O(log n) avg", "O(n)", AccentOrange, "Data Structures"),
    HEAP("Heap", VisualizerFamily.GRAPH_2D, InputKind.ARRAY, "Medium", "O(log n) ins", "O(n)", AccentOrange, "Data Structures"),
    BFS("Breadth-First Search (BFS)", VisualizerFamily.GRAPH_2D, InputKind.NONE, "Easy", "O(V+E)", "O(V)", AccentGreen, "Graph Traversal"),
    DFS("Depth-First Search (DFS)", VisualizerFamily.GRAPH_2D, InputKind.NONE, "Easy", "O(V+E)", "O(V)", AccentGreen, "Graph Traversal"),
    DIJKSTRA("Dijkstra's Shortest Path", VisualizerFamily.GRAPH_2D, InputKind.NONE, "Medium", "O((V+E) log V)", "O(V)", AccentGreen, "Shortest Path");

    companion object {
        /** Lookup by legacy free-form name (case-insensitive, trimmed). Use only for migration shims. */
        fun fromDisplayName(name: String): AlgorithmId? =
            values().firstOrNull { it.displayName.equals(name.trim(), ignoreCase = true) }
    }
}

/**
 * Which visualizer family owns the rendering of an algorithm. The screen
 * coordinator switches on this — never on the algorithm name.
 */
enum class VisualizerFamily {
    /** 1D linear cells / bars (sorting + searching). */
    LINEAR_1D,
    /** Tree / graph 2D canvas. */
    GRAPH_2D,
    /** Stack / queue / deque buffer container. */
    BUFFER,
}

/** What kind of user input the algorithm accepts (controls the Customize sheet). */
enum class InputKind {
    /** 1D array of ints. */
    ARRAY,
    /** Graph nodes / edges — the customize sheet is disabled. */
    NONE,
}

/**
 * Typed telemetry strip mode for 2D Graph / Tree visualizers so renderers
 * never inspect raw algorithm names or substrings.
 */
enum class GraphTelemetryMode(val frontierLabel: String) {
    NONE("QUEUE"),
    HEAP_ARRAY("ARRAY"),
    BST_TARGET("TARGET"),
    BFS_QUEUE("QUEUE"),
    DFS_STACK("STACK"),
    DIJKSTRA_PQ("PRIORITY QUEUE"),
}

/**
 * Typed declarative metadata shared by the dashboard, the visualizer host,
 * the theory sheet, the code registry, and the step generator.
 *
 * This is the *only* place that owns the canonical "render mode" for an
 * algorithm — the legacy `algorithm.name.equals("Stack", …)` switches
 * scattered across the screen are now derived from [family].
 */
@Immutable
data class AlgorithmSpec(
    val id: AlgorithmId,
    val defaultInput: List<Int>,
    /** How the Customize Input sheet should treat the algorithm. */
    val supportsCustomInput: Boolean,
    /** Whether the algorithm accepts an explicit search target in the input sheet. */
    val acceptsSearchTarget: Boolean = false,
    /** Whether the algorithm is a stack (true) or queue (false). Derived for [VisualizerFamily.BUFFER]. */
    val isStack: Boolean = false,
    /** Specific graph/tree capabilities for interactive editor and tools. */
    val capabilityProfile: GraphCapabilityProfile? = null,
    /** Whether the builder overlay (interactive tools) is exposed. */
    val builderEnabled: Boolean = capabilityProfile?.allowedTools?.isNotEmpty() == true,
    /** Typed telemetry strip mode for [VisualizerFamily.GRAPH_2D]. */
    val graphTelemetryMode: GraphTelemetryMode = GraphTelemetryMode.NONE,
    /**
     * Per-algorithm visuals that float above the family renderer
     * (recursion tree for Merge Sort, weight badges for graph
     * traversals, etc.). Default empty so every existing
     * `AlgorithmSpec(...)` call site keeps compiling.
     *
     * The interface lives in [com.example.algolens.ui.visualizer]
     * because the step type it reads is defined there; this is a
     * strictly additive `model/ -> ui/visualizer/` import, no cycles.
     */
    val overlays: List<VisualizerOverlay> = emptyList(),
    /**
     * Per-algorithm structural regions the host renders around
     * the family renderer (recursion bands, phase strip, merge
     * buffer row for Merge Sort). Each auxiliary reserves a
     * slice of the canvas in the host's `Column`; the spec
     * controls slot, weight, and order.
     */
    val auxiliaryComponents: List<RegionAuxiliary> = emptyList(),
    /**
     * Whether out-of-range cells / bars should be visually
     * dimmed. Set true for divide-and-conquer algorithms with
     * an [com.example.algolens.ui.visualizer.VisualizerStep.activeRange]
     * (Binary Search, Quick Sort, Merge Sort during divide).
     * Applied identically by [com.example.algolens.ui.visualizer.CellArrayVisualizer]
     * and [com.example.algolens.ui.visualizer.BarVisualizer] so
     * the user sees the same "narrowing window" effect in either
     * mode.
     */
    val dimOutOfRangeCells: Boolean = false,
)
