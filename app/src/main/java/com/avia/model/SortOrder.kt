package com.avia.model

/**
 * Direction a sorting / search algorithm orders its comparisons.
 *
 *   - [ASC] — the algorithm finds the **smallest** element at the head of
 *     the list and the **largest** at the tail. Default for all sort
 *     generators; matches the "smallest first, biggest last" intuition
 *     most teaching materials use.
 *   - [DESC] — the algorithm finds the **largest** element at the head
 *     and the **smallest** at the tail. Flips every comparison
 *     direction inside the step generators.
 *
 * The user picks this in the [com.avia.ui.visualizer.CustomizeInputSheet]
 * and it is threaded into the step generator so the comparison
 * expressions and final array both match the chosen direction.
 */
enum class SortOrder { ASC, DESC }
