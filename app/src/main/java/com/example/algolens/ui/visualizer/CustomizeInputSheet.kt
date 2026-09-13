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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.InputValidationResult
import com.example.algolens.model.SortOrder
import com.example.algolens.ui.components.SegmentedToggle
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderMedium
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import kotlin.random.Random

/**
 * Preset data structure for edge case configurations.
 */
data class ArrayPreset(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String
)

val PRESET_OPTIONS = listOf(
    ArrayPreset("Random", "Random", Icons.Default.Shuffle, "Randomized integers between 10-95"),
    ArrayPreset("Already Sorted", "Already Sorted", Icons.AutoMirrored.Filled.TrendingUp, "Ascending order (Best case for Insertion/Bubble)"),
    ArrayPreset("Reverse Sorted", "Reverse Sorted", Icons.AutoMirrored.Filled.TrendingDown, "Descending order (Worst case for many sorts)"),
    ArrayPreset("All Equal", "All Equal", Icons.Default.FormatListNumbered, "Identical elements (Duplicates edge case)"),
    ArrayPreset("Nearly Sorted", "Nearly Sorted", Icons.Default.RestartAlt, "Sorted except for a single swapped pair")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeInputSheet(
    initialArray: List<Int>,
    initialSortOrder: SortOrder = SortOrder.ASC,
    initialSearchTarget: Int? = null,
    showSearchTarget: Boolean = false,
    onApply: (List<Int>, SortOrder, Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var inputStr by remember { mutableStateOf(initialArray.joinToString(", ")) }
    var arrayLength by remember { mutableFloatStateOf(initialArray.size.toFloat().coerceIn(5f, 15f)) }
    var selectedPreset by remember { mutableStateOf<String?>(null) }
    var sortOrder by remember { mutableStateOf(initialSortOrder) }
    var searchTargetStr by remember { mutableStateOf(initialSearchTarget?.toString() ?: "") }

    // Preset data generator
    fun generatePreset(preset: String, length: Int): List<Int> {
        val len = length.coerceIn(5, 15)
        return when (preset) {
            "Random" -> {
                val numbers = (10..95).shuffled()
                numbers.take(len)
            }
            "Already Sorted" -> {
                val step = (85 / len).coerceAtLeast(4)
                List(len) { 10 + it * step }
            }
            "Reverse Sorted" -> {
                val step = (85 / len).coerceAtLeast(4)
                List(len) { 10 + (len - 1 - it) * step }
            }
            "All Equal" -> {
                val value = 42
                List(len) { value }
            }
            "Nearly Sorted" -> {
                val step = (85 / len).coerceAtLeast(4)
                val list = MutableList(len) { 10 + it * step }
                if (list.size >= 4) {
                    val idx1 = list.size / 3
                    val idx2 = (list.size * 2) / 3
                    val temp = list[idx1]
                    list[idx1] = list[idx2]
                    list[idx2] = temp
                }
                list
            }
            else -> List(len) { Random.nextInt(10, 95) }
        }
    }

    // Input parser and validator
    val validationResult by remember(inputStr) {
        derivedStateOf {
            val trimmed = inputStr.trim()
            if (trimmed.isEmpty()) {
                InputValidationResult.Error("Please enter comma-separated numbers (e.g. 12, 34, 56)")
            } else {
                val tokens = trimmed.split(",")
                val invalidTokens = tokens.filter { it.trim().toIntOrNull() == null }
                if (invalidTokens.isNotEmpty()) {
                    InputValidationResult.Error("Invalid input: '${invalidTokens.first().trim()}' is not a valid number")
                } else {
                    val parsed = tokens.mapNotNull { it.trim().toIntOrNull() }
                    when {
                        parsed.size < 3 -> InputValidationResult.Error("Array must contain at least 3 elements (current: ${parsed.size})")
                        parsed.size > 16 -> InputValidationResult.Error("Maximum 16 elements supported for visualization (current: ${parsed.size})")
                        parsed.any { it <= 0 || it > 999 } -> InputValidationResult.Error("Numbers must be positive integers between 1 and 999")
                        else -> InputValidationResult.Valid(parsed)
                    }
                }
            }
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
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customize Input Array",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select an edge-case preset or type custom numbers",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 8.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(AlgoTokens.iconButtonSm)
                        .clip(CircleShape)
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                }
            }

            // ── 1. Edge Case Presets LazyRow (Material 3 FilterChips) ──
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                Text(
                    text = "EDGE CASE PRESETS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4),
                    contentPadding = PaddingValues(vertical = AlgoTokens.space1)
                ) {
                    items(PRESET_OPTIONS, key = { it.id }) { preset ->
                        val isSelected = selectedPreset == preset.id
                        val presetColor = when (preset.id) {
                            "Random" -> PrimaryCyan
                            "Already Sorted" -> AccentGreen
                            "Reverse Sorted" -> AccentPink
                            "All Equal" -> AccentYellow
                            else -> SecondaryPurple
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPreset = preset.id
                                val targetLen = arrayLength.toInt().coerceIn(8, 10)
                                arrayLength = targetLen.toFloat()
                                val generated = generatePreset(preset.id, targetLen)
                                inputStr = generated.joinToString(", ")
                            },
                            label = {
                                Text(
                                    text = preset.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(AlgoTokens.inlineIconSm),
                                    tint = if (isSelected) presetColor else TextMuted
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CanvasBackground,
                                labelColor = TextSecondary,
                                iconColor = TextMuted,
                                selectedContainerColor = presetColor.copy(alpha = 0.18f),
                                selectedLabelColor = presetColor,
                                selectedLeadingIconColor = presetColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BorderSubtle,
                                selectedBorderColor = presetColor,
                                borderWidth = if (isSelected) AlgoTokens.strokeMedium else AlgoTokens.strokeThin
                            ),
                            shape = RoundedCornerShape(AlgoTokens.radiusSm)
                        )
                    }
                }
            }

            // ── 2. Manual Array Input Field ──
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ARRAY VALUES (COMMA-SEPARATED):",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (validationResult is InputValidationResult.Valid) {
                        val count = (validationResult as InputValidationResult.Valid).parsed.size
                        Text(
                            text = "$count elements",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CanvasBackground)
                        .border(
                            width = AlgoTokens.strokeThin,
                            color = when (validationResult) {
                                is InputValidationResult.Valid -> BorderCyan
                                is InputValidationResult.Error -> AccentRed.copy(alpha = 0.8f)
                            },
                            shape = RoundedCornerShape(AlgoTokens.radiusSm)
                        )
                        .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3)
                ) {
                    BasicTextField(
                        value = inputStr,
                        onValueChange = {
                            inputStr = it
                            selectedPreset = null
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = when (validationResult) {
                                is InputValidationResult.Valid -> PrimaryCyan
                                is InputValidationResult.Error -> TextPrimary
                            },
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(PrimaryCyan),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Real-time Validation Feedback Message
                when (val res = validationResult) {
                    is InputValidationResult.Valid -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                            modifier = Modifier.padding(top = AlgoTokens.space1)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                            Text(
                                text = "Ready: ${res.parsed.size} valid integers parsed",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGreen,
                                fontSize = 8.sp
                            )
                        }
                    }
                    is InputValidationResult.Error -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                            modifier = Modifier.padding(top = AlgoTokens.space1)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                            Text(
                                text = res.message,
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentRed,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }

            // ── 3. Array Length Slider ──
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ARRAY LENGTH SLIDER:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${arrayLength.toInt()} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
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

            // ── 4. Sort Order Toggle ──
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                Text(
                    text = "SORT ORDER:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold
                )
                SegmentedToggle(
                    options = listOf("ASC" to SortOrder.ASC, "DESC" to SortOrder.DESC),
                    selectedKey = sortOrder,
                    accent = PrimaryCyan,
                    onSelect = { key -> sortOrder = key as SortOrder }
                )
            }

            // ── 5. Search Target (Linear / Binary Search only) ──
            if (showSearchTarget) {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEARCH FOR (VALUE):",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (searchTargetStr.isNotBlank()) {
                            val parsedTarget = searchTargetStr.trim().toIntOrNull()
                            Text(
                                text = if (parsedTarget != null) "target set" else "invalid",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (parsedTarget != null) AccentGreen else AccentRed,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CanvasBackground)
                            .border(
                                width = AlgoTokens.strokeThin,
                                color = if (searchTargetStr.isBlank() || searchTargetStr.trim().toIntOrNull() != null)
                                    BorderCyan
                                else
                                    AccentRed.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(AlgoTokens.radiusSm)
                            )
                            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3)
                    ) {
                        BasicTextField(
                            value = searchTargetStr,
                            onValueChange = { searchTargetStr = it },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = if (searchTargetStr.isBlank() || searchTargetStr.trim().toIntOrNull() != null)
                                    PrimaryCyan
                                else
                                    TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            cursorBrush = SolidColor(PrimaryCyan),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ── 6. Apply Button ──
            val isValid = validationResult is InputValidationResult.Valid &&
                !(showSearchTarget && searchTargetStr.isNotBlank() && searchTargetStr.trim().toIntOrNull() == null)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(if (isValid) PrimaryCyan else PrimaryCyan.copy(alpha = 0.35f))
                    .clickable(enabled = isValid) {
                        if (validationResult is InputValidationResult.Valid) {
                            val parsed = (validationResult as InputValidationResult.Valid).parsed
                            val parsedTarget = if (showSearchTarget && searchTargetStr.isNotBlank())
                                searchTargetStr.trim().toIntOrNull()
                            else
                                null
                            onApply(parsed, sortOrder, parsedTarget)
                            onDismiss()
                        }
                    }
                    .padding(vertical = AlgoTokens.space5),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Apply & Reset Visualizer",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isValid) DarkBackground else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space3))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeInputSheetPreview() {
    AlgoLensTheme {
        CustomizeInputSheet(
            initialArray = listOf(64, 34, 25, 12, 22, 11, 90),
            onApply = { _, _, _ -> },
            onDismiss = {}
        )
    }
}
