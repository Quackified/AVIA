# 🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS

## 1. Executive Summary & Objective

In **AlgoLens** (`AVIA`), the 1D Array Visualizer (`LINEAR_1D`) features high-craft physics animations: the 3-phase swap arc (`lift → travel → settle`) in `SwapFlight.kt`, animated slot reordering in `CellGrid.kt`, and spring-driven pointer transitions.

However, the remaining visualizer families currently suffer from **abrupt frame cuts and zero motion continuity**:
1. **BUFFER Visualizer (`BUFFER`: Stack & Queue)**:
   - Elements appear and vanish instantly on `PUSH`, `POP`, `ENQUEUE`, and `DEQUEUE`.
   - The Stack UI requires redesign: moving away from a plain column of boxes to an intuitive, physical laboratory-grade instrument (graduated memory column / chamber with legible capacity levels and fluid entry/exit).
2. **GRAPH_2D Visualizer (`GRAPH_2D`: BST, Heap, BFS, DFS, Dijkstra)**:
   - **Heap swaps teleport**: When sift-up or sift-down occurs, parent and child swap index coordinates instantly without positional interpolation.
   - **BST insertions lack path history**: Newly inserted nodes snap directly to their leaf position without showing the traversal descent down the tree.
   - **Graph traversals lack signal propagation**: BFS, DFS, and Dijkstra show edges turning colors instantly; there is no wavefront pulse or traveling particle indicating traversal flow from node $u$ to $v$.
   - **Node state transitions lack visceral feedback**: Visiting a node or discovering the target does not have a scale punch or ripple ring.

### Objective
Design and implement a **Unified Animation & Motion Framework** for `BUFFER` and `GRAPH_2D` visualizers in Jetpack Compose, while redesigning the Stack UI into an intuitive, high-craft developer instrument.

---

## 2. Core Problem Analysis & Current State

### 2.1 Stack & Queue (Buffer Family)
- **Current State**: `BufferVisualizer.kt` renders items inside a basic bordered `Column` (Stack) or `Row` (Queue). State changes are limited to `animateColorAsState` on background and border.
- **Deficiencies**:
  - Pushing an item into the Stack should feel like an element dropping into a physical vacuum chamber and settling with a slight spring bounce.
  - Popping an item should lift it upwards out of the chamber mouth before fading out.
  - Queue operations lack horizontal transit motion (items sliding from entry tail to exit head).
  - The Stack chamber looks generic; it lacks graduation tick marks, depth indexes ($0..7$), and tactile visual feedback.

### 2.2 Heap & BST (Tree Structures)
- **Current State**: `GraphTreeRenderer.kt` renders nodes via `Canvas.drawCircle` based on static `(x, y)` coordinates emitted in each `VisualizerStep`.
- **Deficiencies**:
  - In Heap Sort / Heapify, swaps are fundamental. Teleporting nodes breaks the user's mental model of tree rebalancing.
  - In BST insertion, the user needs to visualize why the element went left vs. right at each comparison.

### 2.3 Graph Traversals & Shortest Path (BFS, DFS, Dijkstra)
- **Current State**: Edge states switch between `DEFAULT`, `VISITED`, and `HIGHLIGHTED` without directional motion.
- **Deficiencies**:
  - When BFS/DFS traverses an edge $(u, v)$, a directional light pulse or beam should travel from $u$ to $v$.
  - When Dijkstra relaxes an edge, the comparison and distance update should visually trigger from the source node to the target node.
  - When the final target is found or shortest path reconstructed, the path should illuminate sequentially from Start to Target.

---

## 3. Detailed Specifications & Desired State

### 3.1 Work Package A: Stack UI Redesign & Buffer Physics

1. **Stack Chamber Redesign**:
   - Redesign the Stack container in `BufferVisualizer.kt`:
     - Double-bezel chamber with subtle inner glow and etched graduation marks on the left rail (`[7] MAX`, `[6]`, ..., `[0] BASE`).
     - Clear visual mouth aperture with subtle chamfered guides.
     - Live capacity meter badge (`COUNT: N / 8`) and dynamic headroom indicator.
     - Clean typography using `JetBrainsMono` for slot memory addresses/indices.
2. **Stack Motion Physics**:
   - **`PUSH` (Drop & Settle)**:
     - The incoming item spawns above the chamber mouth, accelerates downward along the vertical axis, and settles into its slot using `AlgoTokens.settleSpring` (slight damped bounce).
   - **`POP` (Eject & Dissolve)**:
     - The top item lifts upward out of the mouth with negative Y translation and dissolves (`alpha: 1f → 0f`, `scale: 1f → 0.9f`).
   - **`PEEK` (Inspection Glow)**:
     - The top item scales up subtly (`1.05x`) with a cyan/purple inspection pulse.
3. **Queue Motion (Linear Pipeline)**:
   - Items enter from the right boundary with a horizontal slide-in (`ENQUEUE`).
   - Existing items smoothly shift one slot to the left via `Modifier.animateItemPlacement()` or animated offset transitions.
   - Dequeued items slide out through the left exit port with a fade-out.

---

### 3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`)

1. **Positional Node Interpolation (Heap Swaps & Tree Rebalancing)**:
   - Track node identities (`node.id`) across consecutive `VisualizerStep`s.
   - When a node's logical coordinate changes from $(x_1, y_1)$ to $(x_2, y_2)$ (e.g. Heap swap), animate its center position smoothly using Compose `Animatable` or `animateOffsetAsState` with `AlgoTokens.cellTravelSpring`.
   - Prevent node overlapping during linear swaps by applying a subtle transverse arc offset (similar to `SwapFlight.kt`).
2. **Directional Traversal Signal Propagation (BFS, DFS, Dijkstra)**:
   - For edges transitioning to `VISITED` or `ACTIVE`:
     - Render a moving particle or glowing dash head along the edge vector:
       $$\vec{P}(t) = (1 - t)\vec{u} + t\vec{v}, \quad t \in [0, 1]$$
     - Pulse duration: proportional to playback speed (default ~240ms at 1.0x).
3. **Node Activation Pop & Ripple**:
   - When a node transitions to `ElementState.ACTIVE` or `TARGET`:
     - Scale pop: `1.0f → 1.18f → 1.0f` using `AlgoTokens.evalSpring`.
     - Transient ripple: an expanding hairline circular ring dissipating outward from the node boundary ($R \to R + 14\text{dp}$, alpha $0.8 \to 0$).
4. **Shortest Path Sequential Neon Trace**:
   - When reaching terminal state in Dijkstra / Target BFS:
     - Sequentially illuminate edges and nodes along the reconstructed path from Start to Target with an emerald/cyan travelling beam.

---

### 3.3 Work Package C: Transport & Scrubber Coordination

1. **Scrubbing & Step Snapping**:
   - Rapid scrubbing on the `PlaybackRail` must **cancel running animations immediately** and snap directly to the target frame's exact coordinates and states.
   - Animations should only play during continuous playback (`isPlaying == true`) or single-step advance (`onStepForward`).
2. **Speed Scaling**:
   - Scale animation durations and spring stiffness according to `AppSettings.playbackSpeed`:
     - `0.5x`: 1.5x duration (slower, educational observation).
     - `1.0x`: standard duration (~250ms–350ms).
     - `2.0x`: 0.5x duration (snappy, fast execution).

---

## 4. Architectural Invariants & Constraints

1. **Tokenized Design System (`docs/DESIGN.md`)**:
   - No hardcoded raw hex colors. Use `ui/theme/Color.kt` (`PrimaryCyan`, `SecondaryPurple`, `AccentGreen`, `AccentOrange`, `DarkBackground`, `CanvasBackground`).
   - Use standard springs from `AlgoTokens`: `cellTravelSpring`, `evalSpring`, `settleSpring`, `panelFadeSpring`.
2. **Single Switch Rule (`AlgorithmStepRepository.kt`)**:
   - Step generation remains pure and deterministic. Animation is a presentation-layer concern driven by step delta transitions; step data structures must remain immutable.
3. **Single Family Dispatcher (`VisualizerHost.kt`)**:
   - Do not alter family dispatching. `LINEAR_1D`, `BUFFER`, and `GRAPH_2D` keep their respective canvas containers.
4. **Compose Performance & 60 FPS Floor**:
   - Animate coordinates and signals using `drawWithContent` / `Canvas` draw calls or `Modifier.graphicsLayer` to prevent layout recomposition thrashing.
5. **Zero Regressions**:
   - All 193 unit tests in `./gradlew.bat testDebugUnitTest` must continue to pass cleanly.

---

## 5. Affected Files & Subsystem Map

| Subsystem | File Path | Scope of Changes |
|---|---|---|
| **Buffer Visualizer** | `app/src/main/java/com/example/algolens/ui/visualizer/BufferVisualizer.kt` | Redesign Stack beaker UI; add drop/ejection physics; add Queue slide transitions. |
| **Graph Tree Renderer** | `app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt` | Add node position interpolation, directional edge traversal pulses, and ripple rings. |
| **Graph Tree Visualizer** | `app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeVisualizer.kt` | Bridge step transitions with node position animatables; coordinate snap-on-scrub. |
| **Motion Tokens & Specs** | `app/src/main/java/com/example/algolens/ui/theme/Theme.kt` / `SwapFlight.kt` | Ensure shared spring tokens (`AlgoTokens`) cover buffer drop and graph signal pulses. |
| **Step Repository & Models**| `app/src/main/java/com/example/algolens/data/AlgorithmStepRepository.kt` | Verify step metadata (`activeEdge`, `pathEdges`, `swappedIndices`) cleanly exposes delta information. |
| **Unit & Visualizer Tests** | `app/src/test/java/com/example/algolens/` | Regression validation across all test suites. |

---

## 6. Verification Criteria

1. **Compilation**: Clean build with `./gradlew.bat assembleDebug` and zero new lint errors.
2. **Unit Tests**: 100% pass rate on `./gradlew.bat testDebugUnitTest` (all 193+ tests green).
3. **Stack Visual Quality**:
   - Modern, high-craft graduated beaker with clear entry mouth and capacity indicators.
   - Push drops smoothly with a subtle bottom settle; Pop lifts out and fades cleanly.
4. **Graph / Tree Animation**:
   - Heap parent-child swaps visibly glide to their new slots.
   - Traversals show a directional pulse traveling along edges from source to neighbor.
   - Dijkstra shortest path illuminates sequentially when target is discovered.
5. **Scrubber Responsiveness**:
   - Instant response when dragging the scrubber with zero animation stutter or ghost nodes.
