package com.avia.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import androidx.annotation.VisibleForTesting
import com.avia.data.AppSettings
import com.avia.ui.visualizer.ElementState
import com.avia.ui.visualizer.VisualizerStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, offline, zero-asset audio synthesis engine for algorithm visualizers.
 *
 * Synthesizes 16-bit mono PCM waveforms via [AudioTrack] at 44,100 Hz on the STREAM_MUSIC
 * audio channel ([AudioAttributes.USAGE_MEDIA]):
 * - Value Comparisons: Frequency scaled to element magnitude (293.66 Hz [D4] – 1174.66 Hz [D6])
 *   with psychoacoustic overtone blending (fundamental + 2nd & 3rd harmonics) engineered for
 *   maximum acoustic projection on mobile phone speakers.
 * - Swaps: Punchy resonant dual-tone burst (musical major third interval).
 * - Sorted Placements: Bright, resonant high-register bell chime with shimmering metallic overtones.
 * - Target Found: Celebratory, full-bodied major triad chord (C5 - E5 - G5 - C6).
 * - Stack / Queue Push & Pop: Crisp ascending vs descending micro-chirps.
 * - Graph Node Visits: Warm wooden marimba/ping tone.
 * - Completion Sweep: Ascending pitch arpeggio across sorted elements.
 *
 * Employs Hann attack smoothing (2.5ms), gentle sustained exponential decay, and smooth
 * cosine tail fade-outs to guarantee zero DC offset clicks or cutoff pops.
 *
 * Honors [AppSettings.soundEnabled] and [AppSettings.soundVolume]. All synthesis and writes execute off the UI thread.
 */
object AlgorithmAudioEngine {

    const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val audioChannel = Channel<ShortArray>(capacity = Channel.CONFLATED)

    @Volatile
    private var track: AudioTrack? = null

    init {
        scope.launch {
            for (pcm in audioChannel) {
                if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) continue
                ensureTrack()?.let { audioTrack ->
                    try {
                        if (audioTrack.playState != AudioTrack.PLAYSTATE_PLAYING) {
                            audioTrack.play()
                        }
                        audioTrack.write(pcm, 0, pcm.size)
                    } catch (_: Exception) {
                        // AudioTrack write failed or track was released
                    }
                }
            }
        }
    }

    @Synchronized
    private fun ensureTrack(): AudioTrack? {
        if (track != null && track?.state == AudioTrack.STATE_INITIALIZED) {
            return track
        }
        return try {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)
            val bufferSize = (minBuf * 2).coerceAtLeast(8192)

            val newTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                    }
                }
                .build()
            newTrack.setVolume(1.0f)
            track = newTrack
            newTrack
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Immediately pauses and flushes the underlying AudioTrack buffer to drop pending sound.
     */
    fun stop() {
        try {
            track?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.pause()
                    it.flush()
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Releases native AudioTrack resources.
     */
    fun release() {
        try {
            track?.let {
                it.stop()
                it.release()
            }
            track = null
        } catch (_: Exception) {}
    }

    /**
     * Inspects the current and previous visualizer step deltas and triggers
     * the corresponding synthesized audio cue.
     */
    fun playStep(step: VisualizerStep, prevStep: VisualizerStep? = null) {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return

        // 1. Completion sweep
        val isCompletion = step.phaseLabel.contains("SORTED", ignoreCase = true) ||
            step.phaseLabel.contains("COMPLETE", ignoreCase = true) ||
            step.phaseLabel.contains("FINISHED", ignoreCase = true)
        val prevWasCompletion = prevStep != null && (
            prevStep.phaseLabel.contains("SORTED", ignoreCase = true) ||
            prevStep.phaseLabel.contains("COMPLETE", ignoreCase = true) ||
            prevStep.phaseLabel.contains("FINISHED", ignoreCase = true)
        )
        if (isCompletion && !prevWasCompletion) {
            playCompletionArpeggio(
                elementCount = step.array.size.takeIf { it > 0 } ?: step.nodes.size.takeIf { it > 0 } ?: 8,
                values = step.array.takeIf { it.isNotEmpty() }
            )
            return
        }

        // 2. Target found (deduplicated across steps)
        val hasFound = step.elementStates.any { it.value == ElementState.FOUND } ||
            step.phaseLabel.contains("FOUND", ignoreCase = true) ||
            step.nodes.any { it.state == ElementState.FOUND }
        val prevHadFound = prevStep != null && (
            prevStep.elementStates.any { it.value == ElementState.FOUND } ||
            prevStep.phaseLabel.contains("FOUND", ignoreCase = true) ||
            prevStep.nodes.any { it.state == ElementState.FOUND }
        )
        if (hasFound && !prevHadFound) {
            playTargetFound()
            return
        }

        // 3. Swap in progress
        val isSwapping = step.swappedIndices != null ||
            step.phaseLabel.contains("SWAP", ignoreCase = true) ||
            step.elementStates.any { it.value == ElementState.SWAPPING }
        if (isSwapping) {
            val idxPair = step.swappedIndices
            val val1 = idxPair?.let { step.array.getOrNull(it.first) } ?: 50
            val val2 = idxPair?.let { step.array.getOrNull(it.second) } ?: 60
            playSwap(val1, val2)
            return
        }

        // 4. Element freshly sorted
        val newlySorted = step.elementStates.count { it.value == ElementState.SORTED } >
            (prevStep?.elementStates?.count { it.value == ElementState.SORTED } ?: 0)
        if (newlySorted) {
            val sortedVal = step.sortedBoundary?.let { step.array.getOrNull(it) } ?: 80
            playSorted(sortedVal)
            return
        }

        // 5. Stack / Queue Buffer operations
        if (step.buffer.isNotEmpty() || (prevStep != null && prevStep.buffer.isNotEmpty())) {
            val currSize = step.buffer.size
            val prevSize = prevStep?.buffer?.size ?: 0
            if (currSize > prevSize) {
                playPush()
                return
            } else if (currSize < prevSize) {
                playPop()
                return
            }
        }

        // 6. Graph node visits & edge traversals
        if (step.activeNodeId != null && step.activeNodeId != prevStep?.activeNodeId) {
            val nodeIdx = step.nodes.indexOfFirst { it.id == step.activeNodeId }.coerceAtLeast(0)
            playNodeVisit(nodeIdx, step.nodes.size.coerceAtLeast(1))
            return
        }

        // 7. Comparison / Element Probe
        val comparingIndices = step.elementStates.filter { it.value == ElementState.COMPARING }.keys
        if (comparingIndices.isNotEmpty()) {
            val firstIdx = comparingIndices.first()
            val value = step.array.getOrNull(firstIdx) ?: (firstIdx * 10 + 10)
            val maxVal = (step.array.maxOrNull() ?: 100).coerceAtLeast(1)
            playCompare(value, maxVal)
            return
        }

        // Default subtle step tick
        playCompare(50, 100)
    }

    /**
     * Pure sine tone enriched with harmonic overtones mapped to element magnitude
     * between 293.66 Hz (D4) and 1174.66 Hz (D6).
     */
    fun playCompare(value: Int, maxValue: Int = 100) {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val normalized = (value.toFloat() / maxValue.coerceAtLeast(1).toFloat()).coerceIn(0.01f, 1f)
        val freq = 293.66 * Math.pow(2.0, (normalized * 2.0).toDouble())
        val pcm = synthesizeTone(freq, durationMs = 48, volume = 0.90f)
        audioChannel.trySend(pcm)
    }

    /**
     * Resonant dual-tone micro-burst signifying an element inversion / swap.
     */
    fun playSwap(val1: Int, val2: Int, maxValue: Int = 100) {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val n1 = (val1.toFloat() / maxValue.coerceAtLeast(1).toFloat()).coerceIn(0.05f, 1f)
        val f1 = 329.63 * Math.pow(2.0, (n1 * 1.5).toDouble())
        val f2 = f1 * 1.2599
        val pcm = synthesizeDualTone(f1, f2, durationMs = 64, volume = 0.94f)
        audioChannel.trySend(pcm)
    }

    /**
     * High-register bell chime confirming an element is permanently locked in sorted order.
     */
    fun playSorted(value: Int, maxValue: Int = 100) {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val n = (value.toFloat() / maxValue.coerceAtLeast(1).toFloat()).coerceIn(0.1f, 1f)
        val freq = 880.0 + (n * 220.0)
        val pcm = synthesizeBellChime(freq = freq, durationMs = 90, volume = 0.94f)
        audioChannel.trySend(pcm)
    }

    /**
     * Celebratory major triad chord (C5 - E5 - G5 - C6) for search target found or goal achievement.
     */
    fun playTargetFound() {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val pcm = synthesizeChord(
            doubleArrayOf(523.25, 659.25, 783.99, 1046.50),
            durationMs = 200,
            volume = 0.96f
        )
        audioChannel.trySend(pcm)
    }

    /**
     * Ascending pitch chirp for Stack / Queue push (380 Hz -> 680 Hz).
     */
    fun playPush() {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val pcm = synthesizeChirp(startFreq = 380.0, endFreq = 680.0, durationMs = 46, volume = 0.92f)
        audioChannel.trySend(pcm)
    }

    /**
     * Descending pitch chirp for Stack / Queue pop (680 Hz -> 380 Hz).
     */
    fun playPop() {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val pcm = synthesizeChirp(startFreq = 680.0, endFreq = 380.0, durationMs = 46, volume = 0.92f)
        audioChannel.trySend(pcm)
    }

    /**
     * Warm marimba ping for BFS / DFS / Dijkstra graph node traversal.
     */
    fun playNodeVisit(nodeIndex: Int, totalNodes: Int = 10) {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val step = (nodeIndex.toDouble() / totalNodes.coerceAtLeast(1).toDouble()).coerceIn(0.0, 1.0)
        val freq = 392.0 + (step * 440.0)
        val pcm = synthesizeTone(freq, durationMs = 56, volume = 0.92f)
        audioChannel.trySend(pcm)
    }

    /**
     * Elongated ascending flourish celebrating completed algorithm execution.
     * Synchronized to the sequential pulse wave across the array (staggered at 65ms per element)
     * and crowned with a sustained resonant chord bloom that decays across the full 600ms celebration pulse.
     */
    fun playCompletionArpeggio(elementCount: Int = 8, values: List<Int>? = null) {
        if (!AppSettings.soundEnabled || AppSettings.soundVolume <= 0.001f) return
        val count = elementCount.coerceIn(4, 16)
        val staggerMs = 65
        val waveDurationMs = count * staggerMs
        val pulseDurationMs = 650
        val totalDurationMs = waveDurationMs + pulseDurationMs
        val totalSamples = (SAMPLE_RATE * (totalDurationMs / 1000.0)).toInt()
        val pcm = ShortArray(totalSamples)

        // 1. Synthesize sequential ascending wave matching each element slot
        val scaleBase = doubleArrayOf(
            523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77, 1046.50,
            1174.66, 1318.51, 1396.91, 1567.98, 1760.00, 1975.53, 2093.00, 2349.32
        )
        val maxVal = values?.maxOrNull()?.coerceAtLeast(1) ?: 100

        for (i in 0 until count) {
            val freq = if (values != null && i < values.size) {
                val norm = (values[i].toFloat() / maxVal.toFloat()).coerceIn(0.05f, 1f)
                440.0 * Math.pow(2.0, (norm * 1.5).toDouble())
            } else {
                scaleBase[i % scaleBase.size]
            }
            val offsetSamples = ((i * staggerMs * SAMPLE_RATE) / 1000).coerceAtMost(totalSamples - 1)
            val noteBuf = synthesizeBellChime(freq, durationMs = 140, volume = 0.82f)
            for (j in noteBuf.indices) {
                val targetIdx = offsetSamples + j
                if (targetIdx < pcm.size) {
                    pcm[targetIdx] = (pcm[targetIdx] + noteBuf[j])
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                }
            }
        }

        // 2. Crown with resonant chord bloom starting at waveDurationMs, decaying with the 600ms pulse
        val chordOffsetSamples = ((waveDurationMs * SAMPLE_RATE) / 1000).coerceAtMost(totalSamples - 1)
        val chordFreqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50, 1318.51) // C5 - E5 - G5 - C6 - E6
        val chordBuf = synthesizeChord(chordFreqs, durationMs = pulseDurationMs, volume = 0.95f)
        for (j in chordBuf.indices) {
            val targetIdx = chordOffsetSamples + j
            if (targetIdx < pcm.size) {
                pcm[targetIdx] = (pcm[targetIdx] + chordBuf[j])
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
        }

        audioChannel.trySend(pcm)
    }

    // ── Waveform Synthesis Helpers ──

    @VisibleForTesting
    internal fun synthesizeTone(freq: Double, durationMs: Int, volume: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val samples = ShortArray(numSamples)
        val attackSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val releaseSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val effectiveVolume = (volume * AppSettings.soundVolume).coerceIn(0f, 1f)

        for (i in 0 until numSamples) {
            val env = if (i < attackSamples) {
                0.5 * (1.0 - cos(PI * i / attackSamples))
            } else {
                exp(-1.8 * (i - attackSamples).toDouble() / (numSamples - attackSamples).toDouble())
            }
            val tailFade = if (i >= numSamples - releaseSamples) {
                0.5 * (1.0 + cos(PI * (i - (numSamples - releaseSamples)) / releaseSamples))
            } else {
                1.0
            }
            val angle = 2.0 * PI * freq * i / SAMPLE_RATE
            // Acoustic harmonic overtone blend: 70% fundamental + 22% 2nd harmonic + 8% 3rd harmonic
            val raw = 0.70 * sin(angle) + 0.22 * sin(2.0 * angle) + 0.08 * sin(3.0 * angle)
            val sampleVal = (raw * env * tailFade * effectiveVolume * 32767.0).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    @VisibleForTesting
    internal fun synthesizeDualTone(f1: Double, f2: Double, durationMs: Int, volume: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val samples = ShortArray(numSamples)
        val attackSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val releaseSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val effectiveVolume = (volume * AppSettings.soundVolume).coerceIn(0f, 1f)

        for (i in 0 until numSamples) {
            val env = if (i < attackSamples) {
                0.5 * (1.0 - cos(PI * i / attackSamples))
            } else {
                exp(-1.9 * (i - attackSamples).toDouble() / (numSamples - attackSamples).toDouble())
            }
            val tailFade = if (i >= numSamples - releaseSamples) {
                0.5 * (1.0 + cos(PI * (i - (numSamples - releaseSamples)) / releaseSamples))
            } else {
                1.0
            }
            val angle1 = 2.0 * PI * f1 * i / SAMPLE_RATE
            val angle2 = 2.0 * PI * f2 * i / SAMPLE_RATE
            val tone1 = 0.76 * sin(angle1) + 0.24 * sin(2.0 * angle1)
            val tone2 = 0.76 * sin(angle2) + 0.24 * sin(2.0 * angle2)
            val raw = (tone1 + tone2) * 0.5
            val sampleVal = (raw * env * tailFade * effectiveVolume * 32767.0).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    @VisibleForTesting
    internal fun synthesizeBellChime(freq: Double, durationMs: Int, volume: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val samples = ShortArray(numSamples)
        val attackSamples = (SAMPLE_RATE * 0.0015).toInt().coerceAtLeast(1)
        val releaseSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val effectiveVolume = (volume * AppSettings.soundVolume).coerceIn(0f, 1f)

        for (i in 0 until numSamples) {
            val env = if (i < attackSamples) {
                0.5 * (1.0 - cos(PI * i / attackSamples))
            } else {
                exp(-2.2 * (i - attackSamples).toDouble() / (numSamples - attackSamples).toDouble())
            }
            val tailFade = if (i >= numSamples - releaseSamples) {
                0.5 * (1.0 + cos(PI * (i - (numSamples - releaseSamples)) / releaseSamples))
            } else {
                1.0
            }
            val angle = 2.0 * PI * freq * i / SAMPLE_RATE
            // Bell shimmer: 62% fundamental + 26% octave overtone + 12% inharmonic metallic overtone (2.76x)
            val raw = 0.62 * sin(angle) + 0.26 * sin(2.0 * angle) + 0.12 * sin(2.76 * angle)
            val sampleVal = (raw * env * tailFade * effectiveVolume * 32767.0).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    @VisibleForTesting
    internal fun synthesizeChord(frequencies: DoubleArray, durationMs: Int, volume: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val samples = ShortArray(numSamples)
        val attackSamples = (SAMPLE_RATE * 0.0035).toInt().coerceAtLeast(1)
        val releaseSamples = (SAMPLE_RATE * 0.0035).toInt().coerceAtLeast(1)
        val effectiveVolume = (volume * AppSettings.soundVolume).coerceIn(0f, 1f)
        val factor = 1.0 / frequencies.size.coerceAtLeast(1)

        for (i in 0 until numSamples) {
            val env = if (i < attackSamples) {
                0.5 * (1.0 - cos(PI * i / attackSamples))
            } else {
                exp(-1.4 * (i - attackSamples).toDouble() / (numSamples - attackSamples).toDouble())
            }
            val tailFade = if (i >= numSamples - releaseSamples) {
                0.5 * (1.0 + cos(PI * (i - (numSamples - releaseSamples)) / releaseSamples))
            } else {
                1.0
            }
            var sum = 0.0
            for (freq in frequencies) {
                val angle = 2.0 * PI * freq * i / SAMPLE_RATE
                val voice = 0.78 * sin(angle) + 0.22 * sin(2.0 * angle)
                sum += voice * factor
            }
            val sampleVal = (sum * env * tailFade * effectiveVolume * 32767.0).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    @VisibleForTesting
    internal fun synthesizeChirp(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val samples = ShortArray(numSamples)
        val attackSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val releaseSamples = (SAMPLE_RATE * 0.0025).toInt().coerceAtLeast(1)
        val effectiveVolume = (volume * AppSettings.soundVolume).coerceIn(0f, 1f)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples.toDouble()
            val env = if (i < attackSamples) {
                0.5 * (1.0 - cos(PI * i / attackSamples))
            } else {
                exp(-1.5 * progress)
            }
            val tailFade = if (i >= numSamples - releaseSamples) {
                0.5 * (1.0 + cos(PI * (i - (numSamples - releaseSamples)) / releaseSamples))
            } else {
                1.0
            }
            val phase = 2.0 * PI * (startFreq + (endFreq - startFreq) * 0.5 * progress) * (i.toDouble() / SAMPLE_RATE)
            val raw = 0.76 * sin(phase) + 0.24 * sin(phase * 2.0)
            val sampleVal = (raw * env * tailFade * effectiveVolume * 32767.0).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }
}
