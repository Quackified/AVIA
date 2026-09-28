# Graph Report - AlgoLens  (2026-09-27)

## Corpus Check
- 129 files · ~182,092 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 55 file(s) not represented in the graph (top: .xml 46, (none) 4, .IssueRegistry 2)

## Summary
- 1643 nodes · 5409 edges · 113 communities (78 shown, 35 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 124 edges (avg confidence: 0.87)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9b93bf0a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ChallengeModeManager.kt
- VisualizerStep
- ElementState
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
- AiChatBackendTest.kt
- callName
- callName
- AlgorithmId
- AlgoGlyphs.kt
- kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- ProjectConstraintBrief
- StepToken
- VisualizerScreenState
- ChatScreen.kt
- ChatSessionState.kt
- AlgorithmStepRepository.kt
- VisualizerHost.kt
- .processQuery
- BootController
- VisualizerScreen.kt
- AppSettings
- AuthRepository
- AccountTemplateCard.kt
- AuthRepository.kt
- GlassPanel.kt
- AviaLogo.kt
- AlgoLensTheme
- RailIconButton
- main/com/example/algolens/lint/AlgoLensIssueRegistry.kt
- AlgoLensDetectorsTest
- VisualizerScreenStateTest.kt
- CellGrid
- StateDeckPage
- AlgoLensDetectorsTest
- ChatConversation
- RecursionTreeOverlay
- ChallengeModeManagerTest.kt
- UserPreferencesTest
- ProfileScreen.kt
- FakeAuthRepository
- .highlight
- CellGlowPainter.kt
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
- ChatHistorySidebar
- SectionLabel
- UElementHandler
- StageLegend
- ProfileEditorDraft
- FlowchartShape
- TableAlignment
- UserPreferences.kt
- ExampleUnitTest
- Profile and Edit Profile
- SignedIn
- UnavailableFirebaseAuthRepository
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
- UElementHandler
- .Content
- ProfileValidator
- ExploreCatalogScreen
- AlgorithmTheorySheet
- DeckPage
- CompactDropdownMenuItem
- Dp
- PaddingValues
- Shape
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
- pi
- rotate

## God Nodes (most connected - your core abstractions)
1. `VisualizerStep` - 80 edges
2. `VisualizerScreenState` - 50 edges
3. `AlgoLensTheme()` - 47 edges
4. `AlgorithmId` - 45 edges
5. `AlgoTokens` - 42 edges
6. `VisualizerScreenStateTest` - 41 edges
7. `ChatSessionManager` - 38 edges
8. `buildPredictionQuestion()` - 34 edges
9. `AlgoType` - 30 edges
10. `ChallengeModeManagerTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `2. Chat UI cleanup (`ui/chat/ChatScreen.kt`)` --references--> `pressPhysics()`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/main/java/com/example/algolens/ui/components/Instrument.kt
- `Goal` --references--> `MainActivity`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/MainActivity.kt
- `3. Minimalist boot splash` --references--> `AppSettings`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/main/java/com/example/algolens/data/AppSettings.kt
- `Goal` --references--> `BootController`  [INFERRED]
  docs/TASK.md → app/src/main/java/com/example/algolens/ui/boot/BootController.kt
- `5. Verification` --references--> `BootControllerTest`  [INFERRED]
  docs/IMPLEMENTATION.md → app/src/test/java/com/example/algolens/BootControllerTest.kt

## Import Cycles
- None detected.

## Communities (113 total, 35 thin omitted)

### Community 0 - "ChallengeModeManager.kt"
Cohesion: 0.09
Nodes (160): accentgreen, accentorange, accentpink, accentpinkglow, accentred, accentyellow, algolenstheme, algotokens (+152 more)

### Community 1 - "VisualizerStep"
Cohesion: 0.18
Nodes (5): buildPredictionQuestion(), highlightedCellPair(), predictionAnswerFor(), VisualizerStep, ChallengeModeManagerTest

### Community 2 - "ElementState"
Cohesion: 0.06
Nodes (31): PracticeQuestionRepository, PracticeDifficulty, EASY, HARD, MEDIUM, PracticeOption, PracticeQuestion, BufferSnapshot (+23 more)

### Community 3 - "SwapFlight.kt"
Cohesion: 0.07
Nodes (37): abs, activity, animatecontentsize, animationspec, animationvector1d, Dp, Modifier, smoothPanelExpansion() (+29 more)

### Community 4 - ".generateStepsForAlgorithm"
Cohesion: 0.13
Nodes (5): AlgorithmStepRepository, SortOrder, ASC, DESC, AlgorithmStepRepositoryTest

### Community 5 - "TraceLanguage"
Cohesion: 0.32
Nodes (7): AlgorithmCodeRegistry, MultiLangCode, TraceLanguage, CPP, JAVA, KOTLIN, PYTHON

### Community 6 - "OffscreenPointerBanner"
Cohesion: 0.20
Nodes (8): OffscreenPointerTarget, LazyListState, Modifier, OffscreenCellPopup(), OffscreenPointerBanner(), Modifier, OffscreenCellPopup(), PointerBannerOverlay

### Community 8 - "ChatMarkdownMessage"
Cohesion: 0.10
Nodes (24): ChatFlowchartBlock(), FlowchartEdge, FlowchartEdgeConnector(), FlowchartNode, FlowchartNodeCard(), Modifier, ParsedFlowchart, parseFlowchart() (+16 more)

### Community 9 - "Instrument.kt"
Cohesion: 0.17
Nodes (18): AlgoHairline(), DoubleBezelShell(), EntryCascade, entryCascadeInWindow(), EntryCascadeProvider(), InstrumentMeter(), InstrumentRule(), Color (+10 more)

### Community 10 - "ProfileFlowTest.kt"
Cohesion: 0.07
Nodes (25): activityscenariorule, androidjunit4, ExampleInstrumentedTest, VisualizerScreenshotTest, before, FrameTimingBenchmark, StartupBenchmark, Bitmap (+17 more)

### Community 11 - "NavTab"
Cohesion: 0.25
Nodes (8): BottomNavBar(), BottomNavBarPreview(), Modifier, NavTab, CHAT, EXPLORE, HOME, PROFILE

### Community 12 - "PredictionQuestion"
Cohesion: 0.18
Nodes (20): bufferPushPopQuestion(), comparePairQuestion(), foundOrCompareQuestion(), graphVisitQuestion(), heapQuestion(), Indices, insertionSortQuestion(), mergeSortQuestion() (+12 more)

### Community 14 - "GraphTreeRenderer.kt"
Cohesion: 0.17
Nodes (12): atan2, canvas, cos, graphedgedefault, immutable, nativecanvas, paint, patheffect (+4 more)

### Community 15 - "AiChatBackendTest.kt"
Cohesion: 0.15
Nodes (17): VisualizerHeaderMenuTest, assertequals, assertfalse, assertnotnull, asserttrue, experimentalcoroutinesapi, flowchartshape, icons (+9 more)

### Community 16 - "callName"
Cohesion: 0.29
Nodes (6): callName(), hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), UCallExpression, normalizedPath()

### Community 17 - "callName"
Cohesion: 0.29
Nodes (6): callName(), hasDpLiteralArg(), hasHexLiteralArg(), isAppFile(), normalizedPath(), UCallExpression

### Community 18 - "AlgorithmId"
Cohesion: 0.07
Nodes (27): AlgorithmRegistry, AlgorithmId, BFS, BINARY_SEARCH, BINARY_SEARCH_TREE, BUBBLE_SORT, DFS, HEAP (+19 more)

### Community 19 - "AlgoGlyphs.kt"
Cohesion: 0.25
Nodes (6): CompactDropdownMenuItem(), ComplexityPill(), androidx, ImageVector, Color, pathbuilder

### Community 20 - "kotlin/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): Detector, implementation, Issue, IssueRegistry, AlgoLensIssueRegistry, HardcodedHexColorDetector, RawDpSpacingDetector, RoundedCornerShapeLiteralDetector (+5 more)

### Community 21 - "ProjectConstraintBrief"
Cohesion: 0.09
Nodes (22): DataScale, LARGE_100K_PLUS, MEDIUM_1K, SMALL_UNDER_64, UNSPECIFIED, GraphGoal, EXHAUSTIVE_OR_CYCLE, NONE (+14 more)

### Community 22 - "StepToken"
Cohesion: 0.12
Nodes (17): StepToken, COMPARE, COMPLETE, DEQUEUE, ELEVATE, ENQUEUE, FOUND, MISS (+9 more)

### Community 23 - "VisualizerScreenState"
Cohesion: 0.09
Nodes (13): Algorithm, androidx, rememberVisualizerScreenState(), VisualizerScreenState, BufferOp, ChallengeState, 1. Visualizer state fixes (`ui/visualizer/VisualizerScreenState.kt`), GraphCustomization (+5 more)

### Community 24 - "ChatScreen.kt"
Cohesion: 0.08
Nodes (38): alertdialog, algohairline, annotatedstring, AssistantMessageBubble(), ChatHeader(), ChatInputDock(), ChatScreen(), CodeSnippetBlock() (+30 more)

### Community 25 - "ChatSessionState.kt"
Cohesion: 0.10
Nodes (22): ChatResponseProvider, OfflineCatalogChatProvider, ChatAction, ChatSender, ASSISTANT, SYSTEM, USER, ChatStatus (+14 more)

### Community 26 - "AlgorithmStepRepository.kt"
Cohesion: 0.11
Nodes (20): BufferOp, Dequeue, Enqueue, Peek, Pop, Push, QueueOp, GraphVisualizer() (+12 more)

### Community 27 - "VisualizerHost.kt"
Cohesion: 0.05
Nodes (54): BarVisualizer(), BarVisualizerPreview(), com, Modifier, BufferStageControls(), BufferVisualizer(), BufferVisualizerQueuePreview(), BufferVisualizerStackPreview() (+46 more)

### Community 28 - ".processQuery"
Cohesion: 0.09
Nodes (7): AiChatEngine, ChatCodeSnippet, ChatComplexitySnapshot, ChatMessage, AlgorithmTheoryData, AlgorithmTheoryRepository, AiChatBackendTest

### Community 29 - "BootController"
Cohesion: 0.11
Nodes (15): MainActivity, BootController, BootControllerEffect(), BootControllerTest, Bundle, ComponentActivity, delay, 2. Chat UI cleanup (`ui/chat/ChatScreen.kt`) (+7 more)

### Community 30 - "VisualizerScreen.kt"
Cohesion: 0.15
Nodes (16): animatable, animatedpasstate, SampleData, Algorithm, AiTutorSheet(), AiTutorSheetPreview(), EmptyCanvas(), Modifier (+8 more)

### Community 31 - "AppSettings"
Cohesion: 0.21
Nodes (5): AppSettings, Constraints, Goal, Verification basis, SharedPreferences

### Community 32 - "AuthRepository"
Cohesion: 0.12
Nodes (4): AuthRepository, GuestDataMigrationPolicy, KEEP_SEPARATE, MERGE_GUEST_TO_ACCOUNT

### Community 33 - "AccountTemplateCard.kt"
Cohesion: 0.12
Nodes (17): activityresultcontracts, ProfileField(), bitmapfactory, contentdescription, focusdirection, imagebitmap, ImeAction, intent (+9 more)

### Community 34 - "AuthRepository.kt"
Cohesion: 0.22
Nodes (8): AuthAccountState, AuthProviderType, EMAIL_PASSWORD, GOOGLE_FIREBASE, Error, Loading, chathistoryrepository, stable

### Community 35 - "GlassPanel.kt"
Cohesion: 0.20
Nodes (15): animatefloat, AlgoWorkspaceBackground(), AmbientGlowDivider(), GlassSurface(), Color, Dp, Modifier, Shape (+7 more)

### Community 36 - "AviaLogo.kt"
Cohesion: 0.09
Nodes (27): BootOverlay(), Modifier, AviaLogo(), Color, Dp, Modifier, CatalogueGroupHeader(), CatalogueSection (+19 more)

### Community 37 - "AlgoLensTheme"
Cohesion: 0.18
Nodes (13): AlgoCard(), AlgoCardChrome, AlgoCardPreview(), Modifier, AlgoLensTheme(), CustomizeGraphSheet(), CustomizeGraphSheetBstPreview(), CustomizeGraphSheetHeapPreview() (+5 more)

### Community 38 - "RailIconButton"
Cohesion: 0.20
Nodes (11): IconPillButton(), androidx, Color, ImageVector, RailIconButton(), SegmentedToggle(), CustomizeInputSheet(), CustomizeInputSheetPreview() (+3 more)

### Community 39 - "main/com/example/algolens/lint/AlgoLensIssueRegistry.kt"
Cohesion: 0.23
Nodes (13): category, AlgoLensIssueRegistry, HardcodedHexColorDetector, Detector, Issue, IssueRegistry, UastScanner, UElement (+5 more)

### Community 40 - "AlgoLensDetectorsTest"
Cohesion: 0.22
Nodes (4): AlgoLensDetectorsTest, Detector, Issue, LintDetectorTest

### Community 41 - "VisualizerScreenStateTest.kt"
Cohesion: 0.18
Nodes (6): AlgorithmId, AttachedDeckTabs(), Algorithm, assertnull, DeckPage, VisualizerStep

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
Cohesion: 0.25
Nodes (5): Modifier, RecursionTreeOverlay, Modifier, VisualizerOverlay, IntRange

### Community 47 - "ChallengeModeManagerTest.kt"
Cohesion: 0.09
Nodes (22): CanvasChallengePrompt(), CanvasChallengePromptFoundPreview(), challengeEligibleIndices(), ChallengeFeedback, ChallengeQuestionType, SELECT_COMPARE_PAIR, SELECT_PIVOT, SWAP_DECISION (+14 more)

### Community 49 - "ProfileScreen.kt"
Cohesion: 0.12
Nodes (20): Algorithm, AuthRepository, Modifier, ProfileOptionItem(), ProfileScreen(), ProfileScreenPreview(), asimagebitmap, authaccountstate (+12 more)

### Community 50 - "FakeAuthRepository"
Cohesion: 0.24
Nodes (3): ProfileFlowTest, FakeAuthRepository, SignedOut

### Community 51 - ".highlight"
Cohesion: 0.14
Nodes (7): AnnotatedString, SyntaxHighlighter, Modifier, TraceStrip(), TraceStripPreview(), FeatureEnhancementsTest, TraceLanguage

### Community 52 - "CellGlowPainter.kt"
Cohesion: 0.47
Nodes (5): drawCellGlow(), Color, CornerRadius, drawscope, stroke

### Community 53 - "VisualizerHeader"
Cohesion: 0.47
Nodes (10): HeaderActions(), HeaderModeRow(), HeaderOverflowMenuHost(), HeaderTitle(), Algorithm, AlgorithmSpec, Modifier, VisualizerScreenState (+2 more)

### Community 54 - "RegionAuxiliary"
Cohesion: 0.11
Nodes (11): AuxiliarySlot, BOTTOM, TOP, RegionAuxiliary, Modifier, MergeBufferRow, androidx, Modifier (+3 more)

### Community 55 - "BufferOpEditor"
Cohesion: 0.29
Nodes (10): BufferOpEditor(), CustomizeBufferSheet(), CustomizeBufferSheetStackPreview(), CustomizeQueueSheet(), CustomizeQueueSheetPreview(), BufferOp, Color, QueueOp (+2 more)

### Community 56 - "InputValidationResult"
Cohesion: 0.18
Nodes (10): Error, ForBst, ForHeap, ForTraversal, GraphCustomization, InputValidationResult, Valid, Color (+2 more)

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
Cohesion: 0.20
Nodes (10): CodeLineAccent, Family, Color, CodeListing(), InlineVarChip(), androidx, AnnotatedString, Color (+2 more)

### Community 62 - "AlgoLensApp.kt"
Cohesion: 0.14
Nodes (19): animatedcontent, AlgoLensApp(), AlgoLensAppPreview(), AppShell(), Algorithm, Modifier, RootStage, APP (+11 more)

### Community 63 - "InstrumentDeck"
Cohesion: 0.17
Nodes (12): CodeTracePane(), CodeTracePanePreview(), Modifier, State, InstrumentDeck(), InstrumentDeckPreview(), Algorithm, androidx (+4 more)

### Community 64 - "ChatHistorySidebar"
Cohesion: 0.20
Nodes (10): ProjectRecommendationPayload, RecommendedAlgorithmCandidate, CandidateCard(), ChatHistorySidebar(), ComplexityPill(), androidx, ImageVector, Modifier (+2 more)

### Community 65 - "SectionLabel"
Cohesion: 0.24
Nodes (12): CommonComponentsPreview(), ComplexityCard(), CustomSwitch(), Color, ImageVector, Modifier, OfflineBadge(), SectionLabel() (+4 more)

### Community 66 - "UElementHandler"
Cohesion: 0.36
Nodes (6): JavaContext, UElementHandler, UElementHandler, UElementHandler, UElementHandler, UElementHandler

### Community 67 - "StageLegend"
Cohesion: 0.40
Nodes (5): Color, Modifier, LegendEntry(), StageLegend(), StageLegendPreview()

### Community 68 - "ProfileEditorDraft"
Cohesion: 0.27
Nodes (4): UserProfile, ProfileEditorDraft, ProfileEditorDraftTest, UserProfile

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

### Community 85 - "GraphTreeRenderer"
Cohesion: 0.39
Nodes (6): GraphCanvasGeometry, GraphTreeRenderer(), GraphEdgeState, GraphNodeState, Modifier, Offset

### Community 87 - "ArrayViewMode"
Cohesion: 0.50
Nodes (3): ArrayViewMode, BARS, CELLS

### Community 90 - "ComparisonBridgeOverlay.kt"
Cohesion: 0.29
Nodes (9): animatefloatasstate, ActivePairPointerBracket(), ComparisonBridgeOverlay(), Modifier, brush, path, SlotTransform, strokecap (+1 more)

### Community 91 - "UElementHandler"
Cohesion: 0.36
Nodes (6): UElementHandler, JavaContext, UElementHandler, UElementHandler, UElementHandler, UElementHandler

### Community 92 - ".Content"
Cohesion: 0.50
Nodes (3): Modifier, VisualizerScreenState, VisualizerStep

### Community 94 - "ExploreCatalogScreen"
Cohesion: 0.50
Nodes (5): ExploreCatalogScreen(), ExploreCatalogScreenPreview(), Modifier, PracticeTrack, TrackCatalogCard()

### Community 95 - "AlgorithmTheorySheet"
Cohesion: 0.40
Nodes (6): AlgorithmTheorySheet(), AlgorithmTheorySheetPreview(), ComplexityBentoCard(), Color, Modifier, PropertyBadge()

### Community 96 - "DeckPage"
Cohesion: 0.50
Nodes (3): DeckPage, STATE, TRACE

### Community 97 - "CompactDropdownMenuItem"
Cohesion: 0.50
Nodes (4): CompactDropdownMenuItem(), CompactHeaderPill(), androidx, Color

## Knowledge Gaps
- **151 isolated node(s):** `6. Deviations / known issues`, `Verification basis`, `ArrayPreset`, `TourStep`, `Family` (+146 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 397 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **35 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CodeListing()` connect `CodeListing` to `ChallengeModeManager.kt`, `TraceLanguage`, `InstrumentDeck`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `VisualizerScreenState` connect `VisualizerScreenState` to `ChallengeModeManager.kt`, `RailIconButton`, `OffscreenPointerBanner`, `VisualizerScreenStateTest`, `VisualizerScreenStateTest.kt`, `RecursionTreeOverlay`, `AlgorithmId`, `RegionAuxiliary`, `VisualizerHost.kt`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **Why does `AlgorithmId` connect `AlgorithmId` to `ChallengeModeManager.kt`, `VisualizerStep`, `ElementState`, `.generateStepsForAlgorithm`, `AlgoLensTheme`, `ChallengeModeManagerTest.kt`, `AiChatBackendTest.kt`, `AlgorithmStepRepository.kt`, `.processQuery`, `VisualizerScreen.kt`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **What connects `6. Deviations / known issues`, `Verification basis`, `ArrayPreset` to the rest of the system?**
  _151 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ChallengeModeManager.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.08765515684946965 - nodes in this community are weakly interconnected._
- **Should `ElementState` be split into smaller, more focused modules?**
  _Cohesion score 0.05925925925925926 - nodes in this community are weakly interconnected._
- **Should `SwapFlight.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06951219512195123 - nodes in this community are weakly interconnected._