# PROJECT.md — What is AVIA (`AlgoLens`)?

> **Workflow Role:** Canonical product definition ("What is this app?"). Read by all planning, execution, and review agents before proposing or implementing tasks.

<!-- impeccable:product-schema 1 -->

## Platform

Android (`compileSdk 37`, `minSdk 24`, Kotlin + Jetpack Compose)

## Users

Primary user: a computer-science student working through a data structures & algorithms course. They open AVIA to *see* how a sort, search, tree, or graph traversal actually unfolds — not to read prose, not to memorise pseudocode, and not to be quizzed. The job is to convert an abstract algorithm description into a felt mental model of step-by-step mechanics.

Secondary audiences: coding-interview preparation and personal CS reference (`BOOKMARKED` algorithms, interactive Challenge Mode, Practice Drills, offline-ready execution, and zero social/monetization noise).

## Product Purpose

AVIA makes 13 canonical CS algorithms feel mechanical by animating them in lockstep with their source code and live memory/state telemetry. Success looks like: the user opens an algorithm, presses play (or scrubs / steps), watches the canvas, pointer badges, frontier/buffer strip, and code-trace line move together, and walks away with a clearer picture of what the algorithm is doing at each step.

The product is the visualization workspace itself. Everything that does not directly serve the "watch, manipulate, and understand" job is out of scope.

## Positioning

Phone-first, offline, no account. A single Android app that ships all 13 algorithms baked in, with an `OfflineBadge` visible in the chrome to make the constraint legible.

The visual system is deliberately developer-tool-shaped ("Atmospheric Minimalism & High-Craft Dark UI", `#080D14` deep slate canvas, `#38BDF8` sky cyan, `#10B981` emerald active highlights, `DoubleBezelShell` framing, `1.5dp` `AlgoGlyphs` vector icons, monospace telemetry, four-language code trace) rather than classroom-friendly or gamified.

## Operating Context

- **Form factor:** Android phone (`compileSdk 37`, `minSdk 24`). Edge-to-edge with custom status / navigation bar tinting.
- **Network posture:** Offline-only by design.
- **Languages in the code trace:** Kotlin, Java, Python, C++ — every algorithm ships all four (`multiLanguageRegistry_supportsAll4LanguagesForAlgorithms` test enforces this).
- **Persistence:** Local `SharedPreferences` via `AppSettings` (`preferredLanguage`, `defaultPlaybackSpeedMs`, `highContrastNodeOutlines`, `autoOpenDeckOnPlay`, `showComplexityBadges`, `defaultCellScale`, `hapticsEnabled`, `bookmarkedAlgorithmIds`) and `UserPreferences` (`hasCompletedOnboarding`).
- **Build & test:** `.\gradlew.bat assembleDebug` and `.\gradlew.bat testDebugUnitTest` (129 unit tests).
- **Workflow documentation (`docs/`):**
  - [`docs/PROJECT.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/PROJECT.md) — What is this app?
  - [`docs/ARCHITECTURE.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/ARCHITECTURE.md) — How does the code work?
  - [`docs/DESIGN.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/DESIGN.md) — How should the UI look/behave? (*Impeccable*)
  - [`docs/TASK.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/TASK.md) — What should we do right now? (*ChatGPT — Every task*)
  - [`docs/IMPLEMENTATION.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/IMPLEMENTATION.md) — What did Antigravity actually change? (*Antigravity*)
  - [`docs/REVIEW.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/REVIEW.md) — Did Antigravity do it correctly? (*Codex/Astra*)

## Capabilities and Constraints

**Capabilities (confirmed by code):**

- **13 algorithms across 3 visualizer families (`VisualizerFamily`):**
  - `LINEAR_1D` (Cells / Bars): Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort, Linear Search, Binary Search.
  - `BUFFER` (LIFO / FIFO containers + live stage controls): Stack (`+ PUSH`, `POP`, `PEEK`), Queue (`+ ENQ`, `DEQ`).
  - `GRAPH_2D` (2D hierarchical trees & interactive weighted graphs): Binary Search Tree (BST), Heap (Max-Heap / Min-Heap with synchronized array strip), Breadth-First Search (BFS), Depth-First Search (DFS).
- **Playback transport:** Play / Pause / Step Forward / Step Backward / Live Scrub Preview / Speed Cycle (`0.5x`, `1.0x`, `2.0x`), hoisted into `@Stable VisualizerScreenState`.
- **Focus Deck (`InstrumentDeck`):** Dual-tab terminal inspector (`TRACE` multi-language code trace + `STATE` live variable inspector and call stack frames).
- **Family-aware Custom Input:** `CustomizeInputSheet` (`LINEAR_1D`), `CustomizeBufferSheet` / `CustomizeQueueSheet` (`BUFFER`), `CustomizeGraphSheet` + interactive tap/drag Graph Builder (`GRAPH_2D`).
- **Challenge Mode & Practice Drills:** Real-time step prediction questions (`ChallengeModeManager`) and standalone complexity/step drills (`PracticeScreen`).
- **Bookmarks & Settings:** Persistent bookmarks (`VisualizerHeader` toggle + `ProfileScreen` list with empty-state catalog CTA) and reactive workspace preferences (`SettingsScreen` ↔ `AppSettings`).

**Constraints (durable red lines):**

- **Tokenized design system (`docs/DESIGN.md`):** No raw colors outside `ui/theme/Color.kt`; use `AlgoTokens`, `DoubleBezelShell`, `AlgoGlyphs` (`1.5dp` stroke, `StrokeCap.Round`), and `Modifier.pressPhysics`.
- **Dark-only:** `DarkColorScheme` on `#080D14` deep slate canvas.
- **44dp minimum touch target:** Primary interactive controls must enforce `AlgoTokens.Spacing.minTouchTarget` (`44.dp`).
- **Single Switch Rule:** `AlgorithmStepRepository.generateStepsForAlgorithm` is the only `when (algorithm.id)` step-generator switch in the codebase.
- **Single Family Dispatcher:** `VisualizerHost` is the only `when (spec.id.family)` renderer dispatcher in the codebase.

## Product Principles

1. **The visualization is the product.** Every other surface exists to get the user into the visualizer faster or reinforce what they observed on stage.
2. **Workspace, not toy.** Treat the user as someone who already lives in an IDE. Precision instrument aesthetics beat gamified clutter.
3. **Declarative over imperative.** Adding or modifying an algorithm happens through `AlgorithmRegistry`, `AlgorithmStepRepository`, and `VisualizerStep` telemetry — never via screen-level `if (algorithm.name == ...)` hacks.
4. **Zero regressions.** Every task must compile cleanly and pass `.\gradlew.bat testDebugUnitTest`.
