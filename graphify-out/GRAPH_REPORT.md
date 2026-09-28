# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 143 files · ~204,258 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1953 nodes · 6236 edges · 172 communities (97 shown, 75 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 202 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9f507ecd`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- VisualizerStep
- PracticeSessionManager
- SwapFlight.kt
- AlgorithmStepRepository
- TraceLanguage
- OffscreenPointerBanner
- VisualizerScreenStateTest
- VisualizerBenchmarks.kt
- Instrument.kt
- ChatMarkdownMessage
- NavTab
- PredictionQuestion
- ChatSessionManager
- GraphTreeRenderer.kt
- ProfileFlowTest.kt
- UElementHandler
- UElementHandler
- GraphCapabilityProfile
- Step-by-step implementation plan
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- InputOrder
- StepToken
- VisualizerScreenState
- ChatScreen
- UnifiedGraphEditorTest.kt
- VisualizerState.kt
- BufferVisualizer
- ChatMessage
- Context
- VisualizerScreen.kt
- FeatureEnhancementsTest
- AuthRepository
- GraphTool
- AuthRepository.kt
- GlassPanel.kt
- AviaLogo.kt
- AlgoLensTheme
- BootController
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgorithmId
- CellGrid
- FeatureEnhancementsTest.kt
- AlgoLensDetectorsTest
- ChatConversation
- RecursionTreeOverlay
- ChallengeModeManagerTest.kt
- UserPreferencesTest
- ProfileScreen.kt
- FakeAuthRepository
- .generateStepsForAlgorithm
- AccountTemplateCard.kt
- .processQuery
- VisualizerHeader
- BufferOpEditor
- RegionAuxiliary
- DoubleBezelShell
- CatalogueSortMode
- RailIconButton
- Dp
- .resolve
- AlgoLensApp.kt
- InstrumentDeck
- 6. Implementation Steps & Validation Checklist
- SettingsScreen
- VisualizerHost.kt
- MainActivity.kt
- IMPLEMENTATION.md
- FlowchartShape
- TableAlignment
- AiChatBackendTest.kt
- ChatSessionState.kt
- Profile and Edit Profile
- GraphCustomization
- UnavailableFirebaseAuthRepository
- Color
- Color
- PaddingValues
- TextUnit
- .from
- DashboardScreen
- ArrayViewMode
- .specFor
- imepadding
- ComparisonBridgeOverlay.kt
- ElementState
- AlgorithmRegistry
- BstTreeNode
- .generateBSTSteps
- AlgorithmTheorySheet
- DeckPage
- HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework
- Dp
- PaddingValues
- Shape
- PredictionKind
- Detector
- Issue
- IssueRegistry
- JavaContext
- UastScanner
- UCallExpression
- UElement
- UElementHandler
- Vendor
- pi
- rotate
- UnifiedGraphEditorTest
- ExampleUnitTest
- VisualizerScreenState.kt
- .shortestPath
- BufferOp
- CellGlowPainter.kt
- GraphEdgeState
- GraphNodeState
- .Content
- ProjectConstraintBrief
- Offset
- com
- VisualizerHeaderMenuTest.kt
- GraphGoal
- BarVisualizer
- com
- .highlight
- AuthRepository
- AlgorithmSpec
- SortOrder
- VisualizerScreenState
- ArrayViewMode
- StageLegend
- detectdraggestures
- chataction
- ChatCodeSnippet
- ChatComplexitySnapshot
- ChatMessage
- ChatPromptStarter
- chatsender
- ChatSessionManager
- detecttapgestures
- projectalgorithmrecommender
- WidgetType
- MergeBufferRow
- GraphCanvasEngine
- .submitNodePrediction
- SampleData
- androidx
- GraphCapabilityProfile
- ImageVector
- VisualizerScreenState
- BufferOp
- QueueOp
- SortOrder
- graphcanvasgeometry
- start
- target
- CellArrayVisualizer
- highlightedCellPair
- .makeAlgorithm
- GuidedTourOverlay
- GraphCapabilityProfile
- GraphTool
- GraphCapabilityProfile
- GraphTool
- VisualizerStep
- graphtool
- nodeplacementmode

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 78 edges
2. `AlgorithmId` - 59 edges
3. `VisualizerStep` - 45 edges
4. `AlgoLensTheme()` - 43 edges
5. `ChatSessionManager` - 41 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `AlgoTokens` - 38 edges
8. `buildPredictionQuestion()` - 34 edges
9. `GraphCanvasEngine()` - 32 edges
10. `UnifiedGraphEditorTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `5. Build shared viewport geometry and canvas engine` --references--> `GraphTreeRenderer()`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `4.1 Viewport Pan & Pinch-to-Zoom Engine` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Step 3: Viewport & Renderer Refactoring` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `5. Verification` --references--> `BootControllerTest`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/test/java/com/example/algolens/BootControllerTest.kt

## Import Cycles
- None detected.

## Communities (172 total, 75 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.07
Nodes (192): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algohairline (+184 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.18
Nodes (5): buildPredictionQuestion(), predictionAnswerFor(), BufferItem, VisualizerStep, ChallengeModeManagerTest

### Community 2 - "PracticeSessionManager"
Cohesion: 0.06
Nodes (26): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+18 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.06
Nodes (40): abs, activity, animatable, animatecontentsize, animationspec, animationvector1d, Dp, Modifier (+32 more)

### Community 4 - "AlgorithmStepRepository"
Cohesion: 0.18
Nodes (10): AlgorithmStepRepository, Algorithm, BufferOp, Offset, QueueOp, SortOrder, VisualizerStep, bufferitem (+2 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.14
Nodes (18): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+10 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 8 - "VisualizerBenchmarks.kt"
Cohesion: 0.13
Nodes (11): androidjunit4, ExampleInstrumentedTest, FrameTimingBenchmark, StartupBenchmark, compilationmode, frametimingmetric, instrumentationregistry, macrobenchmarkrule (+3 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.11
Nodes (25): ChatTableBlock(), Modifier, AlgoHairline(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule() (+17 more)

### Community 10 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (22): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+14 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "PredictionQuestion"
Cohesion: 0.25
Nodes (21): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), VisualizerStep (+13 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.19
Nodes (13): atan2, canvas, cos, graphedgedefault, nativecanvas, Offset, paint, patheffect (+5 more)

### Community 15 - "ProfileFlowTest.kt"
Cohesion: 0.16
Nodes (12): activityscenariorule, VisualizerScreenshotTest, before, Bitmap, compositionlocalprovider, createandroidcomposerule, density, file (+4 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 18 - "GraphCapabilityProfile"
Cohesion: 0.13
Nodes (13): GraphCapabilityProfile, GraphFloatingToolbar(), Modifier, GraphFrontierTelemetryStrip(), GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), GraphEdgeState (+5 more)

### Community 19 - "Step-by-step implementation plan"
Cohesion: 0.08
Nodes (25): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE, 2. Establish authoritative state and reset contracts (+17 more)

### Community 20 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.28
Nodes (11): Detector, Issue, IssueRegistry, AlgoLensIssueRegistry, HardcodedHexColorDetector, RawDpSpacingDetector, RoundedCornerShapeLiteralDetector, VisualizerScreenMutationDetector (+3 more)

### Community 21 - "InputOrder"
Cohesion: 0.29
Nodes (7): InputOrder, ALREADY_SORTED, LIFO_FIFO_STREAM, NEARLY_SORTED, RANDOM_UNSORTED, TREE_OR_GRAPH, UNSPECIFIED

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 23 - "VisualizerScreenState"
Cohesion: 0.07
Nodes (11): GraphEditorSnapshot, androidx, ArrayViewMode, GraphEdgeState, GraphNodeState, Offset, VisualizerStep, VisualizerScreenState (+3 more)

### Community 24 - "ChatScreen"
Cohesion: 0.13
Nodes (22): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+14 more)

### Community 25 - "UnifiedGraphEditorTest.kt"
Cohesion: 0.34
Nodes (8): assertequals, assertfalse, assertnotnull, assertnull, asserttrue, graphsearch, proxy, test

### Community 26 - "VisualizerState.kt"
Cohesion: 0.18
Nodes (11): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphNodeState, Pointer, VisualizerRenderMode, BARS (+3 more)

### Community 27 - "BufferVisualizer"
Cohesion: 0.16
Nodes (14): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), BufferOp, Color, Modifier (+6 more)

### Community 28 - "ChatMessage"
Cohesion: 0.11
Nodes (20): AiChatEngine, ChatResponseProvider, OfflineCatalogChatProvider, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, ChatSender (+12 more)

### Community 29 - "Context"
Cohesion: 0.53
Nodes (3): android, UserPreferences, Context

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.17
Nodes (15): algoworkspacebackground, animatedpasstate, EmptyCanvas(), Algorithm, Modifier, VisualizerScreen(), VisualizerScreenPreview(), Modifier (+7 more)

### Community 31 - "FeatureEnhancementsTest"
Cohesion: 0.12
Nodes (3): AppSettings, FeatureEnhancementsTest, SharedPreferences

### Community 32 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 33 - "GraphTool"
Cohesion: 0.08
Nodes (29): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+21 more)

### Community 34 - "AuthRepository.kt"
Cohesion: 0.17
Nodes (7): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, Loading, ProfileValidator

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "AviaLogo.kt"
Cohesion: 0.31
Nodes (8): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource, r

### Community 37 - "AlgoLensTheme"
Cohesion: 0.18
Nodes (11): ProfileFlowTest, AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField() (+3 more)

### Community 38 - "BootController"
Cohesion: 0.19
Nodes (7): BootController, BootControllerEffect(), BootOverlay(), Modifier, BootControllerTest, delay, stable

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "AlgorithmId"
Cohesion: 0.11
Nodes (15): AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA, HEAP (+7 more)

### Community 42 - "CellGrid"
Cohesion: 0.27
Nodes (11): CellGrid(), CellItem(), Dp, LazyListState, Modifier, State, TextUnit, BottomPointerBadge() (+3 more)

### Community 43 - "FeatureEnhancementsTest.kt"
Cohesion: 0.29
Nodes (8): Algorithm, ElementState, fakeauthrepository, GraphEdgeState, GraphNodeState, guestdatamigrationpolicy, preset_options, tour_steps

### Community 44 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 45 - "ChatConversation"
Cohesion: 0.33
Nodes (3): ChatHistoryRepository, ChatConversation, JSONObject

### Community 46 - "RecursionTreeOverlay"
Cohesion: 0.23
Nodes (6): GraphIntegerEntryDialog(), Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 47 - "ChallengeModeManagerTest.kt"
Cohesion: 0.16
Nodes (12): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+4 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.16
Nodes (16): ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, authaccountstate, AuthRepository (+8 more)

### Community 50 - "FakeAuthRepository"
Cohesion: 0.23
Nodes (5): FakeAuthRepository, SignedIn, SignedOut, UserProfile, ProfileEditorDraftTest

### Community 52 - "AccountTemplateCard.kt"
Cohesion: 0.11
Nodes (23): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileEditorDraft (+15 more)

### Community 54 - "VisualizerHeader"
Cohesion: 0.27
Nodes (14): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm, AlgorithmSpec (+6 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 56 - "RegionAuxiliary"
Cohesion: 0.18
Nodes (6): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, RecursionBands

### Community 57 - "DoubleBezelShell"
Cohesion: 0.29
Nodes (10): DoubleBezelShell(), FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor() (+2 more)

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "RailIconButton"
Cohesion: 0.22
Nodes (9): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), androidx, Modifier (+1 more)

### Community 61 - ".resolve"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 62 - "AlgoLensApp.kt"
Cohesion: 0.13
Nodes (21): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, RootStage, APP (+13 more)

### Community 63 - "InstrumentDeck"
Cohesion: 0.25
Nodes (8): InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx, Modifier, State, VisualizerStep, WidgetManagerModal()

### Community 64 - "6. Implementation Steps & Validation Checklist"
Cohesion: 0.11
Nodes (18): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`), 4.3 Interactive Tool Logic & Touch Handling, 4.4 Fullscreen Edge-Aware Canvas Overlay (+10 more)

### Community 65 - "SettingsScreen"
Cohesion: 0.11
Nodes (24): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+16 more)

### Community 66 - "VisualizerHost.kt"
Cohesion: 0.31
Nodes (13): AlgorithmSpec, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas(), ArrayViewMode, Modifier, State (+5 more)

### Community 67 - "MainActivity.kt"
Cohesion: 0.28
Nodes (7): MainActivity, Bundle, ComponentActivity, 3. Minimalist boot splash, enableedgetoedge, installsplashscreen, setcontent

### Community 68 - "IMPLEMENTATION.md"
Cohesion: 0.25
Nodes (4): 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 4. Tests, 5. Verification, 6. Deviations / known issues

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 71 - "AiChatBackendTest.kt"
Cohesion: 0.25
Nodes (7): experimentalcoroutinesapi, flowchartshape, parseflowchart, parsemarkdowninline, parsemarkdowntable, runblocking, tablealignment

### Community 75 - "ChatSessionState.kt"
Cohesion: 0.15
Nodes (12): ChatAction, LaunchVisualizer, QueryFollowUp, rememberChatSessionManager(), chathistoryrepository, chatresponseprovider, CoroutineScope, job (+4 more)

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 79 - "GraphCustomization"
Cohesion: 0.10
Nodes (19): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, 1.1 Binary Search Tree (BST) Traversal Family (+11 more)

### Community 80 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.24
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 85 - ".from"
Cohesion: 0.21
Nodes (6): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Modifier, Offset

### Community 86 - "DashboardScreen"
Cohesion: 0.15
Nodes (17): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec (+9 more)

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.29
Nodes (9): animatefloatasstate, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, path, SlotTransform, strokecap (+1 more)

### Community 91 - "ElementState"
Cohesion: 0.17
Nodes (12): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+4 more)

### Community 92 - "AlgorithmRegistry"
Cohesion: 0.14
Nodes (11): AlgorithmRegistry, AlgorithmSpec, InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D (+3 more)

### Community 94 - ".generateBSTSteps"
Cohesion: 0.51
Nodes (5): GraphEdgeState, GraphNodeState, 3.1 Unifying the `customGraph` Pipeline in `AlgorithmStepRepository.kt`, Step 2: Step Repository Integration, The Core Problem Today

### Community 95 - "AlgorithmTheorySheet"
Cohesion: 0.33
Nodes (7): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Algorithm, Color, Modifier, PropertyBadge()

### Community 96 - "DeckPage"
Cohesion: 0.50
Nodes (3): DeckPage, STATE, TRACE

### Community 97 - "HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework"
Cohesion: 0.18
Nodes (10): 1. Executive Summary, 2. File Status Breakdown, 3. Pending Implementation Work, 4. Verification Plan, 5. Quick Context for Resuming Agent, Already Modified on Disk (Validated), HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework, Work Package A: Dijkstra Generation & Trace Code (+2 more)

### Community 101 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 115 - "VisualizerScreenState.kt"
Cohesion: 0.11
Nodes (12): Modifier, PlaybackRail(), PlaybackRailPreview(), Algorithm, GraphTool, rememberVisualizerScreenState(), BufferOp, 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`) (+4 more)

### Community 116 - ".shortestPath"
Cohesion: 0.18
Nodes (4): GraphSearch, Result, GraphSearchTest, 4. Integrate generators and correct preview semantics

### Community 117 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 118 - "CellGlowPainter.kt"
Cohesion: 0.60
Nodes (4): drawCellGlow(), Color, CornerRadius, drawscope

### Community 121 - ".Content"
Cohesion: 0.33
Nodes (5): Modifier, VisualizerScreenState, VisualizerStep, WeightBadgeOverlay, VisualizerOverlay

### Community 122 - "ProjectConstraintBrief"
Cohesion: 0.15
Nodes (13): ProjectAlgorithmRecommender, DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED, ProjectConstraintBrief, ProjectRecommendationPayload (+5 more)

### Community 125 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 126 - "GraphGoal"
Cohesion: 0.40
Nodes (5): GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE, SHORTEST_UNWEIGHTED_PATH, WEIGHTED_SHORTEST_PATH

### Community 127 - "BarVisualizer"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

### Community 129 - ".highlight"
Cohesion: 0.22
Nodes (6): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), TraceLanguage

### Community 132 - "SortOrder"
Cohesion: 0.33
Nodes (5): SortOrder, ASC, DESC, CustomizeInputSheet(), CustomizeInputSheetPreview()

### Community 135 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 146 - "WidgetType"
Cohesion: 0.40
Nodes (4): WidgetType, STATE, TELEMETRY, TRACE

### Community 148 - "GraphCanvasEngine"
Cohesion: 0.11
Nodes (16): GraphTreeMutations, GraphEdgeState, GraphNodeState, Offset, GraphCanvasEngine(), GraphEdgeState, GraphNodeState, Modifier (+8 more)

### Community 161 - "CellArrayVisualizer"
Cohesion: 0.40
Nodes (5): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State

### Community 164 - "GuidedTourOverlay"
Cohesion: 0.67
Nodes (3): GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier

## Knowledge Gaps
- **201 isolated node(s):** `MOVE`, `ADD`, `LINK`, `WEIGHT`, `ENDPOINTS` (+196 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 494 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **75 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `VisualizerStep`, `PracticeSessionManager`, `AlgorithmStepRepository`, `TraceLanguage`, `AlgoLensTheme`, `AiChatBackendTest.kt`, `ChatSessionState.kt`, `FeatureEnhancementsTest.kt`, `AlgorithmRegistry`, `ChallengeModeManagerTest.kt`, `GraphCustomization`, `SampleData`, `.specFor`, `UnifiedGraphEditorTest.kt`, `ProjectConstraintBrief`, `ChatMessage`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChatScreen.kt`, `.resolve`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `VisualizerHost.kt`, `AlgorithmStepRepository`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `ElementState`, `GraphTreeRenderer.kt`, `RecursionTreeOverlay`, `UnifiedGraphEditorTest`, `WidgetType`, `VisualizerScreenState.kt`, `GraphCanvasEngine`, `.submitNodePrediction`, `RegionAuxiliary`, `UnifiedGraphEditorTest.kt`, `.specFor`, `BstTreeNode`, `InstrumentDeck`?**
  _High betweenness centrality (0.063) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `VisualizerScreenState` (e.g. with `.visualizerScreenState_decoratesNodesWithUserCustomCoordinates()` and `.visualizerScreenState_effectiveTraversalNodeIdsExplicitEmpty()`) actually correct?**
  _`VisualizerScreenState` has 6 INFERRED edges - model-reasoned connections that need verification._
- **What connects `MOVE`, `ADD`, `LINK` to the rest of the system?**
  _201 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.07270119521912351 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager` be split into smaller, more focused modules?**
  _Cohesion score 0.06285714285714286 - nodes in this community are weakly interconnected._