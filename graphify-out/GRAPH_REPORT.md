# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 143 files · ~203,622 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1946 nodes · 6207 edges · 161 communities (98 shown, 63 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 192 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9abc712b`
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
- GraphEdgeState
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
- FeatureEnhancementsTest.kt
- VisualizerState.kt
- BufferVisualizer
- ChatMessage
- VisualizerScreenState.kt
- VisualizerScreen
- FeatureEnhancementsTest
- AuthRepository
- GraphBuilderGestures.kt
- AuthRepository.kt
- GlassPanel.kt
- AviaLogo.kt
- CustomizeGraphSheet
- BootController
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgorithmId
- CellGrid
- StateDeckPage
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
- GraphTelemetryMode
- DoubleBezelShell
- CatalogueSortMode
- ProfileEditorDraft
- Dp
- .resolve
- AlgoLensApp.kt
- InstrumentDeck
- 6. Implementation Steps & Validation Checklist
- SettingsScreen
- VisualizerHost.kt
- GraphTool
- AlgoLensTheme
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
- GraphTreeMutations
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
- rememberVisualizerScreenState
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
- GraphCapabilityProfile.kt
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
- GraphEditorSnapshot
- GraphCanvasEngine
- .submitNodePrediction
- SampleData
- androidx
- GraphTreeVisualizer
- ImageVector
- VisualizerScreenState
- BufferOp
- QueueOp
- SortOrder
- graphcanvasgeometry
- start
- target

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 79 edges
2. `AlgorithmId` - 59 edges
3. `VisualizerStep` - 45 edges
4. `AlgoLensTheme()` - 43 edges
5. `ChatSessionManager` - 41 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `AlgoTokens` - 38 edges
8. `buildPredictionQuestion()` - 34 edges
9. `GraphCanvasEngine()` - 31 edges
10. `ChallengeModeManagerTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `4.1 Viewport Pan & Pinch-to-Zoom Engine` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Step 3: Viewport & Renderer Refactoring` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `Step 1: Capability Profiles & Models` --references--> `AlgorithmSpec`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/model/AlgorithmId.kt
- `2. Chat UI cleanup (`ui/chat/ChatScreen.kt`)` --references--> `pressPhysics()`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/main/java/com/example/algolens/ui/components/Instrument.kt

## Import Cycles
- None detected.

## Communities (161 total, 63 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.07
Nodes (193): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algohairline (+185 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.16
Nodes (6): buildPredictionQuestion(), highlightedCellPair(), predictionAnswerFor(), BufferItem, VisualizerStep, ChallengeModeManagerTest

### Community 2 - "PracticeSessionManager"
Cohesion: 0.06
Nodes (26): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+18 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.06
Nodes (40): abs, activity, animatable, animatecontentsize, animationspec, animationvector1d, Dp, Modifier (+32 more)

### Community 4 - "AlgorithmStepRepository"
Cohesion: 0.20
Nodes (9): AlgorithmStepRepository, Algorithm, BufferOp, Offset, SortOrder, VisualizerStep, bufferitem, log (+1 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.14
Nodes (18): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+10 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 7 - "VisualizerScreenStateTest"
Cohesion: 0.10
Nodes (3): Algorithm, VisualizerScreenStateTest, 4. Tests

### Community 8 - "GraphEdgeState"
Cohesion: 0.23
Nodes (3): GraphEdgeState, GraphNodeState, Offset

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
Cohesion: 0.28
Nodes (19): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), VisualizerStep (+11 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.17
Nodes (11): atan2, cos, graphedgedefault, immutable, nativecanvas, paint, patheffect, sin (+3 more)

### Community 15 - "ProfileFlowTest.kt"
Cohesion: 0.08
Nodes (24): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark, Bitmap (+16 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 18 - "GraphCapabilityProfile"
Cohesion: 0.23
Nodes (3): GraphCapabilityProfile, GraphFloatingToolbar(), Modifier

### Community 19 - "Step-by-step implementation plan"
Cohesion: 0.11
Nodes (17): 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document, Automated coverage, Compose/device checks (+9 more)

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
Cohesion: 0.08
Nodes (12): androidx, ArrayViewMode, GraphEdgeState, GraphNodeState, Offset, VisualizerStep, VisualizerScreenState, BufferOp (+4 more)

### Community 24 - "ChatScreen"
Cohesion: 0.13
Nodes (22): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+14 more)

### Community 25 - "FeatureEnhancementsTest.kt"
Cohesion: 0.19
Nodes (18): Algorithm, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, ElementState, GraphEdgeState (+10 more)

### Community 26 - "VisualizerState.kt"
Cohesion: 0.18
Nodes (11): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphNodeState, Pointer, VisualizerRenderMode, BARS (+3 more)

### Community 27 - "BufferVisualizer"
Cohesion: 0.16
Nodes (14): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), BufferOp, Color, Modifier (+6 more)

### Community 28 - "ChatMessage"
Cohesion: 0.11
Nodes (20): AiChatEngine, ChatResponseProvider, OfflineCatalogChatProvider, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, ChatSender (+12 more)

### Community 29 - "VisualizerScreenState.kt"
Cohesion: 0.19
Nodes (10): android, UserPreferences, GraphTool, Context, delay, GraphCustomization, launchedeffect, mutablelongstateof (+2 more)

### Community 30 - "VisualizerScreen"
Cohesion: 0.22
Nodes (10): GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, EmptyCanvas(), Algorithm, Modifier, VisualizerScreen(), VisualizerScreenPreview() (+2 more)

### Community 31 - "FeatureEnhancementsTest"
Cohesion: 0.13
Nodes (3): AppSettings, FeatureEnhancementsTest, SharedPreferences

### Community 32 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 33 - "GraphBuilderGestures.kt"
Cohesion: 0.13
Nodes (18): GraphBuilderGestures(), GraphCapabilityProfile, GraphEdgeState, GraphNodeState, GraphTool, Modifier, Offset, start (+10 more)

### Community 34 - "AuthRepository.kt"
Cohesion: 0.17
Nodes (7): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, Loading, ProfileValidator

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "AviaLogo.kt"
Cohesion: 0.15
Nodes (14): BootOverlay(), Modifier, AviaLogo(), Color, Dp, Modifier, colorfilter, font (+6 more)

### Community 37 - "CustomizeGraphSheet"
Cohesion: 0.25
Nodes (9): CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField(), StartNodeDropdown(), ValuesField() (+1 more)

### Community 38 - "BootController"
Cohesion: 0.13
Nodes (13): MainActivity, BootController, BootControllerEffect(), BootControllerTest, Bundle, ComponentActivity, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 3. Minimalist boot splash (+5 more)

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

### Community 43 - "StateDeckPage"
Cohesion: 0.27
Nodes (10): ComplexityStatePill(), androidx, Modifier, VisualizerStep, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage(), StateDeckPagePreview() (+2 more)

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
Cohesion: 0.13
Nodes (14): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+6 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.16
Nodes (16): ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, authaccountstate, AuthRepository (+8 more)

### Community 50 - "FakeAuthRepository"
Cohesion: 0.28
Nodes (4): FakeAuthRepository, SignedIn, SignedOut, UserProfile

### Community 51 - ".generateStepsForAlgorithm"
Cohesion: 0.18
Nodes (3): QueueOp, AlgorithmStepRepositoryTest, DijkstraStepsTest

### Community 52 - "AccountTemplateCard.kt"
Cohesion: 0.13
Nodes (21): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileField() (+13 more)

### Community 54 - "VisualizerHeader"
Cohesion: 0.06
Nodes (36): SortOrder, ASC, DESC, AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, IconPillButton() (+28 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 56 - "GraphTelemetryMode"
Cohesion: 0.29
Nodes (7): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE

### Community 57 - "DoubleBezelShell"
Cohesion: 0.29
Nodes (10): DoubleBezelShell(), FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor() (+2 more)

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "ProfileEditorDraft"
Cohesion: 0.27
Nodes (3): ProfileEditorDraft, ProfileEditorDraftTest, UserProfile

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
Cohesion: 0.10
Nodes (20): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`) (+12 more)

### Community 65 - "SettingsScreen"
Cohesion: 0.20
Nodes (14): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+6 more)

### Community 66 - "VisualizerHost.kt"
Cohesion: 0.22
Nodes (17): AlgorithmSpec, CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State, Modifier, PhaseBanner() (+9 more)

### Community 67 - "GraphTool"
Cohesion: 0.22
Nodes (10): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, GraphBuilderBanner() (+2 more)

### Community 68 - "AlgoLensTheme"
Cohesion: 0.29
Nodes (5): ProfileFlowTest, AlgoLensTheme(), Modifier, TraceStrip(), TraceStripPreview()

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
Cohesion: 0.27
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 85 - ".from"
Cohesion: 0.23
Nodes (7): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Modifier, Offset, 5. Build shared viewport geometry and canvas engine

### Community 86 - "DashboardScreen"
Cohesion: 0.13
Nodes (18): Algorithm, AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, CatalogueGroupHeader(), CatalogueSection, CategoryChip() (+10 more)

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.25
Nodes (10): animatefloatasstate, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, canvas, path, SlotTransform (+2 more)

### Community 91 - "ElementState"
Cohesion: 0.17
Nodes (11): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+3 more)

### Community 92 - "AlgorithmRegistry"
Cohesion: 0.19
Nodes (10): AlgorithmRegistry, AlgorithmSpec, InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D (+2 more)

### Community 94 - ".generateBSTSteps"
Cohesion: 0.44
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

### Community 115 - "rememberVisualizerScreenState"
Cohesion: 0.17
Nodes (5): Modifier, PlaybackRail(), PlaybackRailPreview(), Algorithm, rememberVisualizerScreenState()

### Community 116 - ".shortestPath"
Cohesion: 0.19
Nodes (4): GraphSearch, Result, GraphSearchTest, Relevant existing implementation

### Community 117 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 118 - "CellGlowPainter.kt"
Cohesion: 0.47
Nodes (5): drawCellGlow(), Color, CornerRadius, drawscope, stroke

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
Cohesion: 0.33
Nodes (3): AnnotatedString, SyntaxHighlighter, TraceLanguage

### Community 132 - "GraphCapabilityProfile.kt"
Cohesion: 0.40
Nodes (4): NodePlacementMode, FREEFORM, HEAP_ARRAY_PUSH, KEYED_BST_INSERT

### Community 135 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 146 - "WidgetType"
Cohesion: 0.40
Nodes (4): WidgetType, STATE, TELEMETRY, TRACE

### Community 148 - "GraphCanvasEngine"
Cohesion: 0.12
Nodes (14): GraphCanvasEngine(), GraphCapabilityProfile, GraphEdgeState, GraphNodeState, GraphTool, Modifier, Offset, start (+6 more)

### Community 152 - "GraphTreeVisualizer"
Cohesion: 0.27
Nodes (11): GraphFrontierTelemetryStrip(), GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), GraphCapabilityProfile, GraphEdgeState, GraphNodeState, Modifier (+3 more)

## Knowledge Gaps
- **201 isolated node(s):** `TRACE`, `STATE`, `TELEMETRY`, `ArrayPreset`, `TourStep` (+196 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 488 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **63 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `VisualizerStep`, `PracticeSessionManager`, `AlgorithmStepRepository`, `TraceLanguage`, `CustomizeGraphSheet`, `AiChatBackendTest.kt`, `ChatSessionState.kt`, `AlgorithmRegistry`, `ChallengeModeManagerTest.kt`, `GraphCustomization`, `SampleData`, `.specFor`, `FeatureEnhancementsTest.kt`, `ProjectConstraintBrief`, `ChatMessage`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `AlgorithmStepRepository`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `WidgetType`, `GraphEditorSnapshot`, `.submitNodePrediction`, `FeatureEnhancementsTest.kt`, `VisualizerScreenState.kt`, `RecursionTreeOverlay`, `.generateStepsForAlgorithm`, `VisualizerHeader`, `InstrumentDeck`, `6. Implementation Steps & Validation Checklist`, `VisualizerHost.kt`, `.specFor`, `ElementState`, `GraphTreeMutations`, `UnifiedGraphEditorTest`, `rememberVisualizerScreenState`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChatScreen.kt`, `.resolve`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **What connects `TRACE`, `STATE`, `TELEMETRY` to the rest of the system?**
  _201 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.07240102892276805 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager` be split into smaller, more focused modules?**
  _Cohesion score 0.06285714285714286 - nodes in this community are weakly interconnected._
- **Should `SwapFlight.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06342494714587738 - nodes in this community are weakly interconnected._