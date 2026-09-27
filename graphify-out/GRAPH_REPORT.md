# Graph Report - AlgoLens  (2026-09-26)

## Corpus Check
- 129 files · ~183,298 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1595 nodes · 5369 edges · 102 communities (79 shown, 23 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 110 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `0e3c5387`
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
- test
- UElementHandler
- callName
- AlgorithmId
- AlgoGlyphs.kt
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- ProjectConstraintBrief
- StepToken
- VisualizerScreenState
- FeatureEnhancementsTest
- ChatSessionState.kt
- GraphNodeState
- VisualizerHost.kt
- .processQuery
- BootController
- VisualizerScreen.kt
- AviaLogo.kt
- FakeAuthRepository
- AccountTemplateCard.kt
- VisualizerScreenState.kt
- GlassPanel.kt
- DashboardScreen
- AlgoCard
- RailIconButton
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- AlgorithmStepRepository.kt
- CellGrid.kt
- StateDeckPage
- AlgoLensDetectorsTest
- ChatConversation
- RecursionTreeOverlay
- ChallengeModeManagerTest.kt
- UserPreferencesTest
- ProfileScreen.kt
- AlgoLensTheme
- AiChatBackendTest.kt
- Offset
- VisualizerHeader
- RegionAuxiliary
- BufferOpEditor
- InputValidationResult
- OnboardingScreen
- CatalogueSortMode
- EditProfileSheet
- Dp
- CodeListing
- AlgoLensApp.kt
- InstrumentDeck
- ChatScreen
- SectionLabel
- UElementHandler
- StageLegend
- ElementState
- FlowchartShape
- TableAlignment
- UserPreferences.kt
- ExampleUnitTest
- Profile and Edit Profile
- VisualizerBenchmarks.kt
- MainActivity.kt
- Color
- Color
- PaddingValues
- TextUnit
- GraphTreeRenderer
- ImageVector
- ArrayViewMode
- Color
- imepadding
- ComparisonBridgeOverlay.kt
- Type.kt
- WeightBadgeOverlay
- GraphCustomization
- ExploreCatalogScreen
- RootStage
- VisualizerOverlay
- GuidedTourOverlay
- Dp
- PaddingValues
- Shape
- instrumentmeter

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 83 edges
2. `AlgoLensTheme()` - 48 edges
3. `AlgorithmId` - 46 edges
4. `AlgoTokens` - 42 edges
5. `ChatSessionManager` - 39 edges
6. `VisualizerScreenState` - 38 edges
7. `buildPredictionQuestion()` - 33 edges
8. `ChallengeModeManagerTest` - 31 edges
9. `VisualizerScreenStateTest` - 31 edges
10. `AlgoType` - 30 edges

## Surprising Connections (you probably didn't know these)
- `Step-by-step implementation plan` --references--> `AlgoGlyphs`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/components/AlgoGlyphs.kt
- `Graph/tree pipeline` --references--> `GraphCanvasGeometry`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/visualizer/GraphTreeRenderer.kt
- `Explore/Practice and Settings` --references--> `PracticeSessionManager`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/practice/PracticeSessionState.kt
- `Graph/tree pipeline` --references--> `AlgorithmRegistry`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/data/AlgorithmRegistry.kt
- `Graph/tree pipeline` --references--> `AlgorithmStepRepository`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/data/AlgorithmStepRepository.kt

## Import Cycles
- None detected.

## Communities (102 total, 23 thin omitted)

### Community 0 - "ChatScreen.kt"
Cohesion: 0.08
Nodes (173): accentgreen, accentorange, accentpink, accentred, accentyellow, alertdialog, algohairline, algolenstheme (+165 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.18
Nodes (5): buildPredictionQuestion(), highlightedCellPair(), predictionAnswerFor(), VisualizerStep, ChallengeModeManagerTest

### Community 2 - "PracticeSessionManager"
Cohesion: 0.08
Nodes (22): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+14 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.06
Nodes (40): abs, activity, animatable, animatecontentsize, animationspec, animationvector1d, Dp, Modifier (+32 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.15
Nodes (5): AlgorithmStepRepository, SortOrder, ASC, DESC, AlgorithmStepRepositoryTest

### Community 5 - "TraceLanguage"
Cohesion: 0.32
Nodes (7): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.22
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 7 - "VisualizerScreenStateTest"
Cohesion: 0.07
Nodes (14): VisualizerHeaderMenuTest, VisualizerScreenStateTest, Documentation and inspection basis, Explore/Practice and Settings, Files/components likely affected, Goal, Graph/tree pipeline, Header and Chat precedent (+6 more)

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.18
Nodes (17): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+9 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.11
Nodes (18): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, Bitmap, compositionlocalprovider, createandroidcomposerule (+10 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "PredictionQuestion"
Cohesion: 0.18
Nodes (20): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), mergeSortQuestion() (+12 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.20
Nodes (12): Modifier, atan2, canvas, cos, graphedgedefault, nativecanvas, paint, patheffect (+4 more)

### Community 15 - "test"
Cohesion: 0.23
Nodes (12): SampleData, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, icons, morevert (+4 more)

### Community 16 - "UElementHandler"
Cohesion: 0.17
Nodes (12): callName(), UElementHandler, hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), JavaContext, UCallExpression, UElementHandler (+4 more)

### Community 17 - "callName"
Cohesion: 0.19
Nodes (9): CodeLineAccent, Family, Color, callName(), hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), UCallExpression (+1 more)

### Community 18 - "AlgorithmId"
Cohesion: 0.07
Nodes (26): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP (+18 more)

### Community 19 - "AlgoGlyphs.kt"
Cohesion: 0.18
Nodes (9): CompactDropdownMenuItem(), ComplexityPill(), androidx, ImageVector, Color, path, pathbuilder, strokecap (+1 more)

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
Nodes (5): DeckPage, STATE, TRACE, rememberVisualizerScreenState(), VisualizerScreenState

### Community 25 - "ChatSessionState.kt"
Cohesion: 0.08
Nodes (30): AiChatEngine, ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, ChatPromptStarter (+22 more)

### Community 26 - "GraphNodeState"
Cohesion: 0.16
Nodes (11): GraphVisualizer(), GraphVisualizerPreview(), Modifier, GraphEdgeState, GraphNodeState, Pointer, VisualizerRenderMode, BARS (+3 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.06
Nodes (54): BarVisualizer(), BarVisualizerPreview(), com, Modifier, BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview() (+46 more)

### Community 29 - "BootController"
Cohesion: 0.23
Nodes (6): BootController, BootControllerEffect(), AlgorithmicWaveLoader(), BootOverlay(), Modifier, BootControllerTest

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.11
Nodes (21): animatedpasstate, Algorithm, AiTutorSheet(), AiTutorSheetPreview(), AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color (+13 more)

### Community 31 - "AviaLogo.kt"
Cohesion: 0.36
Nodes (7): AviaLogo(), Color, Dp, Modifier, colorfilter, image, painterresource

### Community 32 - "FakeAuthRepository"
Cohesion: 0.06
Nodes (22): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, AuthRepository, CredentialValidator, Error, FakeAuthRepository (+14 more)

### Community 33 - "AccountTemplateCard.kt"
Cohesion: 0.13
Nodes (16): activityresultcontracts, ProfileField(), contentdescription, focusdirection, imagebitmap, ImeAction, intent, keyboardactions (+8 more)

### Community 34 - "VisualizerScreenState.kt"
Cohesion: 0.33
Nodes (6): AppSettings, delay, launchedeffect, mutablefloatstateof, mutablelongstateof, stable

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "DashboardScreen"
Cohesion: 0.21
Nodes (13): CatalogueGroupHeader(), CatalogueSection, CategoryChip(), CategoryChipSpec, complexityRank(), complexityTierTitle(), DashboardScreen(), DashboardScreenPreview() (+5 more)

### Community 37 - "AlgoCard"
Cohesion: 0.50
Nodes (4): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier

### Community 38 - "RailIconButton"
Cohesion: 0.20
Nodes (11): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), CustomizeInputSheet(), CustomizeInputSheetPreview() (+3 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.19
Nodes (15): category, implementation, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner (+7 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "AlgorithmStepRepository.kt"
Cohesion: 0.16
Nodes (9): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp, BufferItem (+1 more)

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
Cohesion: 0.43
Nodes (3): Modifier, RecursionTreeOverlay, IntRange

### Community 47 - "ChallengeModeManagerTest.kt"
Cohesion: 0.09
Nodes (22): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+14 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.13
Nodes (19): Algorithm, AuthRepository, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, authaccountstate (+11 more)

### Community 50 - "AlgoLensTheme"
Cohesion: 0.26
Nodes (7): ProfileFlowTest, AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview(), CustomizeGraphSheetTraversalPreview(), StartNodeDropdown()

### Community 51 - "AiChatBackendTest.kt"
Cohesion: 0.14
Nodes (12): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), experimentalcoroutinesapi, flowchartshape, parseflowchart (+4 more)

### Community 52 - "Offset"
Cohesion: 0.38
Nodes (6): drawCellGlow(), Color, CornerRadius, drawscope, Offset, stroke

### Community 53 - "VisualizerHeader"
Cohesion: 0.27
Nodes (14): CompactDropdownMenuItem(), CompactHeaderPill(), HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm, AlgorithmSpec (+6 more)

### Community 54 - "RegionAuxiliary"
Cohesion: 0.11
Nodes (11): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+3 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 56 - "InputValidationResult"
Cohesion: 0.33
Nodes (6): Error, InputValidationResult, Valid, Color, SearchKeyField(), ValuesField()

### Community 57 - "OnboardingScreen"
Cohesion: 0.28
Nodes (9): FeatureChip(), Modifier, MiniCellItem(), MiniFlowchartNode(), MiniSnapshotBar(), OnboardingScreen(), OnboardingSlideAiTutor(), OnboardingSlidePractice() (+1 more)

### Community 58 - "CatalogueSortMode"
Cohesion: 0.33
Nodes (5): CatalogueSortMode, COMPLEXITY, DEFAULT, DIFFICULTY, NAME

### Community 59 - "EditProfileSheet"
Cohesion: 0.26
Nodes (9): AccountStatusCard(), EditProfileAndAccountSheet(), EditProfileSheet(), AuthRepository, Modifier, ProfileAvatar(), ProfileTheme(), ProfileTopBar() (+1 more)

### Community 61 - "CodeListing"
Cohesion: 0.18
Nodes (11): CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color, LazyListState, Modifier, CodeTracePane() (+3 more)

### Community 62 - "AlgoLensApp.kt"
Cohesion: 0.19
Nodes (15): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, Modifier, PracticeScreen() (+7 more)

### Community 63 - "InstrumentDeck"
Cohesion: 0.20
Nodes (10): AttachedDeckTabs(), InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx, Modifier, State, VisualizerScreenState (+2 more)

### Community 64 - "ChatScreen"
Cohesion: 0.14
Nodes (20): ProjectRecommendationPayload, RecommendedAlgorithmCandidate, AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock(), ComplexityMatrixBlock() (+12 more)

### Community 65 - "SectionLabel"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 66 - "UElementHandler"
Cohesion: 0.36
Nodes (6): UElementHandler, JavaContext, UElementHandler, UElementHandler, UElementHandler, UElementHandler

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "ElementState"
Cohesion: 0.20
Nodes (10): ElementState, ACTIVE, COMPARING, FOUND, IDLE, PIVOT, SORTED, SWAPPING (+2 more)

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

### Community 79 - "VisualizerBenchmarks.kt"
Cohesion: 0.20
Nodes (7): FrameTimingBenchmark, StartupBenchmark, compilationmode, frametimingmetric, macrobenchmarkrule, startupmode, startuptimingmetric

### Community 80 - "MainActivity.kt"
Cohesion: 0.32
Nodes (6): MainActivity, Bundle, ComponentActivity, enableedgetoedge, installsplashscreen, setcontent

### Community 85 - "GraphTreeRenderer"
Cohesion: 0.46
Nodes (5): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Offset

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.48
Nodes (6): animatefloatasstate, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, SlotTransform

### Community 91 - "Type.kt"
Cohesion: 0.33
Nodes (5): font, r, TextStyle, TextUnit, typography

### Community 92 - "WeightBadgeOverlay"
Cohesion: 0.33
Nodes (5): Modifier, VisualizerScreenState, VisualizerStep, WeightBadgeOverlay, VisualizerOverlay

### Community 93 - "GraphCustomization"
Cohesion: 0.40
Nodes (4): ForBst, ForHeap, ForTraversal, GraphCustomization

### Community 94 - "ExploreCatalogScreen"
Cohesion: 0.50
Nodes (5): ExploreCatalogScreen(), ExploreCatalogScreenPreview(), Modifier, PracticeTrack, TrackCatalogCard()

### Community 95 - "RootStage"
Cohesion: 0.50
Nodes (4): RootStage, APP, BOOT, ONBOARDING

### Community 97 - "GuidedTourOverlay"
Cohesion: 0.67
Nodes (3): GuidedTourOverlay(), GuidedTourOverlayPreview(), Modifier

## Knowledge Gaps
- **154 isolated node(s):** `BOOT`, `ONBOARDING`, `APP`, `Goal`, `Documentation and inspection basis` (+149 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 382 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CodeListing()` connect `CodeListing` to `ChatScreen.kt`, `callName`, `TraceLanguage`?**
  _High betweenness centrality (0.084) - this node is a cross-community bridge._
- **Why does `callName()` connect `UElementHandler` to `callName`, `main/com/example/algolens/lint/AlgoLensIssueRegistry.kt`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Why does `VisualizerStep` connect `VisualizerStep` to `ChatScreen.kt`, `.generateStepsForAlgorithm`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `PredictionQuestion`, `test`, `AlgorithmId`, `VisualizerScreenState`, `GraphNodeState`, `VisualizerHost.kt`, `AlgorithmStepRepository.kt`, `CellGrid.kt`, `RecursionTreeOverlay`, `ChallengeModeManagerTest.kt`, `AiChatBackendTest.kt`, `RegionAuxiliary`, `CodeListing`, `ComparisonBridgeOverlay.kt`, `VisualizerOverlay`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **What connects `BOOT`, `ONBOARDING`, `APP` to the rest of the system?**
  _154 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChatScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.0795563741191544 - nodes in this community are weakly interconnected._
- **Should `PracticeSessionManager` be split into smaller, more focused modules?**
  _Cohesion score 0.07536231884057971 - nodes in this community are weakly interconnected._
- **Should `SwapFlight.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06342494714587738 - nodes in this community are weakly interconnected._