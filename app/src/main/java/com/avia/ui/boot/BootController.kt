package com.avia.ui.boot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Compose-aware state holder for the cold-start boot overlay.
 *
 * `MainActivity` polls [ready] from a `setKeepOnScreenCondition` callback so
 * the OS-level splash dismisses only after the in-Compose boot overlay has
 * rendered its first frame and we have given the user a beat to perceive the
 * "workspace booting" crossfade. The default hold ([DEFAULT_HOLD_MS]) is kept
 * short — long enough for the brand mark to register, short enough that the
 * launch doesn't read as laggy. Tuneable via the constructor for tests and
 * for callers who want a longer brand beat.
 *
 * [ready] is backed by `mutableStateOf` so any Composable that reads it
 * (`BootOverlay`, `AlgoLensApp`'s `AnimatedContent`) automatically registers
 * a snapshot dependency and recomposes when [markReady] flips the flag.
 * Without this, the cold-start overlay would freeze on the
 * "loading workspace" state — the previous version used a non-Compose
 * `var Boolean` and Compose never observed the transition.
 *
 * Marked `@Stable` because the only mutable state inside is the
 * `mutableStateOf`-backed [ready]; the constructor parameters are val-immutable.
 */
@Stable
class BootController(
    /** How long the in-Compose overlay stays on screen before flipping ready. */
    val holdDurationMs: Long = DEFAULT_HOLD_MS,
) {
    /**
     * `false` while the OS splash is up; flips to `true` after [holdDurationMs].
     * Backed by `mutableStateOf` so reads inside `@Composable` functions
     * register snapshot dependencies and recompose when [markReady] fires.
     */
    var ready: Boolean by mutableStateOf(false)
        private set

    /**
     * Flip [ready] to `true`. Internal so [BootControllerEffect] (same module)
     * can drive the transition, but external callers cannot — preserving the
     * "state belongs to the controller" invariant.
     */
    internal fun markReady() {
        ready = true
    }

    companion object {
        /**
         * 1200ms — a considerate but brisk boot hold so the brand mark and
         * hairline progress sweep register once before the crossfade into the
         * workspace. Previous 1800ms held a three-system animation long enough
         * to read as a wait; the minimalist single-focal splash earns less time.
         */
        const val DEFAULT_HOLD_MS: Long = 1_200L
    }
}

/**
 * Compose entry point that drives [controller] from a [LaunchedEffect] so the
 * countdown starts the moment the root composition mounts. Pure — no UI side
 * effects — so it stays trivial to test by snapshotting `controller.ready`.
 */
@Composable
fun BootControllerEffect(controller: BootController) {
    LaunchedEffect(controller) {
        delay(controller.holdDurationMs)
        controller.markReady()
    }
}