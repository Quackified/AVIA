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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
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
            .padding(horizontal = AlgoTokens.space5 + AlgoTokens.space1, vertical = AlgoTokens.space3)
            .smoothPanelExpansion(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
            verticalAlignment = Alignment.Top
        ) {
            // Title takes the available space and truncates with "..."
            // when it would clip into the action cluster. Actions are
            // pinned to the right (no second-row wrap, per user
            // instructions).
            HeaderTitle(
                algorithm = algorithm,
                currentStepIdx = state.currentStepIdx,
                totalSteps = state.totalSteps,
                modifier = Modifier.weight(1f),
                onBack = onBack
            )
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
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    // Static per-algorithm values — cache so they're not recomputed every
    // recomposition during playback (algorithm id / name / complexity never
    // change between steps; only `currentStepIdx` varies).
    val algoNameUpper = remember(algorithm.id) {
        algorithm.name.uppercase()
    }
    // Read-then-remember: theme bases are read in @Composable scope, then
    // the .copy(...) merge is cached so playback recompositions reuse a
    // stable TextStyle instead of allocating one per step.
    val titleBase = MaterialTheme.typography.titleMedium
    val counterBase = MaterialTheme.typography.bodySmall
    val headerTitleStyle = remember(titleBase) {
        titleBase.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
    val stepCounterStyle = remember(counterBase) {
        counterBase.copy(
            color = TextMuted,
            fontSize = 8.5.sp,
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        // Back button: icon / container / border are fully static — cache
        // the static icon + size parameters once so the wrapper does not
        // rebuild the size/tint objects every recomposition.
        val backIcon by remember {
            derivedStateOf {
                Icons.AutoMirrored.Filled.ArrowBack
            }
        }
        val backButtonSize by remember {
            derivedStateOf {
                AlgoTokens.iconButtonSm - 2.dp
            }
        }
        val backIconSize by remember {
            derivedStateOf {
                AlgoTokens.inlineIconMd
            }
        }

        RailIconButton(
            icon = backIcon,
            contentDescription = "Back",
            boxSize = backButtonSize,
            iconSize = backIconSize,
            tint = TextSecondary,
            container = CardBackground,
            borderColor = BorderSubtle,
            onClick = onBack
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = algoNameUpper,
                style = headerTitleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Step ${currentStepIdx + 1} of $totalSteps",
                style = stepCounterStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    // Static per-algorithm text values used in the action cluster and
    // dropdown — cache once per algorithm so they're not re-built every
    // recomposition during playback (algorithm id / timeComplexity /
    // spaceComplexity never change between steps).
    val timeLabel = remember(algorithm.id) {
        "TIME ${algorithm.timeComplexity}"
    }
    val spaceLabel = remember(algorithm.id) {
        "SPACE ${algorithm.spaceComplexity}"
    }

    // Helper: materialise the TIME / SPACE complexity strings from the
    // algorithm reference (used by HeaderOverflowMenuHost below where we do
    // not yet have the cached labels available because that composable's
    // signature is owned by a different branch and we keep the change minimal).

    Row(
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconPillButton(
            label = timeLabel,
            accent = PrimaryCyan,
            accentContainer = CyanSubtle,
            borderColor = PrimaryCyan.copy(alpha = 0.3f),
            onClick = { state.showTheorySheet = true }
        )

        IconPillButton(
            label = spaceLabel,
            accent = PurpleGlow,
            accentContainer = PurpleSubtle,
            borderColor = SecondaryPurple.copy(alpha = 0.3f),
            onClick = { state.showTheorySheet = true }
        )

        // Guided-tour icon button: fully static icon params — cache the
        // icon + sizes once so the wrapper does not rebuild them every step.
        val helpIcon by remember {
            derivedStateOf { Icons.AutoMirrored.Filled.HelpOutline }
        }
        val helpButtonSize by remember {
            derivedStateOf { AlgoTokens.iconButtonSm - 4.dp }
        }
        val helpIconSize by remember {
            derivedStateOf { AlgoTokens.inlineIconMd }
        }

        RailIconButton(
            icon = helpIcon,
            contentDescription = "Guided Tour",
            boxSize = helpButtonSize,
            iconSize = helpIconSize,
            tint = PrimaryCyan,
            container = CardBackground,
            borderColor = BorderSubtle,
            onClick = { state.showGuidedTour = true }
        )

        Box {
            // Kebab icon button: icon / sizes are fully static — cache once.
            val kebabIcon by remember {
                derivedStateOf { Icons.Default.MoreVert }
            }
            val kebabButtonSize by remember {
                derivedStateOf { AlgoTokens.iconButtonSm - 4.dp }
            }
            val kebabIconSize by remember {
                derivedStateOf { AlgoTokens.inlineIconMd }
            }

            RailIconButton(
                icon = kebabIcon,
                contentDescription = "More actions",
                boxSize = kebabButtonSize,
                iconSize = kebabIconSize,
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
    // Static per-algorithm cache for the dropdown's static labels and icons.
    // These never change across playback steps for a given algorithm — caching
    // them here avoids rebuilding the text / icon objects every recomposition.
    val algoTitleLabelText = remember { "Theory Sheet" }
    val algoTitleIcon = remember { Icons.AutoMirrored.Filled.MenuBook }
    val algoTitleIconSize = remember { 20.dp }
    val dropdownChevronIcon = remember { Icons.Default.ArrowDropDown }

    val inspectorLabelText = remember { "Variable Inspector" }
    val inspectorLeadingIcon = remember { Icons.Default.Code }
    val inspectorLeadingIconSize = remember { 20.dp }

    val callStackLabelText = remember { "Memory Call Stack" }
    val callStackLeadingIcon = remember { Icons.Default.Terminal }
    val callStackLeadingIconSize = remember { 20.dp }

    val customizeIcon = remember { Icons.Default.Tune }
    val customizeIconSize = remember { 20.dp }
    val customizeLabel = remember(spec?.id) {
        if (spec == null) "Customize Array"
        else when (spec.id) {
            com.example.algolens.model.AlgorithmId.STACK -> "Customize Stack"
            com.example.algolens.model.AlgorithmId.QUEUE -> "Customize Queue"
            com.example.algolens.model.AlgorithmId.HEAP -> "Customize Heap"
            com.example.algolens.model.AlgorithmId.BINARY_SEARCH_TREE -> "Customize BST"
            com.example.algolens.model.AlgorithmId.BFS,
            com.example.algolens.model.AlgorithmId.DFS -> "Customize Traversal"
            else -> "Customize Array"
        }
    }

    DropdownMenu(
        expanded = menuOpen,
        onDismissRequest = { onMenuOpenChange(false) },
        modifier = Modifier.background(CardBackground)
    ) {
        // THEORY — opens the theory sheet, dismisses the popover.
        DropdownMenuItem(
            text = { Text(algoTitleLabelText, color = TextPrimary, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    algoTitleIcon,
                    contentDescription = null,
                    tint = PurpleGlow,
                    modifier = Modifier.size(algoTitleIconSize)
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
                    Text(inspectorLabelText, color = TextPrimary, fontSize = 13.sp)
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
                    inspectorLeadingIcon,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(inspectorLeadingIconSize)
                )
            },
            trailingIcon = {
                Icon(
                    dropdownChevronIcon,
                    contentDescription = if (inspectorOpen) "Collapse" else "Expand",
                    tint = TextMuted,
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = if (inspectorOpen) 180f else 0f
                        }
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
                    Text(callStackLabelText, color = TextPrimary, fontSize = 13.sp)
                    Text(
                        "depth ${currentStep.recursionDepth + 1}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            leadingIcon = {
                Icon(
                    callStackLeadingIcon,
                    contentDescription = null,
                    tint = PurpleGlow,
                    modifier = Modifier.size(callStackLeadingIconSize)
                )
            },
            trailingIcon = {
                Icon(
                    dropdownChevronIcon,
                    contentDescription = if (callStackOpen) "Collapse" else "Expand",
                    tint = TextMuted,
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = if (callStackOpen) 180f else 0f
                        }
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
            DropdownMenuItem(
                text = { Text(customizeLabel, color = TextPrimary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        customizeIcon,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(customizeIconSize)
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
            for ((k, v) in step.variables) {
                VarBadge(label = k, value = v)
            }
        } else {
            for ((label, index) in step.bottomPointers) {
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            for ((label, index) in step.topPointers) {
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
    // Read-then-remember: color/weight/size are static badge chrome, so bake
    // them into one stable TextStyle. Per-step value changes then only
    // remeasure the string — Text's internal style.merge() sees a stable
    // style instead of rebuilding overrides every recomposition.
    val base = MaterialTheme.typography.bodySmall
    val badgeStyle = remember(base) {
        base.copy(
            color = PrimaryCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp
        )
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CyanSubtle)
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
    ) {
        Text(
            text = "$label = $value",
            style = badgeStyle
        )
    }
}

@Composable
private fun StackInfoBadge(label: String, value: String) {
    // Read-then-remember: same static-chrome bake as VarBadge — the live
    // value string stays dynamic, the TextStyle refs stay stable.
    val badgeBase = MaterialTheme.typography.labelSmall
    val labelStyle = remember(badgeBase) {
        badgeBase.copy(
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp
        )
    }
    val valueStyle = remember(badgeBase) {
        badgeBase.copy(
            color = PurpleGlow,
            fontWeight = FontWeight.SemiBold,
            fontSize = 7.5.sp
        )
    }
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
            style = labelStyle
        )
        Text(
            text = value,
            style = valueStyle
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
        // The preset scales the CELLS renderer's `cellWidth` math in
        // `CellArrayVisualizer` so the cells themselves visibly
        // shrink/grow. The BARS renderer uses weight-based bars, so
        // the toggle is a no-op there — the same preset is reused for
        // the BARS renderer's gap spacing so the visual weight tracks
        // across both modes.
        if (state.arrayViewMode != ArrayViewMode.BARS) {
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
        }
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
