package com.example.algolens.model

/**
 * Data structures for Graph visualization.
 */

data class GraphNode(
    val id: String,
    val x: Float, // Normalized 0-100 or absolute? Let's use normalized for scaling.
    val y: Float,
    val label: String
)

data class GraphEdge(
    val from: String,
    val to: String,
    val weight: Int
)
