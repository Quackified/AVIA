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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.GraphCustomization
import com.example.algolens.model.InputValidationResult
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * Graph / tree customize sheet. Body switches on the algorithm id so
 * the user sees fields that match the family:
 *   - HEAP              → "Heap values" text field
 *   - BINARY_SEARCH_TREE → "Values to insert" + "Search key" text fields
 *   - BFS / DFS         → "Start node" dropdown over the existing graph
 *
 * On apply, the sheet emits a [GraphCustomization] (ForHeap / ForBst /
 * ForTraversal) which the screen-state dispatcher stores in
 * `state.graphConfig` and feeds to the step generator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeGraphSheet(
    algorithmId: AlgorithmId,
    initialValues: List<Int> = emptyList(),
    initialSearchKey: Int? = null,
    initialStartNodeId: String? = null,
    availableNodeIds: List<String> = com.example.algolens.data.AlgorithmStepRepository.canonicalWeightedGraph().first.map { it.id },
    onApply: (GraphCustomization) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val safeNodes = remember(availableNodeIds) {
        availableNodeIds.ifEmpty { listOf("A") }
    }

    val title = when (algorithmId) {
        AlgorithmId.HEAP -> "Customize Heap"
        AlgorithmId.BINARY_SEARCH_TREE -> "Customize BST"
        AlgorithmId.BFS, AlgorithmId.DFS -> "Customize Traversal"
        else -> "Customize Graph"
    }
    val hint = when (algorithmId) {
        AlgorithmId.HEAP -> "Set the leaf values that build the heap"
        AlgorithmId.BINARY_SEARCH_TREE -> "Choose values to insert and a key to search for"
        AlgorithmId.BFS, AlgorithmId.DFS -> "Choose a starting node in the existing graph"
        else -> "Customize graph input"
    }

    // Internal state per family. We keep them all so a family switch
    // doesn't lose the user's input.
    var valuesStr by remember {
        mutableStateOf(
            initialValues.joinToString(", ").ifEmpty {
                when (algorithmId) {
                    AlgorithmId.HEAP -> com.example.algolens.data.AlgorithmStepRepository.DEFAULT_HEAP_INPUT.joinToString(", ")
                    AlgorithmId.BINARY_SEARCH_TREE -> com.example.algolens.data.AlgorithmStepRepository.defaultBstValues.joinToString(", ")
                    else -> ""
                }
            }
        )
    }
    var searchKeyStr by remember {
        mutableStateOf(
            (initialSearchKey ?: com.example.algolens.data.AlgorithmStepRepository.defaultBstSearchKey).toString()
        )
    }
    var startNodeId by remember(safeNodes, initialStartNodeId) {
        val resolved = if (initialStartNodeId != null && initialStartNodeId in safeNodes) {
            initialStartNodeId
        } else {
            safeNodes.first()
        }
        mutableStateOf(resolved)
    }

    val valuesValidation by remember(valuesStr) {
        derivedStateOf {
            val trimmed = valuesStr.trim()
            if (trimmed.isEmpty()) {
                InputValidationResult.Error("Values cannot be empty")
            } else {
                val tokens = trimmed.split(",")
                val invalid = tokens.filter { it.trim().toIntOrNull() == null }
                if (invalid.isNotEmpty()) {
                    InputValidationResult.Error("Invalid: '${invalid.first().trim()}'")
                } else {
                    val parsed = tokens.map { it.trim().toInt() }
                    when {
                        parsed.size > 15 -> InputValidationResult.Error("Maximum 15 values (current: ${parsed.size})")
                        parsed.any { it <= 0 || it > 999 } -> InputValidationResult.Error("Values must be 1..999")
                        else -> InputValidationResult.Valid(parsed)
                    }
                }
            }
        }
    }
    val searchKeyValidation by remember(searchKeyStr) {
        derivedStateOf {
            val v = searchKeyStr.trim().toIntOrNull()
            when {
                v == null -> InputValidationResult.Error("Search key must be an integer")
                v <= 0 || v > 999 -> InputValidationResult.Error("Search key must be 1..999")
                else -> InputValidationResult.Valid(listOf(v))
            }
        }
    }

    val isValid = (algorithmId != AlgorithmId.BINARY_SEARCH_TREE ||
                  (valuesValidation is InputValidationResult.Valid &&
                   searchKeyValidation is InputValidationResult.Valid)) &&
                  (algorithmId != AlgorithmId.HEAP || valuesValidation is InputValidationResult.Valid) &&
                  ((algorithmId != AlgorithmId.BFS && algorithmId != AlgorithmId.DFS) || startNodeId in safeNodes)

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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CanvasBackground)
                        .border(1.dp, BorderSubtle, CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Body — branches on algorithm id
            when (algorithmId) {
                AlgorithmId.HEAP -> {
                    ValuesField(
                        label = "HEAP VALUES (COMMA-SEPARATED):",
                        value = valuesStr,
                        accent = SecondaryPurple,
                        validation = valuesValidation,
                        onChange = { valuesStr = it }
                    )
                }
                AlgorithmId.BINARY_SEARCH_TREE -> {
                    ValuesField(
                        label = "VALUES TO INSERT (COMMA-SEPARATED):",
                        value = valuesStr,
                        accent = SecondaryPurple,
                        validation = valuesValidation,
                        onChange = { valuesStr = it }
                    )
                    SearchKeyField(
                        value = searchKeyStr,
                        validation = searchKeyValidation,
                        onChange = { searchKeyStr = it }
                    )
                }
                AlgorithmId.BFS, AlgorithmId.DFS -> {
                    StartNodeDropdown(
                        current = startNodeId,
                        nodes = safeNodes,
                        onSelect = { startNodeId = it }
                    )
                }
                else -> {
                    Text(
                        text = "No customize input available for this algorithm.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // Apply Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isValid) PrimaryCyan else PrimaryCyan.copy(alpha = 0.35f))
                    .clickable(enabled = isValid) {
                        val config: GraphCustomization = when (algorithmId) {
                            AlgorithmId.HEAP -> {
                                val parsed = (valuesValidation as InputValidationResult.Valid).parsed
                                GraphCustomization.ForHeap(parsed)
                            }
                            AlgorithmId.BINARY_SEARCH_TREE -> {
                                val parsedVals = (valuesValidation as InputValidationResult.Valid).parsed
                                val parsedKey = (searchKeyValidation as InputValidationResult.Valid).parsed.first()
                                GraphCustomization.ForBst(parsedVals, parsedKey)
                            }
                            AlgorithmId.BFS, AlgorithmId.DFS -> {
                                GraphCustomization.ForTraversal(startNodeId)
                            }
                            else -> GraphCustomization.ForHeap(emptyList())
                        }
                        onApply(config)
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Apply & Reset Visualizer",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isValid) DarkBackground else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

/**
 * Comma-separated integer-list field with a label and live validation
 * feedback. Reused by HEAP and BST for their "values" input.
 */
@Composable
private fun ValuesField(
    label: String,
    value: String,
    accent: Color,
    validation: InputValidationResult,
    onChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = AlgoType.microSize,
                letterSpacing = AlgoType.trackSection,
                fontWeight = FontWeight.Bold
            )
            if (validation is InputValidationResult.Valid) {
                Text(
                    text = "${validation.parsed.size} elements",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CanvasBackground)
                .border(
                    width = 1.dp,
                    color = when (validation) {
                        is InputValidationResult.Valid -> BorderCyan
                        is InputValidationResult.Error -> AccentRed.copy(alpha = 0.8f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = when (validation) {
                        is InputValidationResult.Valid -> accent
                        is InputValidationResult.Error -> TextPrimary
                    },
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = SolidColor(PrimaryCyan),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        when (val res = validation) {
            is InputValidationResult.Valid -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(AlgoGlyphs.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(11.dp))
                    Text("Ready: ${res.parsed.size} valid integers parsed", style = MaterialTheme.typography.labelSmall, color = AccentGreen, fontSize = AlgoType.microSize)
                }
            }
            is InputValidationResult.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(AlgoGlyphs.Alert, contentDescription = null, tint = AccentRed, modifier = Modifier.size(11.dp))
                    Text(res.message, style = MaterialTheme.typography.labelSmall, color = AccentRed, fontSize = AlgoType.microSize)
                }
            }
        }
    }
}

/**
 * Single-integer field for the BST search key.
 */
@Composable
private fun SearchKeyField(
    value: String,
    validation: InputValidationResult,
    onChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "SEARCH KEY:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = AlgoType.microSize,
            letterSpacing = AlgoType.trackSection,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CanvasBackground)
                .border(
                    width = 1.dp,
                    color = when (validation) {
                        is InputValidationResult.Valid -> BorderCyan
                        is InputValidationResult.Error -> AccentRed.copy(alpha = 0.8f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = when (validation) {
                        is InputValidationResult.Valid -> PurpleGlow
                        is InputValidationResult.Error -> TextPrimary
                    },
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = SolidColor(PrimaryCyan),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        when (val res = validation) {
            is InputValidationResult.Valid -> {}
            is InputValidationResult.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(AlgoGlyphs.Alert, contentDescription = null, tint = AccentRed, modifier = Modifier.size(11.dp))
                    Text(res.message, style = MaterialTheme.typography.labelSmall, color = AccentRed, fontSize = AlgoType.microSize)
                }
            }
        }
    }
}

/**
 * Dropdown picker for the BFS/DFS start node. The user picks from the
 * existing A/B/C/D/E graph topology.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartNodeDropdown(
    current: String,
    nodes: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "START NODE:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = AlgoType.microSize,
            letterSpacing = AlgoType.trackSection,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CanvasBackground)
                .border(1.dp, BorderCyan, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Node $current",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = AlgoGlyphs.ChevronDown,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                nodes.forEach { id ->
                    DropdownMenuItem(
                        text = { Text("Node $id", color = TextPrimary, fontSize = AlgoType.bodySize) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeGraphSheetHeapPreview() {
    AlgoLensTheme {
        CustomizeGraphSheet(
            algorithmId = AlgorithmId.HEAP,
            initialValues = listOf(3, 8, 9, 2, 6, 1, 5),
            onApply = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeGraphSheetBstPreview() {
    AlgoLensTheme {
        CustomizeGraphSheet(
            algorithmId = AlgorithmId.BINARY_SEARCH_TREE,
            initialValues = listOf(50, 30, 70, 20, 40, 60, 80),
            initialSearchKey = 40,
            onApply = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeGraphSheetTraversalPreview() {
    AlgoLensTheme {
        CustomizeGraphSheet(
            algorithmId = AlgorithmId.BFS,
            initialStartNodeId = "A",
            onApply = {},
            onDismiss = {}
        )
    }
}
