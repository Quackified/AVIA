# Graph Report - AlgoLens  (2026-09-24)

## Corpus Check
- 120 files · ~115,790 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 54 file(s) not represented in the graph (top: .xml 46, (none) 3, .IssueRegistry 2)

## Summary
- 1278 nodes · 4682 edges · 78 communities (66 shown, 12 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 55 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- ChallengeModeManager / CustomizeInputSheet
- VisualizerStep / buildPredictionQuestion()
- PracticeSessionManager / PracticeSessionState
- SwapFlight / Theme
- .generateStepsForAlgorithm() / AlgorithmStepRepository
- TraceLanguage / AlgorithmCodeRegistry
- HANDOVER / PRODUCT
- VisualizerScreenStateTest / .makeState()
- ChatMarkdownMessage() / parseMarkdownInline()
- Instrument / DoubleBezelShell()
- VisualizerScreenshotTest / VisualizerBenchmarks
- AlgoLensApp / AppShell()
- ChatScreen / ChatScreen()
- ChatSessionState / AiChatEngine
- GraphTreeRenderer / WeightBadgeOverlay
- AiChatBackendTest / FeatureEnhancementsTest
- UElementHandler / callName()
- UElementHandler / callName()
- AlgorithmId / AlgorithmRegistry
- AlgoLensIssueRegistry / HardcodedHexColorDetector
- AlgoLensIssueRegistry / HardcodedHexColorDetector
- GlassPanel / ChallengeGlowTargets
- StepToken / COMPARE
- VisualizerScreenState / rememberVisualizerScreenState()
- ComparisonBridgeOverlay / AlgoGlyphs
- AiChatEngine / ChatMessage
- GraphNodeState / GraphTreeVisualizer()
- VisualizerHost / AlgorithmSpec
- AlgoLensDetectorsTest / .lintTask()
- AlgoLensDetectorsTest / .lintTask()
- VisualizerScreen / VisualizerScreen()
- AviaLogo / AviaLogo()
- SectionLabel() / SettingsScreen()
- .specFor() / AuxiliaryComponentTest
- VisualizerScreenState / launchedeffect
- .processQuery() / AiChatBackendTest
- DashboardScreen() / CatalogueGroupHeader()
- AlgoLensTheme() / CustomizeGraphSheet()
- RailIconButton() / IconPillButton()
- CanvasChallengePrompt() / ChallengeState
- ChatSessionManager / .sendMessage()
- BufferItem / BufferVisualizer()
- CellGrid() / CellItem()
- InstrumentDeck() / StateDeckPage()
- ElementState / UI_GUIDELINES
- Algorithm / VisualizerHeader()
- BootController / BootControllerTest
- PredictionKind / FOUND_DECISION
- UserPreferencesTest / .createMockPreferences()
- AlgoCard() / ProfileScreen()
- OnboardingScreen() / OnboardingSlideAiTutor()
- DeckPage / TASKS
- MainActivity / .onCreate()
- AlgorithmId / VisualizerOverlay
- RecursionBands / RegionAuxiliary
- BufferOpEditor() / CustomizeBufferSheet()
- RecursionTreeOverlay / .Content()
- UserPreferences / .init()
- CatalogueSortMode / COMPLEXITY
- AlgorithmTheorySheet() / ComplexityBentoCard()
- PhaseStrip / .StripContent()
- VisualizerHeaderMenuTest / icons
- MergeBufferRow / .BufferContent()
- CellArrayVisualizer() / CellArrayVisualizerPreview()
- CellGlowPainter / drawCellGlow()
- ChallengeQuestionType / challengeEligibleIndices()
- .resolve() / CodeLineAccent
- StageLegend() / LegendEntry()
- VisualizerAuxiliary / AuxiliarySlot
- FlowchartShape / DECISION
- TableAlignment / CENTER
- BarVisualizer() / BarVisualizerPreview()

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 103 edges
2. `AlgoTokens` - 59 edges
3. `AlgorithmId` - 56 edges
4. `VisualizerScreenState` - 51 edges
5. `AlgoLensTheme()` - 50 edges
6. `AlgoType` - 42 edges
7. `AlgoGlyphs` - 33 edges
8. `buildPredictionQuestion()` - 33 edges
9. `VisualizerScreenStateTest` - 33 edges
10. `Algorithm` - 31 edges

## Surprising Connections (you probably didn't know these)
- `UserMessageBubble()` --calls--> `parseMarkdownInline()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt → app/src/main/java/com/example/algolens/ui/chat/ChatMarkdownRenderer.kt
- `AssistantMessageBubble()` --calls--> `ChatMarkdownMessage()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt → app/src/main/java/com/example/algolens/ui/chat/ChatMarkdownRenderer.kt
- `PracticeScreen()` --calls--> `PracticeVisualCanvas()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/practice/PracticeScreen.kt → app/src/main/java/com/example/algolens/ui/practice/PracticeVisualCanvas.kt
- `VisualizerScreen()` --calls--> `AlgorithmTheorySheet()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/visualizer/VisualizerScreen.kt → app/src/main/java/com/example/algolens/ui/visualizer/AlgorithmTheorySheet.kt
- `Linear1DCanvas()` --calls--> `BarVisualizer()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/visualizer/VisualizerHost.kt → app/src/main/java/com/example/algolens/ui/visualizer/BarVisualizer.kt

## Import Cycles
- None detected.

## Communities (78 total, 12 thin omitted)

### Community 0 - "ChallengeModeManager / CustomizeInputSheet"
Cohesion: 0.10
Nodes (143): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, Alignment, alpha (+135 more)

### Community 1 - "VisualizerStep / buildPredictionQuestion()"
Cohesion: 0.13
Nodes (22): bufferPushPopQuestion(), buildPredictionQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair(), Indices (+14 more)

### Community 2 - "PracticeSessionManager / PracticeSessionState"
Cohesion: 0.08
Nodes (22): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+14 more)

### Community 3 - "SwapFlight / Theme"
Cohesion: 0.06
Nodes (41): abs, activity, animatable, animatecontentsize, animationspec, animationvector1d, Color, Dp (+33 more)

### Community 4 - ".generateStepsForAlgorithm() / AlgorithmStepRepository"
Cohesion: 0.09
Nodes (13): AlgorithmStepRepository, BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp (+5 more)

### Community 5 - "TraceLanguage / AlgorithmCodeRegistry"
Cohesion: 0.09
Nodes (24): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+16 more)

### Community 6 - "HANDOVER / PRODUCT"
Cohesion: 0.09
Nodes (21): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D, OffscreenPointerTarget (+13 more)

### Community 8 - "ChatMarkdownMessage() / parseMarkdownInline()"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument / DoubleBezelShell()"
Cohesion: 0.13
Nodes (25): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+17 more)

### Community 10 - "VisualizerScreenshotTest / VisualizerBenchmarks"
Cohesion: 0.10
Nodes (17): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, FrameTimingBenchmark, StartupBenchmark, Bitmap, compilationmode (+9 more)

### Community 11 - "AlgoLensApp / AppShell()"
Cohesion: 0.12
Nodes (23): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Modifier, RootStage, APP, BOOT (+15 more)

### Community 12 - "ChatScreen / ChatScreen()"
Cohesion: 0.17
Nodes (22): annotatedstring, ChatComplexitySnapshot, AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), ComplexityMatrixBlock() (+14 more)

### Community 13 - "ChatSessionState / AiChatEngine"
Cohesion: 0.12
Nodes (19): ChatAction, ChatCodeSnippet, ChatPromptStarter, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus (+11 more)

### Community 14 - "GraphTreeRenderer / WeightBadgeOverlay"
Cohesion: 0.15
Nodes (16): GraphCanvasGeometry, GraphTreeRenderer(), Modifier, Modifier, WeightBadgeOverlay, atan2, cos, graphedgedefault (+8 more)

### Community 15 - "AiChatBackendTest / FeatureEnhancementsTest"
Cohesion: 0.26
Nodes (11): SampleData, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, experimentalcoroutinesapi, preset_options (+3 more)

### Community 16 - "UElementHandler / callName()"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler / callName()"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 18 - "AlgorithmId / AlgorithmRegistry"
Cohesion: 0.12
Nodes (15): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP (+7 more)

### Community 19 - "AlgoLensIssueRegistry / HardcodedHexColorDetector"
Cohesion: 0.23
Nodes (13): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+5 more)

### Community 20 - "AlgoLensIssueRegistry / HardcodedHexColorDetector"
Cohesion: 0.23
Nodes (13): AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement, Vendor (+5 more)

### Community 21 - "GlassPanel / ChallengeGlowTargets"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 22 - "StepToken / COMPARE"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 24 - "ComparisonBridgeOverlay / AlgoGlyphs"
Cohesion: 0.17
Nodes (13): animatefloatasstate, ImageVector, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, canvas, localdensity (+5 more)

### Community 25 - "AiChatEngine / ChatMessage"
Cohesion: 0.20
Nodes (4): AiChatEngine, ChatMessage, AlgorithmTheoryData, AlgorithmTheoryRepository

### Community 26 - "GraphNodeState / GraphTreeVisualizer()"
Cohesion: 0.19
Nodes (13): GraphBuilderBanner(), GraphBuilderGestures(), GraphBuilderToolbar(), Modifier, GraphTreeVisualizer(), GraphTreeVisualizerPreview(), Modifier, GraphVisualizer() (+5 more)

### Community 27 - "VisualizerHost / AlgorithmSpec"
Cohesion: 0.24
Nodes (13): AlgorithmSpec, ArrayViewMode, BARS, CELLS, Modifier, PhaseBanner(), BufferCanvas(), GraphTreeCanvas() (+5 more)

### Community 28 - "AlgoLensDetectorsTest / .lintTask()"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 29 - "AlgoLensDetectorsTest / .lintTask()"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 30 - "VisualizerScreen / VisualizerScreen()"
Cohesion: 0.16
Nodes (13): animatedpasstate, GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, Modifier, VisualizerScreen(), VisualizerScreenPreview(), blur (+5 more)

### Community 31 - "AviaLogo / AviaLogo()"
Cohesion: 0.18
Nodes (12): AviaLogo(), Color, Dp, Modifier, TextUnit, colorfilter, font, image (+4 more)

### Community 32 - "SectionLabel() / SettingsScreen()"
Cohesion: 0.20
Nodes (14): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+6 more)

### Community 34 - "VisualizerScreenState / launchedeffect"
Cohesion: 0.19
Nodes (9): AppSettings, ForBst, ForHeap, ForTraversal, GraphCustomization, delay, launchedeffect, mutablelongstateof (+1 more)

### Community 36 - "DashboardScreen() / CatalogueGroupHeader()"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 37 - "AlgoLensTheme() / CustomizeGraphSheet()"
Cohesion: 0.21
Nodes (12): Error, InputValidationResult, Valid, AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview() (+4 more)

### Community 38 - "RailIconButton() / IconPillButton()"
Cohesion: 0.18
Nodes (12): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), CustomizeInputSheet(), CustomizeInputSheetPreview() (+4 more)

### Community 39 - "CanvasChallengePrompt() / ChallengeState"
Cohesion: 0.20
Nodes (10): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeFeedback, ChallengeState, FeedbackRow(), Modifier, PredictionPromptBody(), PromptHeader() (+2 more)

### Community 41 - "BufferItem / BufferVisualizer()"
Cohesion: 0.20
Nodes (11): BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), Color, Modifier, QueueCanvas(), QueueGate() (+3 more)

### Community 42 - "CellGrid() / CellItem()"
Cohesion: 0.27
Nodes (11): CellGrid(), CellItem(), Dp, LazyListState, Modifier, State, TextUnit, BottomPointerBadge() (+3 more)

### Community 43 - "InstrumentDeck() / StateDeckPage()"
Cohesion: 0.18
Nodes (11): InstrumentDeck(), InstrumentDeckPreview(), Modifier, State, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage() (+3 more)

### Community 44 - "ElementState / UI_GUIDELINES"
Cohesion: 0.35
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 45 - "Algorithm / VisualizerHeader()"
Cohesion: 0.29
Nodes (9): Algorithm, CompactHeaderPill(), HeaderActions(), HeaderOverflowMenuHost(), HeaderTitle(), Color, Modifier, VisualizerHeader() (+1 more)

### Community 46 - "BootController / BootControllerTest"
Cohesion: 0.31
Nodes (3): BootController, BootControllerEffect(), BootControllerTest

### Community 47 - "PredictionKind / FOUND_DECISION"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 48 - "UserPreferencesTest / .createMockPreferences()"
Cohesion: 0.27
Nodes (4): UserPreferencesTest, before, proxy, SharedPreferences

### Community 49 - "AlgoCard() / ProfileScreen()"
Cohesion: 0.25
Nodes (9): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, CatalogueRow(), Color, Modifier, ProfileScreen() (+1 more)

### Community 50 - "OnboardingScreen() / OnboardingSlideAiTutor()"
Cohesion: 0.28
Nodes (9): FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor(), OnboardingSlidePractice() (+1 more)

### Community 51 - "DeckPage / TASKS"
Cohesion: 0.28
Nodes (4): DeckPage, STATE, TRACE, AttachedDeckTabs()

### Community 52 - "MainActivity / .onCreate()"
Cohesion: 0.32
Nodes (6): MainActivity, Bundle, ComponentActivity, enableedgetoedge, installsplashscreen, setcontent

### Community 53 - "AlgorithmId / VisualizerOverlay"
Cohesion: 0.29
Nodes (3): Modifier, VisualizerOverlay, immutable

### Community 54 - "RecursionBands / RegionAuxiliary"
Cohesion: 0.32
Nodes (4): RegionAuxiliary, Modifier, RecursionBands, stroke

### Community 55 - "BufferOpEditor() / CustomizeBufferSheet()"
Cohesion: 0.39
Nodes (8): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), Color, OpRow(), OpRowData

### Community 56 - "RecursionTreeOverlay / .Content()"
Cohesion: 0.43
Nodes (3): Modifier, RecursionTreeOverlay, IntRange

### Community 57 - "UserPreferences / .init()"
Cohesion: 0.53
Nodes (3): android, UserPreferences, Context

### Community 58 - "CatalogueSortMode / COMPLEXITY"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "AlgorithmTheorySheet() / ComplexityBentoCard()"
Cohesion: 0.40
Nodes (6): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color, Modifier, PropertyBadge()

### Community 60 - "PhaseStrip / .StripContent()"
Cohesion: 0.40
Nodes (3): androidx, Modifier, PhaseStrip

### Community 61 - "VisualizerHeaderMenuTest / icons"
Cohesion: 0.33
Nodes (3): VisualizerHeaderMenuTest, icons, morevert

### Community 63 - "CellArrayVisualizer() / CellArrayVisualizerPreview()"
Cohesion: 0.40
Nodes (5): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State

### Community 64 - "CellGlowPainter / drawCellGlow()"
Cohesion: 0.60
Nodes (4): drawCellGlow(), Color, CornerRadius, drawscope

### Community 65 - "ChallengeQuestionType / challengeEligibleIndices()"
Cohesion: 0.40
Nodes (5): challengeEligibleIndices(), ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION

### Community 66 - ".resolve() / CodeLineAccent"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 67 - "StageLegend() / LegendEntry()"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "VisualizerAuxiliary / AuxiliarySlot"
Cohesion: 0.50
Nodes (3): AuxiliarySlot, BOTTOM, TOP

### Community 69 - "FlowchartShape / DECISION"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment / CENTER"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 71 - "BarVisualizer() / BarVisualizerPreview()"
Cohesion: 0.50
Nodes (4): BarVisualizer(), BarVisualizerPreview(), com, Modifier

## Knowledge Gaps
- **110 isolated node(s):** `KOTLIN`, `JAVA`, `PYTHON`, `CPP`, `BUBBLE_SORT` (+105 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 253 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep / buildPredictionQuestion()` to `ChallengeModeManager / CustomizeInputSheet`, `.generateStepsForAlgorithm() / AlgorithmStepRepository`, `TraceLanguage / AlgorithmCodeRegistry`, `HANDOVER / PRODUCT`, `VisualizerScreenStateTest / .makeState()`, `GraphTreeRenderer / WeightBadgeOverlay`, `AiChatBackendTest / FeatureEnhancementsTest`, `VisualizerScreenState / rememberVisualizerScreenState()`, `ComparisonBridgeOverlay / AlgoGlyphs`, `GraphNodeState / GraphTreeVisualizer()`, `VisualizerHost / AlgorithmSpec`, `.specFor() / AuxiliaryComponentTest`, `CanvasChallengePrompt() / ChallengeState`, `BufferItem / BufferVisualizer()`, `CellGrid() / CellItem()`, `InstrumentDeck() / StateDeckPage()`, `ElementState / UI_GUIDELINES`, `Algorithm / VisualizerHeader()`, `AlgorithmId / VisualizerOverlay`, `RecursionBands / RegionAuxiliary`, `RecursionTreeOverlay / .Content()`, `PhaseStrip / .StripContent()`, `MergeBufferRow / .BufferContent()`, `CellArrayVisualizer() / CellArrayVisualizerPreview()`, `ChallengeQuestionType / challengeEligibleIndices()`, `VisualizerAuxiliary / AuxiliarySlot`, `BarVisualizer() / BarVisualizerPreview()`?**
  _High betweenness centrality (0.113) - this node is a cross-community bridge._
- **Why does `AlgoTokens` connect `ChallengeModeManager / CustomizeInputSheet` to `.resolve() / CodeLineAccent`, `SwapFlight / Theme`, `HANDOVER / PRODUCT`, `Instrument / DoubleBezelShell()`, `AlgoLensApp / AppShell()`, `ChatScreen / ChatScreen()`, `ElementState / UI_GUIDELINES`, `DeckPage / TASKS`, `GlassPanel / ChallengeGlowTargets`, `RecursionBands / RegionAuxiliary`, `ComparisonBridgeOverlay / AlgoGlyphs`, `VisualizerScreen / VisualizerScreen()`, `AviaLogo / AviaLogo()`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId / AlgorithmRegistry` to `ChallengeModeManager / CustomizeInputSheet`, `.specFor() / AuxiliaryComponentTest`, `PracticeSessionManager / PracticeSessionState`, `VisualizerStep / buildPredictionQuestion()`, `.generateStepsForAlgorithm() / AlgorithmStepRepository`, `AlgoLensTheme() / CustomizeGraphSheet()`, `HANDOVER / PRODUCT`, `CanvasChallengePrompt() / ChallengeState`, `ChatScreen / ChatScreen()`, `ChatSessionState / AiChatEngine`, `Algorithm / VisualizerHeader()`, `AiChatBackendTest / FeatureEnhancementsTest`, `AlgorithmId / VisualizerOverlay`, `AiChatEngine / ChatMessage`, `VisualizerHost / AlgorithmSpec`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **What connects `KOTLIN`, `JAVA`, `PYTHON` to the rest of the system?**
  _110 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChallengeModeManager / CustomizeInputSheet` be split into smaller, more focused modules?**
  _Cohesion score 0.09826398456875172 - nodes in this community are weakly interconnected._
- **Should `VisualizerStep / buildPredictionQuestion()` be split into smaller, more focused modules?**
  _Cohesion score 0.12653061224489795 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager / PracticeSessionState` be split into smaller, more focused modules?**
  _Cohesion score 0.07536231884057971 - nodes in this community are weakly interconnected._