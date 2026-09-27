# TASK - Chat UI cleanup, visualizer state audit, minimalist boot splash

## Goal

1. **Chat UI**: take the existing AVIA conversation design and optimize it for a cleaner,
   more user-friendly look - remove repeated identity chrome and decorative border noise,
   unify interactive affordances on the shared `pressPhysics` language, quiet the transient
   "thinking" cue, and simplify the composer dock. No navigation, backend, or structural changes.
2. **Visualizer logic states**: audit the step/playhead/challenge state machine for gaps,
   bugs, and glitches; fix confirmed issues with regression tests:
   - fabricated `PROCESSING` phase while the step stream is empty,
   - dead-feeling play press when parked on the final step,
   - stale in-flight challenge question surviving a step-stream regeneration,
   - undocumented `-1` regen-target sentinel and the intentional playhead-policy split
     (live buffer ops land on the new op; array/sort/graph changes restart at 0),
   - stale doc constants in `AppSettings`, `BootController`, and `MainActivity` comments.
3. **Boot splash**: minimalist redesign - single focal system (AVIA mark settle + hairline
   progress line), shorter hold (1800ms -> 1200ms), tap-to-skip retained without signage.

## Constraints

- Existing `AlgoTokens` / `AlgoType` / palette only; no new dependencies, no new screens.
- Preserve 44dp touch targets, IME handling, and OS-splash handoff (`setKeepOnScreenCondition`).
- Unrelated `graphify-out` working-tree changes must be preserved.

## Verification basis

Static reading plus `testDebugUnitTest` / `compileDebugKotlin`. No device was attached; behavior
claims below separate implemented from device-verified (the latter was not performed).
