package com.example.algolens.data

import androidx.compose.ui.geometry.Offset
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState

/**
 * Pure structure-aware mutations for General Graphs, Binary Search Trees, and Heaps.
 * Ensures data integrity, invariants, and deterministic layouts across edits and playback.
 */
object GraphTreeMutations {

    const val MAX_BST_NODES = 15
    const val MAX_HEAP_NODES = 15
    const val MIN_KEY_VALUE = 1
    const val MAX_KEY_VALUE = 999

    // ─────────────────────────────────────────────────────────────
    // 1. General Graph Mutations (BFS, DFS, Dijkstra)
    // ─────────────────────────────────────────────────────────────

    /**
     * Finds the next available node label.
     * Evaluates A..Z first; once exhausted, yields collision-safe N1, N2, etc.
     */
    fun getNextAvailableNodeLabel(existingNodes: List<GraphNodeState>): String {
        val usedLabels = existingNodes.map { it.label }.toSet()
        for (ch in 'A'..'Z') {
            val s = ch.toString()
            if (s !in usedLabels) return s
        }
        var counter = 1
        while ("N$counter" in usedLabels) {
            counter++
        }
        return "N$counter"
    }

    /**
     * Determines the next available positive integer weight using the MEX
     * (Minimum Excluded Positive Integer) algorithm. If edges have weights
     * 1..10 and weight 6 was removed, the next created edge takes 6, and
     * subsequent edges resume at 11.
     */
    fun getNextAvailableWeight(edges: List<GraphEdgeState>): Int {
        val used = edges.mapNotNull { it.weight }.toSet()
        var candidate = 1
        while (candidate in used) {
            candidate++
        }
        return candidate.coerceIn(1, 99)
    }

    /**
     * Adds a new node to a general graph. If [autoLinkFromNodeId] is provided and exists,
     * creates an undirected edge between the selected node and the newly added node.
     */
    fun addGeneralGraphNode(
        nodes: List<GraphNodeState>,
        edges: List<GraphEdgeState>,
        x: Float,
        y: Float,
        autoLinkFromNodeId: String? = null,
        defaultWeight: Int? = null
    ): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        val nextLabel = getNextAvailableNodeLabel(nodes)
        val newNode = GraphNodeState(
            id = nextLabel,
            label = nextLabel,
            x = x,
            y = y,
            state = ElementState.ACTIVE
        )
        val updatedNodes = nodes + newNode
        var updatedEdges = edges
        if (autoLinkFromNodeId != null && nodes.any { it.id == autoLinkFromNodeId }) {
            val weight = defaultWeight ?: getNextAvailableWeight(edges)
            updatedEdges = updatedEdges + GraphEdgeState(
                from = autoLinkFromNodeId,
                to = nextLabel,
                weight = weight,
                isDirected = false,
                isHighlighted = true
            )
        }
        return updatedNodes to updatedEdges
    }

    /**
     * Links two nodes in a general graph.
     * Enforces invariants: no self-loops, no duplicate edges (order-independent for undirected).
     */
    fun linkGeneralGraphNodes(
        edges: List<GraphEdgeState>,
        fromId: String,
        toId: String,
        isDirected: Boolean = false,
        weight: Int? = null
    ): List<GraphEdgeState>? {
        if (fromId == toId) return null // Reject self-loops

        val alreadyExists = edges.any { e ->
            if (isDirected) {
                e.from == fromId && e.to == toId
            } else {
                (e.from == fromId && e.to == toId) || (e.from == toId && e.to == fromId)
            }
        }
        if (alreadyExists) return null // Reject duplicate edges

        val effectiveWeight = weight ?: getNextAvailableWeight(edges)
        val newEdge = GraphEdgeState(
            from = fromId,
            to = toId,
            weight = effectiveWeight,
            isDirected = isDirected,
            isHighlighted = true
        )
        return edges + newEdge
    }

    /**
     * Deletes a node and all incident edges.
     */
    fun deleteGeneralGraphNode(
        nodes: List<GraphNodeState>,
        edges: List<GraphEdgeState>,
        nodeId: String
    ): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        val updatedNodes = nodes.filter { it.id != nodeId }
        val updatedEdges = edges.filter { it.from != nodeId && it.to != nodeId }
        return updatedNodes to updatedEdges
    }

    /**
     * Deletes an edge matching the given endpoints.
     */
    fun deleteGeneralGraphEdge(
        edges: List<GraphEdgeState>,
        fromId: String,
        toId: String
    ): List<GraphEdgeState> {
        return edges.filterNot { e ->
            (e.from == fromId && e.to == toId) || (!e.isDirected && e.from == toId && e.to == fromId)
        }
    }

    /**
     * Cycles edge weight: 1 -> 2 -> 3 -> 5 -> 8 -> 1.
     * Non-sequence weights advance to 1.
     */
    fun cycleEdgeWeight(
        edges: List<GraphEdgeState>,
        fromId: String,
        toId: String
    ): List<GraphEdgeState> {
        val cycle = listOf(1, 2, 3, 5, 8)
        return edges.map { e ->
            val matches = (e.from == fromId && e.to == toId) ||
                (!e.isDirected && e.from == toId && e.to == fromId)
            if (matches) {
                val currentW = e.weight ?: 1
                val idx = cycle.indexOf(currentW)
                val nextW = if (idx >= 0) cycle[(idx + 1) % cycle.size] else 1
                e.copy(weight = nextW, isHighlighted = true)
            } else {
                e
            }
        }
    }

    /**
     * Sets explicit edge weight in 1..99.
     */
    fun setEdgeWeight(
        edges: List<GraphEdgeState>,
        fromId: String,
        toId: String,
        weight: Int
    ): List<GraphEdgeState> {
        val clampedWeight = weight.coerceIn(1, 99)
        return edges.map { e ->
            val matches = (e.from == fromId && e.to == toId) ||
                (!e.isDirected && e.from == toId && e.to == fromId)
            if (matches) e.copy(weight = clampedWeight, isHighlighted = true) else e
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Binary Search Tree (BST) Engine & Mutations
    // ─────────────────────────────────────────────────────────────

    data class BstTreeNode(
        val id: String,
        var value: Int,
        var left: BstTreeNode? = null,
        var right: BstTreeNode? = null
    )

    /**
     * Reconstructs a BST from sequential values, enforcing equal-goes-right.
     * Allocates collision-safe IDs ("v", "v#1", "v#2") for duplicate values.
     */
    fun buildBstTree(values: List<Int>): BstTreeNode? {
        if (values.isEmpty()) return null
        val seenIds = mutableSetOf<String>()

        fun allocateId(v: Int): String {
            val base = v.toString()
            var id = base
            var k = 1
            while (id in seenIds) {
                id = "$base#$k"
                k++
            }
            seenIds.add(id)
            return id
        }

        val root = BstTreeNode(id = allocateId(values[0]), value = values[0])
        for (i in 1 until values.size) {
            val v = values[i]
            val newId = allocateId(v)
            val newNode = BstTreeNode(id = newId, value = v)
            var cur = root
            while (true) {
                if (v < cur.value) {
                    if (cur.left == null) {
                        cur.left = newNode
                        break
                    } else {
                        cur = cur.left!!
                    }
                } else {
                    if (cur.right == null) {
                        cur.right = newNode
                        break
                    } else {
                        cur = cur.right!!
                    }
                }
            }
        }
        return root
    }

    /**
     * Inserts a key into the BST, enforcing the 15-node limit and 1..999 key bounds.
     */
    fun insertIntoBst(root: BstTreeNode?, key: Int, maxNodes: Int = MAX_BST_NODES): BstTreeNode? {
        val clampedKey = key.coerceIn(MIN_KEY_VALUE, MAX_KEY_VALUE)
        if (root == null) {
            return BstTreeNode(id = clampedKey.toString(), value = clampedKey)
        }

        // Check node count
        val allNodes = collectBstNodesInOrder(root)
        if (allNodes.size >= maxNodes) {
            return root // Capacity reached
        }

        val seenIds = allNodes.map { it.id }.toMutableSet()
        val baseId = clampedKey.toString()
        var newId = baseId
        var k = 1
        while (newId in seenIds) {
            newId = "$baseId#$k"
            k++
        }

        val newNode = BstTreeNode(id = newId, value = clampedKey)
        var cur: BstTreeNode = root
        while (true) {
            if (clampedKey < cur.value) {
                if (cur.left == null) {
                    cur.left = newNode
                    break
                } else {
                    cur = cur.left!!
                }
            } else {
                if (cur.right == null) {
                    cur.right = newNode
                    break
                } else {
                    cur = cur.right!!
                }
            }
        }
        return root
    }

    /**
     * Deletes a node by [targetId] from the BST using standard in-order successor policy.
     * Leaf: disconnect.
     * 1-child: splice child.
     * 2-children: replace with leftmost child in right subtree (successor).
     */
    fun deleteFromBst(root: BstTreeNode?, targetId: String): BstTreeNode? {
        if (root == null) return null

        fun findMin(node: BstTreeNode): BstTreeNode {
            var cur = node
            while (cur.left != null) {
                cur = cur.left!!
            }
            return cur
        }

        fun deleteRec(node: BstTreeNode?, id: String): BstTreeNode? {
            if (node == null) return null

            if (node.id == id) {
                // Case 1: Leaf
                if (node.left == null && node.right == null) {
                    return null
                }
                // Case 2: One child (right)
                if (node.left == null) {
                    return node.right
                }
                // Case 2: One child (left)
                if (node.right == null) {
                    return node.left
                }
                // Case 3: Two children -> find in-order successor in right subtree
                val successor = findMin(node.right!!)
                // Copy successor value and ID to this node
                node.value = successor.value
                val updatedRight = deleteRec(node.right, successor.id)
                node.right = updatedRight
                return node
            }

            node.left = deleteRec(node.left, id)
            node.right = deleteRec(node.right, id)
            return node
        }

        return deleteRec(root, targetId)
    }

    fun collectBstNodesInOrder(root: BstTreeNode?): List<BstTreeNode> {
        val list = mutableListOf<BstTreeNode>()
        fun inOrder(node: BstTreeNode?) {
            if (node == null) return
            inOrder(node.left)
            list.add(node)
            inOrder(node.right)
        }
        inOrder(root)
        return list
    }

    /**
     * Extracts values in pre-order traversal sequence.
     * Rebuilding a BST from pre-order sequence reproduces the exact tree structure.
     */
    fun bstToPreOrderValues(root: BstTreeNode?): List<Int> {
        val list = mutableListOf<Int>()
        fun preOrder(node: BstTreeNode?) {
            if (node == null) return
            list.add(node.value)
            preOrder(node.left)
            preOrder(node.right)
        }
        preOrder(root)
        return list
    }

    /**
     * Calculates deterministic 2D layout for BST, preserving any manual overrides in [customCoordinates].
     */
    fun bstToGraph(
        root: BstTreeNode?,
        customCoordinates: Map<String, Offset> = emptyMap()
    ): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        if (root == null) return emptyList<GraphNodeState>() to emptyList()

        val inOrderList = mutableListOf<BstTreeNode>()
        val depths = mutableMapOf<String, Int>()
        val edges = mutableListOf<GraphEdgeState>()

        fun traverse(node: BstTreeNode?, depth: Int) {
            if (node == null) return
            depths[node.id] = depth
            traverse(node.left, depth + 1)
            inOrderList.add(node)
            traverse(node.right, depth + 1)
            node.left?.let { edges.add(GraphEdgeState(node.id, it.id, isDirected = true)) }
            node.right?.let { edges.add(GraphEdgeState(node.id, it.id, isDirected = true)) }
        }
        traverse(root, 0)

        val total = inOrderList.size
        val maxDepth = (depths.values.maxOrNull() ?: 0).coerceAtLeast(1)
        val rankMap = inOrderList.mapIndexed { idx, n -> n.id to idx }.toMap()

        val spanX = maxOf(160f, (total - 1) * 28f)
        val spanY = maxOf(86f, maxDepth * 26f)

        // Keep root as first element so generators can immediately find rootId at nodes.firstOrNull()?.id
        val orderedNodes = listOf(root) + inOrderList.filter { it.id != root.id }

        val nodes = orderedNodes.map { n ->
            val custom = customCoordinates[n.id]
            val x = custom?.x ?: (if (total <= 1) 100f else 20f + ((rankMap[n.id] ?: 0).toFloat() / (total - 1).toFloat()) * spanX)
            val y = custom?.y ?: (if (maxDepth == 0) 50f else 18f + ((depths[n.id] ?: 0).toFloat() / maxDepth.toFloat()) * spanY)
            GraphNodeState(
                id = n.id,
                label = n.value.toString(),
                x = x,
                y = y,
                state = ElementState.IDLE
            )
        }
        return nodes to edges
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Binary Heap Mutations & Layout
    // ─────────────────────────────────────────────────────────────

    /**
     * Appends a value to the heap array, respecting [MAX_HEAP_NODES] limit and [MIN_KEY_VALUE]..[MAX_KEY_VALUE].
     */
    fun pushHeapValue(currentValues: List<Int>, value: Int, maxCapacity: Int = MAX_HEAP_NODES): List<Int> {
        if (currentValues.size >= maxCapacity) return currentValues
        val clamped = value.coerceIn(MIN_KEY_VALUE, MAX_KEY_VALUE)
        return currentValues + clamped
    }

    /**
     * Removes the root element (slot 0) from the heap array.
     */
    fun removeHeapRoot(currentValues: List<Int>): List<Int> {
        if (currentValues.isEmpty()) return emptyList()
        if (currentValues.size == 1) return emptyList()
        // Replace root with last element, matching heap extraction
        return listOf(currentValues.last()) + currentValues.subList(1, currentValues.size - 1)
    }

    /**
     * Removes the last leaf element (tail) from the heap array.
     */
    fun removeHeapTail(currentValues: List<Int>): List<Int> {
        return if (currentValues.isNotEmpty()) currentValues.dropLast(1) else emptyList()
    }

    /**
     * Checks if a slot index is a valid target for deletion (slot 0 for root, lastIndex for tail).
     */
    fun isSupportedHeapDeletionSlot(slotIdx: Int, heapSize: Int): Boolean {
        if (heapSize <= 0) return false
        return slotIdx == 0 || slotIdx == (heapSize - 1)
    }

    /**
     * Constructs graph nodes and mathematical 2i+1 / 2i+2 edges for binary heap.
     * Preserves slot coordinate overrides from [customCoordinates] keyed by slot index string.
     * Unweighted edges (weight = null) to maintain clear role presentation.
     */
    fun heapToGraph(
        values: List<Int>,
        states: Map<Int, ElementState> = emptyMap(),
        heapBound: Int = values.size,
        customCoordinates: Map<String, Offset> = emptyMap()
    ): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        if (values.isEmpty()) return emptyList<GraphNodeState>() to emptyList()

        val maxLevel = if (values.size <= 1) 0 else 31 - Integer.numberOfLeadingZeros(values.size)
        val treeNodes = values.mapIndexed { idx, v ->
            val slotId = idx.toString()
            val custom = customCoordinates[slotId]
            val level = 31 - Integer.numberOfLeadingZeros(idx + 1)
            val indexInLevel = (idx + 1) - (1 shl level)
            val nodesInLevel = 1 shl level

            val defaultX = 15f + ((indexInLevel + 0.5f) / nodesInLevel.toFloat()) * 170f
            val defaultY = if (maxLevel == 0) 54f else 18f + (level.toFloat() / maxLevel.toFloat()) * 82f

            val x = custom?.x ?: defaultX
            val y = custom?.y ?: defaultY

            val st = states[idx] ?: if (idx >= heapBound) ElementState.SORTED else ElementState.IDLE
            GraphNodeState(slotId, v.toString(), x, y, st)
        }

        val treeEdges = mutableListOf<GraphEdgeState>()
        for (i in 0 until values.size) {
            val left = 2 * i + 1
            val right = 2 * i + 2
            if (left < values.size) {
                val hi = (states[i] == ElementState.ACTIVE || states[i] == ElementState.SWAPPING) &&
                    (states[left] == ElementState.COMPARING || states[left] == ElementState.SWAPPING)
                treeEdges.add(GraphEdgeState(i.toString(), left.toString(), weight = null, isDirected = true, isHighlighted = hi))
            }
            if (right < values.size) {
                val hi = (states[i] == ElementState.ACTIVE || states[i] == ElementState.SWAPPING) &&
                    (states[right] == ElementState.COMPARING || states[right] == ElementState.SWAPPING)
                treeEdges.add(GraphEdgeState(i.toString(), right.toString(), weight = null, isDirected = true, isHighlighted = hi))
            }
        }
        return treeNodes to treeEdges
    }
}
