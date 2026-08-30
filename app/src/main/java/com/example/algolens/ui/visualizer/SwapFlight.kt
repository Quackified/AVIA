package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.algolens.ui.theme.AlgoTokens
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Single source of truth for the 3-phase "lift → glide → settle" swap arc shared
 * by every 1D-array visualizer (cells, bars). Springs are inlined as concretely
 * typed defaults so no consumer depends on external token initialisation order.
 */
data class FlightSpec(
    val liftHeight: Dp = 16.dp,
    val liftScale: Float = 1.18f,
    val liftSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    val travelSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    val settleSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMedium,
    ),
    val landScaleSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
)

/** Default flight spec — swap arcs use the IDE-standard lift/glide/settle. */
val DefaultFlightSpec = FlightSpec()

/**
 * Snapshot of a single slot's in-flight transform. All three are [State], so a
 * caller that reads them only inside graphicsLayer / drawBehind pays zero
 * recomposition cost on each animation frame.
 */
interface SlotTransform {
    val offsetX: State<Float>
    val offsetY: State<Float>
    val scale: State<Float>
}

/** Result of [rememberSlotFlightMap]: per-slot live transform queries. */
interface SlotFlightMap {
    fun transform(slotIndex: Int): SlotTransform

    /** Stable element identities (each element's origin slot) for item keys. */
    val elementIds: List<Int>
}

/**
 * Detects which slot pairs swapped values between [previous] and [current].
 * Trusts [swappedIndices] when valid; otherwise diffs by value (a pure swap of
 * exactly two crossed slots).
 */
fun findSwappedPairs(
    previous: List<Int>,
    current: List<Int>,
    swappedIndices: Pair<Int, Int>? = null,
): List<Pair<Int, Int>> = when {
    swappedIndices != null &&
        swappedIndices.first in current.indices &&
        swappedIndices.second in current.indices ->
        listOf(swappedIndices.first to swappedIndices.second)

    previous.size == current.size -> {
        val changed = current.indices.filter { previous[it] != current[it] }
        if (changed.size == 2) {
            val (a, b) = changed
            if (previous[a] == current[b] && previous[b] == current[a])
                listOf(a to b) else emptyList()
        } else {
            emptyList()
        }
    }

    else -> emptyList()
}

/**
 * Animates this composable through the canonical 3-phase swap arc from [from]
 * to [to] when [active]:
 *   1. LIFT   — pop straight up off the source slot (scale swells in parallel).
 *   2. GLIDE  — horizontal travel to the destination slot, mid-air.
 *   3. SETTLE — descend into the destination slot and lock with a small bounce.
 *
 * All motion is applied through [graphicsLayer], so animation frames only
 * re-record the layer — they never recompose or re-layout this subtree. The
 * coroutine runs under [NonCancellable], so a swap can never be abandoned
 * mid-air leaving the node stranded lifted, shifted or enlarged.
 */
fun Modifier.animatedSlotSwap(
    active: Boolean,
    from: DpOffset,
    to: DpOffset,
    spec: FlightSpec = DefaultFlightSpec,
): Modifier = composed {
    val density = LocalDensity.current
    val liftHeightPx = with(density) { spec.liftHeight.toPx() }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(active, from, to) {
        if (!active) {
            offsetX.snapTo(0f); offsetY.snapTo(0f); scale.snapTo(1f)
            return@LaunchedEffect
        }
        withContext(NonCancellable) {
            val fromPx = with(density) { Offset(from.x.toPx(), from.y.toPx()) }
            val toPx = with(density) { Offset(to.x.toPx(), to.y.toPx()) }
            offsetX.snapTo(fromPx.x - toPx.x)
            offsetY.snapTo(fromPx.y - toPx.y)
            scale.snapTo(1f)

            // 1) LIFT
            launch { scale.animateTo(spec.liftScale, spec.liftSpring) }
            offsetY.animateTo(-liftHeightPx, spec.liftSpring)

            // 2) GLIDE
            offsetX.animateTo(0f, spec.travelSpring)

            // 3) SETTLE & LOCK
            offsetY.animateTo(0f, spec.settleSpring)
            scale.animateTo(1f, spec.landScaleSpring)

            offsetX.snapTo(0f); offsetY.snapTo(0f); scale.snapTo(1f)
        }
    }

    graphicsLayer {
        translationX = offsetX.value
        translationY = offsetY.value
        scaleX = scale.value
        scaleY = scale.value
        shadowElevation =
            if (scale.value > 1.01f || abs(offsetY.value) > 0.5f) {
                AlgoTokens.elevationTraveling.toPx()
            } else 0f
    }
}

/**
 * Bouncy scale-pop for a node/block that is "being evaluated" (graph traversal
 * visiting a node, buffer peek, pivot selection, etc.). Grows to 1.12× with a
 * medium-bouncy spring, returns to 1× on release. Driven via [graphicsLayer].
 */
fun Modifier.nodePop(
    active: Boolean,
    targetScale: Float = 1.12f,
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(active) {
        if (active) scale.animateTo(targetScale, AlgoTokens.evalSpring)
        else scale.animateTo(1f, AlgoTokens.evalSpring)
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/** Direction a buffer block slides in from. */
enum class SlideDirection { Top, Bottom, Left, Right }

/**
 * Spring-physics directional slide-in / slide-out for buffer enqueue & dequeue.
 * The block enters from the correct edge (top for a stack push, bottom for a
 * queue enqueue) and settles with a small overshoot bounce. When [visible] is
 * false the element translates off-edge and fades.
 */
fun Modifier.slideEnter(
    visible: Boolean,
    direction: SlideDirection,
    distance: Dp = 60.dp,
): Modifier = composed {
    val density = LocalDensity.current
    val distancePx = with(density) { distance.toPx() }
    val tx = remember { Animatable(0f) }
    val ty = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            when (direction) {
                SlideDirection.Top -> { ty.snapTo(-distancePx); tx.snapTo(0f) }
                SlideDirection.Bottom -> { ty.snapTo(distancePx); tx.snapTo(0f) }
                SlideDirection.Left -> { tx.snapTo(-distancePx); ty.snapTo(0f) }
                SlideDirection.Right -> { tx.snapTo(distancePx); ty.snapTo(0f) }
            }
            alpha.snapTo(0f)
            launch { tx.animateTo(0f, AlgoTokens.cellTravelSpring) }
            ty.animateTo(0f, AlgoTokens.cellTravelSpring)
            alpha.animateTo(
                1f,
                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
            )
        } else {
            val (exitX, exitY) = when (direction) {
                SlideDirection.Top -> 0f to -distancePx
                SlideDirection.Bottom -> 0f to distancePx
                SlideDirection.Left -> -distancePx to 0f
                SlideDirection.Right -> distancePx to 0f
            }
            launch { tx.animateTo(exitX, AlgoTokens.cellTravelSpring) }
            ty.animateTo(exitY, AlgoTokens.cellTravelSpring)
            alpha.animateTo(
                0f,
                spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium),
            )
        }
    }

    graphicsLayer {
        translationX = tx.value
        translationY = ty.value
        this.alpha = alpha.value
    }
}

/**
 * Remembers and drives the per-slot swap flight state for a 1D array whose
 * value list is [current] and whose previous value list is [previous].
 *
 * Handles: swap detection, stable element-identity tracking, per-slot
 * (offsetX, offsetY, scale) [Animatable] triples, NonCancellable 3-phase
 * flights, and cancel-safe reset of every non-traveling slot back to rest.
 *
 * Visualizers read [SlotFlightMap.transform] inside their per-element content;
 * reading there (never in composition) means animation frames cost nothing.
 */
@Composable
fun rememberSlotFlightMap(
    current: List<Int>,
    previous: List<Int>,
    swappedIndices: Pair<Int, Int>? = null,
    slotPitchPx: Float,
    spec: FlightSpec = DefaultFlightSpec,
    key: Any = current,
): SlotFlightMap {
    val scope = rememberCoroutineScope()

    val offsetsX = remember { mutableMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val offsetsY = remember { mutableMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val scales = remember { mutableMapOf<Int, Animatable<Float, AnimationVector1D>>() }
    val liftHeightPx = with(LocalDensity.current) { spec.liftHeight.toPx() }

    var prevArray by remember { mutableStateOf(previous) }
    var elementIdList by remember(current.size) { mutableStateOf(List(current.size) { it }) }
    var lastPlannedKey by remember { mutableStateOf<Any?>(null) }

    // The flight plan persists across the state-write-induced composition
    // restart so the LaunchedEffect always sees the real swap pairs.
    val plannedPairs = remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }

    if (lastPlannedKey != key) {
        lastPlannedKey = key
        plannedPairs.value = findSwappedPairs(prevArray, current, swappedIndices)

        if (prevArray.size == current.size) {
            val prevIds = elementIdList
            val newIds = MutableList(current.size) { it }
            val used = BooleanArray(prevArray.size)
            current.forEachIndexed { i, v ->
                val j = prevArray.indices.firstOrNull { !used[it] && prevArray[it] == v }
                if (j != null) { newIds[i] = prevIds[j]; used[j] = true }
            }
            elementIdList = newIds
        }

        val planSlots = plannedPairs.value.flatMap { (a, b) -> listOf(a, b) }.toSet()
        offsetsX.keys.toList().forEach { s -> if (s !in planSlots) offsetsX[s] = Animatable(0f) }
        offsetsY.keys.toList().forEach { s -> if (s !in planSlots) offsetsY[s] = Animatable(0f) }
        scales.keys.toList().forEach { s -> if (s !in planSlots) scales[s] = Animatable(1f) }
        prevArray = current
    }

    LaunchedEffect(key) {
        val flightPairs = plannedPairs.value
        for ((from, to) in flightPairs) {
            val distance = (to - from) * slotPitchPx
            // Pre-position so the FIRST rendered frame shows the pre-swap state.
            for (slot in listOf(from, to)) {
                val ox = offsetsX.getOrPut(slot) { Animatable(0f) }
                val oy = offsetsY.getOrPut(slot) { Animatable(0f) }
                val sc = scales.getOrPut(slot) { Animatable(1f) }
                ox.snapTo(if (slot == from) distance else -distance)
                oy.snapTo(0f); sc.snapTo(1f)
            }
            for (slot in listOf(from, to)) {
                val ox = offsetsX[slot] ?: continue
                val oy = offsetsY[slot] ?: continue
                val sc = scales[slot] ?: continue
                scope.launch {
                    withContext(NonCancellable) {
                        launch { sc.animateTo(spec.liftScale, spec.liftSpring) }
                        oy.animateTo(-liftHeightPx, spec.liftSpring)
                        ox.animateTo(0f, spec.travelSpring)
                        oy.animateTo(0f, spec.settleSpring)
                        sc.animateTo(1f, spec.landScaleSpring)
                        ox.snapTo(0f); oy.snapTo(0f); sc.snapTo(1f)
                    }
                }
            }
        }
    }

    return object : SlotFlightMap {
        override val elementIds: List<Int> get() = elementIdList

        override fun transform(slotIndex: Int): SlotTransform {
            val ox = offsetsX.getOrPut(slotIndex) { Animatable(0f) }
            val oy = offsetsY.getOrPut(slotIndex) { Animatable(0f) }
            val sc = scales.getOrPut(slotIndex) { Animatable(1f) }
            return object : SlotTransform {
                override val offsetX = ox.asState()
                override val offsetY = oy.asState()
                override val scale = sc.asState()
            }
        }
    }
}


