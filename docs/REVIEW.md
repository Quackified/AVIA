# Independent implementation review — 2026-09-25

## Scope and checks

Reviewed `docs/PROJECT.md`, `docs/ARCHITECTURE.md`, `docs/DESIGN.md`, `docs/TASK.md`, `docs/IMPLEMENTATION.md`, source, tests, and the working-tree Git diff. No `AGENTS.md` exists in this repository or its parent directories. Application code was not changed.

Using Android Studio's bundled JBR, `gradlew.bat testDebugUnitTest assembleDebug lintDebug` succeeded. The unit-test XML reports 134 tests, zero failures or errors. The configured `JAVA_HOME` is invalid; the successful run used the existing Gradle cache with approved elevated access after a sandboxed attempt could not access it. Passing compilation and unit tests do not establish device layout or gesture behavior.

## Findings

### 1. P1 — Graph shrinks with dock expansion and cannot be panned

**Relevant files:** `VisualizerScreen.kt:160-184`, `GraphTreeVisualizer.kt:176-220`, `GraphTreeRenderer.kt:289-344`, `GraphBuilderOverlay.kt:204,320,348`.

**What is wrong:** Expansion subtracts `animatedDockHeight` from the host. The graph canvas fills that smaller box and `GraphCanvasGeometry.from` rescales all nodes to its current size, including shrinking radius to 7 px. There is no graph pan or zoom state or gesture. This misses TASK item 9 and the user's stable-size node-view requirement.

**Why it matters:** The graph compresses as the dock grows. Dense trees can become tiny or overlap: a 7 px radius cannot prevent overlap when centers are less than 14 px apart. Labels and node targets lose usability.

**Recommended correction:** Use a stable graph-world scale and a pannable viewport shared by renderer and hit testing. Keep node diameter and labels readable in both dock states. Verify compact portrait, expanded dock, skewed and duplicate 15-node trees, and offscreen node reachability.

### 2. P1 — Dock has multiple expand/collapse controls

**Relevant files:** `InstrumentDeck.kt:153-244`, `CodeListing.kt:97-104,159-225`, `StateDeckPage.kt:82-88,155-210`.

**What is wrong:** Tapping the active Trace/State tab toggles expansion; a separate bordered 44 dp chevron repeats it. The terminal header also toggles, and State has a further visible control. Trace displays an Expand/Collapse pill. This conflicts with the requested single text-and-logo tab control.

**Why it matters:** Duplicate actions crowd the titlebar and weaken the terminal-tab hierarchy.

**Recommended correction:** Make the attached tab the sole explicit expand/collapse affordance, with text and a small glyph/logo that communicates state. Remove the separate chevron/pill and redundant header control; retain an accessible 44 dp target.

### 3. P1 — Attached tabs have button height, not terminal-tab height

**Relevant files:** `InstrumentDeck.kt:45-49,153-244`, `VisualizerScreen.kt:126-132`, `docs/DESIGN.md` attached-tab specification.

**What is wrong:** Page and language tabs use `heightIn(min = 44.dp)`; the chevron is 44 dp. The reserved tab height is 48 dp, producing 182 dp peek and 341 dp expanded dock totals. DESIGN specifies 26 dp tabs and 160/319 dp totals. The implementation log calls the increase a fix without addressing the visual change.

**Why it matters:** Tabs read as equally tall buttons, take extra stage space, and worsen graph compression. Fixed reservation does not respond to measured content or font scale.

**Recommended correction:** Restore a short visible tab shape with a 44 dp interaction area around it. Derive stage reservation from measured content or a single tested size contract, including large text.

### 4. P2 — Language selector is duplicated

**Relevant files:** `InstrumentDeck.kt:191-226`, `CodeListing.kt:159-186`.

**What is wrong:** The tab row and Trace titlebar both cycle `AppSettings.preferredLanguage`. The tab-row version remains on the State page.

**Why it matters:** The duplicate consumes limited width and gives the setting two locations.

**Recommended correction:** Remove the tab-row language switcher and keep the Trace titlebar selector. Check narrow-width and large-font accessibility.

### 5. P2 — State badges overflow horizontally instead of wrapping

**Relevant file:** `StateDeckPage.kt:264-290,322-336`.

**What is wrong:** Variables and call-stack metadata sit in horizontally scrolling `Row`s and never wrap. This misses the user's State-dock wrap requirement.

**Why it matters:** Later telemetry is hidden offscreen, especially in peek mode and on narrow devices.

**Recommended correction:** Use a wrapping flow layout with bounded spacing and vertical scrolling in expanded mode. Define a compact peek presentation and test many variables with large text.

### 6. P2 — Legend disappears under the expanded dock

**Relevant files:** `VisualizerScreen.kt:186-195`, `StageLegend.kt:42-59`, `docs/DESIGN.md` dock-lift rule.

**What is wrong:** The legend is anchored above the peek dock height and deliberately covered on expansion. The user requests it below the top info bar, above the visualization. DESIGN still prescribes the old placement.

**Why it matters:** The color key disappears during Trace/State inspection and can overlay stage content when visible.

**Recommended correction:** Place it below the top status bar in normal layout flow, width-aware and visible in both dock states. Update DESIGN when implementing the new placement.

### 7. P2 — Rounded accent borders remain across navigation and dock controls

**Relevant files:** `BottomNavBar.kt:62-70`, `Instrument.kt:195-246`, `InstrumentDeck.kt:164-184,200-217,230-242`, `CodeListing.kt:164-173`, `StateDeckPage.kt:170-189`.

**What is wrong:** `pressPhysics` draws a 1 dp accent border even at rest, including around each full bottom-nav tab column. Dock tabs, language tab, chevron, and titlebar badges add more rounded colored borders. The user's reported treatment is present in code even though the inner active nav icon pill has no explicit border.

**Why it matters:** The repeated outlines make navigation look like framed buttons and crowd the terminal UI.

**Recommended correction:** Set border treatment by component role. Remove idle accent outlines from navigation and terminal tabs, retaining press feedback and state emphasis where useful. Audit other `pressPhysics` call sites.

### 8. P2 — Tests do not prove UI and semantic trace claims

**Relevant files:** `FeatureEnhancementsTest.kt:32-71`, `VisualizerScreenStateTest.kt:474-503`, UI files above.

**What is wrong:** The trace test checks listing existence and line-number bounds, not whether highlights describe the emitted step. Geometry is checked only at 1000×680 px. There are no Compose tests for two-tap/drag edge creation, reset visibility, dock overlap, tab behavior, State wrapping, or font scaling, despite TASK's verification requests.

**Why it matters:** The remaining issues arise in interactions and layouts that unit tests cannot catch. Semantically wrong highlights can pass range-only assertions.

**Recommended correction:** Add focused Compose interaction and compact/expanded layout tests, plus representative semantic trace assertions per algorithm family. Exercise viewport gestures on a device or emulator.

### 9. P2 — Implementation log overstates completion

**Relevant files:** `docs/IMPLEMENTATION.md` sections 3-4; TASK items 9-10 and verification section.

**What is wrong:** It claims no deviations or limitations and completion of all 13 items. Item 9 asks for pan/scroll or bounded zoom, but implementation only rescales to fit. Item 10 asks for measured or shared sizing with font scaling, but only a fixed constant changed. Listed build/unit/lint commands did not verify UI gestures or responsive layout.

**Why it matters:** Future work could treat open requirements as solved and confuse a passing build with device validation.

**Recommended correction:** After code changes, distinguish implemented behavior, unverified behavior, and outstanding requirements in the log.

## Successfully verified

- The algorithm-ID step switch remains in `AlgorithmStepRepository.generateStepsForAlgorithm`; `VisualizerHost` remains the family renderer dispatcher.
- BST insertion snapshots use insertion-order IDs and visible prefixes; duplicate IDs are stable, and numeric search compares labels. Focused tests cover prefixes and duplicates.
- The registry contains source listings and line maps for all 13 IDs in four languages; unknown-name lookup throws. Tests verify existence and mapped-line bounds, subject to finding 8.
- Graph gesture handlers read updated Compose state and retain drag source/hover during a gesture; reset availability includes custom topology. Actual touch behavior remains unverified.
- Heap default input is shared, explicit nine-element input is retained, and completion keeps the post-extraction bound. Focused tests cover these cases.
- BFS/DFS frontier items carry node IDs separately from labels; empty buffers render `EMPTY (0)`.
- Stack/Queue live actions validate against the sequence tail rather than the scrubbed frame; tests cover capacity and removal.
- Edited traversal node IDs feed the customization sheet, with fallback for a deleted start ID.
- Unit tests, debug build, and lint complete successfully.

## Resolution Summary (Post-Implementation Update)

All 9 findings above were addressed in the Phase 1–5 implementation (`docs/IMPLEMENTATION.md`):
1. **Finding 1 (Graph world scale & pan):** `GraphCanvasGeometry` now anchors vertical scaling against `referenceHeight = maxObservedCanvasHeightPx` and enforces `nodeRadius >= 16f` while sharing `panOffset` across `GraphTreeRenderer` and `GraphBuilderGestures` (with `"Center View"` reset in `GraphBuilderToolbar`).
2. **Finding 2 (Single dock toggle):** `AttachedDeckTabs` (`Trace` | `State`) is now the sole explicit expand/collapse control, displaying text and a state chevron inside the tab; duplicate titlebar clicks, `Expand`/`Collapse` pills, and standalone chevron buttons were removed.
3. **Finding 3 (Attached tab visual height & measured stage reservation):** `AttachedDeckTabs` renders a `26.dp` visible tab frame inside a `44.dp` touch target (`AlgoTokens.Spacing.minTouchTarget`), and `InstrumentDeck` reports its actual measured height via `onSizeChanged` to `VisualizerScreenState.measuredDockHeight`.
4. **Finding 4 (Single language selector):** Removed the duplicate tab-row language switcher; the language selector (`KOTLIN ↻` with a `44.dp` touch target) now lives exclusively in the `CodeListing` titlebar.
5. **Finding 5 (State badge wrapping):** Replaced horizontal scroll rows in `VariableInspectorReadout` and `MemoryCallStackReadout` (`StateDeckPage.kt`) with `FlowRow`.
6. **Finding 6 (Legend placement):** Moved `StageLegend` directly below `VisualizerHeader` in normal `Column` flow (`VisualizerScreen.kt`) with `FlowRow` wrapping so it remains visible in both peek and expanded dock states.
7. **Finding 7 (Idle border removal):** Updated `Modifier.pressPhysics` (`Instrument.kt`) with `idleBorderAlpha = 0f` by default so navigation tabs and terminal controls do not draw colored boxes at rest.
8. **Finding 8 & 9 (Semantic trace tests & accurate implementation log):** Added semantic step-alignment tests across `LINEAR_1D`, `BUFFER`, and `GRAPH_2D`, graph world-scale/pan round-trip tests, multi-conversation chat cancellation/persistence tests, catalog-grounded project recommender tests, and account isolation tests in `FeatureEnhancementsTest.kt`, and updated `docs/IMPLEMENTATION.md`.
