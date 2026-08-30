package com.example.algolens.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmSpec
import com.example.algolens.model.VisualizerFamily
import com.example.algolens.ui.components.IconPillButton
import com.example.algolens.ui.components.RailIconButton
import com.example.algolens.ui.components.SegmentedToggle
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.smoothPanelExpansion

/**
 * Compact workspace header. Three responsibilities:
 *  1. **Back / title / step counter** on the left.
 *  2. **Action cluster** on the right: TIME / SPACE complexity
 *     readouts (primary info, inline), guided-tour help, and a
 *     kebab that opens a popover holding the theory sheet, the
 *     variable inspector and memory call stack toggles, and the
 *     customize input button.
 *  3. **Smooth resizing** when the user toggles things (e.g. expands
 *     the mode chips row), using [smoothPanelExpansion] so the
 *     workspace never snaps jarringly.
 *
 * The header is *pure* with respect to the data model — it only
 * reads from the hoisted [VisualizerScreenState] and invokes its
 * mutators. The popover's open/closed state and the inspector /
 * call-stack toggles are the only local UI state, owned here via
 * [rememberSaveable] so they survive configuration changes.
 *
 * Layout decision: TIME and SPACE are primary information (the
 * "is this algorithm fast?" glance), so they live inline. THEORY
 * is a sub-action (the user *visits* it, not *reads* it), so it
 * lives in the kebab popover. The variable inspector + memory
 * call stack are readouts that can clutter the workspace when
 * the user doesn't need them; the kebab popover holds their
 * toggles, and the readout content expands inline below the
 * toggle row when on. The guided tour (`?`) stays in the header
 * as a first-run discoverability anchor.
 */
@Composable
fun VisualizerHeader(
    algorithm: Algorithm,
    spec: AlgorithmSpec?,
    state: VisualizerScreenState,
    currentStep: VisualizerStep,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    var inspectorOpen by rememberSaveable { mutableStateOf(false) }
    var callStackOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = AlgoTokens.space3)
            .smoothPanelExpansion(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderTitle(algorithm, state.currentStepIdx, state.totalSteps, onBack)
            HeaderActions(
                state = state,
                algorithm = algorithm,
                spec = spec,
                currentStep = currentStep,
                menuOpen = menuOpen,
                onMenuOpenChange = { menuOpen = it },
                inspectorOpen = inspectorOpen,
                onInspectorOpenChange = { inspectorOpen = it },
                callStackOpen = callStackOpen,
                onCallStackOpenChange = { callStackOpen = it }
            )
        }

        // Mode chips row — only shown for LINEAR_1D algorithms.
        if (spec?.id?.family == VisualizerFamily.LINEAR_1D) {
            HeaderModeRow(state)
        }
    }
}

@Composable
private fun HeaderTitle(
    algorithm: Algorithm,
    currentStepIdx: Int,
    totalSteps: Int,
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        RailIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            boxSize = AlgoTokens.iconButtonSm - 2.dp,
            iconSize = AlgoTokens.inlineIconMd,
            tint = TextSecondary,
            container = CardBackground,
            borderColor = BorderSubtle,
            onClick = onBack
        )

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
}

/**
 * The right-side cluster: TIME / SPACE complexity readouts, the
 * guided-tour help, and the kebab trigger. All chips are
 * uniform-height so the cluster never stretches.
 *
 * The kebab is wrapped in a Box so the [DropdownMenu] in
 * [HeaderOverflowMenuHost] can anchor to it. Material 3's
 * `DropdownMenu` is a popup; it positions itself relative to its
 * nearest `Box` ancestor in the Compose tree.
 */
@Composable
private fun HeaderActions(
    state: VisualizerScreenState,
    algorithm: Algorithm,
    spec: AlgorithmSpec?,
    currentStep: VisualizerStep,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    inspectorOpen: Boolean,
    onInspectorOpenChange: (Boolean) -> Unit,
    callStackOpen: Boolean,
    onCallStackOpenChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconPillButton(
            label = "TIME ${algorithm.timeComplexity}",
            accent = PrimaryCyan,
            accentContainer = CyanSubtle,
            borderColor = PrimaryCyan.copy(alpha = 0.3f),
            onClick = { state.showTheorySheet = true }
        )

        IconPillButton(
            label = "SPACE ${algorithm.spaceComplexity}",
            accent = PurpleGlow,
            accentContainer = PurpleSubtle,
            borderColor = SecondaryPurple.copy(alpha = 0.3f),
            onClick = { state.showTheorySheet = true }
        )

        RailIconButton(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = "Guided Tour",
            boxSize = AlgoTokens.iconButtonSm - 4.dp,
            iconSize = AlgoTokens.inlineIconMd,
            tint = PrimaryCyan,
            container = CardBackground,
            borderColor = BorderSubtle,
            onClick = { state.showGuidedTour = true }
        )

        Box {
            // Bare icon button: no container fill, no border — the
            // kebab reads as a plain overflow glyph.
            RailIconButton(
                icon = Icons.Default.MoreVert,
                contentDescription = "More actions",
                boxSize = AlgoTokens.iconButtonSm - 4.dp,
                iconSize = AlgoTokens.inlineIconMd,
                tint = TextSecondary,
                container = Color.Transparent,
                borderColor = Color.Transparent,
                onClick = { onMenuOpenChange(!menuOpen) }
            )
            HeaderOverflowMenuHost(
                algorithm = algorithm,
                spec = spec,
                state = state,
                currentStep = currentStep,
                menuOpen = menuOpen,
                onMenuOpenChange = onMenuOpenChange,
                inspectorOpen = inspectorOpen,
                onInspectorOpenChange = onInspectorOpenChange,
                callStackOpen = callStackOpen,
                onCallStackOpenChange = onCallStackOpenChange
            )
        }
    }
}

/**
 * Kebab popover. Standard Material 3 dropdown: one [DropdownMenuItem]
 * row per action (leading icon + label, optional trailing chevron /
 * summary), separated by [HorizontalDivider]s. The inspector / call
 * stack rows are disclosure rows — tapping toggles their readout,
 * which expands inline below the row.
 */
@Composable
private fun HeaderOverflowMenuHost(
    algorithm: Algorithm,
    spec: AlgorithmSpec?,
    state: VisualizerScreenState,
    currentStep: VisualizerStep,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    inspectorOpen: Boolean,
    onInspectorOpenChange: (Boolean) -> Unit,
    callStackOpen: Boolean,
    onCallStackOpenChange: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = menuOpen,
        onDismissRequest = { onMenuOpenChange(false) },
        modifier = Modifier.background(CardBackground)
    ) {
        // THEORY — opens the theory sheet, dismisses the popover.
        DropdownMenuItem(
            text = { Text("Theory Sheet", color = TextPrimary, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = PurpleGlow,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = {
                onMenuOpenChange(false)
                state.showTheorySheet = true
            }
        )

        HorizontalDivider()

        // VARIABLE INSPECTOR — disclosure row.
        DropdownMenuItem(
            text = {
                Column {
                    Text("Variable Inspector", color = TextPrimary, fontSize = 13.sp)
                    Text(
                        text = if (currentStep.variables.isNotEmpty())
                            "${currentStep.variables.size} live variables"
                        else "pointers",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Code,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = if (inspectorOpen) "Collapse" else "Expand",
                    tint = TextMuted,
                    modifier = Modifier.graphicsLayer { rotationZ = if (inspectorOpen) 180f else 0f }
                )
            },
            onClick = { onInspectorOpenChange(!inspectorOpen) }
        )
        if (inspectorOpen) {
            VariableInspectorReadout(step = currentStep)
        }

        HorizontalDivider()

        // MEMORY CALL STACK — disclosure row.
        DropdownMenuItem(
            text = {
                Column {
                    Text("Memory Call Stack", color = TextPrimary, fontSize = 13.sp)
                    Text(
                        "depth ${currentStep.recursionDepth + 1}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Terminal,
                    contentDescription = null,
                    tint = PurpleGlow,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = if (callStackOpen) "Collapse" else "Expand",
                    tint = TextMuted,
                    modifier = Modifier.graphicsLayer { rotationZ = if (callStackOpen) 180f else 0f }
                )
            },
            onClick = { onCallStackOpenChange(!callStackOpen) }
        )
        if (callStackOpen) {
            MemoryCallStackReadout(
                algorithmName = algorithm.name,
                step = currentStep
            )
        }

        // CUSTOMIZE — only for algorithms that support custom input.
        // The label is family-aware so the user sees "Customize Stack" /
        // "Customize Queue" / "Customize BST" / "Customize Traversal"
        // instead of the generic "Customize Array". The screen-level
        // dispatcher in `VisualizerScreen.kt` reads `spec.id.family` to
        // decide which sheet (`CustomizeInputSheet` / `CustomizeBufferSheet`
        // / `CustomizeGraphSheet`) to render.
        if (spec?.supportsCustomInput == true) {
            HorizontalDivider()
            val customizeLabel = when (spec.id) {
                com.example.algolens.model.AlgorithmId.STACK -> "Customize Stack"
                com.example.algolens.model.AlgorithmId.QUEUE -> "Customize Queue"
                com.example.algolens.model.AlgorithmId.HEAP -> "Customize Heap"
                com.example.algolens.model.AlgorithmId.BINARY_SEARCH_TREE -> "Customize BST"
                com.example.algolens.model.AlgorithmId.BFS,
                com.example.algolens.model.AlgorithmId.DFS -> "Customize Traversal"
                else -> "Customize Array"
            }
            DropdownMenuItem(
                text = { Text(customizeLabel, color = TextPrimary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    onMenuOpenChange(false)
                    state.showInputSheet = true
                }
            )
        }
    }
}

/**
 * A disclosure row body: the readout that expands inline below the
 * "Variable Inspector" / "Memory Call Stack" dropdown rows.
 */
@Composable
private fun ColumnScope.VariableInspectorReadout(step: VisualizerStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (step.variables.isNotEmpty()) {
            step.variables.forEach { (k, v) ->
                VarBadge(label = k, value = v)
            }
        } else {
            step.bottomPointers.forEach { (label, index) ->
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            step.topPointers.forEach { (label, index) ->
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            if (step.bottomPointers.isEmpty() && step.topPointers.isEmpty()) {
                VarBadge(label = "step", value = "${step.stepIndex + 1}")
            }
        }
    }
}

@Composable
private fun ColumnScope.MemoryCallStackReadout(
    algorithmName: String,
    step: VisualizerStep
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        Text(
            text = "$algorithmName(size=${step.array.size})",
            style = MaterialTheme.typography.labelSmall,
            color = PurpleGlow,
            fontWeight = FontWeight.Bold,
            fontSize = 8.5.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StackInfoBadge(label = "DEPTH", value = "${step.recursionDepth + 1}")
            StackInfoBadge(label = "MODE", value = step.renderMode.name.lowercase())
            StackInfoBadge(
                label = "LINE",
                value = if (step.activeCodeLines.isEmpty()) "—" else step.activeCodeLines.joinToString(",")
            )
        }
    }
}

@Composable
private fun VarBadge(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CyanSubtle)
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
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

@Composable
private fun StackInfoBadge(label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(PurpleSubtle)
            .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = PurpleGlow,
            fontWeight = FontWeight.SemiBold,
            fontSize = 7.5.sp
        )
    }
}

@Composable
private fun HeaderModeRow(state: VisualizerScreenState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cell size preset: S (0.7x), M (1x), L (1.25x) of the
        // responsive default. Keeps 7-8+ element arrays from
        // clipping on small screens.
        SegmentedToggle(
            options = listOf(
                "S" to 0.7f,
                "M" to 1f,
                "L" to 1.25f
            ),
            selectedKey = state.cellScale,
            onContainer = DarkBackground,
            onSelect = { key -> state.applyCellScale(key as Float) }
        )
        SegmentedToggle(
            options = listOf(
                "Box / Trace Mode" to ArrayViewMode.CELLS,
                "Bar Chart Mode" to ArrayViewMode.BARS
            ),
            selectedKey = state.arrayViewMode,
            onContainer = DarkBackground,
            onSelect = { key -> state.arrayViewMode = key as ArrayViewMode }
        )
    }
}
