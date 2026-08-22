package com.example.algolens.data

import com.example.algolens.model.Algorithm

object SampleData {
    val algorithms = listOf(
        // ── Sorting (5) ──
        Algorithm(1, "Bubble Sort", "Sorting", "O(n²)", "O(1)", "Easy", "#22D3EE"),
        Algorithm(2, "Selection Sort", "Sorting", "O(n²)", "O(1)", "Easy", "#22D3EE"),
        Algorithm(3, "Insertion Sort", "Sorting", "O(n²)", "O(1)", "Easy", "#22D3EE"),
        Algorithm(4, "Merge Sort", "Sorting", "O(n log n)", "O(n)", "Medium", "#22D3EE"),
        Algorithm(5, "Quick Sort", "Sorting", "O(n log n)", "O(log n)", "Medium", "#22D3EE"),

        // ── Searching (2) ──
        Algorithm(6, "Linear Search", "Searching", "O(n)", "O(1)", "Easy", "#C084FC"),
        Algorithm(7, "Binary Search", "Searching", "O(log n)", "O(1)", "Easy", "#C084FC"),

        // ── Data Structures (4) ──
        Algorithm(8, "Stack", "Data Structures", "O(1) push/pop", "O(n)", "Easy", "#FB923C"),
        Algorithm(9, "Queue", "Data Structures", "O(1) enq/deq", "O(n)", "Easy", "#FB923C"),
        Algorithm(10, "Binary Search Tree", "Data Structures", "O(log n) avg", "O(n)", "Medium", "#FB923C"),
        Algorithm(11, "Heap", "Data Structures", "O(log n) ins", "O(n)", "Medium", "#FB923C"),

        // ── Graph Traversal (2) ──
        Algorithm(12, "Breadth-First Search (BFS)", "Graph Traversal", "O(V+E)", "O(V)", "Easy", "#4ADE80"),
        Algorithm(13, "Depth-First Search (DFS)", "Graph Traversal", "O(V+E)", "O(V)", "Easy", "#4ADE80")
    )

    val categories = listOf("All", "Sorting", "Searching", "Data Structures", "Graph Traversal")
}
