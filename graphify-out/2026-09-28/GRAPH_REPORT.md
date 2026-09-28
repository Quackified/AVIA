# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 143 files · ~205,539 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1966 nodes · 6279 edges · 210 communities (91 shown, 119 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 136 edges (avg confidence: 0.89)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `31fb097a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- VisualizerStep
- GraphTreeRenderer.kt
- UnifiedGraphEditorTest.kt
- PracticeSessionManager
- VisualizerScreenState
- AlgorithmStepRepository
- SwapFlight.kt
- AlgorithmId
- GraphEdgeState
- VisualizerScreenStateTest
- ChatSessionManager
- ChatSessionState.kt
- ProfileFlowTest.kt
- GraphNodeState
- ChatMarkdownMessage
- FeatureEnhancementsTest.kt
- ProjectConstraintBrief
- GraphCanvasEngine
- BootController
- TraceLanguage
- ChatScreen
- AccountTemplateCard.kt
- AlgoLensApp.kt
- GraphCustomization
- Step-by-step implementation plan
- ChatHistoryRepository
- .specFor
- CompactIconButton
- RegionAuxiliary
- VisualizerScreen
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AuthRepository
- UnavailableFirebaseAuthRepository
- StepToken
- ProfileScreen.kt
- OffscreenPointerBanner
- AlgorithmRegistry
- ElementState
- AlgoLensDetectorsTest
- 6. Implementation Steps & Validation Checklist
- AlgoLensDetectorsTest
- .processQuery
- AviaLogo.kt
- Instrument.kt
- FakeAuthRepository
- .generateStepsForAlgorithm
- EditProfileSheet
- PredictionQuestion
- VisualizerHost.kt
- BstTreeNode
- SettingsScreen
- DoubleBezelShell
- FeatureEnhancementsTest
- BufferVisualizer
- CodeListing
- InstrumentDeck
- RecursionTreeOverlay
- AuthRepository.kt
- HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework
- AppSettings.kt
- ExampleUnitTest
- PredictionKind
- BufferOpEditor
- UElementHandler
- callName
- UElementHandler
- callName
- AppSettings
- AlgoLensTheme
- StateDeckPage
- BufferOp
- InputOrder
- AlgorithmTheorySheet
- VisualizerState.kt
- SortOrder
- CatalogueSortMode
- ExploreCatalogScreen
- StageLegend
- WidgetType
- Profile and Edit Profile
- RootStage
- FlowchartShape
- TableAlignment
- ArrayViewMode
- DeckPage
- com
- ChallengeModeManagerTest.kt
- CanvasChallengePrompt
- MainActivity.kt
- AlgorithmSpec
- GraphTelemetryMode
- UserPreferences.kt
- androidx
- .generateBSTSteps
- PhaseStrip
- DijkstraStepsTest
- VisualizerHeaderMenuTest.kt
- DataScale
- AuthRepository
- AuthRepository
- Color
- RecursionBands
- VisualizerStep
- GraphCapabilityProfile
- GraphEdgeState
- GraphNodeState
- GraphTool
- GraphEdgeState
- GraphNodeState
- Offset
- GraphCapabilityProfile
- GraphEdgeState
- GraphNodeState
- GraphTool
- GraphEdgeState
- GraphNodeState
- com
- GraphCapabilityProfile
- GraphEdgeState
- GraphNodeState
- VisualizerStep
- VisualizerScreenState
- PlaybackRail
- VisualizerStep
- WeightBadgeOverlay
- AlgorithmSpec
- VisualizerScreenState
- VisualizerStep
- AlgorithmSpec
- VisualizerScreenState
- VisualizerStep
- Color
- Dp
- GraphTool
- LazyListState
- VisualizerScreenState
- VisualizerStep
- TextUnit
- authaccountstate
- AuthRepository
- BootController
- bootcontrollereffect
- ArrayViewMode
- BufferOp
- chataction
- chatbubble
- ChatCodeSnippet
- ChatComplexitySnapshot
- chathistoryrepository
- ChatMessage
- ChatPromptStarter
- chatresponseprovider
- chatsender
- ChatSessionManager
- compacticonbutton
- compasscalibration
- customswitch
- detectdraggestures
- detecttapgestures
- explore
- fakeauthrepository
- graphcanvasgeometry
- GraphEdgeState
- GraphNodeState
- graphsearch
- GraphTelemetryMode
- guestdatamigrationpolicy
- home
- iconpillbutton
- imepadding
- instrumentmeter
- Detector
- Issue
- IssueRegistry
- JavaContext
- UastScanner
- UCallExpression
- UElement
- UElementHandler
- Vendor
- NavTab
- nodeplacementmode
- offlinecatalogchatprovider
- parsemarkdowninline
- person
- pi
- projectalgorithmrecommender
- QueueOp
- railiconbutton
- rotate
- sectionlabel
- segmentedtoggle
- Shape
- smoothpanelexpansion
- SortOrder
- start
- target
- TraceLanguage
- tune
- unavailablefirebaseauthrepository
- UserProfile
- visualizerfamily
- VisualizerStep

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 84 edges
2. `VisualizerStep` - 82 edges
3. `AlgorithmId` - 60 edges
4. `AlgoTokens` - 57 edges
5. `AlgoLensTheme()` - 55 edges
6. `AlgoType` - 43 edges
7. `VisualizerScreenStateTest` - 41 edges
8. `ChatSessionManager` - 41 edges
9. `AlgoGlyphs` - 38 edges
10. `buildPredictionQuestion()` - 34 edges

## Surprising Connections (you probably didn't know these)
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `4.1 Viewport Pan & Pinch-to-Zoom Engine` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Step 3: Viewport & Renderer Refactoring` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Step 1: Capability Profiles & Models` --references--> `AlgorithmSpec`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/model/AlgorithmId.kt
- `5. Build shared viewport geometry and canvas engine` --references--> `GraphTreeRenderer()`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt

## Import Cycles
- None detected.

## Communities (210 total, 119 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.08
Nodes (185): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algohairline (+177 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.16
Nodes (8): buildPredictionQuestion(), graphVisitQuestion(), highlightedCellPair(), predictionAnswerFor(), quickSortQuestion(), BufferItem, VisualizerStep, ChallengeModeManagerTest

### Community 2 - "GraphTreeRenderer.kt"
Cohesion: 0.06
Nodes (54): animatefloat, animatefloatasstate, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier (+46 more)

### Community 3 - "UnifiedGraphEditorTest.kt"
Cohesion: 0.08
Nodes (27): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+19 more)

### Community 4 - "PracticeSessionManager"
Cohesion: 0.06
Nodes (26): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+18 more)

### Community 5 - "VisualizerScreenState"
Cohesion: 0.06
Nodes (19): GraphEditorSnapshot, Algorithm, AlgorithmId, androidx, BufferOp, GraphEdgeState, GraphNodeState, Offset (+11 more)

### Community 6 - "AlgorithmStepRepository"
Cohesion: 0.18
Nodes (11): AlgorithmStepRepository, Algorithm, AlgorithmId, Offset, QueueOp, SortOrder, VisualizerStep, bufferitem (+3 more)

### Community 7 - "SwapFlight.kt"
Cohesion: 0.06
Nodes (42): abs, activity, animatable, animatecontentsize, animationspec, animationvector1d, Color, Dp (+34 more)

### Community 8 - "AlgorithmId"
Cohesion: 0.10
Nodes (20): AiChatEngine, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA (+12 more)

### Community 9 - "GraphEdgeState"
Cohesion: 0.14
Nodes (9): GraphSearch, Result, GraphFrontierTelemetryStrip(), GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphSearchTest (+1 more)

### Community 12 - "ChatSessionState.kt"
Cohesion: 0.07
Nodes (31): ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus (+23 more)

### Community 13 - "ProfileFlowTest.kt"
Cohesion: 0.08
Nodes (23): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark, Bitmap (+15 more)

### Community 14 - "GraphNodeState"
Cohesion: 0.17
Nodes (5): GraphCanvasGeometry, GraphTreeRenderer(), Offset, GraphNodeState, UnifiedGraphEditorTest

### Community 15 - "ChatMarkdownMessage"
Cohesion: 0.14
Nodes (16): CalloutType, CAUTION, IMPORTANT, NOTE, TIP, WARNING, ChatCalloutBlock(), ChatEmbeddedCodeBlock() (+8 more)

### Community 16 - "FeatureEnhancementsTest.kt"
Cohesion: 0.22
Nodes (13): Algorithm, Algorithm, SampleData, Algorithm, assertequals, assertfalse, assertnotnull, assertnull (+5 more)

### Community 17 - "ProjectConstraintBrief"
Cohesion: 0.13
Nodes (15): ProjectAlgorithmRecommender, GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE, SHORTEST_UNWEIGHTED_PATH, WEIGHTED_SHORTEST_PATH, ProjectConstraintBrief, ProjectRecommendationPayload (+7 more)

### Community 18 - "GraphCanvasEngine"
Cohesion: 0.11
Nodes (15): GraphTreeMutations, ElementState, GraphEdgeState, GraphNodeState, Offset, GraphCanvasEngine(), Modifier, Offset (+7 more)

### Community 19 - "BootController"
Cohesion: 0.18
Nodes (7): BootController, BootControllerEffect(), BootControllerTest, 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`), 3. Minimalist boot splash, 5. Verification, 6. Deviations / known issues

### Community 20 - "TraceLanguage"
Cohesion: 0.26
Nodes (7): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 21 - "ChatScreen"
Cohesion: 0.13
Nodes (22): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+14 more)

### Community 22 - "AccountTemplateCard.kt"
Cohesion: 0.14
Nodes (19): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), Dp, Modifier, Shape, ProfileAvatar(), ProfileField() (+11 more)

### Community 23 - "AlgoLensApp.kt"
Cohesion: 0.12
Nodes (23): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, BootOverlay(), Modifier (+15 more)

### Community 24 - "GraphCustomization"
Cohesion: 0.10
Nodes (19): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, 1.1 Binary Search Tree (BST) Traversal Family (+11 more)

### Community 25 - "Step-by-step implementation plan"
Cohesion: 0.11
Nodes (18): 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 5. Build shared viewport geometry and canvas engine, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document, Automated coverage (+10 more)

### Community 26 - "ChatHistoryRepository"
Cohesion: 0.33
Nodes (3): ChatHistoryRepository, ChatConversation, JSONObject

### Community 27 - ".specFor"
Cohesion: 0.05
Nodes (29): Algorithm, InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D (+21 more)

### Community 28 - "CompactIconButton"
Cohesion: 0.16
Nodes (19): CompactIconButton(), IconPillButton(), androidx, Color, ImageVector, Modifier, RailIconButton(), SegmentedToggle() (+11 more)

### Community 29 - "RegionAuxiliary"
Cohesion: 0.21
Nodes (6): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow

### Community 30 - "VisualizerScreen"
Cohesion: 0.18
Nodes (12): AiTutorSheet(), AiTutorSheetPreview(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, EmptyCanvas(), Algorithm, Modifier (+4 more)

### Community 31 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement (+5 more)

### Community 32 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): Detector, implementation, Issue, IssueRegistry, AlgoLensIssueRegistry, HardcodedHexColorDetector, RawDpSpacingDetector, RoundedCornerShapeLiteralDetector (+5 more)

### Community 33 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 34 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.24
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 35 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 36 - "ProfileScreen.kt"
Cohesion: 0.20
Nodes (13): ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, bitmapfactory, cliptobounds (+5 more)

### Community 37 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 38 - "AlgorithmRegistry"
Cohesion: 0.13
Nodes (10): AlgorithmRegistry, GraphCapabilityProfile, GraphFloatingToolbar(), Modifier, GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), Modifier (+2 more)

### Community 39 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "6. Implementation Steps & Validation Checklist"
Cohesion: 0.10
Nodes (20): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`) (+12 more)

### Community 42 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 43 - ".processQuery"
Cohesion: 0.14
Nodes (9): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+1 more)

### Community 44 - "AviaLogo.kt"
Cohesion: 0.18
Nodes (12): AviaLogo(), Color, Dp, Modifier, TextUnit, colorfilter, font, image (+4 more)

### Community 45 - "Instrument.kt"
Cohesion: 0.15
Nodes (21): AlgoHairline(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color, Dp (+13 more)

### Community 46 - "FakeAuthRepository"
Cohesion: 0.31
Nodes (4): FakeAuthRepository, SignedIn, SignedOut, UserProfile

### Community 48 - "EditProfileSheet"
Cohesion: 0.16
Nodes (6): ProfileFlowTest, EditProfileSheet(), ProfileEditorDraft, ProfileTheme(), ProfileTopBar(), ProfileEditorDraftTest

### Community 49 - "PredictionQuestion"
Cohesion: 0.32
Nodes (12): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), heapQuestion(), insertionSortQuestion(), mergeSortQuestion(), parseStepToken(), PredictionQuestion (+4 more)

### Community 50 - "VisualizerHost.kt"
Cohesion: 0.16
Nodes (20): AlgorithmSpec, BarVisualizer(), BarVisualizerPreview(), com, Modifier, CellArrayVisualizer(), CellArrayVisualizerPreview(), com (+12 more)

### Community 52 - "SettingsScreen"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 53 - "DoubleBezelShell"
Cohesion: 0.25
Nodes (11): DoubleBezelShell(), PaddingValues, FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen() (+3 more)

### Community 54 - "FeatureEnhancementsTest"
Cohesion: 0.14
Nodes (3): AnnotatedString, SyntaxHighlighter, FeatureEnhancementsTest

### Community 55 - "BufferVisualizer"
Cohesion: 0.20
Nodes (12): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), BufferOp, Modifier, QueueOp (+4 more)

### Community 56 - "CodeListing"
Cohesion: 0.20
Nodes (10): CodeLineAccent, Family, Color, CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color (+2 more)

### Community 57 - "InstrumentDeck"
Cohesion: 0.15
Nodes (13): CodeTracePane(), CodeTracePanePreview(), com, Modifier, State, InstrumentDeck(), InstrumentDeckPreview(), Algorithm (+5 more)

### Community 58 - "RecursionTreeOverlay"
Cohesion: 0.23
Nodes (6): GraphIntegerEntryDialog(), Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 59 - "AuthRepository.kt"
Cohesion: 0.17
Nodes (7): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, Loading, ProfileValidator

### Community 60 - "HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework"
Cohesion: 0.18
Nodes (10): 1. Executive Summary, 2. File Status Breakdown, 3. Pending Implementation Work, 4. Verification Plan, 5. Quick Context for Resuming Agent, Already Modified on Disk (Validated), HANDOVER - Node Graph Editor, Explore UI Constraints & Dijkstra Framework, Work Package A: Dijkstra Generation & Trace Code (+2 more)

### Community 61 - "AppSettings.kt"
Cohesion: 0.21
Nodes (5): Context, UserPreferencesTest, mutablelongstateof, SharedPreferences, stable

### Community 63 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 64 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 65 - "UElementHandler"
Cohesion: 0.36
Nodes (6): JavaContext, UElementHandler, UElementHandler, UElementHandler, UElementHandler, UElementHandler

### Community 66 - "callName"
Cohesion: 0.29
Nodes (6): callName(), hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), UCallExpression, normalizedPath()

### Community 67 - "UElementHandler"
Cohesion: 0.36
Nodes (6): UElementHandler, JavaContext, UElementHandler, UElementHandler, UElementHandler, UElementHandler

### Community 68 - "callName"
Cohesion: 0.29
Nodes (6): callName(), hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UCallExpression

### Community 70 - "AlgoLensTheme"
Cohesion: 0.19
Nodes (13): AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField(), StartNodeDropdown() (+5 more)

### Community 71 - "StateDeckPage"
Cohesion: 0.25
Nodes (9): ComplexityStatePill(), androidx, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage(), StateDeckPagePreview(), VarBadge() (+1 more)

### Community 72 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 73 - "InputOrder"
Cohesion: 0.29
Nodes (7): InputOrder, ALREADY_SORTED, LIFO_FIFO_STREAM, NEARLY_SORTED, RANDOM_UNSORTED, TREE_OR_GRAPH, UNSPECIFIED

### Community 74 - "AlgorithmTheorySheet"
Cohesion: 0.33
Nodes (7): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Algorithm, Color, Modifier, PropertyBadge()

### Community 75 - "VisualizerState.kt"
Cohesion: 0.29
Nodes (6): Pointer, VisualizerRenderMode, BARS, BUFFER, CELLS, GRAPH_TREE

### Community 76 - "SortOrder"
Cohesion: 0.33
Nodes (5): SortOrder, ASC, DESC, CustomizeInputSheet(), CustomizeInputSheetPreview()

### Community 77 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 78 - "ExploreCatalogScreen"
Cohesion: 0.50
Nodes (5): ExploreCatalogScreen(), ExploreCatalogScreenPreview(), Modifier, PracticeTrack, TrackCatalogCard()

### Community 79 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 80 - "WidgetType"
Cohesion: 0.40
Nodes (4): WidgetType, STATE, TELEMETRY, TRACE

### Community 81 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 82 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 83 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 84 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 85 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 86 - "DeckPage"
Cohesion: 0.50
Nodes (3): DeckPage, STATE, TRACE

### Community 88 - "ChallengeModeManagerTest.kt"
Cohesion: 0.17
Nodes (10): challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION, FeedbackRow(), Indices (+2 more)

### Community 89 - "CanvasChallengePrompt"
Cohesion: 0.24
Nodes (8): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeState, Modifier, PredictionPromptBody(), PromptHeader(), TapIndicesChip(), YesNoButtonRow()

### Community 90 - "MainActivity.kt"
Cohesion: 0.32
Nodes (6): MainActivity, Bundle, ComponentActivity, enableedgetoedge, installsplashscreen, setcontent

### Community 92 - "GraphTelemetryMode"
Cohesion: 0.29
Nodes (7): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE

### Community 93 - "UserPreferences.kt"
Cohesion: 0.53
Nodes (3): android, Context, UserPreferences

### Community 96 - ".generateBSTSteps"
Cohesion: 0.44
Nodes (5): GraphEdgeState, GraphNodeState, 3.1 Unifying the `customGraph` Pipeline in `AlgorithmStepRepository.kt`, Step 2: Step Repository Integration, The Core Problem Today

### Community 97 - "PhaseStrip"
Cohesion: 0.40
Nodes (3): androidx, Modifier, PhaseStrip

### Community 99 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 100 - "DataScale"
Cohesion: 0.40
Nodes (5): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED

### Community 125 - "PlaybackRail"
Cohesion: 0.67
Nodes (3): Modifier, PlaybackRail(), PlaybackRailPreview()

## Knowledge Gaps
- **201 isolated node(s):** `TRACE`, `STATE`, `TELEMETRY`, `ArrayPreset`, `TourStep` (+196 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 528 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **119 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CodeListing()` connect `CodeListing` to `ChatScreen.kt`, `InstrumentDeck`, `TraceLanguage`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `UnifiedGraphEditorTest.kt`, `AlgorithmStepRepository`, `VisualizerScreenStateTest`, `GraphNodeState`, `FeatureEnhancementsTest.kt`, `GraphCanvasEngine`, `.specFor`, `CompactIconButton`, `RegionAuxiliary`, `OffscreenPointerBanner`, `6. Implementation Steps & Validation Checklist`, `.generateStepsForAlgorithm`, `VisualizerHost.kt`, `BstTreeNode`, `InstrumentDeck`, `RecursionTreeOverlay`, `WidgetType`, `PlaybackRail`, `WeightBadgeOverlay`?**
  _High betweenness centrality (0.065) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `VisualizerStep`, `UnifiedGraphEditorTest.kt`, `PracticeSessionManager`, `AlgorithmRegistry`, `AlgoLensTheme`, `ChatSessionState.kt`, `FeatureEnhancementsTest.kt`, `ProjectConstraintBrief`, `VisualizerHost.kt`, `TraceLanguage`, `ChallengeModeManagerTest.kt`, `CanvasChallengePrompt`, `.specFor`, `GraphCustomization`?**
  _High betweenness centrality (0.059) - this node is a cross-community bridge._
- **What connects `TRACE`, `STATE`, `TELEMETRY` to the rest of the system?**
  _201 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.07648042823686851 - nodes in this community are weakly interconnected._
- **Should `GraphTreeRenderer.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.05649717514124294 - nodes in this community are weakly interconnected._
- **Should `UnifiedGraphEditorTest.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.08045977011494253 - nodes in this community are weakly interconnected._