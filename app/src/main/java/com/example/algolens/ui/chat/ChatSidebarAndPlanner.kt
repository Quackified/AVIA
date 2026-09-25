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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Gemini App-inspired slide-over Conversation Sidebar featuring:
 * - Minimalist top bar ("AVIA" + borderless close icon)
 * - Full-width pill-shaped "New chat" primary button with leading compose/edit icon
 * - Clean borderless menu rows (Search chats, Algorithm planner, Complexity compare, Code library)
 * - "Workspace" section with vertical indicator bar + "+ New project plan"
 * - "Recents" section with clean single-line threads and subtle right-aligned inline actions
 */
@Composable
fun ChatHistorySidebar(
    conversations: List<ChatConversation>,
    activeConversationId: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectConversation: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenPlanner: () -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearAllHistory: () -> Unit,
    onQuickPrompt: (String) -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
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
                .fillMaxWidth(0.84f)
                .background(CanvasBackground)
                .border(AlgoTokens.strokeThin, BorderSubtle)
                .clickable(enabled = false) { }
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            // ── 1. Top Gemini-style Header ("AVIA" + Close "✕") ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space1, vertical = AlgoTokens.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVIA",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(AlgoTokens.iconButtonSm)
                        .clip(CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Close,
                        contentDescription = "Close Sidebar",
                        tint = TextSecondary,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                }
            }

            // ── 2. Primary "New chat" Pill Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(CardBackgroundElevated)
                    .border(AlgoTokens.strokeThin, BorderSubtle, CircleShape)
                    .clickable { onNewChat() }
                    .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.EditPencil,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                    Text(
                        text = "New chat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ── 3. Clean Borderless Menu List (Gemini App Style) ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                // "Search chats" inline borderless search row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space3),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Search,
                        contentDescription = "Search chats",
                        tint = TextSecondary,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search chats",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                            cursorBrush = SolidColor(PrimaryCyan),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(AlgoTokens.inlineIconMd)
                                .clip(CircleShape)
                                .clickable { onSearchQueryChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                        }
                    }
                }

                GeminiSidebarMenuItem(
                    icon = AlgoGlyphs.Compass,
                    label = "Algorithm planner",
                    onClick = onOpenPlanner
                )

                GeminiSidebarMenuItem(
                    icon = AlgoGlyphs.Spark,
                    label = "Complexity compare",
                    onClick = { onQuickPrompt("Compare Quick Sort and Merge Sort") }
                )

                GeminiSidebarMenuItem(
                    icon = AlgoGlyphs.Code,
                    label = "Code library",
                    onClick = { onQuickPrompt("Show Binary Search implementation in Kotlin") }
                )
            }

            // ── 4. "Workspace" Section (Replicating Gemini's "Notebooks" Section) ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Text(
                    text = "Workspace",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = AlgoTokens.space3)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable { onOpenPlanner() }
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    Box(
                        modifier = Modifier
                            .width(AlgoTokens.space1)
                            .height(AlgoTokens.space6)
                            .background(BorderSubtle, CircleShape)
                    )
                    Icon(
                        imageVector = AlgoGlyphs.Plus,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                    Text(
                        text = "New project plan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }

            // Confirmation Banner for Delete or Clear All
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

            // ── 5. "Recents" Section (Replicating Gemini's "Recents" List) ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space3),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recents",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )

                if (conversations.isNotEmpty()) {
                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .clickable { confirmClearAll = true }
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                    )
                }
            }

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
                            .clip(CircleShape)
                            .background(
                                if (isActive) CardBackgroundElevated else CanvasBackground
                            )
                            .clickable { onSelectConversation(conv.id) }
                            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space3),
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
                            Text(
                                text = conv.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isActive) PrimaryCyan else TextPrimary,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            if (!isRenaming) {
                                Box(
                                    modifier = Modifier
                                        .size(AlgoTokens.inlineIconLg)
                                        .clip(CircleShape)
                                        .clickable {
                                            renamingConversationId = conv.id
                                            renameBuffer = conv.title
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = AlgoGlyphs.EditPencil,
                                        contentDescription = "Rename Conversation",
                                        tint = TextMuted,
                                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(AlgoTokens.inlineIconLg)
                                    .clip(CircleShape)
                                    .clickable {
                                        pendingDeleteId = conv.id
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AlgoGlyphs.Trash,
                                    contentDescription = "Delete Conversation",
                                    tint = TextMuted,
                                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GeminiSidebarMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextSecondary,
            modifier = Modifier.size(AlgoTokens.inlineIconMd)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
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
                            text = "Grounded against AlgoLens's 13-algorithm repository",
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
