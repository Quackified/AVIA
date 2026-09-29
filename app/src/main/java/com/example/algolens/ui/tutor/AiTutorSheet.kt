package com.example.algolens.ui.tutor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.AlgoHairline
import com.example.algolens.ui.components.ComplexityCard
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.visualizer.VisualizerStep

/**
 * Interactive In-Visualizer AI Tutor Sheet.
 *
 * Provides:
 *  - Real-time step intelligence explaining the exact operation, pointers, and variables.
 *  - In-sheet mini transport allowing the user to step forward/backward while keeping the tutor active.
 *  - Interactive quick prompt chips ("Why this action?", "Invariant check", "What's next?", "Complexity")
 *    with immediate offline pedagogical responses.
 *  - "Discuss with AVIA in Chat →" action bridging into a dedicated chat thread with a persistent return-to-canvas bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorSheet(
    algorithm: Algorithm,
    currentStep: VisualizerStep,
    stepNumber: Int,
    totalSteps: Int,
    onStepBack: () -> Unit,
    onStepForward: () -> Unit,
    onOpenFullChat: (prompt: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var selectedTopic by remember(stepNumber) { mutableStateOf<String?>("Why this action?") }

    val baseExplanation = currentStep.description.ifBlank {
        "Processing ${algorithm.name} step $stepNumber. The algorithm is maintaining its execution invariant across the active elements."
    }

    // Dynamic responses for quick-prompt questions
    val topicAnswer = remember(selectedTopic, stepNumber, currentStep) {
        when (selectedTopic) {
            "Why this action?" -> {
                val expr = currentStep.comparisonExpr
                if (!expr.isNullOrBlank()) {
                    "Evaluation: $expr. The algorithm inspects this condition to maintain its ordering or exploration guarantee."
                } else if (currentStep.phaseLabel.contains("SWAP", ignoreCase = true)) {
                    "Inversion identified! The elements were out of target order and are transposed to progress towards sorted order."
                } else {
                    "The current step executes phase '${currentStep.phaseLabel}'. Elements are indexed and pointers shift toward the next boundary."
                }
            }
            "Invariant check" -> {
                "Invariant for ${algorithm.name}: All elements processed before the current partition/boundary satisfy the algorithm's ordering constraint."
            }
            "What's next?" -> {
                if (stepNumber < totalSteps) {
                    "Step ${stepNumber + 1} will advance pointers to evaluate the next candidate element or boundary transition."
                } else {
                    "Execution is complete! The data structure or array has reached its final state."
                }
            }
            "Complexity" -> {
                "Time Complexity: ${algorithm.timeComplexity}. Space Complexity: ${algorithm.spaceComplexity}. Offline analysis shows current step cost is bounded by O(1) local state mutation."
            }
            else -> baseExplanation
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = AlgoTokens.space3, bottom = AlgoTokens.space2)
                    .size(width = AlgoTokens.iconButtonLg, height = AlgoTokens.space2)
                    .clip(RoundedCornerShape(AlgoTokens.radiusXl))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            // ── 1. Tutor Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(PurpleSubtle)
                            .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusSm)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Spark,
                            contentDescription = null,
                            tint = PurpleGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "AI Tutor · ${algorithm.name}",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Offline Step Intelligence",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize
                        )
                    }
                }

                // In-Sheet Mini Transport Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeHairline, BorderSubtle, CircleShape)
                            .clickable(enabled = stepNumber > 1) { onStepBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.StepBack,
                            contentDescription = "Previous Step",
                            tint = if (stepNumber > 1) TextPrimary else TextMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(PurpleSubtle)
                            .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusXs))
                            .padding(horizontal = AlgoTokens.space3, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Step $stepNumber of $totalSteps",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeHairline, BorderSubtle, CircleShape)
                            .clickable(enabled = stepNumber < totalSteps) { onStepForward() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.StepForward,
                            contentDescription = "Next Step",
                            tint = if (stepNumber < totalSteps) TextPrimary else TextMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            AlgoHairline(color = BorderSubtle)

            // ── 2. Primary Step Explanation ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CardBackgroundElevated)
                    .border(AlgoTokens.strokeThin, BorderSubtle.copy(alpha = 0.6f), RoundedCornerShape(AlgoTokens.radiusSm))
                    .padding(AlgoTokens.space4)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                    Text(
                        text = "LIVE STEP CONTEXT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackSection
                    )
                    Text(
                        text = baseExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            // ── 3. Quick-Prompt Question Chips ──
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                Text(
                    text = "EXPLORE IN DEPTH",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize,
                    letterSpacing = AlgoType.trackSection
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    val topics = listOf("Why this action?", "Invariant check", "What's next?", "Complexity")
                    topics.forEach { topic ->
                        val isSelected = selectedTopic == topic
                        val chipBg = if (isSelected) PurpleSubtle else CardBackgroundElevated
                        val chipBorder = if (isSelected) SecondaryPurple else BorderSubtle
                        val chipText = if (isSelected) PurpleGlow else TextSecondary

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(chipBg)
                                .border(AlgoTokens.strokeThin, chipBorder, RoundedCornerShape(AlgoTokens.radiusXs))
                                .clickable { selectedTopic = topic }
                                .padding(horizontal = AlgoTokens.space3, vertical = 6.dp)
                        ) {
                            Text(
                                text = topic,
                                style = MaterialTheme.typography.labelSmall,
                                color = chipText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = AlgoType.microSize
                            )
                        }
                    }
                }
            }

            // ── 4. Interactive Answer Box ──
            AnimatedVisibility(visible = selectedTopic != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(PurpleSubtle.copy(alpha = 0.4f))
                        .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(AlgoTokens.space4)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Spark,
                                contentDescription = null,
                                tint = PurpleGlow,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = selectedTopic ?: "TUTOR ANALYSIS",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = AlgoType.microSize
                            )
                        }
                        Text(
                            text = topicAnswer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // ── 5. Complexity Summary Card ──
            ComplexityCard(
                timeComplexity = algorithm.timeComplexity,
                spaceComplexity = algorithm.spaceComplexity
            )

            // ── 6. "Discuss with AVIA in Chat" Action Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(SecondaryPurple)
                    .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), accent = PurpleGlow)
                    .clickable {
                        onDismiss()
                        val discussionPrompt = "Can you explain Step $stepNumber of ${algorithm.name} (${currentStep.phaseLabel})? Here is the context: $baseExplanation"
                        onOpenFullChat(discussionPrompt)
                    }
                    .padding(vertical = AlgoTokens.space3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Chat,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Discuss with AVIA in Chat →",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space4))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun AiTutorSheetPreview() {
    AlgoLensTheme {
        AiTutorSheet(
            algorithm = SampleData.algorithms.first(),
            currentStep = VisualizerStep(
                stepIndex = 3,
                phaseLabel = "PARTITIONING",
                description = "Comparing arr[1] (34) with pivot (25). Since 34 > 25, right pointer advances."
            ),
            stepNumber = 4,
            totalSteps = 18,
            onStepBack = {},
            onStepForward = {},
            onOpenFullChat = {},
            onDismiss = {}
        )
    }
}
