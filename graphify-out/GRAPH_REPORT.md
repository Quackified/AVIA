# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 146 files · ~210,882 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1958 nodes · 6318 edges · 205 communities (98 shown, 107 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 119 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3e9cec74`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BufferVisualizer.kt
- ChatMarkdownMessage
- VisualizerScreenState
- SwapFlight.kt
- BufferOpEditor
- PracticeSessionManager
- FeatureEnhancementsTest.kt
- .processQuery
- VisualizerScreenStateTest
- GraphCanvasEngine
- VisualizerStep
- ProfileFlowTest.kt
- GraphBuilderGestures.kt
- ChatScreen.kt
- GraphEdgeState
- ChatSessionManager
- BootController
- .generateStepsForAlgorithm
- Instrument.kt
- TraceLanguage
- AlgorithmSpec
- AlgoLensApp.kt
- AccountTemplateCard.kt
- GraphTreeVisualizer
- AppSettings
- ChatHistoryRepository.kt
- ChatMessage.kt
- GraphTreeMotionEngine.kt
- UnifiedGraphEditorTest
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- UElementHandler
- UElementHandler
- AlgoCard
- ChatHistoryRepository
- VisualizerHost.kt
- ProfileScreen.kt
- GlassSurface
- CustomizeGraphSheet
- AlgorithmId
- AuthRepository
- UnavailableFirebaseAuthRepository
- PredictionQuestion
- StepToken
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgoLensDetectorsTest
- ActivePairPointerBracket
- .specFor
- FakeAuthRepository
- RegionAuxiliary
- AviaLogo.kt
- DashboardScreen
- ProfileEditorDraft
- ChallengeModeManager.kt
- ProfileValidator
- BstTreeNode
- PracticeSnapshot
- SettingsScreen
- BufferVisualizer
- CellGrid
- RecursionTreeOverlay
- UserPreferencesTest
- AiChatBackendTest.kt
- Step-by-step implementation plan
- OffscreenPointerBanner
- PredictionKind
- ChatMessage
- OnboardingScreen
- StateDeckPage
- Theme.kt
- BufferOp
- Relevant existing implementation
- InstrumentDeck
- UserPreferences
- GraphTelemetryMode
- InputOrder
- VisualizerState.kt
- CatalogueSortMode
- DataScale
- GraphGoal
- setvalue
- VisualizerScreen.kt
- .resolve
- WidgetType
- ExampleUnitTest
- 🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS
- RootStage
- FlowchartShape
- TableAlignment
- ArrayViewMode
- BarVisualizer
- DeckPage
- CodeListing
- VisualizerHeaderMenuTest.kt
- AlgoLensTheme
- CompactIconButton
- rememberVisualizerScreenState
- AuthRepository
- AuthRepository
- Color
- Color
- Dp
- LazyListState
- TextUnit
- VisualizerStep
- GraphCapabilityProfile
- GraphEdgeState
- GraphNodeState
- GraphTool
- GraphEdgeState
- GraphNodeState
- Offset
- ElementState
- FeatureEnhancementsTest
- ChatHistorySidebar
- ChatSessionState.kt
- AlgorithmTheorySheet
- SortOrder
- com
- Type.kt
- StageLegend
- PracticeDifficulty
- ProjectPlannerSheet
- VisualizerScreenState
- VisualizerStep
- AlgorithmSpec
- VisualizerScreenState
- VisualizerStep
- AlgorithmSpec
- VisualizerScreenState
- RecursionBands
- .initialGraphSheetValues
- com
- start
- VisualizerScreenState
- VisualizerStep
- authaccountstate
- AuthRepository
- BootController
- bootcontrollereffect
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
- target
- State
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
- Algorithm
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
- AlgorithmId
- androidx
- SortOrder
- TraceLanguage
- tune
- unavailablefirebaseauthrepository
- UserProfile
- ArrayViewMode
- VisualizerStep

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 86 edges
2. `VisualizerStep` - 77 edges
3. `AlgorithmId` - 58 edges
4. `AlgoTokens` - 56 edges
5. `AlgoLensTheme()` - 54 edges
6. `AlgoType` - 41 edges
7. `ChatSessionManager` - 41 edges
8. `VisualizerScreenStateTest` - 41 edges
9. `AlgoGlyphs` - 36 edges
10. `GraphCanvasEngine()` - 34 edges

## Surprising Connections (you probably didn't know these)
- `2.2 Heap & BST (Tree Structures)` --references--> `VisualizerStep`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`)` --references--> `VisualizerStep`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `3.3 Work Package C: Transport & Scrubber Coordination` --references--> `PlaybackRail()`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/PlaybackRail.kt
- `4. Architectural Invariants & Constraints` --references--> `AlgoTokens`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/theme/Theme.kt
- `5. Affected Files & Subsystem Map` --references--> `AlgoTokens`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/theme/Theme.kt

## Import Cycles
- None detected.

## Communities (205 total, 107 thin omitted)

### Community 0 - "BufferVisualizer.kt"
Cohesion: 0.08
Nodes (171): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, algohairline, algolenstheme (+163 more)

### Community 1 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 2 - "VisualizerScreenState"
Cohesion: 0.06
Nodes (17): AlgorithmId, androidx, GraphEditorSnapshot, ArrayViewMode, BufferOp, GraphEdgeState, GraphNodeState, GraphTool (+9 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.13
Nodes (22): animationspec, animatedSlotSwap(), findSwappedPairs(), FlightSpec, Dp, Modifier, State, nodePop() (+14 more)

### Community 4 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 5 - "PracticeSessionManager"
Cohesion: 0.09
Nodes (13): PracticeQuestionRepository, PracticeOption, PracticeQuestion, PracticeGraphEdge, PracticeGraphNode, PracticeSessionManager, rememberPracticeSessionManager(), PracticeBackendTest (+5 more)

### Community 6 - "FeatureEnhancementsTest.kt"
Cohesion: 0.17
Nodes (17): abs, Algorithm, Algorithm, SampleData, WeightBadgeOverlay, Algorithm, assertequals, assertfalse (+9 more)

### Community 9 - "GraphCanvasEngine"
Cohesion: 0.09
Nodes (20): GraphTreeMutations, ElementState, GraphEdgeState, GraphNodeState, Offset, GraphCanvasEngine(), GraphCapabilityProfile, GraphEdgeState (+12 more)

### Community 10 - "VisualizerStep"
Cohesion: 0.17
Nodes (8): buildPredictionQuestion(), graphVisitQuestion(), highlightedCellPair(), predictionAnswerFor(), quickSortQuestion(), BufferItem, VisualizerStep, ChallengeModeManagerTest

### Community 11 - "ProfileFlowTest.kt"
Cohesion: 0.08
Nodes (23): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark, Bitmap (+15 more)

### Community 12 - "GraphBuilderGestures.kt"
Cohesion: 0.09
Nodes (25): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+17 more)

### Community 13 - "ChatScreen.kt"
Cohesion: 0.10
Nodes (33): alertdialog, annotatedstring, AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem() (+25 more)

### Community 14 - "GraphEdgeState"
Cohesion: 0.13
Nodes (7): GraphSearch, Result, GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphSearchTest

### Community 15 - "ChatSessionManager"
Cohesion: 0.11
Nodes (4): ChatAction, LaunchVisualizer, QueryFollowUp, ChatSessionManager

### Community 16 - "BootController"
Cohesion: 0.13
Nodes (13): MainActivity, BootController, BootControllerEffect(), BootControllerTest, Bundle, ComponentActivity, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 3. Minimalist boot splash (+5 more)

### Community 17 - ".generateStepsForAlgorithm"
Cohesion: 0.11
Nodes (15): AlgorithmStepRepository, Algorithm, AlgorithmId, BufferOp, GraphEdgeState, GraphNodeState, Offset, QueueOp (+7 more)

### Community 18 - "Instrument.kt"
Cohesion: 0.17
Nodes (19): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+11 more)

### Community 19 - "TraceLanguage"
Cohesion: 0.26
Nodes (7): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 20 - "AlgorithmSpec"
Cohesion: 0.27
Nodes (12): AlgorithmSpec, CompactDropdownMenuItem(), CompactHeaderPill(), HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm (+4 more)

### Community 21 - "AlgoLensApp.kt"
Cohesion: 0.12
Nodes (23): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, BootOverlay(), Modifier (+15 more)

### Community 22 - "AccountTemplateCard.kt"
Cohesion: 0.11
Nodes (24): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), Dp, Modifier, Shape, ProfileAvatar() (+16 more)

### Community 23 - "GraphTreeVisualizer"
Cohesion: 0.12
Nodes (14): GraphCapabilityProfile, GraphFloatingToolbar(), Modifier, GraphFrontierTelemetryStrip(), GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), GraphCapabilityProfile (+6 more)

### Community 24 - "AppSettings"
Cohesion: 0.29
Nodes (3): AppSettings, Context, SharedPreferences

### Community 25 - "ChatHistoryRepository.kt"
Cohesion: 0.17
Nodes (10): ChatResponseProvider, OfflineCatalogChatProvider, ChatStatus, COMPLETE, ERROR, SENT, THINKING, Context (+2 more)

### Community 26 - "ChatMessage.kt"
Cohesion: 0.24
Nodes (8): ProjectAlgorithmRecommender, ChatSender, ASSISTANT, SYSTEM, USER, ProjectConstraintBrief, ProjectRecommendationPayload, RecommendedAlgorithmCandidate

### Community 27 - "GraphTreeMotionEngine.kt"
Cohesion: 0.12
Nodes (14): animatable, animationvector1d, computeTransverseArcPosition(), GraphMotionState, GraphNodeState, Offset, VisualizerStep, NodeRipple (+6 more)

### Community 28 - "UnifiedGraphEditorTest"
Cohesion: 0.12
Nodes (10): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Modifier, Offset, GraphNodeState, UnifiedGraphEditorTest (+2 more)

### Community 29 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 30 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 31 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 32 - "AlgoCard"
Cohesion: 0.20
Nodes (7): Algorithm, AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, immutable, toargb

### Community 33 - "ChatHistoryRepository"
Cohesion: 0.33
Nodes (3): ChatHistoryRepository, ChatConversation, JSONObject

### Community 34 - "VisualizerHost.kt"
Cohesion: 0.20
Nodes (18): AlgorithmSpec, CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State, Modifier, PhaseBanner() (+10 more)

### Community 35 - "ProfileScreen.kt"
Cohesion: 0.22
Nodes (12): ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, bitmapfactory, cliptobounds (+4 more)

### Community 36 - "GlassSurface"
Cohesion: 0.29
Nodes (7): AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape

### Community 37 - "CustomizeGraphSheet"
Cohesion: 0.25
Nodes (9): CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField(), StartNodeDropdown(), ValuesField() (+1 more)

### Community 38 - "AlgorithmId"
Cohesion: 0.09
Nodes (23): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA (+15 more)

### Community 39 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 40 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.21
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 41 - "PredictionQuestion"
Cohesion: 0.24
Nodes (15): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), heapQuestion(), Indices, insertionSortQuestion(), mergeSortQuestion(), NodeId (+7 more)

### Community 42 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 43 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.28
Nodes (11): Detector, Issue, IssueRegistry, AlgoLensIssueRegistry, HardcodedHexColorDetector, RawDpSpacingDetector, RoundedCornerShapeLiteralDetector, VisualizerScreenMutationDetector (+3 more)

### Community 44 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 45 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 46 - "ActivePairPointerBracket"
Cohesion: 0.83
Nodes (4): ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, SlotTransform

### Community 48 - "FakeAuthRepository"
Cohesion: 0.30
Nodes (6): AuthAccountState, Error, FakeAuthRepository, Loading, SignedIn, SignedOut

### Community 49 - "RegionAuxiliary"
Cohesion: 0.14
Nodes (9): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+1 more)

### Community 50 - "AviaLogo.kt"
Cohesion: 0.36
Nodes (7): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource

### Community 51 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 52 - "ProfileEditorDraft"
Cohesion: 0.29
Nodes (3): UserProfile, ProfileEditorDraft, ProfileEditorDraftTest

### Community 53 - "ChallengeModeManager.kt"
Cohesion: 0.18
Nodes (16): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+8 more)

### Community 56 - "PracticeSnapshot"
Cohesion: 0.21
Nodes (12): BufferSnapshot, GraphSnapshot, LinearSnapshot, PracticeSnapshot, Modifier, PracticeScreen(), BufferSnapshotView(), GraphSnapshotView() (+4 more)

### Community 57 - "SettingsScreen"
Cohesion: 0.20
Nodes (14): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+6 more)

### Community 58 - "BufferVisualizer"
Cohesion: 0.18
Nodes (15): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), BufferOp, Modifier, QueueOp, VisualizerStep (+7 more)

### Community 59 - "CellGrid"
Cohesion: 0.10
Nodes (24): animatefloat, drawCellGlow(), Color, CellGrid(), CellItem(), ElementState, Modifier, State (+16 more)

### Community 60 - "RecursionTreeOverlay"
Cohesion: 0.23
Nodes (6): GraphIntegerEntryDialog(), Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 62 - "AiChatBackendTest.kt"
Cohesion: 0.13
Nodes (11): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), experimentalcoroutinesapi, flowchartshape, parseflowchart (+3 more)

### Community 63 - "Step-by-step implementation plan"
Cohesion: 0.11
Nodes (18): 1. Introduce validated capability profiles, 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document, Automated coverage (+10 more)

### Community 64 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 65 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 66 - "ChatMessage"
Cohesion: 0.16
Nodes (7): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, AlgorithmTheoryData, AlgorithmTheoryRepository

### Community 67 - "OnboardingScreen"
Cohesion: 0.28
Nodes (9): FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor(), OnboardingSlidePractice() (+1 more)

### Community 68 - "StateDeckPage"
Cohesion: 0.25
Nodes (9): ComplexityStatePill(), androidx, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage(), StateDeckPagePreview(), VarBadge() (+1 more)

### Community 69 - "Theme.kt"
Cohesion: 0.12
Nodes (16): activity, animatecontentsize, Color, Dp, Modifier, PaddingValues, smoothPanelExpansion(), Spacing (+8 more)

### Community 70 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 71 - "Relevant existing implementation"
Cohesion: 0.22
Nodes (8): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Relevant existing implementation

### Community 72 - "InstrumentDeck"
Cohesion: 0.18
Nodes (11): InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx, Modifier, State, VisualizerStep, Modifier (+3 more)

### Community 73 - "UserPreferences"
Cohesion: 0.60
Nodes (3): android, Context, UserPreferences

### Community 74 - "GraphTelemetryMode"
Cohesion: 0.29
Nodes (7): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE

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

### Community 79 - "GraphGoal"
Cohesion: 0.40
Nodes (5): GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE, SHORTEST_UNWEIGHTED_PATH, WEIGHTED_SHORTEST_PATH

### Community 80 - "setvalue"
Cohesion: 0.24
Nodes (11): AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, delay, launchedeffect, mutablefloatstateof, mutableintstateof, mutablelongstateof (+3 more)

### Community 81 - "VisualizerScreen.kt"
Cohesion: 0.17
Nodes (15): algoworkspacebackground, animatedpasstate, EmptyCanvas(), Algorithm, Modifier, VisualizerScreen(), VisualizerScreenPreview(), Modifier (+7 more)

### Community 82 - ".resolve"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 83 - "WidgetType"
Cohesion: 0.40
Nodes (4): WidgetType, STATE, TELEMETRY, TRACE

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

### Community 89 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "BarVisualizer"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

### Community 91 - "DeckPage"
Cohesion: 0.50
Nodes (3): DeckPage, STATE, TRACE

### Community 92 - "CodeListing"
Cohesion: 0.17
Nodes (12): CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color, LazyListState, Modifier, CodeTracePane() (+4 more)

### Community 93 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 94 - "AlgoLensTheme"
Cohesion: 0.29
Nodes (5): ProfileFlowTest, AlgoLensTheme(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier

### Community 95 - "CompactIconButton"
Cohesion: 0.24
Nodes (11): CompactIconButton(), IconPillButton(), androidx, Color, ImageVector, Modifier, RailIconButton(), SegmentedToggle() (+3 more)

### Community 113 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 115 - "ChatHistorySidebar"
Cohesion: 0.25
Nodes (8): CandidateCard(), ChatHistorySidebar(), ComplexityPill(), androidx, ImageVector, Modifier, ProjectRecommendationBlock(), SidebarOptionItem()

### Community 116 - "ChatSessionState.kt"
Cohesion: 0.32
Nodes (7): Context, rememberChatSessionManager(), CoroutineScope, job, launch, localcontext, remembercoroutinescope

### Community 117 - "AlgorithmTheorySheet"
Cohesion: 0.33
Nodes (7): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Algorithm, Color, Modifier, PropertyBadge()

### Community 118 - "SortOrder"
Cohesion: 0.33
Nodes (5): SortOrder, ASC, DESC, CustomizeInputSheet(), CustomizeInputSheetPreview()

### Community 120 - "Type.kt"
Cohesion: 0.33
Nodes (5): TextUnit, font, r, TextStyle, typography

### Community 121 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 122 - "PracticeDifficulty"
Cohesion: 0.50
Nodes (4): PracticeDifficulty, EASY, HARD, MEDIUM

### Community 123 - "ProjectPlannerSheet"
Cohesion: 0.50
Nodes (4): PlannerChipGroup(), ProjectPlannerSheet(), ToggleConstraintChip(), T

## Knowledge Gaps
- **182 isolated node(s):** `TRACE`, `STATE`, `TELEMETRY`, `Objective`, `2.1 Stack & Queue (Buffer Family)` (+177 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 496 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **107 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep` to `BufferVisualizer.kt`, `VisualizerScreenState`, `RecursionBands`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `GraphEdgeState`, `AlgorithmSpec`, `UnifiedGraphEditorTest`, `VisualizerHost.kt`, `PredictionQuestion`, `ActivePairPointerBracket`, `.specFor`, `RegionAuxiliary`, `ChallengeModeManager.kt`, `RecursionTreeOverlay`, `AiChatBackendTest.kt`, `Step-by-step implementation plan`, `OffscreenPointerBanner`, `StateDeckPage`, `VisualizerState.kt`, `🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS`, `BarVisualizer`, `CodeListing`, `FeatureEnhancementsTest`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `BufferVisualizer.kt`, `.initialGraphSheetValues`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `GraphCanvasEngine`, `.generateStepsForAlgorithm`, `AlgorithmSpec`, `GraphTreeMotionEngine.kt`, `UnifiedGraphEditorTest`, `VisualizerHost.kt`, `.specFor`, `RegionAuxiliary`, `BstTreeNode`, `RecursionTreeOverlay`, `OffscreenPointerBanner`, `InstrumentDeck`, `setvalue`, `WidgetType`, `CompactIconButton`, `rememberVisualizerScreenState`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `BufferVisualizer.kt`, `ChatMessage`, `PracticeSessionManager`, `FeatureEnhancementsTest.kt`, `CustomizeGraphSheet`, `VisualizerStep`, `GraphEdgeState`, `.specFor`, `TraceLanguage`, `ChatSessionState.kt`, `ChallengeModeManager.kt`, `ChatHistoryRepository.kt`, `ChatMessage.kt`, `UnifiedGraphEditorTest`, `AiChatBackendTest.kt`?**
  _High betweenness centrality (0.056) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `VisualizerStep` (e.g. with `2.2 Heap & BST (Tree Structures)` and `3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`)`) actually correct?**
  _`VisualizerStep` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TRACE`, `STATE`, `TELEMETRY` to the rest of the system?**
  _182 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BufferVisualizer.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.08079564114046872 - nodes in this community are weakly interconnected._
- **Should `ChatMarkdownMessage` be split into smaller, more focused modules?**
  _Cohesion score 0.09971509971509972 - nodes in this community are weakly interconnected._