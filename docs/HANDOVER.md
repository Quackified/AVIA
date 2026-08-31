# AlgoLens — Handover Brief for the Next Agent

> **Context:** You are picking up the AlgoLens Android Studio project
> after a 5-phase UI architecture refactor. Phases 0–3 are **done,
> compiled, and tested**. Phase 4 (the `VisualizerOverlay` extension
> surface) is still pending. Phase 5 (hygiene: split the
> 2,500-line monoliths, delete dead code, enforce the new design
> system with `AlgoTokens`) is **done** (4.2.1–4.2.5; 4.2.6
> deferred to Phase 6).
>
> **Tooling context:** This is an **Android Studio** project with
> the **`jetpack-compose`** + **`impeccable`** + **`build-system`**
> skills available under `.agents/skills/`. Use them.
> `docs/UI_GUIDELINES.md` and `docs/ALGORITHM_AUTHORING.md` are the
> project-specific docs that govern every change in this repo.

---

## 1. Project snapshot

| | |
|---|---|
| **Project** | AlgoLens — Android app that visualises 13 sorting / searching / data-structure / graph algorithms. |
| **Build** | Gradle (Kotlin DSL, AGP 9.4.1), compileSdk 37, minSdk 24, Kotlin + Jetpack Compose. |
| **Build command** | `cd 'c:\Users\Quacky\Documents\Coding\Android Studio\Projects\AlgoLens'; ./gradlew --no-daemon --offline :app:assembleDebug` |
| **Test command** | `./gradlew :app:testDebugUnitTest` (29 tests, all passing) |
| **Status** | `compileDebugKotlin` ✓ · `assembleDebug` ✓ · `testDebugUnitTest` ✓ (5 + 4 + 8 + 12 = 29 passing) |
| **Last commit message** | Should describe the 4-phase refactor (Phases 0+1: foundation, 2: renderers, 3: god-screen decomposition + design system). |

## 2. What was just done (in case you need to revert)

The full refactor is broken into 4 phases; all of them are merged
in. Read `docs/UI_GUIDELINES.md` and `docs/ALGORITHM_AUTHORING.md`
first to understand the contract, then re-read this section for
the specific files involved.

### Phase 0+1: Foundation

- **`model/AlgorithmId.kt`** — `enum class AlgorithmId` with 13
  entries carrying display name, family, accent, etc. Also
  `enum class VisualizerFamily` (`LINEAR_1D`, `GRAPH_2D`, `BUFFER`)
  and `enum class InputKind` (`ARRAY`, `NONE`).
- **`model/Algorithm.kt`** — now a thin UI handle with a `val id: AlgorithmId`. All stringly-typed fields (`name`, `category`, `timeComplexity`, etc.) are derived properties.
- **`data/AlgorithmRegistry.kt`** — `Map<AlgorithmId, AlgorithmSpec>` with helpers `specFor(id)`, `familyFor(id)`, `supportsCustomInput(id)`.
- **`data/AlgorithmStepRepository.kt`** — `when (algorithm.id)` is the **only** switch in the codebase for step generators. Fallback is `Log.w` + empty list, **never** silent fall-through to Bubble Sort.
- **`data/SampleData.kt`** — the canonical ordered list of algorithms. The `categories` list is derived from the enum.
- **Deleted:** `model/SortStep.kt`, `model/GraphData.kt`, `data/BubbleSort.kt`.
- **`ExampleUnitTest.bubbleSort_sortsCorrectly`** now uses `generateStepsForId(AlgorithmId.BUBBLE_SORT, input)`.

### Phase 2: Renderers

- **`visualizer/BarVisualizer.kt`** + **`visualizer/GraphVisualizer.kt`** migrated from legacy `SortStep` / `model.GraphNode` to unified `VisualizerStep` + `GraphNodeState` / `GraphEdgeState`. Use `AlgoTokens` for radii / strokes.
- **`visualizer/VisualizerHost.kt`** — the single dispatcher that switches on `spec.id.family` and composes the right renderer. **The screen no longer has a `when (renderMode)`.**
- **`visualizer/CodeTracePane.kt`** — removed the legacy `CodeTracePane(step: SortStep, ...)` overload.

### Phase 3: God-screen decomposition + design system

- **`visualizer/VisualizerScreenState.kt`** — `@Stable class VisualizerScreenState` that owns all playback state. The factory `rememberVisualizerScreenState(algorithm)` also runs the playback `LaunchedEffect` once.
- **`visualizer/VisualizerHeader.kt`** — the top region composable.
- **`visualizer/PlaybackRail.kt`** — the bottom transport row.
- **`visualizer/ArrayViewMode.kt`** — moved out of `VisualizerScreen.kt` into its own file.
- **`components/WorkspaceControls.kt`** — the shared `RailIconButton` (now takes `Dp`, not `Int`), `IconPillButton`, `SegmentedToggle`. Replaces the 4 hand-rolled copies in the screen.
- **`ui/theme/Theme.kt`** — added 14 new tokens: `space1`–`space8` (4dp grid), `iconButtonSm/Md/Lg`, `inlineIconSm/Md/Lg`, `minTouchTarget`. See `docs/UI_GUIDELINES.md` §3, §6.
- **`test/VisualizerScreenStateTest.kt`** — 12 tests covering the state machine.
- **Applied** the new components to `DashboardScreen` (category filter → `SegmentedToggle`) and `ProfileScreen` (BOOKMARKED section → `SectionLabel`).

### Net effect

- `VisualizerScreen.kt`: **870 lines → 216 lines** (-75%).
- `model/`: down from 4 files to 2.
- `data/`: removed `BubbleSort.kt`; `AlgorithmStepRepository` lost the dead `getCodeLinesForAlgorithm` and the silent fallback.
- The visualizer family dispatch now lives in exactly one place (`VisualizerHost`), keyed on the typed `VisualizerFamily` enum.
- The state machine is hoisted into one `@Stable` class, testable without Compose runtime.
- **Two** documentation files codify the design system (`UI_GUIDELINES.md`, 16 sections) and the algorithm-authoring contract (`ALGORITHM_AUTHORING.md`).

---

## 3. Skills to load for this project

This is an **Android Studio** project. The skills under
`.agents/skills/` are organised by Google / Android domain. Before
starting any task, load the relevant ones with `skill impeccable` /
`skill jetpack-compose/...` — they encode the latest Compose / AGP /
testing patterns and will save you from re-deriving them.

| Skill | Path | When to load |
|---|---|---|
| `impeccable` | `.agents/skills/impeccable` | **Always load first** — this is the code-quality skill that drives the project's expectations for naming, structure, dead-code detection, and lint compliance. Use the `impeccable/reference` rule sets to audit every PR. |
| `jetpack-compose/adaptive` | `.agents/skills/jetpack-compose/adaptive` | Any task that touches responsive layout (foldables, tablets, multi-window). AlgoLens is phone-only today but the visualizer is the natural place to add window-size-class awareness. |
| `jetpack-compose/theming` | `.agents/skills/jetpack-compose/theming` | Every time you touch `Theme.kt`, `Color.kt`, or `Type.kt`. Tokenization is the contract; this skill is the source of truth. |
| `jetpack-compose/migration` | `.agents/skills/jetpack-compose/migration` | Not needed yet (no XML views to migrate), but worth knowing exists. |
| `build-system/agp` | `.agents/skills/build-system/agp` | Touching `build.gradle.kts`, dependency upgrades, build-config debugging. |
| `testing/testing-setup` | `.agents/skills/testing/testing-setup` | Adding or migrating tests. The current test set is JUnit-only — there is no Compose UI test yet. |
| `performance/r8-analyzer` | `.agents/skills/performance/r8-analyzer` | Phase 5.6 below — when the visualizer monoliths are split, use this to check that the R8 graph still works. |
| `profilers/android-profiler` | `.agents/skills/profilers/android-profiler` | Phase 6 — verifying that the new overlay / dispatcher architecture actually performs better than the old one. |
| `navigation/navigation-3` | `.agents/skills/navigation/navigation-3` | Not currently used; AlgoLens uses a hand-rolled `if/when` nav. Consider adopting if multi-pane support is needed. |
| `system/edge-to-edge` | `.agents/skills/system/edge-to-edge` | Verify that all the new `statusBarsPadding` / `navigationBarsPadding` paths compose with edge-to-edge. |

**You can load a skill from the agent shell with the `skill` tool,
e.g.:**

```
skill impeccable
skill jetpack-compose/theming
```

The impeccable skill in particular is opinionated and will catch a
lot of low-quality patterns (dead code, wrong-modifier orders,
missing content descriptions, hard-coded colors) — run it before
each PR.

---

## 4. Tasks remaining (the actual work)

The remaining work is split into **Phase 4** (feature work — the
overlay extension surface), **Phase 5** (hygiene — split monoliths
and delete dead code), and **Phase 6** (verification — perf, lint,
Compose UI tests). The tasks below are ordered so each one is
independently shippable; the recommended order is the same as
numbered.

### 4.1 Phase 4 — `VisualizerOverlay` extension surface

> **Why:** §15 of `docs/UI_GUIDELINES.md` documents the overlay
> contract, and `docs/ALGORITHM_AUTHORING.md` documents the
> `AlgorithmSpec.overlays` field, but neither is wired up yet.
> Without it, the only way to add a Merge Sort recursion-tree
> decoration is to fork `CellArrayVisualizer` — exactly the
> per-algorithm invasion the registry was supposed to prevent.

**Task 4.1.1 — Add the `overlays: List<VisualizerOverlay>` field to `AlgorithmSpec` and propagate it through `VisualizerHost`.**

- Add to `model/AlgorithmId.kt` (or a new `model/VisualizerOverlay.kt`):
  ```kotlin
  fun interface VisualizerOverlay {
      @Composable
      fun Content(
          step: VisualizerStep,
          state: VisualizerScreenState,
          modifier: Modifier
      )
  }
  ```
- Add `val overlays: List<VisualizerOverlay> = emptyList()` to
  `AlgorithmSpec`.
- In `VisualizerHost.kt`, after the renderer composes, iterate
  `spec.overlays` and call `overlay.Content(step, state, Modifier)`.

**Task 4.1.2 — Build the first real overlay: the **Merge Sort recursion tree**.**

- New file `visualizer/RecursionTreeOverlay.kt`.
- Reads `step.recursionDepth` + `step.mergeBlocks` from
  `VisualizerStep`.
- Renders a small tree diagram in the top-right corner of the canvas
  using `Canvas` + `AlgoTokens`.
- Attached via `AlgorithmSpec(overlays = listOf(RecursionTreeOverlay()))`
  for `AlgorithmId.MERGE_SORT` only.
- Acceptance: a Playwright-style visual check that the overlay
  appears on Merge Sort and not on other algorithms.

**Task 4.1.3 — Build the **Dijkstra-style weight badges overlay**.**

- New file `visualizer/WeightBadgeOverlay.kt`.
- Reads `step.edges` and overlays the weight on each edge (the
  legacy `GraphVisualizer` already had a weight badge but it was
  hard-coded). Reusable for any future weighted graph.
- Currently the BFS/DFS edge weights are all `null`, so this
  overlay will look correct but inert until Phase 4.x adds
  weighted algorithms. That's fine — ship it as a no-op for the
  existing 13 algorithms.

**Task 4.1.4 — Wire the **CellArrayVisualizer's `OutOfBounds` indicator** as an overlay (not as a built-in).**

- The current `OffscreenPointerTarget` rendering lives inside
  `CellArrayVisualizer`. Extract it into a
  `PointerBannerOverlay` that any `LINEAR_1D` algorithm can opt
  into.
- Update `AlgorithmSpec` entries for sort/search to add
  `overlays = listOf(PointerBannerOverlay())`.

**Task 4.1.5 — Add a `VisualizerOverlay` test harness.**

- `test/VisualizerOverlayTest.kt`: a smoke test that ensures
  `AlgorithmRegistry` returns specs with the right `overlays` for
  Merge Sort (recursion tree) and that other algorithms return
  `emptyList()` or don't carry this overlay.

---

### 4.2 Phase 5 — Hygiene (✅ COMPLETE)

> **Why:** Three visualizer files were > 700 lines. Splitting them
> makes every Phase 4 overlay easier to add and makes R8 / lint
> runs cheaper. This phase also deletes the dead file left behind
> by Phase 2 and enforces the new design system across the
> remaining renderers.
>
> **Status:** All five sub-tasks except 4.2.6 are done. The 3-way
> monolith split + dead file deletion + AlgoTokens migration is
> complete; the screenshot test is deferred to Phase 6 alongside
> the macrobenchmark work (it shares the same test infrastructure).

**Task 4.2.1 — Delete the now-unused `visualizer/ArrayVisualizer.kt`.** ✅

- Had no callers in production code (the only references left
  were its own `@Preview` and KDoc mentions in 8 other files).
  Removed via `git rm`.

**Task 4.2.2 — Split `CellArrayVisualizer.kt` (was 1,084 lines) into:** ✅

- `CellGrid.kt` (326 lines) — the `LazyRow` + `itemsIndexed` block,
  the new `CellItem` extracted composable, the selection-sort
  boundary curtain divider, the per-cell color triple, and the
  `graphicsLayer` swap-flight transform.
- `CellPointerBadges.kt` (131 lines) — `TopPointerBadge` and
  `BottomPointerBadge` extracted composables (each ~70 lines).
- `OffscreenPointerBanner.kt` (306 lines) — the `derivedStateOf`
  building `offscreenPointers`, the `leftOffscreen`/`rightOffscreen`
  filters, the `Row` with the two `AnimatedVisibility` slots, and
  the private `OffscreenCellPopup` composable. **Stays in this
  file** (not yet merged with `PointerBannerOverlay.kt`); the
  VisualizerOverlayTest still locks the inline semantics.
- `CellGlowPainter.kt` (56 lines) — `internal fun DrawScope.drawCellGlow(...)`
  extracted (was `private` in the original).
- `ChallengeGlowTargets.kt` (40 lines) — `rememberChallengePulseState()`
  composable + `challengeTargetColorTriple()` helper.
- The public `CellArrayVisualizer.kt` (472 lines) is now a thin
  shell that composes the above plus the 4 algorithm banners
  (Insertion/Bubble/Selection/Merge), the comparison callout, and
  the `@Preview`.
- **Acceptance:** public API byte-identical (parameters, defaults,
  types). The `OffscreenPointerTarget` data class stays in
  `CellArrayVisualizer.kt` (public top-level) because
  `FeatureEnhancementsTest.kt` references it by FQN.

**Task 4.2.3 — Split `CodeTracePane.kt` (was 1,173 lines) into:** ✅

- `data/AlgorithmCodeRegistry.kt` (616 lines) — the
  `AlgorithmCodeRegistry` object + `MultiLangCode` data class + the
  7 private `get*Code` builders. **Also moved `TraceLanguage`
  enum** here from `ui.visualizer` because it is part of the
  multi-language data model. `SyntaxHighlighter` (UI-aware)
  stayed in `ui.visualizer` in its own `SyntaxHighlighter.kt`.
- `CodeListing.kt` (299 lines) — the language-tab header `Row`,
  the `LazyColumn` of syntax-highlighted code lines, the
  `InlineVarChip` private composable, and the active-line semantic
  rail with the sync-pulse draw-behind glow.
- `ActiveLinePill.kt` (93 lines) — the `LaunchedEffect` +
  `snapshotFlow` + `animateFloatAsState` calls driving the pill's
  Y/H, plus the rendered pill `Box`. Takes `accent: Color` as a
  parameter (computed in the shell).
- The public `CodeTracePane.kt` (162 lines) is a thin shell
  that owns the `selectedLanguage` state, the single shared
  `LazyListState`, the `codeData` lookup, the
  `activeLinesInCurrentLang` mapping, the `pillAccent` computation,
  the auto-scroll `LaunchedEffect`, and the outer panel chrome.
- **Note:** The original 4-way plan included `VariableInspector.kt`,
  but the legacy `disclosure { … }` block was already removed in
  a prior refactor and moved into the kebab popover in
  `VisualizerHeader.kt`. The remaining "variable inspector" UI is
  the inline per-line `InlineVarChip`, which lives inside
  `CodeListing.kt` as a private composable. **VariableInspector.kt
  is dropped from scope.**
- **Test impact:** Two test files updated for the package move
  (import-only, no body changes):
  - `FeatureEnhancementsTest.kt`: `ui.visualizer.AlgorithmCodeRegistry`
    → `data.AlgorithmCodeRegistry`; same for `TraceLanguage`.
  - `AlgorithmStepRepositoryTest.kt`: same.

**Task 4.2.4 — Split `GraphTreeVisualizer.kt` (was 741 lines) into:** ✅

- `GraphTreeRenderer.kt` (297 lines) — the `Canvas` `drawScope`
  block (edges, drag preview, nodes with halos) and the
  `activeHaloPulse` infinite transition.
- `GraphBuilderOverlay.kt` (334 lines) — the toolbar
  (`GraphBuilderToolbar`), the tap/drag `pointerInput` gesture
  detectors (`GraphBuilderGestures`), the
  `GraphCanvasGeometry` helper (internal, was duplicated 4× in
  the original), and the instruction banner
  (`GraphBuilderBanner`).
- The public `GraphTreeVisualizer.kt` (213 lines) is a thin shell
  that owns all the mutable builder state (`dynamicNodes`,
  `dynamicEdges`, `dragStartNode`, `currentDragPos`,
  `hoveredTargetNodeId`, `selectedNodeId`, `isBuilderActive`,
  the per-node "first visit" pop `nodeScales`) and composes the
  toolbar + gestures + renderer + banner.
- **Dedup:** The 4 duplicated `toCanvasOffset` copies in the
  original are now a single `GraphCanvasGeometry.toCanvasOffset()`
  internal data class, shared by the renderer and both gesture
  detectors.

**Task 4.2.5 — Migrate the visualizer family renderers to the new `AlgoTokens` sizing constants.** ✅

- **Three new `AlgoTokens` constants** added in `Theme.kt`:
  - `radiusXxs = 6.dp` — chip / banner / cell-box radius (was
    the highest-volume literal without a token, 30+ call sites).
  - `strokeMedium = 1.5.dp` — buffer / card border weight
    (between `strokeThin=1.dp` and `strokeActive=2.dp`).
  - `iconButtonXs = 26.dp` — cell key-card icon size (the
    one-off `Modifier.size(26.dp)` in `CellArrayVisualizer.kt`).
- **Correction to the original plan:** the plan said
  `RoundedCornerShape(6.dp) → AlgoTokens.radiusSm`, but
  `radiusSm = 8.dp`, not 6.dp. Mapping 6 → 8 would have caused
  a 33% visual change in cell / chip / banner corners. The
  correct mapping (with the new `radiusXxs`) is **6.dp → radiusXxs**,
  preserving exact pixel-identical visuals.
- **Files migrated:** the new `CellArrayVisualizer`, `CellGrid`,
  `CellPointerBadges`, `OffscreenPointerBanner`, `CodeListing`,
  `ActiveLinePill`, `GraphTreeVisualizer`, `GraphTreeRenderer`,
  `GraphBuilderOverlay` were built from the ground up using
  `AlgoTokens.*` for every value with a matching token. The
  pre-existing `BufferVisualizer.kt` was swept mechanically
  (12.dp → radiusMd, 8.dp → radiusSm, 6.dp → radiusXxs, 1.dp →
  strokeThin, 1.5.dp → strokeMedium, 0.5.dp → strokeHairline,
  2.dp spacing → space1, 4.dp spacing → space2).
- **Out of scope (intentionally left as raw literals):** 5.dp,
  3.dp, 9.dp, 10.dp, 11.dp, 14.dp, 18.dp, 20.dp, 36.dp, 42.dp,
  44.dp, 48.dp, 52.dp, 120.dp, 160.dp, 180.dp, 200.dp — these
  are domain-specific component dimensions (buffer heights, cell
  icon sizes, button sizes) where no generic spacing token
  applies. Phase 6's lint rules will decide whether they need
  their own tokens.

**Task 4.2.6 — Add a Compose preview screenshot test for the visualizer.** ⏸ DEFERRED

- The plan was to create `app/src/androidTest/.../VisualizerScreenTest.kt`
  capturing one screenshot per `VisualizerFamily` + `ElementState`.
- **Deferred to Phase 6** alongside the `Macrobenchmark` module
  and the lint rules. Reason: this is the first `androidTest`
  file in the project; adding new test infrastructure (Compose UI
  test dependency, screenshot capture plumbing) is real setup
  work that is better grouped with the macrobenchmark module
  which needs the same scaffolding.
- Tracked as a follow-up issue.

---

### 4.3 Phase 6 — Verification

> **Why:** The refactor was justified by "the existing system is
> hard to extend and slow to compose". This phase proves it.
> Skipping it means we don't know whether the new architecture is
> actually faster, or whether the new components are reused as
> intended.

**Task 4.3.1 — Run the `impeccable` skill across the repo and file
follow-ups for any rule that fires.**

- `skill impeccable` then `impeccable/reference` to see the rules.
- Open one PR per rule cluster (e.g. "dead code", "naming",
  "modifier order"). Each PR should be small and the diff
  should be focused.

**Task 4.3.2 — Compose `Macrobenchmark` for the visualizer.**

- Add a `benchmark` module per the `profilers/android-profiler` skill.
- Measure: time-to-first-frame of `VisualizerScreen` for each
  `VisualizerFamily`, and recomposition cost of `PlaybackRail`
  during a 10s playback.
- Compare against the pre-refactor baseline (you can use the
  previous APK; the visualizer was 870 lines + 12 remember
  blocks + nested LaunchedEffects). Expect: faster TTI, fewer
  recompositions per second.

**Task 4.3.3 — Add a lint rule (or Detekt rule) that flags:

- `Color(0xFF…)` literals outside `ui/theme/Color.kt`.
- `RoundedCornerShape(*.dp)` literals outside `ui/components/`
  and `visualizer/` exception list.
- `.dp` literals inside `Modifier.padding` / `Modifier.spacedBy`
  / `Modifier.size` — should be `AlgoTokens.space*` / `iconButton*` / `inlineIcon*`.
- Direct calls to `mutableStateOf` / `remember` inside
  `VisualizerScreen.kt` (state belongs in `VisualizerScreenState`).

This is what `impeccable/scripts/detector` can scaffold. Don't write
the rules by hand unless the scaffold is insufficient.

**Task 4.3.4 — Run `r8-analyzer` (skill) on a release build and verify the visualizer family's class graph is smaller than before.**

- Compare against the pre-refactor APK (check the build artefacts
  in `.build/shipped-apk/` if you have one).
- Expected: the visualizer family renderer / dispatch classes
  should be tree-shake-friendly; unused families should drop
  cleanly.

---

## 5. Architectural rules to *not* break (red lines)

These are the rules the next 4 phases depend on. Break any of them
and the foundation cracks.

1. **No new `when (algorithm.name)`, `when (currentStep.renderMode)`, or
   `when (algorithm.id)` outside their canonical homes.** Algorithm
   id switches live in `data/AlgorithmStepRepository.kt`; family
   dispatch lives in `ui/visualizer/VisualizerHost.kt`. Anywhere
   else is a bug.

2. **No `var X by remember { mutableStateOf(...) }` inside
   `VisualizerScreen.kt`.** All visualizer state lives in
   `VisualizerScreenState`. The screen body is declarative.

3. **No new `if` in `VisualizerScreen.kt` that branches on
   `algorithm.id` / `algorithm.name` / `algorithm.category`.**
   If you need per-algorithm behavior in the screen, add a field
   on `AlgorithmSpec`.

4. **No new files that re-invent `RailIconButton`,
   `IconPillButton`, or `SegmentedToggle`.** Use the ones in
   `ui/components/WorkspaceControls.kt`.

5. **No `Color(0xFF…)` literals outside `ui/theme/Color.kt`.** Add
   a token in `AlgoTokens` and reference it.

6. **No new `RoundedCornerShape(*.dp)` / `size(*.dp)` literals
   inside `Modifier.padding` / `spacedBy` in
   `visualizer/` or `ui/components/`.** Use the `AlgoTokens.space*`
   and `*iconButton*` / `*inlineIcon*` constants.

7. **No edits to legacy model files** — they don't exist
   anymore. `model/SortStep.kt` and `model/GraphData.kt` were
   deleted. The unified model is `VisualizerStep` /
   `GraphNodeState` / `GraphEdgeState`.

8. **No new `else` fall-through to Bubble Sort** in the step
   repository. Missing generator = `Log.w` + empty list, full stop.

9. **All four code-trace languages** (Kotlin, Java, Python, C++)
   must have an entry in `AlgorithmCodeRegistry` for every
   algorithm in `SampleData.algorithms`. The
   `multiLanguageRegistry_supportsAll4LanguagesForAlgorithms` test
   enforces this — if you add an algorithm, add all four
   implementations in the same PR.

10. **Every new algorithm must work with the default `ArrayViewMode`**
    (cells). If a new visualizer doesn't make sense as cells, set
    `ArrayViewMode` rendering to a sensible fallback (the bars
    renderer works for any 1D algorithm) and add a per-spec
    `preferredViewMode` field in Phase 5 if needed.

---

## 6. Acceptance checklist for Phase 4+ PRs

Before you open a PR, verify the following:

- [ ] `compileDebugKotlin` passes.
- [ ] `testDebugUnitTest` passes with **the same 29 tests** plus any
      new ones you added.
- [ ] `assembleDebug` produces an APK.
- [ ] `impeccable` skill run shows no new fires.
- [ ] No new file under `visualizer/` exceeds 400 lines.
- [ ] No new file references a removed type (`SortStep`,
      `model.GraphNode`, `model.GraphEdge`, `StepType`).
- [ ] If you added an algorithm: the
      `all13Algorithms_generateNonEmptySteps` test still passes (it
      loops over `SampleData.algorithms`).
- [ ] If you added an algorithm: the
      `multiLanguageRegistry_supportsAll4LanguagesForAlgorithms`
      test still passes.
- [ ] If you added an algorithm: a 4-language code entry is in
      `AlgorithmCodeRegistry` (Kotlin + Java + Python + C++).
- [ ] If you added a composable: it has a `@Preview` block.
- [ ] If you added a state field: it has a `VisualizerScreenStateTest`
      case.
- [ ] If you added a token: it is documented in `UI_GUIDELINES.md`
      under the right section.
- [ ] If you changed a layout: it follows the layout grammar in
      `UI_GUIDELINES.md` §11.

---

## 7. Quick navigation map

The most important files for getting oriented:

| File | Why you care |
|---|---|
| `docs/UI_GUIDELINES.md` | The design system. Read it before touching any UI. |
| `docs/ALGORITHM_AUTHORING.md` | The "how to add an algorithm" contract. |
| `model/AlgorithmId.kt` | The single source of truth for the 13 algorithm identities + their family. |
| `data/AlgorithmRegistry.kt` | The `AlgorithmSpec` map — declarative per-algorithm metadata. |
| `data/AlgorithmStepRepository.kt` | The single `when (id)` block for step generators. |
| `ui/visualizer/VisualizerHost.kt` | The single `when (family)` block for renderer dispatch. |
| `ui/visualizer/VisualizerScreenState.kt` | The state machine. Testable. |
| `ui/visualizer/VisualizerScreen.kt` | The thin shell. **Should not grow beyond ~250 lines.** |
| `ui/components/WorkspaceControls.kt` | The shared button / pill / toggle components. |
| `ui/theme/Theme.kt` | The `AlgoTokens` object. Every new token lives here. |
| `test/VisualizerScreenStateTest.kt` | The state machine test pattern. |
| `test/AlgorithmStepRepositoryTest.kt` | The "every algorithm generates steps" loop pattern. |
| `test/FeatureEnhancementsTest.kt` | The "every algorithm has all 4 language code listings" pattern. |
| `.agents/skills/impeccable` | **Run first**, always. |

---

## 8. Notes for the human reviewer (if any)

This is a 4-phase refactor that started from a 870-line god-screen
and an 11-test suite and ended at a decomposed architecture with
29 tests and a documented design system. The next 3 phases
(4 through 6) are about **paying back** the work the refactor
unblocked — adding algorithm-specific gimmicks the old architecture
couldn't support, splitting the 2,700 lines of monolith that
remain, and proving the new architecture is faster and more
maintainable.

The most important message is in §5: the red lines. Every line
in that list is something the previous code base violated, that
took real time to fix, and that the new architecture makes hard
to re-introduce. If a PR violates one, push back.
PLACEHOLDER
PLACEHOLDER