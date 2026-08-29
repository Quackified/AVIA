package com.example.algolens.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple

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
    DFS("Depth-First Search (DFS)", VisualizerFamily.GRAPH_2D, InputKind.NONE, "Easy", "O(V+E)", "O(V)", AccentGreen, "Graph Traversal");

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
    /** Whether the algorithm is a stack (true) or queue (false). Derived for [VisualizerFamily.BUFFER]. */
    val isStack: Boolean = false,
    /** Whether the builder overlay (tap-to-add-node) is exposed. */
    val builderEnabled: Boolean = false,
)
