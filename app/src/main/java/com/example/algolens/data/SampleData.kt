package com.example.algolens.data

import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId

object SampleData {
    /**
     * Canonical ordered list of every algorithm the app currently ships.
     * The visualizer, the dashboard, the profile and the test suite all
     * derive their behaviour from this list — adding a new algorithm is
     * a one-line append plus a one-line entry in
     * [com.example.algolens.data.AlgorithmRegistry].
     */
    val algorithms: List<Algorithm> = listOf(
        // ── Sorting (5) ──
        Algorithm(id = AlgorithmId.BUBBLE_SORT),
        Algorithm(id = AlgorithmId.SELECTION_SORT),
        Algorithm(id = AlgorithmId.INSERTION_SORT),
        Algorithm(id = AlgorithmId.MERGE_SORT),
        Algorithm(id = AlgorithmId.QUICK_SORT),

        // ── Searching (2) ──
        Algorithm(id = AlgorithmId.LINEAR_SEARCH),
        Algorithm(id = AlgorithmId.BINARY_SEARCH),

        // ── Data Structures (4) ──
        Algorithm(id = AlgorithmId.STACK),
        Algorithm(id = AlgorithmId.QUEUE),
        Algorithm(id = AlgorithmId.BINARY_SEARCH_TREE),
        Algorithm(id = AlgorithmId.HEAP),

        // ── Graph Traversal & Shortest Path (3) ──
        Algorithm(id = AlgorithmId.BFS),
        Algorithm(id = AlgorithmId.DFS),
        Algorithm(id = AlgorithmId.DIJKSTRA),
    )

    /** Category labels shown as filter chips on the dashboard. Derived from the enum. */
    val categories: List<String> = listOf("All") + AlgorithmId.values()
        .map { it.categoryLabel }
        .toSortedSet()
        .toList()
}

