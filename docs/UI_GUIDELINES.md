# AVIA — UI Design System & Guidelines

> **Naming.** The shipped product is **AVIA** (launcher label, wordmark,
> screen prose). Repository, Gradle module, Kotlin package, and therefore
> all class/file identifiers (`AlgoLensTypography`, `Theme.AlgoLens`,
> `AlgoLensApp`, `AlgoGlyphs`, `AlgoTokens`) remain `AlgoLens` for
> continuity. When in doubt: user sees "AVIA", code says "Algo".

> **The contract every screen, region, and component agrees to.**
> If something visual lives in the app, it speaks this language.
> Adding a new algorithm or feature is supposed to be cheap: pick
> the right token, pick the right component, and stop thinking
> about pixels.

---

## 1. Design principles

1. **One workspace, three regions.** Header → Canvas → Code Trace,
   with a glow divider and a bottom playback rail. The visualizer
   screen is the only place this layout exists; the dashboard,
   profile and practice screens have their own minimal chrome.
2. **Tokenised, not hard-coded.** Every color, dp, animation, and
   font weight comes from `AlgoTokens` (see
   `app/src/main/java/com/example/algolens/ui/theme/Theme.kt`). If
   you find yourself writing a literal `Color(0xFF…)` outside of
   `ui/theme/Color.kt`, **stop** — add a token instead.
3. **State hoisted, regions narrow.** `VisualizerScreenState` is
   the single source of truth for the workspace. Region composables
   (`VisualizerHeader`, `PlaybackRail`, `VisualizerHost`) read it
   and invoke its mutators. They never `remember` their own
   `Boolean` or `Int`.
4. **Algorithm-specific gimmicks attach, not invade.** Adding a
   recursion-tree overlay for Merge Sort or a weight-badge overlay
   for Dijkstra happens through the spec/registry, not by forking
   the visualizer.
5. **Dark-only, never light.** The whole app is designed for a
   deep tech-noir aesthetic. Do not add light-mode overrides.

---

## 2. Color roles (semantic, not decorative)

The accent palette is **fixed** — never introduce a new hex for a
visual. If a use case does not fit one of the five roles below,
add a new role first.

| Token | Hex | When to use | Examples |
|---|---|---|---|
| `accentCyan` (PrimaryCyan) | `#00E5FF` | Active traversal, read pointers, default focus | Active node border, cell "active" border, scrubber thumb, header title accent |
| `accentPurple` (SecondaryPurple) | `#8B5CF6` | Secondary tracking, auxiliary state | Right-pointer in BFS, key-element in insertion sort, code-trace active line, AI Tutor icon |
| `accentPink` (AccentPink) | `#FF3366` | Comparisons, swaps, mutations, challenge-mode | Swap pair tint, challenge-mode active pill, "incoming swap" badge |
| `accentGreen` (AccentGreen) | `#00E676` | Verified sorted, correct answer, graph-tree traversal | Sorted tail, found element, BFS/DFS visited, theory sheet accents |
| `accentYellow` (AccentYellow) | `#FFB800` | Pivot, threshold, edge alerts | Pivot cell, min-index, partition boundary, "alert" theory accent |

Each accent has a paired *subtle* fill (12–20% alpha) for
containers (`CyanSubtle`, `PurpleSubtle`, `PinkSubtle`, `GreenSubtle`,
`YellowSubtle`). Never pair an accent container with a different
border color — derive the border from the same accent at 30% alpha.

### 2.1 Neutrals

| Token | Hex | Use for |
|---|---|---|
| `workspaceSurface` (`DarkBackground`) | `#0B0F19` | The whole-screen base. Always the bottom layer. |
| `canvasWell` (`CanvasBackground`) | `#060A14` | Sunken layer beneath visualizer content. |
| `glassFill` (`CardBackground`) | `#0C1526` | Default card / panel. |
| `glassElevated` (`CardBackgroundHover`) | `#111D30` | Floating rails, prompts, sheets. |
| `TextPrimary` | `#E2E8F0` | Title text, primary content. |
| `TextSecondary` | `#94A3B8` | Body text, descriptions. |
| `TextMuted` | `#64748B` | Index labels, step counter, secondary meta. |
| `TextDark` | `#334155` | Disabled labels, axis labels. |
| `TextNavy` | `#1E3A5F` | Tertiary separators. |
| `BorderSubtle` | `0x0FFFFFFF` (6% white) | Default 1dp border on cards. |
| `BorderMedium` | `0x1AFFFFFF` (10% white) | Hover / focused border. |
| `BorderCyan` | `0x3800E5FF` (~22% cyan) | Accent-tinted border for selected state. |

### 2.2 Color rules

- **Do not** use `PrimaryCyan` as a body-text color. It is for
  borders, fills, and selected accents.
- **Do not** use `PurpleGlow` for a background. It is a *text* color
  on a `PurpleSubtle` container.
- **Do not** use `BarUnsorted` (the only "bar-state" color) for
  anything outside the 1D bar renderer.
- Always test your color combination on a real device. The two
  "subtle" fills (`CyanSubtle` and `GreenSubtle`) are visually
  similar at 12% alpha; rely on the *border* (not the fill) to
  distinguish them.
---

## 3. Spacing — the 4dp grid

`AlgoTokens.space*` is the only spacing system. There is a 4dp base
and seven named steps:

| Token | Value | When to reach for it |
|---|---|---|
| `space1` | 2dp | Hairline vertical gap inside a tightly packed chip |
| `space2` | 4dp | Padding inside a 32dp icon button; gap between icon and label in a pill |
| `space3` | 6dp | Default row spacing inside a header / rail |
| `space4` | 8dp | Padding around chip text; gap inside a card body |
| `space5` | 12dp | Padding between icon button and an adjacent icon button |
| `space6` | 16dp | Card padding, section margin |
| `space7` | 24dp | Spacing between major page sections (rare) |
| `space8` | 32dp | Top-of-screen hero spacing |

**Rules:**

- A horizontal row of icon buttons uses `space5` (12dp) between
  buttons and `space4` (8dp) inside each.
- A pill with leading icon uses `space4` horizontal padding and
  `space2` vertical padding, with `space2` between icon and label.
- Never write a raw `4.dp` or `8.dp` in a `Modifier.padding` /
  `Modifier.spacedBy` argument — use the `AlgoTokens` constant.
- The 4dp grid is **strict**. If you need 10dp, you are spacing two
  components that should be merged or split.

---

## 4. Radii — five steps, two surfaces

| Token | Value | When |
|---|---|---|
| `radiusXs` | 4dp | Inline pill, status chip |
| `radiusSm` | 8dp | Segmented toggle, control rail |
| `radiusMd` | 12dp | Default card / panel |
| `radiusLg` | 16dp | Modal sheet, large panel |
| `radiusXl` | 24dp | Hero card, onboarding card |

**Two-surface rule:** nested surfaces must use **at least two
radius steps apart** (e.g. a `radiusMd` card can contain `radiusSm`
chips, but a `radiusMd` card containing `radiusXs` chips looks
visually busy). The exception is the segmented toggle inside a chip
— that one is intentionally nested close.

---

## 5. Strokes

| Token | Value | Use for |
|---|---|---|
| `strokeHairline` | 0.5dp | Ambient surface-to-surface dividers |
| `strokeThin` | 1dp | Default card border, icon-button border, pill border |
| `strokeActive` | 2dp | Selected/focused cell, active step border, slider thumb ring |

The "subtle" pair:

| Token | Color |
|---|---|
| `strokeBorderSubtle` | `BorderSubtle` (6% white) |
| `strokeBorderMedium` | `BorderMedium` (10% white) |

Use `strokeBorderSubtle` by default. Promote to `strokeBorderMedium`
on hover/focus states, or to `strokeActive` for the active step.

**Anti-pattern:** stacking two `strokeThin` borders instead of one
`strokeActive` border.

---

## 6. Component sizing

| Token | Value | Use for |
|---|---|---|
| `minTouchTarget` | 44dp | Hard floor for any tappable control |
| `iconButtonSm` | 30dp | Secondary icon-only control (help, theory, back) |
| `iconButtonMd` | 32dp | Default icon-only control (reset, step +/-) |
| `iconButtonLg` | 36dp | Primary transport (play / pause) |
| `inlineIconSm` | 12dp | Icon inside a pill (CUSTOMIZE, TIME chip) |
| `inlineIconMd` | 14dp | Icon next to a label (challenge, tutor) |
| `inlineIconLg` | 18dp | Large inline icon (the play / pause glyph itself) |

**Rule:** every icon button must use **one of** `iconButtonSm` /
`iconButtonMd` / `iconButtonLg`. The icon glyph inside must be
`inlineIconSm` / `inlineIconMd` / `inlineIconLg` respectively. Do
not pair `iconButtonSm` with `inlineIconLg` (looks pinched) or
`iconButtonLg` with `inlineIconSm` (looks empty).

---

## 7. Typography roles

The full scale lives in `Type.kt`. There are four *jobs* the
visualizer cares about:

| Job | Style | Used by |
|---|---|---|
| **Title** | `titleMedium`, Bold, `letterSpacing = 1.sp` | Algorithm name in header |
| **Body** | `bodySmall`, Normal | Description banner, code-trace body |
| **Label (small)** | `labelSmall`, Bold | Every pill, chip, status text |
| **Label (micro)** | `labelSmall`, `fontSize = 7–8.5.sp` | Index labels, tab labels, density-bound small text |

`AlgoLensTypography` is built on JetBrains Mono for everything
except icons. Do not swap to a sans for "legibility" — the
monospace is the IDE metaphor. Title-case strings in headers,
UPPER-CASE for chip labels (TIME, SPACE, CUSTOMIZE).

---

## 8. Motion

Motion is **one of three** spring profiles. Pick the right one
for the right job — never invent a new spring spec on the spot.

| Spec | Purpose | When |
|---|---|---|
| `panelSpring` | Expanding / collapsing panels | Sheet shows, theory drawer slides, segmented toggle resize |
| `cellTravelSpring` | Cell "pop up, shift, settle" travel | The swap-arc lift/glide phase |
| `evalSpring` | Cell "being evaluated" 1.1–1.2x pop | Hover, comparing, found |
| `pointerSpring` | Pointer badge hop between slots | Pivot / min / max chase |
| `lineTrackSpring` | Sliding active-line pill in code trace | Code-trace sync to canvas step |
| `liftSpring` / `settleSpring` | Swap arc two-phase | Pair with `cellTravelSpring` for lift → glide → settle |

Other rules:

- **200–300ms is the maximum** for any single tween. The fastest
  perceptible animation in the app is 100ms (slider drag).
- **`disabledAlpha` is 0.38f** (`AlgoTokens.disabledAlpha`). Use
  the `enabled` parameter on `RailIconButton`; the component
  applies the alpha for you. Don't hand-roll `.alpha(0.38f)`.
- **Never animate two spring properties at once** on the same
  composable — it reads as jittery. If you need to animate size
  + color, use the spring on size and `animateColorAsState` for
  color.

### 8.1 Cold-start boot sequence

The OS-level splash (`Theme.AlgoLens.Splash`, extending
`Theme.SplashScreen` from `androidx.core:core-splashscreen`) shows
a dark `bg_dark` background + a 1.5dp `traversal-cyan` **Bolt** icon —
the same glyph the dashboard uses for its header logo
(`Icons.Default.Bolt`). It is held in place by
`SplashScreen.setKeepOnScreenCondition` until the in-Compose
`BootOverlay` has rendered its first frame and a `LaunchedEffect`
countdown (~300ms) has elapsed. The overlay crossfades out via
`panelFadeSpring` into `AlgoLensApp` — the dashboard mounts into
the same dark workspace the splash revealed.

- **Boot mark:** a 1.5dp Material **Bolt** glyph + a small 1dp
  accent dot just outside its upper-right tip, all in
  `traversal-cyan`. The same glyph that appears next to "AVIA"
  on the dashboard, scaled ×1.35 from a 24×24 viewport into the
  108×108 splash viewport — final bbox occupies ~25% of the
  viewport so the cold-start icon reads as a small, well-proportioned
  logo with generous negative space around it. Same stroke style as
  `ic_zap.xml` (rounded caps and joins). Reused by `ic_boot_mark.xml`
  (splash) and `BootOverlay`'s `BootMarkCanvas` (in-Compose, sized
  via `AlgoTokens.bootMarkSize = 32.dp`).
- **Hold duration:** 300ms (`BootController.DEFAULT_HOLD_MS`).
  Short on purpose — long enough for the bolt to register, short
  enough that launch doesn't read as laggy. This is the one place
  a `tween` with linear easing is permitted (the progress bar fill,
  which is a status indicator, not decoration).
- **State backing.** `BootController.ready` is backed by
  `mutableStateOf(false)` so any Composable that reads it registers
  a snapshot dependency and recomposes when `markReady()` flips
  the flag. Using a plain `var Boolean` would freeze the overlay
  on the "loading workspace" frame because Compose never observes
  the transition — that bug is locked in by
  `BootControllerTest.ready_isBackedByMutableState`.
- **No new motion spec.** The boot fade-out reuses
  `panelFadeSpring` (Float-typed twin of `panelSpring`,
  600ms ceiling) via
  `AnimatedContent(fadeIn + fadeOut(animationSpec = panelFadeSpring))`.

---

## 9. Surfaces and elevation

There are four canonical surface tokens and the elevation values
that sit on top of them:

| Surface | Use for | Elevation |
|---|---|---|
| `workspaceSurface` | Whole-screen base | 0 |
| `canvasWell` | Visualizer content well | 0 (it *is* the well) |
| `glassFill` | Cards, panels, segmented toggles | `elevationRaised` (2dp) |
| `glassElevated` | Floating rails, prompts, sheets | `elevationFloating` (8dp) |

`elevationTraveling` (18dp) is reserved for **in-flight cells**
during the swap arc. It should never be used as a static elevation.

`AmbientGlowDivider` uses `backdropBlur` (16dp) for the theory-
drawer backdrop. This is the only blur we use.

---

## 10. Component catalog (build from these — don't reinvent)

These are the components you should reach for. If you are about to
write a `Box` with the same shape as one of these, *use the
component instead* — duplication has already cost us 3 weeks of
dead `BarVisualizer` code.

| Component | File | Use for |
|---|---|---|
| `AlgoCard` | `ui/components/AlgoCard.kt` | Dashboard list rows |
| `GlassSurface` | `ui/components/GlassPanel.kt` | Tokenised glass card |
| `AlgoWorkspaceBackground` | `ui/components/GlassPanel.kt` | The whole-screen ambient background |
| `AmbientGlowDivider` | `ui/components/GlassPanel.kt` | The single pulse divider |
| `SectionLabel` | `ui/components/CommonComponents.kt` | Section headers (e.g. RESULTS, BOOKMARKED) |
| `OfflineBadge` / `ComplexityCard` | `ui/components/CommonComponents.kt` | Status / info rows |
| `RailIconButton` | `ui/components/WorkspaceControls.kt` | The only icon button. `boxSize` and `iconSize` are `Dp`, not `Int`. |
| `IconPillButton` | `ui/components/WorkspaceControls.kt` | Inline label with optional leading icon |
| `SegmentedToggle` | `ui/components/WorkspaceControls.kt` | 2+ option pill switch (cells/bars, tree/array, directed/undirected) |
| `VisualizerHeader` | `ui/visualizer/VisualizerHeader.kt` | The workspace top region |
| `VisualizerHost` | `ui/visualizer/VisualizerHost.kt` | Family-aware canvas dispatcher |
| `PlaybackRail` | `ui/visualizer/PlaybackRail.kt` | The bottom transport row |
| `VisualizerScreen` | `ui/visualizer/VisualizerScreen.kt` | The thin shell that composes the regions |
| `VisualizerScreenState` | `ui/visualizer/VisualizerScreenState.kt` | The state machine the regions read |

**Don't** add new icon buttons / pills / cards without first
asking "could this be one of the existing components with different
parameters?". The list above is the answer to "how do I make X
look right?" in 90% of cases.

---

## 11. Layout grammar

The visualizer is built from one **Column** with this exact shape:

```
┌─ VisualizerHeader (auto-height) ────────────────┐
│  ┌─ Canvas Box (weight 55f) ────────────────────┐  ← VisualizerHost
│  ├─ AmbientGlowDivider (2dp) ───────────────────┤
│  ├─ Code Trace Box (weight 45f) ────────────────┤  ← CodeTracePane
│  └─ PlaybackRail (auto-height) ────────────────┘
```

`statusBarsPadding()` and `navigationBarsPadding()` are applied
once on the outer `Column` — don't reapply them inside the regions.

The canvas Box uses `Alignment.TopCenter` for the `AnimatedVisibility`
challenge prompt so it sits over the canvas without being clipped
by the region.

The `Modifier.smoothPanelExpansion()` from `Theme.kt` is the only
way to animate region height changes (header resize on mode-toggle,
code trace collapse on variable-inspector tap).

---

## 12. Modal and overlay grammar

The bottom sheets, side drawers, and overlay tour follow three
rules:

1. **Drawn outside the blur.** `AlgoWorkspaceBackground` is the
   only composable that owns the blur; modals are siblings of the
   blurred content so they stay sharp.
2. **State lives in `VisualizerScreenState`.** Sheet visibility
   booleans are `var showTheorySheet: Boolean`, not a `remember`
   in each sheet.
3. **Don't re-instantiate on every recomposition.** The modals are
   *always* composed; they observe their own `isVisible` prop and
   animate in/out with `AnimatedVisibility`. This keeps the enter
   animation smooth even when the parent recomposes for unrelated
   reasons.

---

## 13. Accessibility

- Every tappable `IconButton` has a `contentDescription`. If the
  control's purpose is obvious from its icon (e.g. a play triangle),
  use a short, sentence-case description: `"Play"`, not `"Play the
  algorithm animation"`.
- Color is never the *only* channel. The challenge mode uses a
  pulsing **border** *and* a "TARGET" pill, not just a green cell
  fill.
- Touch targets meet the `minTouchTarget` (44dp) floor whenever
  the control is *primary*. Secondary icon buttons in the rail
  (`30dp`) are a deliberate, documented exception; the rail's
  overall height + spacing means the user can still hit them
  comfortably.
- All animations are `tween` or `spring` based — no linear /
  immediate transitions. The slowest is 600ms (`panelSpring`);
  the fastest is 100ms (slider drag).

---

## 14. Visual state mapping

When a `VisualizerStep` describes an element, it carries an
`ElementState`. The mapping from state to color is *the same*
everywhere — in cells, bars, graph nodes, and buffer items:

| ElementState | Color | When |
|---|---|---|
| `IDLE` | `BarUnsorted` / `CardBackground` | Default, nothing happening |
| `COMPARING` | `accentYellow` | Currently being inspected against a target |
| `SWAPPING` | `accentPink` | Mid-swap, in transit |
| `SORTED` | `accentGreen` | Locked into final position |
| `ACTIVE` | `accentCyan` | General "in flight" (BFS queue head, recursion entry) |
| `VISITED` | `accentGreen` (filled) / `PrimaryCyan` (border) | BFS/DFS visited |
| `FOUND` | `accentGreen` (filled + border) | Search hit |
| `PIVOT` | `accentPurple` | Quick-Sort pivot, Mid index |
| `TARGET` | `accentPurple` | Binary search target, recursion key |

Don't introduce a new color for a state. If the current mapping is
wrong, update **one place** (`AlgoTokens` / the renderer that
reads it) — never the individual cell.

---

## 15. Algorithm-specific additions

When a new algorithm needs a visual that the family renderer
doesn't have, **do not** edit the renderer. Instead:

1. Add a `VisualizerOverlay` (Phase 4) that lays a new composable
   on top of the canvas inside `VisualizerHost`. The renderer
   stays untouched.
2. Attach the overlay list to the `AlgorithmSpec` so it's
   declarative ("this algorithm gets this overlay").
3. If the overlay needs to react to taps or sync with the code
   trace, route its input through `VisualizerScreenState` so it
   stays in lockstep with the playback loop.

**Example:** a recursion-tree overlay for Merge Sort would be a
`VisualizerOverlay` that reads `step.recursionDepth` from the
`VisualizerStep` and draws a tree in the corner — `VisualizerHost`
just stacks it on top of the canvas.

---

## 16. What to do when a guideline doesn't fit

1. Open a discussion first. "There's no guideline for X" is a real
   case but the answer is usually "you're solving a different
   problem".
2. If the new pattern is genuinely novel, **add a guideline**, not
   a special case. Update this file in the same PR.
3. If a guideline is actively wrong, change the guideline and the
   code together — never ship a one-off that breaks the rule
   silently.

---

## 17. Instrument system (v3 — "Calibrated Instrument")

The seven additions below are the system's instrumentation layer. They
are as binding as §2–§14; they exist because the app must read as a
machined developer tool, not as a themed Material app.

### 17.1 Type scale (five steps, nothing else)

| Step | Size / line | Weight | Tracking | Use |
|---|---|---|---|---|
| `display` | 28sp / 34sp | Bold | −0.03em | Screen titles, wordmark, hero numerals |
| `title` | 16sp / 21sp | SemiBold | −0.01em | Region headers |
| `body` | 12sp / 17sp | Normal | 0 | Prose, complexity values, card names |
| `label` | 10sp / 14sp | SemiBold | 0.08em | Section labels, pills, meta |
| `micro` | 9.5sp / 13sp | Normal | 0 | **Canvas-internal numerals only** |

- The scale lives in `ui/theme/Type.kt` (`AlgoLensTypography` +
  `AlgoType`). **A call site that writes `fontSize = …` is a bug.**
  Use a slot, or `AlgoType.<step>Size` when a `Canvas`/`TextMeasurer`
  needs a raw size.
- **Persistent-text floor is 10sp.** Canvas numerals may reach 9.5sp
  and never lower. `ui/theme/Type.kt` is the only file allowed to
  contain an sp literal.
- Section labels are **sentence case**. No ALL-CAPS subheaders, and no
  letter-spacing overrides at the call site (tracking is in the step).

### 17.2 Elevation ladder

`surfaceBase` (every screen's bottom layer) → `surfaceWell` (the
visualizer canvas only) → `surfaceSunken` (outer bezel shells, pressed
panels) → `surfaceCard` (card / panel cores) → `surfaceFloat`
(floating rails, sheets, bottom nav).

A screen base is **never** the well, and a card is never the same rung
as the surface it sits on. Depth comes from stroke + rung change, not
from shadow: `elevationRaised` / `elevationFloating` remain reserved
for in-flight cells.

### 17.3 Scanline

`AlgoWorkspaceBackground` paints one **static** scanline pass: 3% white,
2dp period, drawn once, behind all content. It is the only texture in
the system. Never animate it, never add a second pattern, and never
attach it to a scrolling container.

### 17.4 AlgoGlyphs (iconography)

`ui/components/AlgoGlyphs.kt` is the entire app icon set: 24dp grid,
1.5dp stroke, round caps/joins, **no fills**. Material icons are
**banned in app UI** — the only exception is OS-contract art (launcher
and splash drawables). Every glyph is rendered with an explicit `tint`;
`contentDescription` stays sentence-case.

### 17.5 Double-bezel surfaces

Every card, tile and row is a **plate in a tray**: an outer shell
(`surfaceSunken`, its own radius + hairline), a 2dp inset, then a core
plate (`surfaceCard`, inner hairline) carrying the content. Use
`DoubleBezelShell`, or reproduce the shell/core pair inline as
`AlgoCard` does. Flat single-border cards are a §17.5 violation.

### 17.6 Meters and ruler ticks

- Progress is a **tick meter** (`InstrumentMeter`), never a stock
  Material indicator. It reads its progress through a lambda so the
  transport can advance it without recomposition.
- Panel edges and canvas headers may carry an `InstrumentRule`:
  4dp minor ticks, 20dp major tick every fifth mark, on a 22%-alpha
  accent hairline.

### 17.7 Motion contract

- **Press physics on every tappable**: 4% scale-down plus an accent
  border bloom 0.30 → 0.60, both on `pressSpring`
  (`Modifier.pressPhysics`).
- **Entry cascade**: list and grid content rises 12dp and fades in,
  staggered 40ms per item over 220ms (`Modifier.entryCascade`).
  Content never mounts statically; transform + opacity only.
- **Ambient loops are functional.** The canvas/trace divider breathes
  **only while `isPlaying`**; paused and idle hold the static baseline
  glow.
- Nothing outside 100–600ms. No linear easing (the sole exception
  remains the cold-start boot progress indicator).
