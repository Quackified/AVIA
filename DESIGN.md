---
name: AlgoLens
description: A dark, tokenised IDE workspace for visualising 13 canonical CS algorithms in lockstep with their source code.
colors:
  # ── Surfaces (sunken → floating) ──
  workspace-surface: "#0B0F19"        # DarkBackground — the whole-screen base
  canvas-well: "#060A14"              # CanvasBackground — the visualizer well, deeper than the base
  glass-fill: "#0C1526"               # CardBackground — default card / panel
  glass-elevated: "#111D30"           # CardBackgroundHover — floating rails, prompts, sheets
  glass-sunken: "#080D1B"             # CardBackgroundElevated — pressed / inverted panels

  # ── Accent (functional, never decorative) ──
  traversal-cyan: "#00E5FF"           # PrimaryCyan — active read pointers, traversal head
  tracking-purple: "#8B5CF6"          # SecondaryPurple — secondary tracking, auxiliary state, code-trace active line
  mutation-pink: "#FF3366"            # AccentPink — comparisons, swaps, active mutations
  verified-green: "#00E676"           # AccentGreen — sorted tail, found element, correct challenge answer
  pivot-yellow: "#FFB800"             # AccentYellow — pivot, threshold, edge alert
  warn-red: "#FF4B4B"                 # AccentRed — error / danger
  data-orange: "#FB923C"              # AccentOrange — data-structure family (Stack / Queue / BST / Heap)

  # ── Accent "bright" variants (used in bright container pairs) ──
  cyan-bright: "#22D3EE"
  purple-bright: "#A78BFA"
  green-bright: "#4ADE80"
  yellow-bright: "#FBBF24"
  pink-bright: "#F43F5E"
  red-bright: "#F87171"

  # ── Text (four steps of emphasis) ──
  text-primary: "#E2E8F0"
  text-secondary: "#94A3B8"
  text-muted: "#64748B"
  text-dark: "#334155"
  text-navy: "#1E3A5F"

  # ── Borders (translucent whites + one accent border) ──
  border-subtle: "#0FFFFFFF"          # 6% white — default card border
  border-medium: "#1AFFFFFF"          # 10% white — hover / focused border
  border-cyan: "#3800E5FF"            # ~22% cyan — selected / accent-tinted border

  # ── Bar-state (1D renderer only; never re-used as a general token) ──
  bar-unsorted: "#1A2E50"             # the only "state by hue" color
  bar-compare: "{colors.pivot-yellow}"
  bar-swap: "{colors.warn-red}"
  bar-sorted: "{colors.traversal-cyan}"
  bar-pivot: "{colors.tracking-purple}"
  bar-pointer: "{colors.pivot-yellow}"

typography:
  display:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 700
    fontSize: "28sp / 34sp line"
    letterSpacing: "-0.03sp"
  title:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 600
    fontSize: "13sp / 18sp line"
  body:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 400
    fontSize: "11sp / 16sp line"
  label:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 700
    fontSize: "8sp / 11sp line"
    letterSpacing: "0.12sp"

rounded:
  xs: "4dp"   # inline pill, status chip
  sm: "8dp"   # segmented toggle, control rail
  md: "12dp"  # default card / panel
  lg: "16dp"  # modal sheet, large panel
  xl: "24dp"  # hero card, onboarding card

spacing:
  s1: "2dp"   # hairline vertical gap inside a chip
  s2: "4dp"   # padding inside 32dp icon button; gap between icon and label in a pill
  s3: "6dp"   # default row spacing inside a header / rail
  s4: "8dp"   # padding around chip text; gap inside a card body
  s5: "12dp"  # padding between adjacent icon buttons
  s6: "16dp"  # card padding, section margin
  s7: "24dp"  # spacing between major page sections
  s8: "32dp"  # top-of-screen hero spacing

components:
  # The three controls that are reused across every screen.
  # Stitch's 8-prop component limit; full snippets are in the sidecar.
  rail-icon-button:
    backgroundColor: "{colors.glass-fill}"
    textColor: "{colors.text-secondary}"
    rounded: "{rounded.xs}"            # visual radius; actual shape is circular
    size: "30dp / 32dp / 36dp (sm/md/lg) — single value required; use the sm/md/lg variants"
  icon-pill-button:
    backgroundColor: "{colors.cyan-fill}"  # accent container (12–20% alpha) — accent varies
    textColor: "{colors.traversal-cyan}"   # accent text on the container
    rounded: "{rounded.xs}"
    padding: "4dp vertical / 8dp horizontal"
  segmented-toggle:
    backgroundColor: "{colors.glass-fill}"
    textColor: "{colors.text-muted}"
    rounded: "{rounded.sm}"
    size: "auto / 28dp tall"
---

# Design System: AlgoLens

## Overview

**Creative North Star: "The Late-Night IDE."**

AlgoLens opens the way a developer opens their editor: same monospace, same accent grammar, same transport controls, no marketing chrome. The user is treated as someone who already lives in an IDE — the canvas is the work surface, the code trace is the spec sheet next to it, the playback rail is the tool. The aesthetic is *working late on a hard problem*, not *opening a tutorial app*. Every screen earns its place by getting the user to the visualizer faster and back out cleanly.

The system is dark, tokenised, and built on a 4dp grid. Color is *functional*, never decorative — five semantic accents map to five jobs (read head, tracking, mutation, verified, pivot), and the same accent pair (12–20% container fill + 30% border) is used everywhere. State is communicated by *border pulse + pill*, not by a green cell fill alone. Motion is one of three named springs; nothing is linear. The whole product reads as one continuous workspace rather than a stack of cards.

The two non-code documents that define this world are `docs/UI_GUIDELINES.md` (16 sections, the binding design contract) and `docs/ALGORITHM_AUTHORING.md` (the per-algorithm contract). This file is the index; the two docs are the source of truth on edge cases.

**Key Characteristics:**

- Dark-only. The `DarkColorScheme` is the only scheme. There is no light-mode override, no warm-sepia reading variant, no auto-theme toggle.
- Monospace everywhere. JetBrains Mono at every text role — display, body, code, label. No sans for "legibility."
- Five functional accents with behavioral names. Each accent has a job (Traversal / Tracking / Mutation / Verified / Pivot) and a paired subtle fill + 30%-alpha border.
- Tokenised, never hard-coded. Every color, dp, and animation reads from `AlgoTokens`; raw `Color(0xFF…)` and raw `*.dp` in `Modifier.padding` are banned outside `ui/theme/Color.kt` and the visualizer exception list.
- State hoisted, regions narrow. `VisualizerScreenState` is the only state owner; the screen body is declarative.
- Playback is IDE-shaped. The transport row is a 32dp rail of circular icon buttons; the canvas is a single well beneath it; the active code line slides in lockstep.
- One visualizer family dispatch. `VisualizerHost` is the only `when (family)` block; algorithm-specific additions attach as overlays, never as screen-level switches.

## Colors

The palette is a deep tech-noir IDE workspace. Five functional accents carry every signal; the four neutrals carry the chrome. Color is always paired (container fill + border derived from the same accent); color is never the only channel.

### Accent (functional, never decorative)

- **Traversal Cyan** (`#00E5FF`): the read head. Active traversal (BFS queue head, recursion entry, scanning), the scrubber thumb, the header title accent, the default focus border. Container pair: `CyanSubtle` (12% alpha) + 30% cyan border.
- **Tracking Purple** (`#8B5CF6`): secondary tracking. Right-pointer in BFS, key element in insertion sort, the code-trace active-line pill, the AI Tutor icon. Container pair: `PurpleSubtle` + 30% purple border.
- **Mutation Pink** (`#FF3366`): comparisons, swaps, active mutations. The swap pair tint, the "incoming swap" badge, Challenge mode active pill. Container pair: `PinkSubtle` + 30% pink border.
- **Verified Green** (`#00E676`): the verified-sorted state and the correct challenge answer. Sorted tail, found element, BFS/DFS visited nodes, theory-sheet accents. Container pair: `GreenSubtle` + 30% green border.
- **Pivot Yellow** (`#FFB800`): pivots, thresholds, edge alerts. Quick-Sort pivot, min-index, partition boundary, "alert" theory accent. Container pair: `YellowSubtle` + 30% yellow border.
- **Warn Red** (`#FF4B4B`): error / danger. Snackbar errors, broken-state badges. Container pair: `RedSubtle` + 30% red border.
- **Data Orange** (`#FB923C`): the data-structure family. Stack, Queue, BST, Heap. Container pair: `OrangeSubtle` + 30% orange border.

Bright variants (`CyanBright` `#22D3EE`, `PurpleBright` `#A78BFA`, `GreenBright` `#4ADE80`, `YellowBright` `#FBBF24`, `PinkBright` `#F43F5E`, `RedBright` `#F87171`) exist for the rare case where a brighter on-color is needed against a subtle container. They are not new accents; they are brightness shifts of the same roles.

### Neutral (surfaces, text, borders)

- **Workspace Surface** (`#0B0F19`): the whole-screen base. Always the bottom layer; nothing draws behind it.
- **Canvas Well** (`#060A14`): the visualizer well, *deeper* than the base. It is the layer beneath visualizer content; sunks the data into the chrome.
- **Glass Fill** (`#0C1526`): default card / panel. Default `CardBackground` on `surface` and `surfaceContainer` roles.
- **Glass Elevated** (`#111D30`): floating rails, prompts, sheets. The only `surfaceContainerHighest` value.
- **Glass Sunken** (`#080D1B`): pressed / inverted panels. Used sparingly; the cue is "below the default card."
- **Text Primary** (`#E2E8F0`): titles, primary content.
- **Text Secondary** (`#94A3B8`): body, descriptions.
- **Text Muted** (`#64748B`): index labels, step counter, secondary meta.
- **Text Dark** (`#334155`): disabled labels, axis labels.
- **Text Navy** (`#1E3A5F`): tertiary separators.
- **Border Subtle** (`rgba(255,255,255,0.06)`): default 1dp border on cards.
- **Border Medium** (`rgba(255,255,255,0.10)`): hover / focused border.
- **Border Cyan** (`rgba(0,229,255,0.22)`): accent-tinted border for selected state.

### Bar-state (1D renderer only)

These five hex values are the only colors used in the bar renderer. They are *state* colors, not general-purpose accents; they do not appear anywhere else.

- **Bar Unsorted** (`#1A2E50`): default, nothing happening.
- **Bar Compare** (`#FFB800` → `pivot-yellow`): currently being inspected.
- **Bar Swap** (`#FF4B4B` → `warn-red`): mid-swap, in transit.
- **Bar Sorted** (`#00E5FF` → `traversal-cyan`): locked into final position.
- **Bar Pivot** (`#8B5CF6` → `tracking-purple`): pivot / min-index.

### Named Rules

**The Functional-Accent Rule.** Every accent color has a job. If a use case does not fit one of the five roles (Traversal / Tracking / Mutation / Verified / Pivot), it does not get a new color; it gets the role that fits best, and the inconsistency is fixed at the role level. The two color escapes — `Warn Red` for error and `Data Orange` for the data-structure family — are also functional, not decorative.

**The Paired-Container Rule.** A subtle accent fill is always paired with a border derived from the same accent at 30% alpha. CyanSubtle ↔ BorderCyan. GreenSubtle ↔ a 30% green border. The fill *and* the border agree, or the design is wrong.

**The Color-Is-Never-Only Rule.** State is communicated by border pulse *and* pill, not by color alone. Challenge mode uses a pulsing border *and* a "TARGET" pill. A green cell fill with no border does not communicate "verified."

**The No-Body-Text-Cyan Rule.** `PrimaryCyan` is for borders, fills, and selected accents. It is never used as a body-text color. `PurpleGlow` is a *text* color on a `PurpleSubtle` container; it is never a background.

## Typography

**Display Font:** JetBrains Mono (the system variable font, loaded from `R.font.jetbrains_mono_variable`).

**Body Font:** JetBrains Mono — the same family. There is no secondary body family.

**Label / Mono Font:** JetBrains Mono. Every pill, chip, and density-bound micro label is the same monospace.

**Character:** technical, even, monospace. Title-case for headers, UPPER-CASE for chip labels (`TIME`, `SPACE`, `CUSTOMIZE`). The monospace is the IDE metaphor — do not swap to a sans for "legibility." Letterspacing is slightly tightened on display and headline roles (-0.01 to -0.03 sp) and loosened on label roles (+0.05 to +0.12 sp) so that all-caps labels read as deliberate, not shouting.

### Hierarchy

- **Display** (Bold, 28sp / 34 line, -0.03 letter-spacing): the algorithm name in the hero state. Used once per screen at most.
- **Title** (SemiBold, 13sp / 18 line): section headers, card titles. The workhorse.
- **Body** (Normal, 11sp / 16 line): description banners, code-trace body, paragraph copy.
- **Label (small)** (Bold, 9sp / 13 line, +0.08 letter-spacing): every pill, chip, status text.
- **Label (micro)** (Bold, 8sp / 11 line, +0.12 letter-spacing): index labels, tab labels, density-bound small text. The 7–8.5sp range is the visualizer's "code-trace gutter" and is used only there.

### Named Rules

**The Monospace-Everywhere Rule.** JetBrains Mono is the only family. No sans for headings, no serif for emphasis, no system fallback. If a label needs to be more legible, the fix is *bigger* (move up a step) or *darker* (move to `TextPrimary`), not a font swap.

**The Uppercase-Label Rule.** Pill / chip / status text is UPPER-CASE; header titles are Title-Case. The two cases carry different jobs: UPPER-CASE reads as a *tag*, Title-Case reads as a *name*. Mixing the two on a single element breaks the signal.

## Layout

The visualizer is built from one `Column` with this exact shape:

```
┌─ VisualizerHeader (auto-height) ────────────────┐
│  ┌─ Canvas Box (weight 55f) ────────────────────┐  ← VisualizerHost
│  ├─ AmbientGlowDivider (2dp) ───────────────────┤
│  ├─ Code Trace Box (weight 45f) ────────────────┤  ← CodeTracePane
│  └─ PlaybackRail (auto-height) ────────────────┘
```

`statusBarsPadding()` and `navigationBarsPadding()` are applied once on the outer `Column`; they are not reapplied inside the regions. The canvas Box uses `Alignment.TopCenter` for the `AnimatedVisibility` challenge prompt so it sits over the canvas without being clipped by the region.

**Spacing rhythm:** the 4dp grid is strict. The eight `space1`–`space8` tokens (2dp, 4dp, 6dp, 8dp, 12dp, 16dp, 24dp, 32dp) are the only spacing values used. A horizontal row of icon buttons uses `space5` (12dp) between buttons and `space4` (8dp) inside each. A pill with leading icon uses `space4` horizontal padding and `space2` vertical padding, with `space2` between icon and label.

**The 4dp grid is strict.** If a design needs 10dp, the components should be merged or split. The grid is not a guideline; it is the rule.

**Page frame:** `AlgoTokens.framePadding` (10dp horizontal, 6dp vertical) is the uniform page-level padding for the workspace. Cards and panels use `space6` (16dp) inside.

**Density:** the visualizer is *compact* by default. Body text is 11sp, labels are 8–9sp, the playback rail is 30–36dp. The dashboard is the only place that allows 13sp body and 24dp card padding — and that exception is the visualizer's "rest state," not a norm.

**Responsive behavior:** the app is Android-phone-only (compileSdk 37, minSdk 24). Edge-to-edge with custom status / navigation bar tinting. There is no tablet or foldable layout; there is no size-class awareness. If a future change adds it, the `Modifier.smoothPanelExpansion()` extension is the only sanctioned way to animate region height changes (header resize on mode toggle, code-trace collapse on variable-inspector tap).

## Elevation & Depth

Elevation is conveyed by **tonal layering** (sunken workspace → glass surface → elevated glass → traveling cells) and by **one ambient glow** between regions. There are no drop shadows on cards. The system is flat-by-default and lifts only when a component is in motion.

The four canonical surface tokens and their elevations:

| Surface | Use for | Elevation |
|---|---|---|
| `workspace-surface` | whole-screen base | 0 (it *is* the base) |
| `canvas-well` | visualizer content well | 0 (it *is* the well — deeper than the base by hue, not by shadow) |
| `glass-fill` | cards, panels, segmented toggles | `elevationRaised` (2dp) |
| `glass-elevated` | floating rails, prompts, sheets | `elevationFloating` (8dp) |

`elevationTraveling` (18dp) is reserved for **in-flight cells** during the swap arc. It is never a static elevation; using it on a resting element makes the cell look like it is mid-flight when it is not.

The `AmbientGlowDivider` between the canvas and the code trace uses a `backdropBlur` (16dp) for the theory-drawer backdrop. This is the only blur used in the system; do not introduce other blurs.

### Named Rules

**The Flat-By-Default Rule.** Surfaces are flat at rest. The visual hierarchy is hue + border + subtle fill, not shadow. A drop shadow on a card is almost always wrong; if a surface needs to "lift," the fix is `glass-elevated` + `elevationFloating`, not a `box-shadow`.

**The Traveling-Is-A-State Rule.** `elevationTraveling` is *only* for cells in the middle of a swap-arc flight. It is a *state* elevation, not a *component* elevation. Using it on a static cell makes the animation read as "this cell is broken."

## Shapes

Corner radii follow a five-step scale that the entire app uses:

| Token | Value | When |
|---|---|---|
| `radiusXs` | 4dp | inline pill, status chip |
| `radiusSm` | 8dp | segmented toggle, control rail |
| `radiusMd` | 12dp | default card / panel |
| `radiusLg` | 16dp | modal sheet, large panel |
| `radiusXl` | 24dp | hero card, onboarding card |

Icon buttons are **circular** — they use `CircleShape`, not a radius. The shape is a deliberate "tool" signal: round controls read as IDE-transport, not as form fields.

**The Two-Surface Rule.** Nested surfaces must use **at least two radius steps apart** (e.g. a `radiusMd` card can contain `radiusSm` chips, but a `radiusMd` card containing `radiusXs` chips looks visually busy). The exception is the segmented toggle inside a chip — that one is intentionally nested close.

Strokes are three steps: `strokeHairline` (0.5dp) for ambient dividers, `strokeThin` (1dp) for default card / icon-button / pill borders, `strokeActive` (2dp) for selected / focused cells, the slider thumb ring, and the active step border. **Do not stack two `strokeThin` borders** in place of one `strokeActive` border — the result reads as a thicker hairline, not as selected.

## Components

The component catalog in `ui/components/WorkspaceControls.kt` is the answer to "how do I make X look right?" in 90% of cases. If a new need matches an existing component with different parameters, use the existing component — duplication has already cost weeks of dead `BarVisualizer` code.

### RailIconButton (icon button)

- **Shape:** circular (`CircleShape`).
- **Sizes:** `iconButtonSm` 30dp (secondary), `iconButtonMd` 32dp (default), `iconButtonLg` 36dp (primary transport: play / pause). The icon glyph uses `inlineIconSm` 12dp, `inlineIconMd` 14dp, `inlineIconLg` 18dp respectively. Do not pair `iconButtonSm` with `inlineIconLg` (looks pinched) or `iconButtonLg` with `inlineIconSm` (looks empty).
- **Default tint:** `text-secondary` on `glass-fill` with `border-subtle` 1dp. The component owns its own `disabledAlpha` (0.38f); the rail drives `enabled` from `VisualizerScreenState` rather than wrapping each button in a `.alpha(...)`.
- **Touch target:** 30dp is a deliberate, documented exception to the 44dp floor. The rail's overall height + spacing means the user can still hit them comfortably.

### IconPillButton (status pill)

- **Shape:** `radiusXs` (4dp).
- **Padding:** `space4` (8dp) horizontal, `space2` (4dp) vertical; `space2` (4dp) between icon and label.
- **Color:** `accent` text on `accentContainer` (12–20% alpha) with a 1dp border derived from the same accent at 30% alpha. The *meaning* of the pill is set by the accent; the *container / border* pair is set by the same accent. There is no plain "gray pill" variant.
- **Typography:** `labelSmall`, Bold, 7.5sp, all-caps content. `TIME O(n log n)`, `SPACE O(1)`, `CUSTOMIZE`.
- **Leading icon:** `inlineIconSm` - 1dp, tinted `accent`, `contentDescription = null` (the label is the description).

### SegmentedToggle (2+ option switch)

- **Shape:** outer `radiusSm - 2dp` (6dp); inner option `radiusXs - 2dp` (2dp). The two-step radius is intentional — it makes the inner option read as a sliding pill, not a button.
- **Padding:** `space1` (2dp) inside the track, `space1` between options, `space4` horizontal × `space2` vertical inside each option.
- **Selected state:** the option takes the `accent` (default `traversal-cyan`) as its background, with `onContainer` (the workspace surface) as the text color, in Bold. The unselected options are transparent with `text-muted`, Medium.
- **Reuse:** the same component is used for cells-vs-bars, tree-vs-array (Heap), and directed-vs-undirected (BFS / DFS) — any 2+ state "view" toggle.

### Glass surface (card / panel)

- **Shape:** `radiusMd` (12dp) for default, `radiusLg` (16dp) for modal sheets, `radiusXl` (24dp) for hero cards.
- **Background:** `glass-fill`; floating rails and sheets use `glass-elevated`.
- **Border:** 1dp `border-subtle` by default; promote to `border-medium` on hover / focus; promote to `border-cyan` for selected state.
- **Padding:** `space6` (16dp) inside. Chips inside a card drop to `space2` / `space4`; hero cards use `space7` (24dp).
- **No shadow.** Tonal layering is the depth signal.

### BottomNavBar (5-target navigation)

- **Shape:** full-width bar with a 1dp `border-subtle` top divider.
- **Background:** `glass-elevated`.
- **Target count:** 3–5 destinations. AlgoLens ships four: Home, Explore, Profile, Settings. The visualizer is not a target — it is a full-screen route above the bar.
- **Active state:** `traversal-cyan` icon + label, `traversal-cyan` 1dp top border. Inactive targets are `text-muted` at 60% alpha.
- **Tap target:** 48dp tall, full-bleed label below the icon. Honors the 48dp Material floor even though the rail is compact.

### Signature: VisualizerHost + PlaybackRail

- **VisualizerHost** is the single family-aware dispatcher. It reads `spec.id.family` (LINEAR_1D / GRAPH_2D / BUFFER) and composes the right renderer. **The screen no longer has a `when (renderMode)`** — that is the architectural rule.
- **PlaybackRail** is a single horizontal row of `RailIconButton`s (reset, step-back, play / pause, step-forward, speed, fullscreen, theory, challenge, tutor) on `glass-elevated`. The play / pause uses `iconButtonLg`; the rest are `iconButtonMd`. The row is the only bottom-of-screen chrome in the visualizer.

## Do's and Don'ts

The visual rejections in this section are the ones the user has confirmed the system explicitly holds the line on. They are durable, not task-local.

### Do:

- **Do** read the active accent role before picking a color. If the use case does not match a role, change the role, not the color.
- **Do** pair every subtle accent fill with a border from the same accent at 30% alpha. The fill and the border agree.
- **Do** animate using one of the three named springs (`panelSpring`, `cellTravelSpring`, `evalSpring`, `pointerSpring`, `lineTrackSpring`, `liftSpring` / `settleSpring`). The fastest perceptible animation is 100ms (slider drag); the slowest is 600ms (`panelSpring`). Never linear. The one exception is the cold-start boot progress bar (`BootOverlay`), which uses a linear `tween` because it is a status indicator, not decoration.
- **Do** honor the 4dp grid. `space1` through `space8` are the only spacing tokens; if a design needs 10dp, the components are wrong, not the grid.
- **Do** derive every component from `AlgoTokens`. If you find yourself writing a raw `Color(0xFF…)` outside `ui/theme/Color.kt`, stop and add a token.
- **Do** attach per-algorithm gimmicks as `VisualizerOverlay` (Phase 4) on the `AlgorithmSpec`. The renderer is never forked.

### Don't:

- **Don't** introduce a gamified learning aesthetic. No XP bars, streak counters, confetti, character avatars, or bright primary-school colors. The user is a developer, not a child.
- **Don't** introduce marketing-style hero sections. No gradient hero, no abstract marketing imagery, no "Get started in 30 seconds" framing. There is no marketing surface — every screen is a tool.
- **Don't** introduce iOS design language. No Cupertino toggles, no iOS bottom sheets that ignore the system Back gesture, no swiping card stacks. The user is on Android with Material 3 conventions; the system Back gesture and the predictive Back gesture are honored.
- **Don't** add a light-mode path. The dark workspace is the only workspace. `DarkColorScheme` is the only scheme.
- **Don't** stack two `strokeThin` borders in place of one `strokeActive` border. The result reads as a thicker hairline, not as selected.
- **Don't** use `PrimaryCyan` as a body-text color. It is for borders, fills, and selected accents. `PurpleGlow` is a *text* color on a `PurpleSubtle` container; it is never a background.
- **Don't** place `var X by remember { mutableStateOf(...) }` inside `VisualizerScreen.kt`. All visualizer state lives in `VisualizerScreenState`; the screen body is declarative.
- **Don't** branch on `algorithm.name` / `algorithm.id` / `algorithm.category` inside `VisualizerScreen.kt`. Per-algorithm behavior is a field on `AlgorithmSpec`.
