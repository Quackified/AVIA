package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.algolens.data.AppSettings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.VisualizerFamily
import com.example.algolens.ui.components.AlgoWorkspaceBackground
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.tutor.AiTutorSheet

/**
 * Thin shell that composes the workspace regions and the modal
 * overlays. Every region ([VisualizerHeader], the [VisualizerHost]
 * canvas, [StageLegend], [TraceStrip], [InstrumentDeck],
 * [PlaybackRail]) is either a separate file or a small composable;
 * this body holds the cross-cutting concerns (workspace background,
 * status / nav bar padding, theory-drawer backdrop blur,
 * challenge-mode prompt overlay, modal sheet hosting, sync-pulse
 * heartbeat) and nothing else.
 *
 * Phase 5A note: the stage Box is deliberately the parent of both the
 * legend and the Focus Deck. Overlaying them there — rather than adding
 * more rows to the Column — is what stops a code-view expansion from
 * re-measuring (and therefore resizing) the canvas behind it.
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
    // Keys on the *committed* playhead, not `displayStepIdx`: the pulse is a
    // "you have settled on a new step" cue, and firing it on every step a finger
    // crosses would strobe. A scrub therefore flashes once, on release.
    val haptic = LocalHapticFeedback.current
    val syncPulse = remember { Animatable(0f) }
    val syncPulseState = remember { derivedStateOf { syncPulse.value } }

    LaunchedEffect(state.currentStepIdx) {
        // Fast, crisp pulse decay (280ms) avoids overlapping flashes
        syncPulse.snapTo(1f)
        syncPulse.animateTo(0f, tween(280, easing = FastOutSlowInEasing))
    }

    // ── Tactile Haptics ──
    // Keys on `displayStepIdx` so the tick fires per step *during* a scrub as
    // well as during playback / single-stepping. The swap-heavy steps still get
    // the heavier cue, so the drag reads as "scrubbing through comparisons" and
    // then "landing on a swap".
    LaunchedEffect(state.displayStepIdx) {
        if (AppSettings.hapticsEnabled && state.displayStepIdx > 0) {
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

                // Contextual sub-mode selector / legend slot:
                // - BST: BstModeSelector ([Search | In-Order | Pre-Order | Post-Order])
                // - Queue: QueueModeSelector ([Linear FIFO | Circular Ring])
                // - Linear 1D: StageLegend (pointers, comparisons, active indices)
                // - Graph / Stack: omitted to give stage visualizer maximum vertical canvas headroom
                when (spec?.id) {
                    AlgorithmId.BINARY_SEARCH_TREE -> {
                        BstModeSelector(
                            currentMode = state.bstMode,
                            onSelectMode = { state.selectBstMode(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space1)
                        )
                    }
                    AlgorithmId.QUEUE -> {
                        QueueModeSelector(
                            currentVariant = state.queueVariant,
                            onSelectVariant = { state.selectQueueVariant(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space1)
                        )
                    }
                    else -> {
                        if (spec?.id?.family == VisualizerFamily.LINEAR_1D) {
                            StageLegend(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space1)
                            )
                        }
                    }
                }

                // ── Workspace Area: [Canvas Visualizer > Deck Instruments] ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // 1. Canvas Visualizer (fills available vertical space)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
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

                    // 2. Expand/Collapse Handle Bar (toggles Focus Mode)
                    WorkspaceHandleBar(
                        isFocusMode = state.canvasFocusMode,
                        onToggleFocusMode = { state.canvasFocusMode = !state.canvasFocusMode }
                    )

                    // 3. Challenge Prompt (when challenge in flight and not in focus mode)
                    AnimatedVisibility(
                        visible = state.challengeInFlight && !state.canvasFocusMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
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

                    // 4. Deck Instruments (Slides off-screen when Focus Mode is active)
                    AnimatedVisibility(
                        visible = !state.canvasFocusMode,
                        enter = expandVertically(
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(tween(200)),
                        exit = shrinkVertically(
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                            )
                        ) + fadeOut(tween(150))
                    ) {
                        InstrumentDeck(
                            state = state,
                            algorithm = algorithm,
                            currentStep = state.currentStep,
                            syncPulse = syncPulseState,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bottom playback rail anchored to the screen bottom.
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
                    initialSearchTarget = if (spec.acceptsSearchTarget) state.searchTarget else null,
                    showSearchTarget = spec.acceptsSearchTarget,
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
                    if (spec.isStack) {
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
                    initialValues = state.initialGraphSheetValues(spec),
                    initialSearchKey = state.initialGraphSearchKey,
                    initialStartNodeId = state.effectiveTraversalStartNodeId,
                    availableNodeIds = state.effectiveTraversalNodeIds,
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
