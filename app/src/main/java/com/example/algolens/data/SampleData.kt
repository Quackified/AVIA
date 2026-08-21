package com.example.algolens.data

import com.example.algolens.model.Algorithm

object SampleData {
    val algorithms = listOf(
        Algorithm(1, "Bubble Sort", "Sorting", "O(n²)", "O(1)", "Easy", "#22D3EE"),
        Algorithm(2, "Quick Sort", "Sorting", "O(n log n)", "O(log n)", "Medium", "#22D3EE"),
        Algorithm(3, "Merge Sort", "Sorting", "O(n log n)", "O(n)", "Medium", "#22D3EE"),
        Algorithm(4, "BFS", "Graphs", "O(V+E)", "O(V)", "Easy", "#4ADE80"),
        Algorithm(5, "Dijkstra's", "Graphs", "O(V²)", "O(V)", "Hard", "#4ADE80"),
        Algorithm(6, "Knapsack 0/1", "Dynamic Programming", "O(nW)", "O(nW)", "Hard", "#FB923C"),
        Algorithm(7, "Longest CS", "Dynamic Programming", "O(mn)", "O(mn)", "Medium", "#FB923C"),
        Algorithm(8, "Binary Search", "Searching", "O(log n)", "O(1)", "Easy", "#C084FC")
    )

    val categories = listOf("All", "Sorting", "Graphs", "DP", "Searching")
}
