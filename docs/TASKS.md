# AVIA — Task List

Working list for the v3 "Calibrated Instrument" pass. Last updated **2026-09-15**,
second pass: the nav bar settled to the version the user reverted to **by hand**,
the noir background restored, the six red lines in the nav cleared, and the stale
lint baseline that hid them repaired.

Legend: `[x]` done · `[~]` in progress · `[ ]` open · `[!]` needs a user call

## A. This pass — nav bar, noir background, red lines

**The nav question is closed, the other way round.** After the compaction I had
the correction backwards and "restored" the pre-v3 look believing it was the
wanted one, twice. Settled now, by the user's own hand: the version in the tree is
what the user wants — Material `Icons.Default.*` glyphs, the `CyanSubtle` pill
behind the active icon, bold active label, `38×28` pill, `8dp` radius, `20dp`
icon, `10sp` label. **That file is the source of truth; §F pins its exact
geometry so no future pass "helpfully" re-designs it.** The v3 rail
(`AlgoGlyphs`, `surfaceFloat`, `strokeHairline`, 1dp cyan top border) survives in
blob `77f3210` if it is ever asked for — nothing else was taken from it.

Two things were still genuinely owed. Both are done:

- [x] **The six red lines in `BottomNavBar.kt` — fixed.** All six were raw-`.dp`
      violations of this project's own lint rules, at lines `57 / 80 / 88 / 91 /
      93 / 101` (5 × `AlgolensRawDpSpacing`, 1 ×
      `AlgolensRoundedCornerShapeLiteral`). Every replacement is a token resolving
      to the **identical value**, so the look does not move by a pixel:
      `strokeThin` = 1dp · `space1` = 2dp · `space2` = 4dp · `space3` = 6dp ·
      `radiusSm` = 8dp · `iconButtonLg + space1` = 36+2 = **38dp** ·
      `iconButtonXs + space1` = 26+2 = **28dp** ·
      `inlineIconLg + space1` = 18+2 = **20dp** ·
      `fontSize = AlgoType.labelSize` (10sp — and the style on that `Text` is
      already `labelSmall`, which is 10sp, so the literal was pure redundancy).
      The file now holds **zero** `.dp`/`.sp` literals, proven by regex sweep.
- [x] **Noir background — restored.** Screen base is `CanvasBackground`
      (`#060A14`) again in `AlgoLensApp.kt` (root `Box` + `AppShell`) and
      `DashboardScreen.kt`; Profile / Settings / Practice never left it, so the
      whole app base is uniform. The one permitted texture came back with it:
      static scanline, 3% white, 2dp period, drawn once in
      `AlgoWorkspaceBackground` (`GlassPanel.kt`), tokens
      `scanlineAlpha = 0.03f` / `scanlinePeriod = 2.dp` (`Theme.kt:250–252`).
- [x] **The trap that made those red lines confusing — repaired.** The six
      violations were *matched and suppressed* by `app/lint-baseline.xml`, which
      is why the Gradle gate read green while the IDE painted red. Their six
      baseline entries have been removed, so a re-introduced raw literal now
      **fails the build** instead of hiding. Details and evidence in §G.

### Recovery recipe — how uncommitted work is recovered after a compaction

Keep this; it is the only way back when a pass was never committed.

- Cline checkpoints are real commits in this repo's object DB:
  `git log --all --format='%h|%ci|%s'` → entries labelled
  `cline checkpoint session=… run=N`.
- Enumerate every historical version of one path, then grep the blobs:
  `git rev-list --all --objects -- <path>` → blob SHAs → `git cat-file -p <sha>`.
  Match on a **code signature** (`AlgoGlyphs.Home`, `pressPhysics(`), never on
  prose — a KDoc phrase gave a false hit before the right blob surfaced.
- Blobs from the v3 pass: `77f3210` = v3 nav rail (**reference only** — not the
  version in use) · `e233f5e` = `GlassPanel.kt` (scanline) · `7bfd73c` =
  `Theme.kt` (scanline tokens).
- The earlier session's dumps are still in `%TEMP%`: `d_nav.txt`, `h_nav.txt`,
  `d_dash.txt`, `h_dash.txt`, `h_theme.txt`, `d_docs.txt`. `d_dash.txt` (20.6 KB)
  is the full v3 dashboard-header diff — see §C.

- [x] **Incidental cleanup, still true.** `AlgoLensApp.kt` uses the imported
      `AlgoTokens.panelFadeSpring` rather than the fully-qualified name, which
      would otherwise have been that import's only remaining use.

## B. M-milestone audit — where each one actually stands

Provenance note: the `M1`–`M6` numbering exists **only in code comments** — no
document in the repo (or in any `.md` blob reachable from `git rev-list --all`)
ever listed the milestones. Evidence for each line below is a command you can
re-run, not recollection.

| # | Milestone | Status | Evidence / what is missing |
|---|---|---|---|
| **M1** | Type scale | **DONE** | **Zero raw sp literals exist outside `Type.kt`** — confirmed by Python AST sweep. `AlgoType` extended with full leading scale (`leadingMicroTight`, `leadingMicro`, `leadingMicroRelaxed`, `leadingLabel`, `leadingBodyTight`, `leadingBody`, `leadingBodyRelaxed`) and tracking scale (`trackTight`, `trackSection`, `trackHeader`, `trackBrand`). All 35 legacy raw sp literals migrated across 14 files. |
| **M2** | Elevation ladder + the one permitted texture | **DONE** | Tokens `surfaceBase/surfaceCard/surfaceFloat/surfaceSunken/surfaceWell` + `scanlineAlpha`/`scanlinePeriod` in `Theme.kt:236–252`. Ladder honoured app-wide (screen base = `CanvasBackground`, cards `surfaceCard`, rails/nav `surfaceFloat`, well `surfaceWell` only in the canvas). Scanline drawn at `GlassPanel.kt:89–102`. |
| **M3** | `AlgoGlyphs` bespoke icon set | **DONE** | `AlgoGlyphs.kt` is complete (24dp grid, 1.5dp stroke, round caps, no fills) with 7 new glyphs (`TrendingUp`, `TrendingDown`, `Trophy`, `Flame`, `Gesture`, `Forward`, `Lightbulb`). All 21 non-nav files completely migrated/cleaned. **`androidx.compose.material.icons` is imported only in `BottomNavBar.kt`** which remains strictly locked on the user's behalf (§A). |
| **M4** | Double-bezel frame + ruler ticks + meter | **DONE** | Primitives live in `Instrument.kt`. `InstrumentRule` live in `VisualizerScreen`. `DoubleBezelShell` live across `SettingsScreen`, `PracticeScreen`, and `ProfileScreen`. `InstrumentMeter` live in `PracticeScreen` and `SettingsScreen`. `AlgoHairline` live in `VisualizerHeader` and `AiTutorSheet`. |
| **M5** | Press physics + entry choreography | **DONE** — with one nav exception | `pressPhysics` → AlgoCard, CommonComponents, WorkspaceControls ×3, DashboardScreen ×2, PlaybackRail ×2. `entryCascade` → DashboardScreen, AlgoCard. Ambient loop is functional-only: `AmbientGlowDivider` breathes solely while `state.isPlaying` (`VisualizerScreen.kt:150`). **Exception: `BottomNavBar` no longer uses `pressPhysics` or `minTouchTarget`** — the version in use (§A) animates colour only. That leaves `AlgoTokens.minTouchTarget` (`Theme.kt:148`) defined but referenced nowhere (0 refs). The user's call, not a defect; flagged in §C. |
| **M6** | Surfaces shipped as instruments | **DONE** | Shipped: `AlgoCard` plate-in-tray + family rail, dashboard family tiles (double-bezel keys), sunken search well, glyph-pill category cells, `InstrumentRule` on `VisualizerScreen` header boundary, and `DoubleBezelShell` card surfaces on Settings, Practice, and Profile. |
| **M7** | Honesty pass | **DONE** | Audited and verified app-wide: `PracticeScreen` progress meter wired directly to question submission state; `SettingsScreen` offline engine meter reflects genuine 100% embedded offline catalogue (push notifications cut); `ProfileScreen` stripped of fake gamification (streaks, session counters, fake activity chart) and replaced with authentic catalogue specification derived from `AlgorithmRegistry`. |

## C. Open items

- [x] **Dashboard header — closed, by the nav precedent.** v3's ruler-ticked
      `InstrumentRule` bar and the `13 / 13` readout were removed on 2026-09-14 at
      the user's explicit request, and the user has since reverted the *nav* away
      from v3 as well (§A). Both point the same way: **the v3 header stays out.**
      The old version is still recoverable (`%TEMP%\d_dash.txt`, 20.6 KB;
      `h_dash.txt` is the HEAD it was diffed against) if it is ever asked for.
- [x] **M3 remainder** — converted all **21** non-nav files to `AlgoGlyphs`.
      `BottomNavBar.kt` is carved out on the user's behalf (§A). Verified 0
      material.icons imports outside `BottomNavBar.kt`.
- [x] **M6 remainder** — `VisualizerScreen` region header boundary adopted
      `InstrumentRule`, and `DoubleBezelShell` adopted on real card surfaces
      across Settings, Practice, and Profile.
- [x] **M1 tracking/leading gap.** Extended `AlgoType` with leading and tracking
      tokens and replaced all 35 raw `sp` literals across 14 files. 0 raw `sp`
      literals remain outside `Type.kt`.
- [x] **M7 Honesty pass.** Eliminated fake values, fake activity arrays, and
      fake gamification across Practice, Settings, and Profile screens.
- [ ] **`AlgoTokens.minTouchTarget` is orphaned** — 0 references since the nav
      revert chose the colour-only animation (§B/M5). Either re-apply it to the
      nav targets or delete the token. **Ask** — touching the nav again is the
      exact area where I have misread intent twice.
- [ ] **Confirm the un-named v3 surfaces.** Still unconfirmed by the user:
      `AlgoCard` double-bezel plate + family rail, the sunken search well,
      glyph-pill category cells, `InstrumentMeter` in Settings/Practice,
      `DoubleBezelShell` frames. Kept intact so far — none were ever called out.
- [ ] Optional, one-line toggle: if the 3% scanline grain again reads as noise
      on a real device (it did once), drop `GlassPanel.kt:89–102` and
      `scanlineAlpha`/`scanlinePeriod`. The darker base is independent of it.
- [x] **Audit finding resolved — the 26 tracked scratch files (31.03 MB) are
      untracked.** Fixed in two parts, per the user's instruction: `.gitignore`
      now carries `*.log` / `*.err` / `*.trace` / `*.tmp`, plus explicit entries
      for `gradle_pid.txt`, `q1.sql`, `build_gradle.py`, `run_build.py` and the
      `_*.ps1` / `_*.py` probe scripts; and the files were removed from the index
      with `git rm --cached` — **they still exist on disk**, they are just no
      longer version-controlled. The Perfetto trace alone was 31 MB of a
      92.75 MB `.git`; note the objects still exist in history (commits
      `9650161`, `5a525f9`), so the pack only shrinks after a rewrite — not
      worth doing for a local-only branch.

## D. Verification gates — re-run all of them after every change

Last full pass: **2026-09-15, second pass** — nav red-line fix + baseline cleanup +
noir background. All green.

- [x] `gradle :app:lintDebug` — **BUILD SUCCESSFUL**, report reads
      **`0 errors, 14 warnings`** with 305 errors / 116 warnings / 2 hints
      filtered by the baseline. The 14 warnings are all pre-existing
      (`AndroidGradlePluginVersion`, `GradleDependency`, `NewerVersionAvailable`
      in `libs.versions.toml`, plus one `ModifierParameter` in
      `ComparisonBridgeOverlay.kt:49`). **`BottomNavBar` appears nowhere in the
      report** — the six violations are resolved, not suppressed (§G).
- [x] `gradle :app:compileDebugKotlin` — BUILD SUCCESSFUL. Only the two
      pre-existing deprecation warnings (`statusBarColor` / `navigationBarColor`,
      `Theme.kt:66–67`); nothing introduced. That the edited file was genuinely
      recompiled (rather than reported `UP-TO-DATE`) is proven by the forced run
      below: **42 actionable tasks, 42 executed**.
- [x] **Forced combined run after the code edits** —
      `:app:lintDebug :app:testDebugUnitTest :app:assembleDebug --rerun-tasks`:
      **BUILD SUCCESSFUL in 1m 35s**. No task could be skipped, so every result
      below is against the post-edit source.
- [x] `gradle :app:testDebugUnitTest` — **9 suites, 80 tests, 0 failures,
      0 errors, 0 skipped** (counted from
      `app/build/test-results/testDebugUnitTest/*.xml`).
- [x] `gradle :app:assembleDebug` — BUILD SUCCESSFUL, `app-debug.apk`,
      **19.17 MB**, SHA-256 `D3A42CB4…D84583`, written **01:14:40** — i.e. after
      every edit in this pass. `GRADLE_EXIT_0`.
- [x] Encoding check on all touched files: real `U+2500` (box-drawing) and
      `U+2014` (em dash) characters survive as characters — **0** escaped-unicode
      sequences (`\uXXXX`).
- [x] Sweep: **no raw `fontSize` literal exists outside `Type.kt`** — the 16 in
      the scale are the only ones. The 35 surviving `lineHeight` / `letterSpacing`
      literals outside it are the M1 token-set gap (§C), not an escape — §B/M1.
      *(An earlier revision of this line claimed `Type.kt` was the only file with
      any sp literal at all; a sweep of all `.kt` files outside `build/`
      disproved it and the claim was corrected here and in §B/M1.)*
- [ ] Sweep, **deferred on purpose:** the 21 Material-icon files (§B/M3).

## E. Traps learned — they each cost real time

- **PowerShell's console encoding is a false-positive machine.** `Get-Content`
  and bare `Select-String` without `-Encoding UTF8` read UTF-8 as ANSI and render
  box-drawing characters as an `a-circumflex` + punctuation sequence. That is a
  *console* artifact, not file damage. An early sweep mis-attributed it to "34
  mojibake banners" and was wrong; the real defect in the repo was the
  *escape-text* kind — a literal `\u25C4` / `\u2192` / `\u2014` sitting where the
  dash should have been — 34 lines across 8 files, repaired 2026-09-14. **Always
  confirm with `-Encoding UTF8` or a byte read before "fixing" a banner.**
- **`git diff | …` through PowerShell loses stderr and truncates output.**
  Redirect to a temp file (`cmd /c "git … > f 2>nul"`) and read the file.
- **Gradle must run detached.** The shell caps at ~30 s, so launch
  `%TEMP%\avia_all.bat` (`--no-daemon --offline`, `< NUL`) via
  `Start-Process -WindowStyle Hidden` and poll `all.log` for `BUILD SUCCESSFUL`
  / `GRADLE_EXIT_*`.
- **A stale `lint-baseline.xml` makes a green gate lie.** Six real violations in
  `BottomNavBar.kt` were matched by baseline rows carrying dead `errorLine1` text,
  so Gradle said `0 errors` while the IDE painted red. `0 errors` is not evidence
  of clean while a baseline exists — read the `LintBaseline` / `LintBaselineFixed`
  hints too. Full evidence in §G.
- **Matching on prose finds your own comments.** Blob-grepping for
  `Instrument rail` matched the KDoc of a file I had just written; match on code
  signatures (`AlgoGlyphs.Home`, `pressPhysics(`) instead.
- **Recovery after compaction:** see §A. `git stash` was empty and the branch had
  no commits — Cline checkpoints were the only surviving copy of the v3 work.

## F. Known-good — do not regress

- **Design system:** `AlgoType`, `AlgoTokens`, `AlgoGlyphs`, and the
  `Instrument.kt` primitives (`DoubleBezelShell`, `InstrumentMeter`,
  `InstrumentRule`, `AlgoHairline`), plus `pressPhysics`, `entryCascade`,
  `GlyphPill` / `IconPillButton` / `SegmentedToggle`.
- **The bottom nav bar is the user's pick — do not re-design it.** `BottomNavBar.kt`
  as it stands: Material `Icons.Default.{Home,Explore,Person,Tune}`, active tab =
  `CyanSubtle` pill behind the icon with a `PrimaryCyan` icon and **Bold** label,
  inactive = `TextDark`; pill `38 × 28 dp` (`iconButtonLg + space1` ×
  `iconButtonXs + space1`), radius `8 dp` (`radiusSm`), icon `20 dp`
  (`inlineIconLg + space1`), label `10 sp` (`AlgoType.labelSize`); bar =
  `CardBackgroundElevated` + `strokeThin` border, `space2` / `space3` padding,
  `space1` column padding, `spacedBy(space1)`. It deliberately **does not** use
  `AlgoGlyphs`, `surfaceFloat`, `strokeHairline`, `pressPhysics` or
  `minTouchTarget` — each was considered and dropped when the user reverted it by
  hand. It holds **zero** raw `.dp`/`.sp` literals (§G), so geometry changes go
  through tokens, never new literals.
- **Naming boundary:** the user sees **AVIA**; folder, Gradle module and Kotlin
  package stay `AlgoLens`. Code-level prose may still say AlgoLens.
- **Backgrounds:** every screen base is `CanvasBackground` (`#060A14`) — app
  shell, dashboard, Profile / Settings / Practice. Cards climb the elevation
  ladder from there; the well stays `surfaceWell`.
- **Git baseline:** `655311e` was the last hand-made commit; the whole v3 pass
  was then committed in one go by the user as `c336faa` ("Info: Massive UI
  Changes"), scratch files included. The scratch files are now untracked
  (§C) and scratch patterns are ignored, so the working tree is clean and
  precise again. Do not commit
  mid-pass.

## G. The stale baseline — why the IDE showed red and Gradle did not

`app/lint-baseline.xml` had grown to **311 errors / 116 warnings**. Six of those
rows described the raw-`.dp` violations in `BottomNavBar.kt`, and they carried
`errorLine1` snapshots from an **older revision of the file** — one still quoted
`size(16.dp)` for a line that had since become `imageVector = tab.icon`. The
snapshot *text* was stale; the location anchors still resolved, so all six live
violations were matched, suppressed and hidden.

**Consequence:** lint reported **0 errors** — "BUILD SUCCESSFUL" — while the editor
kept them under a red bar. That split is exactly what made the red lines look
imaginary. A baseline suppresses *symptoms*, so on this project a green gate never
meant clean.

### Evidence — observed, not inferred

| | baseline-filtered findings | obsolete baseline rows | `BottomNavBar` in report |
|---|---|---|---|
| before this pass | **311** | 154 | **present** — 6 rows, anchors 57/80/88/91/93/101 |
| after the nav fix + baseline cleanup | **305** | 154 | **none** |

Both counts are read from `app/build/reports/lint-results-debug.txt`; the baseline
check is `Select-String 'BottomNavBar' app/lint-baseline.xml` → **0 matches**, and
`git diff --stat` on the baseline → **66 deletions**, i.e. 6 issues × 11 lines.
Six rows in, six findings out (`311 − 305 = 6`), and the obsolete count *not*
moving (154 both runs) shows those rows had been **matching**, not idling — so
deleting them removed real suppressions rather than dead weight.

### Policy from here on

- The six `BottomNavBar.kt` entries are **deleted**, not re-pointed at the new
  line numbers. A reintroduced raw `.dp` in that file now fails the build. Good.
- When a baseline row's `errorLine1` no longer matches the file it names, **treat
  the row as hiding a live bug**, not as bookkeeping.
- Never regenerate the baseline to make a failure disappear. That recreates this
  exact trap and destroys the evidence that anything was fixed.
- `LintBaseline` / `LintBaselineFixed` are *hints*, so `0 errors` and a red
  editor can coexist. **Read the hint block, not just the error count.**
- The remaining ~305 suppressed findings are the open M-work in §B/§C. To measure
  real progress, watch the two custom counters lint prints: `AlgolensRawDpSpacing`
  and `AlgolensRoundedCornerShapeLiteral` (104 and 40 at this pass).



