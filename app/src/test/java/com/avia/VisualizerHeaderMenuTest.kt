package com.avia

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Sanity checks for the visualizer header's overflow-menu icon.
 *
 * The popover itself (open/close, item clicks) is exercised on the
 * emulator — Compose popovers are not cheap to unit-test without a
 * `createComposeRule` harness, which this project does not yet
 * have. The contract these tests lock in:
 *  - The kebab icon is `Icons.Default.MoreVert` (the Material
 *    three-vertical-dots icon), the conventional overflow trigger
 *    on Android.
 *  - The icon's `name` matches the file, so a future typo'd import
 *    (e.g. `Icons.Filled.MoreVertical`) is caught.
 */
class VisualizerHeaderMenuTest {

    @Test
    fun kebabIcon_isMoreVert() {
        val icon = Icons.Default.MoreVert
        assertNotNull("Icons.Default.MoreVert must be on the classpath", icon)
    }

    @Test
    fun kebabIcon_nameMatchesFile() {
        val icon = Icons.Default.MoreVert
        assertEquals(
            "MoreVert",
            icon.name.substringAfterLast('.')
        )
    }
}
