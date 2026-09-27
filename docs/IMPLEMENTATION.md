# IMPLEMENTATION.md - Chat UI cleanup, visualizer state audit, minimalist boot splash

## 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)

- **Empty-stream phase flash**: `currentStep` now returns a neutral `VisualizerStep(phaseLabel = "STANDBY")`
  while `steps` is empty instead of the data-class default, which rendered a fabricated PROCESSING pill
  (PhaseBanner maps unknown labels to the cyan default style) before the first generation completed.
- **Play-at-end restart**: `togglePlay()` now rewinds to step 0 when starting playback while parked on the
  final step (and clears any in-flight scrub preview). Previously the playback loop's
  `currentStepIdx < steps.lastIndex` guard cancelled the tick immediately, making the button feel dead.
  Mid-stream play presses are unchanged (playhead preserved).
- **Challenge/regen race**: when a regeneration carries no explicit landing target (array/sort/graph
  changes) the effect now also clears `challengeState.selectedIndices` and `feedback`, so a question posed
  against the old stream can no longer be scored against regenerated steps.
- **Documented invariants** (comments, no behavior change): the `-1` regen sentinel resolving to
  `lastIndex - 1` is correct by construction because the Stack/Queue generators always end with exactly one
  "sequence complete" DONE frame (`AlgorithmStepRepository` stack/queue tails verified); the playhead-policy
  split (live buffer ops land on the new op, other input changes restart at 0) is intentional and now stated.
- **ElementState render coverage audit** (no code change needed): all nine `ElementState` values are
  consumed by at least one canvas family; per-family `else ->` fallbacks cover the cross-family subsets
  (e.g. `CellGrid` handles all nine; `BufferVisualizer` falls back sensibly for sort states that buffer
  generators never emit). No orphan state found.

## 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`)

- **Assistant identity**: the repeated "AVIA CORE" tracked-caps label row was replaced with a single quiet
  purple Spark glyph per reply; the user-side "YOU" micro-label remains for asymmetry.
- **Border noise**: removed decorative 1px `BorderSubtle` outlines from static/structural containers
  (complexity matrix block, code-snippet block, follow-up chips, composer dock top edge). Background tone
  + spacing now carry separation; borders remain on interactive affordances (starter chips keep press
  feedback + border, Launch-Visualizer action keeps its cyan armed border).
- **Unified affordances**: follow-up suggestion chips now use the shared `pressPhysics` modifier
  (SecondaryPurple accent) like every other pressable in the app.
- **Thinking cue**: the bordered "REASONING MATRIX RUNTIME..." chip was replaced with the Spark glyph plus
  a quiet "Thinking..." line in `TextMuted` - a transient state no longer wears persistent-block chrome.
- **Composer**: quieter "Ask about an algorithm..." placeholder; armed send button (cyan) vs resting outline
  preserved; edit bar and layout untouched otherwise. Auto-scroll `LaunchedEffect` and IME handling unchanged.

## 3. Minimalist boot splash

- `ui/boot/BootOverlay.kt` rewritten to a single focal system: AVIA mark + wordmark fade/settle in over
  520ms (0.94 -> 1.0 scale), one 120dp hairline progress line sweeping linearly across the hold,
  `panelFadeSpring` exit into the workspace. Removed: orbital halo ring, 5-bar harmonic wave loader,
  status readout text, and the "TAP ANYWHERE TO SKIP" caption (tap-to-skip gesture itself retained,
  indication-free).
- `BootController.DEFAULT_HOLD_MS` 1800 -> 1200ms; class doc corrected (it claimed "~300ms").
  `BootControllerTest.defaultHoldIsSnappy` updated and passing.
- Stale comments fixed: `AppSettings` header now lists the real speed ladder (1000/600/300);
  `MainActivity` splash-handoff doc references `DEFAULT_HOLD_MS` instead of a hardcoded "~600ms".

## 4. Tests

New regression tests in `VisualizerScreenStateTest`:
`currentStep_emptyStream_showsNeutralStandby_notProcessing`,
`togglePlay_atLastStep_restartsFromBeginning`,
`togglePlay_midStream_keepsPlayhead`.

## 5. Verification

- `compileDebugKotlin` and `testDebugUnitTest`: BUILD SUCCESSFUL, all suites green (results XML confirmed
  the three new tests and updated `BootControllerTest` executed).
- Not device-verified: no emulator/physical device attached this session. Boot-splash visuals, chat layout
  rhythm, and play-at-end feel should get one manual pass on device.

## 6. Deviations / known issues

- Assistant identity glyph still renders once per message (kept as a left gutter anchor); showing it only
  on the first reply was considered and rejected as it made later replies ambiguous after history scroll.
- Speed-change playback jitter (loop restarts its delay mid-tick on speed cycle) left as-is: cosmetic, and
  fixing it would require restructuring the tick loop for negligible gain.
- **`lintDebug` fails pre-existing (not from this cycle).** 103 custom-rule errors
  (`AlgolensRawDpSpacing`, `AlgolensHardcodedHexColor`) sit mostly in files untouched by this
  cycle (ProfileScreen, AlgorithmTheorySheet, ChatSidebarAndPlanner, and pre-existing lines of
  ChatScreen). The report also flags ~200 baseline entries "not found in the project" —
  `lint-baseline.xml` has drifted out of sync with current line positions. None of the flagged
  lines fall inside this cycle's diff hunks. **Do not blindly run `gradlew lintFix`:** its custom
  quickfixes rewrote an unrelated file (`UserPreferences.kt`: `edit().putBoolean(...).apply()` to
  `edit { }` without the `androidx.core.content.edit` import, breaking compilation; reverted).
  Baseline refresh or manual token cleanup is a separate, deliberately scoped task.

