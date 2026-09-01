package com.example.algolens.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Phase 6 / task 4.3.2 - Macrobenchmarks for the AlgoLens visualizer.
 *
 * Run with:
 *   ./gradlew :benchmark:connectedCheck -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
 * (requires a physical device; benchmarks run against the app's `benchmark`
 * variant, which is R8-minified like release).
 *
 * Measured:
 *  - [StartupBenchmark] cold time-to-initial-display of the app shell
 *    (the entry point into VisualizerScreen).
 *  - [FrameTimingBenchmark] frame durations over a 10s idle playback session.
 *
 * NOTE: per-VisualizerFamily deep navigation (LINEAR_1D / GRAPH_2D / BUFFER)
 * needs a deep link or intent extra in the app so benchmarks can open a
 * specific algorithm deterministically; that hook is not part of Phase 6.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartup_timeToInitialDisplay() = benchmarkRule.measureRepeated(
        packageName = "com.example.algolens",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
    }
}

@RunWith(AndroidJUnit4::class)
class FrameTimingBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun visualizerPlayback_frameTiming() = benchmarkRule.measureRepeated(
        packageName = "com.example.algolens",
        metrics = listOf(FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
        // 10s of on-screen playback with the default (step 0) visualizer state.
        Thread.sleep(10_000)
    }
}
