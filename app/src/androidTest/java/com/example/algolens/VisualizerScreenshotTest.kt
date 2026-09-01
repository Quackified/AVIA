package com.example.algolens

import android.graphics.Bitmap
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Phase 6 / task 4.2.6 - the project's first androidTest screenshot test.
 *
 * Launches the app, waits for the first frame, captures the screen through
 * UiAutomation and asserts the render is not blank. The PNG is written to the
 * app's external files dir (adb-pull-able) so visual regressions can be
 * diffed between builds.
 */
@RunWith(AndroidJUnit4::class)
class VisualizerScreenshotTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun appLaunches_rendersNonBlankScreenshot() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation

        // Let the first frames settle.
        instrumentation.waitForIdleSync()
        Thread.sleep(1500)

        val screenshot: Bitmap? = uiAutomation.takeScreenshot()
        assertNotNull("UiAutomation returned no screenshot", screenshot)
        val bitmap = screenshot!!

        // A blank surface has ~1 distinct color; a rendered workspace has many.
        val distinctColors = countDistinctColors(bitmap)
        assertTrue(
            "Screen looks blank (only $distinctColors distinct colors)",
            distinctColors > 32,
        )

        // Persist for diffing: adb pull /sdcard/Android/data/com.example.algolens/files/screenshots/
        val dir = instrumentation.targetContext.getExternalFilesDir("screenshots")
            ?: instrumentation.targetContext.filesDir
        dir.mkdirs()
        val out = File(dir, "home-${System.currentTimeMillis()}.png")
        FileOutputStream(out).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("Screenshot not written: $out", out.exists() && out.length() > 0)
    }

    private fun countDistinctColors(bitmap: Bitmap): Int {
        val step = maxOf(1, minOf(bitmap.width, bitmap.height) / 128)
        val colors = HashSet<Int>()
        var y = 0
        while (y < bitmap.height) {
            var x = 0
            while (x < bitmap.width) {
                colors.add(bitmap.getPixel(x, y))
                x += step
            }
            y += step
        }
        return colors.size
    }
}
