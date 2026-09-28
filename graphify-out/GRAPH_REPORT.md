# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 140 files · ~200,725 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1914 nodes · 6062 edges · 153 communities (93 shown, 60 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 190 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `59a9e255`
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
- GraphTreeMutations
- Instrument.kt
- AlgoHairline
- NavTab
- PredictionQuestion
- ChatSessionManager
- GraphTreeRenderer.kt
- FeatureEnhancementsTest.kt
- UElementHandler
- UElementHandler
- GraphCapabilityProfile
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
- VisualizerScreenState.kt
- VisualizerScreen
- FeatureEnhancementsTest
- AuthRepository
- GraphBuilderGestures.kt
- AuthRepository.kt
- GlassPanel.kt
- AviaLogo.kt
- AlgoLensTheme
- RailIconButton
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
- VisualizerHeader
- RegionAuxiliary
- BufferOpEditor
- GraphTelemetryMode
- OnboardingScreen
- CatalogueSortMode
- ProfileEditorDraft
- Dp
- .resolve
- AlgoLensApp.kt
- InstrumentDeck
- 6. Implementation Steps & Validation Checklist
- SectionLabel
- VisualizerHost.kt
- GraphTool
- PracticeQuestionRepository
- FlowchartShape
- TableAlignment
- Context
- ChatSessionState.kt
- Profile and Edit Profile
- GraphCustomization
- UnavailableFirebaseAuthRepository
- Color
- Color
- PaddingValues
- TextUnit
- GraphCanvasGeometry
- DashboardScreen
- ArrayViewMode
- .specFor
- imepadding
- ComparisonBridgeOverlay.kt
- ElementState
- AlgorithmId.kt
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
- rememberVisualizerScreenState
- .shortestPath
- BufferOp
- PracticeSessionState.kt
- GraphEdgeState
- GraphNodeState
- PracticeSnapshot
- ProjectConstraintBrief
- Offset
- com
- androidx
- RootStage
- BarVisualizer
- com
- .highlight
- AuthRepository
- AlgorithmSpec
- State
- VisualizerScreenState
- ArrayViewMode
- BufferOp
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
- QueueOp
- SortOrder
- GraphCanvasEngine
- CellArrayVisualizer
- start
- target
- VisualizerStep

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 67 edges
2. `AlgorithmId` - 62 edges
3. `VisualizerStep` - 45 edges
4. `AlgoLensTheme()` - 44 edges
5. `ChatSessionManager` - 41 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `AlgoTokens` - 39 edges
8. `buildPredictionQuestion()` - 34 edges
9. `GraphCanvasEngine()` - 31 edges
10. `ChallengeModeManagerTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `Step 1: Capability Profiles & Models` --references--> `AlgorithmSpec`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/model/AlgorithmId.kt
- `4.1 Viewport Pan & Pinch-to-Zoom Engine` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Step 3: Viewport & Renderer Refactoring` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `2. Chat UI cleanup (`ui/chat/ChatScreen.kt`)` --references--> `pressPhysics()`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/main/java/com/example/algolens/ui/components/Instrument.kt

## Import Cycles
- None detected.

## Communities (153 total, 60 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.07
Nodes (191): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algohairline (+183 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.16
Nodes (7): buildPredictionQuestion(), predictionAnswerFor(), BufferItem, GraphNodeState, VisualizerStep, ChallengeModeManagerTest, 3.1 Dijkstra Prediction Question Generator

### Community 3 - "SwapFlight.kt"
Cohesion: 0.07
Nodes (37): abs, activity, animatecontentsize, animationspec, animationvector1d, Dp, Modifier, smoothPanelExpansion() (+29 more)

### Community 4 - "AlgorithmStepRepository"
Cohesion: 0.18
Nodes (10): AlgorithmStepRepository, Algorithm, BufferOp, Offset, QueueOp, SortOrder, VisualizerStep, bufferitem (+2 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.14
Nodes (18): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+10 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.24
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 7 - "VisualizerScreenStateTest"
Cohesion: 0.06
Nodes (16): MainActivity, BootController, BootControllerEffect(), BootControllerTest, Algorithm, VisualizerScreenStateTest, Bundle, ComponentActivity (+8 more)

### Community 8 - "GraphTreeMutations"
Cohesion: 0.20
Nodes (4): GraphTreeMutations, GraphEdgeState, GraphNodeState, Offset

### Community 9 - "Instrument.kt"
Cohesion: 0.18
Nodes (17): animatable, DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+9 more)

### Community 10 - "AlgoHairline"
Cohesion: 0.10
Nodes (25): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+17 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "PredictionQuestion"
Cohesion: 0.21
Nodes (22): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair(), Indices, insertionSortQuestion() (+14 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.14
Nodes (17): Modifier, RecursionBands, drawCellGlow(), Color, Modifier, atan2, canvas, CornerRadius (+9 more)

### Community 15 - "FeatureEnhancementsTest.kt"
Cohesion: 0.05
Nodes (54): activityscenariorule, Algorithm, AlgorithmId, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, Algorithm, SampleData (+46 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 18 - "GraphCapabilityProfile"
Cohesion: 0.14
Nodes (13): GraphCapabilityProfile, GraphFloatingToolbar(), Modifier, GraphFrontierTelemetryStrip(), GraphTreeVisualizer(), GraphTreeVisualizerPreview(), HeapSynchronizedArrayStrip(), GraphEdgeState (+5 more)

### Community 19 - "Step-by-step implementation plan"
Cohesion: 0.10
Nodes (19): 1. Introduce validated capability profiles, 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 5. Build shared viewport geometry and canvas engine, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document (+11 more)

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
Nodes (12): androidx, AttachedDeckTabs(), ArrayViewMode, BufferOp, GraphEdgeState, GraphNodeState, Offset, VisualizerStep (+4 more)

### Community 24 - "ChatScreen"
Cohesion: 0.11
Nodes (26): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+18 more)

### Community 25 - "ChatHistoryRepository.kt"
Cohesion: 0.13
Nodes (12): ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatStatus, COMPLETE, ERROR, SENT, THINKING (+4 more)

### Community 26 - "VisualizerState.kt"
Cohesion: 0.18
Nodes (10): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, Pointer, VisualizerRenderMode, BARS, BUFFER (+2 more)

### Community 27 - "BufferVisualizer"
Cohesion: 0.16
Nodes (14): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), BufferOp, Color, Modifier (+6 more)

### Community 28 - ".processQuery"
Cohesion: 0.15
Nodes (7): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, AlgorithmTheoryData, AlgorithmTheoryRepository

### Community 29 - "VisualizerScreenState.kt"
Cohesion: 0.25
Nodes (7): QueueOp, SortOrder, delay, GraphCustomization, launchedeffect, mutablelongstateof, stable

### Community 30 - "VisualizerScreen"
Cohesion: 0.14
Nodes (14): AiTutorSheet(), AiTutorSheetPreview(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, Color, Modifier, LegendEntry() (+6 more)

### Community 31 - "FeatureEnhancementsTest"
Cohesion: 0.13
Nodes (3): AppSettings, FeatureEnhancementsTest, SharedPreferences

### Community 32 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 33 - "GraphBuilderGestures.kt"
Cohesion: 0.14
Nodes (15): GraphBuilderGestures(), GraphEdgeState, GraphNodeState, Modifier, Offset, start, target, awaiteachgesture (+7 more)

### Community 34 - "AuthRepository.kt"
Cohesion: 0.17
Nodes (7): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, Loading, ProfileValidator

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "AviaLogo.kt"
Cohesion: 0.15
Nodes (14): BootOverlay(), Modifier, AviaLogo(), Color, Dp, Modifier, colorfilter, font (+6 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.18
Nodes (11): ProfileFlowTest, AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color, SearchKeyField() (+3 more)

### Community 38 - "RailIconButton"
Cohesion: 0.14
Nodes (14): SortOrder, ASC, DESC, IconPillButton(), androidx, Color, ImageVector, RailIconButton() (+6 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "AlgorithmId"
Cohesion: 0.11
Nodes (16): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA (+8 more)

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
Cohesion: 0.15
Nodes (12): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+4 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.14
Nodes (18): ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, authaccountstate, AuthRepository (+10 more)

### Community 50 - "FakeAuthRepository"
Cohesion: 0.28
Nodes (4): FakeAuthRepository, SignedIn, SignedOut, UserProfile

### Community 52 - "AccountTemplateCard.kt"
Cohesion: 0.12
Nodes (23): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileField() (+15 more)

### Community 53 - "VisualizerHeader"
Cohesion: 0.27
Nodes (14): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm, AlgorithmSpec (+6 more)

### Community 54 - "RegionAuxiliary"
Cohesion: 0.14
Nodes (9): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+1 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 56 - "GraphTelemetryMode"
Cohesion: 0.29
Nodes (7): GraphTelemetryMode, BFS_QUEUE, BST_TARGET, DFS_STACK, DIJKSTRA_PQ, HEAP_ARRAY, NONE

### Community 57 - "OnboardingScreen"
Cohesion: 0.28
Nodes (9): FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor(), OnboardingSlidePractice() (+1 more)

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
Nodes (21): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, ExploreCatalogScreen(), ExploreCatalogScreenPreview() (+13 more)

### Community 63 - "InstrumentDeck"
Cohesion: 0.25
Nodes (8): InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx, Modifier, State, VisualizerScreenState, VisualizerStep

### Community 64 - "6. Implementation Steps & Validation Checklist"
Cohesion: 0.10
Nodes (20): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`) (+12 more)

### Community 65 - "SectionLabel"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 66 - "VisualizerHost.kt"
Cohesion: 0.35
Nodes (12): AlgorithmSpec, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas(), ArrayViewMode, Modifier, VisualizerStep (+4 more)

### Community 67 - "GraphTool"
Cohesion: 0.14
Nodes (14): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+6 more)

### Community 68 - "PracticeQuestionRepository"
Cohesion: 0.24
Nodes (8): PracticeQuestionRepository, PracticeGraphEdge, PracticeGraphNode, PracticeDifficulty, practicegraphedge, practicegraphnode, practiceoption, PracticeQuestion

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 71 - "Context"
Cohesion: 0.53
Nodes (3): android, UserPreferences, Context

### Community 75 - "ChatSessionState.kt"
Cohesion: 0.15
Nodes (13): ChatSender, ASSISTANT, SYSTEM, USER, rememberChatSessionManager(), chathistoryrepository, chatresponseprovider, CoroutineScope (+5 more)

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 79 - "GraphCustomization"
Cohesion: 0.10
Nodes (18): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, 1.1 Binary Search Tree (BST) Traversal Family (+10 more)

### Community 80 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.27
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 85 - "GraphCanvasGeometry"
Cohesion: 0.38
Nodes (5): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Offset

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
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 92 - "AlgorithmId.kt"
Cohesion: 0.11
Nodes (14): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D, Modifier (+6 more)

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
Cohesion: 0.18
Nodes (3): Algorithm, rememberVisualizerScreenState(), 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`)

### Community 116 - ".shortestPath"
Cohesion: 0.17
Nodes (4): GraphSearch, Result, GraphSearchTest, Relevant existing implementation

### Community 117 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 118 - "PracticeSessionState.kt"
Cohesion: 0.25
Nodes (7): PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, rememberPracticeSessionManager()

### Community 121 - "PracticeSnapshot"
Cohesion: 0.31
Nodes (9): BufferSnapshot, GraphSnapshot, LinearSnapshot, PracticeSnapshot, BufferSnapshotView(), GraphSnapshotView(), Modifier, LinearSnapshotView() (+1 more)

### Community 122 - "ProjectConstraintBrief"
Cohesion: 0.15
Nodes (14): ProjectAlgorithmRecommender, DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED, GraphGoal, EXHAUSTIVE_OR_CYCLE (+6 more)

### Community 126 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 127 - "BarVisualizer"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

### Community 129 - ".highlight"
Cohesion: 0.22
Nodes (6): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), TraceLanguage

### Community 148 - "GraphCanvasEngine"
Cohesion: 0.14
Nodes (12): GraphCanvasEngine(), GraphEdgeState, GraphNodeState, Modifier, Offset, edges, key, nodeId (+4 more)

### Community 149 - "CellArrayVisualizer"
Cohesion: 0.40
Nodes (5): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State

## Knowledge Gaps
- **198 isolated node(s):** `MOVE`, `ADD`, `LINK`, `WEIGHT`, `ENDPOINTS` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 485 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **60 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `VisualizerStep`, `VisualizerHost.kt`, `AlgorithmStepRepository`, `TraceLanguage`, `PracticeQuestionRepository`, `AlgoLensTheme`, `ChatSessionState.kt`, `AlgorithmId.kt`, `FeatureEnhancementsTest.kt`, `ChallengeModeManagerTest.kt`, `GraphCustomization`, `PracticeSessionState.kt`, `.specFor`, `ProjectConstraintBrief`, `.processQuery`, `VisualizerScreenState.kt`?**
  _High betweenness centrality (0.096) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `VisualizerStep`, `AlgorithmStepRepository`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `GraphTreeMutations`, `GraphTreeRenderer.kt`, `FeatureEnhancementsTest.kt`, `VisualizerScreenState.kt`, `RailIconButton`, `RecursionTreeOverlay`, `RegionAuxiliary`, `6. Implementation Steps & Validation Checklist`, `VisualizerHost.kt`, `GraphTool`, `.specFor`, `BstTreeNode`, `UnifiedGraphEditorTest`, `rememberVisualizerScreenState`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChatScreen.kt`, `.resolve`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `VisualizerScreenState` (e.g. with `.visualizerScreenState_decoratesNodesWithUserCustomCoordinates()` and `.visualizerScreenState_effectiveTraversalNodeIdsExplicitEmpty()`) actually correct?**
  _`VisualizerScreenState` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `MOVE`, `ADD`, `LINK` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.07155788157071855 - nodes in this community are weakly interconnected._
- **Should `SwapFlight.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06951219512195123 - nodes in this community are weakly interconnected._