# Graph Report - AlgoLens  (2026-09-26)

## Corpus Check
- 128 files · ~181,936 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1523 nodes · 5167 edges · 90 communities (71 shown, 19 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 89 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `be8aa19b`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChallengeModeManager.kt
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
- ChatScreen.kt
- ChatSessionManager
- GraphTreeRenderer.kt
- ChallengeModeManagerTest.kt
- UElementHandler
- callName
- AlgorithmId
- ComparisonBridgeOverlay.kt
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- ProjectConstraintBrief
- StepToken
- VisualizerScreenState
- CanvasChallengePrompt
- ChatSessionState.kt
- GraphNodeState
- VisualizerHost.kt
- .processQuery
- BootController
- VisualizerScreen.kt
- AviaLogo.kt
- UnavailableFirebaseAuthRepository
- .specFor
- VisualizerScreenState.kt
- GlassPanel.kt
- DashboardScreen
- AlgoLensTheme
- VisualizerHeader
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- BufferVisualizer
- CellGrid
- InstrumentDeck
- AlgoLensDetectorsTest
- ChatConversation
- RecursionTreeOverlay
- PredictionKind
- UserPreferencesTest
- AccountTemplateCard.kt
- EditProfileSheet
- BarVisualizer
- CellGlowPainter.kt
- AuthRepository
- RegionAuxiliary
- BufferOpEditor
- InputValidationResult
- DoubleBezelShell
- CatalogueSortMode
- AlgorithmTheorySheet
- Dp
- FakeAuthRepository
- AlgoLensApp.kt
- PracticeSnapshot
- ChatHistorySidebar
- SectionLabel
- UElementHandler
- StageLegend
- VisualizerFamily
- FlowchartShape
- TableAlignment
- UserPreferences.kt
- ExampleUnitTest
- Profile and Edit Profile
- AlgorithmRegistry
- CellArrayVisualizer
- Color
- Color
- PaddingValues
- TextUnit
- ChallengeQuestionType
- ImageVector
- ArrayViewMode
- Color
- imepadding

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 99 edges
2. `AlgoLensTheme()` - 54 edges
3. `AlgoTokens` - 51 edges
4. `AlgorithmId` - 49 edges
5. `VisualizerScreenState` - 46 edges
6. `ChatSessionManager` - 39 edges
7. `AlgoType` - 36 edges
8. `buildPredictionQuestion()` - 33 edges
9. `AlgoGlyphs` - 31 edges
10. `ChallengeModeManagerTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `UserMessageBubble()` --calls--> `parseMarkdownInline()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt → app/src/main/java/com/example/algolens/ui/chat/ChatMarkdownRenderer.kt
- `AssistantMessageBubble()` --calls--> `ChatMarkdownMessage()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt → app/src/main/java/com/example/algolens/ui/chat/ChatMarkdownRenderer.kt
- `AppShell()` --calls--> `ChatScreen()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt
- `ChatScreen()` --calls--> `ChatHistorySidebar()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt → app/src/main/java/com/example/algolens/ui/chat/ChatSidebarAndPlanner.kt
- `ChatScreen()` --calls--> `ProjectPlannerSheet()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt → app/src/main/java/com/example/algolens/ui/chat/ChatSidebarAndPlanner.kt

## Import Cycles
- None detected.

## Communities (90 total, 19 thin omitted)

### Community 0 - "ChallengeModeManager.kt"
Cohesion: 0.09
Nodes (152): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, algotokens, algotype (+144 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.12
Nodes (25): bufferPushPopQuestion(), buildPredictionQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair(), Indices (+17 more)

### Community 2 - "PracticeSessionManager"
Cohesion: 0.10
Nodes (13): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, PracticeGraphEdge (+5 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.07
Nodes (38): abs, activity, animatecontentsize, animationspec, animationvector1d, Dp, Modifier, smoothPanelExpansion() (+30 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.09
Nodes (12): AlgorithmStepRepository, BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp (+4 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.12
Nodes (21): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+13 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.22
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.14
Nodes (22): animatable, AlgoHairline(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+14 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.07
Nodes (26): activityscenariorule, algolenstheme, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark (+18 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "ChatScreen.kt"
Cohesion: 0.09
Nodes (35): alertdialog, algohairline, annotatedstring, AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock() (+27 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.13
Nodes (15): GraphCanvasGeometry, Modifier, Modifier, WeightBadgeOverlay, atan2, cos, graphedgedefault, nativecanvas (+7 more)

### Community 15 - "ChallengeModeManagerTest.kt"
Cohesion: 0.12
Nodes (22): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+14 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "callName"
Cohesion: 0.19
Nodes (9): CodeLineAccent, Family, Color, callName(), hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), UCallExpression (+1 more)

### Community 18 - "AlgorithmId"
Cohesion: 0.13
Nodes (14): AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP, INSERTION_SORT (+6 more)

### Community 19 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.19
Nodes (12): animatefloatasstate, ImageVector, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, canvas, path (+4 more)

### Community 20 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.28
Nodes (11): AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement, Vendor (+3 more)

### Community 21 - "ProjectConstraintBrief"
Cohesion: 0.09
Nodes (22): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED, GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE (+14 more)

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 23 - "VisualizerScreenState"
Cohesion: 0.11
Nodes (6): DeckPage, STATE, TRACE, AttachedDeckTabs(), rememberVisualizerScreenState(), VisualizerScreenState

### Community 24 - "CanvasChallengePrompt"
Cohesion: 0.14
Nodes (8): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeFeedback, ChallengeState, FeedbackRow(), Modifier, PromptHeader(), FeatureEnhancementsTest

### Community 25 - "ChatSessionState.kt"
Cohesion: 0.08
Nodes (31): AiChatEngine, ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter (+23 more)

### Community 26 - "GraphNodeState"
Cohesion: 0.13
Nodes (20): GraphBuilderBanner(), GraphBuilderGestures(), GraphBuilderToolbar(), Modifier, GraphTreeRenderer(), GraphTreeVisualizer(), Modifier, GraphVisualizer() (+12 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.36
Nodes (10): AlgorithmSpec, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas(), Modifier, State, Linear1DCanvas() (+2 more)

### Community 28 - ".processQuery"
Cohesion: 0.11
Nodes (10): AnnotatedString, SyntaxHighlighter, AiChatBackendTest, experimentalcoroutinesapi, flowchartshape, parseflowchart, parsemarkdowntable, runblocking (+2 more)

### Community 29 - "BootController"
Cohesion: 0.14
Nodes (12): MainActivity, BootController, BootControllerEffect(), AlgorithmicWaveLoader(), BootOverlay(), Modifier, BootControllerTest, Bundle (+4 more)

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.15
Nodes (18): animatedpasstate, SampleData, Algorithm, AiTutorSheet(), AiTutorSheetPreview(), Modifier, PlaybackRail(), PlaybackRailPreview() (+10 more)

### Community 31 - "AviaLogo.kt"
Cohesion: 0.31
Nodes (8): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource, r

### Community 32 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.23
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 34 - "VisualizerScreenState.kt"
Cohesion: 0.13
Nodes (11): AppSettings, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, ProfileValidator, chathistoryrepository, delay, launchedeffect (+3 more)

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.16
Nodes (14): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview() (+6 more)

### Community 38 - "VisualizerHeader"
Cohesion: 0.17
Nodes (15): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), CustomizeInputSheet(), CustomizeInputSheetPreview() (+7 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "BufferVisualizer"
Cohesion: 0.20
Nodes (10): BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), Color, Modifier, QueueCanvas(), QueueGate() (+2 more)

### Community 42 - "CellGrid"
Cohesion: 0.27
Nodes (11): CellGrid(), CellItem(), Dp, LazyListState, Modifier, State, TextUnit, BottomPointerBadge() (+3 more)

### Community 43 - "InstrumentDeck"
Cohesion: 0.18
Nodes (11): InstrumentDeck(), InstrumentDeckPreview(), Modifier, State, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage() (+3 more)

### Community 44 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 45 - "ChatConversation"
Cohesion: 0.33
Nodes (3): ChatHistoryRepository, ChatConversation, JSONObject

### Community 46 - "RecursionTreeOverlay"
Cohesion: 0.25
Nodes (5): Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 47 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 49 - "AccountTemplateCard.kt"
Cohesion: 0.07
Nodes (38): activityresultcontracts, AccountStatusCard(), EditProfileAndAccountSheet(), Modifier, ProfileAvatar(), ProfileField(), AuthRepository, Modifier (+30 more)

### Community 50 - "EditProfileSheet"
Cohesion: 0.14
Nodes (8): ProfileFlowTest, EditProfileSheet(), AuthRepository, ProfileEditorDraft, ProfileTheme(), ProfileTopBar(), ProfileEditorDraftTest, UserProfile

### Community 51 - "BarVisualizer"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

### Community 52 - "CellGlowPainter.kt"
Cohesion: 0.47
Nodes (5): drawCellGlow(), Color, CornerRadius, drawscope, stroke

### Community 53 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 54 - "RegionAuxiliary"
Cohesion: 0.11
Nodes (11): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+3 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.39
Nodes (8): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), Color, OpRow(), OpRowData

### Community 56 - "InputValidationResult"
Cohesion: 0.18
Nodes (10): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Color (+2 more)

### Community 57 - "DoubleBezelShell"
Cohesion: 0.25
Nodes (11): DoubleBezelShell(), PaddingValues, FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen() (+3 more)

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "AlgorithmTheorySheet"
Cohesion: 0.40
Nodes (6): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color, Modifier, PropertyBadge()

### Community 61 - "FakeAuthRepository"
Cohesion: 0.27
Nodes (7): AuthAccountState, Error, FakeAuthRepository, Loading, SignedIn, SignedOut, UserProfile

### Community 62 - "AlgoLensApp.kt"
Cohesion: 0.16
Nodes (17): Algorithm, animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Modifier, RootStage, APP (+9 more)

### Community 63 - "PracticeSnapshot"
Cohesion: 0.21
Nodes (12): BufferSnapshot, GraphSnapshot, LinearSnapshot, PracticeSnapshot, Modifier, PracticeScreen(), BufferSnapshotView(), GraphSnapshotView() (+4 more)

### Community 64 - "ChatHistorySidebar"
Cohesion: 0.20
Nodes (10): ProjectRecommendationPayload, RecommendedAlgorithmCandidate, CandidateCard(), ChatHistorySidebar(), ComplexityPill(), androidx, ImageVector, Modifier (+2 more)

### Community 65 - "SectionLabel"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 66 - "UElementHandler"
Cohesion: 0.36
Nodes (6): UElementHandler, JavaContext, UElementHandler, UElementHandler, UElementHandler, UElementHandler

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "VisualizerFamily"
Cohesion: 0.22
Nodes (7): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 71 - "UserPreferences.kt"
Cohesion: 0.53
Nodes (3): android, UserPreferences, Context

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 80 - "CellArrayVisualizer"
Cohesion: 0.40
Nodes (5): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State

### Community 85 - "ChallengeQuestionType"
Cohesion: 0.40
Nodes (5): challengeEligibleIndices(), ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

## Knowledge Gaps
- **149 isolated node(s):** `USER`, `ASSISTANT`, `SYSTEM`, `SENT`, `THINKING` (+144 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 359 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **19 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep` to `ChallengeModeManager.kt`, `.generateStepsForAlgorithm`, `TraceLanguage`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `Instrument.kt`, `GraphTreeRenderer.kt`, `ChallengeModeManagerTest.kt`, `ComparisonBridgeOverlay.kt`, `VisualizerScreenState`, `CanvasChallengePrompt`, `GraphNodeState`, `VisualizerHost.kt`, `.specFor`, `VisualizerHeader`, `BufferVisualizer`, `CellGrid`, `InstrumentDeck`, `RecursionTreeOverlay`, `BarVisualizer`, `RegionAuxiliary`, `CellArrayVisualizer`, `ChallengeQuestionType`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChallengeModeManager.kt`, `.specFor`, `PracticeSessionManager`, `VisualizerStep`, `VisualizerFamily`, `.generateStepsForAlgorithm`, `AlgoLensTheme`, `AlgorithmRegistry`, `ChallengeModeManagerTest.kt`, `CanvasChallengePrompt`, `ChatSessionState.kt`, `GraphNodeState`, `VisualizerHost.kt`, `.processQuery`, `VisualizerScreen.kt`?**
  _High betweenness centrality (0.059) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChallengeModeManager.kt`, `callName`?**
  _High betweenness centrality (0.052) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `AlgoLensTheme()` (e.g. with `.editorRestoresDraftAndRequiresExplicitDiscard()` and `.narrowLargeTextEditorRemainsUsable()`) actually correct?**
  _`AlgoLensTheme()` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `USER`, `ASSISTANT`, `SYSTEM` to the rest of the system?**
  _149 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChallengeModeManager.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.08906013754084768 - nodes in this community are weakly interconnected._
- **Should `VisualizerStep` be split into smaller, more focused modules?**
  _Cohesion score 0.11538461538461539 - nodes in this community are weakly interconnected._