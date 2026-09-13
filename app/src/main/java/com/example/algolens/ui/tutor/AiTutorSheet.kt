package com.example.algolens.ui.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.components.ComplexityCard
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorSheet(
    stepNumber: Int,
    explanation: String,
    timeComplexity: String = "O(n²)",
    spaceComplexity: String = "O(1)",
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val defaultText = explanation.ifBlank {
        "Comparing elements at the current step. In Bubble Sort, adjacent elements are evaluated in pairs and swapped if they are out of order, bubbling larger values to their correct sorted position."
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
                    .background(Color.White.copy(alpha = 0.15f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.iconButtonMd)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(PurpleSubtle)
                            .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusSm)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PurpleGlow,
                            modifier = Modifier.size(AlgoTokens.inlineIconMd)
                        )
                    }

                    Column {
                        Text(
                            text = "AI Tutor",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Offline Step Intelligence",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                // Step Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PurpleSubtle)
                        .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AlgoTokens.space2)
                                .clip(CircleShape)
                                .background(PurpleGlow)
                        )
                        Text(
                            text = "Step $stepNumber",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderSubtle, thickness = AlgoTokens.strokeThin)

            // Explanation Text
            Text(
                text = defaultText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            // Complexity Card
            ComplexityCard(
                timeComplexity = timeComplexity,
                spaceComplexity = spaceComplexity
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                listOf("Trace Full", "Next Concept", "Why Swap?").forEach { label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(PurpleSubtle)
                            .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.25f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .clickable { }
                            .padding(vertical = AlgoTokens.space4),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space5))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun AiTutorSheetPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        AiTutorSheet(
            stepNumber = 4,
            explanation = "Comparing elements at index 1 (34) and index 2 (25). Since 34 > 25, a swap will occur in the next step to bubble the larger value upward.",
            timeComplexity = "O(n²)",
            spaceComplexity = "O(1)",
            onDismiss = {}
        )
    }
}
