# Graph Report - AlgoLens  (2026-09-29)

## Corpus Check
- 150 files · ~219,099 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 2017 nodes · 6559 edges · 194 communities (89 shown, 105 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 134 edges (avg confidence: 0.87)
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
- BufferOpEditor
- PracticeSessionManager
- FeatureEnhancementsTest.kt
- .processQuery
- VisualizerScreenStateTest
- GraphCanvasEngine
- VisualizerStep
- ProfileFlowTest.kt
- GraphBuilderGestures.kt
- ChatScreen
- .buildAdjacency
- ChatSessionManager
- BootController
- .generateStepsForAlgorithm
- Instrument.kt
- TraceLanguage
- CompactIconButton
- AlgoLensApp.kt
- AccountTemplateCard.kt
- AlgorithmRegistry
- AlgorithmStepRepository.kt
- ChatSessionState.kt
- ChatHistoryRepository.kt
- GraphTreeMotionEngine.kt
- UnifiedGraphEditorTest
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- UElementHandler
- UElementHandler
- AlgoCard
- ChatHistoryRepository
- VisualizerHost.kt
- QueueOp
- UnifiedMotionSystemTest
- CustomizeGraphSheet
- GraphTreeVisualizer
- AuthRepository
- UnavailableFirebaseAuthRepository
- PredictionQuestion
- StepToken
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgoLensDetectorsTest
- CircularQueueVisualizer
- .specFor
- FakeAuthRepository
- RegionAuxiliary
- AviaLogo.kt
- DashboardScreen
- ProfileEditorDraft
- ChallengeModeManagerTest.kt
- ExploreCatalogScreen
- BstTreeNode
- SlideDirection
- SettingsScreen
- BufferVisualizer
- GraphTreeRenderer.kt
- RecursionTreeOverlay
- AppSettings.kt
- AiChatBackendTest.kt
- Step-by-step implementation plan
- OffscreenPointerBanner
- PredictionKind
- AlgorithmId
- DoubleBezelShell
- StateDeckPage
- Theme.kt
- BufferOp
- AlgorithmId.kt
- InstrumentDeck
- TraceStrip
- TelemetryDeckPage
- InputOrder
- VisualizerState.kt
- CatalogueSortMode
- DataScale
- BufferOp
- QueueOp
- VisualizerScreen
- .resolve
- BufferOp
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
- QueueOp
- BufferOp
- AuthRepository
- AuthRepository
- Color
- QueueOp
- BufferOp
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
- GraphTool
- QueueOp
- AlgorithmTheorySheet
- SortOrder
- com
- StageLegend
- VisualizerScreenState
- VisualizerStep
- AlgorithmSpec
- VisualizerScreenState
- VisualizerStep
- AlgorithmSpec
- VisualizerScreenState
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
- offlinecatalogchatprovider
- parsemarkdowninline
- person
- projectalgorithmrecommender
- QueueOp
- railiconbutton
- rotate
- sectionlabel
- segmentedtoggle
- Shape
- smoothpanelexpansion
- androidx
- TraceLanguage
- tune
- unavailablefirebaseauthrepository
- UserProfile
- ArrayViewMode

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 93 edges
2. `VisualizerStep` - 73 edges
3. `AlgorithmId` - 55 edges
4. `AlgoTokens` - 53 edges
5. `AlgoLensTheme()` - 50 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `ChatSessionManager` - 41 edges
8. `AlgoType` - 40 edges
9. `TraceLanguage` - 37 edges
10. `AlgoGlyphs` - 34 edges

## Surprising Connections (you probably didn't know these)
- `4. Architectural Invariants & Constraints` --references--> `AlgoTokens`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/theme/Theme.kt
- `5. Affected Files & Subsystem Map` --references--> `AlgoTokens`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/theme/Theme.kt
- `2.2 Heap & BST (Tree Structures)` --references--> `VisualizerStep`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`)` --references--> `VisualizerStep`  [INFERRED]
  docs/ANIMATION_HANDOVER_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt

## Import Cycles
- None detected.

## Communities (194 total, 105 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.08
Nodes (192): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algohairline (+184 more)

### Community 1 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 2 - "VisualizerScreenState"
Cohesion: 0.05
Nodes (25): androidx, Modifier, PlaybackRail(), PlaybackRailPreview(), GraphEditorSnapshot, Algorithm, AlgorithmId, ArrayViewMode (+17 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.16
Nodes (18): abs, animationspec, animatedSlotSwap(), findSwappedPairs(), FlightSpec, Dp, Modifier, State (+10 more)

### Community 4 - "BufferOpEditor"
Cohesion: 0.39
Nodes (8): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), Color, OpRow(), OpRowData

### Community 5 - "PracticeSessionManager"
Cohesion: 0.05
Nodes (30): android, PracticeQuestionRepository, Context, UserPreferences, PracticeDifficulty, EASY, HARD, MEDIUM (+22 more)

### Community 6 - "FeatureEnhancementsTest.kt"
Cohesion: 0.17
Nodes (19): Algorithm, AlgorithmId, Algorithm, SampleData, GraphEdgeState, assertequals, assertfalse, assertnotnull (+11 more)

### Community 8 - "VisualizerScreenStateTest"
Cohesion: 0.07
Nodes (13): MainActivity, Algorithm, VisualizerScreenStateTest, Bundle, ComponentActivity, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 3. Minimalist boot splash, 4. Tests (+5 more)

### Community 9 - "GraphCanvasEngine"
Cohesion: 0.09
Nodes (20): GraphTreeMutations, ElementState, GraphEdgeState, GraphNodeState, Offset, GraphCanvasEngine(), GraphCapabilityProfile, GraphEdgeState (+12 more)

### Community 10 - "VisualizerStep"
Cohesion: 0.20
Nodes (8): buildPredictionQuestion(), highlightedCellPair(), predictionAnswerFor(), swapOrComparePairQuestion(), swapQuestion(), BufferItem, VisualizerStep, ChallengeModeManagerTest

### Community 11 - "ProfileFlowTest.kt"
Cohesion: 0.08
Nodes (23): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark, Bitmap (+15 more)

### Community 12 - "GraphBuilderGestures.kt"
Cohesion: 0.08
Nodes (27): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+19 more)

### Community 13 - "ChatScreen"
Cohesion: 0.11
Nodes (26): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+18 more)

### Community 14 - ".buildAdjacency"
Cohesion: 0.16
Nodes (4): GraphSearch, Result, GraphSearchTest, 4. Integrate generators and correct preview semantics

### Community 16 - "BootController"
Cohesion: 0.29
Nodes (3): BootController, BootControllerEffect(), BootControllerTest

### Community 17 - ".generateStepsForAlgorithm"
Cohesion: 0.13
Nodes (8): AlgorithmStepRepository, GraphEdgeState, GraphNodeState, Offset, SortOrder, VisualizerStep, AlgorithmStepRepositoryTest, DijkstraStepsTest

### Community 18 - "Instrument.kt"
Cohesion: 0.21
Nodes (15): AlgoHairline(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color, Dp (+7 more)

### Community 19 - "TraceLanguage"
Cohesion: 0.22
Nodes (8): AlgorithmCodeRegistry, AlgorithmId, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 20 - "CompactIconButton"
Cohesion: 0.11
Nodes (24): SortOrder, ASC, DESC, CompactIconButton(), IconPillButton(), androidx, Color, ImageVector (+16 more)

### Community 21 - "AlgoLensApp.kt"
Cohesion: 0.13
Nodes (22): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, BootOverlay(), Modifier (+14 more)

### Community 22 - "AccountTemplateCard.kt"
Cohesion: 0.08
Nodes (36): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), Dp, Modifier, Shape, ProfileAvatar() (+28 more)

### Community 23 - "AlgorithmRegistry"
Cohesion: 0.12
Nodes (10): AlgorithmRegistry, AlgorithmSpec, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D, GraphCapabilityProfile, GraphFloatingToolbar() (+2 more)

### Community 24 - "AlgorithmStepRepository.kt"
Cohesion: 0.12
Nodes (13): Algorithm, AlgorithmId, BstMode, IN_ORDER, POST_ORDER, PRE_ORDER, SEARCH, QueueVariant (+5 more)

### Community 25 - "ChatSessionState.kt"
Cohesion: 0.15
Nodes (13): ChatResponseProvider, OfflineCatalogChatProvider, ChatStatus, COMPLETE, ERROR, SENT, THINKING, Context (+5 more)

### Community 26 - "ChatHistoryRepository.kt"
Cohesion: 0.12
Nodes (19): ProjectAlgorithmRecommender, ChatAction, ChatSender, ASSISTANT, SYSTEM, USER, GraphGoal, EXHAUSTIVE_OR_CYCLE (+11 more)

### Community 27 - "GraphTreeMotionEngine.kt"
Cohesion: 0.16
Nodes (13): animatable, animationvector1d, GraphMotionState, GraphNodeState, Offset, VisualizerStep, NodeRipple, rememberGraphMotionState() (+5 more)

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
Cohesion: 0.22
Nodes (6): Algorithm, AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, immutable

### Community 33 - "ChatHistoryRepository"
Cohesion: 0.33
Nodes (3): ChatHistoryRepository, ChatConversation, JSONObject

### Community 34 - "VisualizerHost.kt"
Cohesion: 0.35
Nodes (12): AlgorithmSpec, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas(), ArrayViewMode, Modifier, State (+4 more)

### Community 35 - "QueueOp"
Cohesion: 0.15
Nodes (4): Dequeue, Enqueue, QueueOp, TreeTraversalAndCircularQueueTest

### Community 37 - "CustomizeGraphSheet"
Cohesion: 0.25
Nodes (9): CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField(), StartNodeDropdown(), ValuesField() (+1 more)

### Community 38 - "GraphTreeVisualizer"
Cohesion: 0.27
Nodes (11): GraphFrontierTelemetryStrip(), GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), GraphCapabilityProfile, GraphEdgeState, GraphNodeState, Modifier (+3 more)

### Community 39 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 40 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.27
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 41 - "PredictionQuestion"
Cohesion: 0.14
Nodes (18): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), mergeSortQuestion() (+10 more)

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

### Community 46 - "CircularQueueVisualizer"
Cohesion: 0.25
Nodes (9): CircularPointerBadge(), CircularQueueVisualizer(), CircularSlotItem(), getPointerBadgeInfo(), Color, Dp, Modifier, VisualizerStep (+1 more)

### Community 48 - "FakeAuthRepository"
Cohesion: 0.18
Nodes (10): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, FakeAuthRepository, Loading, SignedIn (+2 more)

### Community 49 - "RegionAuxiliary"
Cohesion: 0.11
Nodes (11): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+3 more)

### Community 50 - "AviaLogo.kt"
Cohesion: 0.18
Nodes (12): AviaLogo(), Color, Dp, Modifier, TextUnit, colorfilter, font, image (+4 more)

### Community 51 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 52 - "ProfileEditorDraft"
Cohesion: 0.19
Nodes (3): ProfileValidator, ProfileEditorDraft, ProfileEditorDraftTest

### Community 53 - "ChallengeModeManagerTest.kt"
Cohesion: 0.15
Nodes (12): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+4 more)

### Community 54 - "ExploreCatalogScreen"
Cohesion: 0.50
Nodes (5): ExploreCatalogScreen(), ExploreCatalogScreenPreview(), Modifier, PracticeTrack, TrackCatalogCard()

### Community 56 - "SlideDirection"
Cohesion: 0.40
Nodes (5): SlideDirection, Bottom, Left, Right, Top

### Community 57 - "SettingsScreen"
Cohesion: 0.20
Nodes (14): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+6 more)

### Community 58 - "BufferVisualizer"
Cohesion: 0.17
Nodes (15): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), Color, ElementState, Modifier, VisualizerStep (+7 more)

### Community 59 - "GraphTreeRenderer.kt"
Cohesion: 0.05
Nodes (59): animatefloat, animatefloatasstate, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier (+51 more)

### Community 60 - "RecursionTreeOverlay"
Cohesion: 0.23
Nodes (6): GraphIntegerEntryDialog(), Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 61 - "AppSettings.kt"
Cohesion: 0.27
Nodes (3): Context, UserPreferencesTest, SharedPreferences

### Community 62 - "AiChatBackendTest.kt"
Cohesion: 0.15
Nodes (9): AnnotatedString, SyntaxHighlighter, delay, experimentalcoroutinesapi, flowchartshape, parseflowchart, parsemarkdowntable, runblocking (+1 more)

### Community 63 - "Step-by-step implementation plan"
Cohesion: 0.12
Nodes (16): 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document, Automated coverage, Compose/device checks, Design approach (+8 more)

### Community 64 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 65 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 66 - "AlgorithmId"
Cohesion: 0.09
Nodes (21): AiChatEngine, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA (+13 more)

### Community 67 - "DoubleBezelShell"
Cohesion: 0.23
Nodes (12): DoubleBezelShell(), PaddingValues, Shape, FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar() (+4 more)

### Community 68 - "StateDeckPage"
Cohesion: 0.25
Nodes (9): ComplexityStatePill(), androidx, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage(), StateDeckPagePreview(), VarBadge() (+1 more)

### Community 69 - "Theme.kt"
Cohesion: 0.13
Nodes (15): activity, animatecontentsize, Color, Dp, Modifier, PaddingValues, smoothPanelExpansion(), Spacing (+7 more)

### Community 70 - "BufferOp"
Cohesion: 0.33
Nodes (4): BufferOp, Peek, Pop, Push

### Community 71 - "AlgorithmId.kt"
Cohesion: 0.08
Nodes (21): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE, InputKind (+13 more)

### Community 72 - "InstrumentDeck"
Cohesion: 0.25
Nodes (8): InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx, Modifier, State, VisualizerStep, WidgetManagerModal()

### Community 73 - "TraceStrip"
Cohesion: 0.50
Nodes (4): Modifier, VisualizerStep, TraceStrip(), TraceStripPreview()

### Community 74 - "TelemetryDeckPage"
Cohesion: 0.67
Nodes (3): Modifier, VisualizerStep, TelemetryDeckPage()

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

### Community 81 - "VisualizerScreen"
Cohesion: 0.18
Nodes (13): BstModeSelector(), Modifier, QueueModeSelector(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, EmptyCanvas(), Algorithm (+5 more)

### Community 82 - ".resolve"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

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
Cohesion: 0.15
Nodes (13): CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color, LazyListState, Modifier, CodeTracePane() (+5 more)

### Community 93 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 94 - "AlgoLensTheme"
Cohesion: 0.29
Nodes (5): ProfileFlowTest, AlgoLensTheme(), GraphVisualizer(), GraphVisualizerPreview(), Modifier

### Community 113 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 117 - "AlgorithmTheorySheet"
Cohesion: 0.33
Nodes (7): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Algorithm, Color, Modifier, PropertyBadge()

### Community 121 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

## Knowledge Gaps
- **187 isolated node(s):** `KOTLIN`, `JAVA`, `PYTHON`, `CPP`, `Push` (+182 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 512 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **105 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `FeatureEnhancementsTest.kt`, `VisualizerScreenStateTest`, `GraphCanvasEngine`, `CompactIconButton`, `AlgorithmStepRepository.kt`, `UnifiedGraphEditorTest`, `VisualizerHost.kt`, `QueueOp`, `UnifiedMotionSystemTest`, `.specFor`, `RegionAuxiliary`, `BstTreeNode`, `BufferVisualizer`, `RecursionTreeOverlay`, `OffscreenPointerBanner`, `BufferOp`, `AlgorithmId.kt`, `InstrumentDeck`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `CodeListing` to `ChatScreen.kt`, `.resolve`, `TraceLanguage`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `FeatureEnhancementsTest.kt`, `CompactIconButton`, `UnifiedGraphEditorTest`, `VisualizerHost.kt`, `PredictionQuestion`, `.specFor`, `RegionAuxiliary`, `ChallengeModeManagerTest.kt`, `GraphTreeRenderer.kt`, `RecursionTreeOverlay`, `Step-by-step implementation plan`, `OffscreenPointerBanner`, `StateDeckPage`, `AlgorithmId.kt`, `VisualizerState.kt`, `🏛️ MISSION PROMPT: UNIFIED ANIMATION & MOTION SYSTEM FOR BUFFER & GRAPH VISUALIZERS`, `BarVisualizer`, `AlgoLensTheme`, `FeatureEnhancementsTest`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `VisualizerStep` (e.g. with `2.2 Heap & BST (Tree Structures)` and `3.2 Work Package B: Graph & Tree Motion Engine (`GraphTreeRenderer.kt`)`) actually correct?**
  _`VisualizerStep` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `KOTLIN`, `JAVA`, `PYTHON` to the rest of the system?**
  _187 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.07500544645358065 - nodes in this community are weakly interconnected._
- **Should `ChatMarkdownMessage` be split into smaller, more focused modules?**
  _Cohesion score 0.09971509971509972 - nodes in this community are weakly interconnected._