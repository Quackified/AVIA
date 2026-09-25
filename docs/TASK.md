# TASK.md — Active Task Specification

> **Workflow Owner:** ChatGPT (Planner) → Antigravity (Executor) → Codex/Astra (Reviewer)
> **Status:** ✅ COMPLETED (Logged in [`docs/IMPLEMENTATION.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/IMPLEMENTATION.md))

---

## Current Handover Task: UI Critique Completion & Data Structure / Graph Algorithm Logic Fixes

Finish the remaining UI critique items and broken/semi-functional data structure & graph algorithm logic in AVIA (`AlgoLens`) while strictly adhering to [`docs/DESIGN.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/DESIGN.md) ("Atmospheric Minimalism & High-Craft Dark UI", `#080D14` deep slate, `#38BDF8` cyan, `#10B981` emerald, `DoubleBezelShell`, `AlgoGlyphs`, and `Modifier.pressPhysics`):

### 1. Wire `SettingsScreen.kt` to `AppSettings` (`ui/settings/SettingsScreen.kt` & `data/AppSettings.kt`)
- Replace dead local `remember` state (`selectedLanguage`, `speedSlider`, `highContrast`, `autoPlay`, `showComplexity`) with two-way reactive bindings in `AppSettings` (backed by `SharedPreferences` so preferences persist across cold launches).
- Replace non-functional mock toggles with real workspace settings (`Default Trace Language`, `Default Playback Speed`, `High-Contrast Node Outlines`, `Auto-Open Deck on Play`, `Inline Complexity Readouts`, `Default Cell Size`, `Tactile Haptic Feedback`, and `Clear Saved Data`).

### 2. Wire Real Bookmarks in `VisualizerHeader.kt` & `ProfileScreen.kt`
- Add `bookmarkedAlgorithmIds: Set<String>` and `toggleBookmark(algorithmId: String)` to `AppSettings`.
- Add a `Bookmark` (`AlgoGlyphs.Bookmark` / `AlgoGlyphs.BookmarkFilled`) toggle button in `VisualizerHeader.kt`.
- Update `ProfileScreen.kt` to render the real bookmarked algorithms from `AppSettings.bookmarkedAlgorithmIds` (replacing the hardcoded `MERGE_SORT, QUICK_SORT, BFS` list) with a clean `DoubleBezelShell` empty state CTA (`BROWSE CATALOG`) when none are bookmarked.

### 3. Enforce `44.dp` Minimum Touch Targets & `AlgoGlyphs` Consistency
- Expand all sub-44dp interactive hit areas (`OnboardingScreen.kt` SKIP button, `SettingsScreen.kt` back button, `PracticeScreen.kt` back button, `ProfileScreen.kt` settings button, and `InstrumentDeck.kt` / `CodeListing.kt` / `StateDeckPage.kt` collapse/expand icons) to `AlgoTokens.Spacing.minTouchTarget` (`44.dp`).
- Replace remaining `Icons.Default.*` Material icons in `BottomNavBar.kt` and `SettingsScreen.kt` with `1.5.dp` stroke `AlgoGlyphs` (`StrokeCap.Round`) and ensure `Modifier.pressPhysics` is applied across interactive cards/buttons.

### 4. Fix Broken & Semi-Functional Stack, Queue, BST, Heap, BFS, and DFS Logic
- **Stack & Queue:**
  - Fix `VisualizerScreenState` initializing `bufferOps` and `queueOps` to `emptyList()` (which overrode `defaultStackOps()` / `defaultQueueOps()` and caused Stack and Queue to open empty with 0 operations).
  - Emit a pre-removal `ElementState.SWAPPING` highlight step before removing elements on `Pop` and `Dequeue`.
  - Fix live stage buttons (`+ PUSH`, `POP`, `PEEK`, `+ ENQ`, `DEQ`) so they jump directly to the newly appended operation step.
  - Populate `comparisonExpr`, `variables`, and `callStack` on every Stack and Queue step.
- **Binary Search Tree (BST):**
  - Emit incremental tree construction (`INSERTING`) steps followed by `SEARCHING` steps for `searchKey`.
  - Normalize custom BST layout coordinates in `buildBstFromValues` using in-order rank (`x`) and depth (`y`) so custom or skewed trees never clip or overlap.
  - Populate `visitedNodeIds`, `buffer` (`BST SEARCH PATH`), `variables`, and `callStack` on every BST step.
  - Pass the BST's actual values (`defaultBstValues` or `ForBst.values`) to `CustomizeGraphSheet` instead of `state.arrayData`.
- **Heap:**
  - Read `GraphCustomization.ForHeap.values` in `rememberVisualizerScreenState` and `VisualizerScreen.kt` so custom Heap inputs are applied instead of ignored.
  - Dynamically calculate level/slot `(x, y)` coordinates in `generateHeapSteps` for any heap size (`1..15`) and populate `variables` and `callStack`.
- **BFS & DFS:**
  - Add node `"F"` to `StartNodeDropdown` in `CustomizeGraphSheet.kt`.
  - Wire `onGraphModified` in `VisualizerHost.kt` and `GraphTreeVisualizer.kt` to `state.customGraph` so interactive Graph Builder edits regenerate real BFS/DFS traversal steps on the modified graph.
  - Populate `variables` and `callStack` on every BFS and DFS step.

### 5. Verification
- Run `.\gradlew.bat testDebugUnitTest` and verify all unit tests compile and pass with zero regressions.
