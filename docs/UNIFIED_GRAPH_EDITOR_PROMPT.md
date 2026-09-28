# 🏛️ MISSION PROMPT: UNIFIED NODE GRAPH & TREE EDITOR FRAMEWORK FOR ALGOLENS

## 1. Executive Summary & Objective

You are tasked with completely overhauling the graph visualizer in **AlgoLens** (`app/src/main/java/com/example/algolens/ui/visualizer/`) to replace the current fragmented, fragile visualizer with a unified, modular, and extensible **Node Graph Editor Framework**.

### The Core Problem Today
1. **Tool Over-Generalization (Leaky Abstraction)**: All 6 tools (`MOVE`, `ADD`, `LINK`, `WEIGHT`, `ENDPOINTS`, `DELETE`) are hardcoded into a single floating toolbar. Algorithms like **Binary Search Tree (BST)** and **Binary Heap** inherit edge-weight editing (`WEIGHT`), arbitrary free-form edge creation (`LINK`), and pathfinding endpoint assignment (`ENDPOINTS`), which violate their structural invariants and confuse users.
2. **Disconnected Custom Graph Pipeline**: `generateBFSSteps`, `generateDFSSteps`, and `generateDijkstraSteps` accept a `customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>?`, but `generateBSTSteps` and `generateHeapSteps` discard custom graphs entirely, regenerating from scratch on every step and clashing with local edits.
3. **State Loss & Coordinate Jumping**: Scrubbing steps or playing the algorithm causes node positions to jump or disconnect from local gesture edits.
4. **Missing Modular Capability System**: The system currently uses a binary boolean `builderEnabled`. There is no per-algorithm capability matrix to declare what each data structure can or cannot do in interactive mode.
5. **Layout & Viewport Fragility**: No pinch-to-zoom, fixed canvas bounds that cause node collisions on varying screen sizes, and unhandled system insets in fullscreen mode.

### The Desired State
A unified **`GraphCanvasEngine`** that supports both **General Graphs** (BFS, DFS, Dijkstra, future A*, etc.) and **Structured Trees** (BST, Binary Heap, future AVL/Red-Black trees) under a shared interaction model, governed by a strict **`GraphCapabilityProfile`**.

---

## 2. Core Architecture: Capability & Profile System

Create a modular capability profile system so that functional modules (Weight editing, Endpoint assignment, Free-form linking, Keyed insertion) are **only** inherited by algorithms where they make mathematical and conceptual sense.

### 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`
```kotlin
package com.example.algolens.model

/**
 * Defines which editing tools and topological interactions an algorithm supports.
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
    val hintProvider: (GraphTool, selectedNodeId: String?) -> String
)

enum class NodePlacementMode {
    FREEFORM,          // Place node anywhere on canvas (BFS, DFS, Dijkstra)
    KEYED_BST_INSERT,  // Prompt user for integer key; node places according to BST invariants
    HEAP_ARRAY_PUSH    // Append value to heap array; node auto-places at next binary tree level
}

enum class GraphTool(val label: String, val tooltip: String) {
    MOVE("Move", "Drag nodes or pan canvas"),
    ADD("Add Node", "Insert a new node"),
    LINK("Connect", "Draw edge between nodes"),
    WEIGHT("Weight", "Cycle or edit edge weight"),
    ENDPOINTS("Endpoints", "Set Start / Target nodes"),
    DELETE("Delete", "Remove node or edge")
}
```

### 2.2 Algorithm Capability Mapping
Define explicit profiles in `AlgorithmRegistry.kt`:

| Algorithm | MOVE | ADD | LINK | WEIGHT | ENDPOINTS | DELETE | Placement Mode |
|---|:---:|:---:|:---:|:---:|:---:|:---:|---|
| **Dijkstra** | ✅ | ✅ | ✅ | ✅ (1..99) | ✅ (Start & Target) | ✅ | `FREEFORM` |
| **BFS / DFS** | ✅ | ✅ | ✅ | ❌ (Unweighted) | ✅ (Start & Target) | ✅ | `FREEFORM` |
| **Binary Search Tree** | ✅ (Local nudge) | ✅ (Keyed dialog) | ❌ (Tree structure auto-derived) | ❌ | ❌ (Uses Search Key) | ✅ (BST deletion) | `KEYED_BST_INSERT` |
| **Binary Heap** | ✅ (Local nudge) | ✅ (Value push) | ❌ (Indices 2i+1, 2i+2) | ❌ | ❌ | ✅ (Extract root/tail) | `HEAP_ARRAY_PUSH` |

---

## 3. Unified Data Layer & Pipeline Overhaul

### 3.1 Unifying the `customGraph` Pipeline in `AlgorithmStepRepository.kt`
Currently, `generateBSTSteps` and `generateHeapSteps` take hardcoded arrays and discard graph modifications.
Refactor all graph-type step generators to honor the unified model:

1. **`generateBSTSteps`**:
   - Signature: `generateBSTSteps(values: List<Int>, searchKey: Int, customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null)`
   - When `customGraph` is present:
     - Nodes retain their user-dragged coordinates (`x`, `y`).
     - Adding a node in BST mode inserts the integer into the BST sequence, re-computes the tree topology, but **preserves existing node positions** that the user customized.
   - Remove any remaining incremental insertion loops; Step 0 must immediately present the fully pre-built tree in `INITIALIZING` phase.

2. **`generateHeapSteps`**:
   - Signature: `generateHeapSteps(input: List<Int>, sortOrder: SortOrder, customPositions: Map<String, Offset>? = null)`
   - Tree topology is strictly defined by binary heap rules: parent $i$, left child $2i+1$, right child $2i+2$.
   - The user can adjust node positions visually (`customPositions`), but cannot arbitrarily link illegal edges.

3. **`generateBFSSteps` / `generateDFSSteps` / `generateDijkstraSteps`**:
   - When `customGraph` is modified, preserve all user node coordinates and edge weights.
   - If endpoints are designated (`graphStartNodeId`, `graphTargetNodeId`), validate that both exist in the node set; if one is deleted, fall back gracefully to the first available node.

### 3.2 Persistent Canvas Coordinates & Step State Decorator
Decouple **Topology** (node coordinates, edges, weights) from **Step State** (colors, element states, highlights, callstack, variables):
- Store `userCustomCoordinates: Map<String, Offset>` in `VisualizerScreenState`.
- When stepping through the algorithm, merge the step's `ElementState` (e.g. `COMPARING`, `ACTIVE`, `VISITED`) onto the node's **persisted user coordinates**, preventing nodes from resetting their positions between steps.

---

## 4. UI & Interaction Layer Redesign

### 4.1 Viewport Pan & Pinch-to-Zoom Engine
Refactor `GraphCanvasGeometry` in `app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt`:
- Support continuous **Scale / Zoom** ($0.5\times$ to $2.5\times$) via `Modifier.pointerInput` with `detectTransformGestures`.
- Apply transform matrix uniformly across nodes, edges, labels, weight pills, and endpoint halos.
- Provide quick utility actions:
  - **"Center View"**: Resets pan offset to center of mass.
  - **"Fit to Screen"**: Calculates bounding box of all nodes and sets zoom level to fit with 24dp margins.

### 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`)
Replace the monolithic toolbar with a profile-driven toolbar:
- Filter available tools dynamically: `profile.allowedTools.forEach { tool -> ... }`.
- If an algorithm does not support `WEIGHT`, the Weight tool is **not** rendered in the toolbar.
- If an algorithm does not support `ENDPOINTS`, the Endpoints tool is **not** rendered.
- If `profile.allowedTools` is empty (read-only graphs), hide the tool selector and only render viewport controls (`Fit`, `Center`, `Fullscreen`).
- Styling:
  - Use `AlgoTokens.radiusMd` pill with `DarkBackground.copy(alpha = 0.94f)`.
  - Border: `AlgoTokens.strokeThin` with `PrimaryCyan.copy(alpha = 0.35f)`.
  - Active tool highlighted with `CyanSubtle` background and `PrimaryCyan` tint.

### 4.3 Interactive Tool Logic & Touch Handling
Refactor `GraphBuilderGestures.kt`:
1. **`MOVE`**:
   - Hit test nodes with touch radius $\max(28\text{dp}, r_{\text{node}} + 12\text{dp})$.
   - Dragging node moves it within bounded world space.
   - Dragging canvas background pans the viewport.
   - Tapping node selects it for inspection or challenge prediction.
2. **`ADD` (Context-Sensitive)**:
   - For **General Graphs**: Tap canvas to plant node with next available letter (`A`, `B`, `C`...). If a node was already selected, automatically link from selected node to new node.
   - For **BST**: Tap canvas opens a quick number picker / dialog (`"Enter node key"`). Upon confirmation, key is inserted into BST, and tree smoothly re-lays out.
   - For **Heap**: Tap canvas opens a number dialog (`"Enter heap value"`). Appends to heap array.
3. **`LINK` (General Graphs Only)**:
   - Drag from source node displays a rubber-band dashed cyan line following finger position.
   - Releasing over a target node creates an undirected or directed edge.
   - Tapping node A then node B also creates an edge.
4. **`WEIGHT` (Weighted Graphs Only)**:
   - Tapping an edge weight badge opens an inline stepper or cycles weights $1 \to 2 \to 3 \to 5 \to 8 \to 1$.
   - Long-press allows direct numeric entry.
5. **`ENDPOINTS` (Pathfinding Only)**:
   - Tap unassigned node $\to$ becomes **START** (green pulsing halo + `START` badge).
   - Tap second node $\to$ becomes **TARGET** (pink pulsing halo + `TARGET` badge).
   - Tap either again $\to$ clears assignment.
   - Computes dynamic live preview path via `GraphSearch.shortestPath` when both are present.
6. **`DELETE`**:
   - Tap node $\to$ removes node and all incident edges.
   - Tap edge $\to$ removes edge.

### 4.4 Fullscreen Edge-Aware Canvas Overlay
Implement the fullscreen canvas as an edge-to-edge dialog:
- Wrap dialog container with `.statusBarsPadding().displayCutoutPadding().navigationBarsPadding()`.
- Top Header: Bounded in a `Surface` with `CanvasBackground`, containing algorithm title, node/edge telemetry, and an `Exit` button.
- Bottom: Floating toolbar anchored cleanly above the synchronized telemetry strip with dedicated safe padding.
- Never allow system notches, camera cutouts, or 3-button navigation bars to clip nodes, edge badges, or tool buttons.

---

## 5. Synchronized Telemetry Strips

Ensure the canvas integrates cleanly with bottom telemetry for Figma parity:
1. **Heap Array Strip**:
   - Render directly under heap tree.
   - Cells show indices $0 \dots N-1$, values, and role badges (`P` for parent, `L` for $2i+1$, `R` for $2i+2$).
   - Synchronized states (`ACTIVE`, `SWAPPING`, `SORTED`) matching tree nodes.
2. **Frontier Telemetry Strip**:
   - Displays live queue (BFS), stack (DFS), or min-priority queue (Dijkstra).
   - Shows visited order sequence: `A → B → D → ...`.
   - Shows cumulative highlighted path weight ($\sum w$).

---

## 6. Implementation Steps & Validation Checklist

### Step 1: Capability Profiles & Models
- [ ] Create `GraphCapabilityProfile.kt` with profiles for `BFS`, `DFS`, `DIJKSTRA`, `BST`, `HEAP`.
- [ ] Add `capabilityProfile` to `AlgorithmSpec` in `AlgorithmRegistry.kt`.

### Step 2: Step Repository Integration
- [ ] Update `generateBSTSteps` to accept persistent custom positions and ensure step 0 is pre-built.
- [ ] Update `generateHeapSteps` to honor custom node coordinates while maintaining heap tree structure.
- [ ] Verify `generateDijkstraSteps`, `generateBFSSteps`, `generateDFSSteps` fully preserve weights and endpoints.

### Step 3: Viewport & Renderer Refactoring
- [ ] Add zoom/pan transform matrix to `GraphCanvasGeometry`.
- [ ] Ensure edge weights, arrows, halos, and preview paths scale correctly with zoom.
- [ ] Verify node text remains crisp and legible at all scales.

### Step 4: Toolbar & Gestures Refactoring
- [ ] Refactor `GraphFloatingToolbar` to dynamically render only tools allowed by `spec.capabilityProfile`.
- [ ] Refactor `GraphBuilderGestures` to execute context-sensitive actions based on profile.
- [ ] Remove duplicate/redundant buttons from top canvas bars.

### Step 5: Fullscreen Inset & Layout Hardening
- [ ] Apply `statusBarsPadding()`, `displayCutoutPadding()`, `navigationBarsPadding()` to fullscreen dialogs.
- [ ] Test on simulated notch / punch-hole devices.

### Step 6: Test Suite & Verification
- [ ] Run `.\gradlew.bat :app:compileDebugKotlin` — zero errors.
- [ ] Run `.\gradlew.bat :app:testDebugUnitTest` — all unit tests pass.
- [ ] Verify BST pre-built state without insertion blinks.
- [ ] Verify Dijkstra edge weight cycling and shortest-path preview.
- [ ] Run `graphify update .` to sync AST knowledge graph.
