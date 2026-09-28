# Graph Report - AlgoLens  (2026-09-28)

## Corpus Check
- 140 files · ~200,843 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1910 nodes · 6062 edges · 150 communities (91 shown, 59 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 184 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `33c5d878`
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
- GraphCanvasEngine
- Instrument.kt
- VisualizerScreenshotTest.kt
- NavTab
- PredictionQuestion
- ChatSessionManager
- GraphTreeRenderer.kt
- FeatureEnhancementsTest.kt
- UElementHandler
- UElementHandler
- AlgorithmRegistry
- TASK — Unified graph and tree editor
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- InputOrder
- StepToken
- VisualizerScreenState
- ChatScreen
- ChatHistoryRepository.kt
- VisualizerState.kt
- BufferVisualizer
- AlgorithmId
- BootController
- VisualizerScreen.kt
- FeatureEnhancementsTest
- AuthRepository
- GraphTool
- AppSettings.kt
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
- EditProfileSheet
- Dp
- .resolve
- AlgoLensApp.kt
- InstrumentDeck
- 6. Implementation Steps & Validation Checklist
- SectionLabel
- VisualizerHost.kt
- StageLegend
- ProfileFlowTest.kt
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
- .from
- DashboardScreen
- ArrayViewMode
- .specFor
- imepadding
- ComparisonBridgeOverlay.kt
- ElementState
- .Content
- UnifiedGraphEditorTest
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
- Step-by-step implementation plan
- ExampleUnitTest
- AlgoCard
- GraphSearch
- BufferOp
- AiChatBackendTest.kt
- GraphEdgeState
- GraphNodeState
- VisualizerHeaderMenuTest.kt
- ProjectConstraintBrief
- Offset
- com
- androidx
- RootStage
- BarVisualizer
- com
- SampleData
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
- start
- target

## God Nodes (most connected - your core abstractions)
1. `VisualizerScreenState` - 68 edges
2. `AlgorithmId` - 63 edges
3. `VisualizerStep` - 45 edges
4. `AlgoLensTheme()` - 44 edges
5. `ChatSessionManager` - 41 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `AlgoTokens` - 39 edges
8. `buildPredictionQuestion()` - 34 edges
9. `GraphCanvasEngine()` - 31 edges
10. `ChallengeModeManagerTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `Step 1: Capability Profiles & Models` --references--> `AlgorithmSpec`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/model/AlgorithmId.kt
- `4.1 Viewport Pan & Pinch-to-Zoom Engine` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Step 3: Viewport & Renderer Refactoring` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/UNIFIED_GRAPH_EDITOR_PROMPT.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Things that must NOT change` --references--> `VisualizerStep`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/VisualizerState.kt
- `2. Chat UI cleanup (`ui/chat/ChatScreen.kt`)` --references--> `pressPhysics()`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/main/java/com/example/algolens/ui/components/Instrument.kt

## Import Cycles
- None detected.

## Communities (150 total, 59 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.07
Nodes (184): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, alertdialog, algohairline (+176 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.14
Nodes (8): buildPredictionQuestion(), highlightedCellPair(), predictionAnswerFor(), BufferItem, GraphNodeState, VisualizerStep, ChallengeModeManagerTest, 3.1 Dijkstra Prediction Question Generator

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
Cohesion: 0.05
Nodes (48): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, ChatFlowchartBlock() (+40 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.22
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 7 - "VisualizerScreenStateTest"
Cohesion: 0.09
Nodes (3): Algorithm, VisualizerScreenStateTest, 4. Tests

### Community 8 - "GraphCanvasEngine"
Cohesion: 0.10
Nodes (16): GraphTreeMutations, GraphEdgeState, GraphNodeState, Offset, GraphCanvasEngine(), GraphEdgeState, GraphNodeState, Modifier (+8 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.18
Nodes (16): AlgoHairline(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color, Modifier (+8 more)

### Community 10 - "VisualizerScreenshotTest.kt"
Cohesion: 0.10
Nodes (17): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, FrameTimingBenchmark, StartupBenchmark, Bitmap, compilationmode (+9 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "PredictionQuestion"
Cohesion: 0.28
Nodes (19): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), VisualizerStep (+11 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.16
Nodes (14): drawCellGlow(), Color, atan2, CornerRadius, cos, drawscope, graphedgedefault, nativecanvas (+6 more)

### Community 15 - "FeatureEnhancementsTest.kt"
Cohesion: 0.22
Nodes (16): Algorithm, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, ElementState, GraphEdgeState (+8 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): JavaContext, callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UElementHandler (+4 more)

### Community 18 - "AlgorithmRegistry"
Cohesion: 0.09
Nodes (17): AlgorithmRegistry, InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D (+9 more)

### Community 19 - "TASK — Unified graph and tree editor"
Cohesion: 0.18
Nodes (10): Automated coverage, Compose/device checks, Design approach, Files/components likely affected, Goal, Potential risks or edge cases, Required commands after implementation, TASK — Unified graph and tree editor (+2 more)

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
Nodes (18): androidx, AttachedDeckTabs(), Algorithm, ArrayViewMode, BufferOp, GraphEdgeState, GraphNodeState, Offset (+10 more)

### Community 24 - "ChatScreen"
Cohesion: 0.11
Nodes (26): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), CompactDropdownMenuItem(), ComplexityMatrixBlock(), ComplexityPill() (+18 more)

### Community 25 - "ChatHistoryRepository.kt"
Cohesion: 0.14
Nodes (11): ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatSender, ASSISTANT, SYSTEM, USER, LaunchVisualizer (+3 more)

### Community 26 - "VisualizerState.kt"
Cohesion: 0.18
Nodes (10): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, Pointer, VisualizerRenderMode, BARS, BUFFER (+2 more)

### Community 27 - "BufferVisualizer"
Cohesion: 0.16
Nodes (14): BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), BufferOp, Color, Modifier (+6 more)

### Community 28 - "AlgorithmId"
Cohesion: 0.09
Nodes (23): AiChatEngine, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, DIJKSTRA (+15 more)

### Community 29 - "BootController"
Cohesion: 0.16
Nodes (8): BootController, BootControllerEffect(), BootControllerTest, delay, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`), 5. Verification, 6. Deviations / known issues, launchedeffect

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.14
Nodes (19): animatedpasstate, Algorithm, AiTutorSheet(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, Modifier, PlaybackRail() (+11 more)

### Community 32 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 33 - "GraphTool"
Cohesion: 0.08
Nodes (29): GraphTool, ADD, DELETE, ENDPOINTS, LINK, MOVE, WEIGHT, NodePlacementMode (+21 more)

### Community 34 - "AppSettings.kt"
Cohesion: 0.15
Nodes (8): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, Loading, ProfileValidator, stable

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "AviaLogo.kt"
Cohesion: 0.15
Nodes (14): BootOverlay(), Modifier, AviaLogo(), Color, Dp, Modifier, colorfilter, font (+6 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.16
Nodes (13): ProfileFlowTest, AlgoLensTheme(), AiTutorSheetPreview(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color (+5 more)

### Community 38 - "RailIconButton"
Cohesion: 0.18
Nodes (11): SortOrder, ASC, DESC, IconPillButton(), androidx, Color, ImageVector, RailIconButton() (+3 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

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
Cohesion: 0.12
Nodes (21): DoubleBezelShell(), AccountStatusCard(), ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), ProfileTheme() (+13 more)

### Community 50 - "FakeAuthRepository"
Cohesion: 0.21
Nodes (5): FakeAuthRepository, SignedIn, SignedOut, UserProfile, ProfileEditorDraftTest

### Community 51 - ".generateStepsForAlgorithm"
Cohesion: 0.18
Nodes (3): QueueOp, AlgorithmStepRepositoryTest, DijkstraStepsTest

### Community 52 - "AccountTemplateCard.kt"
Cohesion: 0.13
Nodes (16): activityresultcontracts, ProfileField(), contentdescription, dispatchers, focusdirection, imagebitmap, ImeAction, intent (+8 more)

### Community 53 - "VisualizerHeader"
Cohesion: 0.27
Nodes (14): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm, AlgorithmSpec (+6 more)

### Community 54 - "RegionAuxiliary"
Cohesion: 0.11
Nodes (11): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+3 more)

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

### Community 59 - "EditProfileSheet"
Cohesion: 0.28
Nodes (7): EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileEditorDraft, UserProfile

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
Cohesion: 0.10
Nodes (20): 1. Executive Summary & Objective, 2.1 File: `app/src/main/java/com/example/algolens/model/GraphCapabilityProfile.kt`, 2.2 Algorithm Capability Mapping, 2. Core Architecture: Capability & Profile System, 3.2 Persistent Canvas Coordinates & Step State Decorator, 3. Unified Data Layer & Pipeline Overhaul, 4.1 Viewport Pan & Pinch-to-Zoom Engine, 4.2 Modular Floating Glass Toolbar (`GraphFloatingToolbar.kt`) (+12 more)

### Community 65 - "SectionLabel"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 66 - "VisualizerHost.kt"
Cohesion: 0.22
Nodes (17): AlgorithmSpec, CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State, Modifier, PhaseBanner() (+9 more)

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "ProfileFlowTest.kt"
Cohesion: 0.14
Nodes (14): MainActivity, before, Bundle, ComponentActivity, compositionlocalprovider, createandroidcomposerule, density, 3. Minimalist boot splash (+6 more)

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
Cohesion: 0.14
Nodes (14): ChatStatus, COMPLETE, ERROR, SENT, THINKING, rememberChatSessionManager(), chathistoryrepository, chatresponseprovider (+6 more)

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 79 - "GraphCustomization"
Cohesion: 0.10
Nodes (18): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, 1.1 Binary Search Tree (BST) Traversal Family (+10 more)

### Community 80 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.27
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 85 - ".from"
Cohesion: 0.20
Nodes (7): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Modifier, Offset, 5. Build shared viewport geometry and canvas engine

### Community 86 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.25
Nodes (10): animatefloatasstate, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, canvas, path, SlotTransform (+2 more)

### Community 91 - "ElementState"
Cohesion: 0.17
Nodes (11): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+3 more)

### Community 92 - ".Content"
Cohesion: 0.33
Nodes (5): Modifier, VisualizerScreenState, VisualizerStep, WeightBadgeOverlay, VisualizerOverlay

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

### Community 113 - "Step-by-step implementation plan"
Cohesion: 0.25
Nodes (8): 1. Introduce validated capability profiles, 2. Establish authoritative state and reset contracts, 3. Implement pure structure-aware mutations, 4. Integrate generators and correct preview semantics, 6. Rebuild profile-driven tools and accessible interactions, 7. Consolidate fullscreen and telemetry, 8. Validate and document, Step-by-step implementation plan

### Community 115 - "AlgoCard"
Cohesion: 0.50
Nodes (4): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier

### Community 116 - "GraphSearch"
Cohesion: 0.15
Nodes (6): GraphSearch, Result, GraphFrontierTelemetryStrip(), GraphEdgeState, GraphSearchTest, Relevant existing implementation

### Community 117 - "BufferOp"
Cohesion: 0.25
Nodes (7): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp

### Community 118 - "AiChatBackendTest.kt"
Cohesion: 0.25
Nodes (7): experimentalcoroutinesapi, flowchartshape, parseflowchart, parsemarkdowninline, parsemarkdowntable, runblocking, tablealignment

### Community 121 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 122 - "ProjectConstraintBrief"
Cohesion: 0.15
Nodes (14): ProjectAlgorithmRecommender, DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED, GraphGoal, EXHAUSTIVE_OR_CYCLE (+6 more)

### Community 126 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 127 - "BarVisualizer"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

## Knowledge Gaps
- **198 isolated node(s):** `BUBBLE_SORT`, `SELECTION_SORT`, `INSERTION_SORT`, `MERGE_SORT`, `QUICK_SORT` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 481 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **59 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChatScreen.kt`, `GraphTool`, `VisualizerHost.kt`, `VisualizerStep`, `AlgorithmStepRepository`, `6. Implementation Steps & Validation Checklist`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `GraphCanvasEngine`, `RecursionTreeOverlay`, `FeatureEnhancementsTest.kt`, `.generateStepsForAlgorithm`, `RegionAuxiliary`, `.specFor`, `ElementState`, `UnifiedGraphEditorTest`, `VisualizerScreen.kt`?**
  _High betweenness centrality (0.091) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `SampleData`, `PracticeSessionManager`, `VisualizerStep`, `AlgorithmStepRepository`, `TraceLanguage`, `AlgoLensTheme`, `VisualizerHost.kt`, `ChatSessionState.kt`, `ChallengeModeManagerTest.kt`, `FeatureEnhancementsTest.kt`, `GraphCustomization`, `AlgorithmRegistry`, `AiChatBackendTest.kt`, `VisualizerScreenState`, `.specFor`, `ProjectConstraintBrief`?**
  _High betweenness centrality (0.089) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChatScreen.kt`, `.resolve`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **What connects `BUBBLE_SORT`, `SELECTION_SORT`, `INSERTION_SORT` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.07496513249651325 - nodes in this community are weakly interconnected._
- **Should `VisualizerStep` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager` be split into smaller, more focused modules?**
  _Cohesion score 0.06285714285714286 - nodes in this community are weakly interconnected._