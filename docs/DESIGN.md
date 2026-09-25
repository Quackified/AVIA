---
name: AVIA
description: AVIA (Algorithm Visualizer & Interactive Assistant) — an offline-first, dark tech-noir IDE and precision instrument workspace for visualising 13 canonical CS algorithms in lockstep with four-language source code, interactive practice, and an offline AI tutor. Repo/module/package names remain `AlgoLens`; the user-facing product name is AVIA.
colors:
  # ── Surfaces (5-step M2 elevation ladder + specialized wells) ──
  workspace-surface: "#0B0F19"        # surfaceBase / DarkBackground — every screen's base layer
  canvas-well: "#060A14"              # surfaceWell / CanvasBackground — visualizer well & terminal body
  glass-sunken: "#080D1B"             # surfaceSunken / CardBackgroundElevated — outer bezel trays, bottom nav, titlebars
  glass-fill: "#0C1526"               # surfaceCard / CardBackground — core card plate, default control fill
  glass-elevated: "#111D30"           # surfaceFloat / CardBackgroundHover — floating rails, elevated sheets, hover state
  chip-background: "#0F172A"          # ChipBackground — sunken callout / comparison banner backdrop
  option-unselected: "#1A2540"        # OptionUnselected — unselected switch / toggle track fill

  # ── Functional Accents (semantic, never decorative) ──
  traversal-cyan: "#00E5FF"           # PrimaryCyan / accentCyan — active traversal, read head, primary transport
  tracking-purple: "#8B5CF6"          # SecondaryPurple / accentPurple — secondary pointers, code-trace sync, AI Tutor
  mutation-pink: "#FF3366"            # AccentPink / accentPink — comparisons, active swaps, Challenge Mode
  verified-green: "#00E676"           # AccentGreen / accentGreen — sorted tail, found target, verified answer
  pivot-yellow: "#FFB800"             # AccentYellow / accentYellow — pivot elements, boundary thresholds, edge alerts
  warn-red: "#FF4B4B"                 # AccentRed — error states, hard difficulty, mid-swap bar state
  data-orange: "#FB923C"              # AccentOrange — data structures family (Stack/Queue/BST/Heap), offline/streak badges

  # ── Translucent Accent Containers (~12% alpha, 0x1F) ──
  cyan-fill: "#1F00E5FF"              # CyanSubtle / cyanFill
  purple-fill: "#1F8B5CF6"            # PurpleSubtle / purpleFill
  pink-fill: "#1FFF3366"              # PinkSubtle / pinkFill
  green-fill: "#1F00E676"             # GreenSubtle / greenFill
  yellow-fill: "#1FFFB800"            # YellowSubtle / yellowFill
  red-fill: "#1FFF4B4B"               # RedSubtle
  orange-fill: "#1FFB923C"            # OrangeSubtle

  # ── Accent Bright & Glow Variants ──
  cyan-bright: "#22D3EE"              # CyanBright
  purple-bright: "#A78BFA"            # PurpleGlow — high-contrast text/icon tint on purple-fill containers
  green-bright: "#4ADE80"             # GreenBright
  yellow-bright: "#FBBF24"            # YellowBright
  pink-bright: "#F43F5E"              # PinkBright
  red-bright: "#F87171"               # RedBright
  cyan-glow: "#3300E5FF"              # CyanGlow — ~20% alpha cyan halo
  pink-glow: "#44FF3366"              # AccentPinkGlow — ~27% alpha swap-flight halo

  # ── Neutral Text & Glyph Hierarchy ──
  text-primary: "#E2E8F0"             # TextPrimary — primary titles, active values, code foreground
  text-secondary: "#94A3B8"           # TextSecondary — body prose, secondary icons, descriptions
  text-muted: "#64748B"               # TextMuted — step counters, index labels, inactive tab labels
  badge-muted: "#475569"              # BadgeMuted — fallback pointer badge slate
  text-dark: "#334155"                # TextDark — section labels, inactive bottom-nav items, disabled glyphs
  text-navy: "#1E3A5F"                # TextNavy — dot separators, tertiary axis marks

  # ── Borders & Graph Strokes ──
  border-subtle: "#0FFFFFFF"          # BorderSubtle (6% white) — default 0.5dp/1dp hairline & card border
  border-medium: "#1AFFFFFF"          # BorderMedium (10% white) — hover/focus border & meter ticks
  border-cyan: "#3800E5FF"            # BorderCyan (~22% cyan) — selected/active accent border
  graph-edge-default: "#1E293B"       # GraphEdgeDefault — unhighlighted 2D graph/tree edge stroke

  # ── 1D Bar Renderer State Base ──
  bar-unsorted: "#1A2E50"             # BarUnsorted — resting bar color in 1D BarVisualizer only

typography:
  display:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 700
    fontSize: "28sp"
    lineHeight: "34sp"
    letterSpacing: "-0.03sp"
  headline:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 600
    fontSize: "16sp"
    lineHeight: "21sp"
    letterSpacing: "-0.01sp"
  title:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 600
    fontSize: "16sp"
    lineHeight: "21sp"
    letterSpacing: "-0.01sp"
  title-small:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 500
    fontSize: "12sp"
    lineHeight: "17sp"
    letterSpacing: "0sp"
  body:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 400
    fontSize: "12sp"
    lineHeight: "17sp"
    letterSpacing: "0sp"
  body-small:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 400
    fontSize: "10sp"
    lineHeight: "14sp"
    letterSpacing: "0sp"
  label:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 600
    fontSize: "10sp"
    lineHeight: "14sp"
    letterSpacing: "0.08sp"
  micro:
    fontFamily: "JetBrains Mono, ui-monospace, monospace"
    fontWeight: 400
    fontSize: "9.5sp"
    lineHeight: "13sp"
    letterSpacing: "0sp"

rounded:
  xs: "4dp"           # radiusXs — inline pills, status chips, terminal top-start tab anchor
  xxs: "6dp"          # radiusXxs — compact filter chips, segmented toggle track, onboarding tick indicators
  sm: "8dp"           # radiusSm — secondary square buttons, bottom-nav active pill, speed chip
  bezel-inner: "11dp" # bezelInnerRadius (radiusMd - bezelInset) — core plate inside DoubleBezelShell
  md: "12dp"          # radiusMd / bezelRadius — default card, DoubleBezelShell outer tray, terminal frame
  lg: "16dp"          # radiusLg — modal bottom sheets, large overlays
  xl: "24dp"          # radiusXl — hero onboarding and summary containers
  full: "9999dp"      # CircleShape — RailIconButton, CustomSwitch, status dots

spacing:
  s1: "2dp"           # space1 / bezelGap — hairline gap inside segmented toggles & between outer/inner bezels
  s2: "4dp"           # space2 — vertical chip padding, icon-to-label gap
  s3: "6dp"           # space3 — compact header/rail vertical rhythm, status dot size
  s4: "8dp"           # space4 — horizontal chip padding, transport button spacing
  s5: "12dp"          # space5 — standard horizontal page inset, card internal padding
  s6: "16dp"          # space6 — section padding, modal sheet margin
  s7: "24dp"          # space7 — major section separation, active onboarding tick width
  s8: "32dp"          # space8 / bootMarkSize — hero top spacing, cold-start logo mark size

components:
  rail-icon-button:
    backgroundColor: "{colors.glass-fill}"
    textColor: "{colors.text-secondary}"
    rounded: "{rounded.full}"
    size: "30dp / 32dp / 36dp"
  rail-icon-button-hero:
    backgroundColor: "{colors.traversal-cyan}"
    textColor: "{colors.workspace-surface}"
    rounded: "{rounded.full}"
    size: "42dp"
  icon-pill-button:
    backgroundColor: "{colors.cyan-fill}"
    textColor: "{colors.traversal-cyan}"
    rounded: "{rounded.xs}"
    padding: "4dp 8dp"
  segmented-toggle:
    backgroundColor: "{colors.glass-fill}"
    textColor: "{colors.text-muted}"
    rounded: "{rounded.xxs}"
    padding: "2dp"
  double-bezel-shell:
    backgroundColor: "{colors.glass-sunken}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.md}"
    padding: "2dp"
  algo-card:
    backgroundColor: "{colors.glass-fill}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.bezel-inner}"
    padding: "8dp 12dp"
  bottom-nav-bar:
    backgroundColor: "{colors.glass-sunken}"
    textColor: "{colors.text-dark}"
    rounded: "{rounded.sm}"
    padding: "6dp 4dp"
---

# Design System: AVIA

## Overview

**Creative North Star: "The Late-Night IDE & Precision Instrument."**

AVIA (shipped product name; repository and Kotlin package remain `AlgoLens`) opens the way a developer opens their editor or hardware logic analyzer: a deep tech-noir dark workspace, JetBrains Mono across every typographic role, semantic signal accents, and zero marketing fluff. The user is treated as an operator who already lives in an IDE—the visualizer canvas is the live work surface, the docked 3-line peek/expand terminal (`InstrumentDeck`) is the spec sheet locked in step with execution, and the two-tier `PlaybackRail` is the precision transport tool.

Every surface is engineered like a physical instrument panel. Cards do not float on drop shadows; they sit as inset core plates inside sunken trays (`DoubleBezelShell`), framed by 0.5dp–1dp hairlines, ruler-ticked progress meters (`InstrumentMeter`, `InstrumentRule`), and a bespoke 1.5dp pure-stroke vector icon set (`AlgoGlyphs`). Touch feedback is mechanical rather than decorative: every tappable surface compresses by 4% (`pressScale = 0.96f`) while blooming its 1dp accent border (`0.30f → 0.60f` alpha) on a non-bouncy spring (`Modifier.pressPhysics`).

The entire app—from the orbital harmonic-wave boot sequence (`BootOverlay`) and 3-slide interactive onboarding tour (`OnboardingScreen`) to the four main shell tabs (`Home` catalogue, `Explore` practice engine, `Chat` offline AI assistant, `Profile` operator readout) and the full-screen `VisualizerScreen` and `SettingsScreen`—draws exclusively from `AlgoTokens` and `AlgoType`.

**Key Characteristics:**

- **Dark-only tech-noir palette:** `DarkColorScheme` is the sole theme. No light mode, no warm-sepia mode, and no dynamic OS wallpaper color overrides.
- **Monospace everywhere:** `JetBrains Mono` (`R.font.jetbrains_mono_variable`) governs every text step from 28sp hero displays down to the 9.5sp persistent-text floor (`AlgoType.microSize`), with tabular numerals (`tnum`) on all live counters.
- **Double-bezel instrument architecture:** Premium surfaces (`AlgoCard`, `DoubleBezelShell`, practice cards, AI assistant bubbles, profile bento cells) use a sunken outer tray (`#080D1B`, 12dp radius) with a 2dp gap around an inset core plate (`#0C1526`, 11dp radius).
- **Five semantic accents + two functional escapes:** Color is strictly behavioral (`Traversal Cyan`, `Tracking Purple`, `Mutation Pink`, `Verified Green`, `Pivot Yellow`, plus `Warn Red` for errors/hard difficulty and `Data Orange` for data structures/streaks/offline status).
- **Bespoke 1.5dp stroke iconography (`AlgoGlyphs`):** Hand-authored Lucide-style 24dp vectors with 1.5dp round-cap/round-join strokes and zero fills.
- **Tactile press physics & one-shot entry cascade:** `Modifier.pressPhysics` provides spring-driven compression + border bloom without idle `graphicsLayer` overhead; `EntryCascadeProvider` choreographs only the first 6 visible list items on initial screen mount (`540ms` window) and becomes a zero-allocation no-op during scrolling.
- **Unified Live Narrative Stage + Docked Terminal:** The visualizer stage lifts its canvas smoothly (`160dp` peek ↔ `319dp` expanded) above a docked terminal with attached `Trace` | `State` window tabs while burying the stationary `StageLegend` underneath on expansion.

## Colors

The palette is a high-contrast, low-glare tech-noir workspace where five neutral elevation steps establish structural depth and seven functional accents carry every algorithmic signal.

### Primary

- **Traversal Cyan** (`#00E5FF` / `PrimaryCyan` / `AlgoTokens.accentCyan`): The primary read head and active signal. Used for active traversal pointers (`i`, `j`, BFS queue head, binary search midpoint), the hero Play/Pause transport fill, the timeline scrubber thumb and active track, active bottom-navigation pills, active `Trace` | `State` terminal tab indicators, and the AVIA brand wordmark. Paired container: `cyan-fill` (`#1F00E5FF`, 12% alpha) with `border-cyan` (`#3800E5FF`, ~22% alpha) or 30% cyan border.

### Secondary

- **Tracking Purple** (`#8B5CF6` / `SecondaryPurple` / `AlgoTokens.accentPurple`): Secondary pointer tracking, auxiliary structures, code-trace active-line sync, and the AI Tutor / Chat identity (`ChatHeader` badge, `AiTutorSheet`, `PlaybackRail` AI Tutor trigger). Paired container: `purple-fill` (`#1F8B5CF6`) with `purple-bright` (`#A78BFA` / `PurpleGlow`) for foreground glyphs and text.
- **Mutation Pink** (`#FF3366` / `AccentPink` / `AlgoTokens.accentPink`): Active comparisons, element swaps, structural mutations, and Challenge Mode (`PlaybackRail` Challenge button, `CanvasChallengePrompt` active pills). Paired container: `pink-fill` (`#1FFF3366`) and swap-flight halo `pink-glow` (`#44FF3366`).

### Tertiary

- **Verified Green** (`#00E676` / `AccentGreen` / `AlgoTokens.accentGreen`): Verified sorted partitions, found search targets, visited graph/tree nodes, `Easy` difficulty indicators, `O(1)` complexity tier accents, and correct practice/challenge answers. Paired container: `green-fill` (`#1F00E676`).
- **Pivot Yellow** (`#FFB800` / `AccentYellow` / `AlgoTokens.accentYellow`): Quick-Sort pivots, selection min-index markers, boundary thresholds, `Medium` difficulty indicators, `O(n log n)` complexity tier accents, and 1D bar comparison highlights. Paired container: `yellow-fill` (`#1FFFB800`).
- **Data Orange** (`#FB923C` / `AccentOrange`): Functional family color for Data Structures (`Stack`, `Queue`, `BST`, `Heap`), `O(n²)` quadratic complexity tier accents, `OfflineBadge` chrome, and consecutive practice streak badges (`2x STREAK`). Paired container: `orange-fill` (`#1FFB923C`).
- **Warn Red** (`#FF4B4B` / `AccentRed`): Error states, incorrect challenge/practice selections, `Hard` difficulty indicators, and mid-swap 1D bar highlights (`BarSwap`). Paired container: `red-fill` (`#1FFF4B4B`).

### Neutral

- **Workspace Surface** (`#0B0F19` / `DarkBackground` / `AlgoTokens.surfaceBase`): Base workspace layer for `AlgoWorkspaceBackground` and translucent bottom transport (`94%` alpha).
- **Canvas Well** (`#060A14` / `CanvasBackground` / `AlgoTokens.surfaceWell`): The deepest surface in the hierarchy. Used for the sunken visualizer canvas well, the docked `InstrumentDeck` terminal body, and the base background of `DashboardScreen`, `PracticeScreen`, `ChatScreen`, `ProfileScreen`, `SettingsScreen`, `OnboardingScreen`, and `BootOverlay`.
- **Glass Sunken** (`#080D1B` / `CardBackgroundElevated` / `AlgoTokens.surfaceSunken`): Sunken bezel tray of `DoubleBezelShell` and `AlgoCard`, the `BottomNavBar` bar surface, the OS navigation bar tint, and attached active terminal tabs.
- **Glass Fill** (`#0C1526` / `CardBackground` / `AlgoTokens.surfaceCard`): Core plate inside `DoubleBezelShell` and `AlgoCard`, default background of `RailIconButton`, `SegmentedToggle`, search input wells, and unselected filter chips.
- **Glass Elevated** (`#111D30` / `CardBackgroundHover` / `AlgoTokens.surfaceFloat`): Elevated interactive buttons, floating popovers, hover states, and modal sheet cards.
- **Text Hierarchy:**
  - **Text Primary** (`#E2E8F0`): Screen headers, algorithm titles, cell values, code-trace source text.
  - **Text Secondary** (`#94A3B8`): Narrative explanations, card descriptions, secondary action icons.
  - **Text Muted** (`#64748B`): Tabular step counters (`01/65`), complexity readouts, inactive tabs, index labels.
  - **Text Dark** (`#334155`): `SectionLabel` text, inactive `BottomNavBar` targets, trailing card chevrons.
  - **Text Navy** (`#1E3A5F`): Inline metadata separators (`·`) and subtle structural ticks.
- **Borders & Canvas Neutrals:**
  - **Border Subtle** (`#0FFFFFFF`, 6% white): Default 0.5dp/1dp hairline border across cards, bezels, and buttons.
  - **Border Medium** (`#1AFFFFFF`, 10% white): Elevated glass borders, `InstrumentMeter` unfilled ticks, and `InstrumentRule` tick marks.
  - **Border Cyan** (`#3800E5FF`, ~22% cyan): Active/selected cyan border on cards, pills, and onboarding actions.
  - **Bar Unsorted** (`#1A2E50`): Resting bar fill inside `BarVisualizer` (never used outside the 1D bar renderer).
  - **Graph Edge Default** (`#1E293B`): Unhighlighted edge stroke in `GraphTreeVisualizer`.

### Named Rules

**The Functional-Accent Rule.** Every accent color has a strict behavioral job. If a UI element does not represent Traversal (`Cyan`), Secondary Tracking / AI (`Purple`), Mutation / Challenge (`Pink`), Verified / Found (`Green`), Pivot / Threshold (`Yellow`), Data Structure / Offline / Streak (`Orange`), or Error / Hard (`Red`), it does not get a new hex code—it maps to the closest semantic role. Hardcoded `Color(0xFF…)` literals outside `ui/theme/Color.kt` are blocked by the custom lint detector `HardcodedHexColorDetector`.

**The Paired-Container Rule.** Every translucent accent fill (`*Subtle` at `0x1F` / ~12% alpha) must be paired with a border and foreground derived from the *same* accent family (typically `0.30f–0.45f` alpha border + full-strength or `*Bright` foreground). Never mix a cyan fill with a purple border or a green fill with white border.

**The Color-Is-Never-Only Rule.** Algorithmic and interactive states are never communicated by hue alone. Challenge Mode combines an `AccentPink` tint with a pulsing target border and an explicit `"TARGET"` pill; difficulty rows combine a `6dp` tinted dot with a text label (`Easy`, `Medium`, `Hard`); active terminal tabs combine a `6dp` `PrimaryCyan` status LED dot with bold `TextPrimary` typography.

**The No-Body-Text-Cyan Rule.** `PrimaryCyan` (`#00E5FF`) is reserved for borders, badges, short metric values (`O(n log n)`, `1.0x`, `100%`), wordmarks, and active controls. Long-form prose and narrative descriptions always use `TextPrimary` (`#E2E8F0`) or `TextSecondary` (`#94A3B8`). `PurpleGlow` (`#A78BFA`) is strictly a foreground text/icon tint over `PurpleSubtle`, never a background fill.

## Typography

**Display Font:** `JetBrains Mono` (`R.font.jetbrains_mono_variable` — weights `400 Normal`, `500 Medium`, `600 SemiBold`, `700 Bold`).

**Body Font:** `JetBrains Mono` (identical family across all prose, markdown chat responses, theory sheets, and code listings).

**Label / Mono Font:** `JetBrains Mono`.

**Character:** Crisp, technical, fixed-width instrument typography. Because every character shares the same advance width, code listings, complexity tables, variable readouts, and zero-padded step counters align mechanically without layout jitter.

### Hierarchy

The type system (`AlgoLensTypography` + `AlgoType` in `ui/theme/Type.kt`) defines five primary steps plus two intermediate Material sub-steps:

- **Display** (`AlgoType.display` / `displayLarge`–`Small`: **Bold 700**, `28sp` size, `34sp` line-height, `-0.03sp` letter-spacing): Screen hero titles, large metric numerals on `ProfileScreen`, and the `AVIA` cold-start wordmark.
- **Title / Headline** (`AlgoType.title` / `headlineLarge`–`Small`, `titleLarge`–`Medium`: **SemiBold 600**, `16sp` size, `21sp` line-height, `-0.01sp` letter-spacing): Workspace region headers (`VisualizerHeader` algorithm title in `Bold` with `trackHeader = 1.sp`), modal sheet titles, and onboarding slide headings.
- **Title Small** (`titleSmall`: **Medium 500**, `12sp` size, `17sp` line-height, `0sp` letter-spacing): Primary row titles inside `AlgoCard` (`DashboardScreen` and `ProfileScreen`) and compact toolbar titles (`Explore: Practice`).
- **Body** (`AlgoType.body` / `bodyLarge`–`Medium`: **Normal 400**, `12sp` size, `17sp` line-height, `0sp` letter-spacing): Step narrative descriptions, `ChatScreen` markdown prose, `AlgorithmTheorySheet` explanations, and code-trace source lines.
- **Body Small** (`bodySmall`: **Normal 400**, `10sp` size, `14sp` line-height, `0sp` letter-spacing): Secondary card metadata (time complexity sub-labels in `AlgoCard`, `ComplexityCard` row labels).
- **Label** (`AlgoType.label` / `labelLarge`–`Small`: **SemiBold 600**, `10sp` size, `14sp` line-height, `+0.08sp` letter-spacing): `SectionLabel` headers, `BottomNavBar` tab labels, `PlaybackRail` speed chip (`1.0x`) and step counters (`01/65`), and `AttachedDeckTabs` (`Trace`, `State`).
- **Micro** (`AlgoType.micro` / `AlgoType.microSize`: **Normal 400** or **Bold 700**, `9.5sp` size, `13sp` line-height, `0sp` letter-spacing): The hard persistent-text floor. Used for `IconPillButton` labels (`TIME O(n log n)`), `SegmentedToggle` options, `OfflineBadge`, category filter pills, and canvas-internal index/weight numerals via `TextMeasurer`.

**Tracking & Leading Tokens (`AlgoType`):**
- Explicit tracking constants: `trackTight` (`0.6sp`), `trackSection` (`0.8sp`), `trackHeader` (`1.0sp`), `trackBrand` (`4.0sp`).
- Explicit leading scale: `leadingMicroTight` (`11sp`), `leadingMicro` (`12sp`), `leadingMicroRelaxed` (`13sp`), `leadingLabel` (`14sp`), `leadingBodyTight` (`15sp`), `leadingBody` (`16sp`), `leadingBodyDefault` (`17sp`), `leadingBodyRelaxed` (`18sp`), `leadingTitle` (`21sp`), `leadingDisplay` (`34sp`).

### Named Rules

**The Monospace-Everywhere Rule.** `JetBrains Mono` is the sole font family in the application. Never substitute Roboto, Inter, or system sans-serif for prose or UI chrome. When hierarchy or scannability needs adjustment, vary `FontWeight` (`Normal` → `SemiBold` → `Bold`), step size (`9.5sp` → `10sp` → `12sp` → `16sp`), or text token (`TextMuted` → `TextPrimary`)—never the typeface.

**The 9.5sp Persistent-Text Floor Rule.** `AlgoType.microSize` (`9.5sp`) is the minimum permitted text size anywhere in persistent chrome or canvas callouts. Nothing in the app renders below `9.5sp`.

**The Case-Signal Rule.** Case carries structural meaning:
- `SectionLabel` (`ui/components/CommonComponents.kt`) uses **Sentence case** (`Preferred Language`, `Connectivity`) at `10sp` (`labelSmall`, `TextDark`)—never all-caps.
- Inline status pills (`IconPillButton`), header algorithm names (`BUBBLE SORT`), and telemetry badges (`2x STREAK`, `10 PTS`, `OPERATOR PROFILE`, `SKIP`) use **UPPERCASE** with explicit positive tracking (`0.08sp` to `1.0sp`).
- Card titles (`AlgoCard`) and navigation labels (`Home`, `Explore`, `Chat`, `Profile`) use **Title Case**.

**The Tabular-Numeral Rule.** Any text element displaying live-updating step indices, percentages, or timers (`Step 01 of 65`, `01/65`, `42%`) must set `fontFeatureSettings = "tnum"` so digits never shift horizontal layout width during playback or scrubbing.

## Layout

### Root Stage & Navigation Architecture

`AlgoLensApp` (`ui/AlgoLensApp.kt`) orchestrates a three-stage root state machine inside a full-bleed `CanvasBackground` (`#060A14`) container, crossfading transitions via `AlgoTokens.panelFadeSpring`:

1. **`RootStage.BOOT` (`BootOverlay`):** Cold-start splash holding the `AviaLogo` inside an `88dp` 360° rotating cyan/purple sweep arc, `AlgorithmicWaveLoader` harmonic bar wave, and stage status telemetry (`INITIALIZING INVARIANT CORE` → `CALIBRATING ALGORITHMIC MODULES` → `WORKSPACE READY`). Tapping anywhere fast-forwards immediately into the app.
2. **`RootStage.ONBOARDING` (`OnboardingScreen`):** 3-slide `HorizontalPager` tour (`Visualizer`, `Offline AI Tutor`, `Practice & Challenges`) framed with a top `AVIA` brand bar + `SKIP` button and bottom precision tick indicators (`24dp` active cyan bar vs `6dp` inactive ticks) + `pressPhysics` action button.
3. **`RootStage.APP` (`AppShell`):**
   - **Four-Tab Primary Shell (`BottomNavBar`):** Hosts `NavTab.HOME` (`DashboardScreen`), `NavTab.EXPLORE` (`PracticeScreen`), `NavTab.CHAT` (`ChatScreen`), and `NavTab.PROFILE` (`ProfileScreen`).
   - **Two Full-Screen Routes Above the Bar:** Selecting an algorithm opens `VisualizerScreen` full-screen (hiding `BottomNavBar`); tapping the `Tune` settings button on `ProfileScreen` opens `SettingsScreen` full-screen. Both intercept system Back via `BackHandler`.

### Visualizer Workspace Grammar ("Unified Live Narrative Stage + Docked Terminal")

`VisualizerScreen` (`ui/visualizer/VisualizerScreen.kt`) is built from a single outer `Column` inside `AlgoWorkspaceBackground` with `statusBarsPadding()` and `navigationBarsPadding()` applied once at the root:

```
┌─ VisualizerHeader (auto-height, smoothPanelExpansion) ──────────────────────┐
│  ├─ Row 1: [Back 30dp] + ALGORITHM NAME / Step 01 of 65 | TIME · SPACE · ? · ⋮ │
│  └─ Row 2 (optional): [Cells | Bars] SegmentedToggle    | [Customize] Pill  │
├─ Box (weight 1f) — Unified Live Narrative Stage ────────────────────────────┤
│  ├─ Box (fillMaxSize, paddingBottom = animatedDockHeight: 160dp ↔ 319dp)    │
│  │    └─ VisualizerHost (LINEAR_1D / GRAPH_2D / BUFFER + Overlays)          │
│  ├─ StageLegend (BottomStart, static paddingBottom = 160dp + 4dp)           │
│  └─ Column (BottomCenter, fillMaxWidth)                                     │
│       ├─ CanvasChallengePrompt (AnimatedVisibility when challengeInFlight)  │
│       └─ InstrumentDeck (Docked Terminal)                                   │
│            ├─ AttachedDeckTabs (26dp tall: [• Trace] [• State] flush top)   │
│            └─ Terminal Frame (126dp 3-Line Peek ↔ 285dp Expanded)           │
├─ PlaybackRail (Two-Tier Docked Transport on DarkBackground @ 94% alpha) ────┤
│  ├─ Tier 1: 01/65 · [Full-Width Step Slider (26dp)] · 100%                  │
│  └─ Tier 2: [Reset 36dp][1.0x] | [Prev 36dp][Play 42dp][Next 36dp] | [🎯][✨]│
└─────────────────────────────────────────────────────────────────────────────┘
```

### Spacing Grid & Choreography

- **Strict 4dp Grid (`AlgoTokens.space1`–`space8`):** Only eight spacing tokens exist in the system: `space1` (`2dp`), `space2` (`4dp`), `space3` (`6dp`), `space4` (`8dp`), `space5` (`12dp`), `space6` (`16dp`), `space7` (`24dp`), and `space8` (`32dp`). Standard horizontal page padding across headers, rails, and lists is `space5` (`12dp`) or `space6` (`16dp`); workspace frame padding is `AlgoTokens.framePadding` (`10dp` horizontal, `6dp` vertical).
- **Responsive Panel Resizing:** `Modifier.smoothPanelExpansion()` (`animateContentSize` driven by `AlgoTokens.panelSpring`: `DampingRatioNoBouncy`, `StiffnessLow`) animates dynamic header rows without abrupt layout jumps.
- **One-Shot Entry Cascade (`EntryCascadeProvider` + `Modifier.entryCascade`):** When a list screen mounts (`DashboardScreen`), `EntryCascadeProvider` opens a `540ms` choreography window (`entryWindowMs`). Only the first `entryMaxItems` (`6`) rows rise `12dp` (`entryRiseDistance`) and fade in over `220ms` (`entryDurationMs`) with a `40ms` per-item stagger (`entryStaggerMs`, capped at `entryStaggerMax = 5`). Once `540ms` elapses—or for any row with `index >= 6`—`entryCascade` returns `Modifier` untouched with zero coroutines or `graphicsLayer` allocations.

### Named Rules

**The Strict 4dp Grid Rule.** Every padding, gap, and offset must reference `AlgoTokens.space1`–`space8`. Arbitrary intermediate spacing values (such as `10dp` or `14dp` between sibling components) are forbidden; if two elements feel too close at `8dp` (`space4`) and too far at `12dp` (`space5`), adjust their container grouping rather than inventing a non-grid spacing value.

**The Dock-Lift & Static-Legend Rule.** In `VisualizerScreen`, `VisualizerHost` pads its bottom by `animatedDockHeight` (`DOCK_PEEK_TOTAL_HEIGHT = 160dp` at rest, `DOCK_EXPANDED_TOTAL_HEIGHT = 319dp` when expanded) so expanding the terminal smoothly lifts the visualizer canvas and never occludes array cells or graph nodes. Conversely, `StageLegend` uses a **fixed** bottom padding (`DOCK_PEEK_TOTAL_HEIGHT + space2 = 164dp`) so it remains stationary on the stage floor and is cleanly covered by `InstrumentDeck` when the terminal expands.

**The One-Shot Entry Cascade Rule.** List entrance animations must be gated by `EntryCascadeProvider` and capped at `entryMaxItems = 6`. Rows composed via user scrolling after initial mount must render immediately at rest (`alpha = 1f`, `translationY = 0f`)—never fading or sliding in under the user's thumb.

## Elevation & Depth

AVIA is **flat-by-default** and rejects CSS/Material drop shadows on cards. Depth is engineered through a **five-step tonal surface ladder**, **double-bezel tray framing**, **one static scanline texture**, and **state-driven border/halo blooms**.

### Tonal Surface Ladder (M2)

| Token | Color | Elevation | Structural Role |
|---|---|---|---|
| `AlgoTokens.surfaceWell` (`canvasWell`) | `#060A14` | `0dp` (`elevationFlat`) | Deepest recessed well: visualizer canvas floor, `InstrumentDeck` terminal interior, and screen root backgrounds. |
| `AlgoTokens.surfaceSunken` | `#080D1B` | `0dp` (`elevationFlat`) | Recessed tray perimeter of `DoubleBezelShell` and `AlgoCard`, `BottomNavBar` background, and terminal window titlebars. |
| `AlgoTokens.surfaceBase` (`workspaceSurface`) | `#0B0F19` | `0dp` (`elevationFlat`) | Primary IDE workspace base inside `AlgoWorkspaceBackground` and `94%`-opaque `PlaybackRail` dock. |
| `AlgoTokens.surfaceCard` (`glassFill`) | `#0C1526` | `2dp` (`elevationRaised`) | Inset core plate inside `DoubleBezelShell` / `AlgoCard`, resting `RailIconButton`, `SegmentedToggle`, and filter chips. |
| `AlgoTokens.surfaceFloat` (`glassElevated`) | `#111D30` | `8dp` (`elevationFloating`) | Elevated interactive buttons, modal sheets, dropdown menus, and hover/active surfaces. |
| `AlgoTokens.elevationTraveling` | — | `18dp` | Reserved exclusively for **in-flight cells** during a swap-arc animation (`SwapFlight.kt`). |

### Atmospheric & Depth Primitives

- **`AlgoWorkspaceBackground` (`ui/components/GlassPanel.kt`):** Layers four subtle depth cues behind `VisualizerScreen`:
  1. Solid `surfaceBase` (`#0B0F19`).
  2. Top-left radial cyan glow (`accentCyan` at `7%` alpha, radius `85%` width) and bottom-right radial purple glow (`SecondaryPurple` at `5.5%` alpha, radius `90%` width).
  3. Vertical horizon wash from `45%` height down to `canvasWell` at `55%` alpha.
  4. **Static Scanline Texture:** `1px` horizontal white lines at `3%` alpha (`scanlineAlpha = 0.03f`) spaced every `2dp` (`scanlinePeriod = 2.dp`), drawn once in `Canvas`.
- **`AmbientGlowDivider`:** A `2dp` horizontal cyan gradient divider (`dividerGlowAlpha = 0.20f`) with a `2.4x` outer halo. Its `1800ms` breathing animation (`0.75f ↔ 1.0f`) runs **only** while `isPlaying == true` and holds a static baseline when paused.
- **Theory Drawer Backdrop Blur (`AlgoTokens.backdropBlur = 16.dp`):** When `state.showTheorySheet` is true, the main workspace `Box` inside `VisualizerScreen` animates `.blur(16.dp)` over `300ms` while modal sheets (`AlgorithmTheorySheet`, `AiTutorSheet`, `CustomizeInputSheet`) are composed *outside* the blurred node so they remain razor-sharp.

### Named Rules

**The Plate-In-A-Tray Rule.** Nothing floats flat on the background with a drop shadow. Cards and panels use `DoubleBezelShell`: a sunken outer tray (`surfaceSunken` `#080D1B`, `12dp` radius, `0.5dp` `BorderSubtle` or `1dp` `glow` border at `45%` alpha) separated by a `2dp` `bezelGap` from an inset core plate (`surfaceCard` `#0C1526`, `11dp` radius, `0.5dp` `BorderSubtle`).

**The Single-Texture Scanline Rule.** The `3%` white `2dp`-period horizontal scanline inside `AlgoWorkspaceBackground` is the **only** surface texture permitted in the entire design system. Never add noise PNGs, halftone patterns, carbon-fiber grids, or decorative mesh gradients.

**The Functional-Breathing Rule.** Continuous ambient animations (such as `AmbientGlowDivider`) must be gated on active execution state (`isPlaying`). When the workspace is paused or idle, ambient chrome rests completely still so the screen never wastes GPU frames or distracts the operator.

**The Traveling-Is-A-State Rule.** `AlgoTokens.elevationTraveling` (`18dp`) and `AccentPinkGlow` (`#44FF3366`) are applied only to array/buffer cells actively mid-flight during a swap arc (`SwapFlight.kt`). Resting cells sit at `0dp` elevation.

## Shapes

### Corner Radius Scale (`AlgoTokens`)

| Token | Value | Where Used |
|---|---|---|
| `radiusXs` | `4dp` | `IconPillButton` (`TIME`, `SPACE`, `CUSTOMIZE`), `PracticeScreen` score/streak badges, primary onboarding action button, `InstrumentDeck` top-start corner where tabs attach. |
| `radiusXxs` | `6dp` | Category filter chips (`PracticeScreen`), `SegmentedToggle` outer track (`radiusSm - space1 = 6dp`), onboarding page indicator ticks. |
| `radiusSm` | `8dp` | `BottomNavBar` active icon pill, `PlaybackRail` speed chip (`1.0x`), secondary square header buttons (`PracticeScreen` / `ProfileScreen` / `SettingsScreen`), `AttachedDeckTabs` top corners (`topStart = 8dp`, `topEnd = 8dp`). |
| `bezelInnerRadius` | `11dp` | Inner core plate of `DoubleBezelShell` and `AlgoCard` (`bezelRadius 12dp - bezelInset 1dp`). |
| `radiusMd` / `bezelRadius` | `12dp` | Default outer shell of `DoubleBezelShell` and `AlgoCard`, `GlassSurface`, and `InstrumentDeck` terminal frame (`topEnd`, `bottomStart`, `bottomEnd`). |
| `radiusLg` | `16dp` | Modal bottom sheets (`AlgorithmTheorySheet`, `AiTutorSheet`, `CustomizeInputSheet`, `CustomizeGraphSheet`, `CustomizeBufferSheet`). |
| `radiusXl` | `24dp` | Large hero containers and summary cards. |
| `CircleShape` | `50%` | Every `RailIconButton` in `VisualizerHeader` and `PlaybackRail`, `CustomSwitch` track and thumb, `OfflineBadge`, and `6dp` status/difficulty indicator dots. |

### Stroke Scale & Bespoke Iconography

- **Four Stroke Weights (`AlgoTokens`):**
  - `strokeHairline` (`0.5dp`): Outer and inner bezel borders in `DoubleBezelShell` at rest.
  - `strokeThin` (`1.0dp`): Standard border for `RailIconButton`, `IconPillButton`, `SegmentedToggle`, `AlgoHairline` dividers, active `DoubleBezelShell` glow borders, and `Modifier.pressPhysics` bloom borders.
  - `strokeMedium` (`1.5dp`): Buffer structure walls (`Stack`/`Queue`) and the universal stroke width of `AlgoGlyphs`.
  - `strokeActive` (`2.0dp`): Selected/focused visualizer cells, challenge target rings, and active step highlights.
- **`AlgoGlyphs` (`ui/components/AlgoGlyphs.kt`):** A custom, zero-dependency vector icon library built on a `24×24dp` viewport with `1.5f` stroke width, `StrokeCap.Round`, `StrokeJoin.Round`, and `fill = null`. Even circular icons (`PlayCircle`, `Speed`, `Target`, `Search`, `Info`, `Help`) draw circles as two half-arcs so every glyph remains a pure stroke that takes its color from `Icon(tint = …)`.

### Named Rules

**The Double-Bezel Geometry Rule.** Whenever an inner surface nests inside an outer bezel shell with `bezelInset = 1.dp` and `bezelGap = 2.dp`, the inner corner radius must be `bezelInnerRadius = bezelRadius - bezelInset` (`11dp` inside `12dp`) so the concentric curves remain optically parallel.

**The Two-Surface Radius Rule.** General content containers nesting controls inside them must step down at least two radius increments (e.g., a `12dp` `radiusMd` panel hosting `4dp` `radiusXs` pills or `6dp` `radiusXxs` toggles). For `SegmentedToggle`, the outer track is `6dp` (`radiusSm - space1`) and the inner sliding option pill is `2dp` (`radiusXs - space1`).

**The Pure-Stroke Glyph Rule.** Workspace UI icons must use `AlgoGlyphs` (`1.5dp` stroke, no fill) sized via `inlineIconSm` (`12dp`), `inlineIconMd` (`14dp`), or `inlineIconLg` (`18dp`). Never mix solid/filled Material icons into workspace headers, cards, or transport rails (only `BottomNavBar`'s four root tab icons and OS launcher/splash drawables are exempt).

**The No-Stacked-Borders Rule.** Never stack two `strokeThin` (`1dp`) borders to simulate emphasis. Promote the single border width from `strokeHairline` (`0.5dp`) → `strokeThin` (`1dp`) → `strokeActive` (`2dp`), or let `Modifier.pressPhysics` bloom the `1dp` border alpha from `0.30f` to `0.60f`.

## Components

### Buttons (`RailIconButton` & Tactile Action Controls)

- **Character:** Circular IDE transport controls and sharp technical action pills with spring-driven mechanical compression (`Modifier.pressPhysics`).
- **Shape & Sizing (`RailIconButton` in `ui/components/WorkspaceControls.kt`):**
  - Always `CircleShape`.
  - Four strictly paired container/icon size tiers:
    - Secondary header control (`Back`, `Help`, `MoreVert` kebab): `boxSize = iconButtonSm` (`30dp`) + `iconSize = inlineIconMd` (`14dp`).
    - Standard control: `boxSize = iconButtonMd` (`32dp`) + `iconSize = inlineIconMd` (`14dp`).
    - Transport secondary (`Reset`, `Step Back`, `Step Forward`, `Challenge Mode`, `AI Tutor`): `boxSize = iconButtonLg` (`36dp`) + `iconSize = inlineIconMd` (`14dp`) or `inlineIconLg` (`18dp` for step arrows).
    - Hero Play/Pause button: `boxSize = iconButtonLg + space3` (`42dp`) + `iconSize = inlineIconLg` (`18dp`).
- **Color Assignment:**
  - Default resting (`Reset`, `Back`, `Step`): `CardBackground` (`#0C1526`) fill, `1dp` `BorderSubtle` (`#0FFFFFFF`), `TextSecondary` or `TextPrimary` glyph.
  - Hero Play/Pause: solid `PrimaryCyan` (`#00E5FF`) fill and border, `DarkBackground` (`#0B0F19`) glyph.
  - Challenge Mode toggle: `CardBackground` / `TextMuted` when off; `PinkSubtle` (`#1FFF3366`) fill + `1dp` `AccentPink` (`#FF3366`) border and glyph when active.
  - AI Tutor trigger: `PurpleSubtle` (`#1F8B5CF6`) fill, `1dp` `SecondaryPurple.copy(alpha = 0.4f)` border, `PurpleGlow` (`#A78BFA`) spark glyph.
- **Press & Disabled States:** `Modifier.pressPhysics` compresses scale to `0.96f` and blooms border alpha `0.30f → 0.60f` on `pressSpring`. Disabled controls apply `AlgoTokens.disabledAlpha` (`0.38f`) automatically via `enabled = false`.

### Chips (`IconPillButton`, Filter Pills & Status Badges)

- **`IconPillButton` (`ui/components/WorkspaceControls.kt`):**
  - **Shape & Padding:** `radiusXs` (`4dp`), `space4` (`8dp`) horizontal × `space2` (`4dp`) vertical padding, `space2` (`4dp`) icon-to-label gap.
  - **Style:** Paired translucent container (`CyanSubtle`, `PurpleSubtle`, etc.) + `1dp` matching accent border + `inlineIconSm - 1.dp` (`11dp`) leading `AlgoGlyphs` icon + `9.5sp` (`AlgoType.microSize`) Bold uppercase text (`TIME O(n log n)`, `SPACE O(1)`, `CUSTOMIZE`).
- **Category Filter Chips (`DashboardScreen` & `PracticeScreen`):**
  - **Style:** `radiusXxs` (`6dp`) or `CircleShape` pills with category-specific semantic accents (`All` / `Sorting` → `PrimaryCyan`, `Searching` → `SecondaryPurple`, `Structures` → `AccentOrange`, `Graphs` → `AccentGreen`). Selected state uses `<Accent>Subtle` fill + `1dp` `<Accent>` border; unselected uses `CardBackground` + `BorderSubtle`.
- **`OfflineBadge` (`ui/components/CommonComponents.kt`):**
  - **Style:** `CircleShape` pill in `OrangeSubtle` fill with `1dp` `AccentOrange.copy(alpha = 0.35f)` border, `9dp` `AlgoGlyphs.Offline` icon, and `9.5sp` Bold `"Offline"` label.

### Switches & Segmented Controls (`SegmentedToggle` & `CustomSwitch`)

- **`SegmentedToggle` (`ui/components/WorkspaceControls.kt`):**
  - **Shape:** Outer track `RoundedCornerShape(radiusSm - space1)` (`6dp`) on `CardBackground` with `1dp` `BorderSubtle` and `space1` (`2dp`) internal padding; inner option pill `RoundedCornerShape(radiusXs - space1)` (`2dp`).
  - **State:** Active option fills with solid `accent` (default `PrimaryCyan`) and `DarkBackground` Bold `9.5sp` label; inactive options are transparent with `TextMuted` Medium `9.5sp` label and `pressPhysics` enabled. Used for `CELLS` vs `BARS` (`VisualizerHeader`), `TREE` vs `ARRAY` (Heap), and `DIRECTED` vs `UNDIRECTED` (Graphs).
- **`CustomSwitch` (`ui/components/CommonComponents.kt`):**
  - **Dimensions & Shape:** `42dp × 24dp` `CircleShape` track with an `18dp` `CircleShape` thumb that animates between `2dp` (unchecked) and `20dp` (checked) on a `spring(stiffness = 500f, dampingRatio = 0.75f)`.
  - **Colors:** Checked uses `PrimaryCyan` track + `DarkBackground` thumb; unchecked uses `OptionUnselected` (`#1A2540`) track + `TextMuted` thumb.

### Cards & Containers (`DoubleBezelShell` & `AlgoCard`)

- **`DoubleBezelShell` (`ui/components/Instrument.kt`):**
  - **Structure:** Outer `Box` (`bezelRadius = 12dp`, `surfaceSunken` `#080D1B`, `0.5dp` `BorderSubtle` or `1dp` `glow.copy(alpha = 0.45f)` border, `bezelGap = 2dp` padding) wrapping an inner `Column` (`bezelInnerRadius = 11dp`, `surfaceCard` `#0C1526`, `0.5dp` `BorderSubtle`, default `space5 = 12dp` content padding).
  - **Usage:** Wraps practice question cards (`PracticeScreen`), AI assistant message cards and code blocks (`ChatScreen`), profile stat bento cards (`ProfileScreen`), and onboarding feature previews (`OnboardingScreen`).
- **`AlgoCard` (`ui/components/AlgoCard.kt`):**
  - **Character:** "A plate in a tray, not a Material list item."
  - **Structure:** Outer `12dp` `surfaceSunken` tray with `entryCascade(index)` and `pressPhysics(accent = chrome.accent)`, `2dp` bezel gap, and inner `11dp` `surfaceCard` plate (`12dp` horizontal × `8dp` vertical padding).
  - **Contents:** Left `18dp` (`inlineIconLg`) family `AlgoGlyphs` icon (`SwapVert`, `Search`, `Stack`, `Tree`) tinted at `75%` family accent; center column with `12sp` `titleSmall` algorithm name and a metadata row (`10sp` `TextMuted` time complexity · `TextNavy` dot · `6dp` `CircleShape` difficulty dot in `Green`/`Yellow`/`Red` + `10sp` difficulty label); right `14dp` `AlgoGlyphs.ChevronRight` in `TextDark`. Chrome is cached per `algo.id` via `@Immutable AlgoCardChrome` to guarantee zero allocation during `LazyColumn` scrolling.

### Inputs & Fields

- **Dashboard Search Well (`DashboardScreen.kt`):** `BasicTextField` housed in a `radiusMd` (`12dp`) `CardBackground` well with `1dp` `BorderSubtle` (promoting to `BorderCyan` when non-empty), leading `AlgoGlyphs.Search`, `PrimaryCyan` solid cursor brush, and trailing circular clear button (`AlgoGlyphs.Close`).
- **Chat Terminal Dock (`ChatInputDock` in `ChatScreen.kt`):** Bottom-docked `CardBackgroundElevated` bar with `imePadding()`, housing a `radiusSm` (`8dp`) `CardBackground` monospace input field (`JetBrainsMono`, `12sp` `TextPrimary`, `PrimaryCyan` cursor) and a `36dp` (`iconButtonLg`) circular/rounded cyan send button (`AlgoGlyphs.Send`).
- **Family-Aware Customize Sheets (`CustomizeInputSheet`, `CustomizeBufferSheet`, `CustomizeQueueSheet`, `CustomizeGraphSheet`):** Modal sheets dispatched by `spec.id.family` (`LINEAR_1D`, `BUFFER`, `GRAPH_2D`) for editing array inputs, sort order, search targets, stack/queue operation sequences, BST keys, or traversal start nodes.

### Navigation (`BottomNavBar` & `VisualizerHeader`)

- **`BottomNavBar` (`ui/components/BottomNavBar.kt`):**
  - **Bar Surface:** Full-width `Row` on `CardBackgroundElevated` (`#080D1B`) with a `1dp` `BorderSubtle` border, `navigationBarsPadding()`, and `space2` (`4dp`) horizontal × `space3` (`6dp`) vertical padding.
  - **Destinations (`NavTab`):** Four equal-weight tabs: `HOME` ("Home"), `EXPLORE` ("Explore" — Practice Mode), `CHAT` ("Chat" — Offline AI Assistant), and `PROFILE` ("Profile" — Operator Profile & Settings access).
  - **Active vs Inactive State:** Active tab renders an animated `CyanSubtle` pill (`38dp × 28dp`, `radiusSm` `8dp`) behind a `20dp` `PrimaryCyan` icon and `10sp` Bold `PrimaryCyan` label. Inactive tabs render a transparent pill with `TextDark` (`#334155`) icon and `10sp` Normal `TextDark` label.
- **`VisualizerHeader` (`ui/visualizer/VisualizerHeader.kt`):**
  - Top row: `30dp` `RailIconButton(AlgoGlyphs.Back)` + `16sp` Bold uppercase algorithm name (`letterSpacing = 1.sp`) with `9.5sp` tabular step counter (`Step 01 of 65`) on the left; inline `TIME` and `SPACE` `IconPillButton` readouts, `30dp` `?` guided-tour trigger, and `30dp` `⋮` overflow menu trigger (hosting `Algorithm Theory` and `Customize Input`) on the right.
  - Second row (shown for `LINEAR_1D` or customizable algorithms): `SegmentedToggle` (`CELLS` | `BARS`) on the left and `CUSTOMIZE` `IconPillButton` on the right.

### Signature Workspace Instruments

- **`VisualizerHost` (`ui/visualizer/VisualizerHost.kt`):** The single family-aware canvas dispatcher (`when (spec.id.family)` → `LINEAR_1D` (`CellArrayVisualizer` / `BarVisualizer`), `GRAPH_2D` (`GraphTreeVisualizer`), or `BUFFER` (`BufferVisualizer`)) plus composable `VisualizerOverlay` attachments (`ComparisonBridgeOverlay`, `RecursionTreeOverlay`, `WeightBadgeOverlay`, `PhaseStrip`, `MergeBufferRow`) and `VisualizerAuxiliary` slots.
- **`InstrumentDeck` (`ui/visualizer/InstrumentDeck.kt`):** The docked 3-line peek / expand terminal anchored to `Alignment.BottomCenter` of the visualizer stage:
  - **Attached Window Tabs (`AttachedDeckTabs`):** `Trace` and `State` tabs (`26dp` tall, `radiusSm` `8dp` top corners) sit flush on the top-left edge of the terminal frame with no background bar behind them. The active tab uses `CardBackgroundElevated` fill + `1dp` `PrimaryCyan.copy(alpha = 0.35f)` border + `6dp` `PrimaryCyan` LED dot + Bold `TextPrimary` label. Tapping the already-active tab toggles expansion; tapping the inactive tab switches pages.
  - **Terminal Frame:** `CanvasBackground` (`#060A14`) container with asymmetric corners (`topStart = 4dp`, others `12dp`) that animates height via `spring(DampingRatioNoBouncy, StiffnessMediumLow)` between `TERMINAL_PEEK_HEIGHT` (`126dp` — terminal header with traffic-light dots, filename, language switcher/badge, and expand chevron + 3 lines of live code or state) and `TERMINAL_EXPANDED_HEIGHT` (`285dp` — full scrollable 4-language `CodeTracePane` or `StateDeckPage` variable inspector + call stack).
- **`PlaybackRail` (`ui/visualizer/PlaybackRail.kt`):** Two-tier bottom transport dock on `DarkBackground.copy(alpha = 0.94f)` with tactile haptics (`TextHandleMove` on normal steps, `LongPress` on swaps when `AppSettings.hapticsEnabled` is true):
  - **Tier 1:** Zero-padded tabular step counter (`01/65`) + full-width Material 3 `Slider` (`PrimaryCyan` thumb and active track, `CardBackground` inactive track) + tabular percentage (`100%`).
  - **Tier 2:** Left group (`36dp` `Reset` + `36dp` tall `radiusSm` speed cycle chip `0.5x / 1.0x / 2.0x`), true-centered transport cluster (`36dp` `Step Back` + `42dp` filled `PrimaryCyan` `Play/Pause` + `36dp` `Step Forward`), and right group (`36dp` `Challenge Mode` target button + `36dp` `AI Tutor` spark button).
- **`InstrumentMeter` & `InstrumentRule` (`ui/components/Instrument.kt`):**
  - `InstrumentMeter`: 24-tick (`meterTickCount = 24`) `Canvas` progress bar that reads `progress()` inside the draw scope so frame updates never trigger text recomposition.
  - `InstrumentRule`: Ruler-ticked horizontal edge with `4dp` minor ticks (`tickMinor`) and `20dp` major ticks (`tickMajor`) every 5th mark.

## Do's and Don'ts

### Do:

- **Do** source every color, spacing value, corner radius, stroke weight, and spring spec from `AlgoTokens` (`ui/theme/Theme.kt`) and `AlgoType` (`ui/theme/Type.kt`).
- **Do** wrap cards, bento stats, practice questions, and AI assistant blocks in `DoubleBezelShell` (or follow its exact `surfaceSunken` outer tray + `2dp` `bezelGap` + `surfaceCard` `11dp` inner plate geometry, as in `AlgoCard`).
- **Do** apply `Modifier.pressPhysics(shape, accent)` after `.clip(shape)` and `.background(...)` on interactive buttons, pills, switches, and cards so they share the 4% scale compression and `0.30 → 0.60` accent border bloom.
- **Do** use `AlgoGlyphs` (`1.5dp` stroke, round caps/joins, zero fills) for all workspace icons, and pair `RailIconButton` sizes strictly with their matching glyph sizes (`30dp→14dp`, `32dp→14dp`, `36dp→14dp/18dp`, `42dp→18dp`).
- **Do** keep `VisualizerHost` bottom-padded by `animatedDockHeight` (`160dp` peek ↔ `319dp` expanded) and `StageLegend` statically bottom-padded (`164dp`) so expanding `InstrumentDeck` lifts the visualizer smoothly while burying the legend underneath.
- **Do** run `graphify update .` after modifying code files to keep the project knowledge graph synchronized.

### Don't:

- **Don't** write raw `Color(0xFF…)` literals outside `ui/theme/Color.kt` or raw `*.dp` padding/spacing literals outside the theme/token layer (`HardcodedHexColorDetector` and lint rules enforce this).
- **Don't** re-introduce the removed 1-line `TraceStrip` into `VisualizerScreen.kt`; `InstrumentDeck`'s docked 3-line peek (`126dp`) with attached `Trace` | `State` window tabs is the sole code-trace and state dock.
- **Don't** introduce a gamified consumer aesthetic (no XP progress bars, confetti, mascots, social leaderboards, or pastel themes) or marketing hero banners; every screen is an offline developer instrument.
- **Don't** add light-mode themes, Material You dynamic wallpaper colors, or drop shadows on resting cards.
- **Don't** place local `remember { mutableStateOf(...) }` playback or deck state inside `VisualizerScreen.kt` (`AlgolensVisualizerScreenMutation` bans this; all visualizer state belongs in `VisualizerScreenState`) or branch on individual `AlgorithmId` values inside `VisualizerScreen.kt`.
- **Don't** allow `Modifier.entryCascade` to animate items beyond `entryMaxItems` (`6`) or after `entryWindowMs` (`540ms`); scrolling rows must always compose at rest without allocating offscreen render layers.
