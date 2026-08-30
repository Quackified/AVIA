package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Bespoke Merge Sort visualizer.
 *
 * Replaces the standard cell row with a recursion-tree-as-canvas
 * view: the tree is the dominant visual element, with the merge
 * detail panel below it during the merge phase. Tapping a tree
 * node jumps playback to the step where that node is processed.
 *
 * The visualizer reads only step metadata — no algorithm-name
 * string matches. All state is derived from `step.recursionDepth`,
 * `step.ancestorRanges`, `step.activeRange`, `step.mergeBlocks`,
 * `step.auxiliaryArray`, and `step.auxiliaryIndices`.
 *
 * The cells/bars toggle in the header is hidden automatically
 * because [VisualizerFamily.MERGE_SORT] is not [LINEAR_1D], so
 * [HeaderModeRow] skips rendering.
 */
@Composable
fun MergeSortVisualizer(
    spec: com.example.algolens.model.AlgorithmSpec,
    currentStep: VisualizerStep,
    state: VisualizerScreenState,
    modifier: Modifier = Modifier
) {
    val n = currentStep.array.size.coerceAtLeast(1)

    // Build the tree by walking the same recursion the algorithm
    // uses (`m = (l + r) / 2` to split). The resulting per-level
    // ranges exactly match the `activeRange` the step generator
    // emits, so `currentNode` detection works. Earlier versions of
    // this code used a "complete binary tree, 2^d nodes per depth"
    // shape that produced ranges the algorithm never visited
    // (e.g. `[8..9]` for n=9) — and the subsequent
    // `array.subList(range.last+1, …)` on those out-of-bounds
    // ranges threw `IndexOutOfBoundsException`, crashing the app.
    val levels: List<List<IntRange>> = remember(n) {
        val out = mutableListOf<MutableList<IntRange>>()
        fun walk(l: Int, r: Int, depth: Int) {
            while (out.size <= depth) out.add(mutableListOf())
            out[depth].add(l..r)
            if (l < r) {
                val m = (l + r) / 2
                walk(l, m, depth + 1)
                walk(m + 1, r, depth + 1)
            }
        }
        if (n > 0) walk(0, n - 1, 0)
        out.map { it.toList() }
    }
    val maxDepth = (levels.size - 1).coerceAtLeast(0)
    // `nodeRanges` is now `levels` — keep the alias for readability
    // at the call sites.
    val nodeRanges: List<List<IntRange>> = levels

    // State per node: ABSENT (not yet visited), DIVIDING,
    // MERGING, MERGE_COMPLETE, SORTED, DONE.
    val nodeStates: List<List<NodeState>> = remember(currentStep, nodeRanges) {
        nodeRanges.mapIndexed { depth, rangesAtDepth ->
            rangesAtDepth.map { range ->
                nodeStateFor(currentStep, depth, range, maxDepth)
            }
        }
    }

    // The current node is the one whose range matches the current
    // active range at the current depth.
    val currentNode: Pair<Int, Int>? = remember(currentStep) {
        val depth = currentStep.recursionDepth
        if (depth in 0..maxDepth) {
            val rangesAtDepth = nodeRanges.getOrNull(depth) ?: return@remember null
            val idx = rangesAtDepth.indexOfFirst { it == currentStep.activeRange }
            if (idx >= 0) depth to idx else null
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CardBackground)
            .padding(AlgoTokens.space3),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        // ── Phase strip (top, full width) ──
        PhaseStripRow(step = currentStep)

        // ── Main row: merge animation on the left, recursion tree
        // thumbnail on the right. The left panel is the workhorse
        // (the merge operation is what makes merge sort
        // interesting); the right panel is a navigational aid
        // showing where the algorithm is in the recursion. ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            // Left: merge animation.
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                MergeAnimationPanel(
                    step = currentStep,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right: recursion tree (smaller Canvas, but same
            // centered-tree code as before).
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                    .background(CardBackground)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
            ) {
                RecursionTreeCanvas(
                    levels = levels,
                    maxDepth = maxDepth,
                    nodeStates = nodeStates,
                    currentNode = currentNode,
                    currentStep = currentStep,
                    onNodeTap = { depth, range ->
                        val target = state.steps.indexOfFirst {
                            it.activeRange == range && it.phaseLabel.equals("DIVIDING", ignoreCase = true)
                        }
                        if (target >= 0) state.scrubTo(target)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * The state of a single tree node at `(depth, range)`. Derived
 * from the current step's `activeRange` and `ancestorRanges` —
 * the algorithm emits an `ancestorRanges` for every step, so
 * "this node has been visited" is "this range is in
 * ancestorRanges or matches activeRange."
 */
private enum class NodeState { ABSENT, DIVIDING, MERGING, MERGE_COMPLETE, SORTED, DONE }

private fun nodeStateFor(
    step: VisualizerStep,
    depth: Int,
    range: IntRange,
    maxDepth: Int,
): NodeState {
    val active = step.activeRange
    val phase = step.phaseLabel.uppercase()

    if (active == range) {
        return when {
            phase.startsWith("DIVID") || phase.startsWith("INIT") -> NodeState.DIVIDING
            phase.startsWith("MERG") || phase.startsWith("FLUSH") -> NodeState.MERGING
            phase.startsWith("MERGE COMPLETE") -> NodeState.MERGE_COMPLETE
            phase.startsWith("SORTED") -> NodeState.SORTED
            else -> NodeState.DIVIDING
        }
    }
    // The node is an ancestor of the current step.
    if (step.ancestorRanges.size > depth && step.ancestorRanges[depth] == range) {
        // If we're past this ancestor (current depth > this depth),
        // it's "done"; otherwise the algorithm is on the way down.
        return if (step.recursionDepth > depth) NodeState.DONE else NodeState.DIVIDING
    }
    // Children of ancestors that have been completed.
    if (depth <= step.recursionDepth + 1 && step.recursionDepth >= depth - 1 && isSubrangeInPath(range, step.ancestorRanges)) {
        return NodeState.DONE
    }
    return NodeState.ABSENT
}

private fun isSubrangeInPath(range: IntRange, path: List<IntRange>): Boolean {
    for (ancestor in path) {
        if (range.first >= ancestor.first && range.last <= ancestor.last) return true
    }
    return false
}

/**
 * The phase strip — single chip row showing phase, range, blocks,
 * and depth. Same shape as the previous PhaseStrip auxiliary but
 * inlined since this visualizer owns its full layout.
 */
@Composable
private fun PhaseStripRow(step: VisualizerStep) {
    val phase = step.phaseLabel
    val upper = phase.uppercase()
    val (accent, container, border) = when {
        upper.startsWith("DIVID") || upper.startsWith("INIT") -> Triple(
            PrimaryCyan, CyanSubtle.copy(alpha = 0.18f), PrimaryCyan.copy(alpha = 0.3f)
        )
        upper.startsWith("MERGE COMPLETE") || upper.startsWith("SORTED") -> Triple(
            AccentGreen, AlgoTokens.greenFill.copy(alpha = 0.18f), AccentGreen.copy(alpha = 0.3f)
        )
        else -> Triple(
            PurpleGlow, PurpleSubtle, SecondaryPurple.copy(alpha = 0.3f)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhaseChip(phase, accent, container, border)
        step.activeRange?.let { range ->
            PhaseChip("[${range.first}..${range.last}]", accent, container, border)
        }
        if (step.mergeBlocks.size >= 2) {
            val parts = step.mergeBlocks.joinToString(" + ") { "[${it.first}..${it.last}]" }
            PhaseChip(parts, accent, container, border)
        }
        PhaseChip("DEPTH ${step.recursionDepth}", accent, container, border)
    }
}

@Composable
private fun PhaseChip(
    label: String,
    accent: Color,
    container: Color,
    border: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(container)
            .border(AlgoTokens.strokeThin, border, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * The recursion tree rendered as a Canvas. Each node is a
 * circle drawn via `drawCircle` + `drawText`. Tapping inside a
 * node's circle invokes the [onNodeTap] callback.
 *
 * Layout: centered binary tree. At depth `d` there are 2^d
 * "slots" evenly distributed across the canvas width; node
 * `i` sits at the i-th slot's center: `cx = (i + 0.5) / 2^d *
 * canvasWidth`. This puts the root at canvas-center (not the
 * left edge) and centers each row above its children.
 *
 * Active node: a breathing halo (two concentric rings, alpha
 * modulated by the [breathe] animation) and a thicker stroke.
 * Active path edges are drawn on top of the regular edges with
 * a brighter accent color.
 */
@Composable
private fun RecursionTreeCanvas(
    levels: List<List<IntRange>>,
    maxDepth: Int,
    nodeStates: List<List<NodeState>>,
    currentNode: Pair<Int, Int>?,
    currentStep: VisualizerStep,
    onNodeTap: (depth: Int, range: IntRange) -> Unit,
    modifier: Modifier = Modifier
) {
    // ── Halo breathing animation (same pattern as
    // GraphTreeVisualizer's activeHaloPulse). Drives the alpha
    // modulation of the active node's halo rings. 0..1
    // oscillating in a 1.6s loop. ──
    val transition = rememberInfiniteTransition(label = "treeHalo")
    val breathe by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "treeBreathe"
    )

    // ── Tap-to-jump hit-testing data. Compute node centers once
    // per recomposition, then the tap handler tests each one
    // against the touch point. ──
    val nodeCenters = remember(levels) {
        // Pairs of (depth, i) -> (centerX, centerY) as fractions
        // of canvas size. Final layout uses canvas-relative
        // fractions, so we recompute on size change inside
        // the Canvas's draw scope.
        mutableListOf<Pair<Triple<Int, Int, IntRange>, Offset>>()
    }

    val radiusPx = with(androidx.compose.ui.platform.LocalDensity.current) { 11.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(levels, currentStep) {
                    detectTapGestures { tap ->
                        // Hit-test: iterate all nodes, return the
                        // first whose circle contains the tap.
                        val cs = size
                        for ((key, center) in nodeCenters) {
                            val cx = center.x * cs.width
                            val cy = center.y * cs.height
                            val dx = tap.x - cx
                            val dy = tap.y - cy
                            if (dx * dx + dy * dy <= radiusPx * radiusPx) {
                                val (depth, i, range) = key
                                onNodeTap(depth, range)
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            val cs = size
            val rowCount = (maxDepth + 1).coerceAtLeast(1)
            val rowHeight = cs.height / rowCount

            // ── Centered binary-tree layout. At depth `d` there
            // are 2^d "slots" evenly distributed across the canvas
            // width; node `i` sits at the i-th slot's center. This
            // puts the root at canvas-center (not the left edge)
            // and centers each row above its children. The earlier
            // `(i + 0.5) * slotWidth` formula used the bottom
            // row's node count for every row, which anchored
            // the tree to the left half of the canvas. ──
            fun cx(depth: Int, indexInLevel: Int): Float {
                val slots = 1 shl depth
                return cs.width * (indexInLevel + 0.5f) / slots
            }

            // ── Pass 1: collect all node centers for hit-testing ──
            nodeCenters.clear()
            for (depth in 0..maxDepth) {
                val count = levels[depth].size
                for (i in 0 until count) {
                    val nodeCx = cx(depth, i)
                    val nodeCy = (depth + 0.5f) * rowHeight
                    nodeCenters += Triple(depth, i, levels[depth][i]) to
                        Offset(nodeCx / cs.width, nodeCy / cs.height)
                }
            }

            // ── Pass 2: draw all non-active edges first ──
            for (depth in 0 until maxDepth) {
                val parentCount = levels[depth].size
                val childCount = levels.getOrNull(depth + 1)?.size ?: 0
                if (parentCount == 0 || childCount == 0) continue
                // The recursion tree's children at depth d+1 are
                // laid out in the SAME order as they appear in the
                // algorithm's split (left child, right child for
                // each parent). For a complete binary tree, parent
                // i's children are at child indices 2i and 2i+1.
                // The actual tree walk produces the same order
                // (left sub-walk before right sub-walk), so the
                // direct mapping holds: parent i's first child is
                // at child index 2i (or, when the deepest level
                // has fewer than 2^maxDepth nodes, the mapping
                // is a bit different but still in-order).
                val childrenPerParent = (0 until childCount).groupBy { i ->
                    (i * parentCount) / childCount
                }
                for (parentI in 0 until parentCount) {
                    val parentX = cx(depth, parentI)
                    val parentY = (depth + 0.5f) * rowHeight
                    val children = childrenPerParent[parentI] ?: emptyList()
                    for (childI in children) {
                        val childX = cx(depth + 1, childI)
                        val childY = (depth + 1.5f) * rowHeight
                        drawLine(
                            color = BorderSubtle.copy(alpha = 0.5f),
                            start = Offset(parentX, parentY),
                            end = Offset(childX, childY),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }

            // ── Pass 3: draw all node circles (non-active first) ──
            val currentKey = currentNode?.let { (d, i) -> d to i }
            for (depth in 0..maxDepth) {
                val count = levels[depth].size
                for (i in 0 until count) {
                    if (currentKey == depth to i) continue
                    val nodeCx = cx(depth, i)
                    val nodeCy = (depth + 0.5f) * rowHeight
                    drawTreeNode(
                        centerX = nodeCx,
                        centerY = nodeCy,
                        radius = radiusPx,
                        range = levels[depth][i],
                        values = safeSubArray(currentStep.array, levels[depth][i]),
                        state = nodeStates[depth][i],
                        isCurrent = false,
                        breathe = breathe
                    )
                }
            }

            // ── Pass 4: draw the active node on top ──
            currentNode?.let { (depth, i) ->
                val nodeCx = cx(depth, i)
                val nodeCy = (depth + 0.5f) * rowHeight
                drawTreeNode(
                    centerX = nodeCx,
                    centerY = nodeCy,
                    radius = radiusPx,
                    range = levels[depth][i],
                    values = safeSubArray(currentStep.array, levels[depth][i]),
                    state = nodeStates[depth][i],
                    isCurrent = true,
                    breathe = breathe
                )
            }
        }
    }
}

/**
 * Extract a sub-list from the step's array, clamped to valid
 * indices. The recursion tree walk guarantees the range is in
 * bounds, but this guard prevents a crash if a future change
 * produces an out-of-bounds `activeRange`.
 */
private fun safeSubArray(array: List<Int>, range: IntRange): List<Int> {
    if (array.isEmpty()) return emptyList()
    val from = range.first.coerceIn(0, array.size)
    val to = (range.last + 1).coerceIn(from, array.size + 1)
    return if (to > from) array.subList(from, to) else emptyList()
}

/**
 * Draw a single tree node as a circle with a colored fill,
 * stroke, and label. The active node gets a breathing halo
 * (two concentric rings, alpha modulated by [breathe]).
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTreeNode(
    centerX: Float, centerY: Float, radius: Float,
    range: IntRange, values: List<Int>,
    state: NodeState, isCurrent: Boolean, breathe: Float
) {
    val (fillColor, strokeColor, textColor) = when (state) {
        NodeState.ABSENT -> Triple(
            CardBackground.copy(alpha = 0.30f),
            BorderSubtle.copy(alpha = 0.30f),
            TextMuted.copy(alpha = 0.40f)
        )
        NodeState.DIVIDING -> Triple(
            PurpleSubtle.copy(alpha = 0.35f),
            PurpleGlow.copy(alpha = 0.55f),
            PurpleGlow
        )
        NodeState.MERGING -> Triple(
            CyanSubtle.copy(alpha = 0.35f),
            PrimaryCyan.copy(alpha = 0.75f),
            PrimaryCyan
        )
        NodeState.MERGE_COMPLETE -> Triple(
            PurpleSubtle.copy(alpha = 0.45f),
            PurpleGlow.copy(alpha = 0.85f),
            PurpleGlow
        )
        NodeState.SORTED, NodeState.DONE -> Triple(
            AlgoTokens.greenFill.copy(alpha = 0.40f),
            AccentGreen.copy(alpha = 0.75f),
            AccentGreen
        )
    }

    // Active node: render the halo *behind* the circle. The
    // alpha is modulated by the breathing animation so the
    // halo gently pulses.
    if (isCurrent) {
        drawCircle(
            color = strokeColor.copy(alpha = 0.12f + 0.10f * breathe),
            radius = radius * 1.6f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = strokeColor.copy(alpha = 0.24f + 0.14f * breathe),
            radius = radius * 1.25f,
            center = Offset(centerX, centerY)
        )
    }

    // Solid circle.
    drawCircle(
        color = fillColor,
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // Stroke (thicker on the active node).
    drawCircle(
        color = strokeColor,
        radius = radius,
        center = Offset(centerX, centerY),
        style = Stroke(width = if (isCurrent) 2.5f else 1.4f)
    )

    // Label: range on top, value preview below.
    drawContext.canvas.nativeCanvas.apply {
        val textPaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
        // Range label
        textPaint.color = textColor.toArgb()
        textPaint.textSize = if (isCurrent) 9f else 7.5f
        drawText("[${range.first}..${range.last}]", centerX, centerY - 1f, textPaint)
        // Values preview (small, below the range)
        if (values.isNotEmpty()) {
            val preview = if (values.size <= 4) values.joinToString(",")
                          else values.take(3).joinToString(",") + ",…"
            textPaint.textSize = if (isCurrent) 8f else 6.5f
            textPaint.isFakeBoldText = false
            drawText(preview, centerX, centerY + 9f, textPaint)
        }
    }
}

/**
 * The merge animation panel — main view of the bespoke Merge
 * Sort visualizer. Renders three rows inside a single panel:
 *
 *  1. **Header**: "MERGE" label + the two source ranges.
 *  2. **L source row** with the `i` (cyan) pointer badge.
 *  3. **R source row** with the `j` (yellow) pointer badge.
 *  4. **Target row** with the `k` (green) pointer badge, only
 *     rendered when `auxiliaryArray` is non-null (i.e. during a
 *     merge step).
 *
 * The L and R rows render in the **divide phase** too, with
 * the source sub-arrays visible (L shows the left half, R
 * shows the right half). In the divide phase, the target row
 * is hidden, since the algorithm isn't merging yet.
 */
@Composable
private fun MergeAnimationPanel(
    step: VisualizerStep,
    modifier: Modifier = Modifier
) {
    val aux = step.auxiliaryArray
    val auxIndices = step.auxiliaryIndices

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusMd))
            .background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
            .padding(AlgoTokens.space3),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        // Header row: "MERGE" label + the two source ranges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MERGE",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 0.8.sp
            )
            step.mergeBlocks.takeIf { it.size == 2 }?.let { blocks ->
                val l1 = blocks[0].first to blocks[0].last
                val l2 = blocks[1].first to blocks[1].last
                Text(
                    text = "[${l1.first}..${l1.second}] + [${l2.first}..${l2.second}]",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // L source row: range + cells + i badge.
        if (step.mergeBlocks.size == 2) {
            val l1 = step.mergeBlocks[0].first to step.mergeBlocks[0].last
            val l2 = step.mergeBlocks[1].first to step.mergeBlocks[1].last
            val leftSize = l1.second - l1.first + 1
            val i = auxIndices["i"]
            val j = auxIndices["j"]
            val iIndex = i?.takeIf { it < leftSize }   // index into L's cells
            val jIndex = j?.let { it - leftSize }      // index into R's cells

            // L source row.
            SourceSubArray(
                label = "L",
                range = l1.first..l1.second,
                values = safeSubArray(step.array, l1.first..l1.second),
                pointerLabel = if (iIndex != null) "i" else null,
                pointerValue = iIndex,
                pointerColor = PrimaryCyan,
                modifier = Modifier.fillMaxWidth()
            )

            // R source row.
            SourceSubArray(
                label = "R",
                range = l2.first..l2.second,
                values = safeSubArray(step.array, l2.first..l2.second),
                pointerLabel = if (jIndex != null) "j" else null,
                pointerValue = jIndex,
                pointerColor = AccentYellow,
                modifier = Modifier.fillMaxWidth()
            )

            // "merge ↓" divider — only when a merge is in progress.
            if (aux != null && aux.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AlgoTokens.space4),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AlgoTokens.strokeHairline)
                            .background(BorderSubtle)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(CardBackground)
                            .padding(horizontal = AlgoTokens.space2)
                    ) {
                        Text(
                            text = "↓",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }
                }

                // Merged target row. Each cell carries the `k`
                // pointer when it matches auxIndices["k"].
                // Cells that have been filled by previous merge
                // steps are at full opacity; future cells are
                // dimmed.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in aux.indices) {
                        val value = aux[i]
                        val isK = auxIndices["k"] == i
                        val kIndex = auxIndices["k"] ?: -1
                        val isFilled = i < kIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(
                                    when {
                                        isK -> AlgoTokens.cyanFill
                                        isFilled -> AlgoTokens.glassFill
                                        else -> AlgoTokens.glassFill.copy(alpha = 0.4f)
                                    }
                                )
                                .border(
                                    AlgoTokens.strokeThin,
                                    if (isK) PrimaryCyan.copy(alpha = 0.7f) else BorderSubtle,
                                    RoundedCornerShape(AlgoTokens.radiusXs)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isK) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "k",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 6.sp
                                    )
                                    Text(
                                        text = value.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 8.sp
                                    )
                                }
                            } else if (isFilled) {
                                Text(
                                    text = value.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceSubArray(
    label: String,
    range: IntRange,
    values: List<Int>,
    pointerLabel: String?,
    pointerValue: Int?,
    pointerColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "$label [${range.first}..${range.last}]",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 7.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                .background(CardBackground)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                .padding(AlgoTokens.space1),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            values.forEachIndexed { idx, v ->
                val isPointed = pointerLabel != null && pointerValue == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (isPointed) pointerColor.copy(alpha = 0.25f) else AlgoTokens.glassFill)
                        .border(
                            AlgoTokens.strokeThin,
                            if (isPointed) pointerColor else BorderSubtle,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPointed) {
                        Text(
                            text = pointerLabel ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = pointerColor,
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = v.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}
