package com.example.algolens.ui.practice

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.BarUnsorted
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

data class PracticeOption(
    val id: Int,
    val label: String,
    val isCorrect: Boolean
)

@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val practiceArray = remember { listOf(12, 45, 23, 89, 34) }
    val pivotIdx = 3 // 89
    val pointerLeft = 1 // 45
    val pointerRight = 4 // 34

    val options = remember {
        listOf(
            PracticeOption(0, "Index 1 (Value: 45)", false),
            PracticeOption(1, "Index 4 (Value: 34)", true),
            PracticeOption(2, "Index 2 (Value: 23)", false),
            PracticeOption(3, "Index 0 (Value: 12)", false)
        )
    }

    var selectedOptionId by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
    ) {
        // ── 1. Header & Progress ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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

                Text(
                    text = "Practice Mode: QuickSort",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question 3 of 5",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 8.5.sp
                )
                Text(
                    text = "60%",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            }

            LinearProgressIndicator(
                progress = { 0.6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = PrimaryCyan,
                trackColor = CardBackground,
                strokeCap = StrokeCap.Round
            )
        }

        // ── 2. Scrollable Question Content ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Frozen Visualizer Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackgroundElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP 4 · QUICKSORT PARTITION",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp,
                            letterSpacing = 0.8.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PurpleSubtle)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Frozen Canvas",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleGlow,
                                fontSize = 8.sp
                            )
                        }
                    }

                    // Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        practiceArray.forEachIndexed { idx, valNum ->
                            val isPivot = idx == pivotIdx
                            val isLeft = idx == pointerLeft
                            val isRight = idx == pointerRight
                            val barCol = when {
                                isPivot -> SecondaryPurple
                                isLeft || isRight -> AccentYellow
                                else -> BarUnsorted
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Pointer Badges
                                Box(
                                    modifier = Modifier.height(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPivot) {
                                        Text("PIVOT", style = MaterialTheme.typography.labelSmall, color = PurpleGlow, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                    } else if (isLeft) {
                                        Text("L", style = MaterialTheme.typography.labelSmall, color = AccentYellow, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                    } else if (isRight) {
                                        Text("R", style = MaterialTheme.typography.labelSmall, color = AccentYellow, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight((valNum / 90f).coerceIn(0.1f, 1f))
                                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                            .background(barCol)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = valNum.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isPivot) PurpleGlow else if (isLeft || isRight) AccentYellow else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Question Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUESTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp,
                            letterSpacing = 0.8.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(OrangeSubtle)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Medium",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Which element will be swapped with the pivot (89) in the next step?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Multiple Choice Options
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEach { opt ->
                    val isSelected = selectedOptionId == opt.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) CyanSubtle else CardBackground)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) PrimaryCyan else BorderSubtle,
                                shape = RoundedCornerShape(9.dp)
                            )
                            .clickable(enabled = !isSubmitted) { selectedOptionId = opt.id }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) PrimaryCyan else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )

                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(DarkBackground)
                                .border(
                                    width = if (isSelected) 4.dp else 1.dp,
                                    color = if (isSelected) PrimaryCyan else TextDark,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            // Submit Button
            if (!isSubmitted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedOptionId != null) PrimaryCyan else Color(0xFF1A2540))
                        .clickable(enabled = selectedOptionId != null) { isSubmitted = true }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Submit Answer",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedOptionId != null) DarkBackground else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Feedback Overlay
            AnimatedVisibility(
                visible = isSubmitted && selectedOptionId != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val isCorrect = selectedOptionId?.let { id -> options.find { it.id == id }?.isCorrect } ?: false

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCorrect) GreenSubtle else RedSubtle)
                        .border(
                            1.dp,
                            if (isCorrect) AccentGreen else AccentRed,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isCorrect) AccentGreen else AccentRed)
                            )
                            Text(
                                text = if (isCorrect) "Correct! Excellent partition analysis." else "Incorrect Choice",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isCorrect) AccentGreen else AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (isCorrect) {
                                "Index 4 (value 34) is the final smaller element scanned from right. Swapping 34 with pivot 89 correctly places 89 into its sorted partition slot."
                            } else {
                                "QuickSort partition swaps the pivot element with the rightmost element smaller than pivot. Index 4 (value 34) is swapped with 89."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCorrect) AccentGreen else PrimaryCyan)
                                .clickable {
                                    selectedOptionId = null
                                    isSubmitted = false
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Next Question →",
                                style = MaterialTheme.typography.labelMedium,
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun PracticeScreenPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        PracticeScreen(onBack = {})
    }
}
