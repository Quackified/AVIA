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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.algolens.ui.components.AlgoHairline
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
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * Compact workspace header. Three responsibilities:
 *  1. **Back / title / step counter** on the left.
 *  2. **Action cluster** on the right: TIME / SPACE complexity
 *     readouts (primary info, inline), guided-tour help, and a
 *     kebab that opens a popover holding the theory sheet and the
 *     family-aware customize-input entry.
 *  3. **Smooth resizing** when the user toggles things (e.g. expands
 *     the mode chips row), using [smoothPanelExpansion] so the
 *     workspace never snaps jarringly.
 *
 * The header is *pure* with respect to the data model — it only
 * reads from the hoisted [VisualizerScreenState] and invokes its
 * mutators.
 *
 * Layout decision: TIME and SPACE are primary information (the
 * "is this algorithm fast?" glance), so they live inline. THEORY
 * is a sub-action (the user *visits* it, not *reads* it), so it
 * lives in the kebab popover. The guided tour (`?`) stays in the
 * header as a first-run discoverability anchor.
 *
 * The variable inspector and memory call stack used to be toggled
 * from this header's kebab; they now live on the Focus Deck's State
 * page, which sits next to the transport the user is already holding.
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3)
            .smoothPanelExpansion(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title takes the available space and truncates with "..."
            // when it would clip into the action cluster. Actions are
            // pinned to the right (no second-row wrap, per user
            // instructions).
            HeaderTitle(
                algorithm = algorithm,
                currentStepIdx = state.displayStepIdx,
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
                onMenuOpenChange = { menuOpen = it }
            )
        }

        // Sub-bar: Cell View / Bar Chart on the left | Edit Input on the right
        if (spec?.id?.family == VisualizerFamily.LINEAR_1D || spec?.supportsCustomInput == true) {
            HeaderModeRow(
                state = state,
                spec = spec
            )
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
            letterSpacing = AlgoType.trackHeader,
        )
    }
    val stepCounterStyle = remember(counterBase) {
        counterBase.copy(
            color = TextMuted,
            fontSize = AlgoType.microSize,
            fontFeatureSettings = "tnum"
        )
    }
    val padWidth = totalSteps.toString().length.coerceAtLeast(2)
    val stepNumPadded = (currentStepIdx + 1).toString().padStart(padWidth, '0')
    val totalStepsPadded = totalSteps.toString().padStart(padWidth, '0')

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
                AlgoGlyphs.Back
            }
        }
        val backButtonSize by remember {
            derivedStateOf {
                AlgoTokens.iconButtonSm
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
                text = "Step $stepNumPadded of $totalStepsPadded",
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
    onMenuOpenChange: (Boolean) -> Unit
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
    val isBookmarked = com.example.algolens.data.AppSettings.isBookmarked(algorithm.id.name)
    val showComplexity = com.example.algolens.data.AppSettings.showComplexityBadges

    Row(
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showComplexity) {
            CompactHeaderPill(
                label = timeLabel,
                accent = PrimaryCyan,
                accentContainer = CyanSubtle,
                borderColor = PrimaryCyan.copy(alpha = 0.28f),
                onClick = { state.showTheorySheet = true }
            )

            CompactHeaderPill(
                label = spaceLabel,
                accent = PurpleGlow,
                accentContainer = PurpleSubtle,
                borderColor = SecondaryPurple.copy(alpha = 0.28f),
                onClick = { state.showTheorySheet = true }
            )
        }

        val helpIcon by remember {
            derivedStateOf { AlgoGlyphs.Help }
        }
        val actionButtonSize by remember {
            derivedStateOf { AlgoTokens.iconButtonSm }
        }
        val actionGlyphSize by remember {
            derivedStateOf { AlgoTokens.inlineIconSm }
        }

        RailIconButton(
            icon = if (isBookmarked) AlgoGlyphs.BookmarkFilled else AlgoGlyphs.Bookmark,
            contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Algorithm",
            boxSize = actionButtonSize,
            iconSize = actionGlyphSize,
            tint = if (isBookmarked) PrimaryCyan else TextSecondary,
            container = if (isBookmarked) CyanSubtle else Color.Transparent,
            borderColor = if (isBookmarked) PrimaryCyan.copy(alpha = 0.35f) else BorderSubtle,
            onClick = { com.example.algolens.data.AppSettings.toggleBookmark(algorithm.id.name) }
        )

        RailIconButton(
            icon = helpIcon,
            contentDescription = "Guided Tour",
            boxSize = actionButtonSize,
            iconSize = actionGlyphSize,
            tint = PrimaryCyan,
            container = Color.Transparent,
            borderColor = BorderSubtle,
            onClick = { state.showGuidedTour = true }
        )

        Box(contentAlignment = Alignment.Center) {
            val kebabIcon by remember {
                derivedStateOf { AlgoGlyphs.More }
            }

            RailIconButton(
                icon = kebabIcon,
                contentDescription = "More actions",
                boxSize = actionButtonSize,
                iconSize = actionGlyphSize,
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
                onMenuOpenChange = onMenuOpenChange
            )
        }
    }
}

@Composable
private fun CompactHeaderPill(
    label: String,
    accent: Color,
    accentContainer: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(AlgoTokens.radiusXs)
    Box(
        modifier = Modifier
            .height(20.dp)
            .clip(shape)
            .background(accentContainer)
            .border(AlgoTokens.strokeHairline, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = 7.5.sp,
            lineHeight = 7.5.sp,
            maxLines = 1
        )
    }
}

/**
 * Kebab popover. Standard Material 3 dropdown: one [DropdownMenuItem]
 * row per action (leading icon + label), separated by [AlgoHairline]s.
 *
 * Holds exactly two actions: the theory sheet and the family-aware
 * customize-input entry. It used to also carry "Variable Inspector" and
 * "Memory Call Stack" disclosure rows; those readouts moved to the Focus
 * Deck's State page (Phase 5A), because a menu is the wrong home for the
 * app's only per-step variable surface and the same content was two taps
 * deep behind a popover.
 */
@Composable
private fun HeaderOverflowMenuHost(
    algorithm: Algorithm,
    spec: AlgorithmSpec?,
    state: VisualizerScreenState,
    currentStep: VisualizerStep,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit
) {
    // Static per-algorithm cache for the dropdown's static labels and icons.
    // These never change across playback steps for a given algorithm — caching
    // them here avoids rebuilding the text / icon objects every recomposition.
    val algoTitleLabelText = remember { "Theory Sheet" }
    val algoTitleIcon = remember { AlgoGlyphs.Book }
    val algoTitleIconSize = remember { AlgoTokens.inlineIconLg }
    val dropdownChevronIcon = remember { AlgoGlyphs.ChevronDown }

    val customizeIcon = remember { AlgoGlyphs.Tune }
    val customizeIconSize = remember { AlgoTokens.inlineIconLg }
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
            text = { Text(algoTitleLabelText, color = TextPrimary, fontSize = AlgoType.bodySize) },
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

        AlgoHairline()

        // CUSTOMIZE — only for algorithms that support custom input.
        // The label is family-aware so the user sees "Customize Stack" /
        // "Customize Queue" / "Customize BST" / "Customize Traversal"
        // instead of the generic "Customize Array". The screen-level
        // dispatcher in `VisualizerScreen.kt` reads `spec.id.family` to
        // decide which sheet (`CustomizeInputSheet` / `CustomizeBufferSheet`
        // / `CustomizeGraphSheet`) to render.
        if (spec?.supportsCustomInput == true) {
            AlgoHairline()
            DropdownMenuItem(
                text = { Text(customizeLabel, color = TextPrimary, fontSize = AlgoType.bodySize) },
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

@Composable
private fun HeaderModeRow(
    state: VisualizerScreenState,
    spec: AlgorithmSpec?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AlgoTokens.space1),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Cell View / Bar Chart toggle (for 1D linear arrays) or Tree / Array toggle (for Heap)
        if (spec?.id?.family == VisualizerFamily.LINEAR_1D) {
            SegmentedToggle(
                options = listOf(
                    "Cell View" to ArrayViewMode.CELLS,
                    "Bar Chart" to ArrayViewMode.BARS
                ),
                selectedKey = state.arrayViewMode,
                onContainer = DarkBackground,
                onSelect = { key -> state.arrayViewMode = key as ArrayViewMode }
            )
        } else if (spec?.id == com.example.algolens.model.AlgorithmId.HEAP) {
            SegmentedToggle(
                options = listOf(
                    "Tree View" to ArrayViewMode.CELLS,
                    "Array View" to ArrayViewMode.BARS
                ),
                selectedKey = state.arrayViewMode,
                onContainer = DarkBackground,
                onSelect = { key -> state.arrayViewMode = key as ArrayViewMode }
            )
        } else {
            Spacer(modifier = Modifier.width(AlgoTokens.strokeThin))
        }

        // Right: Edit Input action button
        if (spec?.supportsCustomInput == true) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CyanSubtle)
                    .border(
                        AlgoTokens.strokeThin,
                        PrimaryCyan.copy(alpha = 0.45f),
                        RoundedCornerShape(AlgoTokens.radiusSm)
                    )
                    .clickable { state.showInputSheet = true }
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Sliders,
                    contentDescription = "Edit Input",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                )
                Text(
                    text = "Edit Input",
                    color = PrimaryCyan,
                    fontSize = AlgoType.labelSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

