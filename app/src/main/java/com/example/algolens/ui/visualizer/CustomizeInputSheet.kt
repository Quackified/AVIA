package com.example.algolens.ui.visualizer

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeInputSheet(
    initialArray: List<Int>,
    onApply: (List<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var inputStr by remember { mutableStateOf(initialArray.joinToString(", ")) }
    var arrayLength by remember { mutableFloatStateOf(initialArray.size.toFloat()) }
    var selectedPreset by remember { mutableStateOf<String?>(null) }

    fun generatePreset(preset: String, length: Int): List<Int> {
        return when (preset) {
            "Random" -> List(length) { Random.nextInt(10, 95) }
            "Sorted" -> List(length) { (it + 1) * (85 / length) + 10 }
            "Reverse Sorted" -> List(length) { (length - it) * (85 / length) + 10 }
            "Nearly Sorted" -> {
                val list = MutableList(length) { (it + 1) * (85 / length) + 10 }
                if (list.size > 2) {
                    val tmp = list[1]
                    list[1] = list[2]
                    list[2] = tmp
                }
                list
            }
            else -> List(length) { Random.nextInt(10, 95) }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Customize Input",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextMuted,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onDismiss() }
                )
            }

            // Input TextField
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Array Values (comma-separated):",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CanvasBackground)
                        .border(1.dp, BorderCyan, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = inputStr,
                        onValueChange = {
                            inputStr = it
                            selectedPreset = null
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryCyan,
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(PrimaryCyan),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Presets
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Preset Configurations:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Random", "Sorted", "Reverse Sorted", "Nearly Sorted").forEach { preset ->
                        val isSel = selectedPreset == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) CyanSubtle else CanvasBackground)
                                .border(
                                    1.dp,
                                    if (isSel) PrimaryCyan else BorderSubtle,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    selectedPreset = preset
                                    val generated = generatePreset(preset, arrayLength.toInt())
                                    inputStr = generated.joinToString(", ")
                                }
                                .padding(horizontal = 7.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) PrimaryCyan else TextMuted,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Length Slider
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Array Length:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${arrayLength.toInt()} elements",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = arrayLength,
                    onValueChange = {
                        arrayLength = it
                        val len = it.toInt()
                        if (selectedPreset != null) {
                            val generated = generatePreset(selectedPreset!!, len)
                            inputStr = generated.joinToString(", ")
                        } else {
                            val parsed = inputStr.split(",").mapNotNull { s -> s.trim().toIntOrNull() }
                            val adjusted = if (parsed.size < len) {
                                parsed + List(len - parsed.size) { Random.nextInt(10, 95) }
                            } else {
                                parsed.take(len)
                            }
                            inputStr = adjusted.joinToString(", ")
                        }
                    },
                    valueRange = 5f..15f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryCyan,
                        activeTrackColor = PrimaryCyan,
                        inactiveTrackColor = CardBackgroundElevated
                    )
                )
            }

            // Apply Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryCyan)
                    .clickable {
                        val parsed = inputStr.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .filter { it > 0 }
                        if (parsed.isNotEmpty()) {
                            onApply(parsed)
                        }
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Apply & Reset Visualizer",
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeInputSheetPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        CustomizeInputSheet(
            initialArray = listOf(64, 34, 25, 12, 22, 11, 90),
            onApply = {},
            onDismiss = {}
        )
    }
}
