# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

Primary user: a computer-science student working through a data structures & algorithms course. They open AVIA to *see* how a sort, search, tree, or graph traversal actually unfolds — not to read prose, not to memorise pseudocode, and not to be quizzed. The job is to convert an abstract algorithm description into a felt mental model of step-by-step mechanics.

Secondary audiences (confirmed only by surface evidence, not by interview): the app's chrome — `BOOKMARKED` section, Challenge mode, offline badge, and absence of any social / share / leaderboard affordances — is consistent with deliberate practice for coding-interview preparation and with a personal-reference use case. Treat the primary user as the CS student; the other audiences are real but not the design target.

## Product Purpose

AVIA makes 13 canonical CS algorithms feel mechanical by animating them in lockstep with their source code. Success looks like: the user opens an algorithm, presses play, watches the canvas and code-trace line move together, and walks away with a clearer picture of what the algorithm is doing at each step than they had before they opened it.

The product is the visualisation itself. There is no content feed, no course, no community, no monetisation surface. Everything that does not directly serve the "watch and understand" job is out of scope.

## Positioning

Phone-first, offline, no account. A single Android app that ships all 13 algorithms baked in, with an `OfflineBadge` visible in the chrome to make the constraint legible. Most algorithm visualisers are web-based, require a connection, or gate features behind login. AVIA's differentiator is honest engineering — the craft of the visualisation and the integrity of the workspace, not growth hooks.

The visual system is deliberately developer-tool-shaped (dark, tokenised IDE metaphor, monospace type, four-language code trace) rather than classroom-friendly or gamified. This is a positioning choice: the user is treated as someone who already lives in an IDE, not someone who needs to be lured in.

## Operating Context

- **Form factor:** Android phone (compileSdk 37, minSdk 24). Edge-to-edge with custom status / navigation bar tinting. The visualizer is the one screen that uses the full workspace; other screens have minimal chrome.
- **Network posture:** offline-only by design. The `OfflineBadge` is part of the UI, not a fallback state.
- **Languages in the code trace:** Kotlin, Java, Python, C++ — every algorithm ships all four. The `multiLanguageRegistry_supportsAll4LanguagesForAlgorithms` test enforces this.
- **No backend, no auth, no analytics, no remote config, no notifications.** State is local. There is no account, no sync, no cloud.
- **Build & test:** `./gradlew --no-daemon --offline :app:assembleDebug`; 29 unit tests via `./gradlew :app:testDebugUnitTest`.
- **Documentation as contract:** `docs/UI_GUIDELINES.md` and `docs/ALGORITHM_AUTHORING.md` are the design system and the algorithm-authoring contract. They are read before any UI or algorithm change.

## Capabilities and Constraints

**Capabilities (confirmed by code):**

- 13 algorithms across 4 families: sorting (5), searching (2), data structures (4), graph traversal (2). The `AlgorithmId` enum is the single source of truth.
- Family-based dispatch: `LINEAR_1D` (cells / bars), `GRAPH_2D` (tree / graph), `BUFFER` (stack / queue). One `when (family)` block in `VisualizerHost`; nowhere else.
- Playback transport: play / pause / step forward / step back / scrub / speed. State hoisted into a single `@Stable VisualizerScreenState` so it is testable without the Compose runtime.
- Per-step code-trace sync: every `VisualizerStep` carries enough metadata to highlight the active line in all four languages.
- Custom input for array-based algorithms (sorting + searching); fixed input for graph / data-structure algorithms.
- Theory sheets, variable inspector, call stack, Challenge mode, bookmarks, dashboard, profile, settings — all composed from the shared `AlgoTokens` / component catalog.
- Compose preview annotations on every composable.

**Constraints (durable, not aspirational):**

- The catalogue is 13 algorithms. Future work does not expand to a 30-algorithm library or a course / textbook surface.
- The surface area is the 5 nav targets (Home / Explore / Profile / Settings + the visualizer). No onboarding, no community, no paywall, no admin, no sign-in.
- Tokenised design system is a red line: no new colors outside `ui/theme/Color.kt`, no raw `*.dp` in `Modifier.padding` / `spacedBy` / `size` outside the visualizer / components layer, no new component variants when an existing one fits with parameters.
- Dark-only. No light-mode overrides. The `DarkColorScheme` is the only scheme.
- 4dp grid is strict. If a design needs 10dp, the components should be merged or split, not the grid broken.
- Every new algorithm must ship with Kotlin + Java + Python + C++ listings in `AlgorithmCodeRegistry` in the same change.
- All playback state lives in `VisualizerScreenState`. No `remember { mutableStateOf(...) }` in `VisualizerScreen.kt`.
- The single `when (algorithm.id)` for step generators lives in `AlgorithmStepRepository`. Missing generator = `Log.w` + empty list. No silent fall-through to Bubble Sort.

**Quality bar (user-confirmed, qualitative):**

- Intuitive: the user should not need a tutorial to run an algorithm. Controls behave the way an IDE transport behaves.
- Performant: playback at default speed must remain smooth on the minimum-supported device (minSdk 24). No jank during a 10s playback, no recomposition storms in `PlaybackRail`.
- No bugs: the test suite (29 tests) is the regression floor. New work adds tests, never removes them.

**Open / undecided product facts (deliberately not filled in):**

- Whether the app will ever ship beyond Android (no evidence of iOS / web plans; recorded as undecided).
- Whether the algorithm catalogue will grow beyond 13 (currently scoped closed; no roadmap claim).
- Internationalisation (the code trace is English-only across all four languages; no i18n commitment in code or docs).

## Brand Commitments

- **Name:** AVIA. The shipped product name — launcher label (`@string/app_name`), dashboard wordmark, and every piece of user-facing prose. The repository folder, Gradle module, and Kotlin package remain `AlgoLens` for continuity, and code-level prose (file names, KDoc, tests) may still say AlgoLens. In the UI it is "AVIA", never "AlgoLens" and never "algo-visualiser" or any other variant.
- **Voice:** developer-tool. Technical labels, monospace type, no marketing prose. The chrome reads like an IDE, not a textbook.
- **Aesthetic:** dark tech-noir IDE workspace. The five accent roles (cyan / purple / pink / green / yellow) are fixed and semantic, never decorative. Subtle fills are paired with their accent border at 30% alpha. Full surface and token definitions live in `docs/UI_GUIDELINES.md`; that document is the design authority and is treated as a binding contract.
- **Typography:** JetBrains Mono across the app. No swap to a sans for "legibility." The monospace is the IDE metaphor.
- **No marketing assets, no logo asset, no icon set beyond Material symbols.** No claims of users / downloads / press / awards exist in the repo; do not invent any.

## Evidence on Hand

- **Code:** 13 `AlgorithmId` entries in `model/AlgorithmId.kt`; the full `AlgorithmRegistry` and `SampleData` are the canonical catalogue.
- **Documentation:** `docs/UI_GUIDELINES.md` (16 sections, design system) and `docs/ALGORITHM_AUTHORING.md` (algorithm-authoring contract). Both are referenced from the handover brief and from the test suite.
- **Tests:** 29 unit tests across `ExampleUnitTest`, `VisualizerScreenStateTest`, `AlgorithmStepRepositoryTest`, `FeatureEnhancementsTest`. The state-machine test, the "every algorithm generates steps" test, and the "every algorithm has all four language listings" test are the three patterns future work must keep passing.
- **Handover brief:** `docs/HANDOVER.md` documents Phases 0–3 (done, compiled, tested) and the remaining work (Phase 4 overlays, Phase 5 monolith split, Phase 6 verification).
- **Build state at time of init:** `compileDebugKotlin` ✓, `assembleDebug` ✓, `testDebugUnitTest` ✓ (29 passing).

**Evidence explicitly absent — do not fabricate:**

- No users, downloads, ratings, testimonials, press mentions, or awards. Do not invent any.
- No published API or backend. The app has no network call surface.
- No analytics, no crash reporting integration, no A/B testing harness. Do not add by assumption.
- No brand assets (logo, icon set, marketing copy). The visual identity lives in code tokens and the design-system doc; there are no separate files to reference.

## Product Principles

1. **The visualisation is the product.** Every other concern — chrome, profile, settings — exists only to get the user to the visualizer faster and back out cleanly.
2. **Workspace, not toy.** Treat the user as someone who already lives in an IDE. DevTool-grade craft beats classroom-friendly warmth.
3. **No growth hooks.** No accounts, no streaks, no notifications, no share, no leaderboard, no paywall. The product is honest engineering or it is nothing.
4. **Declarative over imperative.** Adding an algorithm = one enum entry + one registry entry + four language listings + the test passes. Adding a per-algorithm gimmick = one overlay class + one registry attachment. No forks, no screen-level switches.
5. **Documentation is the contract.** `UI_GUIDELINES.md` and `ALGORITHM_AUTHORING.md` are read before changes, updated in the same change, and treated as red lines.

## Accessibility & Inclusion

- Minimum touch target is `AlgoTokens.minTouchTarget` (44dp) for primary controls. Secondary rail buttons (30dp) are a deliberate, documented exception; the rail's overall height makes them comfortably reachable.
- Every tappable `IconButton` carries a `contentDescription`. Sentence-case, not verbose ("Play", not "Play the algorithm animation").
- Color is never the only channel. Challenge mode uses a pulsing border *and* a "TARGET" pill. The state-to-color mapping in `UI_GUIDELINES.md` §14 is the single source.
- All animations use `tween` or `spring` — no linear / immediate transitions. The slowest is 600ms (`panelSpring`); the fastest is 100ms (slider drag). Motion is a functional channel, not decoration.
- No internationalisation commitment exists in code or docs. The code trace is English-only across all four languages. Recorded here so future work does not invent a localisation requirement that was never established.
