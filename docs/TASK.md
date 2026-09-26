# TASK — Practice UI, graph editing, and visualizer header

## Goal

Plan the requested refinements before changing source code:

- Make Explore/Practice answer options moderately larger and easier to tap/read.
- Replace the segmented Practice progress meter with a continuous full-width track and proportional fill, as shown in the supplied reference; apply the same treatment to the Settings catalogue meter.
- Investigate and repair graph node interactions so BFS/DFS topology edits affect actual traversal. Remove Interactive Mode and arbitrary topology editing from BST and Heap, retaining their generated tree structure.
- Restore algorithm-name visibility in the visualizer header, move Guided Tour (`?`) into More actions, and match the Chat overflow control/menu design.

This task is planning only. The attached image supplies visual evidence, not instructions. Its relevant cue is the continuous cyan progress fill over an uninterrupted dark track; it does not authorize a new navigation structure, typography system, or quiz backend.

## Documentation and inspection basis

Read `docs/PROJECT.md`, `docs/ARCHITECTURE.md`, and `docs/DESIGN.md`, and inspected the relevant source and existing tests. Root `AGENTS.md` is absent; a hidden-file repository search found no project `AGENTS.md`. Do not invent its requirements. Documentation contains historical inconsistencies (including palette/typeface descriptions and header rules); prefer current theme tokens and actual components for implementation, honoring this request where it supersedes an old layout rule.

Findings below are static code observations, not a claim that all reported bugs have been reproduced on a device. Reproduction is the first implementation step. Existing unrelated `graphify-out` working-tree changes must be preserved.

## Relevant existing implementation

### Explore/Practice and Settings

- `ui/practice/PracticeScreen.kt` renders Explore sessions, question metadata, frozen practice snapshots, answer rows, submit/feedback, and completion. Answer rows currently use full width with `space4` horizontal/vertical padding, `bodySmall` labels, a trailing selection circle, and no explicit minimum row height. They disable selection after submission and color correct/incorrect states.
- Session progress comes from `PracticeSessionManager` in `PracticeSessionState.kt`; preserve this source and its question/completion semantics. `PracticeVisualCanvas.kt` renders separate frozen snapshots; it is not the editable graph workspace.
- `ui/components/Instrument.kt::InstrumentMeter` draws 24 separate ticks, with progress read in the draw lambda. The only current call sites are Practice and `ui/settings/SettingsScreen.kt` (Embedded catalogue, fixed 100%, 13 algorithms/52 traces). Both already request full width; the issue is segmented rendering, not simply width.

### Graph/tree pipeline

- `AlgorithmRegistry` enables `builderEnabled` for BFS and DFS, not BST or Heap; all four share `GRAPH_2D` rendering and typed telemetry modes.
- `VisualizerHost.kt::GraphTreeCanvas` supplies `onGraphModified` only when the spec enables the builder. That callback sets `VisualizerScreenState.customGraph`; the state holder's regeneration effect passes custom topology and the effective traversal start into `AlgorithmStepRepository`, then resets the playhead unless a pending target exists.
- `GraphTreeVisualizer.kt` nevertheless renders the builder toolbar for every graph/tree. When no mutation callback exists, it retains edits locally through `hasLocalEdits`, ceasing to synchronize dynamic nodes/edges with steps. This is a confirmed mismatch for BST/Heap: arbitrary drawn connections never enter their tree generators and can mask step updates.
- `GraphBuilderOverlay.kt` supports empty-space node creation, selection/tap-to-connect, drag-to-connect, edge-midpoint weight cycling (1–9), panning, and reset. Duplicate checks treat both endpoint orders as identical; newly created edges get automatic weights. There are no dedicated node/edge removal or node repositioning controls in this builder. In normal mode node taps select a node and invoke the challenge callback; they do not open an inspector.
- `GraphCanvasGeometry` in `GraphTreeRenderer.kt` supplies drawing/hit-test world transforms and stable scaling/pan. Existing tests cover geometry across deck expansion. Preserve this shared transform rather than adding competing coordinate calculations.
- BFS/DFS generators already consume custom topology, build adjacency respecting `isDirected`, and sort neighbors by ID. BFS accumulates edge weights on its discovery tree; that is not a weighted shortest-path algorithm. Highlight matching checks reversed pairs too, which needs directed-edge verification.
- Local dynamic graph state, asynchronous regeneration/reset, selection/drag references, and step-derived styling can become inconsistent while editing. These are investigation targets; do not assert a particular gesture race without reproducing it.

### Header and Chat precedent

- `VisualizerHeader.kt` weights the title against an unweighted action cluster: TIME/SPACE pills, bookmark, Guided Tour, and More. Long names are forced to one ellipsized uppercase line. Removing Help alone may not free enough space at narrow widths.
- More currently opens Theory Sheet and conditional family-aware customization. Guided Tour uses `state.showGuidedTour`; existing theory/input flags should remain the action destinations.
- `ui/chat/ChatScreen.kt::ChatHeader` uses `CompactIconButton` for More, `CardBackgroundElevated`, subtle border, and compact icon/label dropdown rows. Its menu row helper is private and has raw dimensions; reuse its visual language without copying those dimensions or its conversation actions.
- `VisualizerHeaderMenuTest` checks an unrelated Material `MoreVert` icon, while the actual UI uses `AlgoGlyphs.More`; it does not prove menu behavior or title legibility.

## Files/components likely affected

All paths below are relative to `app/src/main/java/com/example/algolens/` unless stated otherwise.

| Area | Likely files and responsibility |
| --- | --- |
| Practice | `ui/practice/PracticeScreen.kt`: answer sizing, semantics, progress presentation |
| Shared meter/settings | `ui/components/Instrument.kt`, `ui/settings/SettingsScreen.kt`: continuous meter and matching catalogue presentation |
| Tokens | `ui/theme/Theme.kt`, `ui/theme/Type.kt`: only necessary shared sizing/style tokens; `Color.kt` only if an existing semantic color cannot serve |
| Graph view/gestures | `ui/visualizer/GraphTreeVisualizer.kt`, `GraphBuilderOverlay.kt`, `GraphTreeRenderer.kt`: capability gating, viewport/gesture correctness, authoritative rendering |
| State integration | `ui/visualizer/VisualizerHost.kt`, `VisualizerScreenState.kt`: graph edit commands, regeneration and edit/playback lifecycle |
| Graph data | `data/AlgorithmStepRepository.kt`, `model/Algorithm.kt`, `model/GraphCustomization.kt`, `data/AlgorithmRegistry.kt`: only confirmed validation/telemetry/capability fixes |
| Input and guidance | `ui/visualizer/CustomizeGraphSheet.kt`, guided-tour content if its Help location/instructions change |
| Header/menu | `ui/visualizer/VisualizerHeader.kt`, `ui/components/WorkspaceControls.kt`, optional shared menu primitive in `ui/components/`; Chat changes only if extracting an identical shared primitive |
| Regression tests | Existing `PracticeBackendTest`, `AlgorithmStepRepositoryTest`, `VisualizerScreenStateTest`, `FeatureEnhancementsTest`, `ChallengeModeManagerTest`, and `VisualizerHeaderMenuTest` under `app/src/test/java/com/example/algolens/` |
| Documentation | Update relevant design/architecture descriptions after implementation and record validation in `docs/IMPLEMENTATION.md` |

## Step-by-step implementation plan

1. **Reproduce and establish expected behavior.** Capture Practice, Settings, Chat More, and visualizer headers on a narrow phone. Exercise BFS/DFS add/connect by tap and drag, edit after stepping/playing, change start node, pan, expand the deck, reset, and navigate between algorithms. Record actual failures and inspect directed/weighted examples. Repeat on BST and Heap to document the cosmetic-edit mismatch.
2. **Enlarge Practice options.** Use a tokenized minimum height of approximately 48–52dp and increase internal padding from 8dp toward 12dp, with readable body typography. Let multiline labels increase height; keep the trailing indicator distinct and the entire row selectable. Add appropriate selected/disabled accessibility semantics and existing press feedback. Keep options/submit reachable by scrolling on small displays and large font scales.
3. **Make the meter continuous.** Replace the tick drawing with a rounded, uninterrupted neutral track and cyan fill spanning the clamped progress fraction. Use shared tokenized thickness/radius, retain draw-time progress reading, and expose determinate progress semantics. Both current consumers use the same component. Preserve Practice labels/counts and Settings' fixed catalogue meaning. Verify 0%, partial, and 100% without gaps or overflowing ends; follow layout direction deliberately. Do not change the playback Slider.
4. **Gate editing by capability.** Pass `spec.builderEnabled` explicitly into the shared graph view and gesture layer. Hide builder activation, topology reset, weight editing, and builder banner for BST/Heap; guard mutation handlers as well as UI. Keep pan/Center View, generated counts, challenge node taps, BST input/search, Heap input/order and Tree/Array views. Frozen Practice snapshots remain noninteractive.
5. **Repair the graph edit lifecycle.** Keep topology authoritative in the hoisted state holder and step styling authoritative in generated steps. Route edits through a state mutator that pauses playback, invalidates stale challenge/scrub state appropriately, and regenerates from validated topology with a deterministic reset to the beginning. Use only transient selection/drag/viewport state locally; clear stale IDs on reset, algorithm change and removed topology. Verify that a committed edge survives regeneration and changes reachability when the start component can reach it. Retain safe fallback when a configured start no longer exists.
6. **Define meaningful graph operations.** Keep tap/drag connection and add-node actions; provide clear selection feedback and concise instructions. Add selected node/edge deletion and a clear start-node selection route, using the existing traversal sheet where practical. Deleting a node removes incident edges. Separate topology reset from Center View. Do not overload the same drag with both moving and connecting; node repositioning or new directed-edge authoring controls are optional follow-up scope unless reproduction shows they are required. Validate unique IDs, endpoint existence, duplicate-edge policy, and self-loop policy consistently before traversal.
7. **Correct graph semantics where investigation confirms defects.** Preserve deterministic BFS queue and DFS stack/visit order. Test directed-edge rendering/highlighting independently from undirected duplicate handling. Keep weights as explicit traversal-edge/path annotations, clearly distinguishing BFS hop distance from accumulated discovery-path weight; update affected variables/descriptions/code mappings together if their meaning changes. Do not suggest weight cycling changes BFS/DFS visit order or yields weighted shortest paths. Preserve disconnected nodes as visible but unreached and state this clearly at completion.
8. **Rework header hierarchy and More.** Remove the separate Help button; add a Guided Tour menu row that dismisses More before opening the existing tour. Match Chat's compact More container, elevated popup, typography, icon spacing and borders using tokens and `AlgoGlyphs`. Prefer a shared primitive if it avoids duplicated styling. Keep Theory, customization, bookmark and complexity visibility setting. Reserve the top row for Back, a legible algorithm title, bookmark and More; put complexity readouts in a compact secondary metadata row when enabled so they cannot squeeze the name. Integrate step metadata/mode controls without colliding with stage/deck reservations. Allow title wrapping when needed rather than shrinking typography below the design floor. Update obsolete inline comments that require inline Help/no wrapping.
9. **Verify and document.** Add focused behavioral regressions for confirmed graph failures, run required build/test/lint checks, inspect the affected UI in a bounded screenshot pass, fix observed issues, and confirm once. Update relevant documentation to describe continuous meters and the new header/tree capability behavior. Run `graphify update .` only after source changes during implementation, preserving unrelated existing output changes.

## Things that must NOT change

- No source changes during this planning task; only `docs/TASK.md` is authorized now.
- Preserve offline operation, the 13-algorithm catalogue, persistent preferences/bookmarks, Chat behavior, and Explore session scoring/question generation.
- Keep the single step-generator algorithm switch and single family renderer dispatcher. Do not branch on display names or add algorithm-specific decisions to `VisualizerScreen.kt`.
- Keep mutable playback/deck/domain state hoisted; maintain source-code trace, variables, frontier and canvas synchronization across all four trace languages.
- Preserve dark theme, existing semantic palette, proportional UI text/monospace telemetry split, DoubleBezelShell, AlgoGlyphs, press physics, and minimum 44dp touch targets. The screenshot does not override these rules.
- Tree structure remains generated by BST/Heap logic, with existing legal customization. Removing Interactive Mode does not remove challenge predictions, input sheets, panning, or heap array visualization.
- Do not introduce Dijkstra, arbitrary tree editing, graph persistence, accounts, new dependencies, or a general graph-editor redesign as incidental scope.

## Potential risks or edge cases

- Regeneration during a drag can invalidate selection or replace playback styling; reset/edit/challenge transitions need atomic, predictable behavior.
- Reverse directed edges, cycles, disconnected components, dangling endpoints, duplicate IDs, ID normalization, and label generation beyond A–Z need deliberate handling.
- Reset currently uses empty nodes as the signal to restore canonical topology. Deleting the final node must have an explicit outcome (restore defaults with clear feedback or reject that deletion), not silently conflate an empty custom graph with reset.
- Automatic edge weights and a weight-sum label can imply unsupported shortest-path behavior. Weight semantics must match algorithm theory and trace content.
- Node/weight hit targets are currently pixel thresholds; verify density-aware touch behavior, overlap priority, and shared transform round trips after panning/deck resizing.
- Larger options and an extra header metadata row reduce canvas space. Check short screens, long names/labels, large fonts, system insets, and both deck heights without shrinking touch targets.
- Shared component extraction must retain Chat actions and dismissal behavior. A documentation assertion or icon-only unit test cannot establish UI correctness.

## Verification/testing requirements

Implementation must pass `.\gradlew.bat assembleDebug`, `.\gradlew.bat testDebugUnitTest`, and `.\gradlew.bat lintDebug`; investigate failures and distinguish existing failures from introduced ones.

- **Graph behavioral tests:** custom add/connect changes reachable traversal; deterministic neighbor order; directed and undirected edges; cycles without repeated discovery; disconnected and single-node graphs; valid start/fallback; reset and node deletion; invalid endpoints/IDs; weight changes preserve BFS/DFS visit order; tree specs cannot enable topology edits. Exercise the state mutator directly in unit tests and regeneration integration on-device (the `LaunchedEffect` is not exercised by merely assigning state in a unit test).
- **Telemetry regressions:** BFS queue/DFS stack empties at completion; node IDs stay separate from formatted buffer labels; tree highlights and Heap array values stay in sync; four-language mappings remain valid after any generator changes.
- **Practice:** preserve selection, submission lock, correctness feedback, next-question/completion/restart behavior. Check option tap size and wrapping on a small phone with increased font scale.
- **Meters:** inspect 0/60/100% track/fill, accessible percentage semantics, and Settings' continuous fully filled catalogue meter. No backend tests are needed solely for paint changes.
- **Header/menu:** long names (Binary Search Tree, Breadth-First Search, Depth-First Search), complexity badges on/off, bookmark states, all families, narrow width and large text. Verify Guided Tour, Theory and family customization each dismiss the menu and open the intended destination; Back/outside tap dismisses More. Replace misleading icon-only coverage with meaningful behavior coverage if a suitable harness exists; otherwise record device verification rather than adding a new test framework for this menu alone.
- **Device interaction:** tap/drag connections, edge selection/removal, pan/Center View, deck expansion, reset, edit during playback/challenge, algorithm changes. Confirm BST/Heap cannot add/connect/edit edges while normal playback, input customization and node predictions work.

Planning completion: this document only. No build, runtime reproduction, or source-change verification is claimed at this stage.
