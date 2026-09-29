# Graph Report - AlgoLens  (2026-09-29)

## Corpus Check
- 164 files · ~230,620 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 2186 nodes · 7371 edges · 147 communities (86 shown, 61 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 286 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1cf081ab`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- .generateStepsForAlgorithm
- GraphTreeRenderer.kt
- GraphTreeMutations
- FirestoreSyncEngine.kt
- VisualizerScreenState
- PracticeSessionManager
- AlgorithmId
- ChatSessionManager
- GraphTreeVisualizer
- ChatSessionState.kt
- FeatureEnhancementsTest.kt
- AviaApp.kt
- ChatMessage
- ChatScreen
- VisualizerScreenStateTest
- TraceLanguage
- StepToken
- ProfileFlowTest.kt
- VisualizerStep
- GraphEdgeState
- AccountTemplateCard.kt
- ChatMarkdownMessage
- GraphNodeState
- DataScale
- VisualizerFamily
- Instrument.kt
- VisualizerHost.kt
- ChatHistoryRepository
- 6. Implementation Steps & Validation Checklist
- ChallengeModeManagerTest.kt
- ElementState
- FakeAuthRepository
- RegionAuxiliary
- Step-by-step implementation plan
- UElementHandler
- UElementHandler
- SwapFlight.kt
- .processQuery
- FirebaseAuthRepository.kt
- AppSettings
- GoogleSignInHelper.kt
- ProfileScreen.kt
- Algorithm
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AviaIssueRegistry.kt
- UnavailableFirebaseAuthRepository
- .specFor
- InputValidationResult
- 🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS
- Findings
- AlgoLensDetectorsTest
- AviaDetectorsTest
- SettingsScreen
- VisualizerScreen
- 2. Package & File Map (`app/src/main/java/com/avia/`)
- FirebaseAuthRepository
- PredictStepArena
- DashboardScreen
- CellGrid
- Corner Radius Scale (`AlgoTokens`)
- ComparisonBridgeOverlay.kt
- OffscreenPointerBanner
- InstrumentDeck
- HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework
- VisualizerScreenState.kt
- ChallengeGlowTargets.kt
- PredictionKind
- FirestoreSyncEngineTest
- ChatHistorySidebar
- FeatureEnhancementsTest
- CircularQueueVisualizer
- CellGlowPainter.kt
- AviaGlyphs
- PROJECT.md — What is AVIA (`AlgoLens`)?
- BootController
- AlgoLensTheme
- VisualizerAuxiliary.kt
- InputOrder
- ExploreView
- RecursionTreeOverlay
- DeckPage
- UserPreferencesTest
- IMPLEMENTATION.md
- UserPreferences.kt
- CatalogueSortMode
- CodeLineAccent
- VisualizerHeaderMenuTest.kt
- HeaderOverflowMenuHost
- ExampleUnitTest
- Profile and Edit Profile
- FlowchartShape
- TableAlignment
- ExploreMode
- algohairline
- algoworkspacebackground
- authaccountstate
- auxiliaryslot
- bootcontrollereffect
- chataction
- chatbubble
- chathistoryrepository
- chatresponseprovider
- chatsender
- compacticonbutton
- compasscalibration
- computetransversearcposition
- customswitch
- detectdraggestures
- detecttapgestures
- explore
- fakeauthrepository
- flowchartshape
- graphcanvasgeometry
- graphsearch
- guestdatamigrationpolicy
- home
- iconpillbutton
- imepadding
- nodeplacementmode
- noderipple
- offlinecatalogchatprovider
- parseflowchart
- parsemarkdowninline
- parsemarkdowntable
- person
- practicegraphedge
- practicegraphnode
- practiceoption
- projectalgorithmrecommender
- railiconbutton
- rotate
- sectionlabel
- segmentedtoggle
- smoothpanelexpansion
- tablealignment
- traversalsignal
- tune
- unavailablefirebaseauthrepository
- visualizerfamily
- visualizerrendermode
- GraphGoal

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 115 edges
2. `VisualizerScreenState` - 96 edges
3. `AlgorithmId` - 78 edges
4. `AlgoTokens` - 65 edges
5. `Algorithm` - 64 edges
6. `AlgoLensTheme()` - 56 edges
7. `GraphNodeState` - 50 edges
8. `GraphEdgeState` - 44 edges
9. `VisualizerScreenStateTest` - 42 edges
10. `ChatSessionManager` - 42 edges

## Surprising Connections (you probably didn't know these)
- `Tertiary` --references--> `OfflineBadge()`  [INFERRED]
  docs/DESIGN.md → app/src/main/java/com/avia/ui/components/CommonComponents.kt
- `2.2 Multi-Step Undo / Redo History Stack` --references--> `GraphFloatingToolbar()`  [INFERRED]
  docs/FUTURE_WORK.md → app/src/main/java/com/avia/ui/visualizer/GraphFloatingToolbar.kt
- `Neutral` --references--> `PracticeScreen()`  [INFERRED]
  docs/DESIGN.md → app/src/main/java/com/avia/ui/practice/PracticeScreen.kt
- `Root Stage & Navigation Architecture` --references--> `PracticeScreen()`  [INFERRED]
  docs/DESIGN.md → app/src/main/java/com/avia/ui/practice/PracticeScreen.kt
- `2. Package & File Map (`app/src/main/java/com/avia/`)` --references--> `AlgoTokens`  [INFERRED]
  docs/ARCHITECTURE.md → app/src/main/java/com/avia/ui/theme/Theme.kt

## Import Cycles
- None detected.

## Communities (147 total, 61 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.08
Nodes (199): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algoglyphs (+191 more)

### Community 1 - ".generateStepsForAlgorithm"
Cohesion: 0.06
Nodes (17): AlgorithmStepRepository, Offset, BufferOp, Dequeue, Enqueue, Peek, Pop, Push (+9 more)

### Community 2 - "GraphTreeRenderer.kt"
Cohesion: 0.15
Nodes (14): Modifier, Modifier, WeightBadgeOverlay, atan2, canvas, cos, graphedgedefault, nativecanvas (+6 more)

### Community 3 - "GraphTreeMutations"
Cohesion: 0.15
Nodes (3): BstTreeNode, GraphTreeMutations, Offset

### Community 4 - "FirestoreSyncEngine.kt"
Cohesion: 0.06
Nodes (31): Result, Anonymous, Authenticated, AuthRepository, AuthState, Guest, StateFlow, CloudSyncRepository (+23 more)

### Community 5 - "VisualizerScreenState"
Cohesion: 0.09
Nodes (7): PlaybackRailPreview(), androidx, com, rememberVisualizerScreenState(), VisualizerScreenState, WidgetManagerModal(), 3.2 Hoisted State Machine (`@Stable VisualizerScreenState`)

### Community 6 - "PracticeSessionManager"
Cohesion: 0.08
Nodes (21): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+13 more)

### Community 7 - "AlgorithmId"
Cohesion: 0.08
Nodes (19): AnalyticsTracker, FirebaseAnalyticsManager, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS (+11 more)

### Community 9 - "GraphTreeVisualizer"
Cohesion: 0.05
Nodes (41): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE, GraphCapabilityProfile (+33 more)

### Community 10 - "ChatSessionState.kt"
Cohesion: 0.10
Nodes (20): ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus (+12 more)

### Community 11 - "FeatureEnhancementsTest.kt"
Cohesion: 0.19
Nodes (15): AlgorithmRegistry, SampleData, SyntaxHighlighter, assertequals, assertfalse, assertnotnull, assertnull, asserttrue (+7 more)

### Community 12 - "AviaApp.kt"
Cohesion: 0.10
Nodes (31): animatedcontent, VisualizerReturnContext, AlgoLensApp(), AppShell(), AviaApp(), AviaAppPreview(), Modifier, RootStage (+23 more)

### Community 13 - "ChatMessage"
Cohesion: 0.22
Nodes (7): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, AlgorithmTheoryData, AlgorithmTheoryRepository

### Community 14 - "ChatScreen"
Cohesion: 0.16
Nodes (19): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+11 more)

### Community 16 - "TraceLanguage"
Cohesion: 0.10
Nodes (25): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, BstMode (+17 more)

### Community 17 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 18 - "ProfileFlowTest.kt"
Cohesion: 0.08
Nodes (22): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, FrameTimingBenchmark, StartupBenchmark, Bitmap, compilationmode (+14 more)

### Community 19 - "VisualizerStep"
Cohesion: 0.11
Nodes (27): bufferPushPopQuestion(), buildPredictionQuestion(), comparePairQuestion(), dijkstraPredictionQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair() (+19 more)

### Community 20 - "GraphEdgeState"
Cohesion: 0.09
Nodes (19): GraphSearch, GraphCanvasEngine(), GraphIntegerEntryDialog(), IntRange, Modifier, Offset, start, target (+11 more)

### Community 21 - "AccountTemplateCard.kt"
Cohesion: 0.09
Nodes (28): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Dp, Modifier, Shape (+20 more)

### Community 22 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 23 - "GraphNodeState"
Cohesion: 0.21
Nodes (6): GraphCanvasGeometry, GraphTreeRenderer(), Offset, GraphNodeState, UnifiedGraphEditorTest, GraphMotionState

### Community 24 - "DataScale"
Cohesion: 0.40
Nodes (5): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED

### Community 25 - "VisualizerFamily"
Cohesion: 0.22
Nodes (7): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D

### Community 26 - "Instrument.kt"
Cohesion: 0.10
Nodes (29): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+21 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.17
Nodes (17): AlgorithmSpec, ArrayViewMode, BARS, CELLS, CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier (+9 more)

### Community 28 - "ChatHistoryRepository"
Cohesion: 0.24
Nodes (6): ChatHistoryRepository, ownerId, ChatConversation, conversation, conversationId, JSONObject

### Community 29 - "6. Implementation Steps & Validation Checklist"
Cohesion: 0.08
Nodes (23): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.1 Unifying the `customGraph` Pipeline in `AlgorithmStepRepository.kt`, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine (+15 more)

### Community 30 - "ChallengeModeManagerTest.kt"
Cohesion: 0.15
Nodes (12): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+4 more)

### Community 31 - "ElementState"
Cohesion: 0.08
Nodes (29): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), Color, Modifier, QueueCanvas(), QueueGate() (+21 more)

### Community 32 - "FakeAuthRepository"
Cohesion: 0.21
Nodes (5): FakeAuthRepository, SignedIn, SignedOut, UserProfile, ProfileEditorDraftTest

### Community 33 - "RegionAuxiliary"
Cohesion: 0.15
Nodes (8): RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier, PhaseStrip, Modifier, RecursionBands

### Community 34 - "Step-by-step implementation plan"
Cohesion: 0.10
Nodes (20): 1. Introduce validated capability profiles, 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 5. Build shared viewport geometry and canvas engine, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document (+12 more)

### Community 35 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 36 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 37 - "SwapFlight.kt"
Cohesion: 0.05
Nodes (51): abs, activity, animatecontentsize, animationspec, animationvector1d, AviaTokens, AviaTokensBase, Color (+43 more)

### Community 39 - "FirebaseAuthRepository.kt"
Cohesion: 0.07
Nodes (15): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, AuthRepository, Error, GuestDataMigrationPolicy, KEEP_SEPARATE (+7 more)

### Community 40 - "AppSettings"
Cohesion: 0.18
Nodes (7): AppSettings, Context, ownerId, SharedPreferences, HeaderActions(), bookmarks, stable

### Community 41 - "GoogleSignInHelper.kt"
Cohesion: 0.11
Nodes (21): Cancelled, Failure, GoogleSignInCredentials, GoogleSignInHelper, GoogleSignInResult, Context, Success, AviaType (+13 more)

### Community 42 - "ProfileScreen.kt"
Cohesion: 0.18
Nodes (15): AlgoCard(), AlgoCardChrome, AlgorithmCard(), Modifier, AuthRepository, ImageVector, Modifier, ProfileOptionItem() (+7 more)

### Community 43 - "Algorithm"
Cohesion: 0.11
Nodes (5): Algorithm, computeTransverseArcPosition(), EmptyCanvas(), DijkstraStepsTest, UnifiedMotionSystemTest

### Community 44 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement (+5 more)

### Community 45 - "AviaIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): implementation, IssueRegistry, AviaIssueRegistry, HardcodedHexColorDetector, Detector, Issue, RawDpSpacingDetector, RoundedCornerShapeLiteralDetector (+5 more)

### Community 46 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.24
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 48 - "InputValidationResult"
Cohesion: 0.12
Nodes (15): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Color (+7 more)

### Community 49 - "🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS"
Cohesion: 0.13
Nodes (14): 1. Executive Summary & Objective, 2.1 Stack & Queue (Buffer Family), 2.2 Heap & BST (Tree Structures), 2.3 Graph Traversals & Shortest Path (BFS, DFS, Dijkstra), 2. Core Problem Analysis & Current State, 3.1 Work Package A: Stack UI Redesign & Buffer Physics, 3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`), 3.3 Work Package C: Transport & Scrubber Coordination (+6 more)

### Community 50 - "Findings"
Cohesion: 0.13
Nodes (14): 1. P1 — Graph shrinks with dock expansion and cannot be panned, 2. P1 — Dock has multiple expand/collapse controls, 3. P1 — Attached tabs have button height, not terminal-tab height, 4. P2 — Language selector is duplicated, 5. P2 — State badges overflow horizontally instead of wrapping, 6. P2 — Legend disappears under the expanded dock, 7. P2 — Rounded accent borders remain across navigation and dock controls, 8. P2 — Tests do not prove UI and semantic trace claims (+6 more)

### Community 51 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 52 - "AviaDetectorsTest"
Cohesion: 0.22
Nodes (4): AviaDetectorsTest, Detector, Issue, LintDetectorTest

### Community 53 - "SettingsScreen"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 54 - "VisualizerScreen"
Cohesion: 0.07
Nodes (38): AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape, AiTutorSheet() (+30 more)

### Community 55 - "2. Package & File Map (`app/src/main/java/com/avia/`)"
Cohesion: 0.25
Nodes (7): ProjectAlgorithmRecommender, ProjectConstraintBrief, ProjectRecommendationPayload, RecommendedAlgorithmCandidate, 1. High-Level Architecture & Data Flow, 2. Package & File Map (`app/src/main/java/com/avia/`), ARCHITECTURE.md — AVIA Technical Architecture Guide

### Community 56 - "FirebaseAuthRepository"
Cohesion: 0.12
Nodes (3): FirebaseAuthRepository, AuthRepository, FirebaseAuthRepositoryTest

### Community 57 - "PredictStepArena"
Cohesion: 0.17
Nodes (10): Modifier, PredictStepArena(), PredictStepArenaPreview(), 2.1 Directed Edge Support & Arrowheads, 2.2 Multi-Step Undo / Redo History Stack, 2.3 Edge Weight Input Dialog & Custom Negative Weight Warnings, 2. Interactive Graph Editor Enhancements, 3.1 Dijkstra Prediction Question Generator (Completed) (+2 more)

### Community 58 - "DashboardScreen"
Cohesion: 0.13
Nodes (20): AviaLogo(), Color, Dp, Modifier, CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec (+12 more)

### Community 59 - "CellGrid"
Cohesion: 0.27
Nodes (11): CellGrid(), CellItem(), Dp, LazyListState, Modifier, State, TextUnit, BottomPointerBadge() (+3 more)

### Community 60 - "Corner Radius Scale (`AlgoTokens`)"
Cohesion: 0.12
Nodes (31): CompactIconButton(), IconPillButton(), androidx, Color, ImageVector, Modifier, RailIconButton(), SegmentedToggle() (+23 more)

### Community 61 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.33
Nodes (8): ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, path, pathbuilder, SlotTransform, strokecap, strokejoin

### Community 62 - "OffscreenPointerBanner"
Cohesion: 0.20
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 63 - "InstrumentDeck"
Cohesion: 0.07
Nodes (31): BarVisualizer(), BarVisualizerPreview(), com, Modifier, CodeTracePane(), CodeTracePanePreview(), com, Modifier (+23 more)

### Community 64 - "HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework"
Cohesion: 0.18
Nodes (10): 1. Executive Summary, 2. File Status Breakdown, 3. Pending Implementation Work, 4. Verification Plan, 5. Quick Context for Resuming Agent, Already Modified on Disk (Validated), HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework, Work Package A: Dijkstra Generation & Trace Code (+2 more)

### Community 65 - "VisualizerScreenState.kt"
Cohesion: 0.14
Nodes (8): GraphEditorSnapshot, Offset, WidgetType, STATE, TELEMETRY, TRACE, delay, mutablelongstateof

### Community 66 - "ChallengeGlowTargets.kt"
Cohesion: 0.28
Nodes (8): animatefloat, challengeTargetColorTriple(), Color, State, rememberChallengePulseState(), infiniterepeatable, rememberinfinitetransition, repeatmode

### Community 67 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 69 - "ChatHistorySidebar"
Cohesion: 0.25
Nodes (8): CandidateCard(), ChatHistorySidebar(), ComplexityPill(), androidx, ImageVector, Modifier, ProjectRecommendationBlock(), SidebarOptionItem()

### Community 71 - "CircularQueueVisualizer"
Cohesion: 0.32
Nodes (8): calculatePointerRadius(), CircularPointerBadge(), CircularQueueVisualizer(), CircularSlotItem(), getPointerBadgeInfo(), Color, Dp, Modifier

### Community 72 - "CellGlowPainter.kt"
Cohesion: 0.48
Nodes (6): drawCellGlow(), drawCircleGlow(), Color, Offset, CornerRadius, drawscope

### Community 74 - "PROJECT.md — What is AVIA (`AlgoLens`)?"
Cohesion: 0.22
Nodes (8): Capabilities and Constraints, Operating Context, Platform, Positioning, Product Principles, Product Purpose, PROJECT.md — What is AVIA (`AlgoLens`)?, Users

### Community 75 - "BootController"
Cohesion: 0.14
Nodes (11): MainActivity, BootController, BootControllerEffect(), AlgorithmCardPreview(), AviaTheme(), BootControllerTest, Bundle, ComponentActivity (+3 more)

### Community 76 - "AlgoLensTheme"
Cohesion: 0.11
Nodes (23): ProfileFlowTest, BottomNavBarPreview(), AlgoLensTheme(), AlgorithmTheorySheetPreview(), BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet() (+15 more)

### Community 77 - "VisualizerAuxiliary.kt"
Cohesion: 0.50
Nodes (3): AuxiliarySlot, BOTTOM, TOP

### Community 78 - "InputOrder"
Cohesion: 0.29
Nodes (7): InputOrder, ALREADY_SORTED, LIFO_FIFO_STREAM, NEARLY_SORTED, RANDOM_UNSORTED, TREE_OR_GRAPH, UNSPECIFIED

### Community 79 - "ExploreView"
Cohesion: 0.50
Nodes (4): ExploreView, HUB, PREDICT_ARENA, QUIZ_CATALOG

### Community 80 - "RecursionTreeOverlay"
Cohesion: 0.25
Nodes (5): IntRange, Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay

### Community 81 - "DeckPage"
Cohesion: 0.33
Nodes (3): DeckPage, STATE, TRACE

### Community 83 - "IMPLEMENTATION.md"
Cohesion: 0.29
Nodes (6): 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`), 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 3. Minimalist boot splash, 4. Tests, 5. Verification, 6. Deviations / known issues

### Community 84 - "UserPreferences.kt"
Cohesion: 0.53
Nodes (3): android, Context, UserPreferences

### Community 85 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 87 - "CodeLineAccent"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 88 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 89 - "HeaderOverflowMenuHost"
Cohesion: 0.40
Nodes (5): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderOverflowMenuHost(), androidx, Color

### Community 91 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 92 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 93 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 94 - "ExploreMode"
Cohesion: 0.67
Nodes (3): ExploreMode, PRACTICE_QUIZ, PREDICT_STEP

### Community 152 - "GraphGoal"
Cohesion: 0.40
Nodes (5): GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE, SHORTEST_UNWEIGHTED_PATH, WEIGHTED_SHORTEST_PATH

## Knowledge Gaps
- **270 isolated node(s):** `HUB`, `PREDICT_ARENA`, `QUIZ_CATALOG`, `PREDICT_STEP`, `PRACTICE_QUIZ` (+265 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 569 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **61 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `GraphTreeRenderer.kt`, `VisualizerScreenState`, `GraphTreeVisualizer`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `GraphEdgeState`, `GraphNodeState`, `Instrument.kt`, `VisualizerHost.kt`, `ChallengeModeManagerTest.kt`, `ElementState`, `RegionAuxiliary`, `SwapFlight.kt`, `AppSettings`, `VisualizerScreen`, `CellGrid`, `Corner Radius Scale (`AlgoTokens`)`, `ComparisonBridgeOverlay.kt`, `OffscreenPointerBanner`, `InstrumentDeck`, `FeatureEnhancementsTest`, `CircularQueueVisualizer`, `VisualizerAuxiliary.kt`, `RecursionTreeOverlay`, `HeaderOverflowMenuHost`?**
  _High betweenness centrality (0.091) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `FirestoreSyncEngine.kt`, `VisualizerScreenState`, `PracticeSessionManager`, `ChatSessionState.kt`, `FeatureEnhancementsTest.kt`, `ChatMessage`, `TraceLanguage`, `VisualizerStep`, `VisualizerFamily`, `VisualizerHost.kt`, `ChallengeModeManagerTest.kt`, `.processQuery`, `.specFor`, `InputValidationResult`, `2. Package & File Map (`app/src/main/java/com/avia/`)`, `InstrumentDeck`, `VisualizerScreenState.kt`, `AlgoLensTheme`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `GraphTreeRenderer.kt`, `GraphTreeMutations`, `GraphTreeVisualizer`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `TraceLanguage`, `VisualizerStep`, `GraphEdgeState`, `GraphNodeState`, `VisualizerHost.kt`, `ChallengeModeManagerTest.kt`, `AppSettings`, `Algorithm`, `.specFor`, `InputValidationResult`, `Corner Radius Scale (`AlgoTokens`)`, `OffscreenPointerBanner`, `InstrumentDeck`, `VisualizerScreenState.kt`, `VisualizerAuxiliary.kt`, `RecursionTreeOverlay`, `DeckPage`, `HeaderOverflowMenuHost`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `VisualizerScreenState` (e.g. with `3.2 Hoisted State Machine (`@Stable VisualizerScreenState`)` and `Don't:`) actually correct?**
  _`VisualizerScreenState` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Are the 3 inferred relationships involving `AlgorithmId` (e.g. with `2. Package & File Map (`app/src/main/java/com/avia/`)` and `Don't:`) actually correct?**
  _`AlgorithmId` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 5 inferred relationships involving `AlgoTokens` (e.g. with `2. Package & File Map (`app/src/main/java/com/avia/`)` and `Corner Radius Scale (`AlgoTokens`)`) actually correct?**
  _`AlgoTokens` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `HUB`, `PREDICT_ARENA`, `QUIZ_CATALOG` to the rest of the system?**
  _270 weakly-connected nodes found - possible documentation gaps or missing edges._