# Graph Report - AlgoLens  (2026-09-26)

## Corpus Check
- 128 files · ~179,461 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1412 nodes · 4860 edges · 79 communities (61 shown, 18 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 73 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `67004298`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChatScreen.kt
- buildPredictionQuestion
- ElementState
- SwapFlight.kt
- .generateStepsForAlgorithm
- TraceLanguage
- CellArrayVisualizer
- VisualizerScreenStateTest
- ChatMarkdownMessage
- Instrument.kt
- ProfileFlowTest.kt
- NavTab
- ChatScreen
- ChatSessionState.kt
- GraphTreeRenderer.kt
- test
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgorithmId
- ComparisonBridgeOverlay.kt
- AlgorithmId.kt
- AccountTemplateCard.kt
- StepToken
- VisualizerScreenState
- VisualizerStep
- AiChatEngine
- GraphNodeState
- VisualizerHost.kt
- .processQuery
- BootOverlay.kt
- VisualizerScreen.kt
- AviaLogo.kt
- FakeAuthRepository
- .specFor
- getvalue
- ChallengeGlowTargets.kt
- DashboardScreen
- AlgoLensTheme
- Algorithm
- CanvasChallengePrompt
- ChatSessionManager
- BufferItem
- CellGrid.kt
- InstrumentDeck
- parseFlowchart
- GlassPanel.kt
- RecursionTreeOverlay
- PredictionKind
- UserPreferencesTest
- ProfileScreen.kt
- EditProfileSheet
- Theme.kt
- CellGlowPainter.kt
- RecursionBands.kt
- RegionAuxiliary
- BufferOpEditor
- InputValidationResult
- UserPreferences.kt
- CatalogueSortMode
- AlgorithmTheorySheet
- Dp
- VisualizerHeaderMenuTest
- AlgoLensApp.kt
- tracelanguage
- ChallengeQuestionType
- StageLegend
- FlowchartShape
- TableAlignment
- ExampleUnitTest
- Profile and Edit Profile
- Color
- Color
- PaddingValues
- TextUnit

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 99 edges
2. `AlgoLensTheme()` - 55 edges
3. `AlgoTokens` - 53 edges
4. `AlgorithmId` - 53 edges
5. `VisualizerScreenState` - 46 edges
6. `AlgoType` - 38 edges
7. `buildPredictionQuestion()` - 33 edges
8. `AlgoGlyphs` - 32 edges
9. `ChallengeModeManagerTest` - 30 edges
10. `VisualizerScreenStateTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `EditProfileSheet()` --calls--> `ProfileTheme()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt → app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt
- `EditProfileSheet()` --calls--> `ProfileTopBar()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt → app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt
- `AppShell()` --calls--> `PracticeScreen()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt → app/src/main/java/com/example/algolens/ui/practice/PracticeScreen.kt
- `PracticeScreen()` --calls--> `PracticeVisualCanvas()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/practice/PracticeScreen.kt → app/src/main/java/com/example/algolens/ui/practice/PracticeVisualCanvas.kt
- `AccountStatusCard()` --calls--> `DoubleBezelShell()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt → app/src/main/java/com/example/algolens/ui/components/Instrument.kt

## Import Cycles
- None detected.

## Communities (79 total, 18 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.10
Nodes (135): accentgreen, accentorange, accentpink, accentred, accentyellow, algotokens, algotype, Alignment (+127 more)

### Community 1 - "buildPredictionQuestion"
Cohesion: 0.17
Nodes (3): buildPredictionQuestion(), predictionAnswerFor(), ChallengeModeManagerTest

### Community 2 - "ElementState"
Cohesion: 0.06
Nodes (31): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+23 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.12
Nodes (23): animationspec, animationvector1d, animatedSlotSwap(), findSwappedPairs(), FlightSpec, Dp, Modifier, State (+15 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.09
Nodes (12): AlgorithmStepRepository, BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp (+4 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.09
Nodes (24): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+16 more)

### Community 6 - "CellArrayVisualizer"
Cohesion: 0.13
Nodes (13): CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier, State, OffscreenPointerTarget, LazyListState, Modifier (+5 more)

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.14
Nodes (16): CalloutType, CAUTION, IMPORTANT, NOTE, TIP, WARNING, ChatCalloutBlock(), ChatEmbeddedCodeBlock() (+8 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.06
Nodes (54): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+46 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.05
Nodes (33): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, MainActivity, BootController, BootControllerEffect(), BootControllerTest (+25 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "ChatScreen"
Cohesion: 0.29
Nodes (12): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), ComplexityMatrixBlock(), ComplexityPill(), Color (+4 more)

### Community 13 - "ChatSessionState.kt"
Cohesion: 0.11
Nodes (21): ChatAction, ChatCodeSnippet, ChatPromptStarter, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus (+13 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.13
Nodes (16): GraphCanvasGeometry, GraphTreeRenderer(), Modifier, Modifier, atan2, cos, graphedgedefault, immutable (+8 more)

### Community 15 - "test"
Cohesion: 0.20
Nodes (14): SampleData, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, experimentalcoroutinesapi, icons (+6 more)

### Community 16 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.07
Nodes (30): CodeLineAccent, Family, Color, AlgoLensIssueRegistry, callName(), HardcodedHexColorDetector, UElementHandler, hasDpLiteralArg() (+22 more)

### Community 17 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.07
Nodes (31): category, implementation, AlgoLensIssueRegistry, callName(), HardcodedHexColorDetector, UElementHandler, hasDpLiteralArg(), hasHexLiteralArg() (+23 more)

### Community 18 - "AlgorithmId"
Cohesion: 0.11
Nodes (15): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP (+7 more)

### Community 19 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.17
Nodes (13): animatefloatasstate, ImageVector, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, Color, path (+5 more)

### Community 20 - "AlgorithmId.kt"
Cohesion: 0.16
Nodes (10): InputKind, ARRAY, NONE, VisualizerFamily, BUFFER, GRAPH_2D, LINEAR_1D, Modifier (+2 more)

### Community 21 - "AccountTemplateCard.kt"
Cohesion: 0.12
Nodes (18): activityresultcontracts, ProfileAvatar(), ProfileField(), contentdescription, focusdirection, imagebitmap, ImeAction, intent (+10 more)

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 23 - "VisualizerScreenState"
Cohesion: 0.11
Nodes (5): DeckPage, STATE, TRACE, rememberVisualizerScreenState(), VisualizerScreenState

### Community 24 - "VisualizerStep"
Cohesion: 0.20
Nodes (22): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), highlightedCellPair(), Indices, insertionSortQuestion() (+14 more)

### Community 25 - "AiChatEngine"
Cohesion: 0.21
Nodes (4): AiChatEngine, ChatComplexitySnapshot, ChatMessage, AlgorithmTheoryData

### Community 26 - "GraphNodeState"
Cohesion: 0.12
Nodes (19): GraphBuilderBanner(), GraphBuilderGestures(), GraphBuilderToolbar(), Modifier, GraphTreeVisualizer(), GraphTreeVisualizerPreview(), Modifier, GraphVisualizer() (+11 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.17
Nodes (17): AlgorithmSpec, ArrayViewMode, BARS, CELLS, BarVisualizer(), BarVisualizerPreview(), com, Modifier (+9 more)

### Community 29 - "BootOverlay.kt"
Cohesion: 0.27
Nodes (9): AlgorithmicWaveLoader(), BootOverlay(), Modifier, fastoutslowineasing, lineareasing, mutableinteractionsource, pi, rotate (+1 more)

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.21
Nodes (12): animatedpasstate, AiTutorSheet(), CustomizeInputSheet(), CustomizeInputSheetPreview(), EmptyCanvas(), Modifier, VisualizerScreen(), VisualizerScreenPreview() (+4 more)

### Community 31 - "AviaLogo.kt"
Cohesion: 0.18
Nodes (12): AviaLogo(), Color, Dp, Modifier, colorfilter, font, image, painterresource (+4 more)

### Community 32 - "FakeAuthRepository"
Cohesion: 0.06
Nodes (17): ProfileFlowTest, AuthAccountState, AuthRepository, CredentialValidator, Error, FakeAuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE (+9 more)

### Community 34 - "getvalue"
Cohesion: 0.22
Nodes (13): AppSettings, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, chathistoryrepository, getvalue, launchedeffect, mutablefloatstateof (+5 more)

### Community 35 - "ChallengeGlowTargets.kt"
Cohesion: 0.28
Nodes (8): animatefloat, challengeTargetColorTriple(), Color, State, rememberChallengePulseState(), infiniterepeatable, rememberinfinitetransition, repeatmode

### Community 36 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.16
Nodes (14): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, AlgoLensTheme(), AiTutorSheetPreview(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview() (+6 more)

### Community 38 - "Algorithm"
Cohesion: 0.17
Nodes (17): Algorithm, IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), Modifier (+9 more)

### Community 39 - "CanvasChallengePrompt"
Cohesion: 0.28
Nodes (7): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), ChallengeFeedback, ChallengeState, FeedbackRow(), Modifier, PromptHeader()

### Community 41 - "BufferItem"
Cohesion: 0.18
Nodes (11): BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), Color, Modifier, QueueCanvas(), QueueGate() (+3 more)

### Community 42 - "CellGrid.kt"
Cohesion: 0.14
Nodes (21): abs, accentpinkglow, animatable, CellGrid(), CellItem(), Dp, LazyListState, Modifier (+13 more)

### Community 43 - "InstrumentDeck"
Cohesion: 0.40
Nodes (5): AttachedDeckTabs(), InstrumentDeck(), InstrumentDeckPreview(), Modifier, State

### Community 44 - "parseFlowchart"
Cohesion: 0.31
Nodes (8): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart()

### Community 45 - "GlassPanel.kt"
Cohesion: 0.46
Nodes (7): AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape

### Community 46 - "RecursionTreeOverlay"
Cohesion: 0.43
Nodes (3): Modifier, RecursionTreeOverlay, IntRange

### Community 47 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.12
Nodes (20): AuthRepository, ImageVector, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), ProfileTheme(), ProfileTopBar() (+12 more)

### Community 50 - "EditProfileSheet"
Cohesion: 0.28
Nodes (7): AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileEditorDraft, UserProfile

### Community 51 - "Theme.kt"
Cohesion: 0.14
Nodes (14): activity, animatecontentsize, Dp, Modifier, smoothPanelExpansion(), Spacing, composed, darkcolorscheme (+6 more)

### Community 52 - "CellGlowPainter.kt"
Cohesion: 0.47
Nodes (5): drawCellGlow(), Color, CornerRadius, drawscope, stroke

### Community 53 - "RecursionBands.kt"
Cohesion: 0.50
Nodes (3): Modifier, RecursionBands, canvas

### Community 54 - "RegionAuxiliary"
Cohesion: 0.14
Nodes (9): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+1 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.39
Nodes (8): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), Color, OpRow(), OpRowData

### Community 56 - "InputValidationResult"
Cohesion: 0.18
Nodes (10): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Color (+2 more)

### Community 57 - "UserPreferences.kt"
Cohesion: 0.53
Nodes (3): android, UserPreferences, Context

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "AlgorithmTheorySheet"
Cohesion: 0.40
Nodes (6): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color, Modifier, PropertyBadge()

### Community 62 - "AlgoLensApp.kt"
Cohesion: 0.15
Nodes (18): Algorithm, animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Modifier, RootStage, APP (+10 more)

### Community 65 - "ChallengeQuestionType"
Cohesion: 0.40
Nodes (5): challengeEligibleIndices(), ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

## Knowledge Gaps
- **135 isolated node(s):** `ArrayPreset`, `TourStep`, `LaunchVisualizer`, `QueryFollowUp`, `BufferSnapshot` (+130 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 314 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **18 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `buildPredictionQuestion`, `.generateStepsForAlgorithm`, `TraceLanguage`, `CellArrayVisualizer`, `VisualizerScreenStateTest`, `Instrument.kt`, `GraphTreeRenderer.kt`, `test`, `ComparisonBridgeOverlay.kt`, `AlgorithmId.kt`, `VisualizerScreenState`, `GraphNodeState`, `VisualizerHost.kt`, `Algorithm`, `CanvasChallengePrompt`, `BufferItem`, `CellGrid.kt`, `InstrumentDeck`, `RecursionTreeOverlay`, `RecursionBands.kt`, `RegionAuxiliary`, `ChallengeQuestionType`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `.specFor`, `ElementState`, `buildPredictionQuestion`, `.generateStepsForAlgorithm`, `AlgoLensTheme`, `CanvasChallengePrompt`, `ChatSessionState.kt`, `test`, `AlgorithmId.kt`, `VisualizerStep`, `AiChatEngine`, `GraphNodeState`, `VisualizerHost.kt`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChatScreen.kt`, `main/com/example/algolens/lint/AlgoLensIssueRegistry.kt`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **What connects `ArrayPreset`, `TourStep`, `LaunchVisualizer` to the rest of the system?**
  _135 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.10288520564763659 - nodes in this community are weakly interconnected._
- **Should `ElementState` be split into smaller, more focused modules?**
  _Cohesion score 0.058001397624039136 - nodes in this community are weakly interconnected._
- **Should `SwapFlight.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.12307692307692308 - nodes in this community are weakly interconnected._