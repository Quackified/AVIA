# Taste-Skills Handover  for CLI AI without skill detection

> Copy-paste the PROMPT BLOCK into the CLI AI as first message.
> Then attach / paste REQUISITE FILES. No skill auto-loading needed.
> Updated 2026-09-16 after the Calibrated-Instrument pass (M1-M5 landed),
> the scroll-jank fix and the git hygiene pass. Reflects HEAD 4bce8e4.

## PROMPT BLOCK (copy below this line)

You are working in AVIA (formerly AlgoLens) - Android Jetpack Compose
(compileSdk 37, minSdk 24). You do NOT have skill detection. Manually load
the contracts below and follow them globally for every UI change.

STEP 0 - LOAD FIRST (in order, do not skip):
1. PRODUCT.md - truth, users (CS student), constraints (offline-only, no
   backend/auth/analytics, 13 algorithms x 4 languages).
2. DESIGN.md - normative tokens (YAML frontmatter) + 8 sections.
   Frontmatter is normative; prose is context.
3. docs/UI_GUIDELINES.md (16 sections) - BINDING. Tokenised not hard-coded,
   state hoisted in VisualizerScreenState, overlays via AlgorithmSpec,
   dark-only, 4dp grid, accent roles, springs.
4. docs/TASKS.md - THE AUDIT. Milestone status (M1-M7), open items,
   known-good/dont-regress list, toolchain traps. Read sections B and F
   before editing anything.
5. docs/ALGORITHM_AUTHORING.md - algorithm contract.
6. .clinerules/guidelines.md - global enforcement.
7. ui/theme/Color.kt + Theme.kt (AlgoTokens) + Type.kt (AlgoType) - code
   source of truth.
8. Taste-skills (paste contents inline, style authority):
   - design-taste-frontend/SKILL.md - LANDING/MARKETING ONLY. Header bans
     dashboards/data-tables/multi-step product UI. DO NOT apply to
     visualizer/dashboard/practice/settings.
   - redesign-existing-projects/SKILL.md - audit workflow: Scan -> Diagnose
     (list generics) -> Fix in existing stack, no rewrite.
   - high-end-visual-design/SKILL.md - premium execution (variance
     archetype, double-bezel, button-in-button, cubic-bezier, pre-output
     checklist). Adapt to Compose Dp.
   - minimalist-ui/SKILL.md - ONLY if brief says light-minimal editorial.
     Conflicts with dark tech-noir; never mix worlds.
   - imagegen-frontend-mobile/SKILL.md - IMAGES ONLY. Never writes code.
   - full-output-enforcement/SKILL.md - no truncation: no // ..., TODO,
     skeleton. On limit stop at clean breakpoint + [PAUSED - X of Y
     complete. Send continue to resume from: next].

STEP 1 - IDENTITY (the vibes - unchanged):
- App name is AVIA (Algorithm Visualizer & Interactive Assistant).
- JetBrains Mono everywhere. Dark-only: base #0B0F19, well #060A14
  (canvas visualizer only), glass #0C1526/#111D30. No light mode.
- Accents functional only: cyan #00E5FF traversal, purple #8B5CF6 tracking,
  pink #FF3366 compare/swap, green #00E676 sorted/verified, yellow #FFB800
  pivot. Subtle fill 12-20pct + same-accent border 30pct. No new hex outside
  Color.kt.
- 4dp grid strict: AlgoTokens.space1-space8 (2/4/6/8/12/16/24/32dp).
  All tokens from AlgoTokens; type sizes from AlgoType (display 28, title 16,
  body 12, label 10, micro 9.5 canvas-only). Persistent text floor 10sp.
- ALL-CAPS dies except true status pills (TARGET, OFFLINE, max 3 in app).

STEP 2 - INSTRUMENT PRIMITIVES (docs/UI_GUIDELINES.md + Instrument.kt):
Every premium surface is built from these; do not hand-roll substitutes:
- DoubleBezelShell - outer shell + inset core plate (M4). Zero call sites
  so far - adopt when touching cards/sheets.
- InstrumentMeter - bespoke 2dp canvas meter w/ tick marks. Stock M3
  Linear/CircularProgressIndicator are BANNED.
- InstrumentRule - faint ruler ticks for canvas edges (TextMuted @40%,
  4dp minor / 20dp major). Ticks never carry information alone.
- AlgoHairline.
- pressPhysics - press scale 0.96 + accent border bloom (M5). Stroke is
  remember-cached; graphicsLayer attaches only mid-press. Never bypass it
  for tappables.
- EntryCascadeProvider + Modifier.entryCascade(index) - one-shot screen-mount
  entry window (M5). AFTER the window closes or past entryMaxItems=6 rows,
  it is a strict no-op: no Animatable, no coroutine, no render layer. A
  screen that never provides the gate degrades to rows-at-rest, never to
  animate-forever. This design killed the dashboard scroll jank - do not
  regress it to per-item-instance animation.
- AlgoGlyphs - bespoke stroke-1.5 icon set (24dp grid, 1.5dp stroke, round
  caps, no fills). BANNED: filled Icons.Default.* in app UI except OS
  surfaces (launcher, splash). 22 files still migrate - see docs/TASKS.md B.
- The user-approved BottomNavBar (instrument rail: surfaceFloat + hairline
  top edge, 1dp cyan border on active tab, TextMuted 60pct inactive,
  colour-only animation) is LOCKED - see TASKS.md F. Do not re-design it.

STEP 3 - GLOBAL RULES (every Compose layout/view/animation):
- BANNED: generic Material cards, stock shadows, default list items,
  Inter/Roboto, AI-purple gradient hero, 3-equal-cards row, gamification,
  hardcoded fake values (progress floats, stat numbers, activity arrays),
  raw dp/sp outside AlgoTokens/AlgoType (lint: AlgolensRawDpSpacing,
  AlgolensRoundedCornerShapeLiteral - enforced), light mode, iOS idioms.
- MANDATORY: every algorithm view = custom Canvas or bespoke layout. One
  VisualizerHost when(family) (LINEAR_1D/GRAPH_2D/BUFFER). No branching on
  algorithm.name/id in screens. State in VisualizerScreenState only.
- Motion: panelSpring 600ms ceiling / 100ms floor, transform+opacity only.
  AmbientGlowDivider breathes ONLY while state.isPlaying. minTouchTarget
  44dp (rail secondary 32dp). contentDescription sentence-case. Color never
  sole channel. Predictive back honored (M7 honesty pass).
- Before editing state one-line Design Read. If ambiguous ask exactly ONE
  question. Never branch on name/id; no silent guideline misses.

STEP 4 - TASK PROTOCOL:
- Scan target + one incumbent visual truth before editing. Diagnose
  generics, fix in existing stack (Material3 Compose, no web libs).
- Guideline miss: discuss first; novel pattern -> update
  docs/UI_GUIDELINES.md same PR; never silent one-off.
- Verify: :app:compileDebugKotlin :app:testDebugUnitTest :app:assembleDebug
  all BUILD SUCCESSFUL (80 unit tests, 9 suites - not the old 29).
  :app:lintDebug 0 errors (lint baseline app/lint-baseline.xml - if you
  fix a violation, DELETE its stale baseline row; the row then re-matches
  and silently suppresses the new finding).
- Update docs/TASKS.md milestone rows when you complete/shrink an item.

TOOLCHAIN TRAPS (this machine - read before building):
- Shell output beyond ~30s gets capped: launch Gradle detached
  (Start-Process cmd.exe /c "... > all.log 2>&1") and poll the log.
- PowerShell reads UTF-8 as ANSI: never diagnose encoding from its output;
  verify with byte reads. Fix scripts must be UTF-8-safe.
- Cline checkpoint commits snapshot the whole tree - review git status
  before committing; .gitignore now covers *.log *.err *.trace *.tmp.

REQUISITES - 16 files, root C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens:
1 PRODUCT.md, 2 DESIGN.md, 3 docs/UI_GUIDELINES.md, 4 docs/TASKS.md,
5 docs/ALGORITHM_AUTHORING.md, 6 .clinerules/guidelines.md,
7 ui/theme/Color.kt, 8 ui/theme/Theme.kt, 9 ui/theme/Type.kt,
10 design-taste-frontend/SKILL.md, 11 redesign-existing-projects/SKILL.md,
12 high-end-visual-design/SKILL.md, 13 minimalist-ui/SKILL.md,
14 imagegen-frontend-mobile/SKILL.md, 15 full-output-enforcement/SKILL.md,
16 skills-lock.json.