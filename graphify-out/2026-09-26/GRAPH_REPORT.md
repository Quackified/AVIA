# Graph Report - AlgoLens  (2026-09-26)

## Corpus Check
- 128 files · ~179,868 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1414 nodes · 4860 edges · 87 communities (68 shown, 19 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 81 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b4157032`
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
- ChatScreen
- ChatSessionState.kt
- GraphTreeRenderer.kt
- test
- UElementHandler
- UElementHandler
- AlgorithmId
- ComparisonBridgeOverlay.kt
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AccountTemplateCard.kt
- StepToken
- VisualizerScreenState
- ChallengeModeManager.kt
- AiChatEngine
- GraphNodeState
- VisualizerHost.kt
- .processQuery
- BootController
- VisualizerScreen.kt
- AviaLogo.kt
- FakeAuthRepository
- .specFor
- setvalue
- GlassPanel.kt
- DashboardScreen
- Algorithm
- RailIconButton
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- BufferItem
- CellGrid.kt
- InstrumentDeck
- AlgoLensDetectorsTest
- Offset
- RecursionTreeOverlay
- PredictionKind
- UserPreferencesTest
- ProfileScreen.kt
- EditProfileSheet
- AlgoLensTheme
- CellGlowPainter.kt
- ElementState
- RegionAuxiliary
- BufferOpEditor
- CustomizeGraphSheet
- OnboardingScreen
- CatalogueSortMode
- AlgorithmTheorySheet
- Dp
- VisualizerHeaderMenuTest
- AppShell
- tracelanguage
- MainActivity.kt
- SectionLabel
- DeckPage
- StageLegend
- Type.kt
- FlowchartShape
- TableAlignment
- GraphCustomization
- ExampleUnitTest
- Profile and Edit Profile
- .resolve
- RootStage
- Color
- Color
- PaddingValues
- TextUnit
- SettingsScreen
- ImageVector

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 99 edges
2. `AlgoLensTheme()` - 54 edges
3. `AlgoTokens` - 53 edges
4. `AlgorithmId` - 53 edges
5. `VisualizerScreenState` - 46 edges
6. `AlgoType` - 38 edges
7. `buildPredictionQuestion()` - 33 edges
8. `AlgoGlyphs` - 30 edges
9. `ChallengeModeManagerTest` - 30 edges
10. `VisualizerScreenStateTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `AccountStatusCard()` --calls--> `DoubleBezelShell()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt → app/src/main/java/com/example/algolens/ui/components/Instrument.kt
- `EditProfileSheet()` --calls--> `ProfileTheme()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt → app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt
- `EditProfileSheet()` --calls--> `ProfileTopBar()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt → app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt
- `ProfileScreen()` --calls--> `AlgoCard()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt → app/src/main/java/com/example/algolens/ui/components/AlgoCard.kt
- `ProfileScreen()` --calls--> `DoubleBezelShell()`  [INFERRED]
  app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt → app/src/main/java/com/example/algolens/ui/components/Instrument.kt

## Import Cycles
- None detected.

## Communities (87 total, 19 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.09
Nodes (146): accentgreen, accentorange, accentpink, accentred, accentyellow, algotokens, algotype, Alignment (+138 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.15
Nodes (7): buildPredictionQuestion(), graphVisitQuestion(), highlightedCellPair(), predictionAnswerFor(), quickSortQuestion(), VisualizerStep, ChallengeModeManagerTest

### Community 2 - "PracticeSessionManager"
Cohesion: 0.08
Nodes (21): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+13 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.06
Nodes (40): abs, activity, animatable, animatecontentsize, animationspec, animationvector1d, Dp, Modifier (+32 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.09
Nodes (13): AlgorithmStepRepository, BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp (+5 more)

### Community 5 - "TraceLanguage"
Cohesion: 0.09
Nodes (24): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON, CodeListing() (+16 more)

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.22
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.14
Nodes (24): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+16 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.07
Nodes (26): activityscenariorule, algolenstheme, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark (+18 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "ChatScreen"
Cohesion: 0.29
Nodes (12): AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), ComplexityMatrixBlock(), ComplexityPill(), Color (+4 more)

### Community 13 - "ChatSessionState.kt"
Cohesion: 0.12
Nodes (16): ChatAction, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus, COMPLETE, ERROR (+8 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.15
Nodes (14): Modifier, WeightBadgeOverlay, atan2, canvas, cos, graphedgedefault, nativecanvas, paint (+6 more)

### Community 15 - "test"
Cohesion: 0.21
Nodes (13): assertequals, assertfalse, assertnotnull, assertnull, asserttrue, experimentalcoroutinesapi, icons, morevert (+5 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 18 - "AlgorithmId"
Cohesion: 0.08
Nodes (22): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP (+14 more)

### Community 19 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.21
Nodes (11): animatefloatasstate, ImageVector, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, Color, path, pathbuilder (+3 more)

### Community 20 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+5 more)

### Community 21 - "AccountTemplateCard.kt"
Cohesion: 0.13
Nodes (16): activityresultcontracts, ProfileField(), contentdescription, focusdirection, imagebitmap, ImeAction, intent, keyboardactions (+8 more)

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 24 - "ChallengeModeManager.kt"
Cohesion: 0.14
Nodes (31): bufferPushPopQuestion(), CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT (+23 more)

### Community 25 - "AiChatEngine"
Cohesion: 0.19
Nodes (8): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter, AlgorithmTheoryData, AlgorithmTheoryRepository, uuid

### Community 26 - "GraphNodeState"
Cohesion: 0.17
Nodes (10): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphNodeState, Pointer, VisualizerRenderMode, BARS, BUFFER (+2 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.16
Nodes (18): AlgorithmSpec, ArrayViewMode, BARS, CELLS, CellArrayVisualizer(), CellArrayVisualizerPreview(), com, Modifier (+10 more)

### Community 29 - "BootController"
Cohesion: 0.23
Nodes (6): BootController, BootControllerEffect(), AlgorithmicWaveLoader(), BootOverlay(), Modifier, BootControllerTest

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.19
Nodes (13): animatedpasstate, AiTutorSheet(), GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier, EmptyCanvas(), Modifier, VisualizerScreen() (+5 more)

### Community 31 - "AviaLogo.kt"
Cohesion: 0.36
Nodes (7): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource

### Community 32 - "FakeAuthRepository"
Cohesion: 0.06
Nodes (16): AuthAccountState, AuthRepository, CredentialValidator, Error, FakeAuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT (+8 more)

### Community 34 - "setvalue"
Cohesion: 0.20
Nodes (13): AppSettings, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, chathistoryrepository, delay, launchedeffect, mutablefloatstateof (+5 more)

### Community 35 - "GlassPanel.kt"
Cohesion: 0.18
Nodes (16): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+8 more)

### Community 36 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 37 - "Algorithm"
Cohesion: 0.20
Nodes (13): SampleData, Algorithm, AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, CompactHeaderPill(), HeaderActions() (+5 more)

### Community 38 - "RailIconButton"
Cohesion: 0.18
Nodes (12): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), CustomizeInputSheet(), CustomizeInputSheetPreview() (+4 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement, Vendor (+5 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "BufferItem"
Cohesion: 0.20
Nodes (11): BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview(), CapacityIndicator(), Color, Modifier, QueueCanvas(), QueueGate() (+3 more)

### Community 42 - "CellGrid.kt"
Cohesion: 0.15
Nodes (20): accentpinkglow, CellGrid(), CellItem(), Dp, LazyListState, Modifier, State, TextUnit (+12 more)

### Community 43 - "InstrumentDeck"
Cohesion: 0.18
Nodes (11): InstrumentDeck(), InstrumentDeckPreview(), Modifier, State, Modifier, MemoryCallStackReadout(), StackInfoBadge(), StateDeckPage() (+3 more)

### Community 44 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 45 - "Offset"
Cohesion: 0.22
Nodes (12): GraphBuilderBanner(), GraphBuilderGestures(), GraphBuilderToolbar(), Modifier, GraphCanvasGeometry, GraphTreeRenderer(), Modifier, GraphTreeVisualizer() (+4 more)

### Community 46 - "RecursionTreeOverlay"
Cohesion: 0.25
Nodes (5): Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 47 - "PredictionKind"
Cohesion: 0.20
Nodes (10): PredictionKind, FOUND_DECISION, SELECT_COMPARE_PAIR, SELECT_PIVOT, SELECT_VISIT_NODE, SWAP_DECISION, WILL_DEQUEUE, WILL_ENQUEUE (+2 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.11
Nodes (21): AccountStatusCard(), AuthRepository, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, authaccountstate (+13 more)

### Community 50 - "EditProfileSheet"
Cohesion: 0.21
Nodes (10): EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileEditorDraft, ProfileTheme(), ProfileTopBar() (+2 more)

### Community 51 - "AlgoLensTheme"
Cohesion: 0.23
Nodes (7): ProfileFlowTest, AlgoLensTheme(), AiTutorSheetPreview(), BarVisualizer(), BarVisualizerPreview(), com, Modifier

### Community 52 - "CellGlowPainter.kt"
Cohesion: 0.60
Nodes (4): drawCellGlow(), Color, CornerRadius, drawscope

### Community 53 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

### Community 54 - "RegionAuxiliary"
Cohesion: 0.11
Nodes (11): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+3 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.39
Nodes (8): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), Color, OpRow(), OpRowData

### Community 56 - "CustomizeGraphSheet"
Cohesion: 0.20
Nodes (11): Error, InputValidationResult, Valid, CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), Color (+3 more)

### Community 57 - "OnboardingScreen"
Cohesion: 0.28
Nodes (9): FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor(), OnboardingSlidePractice() (+1 more)

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "AlgorithmTheorySheet"
Cohesion: 0.40
Nodes (6): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color, Modifier, PropertyBadge()

### Community 62 - "AppShell"
Cohesion: 0.21
Nodes (10): Algorithm, android, UserPreferences, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Modifier, BootController (+2 more)

### Community 64 - "MainActivity.kt"
Cohesion: 0.32
Nodes (6): MainActivity, Bundle, ComponentActivity, enableedgetoedge, installsplashscreen, setcontent

### Community 65 - "SectionLabel"
Cohesion: 0.36
Nodes (8): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel()

### Community 66 - "DeckPage"
Cohesion: 0.29
Nodes (4): DeckPage, STATE, TRACE, AttachedDeckTabs()

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "Type.kt"
Cohesion: 0.33
Nodes (5): font, r, TextStyle, TextUnit, typography

### Community 69 - "FlowchartShape"
Cohesion: 0.50
Nodes (4): FlowchartShape, DECISION, PROCESS, TERMINAL

### Community 70 - "TableAlignment"
Cohesion: 0.50
Nodes (4): TableAlignment, CENTER, LEFT, RIGHT

### Community 71 - "GraphCustomization"
Cohesion: 0.40
Nodes (4): ForBst, ForHeap, ForTraversal, GraphCustomization

### Community 78 - "Profile and Edit Profile"
Cohesion: 0.40
Nodes (4): Direction contract, Feature opportunities, Profile and Edit Profile, Scope and constraints

### Community 79 - ".resolve"
Cohesion: 0.60
Nodes (3): CodeLineAccent, Family, Color

### Community 80 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 85 - "SettingsScreen"
Cohesion: 0.67
Nodes (4): Modifier, SettingsCard(), SettingsScreen(), SettingsScreenPreview()

## Knowledge Gaps
- **135 isolated node(s):** `ArrayPreset`, `TourStep`, `LaunchVisualizer`, `QueryFollowUp`, `Family` (+130 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 317 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **19 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AlgorithmId` connect `AlgorithmId` to `ChatScreen.kt`, `.specFor`, `PracticeSessionManager`, `VisualizerStep`, `.generateStepsForAlgorithm`, `Algorithm`, `ChatSessionState.kt`, `test`, `ChallengeModeManager.kt`, `AiChatEngine`, `CustomizeGraphSheet`, `VisualizerHost.kt`?**
  _High betweenness centrality (0.101) - this node is a cross-community bridge._
- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `TraceLanguage`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `GraphTreeRenderer.kt`, `test`, `ComparisonBridgeOverlay.kt`, `VisualizerScreenState`, `ChallengeModeManager.kt`, `GraphNodeState`, `VisualizerHost.kt`, `.specFor`, `Algorithm`, `BufferItem`, `CellGrid.kt`, `InstrumentDeck`, `Offset`, `RecursionTreeOverlay`, `AlgoLensTheme`, `RegionAuxiliary`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Why does `CodeListing()` connect `TraceLanguage` to `ChatScreen.kt`, `.resolve`?**
  _High betweenness centrality (0.051) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `AlgoLensTheme()` (e.g. with `.editorRestoresDraftAndRequiresExplicitDiscard()` and `.narrowLargeTextEditorRemainsUsable()`) actually correct?**
  _`AlgoLensTheme()` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `ArrayPreset`, `TourStep`, `LaunchVisualizer` to the rest of the system?**
  _135 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.09140893470790377 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager` be split into smaller, more focused modules?**
  _Cohesion score 0.07610993657505286 - nodes in this community are weakly interconnected._