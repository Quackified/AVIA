package com.example.algolens.ui.visualizer

import androidx.compose.ui.graphics.Color
import com.example.algolens.ui.theme.AlgoTokens

/**
 * Semantic accent families for code-trace lines, ordered by precedence.
 *
 * Each entry is a (keyword, accent) pair. The first match in this list wins
 * for any line, so high-priority operations (swap / pivot) override
 * catch-all matches. The accent colours are the same [AlgoTokens] used by
 * the visualizer cells, pointer badges, and challenge halo — so when a
 * `swap` line is active the code-trace pill, the per-line rail, the
 * InlineVarChip and the in-cell swap tint all share the same pink.
 *
 * Precedence (top wins):
 *  1. SWAP     — pink   (mutation: `swap`, `arr.swap`, `std::swap`)
 *  2. PIVOT    — yellow (selection pivot / binary search mid)
 *  3. KEY      — purple (insertion sort key, merge helper, `arr[j+1] = key`)
 *  4. SORTED   — green  (sorted/verified state)
 *  5. FOUND    — green  (binary/linear search hit / early return)
 *  6. else     — cyan   (catch-all: scans, assignments, returns, compares)
 */
internal object CodeLineAccent {

    /** A keyword + the accent colour that represents its semantic family. */
    private data class Family(val keywords: List<String>, val accent: Color)

    /**
     * Ordered family list. The first family whose keywords match the line
     * (case-insensitive substring) wins. Substrings are checked as
     * whole-word matches against common operator / identifier characters
     * to avoid false positives like `temp` matching `tmp`.
     *
     * COMPARE keywords fall through to the cyan default — reads are
     * semantically neutral, so they share the same accent as assignments
     * and scans. SWAP and PIVOT are the two operation families that
     * deserve distinct visual treatment.
     */
    private val families: List<Family> = listOf(
        Family(listOf("swap", "std::swap"), AlgoTokens.accentPink),
        Family(listOf("pivot", "mid"), AlgoTokens.accentYellow),
        Family(listOf("key", "arr[j + 1] = key"), AlgoTokens.accentPurple),
        Family(listOf("sorted", "verify"), AlgoTokens.accentGreen),
        Family(listOf("return", "found", "return mid", "return -1"), AlgoTokens.accentGreen),
    )

    private val defaultAccent: Color = AlgoTokens.accentCyan

    /**
     * Resolve the accent for a single code line. Returns the highest-precedence
     * matching family, or [defaultAccent] (cyan) if no family matches.
     */
    fun resolve(rawLine: String): Color {
        for (family in families) {
            if (family.keywords.any { keyword -> rawLine.contains(keyword, ignoreCase = true) }) {
                return family.accent
            }
        }
        return defaultAccent
    }
}
