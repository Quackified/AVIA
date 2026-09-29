# ARCHITECTURE.md — AVIA Technical Architecture Guide

> **Workflow Role:** Canonical technical & code architecture guide ("How does the code work?"). Read before planning (`TASK.md`), executing (`IMPLEMENTATION.md`), or auditing (`REVIEW.md`).

---

## 1. High-Level Architecture & Data Flow

```
AviaApp (RootStage: BOOT -> ONBOARDING -> APP)
  └── AppShell (Hoists ChatSessionManager + AuthRepository across tab switches)
        ├── BottomNavBar (HOME | EXPLORE | CHAT | PROFILE)
        ├── DashboardScreen (Catalog filter, search, featured bento, AlgorithmCards)
        ├── ExploreCatalogScreen / PracticeScreen (Predict Step Arena + Practice Quiz tracks)
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

## 2. Package & File Map (`app/src/main/java/com/avia/`)

| Layer / Package | Key Files | Responsibility |
|---|---|---|
| **`model/`** | [`AlgorithmId.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/model/AlgorithmId.kt), [`Algorithm.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/model/Algorithm.kt), [`GraphCustomization.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/model/GraphCustomization.kt), [`model/chat/ChatMessage.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/model/chat/ChatMessage.kt) | Strongly-typed `AlgorithmId` enum (14 algorithms including Dijkstra), `VisualizerFamily`, `AlgorithmSpec`, customization sealed types (`BufferOp`, `QueueOp`), and chat/recommendation models (`ChatConversation`, `ProjectConstraintBrief`, `ProjectRecommendationPayload`). |
| **`data/auth/` & `data/firebase/`** | [`GoogleSignInHelper.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/data/auth/GoogleSignInHelper.kt), [`FirebaseAuthRepository.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/data/firebase/FirebaseAuthRepository.kt), [`FirestoreSyncEngine.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/data/firebase/FirestoreSyncEngine.kt), [`AnalyticsTracker.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/data/firebase/AnalyticsTracker.kt) | Production Firebase identity & cloud synchronization infrastructure: AndroidX Credential Manager Google Sign-In with One-Tap (`GoogleSignInHelper`), anonymous-to-Google account linking (`FirebaseAuthRepository`), offline-first Cloud Firestore bookmark and progress synchronization (`FirestoreSyncEngine`), and analytics tracking with seamless offline fallbacks. |
| **`ui/theme/`** | [`Color.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/theme/Color.kt), [`Theme.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/theme/Theme.kt), [`Type.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/theme/Type.kt) | Semantic color palette (`#080D14` canvas, `#00E5FF` cyan, `#00E676` emerald), `AviaTokens` (`AlgoTokens`) spacing/radii/springs (`AviaTokens.Spacing.minTouchTarget = 44.dp`), and `AviaType` (`AlgoType`) typography (`JetBrainsMono` variable font). |
| **`ui/components/`** | [`Instrument.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/components/Instrument.kt), [`AviaGlyphs.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/components/AviaGlyphs.kt), [`AlgorithmCard.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/components/AlgorithmCard.kt), [`WorkspaceControls.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/components/WorkspaceControls.kt), [`BottomNavBar.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/components/BottomNavBar.kt) | Core instrument primitives (`DoubleBezelShell`, `InstrumentMeter`, `InstrumentRule`), bespoke `1.5dp` stroke `AviaGlyphs` (`StrokeCap.Round`), `AlgorithmCard`, and reusable control pills/toggles. |
| **`ui/practice/`** | [`ExploreCatalogScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/practice/ExploreCatalogScreen.kt), [`PredictStepArena.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/practice/PredictStepArena.kt), [`PracticeScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/practice/PracticeScreen.kt) | Dedicated Explore Hub with mode toggle between Predict Step Arena and Practice Quiz tracks. |
| **`ui/visualizer/`** | [`VisualizerScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/visualizer/VisualizerScreen.kt), [`VisualizerScreenState.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/visualizer/VisualizerScreenState.kt), [`ChallengeModeManager.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/visualizer/ChallengeModeManager.kt), [`StageLegend.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/visualizer/StageLegend.kt), [`InstrumentDeck.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/visualizer/InstrumentDeck.kt), [`GraphTreeVisualizer.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/avia/ui/visualizer/GraphTreeVisualizer.kt) | Complete visualizer workspace with `ChallengeModeManager` driving step predictions (swaps, comparisons, queue push/pop, tree visits, and Dijkstra extract-min / edge relaxations). |

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
