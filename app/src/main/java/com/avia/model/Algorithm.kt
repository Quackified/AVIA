package com.avia.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.toArgb

/**
 * Lightweight UI handle for an algorithm, kept here so existing call sites
 * that pass an `Algorithm` around (Dashboard → Visualizer, Profile,
 * Practice, etc.) keep working. All *behavioural* decisions are routed
 * through [id] → [com.avia.data.AlgorithmRegistry] instead of
 * branching on string fields.
 *
 * `Algorithm.id` was previously an Int sequence (1..13). It is now an
 * [AlgorithmId] so the rest of the app can switch on a stable enum
 * instead of guessing from a free-form name. The old [name], [category],
 * [timeComplexity], [spaceComplexity], [difficulty] and [colorHex]
 * accessors are preserved as derived properties so call sites that read
 * them (Dashboard, Profile, AlgoCard) keep compiling.
 */
@Immutable
data class Algorithm(
    val id: AlgorithmId,
    val name: String = id.displayName,
    val category: String = id.categoryLabel,
    val timeComplexity: String = id.timeComplexity,
    val spaceComplexity: String = id.spaceComplexity,
    val difficulty: String = id.difficulty,
    val colorHex: String = id.toHex(),
)

private fun AlgorithmId.toHex(): String {
    // Compose's `Color.toArgb()` returns a packed ARGB int, which is the
    // exact format `android.graphics.Color.parseColor` expects.
    val argb = this.accent.toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    return "#%02X%02X%02X".format(r, g, b)
}


