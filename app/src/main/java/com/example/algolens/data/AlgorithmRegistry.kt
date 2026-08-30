package com.example.algolens.data

import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.AlgorithmSpec
import com.example.algolens.model.InputKind
import com.example.algolens.model.VisualizerFamily
import com.example.algolens.ui.visualizer.WeightBadgeOverlay

/**
 * Typed registry of [AlgorithmSpec] entries. The single source of truth
 * for *which* algorithms exist, *which* visualizer family they belong to,
 * and *which* input they default to. The repository, the visualizer
 * host, the dashboard and the test suite all consult this map — no
 * string-typed lookups anywhere.
 *
 * Adding a new algorithm is now a two-step change:
 *   1. Add a new [AlgorithmId] enum constant with display metadata.
 *   2. Append one [AlgorithmSpec] entry in [specs] below.
 *
 * The repository will pick it up via [specFor] / [idFor] without further
 * code changes.
 */
object AlgorithmRegistry {

    private val specs: Map<AlgorithmId, AlgorithmSpec> = mapOf(
        // ── Sorting (5) ──
        AlgorithmId.BUBBLE_SORT to AlgorithmSpec(
            id = AlgorithmId.BUBBLE_SORT,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT,
            supportsCustomInput = true,
        ),
        AlgorithmId.SELECTION_SORT to AlgorithmSpec(
            id = AlgorithmId.SELECTION_SORT,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT,
            supportsCustomInput = true,
        ),
        AlgorithmId.INSERTION_SORT to AlgorithmSpec(
            id = AlgorithmId.INSERTION_SORT,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT,
            supportsCustomInput = true,
        ),
        AlgorithmId.MERGE_SORT to AlgorithmSpec(
            id = AlgorithmId.MERGE_SORT,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT,
            supportsCustomInput = true,
            // The bespoke MergeSortVisualizer owns the full layout
            // (recursion tree + phase strip + merge detail). No
            // overlays, no auxiliaries, no dimmer — the cells/bars
            // toggle in the header is hidden because this algorithm
            // is in the MERGE_SORT family, not LINEAR_1D.
        ),
        AlgorithmId.QUICK_SORT to AlgorithmSpec(
            id = AlgorithmId.QUICK_SORT,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT,
            supportsCustomInput = true,
            dimOutOfRangeCells = true,
        ),

        // ── Searching (2) ──
        AlgorithmId.LINEAR_SEARCH to AlgorithmSpec(
            id = AlgorithmId.LINEAR_SEARCH,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT,
            supportsCustomInput = true,
        ),
        AlgorithmId.BINARY_SEARCH to AlgorithmSpec(
            id = AlgorithmId.BINARY_SEARCH,
            // The visualizer sorts before searching; both 1..9 and the
            // mixed DEFAULT_INPUT are valid starting points.
            defaultInput = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
            supportsCustomInput = true,
            dimOutOfRangeCells = true,
        ),

        // ── Data Structures (4) ──
        AlgorithmId.STACK to AlgorithmSpec(
            id = AlgorithmId.STACK,
            defaultInput = emptyList(),
            supportsCustomInput = false,
            isStack = true,
        ),
        AlgorithmId.QUEUE to AlgorithmSpec(
            id = AlgorithmId.QUEUE,
            defaultInput = emptyList(),
            supportsCustomInput = false,
            isStack = false,
        ),
        AlgorithmId.BINARY_SEARCH_TREE to AlgorithmSpec(
            id = AlgorithmId.BINARY_SEARCH_TREE,
            defaultInput = emptyList(),
            supportsCustomInput = false,
        ),
        AlgorithmId.HEAP to AlgorithmSpec(
            id = AlgorithmId.HEAP,
            defaultInput = AlgorithmStepRepository.DEFAULT_INPUT.take(7),
            supportsCustomInput = false,
        ),

        // ── Graph Traversal (2) ──
        AlgorithmId.BFS to AlgorithmSpec(
            id = AlgorithmId.BFS,
            defaultInput = emptyList(),
            supportsCustomInput = false,
            overlays = listOf(WeightBadgeOverlay),
        ),
        AlgorithmId.DFS to AlgorithmSpec(
            id = AlgorithmId.DFS,
            defaultInput = emptyList(),
            supportsCustomInput = false,
            overlays = listOf(WeightBadgeOverlay),
        ),
    )

    /** Returns the [AlgorithmSpec] for [id], or null if the algorithm isn't registered. */
    fun specFor(id: AlgorithmId): AlgorithmSpec? = specs[id]

    /** Returns every registered spec, in the order they should be displayed. */
    fun allSpecs(): List<AlgorithmSpec> = specs.values.toList()

    /** Returns every registered id. */
    fun allIds(): List<AlgorithmId> = specs.keys.toList()

    /**
     * Returns the [VisualizerFamily] for an [AlgorithmId], defaulting to
     * the family declared on the [AlgorithmId] itself if the spec is
     * missing. This makes the lookup safe for partial registries and
     * matches what the visualizer would have chosen before the refactor.
     */
    fun familyFor(id: AlgorithmId): VisualizerFamily =
        specFor(id)?.id?.family ?: id.family

    /** Whether the algorithm accepts a user-customised input array. */
    fun supportsCustomInput(id: AlgorithmId): Boolean =
        specFor(id)?.supportsCustomInput ?: (id.inputKind == InputKind.ARRAY)
}
