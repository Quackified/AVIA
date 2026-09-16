package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.algolens.data.AppSettings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.model.VisualizerFamily
import com.example.algolens.ui.components.AlgoWorkspaceBackground
import com.example.algolens.ui.components.AmbientGlowDivider
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.tutor.AiTutorSheet

/**
 * Thin shell that composes the four workspace regions and the modal
 * overlays. Every region ([VisualizerHeader], the [VisualizerHost]
 * canvas, [AmbientGlowDivider], [CodeTracePane], [PlaybackRail]) is
 * either a separate file or a small composable; this body holds the
 * cross-cutting concerns (workspace background, status / nav bar
 * padding, theory-drawer backdrop blur, challenge-mode prompt
 * overlay, modal sheet hosting, sync-pulse heartbeat) and nothing
 * else.
 *
 * State lives in [VisualizerScreenState]; see
 * [rememberVisualizerScreenState] for the factory + playback loop.
 */
@Composable
fun VisualizerScreen(
    algorithm: Algorithm,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberVisualizerScreenState(algorithm)
    val spec = remember(algorithm.id) { AlgorithmRegistry.specFor(algorithm.id) }

    // ── Sync-pulse heartbeat ──
    val haptic = LocalHapticFeedback.current
    val syncPulse = remember { Animatable(0f) }
    val syncPulseState = remember { derivedStateOf { syncPulse.value } }

    LaunchedEffect(state.currentStepIdx) {
        // Fast, crisp pulse decay (280ms) avoids overlapping flashes
        syncPulse.snapTo(1f)
        syncPulse.animateTo(0f, tween(280, easing = FastOutSlowInEasing))
    }

    // ── Tactile Haptics ──
    LaunchedEffect(state.currentStepIdx) {
        if (AppSettings.hapticsEnabled && state.currentStepIdx > 0) {
            val step = state.currentStep
            val isSwapping = step.swappedIndices != null || step.phaseLabel.contains("SWAP", ignoreCase = true)
            if (isSwapping) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            } else {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    // ── Challenge-mode "glowing target" indices ──
    val challengeTargets = if (state.challengeInFlight) {
        challengeEligibleIndices(
            step = state.currentStep,
            nextStep = state.steps.getOrNull(state.currentStepIdx + 1),
            type = state.challengeState.questionType
        )
    } else emptySet()

    // ── Theory drawer backdrop blur ──
    val backdropBlur by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (state.showTheorySheet) AlgoTokens.backdropBlur else 0.dp,
        animationSpec = tween(300),
        label = "theoryBackdropBlur"
    )

    AlgoWorkspaceBackground(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(backdropBlur)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                VisualizerHeader(
                    algorithm = algorithm,
                    spec = spec,
                    state = state,
                    currentStep = state.currentStep,
                    onBack = onBack
                )

                // Canvas (65% weight)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(65f)
                ) {
                    if (spec != null) {
                        VisualizerHost(
                            spec = spec,
                            currentStep = state.currentStep,
                            arrayViewMode = state.arrayViewMode,
                            selectedCellIndices = state.challengeState.selectedIndices,
                            challengeTargetIndices = challengeTargets,
                            syncPulse = syncPulseState,
                            onCellClick = { idx ->
                                // Prediction in flight: only the cells the question
                                // actually offers are answerable. A stray tap can no
                                // longer poison the selection that gets scored, and
                                // taps on eligible cells are the same state the
                                // prompt's own keys read and submit.
                                if (!state.challengeInFlight || idx in challengeTargets) {
                                    state.setCellSelected(idx)
                                }
                            },
                            state = state
                        )
                    } else {
                        EmptyCanvas(algorithm)
                    }
                }

                // Boundary divider (M5: breathes only while playing; a step
                // change still spikes the pulse).
                AmbientGlowDivider(
                    pulseProvider = { syncPulse.value },
                    isPlaying = state.isPlaying
                )

                // Challenge prompt  lives BETWEEN the canvas and the code
                // trace, not on top of the canvas. Algorithm details stay
                // fully visible; the prompt stays in the natural "between"
                // reading position. Sized by content (not weight), so the
                // canvas + code trace still keep their 65/35 proportions.
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.challengeInFlight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    enter = fadeIn(tween(220)) + expandVertically(),
                    exit = fadeOut(tween(180)) + shrinkVertically()
                ) {
                    CanvasChallengePrompt(
                        algorithm = algorithm.id,
                        step = state.currentStep,
                        nextStep = state.steps.getOrNull(state.currentStepIdx + 1),
                        state = state.challengeState,
                        onStateChange = { state.updateChallenge { it } },
                        onContinueNext = { state.stepForward() }
                    )
                }

                // Code trace (35% weight).
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(35f)
                ) {
                    CodeTracePane(
                        step = state.currentStep,
                        algorithmName = algorithm.name,
                        syncPulse = syncPulseState,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Bottom playback rail  anchored to the screen bottom.
                PlaybackRail(state = state)
            }
        }

        //  Modals & Overlays (drawn OUTSIDE the blur so they stay sharp).
        //  The customize-input sheet is family-aware: each family has a
        //  different shape of user input, so we dispatch on `spec.id.family`.
        if (state.showInputSheet && spec != null) {
            when (spec.id.family) {
                VisualizerFamily.LINEAR_1D -> CustomizeInputSheet(
                    initialArray = state.arrayData,
                    initialSortOrder = state.lastAppliedSortOrder,
                    initialSearchTarget = if (spec.id == com.example.algolens.model.AlgorithmId.LINEAR_SEARCH || spec.id == com.example.algolens.model.AlgorithmId.BINARY_SEARCH) state.searchTarget else null,
                    showSearchTarget = spec.id == com.example.algolens.model.AlgorithmId.LINEAR_SEARCH || spec.id == com.example.algolens.model.AlgorithmId.BINARY_SEARCH,
                    onApply = { values, order, target ->
                        state.arrayData = values
                        state.lastAppliedSortOrder = order
                        state.searchTarget = target
                        state.currentStepIdx = 0
                        state.isPlaying = false
                        state.showInputSheet = false
                    },
                    onDismiss = { state.showInputSheet = false }
                )
                VisualizerFamily.BUFFER -> {
                    if (spec.id == com.example.algolens.model.AlgorithmId.STACK) {
                        CustomizeBufferSheet(
                            initialOps = state.bufferOps,
                            onApply = { ops ->
                                state.bufferOps = ops
                                state.currentStepIdx = 0
                                state.isPlaying = false
                                state.showInputSheet = false
                            },
                            onDismiss = { state.showInputSheet = false }
                        )
                    } else {
                        CustomizeQueueSheet(
                            initialOps = state.queueOps,
                            onApply = { ops ->
                                state.queueOps = ops
                                state.currentStepIdx = 0
                                state.isPlaying = false
                                state.showInputSheet = false
                            },
                            onDismiss = { state.showInputSheet = false }
                        )
                    }
                }
                VisualizerFamily.GRAPH_2D -> CustomizeGraphSheet(
                    algorithmId = spec.id,
                    initialValues = state.arrayData,
                    initialSearchKey = (state.graphConfig as? com.example.algolens.model.GraphCustomization.ForBst)?.searchKey,
                    initialStartNodeId = (state.graphConfig as? com.example.algolens.model.GraphCustomization.ForTraversal)?.startNodeId,
                    onApply = { config ->
                        state.graphConfig = config
                        state.currentStepIdx = 0
                        state.isPlaying = false
                        state.showInputSheet = false
                    },
                    onDismiss = { state.showInputSheet = false }
                )
            }
        }

        AlgorithmTheorySheet(
            algorithm = algorithm,
            isVisible = state.showTheorySheet,
            onDismiss = { state.showTheorySheet = false }
        )

        if (state.showTutorSheet) {
            AiTutorSheet(
                stepNumber = state.currentStepIdx + 1,
                explanation = state.currentStep.description,
                timeComplexity = algorithm.timeComplexity,
                spaceComplexity = algorithm.spaceComplexity,
                onDismiss = { state.showTutorSheet = false }
            )
        }

        if (state.showGuidedTour) {
            GuidedTourOverlay(onDismiss = { state.showGuidedTour = false })
        }
    }
}

/**
 * Defensive fallback for the (currently unreachable) case of an
 * algorithm with no [com.example.algolens.model.AlgorithmSpec]. The
 * registry guards `generateStepsForAlgorithm` so we should never
 * land here in production, but a clear empty state is friendlier
 * than a silent black canvas.
 */
@Composable
private fun EmptyCanvas(algorithm: Algorithm) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Visualizer not registered for ${algorithm.name}",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun VisualizerScreenPreview() {
    AlgoLensTheme {
        VisualizerScreen(
            algorithm = SampleData.algorithms.find { it.name == "Quick Sort" }
                ?: SampleData.algorithms.first(),
            onBack = {}
        )
    }
}
