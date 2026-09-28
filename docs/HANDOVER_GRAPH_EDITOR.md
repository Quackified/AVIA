# HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework

**Timestamp**: September 27, 2026
**Branch**: master
**Parent Task**: Fix Explore UI wrap expansion, fix Interactive Mode header wrap expansion, implement goal-directed graph search + endpoints, add Dijkstra's Shortest Path as the 14th algorithm, build a full-featured node graph editor with fullscreen canvas and floating toolbar.

---

## 1. Executive Summary

This cycle touches three distinct subsystems:
1. **Explore / Practice UI layout bugfixes**: unconstrained row wraps in headers ('30 CHALLENGES', '0 PTS') and track catalog cards were causing vertical height blowouts under variable-width fonts or narrow viewports.
2. **Algorithm Engine additions**: BFS and DFS converted from blind, exhaustive-only traversals to **goal-directed target searches** with parent-map path reconstruction, early termination at target, correct ElementState.FOUND semantics, and terminal 'TARGET REACHED' / 'TARGET UNREACHABLE' steps. Introduction of **AlgorithmId.DIJKSTRA** as AlgoLens's 14th algorithm with its own priority-queue telemetry.
3. **Interactive Mode & Node Graph Editor**: fix the unconstrained header on the canvas, add a 6-tool mode architecture (MOVE, ADD, LINK, WEIGHT, ENDPOINTS, DELETE), custom fullscreen toggle with borderless overlay, and floating bottom glass toolbar.

---

## 2. File Status Breakdown

### Already Modified on Disk (Validated)

| File | Subsystem | Modifications |
|---|---|---|
| pp/src/main/java/com/example/algolens/ui/practice/ExploreCatalogScreen.kt | UI Constraints | Title Column given weight(1f); title/subtitle set to maxLines = 1, overflow = TextOverflow.Ellipsis; '30 CHALLENGES' pill set to maxLines = 1; category chips given maxLines = 1; TrackCatalogCard row given weight(1f), title Column given weight(1f), badge given maxLines = 1, description clamped to maxLines = 3. No more wrap blowouts. |
| pp/src/main/java/com/example/algolens/ui/practice/PracticeScreen.kt | UI Constraints | Title Column given weight(1f); title/subtitle given maxLines = 1 + ellipsis; score/streak badges given maxLines = 1; category filter chips given maxLines = 1. |
| pp/src/main/java/com/example/algolens/ui/visualizer/GraphBuilderOverlay.kt | Canvas Header | Left toolbar group placed in .weight(1f).horizontalScroll(rememberScrollState()); status chips and stats text set to maxLines = 1; right actions row kept shrink-resistant. The 'Interactive Mode' header height can no longer grow. |
| pp/src/main/java/com/example/algolens/model/AlgorithmId.kt | Model | Added DIJKSTRA('Dijkstra Shortest Path', VisualizerFamily.GRAPH_2D, InputKind.NONE, 'Medium', 'O((V+E) log V)', 'O(V)', AccentGreen, 'Shortest Path'). Added GraphTelemetryMode.DIJKSTRA_PQ('PRIORITY QUEUE'). |
| pp/src/main/java/com/example/algolens/data/AlgorithmRegistry.kt | Registry | Added registration entry for AlgorithmId.DIJKSTRA with supportsCustomInput = true, builderEnabled = true, graphTelemetryMode = DIJKSTRA_PQ, and overlays = listOf(WeightBadgeOverlay). |
| pp/src/main/java/com/example/algolens/ui/visualizer/ChallengeModeManager.kt | Challenge | Added explicit exhaustive branch AlgorithmId.DIJKSTRA -> null in buildPredictionQuestion with comment pointing to docs/FUTURE_WORK.md. |
| pp/src/main/java/com/example/algolens/data/chat/AiChatEngine.kt | AVIA Tutor | Added Dijkstra flowchart (Init dist[s]=0 -> PQ loop -> Extract-Min -> Relax -> Update) and updated algorithm count to Graph Traversal & Shortest Path (3). |
| pp/src/main/java/com/example/algolens/data/AlgorithmStepRepository.kt | Graph Search Engine | Added pure helper object GraphSearch at bottom of file (BFS, DFS, shortest path, adjacency builder, Result with path reconstruction). Converted generateBFSSteps and generateDFSSteps to accept optional targetNodeId: String? = null with early termination and path-edge highlighting. Added targetNodeId parameter to generateStepsForAlgorithm and routed Dijkstra in dispatch when. |

---

## 3. Pending Implementation Work

### Work Package A: Dijkstra Generation & Trace Code
1. **AlgorithmStepRepository.kt**:
   - Implement generateDijkstraSteps(startNodeId: String, customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null, targetNodeId: String? = null): List<VisualizerStep>.
   - Mechanics:
     - Maintains dist[u] initialized to 0 for start, infinity for others.
     - Maintains prev[u] map for parent reconstruction.
     - Min-priority queue: PriorityQueue<Pair<String, Int>> ordered by distance.
     - Telemetry: step.buffer displays current priority queue elements as BufferItem(value = '\(d=\)', nodeId = id).
     - Step phases: INITIALIZING -> EXTRACTING_MIN (ACTIVE on settled node) -> RELAXING (COMPARING on candidate edge) -> UPDATING_DIST / SKIPPING -> goal-directed stop if target reached -> COMPLETED / TARGET REACHED.
     - Highlight tree edges (prev pointers) on settled nodes.
2. **AlgorithmCodeRegistry.kt**:
   - Add Dijkstra trace code in Kotlin, Java, Python, and C++ with accurate line mappings to maintain multi-language code trace parity.
   - Patch getCode(id: AlgorithmId) to include AlgorithmId.DIJKSTRA -> getDijkstraCode(language).
3. **CustomizeGraphSheet.kt**:
   - Ensure the 'START NODE' dropdown activates for AlgorithmId.DIJKSTRA alongside BFS/DFS.

### Work Package B: Graph Editor Full-Screen & Floating Toolbar
1. **GraphTreeVisualizer.kt**:
   - Canvas top-right fullscreen button:
     - Position an AlgoGlyphs.Expand icon button in the top-right overlay of the canvas.
     - State: ar isFullscreen by remember { mutableStateOf(false) }.
     - In fullscreen, render the entire visualizer canvas + controls inside a borderless Dialog or edge-to-edge box with dark translucent backdrop, preserving node/edge states.
     - Top-right icon swaps to AlgoGlyphs.Close when in fullscreen.
2. **Tool System Architecture (GraphBuilderOverlay.kt)**:
   - Introduce enum class GraphTool { MOVE, ADD, LINK, WEIGHT, ENDPOINTS, DELETE }.
   - Floating bottom glass toolbar (rendered in fullscreen mode and when builder active):
     - Pill container with DoubleBezelShell or dark card styling, blur backdrop, horizontal icon buttons with tooltips/labels.
     - Tool buttons:
       1. **Move** (AlgoGlyphs.Tap / Hand): Drag any node to reposition in the 2D world.
       2. **Add Node** (AlgoGlyphs.Plus): Tap anywhere on canvas to plant a node with the next label.
       3. **Link / Edge** (Connect icon): Tap node A then node B to create an edge.
       4. **Weight** (AlgoGlyphs.Speed / Tag): Tap edge weight badge to open inline stepper (-/value/+), or tap-cycle 1-9.
       5. **Endpoints** (AlgoGlyphs.Compass / Flag): Tap node once -> set START (green ring + 'START' badge); tap another node -> set END / DESTINATION (pink ring + 'END' badge); tap selected endpoint -> clear.
       6. **Delete** (AlgoGlyphs.Close / Trash): Tap node or edge to remove.
     - Utility actions in toolbar: Center View, Reset Graph, Exit / Done.
3. **Endpoint Persistence & State Wiring**:
   - VisualizerScreenState / VisualizerHost: add graphStartNodeId: String? and graphTargetNodeId: String?.
   - Endpoints set via the editor immediately feed generateStepsForAlgorithm(..., traversalStartNodeId = start, targetNodeId = target), triggering goal-directed BFS/DFS/Dijkstra.
4. **Renderer Enhancements (GraphTreeRenderer.kt)**:
   - Render green halo + 'START' text above the designated start node.
   - Render pink halo + 'DESTINATION' / 'TARGET' text above the designated target node.
   - Live dotted/highlighted preview path connecting Start -> End computed dynamically via GraphSearch.shortestPath(...) when both endpoints are assigned.

### Work Package C: Documentation
1. Create docs/FUTURE_WORK.md documenting:
   - BST Traversal Family variants (Pre-Order, In-Order, Post-Order, Level-Order).
   - Queue Family variants (Circular Queue, Deque, Priority Queue, Blocking Queue).
   - 0-1 BFS variant for binary-weighted graphs.
   - A* heuristic search.
   - Challenge Mode prediction question generator for Dijkstra (PQ extract-min prediction).
   - Directed edge toggling in graph editor.
   - Multi-step undo/redo stack for editor modifications.

---

## 4. Verification Plan

1. **Static Analysis & Compilation**:
   - Run .\gradlew.bat :app:compileDebugKotlin to verify no symbol mismatches or exhaustive when gaps.
2. **Unit Tests**:
   - Create pp/src/test/java/com/example/algolens/data/GraphSearchTest.kt (BFS/DFS goal-stop, path, unreachable, null-target parity).
   - Create pp/src/test/java/com/example/algolens/data/DijkstraStepsTest.kt (distances, prev, goal-stop, unreachable).
   - Run .\gradlew.bat :app:testDebugUnitTest.
3. **Build Assembly**:
   - Run .\gradlew.bat :app:assembleDebug to verify end-to-end packaging.
   - Do NOT run lintFix per workspace rules.

---

## 5. Quick Context for Resuming Agent

- The layout constraint fixes in Phase 1 are **fully written and diff-checked**.
- AlgorithmStepRepository.kt currently contains GraphSearch and the goal-directed BFS/DFS logic, but generateDijkstraSteps still needs to be placed into the AlgorithmStepRepository object body before line 2233 where GraphSearch begins.
- AlgorithmId.DIJKSTRA and its registry specs are already committed to their respective enum/spec files.
- The immediate next coding step is implementing generateDijkstraSteps in AlgorithmStepRepository.kt and adding Dijkstra code trace to AlgorithmCodeRegistry.kt.
