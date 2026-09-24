package com.example.algolens.ui.visualizer

/**
 * The pages of the bottom [InstrumentDeck] — the single bottom-anchored surface
 * that replaced the permanently-docked 3-line terminal.
 *
 * Deliberately two pages, not three. A "Study" page (theory / tutor / challenge)
 * was designed and then cut: all three of those affordances already have exactly
 * one home each (the header kebab's theory row, the rail's tutor button, the
 * rail's challenge button), so a deck page would have re-created the duplicate
 * affordances this pass exists to remove.
 *
 * Labels are sentence case — never ALL-CAPS (M1).
 */
enum class DeckPage(val label: String) {
    /** Syntax-highlighted source listing, scrolled to the active line. */
    TRACE("Trace"),

    /** Live variable readout + recursion / call-stack depth. */
    STATE("State")
}
