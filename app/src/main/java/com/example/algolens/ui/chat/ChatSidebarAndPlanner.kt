package com.example.algolens.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ChatConversation
import com.example.algolens.model.chat.ProjectConstraintBrief
import com.example.algolens.model.chat.ProjectRecommendationPayload
import com.example.algolens.model.chat.RecommendedAlgorithmCandidate
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern AVIA Slide-over Conversation Sidebar.
 * Designed with Profile Option Item styling:
 * - RoundedCornerShape(AlgoTokens.radiusSm)
 * - heightIn(min = 44.dp)
 * - Clear spacing and divider
 * - Useful functional buttons: New chat, Search, Pinned filter toggle, Browse catalog
 * - Recents list with pin indicators and inline actions
 */
@Composable
fun ChatHistorySidebar(
    conversations: List<ChatConversation>,
    activeConversationId: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectConversation: (String) -> Unit,
    onNewChat: () -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearAllHistory: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    filterPinnedOnly: Boolean = false,
    onToggleFilterPinnedOnly: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onOpenPlanner: () -> Unit = {},
    onQuickPrompt: (String) -> Unit = {}
) {
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var confirmClearAll by remember { mutableStateOf(false) }
    var renamingConversationId by remember { mutableStateOf<String?>(null) }
    var renameBuffer by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.72f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .background(CanvasBackground)
                .border(AlgoTokens.strokeThin, BorderSubtle)
                .clickable(enabled = false) { }
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            // ── 1. Header ("AVIA CHATS" + Close "✕") ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space1, vertical = AlgoTokens.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(PrimaryCyan)
                    )
                    Text(
                        text = "AVIA CHATS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CardBackgroundElevated)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Close,
                        contentDescription = "Close Sidebar",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // ── 2. Search Conversations Field ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CardBackgroundElevated)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Search,
                        contentDescription = "Search chats",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search conversations...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = TextPrimary),
                            cursorBrush = SolidColor(PrimaryCyan),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .clickable { onSearchQueryChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // ── 3. Functional Buttons (ProfileOptionItem Style) ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                SidebarOptionItem(
                    icon = AlgoGlyphs.EditPencil,
                    title = "New chat",
                    onClick = {
                        onNewChat()
                        onDismiss()
                    }
                )

                SidebarOptionItem(
                    icon = AlgoGlyphs.Pin,
                    title = "Pinned chats",
                    trailingText = if (filterPinnedOnly) "Active" else null,
                    isActive = filterPinnedOnly,
                    onClick = onToggleFilterPinnedOnly
                )

                SidebarOptionItem(
                    icon = AlgoGlyphs.Code,
                    title = "Browse catalog",
                    trailingIcon = AlgoGlyphs.ChevronRight,
                    onClick = {
                        onNavigateToCatalog()
                        onDismiss()
                    }
                )
            }

            // ── 4. Elegant Section Divider ──
            HorizontalDivider(
                modifier = Modifier.padding(vertical = AlgoTokens.space1),
                color = BorderSubtle,
                thickness = 0.5.dp
            )

            // ── 5. Confirmation Banner for Delete or Clear All ──
            if (pendingDeleteId != null || confirmClearAll) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(RedSubtle)
                        .border(AlgoTokens.strokeThin, AccentRed, RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(AlgoTokens.space3)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                        Text(
                            text = if (confirmClearAll) {
                                "Clear all saved conversations? This cannot be undone."
                            } else {
                                "Delete this conversation permanently?"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(AccentRed)
                                    .clickable {
                                        if (confirmClearAll) {
                                            onClearAllHistory()
                                            confirmClearAll = false
                                        } else {
                                            pendingDeleteId?.let(onDeleteConversation)
                                            pendingDeleteId = null
                                        }
                                    }
                                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                            ) {
                                Text(
                                    text = "Confirm Delete",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                    .background(CardBackground)
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                                    .clickable {
                                        pendingDeleteId = null
                                        confirmClearAll = false
                                    }
                                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // ── 6. "Recents" Section Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (filterPinnedOnly) "PINNED CONVERSATIONS" else "RECENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection
                )

                if (conversations.isNotEmpty()) {
                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { confirmClearAll = true }
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                    )
                }
            }

            // ── 7. Recents Conversations Stream ──
            if (conversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = AlgoTokens.space6),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = if (filterPinnedOnly) "No pinned chats" else "No recent conversations",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    items(conversations, key = { it.id }) { conv ->
                        val isActive = conv.id == activeConversationId
                        val isRenaming = renamingConversationId == conv.id

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(if (isActive) CyanSubtle else Color.Transparent)
                                .border(
                                    AlgoTokens.strokeThin,
                                    if (isActive) BorderCyan.copy(alpha = 0.35f) else Color.Transparent,
                                    RoundedCornerShape(AlgoTokens.radiusSm)
                                )
                                .clickable {
                                    onSelectConversation(conv.id)
                                    onDismiss()
                                }
                                .padding(horizontal = AlgoTokens.space3, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isRenaming) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                                ) {
                                    BasicTextField(
                                        value = renameBuffer,
                                        onValueChange = { renameBuffer = it },
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        cursorBrush = SolidColor(PrimaryCyan),
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(CardBackground, RoundedCornerShape(AlgoTokens.radiusXs))
                                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(AlgoTokens.iconButtonSm)
                                            .clip(CircleShape)
                                            .clickable {
                                                onRenameConversation(conv.id, renameBuffer)
                                                renamingConversationId = null
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = AlgoGlyphs.Check,
                                            contentDescription = "Save Thread Name",
                                            tint = AccentGreen,
                                            modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (conv.isPinned) {
                                        Icon(
                                            imageVector = AlgoGlyphs.Pin,
                                            contentDescription = "Pinned",
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = conv.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isActive) PrimaryCyan else TextPrimary,
                                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                            ) {
                                if (!isRenaming) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                            .clickable {
                                                renamingConversationId = conv.id
                                                renameBuffer = conv.title
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = AlgoGlyphs.EditPencil,
                                            contentDescription = "Rename Conversation",
                                            tint = TextDark,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                        .clickable {
                                            pendingDeleteId = conv.id
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = AlgoGlyphs.Trash,
                                        contentDescription = "Delete Conversation",
                                        tint = TextDark,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean, modern sidebar option row matching ProfileOptionItem design.
 */
@Composable
private fun SidebarOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    trailingIcon: ImageVector? = null,
    isActive: Boolean = false,
    isDestructive: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(if (isActive) CyanSubtle else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = AlgoTokens.space3, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = when {
                isDestructive -> AccentRed
                isActive -> PrimaryCyan
                else -> TextSecondary
            }
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isDestructive -> AccentRed
                isActive -> PrimaryCyan
                else -> TextPrimary
            },
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = AlgoType.labelSize
            )
        }
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isDestructive) AccentRed.copy(alpha = 0.6f) else TextDark
            )
        }
    }
}

/**
 * Interactive Project Algorithm Planner Sheet ("Plan an algorithm for my project").
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectPlannerSheet(
    onSubmit: (ProjectConstraintBrief) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var projectGoal by remember { mutableStateOf("") }
    var dataScale by remember { mutableStateOf(ProjectConstraintBrief.DataScale.MEDIUM_1K) }
    var inputOrder by remember { mutableStateOf(ProjectConstraintBrief.InputOrder.RANDOM_UNSORTED) }
    var graphGoal by remember { mutableStateOf(ProjectConstraintBrief.GraphGoal.NONE) }
    var requiresStability by remember { mutableStateOf(false) }
    var strictMemoryInPlace by remember { mutableStateOf(false) }
    var frequentUpdates by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.76f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        DoubleBezelShell(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) { },
            contentPadding = PaddingValues(AlgoTokens.space5)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                        Text(
                            text = "PROJECT ALGORITHM PLANNER",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grounded against AlgoLens's 14-algorithm repository",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                // 1. Project idea input
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                    Text(
                        text = "Describe your project or problem",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.45f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(AlgoTokens.space3)
                    ) {
                        if (projectGoal.isEmpty()) {
                            Text(
                                text = "e.g., Leaderboard sorting for 50k players, or dependency cycle check...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDark
                            )
                        }
                        BasicTextField(
                            value = projectGoal,
                            onValueChange = { projectGoal = it },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                            cursorBrush = SolidColor(PrimaryCyan),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 2. Dataset scale
                PlannerChipGroup<ProjectConstraintBrief.DataScale>(
                    label = "Dataset Scale",
                    options = ProjectConstraintBrief.DataScale.entries,
                    selected = dataScale,
                    optionLabel = { it.label },
                    onSelect = { dataScale = it }
                )

                // 3. Input order / structure shape
                PlannerChipGroup<ProjectConstraintBrief.InputOrder>(
                    label = "Input Order & Domain",
                    options = ProjectConstraintBrief.InputOrder.entries,
                    selected = inputOrder,
                    optionLabel = { it.label },
                    onSelect = { inputOrder = it }
                )

                // 4. Graph goal
                PlannerChipGroup<ProjectConstraintBrief.GraphGoal>(
                    label = "Graph / Routing Goal",
                    options = ProjectConstraintBrief.GraphGoal.entries,
                    selected = graphGoal,
                    optionLabel = { it.label },
                    onSelect = { graphGoal = it }
                )

                // 5. Boolean Constraint Toggles
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                    Text(
                        text = "Engineering Constraints",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        ToggleConstraintChip(
                            label = "Stable Order Required",
                            checked = requiresStability,
                            onToggle = { requiresStability = !requiresStability }
                        )
                        ToggleConstraintChip(
                            label = "In-Place O(1) Memory",
                            checked = strictMemoryInPlace,
                            onToggle = { strictMemoryInPlace = !strictMemoryInPlace }
                        )
                        ToggleConstraintChip(
                            label = "Frequent Live Updates",
                            checked = frequentUpdates,
                            onToggle = { frequentUpdates = !frequentUpdates }
                        )
                    }
                }

                // Submit action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(PrimaryCyan)
                        .clickable {
                            onSubmit(
                                ProjectConstraintBrief(
                                    projectGoal = projectGoal.trim(),
                                    dataScale = dataScale,
                                    inputOrder = inputOrder,
                                    requiresStability = requiresStability,
                                    strictMemoryInPlace = strictMemoryInPlace,
                                    frequentUpdates = frequentUpdates,
                                    graphGoal = graphGoal
                                )
                            )
                        }
                        .padding(vertical = AlgoTokens.space3),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Generate Grounded Recommendation",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleConstraintChip(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(if (checked) CyanSubtle else CardBackgroundElevated)
            .border(
                AlgoTokens.strokeThin,
                if (checked) BorderCyan else BorderSubtle,
                RoundedCornerShape(AlgoTokens.radiusXs)
            )
            .clickable { onToggle() }
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
    ) {
        Text(
            text = if (checked) "✓ $label" else label,
            style = MaterialTheme.typography.bodySmall,
            color = if (checked) PrimaryCyan else TextSecondary,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> PlannerChipGroup(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(if (isSelected) CyanSubtle else CardBackgroundElevated)
                        .border(
                            AlgoTokens.strokeThin,
                            if (isSelected) BorderCyan else BorderSubtle,
                            RoundedCornerShape(AlgoTokens.radiusXs)
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                ) {
                    Text(
                        text = optionLabel(option),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) PrimaryCyan else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Renders structured `ProjectRecommendationPayload` inside `AssistantMessageBubble`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectRecommendationBlock(
    payload: ProjectRecommendationPayload,
    onLaunchVisualizer: (AlgorithmId) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        // Explicit out-of-catalog warning when the user's project requires an external algorithm
        payload.outOfCatalogNotice?.let { notice ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(YellowSubtle)
                    .border(AlgoTokens.strokeThin, AccentYellow, RoundedCornerShape(AlgoTokens.radiusSm))
                    .padding(AlgoTokens.space3)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                    Text(
                        text = "CATALOG SCOPE NOTICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentYellow,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = notice,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }

        payload.candidates.forEachIndexed { index, candidate ->
            CandidateCard(
                rank = index + 1,
                candidate = candidate,
                onLaunchVisualizer = { onLaunchVisualizer(candidate.algorithmId) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CandidateCard(
    rank: Int,
    candidate: RecommendedAlgorithmCandidate,
    onLaunchVisualizer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(
                AlgoTokens.strokeThin,
                if (rank == 1) BorderCyan else BorderSubtle,
                RoundedCornerShape(AlgoTokens.radiusSm)
            )
            .padding(AlgoTokens.space4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (rank == 1) PrimaryCyan else PurpleSubtle)
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                    ) {
                        Text(
                            text = "#$rank",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (rank == 1) TextNavy else PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.microSize
                        )
                    }
                    Text(
                        text = candidate.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (candidate.isStable) "Stable" else if (candidate.isInPlace) "In-Place" else "Catalog Verified",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            // Complexity badges (tabular numerals)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                ComplexityPill(label = "Avg", value = candidate.averageCase, tint = AccentGreen)
                ComplexityPill(label = "Worst", value = candidate.worstCase, tint = AccentOrange)
                ComplexityPill(label = "Space", value = candidate.spaceComplexity, tint = PrimaryCyan)
            }

            Text(
                text = candidate.fitReason,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontSize = AlgoType.labelSize,
                lineHeight = AlgoType.leadingLabel
            )

            Text(
                text = "Trade-off: ${candidate.tradeOffs}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = AlgoType.labelSize,
                lineHeight = AlgoType.leadingLabel
            )

            // One-tap Open in Visualizer action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(CyanSubtle)
                    .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusXs))
                    .clickable { onLaunchVisualizer() }
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open in Visualizer → ${candidate.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = AlgoGlyphs.ChevronRight,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComplexityPill(
    label: String,
    value: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CardBackground)
            .border(AlgoTokens.strokeHairline, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
    }
}

private fun formatConversationTimestamp(epochMs: Long): String {
    return try {
        val formatter = SimpleDateFormat("MMM d, HH:mm", Locale.US)
        formatter.format(Date(epochMs))
    } catch (_: Exception) {
        "Saved"
    }
}
