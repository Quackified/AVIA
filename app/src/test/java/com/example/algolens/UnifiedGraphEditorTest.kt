package com.example.algolens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.GraphSearch
import com.example.algolens.data.GraphTreeMutations
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.GraphCapabilityProfile
import com.example.algolens.model.GraphTool
import com.example.algolens.model.NodePlacementMode
import com.example.algolens.ui.visualizer.GraphCanvasGeometry
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState
import com.example.algolens.ui.visualizer.VisualizerScreenState
import com.example.algolens.ui.visualizer.VisualizerStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Comprehensive unit tests for the Unified Graph and Tree Editor framework.
 * Tests capability profiles, pure structure mutations (General Graph, BST, Heap),
 * Dijkstra preview vs fewest hops, geometry transforms and invertibility,
 * and coordinate persistence across steps in VisualizerScreenState.
 */
class UnifiedGraphEditorTest {

    // ─────────────────────────────────────────────────────────────
    // 1. Graph Capability Profiles
    // ─────────────────────────────────────────────────────────────

    @Test
    fun dijkstraProfile_containsAllFiveToolsAndWeightedPositiveEditing() {
        val profile = GraphCapabilityProfile.dijkstraProfile()
        assertEquals(
            setOf(GraphTool.MOVE, GraphTool.ADD, GraphTool.LINK, GraphTool.ENDPOINTS, GraphTool.DELETE),
            profile.allowedTools
        )
        assertTrue(profile.canMoveNodes)
        assertTrue(profile.canAddNode)
        assertTrue(profile.canConnectNodes)
        assertTrue(profile.canEditEdgeWeights)
        assertTrue(profile.canSetEndpoints)
        assertTrue(profile.canDeleteElements)
        assertFalse(profile.supportsDirectedEdges)
        assertEquals(NodePlacementMode.FREEFORM, profile.nodePlacementMode)
    }

    @Test
    fun bfsDfsProfile_allowsToolsAndEnablesWeightEditing() {
        val profile = GraphCapabilityProfile.bfsDfsProfile()
        assertEquals(
            setOf(GraphTool.MOVE, GraphTool.ADD, GraphTool.LINK, GraphTool.ENDPOINTS, GraphTool.DELETE),
            profile.allowedTools
        )
        assertTrue(profile.canEditEdgeWeights)
        assertTrue(profile.canConnectNodes)
        assertTrue(profile.canSetEndpoints)
    }

    @Test
    fun bstProfile_isViewOnlyModeWithoutToolbarOrFullscreen() {
        val profile = GraphCapabilityProfile.bstProfile()
        assertTrue(profile.allowedTools.isEmpty())
        assertTrue(profile.isReadOnly)
        assertFalse(profile.supportsFullscreen)
        assertFalse(profile.canMoveNodes)
        assertFalse(profile.canAddNode)
        assertFalse(profile.canConnectNodes)
        assertFalse(profile.canEditEdgeWeights)
        assertFalse(profile.canSetEndpoints)
        assertFalse(profile.canDeleteElements)
        assertTrue(profile.supportsDirectedEdges)
        assertTrue(profile.canPanAndZoom)
    }

    @Test
    fun heapProfile_isViewOnlyModeWithoutToolbarOrFullscreen() {
        val profile = GraphCapabilityProfile.heapProfile()
        assertTrue(profile.allowedTools.isEmpty())
        assertTrue(profile.isReadOnly)
        assertFalse(profile.supportsFullscreen)
        assertFalse(profile.canMoveNodes)
        assertFalse(profile.canAddNode)
        assertFalse(profile.canConnectNodes)
        assertFalse(profile.canEditEdgeWeights)
        assertFalse(profile.canSetEndpoints)
        assertFalse(profile.canDeleteElements)
        assertTrue(profile.supportsDirectedEdges)
        assertTrue(profile.canPanAndZoom)
    }

    @Test
    fun readOnlyProfile_hasNoAllowedTools() {
        val profile = GraphCapabilityProfile.readOnlyProfile()
        assertTrue(profile.allowedTools.isEmpty())
        assertTrue(profile.isReadOnly)
        assertFalse(profile.supportsFullscreen)
        assertFalse(profile.canMoveNodes)
        assertFalse(profile.canAddNode)
        assertTrue(profile.canPanAndZoom)
    }

    @Test
    fun algorithmRegistry_attachesCorrectCapabilityProfiles() {
        val dijkstraSpec = AlgorithmRegistry.specFor(AlgorithmId.DIJKSTRA)
        assertNotNull(dijkstraSpec?.capabilityProfile)
        assertTrue(dijkstraSpec!!.capabilityProfile!!.canEditEdgeWeights)
        assertTrue(dijkstraSpec.builderEnabled)
        assertTrue(dijkstraSpec.capabilityProfile!!.supportsFullscreen)

        val bfsSpec = AlgorithmRegistry.specFor(AlgorithmId.BFS)
        assertNotNull(bfsSpec?.capabilityProfile)
        assertTrue(bfsSpec!!.capabilityProfile!!.canEditEdgeWeights)
        assertTrue(bfsSpec.builderEnabled)
        assertTrue(bfsSpec.capabilityProfile!!.supportsFullscreen)

        val bstSpec = AlgorithmRegistry.specFor(AlgorithmId.BINARY_SEARCH_TREE)
        assertNotNull(bstSpec?.capabilityProfile)
        assertTrue(bstSpec!!.capabilityProfile!!.isReadOnly)
        assertFalse(bstSpec.builderEnabled)
        assertFalse(bstSpec.capabilityProfile!!.supportsFullscreen)

        val heapSpec = AlgorithmRegistry.specFor(AlgorithmId.HEAP)
        assertNotNull(heapSpec?.capabilityProfile)
        assertTrue(heapSpec!!.capabilityProfile!!.isReadOnly)
        assertFalse(heapSpec.builderEnabled)
        assertFalse(heapSpec.capabilityProfile!!.supportsFullscreen)
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Pure Structure-Aware Mutations — General Graph
    // ─────────────────────────────────────────────────────────────

    @Test
    fun getNextAvailableWeight_findsSmallestMissingPositiveInteger() {
        // Empty edges -> returns 1
        assertEquals(1, GraphTreeMutations.getNextAvailableWeight(emptyList()))

        // Consecutive weights 1..5 -> returns 6
        val edges1to5 = (1..5).map { GraphEdgeState(from = "A", to = "B$it", weight = it) }
        assertEquals(6, GraphTreeMutations.getNextAvailableWeight(edges1to5))

        // Weights 1, 2, 4, 5 (missing 3) -> returns 3
        val edgesMissing3 = listOf(1, 2, 4, 5).map { GraphEdgeState(from = "A", to = "B$it", weight = it) }
        assertEquals(3, GraphTreeMutations.getNextAvailableWeight(edgesMissing3))

        // User case: 1..10 implemented, 6 removed -> returns 6
        val edges1to10Minus6 = (1..10).filter { it != 6 }.map { GraphEdgeState(from = "A", to = "B$it", weight = it) }
        assertEquals(6, GraphTreeMutations.getNextAvailableWeight(edges1to10Minus6))

        // Weights with nulls ignored
        val edgesWithNulls = listOf(
            GraphEdgeState(from = "A", to = "B", weight = null),
            GraphEdgeState(from = "B", to = "C", weight = 1),
            GraphEdgeState(from = "C", to = "D", weight = 2)
        )
        assertEquals(3, GraphTreeMutations.getNextAvailableWeight(edgesWithNulls))
    }

    @Test
    fun addGeneralGraphNode_autoLinksWithNextAvailableWeight() {
        val nA = GraphNodeState("A", "A", 0f, 0f)
        val nB = GraphNodeState("B", "B", 100f, 0f)
        val e1 = GraphEdgeState("A", "B", weight = 1)
        val (updatedNodes, updatedEdges) = GraphTreeMutations.addGeneralGraphNode(
            nodes = listOf(nA, nB),
            edges = listOf(e1),
            x = 200f,
            y = 0f,
            autoLinkFromNodeId = "B"
        )
        assertEquals(3, updatedNodes.size)
        assertEquals(2, updatedEdges.size)
        assertEquals(2, updatedEdges.last().weight) // MEX is 2
    }

    @Test
    fun getNextAvailableNodeLabel_generatesAlphabetThenSafeNLabels() {
        val nodesAtoZ = ('A'..'Z').map { GraphNodeState(it.toString(), it.toString(), 0f, 0f) }
        val next = GraphTreeMutations.getNextAvailableNodeLabel(nodesAtoZ)
        assertEquals("N1", next)

        val nodesWithN1 = nodesAtoZ + GraphNodeState("N1", "N1", 0f, 0f)
        assertEquals("N2", GraphTreeMutations.getNextAvailableNodeLabel(nodesWithN1))
    }

    @Test
    fun linkGeneralGraphNodes_rejectsSelfLoopsAndDuplicates() {
        val edges = listOf(GraphEdgeState("A", "B", weight = 3))
        // Self-loop rejected
        assertNull(GraphTreeMutations.linkGeneralGraphNodes(edges, "A", "A"))
        // Existing duplicate edge rejected (undirected)
        assertNull(GraphTreeMutations.linkGeneralGraphNodes(edges, "A", "B"))
        assertNull(GraphTreeMutations.linkGeneralGraphNodes(edges, "B", "A"))
        // Valid edge accepted
        val updated = GraphTreeMutations.linkGeneralGraphNodes(edges, "B", "C", weight = 5)
        assertNotNull(updated)
        assertEquals(2, updated!!.size)
    }

    @Test
    fun cycleEdgeWeight_cyclesThroughSequenceAndClampsDirectWeight() {
        val edges = listOf(GraphEdgeState("A", "B", weight = 1))
        val step1 = GraphTreeMutations.cycleEdgeWeight(edges, "A", "B")
        assertEquals(2, step1[0].weight)
        val step2 = GraphTreeMutations.cycleEdgeWeight(step1, "A", "B")
        assertEquals(3, step2[0].weight)
        val step3 = GraphTreeMutations.cycleEdgeWeight(step2, "A", "B")
        assertEquals(5, step3[0].weight)
        val step4 = GraphTreeMutations.cycleEdgeWeight(step3, "A", "B")
        assertEquals(8, step4[0].weight)
        val step5 = GraphTreeMutations.cycleEdgeWeight(step4, "A", "B")
        assertEquals(1, step5[0].weight)

        // Non-cycle weight resets to 1
        val nonCycle = listOf(GraphEdgeState("A", "B", weight = 7))
        assertEquals(1, GraphTreeMutations.cycleEdgeWeight(nonCycle, "A", "B")[0].weight)

        // Direct weight clamping
        val clampedHigh = GraphTreeMutations.setEdgeWeight(edges, "A", "B", 150)
        assertEquals(99, clampedHigh[0].weight)
        val clampedLow = GraphTreeMutations.setEdgeWeight(edges, "A", "B", -5)
        assertEquals(1, clampedLow[0].weight)
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Pure Structure-Aware Mutations — Binary Search Tree (BST)
    // ─────────────────────────────────────────────────────────────

    @Test
    fun bstTree_buildsEqualGoesRightAndHandlesDuplicatesWithIdSuffix() {
        val values = listOf(50, 30, 70, 30)
        val root = GraphTreeMutations.buildBstTree(values)
        assertNotNull(root)
        assertEquals("50", root!!.id)
        assertEquals(50, root.value)
        assertEquals("30", root.left?.id)
        // Equal key 30 goes right of the first 30
        assertEquals("30#1", root.left?.right?.id)
        assertEquals(30, root.left?.right?.value)
    }

    @Test
    fun bstTree_deletionPreservesInOrderSuccessorPolicy() {
        // Construct tree: Root 50, Left 30, Right 70, Right's children: 60, 80
        val root = GraphTreeMutations.buildBstTree(listOf(50, 30, 70, 60, 80))
        // Delete root 50: in-order successor in right subtree is 60
        val newRoot = GraphTreeMutations.deleteFromBst(root, "50")
        assertNotNull(newRoot)
        assertEquals(60, newRoot!!.value)
        assertEquals(70, newRoot.right?.value)
        assertNull(newRoot.right?.left) // 60 was spliced out
    }

    @Test
    fun bstPreOrderValues_reproducesIdenticalTreeStructure() {
        val originalValues = listOf(50, 30, 70, 20, 40, 60, 80)
        val root = GraphTreeMutations.buildBstTree(originalValues)
        val preOrder = GraphTreeMutations.bstToPreOrderValues(root)
        val reconstructedRoot = GraphTreeMutations.buildBstTree(preOrder)

        val origNodes = GraphTreeMutations.collectBstNodesInOrder(root).map { it.value }
        val reconNodes = GraphTreeMutations.collectBstNodesInOrder(reconstructedRoot).map { it.value }
        assertEquals(origNodes, reconNodes)
    }

    @Test
    fun bstTree_enforcesFifteenNodeCapacityLimit() {
        var root: GraphTreeMutations.BstTreeNode? = null
        for (i in 1..15) {
            root = GraphTreeMutations.insertIntoBst(root, i * 10)
        }
        assertEquals(15, GraphTreeMutations.collectBstNodesInOrder(root).size)
        // 16th insert rejected
        val overflowRoot = GraphTreeMutations.insertIntoBst(root, 999)
        assertEquals(15, GraphTreeMutations.collectBstNodesInOrder(overflowRoot).size)
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Pure Structure-Aware Mutations — Binary Heap
    // ─────────────────────────────────────────────────────────────

    @Test
    fun heapMutations_pushExtractAndTailRemoval() {
        val initial = listOf(10, 20, 30, 40, 50)
        val pushed = GraphTreeMutations.pushHeapValue(initial, 60)
        assertEquals(listOf(10, 20, 30, 40, 50, 60), pushed)

        val tailRemoved = GraphTreeMutations.removeHeapTail(pushed)
        assertEquals(listOf(10, 20, 30, 40, 50), tailRemoved)

        // Root extract replaces root with tail
        val rootExtracted = GraphTreeMutations.removeHeapRoot(tailRemoved)
        assertEquals(50, rootExtracted.first())
        assertEquals(4, rootExtracted.size)

        // Capacity check (max 15)
        var maxHeap = (1..15).toList()
        assertEquals(15, GraphTreeMutations.pushHeapValue(maxHeap, 99).size)
    }

    @Test
    fun heapDeletionSlot_validatesOnlyRootAndTail() {
        assertTrue(GraphTreeMutations.isSupportedHeapDeletionSlot(0, 7)) // root
        assertTrue(GraphTreeMutations.isSupportedHeapDeletionSlot(6, 7)) // tail
        assertFalse(GraphTreeMutations.isSupportedHeapDeletionSlot(1, 7)) // interior
        assertFalse(GraphTreeMutations.isSupportedHeapDeletionSlot(3, 7)) // interior
    }

    // ─────────────────────────────────────────────────────────────
    // 5. Dijkstra Preview Least-Cost Route vs Fewest Hops
    // ─────────────────────────────────────────────────────────────

    @Test
    fun dijkstraShortestPath_findsLeastCostRouteDifferingFromFewestHops() {
        // Direct A -> B has 1 hop, weight 10
        // Route A -> C -> D -> B has 3 hops, weights 2 + 2 + 2 = 6
        val edges = listOf(
            GraphEdgeState("A", "B", weight = 10),
            GraphEdgeState("A", "C", weight = 2),
            GraphEdgeState("C", "D", weight = 2),
            GraphEdgeState("D", "B", weight = 2)
        )
        val adj = GraphSearch.buildAdjacency(edges)

        // BFS finds fewest hops: ["A", "B"] (1 hop)
        val bfsPath = GraphSearch.shortestPath(adj, "A", "B")
        assertEquals(listOf("A", "B"), bfsPath)

        // Dijkstra finds least cost: ["A", "C", "D", "B"] (cost 6)
        val (dijkstraPath, cost) = GraphSearch.dijkstraShortestPath(adj, "A", "B")
        assertEquals(listOf("A", "C", "D", "B"), dijkstraPath)
        assertEquals(6, cost)
    }

    @Test
    fun dijkstraShortestPath_handlesSameStartAndTargetAndUnreachable() {
        val edges = listOf(GraphEdgeState("A", "B", weight = 5))
        val adj = GraphSearch.buildAdjacency(edges)

        val (samePath, sameCost) = GraphSearch.dijkstraShortestPath(adj, "A", "A")
        assertEquals(listOf("A"), samePath)
        assertEquals(0, sameCost)

        val (unreachablePath, unreachableCost) = GraphSearch.dijkstraShortestPath(adj, "A", "Z")
        assertTrue(unreachablePath.isEmpty())
        assertEquals(-1, unreachableCost)
    }

    // ─────────────────────────────────────────────────────────────
    // 6. GraphCanvasGeometry Viewport & Exact Invertibility
    // ─────────────────────────────────────────────────────────────

    @Test
    fun geometry_exactInvertibilityAcrossMultipleScalesAndPans() {
        val nodes = listOf(
            GraphNodeState("A", "A", 20f, 30f),
            GraphNodeState("B", "B", 120f, 80f),
            GraphNodeState("C", "C", 200f, 150f)
        )
        val drawSize = Size(600f, 800f)
        val testPans = listOf(Offset.Zero, Offset(50f, -80f), Offset(-120f, 200f))
        val testZooms = listOf(0.5f, 1.0f, 1.75f, 2.5f)

        for (pan in testPans) {
            for (zoom in testZooms) {
                val geom = GraphCanvasGeometry.from(
                    nodes = nodes,
                    drawSize = drawSize,
                    panOffset = pan,
                    zoom = zoom
                )
                for (node in nodes) {
                    val canvasOffset = geom.toCanvasOffset(node.x, node.y)
                    val worldCoords = geom.toWorldCoords(canvasOffset)
                    assertEquals("Invertibility X failed at zoom $zoom", node.x, worldCoords.x, 0.05f)
                    assertEquals("Invertibility Y failed at zoom $zoom", node.y, worldCoords.y, 0.05f)
                }
            }
        }
    }

    @Test
    fun geometry_computeCenterPan_placesCenterOfMassAtViewportCenter() {
        val nodes = listOf(
            GraphNodeState("A", "A", 40f, 40f),
            GraphNodeState("B", "B", 160f, 160f)
        )
        val drawSize = Size(400f, 400f)
        val centerPan = GraphCanvasGeometry.computeCenterPan(nodes, drawSize, zoom = 1f)

        val geom = GraphCanvasGeometry.from(nodes, drawSize, panOffset = centerPan, zoom = 1f)
        val cA = geom.toCanvasOffset(40f, 40f)
        val cB = geom.toCanvasOffset(160f, 160f)
        val centerOfMassX = (cA.x + cB.x) / 2f
        val centerOfMassY = (cA.y + cB.y) / 2f

        assertEquals(200f, centerOfMassX, 1.0f)
        assertEquals(200f, centerOfMassY, 1.0f)
    }

    @Test
    fun geometry_computeFitZoomAndPan_boundsNodesWithinMargin() {
        val nodes = listOf(
            GraphNodeState("A", "A", 0f, 0f),
            GraphNodeState("B", "B", 300f, 400f)
        )
        val drawSize = Size(500f, 500f)
        val (fitZoom, fitPan) = GraphCanvasGeometry.computeFitZoomAndPan(nodes, drawSize, marginPx = 24f)

        assertTrue("Fit zoom should be within bounds", fitZoom in 0.5f..2.5f)
        val geom = GraphCanvasGeometry.from(nodes, drawSize, panOffset = fitPan, zoom = fitZoom)
        val cA = geom.toCanvasOffset(0f, 0f)
        val cB = geom.toCanvasOffset(300f, 400f)

        assertTrue(cA.x >= 0f)
        assertTrue(cA.y >= 0f)
        assertTrue(cB.x <= 500f)
        assertTrue(cB.y <= 500f)
    }

    @Test
    fun geometry_isotropicScaling_preventsVerticalStretchingOnFullscreenExpansion() {
        val nodes = listOf(
            GraphNodeState("A", "A", 0f, 0f),
            GraphNodeState("B", "B", 100f, 0f),
            GraphNodeState("C", "C", 0f, 100f)
        )
        // Embedded size (wide and short)
        val embeddedSize = Size(600f, 300f)
        val geomEmbedded = GraphCanvasGeometry.from(nodes, embeddedSize, referenceHeight = 300f)
        val embA = geomEmbedded.toCanvasOffset(0f, 0f)
        val embB = geomEmbedded.toCanvasOffset(100f, 0f)
        val embC = geomEmbedded.toCanvasOffset(0f, 100f)

        val embDistX = embB.x - embA.x
        val embDistY = embC.y - embA.y
        assertEquals("Embedded aspect ratio must be 1:1 (isotropic)", embDistX, embDistY, 0.01f)

        // Fullscreen size (tall expansion)
        val fullscreenSize = Size(600f, 1000f)
        val geomFullscreen = GraphCanvasGeometry.from(nodes, fullscreenSize, referenceHeight = 300f)
        val fullA = geomFullscreen.toCanvasOffset(0f, 0f)
        val fullB = geomFullscreen.toCanvasOffset(100f, 0f)
        val fullC = geomFullscreen.toCanvasOffset(0f, 100f)

        val fullDistX = fullB.x - fullA.x
        val fullDistY = fullC.y - fullA.y
        assertEquals("Fullscreen aspect ratio must be 1:1 (isotropic)", fullDistX, fullDistY, 0.01f)
        assertEquals("Fullscreen must not stretch scale relative to reference", embDistX, fullDistX, 0.01f)
    }

    @Test
    fun geometry_worldToScreen_and_toWorldCoords_areExactInverses_and_invariantToNodes() {
        val drawSize = Size(600f, 400f)
        val emptyGeom = GraphCanvasGeometry.from(emptyList(), drawSize)
        val testScreenOffset = Offset(250f, 180f)

        // Converting screen touch to world
        val worldCoord = emptyGeom.toWorldCoords(testScreenOffset)

        // Single node added at that world coordinate
        val nodeA = GraphNodeState("A", "A", worldCoord.x, worldCoord.y)
        val geomWithA = GraphCanvasGeometry.from(listOf(nodeA), drawSize)

        // Invariant: baseScale and nodeCenter must be completely invariant
        assertEquals(emptyGeom.baseScale, geomWithA.baseScale, 0.0001f)
        assertEquals(emptyGeom.nodeCenterX, geomWithA.nodeCenterX, 0.0001f)
        assertEquals(emptyGeom.nodeCenterY, geomWithA.nodeCenterY, 0.0001f)

        // Invariant: screen position of nodeA matches exactly where the user tapped
        val screenPosA = geomWithA.toCanvasOffset(nodeA.x, nodeA.y)
        assertEquals(testScreenOffset.x, screenPosA.x, 0.01f)
        assertEquals(testScreenOffset.y, screenPosA.y, 0.01f)

        // Invariant: adding second node does not shift nodeA
        val nodeB = GraphNodeState("B", "B", 50f, 50f)
        val geomWithAB = GraphCanvasGeometry.from(listOf(nodeA, nodeB), drawSize)
        val screenPosAAfterB = geomWithAB.toCanvasOffset(nodeA.x, nodeA.y)
        assertEquals(screenPosA.x, screenPosAAfterB.x, 0.01f)
        assertEquals(screenPosA.y, screenPosAAfterB.y, 0.01f)
    }

    // ─────────────────────────────────────────────────────────────
    // 7. VisualizerScreenState Node Coordinate Persistence
    // ─────────────────────────────────────────────────────────────

    @Test
    fun visualizerScreenState_decoratesNodesWithUserCustomCoordinates() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.BFS))
        state.steps = listOf(
            VisualizerStep(
                stepIndex = 0,
                nodes = listOf(
                    GraphNodeState("A", "A", 10f, 10f),
                    GraphNodeState("B", "B", 20f, 20f)
                )
            ),
            VisualizerStep(
                stepIndex = 1,
                nodes = listOf(
                    GraphNodeState("A", "A", 10f, 10f),
                    GraphNodeState("B", "B", 20f, 20f)
                )
            )
        )

        // Before override
        assertEquals(10f, state.currentStep.nodes.first().x, 0.01f)

        // Move node A to (150, 250)
        state.updateUserCustomCoordinate("A", Offset(150f, 250f))

        // Coordinates persist across steps without regenerating steps
        assertEquals(150f, state.currentStep.nodes.first { it.id == "A" }.x, 0.01f)
        assertEquals(250f, state.currentStep.nodes.first { it.id == "A" }.y, 0.01f)

        // Advance step — custom coordinate remains applied!
        state.stepForward()
        assertEquals(1, state.currentStepIdx)
        assertEquals(150f, state.currentStep.nodes.first { it.id == "A" }.x, 0.01f)
        assertEquals(250f, state.currentStep.nodes.first { it.id == "A" }.y, 0.01f)
    }

    @Test
    fun visualizerScreenState_resetGraphOverridesClearsCoordinatesAndCustomGraph() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.DIJKSTRA))
        state.customGraph = Pair(listOf(GraphNodeState("A", "A", 0f, 0f)), emptyList())
        state.updateUserCustomCoordinate("A", Offset(99f, 99f))
        state.graphStartNodeId = "A"
        state.graphTargetNodeId = "B"

        state.resetGraphOverrides()

        assertNull(state.customGraph)
        assertTrue(state.userCustomCoordinates.isEmpty())
        assertNull(state.graphStartNodeId)
        assertNull(state.graphTargetNodeId)
        assertEquals(GraphTool.MOVE, state.activeGraphTool)
    }

    @Test
    fun visualizerScreenState_effectiveTraversalNodeIdsExplicitEmpty() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.BFS))
        // Null custom graph uses canonical nodes
        assertTrue(state.effectiveTraversalNodeIds.isNotEmpty())

        // Explicit empty custom graph does NOT fall back to canonical defaults
        state.customGraph = Pair(emptyList(), emptyList())
        assertTrue("Empty custom graph must yield empty traversal IDs", state.effectiveTraversalNodeIds.isEmpty())
        assertEquals("", state.effectiveTraversalStartNodeId)
    }

    // ─────────────────────────────────────────────────────────────
    // 8. Undo / Redo History & Workspace Widget Deck
    // ─────────────────────────────────────────────────────────────

    @Test
    fun visualizerScreenState_undoAndRedoGraphMutations() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.DIJKSTRA))
        assertFalse(state.canUndoGraph)
        assertFalse(state.canRedoGraph)

        // Mutation 1: Move Node A to (50, 50)
        state.recordGraphSnapshot()
        state.updateUserCustomCoordinate("A", Offset(50f, 50f))
        assertTrue(state.canUndoGraph)
        assertFalse(state.canRedoGraph)

        // Mutation 2: Move Node A to (100, 100) and set Start Node to "A"
        state.recordGraphSnapshot()
        state.updateUserCustomCoordinate("A", Offset(100f, 100f))
        state.graphStartNodeId = "A"

        assertEquals(Offset(100f, 100f), state.userCustomCoordinates["A"])
        assertEquals("A", state.graphStartNodeId)

        // Undo Mutation 2 -> reverts back to (50, 50) and null start node
        state.undoGraph()
        assertTrue(state.canUndoGraph)
        assertTrue(state.canRedoGraph)
        assertEquals(Offset(50f, 50f), state.userCustomCoordinates["A"])
        assertNull(state.graphStartNodeId)

        // Undo Mutation 1 -> reverts back to empty coordinates
        state.undoGraph()
        assertFalse(state.canUndoGraph)
        assertTrue(state.canRedoGraph)
        assertTrue(state.userCustomCoordinates.isEmpty())

        // Redo Mutation 1 -> restores (50, 50)
        state.redoGraph()
        assertTrue(state.canUndoGraph)
        assertEquals(Offset(50f, 50f), state.userCustomCoordinates["A"])

        // Redo Mutation 2 -> restores (100, 100) and "A"
        state.redoGraph()
        assertFalse(state.canRedoGraph)
        assertEquals(Offset(100f, 100f), state.userCustomCoordinates["A"])
        assertEquals("A", state.graphStartNodeId)
    }

    @Test
    fun visualizerScreenState_widgetDeckManagementAndFocusMode() {
        val state = VisualizerScreenState(Algorithm(id = AlgorithmId.DIJKSTRA))
        assertFalse(state.canvasFocusMode)

        // Toggle Focus Mode
        state.canvasFocusMode = true
        assertTrue(state.canvasFocusMode)

        // Initial default widgets
        assertEquals(
            listOf(com.example.algolens.ui.visualizer.WidgetType.TRACE, com.example.algolens.ui.visualizer.WidgetType.STATE, com.example.algolens.ui.visualizer.WidgetType.TELEMETRY),
            state.enabledWidgets
        )

        // Reorder widgets: move TELEMETRY (index 2) to first position (index 0)
        state.moveWidget(2, 0)
        assertEquals(
            listOf(com.example.algolens.ui.visualizer.WidgetType.TELEMETRY, com.example.algolens.ui.visualizer.WidgetType.TRACE, com.example.algolens.ui.visualizer.WidgetType.STATE),
            state.enabledWidgets
        )
        assertEquals(0, state.activeWidgetIndex)

        // Toggle widget visibility: disable STATE
        state.toggleWidget(com.example.algolens.ui.visualizer.WidgetType.STATE)
        assertFalse(state.enabledWidgets.contains(com.example.algolens.ui.visualizer.WidgetType.STATE))
        assertEquals(2, state.enabledWidgets.size)

        // Toggle widget back on
        state.toggleWidget(com.example.algolens.ui.visualizer.WidgetType.STATE)
        assertTrue(state.enabledWidgets.contains(com.example.algolens.ui.visualizer.WidgetType.STATE))
        assertEquals(3, state.enabledWidgets.size)

        // At least 1 widget must always remain enabled
        state.toggleWidget(com.example.algolens.ui.visualizer.WidgetType.TRACE)
        state.toggleWidget(com.example.algolens.ui.visualizer.WidgetType.STATE)
        assertEquals(1, state.enabledWidgets.size)
        assertEquals(com.example.algolens.ui.visualizer.WidgetType.TELEMETRY, state.enabledWidgets.first())

        // Attempting to disable the last widget should be a no-op
        state.toggleWidget(com.example.algolens.ui.visualizer.WidgetType.TELEMETRY)
        assertEquals(1, state.enabledWidgets.size)
        assertEquals(com.example.algolens.ui.visualizer.WidgetType.TELEMETRY, state.enabledWidgets.first())
    }
}
