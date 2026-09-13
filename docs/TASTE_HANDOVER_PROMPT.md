# Taste-Skills Handover — for CLI AI without skill detection

> Copy-paste the PROMPT BLOCK into the CLI AI as first message.
> Then attach / paste REQUISITE FILES. No skill auto-loading needed.

## PROMPT BLOCK (copy below this line)

You are working in AlgoLens - Android Jetpack Compose (compileSdk 37, minSdk 24). You do NOT have skill detection. Manually load the contracts below and follow them globally for every UI change.

STEP 0 - LOAD FIRST (in order, do not skip):
1. PRODUCT.md - truth, users (CS student), constraints (offline-only, no backend/auth/analytics, 13 algorithms x 4 languages).
2. DESIGN.md - normative tokens (YAML frontmatter) + 8 sections. Frontmatter is normative; prose is context.
3. docs/UI_GUIDELINES.md (16 sections) - BINDING. Tokenised not hard-coded, state hoisted in VisualizerScreenState, overlays via AlgorithmSpec, dark-only, 4dp grid, accent roles, springs.
4. docs/ALGORITHM_AUTHORING.md - algorithm contract.
5. .clinerules/guidelines.md - global enforcement.
6. ui/theme/Color.kt + Theme.kt (AlgoTokens) + Type.kt - code source of truth.
7. Taste-skills (paste contents inline, style authority):
   - design-taste-frontend/SKILL.md - LANDING/MARKETING ONLY. Header bans dashboards/data-tables/multi-step product UI. DO NOT apply to visualizer/dashboard/practice/settings.
   - redesign-existing-projects/SKILL.md - audit workflow: Scan -> Diagnose (list generics) -> Fix in existing stack, no rewrite.
   - high-end-visual-design/SKILL.md - premium execution (variance archetype, double-bezel, button-in-button, cubic-bezier, pre-output checklist). Adapt to Compose Dp.
   - minimalist-ui/SKILL.md - ONLY if brief says light-minimal editorial. Conflicts with dark tech-noir; never mix worlds.
   - imagegen-frontend-mobile/SKILL.md - IMAGES ONLY. S6 Design Bible: lock platform/palette/type/spacing/radius/icon/texture/nav/card/button/shadow before screen 2+. Never writes code.
   - full-output-enforcement/SKILL.md - no truncation: no // ..., TODO, skeleton. On limit stop at clean breakpoint + [PAUSED - X of Y complete. Send continue to resume from: next].

STEP 1 - GLOBAL RULES (every Compose layout/view/animation):
- JetBrains Mono everywhere. Dark-only: base #0B0F19, well #060A14, glass #0C1526/#111D30. No light mode.
- Accents functional only: cyan #00E5FF traversal, purple #8B5CF6 tracking, pink #FF3366 compare/swap, green #00E676 sorted, yellow #FFB800 pivot. Subtle fill 12-20pct + same-accent border 30pct. No new hex outside Color.kt.
- 4dp grid strict: space1-space8 (2/4/6/8/12/16/24/32dp). framePadding 10dp H / 6dp V. All tokens from AlgoTokens.
- BANNED: generic Material cards, stock shadows, default list items, Inter/Roboto, AI-purple gradient hero, 3-equal-cards row, linear motion.
- MANDATORY: every algorithm view = custom Canvas or bespoke layout. One VisualizerHost when(family) (LINEAR_1D/GRAPH_2D/BUFFER). No when(renderMode), no branch on algorithm.name/id in screen. State in VisualizerScreenState only.
- Motion springs/tween only (panelSpring 600ms ceiling, slider 100ms floor). transform+opacity only. minTouchTarget 44dp (rail 30dp exception). Every IconButton contentDescription sentence-case. Color never sole channel.
- Before editing state one-line Design Read. If ambiguous ask exactly ONE question.

STEP 2 - TASK PROTOCOL:
- Scan target + one incumbent visual truth before editing. Diagnose generics, fix in existing stack (Material3 Compose, no Tailwind/web libs).
- Guideline miss: discuss first; novel pattern -> update docs/UI_GUIDELINES.md same PR; never silent one-off.
- Verify: no banned output patterns, full runnable files, keep :app:assembleDebug + 29 unit tests passing.
- Confirm with: Loaded: PRODUCT, DESIGN, UI_GUIDELINES (16s), ALGORITHM_AUTHORING, guidelines, AlgoTokens, skill names. Design Read: ...
REQUISITES - 16 files, root C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens:
1 PRODUCT.md, 2 DESIGN.md, 3 docs/UI_GUIDELINES.md, 4 docs/ALGORITHM_AUTHORING.md, 5 docs/HANDOVER.md, 6 .clinerules/guidelines.md, 7 ui/theme/Color.kt, 8 ui/theme/Theme.kt, 9 ui/theme/Type.kt, 10 design-taste-frontend/SKILL.md, 11 redesign-existing-projects/SKILL.md, 12 high-end-visual-design/SKILL.md, 13 minimalist-ui/SKILL.md, 14 imagegen-frontend-mobile/SKILL.md, 15 full-output-enforcement/SKILL.md, 16 skills-lock.json.
Min viable if tight on tokens: 1,2,3,7,8,11,12,15.
Bundle PS: Get-Content PRODUCT.md,DESIGN.md,docs/UI_GUIDELINES.md,docs/ALGORITHM_AUTHORING.md,.clinerules/guidelines.md -Raw
Then: Get-Content app/src/main/java/com/example/algolens/ui/theme/Color.kt,app/src/main/java/com/example/algolens/ui/theme/Theme.kt,app/src/main/java/com/example/algolens/ui/theme/Type.kt -Raw
Then: Get-Content .agents/skills/redesign-existing-projects/SKILL.md,.agents/skills/high-end-visual-design/SKILL.md,.agents/skills/full-output-enforcement/SKILL.md -Raw

