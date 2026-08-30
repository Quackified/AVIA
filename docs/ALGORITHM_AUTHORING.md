# AlgoLens — Algorithm Authoring Guide

> **The contract for adding a new algorithm to AlgoLens.**
> The goal is that an algorithm PR touches only the registry layer
> plus its own generator file. It does *not* touch the visualizer
> screen, the dispatcher, the chrome, or any other algorithm.

---

## TL;DR

To add **"Heap Sort"** you would:

1. Add one entry in `model/AlgorithmId.kt` (an `enum` constant
   with display metadata + family).
2. Add one entry in `data/AlgorithmRegistry.kt` (the `AlgorithmSpec`
   — default input + supportsCustomInput + isStack).
3. Add one entry in `data/SampleData.kt` (the `Algorithm(id = …)`).
4. Add `private fun generateHeapSort(input)` in
   `data/AlgorithmStepRepository.kt` and one line in the
   `when (algorithm.id)` block.
5. *(Optional)* Add theory + multi-language code in
   `AlgorithmTheorySheet.kt` and `CodeTracePane.kt`.

That's it. **No** edits to `VisualizerScreen`, `VisualizerHost`,
`VisualizerHeader`, `PlaybackRail`, or any of the other algorithms.

---

## What goes where

| Concern | Lives in |
|---|---|
| Algorithm identity (id, display name, family, accent) | `model/AlgorithmId.kt` |
| Step-generator switch | `data/AlgorithmStepRepository.kt` (the only `when (id)` block — see "The Single Switch Rule" below) |
| Per-algorithm metadata (default input, supportsCustomInput, isStack, builderEnabled) | `data/AlgorithmRegistry.kt` |
| Dashboard / profile / category filter list | `data/SampleData.kt` (just an ordered list of `Algorithm(id = …)`) |
| Theory sheet body | `AlgorithmTheoryRepository` in `ui/visualizer/AlgorithmTheorySheet.kt` |
| Multi-language code samples | `AlgorithmCodeRegistry` in `ui/visualizer/CodeTracePane.kt` |

If you find yourself writing anything *outside* one of these files
for a new algorithm, you are either:
1. Adding a brand-new concern (and the question is: does it belong
   on `AlgorithmSpec`? Probably yes.)
2. Or you are over-fitting to one algorithm (stop; add it to the
   registry as a generic spec field).

---

## The Single Switch Rule

`AlgorithmStepRepository.generateStepsForAlgorithm(...)` has exactly
**one** `when (algorithm.id)` block. Adding a new algorithm means
adding **one** `case AlgorithmId.NEW -> generateNewXxx(input)` to
that block. If you find yourself adding a second `when` somewhere
else (e.g. inside `VisualizerScreen`), that is a bug.

The `else -> generateBubbleSort(input)` branch is gone. The
fallback is now an empty list + `Log.w` warning, so a missing
generator is loud instead of silent.

---

## Adding a new visualizer family

For a new visualizer that does not fit `LINEAR_1D` / `GRAPH_2D` /
`BUFFER`:

1. Add a new value to `enum class VisualizerFamily` in
   `model/AlgorithmId.kt`.
2. Add a new private `XxxCanvas(...)` composable in
   `ui/visualizer/VisualizerHost.kt` and a new branch in its
   `when (spec.id.family)`.
3. Add the spec entries for the algorithms that use it.

If the family needs its own renderer file (like `GraphTreeVisualizer`
for the 2D case), import + dispatch to it from `VisualizerHost`.
**Do not** put it in the screen.

---

## Adding an algorithm-specific "gimmick"

A gimmick is something like a recursion-tree overlay for Merge Sort
or a weight-badge overlay for Dijkstra. **Do not** edit the
renderer. Instead:

- Phase 4: a `List<VisualizerOverlay>` on `AlgorithmSpec`. The
  `VisualizerHost` composes the overlays on top of the renderer.
- Each overlay reads from `step` and from `state` so it stays in
  lockstep with the playback loop.
- If the overlay needs its own state, hoist it into
  `VisualizerScreenState` (which is `@Stable` and observable).

---

## Visualizer state contract

`VisualizerStep` is the only thing renderers receive. If your
algorithm needs a new field on the step (e.g. `mergeBlocks` for
Merge Sort, `auxiliaryArray` for merge sort's aux buffer), add it
as a nullable/optional field on `VisualizerStep`. **Do not** create
a new step subclass.

If you need a new field, add it to `VisualizerStep` with a sensible
default. Renderer and generator will be updated in the same PR.

---

## Testing

The existing `AlgorithmStepRepositoryTest` loops over every
`SampleData.algorithms` and asserts that the generator returns
non-empty steps and that the code registry has Python code. **If
you add a new algorithm and break this loop, your PR is rejected.**

The same test should also work for *all 4 languages* in the code
registry (Kotlin, Java, Python, C++). Look at
`FeatureEnhancementsTest.multiLanguageRegistry_supportsAll4LanguagesForAlgorithms`
for the pattern.

For new visualizer-state fields, add an assertion to
`AlgorithmStepRepositoryTest` that exercises the field through a
specific step.

---

## Don't

- **Don't** branch on `algorithm.name` anywhere. Use `algorithm.id`
  (which is an `AlgorithmId` enum). The name is for display only.
- **Don't** add a new `if` to `VisualizerScreen`. If you need
  per-algorithm behavior in the screen, the spec / registry is
  missing a field.
- **Don't** introduce a new `when` over `VisualizerRenderMode` in
  any non-`VisualizerHost` file. The render mode is set on the
  step but the dispatch is owned by `VisualizerHost`.
- **Don't** put a custom composable for one algorithm inside
  `VisualizerHost`. Use the overlay list (Phase 4) instead.
- **Don't** modify the legacy model files (`model/SortStep.kt`,
  `model/GraphData.kt`). They are deleted. The unified model is
  `VisualizerStep` / `GraphNodeState` / `GraphEdgeState`.
- **Don't** write a new copy of `RailIconButton`, `IconPillButton`,
  or `SegmentedToggle`. Use the ones in
  `ui/components/WorkspaceControls.kt`.

---

## See also

- `docs/UI_GUIDELINES.md` — the visual design system that every
  algorithm's chrome must follow.
- `model/AlgorithmId.kt` — the canonical algorithm list.
- `data/AlgorithmRegistry.kt` — the canonical spec list.
- `data/AlgorithmStepRepository.kt` — the canonical step generators.