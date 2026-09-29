# Graph Report - AlgoLens  (2026-09-29)

## Corpus Check
- 150 files · ~219,446 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 2016 nodes · 7024 edges · 151 communities (87 shown, 64 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 414 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `01b4dcb3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- ChatMarkdownMessage
- VisualizerScreenState
- SwapFlight.kt
- GlassPanel.kt
- PracticeSessionManager
- FeatureEnhancementsTest.kt
- .processQuery
- VisualizerScreenStateTest
- GraphCanvasEngine
- VisualizerStep
- ProfileFlowTest.kt
- GraphTool
- ChatScreen
- GraphEdgeState
- ChatSessionManager
- BootController
- .generateStepsForAlgorithm
- Instrument.kt
- TraceLanguage
- Corner Radius Scale (`AlgoTokens`)
- AlgoLensApp.kt
- AccountTemplateCard.kt
- GraphTreeVisualizer
- BstMode
- ChatSessionState.kt
- 2. Package & File Map (`app/src/main/java/com/example/algolens/`)
- GraphTreeMotionEngine.kt
- GraphNodeState
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- UElementHandler
- UElementHandler
- AlgorithmId
- ChatHistoryRepository
- VisualizerHost.kt
- .generateBSTSteps
- Algorithm
- AlgorithmId.kt
- 🏛️ MISSION PROMPT: UNIFIED NODE GRAPH & TREE EDITOR FRAMEWORK FOR ALGOLENS
- AuthRepository
- UnavailableFirebaseAuthRepository
- Findings
- StepToken
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgoLensDetectorsTest
- PROJECT.md — What is AVIA (`AlgoLens`)?
- .specFor
- FakeAuthRepository
- Signature Workspace Instruments
- AviaLogo.kt
- DashboardScreen
- ProfileEditorDraft
- CanvasChallengePrompt
- MainActivity.kt
- GraphTreeMutations
- HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework
- SettingsScreen
- BufferVisualizer
- ComparisonBridgeOverlay.kt
- RecursionTreeOverlay
- UserPreferencesTest
- FeatureEnhancementsTest
- Step-by-step implementation plan
- CellArrayVisualizer
- PredictionKind
- ChatMessage
- VisualizerBenchmarks.kt
- StateDeckPage
- GraphFloatingToolbar
- GraphTelemetryMode
- GraphCustomization
- InstrumentDeck
- 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)
- GraphGoal
- InputOrder
- VisualizerState.kt
- CatalogueSortMode
- DataScale
- HeaderOverflowMenuHost
- Profile and Edit Profile
- AlgoLensTheme
- algohairline
- algolenstheme
- ExampleUnitTest
- 🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS
- RootStage
- FlowchartShape
- TableAlignment
- algotokens
- algotype
- DeckPage
- CodeListing
- VisualizerHeaderMenuTest.kt
- algoworkspacebackground
- auxiliaryslot
- computetransversearcposition
- doublebezelshell
- flowchartshape
- graphsearch
- nodeplacementmode
- noderipple
- parseflowchart
- parsemarkdowntable
- practicegraphedge
- practicegraphnode
- practiceoption
- pressphysics
- tablealignment
- traversalsignal
- visualizerfamily
- visualizerrendermode
- ElementState
- VisualizerScreenState.kt
- AlgoCard
- authaccountstate
- bootcontrollereffect
- chataction
- chatbubble
- chathistoryrepository
- chatresponseprovider
- chatsender
- compacticonbutton
- compasscalibration
- customswitch
- detectdraggestures
- detecttapgestures
- explore
- fakeauthrepository
- graphcanvasgeometry
- guestdatamigrationpolicy
- home
- iconpillbutton
- imepadding
- instrumentmeter
- offlinecatalogchatprovider
- parsemarkdowninline
- person
- projectalgorithmrecommender
- railiconbutton
- rotate
- sectionlabel
- segmentedtoggle
- smoothpanelexpansion
- tune
- unavailablefirebaseauthrepository

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 115 edges
2. `VisualizerScreenState` - 97 edges
3. `AlgoTokens` - 73 edges
4. `AlgorithmId` - 71 edges
5. `Algorithm` - 61 edges
6. `AlgoLensTheme()` - 57 edges
7. `AlgoType` - 54 edges
8. `AlgoGlyphs` - 50 edges
9. `GraphNodeState` - 50 edges
10. `GraphEdgeState` - 44 edges

## Surprising Connections (you probably didn't know these)
- `Step 1: Capability Profiles & Models` --references--> `AlgorithmSpec`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/model/AlgorithmId.kt
- `2. Chat UI cleanup (`ui/chat/ChatScreen.kt`)` --references--> `pressPhysics()`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/main/java/com/example/algolens/ui/components/Instrument.kt
- `7. P2 — Rounded accent borders remain across navigation and dock controls` --references--> `pressPhysics()`  [INFERRED]
  docs/REVIEW.md → app/src/main/java/com/example/algolens/ui/components/Instrument.kt
- `4. Architectural Invariants & Constraints` --references--> `AlgoTokens`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/theme/Theme.kt
- `5. Affected Files & Subsystem Map` --references--> `AlgoTokens`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/theme/Theme.kt

## Import Cycles
- None detected.

## Communities (151 total, 64 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.08
Nodes (193): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, Alignment (+185 more)

### Community 1 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 2 - "VisualizerScreenState"
Cohesion: 0.07
Nodes (10): GraphEditorSnapshot, androidx, com, Offset, rememberVisualizerScreenState(), VisualizerScreenState, WidgetType, STATE (+2 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.06
Nodes (42): abs, activity, animatecontentsize, animationspec, animationvector1d, Color, Dp, Modifier (+34 more)

### Community 4 - "GlassPanel.kt"
Cohesion: 0.17
Nodes (19): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+11 more)

### Community 5 - "PracticeSessionManager"
Cohesion: 0.08
Nodes (21): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+13 more)

### Community 6 - "FeatureEnhancementsTest.kt"
Cohesion: 0.21
Nodes (14): AlgorithmRegistry, SampleData, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, experimentalcoroutinesapi (+6 more)

### Community 9 - "GraphCanvasEngine"
Cohesion: 0.09
Nodes (12): GraphCanvasEngine(), GraphIntegerEntryDialog(), IntRange, Modifier, Offset, start, target, edges (+4 more)

### Community 10 - "VisualizerStep"
Cohesion: 0.12
Nodes (25): bufferPushPopQuestion(), buildPredictionQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair(), Indices (+17 more)

### Community 11 - "ProfileFlowTest.kt"
Cohesion: 0.12
Nodes (16): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, Bitmap, compositionlocalprovider, createandroidcomposerule (+8 more)

### Community 12 - "GraphTool"
Cohesion: 0.08
Nodes (27): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+19 more)

### Community 13 - "ChatScreen"
Cohesion: 0.11
Nodes (26): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+18 more)

### Community 14 - "GraphEdgeState"
Cohesion: 0.14
Nodes (9): GraphSearch, Result, GraphFrontierTelemetryStrip(), GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphSearchTest (+1 more)

### Community 16 - "BootController"
Cohesion: 0.29
Nodes (3): BootController, BootControllerEffect(), BootControllerTest

### Community 17 - ".generateStepsForAlgorithm"
Cohesion: 0.09
Nodes (16): AlgorithmStepRepository, Offset, BufferOp, Dequeue, Enqueue, Peek, Pop, Push (+8 more)

### Community 18 - "Instrument.kt"
Cohesion: 0.14
Nodes (20): AlgoHairline(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color, Dp (+12 more)

### Community 19 - "TraceLanguage"
Cohesion: 0.25
Nodes (7): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 20 - "Corner Radius Scale (`AlgoTokens`)"
Cohesion: 0.14
Nodes (25): CompactIconButton(), IconPillButton(), androidx, Color, ImageVector, Modifier, RailIconButton(), SegmentedToggle() (+17 more)

### Community 21 - "AlgoLensApp.kt"
Cohesion: 0.11
Nodes (36): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Modifier, BootOverlay(), Modifier, BottomNavBar() (+28 more)

### Community 22 - "AccountTemplateCard.kt"
Cohesion: 0.08
Nodes (35): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), Dp, Modifier, Shape, ProfileAvatar() (+27 more)

### Community 23 - "GraphTreeVisualizer"
Cohesion: 0.19
Nodes (6): GraphCapabilityProfile, GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), Modifier, Offset

### Community 24 - "BstMode"
Cohesion: 0.18
Nodes (8): BstMode, IN_ORDER, POST_ORDER, PRE_ORDER, SEARCH, QueueVariant, CIRCULAR_RING, LINEAR_FIFO

### Community 25 - "ChatSessionState.kt"
Cohesion: 0.16
Nodes (11): ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, LaunchVisualizer, QueryFollowUp, Context, rememberChatSessionManager(), CoroutineScope (+3 more)

### Community 26 - "2. Package & File Map (`app/src/main/java/com/example/algolens/`)"
Cohesion: 0.33
Nodes (6): ProjectAlgorithmRecommender, ProjectConstraintBrief, ProjectRecommendationPayload, RecommendedAlgorithmCandidate, AlgorithmTheoryRepository, 2. Package & File Map (`app/src/main/java/com/example/algolens/`)

### Community 27 - "GraphTreeMotionEngine.kt"
Cohesion: 0.25
Nodes (7): GraphMotionState, Offset, NodeRipple, rememberGraphMotionState(), GraphMotionState, TraversalSignal, mutablestatemapof

### Community 28 - "GraphNodeState"
Cohesion: 0.14
Nodes (6): GraphCanvasGeometry, GraphTreeRenderer(), Offset, GraphNodeState, UnifiedGraphEditorTest, GraphMotionState

### Community 29 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 30 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 31 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 32 - "AlgorithmId"
Cohesion: 0.11
Nodes (15): AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA, HEAP (+7 more)

### Community 33 - "ChatHistoryRepository"
Cohesion: 0.33
Nodes (3): ChatHistoryRepository, ChatConversation, JSONObject

### Community 34 - "VisualizerHost.kt"
Cohesion: 0.25
Nodes (12): AlgorithmSpec, ArrayViewMode, BARS, CELLS, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas() (+4 more)

### Community 35 - ".generateBSTSteps"
Cohesion: 0.18
Nodes (4): TreeTraversalAndCircularQueueTest, 3.1 Unifying the `customGraph` Pipeline in `AlgorithmStepRepository.kt`, Step 2: Step Repository Integration, The Core Problem Today

### Community 36 - "Algorithm"
Cohesion: 0.14
Nodes (4): Algorithm, computeTransverseArcPosition(), DijkstraStepsTest, UnifiedMotionSystemTest

### Community 37 - "AlgorithmId.kt"
Cohesion: 0.14
Nodes (11): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D, Modifier (+3 more)

### Community 38 - "🏛️ MISSION PROMPT: UNIFIED NODE GRAPH & TREE EDITOR FRAMEWORK FOR ALGOLENS"
Cohesion: 0.13
Nodes (14): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`) (+6 more)

### Community 39 - "AuthRepository"
Cohesion: 0.08
Nodes (11): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, AuthRepository, Error, GuestDataMigrationPolicy, KEEP_SEPARATE (+3 more)

### Community 40 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.27
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 41 - "Findings"
Cohesion: 0.14
Nodes (13): 1. P1 — Graph shrinks with dock expansion and cannot be panned, 2. P1 — Dock has multiple expand/collapse controls, 3. P1 — Attached tabs have button height, not terminal-tab height, 4. P2 — Language selector is duplicated, 5. P2 — State badges overflow horizontally instead of wrapping, 6. P2 — Legend disappears under the expanded dock, 7. P2 — Rounded accent borders remain across navigation and dock controls, 8. P2 — Tests do not prove UI and semantic trace claims (+5 more)

### Community 42 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 43 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.28
Nodes (11): AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement, Vendor (+3 more)

### Community 44 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 45 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 46 - "PROJECT.md — What is AVIA (`AlgoLens`)?"
Cohesion: 0.19
Nodes (9): android, Context, UserPreferences, Operating Context, Platform, Positioning, Product Purpose, PROJECT.md — What is AVIA (`AlgoLens`)? (+1 more)

### Community 48 - "FakeAuthRepository"
Cohesion: 0.19
Nodes (4): ProfileFlowTest, FakeAuthRepository, SignedIn, SignedOut

### Community 49 - "Signature Workspace Instruments"
Cohesion: 0.09
Nodes (16): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+8 more)

### Community 50 - "AviaLogo.kt"
Cohesion: 0.18
Nodes (12): AviaLogo(), Color, Dp, Modifier, TextUnit, colorfilter, font, image (+4 more)

### Community 51 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 52 - "ProfileEditorDraft"
Cohesion: 0.33
Nodes (3): UserProfile, ProfileEditorDraft, ProfileEditorDraftTest

### Community 53 - "CanvasChallengePrompt"
Cohesion: 0.24
Nodes (7): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeFeedback, ChallengeState, FeedbackRow(), Modifier, PromptHeader()

### Community 54 - "MainActivity.kt"
Cohesion: 0.18
Nodes (10): MainActivity, Bundle, ComponentActivity, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 3. Minimalist boot splash, 5. Verification, 6. Deviations / known issues, enableedgetoedge (+2 more)

### Community 55 - "GraphTreeMutations"
Cohesion: 0.17
Nodes (3): BstTreeNode, GraphTreeMutations, Offset

### Community 56 - "HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework"
Cohesion: 0.18
Nodes (10): 1. Executive Summary, 2. File Status Breakdown, 3. Pending Implementation Work, 4. Verification Plan, 5. Quick Context for Resuming Agent, Already Modified on Disk (Validated), HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework, Work Package A: Dijkstra Generation & Trace Code (+2 more)

### Community 57 - "SettingsScreen"
Cohesion: 0.13
Nodes (20): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+12 more)

### Community 58 - "BufferVisualizer"
Cohesion: 0.12
Nodes (21): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), Color, Modifier, QueueCanvas(), QueueGate() (+13 more)

### Community 59 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.09
Nodes (28): ImageVector, drawCellGlow(), drawCircleGlow(), Color, Offset, CellGrid(), CellItem(), Dp (+20 more)

### Community 60 - "RecursionTreeOverlay"
Cohesion: 0.43
Nodes (3): IntRange, Modifier, RecursionTreeOverlay

### Community 62 - "FeatureEnhancementsTest"
Cohesion: 0.13
Nodes (4): AnnotatedString, SyntaxHighlighter, FeatureEnhancementsTest, 3.3 Adding or Extending an Algorithm

### Community 63 - "Step-by-step implementation plan"
Cohesion: 0.10
Nodes (19): 1. Introduce validated capability profiles, 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 5. Build shared viewport geometry and canvas engine, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document (+11 more)

### Community 64 - "CellArrayVisualizer"
Cohesion: 0.13
Nodes (13): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State, OffscreenPointerTarget, LazyListState, Modifier (+5 more)

### Community 65 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 66 - "ChatMessage"
Cohesion: 0.12
Nodes (18): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, ChatSender, ASSISTANT, SYSTEM (+10 more)

### Community 67 - "VisualizerBenchmarks.kt"
Cohesion: 0.20
Nodes (7): FrameTimingBenchmark, StartupBenchmark, compilationmode, frametimingmetric, macrobenchmarkrule, startupmode, startuptimingmetric

### Community 68 - "StateDeckPage"
Cohesion: 0.25
Nodes (9): ComplexityStatePill(), androidx, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage(), StateDeckPagePreview(), VarBadge() (+1 more)

### Community 69 - "GraphFloatingToolbar"
Cohesion: 0.25
Nodes (8): GraphFloatingToolbar(), Modifier, 6. Implementation Steps & Validation Checklist, Step 1: Capability Profiles & Models, Step 3: Viewport & Renderer Refactoring, Step 4: Toolbar & Gestures Refactoring, Step 5: Fullscreen Inset & Layout Hardening, Step 6: Test Suite & Verification

### Community 70 - "GraphTelemetryMode"
Cohesion: 0.29
Nodes (7): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE

### Community 71 - "GraphCustomization"
Cohesion: 0.08
Nodes (22): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Color (+14 more)

### Community 72 - "InstrumentDeck"
Cohesion: 0.08
Nodes (26): CodeTracePane(), CodeTracePanePreview(), com, Modifier, State, InstrumentDeck(), InstrumentDeckPreview(), androidx (+18 more)

### Community 73 - "1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)"
Cohesion: 0.29
Nodes (5): 1. High-Level Architecture & Data Flow, 3.2 Hoisted State Machine (`@Stable VisualizerScreenState`), 3. Core Architectural Invariants, ARCHITECTURE.md — How Does the Code Work?, 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)

### Community 74 - "GraphGoal"
Cohesion: 0.40
Nodes (5): GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE, SHORTEST_UNWEIGHTED_PATH, WEIGHTED_SHORTEST_PATH

### Community 75 - "InputOrder"
Cohesion: 0.29
Nodes (7): InputOrder, ALREADY_SORTED, LIFO_FIFO_STREAM, NEARLY_SORTED, RANDOM_UNSORTED, TREE_OR_GRAPH, UNSPECIFIED

### Community 76 - "VisualizerState.kt"
Cohesion: 0.29
Nodes (6): Pointer, VisualizerRenderMode, BARS, BUFFER, CELLS, GRAPH_TREE

### Community 77 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 78 - "DataScale"
Cohesion: 0.40
Nodes (5): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED

### Community 79 - "HeaderOverflowMenuHost"
Cohesion: 0.40
Nodes (5): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderOverflowMenuHost(), androidx, Color

### Community 80 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 81 - "AlgoLensTheme"
Cohesion: 0.08
Nodes (37): BottomNavBarPreview(), ExploreCatalogScreenPreview(), AlgoLensTheme(), challengeEligibleIndices(), ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+29 more)

### Community 85 - "🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS"
Cohesion: 0.13
Nodes (14): 1. Executive Summary & Objective, 2.1 Stack & Queue (Buffer Family), 2.2 Heap & BST (Tree Structures), 2.3 Graph Traversals & Shortest Path (BFS, DFS, Dijkstra), 2. Core Problem Analysis & Current State, 3.1 Work Package A: Stack UI Redesign & Buffer Physics, 3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`), 3.3 Work Package C: Transport & Scrubber Coordination (+6 more)

### Community 86 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 87 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 88 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 91 - "DeckPage"
Cohesion: 0.33
Nodes (3): DeckPage, STATE, TRACE

### Community 92 - "CodeListing"
Cohesion: 0.20
Nodes (10): CodeLineAccent, Family, Color, CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color (+2 more)

### Community 93 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 113 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 114 - "VisualizerScreenState.kt"
Cohesion: 0.19
Nodes (7): AppSettings, Context, SharedPreferences, HeaderActions(), mutablefloatstateof, mutablelongstateof, stable

### Community 117 - "AlgoCard"
Cohesion: 0.17
Nodes (13): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color (+5 more)

## Knowledge Gaps
- **223 isolated node(s):** `KOTLIN`, `JAVA`, `PYTHON`, `CPP`, `Loading` (+218 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 482 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **64 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `VisualizerScreenState`, `PracticeSessionManager`, `FeatureEnhancementsTest.kt`, `.processQuery`, `VisualizerStep`, `.generateStepsForAlgorithm`, `TraceLanguage`, `BstMode`, `ChatSessionState.kt`, `2. Package & File Map (`app/src/main/java/com/example/algolens/`)`, `VisualizerHost.kt`, `AlgorithmId.kt`, `PROJECT.md — What is AVIA (`AlgoLens`)?`, `.specFor`, `CanvasChallengePrompt`, `FeatureEnhancementsTest`, `ChatMessage`, `GraphCustomization`, `InstrumentDeck`, `AlgoLensTheme`, `VisualizerScreenState.kt`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `VisualizerScreenState`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `GraphCanvasEngine`, `GraphEdgeState`, `.generateStepsForAlgorithm`, `Instrument.kt`, `Corner Radius Scale (`AlgoTokens`)`, `GraphTreeVisualizer`, `GraphTreeMotionEngine.kt`, `GraphNodeState`, `VisualizerHost.kt`, `.generateBSTSteps`, `AlgorithmId.kt`, `Signature Workspace Instruments`, `CanvasChallengePrompt`, `BufferVisualizer`, `ComparisonBridgeOverlay.kt`, `RecursionTreeOverlay`, `Step-by-step implementation plan`, `CellArrayVisualizer`, `StateDeckPage`, `InstrumentDeck`, `VisualizerState.kt`, `HeaderOverflowMenuHost`, `AlgoLensTheme`, `🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS`, `VisualizerScreenState.kt`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `VisualizerStep`, `GraphTool`, `GraphEdgeState`, `.generateStepsForAlgorithm`, `Corner Radius Scale (`AlgoTokens`)`, `BstMode`, `GraphNodeState`, `VisualizerHost.kt`, `Algorithm`, `AlgorithmId.kt`, `🏛️ MISSION PROMPT: UNIFIED NODE GRAPH & TREE EDITOR FRAMEWORK FOR ALGOLENS`, `.specFor`, `Signature Workspace Instruments`, `CanvasChallengePrompt`, `GraphTreeMutations`, `RecursionTreeOverlay`, `CellArrayVisualizer`, `GraphCustomization`, `InstrumentDeck`, `1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)`, `HeaderOverflowMenuHost`, `DeckPage`, `VisualizerScreenState.kt`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `VisualizerStep` (e.g. with `2.2 Heap & BST (Tree Structures)` and `3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`)`) actually correct?**
  _`VisualizerStep` has 5 INFERRED edges - model-reasoned connections that need verification._
- **Are the 3 inferred relationships involving `VisualizerScreenState` (e.g. with `3.2 Hoisted State Machine (`@Stable VisualizerScreenState`)` and `Don't:`) actually correct?**
  _`VisualizerScreenState` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `AlgoTokens` (e.g. with `4. Architectural Invariants & Constraints` and `5. Affected Files & Subsystem Map`) actually correct?**
  _`AlgoTokens` has 8 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AlgorithmId` (e.g. with `2. Package & File Map (`app/src/main/java/com/example/algolens/`)` and `Don't:`) actually correct?**
  _`AlgorithmId` has 4 INFERRED edges - model-reasoned connections that need verification._