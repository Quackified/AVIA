package com.example.algolens

import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.PointerBannerOverlay
import com.example.algolens.ui.visualizer.RecursionTreeOverlay
import com.example.algolens.ui.visualizer.WeightBadgeOverlay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VisualizerOverlayTest {

    @Test
    fun mergeSort_specHasNoRecursionTreeOverlay() {
        // The bespoke MergeSortVisualizer owns the recursion tree
        // as the main view; the popover RecursionTreeOverlay is
        // no longer attached. The composable still exists (it's
        // reusable for any future algorithm) but is not in any
        // spec's overlays list.
        val spec = AlgorithmRegistry.specFor(AlgorithmId.MERGE_SORT)
        assertNotNull("Merge Sort must be in the registry", spec)
        assertTrue(
            "Merge Sort must not carry RecursionTreeOverlay (bespoke visualizer replaces it)",
            spec!!.overlays.none { it is RecursionTreeOverlay }
        )
        // Sanity: no spec currently attaches RecursionTreeOverlay.
        for (id in AlgorithmId.values()) {
            val other = AlgorithmRegistry.specFor(id) ?: continue
            assertTrue(
                "$id must not carry RecursionTreeOverlay",
                other.overlays.none { it is RecursionTreeOverlay }
            )
        }
    }

    @Test
    fun graphTraversal_specsIncludeWeightBadgeOverlay() {
        for (id in listOf(AlgorithmId.BFS, AlgorithmId.DFS)) {
            val spec = AlgorithmRegistry.specFor(id)
            assertNotNull("$id must be in the registry", spec)
            assertTrue(
                "$id spec must include WeightBadgeOverlay",
                spec!!.overlays.any { it is WeightBadgeOverlay }
            )
        }
        // Non-graph algorithms must not carry the weight-badge overlay.
        for (id in AlgorithmId.values()) {
            if (id == AlgorithmId.BFS || id == AlgorithmId.DFS) continue
            val other = AlgorithmRegistry.specFor(id) ?: continue
            assertTrue(
                "$id must not carry WeightBadgeOverlay",
                other.overlays.none { it is WeightBadgeOverlay }
            )
        }
    }

    @Test
    fun pointerBannerOverlay_isNotWiredYet() {
        // Extraction-prep: the overlay is built but not yet attached
        // to any spec. The actual extraction lands in a follow-up PR.
        for (id in AlgorithmId.values()) {
            val spec = AlgorithmRegistry.specFor(id) ?: continue
            assertTrue(
                "$id must not carry PointerBannerOverlay (extraction is a follow-up)",
                spec.overlays.none { it is PointerBannerOverlay }
            )
        }
    }

    @Test
    fun pointerBannerOverlay_buildOffscreenTargets_matchesInlineSemantics() {
        // Locks the behavior contract: the extraction PR replaces the
        // inline `derivedStateOf` in `CellArrayVisualizer` with a call
        // to this helper. The two must agree on a non-trivial case.
        val step = com.example.algolens.ui.visualizer.VisualizerStep(
            stepIndex = 0,
            array = listOf(5, 3, 8, 1, 9, 2, 7, 4, 6),
            topPointers = mapOf("L" to 0, "R" to 8, "pivot" to 4),
            bottomPointers = mapOf("i" to 0, "j" to 8),
            elementStates = mapOf(4 to ElementState.PIVOT, 7 to ElementState.SWAPPING),
        )
        // Visible window is the middle of the array; pointers at 0, 7,
        // 8 are outside; 4 is inside.
        val visible = setOf(2, 3, 4, 5, 6)

        val targets = PointerBannerOverlay.buildOffscreenTargets(step, visible)

        // First write wins (top pointers iterate first, dedup by index).
        // 0 comes from L (top), 8 from R (top), 7 from SWAPPING (state).
        val indices = targets.map { it.index }.toSet()
        assertEquals("Offscreen indices", setOf(0, 7, 8), indices)

        // The L target at index 0 should carry the "L" label, not "i" —
        // the top-pointer pass ran first and dedup'd.
        val lTarget = targets.firstOrNull { it.index == 0 }
        assertNotNull("Index 0 must produce a target", lTarget)
        assertEquals(
            "Index 0 target must be from the top-pointer pass (L, not i)",
            "L", lTarget!!.label
        )

        // SWAPPING at index 7 is outside the visible window — must
        // produce a "swap" target.
        val swap = targets.firstOrNull { it.label == "swap" && it.index == 7 }
        assertNotNull("Out-of-view SWAPPING cell must produce a target", swap)
    }

    @Test
    fun overlay_summariesAreInformative() {
        // Sanity check: every concrete overlay class has a `KClass.simpleName`
        // that matches the file name. Catches future typo'd classes
        // (e.g. someone naming a class `RecusionTreeOverlay`).
        assertEquals(
            "RecursionTreeOverlay",
            RecursionTreeOverlay::class.simpleName
        )
        assertEquals(
            "WeightBadgeOverlay",
            WeightBadgeOverlay::class.simpleName
        )
        assertEquals(
            "PointerBannerOverlay",
            PointerBannerOverlay::class.simpleName
        )
    }
}
