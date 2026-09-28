package com.example.algolens.model

/**
 * 6-Tool modes for interactive graph/tree authoring, inspection, and goal-directed endpoints.
 */
enum class GraphTool(val label: String, val tooltip: String) {
    MOVE("Move", "Drag nodes or pan canvas"),
    ADD("Add Node", "Insert a new node"),
    LINK("Connect", "Draw edge between nodes"),
    WEIGHT("Weight", "Cycle or edit edge weight"),
    ENDPOINTS("Endpoints", "Set Start / Target nodes"),
    DELETE("Delete", "Remove node or edge")
}

/**
 * Placement semantics for node creation.
 */
enum class NodePlacementMode {
    /** Place node anywhere on canvas (BFS, DFS, Dijkstra). */
    FREEFORM,
    /** Prompt user for integer key; node places according to BST invariants. */
    KEYED_BST_INSERT,
    /** Append value to heap array; node auto-places at next binary tree level. */
    HEAP_ARRAY_PUSH
}

/**
 * Defines which editing tools and topological interactions an algorithm supports.
 * Guarantees that algorithms only inherit tools and interactions that make conceptual and mathematical sense.
 */
data class GraphCapabilityProfile(
    val canMoveNodes: Boolean = true,
    val canPanAndZoom: Boolean = true,
    val canAddNode: Boolean = false,
    val canConnectNodes: Boolean = false,
    val canEditEdgeWeights: Boolean = false,
    val canSetEndpoints: Boolean = false,
    val canDeleteElements: Boolean = false,
    val supportsDirectedEdges: Boolean = false,
    val nodePlacementMode: NodePlacementMode = NodePlacementMode.FREEFORM,
    val allowedTools: Set<GraphTool> = emptySet(),
    val hintProvider: (GraphTool, selectedNodeId: String?) -> String = { tool, _ -> tool.tooltip }
) {
    companion object {
        fun dijkstraProfile(): GraphCapabilityProfile = GraphCapabilityProfile(
            canMoveNodes = true,
            canPanAndZoom = true,
            canAddNode = true,
            canConnectNodes = true,
            canEditEdgeWeights = true,
            canSetEndpoints = true,
            canDeleteElements = true,
            supportsDirectedEdges = false,
            nodePlacementMode = NodePlacementMode.FREEFORM,
            allowedTools = setOf(
                GraphTool.MOVE,
                GraphTool.ADD,
                GraphTool.LINK,
                GraphTool.WEIGHT,
                GraphTool.ENDPOINTS,
                GraphTool.DELETE
            ),
            hintProvider = { tool, selectedId ->
                when (tool) {
                    GraphTool.MOVE -> if (selectedId != null) "Selected Node $selectedId • Drag to reposition • Tap empty space to deselect" else "Drag any node to move • Drag background to pan viewport"
                    GraphTool.ADD -> if (selectedId != null) "Tap canvas to add node and auto-connect to $selectedId" else "Tap anywhere on empty canvas to plant a new node"
                    GraphTool.LINK -> if (selectedId != null) "Selected Node $selectedId: Tap target node to connect" else "Tap or drag from one node to another to create an edge"
                    GraphTool.WEIGHT -> "Tap weight pill to cycle (1..8) • Tap tool for exact weight (1..99)"
                    GraphTool.ENDPOINTS -> "Tap node to set START (green) • Tap another to set TARGET (pink) • Tap to clear"
                    GraphTool.DELETE -> "Tap any node or edge to delete it"
                }
            }
        )

        fun bfsDfsProfile(): GraphCapabilityProfile = GraphCapabilityProfile(
            canMoveNodes = true,
            canPanAndZoom = true,
            canAddNode = true,
            canConnectNodes = true,
            canEditEdgeWeights = false,
            canSetEndpoints = true,
            canDeleteElements = true,
            supportsDirectedEdges = false,
            nodePlacementMode = NodePlacementMode.FREEFORM,
            allowedTools = setOf(
                GraphTool.MOVE,
                GraphTool.ADD,
                GraphTool.LINK,
                GraphTool.ENDPOINTS,
                GraphTool.DELETE
            ),
            hintProvider = { tool, selectedId ->
                when (tool) {
                    GraphTool.MOVE -> if (selectedId != null) "Selected Node $selectedId • Drag to reposition • Tap empty space to deselect" else "Drag any node to move • Drag background to pan viewport"
                    GraphTool.ADD -> if (selectedId != null) "Tap canvas to add node and auto-connect to $selectedId" else "Tap anywhere on empty canvas to plant a new node"
                    GraphTool.LINK -> if (selectedId != null) "Selected Node $selectedId: Tap target node to connect" else "Tap or drag from one node to another to create an edge"
                    GraphTool.WEIGHT -> "BFS and DFS are unweighted; edge weights are disabled."
                    GraphTool.ENDPOINTS -> "Tap node to set START (green) • Tap another to set TARGET (pink) • Tap to clear"
                    GraphTool.DELETE -> "Tap any node or edge to delete it"
                }
            }
        )

        fun bstProfile(): GraphCapabilityProfile = GraphCapabilityProfile(
            canMoveNodes = true,
            canPanAndZoom = true,
            canAddNode = true,
            canConnectNodes = false,
            canEditEdgeWeights = false,
            canSetEndpoints = false,
            canDeleteElements = true,
            supportsDirectedEdges = true,
            nodePlacementMode = NodePlacementMode.KEYED_BST_INSERT,
            allowedTools = setOf(
                GraphTool.MOVE,
                GraphTool.ADD,
                GraphTool.DELETE
            ),
            hintProvider = { tool, selectedId ->
                when (tool) {
                    GraphTool.MOVE -> if (selectedId != null) "Selected Node $selectedId • Drag to nudge position (invariants preserved)" else "Drag node to nudge position (invariants preserved) • Drag background to pan"
                    GraphTool.ADD -> "Tap canvas to insert a new integer key (1..999) into BST"
                    GraphTool.DELETE -> "Tap any node to delete from BST (replaces with in-order successor)"
                    else -> tool.tooltip
                }
            }
        )

        fun heapProfile(): GraphCapabilityProfile = GraphCapabilityProfile(
            canMoveNodes = true,
            canPanAndZoom = true,
            canAddNode = true,
            canConnectNodes = false,
            canEditEdgeWeights = false,
            canSetEndpoints = false,
            canDeleteElements = true,
            supportsDirectedEdges = true,
            nodePlacementMode = NodePlacementMode.HEAP_ARRAY_PUSH,
            allowedTools = setOf(
                GraphTool.MOVE,
                GraphTool.ADD,
                GraphTool.DELETE
            ),
            hintProvider = { tool, selectedId ->
                when (tool) {
                    GraphTool.MOVE -> if (selectedId != null) "Selected Slot $selectedId • Drag to nudge position" else "Drag slot to nudge position • Drag background to pan"
                    GraphTool.ADD -> "Tap canvas to append an integer value (1..999) to heap array"
                    GraphTool.DELETE -> "Tap root (slot 0) to extract or tail leaf to remove"
                    else -> tool.tooltip
                }
            }
        )

        fun readOnlyProfile(): GraphCapabilityProfile = GraphCapabilityProfile(
            canMoveNodes = false,
            canPanAndZoom = true,
            canAddNode = false,
            canConnectNodes = false,
            canEditEdgeWeights = false,
            canSetEndpoints = false,
            canDeleteElements = false,
            supportsDirectedEdges = false,
            nodePlacementMode = NodePlacementMode.FREEFORM,
            allowedTools = emptySet(),
            hintProvider = { tool, _ -> tool.tooltip }
        )
    }
}
