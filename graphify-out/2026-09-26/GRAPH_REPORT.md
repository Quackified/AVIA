# Graph Report - AlgoLens  (2026-09-26)

## Corpus Check
- 128 files · ~177,544 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1399 nodes · 4841 edges · 85 communities (72 shown, 13 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 70 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b6f49d71`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- buildPredictionQuestion
- PracticeSessionManager
- SwapFlight.kt
- .generateStepsForAlgorithm
- TraceLanguage
- RecursionTreeOverlay
- VisualizerScreenStateTest
- ChatMarkdownMessage
- Instrument.kt
- ProfileFlowTest.kt
- NavTab
- ChatScreen
- AiChatEngine.kt
- GraphTreeRenderer.kt
- test
- UElementHandler
- UElementHandler
- AlgorithmId
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AccountTemplateCard.kt
- StepToken
- VisualizerScreenState
- VisualizerStep
- .processQuery
- GraphNodeState
- VisualizerHost.kt
- AlgoLensDetectorsTest
- AlgoLensDetectorsTest
- VisualizerScreen.kt
- AviaLogo.kt
- FakeAuthRepository
- .specFor
- setvalue
- AuthRepository
- DashboardScreen
- AlgoLensTheme
- VisualizerHeader
- CanvasChallengePrompt
- ChatSessionManager
- BufferItem
- CellGrid.kt
- InstrumentDeck
- ElementState
- UnavailableFirebaseAuthRepository
- BootController
- PredictionKind
- UserPreferencesTest.kt
- ProfileScreen.kt
- ProfileEditorDraft
- Theme.kt
- MainActivity.kt
- FeatureEnhancementsTest
- RegionAuxiliary
- BufferOpEditor
- InputValidationResult
- .setOnboardingCompleted
- CatalogueSortMode
- AlgorithmTheorySheet
- CodeListing
- VisualizerHeaderMenuTest.kt
- AppShell
- VisualizerBenchmarks.kt
- rememberSlotFlightMap
- ChallengeQuestionType
- .resolve
- StageLegend
- VisualizerAuxiliary.kt
- FlowchartShape
- TableAlignment
- Type.kt
- ExampleUnitTest
- Profile and Edit Profile
- RootStage
- AuthProviderType
- Color
- Color
- PaddingValues
- TextUnit

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 99 edges
2. `AlgoLensTheme()` - 55 edges
3. `AlgoTokens` - 54 edges
4. `AlgorithmId` - 53 edges
5. `VisualizerScreenState` - 46 edges
6. `AlgoType` - 39 edges
7. `buildPredictionQuestion()` - 33 edges
8. `AlgoGlyphs` - 30 edges
9. `ChallengeModeManagerTest` - 30 edges
10. `VisualizerScreenStateTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `AlgoLensApp()` --calls--> `BootControllerEffect()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/boot/BootController.kt
- `AlgoLensApp()` --calls--> `OnboardingScreen()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/onboarding/OnboardingScreen.kt
- `AppShell()` --calls--> `ChatScreen()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt
- `AppShell()` --calls--> `BottomNavBar()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/components/BottomNavBar.kt
- `AppShell()` --calls--> `DashboardScreen()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/dashboard/DashboardScreen.kt

## Import Cycles
- None detected.

## Communities (85 total, 13 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.10
Nodes (143): accentgreen, accentorange, accentpink, accentred, accentyellow, Alignment, alpha, animatecolorasstate (+135 more)

### Community 1 - "buildPredictionQuestion"
Cohesion: 0.17
Nodes (3): buildPredictionQuestion(), predictionAnswerFor(), ChallengeModeManagerTest

### Community 2 - "PracticeSessionManager"
Cohesion: 0.08
Nodes (22): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+14 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.17
Nodes (16): animationspec, animationvector1d, animatedSlotSwap(), FlightSpec, Dp, Modifier, nodePop(), SlideDirection (+8 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.09
Nodes (12): AlgorithmStepRepository, BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp (+4 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.32
Nodes (7): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 6 - "RecursionTreeOverlay"
Cohesion: 0.12
Nodes (13): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay (+5 more)

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.06
Nodes (51): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+43 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.12
Nodes (16): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, Bitmap, compositionlocalprovider, createandroidcomposerule, density (+8 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "ChatScreen"
Cohesion: 0.29
Nodes (12): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), ComplexityMatrixBlock(), ComplexityPill(), Color (+4 more)

### Community 13 - "AiChatEngine.kt"
Cohesion: 0.14
Nodes (14): ChatAction, ChatCodeSnippet, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus, COMPLETE (+6 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.06
Nodes (48): animatefloat, animatefloatasstate, ImageVector, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp (+40 more)

### Community 15 - "test"
Cohesion: 0.27
Nodes (10): assertequals, assertfalse, assertnotnull, assertnull, asserttrue, experimentalcoroutinesapi, preset_options, runblocking (+2 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 18 - "AlgorithmId"
Cohesion: 0.08
Nodes (22): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP (+14 more)

### Community 19 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+5 more)

### Community 20 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement, Vendor (+5 more)

### Community 21 - "AccountTemplateCard.kt"
Cohesion: 0.10
Nodes (27): activityresultcontracts, EditProfileAndAccountSheet(), EditProfileSheet(), Dp, Modifier, ProfileAvatar(), ProfileField(), asimagebitmap (+19 more)

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 23 - "VisualizerScreenState"
Cohesion: 0.11
Nodes (5): DeckPage, STATE, TRACE, rememberVisualizerScreenState(), VisualizerScreenState

### Community 24 - "VisualizerStep"
Cohesion: 0.20
Nodes (22): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair(), Indices, insertionSortQuestion() (+14 more)

### Community 25 - ".processQuery"
Cohesion: 0.15
Nodes (6): AiChatEngine, ChatComplexitySnapshot, ChatMessage, AlgorithmTheoryData, AlgorithmTheoryRepository, AiChatBackendTest

### Community 26 - "GraphNodeState"
Cohesion: 0.12
Nodes (19): GraphBuilderBanner(), GraphBuilderGestures(), GraphBuilderToolbar(), Modifier, GraphTreeVisualizer(), GraphTreeVisualizerPreview(), Modifier, GraphVisualizer() (+11 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.16
Nodes (18): AlgorithmSpec, ArrayViewMode, BARS, CELLS, CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier (+10 more)

### Community 28 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 29 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.18
Nodes (15): animatedpasstate, SampleData, Algorithm, AiTutorSheet(), Modifier, PlaybackRail(), PlaybackRailPreview(), EmptyCanvas() (+7 more)

### Community 31 - "AviaLogo.kt"
Cohesion: 0.36
Nodes (7): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource

### Community 32 - "FakeAuthRepository"
Cohesion: 0.18
Nodes (7): ProfileFlowTest, AuthAccountState, Error, FakeAuthRepository, Loading, SignedIn, SignedOut

### Community 34 - "setvalue"
Cohesion: 0.20
Nodes (13): AppSettings, rememberChatSessionManager(), chathistoryrepository, CoroutineScope, delay, launch, launchedeffect, mutablefloatstateof (+5 more)

### Community 35 - "AuthRepository"
Cohesion: 0.12
Nodes (5): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT, AccountStatusCard()

### Community 36 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.12
Nodes (18): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, AlgoLensTheme(), AiTutorSheetPreview(), BarVisualizer(), BarVisualizerPreview() (+10 more)

### Community 38 - "VisualizerHeader"
Cohesion: 0.16
Nodes (16): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), CustomizeInputSheet(), CustomizeInputSheetPreview() (+8 more)

### Community 39 - "CanvasChallengePrompt"
Cohesion: 0.28
Nodes (7): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeFeedback, ChallengeState, FeedbackRow(), Modifier, PromptHeader()

### Community 41 - "BufferItem"
Cohesion: 0.18
Nodes (11): BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), Color, Modifier, QueueCanvas(), QueueGate() (+3 more)

### Community 42 - "CellGrid.kt"
Cohesion: 0.12
Nodes (23): abs, accentpinkglow, animatable, CellGrid(), CellItem(), Dp, LazyListState, Modifier (+15 more)

### Community 43 - "InstrumentDeck"
Cohesion: 0.40
Nodes (5): AttachedDeckTabs(), InstrumentDeck(), InstrumentDeckPreview(), Modifier, State

### Community 44 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 45 - "UnavailableFirebaseAuthRepository"
Cohesion: 0.23
Nodes (3): CredentialValidator, Unavailable, UnavailableFirebaseAuthRepository

### Community 46 - "BootController"
Cohesion: 0.31
Nodes (3): BootController, BootControllerEffect(), BootControllerTest

### Community 47 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 48 - "UserPreferencesTest.kt"
Cohesion: 0.27
Nodes (4): UserPreferencesTest, before, proxy, SharedPreferences

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.20
Nodes (13): Modifier, ProfileDestination(), ProfileScreen(), ProfileScreenPreview(), ProfileTheme(), ProfileTopBar(), backhandler, heading (+5 more)

### Community 50 - "ProfileEditorDraft"
Cohesion: 0.16
Nodes (4): ProfileValidator, UserProfile, ProfileEditorDraft, ProfileEditorDraftTest

### Community 51 - "Theme.kt"
Cohesion: 0.13
Nodes (15): activity, animatecontentsize, Dp, Modifier, smoothPanelExpansion(), Spacing, Color, composed (+7 more)

### Community 52 - "MainActivity.kt"
Cohesion: 0.32
Nodes (6): MainActivity, Bundle, ComponentActivity, enableedgetoedge, installsplashscreen, setcontent

### Community 53 - "FeatureEnhancementsTest"
Cohesion: 0.15
Nodes (6): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), FeatureEnhancementsTest

### Community 54 - "RegionAuxiliary"
Cohesion: 0.15
Nodes (8): RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier, PhaseStrip, Modifier, RecursionBands

### Community 55 - "BufferOpEditor"
Cohesion: 0.39
Nodes (8): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), Color, OpRow(), OpRowData

### Community 56 - "InputValidationResult"
Cohesion: 0.18
Nodes (10): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Color (+2 more)

### Community 57 - ".setOnboardingCompleted"
Cohesion: 0.60
Nodes (3): android, UserPreferences, Context

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "AlgorithmTheorySheet"
Cohesion: 0.40
Nodes (6): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color, Modifier, PropertyBadge()

### Community 60 - "CodeListing"
Cohesion: 0.18
Nodes (11): CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color, LazyListState, Modifier, CodeTracePane() (+3 more)

### Community 61 - "VisualizerHeaderMenuTest.kt"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 62 - "AppShell"
Cohesion: 0.24
Nodes (10): Algorithm, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Modifier, AlgorithmicWaveLoader(), BootOverlay(), Modifier (+2 more)

### Community 63 - "VisualizerBenchmarks.kt"
Cohesion: 0.20
Nodes (7): FrameTimingBenchmark, StartupBenchmark, compilationmode, frametimingmetric, macrobenchmarkrule, startupmode, startuptimingmetric

### Community 64 - "rememberSlotFlightMap"
Cohesion: 0.31
Nodes (7): findSwappedPairs(), State, rememberSlotFlightMap(), SlotFlightMap, SlotTransform, SlotFlightMap, SlotTransform

### Community 65 - "ChallengeQuestionType"
Cohesion: 0.40
Nodes (5): challengeEligibleIndices(), ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION

### Community 66 - ".resolve"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "VisualizerAuxiliary.kt"
Cohesion: 0.50
Nodes (3): AuxiliarySlot, BOTTOM, TOP

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 71 - "Type.kt"
Cohesion: 0.33
Nodes (5): font, r, TextStyle, TextUnit, typography

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 79 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 80 - "AuthProviderType"
Cohesion: 0.67
Nodes (3): AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE

## Knowledge Gaps
- **135 isolated node(s):** `Loading`, `Error`, `GOOGLE_FIREBASE`, `EMAIL_PASSWORD`, `MERGE_GUEST_TO_ACCOUNT` (+130 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 310 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `buildPredictionQuestion`, `.generateStepsForAlgorithm`, `RecursionTreeOverlay`, `VisualizerScreenStateTest`, `Instrument.kt`, `GraphTreeRenderer.kt`, `test`, `VisualizerScreenState`, `GraphNodeState`, `VisualizerHost.kt`, `.specFor`, `AlgoLensTheme`, `VisualizerHeader`, `CanvasChallengePrompt`, `BufferItem`, `CellGrid.kt`, `InstrumentDeck`, `FeatureEnhancementsTest`, `RegionAuxiliary`, `CodeListing`, `ChallengeQuestionType`, `VisualizerAuxiliary.kt`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `.specFor`, `PracticeSessionManager`, `setvalue`, `.generateStepsForAlgorithm`, `buildPredictionQuestion`, `AlgoLensTheme`, `CanvasChallengePrompt`, `AiChatEngine.kt`, `test`, `VisualizerStep`, `.processQuery`, `GraphNodeState`, `VisualizerHost.kt`, `VisualizerScreen.kt`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `CodeListing` to `ChatScreen.kt`, `.resolve`, `TraceLanguage`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **What connects `Loading`, `Error`, `GOOGLE_FIREBASE` to the rest of the system?**
  _135 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.09623272884283247 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager` be split into smaller, more focused modules?**
  _Cohesion score 0.07536231884057971 - nodes in this community are weakly interconnected._
- **Should `.generateStepsForAlgorithm` be split into smaller, more focused modules?**
  _Cohesion score 0.09390243902439024 - nodes in this community are weakly interconnected._