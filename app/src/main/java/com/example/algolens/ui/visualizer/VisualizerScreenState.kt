package com.example.algolens.ui.visualizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.AppSettings
import com.example.algolens.data.GraphTreeMutations
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.BufferOp
import com.example.algolens.model.GraphCustomization
import com.example.algolens.model.GraphTool
import com.example.algolens.model.QueueOp
import com.example.algolens.model.SortOrder
import kotlinx.coroutines.delay

/**
 * Modular widgets available in the Deck Instruments workspace carousel.
 */
enum class WidgetType(val title: String, val subtitle: String) {
    TRACE("CODE TRACE", "Active line highlighting & execution trace"),
    STATE("ALGORITHM STATE", "Complexity, variables & step explanations"),
    TELEMETRY("TELEMETRY", "Queue, stack & frontier data structures")
}

/**
 * Snapshot of custom graph topological and endpoint mutations for Undo/Redo.
 */
data class GraphEditorSnapshot(
    val customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>?,
    val userCustomCoordinates: Map<String, Offset>,
    val graphStartNodeId: String?,
    val graphTargetNodeId: String?
)

/**
 * Single source of truth for everything that mutates inside
 * [VisualizerScreen] during a single open of the screen. Lifting all
 * state into one [Stable] class lets the screen body stay declarative
 * ("compose these four regions with this state") and makes each
 * region testable in isolation.
 *
 * Why a class (not 12 individual `remember { mutableStateOf(...) }`
 * in the screen)?
 *  1. **Encapsulates the playback loop.** A single `LaunchedEffect`
 *     in [rememberVisualizerScreenState] reads [isPlaying] and
 *     [playbackSpeedMs] and ticks [currentStepIdx]. Without a hoisted
 *     class, every screen reopen and every recomposition dance would
 *     re-derive the loop.
 *  2. **Clamps and transitions live in one place.** Going past the
 *     last step, jumping to a scrubber value, applying a new custom
 *     input — all of these reset the player the same way, and that
 *     logic now lives in the data class instead of being duplicated
 *     across the rail / header / sheets.
 *  3. **The screen composables become narrow.** [VisualizerHeader],
 *     [PlaybackRail], and the canvas all take *this* object plus a
 *     small set of callbacks. They don't need to know how the state
 *     is held; they just observe it.
 *
 * Marked [Stable] because every public property is backed by a
 * `State<>` that Compose's snapshot system tracks; this lets the
 * compiler skip recomposition of regions that don't read what
 * changed.
 */
@Stable
class VisualizerScreenState(
    initialAlgorithm: Algorithm,
) {
    // ── Step stream ──
    var arrayData: List<Int> by mutableStateOf(
        AlgorithmRegistry.specFor(initialAlgorithm.id)?.defaultInput
            ?: AlgorithmStepRepository.DEFAULT_INPUT
    )

    /** Recomputed via the `remember` factory when [arrayData] changes.
     *  Backed by [mutableStateOf] so assigning the generated list from
     *  the LaunchedEffect immediately recomposes the canvas — without
     *  this, the header showed PROCESSING and cells only appeared after
     *  the next unrelated state change (e.g. pressing play). */
    var steps: List<VisualizerStep> by mutableStateOf(emptyList())
        internal set

    // ── Playback ──
    var currentStepIdx: Int by mutableIntStateOf(0)

    var isPlaying: Boolean by mutableStateOf(false)

    /** Three discrete speeds, cycled by the rail's speed chip. */
    var playbackSpeedMs: Long by mutableLongStateOf(AppSettings.defaultPlaybackSpeedMs)

    // ── Sheet visibility ──
    var showInputSheet: Boolean by mutableStateOf(false)
    var showTheorySheet: Boolean by mutableStateOf(false)
    var showTutorSheet: Boolean by mutableStateOf(false)
    var showGuidedTour: Boolean by mutableStateOf(false)

    // ── Focus Deck (Phase 5A) ──
    /** Which deck page is showing. See [DeckPage]. */
    var deckPage: DeckPage by mutableStateOf(DeckPage.TRACE)

    /** Whether the Focus Deck is expanded over the stage. */
    var deckExpanded: Boolean by mutableStateOf(false)

    /** Actual measured height of the docked terminal (including attached tabs and font scale). */
    var measuredDockHeight: androidx.compose.ui.unit.Dp by mutableStateOf(androidx.compose.ui.unit.Dp(0f))

    /**
     * Transient scrub preview. Non-null only while a finger is on the timeline:
     * [currentStep] renders from it, so the canvas, the narrative strip and the
     * code trace all track the drag without the playhead having moved. Kept
     * separate from [currentStepIdx] so a cancelled gesture leaves the playhead
     * exactly where it was.
     */
    var scrubTarget: Int? by mutableStateOf(null)

    // ── Challenge mode ──
    var challengeState: ChallengeState by mutableStateOf(ChallengeState())

    // ── View mode (cells vs bars, only meaningful for LINEAR_1D) ──
    var arrayViewMode: ArrayViewMode by mutableStateOf(ArrayViewMode.CELLS)

    // ── Cell size scale (user-adjustable, 0.6x .. 1.4x of the
    //    responsive default). Defaults to Small (0.7f) to eliminate
    //    horizontal clipping on standard-sized arrays. ──
    var cellScale: Float by mutableFloatStateOf(AppSettings.defaultCellScale)

    // ── Sort order (ASC | DESC), applied to the LINEAR_1D
    //    customize-input sheet. Persists across algorithm switches
    //    so a user who set DESC on Bubble Sort will see DESC on
    //    Quick Sort too — they can re-toggle in the sheet. ──
    var lastAppliedSortOrder: SortOrder by mutableStateOf(SortOrder.ASC)

    // ── Search target (Linear / Binary Search). Chosen in the
    //    customize-input sheet; null means the generator default. ──
    var searchTarget: Int? by mutableStateOf(null)

    // ── Buffer customize state (Stack / Queue). Initialized to canonical
    //    default sequences so Stack and Queue open with rich interactive steps. ──
    var bufferOps: List<BufferOp> by mutableStateOf(AlgorithmStepRepository.defaultStackOps())
    var queueOps: List<QueueOp> by mutableStateOf(AlgorithmStepRepository.defaultQueueOps())

    // ── Graph customize state (BST / Heap / BFS / DFS). The sheet
    //    emits a `GraphCustomization` (ForHeap | ForBst | ForTraversal)
    //    which is stored here. The step generator dispatch reads
    //    the active variant. ──
    var graphConfig: GraphCustomization? by mutableStateOf(null)

    // ── Interactive Graph Builder topology (BFS / DFS / Dijkstra). Null uses canonicalWeightedGraph(). ──
    var customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? by mutableStateOf(null)

    // ── Persistent node coordinate overrides across all steps (independent of playback/topology) ──
    var userCustomCoordinates: Map<String, Offset> by mutableStateOf(emptyMap())

    // ── Active Graph Tool & Selection state ──
    var activeGraphTool: GraphTool by mutableStateOf(GraphTool.MOVE)
    var selectedGraphNodeId: String? by mutableStateOf(null)

    // ── Goal-directed graph endpoints (START / TARGET) ──
    var graphStartNodeId: String? by mutableStateOf(null)
    var graphTargetNodeId: String? by mutableStateOf(null)

    // ── Workspace Focus Mode (expands Canvas Visualizer, hides bottom widget deck) ──
    var canvasFocusMode: Boolean by mutableStateOf(false)

    // ── Modular Widget Carousel ──
    var activeWidgetIndex: Int by mutableIntStateOf(0)
    var enabledWidgets: List<WidgetType> by mutableStateOf(
        listOf(WidgetType.TRACE, WidgetType.STATE, WidgetType.TELEMETRY)
    )

    fun toggleWidget(type: WidgetType) {
        if (type in enabledWidgets) {
            if (enabledWidgets.size > 1) {
                enabledWidgets = enabledWidgets - type
                activeWidgetIndex = activeWidgetIndex.coerceAtMost(enabledWidgets.lastIndex)
            }
        } else {
            enabledWidgets = enabledWidgets + type
        }
    }

    fun moveWidget(fromIndex: Int, toIndex: Int) {
        if (fromIndex in enabledWidgets.indices && toIndex in enabledWidgets.indices && fromIndex != toIndex) {
            val list = enabledWidgets.toMutableList()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            enabledWidgets = list
            activeWidgetIndex = toIndex
        }
    }

    // ── Undo / Redo History for Graph Editor ──
    private val undoStack = ArrayDeque<GraphEditorSnapshot>()
    private val redoStack = ArrayDeque<GraphEditorSnapshot>()

    val canUndoGraph: Boolean get() = undoStack.isNotEmpty()
    val canRedoGraph: Boolean get() = redoStack.isNotEmpty()

    fun recordGraphSnapshot() {
        val current = GraphEditorSnapshot(
            customGraph = customGraph,
            userCustomCoordinates = userCustomCoordinates,
            graphStartNodeId = graphStartNodeId,
            graphTargetNodeId = graphTargetNodeId
        )
        if (undoStack.size >= 30) {
            undoStack.removeFirst()
        }
        undoStack.addLast(current)
        redoStack.clear()
    }

    fun undoGraph() {
        if (undoStack.isEmpty()) return
        val current = GraphEditorSnapshot(
            customGraph = customGraph,
            userCustomCoordinates = userCustomCoordinates,
            graphStartNodeId = graphStartNodeId,
            graphTargetNodeId = graphTargetNodeId
        )
        redoStack.addLast(current)
        val prev = undoStack.removeLast()
        customGraph = prev.customGraph
        userCustomCoordinates = prev.userCustomCoordinates
        graphStartNodeId = prev.graphStartNodeId
        graphTargetNodeId = prev.graphTargetNodeId
    }

    fun redoGraph() {
        if (redoStack.isEmpty()) return
        val current = GraphEditorSnapshot(
            customGraph = customGraph,
            userCustomCoordinates = userCustomCoordinates,
            graphStartNodeId = graphStartNodeId,
            graphTargetNodeId = graphTargetNodeId
        )
        undoStack.addLast(current)
        val next = redoStack.removeLast()
        customGraph = next.customGraph
        userCustomCoordinates = next.userCustomCoordinates
        graphStartNodeId = next.graphStartNodeId
        graphTargetNodeId = next.graphTargetNodeId
    }

    fun resetGraphHistory() {
        undoStack.clear()
        redoStack.clear()
    }

    // ── Derived helpers ──
    /** Clamped to ≥ 1 so scrubbers / counters never render `Step 0 of 0`. */
    val totalSteps: Int get() = steps.size.coerceAtLeast(1)

    /**
     * The step index every region renders from: the scrub preview while a drag
     * is in flight, otherwise the committed playhead. Centralising it here is
     * why live scrub previewing required no changes in the canvas or the trace.
     */
    val displayStepIdx: Int
        get() = scrubTarget?.coerceIn(0, steps.lastIndex.coerceAtLeast(0)) ?: currentStepIdx

    /**
     * Safe to read regardless of [steps] size. When the step stream is empty
     * (the brief window before the first generation completes) this returns a
     * neutral `STANDBY` frame — never a fabricated `PROCESSING` phase, which
     * previously flashed a "work is happening" pill while nothing existed yet.
     *
     * Decorates current step nodes with persistent [userCustomCoordinates] by ID,
     * ensuring node drags persist across steps and playback without step regeneration.
     */
    val currentStep: VisualizerStep
        get() {
            val baseStep = if (steps.isEmpty()) VisualizerStep(phaseLabel = "STANDBY") else steps[displayStepIdx.coerceIn(0, steps.lastIndex)]
            if (userCustomCoordinates.isEmpty() || baseStep.nodes.isEmpty()) return baseStep
            return baseStep.copy(
                nodes = baseStep.nodes.map { node ->
                    userCustomCoordinates[node.id]?.let { node.copy(x = it.x, y = it.y) } ?: node
                }
            )
        }

    /**
     * Playhead position as 0f..1f. The transport reads this through a lambda so
     * advancing the meter never recomposes a text node.
     */
    val stepProgress: Float
        get() = if (totalSteps <= 1) {
            0f
        } else {
            (displayStepIdx.toFloat() / (totalSteps - 1).toFloat()).coerceIn(0f, 1f)
        }

    /** True when challenge mode is "in flight" (active prompt, no feedback yet). */
    val challengeInFlight: Boolean
        get() = challengeState.isActive && challengeState.feedback == null

    val speedLabel: String
        get() = when (playbackSpeedMs) {
            1_000L -> "0.5x"
            600L -> "1.0x"
            300L -> "2.0x"
            else -> "1.0x"
        }

    // ── Mutators ──
    fun togglePlay() {
        isPlaying = !isPlaying
        if (isPlaying) {
            // Pressing play while parked on the final step restarts from the
            // top. Previously a play press at the end started a tick that the
            // loop immediately cancelled, which read as a dead button.
            if (currentStepIdx >= steps.lastIndex) {
                currentStepIdx = 0
                scrubTarget = null
            }
            if (AppSettings.autoOpenDeckOnPlay) {
                deckExpanded = true
            }
        }
    }
    /** Used by the playback LaunchedEffect to step forward. */
    internal fun advance() {
        if (currentStepIdx < steps.lastIndex) currentStepIdx++
    }

    fun stepForward() {
        isPlaying = false
        scrubTarget = null
        if (currentStepIdx < steps.lastIndex) currentStepIdx++
    }

    fun stepBackward() {
        isPlaying = false
        scrubTarget = null
        if (currentStepIdx > 0) currentStepIdx--
    }

    fun reset() {
        currentStepIdx = 0
        isPlaying = false
        scrubTarget = null
    }

    fun scrubTo(idx: Int) {
        isPlaying = false
        scrubTarget = null
        currentStepIdx = idx.coerceIn(0, steps.lastIndex.coerceAtLeast(0))
    }

    // ── Focus Deck ──
    /** Opens the deck on [page] (idempotent if already there and open). */
    fun openDeck(page: DeckPage) {
        deckPage = page
        deckExpanded = true
    }

    fun collapseDeck() {
        deckExpanded = false
    }

    fun toggleDeckPage(page: DeckPage) {
        if (deckPage == page && deckExpanded) {
            deckExpanded = false
        } else {
            deckPage = page
            deckExpanded = true
        }
    }

    // ── Scrub preview (live under the finger, committed on release) ──
    /**
     * Moves the *preview* playhead without committing it. Pauses playback so a
     * drag is never fighting the playback tick.
     */
    fun previewScrub(idx: Int) {
        if (steps.isEmpty()) return
        isPlaying = false
        scrubTarget = idx.coerceIn(0, steps.lastIndex)
    }

    /** Promotes an in-flight preview to the real playhead, if there is one. */
    fun commitScrub() {
        scrubTarget?.let { scrubTo(it) }
        scrubTarget = null
    }

    /** Drops an in-flight preview, leaving the playhead untouched. */
    fun cancelScrub() {
        scrubTarget = null
    }

    /** Cell size preset from the header S/M/L toggle (clamped). */
    fun applyCellScale(scale: Float) {
        cellScale = scale.coerceIn(0.6f, 1.4f)
    }

    fun cycleSpeed() {
        playbackSpeedMs = when (playbackSpeedMs) {
            1_000L -> 600L
            600L -> 300L
            300L -> 1_000L
            else -> 600L
        }
    }



    fun toggleChallenge() {
        isPlaying = false
        challengeState = challengeState.copy(isActive = !challengeState.isActive)
    }

    fun updateChallenge(transform: (ChallengeState) -> ChallengeState) {
        challengeState = transform(challengeState)
    }

    fun setCellSelected(idx: Int) {
        val current = challengeState.selectedIndices
        val updated = if (current.contains(idx)) current - idx else current + idx
        challengeState = challengeState.copy(selectedIndices = updated)
    }

    /**
     * Evaluates a direct node tap on the 2D Graph / Tree / Heap canvas when
     * Challenge Mode is active (`SELECT_VISIT_NODE` or Heap index `SELECT_COMPARE_PAIR`).
     */
    fun submitNodePrediction(nodeId: String, algorithmId: AlgorithmId) {
        if (!challengeInFlight) return
        val nextStep = steps.getOrNull(displayStepIdx + 1)
        val q = buildPredictionQuestion(algorithmId, currentStep, nextStep) ?: return
        val idx = nodeId.toIntOrNull()
        val isCorrect = when (q.kind) {
            PredictionKind.SELECT_VISIT_NODE -> predictionAnswerFor(
                question = q,
                userSelectedNodeId = nodeId
            )
            PredictionKind.SELECT_COMPARE_PAIR,
            PredictionKind.SELECT_PIVOT -> if (idx != null) {
                setCellSelected(idx)
                return
            } else false
            else -> return
        }
        val pts = if (isCorrect) q.pointsAvailable + (challengeState.streak * 20) else 0
        challengeState = challengeState.copy(
            score = challengeState.score + pts,
            streak = if (isCorrect) challengeState.streak + 1 else 0,
            totalQuestions = challengeState.totalQuestions + 1,
            correctAnswers = challengeState.correctAnswers + (if (isCorrect) 1 else 0),
            feedback = ChallengeFeedback(
                isCorrect = isCorrect,
                message = if (isCorrect) "Spot on! Node $nodeId is next. ${q.contextLine}" else "Incorrect. Expected ${(q.answer as? PredictionAnswer.NodeId)?.id ?: "another node"}. ${q.contextLine}",
                pointsAwarded = pts
            )
        )
    }

    internal var pendingStepAfterRegen: Int? = null

    /**
     * Effective Stack values at the end of the stored operation sequence (`bufferOps`),
     * bounded by `bufferCapacity` (8). Used by live controls so availability and
     * capacity validation match the sequence tail where new operations are appended.
     */
    val tailStackValues: List<Int>
        get() {
            val ops = bufferOps.ifEmpty { AlgorithmStepRepository.defaultStackOps() }
            val stack = mutableListOf<Int>()
            for (op in ops) {
                when (op) {
                    is BufferOp.Push -> if (stack.size < 8) stack.add(op.value)
                    BufferOp.Pop -> if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex)
                    BufferOp.Peek -> Unit
                }
            }
            return stack
        }

    /**
     * Effective Queue values at the end of the stored operation sequence (`queueOps`),
     * bounded by `bufferCapacity` (8).
     */
    val tailQueueValues: List<Int>
        get() {
            val ops = queueOps.ifEmpty { AlgorithmStepRepository.defaultQueueOps() }
            val queue = mutableListOf<Int>()
            for (op in ops) {
                when (op) {
                    is QueueOp.Enqueue -> if (queue.size < 8) queue.add(op.value)
                    QueueOp.Dequeue -> if (queue.isNotEmpty()) queue.removeAt(0)
                }
            }
            return queue
        }

    fun tailBufferSize(isStack: Boolean): Int =
        if (isStack) tailStackValues.size else tailQueueValues.size

    fun canAppendToBuffer(isStack: Boolean, capacity: Int = 8): Boolean =
        tailBufferSize(isStack) < capacity

    fun canRemoveFromBuffer(isStack: Boolean): Boolean =
        tailBufferSize(isStack) > 0

    /**
     * Available node IDs for BFS/DFS traversal start selection, derived from
     * the live edited graph (`customGraph`) when present or the canonical graph.
     * Empty custom graph explicitly yields an empty list (does not fall back to defaults).
     */
    val effectiveTraversalNodeIds: List<String>
        get() {
            val cg = customGraph
            if (cg != null) {
                return cg.first.map { it.id }
            }
            return AlgorithmStepRepository.canonicalWeightedGraph().first.map { it.id }
        }

    /**
     * Validated traversal start node ID that automatically resets to the first
     * available node if a previously selected start node was deleted.
     */
    val effectiveTraversalStartNodeId: String
        get() {
            val ids = effectiveTraversalNodeIds
            val configured = (graphConfig as? GraphCustomization.ForTraversal)?.startNodeId
            return if (configured != null && configured in ids) configured else (ids.firstOrNull() ?: "")
        }

    fun updateUserCustomCoordinate(nodeId: String, coords: Offset) {
        userCustomCoordinates = userCustomCoordinates + (nodeId to coords)
    }

    fun insertBstKey(key: Int) {
        val currentValues = (graphConfig as? GraphCustomization.ForBst)?.values
            ?: AlgorithmStepRepository.defaultBstValues
        val searchKey = (graphConfig as? GraphCustomization.ForBst)?.searchKey
            ?: AlgorithmStepRepository.defaultBstSearchKey
        val root = GraphTreeMutations.buildBstTree(currentValues)
        val newRoot = GraphTreeMutations.insertIntoBst(root, key)
        val updated = GraphTreeMutations.bstToPreOrderValues(newRoot)
        isPlaying = false
        graphConfig = GraphCustomization.ForBst(updated, searchKey)
    }

    fun deleteBstNode(nodeId: String) {
        val currentValues = (graphConfig as? GraphCustomization.ForBst)?.values
            ?: AlgorithmStepRepository.defaultBstValues
        val searchKey = (graphConfig as? GraphCustomization.ForBst)?.searchKey
            ?: AlgorithmStepRepository.defaultBstSearchKey
        val root = GraphTreeMutations.buildBstTree(currentValues)
        val newRoot = GraphTreeMutations.deleteFromBst(root, nodeId)
        val updated = GraphTreeMutations.bstToPreOrderValues(newRoot)
        isPlaying = false
        userCustomCoordinates = userCustomCoordinates - nodeId
        if (selectedGraphNodeId == nodeId) selectedGraphNodeId = null
        graphConfig = GraphCustomization.ForBst(updated, searchKey)
    }

    fun pushHeapValue(value: Int) {
        val currentValues = (graphConfig as? GraphCustomization.ForHeap)?.values
            ?: AlgorithmStepRepository.DEFAULT_HEAP_INPUT
        val updated = GraphTreeMutations.pushHeapValue(currentValues, value)
        isPlaying = false
        graphConfig = GraphCustomization.ForHeap(updated)
    }

    fun extractHeapRoot() {
        val currentValues = (graphConfig as? GraphCustomization.ForHeap)?.values
            ?: AlgorithmStepRepository.DEFAULT_HEAP_INPUT
        val updated = GraphTreeMutations.removeHeapRoot(currentValues)
        isPlaying = false
        userCustomCoordinates = userCustomCoordinates - "0"
        if (selectedGraphNodeId == "0") selectedGraphNodeId = null
        graphConfig = GraphCustomization.ForHeap(updated)
    }

    fun removeHeapTail() {
        val currentValues = (graphConfig as? GraphCustomization.ForHeap)?.values
            ?: AlgorithmStepRepository.DEFAULT_HEAP_INPUT
        val lastIdx = (currentValues.size - 1).toString()
        val updated = GraphTreeMutations.removeHeapTail(currentValues)
        isPlaying = false
        userCustomCoordinates = userCustomCoordinates - lastIdx
        if (selectedGraphNodeId == lastIdx) selectedGraphNodeId = null
        graphConfig = GraphCustomization.ForHeap(updated)
    }

    fun resetGraphOverrides() {
        customGraph = null
        graphConfig = null
        userCustomCoordinates = emptyMap()
        selectedGraphNodeId = null
        activeGraphTool = GraphTool.MOVE
        graphStartNodeId = null
        graphTargetNodeId = null
        reset()
    }

    /**
     * Typed initial value list for [CustomizeGraphSheet] driven by [com.example.algolens.model.AlgorithmSpec]
     * so `VisualizerScreen.kt` never branches on individual [AlgorithmId] constants.
     */
    fun initialGraphSheetValues(spec: com.example.algolens.model.AlgorithmSpec): List<Int> =
        when (spec.graphTelemetryMode) {
            com.example.algolens.model.GraphTelemetryMode.BST_TARGET ->
                (graphConfig as? GraphCustomization.ForBst)?.values ?: spec.defaultInput
            com.example.algolens.model.GraphTelemetryMode.HEAP_ARRAY ->
                (graphConfig as? GraphCustomization.ForHeap)?.values ?: spec.defaultInput
            else -> arrayData.ifEmpty { spec.defaultInput }
        }

    val initialGraphSearchKey: Int
        get() = (graphConfig as? GraphCustomization.ForBst)?.searchKey
            ?: AlgorithmStepRepository.defaultBstSearchKey

    /**
     * Appends a live Stack operation directly from the stage controls after validating
     * against the sequence tail (`tailStackValues`), then jumps the playhead to the new step.
     */
    fun appendLiveStackOp(op: BufferOp) {
        when (op) {
            is BufferOp.Push -> if (!canAppendToBuffer(isStack = true)) return
            BufferOp.Pop, BufferOp.Peek -> if (!canRemoveFromBuffer(isStack = true)) return
        }
        val baseOps = if (bufferOps.isNotEmpty()) {
            bufferOps
        } else {
            AlgorithmStepRepository.defaultStackOps()
        }
        val updated = baseOps + op
        pendingStepAfterRegen = -1 // sentinel: jump to the last operation step before DONE
        bufferOps = updated
    }

    /**
     * Appends a live Queue operation directly from the stage controls after validating
     * against the sequence tail (`tailQueueValues`), then jumps the playhead to the new step.
     */
    fun appendLiveQueueOp(op: QueueOp) {
        when (op) {
            is QueueOp.Enqueue -> if (!canAppendToBuffer(isStack = false)) return
            QueueOp.Dequeue -> if (!canRemoveFromBuffer(isStack = false)) return
        }
        val baseOps = if (queueOps.isNotEmpty()) {
            queueOps
        } else {
            AlgorithmStepRepository.defaultQueueOps()
        }
        val updated = baseOps + op
        pendingStepAfterRegen = -1
        queueOps = updated
    }
}

/**
 * Factory + playback [LaunchedEffect] are paired here so the screen
 * doesn't have to know about either. Returning a [VisualizerScreenState]
 * from `remember` plus driving the playback tick from a single
 * `LaunchedEffect` keeps recomposition costs stable regardless of
 * how many regions compose the state.
 */
@Composable
fun rememberVisualizerScreenState(algorithm: Algorithm): VisualizerScreenState {
    val state = remember(algorithm) { VisualizerScreenState(algorithm) }

    // Regenerate steps when the algorithm or any user-customized
    // input changes (array values, sort order, buffer op list,
    // graph config, or interactive customGraph).
    LaunchedEffect(
        algorithm,
        state.arrayData,
        state.lastAppliedSortOrder,
        state.searchTarget,
        state.bufferOps,
        state.queueOps,
        state.graphConfig,
        state.customGraph,
        state.graphStartNodeId,
        state.graphTargetNodeId,
    ) {
        val heapValues = (state.graphConfig as? GraphCustomization.ForHeap)?.values
        val effectiveInput = if (algorithm.id == AlgorithmId.HEAP && !heapValues.isNullOrEmpty()) {
            heapValues
        } else {
            state.arrayData
        }
        val bstValues = (state.graphConfig as? GraphCustomization.ForBst)?.values
            ?: AlgorithmStepRepository.defaultBstValues
        val bstSearchKey = (state.graphConfig as? GraphCustomization.ForBst)?.searchKey
            ?: AlgorithmStepRepository.defaultBstSearchKey
        val traversalStart = state.graphStartNodeId ?: state.effectiveTraversalStartNodeId.takeIf { it.isNotEmpty() }
        val traversalTarget = state.graphTargetNodeId

        state.steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            algorithm,
            inputArray = effectiveInput,
            sortOrder = state.lastAppliedSortOrder,
            searchTarget = state.searchTarget,
            bufferOps = state.bufferOps,
            queueOps = state.queueOps,
            bstValues = bstValues,
            bstSearchKey = bstSearchKey,
            traversalStartNodeId = traversalStart ?: "A",
            targetNodeId = traversalTarget,
            customGraph = state.customGraph,
            customCoordinates = state.userCustomCoordinates,
        )
        val targetIdx = state.pendingStepAfterRegen
        state.pendingStepAfterRegen = null
        if (targetIdx != null && state.steps.isNotEmpty()) {
            // A target was requested (live Stack/Queue op appended): land the
            // playhead on the freshly generated operation. The -1 sentinel
            // resolves to `lastIndex - 1`, which is correct by construction —
            // the buffer generators always end with a single "…sequence
            // complete" DONE frame, so the frame before it is the last op.
            // Other input changes (array values, sort order, graph config)
            // carry no target and intentionally fall through to reset():
            // a full re-run deserves a fresh start at step 0.
            val resolved = if (targetIdx < 0) {
                (state.steps.lastIndex - 1).coerceAtLeast(0)
            } else {
                targetIdx.coerceIn(0, (state.steps.lastIndex - 1).coerceAtLeast(0))
            }
            state.scrubTo(resolved)
        } else {
            state.reset()
            // The previous question/selection was scored against the *old*
            // step stream; keeping it in flight would let a stale answer be
            // evaluated against regenerated steps. Drop it so Challenge Mode
            // re-poses against the new stream.
            state.updateChallenge { it.copy(selectedIndices = emptySet(), feedback = null) }
        }
    }

    // Playback tick — single source of "is the animation advancing?".
    LaunchedEffect(state.isPlaying, state.playbackSpeedMs) {
        while (state.isPlaying) {
            delay(state.playbackSpeedMs)
            if (state.isPlaying) {
                if (state.currentStepIdx < state.steps.lastIndex) {
                    state.advance()
                } else {
                    // Reached the end — stop playing so the rail UI updates.
                    state.togglePlay()
                }
            }
        }
    }

    // Sync cell scale & default playback speed when changed in Settings
    LaunchedEffect(AppSettings.defaultCellScale) {
        state.applyCellScale(AppSettings.defaultCellScale)
    }
    LaunchedEffect(AppSettings.defaultPlaybackSpeedMs) {
        state.playbackSpeedMs = AppSettings.defaultPlaybackSpeedMs
    }

    return state
}
