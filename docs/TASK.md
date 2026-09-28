# TASK — Unified graph and tree editor

## Goal

Implement `docs/UNIFIED_GRAPH_EDITOR_PROMPT.md` within the existing Kotlin/Jetpack Compose architecture. Share one `GraphCanvasEngine` between embedded and fullscreen surfaces, with algorithm-specific capabilities for BFS, DFS, Dijkstra, BST, and Heap. Separate editable topology, user coordinates, viewport state, and execution highlights so editing and playback remain synchronized.

This is an implementation plan only. No source changes, builds, device validation, or graphify updates were performed for this planning task. Findings describe the current working tree, including existing uncommitted work, rather than assuming the mission prompt's description is current.

### Design approach

Use Impeccable's **shape** method to resolve interaction contracts before implementation. This is an **Operate** surface: learners edit structures and follow execution. Preserve AVIA's dark instrument workspace, canvas well, semantic accents, typography, and shared controls. Accessibility and responsive checks belong in implementation verification; polish follows functional correctness, not this planning pass.

The Impeccable context launcher could not initialize its external cache. Context was inspected directly through `docs/DESIGN.md`, theme code, and the visualizer. No PRODUCT.md or relevant graph screenshot fixtures were found in the project search; no Figma source was supplied. Do not claim verified Figma parity. Current theme code takes precedence where the design document is stale, including its canvas typography sizes.

## Relevant existing implementation

Production paths below are relative to `app/src/main/java/com/example/algolens/`.

| Area | Current implementation and consequence |
| --- | --- |
| Registry | `model/AlgorithmId.kt` defines `AlgorithmSpec`, graph family routing, and `GraphTelemetryMode`. `data/AlgorithmRegistry.kt` enables `builderEnabled` only for BFS/DFS/Dijkstra. BST/Heap are currently read-only, contrary to the prompt's claim that they already inherit visible editor tools. |
| Host/state | `ui/visualizer/VisualizerHost.kt` wires graph edits, endpoints, and challenge taps. It converts an empty edited graph to `null`, restoring defaults. `VisualizerScreenState.kt` owns customization and regenerates steps on semantic input changes, restarting at zero. Coordinates have no independent store; moves currently use the topology callback and cause regeneration. |
| Composition | `GraphTreeVisualizer.kt` owns local nodes/edges, edit mode, selection, tool, and pan. It copies execution steps into local data only when editing is off, allowing stale highlights during editing. Embedded/fullscreen wiring is duplicated. |
| Tools | `GraphTool`, `GraphBuilderGestures`, `GraphFloatingToolbar`, banner, and header are all in `GraphBuilderOverlay.kt`; the prompt's standalone gesture/toolbar files do not exist. Toolbar rendering iterates every tool. Gestures implement pan, movement, tap/drag linking, add/delete, endpoints, and weight cycling 1–9 without capabilities. |
| Geometry | `GraphTreeRenderer.kt` owns rendering and `GraphCanvasGeometry`: pan/inverse mapping, node-spacing expansion, and remembered maximum canvas height. Bounds/centering are recomputed from nodes, so edits can shift the apparent world. Zoom and Fit are absent; gesture bounds and hit radii contain fixed floats. |
| BST | `data/AlgorithmStepRepository.kt` already emits a fully pre-built `INITIALIZING` frame. It builds from values, including duplicates with IDs such as `42#1`, placing equal keys to the right. Custom graphs are ignored. Rebuilding duplicate IDs after deletion can change identity. Skewed-tree layout already expands spacing. |
| Heap | The repository emits heap construction/sort frames with index-string node IDs, synchronized arrays, max-heap for ASC and min-heap for DESC. Edges follow `2i+1`/`2i+2`, but overload `weight` with child indices. Coordinates are regenerated in each frame. |
| Traversals | BFS/DFS/Dijkstra already accept custom graphs, retain coordinates/weights, reset transient highlights, and support target searches. Invalid starts fall back to the first node; invalid or same-as-start targets are dropped. Empty custom graphs restore canonical data. BFS/DFS currently share weighted canonical edges. |
| Preview | `GraphSearch`, at the bottom of the repository file, supplies adjacency, BFS, DFS, and `shortestPath`. The last uses BFS/fewest hops, so the current preview can disagree with Dijkstra's least-cost route. |
| Telemetry/fullscreen | Heap array/role and frontier/visited/weight strips already exist in `GraphTreeVisualizer.kt`. Weight totals sum all highlighted edges, which need not form one path. Fullscreen already uses an edge-to-edge Dialog plus status-bar, cutout, and navigation-bar padding. Preserve these and fix usable bounds rather than duplicating padding. |
| Inputs/extensions | `model/CustomizeInput.kt` defines `GraphCustomization`. `CustomizeGraphSheet.kt` accepts up to 15 BST/Heap values in 1..999, including duplicates. `WeightBadgeOverlay.kt` is already a no-op because the renderer owns badges; comments are stale. `GraphVisualizer.kt` is a separate lightweight renderer, not the active editor. |

Existing tests cover traversal endpoints, Dijkstra execution, fewest-hop preview, heap/BST telemetry, playback state, and 15-node skewed/duplicate BST geometry. The instrumentation screenshot test only checks a nonblank app launch; it is not a graph editor regression suite. `docs/HANDOVER_GRAPH_EDITOR.md` contains historical pending work that is already implemented, so source code is the primary evidence.

## Files/components likely affected

- **Models/registry:** new `model/GraphCapabilityProfile.kt`; existing `model/AlgorithmId.kt`, `data/AlgorithmRegistry.kt`, and potentially `model/CustomizeInput.kt` for stable structured-edit data.
- **Mutations/generation:** `data/AlgorithmStepRepository.kt`; focused new pure helpers for graph/tree validation, identity, mutation, and layout. Keep the existing public generation entry point with compatible default arguments.
- **State/wiring:** `ui/visualizer/VisualizerScreenState.kt`, `VisualizerHost.kt`, and graph customization dispatch in `VisualizerScreen.kt`.
- **Shared canvas:** new `ui/visualizer/GraphCanvasEngine.kt` and viewport/geometry helpers as needed; existing `GraphTreeVisualizer.kt`, `GraphTreeRenderer.kt`, `GraphBuilderOverlay.kt`. Extract `GraphFloatingToolbar.kt` and `GraphBuilderGestures.kt` rather than introducing parallel implementations.
- **Input/telemetry:** `CustomizeGraphSheet.kt`, a reusable integer-entry dialog, and optional extraction of existing telemetry strips. Extend `VisualizerState.kt` only if typed path/role metadata is needed; avoid a broad step-model migration.
- **Compatibility:** `WeightBadgeOverlay.kt` and its tests; `AlgorithmCodeRegistry.kt` only if educational phases/code-line mappings actually change.
- **Tests:** existing `AlgorithmStepRepositoryTest.kt`, `VisualizerScreenStateTest.kt`, `FeatureEnhancementsTest.kt`, `VisualizerOverlayTest.kt`, `data/GraphSearchTest.kt`, `data/DijkstraStepsTest.kt`; new focused editor/geometry and Compose graph interaction tests.
- **Design references:** `ui/theme/Theme.kt`, `Type.kt`, `Color.kt`, shared buttons/press feedback, and `docs/DESIGN.md`. Reuse these rather than redesigning global tokens.

## Step-by-step implementation plan

### 1. Introduce validated capability profiles

Move `GraphTool` to the model layer, adding tooltips, placement modes, and `GraphCapabilityProfile`. Add the profile to `AlgorithmSpec` in its actual definition and register stable profiles in `AlgorithmRegistry`. Remove independent `builderEnabled` decisions, or derive that property during migration.

| Algorithm | Allowed tools | Structure-specific behavior |
| --- | --- | --- |
| Dijkstra | Move, Add, Link, Weight, Endpoints, Delete | Freeform; positive weights 1..99; start/target |
| BFS / DFS | Move, Add, Link, Endpoints, Delete | Freeform; unweighted execution/presentation; start/target |
| BST | Move, Add, Delete | Keyed insertion, actual BST deletion, positional nudge; retain Search Key |
| Heap | Move, Add, Delete | Value push, root extraction/tail removal, positional nudge; retain sort order |
| Read-only | No edit tools | Fit, Center, and Fullscreen remain available |

Validate consistency between flags, allowed tools, and placement modes. Use deterministic tool ordering, enforce capabilities in mutation handlers as well as UI, and reset an invalid active tool on profile changes. Distinguish directed tree edges from permission to create directed links. Preserve existing direction; general-graph creation remains undirected unless the profile explicitly supports directed editing.

### 2. Establish authoritative state and reset contracts

Retain screen-state ownership; no new ViewModel/store architecture is needed. Add `userCustomCoordinates: Map<String, Offset>` in world units and shared viewport state. Separate semantic inputs/topology from execution snapshots and transient selection/drag state.

- Decorate current step nodes with coordinate overrides by ID, retaining current values, colors, and edge highlights. Heap labels must still change during swaps. Remove the frozen local-graph behavior while editing.
- Coordinate changes do not regenerate steps, reset playback, or invalidate predictions. Pan/zoom are also presentation-only.
- Topology, keyed values, weights, and endpoint edits are atomic semantic changes: pause playback, regenerate once at commit, restart at step zero, and clear stale challenge/scrub state using existing methods. Never regenerate on every drag event.
- Distinguish default input (`null`) from explicitly empty input. Delete-last leaves an empty graph/tree; explicit Reset restores defaults. Remove host/repository/default-array fallbacks that conflate these states. Empty structures emit a neutral empty frame and permit Add.
- Separate explicit endpoint badges from execution's default start. Clearing START must not immediately recreate its badge through fallback. For deletion of an assigned endpoint, follow the prompt's fallback to the first remaining node; empty graphs have no endpoints. Handle a resulting start=target as a valid zero-length path consistently in preview and generators.
- Remove coordinates, selection, pending links, and dialog references for deleted IDs. Input replacement clears stale identities/overrides. Reset clears editor overrides, endpoints, and viewport. Playback and fullscreen retain shared state. Persistence is within the current visualizer session; disk/process-death persistence is out of scope.

### 3. Implement pure structure-aware mutations

Dispatch tested operations by placement mode rather than algorithm-name conditions in gestures.

- **General graphs:** stable unique IDs, next available A–Z labels followed by collision-safe N labels, selected-node auto-link on Add, node/incident-edge deletion, and individual edge deletion. Reject self-links and duplicate edges according to direction.
- **BST:** retain duplicates, equal-goes-right ordering, 15-node limit, and 1..999 validation. Allocate identities that survive insertion/deletion instead of recalculating occurrence suffixes. Implement leaf, one-child, and two-child deletion using a consistent successor policy. Preserve unaffected branches and surviving identities: filtering a value out of the insertion sequence is not equivalent to BST deletion. Keep customization synchronized using topology-preserving data/serialization.
- **Heap:** preserve index IDs as slot identities; nudges follow slots, not values during swaps. Push through the heap input model and preserve the existing educational build/sort pipeline. Permit tail removal and root extraction with direction-appropriate repair; explain supported operations for interior-node taps and reject edge deletion. Mutations use authoritative editable input/heap state, never a scrubbed historical sort frame.
- Lay out new/unmodified tree nodes deterministically while preserving manual coordinate overrides. Animate automatic layout changes only, respecting reduced motion. Nudges never alter parent-child relations.

### 4. Integrate generators and correct preview semantics

Thread optional custom topology/coordinates through `generateStepsForAlgorithm`, BST, and Heap with compatible defaults. BST validates/uses edited topology and retains its already-correct fully built step zero. Heap derives edges from indices in every frame and applies custom slot coordinates.

Centralize sanitation for unique IDs, valid endpoints, dangling edges, explicit emptiness, and weighted input. Preserve Dijkstra weights/coordinates. BFS/DFS traverse and preview unweighted edges; do not mutate shared canonical data merely to hide badges. Remove child indices from Heap edge weights and express roles in telemetry.

Preserve `GraphSearch.shortestPath`'s existing fewest-hop contract for BFS/DFS and tests. Add a weighted helper or explicit mode for Dijkstra with deterministic ties. Preview/execution must agree on direction, endpoints, unreachable cases, and positive weights. Distinguish DFS's actual search route from a shortest-hop preview. Recompute preview on semantic changes, not movement or changing step highlights.

### 5. Build shared viewport geometry and canvas engine

Extract the reusable `GraphCanvasEngine` composition for embedded and fullscreen modes. Keep `GraphTreeRenderer` focused on drawing and use one geometry contract for drawing, hit testing, and accessibility overlays.

- Define uniform world-to-screen and inverse transforms with density-aware measurements. Stabilize the world basis through gestures/playback instead of continuously re-centering around moving nodes.
- Add 0.5x–2.5x zoom and pinch-centroid anchoring. Use transform gesture handling with explicit arbitration against single-pointer actions. A second pointer or cancellation must not commit accidental adds/links/deletions.
- Center View translates the center of mass to the usable viewport center while retaining zoom. Fit includes all nodes and visible badges/halos with 24dp margins, excluding header, toolbar, and telemetry. Use a fit-derived base scale with bounded relative user zoom so large/skewed trees can fit without violating the zoom range; document and test this convention.
- Apply transforms uniformly to nodes, edges, arrows, labels, weight pills, endpoint halos, preview, and rubber-band links. Re-render text at effective scale for crispness. Keep node hit radius in screen units at `max(28dp, visible node radius + 12dp)`.
- Replace fixed pan/world clamps with bounds accommodating the existing 15-node skewed layout. Handle empty, single-node, and zero-size geometry safely. Resizing/dock expansion preserve the viewed world location; explicit Fit controls framing.

### 6. Rebuild profile-driven tools and accessible interactions

Retain a compact edit-mode/status header and one tool row; remove repeated actions. Keep viewport utilities accessible in read-only mode. Use `AlgoTokens.radiusMd`, `DarkBackground` at 0.94 alpha, `strokeThin`, cyan border at 0.35 alpha, and `CyanSubtle`/`PrimaryCyan` selection. Reuse AlgoGlyphs, shared typography, and press feedback.

- MOVE selects/inspects or submits a valid challenge prediction; node drag nudges and background drag pans. Disable semantic edits during an in-flight challenge, or explicitly leave that interaction first; predictions must not accidentally mutate topology.
- ADD routes to freeform placement or “Enter node key” / “Enter heap value” dialogs. Reuse integer validation, show inline errors, revalidate capacity on confirmation, and make Cancel side-effect free.
- LINK supports tap–tap and drag with dashed cyan feedback and one validated commit.
- WEIGHT cycles 1→2→3→5→8→1; a non-cycle value advances to 1. Long-press permits direct 1..99 input, with an accessible equivalent action that does not require long-press.
- ENDPOINTS uses explicit START/TARGET text, green/pink halos, profile-specific hints, and the state contracts above.
- DELETE uses structure-specific operations. Edge hit testing includes the segment, not only its midpoint badge.
- Expose tool selection, node/edge descriptions, and actions through Compose semantics rather than an inaccessible raw Canvas. Retain at least existing 44dp control hit areas, preferably 48dp where feasible. Support font scaling, keyboard/focus input, and reduced motion; never communicate state by color alone.

### 7. Consolidate fullscreen and telemetry

Reuse engine/state/tool configuration; retain existing system-bar/cutout padding exactly once. Add a bounded CanvasBackground header Surface with title, counts, and Exit. Reserve real space for toolbar and telemetry so Fit and hit testing exclude obscured areas. Keep Exit reachable at narrow widths, large fonts, landscape, and while the entry IME is open. Back closes entry UI before fullscreen without losing edits.

Reuse existing strips. Resolve heap cells by slot ID rather than incidental list order; synchronize indices, P/L/R roles, values, and ACTIVE/SWAPPING/SORTED states with the same frame. Keep queue/stack/priority ordering and `BufferItem.nodeId` click targets. Preserve visited order explicitly. Show path weight only for an actual weighted path, distinguish preview from execution, and never label an exploration-tree sum as path cost. BFS/DFS and trees have no misleading weight totals. An empty heap must not display a populated `[0..0]` index range.

### 8. Validate and document

Run the checks below, correct related failures, and record actual results. Update nearby stale comments for changed contracts. After source work and validation, run `graphify update .`, preserving unrelated existing output changes. Report unavailability rather than claiming that check passed. Do not run it merely for this planning task.

## Things that must NOT change

- Preserve existing uncommitted changes, unrelated files, and unrelated graphify artifacts.
- No redesign of navigation, Explore, Chat, onboarding, settings, or the overall shell; no new algorithms, undo/redo framework, backend persistence, or dependency migration.
- Keep registry-driven family routing, typed telemetry, `VisualizerStep` snapshots, repository generation, and screen-state playback ownership.
- Preserve other algorithms, search/sort correctness, code trace/call-stack synchronization, challenge scoring, and Stack/Queue regeneration policies.
- Preserve BST's fully built first frame and duplicate semantics, Heap ASC/DESC behavior, and current numeric input limits. Never permit arbitrary tree links, tree edge weights, or pathfinding endpoints for BST/Heap.
- Preserve theme tokens, typeface roles, semantic colors, inset safety, and accessibility. Do not revive the no-op weight overlay or add another competing graph renderer.

## Potential risks or edge cases

- Committing execution snapshots can save transient heap values, visited states, or edge highlights; edits must use authoritative input.
- Duplicate BST keys, successor replacement, deleted/reused labels, and heap slot/value identity can attach coordinates or predictions to the wrong node.
- Content-derived geometry, simultaneous pointers, resizing, and dock expansion can cause jumps or unintended edits unless render/hit-test transforms agree.
- The prompt's generic DELETE description conflicts with tree invariants; structure-specific deletion takes precedence. Root extraction must not replace the heap-sort teaching pipeline.
- Explicit clearing differs from endpoint deletion fallback; isolated nodes and start=target require consistent semantics.
- Large/skewed structures may become small when fitted. Fit provides visibility, zoom provides inspection, and accessible actions provide alternatives. Manual overlap must not silently undo a user's nudge.
- Dijkstra's least-cost route may have more hops. Direction and duplicate-edge rules must agree across mutation, preview, highlighting, and cost calculation.
- Existing tests/docs contain stale weighted-BFS, geometry, and reset-on-empty assumptions. Update only assertions invalidated by this request while retaining correctness coverage.

## Verification/testing requirements

### Automated coverage

- Profiles: all five tool sets, read-only utilities, stable order, and rejected illegal mutations when invoked directly.
- State: coordinates survive stepping/play/scrub/fullscreen; moves do not regenerate/reset; semantic edits regenerate once, pause, and clear stale predictions; explicit reset versus empty edit; replacement clears obsolete IDs.
- BST: fully built step zero, found/missing searches, duplicate identity, insertion, leaf/one-child/two-child/root deletion, ordering, surviving coordinates, capacity, and skewed layouts.
- Heap: index-derived edges every frame, push/root extraction/tail removal, rejected interior/edge deletion, duplicates, both sort orders, slot coordinates through swaps, no weight badges, array/tree parity, empty/single-node states.
- Graphs: coordinate/weight retention, unweighted BFS/DFS, endpoint delete/clear/fallback, all nodes deleted, unique labels after Z/deletion, self/duplicate/dangling/directed edges, invalid numeric input, and weight limits.
- Preview: a least-cost route differing from fewest hops, deterministic ties, unreachable/same-node endpoints, directionality, and cost of the actual path rather than every highlighted edge.
- Geometry: inverse round trips at multiple densities/scales, pinch anchor, zoom bounds, Fit margins, center of mass, resize/dock behavior, 15-node skewed tree, empty/zero-size bounds, and screen-space hit radii.

### Compose/device checks

Use existing Compose test dependencies for focused graph interaction tests: filtering, dialogs/errors/cancel, move/pan/pinch arbitration, tap/drag linking, weight entry, endpoints, deletion, reset, and fullscreen shared state.

Inspect compact portrait, landscape, large font scale, larger/resizable windows, cutout/punch-hole, and gesture/three-button navigation. Check IME, TalkBack/focus actions, touch targets, narrow toolbar overflow, text at zoom extremes, dock expansion, and unobscured Exit/Fit. Perform one batched visual/interaction review and one confirmation pass after fixes; explicitly record unavailable checks.

### Required commands after implementation

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
graphify update .
```

Compilation and unit tests must pass. Lint must introduce no new violations against the existing baseline. Instrumentation requires an available emulator/device; distinguish environmental blockers from failures. Review the final diff for scope and preservation of pre-existing work.
