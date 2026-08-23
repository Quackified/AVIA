package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.OfflineBadge
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.tutor.AiTutorSheet
import kotlinx.coroutines.delay

enum class ArrayViewMode {
    CELLS, // Box / Cell mode with top & bottom pointers & code trace (matches screenshot)
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
            if (algorithm.name.equals("Binary Search", ignoreCase = true)) {
                listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
            } else {
                listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
            }
        )
    }

    // Generate steps from unified repository
    val steps = remember(algorithm, arrayData) {
        AlgorithmStepRepository.generateStepsForAlgorithm(algorithm, arrayData)
    }
    val codeLines = remember(algorithm) {
        AlgorithmStepRepository.getCodeLinesForAlgorithm(algorithm)
    }

    var currentStepIdx by remember(steps) { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeedMs by remember { mutableLongStateOf(600L) }

    var showInputSheet by remember { mutableStateOf(false) }
    var showTutorSheet by remember { mutableStateOf(false) }

    val totalSteps = steps.size.coerceAtLeast(1)
    val currentStep = steps.getOrElse(currentStepIdx.coerceIn(0, totalSteps - 1)) { steps.first() }

    // Auto Playback ticker
    LaunchedEffect(isPlaying, currentStepIdx, totalSteps, playbackSpeedMs) {
        if (isPlaying) {
            if (currentStepIdx < totalSteps - 1) {
                delay(playbackSpeedMs)
                currentStepIdx++
            } else {
                isPlaying = false
            }
        }
    }

    val isArrayBased = algorithm.category == "Sorting" || algorithm.category == "Searching"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Header & Badges ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
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

                // Complexity Badges
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyanSubtle)
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
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
                            .background(CardBackgroundElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (arrayViewMode == ArrayViewMode.CELLS) PrimaryCyan else Color.Transparent)
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
                                .background(if (arrayViewMode == ArrayViewMode.BARS) PrimaryCyan else Color.Transparent)
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

                    // Edit Input Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyanSubtle)
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                            .clickable { showInputSheet = true }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Edit Input",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "Edit Input",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        // ── 2. Unified Visualizer Canvas ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Buffer Mode (Stack / Queue)
                currentStep.renderMode == VisualizerRenderMode.BUFFER ||
                        algorithm.name.equals("Stack", ignoreCase = true) ||
                        algorithm.name.equals("Queue", ignoreCase = true) -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BufferVisualizer(
                            step = currentStep,
                            isStack = algorithm.name.equals("Stack", ignoreCase = true),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Graph / Tree Mode (BFS, DFS, BST, Heap)
                currentStep.renderMode == VisualizerRenderMode.GRAPH_TREE ||
                        algorithm.category == "Graph Traversal" ||
                        algorithm.name.equals("Binary Search Tree", ignoreCase = true) ||
                        algorithm.name.equals("Heap", ignoreCase = true) -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Live Step Description Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackground)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = currentStep.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 13.sp,
                                fontSize = 8.5.sp
                            )
                        }

                        GraphTreeVisualizer(
                            step = currentStep,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                }

                // Array / Search Algorithms (Cells vs Bars)
                else -> {
                    if (arrayViewMode == ArrayViewMode.CELLS) {
                        CellArrayVisualizer(
                            step = currentStep,
                            codeLines = codeLines,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackground)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = currentStep.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    lineHeight = 13.sp,
                                    fontSize = 8.5.sp
                                )
                            }

                            ArrayVisualizer(
                                step = currentStep,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── 3. Timeline Scrubber Slider ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Slider(
                value = currentStepIdx.toFloat(),
                onValueChange = {
                    isPlaying = false
                    currentStepIdx = it.toInt().coerceIn(0, totalSteps - 1)
                },
                valueRange = 0f..(totalSteps - 1).coerceAtLeast(1).toFloat(),
                steps = (totalSteps - 2).coerceAtLeast(0),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryCyan,
                    activeTrackColor = PrimaryCyan,
                    inactiveTrackColor = CardBackground
                ),
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start (Step 1)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextNavy,
                    fontSize = 7.5.sp
                )
                Text(
                    text = "Step ${currentStepIdx + 1} / $totalSteps",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextNavy,
                    fontSize = 7.5.sp
                )
            }
        }

        // ── 4. Bottom Playback Control Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackgroundElevated)
                .border(width = 1.dp, color = BorderSubtle)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .clickable {
                        isPlaying = false
                        currentStepIdx = 0
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = TextMuted,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Skip Back
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .clickable {
                        isPlaying = false
                        if (currentStepIdx > 0) currentStepIdx--
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Step Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Play / Pause FAB
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PrimaryCyan)
                    .clickable {
                        if (!isPlaying && currentStepIdx >= totalSteps - 1) {
                            currentStepIdx = 0
                        }
                        isPlaying = !isPlaying
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = DarkBackground,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Skip Forward
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .clickable {
                        isPlaying = false
                        if (currentStepIdx < totalSteps - 1) currentStepIdx++
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Step Forward",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Speed Selector Toggle
            val speedLabel = when (playbackSpeedMs) {
                1000L -> "0.5x"
                600L -> "1.0x"
                300L -> "2.0x"
                else -> "1.0x"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackground)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .clickable {
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

            // AI Tutor Bot Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PurpleSubtle)
                    .border(1.dp, SecondaryPurple.copy(alpha = 0.4f), CircleShape)
                    .clickable { showTutorSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Tutor",
                    tint = PurpleGlow,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }

    // ── Bottom Sheets ──
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

    if (showTutorSheet) {
        AiTutorSheet(
            stepNumber = currentStepIdx + 1,
            explanation = currentStep.description,
            timeComplexity = algorithm.timeComplexity,
            spaceComplexity = algorithm.spaceComplexity,
            onDismiss = { showTutorSheet = false }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun VisualizerScreenPreview() {
    AlgoLensTheme {
        VisualizerScreen(
            algorithm = SampleData.algorithms.find { it.name == "Quick Sort" } ?: SampleData.algorithms.first(),
            onBack = {}
        )
    }
}
