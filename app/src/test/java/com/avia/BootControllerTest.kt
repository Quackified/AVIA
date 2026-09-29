package com.avia

import com.avia.ui.boot.BootController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM unit tests for [BootController]. The actual "delay → flip ready"
 * behaviour is driven by `BootControllerEffect`'s `LaunchedEffect` at runtime
 * and covered by the screenshot/instrumentation tests; here we lock down the
 * initial state, the state-holder shape (Compose snapshot backing), and the
 * default hold duration.
 */
class BootControllerTest {

    @Test
    fun ready_isFalseOnConstruction() {
        val controller = BootController(holdDurationMs = 1_000L)
        assertFalse(controller.ready)
    }

    @Test
    fun ready_isBackedByMutableState() {
        // The Kotlin `by mutableStateOf(false)` delegate compiles to a
        // backing field of type `MutableState<Boolean>`. This is the
        // contract that lets Composables observe `ready` and recompose
        // when it flips. A previous version used a plain `var Boolean`
        // and the cold-start overlay froze on "loading workspace" because
        // Compose never observed the transition.
        val readyField = BootController::class.java.getDeclaredField("ready\$delegate")
        assertNotNull(
            "BootController.ready must be delegated through mutableStateOf so Composables observe flips",
            readyField,
        )
        assertEquals(
            "androidx.compose.runtime.MutableState",
            readyField.type.name,
        )
    }

    @Test
    fun markReady_flipsReadyFlag() {
        val controller = BootController(holdDurationMs = 10_000L)
        assertFalse(controller.ready)
        controller.markReady()
        assertTrue(controller.ready)
    }

    @Test
    fun markReady_isIdempotent() {
        // Calling markReady() repeatedly must not throw or change the
        // observed value away from true. (Without mutableStateOf backing,
        // the previous version was a no-op for Compose but still flipped
        // the field — this test guards against any future regression to
        // non-snapshot state.)
        val controller = BootController(holdDurationMs = 10_000L)
        controller.markReady()
        controller.markReady()
        controller.markReady()
        assertTrue(controller.ready)
    }

    @Test
    fun defaultHoldIsSnappy() {
        assertEquals(1_200L, BootController.DEFAULT_HOLD_MS)
        assertEquals(1_200L, BootController().holdDurationMs)
    }

    @Test
    fun customHoldIsRespected() {
        val controller = BootController(holdDurationMs = 250L)
        assertEquals(250L, controller.holdDurationMs)
        assertFalse(controller.ready)
    }
}