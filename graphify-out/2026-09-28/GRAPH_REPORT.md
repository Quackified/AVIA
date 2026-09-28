# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 134 files · ~193,096 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1810 nodes · 5738 edges · 148 communities (98 shown, 50 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 192 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e6f66c1d`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- VisualizerStep
- PracticeSessionManager
- SwapFlight.kt
- .generateStepsForAlgorithm
- TraceLanguage
- OffscreenPointerBanner
- VisualizerScreenStateTest
- ChatMarkdownMessage
- Instrument.kt
- ProfileFlowTest.kt
- NavTab
- PredictionQuestion
- ChatSessionManager
- GraphTreeRenderer.kt
- FeatureEnhancementsTest.kt
- UElementHandler
- UElementHandler
- AlgorithmId
- Step-by-step implementation plan
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- InputOrder
- StepToken
- VisualizerScreenState
- ChatScreen
- ChatHistoryRepository.kt
- VisualizerState.kt
- BufferVisualizer
- .processQuery
- BootController
- VisualizerScreen.kt
- FeatureEnhancementsTest
- AuthRepository
- GraphTool
- setvalue
- GlassPanel.kt
- AviaLogo.kt
- AlgoLensTheme
- RailIconButton
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgorithmId
- CellGrid.kt
- StateDeckPage
- AlgoLensDetectorsTest
- ChatConversation
- RecursionTreeOverlay
- ChallengeModeManagerTest.kt
- UserPreferencesTest
- ProfileScreen
- FakeAuthRepository
- .highlight
- Offset
- VisualizerHeader
- RegionAuxiliary
- BufferOpEditor
- Relevant existing implementation
- OnboardingScreen
- CatalogueSortMode
- EditProfileSheet
- Dp
- .resolve
- AlgoLensApp.kt
- InstrumentDeck
- 6. Implementation Steps & Validation Checklist
- SectionLabel
- VisualizerHost.kt
- StageLegend
- ProfileEditorDraft
- FlowchartShape
- TableAlignment
- .setOnboardingCompleted
- ChatSessionState.kt
- Profile and Edit Profile
- 1. Algorithmic Families & Variants
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
- .Content
- PracticeQuestionRepository
- PracticeSessionState.kt
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
- rememberVisualizerScreenState
- VisualizerBenchmarks.kt
- PracticeSnapshot
- GraphTreeVisualizer
- BufferOp
- AiChatBackendTest.kt
- ProfileFlowTest
- RecursionBands.kt
- VisualizerHeaderMenuTest.kt
- DataScale
- GraphGoal
- PhaseStrip
- CellArrayVisualizer
- RootStage
- BarVisualizer
- AuthProviderType
- SampleData
- AuthRepository
- AlgorithmSpec
- State
- VisualizerScreenState
- ArrayViewMode
- BufferOp
- ChallengeState
- chataction
- ChatCodeSnippet
- ChatComplexitySnapshot
- ChatMessage
- ChatPromptStarter
- chatsender
- ChatSessionManager
- com
- projectalgorithmrecommender
- QueueOp
- SortOrder

## God Nodes (most connected - your core abstractions)
1. `AlgorithmId` - 62 edges
2. `VisualizerScreenState` - 55 edges
3. `VisualizerStep` - 45 edges
4. `AlgoLensTheme()` - 44 edges
5. `ChatSessionManager` - 41 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `AlgoTokens` - 39 edges
8. `buildPredictionQuestion()` - 34 edges
9. `ChallengeModeManagerTest` - 30 edges
10. `TraceLanguage` - 28 edges

## Surprising Connections (you probably didn't know these)
- `Step 1: Capability Profiles & Models` --references--> `AlgorithmSpec`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/model/AlgorithmId.kt
- `2.2 Multi-Step Undo / Redo History Stack` --references--> `GraphFloatingToolbar()`  [INFERRED]
  docs/FUTURE_WORK.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphBuilderOverlay.kt
- `5. Build shared viewport geometry and canvas engine` --references--> `GraphTreeRenderer()`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `4.1 Viewport Pan & Pinch-to-Zoom Engine` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt

## Import Cycles
- None detected.

## Communities (148 total, 50 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.07
Nodes (197): accentgreen, accentorange, accentpink, accentred, accentyellow, activityresultcontracts, alertdialog, algohairline (+189 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.15
Nodes (7): buildPredictionQuestion(), highlightedCellPair(), predictionAnswerFor(), BufferItem, VisualizerStep, ChallengeModeManagerTest, 3.1 Dijkstra Prediction Question Generator

### Community 2 - "PracticeSessionManager"
Cohesion: 0.16
Nodes (3): PracticeSessionManager, PracticeBackendTest, PracticeDifficulty

### Community 3 - "SwapFlight.kt"
Cohesion: 0.07
Nodes (38): abs, activity, animatecontentsize, animationspec, animationvector1d, Dp, Modifier, smoothPanelExpansion() (+30 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.06
Nodes (21): AlgorithmStepRepository, GraphSearch, Algorithm, BufferOp, GraphEdgeState, GraphNodeState, QueueOp, SortOrder (+13 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.14
Nodes (18): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+10 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 7 - "VisualizerScreenStateTest"
Cohesion: 0.10
Nodes (3): Algorithm, VisualizerScreenStateTest, 4. Tests

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.15
Nodes (21): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+13 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.11
Nodes (17): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, Bitmap, compositionlocalprovider, createandroidcomposerule (+9 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "PredictionQuestion"
Cohesion: 0.25
Nodes (21): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), VisualizerStep (+13 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.19
Nodes (10): atan2, cos, graphedgedefault, immutable, nativecanvas, paint, patheffect, sin (+2 more)

### Community 15 - "FeatureEnhancementsTest.kt"
Cohesion: 0.20
Nodes (17): Algorithm, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, authaccountstate, elementstate (+9 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 18 - "AlgorithmId"
Cohesion: 0.09
Nodes (23): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA (+15 more)

### Community 19 - "Step-by-step implementation plan"
Cohesion: 0.11
Nodes (18): 1. Introduce validated capability profiles, 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 5. Build shared viewport geometry and canvas engine, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document, Automated coverage (+10 more)

### Community 20 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, Detector, implementation, Issue, IssueRegistry, AlgoLensIssueRegistry, HardcodedHexColorDetector, RawDpSpacingDetector (+5 more)

### Community 21 - "InputOrder"
Cohesion: 0.29
Nodes (7): InputOrder, ALREADY_SORTED, LIFO_FIFO_STREAM, NEARLY_SORTED, RANDOM_UNSORTED, TREE_OR_GRAPH, UNSPECIFIED

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 23 - "VisualizerScreenState"
Cohesion: 0.08
Nodes (13): AttachedDeckTabs(), androidx, ArrayViewMode, BufferOp, com, GraphEdgeState, GraphNodeState, QueueOp (+5 more)

### Community 24 - "ChatScreen"
Cohesion: 0.11
Nodes (26): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+18 more)

### Community 25 - "ChatHistoryRepository.kt"
Cohesion: 0.14
Nodes (15): ChatResponseProvider, OfflineCatalogChatProvider, ProjectAlgorithmRecommender, ChatAction, ChatSender, ASSISTANT, SYSTEM, USER (+7 more)

### Community 26 - "VisualizerState.kt"
Cohesion: 0.18
Nodes (11): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphNodeState, Pointer, VisualizerRenderMode, BARS (+3 more)

### Community 27 - "BufferVisualizer"
Cohesion: 0.16
Nodes (14): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), BufferOp, Color, Modifier (+6 more)

### Community 28 - ".processQuery"
Cohesion: 0.09
Nodes (7): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, AlgorithmTheoryData, AlgorithmTheoryRepository, AiChatBackendTest

### Community 29 - "BootController"
Cohesion: 0.13
Nodes (13): MainActivity, BootController, BootControllerEffect(), BootControllerTest, Bundle, ComponentActivity, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 3. Minimalist boot splash (+5 more)

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.13
Nodes (19): animatable, animatedpasstate, Algorithm, AiTutorSheet(), AiTutorSheetPreview(), Modifier, PlaybackRail(), PlaybackRailPreview() (+11 more)

### Community 31 - "FeatureEnhancementsTest"
Cohesion: 0.13
Nodes (3): AppSettings, FeatureEnhancementsTest, SharedPreferences

### Community 32 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 33 - "GraphTool"
Cohesion: 0.14
Nodes (18): GraphBuilderBanner(), GraphBuilderGestures(), GraphBuilderToolbar(), GraphFloatingToolbar(), GraphTool, ADD, DELETE, ENDPOINTS (+10 more)

### Community 34 - "setvalue"
Cohesion: 0.35
Nodes (8): Context, delay, launchedeffect, mutablefloatstateof, mutablelongstateof, mutablestateof, setvalue, stable

### Community 35 - "GlassPanel.kt"
Cohesion: 0.19
Nodes (16): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+8 more)

### Community 36 - "AviaLogo.kt"
Cohesion: 0.15
Nodes (14): BootOverlay(), Modifier, AviaLogo(), Color, Dp, Modifier, colorfilter, font (+6 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.19
Nodes (13): AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField(), StartNodeDropdown() (+5 more)

### Community 38 - "RailIconButton"
Cohesion: 0.18
Nodes (11): SortOrder, ASC, DESC, IconPillButton(), androidx, Color, ImageVector, RailIconButton() (+3 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement, Vendor (+5 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 42 - "CellGrid.kt"
Cohesion: 0.18
Nodes (17): accentpinkglow, CellGrid(), CellItem(), Dp, LazyListState, Modifier, State, TextUnit (+9 more)

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
Cohesion: 0.25
Nodes (5): Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 47 - "ChallengeModeManagerTest.kt"
Cohesion: 0.16
Nodes (12): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+4 more)

### Community 49 - "ProfileScreen"
Cohesion: 0.22
Nodes (10): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, ImageVector, Modifier, ProfileOptionItem(), ProfileScreen() (+2 more)

### Community 50 - "FakeAuthRepository"
Cohesion: 0.23
Nodes (7): AuthAccountState, Error, FakeAuthRepository, Loading, SignedIn, SignedOut, UserProfile

### Community 51 - ".highlight"
Cohesion: 0.22
Nodes (6): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), TraceLanguage

### Community 52 - "Offset"
Cohesion: 0.47
Nodes (5): drawCellGlow(), Color, CornerRadius, drawscope, Offset

### Community 53 - "VisualizerHeader"
Cohesion: 0.27
Nodes (14): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm, AlgorithmSpec (+6 more)

### Community 54 - "RegionAuxiliary"
Cohesion: 0.21
Nodes (6): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow

### Community 55 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 56 - "Relevant existing implementation"
Cohesion: 0.13
Nodes (14): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE, Error (+6 more)

### Community 57 - "OnboardingScreen"
Cohesion: 0.28
Nodes (9): FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor(), OnboardingSlidePractice() (+1 more)

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "EditProfileSheet"
Cohesion: 0.14
Nodes (12): ProfileValidator, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileField() (+4 more)

### Community 61 - ".resolve"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 62 - "AlgoLensApp.kt"
Cohesion: 0.15
Nodes (19): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, ExploreCatalogScreen(), ExploreCatalogScreenPreview() (+11 more)

### Community 63 - "InstrumentDeck"
Cohesion: 0.25
Nodes (8): InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx, Modifier, State, VisualizerScreenState, VisualizerStep

### Community 64 - "6. Implementation Steps & Validation Checklist"
Cohesion: 0.12
Nodes (16): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`), 4.3 Interactive Tool Logic & Touch Handling, 4.4 Fullscreen Edge-Aware Canvas Overlay (+8 more)

### Community 65 - "SectionLabel"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 66 - "VisualizerHost.kt"
Cohesion: 0.28
Nodes (13): AlgorithmSpec, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas(), ArrayViewMode, Modifier, VisualizerStep (+5 more)

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "ProfileEditorDraft"
Cohesion: 0.29
Nodes (3): ProfileEditorDraft, ProfileEditorDraftTest, UserProfile

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 75 - "ChatSessionState.kt"
Cohesion: 0.14
Nodes (14): ChatStatus, COMPLETE, ERROR, SENT, THINKING, rememberChatSessionManager(), chathistoryrepository, chatresponseprovider (+6 more)

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 79 - "1. Algorithmic Families & Variants"
Cohesion: 0.15
Nodes (12): ForBst, 1.1 Binary Search Tree (BST) Traversal Family, 1.2 Queue & Buffer Family Variants, 1.3 0-1 BFS (Double-Ended Queue Shortest Path), 1.4 A* Heuristic Search ($f(n) = g(n) + h(n)$), 1. Algorithmic Families & Variants, 2.1 Directed Edge Support & Arrowheads, 2.2 Multi-Step Undo / Redo History Stack (+4 more)

### Community 80 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.27
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 85 - ".from"
Cohesion: 0.26
Nodes (7): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Modifier, Offset, Step 3: Viewport & Renderer Refactoring

### Community 86 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.25
Nodes (10): animatefloatasstate, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, localdensity, path, SlotTransform (+2 more)

### Community 91 - "ElementState"
Cohesion: 0.17
Nodes (12): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+4 more)

### Community 92 - ".Content"
Cohesion: 0.33
Nodes (5): Modifier, VisualizerScreenState, VisualizerStep, WeightBadgeOverlay, VisualizerOverlay

### Community 93 - "PracticeQuestionRepository"
Cohesion: 0.27
Nodes (7): PracticeQuestionRepository, PracticeGraphEdge, PracticeGraphNode, practicegraphedge, practicegraphnode, practiceoption, PracticeQuestion

### Community 94 - "PracticeSessionState.kt"
Cohesion: 0.20
Nodes (8): PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, rememberPracticeSessionManager(), mutableintstateof

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

### Community 113 - "rememberVisualizerScreenState"
Cohesion: 0.20
Nodes (3): Algorithm, rememberVisualizerScreenState(), 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)

### Community 114 - "VisualizerBenchmarks.kt"
Cohesion: 0.20
Nodes (7): FrameTimingBenchmark, StartupBenchmark, compilationmode, frametimingmetric, macrobenchmarkrule, startupmode, startuptimingmetric

### Community 115 - "PracticeSnapshot"
Cohesion: 0.31
Nodes (9): BufferSnapshot, GraphSnapshot, LinearSnapshot, PracticeSnapshot, BufferSnapshotView(), GraphSnapshotView(), Modifier, LinearSnapshotView() (+1 more)

### Community 116 - "GraphTreeVisualizer"
Cohesion: 0.36
Nodes (9): GraphFrontierTelemetryStrip(), GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), com, GraphEdgeState, GraphNodeState, Modifier (+1 more)

### Community 117 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 118 - "AiChatBackendTest.kt"
Cohesion: 0.25
Nodes (7): experimentalcoroutinesapi, flowchartshape, parseflowchart, parsemarkdowninline, parsemarkdowntable, runblocking, tablealignment

### Community 120 - "RecursionBands.kt"
Cohesion: 0.33
Nodes (4): Modifier, RecursionBands, canvas, stroke

### Community 121 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 122 - "DataScale"
Cohesion: 0.40
Nodes (5): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED

### Community 123 - "GraphGoal"
Cohesion: 0.40
Nodes (5): GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE, SHORTEST_UNWEIGHTED_PATH, WEIGHTED_SHORTEST_PATH

### Community 124 - "PhaseStrip"
Cohesion: 0.50
Nodes (3): androidx, Modifier, PhaseStrip

### Community 125 - "CellArrayVisualizer"
Cohesion: 0.40
Nodes (5): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State

### Community 126 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 127 - "BarVisualizer"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

### Community 128 - "AuthProviderType"
Cohesion: 0.67
Nodes (3): AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE

## Knowledge Gaps
- **193 isolated node(s):** `KOTLIN`, `JAVA`, `PYTHON`, `CPP`, `BUBBLE_SORT` (+188 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 463 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **50 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `SampleData`, `VisualizerStep`, `VisualizerHost.kt`, `.generateStepsForAlgorithm`, `TraceLanguage`, `AlgoLensTheme`, `setvalue`, `ChatSessionState.kt`, `ChallengeModeManagerTest.kt`, `FeatureEnhancementsTest.kt`, `1. Algorithmic Families & Variants`, `AiChatBackendTest.kt`, `.specFor`, `ChatHistoryRepository.kt`, `.processQuery`, `PracticeQuestionRepository`, `PracticeSessionState.kt`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `ChatSessionManager` connect `ChatSessionManager` to `ChatSessionState.kt`, `ChatConversation`, `FeatureEnhancementsTest.kt`, `AiChatBackendTest.kt`, `ChatScreen`, `ChatHistoryRepository.kt`, `.processQuery`, `FeatureEnhancementsTest`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `VisualizerStep`, `VisualizerHost.kt`, `setvalue`, `.generateStepsForAlgorithm`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `ElementState`, `RecursionTreeOverlay`, `ChallengeModeManagerTest.kt`, `FeatureEnhancementsTest.kt`, `rememberVisualizerScreenState`, `RegionAuxiliary`, `RecursionBands.kt`, `.specFor`, `VisualizerScreen.kt`?**
  _High betweenness centrality (0.054) - this node is a cross-community bridge._
- **What connects `KOTLIN`, `JAVA`, `PYTHON` to the rest of the system?**
  _193 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06505179282868526 - nodes in this community are weakly interconnected._
- **Should `VisualizerStep` be split into smaller, more focused modules?**
  _Cohesion score 0.146218487394958 - nodes in this community are weakly interconnected._
- **Should `SwapFlight.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06736353077816493 - nodes in this community are weakly interconnected._