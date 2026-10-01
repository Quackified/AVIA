package com.avia.model

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
    val allowedTools: Set<GraphTool> = emptySet(),
    val supportsFullscreen: Boolean = allowedTools.isNotEmpty(),
    val nodePlacementMode: NodePlacementMode = NodePlacementMode.FREEFORM,
    val hintProvider: (GraphTool, selectedNodeId: String?) -> String = { tool, _ -> tool.tooltip }
) {
    val isReadOnly: Boolean get() = allowedTools.isEmpty()

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
            supportsFullscreen = true,
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
                    GraphTool.WEIGHT -> "Tap edge to adjust weight with stepper"
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
            canEditEdgeWeights = true,
            canSetEndpoints = true,
            canDeleteElements = true,
            supportsDirectedEdges = false,
            supportsFullscreen = true,
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
                    GraphTool.WEIGHT -> "Tap edge to adjust weight with stepper"
                    GraphTool.ENDPOINTS -> "Tap node to set START (green) • Tap another to set TARGET (pink) • Tap to clear"
                    GraphTool.DELETE -> "Tap any node or edge to delete it"
                }
            }
        )

        /**
         * View-only mode for algorithms that construct structures strictly from input (BST, Heap)
         * or algorithms that do not permit topological editing.
         * Disables the floating toolbar, node creation/deletion, and fullscreen mode.
         */
        fun viewOnlyProfile(supportsDirectedEdges: Boolean = false): GraphCapabilityProfile = GraphCapabilityProfile(
            canMoveNodes = false,
            canPanAndZoom = true,
            canAddNode = false,
            canConnectNodes = false,
            canEditEdgeWeights = false,
            canSetEndpoints = false,
            canDeleteElements = false,
            supportsDirectedEdges = supportsDirectedEdges,
            supportsFullscreen = false,
            nodePlacementMode = NodePlacementMode.FREEFORM,
            allowedTools = emptySet(),
            hintProvider = { tool, _ -> tool.tooltip }
        )

        fun bstProfile(): GraphCapabilityProfile = viewOnlyProfile(supportsDirectedEdges = true)

        fun heapProfile(): GraphCapabilityProfile = viewOnlyProfile(supportsDirectedEdges = true)

        fun readOnlyProfile(): GraphCapabilityProfile = viewOnlyProfile(supportsDirectedEdges = false)
    }
}
