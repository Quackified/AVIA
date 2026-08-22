package com.example.algolens.ui.visualizer

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.SortStep
import com.example.algolens.model.StepType
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

enum class TraceLanguage(val label: String) {
    KOTLIN("Kotlin"),
    PYTHON("Python"),
    JAVA("Java")
}

@Composable
fun CodeTracePane(
    step: SortStep,
    stepIdx: Int,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf(TraceLanguage.KOTLIN) }

    val kotlinCode = listOf(
        "fun bubbleSort(arr: IntArray) {",
        "  val n = arr.size",
        "  for (i in 0 until n - 1) {",
        "    for (j in 0 until n - i - 1) {",
        "      if (arr[j] > arr[j + 1]) {",
        "        arr.swap(j, j + 1)",
        "      }",
        "    }",
        "  }",
        "}"
    )

    val pythonCode = listOf(
        "def bubble_sort(arr):",
        "    n = len(arr)",
        "    for i in range(n - 1):",
        "        for j in range(n - i - 1):",
        "            if arr[j] > arr[j + 1]:",
        "                arr[j], arr[j+1] = arr[j+1], arr[j]",
        "    return arr"
    )

    val javaCode = listOf(
        "public void bubbleSort(int[] arr) {",
        "    int n = arr.length;",
        "    for (int i = 0; i < n - 1; i++) {",
        "        for (int j = 0; j < n - i - 1; j++) {",
        "            if (arr[j] > arr[j + 1]) {",
        "                int temp = arr[j];",
        "                arr[j] = arr[j + 1];",
        "                arr[j + 1] = temp;",
        "            }",
        "        }",
        "    }",
        "}"
    )

    val currentCode = when (selectedLanguage) {
        TraceLanguage.KOTLIN -> kotlinCode
        TraceLanguage.PYTHON -> pythonCode
        TraceLanguage.JAVA -> javaCode
    }

    val activeLineNum = when (step.type) {
        StepType.COMPARE -> 5
        StepType.SWAP -> 6
        StepType.DONE -> 9
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Top: Code Box ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackgroundElevated)
                .border(1.dp, SecondaryPurple.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Code Header with Language Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = PurpleGlow,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Source Code Trace",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Language Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CanvasBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        TraceLanguage.entries.forEach { lang ->
                            val isSel = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) SecondaryPurple else Color.Transparent)
                                    .clickable { selectedLanguage = lang }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = lang.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) Color.White else TextMuted,
                                    fontSize = 7.5.sp
                                )
                            }
                        }
                    }
                }

                // Code Listing
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CanvasBackground)
                        .padding(vertical = 4.dp)
                ) {
                    itemsIndexed(currentCode) { index, line ->
                        val lineNum = index + 1
                        val isActive = lineNum == activeLineNum

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isActive) CyanSubtle else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lineNum.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isActive) PrimaryCyan else TextDark,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.width(22.dp)
                            )
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isActive) PrimaryCyan else TextPrimary,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // ── Bottom Split: Variable Inspector & Call Stack ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Live Variable Inspector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardBackground)
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "LIVE VARIABLE INSPECTOR:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iVal = stepIdx / 5
                        val jVal = step.indices.firstOrNull() ?: 0

                        VarBadge(label = "i", value = iVal.toString())
                        VarBadge(label = "j", value = jVal.toString())
                        VarBadge(label = "swapped", value = (step.type == StepType.SWAP).toString())
                    }
                }
            }

            // Memory Call Stack
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardBackground)
                    .border(1.dp, SecondaryPurple.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MEMORY CALL STACK DEPTH:",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleGlow,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(PurpleSubtle)
                            .border(1.dp, SecondaryPurple.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "bubbleSort(arr, n=${step.array.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleGlow,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "i=${stepIdx / 5}, j=${step.indices.firstOrNull() ?: 0}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VarBadge(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(CyanSubtle)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label = $value",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CodeTracePanePreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        Box(modifier = Modifier.height(450.dp).padding(16.dp)) {
            CodeTracePane(
                step = SortStep(
                    array = listOf(64, 34, 25, 12, 22, 11, 90),
                    type = StepType.COMPARE,
                    indices = listOf(1, 2),
                    description = "Comparing 34 and 25"
                ),
                stepIdx = 2
            )
        }
    }
}
