package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.algolens.ui.theme.AlgoTokens
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Data structure representing a directional traversal photon packet along an edge.
 */
@Stable
data class TraversalSignal(
    val fromNodeId: String,
    val toNodeId: String,
    val progress: Float // 0f to 1f
)

/**
 * Data structure representing an expanding ripple ring around an activated node.
 */
@Stable
data class NodeRipple(
    val nodeId: String,
    val progress: Float, // 0f to 1f
    val isTarget: Boolean = false
)

/**
 * Active motion state for GraphTreeRenderer.
 */
@Stable
interface GraphMotionState {
    /** Live center coordinates for a node ID (incorporates transverse swap arcs). */
    fun getNodeCenter(node: GraphNodeState, defaultCoords: Offset): Offset

    /** Live scale multiplier for a node ID (incorporates eval pop). */
    fun getNodeScale(nodeId: String): Float

    /** Active directional traversal signals traveling along edges. */
    val activeSignals: List<TraversalSignal>

    /** Active expanding ripples around nodes. */
    val activeRipples: List<NodeRipple>

    /** Number of illuminated edges in the sequential shortest-path neon trace. */
    val pathTraceProgress: Float

    /** Sequence of node IDs in the optimal completed path, if ready for neon trace. */
    val completedPath: List<String>
}

/**
 * Pure mathematical helper to compute transverse arc coordinates between two points.
 * Ensures swapping nodes bypass each other without visual collision.
 */
fun computeTransverseArcPosition(
    from: Offset,
    to: Offset,
    progress: Float,
    isFirstSwapper: Boolean,
    arcHeight: Float = 36f
): Offset {
    val t = progress.coerceIn(0f, 1f)
    val dx = to.x - from.x
    val dy = to.y - from.y
    val dist = sqrt(dx * dx + dy * dy)

    // Baseline straight-line interpolation
    val baseX = from.x + t * dx
    val baseY = from.y + t * dy

    if (dist < 1f) return Offset(baseX, baseY)

    // Unit normal vector perpendicular to displacement
    val nx = -dy / dist
    val ny = dx / dist

    // Half-sine wave arc peaking at t = 0.5
    val arcMag = arcHeight * sin(Math.PI.toFloat() * t)
    val sign = if (isFirstSwapper) 1f else -1f

    return Offset(
        x = baseX + sign * nx * arcMag,
        y = baseY + sign * ny * arcMag
    )
}

/**
 * Remembers and drives graph & tree animation physics across step changes:
 * 1. Heap & Tree swaps: Positional interpolation with transverse orbital clearance.
 * 2. Traversal signal wavefront: Photon packet traveling along edge vectors.
 * 3. Node activation: Scale pop (1.18x) and expanding hairline ripple ring.
 * 4. Shortest path completion: Sequential emerald/cyan neon beam tracing from start to target.
 * 5. Scrubber coordination: Snaps instantly to rest state without lingering artifacts when [isScrubbing].
 */
@Composable
fun rememberGraphMotionState(
    currentStep: VisualizerStep,
    previousStep: VisualizerStep?,
    startNodeId: String?,
    targetNodeId: String?,
    playbackSpeedMs: Long = 600L,
    isScrubbing: Boolean = false
): GraphMotionState {
    val nodePositionOffsets = remember { mutableStateMapOf<String, Offset>() }
    val nodeScales = remember { mutableStateMapOf<String, Float>() }
    var activeSignals by remember { mutableStateOf<List<TraversalSignal>>(emptyList()) }
    var activeRipples by remember { mutableStateOf<List<NodeRipple>>(emptyList()) }
    var pathTraceProgress by remember { mutableFloatStateOf(0f) }
    var completedPath by remember { mutableStateOf<List<String>>(emptyList()) }

    // Parse completed optimal path from step variables / comparisonExpr if found
    val parsedGoalPath = remember(currentStep) {
        val varPath = currentStep.variables["path"]
        if (!varPath.isNullOrBlank()) {
            varPath.split("→", "->", " ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        } else {
            val expr = currentStep.comparisonExpr ?: ""
            if (expr.startsWith("PATH:") || expr.startsWith("SHORTEST PATH:")) {
                expr.substringAfter(":")
                    .split("→", "->", " ")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            } else {
                emptyList()
            }
        }
    }

    LaunchedEffect(currentStep.stepIndex, isScrubbing, playbackSpeedMs) {
        if (isScrubbing) {
            // Rapid scrub / fast drag: Instant snap to rest state
            nodePositionOffsets.clear()
            nodeScales.clear()
            activeSignals = emptyList()
            activeRipples = emptyList()
            pathTraceProgress = if (parsedGoalPath.isNotEmpty()) parsedGoalPath.size.toFloat() else 0f
            completedPath = parsedGoalPath
            return@LaunchedEffect
        }

        // Relative speed duration scaling
        val durationMultiplier = when (playbackSpeedMs) {
            300L -> 0.5f
            1000L -> 1.67f
            else -> 1.0f
        }

        val stiffness = when (playbackSpeedMs) {
            300L -> Spring.StiffnessHigh
            1000L -> Spring.StiffnessLow
            else -> Spring.StiffnessMedium
        }
        val travelSpringSpec = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness)
        val evalSpringSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = stiffness)

        coroutineScope {
            // ── 1. Positional Swaps (Heap / Tree rebalancing) ──
            val prevNodes = previousStep?.nodes?.associateBy { it.id } ?: emptyMap()
            val currNodes = currentStep.nodes.associateBy { it.id }

            // Detect swap pairs: either explicitly SWAPPING or swapped values/positions
            val swappingNodeIds = currentStep.nodes
                .filter { it.state == ElementState.SWAPPING }
                .map { it.id }

            val isHeapSwap = swappingNodeIds.size >= 2 || (
                prevNodes.isNotEmpty() &&
                currentStep.phaseLabel.contains("SWAP", ignoreCase = true)
            )

            if (isHeapSwap && swappingNodeIds.size >= 2) {
                val idA = swappingNodeIds[0]
                val idB = swappingNodeIds[1]
                val nodeA = currNodes[idA]
                val nodeB = currNodes[idB]

                if (nodeA != null && nodeB != null) {
                    val posA = Offset(nodeA.x, nodeA.y)
                    val posB = Offset(nodeB.x, nodeB.y)

                    val swapAnim = Animatable(0f)
                    launch {
                        swapAnim.animateTo(1f, travelSpringSpec) {
                            val curT = value
                            // Node A glides from posB -> posA with +arc
                            val curA = computeTransverseArcPosition(posB, posA, curT, isFirstSwapper = true)
                            nodePositionOffsets[idA] = Offset(curA.x - posA.x, curA.y - posA.y)

                            // Node B glides from posA -> posB with heading-relative left-arc (opposite world side from A)
                            val curB = computeTransverseArcPosition(posA, posB, curT, isFirstSwapper = true)
                            nodePositionOffsets[idB] = Offset(curB.x - posB.x, curB.y - posB.y)
                        }
                        nodePositionOffsets.remove(idA)
                        nodePositionOffsets.remove(idB)
                    }
                }
            } else {
                nodePositionOffsets.clear()
            }

            // ── 2. Traversal Signal Wavefront Propagation ──
            val prevActive = previousStep?.activeNodeId
            val currActive = currentStep.activeNodeId

            if (prevActive != null && currActive != null && prevActive != currActive) {
                // Check if an edge links prevActive and currActive
                val hasEdge = currentStep.edges.any {
                    (it.from == prevActive && it.to == currActive) ||
                    (!it.isDirected && it.from == currActive && it.to == prevActive)
                }

                if (hasEdge) {
                    val signalAnim = Animatable(0f)
                    val signalDuration = (380 * durationMultiplier).toInt().coerceIn(120, 800)

                    launch {
                        activeSignals = listOf(TraversalSignal(prevActive, currActive, 0f))
                        signalAnim.animateTo(1f, tween(signalDuration)) {
                            activeSignals = listOf(TraversalSignal(prevActive, currActive, value))
                        }
                        activeSignals = emptyList()
                    }
                }
            } else {
                activeSignals = emptyList()
            }

            // ── 3. Node Activation Scale Pop & Expanding Ripple ──
            val activatedNodeId = when {
                currentStep.activeNodeId != null && currentStep.activeNodeId != previousStep?.activeNodeId ->
                    currentStep.activeNodeId
                currentStep.nodes.any { it.state == ElementState.ACTIVE } ->
                    currentStep.nodes.firstOrNull { it.state == ElementState.ACTIVE }?.id
                currentStep.nodes.any { it.state == ElementState.FOUND } ->
                    currentStep.nodes.firstOrNull { it.state == ElementState.FOUND }?.id
                else -> null
            }

            if (activatedNodeId != null) {
                val isTarget = activatedNodeId == targetNodeId || currentStep.phaseLabel == "FOUND"

                // Scale punch: 1.0f -> 1.18f -> 1.0f
                launch {
                    val scaleAnim = Animatable(1f)
                    scaleAnim.animateTo(1.18f, evalSpringSpec)
                    scaleAnim.animateTo(1.0f, evalSpringSpec)
                    nodeScales.remove(activatedNodeId)
                }

                // Expanding hairline ripple ring
                launch {
                    val rippleAnim = Animatable(0f)
                    val rippleDuration = (500 * durationMultiplier).toInt().coerceIn(180, 1000)
                    activeRipples = listOf(NodeRipple(activatedNodeId, 0f, isTarget))
                    rippleAnim.animateTo(1f, tween(rippleDuration)) {
                        activeRipples = listOf(NodeRipple(activatedNodeId, value, isTarget))
                    }
                    activeRipples = emptyList()
                }
            }

            // ── 4. Shortest Path Sequential Neon Trace ──
            val isSearchComplete = (currentStep.phaseLabel == "FOUND" || currentStep.phaseLabel == "OPTIMAL") && parsedGoalPath.size >= 2

            if (isSearchComplete) {
                completedPath = parsedGoalPath
                val totalHops = (parsedGoalPath.size - 1).coerceAtLeast(1)
                val traceAnim = Animatable(0f)
                val traceDuration = (totalHops * 240 * durationMultiplier).toInt().coerceIn(250, 1800)

                launch {
                    traceAnim.animateTo(totalHops.toFloat(), tween(traceDuration)) {
                        pathTraceProgress = value
                    }
                    pathTraceProgress = totalHops.toFloat()
                }
            } else {
                completedPath = emptyList()
                pathTraceProgress = 0f
            }
        }
    }

    return object : GraphMotionState {
        override fun getNodeCenter(node: GraphNodeState, defaultCoords: Offset): Offset {
            val delta = nodePositionOffsets[node.id] ?: Offset.Zero
            return Offset(defaultCoords.x + delta.x, defaultCoords.y + delta.y)
        }

        override fun getNodeScale(nodeId: String): Float {
            return nodeScales[nodeId] ?: 1f
        }

        override val activeSignals: List<TraversalSignal> get() = activeSignals
        override val activeRipples: List<NodeRipple> get() = activeRipples
        override val pathTraceProgress: Float get() = pathTraceProgress
        override val completedPath: List<String> get() = completedPath
    }
}
