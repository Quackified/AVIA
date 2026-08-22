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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.BubbleSort
import com.example.algolens.model.Algorithm
import com.example.algolens.model.GraphEdge
import com.example.algolens.model.GraphNode
import com.example.algolens.model.SortStep
import com.example.algolens.ui.components.OfflineBadge
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
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

enum class ViewPerspective {
    CANVAS,
    CODE
}

enum class DataStructureMode {
    ARRAY,
    GRAPH
}

@Composable
fun VisualizerScreen(
    algorithm: Algorithm,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var perspective by remember { mutableStateOf(ViewPerspective.CANVAS) }
    var dsMode by remember {
        mutableStateOf(
            if (algorithm.category.equals("Graph Traversal", ignoreCase = true)) DataStructureMode.GRAPH
            else DataStructureMode.ARRAY
        )
    }

    var arrayData by remember { mutableStateOf(listOf(64, 34, 25, 12, 22, 11, 90)) }
    var steps by remember(arrayData) { mutableStateOf(BubbleSort.generateSteps(arrayData)) }
    var currentStepIdx by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    var showInputSheet by remember { mutableStateOf(false) }
    var showTutorSheet by remember { mutableStateOf(false) }

    // Sample Dijkstra Graph Data
    val graphNodes = remember {
        listOf(
            GraphNode("A", 45f, 35f, "A"),
            GraphNode("B", 125f, 25f, "B"),
            GraphNode("C", 205f, 40f, "C"),
            GraphNode("D", 50f, 110f, "D"),
            GraphNode("E", 130f, 120f, "E"),
            GraphNode("F", 210f, 105f, "F")
        )
    }
    val graphEdges = remember {
        listOf(
            GraphEdge("A", "B", 4),
            GraphEdge("A", "D", 2),
            GraphEdge("B", "C", 5),
            GraphEdge("B", "E", 1),
            GraphEdge("D", "E", 3),
            GraphEdge("E", "C", 2),
            GraphEdge("E", "F", 4),
            GraphEdge("C", "F", 3)
        )
    }
    val dijkstraSteps = remember {
        listOf(
            Triple(setOf("A"), "A", emptySet<String>()),
            Triple(setOf("A", "D"), "D", setOf("A-D")),
            Triple(setOf("A", "D", "B"), "B", setOf("A-B")),
            Triple(setOf("A", "D", "B", "E"), "E", setOf("A-B", "B-E")),
            Triple(setOf("A", "D", "B", "E", "C"), "C", setOf("A-B", "B-E", "E-C")),
            Triple(setOf("A", "D", "B", "E", "C", "F"), "F", setOf("A-B", "B-E", "E-F"))
        )
    }

    val maxSteps = if (dsMode == DataStructureMode.ARRAY) steps.size else dijkstraSteps.size

    // Playback loop
    LaunchedEffect(isPlaying, currentStepIdx, maxSteps) {
        if (isPlaying) {
            if (currentStepIdx < maxSteps - 1) {
                delay(500)
                currentStepIdx++
            } else {
                isPlaying = false
            }
        }
    }

    val currentSortStep = steps.getOrElse(currentStepIdx.coerceIn(0, steps.size - 1)) { steps.first() }
    val maxVal = arrayData.maxOrNull() ?: 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Header Control Bar ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            text = if (dsMode == DataStructureMode.ARRAY) algorithm.name else "Dijkstra's Pathfinding",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step ${currentStepIdx + 1} of $maxSteps",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 8.5.sp
                        )
                    }
                }

                OfflineBadge()
            }

            // Universal Perspective Toggle Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackgroundElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (perspective == ViewPerspective.CANVAS) PrimaryCyan else Color.Transparent)
                        .clickable { perspective = ViewPerspective.CANVAS }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Visual Canvas",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (perspective == ViewPerspective.CANVAS) DarkBackground else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (perspective == ViewPerspective.CODE) SecondaryPurple else Color.Transparent)
                        .clickable { perspective = ViewPerspective.CODE }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Code & Stack",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (perspective == ViewPerspective.CODE) Color.White else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
        }

        // ── 2. Main Content Area ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 2.dp)
        ) {
            if (perspective == ViewPerspective.CANVAS) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sub-bar: DS mode switcher + Edit Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (dsMode == DataStructureMode.ARRAY) CyanSubtle else CardBackground)
                                    .border(
                                        1.dp,
                                        if (dsMode == DataStructureMode.ARRAY) PrimaryCyan else BorderSubtle,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        dsMode = DataStructureMode.ARRAY
                                        currentStepIdx = 0
                                        isPlaying = false
                                    }
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "1D Array",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (dsMode == DataStructureMode.ARRAY) PrimaryCyan else TextMuted,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (dsMode == DataStructureMode.GRAPH) GreenSubtle else CardBackground)
                                    .border(
                                        1.dp,
                                        if (dsMode == DataStructureMode.GRAPH) AccentGreen else BorderSubtle,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        dsMode = DataStructureMode.GRAPH
                                        currentStepIdx = 0
                                        isPlaying = false
                                    }
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "2D Graph",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (dsMode == DataStructureMode.GRAPH) AccentGreen else TextMuted,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (dsMode == DataStructureMode.ARRAY) {
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
                                    fontSize = 8.5.sp
                                )
                            }
                        }
                    }

                    // Live Step Description Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        val desc = if (dsMode == DataStructureMode.ARRAY) {
                            currentSortStep.description
                        } else {
                            val graphStep = dijkstraSteps.getOrElse(currentStepIdx) { dijkstraSteps.last() }
                            "Dijkstra: Active node [${graphStep.second}], Visited ${graphStep.first.size}/${graphNodes.size} nodes."
                        }

                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }

                    // Visualizer Canvas Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (dsMode == DataStructureMode.ARRAY) {
                            BarVisualizer(
                                step = currentSortStep,
                                maxVal = maxVal,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val gStep = dijkstraSteps.getOrElse(currentStepIdx) { dijkstraSteps.last() }
                            GraphVisualizer(
                                nodes = graphNodes,
                                edges = graphEdges,
                                visitedNodes = gStep.first,
                                activeNode = gStep.second,
                                pathEdges = gStep.third,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Timeline Scrubber Slider
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Slider(
                            value = currentStepIdx.toFloat(),
                            onValueChange = {
                                isPlaying = false
                                currentStepIdx = it.toInt().coerceIn(0, maxSteps - 1)
                            },
                            valueRange = 0f..(maxSteps - 1).coerceAtLeast(1).toFloat(),
                            steps = (maxSteps - 2).coerceAtLeast(0),
                            colors = SliderDefaults.colors(
                                thumbColor = if (dsMode == DataStructureMode.ARRAY) PrimaryCyan else AccentGreen,
                                activeTrackColor = if (dsMode == DataStructureMode.ARRAY) PrimaryCyan else AccentGreen,
                                inactiveTrackColor = CardBackground
                            ),
                            modifier = Modifier.height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Start (Step 1)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextNavy,
                                fontSize = 8.sp
                            )
                            Text(
                                text = "Step ${currentStepIdx + 1}/$maxSteps",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextNavy,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            } else {
                CodeTracePane(
                    step = currentSortStep,
                    stepIdx = currentStepIdx,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // ── 3. Bottom Playback Control Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackgroundElevated)
                .border(width = 1.dp, color = BorderSubtle)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
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
                    modifier = Modifier.size(16.dp)
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

            // Play / Pause glowing FAB
            val activeColor = if (dsMode == DataStructureMode.ARRAY) PrimaryCyan else AccentGreen
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(activeColor)
                    .clickable {
                        if (!isPlaying && currentStepIdx >= maxSteps - 1) {
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
                        if (currentStepIdx < maxSteps - 1) currentStepIdx++
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
            explanation = currentSortStep.description,
            timeComplexity = algorithm.timeComplexity,
            spaceComplexity = algorithm.spaceComplexity,
            onDismiss = { showTutorSheet = false }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun VisualizerScreenPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        VisualizerScreen(
            algorithm = com.example.algolens.data.SampleData.algorithms.first(),
            onBack = {}
        )
    }
}
