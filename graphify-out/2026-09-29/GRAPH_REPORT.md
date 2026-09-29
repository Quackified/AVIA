# Graph Report - AlgoLens  (2026-09-29)

## Corpus Check
- 164 files · ~229,995 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 2172 nodes · 7366 edges · 155 communities (92 shown, 63 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 279 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9027a878`
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
- ChatHistoryRepository.kt
- FeatureEnhancementsTest.kt
- AviaApp.kt
- GlassPanel.kt
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
- AlgorithmId.kt
- Instrument.kt
- VisualizerHost.kt
- ChatHistoryRepository
- 6. Implementation Steps & Validation Checklist
- FeatureEnhancementsTest
- BufferVisualizer
- FakeAuthRepository
- RegionAuxiliary
- Step-by-step implementation plan
- UElementHandler
- UElementHandler
- SwapFlight.kt
- .processQuery
- AuthRepository
- FirestoreSyncEngine
- GoogleSignInHelper.kt
- ProfileScreen.kt
- Algorithm
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AviaIssueRegistry.kt
- GraphCanvasEngine
- .specFor
- AviaLogo.kt
- 🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS
- Findings
- AlgoLensDetectorsTest
- AviaDetectorsTest
- SettingsScreen
- VisualizerScreen
- ProfileFlowTest
- AppSettings
- GraphTool
- DashboardScreen
- UnifiedGraphEditorTest
- Corner Radius Scale (`AlgoTokens`)
- BstMode
- CellArrayVisualizer
- InstrumentDeck
- HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework
- VisualizerScreenState.kt
- GraphBuilderGestures.kt
- PredictionKind
- FirestoreSyncEngineTest
- AuthState
- EditProfileSheet
- ExploreCatalogScreen
- StateDeckPage
- ChatSessionState.kt
- PROJECT.md — What is AVIA (`AlgoLens`)?
- BootController
- AlgoLensTheme
- GraphTelemetryMode
- InputOrder
- Type.kt
- RecursionTreeOverlay
- DeckPage
- UserPreferencesTest
- IMPLEMENTATION.md
- UserPreferences.kt
- CatalogueSortMode
- AlgoCard
- CodeLineAccent
- VisualizerHeaderMenuTest.kt
- HeaderOverflowMenuHost
- ExampleUnitTest
- Profile and Edit Profile
- FlowchartShape
- TableAlignment
- ExploreMode
- algohairline
- algolenstheme
- algotokens
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
- doublebezelshell
- explore
- fakeauthrepository
- flowchartshape
- graphcanvasgeometry
- graphsearch
- guestdatamigrationpolicy
- home
- iconpillbutton
- imepadding
- instrumentmeter
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
- pressphysics
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
- ChallengeQuestionType
- RootStage

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 116 edges
2. `VisualizerScreenState` - 96 edges
3. `AlgorithmId` - 79 edges
4. `AlgoTokens` - 68 edges
5. `Algorithm` - 65 edges
6. `AlgoLensTheme()` - 58 edges
7. `GraphNodeState` - 50 edges
8. `GraphEdgeState` - 44 edges
9. `ChatSessionManager` - 42 edges
10. `VisualizerScreenStateTest` - 42 edges

## Surprising Connections (you probably didn't know these)
- `2.2 Multi-Step Undo / Redo History Stack` --references--> `GraphFloatingToolbar()`  [INFERRED]
  docs/FUTURE_WORK.md → app/src/main/java/com/avia/ui/visualizer/GraphFloatingToolbar.kt
- `3.3 Adding or Extending an Algorithm` --references--> `AlgorithmCodeRegistry`  [INFERRED]
  docs/ARCHITECTURE.md → app/src/main/java/com/avia/data/AlgorithmCodeRegistry.kt
- `3.1 The Single Switch Rule (`AlgorithmStepRepository`)` --references--> `AlgorithmStepRepository`  [INFERRED]
  docs/ARCHITECTURE.md → app/src/main/java/com/avia/data/AlgorithmStepRepository.kt
- `2. Package & File Map (`app/src/main/java/com/avia/`)` --references--> `GoogleSignInHelper`  [INFERRED]
  docs/ARCHITECTURE.md → app/src/main/java/com/avia/data/auth/GoogleSignInHelper.kt
- `2. Package & File Map (`app/src/main/java/com/avia/`)` --references--> `FirebaseAuthRepository`  [INFERRED]
  docs/ARCHITECTURE.md → app/src/main/java/com/avia/data/firebase/FirebaseAuthRepository.kt

## Import Cycles
- None detected.

## Communities (155 total, 63 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.09
Nodes (182): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algoglyphs (+174 more)

### Community 1 - ".generateStepsForAlgorithm"
Cohesion: 0.07
Nodes (14): AlgorithmStepRepository, Offset, BufferOp, Dequeue, Enqueue, Peek, Pop, Push (+6 more)

### Community 2 - "GraphTreeRenderer.kt"
Cohesion: 0.05
Nodes (49): animatefloat, AviaGlyphs, ImageVector, drawCellGlow(), drawCircleGlow(), Color, Offset, CellGrid() (+41 more)

### Community 3 - "GraphTreeMutations"
Cohesion: 0.16
Nodes (3): BstTreeNode, GraphTreeMutations, Offset

### Community 4 - "FirestoreSyncEngine.kt"
Cohesion: 0.08
Nodes (24): CloudSyncRepository, FirebaseSyncManager, UserProgressRecord, FirebaseAuthManager, AuthRepository, StateFlow, SyncStatus, ERROR (+16 more)

### Community 5 - "VisualizerScreenState"
Cohesion: 0.08
Nodes (8): PlaybackRailPreview(), GraphEditorSnapshot, androidx, com, Offset, rememberVisualizerScreenState(), VisualizerScreenState, 3.2 Hoisted State Machine (`@Stable VisualizerScreenState`)

### Community 6 - "PracticeSessionManager"
Cohesion: 0.05
Nodes (34): PracticeQuestionRepository, ForBst, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion (+26 more)

### Community 7 - "AlgorithmId"
Cohesion: 0.08
Nodes (19): AnalyticsTracker, FirebaseAnalyticsManager, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS (+11 more)

### Community 9 - "GraphTreeVisualizer"
Cohesion: 0.16
Nodes (8): GraphCapabilityProfile, GraphFloatingToolbar(), Modifier, GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), Modifier, Offset

### Community 10 - "ChatHistoryRepository.kt"
Cohesion: 0.11
Nodes (20): ProjectAlgorithmRecommender, ChatAction, ChatCodeSnippet, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus (+12 more)

### Community 11 - "FeatureEnhancementsTest.kt"
Cohesion: 0.13
Nodes (26): AlgorithmRegistry, SampleData, SyntaxHighlighter, ElementState, ACTIVE, COMPARING, FOUND, IDLE (+18 more)

### Community 12 - "AviaApp.kt"
Cohesion: 0.15
Nodes (26): animatedcontent, AlgoLensApp(), AppShell(), AviaApp(), AviaAppPreview(), Modifier, BootOverlay(), Modifier (+18 more)

### Community 13 - "GlassPanel.kt"
Cohesion: 0.32
Nodes (11): AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape, Atmospheric & Depth Primitives (+3 more)

### Community 14 - "ChatScreen"
Cohesion: 0.10
Nodes (27): ChatPromptStarter, AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock() (+19 more)

### Community 15 - "VisualizerScreenStateTest"
Cohesion: 0.08
Nodes (6): VisualizerScreenStateTest, 1. High-Level Architecture & Data Flow, 3.1 The Single Switch Rule (`AlgorithmStepRepository`), 3.3 Adding or Extending an Algorithm, 3. Core Architectural Invariants, ARCHITECTURE.md — AVIA Technical Architecture Guide

### Community 16 - "TraceLanguage"
Cohesion: 0.16
Nodes (14): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+6 more)

### Community 17 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 18 - "ProfileFlowTest.kt"
Cohesion: 0.08
Nodes (22): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, FrameTimingBenchmark, StartupBenchmark, Bitmap, compilationmode (+14 more)

### Community 19 - "VisualizerStep"
Cohesion: 0.12
Nodes (26): bufferPushPopQuestion(), buildPredictionQuestion(), comparePairQuestion(), dijkstraPredictionQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair() (+18 more)

### Community 20 - "GraphEdgeState"
Cohesion: 0.13
Nodes (8): GraphSearch, GraphFrontierTelemetryStrip(), GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphSearchTest, DijkstraPredictionTest

### Community 21 - "AccountTemplateCard.kt"
Cohesion: 0.14
Nodes (17): activityresultcontracts, Dp, Shape, ProfileAvatar(), ProfileField(), contentscale, focusdirection, imagebitmap (+9 more)

### Community 22 - "ChatMarkdownMessage"
Cohesion: 0.08
Nodes (26): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+18 more)

### Community 23 - "GraphNodeState"
Cohesion: 0.24
Nodes (5): GraphCanvasGeometry, GraphTreeRenderer(), Offset, GraphNodeState, GraphMotionState

### Community 24 - "DataScale"
Cohesion: 0.40
Nodes (5): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED

### Community 25 - "AlgorithmId.kt"
Cohesion: 0.12
Nodes (13): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D, VisualizerReturnContext (+5 more)

### Community 26 - "Instrument.kt"
Cohesion: 0.12
Nodes (27): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), InstrumentMeter(), InstrumentRule(), Color, Dp (+19 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.18
Nodes (15): AlgorithmSpec, AuxiliarySlot, BOTTOM, TOP, ArrayViewMode, BARS, CELLS, Modifier (+7 more)

### Community 28 - "ChatHistoryRepository"
Cohesion: 0.24
Nodes (6): ChatHistoryRepository, ownerId, ChatConversation, conversation, conversationId, JSONObject

### Community 29 - "6. Implementation Steps & Validation Checklist"
Cohesion: 0.08
Nodes (23): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.1 Unifying the `customGraph` Pipeline in `AlgorithmStepRepository.kt`, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine (+15 more)

### Community 30 - "FeatureEnhancementsTest"
Cohesion: 0.12
Nodes (8): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeFeedback, ChallengeState, FeedbackRow(), Modifier, PromptHeader(), FeatureEnhancementsTest

### Community 31 - "BufferVisualizer"
Cohesion: 0.09
Nodes (27): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), Color, Modifier, QueueCanvas(), QueueGate() (+19 more)

### Community 32 - "FakeAuthRepository"
Cohesion: 0.13
Nodes (7): CredentialValidator, FakeAuthRepository, SignedIn, SignedOut, Unavailable, UnavailableFirebaseAuthRepository, ProfileEditorDraftTest

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
Nodes (50): abs, activity, animatecontentsize, animationspec, animationvector1d, AviaTokens, AviaTokensBase, Color (+42 more)

### Community 38 - ".processQuery"
Cohesion: 0.10
Nodes (5): AiChatEngine, ChatComplexitySnapshot, ChatMessage, AlgorithmTheoryData, AiChatBackendTest

### Community 39 - "AuthRepository"
Cohesion: 0.09
Nodes (10): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, AuthRepository, Error, GuestDataMigrationPolicy, KEEP_SEPARATE (+2 more)

### Community 40 - "FirestoreSyncEngine"
Cohesion: 0.19
Nodes (6): Result, UserProfile, ChallengeStatsRecord, FirestoreSyncEngine, StateFlow, PracticeProgressRecord

### Community 41 - "GoogleSignInHelper.kt"
Cohesion: 0.16
Nodes (15): Cancelled, Failure, GoogleSignInCredentials, GoogleSignInHelper, GoogleSignInResult, Context, Success, credentialmanager (+7 more)

### Community 42 - "ProfileScreen.kt"
Cohesion: 0.16
Nodes (13): ImageVector, Modifier, ProfileOptionItem(), ProfileScreenPreview(), ProfileTheme(), ProfileTopBar(), asimagebitmap, backhandler (+5 more)

### Community 43 - "Algorithm"
Cohesion: 0.14
Nodes (4): Algorithm, computeTransverseArcPosition(), DijkstraStepsTest, UnifiedMotionSystemTest

### Community 44 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement (+5 more)

### Community 45 - "AviaIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): implementation, IssueRegistry, AviaIssueRegistry, HardcodedHexColorDetector, Detector, Issue, RawDpSpacingDetector, RoundedCornerShapeLiteralDetector (+5 more)

### Community 46 - "GraphCanvasEngine"
Cohesion: 0.12
Nodes (12): GraphCanvasEngine(), GraphIntegerEntryDialog(), IntRange, Modifier, Offset, start, target, edges (+4 more)

### Community 47 - ".specFor"
Cohesion: 0.15
Nodes (5): Modifier, PredictStepArena(), PredictStepArenaPreview(), AuxiliaryComponentTest, VisualizerOverlayTest

### Community 48 - "AviaLogo.kt"
Cohesion: 0.36
Nodes (7): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource

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
Cohesion: 0.13
Nodes (20): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+12 more)

### Community 54 - "VisualizerScreen"
Cohesion: 0.12
Nodes (19): EntryCascadeProvider(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, Color, Modifier, LegendEntry(), StageLegend() (+11 more)

### Community 56 - "AppSettings"
Cohesion: 0.07
Nodes (8): AppSettings, ownerId, ProfileValidator, FirebaseAuthRepository, AuthRepository, HeaderActions(), FirebaseAuthRepositoryTest, bookmarks

### Community 57 - "GraphTool"
Cohesion: 0.14
Nodes (14): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+6 more)

### Community 58 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 60 - "Corner Radius Scale (`AlgoTokens`)"
Cohesion: 0.15
Nodes (25): CompactIconButton(), IconPillButton(), androidx, Color, ImageVector, Modifier, RailIconButton(), SegmentedToggle() (+17 more)

### Community 61 - "BstMode"
Cohesion: 0.15
Nodes (11): BstMode, IN_ORDER, POST_ORDER, PRE_ORDER, SEARCH, QueueVariant, CIRCULAR_RING, LINEAR_FIFO (+3 more)

### Community 62 - "CellArrayVisualizer"
Cohesion: 0.13
Nodes (13): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State, OffscreenPointerTarget, LazyListState, Modifier (+5 more)

### Community 63 - "InstrumentDeck"
Cohesion: 0.11
Nodes (19): CodeTracePane(), CodeTracePanePreview(), com, Modifier, State, InstrumentDeck(), InstrumentDeckPreview(), androidx (+11 more)

### Community 64 - "HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework"
Cohesion: 0.18
Nodes (10): 1. Executive Summary, 2. File Status Breakdown, 3. Pending Implementation Work, 4. Verification Plan, 5. Quick Context for Resuming Agent, Already Modified on Disk (Validated), HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework, Work Package A: Dijkstra Generation & Trace Code (+2 more)

### Community 65 - "VisualizerScreenState.kt"
Cohesion: 0.14
Nodes (12): Context, SharedPreferences, ForHeap, ForTraversal, GraphCustomization, WidgetType, STATE, TELEMETRY (+4 more)

### Community 66 - "GraphBuilderGestures.kt"
Cohesion: 0.18
Nodes (12): GraphBuilderGestures(), Modifier, Offset, start, target, awaiteachgesture, awaitfirstdown, max (+4 more)

### Community 67 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 69 - "AuthState"
Cohesion: 0.28
Nodes (6): Anonymous, Authenticated, AuthRepository, AuthState, Guest, StateFlow

### Community 70 - "EditProfileSheet"
Cohesion: 0.35
Nodes (6): AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileEditorDraft

### Community 71 - "ExploreCatalogScreen"
Cohesion: 0.50
Nodes (5): ExploreCatalogScreen(), ExploreCatalogScreenPreview(), Modifier, PracticeTrack, TrackCatalogCard()

### Community 72 - "StateDeckPage"
Cohesion: 0.25
Nodes (9): ComplexityStatePill(), androidx, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage(), StateDeckPagePreview(), VarBadge() (+1 more)

### Community 73 - "ChatSessionState.kt"
Cohesion: 0.27
Nodes (8): ChatResponseProvider, OfflineCatalogChatProvider, Context, rememberChatSessionManager(), CoroutineScope, delay, job, localcontext

### Community 74 - "PROJECT.md — What is AVIA (`AlgoLens`)?"
Cohesion: 0.22
Nodes (8): Capabilities and Constraints, Operating Context, Platform, Positioning, Product Principles, Product Purpose, PROJECT.md — What is AVIA (`AlgoLens`)?, Users

### Community 75 - "BootController"
Cohesion: 0.16
Nodes (9): MainActivity, BootController, BootControllerEffect(), BootControllerTest, Bundle, ComponentActivity, enableedgetoedge, installsplashscreen (+1 more)

### Community 76 - "AlgoLensTheme"
Cohesion: 0.10
Nodes (27): Error, InputValidationResult, Valid, BottomNavBarPreview(), AlgoLensTheme(), BarVisualizer(), BarVisualizerPreview(), com (+19 more)

### Community 77 - "GraphTelemetryMode"
Cohesion: 0.29
Nodes (7): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE

### Community 78 - "InputOrder"
Cohesion: 0.29
Nodes (7): InputOrder, ALREADY_SORTED, LIFO_FIFO_STREAM, NEARLY_SORTED, RANDOM_UNSORTED, TREE_OR_GRAPH, UNSPECIFIED

### Community 79 - "Type.kt"
Cohesion: 0.38
Nodes (6): AviaType, TextUnit, font, r, TextStyle, typography

### Community 80 - "RecursionTreeOverlay"
Cohesion: 0.43
Nodes (3): IntRange, Modifier, RecursionTreeOverlay

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

### Community 86 - "AlgoCard"
Cohesion: 0.15
Nodes (15): AlgoCard(), AlgoCardChrome, AlgorithmCard(), AlgorithmCardPreview(), Modifier, AviaTheme(), AlgorithmTheorySheet(), AlgorithmTheorySheetPreview() (+7 more)

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

### Community 153 - "ChallengeQuestionType"
Cohesion: 0.40
Nodes (5): challengeEligibleIndices(), ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION

### Community 154 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

## Knowledge Gaps
- **267 isolated node(s):** `KOTLIN`, `JAVA`, `PYTHON`, `CPP`, `Loading` (+262 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 559 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **63 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `GraphTreeRenderer.kt`, `VisualizerScreenState`, `GraphTreeVisualizer`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `GraphEdgeState`, `ChallengeQuestionType`, `AlgorithmId.kt`, `VisualizerHost.kt`, `FeatureEnhancementsTest`, `BufferVisualizer`, `RegionAuxiliary`, `SwapFlight.kt`, `GraphCanvasEngine`, `SettingsScreen`, `AppSettings`, `UnifiedGraphEditorTest`, `Corner Radius Scale (`AlgoTokens`)`, `CellArrayVisualizer`, `InstrumentDeck`, `StateDeckPage`, `AlgoLensTheme`, `RecursionTreeOverlay`, `HeaderOverflowMenuHost`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `GraphTreeMutations`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `VisualizerStep`, `GraphEdgeState`, `GraphNodeState`, `AlgorithmId.kt`, `VisualizerHost.kt`, `FeatureEnhancementsTest`, `Algorithm`, `.specFor`, `AppSettings`, `GraphTool`, `UnifiedGraphEditorTest`, `Corner Radius Scale (`AlgoTokens`)`, `BstMode`, `CellArrayVisualizer`, `InstrumentDeck`, `VisualizerScreenState.kt`, `RecursionTreeOverlay`, `DeckPage`, `HeaderOverflowMenuHost`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `FirestoreSyncEngine.kt`, `VisualizerScreenState`, `PracticeSessionManager`, `ChatHistoryRepository.kt`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `TraceLanguage`, `VisualizerStep`, `AlgorithmId.kt`, `Instrument.kt`, `VisualizerHost.kt`, `FeatureEnhancementsTest`, `.processQuery`, `.specFor`, `InstrumentDeck`, `VisualizerScreenState.kt`, `ChatSessionState.kt`, `AlgoLensTheme`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `VisualizerScreenState` (e.g. with `3.2 Hoisted State Machine (`@Stable VisualizerScreenState`)` and `Don't:`) actually correct?**
  _`VisualizerScreenState` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Are the 3 inferred relationships involving `AlgorithmId` (e.g. with `2. Package & File Map (`app/src/main/java/com/avia/`)` and `Don't:`) actually correct?**
  _`AlgorithmId` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 5 inferred relationships involving `AlgoTokens` (e.g. with `2. Package & File Map (`app/src/main/java/com/avia/`)` and `Corner Radius Scale (`AlgoTokens`)`) actually correct?**
  _`AlgoTokens` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `KOTLIN`, `JAVA`, `PYTHON` to the rest of the system?**
  _267 weakly-connected nodes found - possible documentation gaps or missing edges._