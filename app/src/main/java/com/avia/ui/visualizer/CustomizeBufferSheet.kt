package com.avia.ui.visualizer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.avia.model.BufferOp
import com.avia.model.InputValidationResult
import com.avia.model.QueueOp
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.BorderCyan
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.AlgoType
import com.avia.ui.components.AlgoGlyphs

/**
 * Buffer customize sheet (Stack / Queue). Tailored to the buffer model
 * — a sequence of PUSH / POP / PEEK events for stack, or ENQUEUE /
 * DEQUEUE events for queue. The user builds a list of [BufferOp] (or
 * [QueueOp]) and applies it; the screen-state dispatcher stores it and
 * the step generator walks the list to produce the visualizer steps.
 *
 * Why a separate sheet (rather than reusing the array-input sheet)?
 * Because the buffer's "input" is a *sequence of operations*, not a
 * list of integers. The user thinks in terms of `push(10) → push(20) →
 * pop` rather than `[10, 20, -]`. Encoding this in a numeric array
 * would be lossy and confusing.
 *
 * Two composables are exposed (one per family) so the caller's
 * `onApply` callback can have a type-safe [List<BufferOp>] or
 * [List<QueueOp>] signature. The internal editor state and UI are
 * factored into [BufferOpEditor].
 */
private const val MAX_ROWS = 12

/**
 * Stack customize sheet — emits a `List<BufferOp>` on apply.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeBufferSheet(
    initialOps: List<BufferOp>,
    onApply: (List<BufferOp>) -> Unit,
    onDismiss: () -> Unit,
) {
    BufferOpEditor(
        title = "Customize Stack",
        hint = "Build a sequence of PUSH / POP / PEEK events",
        kinds = listOf("PUSH", "POP", "PEEK"),
        accent = SecondaryPurple,
        valueAccent = PurpleGlow,
        initialRows = initialOps.map { op ->
            when (op) {
                is BufferOp.Push -> OpRowData("PUSH", op.value.toString())
                BufferOp.Pop -> OpRowData("POP", "")
                BufferOp.Peek -> OpRowData("PEEK", "")
            }
        }.ifEmpty { listOf(OpRowData("PUSH", "10"), OpRowData("PUSH", "20"), OpRowData("POP", "")) },
        presets = listOf(
            "Default Demo" to listOf("PUSH:10", "PUSH:25", "PUSH:42", "PEEK", "POP", "PUSH:88"),
            "LIFO Stress" to listOf("PUSH:1", "PUSH:2", "PUSH:3", "POP", "POP"),
            "LIFO + Peeks" to listOf("PUSH:50", "PEEK", "PUSH:75", "PEEK", "POP"),
        ),
        onApply = { rows ->
            onApply(rows.mapNotNull { row ->
                when (row.kind) {
                    "PUSH" -> row.valueText.trim().toIntOrNull()?.let { BufferOp.Push(it) }
                    "POP" -> BufferOp.Pop
                    "PEEK" -> BufferOp.Peek
                    else -> null
                }
            })
        },
        onDismiss = onDismiss,
    )
}

/**
 * Queue customize sheet — emits a `List<QueueOp>` on apply.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeQueueSheet(
    initialOps: List<QueueOp>,
    onApply: (List<QueueOp>) -> Unit,
    onDismiss: () -> Unit,
) {
    BufferOpEditor(
        title = "Customize Queue",
        hint = "Build a sequence of ENQ / DEQ / PEEK events",
        kinds = listOf("ENQ", "DEQ", "PEEK"),
        accent = AccentYellow,
        valueAccent = AccentYellow,
        initialRows = initialOps.map { op ->
            when (op) {
                is QueueOp.Enqueue -> OpRowData("ENQ", op.value.toString())
                QueueOp.Dequeue -> OpRowData("DEQ", "")
                QueueOp.Peek -> OpRowData("PEEK", "")
            }
        }.ifEmpty { listOf(OpRowData("ENQ", "15"), OpRowData("ENQ", "30"), OpRowData("DEQ", "")) },
        presets = listOf(
            "Default Demo" to listOf("ENQ:15", "ENQ:30", "ENQ:45", "DEQ", "ENQ:60"),
            "FIFO Stress" to listOf("ENQ:1", "ENQ:2", "ENQ:3", "DEQ", "DEQ"),
            "FIFO + Peeks" to listOf("ENQ:10", "PEEK", "ENQ:20", "PEEK", "DEQ"),
            "Empty & Fill" to listOf("DEQ", "ENQ:99", "ENQ:100"),
        ),
        onApply = { rows ->
            onApply(rows.mapNotNull { row ->
                when (row.kind) {
                    "ENQ" -> row.valueText.trim().toIntOrNull()?.let { QueueOp.Enqueue(it) }
                    "DEQ" -> QueueOp.Dequeue
                    "PEEK" -> QueueOp.Peek
                    else -> null
                }
            })
        },
        onDismiss = onDismiss,
    )
}

/**
 * Internal row data — `kind` is one of the op labels ("PUSH", "POP",
 * "PEEK", "ENQ", "DEQ") and `valueText` is the raw value the user
 * typed (only meaningful for PUSH/ENQ).
 */
private data class OpRowData(val kind: String, val valueText: String)

/**
 * Shared editor body used by both [CustomizeBufferSheet] and
 * [CustomizeQueueSheet]. Generic in the row editor (text-based) so
 * the caller's `onApply` can produce the right op type.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BufferOpEditor(
    title: String,
    hint: String,
    kinds: List<String>,
    accent: Color,
    valueAccent: Color,
    initialRows: List<OpRowData>,
    presets: List<Pair<String, List<String>>>,
    onApply: (List<OpRowData>) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var rows by remember { mutableStateOf(initialRows) }

    val validation by remember(rows, kinds) {
        derivedStateOf {
            val cleaned = rows.map { row ->
                when (row.kind) {
                    "PUSH", "ENQ" -> {
                        val v = row.valueText.trim().toIntOrNull()
                        when {
                            v == null -> InputValidationResult.Error("Row value must be an integer")
                            v <= 0 || v > 999 -> InputValidationResult.Error("Row value must be in 1..999")
                            else -> InputValidationResult.Valid(listOf(v))
                        }
                    }
                    "POP", "DEQ", "PEEK" -> InputValidationResult.Valid(emptyList())
                    else -> InputValidationResult.Error("Unknown op ${row.kind}")
                }
            }
            if (cleaned.any { it is InputValidationResult.Error }) {
                cleaned.first { it is InputValidationResult.Error } as InputValidationResult.Error
            } else {
                InputValidationResult.Valid(emptyList())
            }
        }
    }
    val isValid = validation is InputValidationResult.Valid

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

            // ── Scrollable Body Area (overflow scroll when operations increase) ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── 1. Operation presets LazyRow ──
                Text(
                    text = "OPERATION PRESETS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize,
                    letterSpacing = AlgoType.trackSection,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(presets) { (label, rawRows) ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                rows = rawRows.map { spec ->
                                    val (k, v) = if (spec.contains(":")) {
                                        val parts = spec.split(":")
                                        parts[0] to parts[1]
                                    } else spec to ""
                                    OpRowData(k, v)
                                }
                            },
                            label = {
                                Text(label, style = MaterialTheme.typography.labelSmall, fontSize = AlgoType.microSize)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CanvasBackground,
                                labelColor = TextSecondary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = BorderSubtle,
                                borderWidth = 1.dp
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // ── 2. Operation rows ──
                Text(
                    text = "OPERATIONS (${rows.size} / $MAX_ROWS):",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = AlgoType.microSize,
                    letterSpacing = AlgoType.trackSection,
                    fontWeight = FontWeight.Bold
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rows.forEachIndexed { idx, row ->
                        OpRow(
                            row = row,
                            kinds = kinds,
                            accent = accent,
                            valueAccent = valueAccent,
                            onChange = { newRow -> rows = rows.toMutableList().also { it[idx] = newRow } },
                            onDelete = { rows = rows.toMutableList().also { it.removeAt(idx) } },
                        )
                    }
                }

                // ── 3. Add row button ──
                if (rows.size < MAX_ROWS) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CanvasBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .clickable {
                                val defaultKind = kinds.first()  // "PUSH" or "ENQ"
                                rows = rows + OpRowData(defaultKind, "")
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Plus,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Add operation",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // ── 4. Reset button ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        .clickable { rows = initialRows }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Reset,
                            contentDescription = null,
                            tint = AccentYellow,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Reset to default",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // ── 5. Validation feedback ──
                val res = validation
                if (res is InputValidationResult.Error) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Alert,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = res.message,
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentRed,
                            fontSize = AlgoType.microSize
                        )
                    }
                } else if (isValid) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Ready: ${rows.size} operations",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen,
                            fontSize = AlgoType.microSize
                        )
                    }
                }
            }

            // ── 6. Apply Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isValid) PrimaryCyan else PrimaryCyan.copy(alpha = 0.35f))
                    .clickable(enabled = isValid) {
                        onApply(rows)
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
 * Single editable row in the buffer customize sheet. The left FilterChip
 * chooses the op kind. The middle BasicTextField is only enabled for
 * ops that take a value. The right "X" deletes the row.
 */
@Composable
private fun OpRow(
    row: OpRowData,
    kinds: List<String>,
    accent: Color,
    valueAccent: Color,
    onChange: (OpRowData) -> Unit,
    onDelete: () -> Unit,
) {
    val valueEnabled = row.kind in setOf("PUSH", "ENQ")
    val valueBorderColor = when {
        valueEnabled && row.valueText.isNotBlank() && row.valueText.toIntOrNull() == null -> AccentRed.copy(alpha = 0.8f)
        valueEnabled && row.valueText.isNotBlank() -> BorderCyan
        else -> BorderSubtle
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Kind chip row
        LazyRow(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(kinds) { kind ->
                FilterChip(
                    selected = row.kind == kind,
                    onClick = { onChange(row.copy(kind = kind, valueText = if (kind == row.kind) row.valueText else "")) },
                    label = {
                        Text(
                            kind,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = AlgoType.microSize,
                            fontWeight = if (row.kind == kind) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CanvasBackground,
                        selectedContainerColor = accent.copy(alpha = 0.18f),
                        labelColor = TextSecondary,
                        selectedLabelColor = accent,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = row.kind == kind,
                        borderColor = BorderSubtle,
                        selectedBorderColor = accent,
                        borderWidth = if (row.kind == kind) 1.5.dp else 1.dp
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
            }
        }
        // Value field (only enabled for PUSH/ENQ)
        Box(
            modifier = Modifier
                .weight(1.2f)
                .clip(RoundedCornerShape(6.dp))
                .background(CanvasBackground)
                .border(1.dp, valueBorderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            if (valueEnabled) {
                BasicTextField(
                    value = row.valueText,
                    onValueChange = { onChange(row.copy(valueText = it)) },
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = if (row.valueText.isBlank()) TextMuted else valueAccent,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = AlgoType.labelSize
                    ),
                    cursorBrush = SolidColor(PrimaryCyan),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = AlgoType.labelSize
                )
            }
        }
        // Delete button
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CanvasBackground)
                .border(1.dp, BorderSubtle, CircleShape)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AlgoGlyphs.Close,
                contentDescription = "Delete",
                tint = AccentRed,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeBufferSheetStackPreview() {
    AlgoLensTheme {
        CustomizeBufferSheet(
            initialOps = listOf(BufferOp.Push(10), BufferOp.Push(25), BufferOp.Pop),
            onApply = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CustomizeQueueSheetPreview() {
    AlgoLensTheme {
        CustomizeQueueSheet(
            initialOps = emptyList(),
            onApply = {},
            onDismiss = {}
        )
    }
}
