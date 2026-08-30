package com.example.algolens

import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.visualizer.auxiliary.MergeBufferRow
import com.example.algolens.ui.visualizer.auxiliary.PhaseStrip
import com.example.algolens.ui.visualizer.auxiliary.RecursionBands
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Auxiliary component wiring tests.
 *
 * The "exquisite" Merge Sort visualizer uses three new structural
 * auxiliaries (RecursionBands, PhaseStrip, MergeBufferRow) declared
 * on the spec. Binary Search, Quick Sort, and the rest of the
 * catalogue don't use them. These tests assert the wiring so a
 * future accidental removal of an auxiliary shows up in CI rather
 * than on a device.
 */
class AuxiliaryComponentTest {

    @Test
    fun mergeSort_specHasNoAuxiliaries() {
        // Merge Sort renders through the standard LINEAR_1D family
        // (CellArrayVisualizer / BarVisualizer). The old bands /
        // phase / buffer row auxiliaries and the bespoke
        // MergeSortVisualizer are gone — no auxiliaries attached.
        val spec = AlgorithmRegistry.specFor(AlgorithmId.MERGE_SORT)
        assertNotNull("Merge Sort must be in the registry", spec)
        assertTrue(
            "Merge Sort must have zero auxiliary components (standard renderer)",
            spec!!.auxiliaryComponents.isEmpty()
        )
    }

    @Test
    fun nonMergeSort_specsHaveNoAuxiliaries() {
        // Every algorithm except Merge Sort should have an empty
        // auxiliaryComponents list. Quick Sort, Binary Search,
        // BFS, etc. don't have a custom auxiliary layout.
        for (id in AlgorithmId.values()) {
            if (id == AlgorithmId.MERGE_SORT) continue
            val spec = AlgorithmRegistry.specFor(id) ?: continue
            assertTrue(
                "$id must not declare auxiliary components",
                spec.auxiliaryComponents.isEmpty()
            )
        }
    }

    @Test
    fun dimOutOfRangeCells_isTrueForDivideAndConquer() {
        // Binary Search, Quick Sort and Merge Sort all carry an
        // activeRange that narrows during playback. The dimmer
        // applies to all three (cells and bars modes).
        for (id in listOf(AlgorithmId.BINARY_SEARCH, AlgorithmId.QUICK_SORT, AlgorithmId.MERGE_SORT)) {
            val spec = AlgorithmRegistry.specFor(id)!!
            assertTrue(
                "$id must have dimOutOfRangeCells=true",
                spec.dimOutOfRangeCells
            )
        }
    }

    @Test
    fun dimOutOfRangeCells_isFalseForLinearSearch() {
        // Linear Search has no narrowing range; the dimmer is a
        // no-op. Sanity check that the flag isn't set on every
        // algorithm.
        val spec = AlgorithmRegistry.specFor(AlgorithmId.LINEAR_SEARCH)!!
        assertFalse(
            "Linear Search must not opt into dimOutOfRangeCells",
            spec.dimOutOfRangeCells
        )
    }

    @Test
    fun mergeSort_everyStepHasAncestorRanges() {
        // The recursion-band strip reads `step.ancestorRanges`. If a
        // step forgets to populate it, the strip renders nothing.
        // Lock the contract: every emit has a non-empty list.
        val input = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
        val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
            com.example.algolens.model.Algorithm(id = AlgorithmId.MERGE_SORT),
            input
        )
        assertTrue("Merge Sort must generate at least one step", steps.isNotEmpty())
        for ((i, step) in steps.withIndex()) {
            assertTrue(
                "Step $i must have at least one ancestor range",
                step.ancestorRanges.isNotEmpty()
            )
            // The first entry must be the root range.
            assertEquals(
                "Step $i root range must be 0 until input.size",
                0 until input.size,
                step.ancestorRanges.first()
            )
        }
    }
}
