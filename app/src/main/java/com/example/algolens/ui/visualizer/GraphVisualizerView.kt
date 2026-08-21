package com.example.algolens.ui.visualizer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.algolens.model.GraphEdge
import com.example.algolens.model.GraphNode

/**
 * A custom View that draws a 2D Graph (nodes and edges).
 * Used for pathfinding algorithms like Dijkstra's.
 */
class GraphVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var nodes: List<GraphNode> = emptyList()
    private var edges: List<GraphEdge> = emptyList()
    
    private var visitedNodes = mutableSetOf<String>()
    private var activeNode: String? = null
    private var pathEdges = mutableSetOf<String>() // Format: "from-to"

    private val edgePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#1E293B")
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val nodePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val nodeStrokePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#94A3B8")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    private val weightBgPaint = Paint().apply {
        color = Color.parseColor("#060A14")
        style = Paint.Style.FILL
    }

    fun setGraph(nodes: List<GraphNode>, edges: List<GraphEdge>) {
        this.nodes = nodes
        this.edges = edges
        invalidate()
    }

    fun updateState(visited: Set<String>, active: String?, paths: Set<String>) {
        this.visitedNodes.clear()
        this.visitedNodes.addAll(visited)
        this.activeNode = active
        this.pathEdges.clear()
        this.pathEdges.addAll(paths)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (nodes.isEmpty()) return

        // View dimensions for scaling
        val w = width.toFloat()
        val h = height.toFloat()
        
        // We assume coordinates in GraphNode are 0-255 based on Figma's viewbox
        val scaleX = w / 255f
        val scaleY = h / 155f

        // 1. Draw Edges
        for (edge in edges) {
            val from = nodes.find { it.id == edge.from } ?: continue
            val to = nodes.find { it.id == edge.to } ?: continue

            val x1 = from.x * scaleX
            val y1 = from.y * scaleY
            val x2 = to.x * scaleX
            val y2 = to.y * scaleY

            val isPath = pathEdges.contains("${edge.from}-${edge.to}") || 
                         pathEdges.contains("${edge.to}-${edge.from}")

            edgePaint.color = if (isPath) Color.parseColor("#4ADE80") else Color.parseColor("#1E293B")
            edgePaint.strokeWidth = if (isPath) 6f else 4f
            
            canvas.drawLine(x1, y1, x2, y2, edgePaint)

            // Draw weight badge
            val midX = (x1 + x2) / 2
            val midY = (y1 + y2) / 2
            
            canvas.drawRect(midX - 20, midY - 15, midX + 20, midY + 15, weightBgPaint)
            textPaint.textSize = 20f
            textPaint.color = if (isPath) Color.parseColor("#4ADE80") else Color.parseColor("#64748B")
            canvas.drawText(edge.weight.toString(), midX, midY + 7, textPaint)
        }

        // 2. Draw Nodes
        for (node in nodes) {
            val nx = node.x * scaleX
            val ny = node.y * scaleY
            
            val isVisited = visitedNodes.contains(node.id)
            val isActive = activeNode == node.id

            // Fill color
            nodePaint.color = when {
                isActive -> Color.parseColor("#404ADE80") // Transparent Green
                isVisited -> Color.parseColor("#3022D3EE") // Transparent Cyan
                else -> Color.parseColor("#0C1526")
            }
            
            // Stroke color
            nodeStrokePaint.color = when {
                isActive -> Color.parseColor("#4ADE80")
                isVisited -> Color.parseColor("#22D3EE")
                else -> Color.parseColor("#22D3EE")
            }

            canvas.drawCircle(nx, ny, 30f, nodePaint)
            canvas.drawCircle(nx, ny, 30f, nodeStrokePaint)

            // Label
            textPaint.textSize = 28f
            textPaint.color = when {
                isActive -> Color.parseColor("#4ADE80")
                isVisited -> Color.parseColor("#E2E8F0")
                else -> Color.parseColor("#94A3B8")
            }
            canvas.drawText(node.label, nx, ny + 10, textPaint)
        }
    }
}
