# REVIEW.md — Code & Design Audit

> **Workflow Owner:** Codex / Astra (Reviewer)
> **Task Under Review:** [`docs/TASK.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/TASK.md)
> **Implementation Log:** [`docs/IMPLEMENTATION.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/IMPLEMENTATION.md)
> **Design Authority:** [`docs/DESIGN.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/DESIGN.md)
> **Architecture Authority:** [`docs/ARCHITECTURE.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/ARCHITECTURE.md)

---

## 1. Review Checklist for Codex / Astra

### A. Requirements & Functional Completeness (`docs/TASK.md`)
- [ ] **Settings Persistence:** Does `SettingsScreen.kt` bind two-way to `AppSettings` (`SharedPreferences`) with zero dead local `remember` variables?
- [ ] **Real Bookmarks:** Does `VisualizerHeader.kt` toggle bookmarks in `AppSettings`, and does `ProfileScreen.kt` render `AppSettings.bookmarkedAlgorithmIds` with a `DoubleBezelShell` empty-state CTA (`BROWSE CATALOG`) when empty?
- [ ] **Stack & Queue Fixes:** Do Stack and Queue open with non-empty default operations (`defaultStackOps()` / `defaultQueueOps()`), emit pre-removal `ElementState.SWAPPING` highlights on `Pop`/`Dequeue`, and jump accurately on live stage controls (`+ PUSH`, `POP`, `PEEK`, `+ ENQ`, `DEQ`)?
- [ ] **BST & Heap Fixes:** Does BST emit `INSERTING` + `SEARCHING` steps with normalized in-order/depth coordinates, `visitedNodeIds`, `variables`, and `callStack`? Does Heap apply `GraphCustomization.ForHeap` values for up to 15 nodes with dynamic coordinates?
- [ ] **BFS & DFS Fixes:** Does `CustomizeGraphSheet.kt` include node `"F"`, and does editing the graph in Interactive Builder mode (`onGraphModified`) regenerate BFS/DFS steps via `state.customGraph`?

### B. Design & Accessibility Compliance (`docs/DESIGN.md`)
- [ ] **`44.dp` Touch Targets:** Do all primary interactive controls (`OnboardingScreen` SKIP, `SettingsScreen` back, `PracticeScreen` back, `ProfileScreen` settings, `InstrumentDeck` / `CodeListing` / `StateDeckPage` collapse/expand) meet `AlgoTokens.Spacing.minTouchTarget` (`44.dp`)?
- [ ] **`AlgoGlyphs` & `Modifier.pressPhysics`:** Are `Icons.Default.*` replaced with `1.5dp` stroke `AlgoGlyphs` (`StrokeCap.Round`) and interactive cards/buttons wired with `Modifier.pressPhysics`?

### C. Build & Test Verification
- [ ] Does `.\gradlew.bat testDebugUnitTest` pass with 0 compilation errors and 0 failing tests?

---

## 2. Reviewer Verdict & Notes
*(To be filled in by Codex / Astra)*

- **Status:** `PENDING_REVIEW`
- **Blocking Issues:** None logged yet.
- **Follow-up Recommendations:**
