package com.avia

import com.avia.data.AppSettings
import com.avia.ui.audio.AlgorithmAudioEngine
import com.avia.ui.visualizer.VisualizerStep
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class AlgorithmAudioEngineTest {

    @Before
    fun setUp() {
        AppSettings.soundEnabled = true
        AppSettings.soundVolume = 1.0f
    }

    @After
    fun tearDown() {
        AppSettings.soundEnabled = true
        AppSettings.soundVolume = 1.0f
    }

    @Test
    fun testSynthesizeTone_generatesLoudPunchyWaveformWithSmoothBoundaries() {
        val durationMs = 48
        val samples = AlgorithmAudioEngine.synthesizeTone(freq = 440.0, durationMs = durationMs, volume = 0.90f)
        val expectedLength = (AlgorithmAudioEngine.SAMPLE_RATE * (durationMs / 1000.0)).toInt()

        assertEquals(expectedLength, samples.size)

        // Verify smooth attack (starts at 0) and smooth release (ends at 0)
        assertEquals(0.toShort(), samples[0])
        assertEquals(0.toShort(), samples[samples.size - 1])

        // Verify powerful peak amplitude (> 60% of 16-bit PCM ceiling, i.e. > 20,000)
        val peak = samples.maxOf { abs(it.toInt()) }
        assertTrue("Peak amplitude ($peak) must exceed 20,000 for loud playback", peak > 20000)

        // Verify RMS power is strong (high acoustic energy)
        val sumSquares = samples.map { it.toLong() * it.toLong() }.sum()
        val rms = kotlin.math.sqrt(sumSquares.toDouble() / samples.size)
        assertTrue("RMS power ($rms) must exceed 7,000 for rich body", rms > 7000.0)
    }

    @Test
    fun testSynthesizeDualTone_generatesResonantTwoToneWithCleanBoundaries() {
        val durationMs = 64
        val samples = AlgorithmAudioEngine.synthesizeDualTone(f1 = 330.0, f2 = 415.0, durationMs = durationMs, volume = 0.94f)

        assertEquals(0.toShort(), samples[0])
        assertEquals(0.toShort(), samples[samples.size - 1])

        val peak = samples.maxOf { abs(it.toInt()) }
        assertTrue("Peak amplitude ($peak) must exceed 20,000", peak > 20000)
    }

    @Test
    fun testSynthesizeBellChime_ringsWithHarmonicsAndSmoothRelease() {
        val durationMs = 90
        val samples = AlgorithmAudioEngine.synthesizeBellChime(freq = 880.0, durationMs = durationMs, volume = 0.94f)

        assertEquals(0.toShort(), samples[0])
        assertEquals(0.toShort(), samples[samples.size - 1])

        val peak = samples.maxOf { abs(it.toInt()) }
        assertTrue("Bell chime peak ($peak) must exceed 20,000", peak > 20000)
    }

    @Test
    fun testSynthesizeChord_maintainsFullHeadroomWithoutClipping() {
        val frequencies = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val durationMs = 200
        val samples = AlgorithmAudioEngine.synthesizeChord(frequencies, durationMs = durationMs, volume = 0.96f)

        assertEquals(0.toShort(), samples[0])
        assertEquals(0.toShort(), samples[samples.size - 1])

        val peak = samples.maxOf { abs(it.toInt()) }
        assertTrue("Chord peak ($peak) must be loud (> 18,000)", peak > 18000)
        assertTrue("Chord peak ($peak) must not clip beyond Short.MAX_VALUE", peak <= Short.MAX_VALUE.toInt())
    }

    @Test
    fun testSynthesizeChirp_smoothlyRampsAndZeroCrossesAtTail() {
        val samples = AlgorithmAudioEngine.synthesizeChirp(startFreq = 380.0, endFreq = 680.0, durationMs = 46, volume = 0.92f)

        assertEquals(0.toShort(), samples[0])
        assertEquals(0.toShort(), samples[samples.size - 1])

        val peak = samples.maxOf { abs(it.toInt()) }
        assertTrue("Chirp peak ($peak) must exceed 18,000", peak > 18000)
    }

    @Test
    fun testSoundVolumeScaling_scalesSampleAmplitudesLinearly() {
        AppSettings.soundVolume = 1.0f
        val samplesFull = AlgorithmAudioEngine.synthesizeTone(freq = 440.0, durationMs = 48, volume = 0.90f)
        val peakFull = samplesFull.maxOf { abs(it.toInt()) }

        AppSettings.soundVolume = 0.5f
        val samplesHalf = AlgorithmAudioEngine.synthesizeTone(freq = 440.0, durationMs = 48, volume = 0.90f)
        val peakHalf = samplesHalf.maxOf { abs(it.toInt()) }

        val ratio = peakHalf.toDouble() / peakFull.toDouble()
        assertTrue("Half volume ratio ($ratio) should be approximately 0.5 (+/- 0.05)", ratio in 0.45..0.55)

        AppSettings.soundVolume = 0.0f
        val samplesZero = AlgorithmAudioEngine.synthesizeTone(freq = 440.0, durationMs = 48, volume = 0.90f)
        val peakZero = samplesZero.maxOf { abs(it.toInt()) }
        assertEquals(0, peakZero)
    }

    @Test
    fun testPlayStep_runsWithoutExceptionWhenSoundDisabled() {
        AppSettings.soundEnabled = false
        val dummyStep = VisualizerStep(
            array = listOf(10, 20, 30),
            elementStates = emptyMap(),
            phaseLabel = "Testing Phase"
        )
        // Should return early safely
        AlgorithmAudioEngine.playStep(dummyStep)
    }
}
