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
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.AppSettings
import com.example.algolens.model.Algorithm
import com.example.algolens.model.BufferOp
import com.example.algolens.model.GraphCustomization
import com.example.algolens.model.QueueOp
import com.example.algolens.model.SortOrder
import kotlinx.coroutines.delay

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
    var playbackSpeedMs: Long by mutableLongStateOf(1_000L)

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

    // ── Buffer customize state (Stack / Queue). When the user
    //    applies an operation list in the customize sheet, this
    //    holds it so the step generator re-runs on the new list. ──
    var bufferOps: List<BufferOp> by mutableStateOf(emptyList())
    var queueOps: List<QueueOp> by mutableStateOf(emptyList())

    // ── Graph customize state (BST / Heap / BFS / DFS). The sheet
    //    emits a `GraphCustomization` (ForHeap | ForBst | ForTraversal)
    //    which is stored here. The step generator dispatch reads
    //    the active variant. ──
    var graphConfig: GraphCustomization? by mutableStateOf(null)

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

    /** Safe to read regardless of [steps] size. */
    val currentStep: VisualizerStep
        get() = if (steps.isEmpty()) VisualizerStep() else steps[displayStepIdx.coerceIn(0, steps.lastIndex)]

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
    fun togglePlay() { isPlaying = !isPlaying }
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
    // graph config). The re-key list deliberately includes every
    // input the step generator dispatch in AlgorithmStepRepository
    // can read.
    LaunchedEffect(
        algorithm,
        state.arrayData,
        state.lastAppliedSortOrder,
        state.searchTarget,
        state.bufferOps,
        state.queueOps,
        state.graphConfig,
    ) {
        // Pull graph-related values out of the GraphCustomization for
        // clean per-parameter forwarding.
        val bstValues = (state.graphConfig as? GraphCustomization.ForBst)?.values ?: listOf(50, 30, 70, 20, 40, 60, 80)
        val bstSearchKey = (state.graphConfig as? GraphCustomization.ForBst)?.searchKey ?: 40
        val traversalStart = (state.graphConfig as? GraphCustomization.ForTraversal)?.startNodeId ?: "A"

        state.steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            algorithm,
            inputArray = state.arrayData,
            sortOrder = state.lastAppliedSortOrder,
            searchTarget = state.searchTarget,
            bufferOps = state.bufferOps,
            queueOps = state.queueOps,
            bstValues = bstValues,
            bstSearchKey = bstSearchKey,
            traversalStartNodeId = traversalStart,
        )
        state.reset()
    }

    // Playback tick  single source of "is the animation advancing?".
    LaunchedEffect(state.isPlaying, state.playbackSpeedMs) {
        while (state.isPlaying) {
            delay(state.playbackSpeedMs)
            if (state.isPlaying) {
                if (state.currentStepIdx < state.steps.lastIndex) {
                    state.advance()
                } else {
                    // Reached the end  stop playing so the rail UI updates.
                    state.togglePlay()
                }
            }
        }
    }

    // Sync cell scale when changed in Settings
    LaunchedEffect(AppSettings.defaultCellScale) {
        state.applyCellScale(AppSettings.defaultCellScale)
    }

    return state
}
