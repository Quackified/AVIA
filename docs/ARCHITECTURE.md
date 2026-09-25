# ARCHITECTURE.md — How Does the Code Work?

> **Workflow Role:** Canonical technical & code architecture guide ("How does the code work?"). Read before planning (`TASK.md`), executing (`IMPLEMENTATION.md`), or auditing (`REVIEW.md`).

---

## 1. High-Level Architecture & Data Flow

```
AlgoLensApp (RootStage: BOOT -> ONBOARDING -> APP)
  └── AppShell (Hoists ChatSessionManager + AuthRepository across tab switches)
        ├── BottomNavBar (HOME | EXPLORE | CHAT | PROFILE)
        ├── DashboardScreen (Catalog filter, search, featured bento, AlgoCards)
        ├── PracticeScreen (Root Explore tab — active-recall drills without redundant Back button)
        ├── ChatScreen (Simplified ChatHeader, collapsible EmptyConversationStarters, editorial prose + DoubleBezelShell attachments, multiline ChatInputDock with single-owner IME insets)
        ├── ProfileScreen (ProfileAvatar + AccountStatusCard + EditProfileSheet + Catalogue specification + Bookmarks)
        ├── SettingsScreen (Two-way SharedPreferences bindings via AppSettings)
        └── VisualizerScreen (Full-screen algorithm workspace)
              ├── rememberVisualizerScreenState(algorithm)  [@Stable state holder]
              │     └── AlgorithmStepRepository.generateStepsForAlgorithm(...)
              ├── VisualizerHeader (Back, Step counter, TIME/SPACE pills, Bookmark, Tour, Kebab)
              ├── StageLegend (Directly below VisualizerHeader in normal flow, FlowRow-wrapped)
              ├── VisualizerHost (Dispatches on spec.id.family)
              │     ├── LINEAR_1D  -> Linear1DCanvas (CellArrayVisualizer | BarVisualizer + Auxiliaries)
              │     ├── GRAPH_2D   -> GraphTreeCanvas (GraphTreeVisualizer + GraphTreeRenderer + Pan/WorldScale)
              │     └── BUFFER     -> BufferCanvas (BufferVisualizer + Live Push/Pop/Enqueue/Dequeue)
              ├── InstrumentDeck (Docked Peek/Expand Terminal: Attached Trace | State tabs as sole toggle)
              └── PlaybackRail (Scrubber with live preview, Play/Pause, Step Fwd/Back, Speed, Challenge)
```

---

## 2. Package & File Map (`app/src/main/java/com/example/algolens/`)

| Layer / Package | Key Files | Responsibility |
|---|---|---|
| **`model/`** | [`AlgorithmId.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/model/AlgorithmId.kt), [`Algorithm.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/model/Algorithm.kt), [`GraphCustomization.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/model/GraphCustomization.kt), [`model/chat/ChatMessage.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/model/chat/ChatMessage.kt) | Strongly-typed `AlgorithmId` enum (13 algorithms), `VisualizerFamily`, `AlgorithmSpec`, customization sealed types (`BufferOp`, `QueueOp`, `GraphCustomization`), and chat/recommendation models (`ChatConversation`, `ProjectConstraintBrief`, `ProjectRecommendationPayload`). |
| **`data/`** | [`AlgorithmRegistry.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/AlgorithmRegistry.kt), [`AlgorithmStepRepository.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/AlgorithmStepRepository.kt), [`AlgorithmCodeRegistry.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/AlgorithmCodeRegistry.kt), [`AppSettings.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/AppSettings.kt), [`data/chat/ChatHistoryRepository.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/chat/ChatHistoryRepository.kt), [`data/chat/ProjectAlgorithmRecommender.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/chat/ProjectAlgorithmRecommender.kt), [`data/auth/AuthRepository.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/auth/AuthRepository.kt) | `AlgorithmRegistry` maps each `AlgorithmId` to its `AlgorithmSpec`. `AlgorithmStepRepository` is the single switch (`when (algorithm.id)`). `ChatHistoryRepository` provides per-owner JSON persistence for multi-conversation history. `ProjectAlgorithmRecommender` grounds project recommendations in `AlgorithmRegistry` and `AlgorithmTheoryRepository`. `AuthRepository` defines the local profile persistence + future Google Sign-In via Firebase Auth boundary (`UnavailableFirebaseAuthRepository` / `FakeAuthRepository`, `ProfileValidator`). |
| **`ui/theme/`** | [`Color.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/theme/Color.kt), [`Theme.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/theme/Theme.kt), [`Type.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/theme/Type.kt) | Semantic color palette (`#080D14` canvas, `#38BDF8` cyan, `#10B981` emerald), `AlgoTokens` spacing/radii/springs (`AlgoTokens.Spacing.minTouchTarget = 44.dp`), and dual-typeface `AlgoType` (`AlgoSans` proportional UI/prose + bundled `JetBrainsMono` `R.font.jetbrains_mono_variable` for tabular numerals and code traces, `11sp` minimum floor). |
| **`ui/components/`** | [`Instrument.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/Instrument.kt), [`AlgoGlyphs.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/AlgoGlyphs.kt), [`WorkspaceControls.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/WorkspaceControls.kt), [`BottomNavBar.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/BottomNavBar.kt) | Core instrument primitives (`DoubleBezelShell`, `InstrumentMeter`, `InstrumentRule`, `AlgoHairline`, `Modifier.pressPhysics` with zero idle border alpha), bespoke `1.5dp` stroke `AlgoGlyphs` (`StrokeCap.Round`), and reusable control pills/toggles (`CompactIconButton` with `32dp` visual size inside a `44dp` touch target, `RailIconButton`). |
| **`ui/visualizer/`** | [`VisualizerScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/VisualizerScreen.kt), [`VisualizerScreenState.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/VisualizerScreenState.kt), [`StageLegend.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/StageLegend.kt), [`InstrumentDeck.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/InstrumentDeck.kt), [`CodeListing.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/CodeListing.kt), [`StateDeckPage.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/StateDeckPage.kt), [`GraphTreeVisualizer.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeVisualizer.kt), [`GraphTreeRenderer.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt), [`GraphBuilderOverlay.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/visualizer/GraphBuilderOverlay.kt) | Complete visualizer workspace, `StageLegend` directly below `VisualizerHeader`, `InstrumentDeck` with attached `Trace` \| `State` tabs (`26dp` visual height inside `44dp` touch target) and measured stage reservation, `FlowRow`-wrapped state inspector, and `GraphCanvasGeometry` with stable world scale (`nodeRadius >= 16f`) and shared `panOffset`. |

---

## 3. Core Architectural Invariants

### 3.1 The Single Switch Rule (`AlgorithmStepRepository`)
`AlgorithmStepRepository.generateStepsForAlgorithm(...)` has **exactly one** `when (algorithm.id)` block.
- Never branch on `algorithm.name` (display-only string).
- Unknown/unregistered specs log a warning and return `emptyList()` — never silently fall through to Bubble Sort.
- Every step generator populates `VisualizerStep` fields appropriate to its family:
  - **`LINEAR_1D`:** `array`, `elementStates`, `topPointers`, `bottomPointers`, `activeRange`, `swappedIndices`, `comparisonExpr`, `activeCodeLines`, `variables`.
  - **`BUFFER` (Stack / Queue):** `buffer` (`List<BufferItem>` with `ElementState.ACTIVE` on insert, pre-removal `ElementState.SWAPPING` step on Pop/Dequeue, `ElementState.FOUND` on Peek), `bufferLabel`, `comparisonExpr`, `variables`, `callStack`.
  - **`GRAPH_2D` (BST / Heap / BFS / DFS):** `nodes` (`List<GraphNodeState>`), `edges` (`List<GraphEdgeState>`), `activeNodeId`, `visitedNodeIds`, `buffer` (search path / frontier queue / DFS call stack), `array` (synchronized Heap array), `variables`, `callStack`.

### 3.2 Hoisted State Machine (`@Stable VisualizerScreenState`)
All mutable visualizer state lives inside `VisualizerScreenState` (`VisualizerScreenState.kt`), created and driven by `rememberVisualizerScreenState(algorithm)`:
- **Step generation `LaunchedEffect`:** Re-runs automatically whenever `algorithm`, `state.arrayData`, `state.lastAppliedSortOrder`, `state.searchTarget`, `state.bufferOps`, `state.queueOps`, `state.graphConfig`, or `state.customGraph` changes.
- **Live Stage Operations:**
  - `appendLiveStackOp(op)` and `appendLiveQueueOp(op)` append to `bufferOps` / `queueOps` and set `pendingStepAfterRegen = -1`, which resolves to `(state.steps.lastIndex - 1).coerceAtLeast(0)` so the playhead lands directly on the newly executed step.
  - Interactive Graph Builder (`onGraphModified` in `GraphTreeVisualizer`) updates `state.customGraph`, triggering real BFS/DFS step regeneration over the user's custom topology.
- **Settings Synchronization:**
  - `AppSettings.defaultPlaybackSpeedMs` (`1_000L` = `0.5x`, `600L` = `1.0x`, `300L` = `2.0x`) and `AppSettings.defaultCellScale` sync into `VisualizerScreenState`.
  - `AppSettings.autoOpenDeckOnPlay` automatically expands `deckExpanded = true` when `togglePlay()` transitions to playing.
  - `AppSettings.highContrastNodeOutlines` boosts node outline alpha and stroke width in `GraphTreeRenderer`.

### 3.3 Adding or Extending an Algorithm
To add or modify an algorithm:
1. **`model/AlgorithmId.kt`:** Define the enum constant with its `VisualizerFamily` (`LINEAR_1D`, `GRAPH_2D`, or `BUFFER`).
2. **`data/AlgorithmRegistry.kt`:** Register its `AlgorithmSpec` (`defaultInput`, `supportsCustomInput`, `isStack`, `overlays`, `auxiliaryComponents`).
3. **`data/AlgorithmStepRepository.kt`:** Implement `generateXxxSteps(...)` and wire its branch in `generateStepsForAlgorithm`.
4. **`ui/visualizer/CodeTracePane.kt` (`AlgorithmCodeRegistry`):** Provide code listings for all 4 languages (`KOTLIN`, `JAVA`, `PYTHON`, `CPP`).
5. **Unit Tests:** Verify via `.\gradlew.bat testDebugUnitTest` (`AlgorithmStepRepositoryTest`, `FeatureEnhancementsTest`, `VisualizerScreenStateTest`).
