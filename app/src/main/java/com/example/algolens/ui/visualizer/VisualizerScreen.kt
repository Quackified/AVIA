package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.AlgoWorkspaceBackground
import com.example.algolens.ui.components.AmbientGlowDivider
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.smoothPanelExpansion
import com.example.algolens.ui.tutor.AiTutorSheet
import kotlinx.coroutines.delay

/**
 * ============================================================================
 *  VisualizerScreen — IDE-Style Workspace Coordinator
 * ============================================================================
 *  Connects the three workspace regions into one continuous surface:
 *
 *   ┌──────────────────────────────────────┐
 *   │  Compact Header (title / modes)      │
 *   ├──────────────────────────────────────┤
 *   │  Algorithm Canvas        (55%)       │ ← challenge prompts & glowing
 *   ├─────── ambient glow divider ─────────┤   targets live ON the canvas
 *   │  ▶ ⏮ ⏭ ──scrubber── 1x 🏆 ✦        │ ← playback embedded in boundary
 *   ├──────────────────────────────────────┤
 *   │  Synchronized Code Trace  (45%)      │ ← variable chips + semantic
 *   └──────────────────────────────────────┘   rails mirror canvas states
 *
 *  Cross-feature synchronicity: a shared [syncPulse] heartbeat fires on
 *  every step transition and drives the divider pulse, the canvas cell
 *  mirror-glow and the code trace active-line glow simultaneously.
 * ============================================================================
 */
enum class ArrayViewMode {
    CELLS, // Box / Cell mode with top & bottom pointers
    BARS   // Vertical animated bar chart
}

@Composable
fun VisualizerScreen(
    algorithm: Algorithm,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var arrayViewMode by remember { mutableStateOf(ArrayViewMode.CELLS) }
    var arrayData by remember {
        mutableStateOf(
            AlgorithmRegistry.specFor(algorithm.id)?.defaultInput
                ?: AlgorithmStepRepository.DEFAULT_INPUT
        )
    }

    // Generate steps from unified repository
    val steps = remember(algorithm, arrayData) {
        AlgorithmStepRepository.generateStepsForAlgorithm(algorithm, arrayData)
    }

    var currentStepIdx by remember(steps) { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeedMs by remember { mutableLongStateOf(600L) }

    var showInputSheet by remember { mutableStateOf(false) }
    var showTutorSheet by remember { mutableStateOf(false) }
    var showTheorySheet by remember { mutableStateOf(false) }
    var showGuidedTour by remember { mutableStateOf(false) }

    var challengeState by remember { mutableStateOf(ChallengeState()) }

    // Guard against an empty / ungenerated step list. Without this,
    // `steps.first()` throws NoSuchElementException on the very first frame
    // before the repository finishes generating steps for some algorithm
    // types (e.g. switching algorithms mid-flight or a generator that
    // returns an empty list for a degenerate input).
    val totalSteps = steps.size.coerceAtLeast(1)
    val resolvedStep: VisualizerStep = if (steps.isEmpty()) {
        // Render a benign placeholder step so the rest of the UI can compose
        // safely. The first non-empty regeneration will replace it.
        VisualizerStep(stepIndex = 0, array = arrayData)
    } else {
        steps.getOrElse(currentStepIdx.coerceIn(0, totalSteps - 1)) { steps.first() }
    }
    val currentStep: VisualizerStep = resolvedStep

    // Playback locks while a challenge question awaits an answer
    val challengeLocked = challengeState.isActive && challengeState.feedback == null

    // Auto Playback ticker
    LaunchedEffect(isPlaying, currentStepIdx, totalSteps, playbackSpeedMs, challengeLocked) {
        if (challengeLocked) {
            isPlaying = false
        } else if (isPlaying) {
            if (currentStepIdx < totalSteps - 1) {
                delay(playbackSpeedMs)
                currentStepIdx++
            } else {
                isPlaying = false
            }
        }
    }

    val isArrayBased = algorithm.category == "Sorting" || algorithm.category == "Searching"

    // ── Cross-feature sync heartbeat: spikes on every state transition ──
    val syncPulse = remember { Animatable(0f) }
    // State<Float> view of the pulse for render-phase reads downstream
    // (Animatable is not a State; this wrapper keeps the per-frame pulse
    // value out of composition entirely).
    val syncPulseState = remember { derivedStateOf { syncPulse.value } }
    LaunchedEffect(currentStepIdx) {
        syncPulse.snapTo(1f)
        syncPulse.animateTo(0f, tween(550))
    }

    // ── Challenge glowing target cells (the canvas IS the answer surface) ──
    val challengeTargets = if (challengeState.isActive && challengeState.feedback == null) {
        challengeEligibleIndices(
            step = currentStep,
            nextStep = steps.getOrNull(currentStepIdx + 1),
            type = challengeState.questionType
        )
    } else {
        emptySet()
    }

    // ── Animated backdrop blur for the Theory side-drawer (16.dp token) ──
    val backdropBlur by animateDpAsState(
        targetValue = if (showTheorySheet) AlgoTokens.backdropBlur else 0.dp,
        animationSpec = tween(300),
        label = "theoryBackdropBlur"
    )

    AlgoWorkspaceBackground(modifier = modifier.fillMaxSize()) {
        // Workspace content — blurred while the theory drawer overlays it
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
                // ── 1. Compact Workspace Header ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .smoothPanelExpansion(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CardBackground)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                    .clickable { onBack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = algorithm.name.uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Step ${currentStepIdx + 1} of $totalSteps",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 8.5.sp
                                )
                            }
                        }

                        // Right cluster: tour, theory drawer, complexity pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardBackground)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { showGuidedTour = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = "Guided Tour",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PurpleSubtle)
                                    .border(
                                        1.dp,
                                        SecondaryPurple.copy(alpha = 0.4f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { showTheorySheet = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Theory Sheet",
                                    tint = PurpleGlow,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanSubtle)
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .clickable { showTheorySheet = true }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TIME ${algorithm.timeComplexity}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PurpleSubtle)
                                    .border(1.dp, SecondaryPurple.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .clickable { showTheorySheet = true }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SPACE ${algorithm.spaceComplexity}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleGlow,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Description / Subtitle
                    Text(
                        text = when (algorithm.name.lowercase()) {
                            "quick sort" -> "A divide & conquer algorithm that partitions the array around a pivot element."
                            "bubble sort" -> "Repeatedly steps through the list, compares adjacent elements and swaps them if out of order."
                            "binary search" -> "Search a sorted array by repeatedly dividing the search interval in half."
                            "breadth-first search (bfs)", "bfs" -> "Level-by-level exploration using a Queue data structure."
                            "depth-first search (dfs)", "dfs" -> "Explores deep branch paths using recursion/stack before backtracking."
                            "stack" -> "LIFO (Last In First Out) linear data structure for push/pop/peek operations."
                            "queue" -> "FIFO (First In First Out) linear data structure for enqueue/dequeue operations."
                            "binary search tree" -> "Hierarchical node structure where left child < node < right child."
                            else -> "${algorithm.name} algorithm execution and state inspection."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp,
                        lineHeight = 12.sp
                    )

                    // Mode Selector for Array Algorithms (Cells vs Bars) + Edit Input
                    if (isArrayBased) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardBackground)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (arrayViewMode == ArrayViewMode.CELLS) PrimaryCyan
                                            else Color.Transparent
                                        )
                                        .clickable { arrayViewMode = ArrayViewMode.CELLS }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Box / Trace Mode",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (arrayViewMode == ArrayViewMode.CELLS) DarkBackground else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (arrayViewMode == ArrayViewMode.BARS) PrimaryCyan
                                            else Color.Transparent
                                        )
                                        .clickable { arrayViewMode = ArrayViewMode.BARS }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Bar Chart Mode",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (arrayViewMode == ArrayViewMode.BARS) DarkBackground else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            // Customize Input Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanSubtle)
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                    .clickable { showInputSheet = true }
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Customize",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "CUSTOMIZE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }

                // ── 2. Algorithm Canvas Region (55% of workspace) ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(55f)
                ) {
                    val hostSpec = AlgorithmRegistry.specFor(algorithm.id)
                    if (hostSpec != null) {
                        VisualizerHost(
                            spec = hostSpec,
                            currentStep = currentStep,
                            arrayViewMode = arrayViewMode,
                            selectedCellIndices = challengeState.selectedIndices,
                            challengeTargetIndices = challengeTargets,
                            syncPulse = syncPulseState,
                            onCellClick = { tappedIdx ->
                                val currentSet = challengeState.selectedIndices
                                val updatedSet = if (currentSet.contains(tappedIdx)) {
                                    currentSet - tappedIdx
                                } else {
                                    currentSet + tappedIdx
                                }
                                challengeState = challengeState.copy(selectedIndices = updatedSet)
                            }
                        )
                    } else {
                        // Defensive fallback — should not happen because
                        // the registry guards generateStepsForAlgorithm,
                        // but render a clear empty state if it ever does.
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

                    // ── Challenge prompt overlays the canvas itself (not a banner) ──
                    androidx.compose.animation.AnimatedVisibility(
                        visible = challengeState.isActive,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        enter = fadeIn(tween(220)) + expandVertically(),
                        exit = fadeOut(tween(180)) + shrinkVertically()
                    ) {
                        CanvasChallengePrompt(
                            step = currentStep,
                            nextStep = steps.getOrNull(currentStepIdx + 1),
                            state = challengeState,
                            onStateChange = { challengeState = it },
                            onContinueNext = {
                                if (currentStepIdx < totalSteps - 1) {
                                    currentStepIdx++
                                }
                            }
                        )
                    }
                }

                // ── 3. Boundary: Ambient Glow Divider (pulses on state transitions) ──
                AmbientGlowDivider(pulseProvider = { syncPulse.value })

                // (Playback rail relocated to the bottom of the workspace — see section 5.)

                // ── 4. Synchronized Code Trace & Variable Inspector (45%) ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(45f)
                ) {
                    CodeTracePane(
                        step = currentStep,
                        algorithmName = algorithm.name,
                        syncPulse = syncPulseState,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // ── 5. Bottom Playback Rail (anchored to the screen bottom) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AlgoTokens.glassElevated.copy(alpha = 0.92f))
                        .border(width = 1.dp, color = BorderSubtle)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset
                    RailIconButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        boxSize = 30,
                        iconSize = 13,
                        tint = TextMuted,
                        container = CardBackground,
                        borderColor = BorderSubtle,
                        enabled = !challengeLocked
                    ) {
                        isPlaying = false
                        currentStepIdx = 0
                    }

                    // Step Back
                    RailIconButton(
                        icon = Icons.Default.SkipPrevious,
                        contentDescription = "Step Back",
                        boxSize = 32,
                        iconSize = 15,
                        tint = TextPrimary,
                        container = CardBackground,
                        borderColor = BorderSubtle,
                        enabled = !challengeLocked
                    ) {
                        isPlaying = false
                        if (currentStepIdx > 0) currentStepIdx--
                    }

                    // Play / Pause
                    RailIconButton(
                        icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        boxSize = 42,
                        iconSize = 20,
                        tint = DarkBackground,
                        container = PrimaryCyan,
                        borderColor = Color.Transparent,
                        enabled = !challengeLocked
                    ) {
                        if (!isPlaying && currentStepIdx >= totalSteps - 1) {
                            currentStepIdx = 0
                        }
                        isPlaying = !isPlaying
                    }

                    // Step Forward
                    RailIconButton(
                        icon = Icons.Default.SkipNext,
                        contentDescription = "Step Forward",
                        boxSize = 32,
                        iconSize = 15,
                        tint = TextPrimary,
                        container = CardBackground,
                        borderColor = BorderSubtle,
                        enabled = !challengeLocked
                    ) {
                        isPlaying = false
                        if (currentStepIdx < totalSteps - 1) currentStepIdx++
                    }

                    // Timeline Scrubber
                    Slider(
                        value = currentStepIdx.toFloat(),
                        onValueChange = {
                            isPlaying = false
                            currentStepIdx = it.toInt().coerceIn(0, totalSteps - 1)
                        },
                        valueRange = 0f..(totalSteps - 1).coerceAtLeast(1).toFloat(),
                        steps = (totalSteps - 2).coerceAtLeast(0),
                        enabled = !challengeLocked,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryCyan,
                            activeTrackColor = PrimaryCyan,
                            inactiveTrackColor = CardBackground
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .alpha(if (challengeLocked) AlgoTokens.disabledAlpha else 1f)
                    )

                    // Speed Selector Toggle
                    val speedLabel = when (playbackSpeedMs) {
                        1000L -> "0.5x"
                        600L -> "1.0x"
                        300L -> "2.0x"
                        else -> "1.0x"
                    }
                    Box(
                        modifier = Modifier
                            .alpha(if (challengeLocked) AlgoTokens.disabledAlpha else 1f)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CardBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                            .clickable(enabled = !challengeLocked) {
                                playbackSpeedMs = when (playbackSpeedMs) {
                                    1000L -> 600L
                                    600L -> 300L
                                    300L -> 1000L
                                    else -> 600L
                                }
                            }
                            .padding(horizontal = 7.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = speedLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    }

                    // Challenge Mode Toggle
                    RailIconButton(
                        icon = Icons.Default.EmojiEvents,
                        contentDescription = "Challenge Mode",
                        boxSize = 32,
                        iconSize = 14,
                        tint = if (challengeState.isActive) AccentPink else TextMuted,
                        container = if (challengeState.isActive) PinkSubtle else CardBackground,
                        borderColor = if (challengeState.isActive) AccentPink else BorderSubtle
                    ) {
                        isPlaying = false
                        challengeState = challengeState.copy(isActive = !challengeState.isActive)
                    }

                    // AI Tutor
                    RailIconButton(
                        icon = Icons.Default.AutoAwesome,
                        contentDescription = "AI Tutor",
                        boxSize = 32,
                        iconSize = 14,
                        tint = PurpleGlow,
                        container = PurpleSubtle,
                        borderColor = SecondaryPurple.copy(alpha = 0.4f)
                    ) {
                        showTutorSheet = true
                    }
                }
            }
        }

        // ── Modals & Overlays ──
        if (showInputSheet) {
            CustomizeInputSheet(
                initialArray = arrayData,
                onApply = { newList ->
                    arrayData = newList
                    currentStepIdx = 0
                    isPlaying = false
                },
                onDismiss = { showInputSheet = false }
            )
        }

        // Theory side-drawer stays composed for enter/exit animations;
        // the workspace behind it is blurred via `backdropBlur`.
        AlgorithmTheorySheet(
            algorithm = algorithm,
            isVisible = showTheorySheet,
            onDismiss = { showTheorySheet = false }
        )

        if (showTutorSheet) {
            AiTutorSheet(
                stepNumber = currentStepIdx + 1,
                explanation = currentStep.description,
                timeComplexity = algorithm.timeComplexity,
                spaceComplexity = algorithm.spaceComplexity,
                onDismiss = { showTutorSheet = false }
            )
        }

        if (showGuidedTour) {
            GuidedTourOverlay(onDismiss = { showGuidedTour = false })
        }
    }
}

/**
 * Compact circular transport button for the embedded boundary playback rail.
 */
@Composable
private fun RailIconButton(
    icon: ImageVector,
    contentDescription: String,
    boxSize: Int,
    iconSize: Int,
    tint: Color,
    container: Color,
    borderColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .alpha(if (enabled) 1f else AlgoTokens.disabledAlpha)
            .size(boxSize.dp)
            .clip(CircleShape)
            .background(container)
            .border(1.dp, borderColor, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize.dp)
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
