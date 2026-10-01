package com.avia.ui.chat

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import com.avia.ui.theme.JetBrainsMono
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.avia.data.SampleData
import com.avia.data.TraceLanguage
import com.avia.model.Algorithm
import com.avia.model.chat.ChatAction
import com.avia.model.chat.ChatCodeSnippet
import com.avia.model.chat.ChatComplexitySnapshot
import com.avia.model.chat.ChatMessage
import com.avia.model.chat.ChatPromptStarter
import com.avia.model.chat.ChatSender
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.AlgoHairline
import com.avia.ui.components.CompactIconButton
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentRed
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.AlgoType
import com.avia.ui.theme.BorderCyan
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.RedSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.visualizer.SyntaxHighlighter

/**
 * AVIA Offline Catalog Conversation Screen (`ChatScreen`).
 *
 * Designed around readable proportional conversation flow:
 *  - Compact toolbar with conversation title, honest offline provider status, history trigger,
 *    restrained New Chat button, and overflow menu with confirmed thread reset.
 *  - Empty-state starter prompts that collapse once the conversation begins.
 *  - Clean user/assistant message presentation without repetitive outer bezel chrome around prose,
 *    while keeping code listings, complexity matrices, and project recommendations in structured cards.
 *  - Multiline proportional composer with single-owner IME/navigation-bar inset handling.
 */
@Composable
fun ChatScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    modifier: Modifier = Modifier,
    manager: ChatSessionManager = rememberChatSessionManager(),
    visualizerReturnContext: com.avia.model.VisualizerReturnContext? = null,
    onReturnToVisualizer: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showRenameDialog by remember { mutableStateOf(false) }
    var renameBuffer by remember { mutableStateOf("") }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var userActionTarget by remember { mutableStateOf<ChatMessage?>(null) }
    var toastNotice by remember { mutableStateOf<String?>(null) }

    val hasUserMessages = remember(manager.messages) {
        manager.messages.any { it.sender == ChatSender.USER }
    }

    BackHandler(enabled = manager.isSidebarOpen || manager.showProjectPlanner || showDeleteConfirm || showRenameDialog || showFeedbackDialog || userActionTarget != null) {
        when {
            userActionTarget != null -> userActionTarget = null
            showRenameDialog -> showRenameDialog = false
            showFeedbackDialog -> showFeedbackDialog = false
            showDeleteConfirm -> showDeleteConfirm = false
            manager.showProjectPlanner -> manager.toggleProjectPlanner(false)
            manager.isSidebarOpen -> manager.toggleSidebar(false)
        }
    }

    // Auto-scroll to latest response on message emission or thinking status change
    LaunchedEffect(manager.activeConversationId, manager.messages.size, manager.isThinking) {
        if (manager.messages.isNotEmpty()) {
            listState.animateScrollToItem(manager.messages.size - 1)
        }
    }

    val onShareConversation = {
        val transcript = buildString {
            append("AVIA Conversation: ${manager.activeConversation.title}\n\n")
            manager.messages.forEach { msg ->
                val role = if (msg.sender == ChatSender.USER) "USER" else "AVIA"
                append("[$role]\n${msg.content}\n\n")
            }
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, transcript)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share conversation")
        context.startActivity(shareIntent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            // ── 1. Compact Conversation Toolbar ──
            ChatHeader(
                conversationTitle = manager.activeConversation.title,
                providerLabel = manager.providerLabel,
                isPinned = manager.activeConversation.isPinned,
                onOpenHistory = {
                    focusManager.clearFocus()
                    manager.toggleSidebar(true)
                },
                onNewChat = {
                    manager.createNewChat()
                },
                onShare = onShareConversation,
                onTogglePin = {
                    manager.togglePinConversation()
                    toastNotice = if (manager.activeConversation.isPinned) "Conversation pinned" else "Conversation unpinned"
                },
                onRename = {
                    renameBuffer = manager.activeConversation.title
                    showRenameDialog = true
                },
                onFeedback = {
                    feedbackText = ""
                    showFeedbackDialog = true
                },
                onDelete = {
                    showDeleteConfirm = true
                }
            )

            AlgoHairline()

            // ── Sticky Return to Canvas Banner ──
            if (visualizerReturnContext != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleSubtle)
                        .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.4f))
                        .clickable { onReturnToVisualizer() }
                        .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Spark,
                                contentDescription = null,
                                tint = PurpleGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Return to ${visualizerReturnContext.algorithm.name} (Step ${visualizerReturnContext.stepIndex + 1})",
                                style = MaterialTheme.typography.labelMedium,
                                color = PurpleGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Resume Canvas",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = AlgoGlyphs.ChevronRight,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // ── Temporary Toast/Feedback Notice Banner ──
            AnimatedVisibility(visible = toastNotice != null) {
                toastNotice?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyanSubtle)
                            .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space2),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryCyan
                        )
                        Icon(
                            imageVector = AlgoGlyphs.Close,
                            contentDescription = "Dismiss",
                            tint = PrimaryCyan,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { toastNotice = null }
                        )
                    }
                }
            }

            // ── 2. Conversation Stream ──
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = AlgoTokens.space6,
                    vertical = AlgoTokens.space4
                ),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                items(
                    items = manager.messages,
                    key = { it.id }
                ) { msg ->
                    when (msg.sender) {
                        ChatSender.USER -> UserMessageBubble(
                            message = msg,
                            onHold = { userActionTarget = msg }
                        )
                        ChatSender.ASSISTANT, ChatSender.SYSTEM -> AssistantMessageBubble(
                            message = msg,
                            onAction = { action ->
                                manager.executeAction(action) { algoId ->
                                    val algo = SampleData.algorithms.firstOrNull { it.id == algoId }
                                    if (algo != null) onAlgorithmClick(algo)
                                }
                            },
                            onFollowUp = { prompt ->
                                if (prompt.contains("Plan an algorithm for my project", ignoreCase = true)) {
                                    manager.toggleProjectPlanner(true)
                                } else {
                                    manager.sendMessage(prompt)
                                }
                            }
                        )
                    }
                }

                // Empty-state prompt starters: shown before the first user message, then collapsed
                if (!hasUserMessages) {
                    item(key = "empty_state_starters") {
                        EmptyConversationStarters(
                            starters = manager.starters,
                            onPlanProjectClick = { manager.toggleProjectPlanner(true) },
                            onStarterClick = { manager.sendStarter(it) }
                        )
                    }
                }

                if (manager.isThinking) {
                    item(key = "thinking_indicator") {
                        ThinkingIndicatorBubble()
                    }
                }
            }

            // ── 3. Multiline Proportional Composer Dock ──
            ChatInputDock(
                inputText = manager.inputText,
                onInputChange = { manager.onInputChange(it) },
                onSend = {
                    manager.sendMessage()
                },
                isThinking = manager.isThinking,
                isEditing = manager.editingMessageId != null,
                onCancelEdit = { manager.cancelEditing() }
            )
        }

        // ── 4. Slide-over Conversation History Sidebar ──
        AnimatedVisibility(
            visible = manager.isSidebarOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ChatHistorySidebar(
                conversations = manager.filteredConversations,
                activeConversationId = manager.activeConversationId,
                searchQuery = manager.searchQuery,
                filterPinnedOnly = manager.filterPinnedOnly,
                onToggleFilterPinnedOnly = { manager.toggleFilterPinnedOnly() },
                onSearchQueryChange = { manager.onSearchQueryChange(it) },
                onSelectConversation = { manager.selectConversation(it) },
                onNewChat = { manager.createNewChat() },
                onNavigateToCatalog = {
                    manager.toggleSidebar(false)
                    val firstAlgo = SampleData.algorithms.firstOrNull()
                    if (firstAlgo != null) onAlgorithmClick(firstAlgo)
                },
                onRenameConversation = { id, title -> manager.renameConversation(id, title) },
                onDeleteConversation = { manager.deleteConversation(it) },
                onClearAllHistory = { manager.clearAllHistory() },
                onDismiss = { manager.toggleSidebar(false) },
                modifier = Modifier.statusBarsPadding()
            )
        }

        // ── 5. Project Algorithm Planner Modal Sheet ──
        AnimatedVisibility(
            visible = manager.showProjectPlanner,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ProjectPlannerSheet(
                onSubmit = { brief -> manager.submitProjectBrief(brief) },
                onDismiss = { manager.toggleProjectPlanner(false) },
                modifier = Modifier.statusBarsPadding()
            )
        }

        // ── 6. Rename Conversation Dialog ──
        if (showRenameDialog) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                containerColor = CardBackground,
                titleContentColor = TextPrimary,
                textContentColor = TextPrimary,
                title = { Text("Rename Conversation", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    BasicTextField(
                        value = renameBuffer,
                        onValueChange = { renameBuffer = it },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                        cursorBrush = SolidColor(PrimaryCyan),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (renameBuffer.isNotBlank()) {
                                manager.renameConversation(manager.activeConversationId, renameBuffer.trim())
                            }
                            showRenameDialog = false
                        }
                    ) {
                        Text("Save", color = PrimaryCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // ── 7. Feedback Dialog ──
        if (showFeedbackDialog) {
            AlertDialog(
                onDismissRequest = { showFeedbackDialog = false },
                containerColor = CardBackground,
                titleContentColor = TextPrimary,
                textContentColor = TextPrimary,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Icon(AlgoGlyphs.Spark, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                        Text("Conversation Feedback", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                        Text(
                            text = "Help us improve offline reasoning, Big-O accuracy, and multi-language code snippets.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        BasicTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                            cursorBrush = SolidColor(PrimaryCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CardBackgroundElevated)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showFeedbackDialog = false
                            feedbackText = ""
                            toastNotice = "Feedback submitted. Thank you!"
                        }
                    ) {
                        Text("Submit", color = PrimaryCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // ── 8. Delete Confirmation Dialog ──
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                containerColor = CardBackground,
                titleContentColor = TextPrimary,
                textContentColor = TextPrimary,
                title = { Text("Delete Conversation?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Are you sure you want to delete '${manager.activeConversation.title}'? This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            manager.deleteConversation(manager.activeConversationId)
                            toastNotice = "Conversation deleted"
                        }
                    ) {
                        Text("Delete", color = AccentRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // ── 9. User Message Hold Context Menu ──
        if (userActionTarget != null) {
            val targetMsg = userActionTarget!!
            AlertDialog(
                onDismissRequest = { userActionTarget = null },
                containerColor = CardBackground,
                titleContentColor = TextPrimary,
                textContentColor = TextPrimary,
                title = {
                    Text(
                        text = "Message Options",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                        Text(
                            text = "\"${targetMsg.content.take(60)}${if (targetMsg.content.length > 60) "…" else ""}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(Modifier.height(4.dp))

                        // Edit
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .clickable {
                                    userActionTarget = null
                                    manager.startEditingMessage(targetMsg.id)
                                }
                                .padding(horizontal = AlgoTokens.space3, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(AlgoGlyphs.EditPencil, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Edit message", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Modifies text and redoes the AI output", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = AlgoType.microSize)
                            }
                        }

                        // Copy
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .clickable {
                                    userActionTarget = null
                                    clipboardManager.setText(AnnotatedString(targetMsg.content))
                                    toastNotice = "Copied text to clipboard"
                                }
                                .padding(horizontal = AlgoTokens.space3, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(AlgoGlyphs.Copy, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            Text("Copy text", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }

                        // Fork
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .clickable {
                                    userActionTarget = null
                                    val forkedId = manager.forkConversationAt(targetMsg.id)
                                    if (forkedId != null) {
                                        toastNotice = "Forked chat into new conversation"
                                    }
                                }
                                .padding(horizontal = AlgoTokens.space3, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(AlgoGlyphs.Fork, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Fork chat on this message", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Branches conversation from this point", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = AlgoType.microSize)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { userActionTarget = null }) {
                        Text("Close", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ChatHeader(
    conversationTitle: String,
    providerLabel: String,
    isPinned: Boolean,
    onOpenHistory: () -> Unit,
    onNewChat: () -> Unit,
    onShare: () -> Unit,
    onTogglePin: () -> Unit,
    onRename: () -> Unit,
    onFeedback: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            CompactIconButton(
                icon = AlgoGlyphs.SidebarMenu,
                contentDescription = "Conversation History",
                onClick = onOpenHistory,
                tint = TextSecondary,
                container = CardBackgroundElevated,
                borderColor = BorderSubtle
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isPinned) {
                        Icon(
                            imageVector = AlgoGlyphs.Pin,
                            contentDescription = "Pinned",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = conversationTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space3)
                            .clip(CircleShape)
                            .background(AccentGreen)
                    )
                    Text(
                        text = providerLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
        ) {
            CompactIconButton(
                icon = AlgoGlyphs.Plus,
                contentDescription = "New Conversation",
                onClick = onNewChat,
                tint = PrimaryCyan,
                container = CardBackgroundElevated,
                borderColor = BorderSubtle
            )

            Box {
                CompactIconButton(
                    icon = AlgoGlyphs.More,
                    contentDescription = "Conversation Options",
                    onClick = { menuExpanded = true },
                    tint = TextSecondary,
                    container = CardBackgroundElevated,
                    borderColor = BorderSubtle
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .background(CardBackgroundElevated)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                ) {
                    CompactDropdownMenuItem(
                        icon = AlgoGlyphs.Share,
                        label = "Share conversation",
                        onClick = {
                            menuExpanded = false
                            onShare()
                        }
                    )
                    CompactDropdownMenuItem(
                        icon = AlgoGlyphs.Pin,
                        label = if (isPinned) "Unpin conversation" else "Pin conversation",
                        iconTint = if (isPinned) PrimaryCyan else TextSecondary,
                        textColor = if (isPinned) PrimaryCyan else TextPrimary,
                        onClick = {
                            menuExpanded = false
                            onTogglePin()
                        }
                    )
                    CompactDropdownMenuItem(
                        icon = AlgoGlyphs.EditPencil,
                        label = "Rename",
                        onClick = {
                            menuExpanded = false
                            onRename()
                        }
                    )
                    CompactDropdownMenuItem(
                        icon = AlgoGlyphs.Spark,
                        label = "Feedback",
                        onClick = {
                            menuExpanded = false
                            onFeedback()
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = BorderSubtle,
                        thickness = 0.5.dp
                    )
                    CompactDropdownMenuItem(
                        icon = AlgoGlyphs.Trash,
                        label = "Delete conversation",
                        iconTint = AccentRed,
                        textColor = AccentRed,
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactDropdownMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: Color = TextSecondary,
    textColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyConversationStarters(
    starters: List<ChatPromptStarter>,
    onPlanProjectClick: () -> Unit,
    onStarterClick: (ChatPromptStarter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Text(
            text = "Try asking or planning",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            fontWeight = FontWeight.SemiBold
        )

        // Highlighted Project Planner Action Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CyanSubtle)
                .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), accent = PrimaryCyan)
                .clickable { onPlanProjectClick() }
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Spark,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(AlgoTokens.inlineIconLg)
                )
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                    Text(
                        text = "Plan an algorithm for my project",
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Match data scale, ordering, and memory constraints against the 14-algorithm catalog",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Icon(
                imageVector = AlgoGlyphs.ChevronRight,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(AlgoTokens.inlineIconMd)
            )
        }

        // Starter question chips in a responsive FlowRow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            starters.forEach { starter ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CardBackgroundElevated)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                        .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), accent = PrimaryCyan)
                        .clickable { onStarterClick(starter) }
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
                ) {
                    Text(
                        text = starter.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserMessageBubble(
    message: ChatMessage,
    onHold: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Text(
            text = "YOU",
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryCyan.copy(alpha = 0.8f),
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = AlgoType.trackSection
        )

        Box(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackgroundElevated.copy(alpha = 0.65f))
                .border(AlgoTokens.strokeHairline, BorderSubtle.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = {},
                    onLongClick = onHold
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = parseMarkdownInline(message.content),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontSize = AlgoType.bodySize,
                lineHeight = 22.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssistantMessageBubble(
    message: ChatMessage,
    onAction: (ChatAction) -> Unit,
    onFollowUp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        // Assistant identity: a single quiet spark glyph.
        Icon(
            imageVector = AlgoGlyphs.Spark,
            contentDescription = null,
            tint = SecondaryPurple,
            modifier = Modifier.size(AlgoTokens.inlineIconSm)
        )

        // Direct borderless content flow with generous breathing room
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Main Explanation Text (Rich Markdown Encoded)
            ChatMarkdownMessage(
                content = message.content,
                modifier = Modifier.fillMaxWidth()
            )

            // Structured Project Algorithm Recommendation (instrument card)
            message.projectRecommendation?.let { payload ->
                ProjectRecommendationBlock(
                    payload = payload,
                    onLaunchVisualizer = { algoId ->
                        val algo = SampleData.algorithms.firstOrNull { it.id == algoId }
                        if (algo != null) {
                            onAction(ChatAction.LaunchVisualizer(algo.id, algo.name))
                        }
                    },
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                )
            }

            // Complexity Matrix (if present)
            message.complexity?.let { matrix ->
                ComplexityMatrixBlock(
                    matrix = matrix,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                )
            }

            // Code Snippet (if present)
            message.codeSnippet?.let { snippet ->
                CodeSnippetBlock(
                    snippet = snippet,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                )
            }

            // Action Buttons (Launch Visualizer)
            if (message.actions.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    message.actions.forEach { action ->
                        when (action) {
                            is ChatAction.LaunchVisualizer -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                        .background(CyanSubtle)
                                        .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                                        .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), accent = PrimaryCyan)
                                        .clickable { onAction(action) }
                                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                                    ) {
                                        Icon(
                                            imageVector = AlgoGlyphs.PlayCircle,
                                            contentDescription = null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(AlgoTokens.inlineIconMd)
                                        )
                                        Text(
                                            text = "Launch Visualizer → ${action.displayName}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = PrimaryCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = AlgoType.labelSize
                                        )
                                    }
                                    Icon(
                                        imageVector = AlgoGlyphs.ChevronRight,
                                        contentDescription = null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                                    )
                                }
                            }
                            is ChatAction.QueryFollowUp -> Unit
                        }
                    }
                }
            }

            // Dynamic Suggested Follow-ups
            if (message.suggestedFollowUps.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    message.suggestedFollowUps.forEach { followUp ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CardBackgroundElevated)
                                .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), accent = SecondaryPurple)
                                .clickable { onFollowUp(followUp) }
                                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = followUp,
                                style = MaterialTheme.typography.bodySmall,
                                color = PurpleGlow,
                                fontWeight = FontWeight.Medium,
                                fontSize = AlgoType.microSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplexityMatrixBlock(
    matrix: ChatComplexitySnapshot,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
            .padding(AlgoTokens.space4),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COMPLEXITY TELEMETRY",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = AlgoType.trackSection
            )
            Text(
                text = "BIG-O RUNTIME & SPACE",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                ComplexityPill(
                    label = "BEST CASE",
                    value = matrix.bestCase,
                    accent = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                ComplexityPill(
                    label = "AVERAGE CASE",
                    value = matrix.averageCase,
                    accent = AccentYellow,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                ComplexityPill(
                    label = "WORST CASE",
                    value = matrix.worstCase,
                    accent = AccentRed,
                    modifier = Modifier.weight(1f)
                )
                ComplexityPill(
                    label = "SPACE COMPLEXITY",
                    value = matrix.spaceComplexity,
                    accent = SecondaryPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ComplexityPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(accent.copy(alpha = 0.08f))
            .border(AlgoTokens.strokeThin, accent.copy(alpha = 0.32f), RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space3, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.labelSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CodeSnippetBlock(
    snippet: ChatCodeSnippet,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Code,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(AlgoTokens.inlineIconSm)
                )
                Text(
                    text = snippet.language.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(snippet.code))
                    }
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Copy,
                        contentDescription = "Copy code",
                        tint = TextMuted,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize
                    )
                }
            }
        }

        val traceLang = when (snippet.language.lowercase().trim()) {
            "kotlin", "kt" -> TraceLanguage.KOTLIN
            "python", "py" -> TraceLanguage.PYTHON
            "java" -> TraceLanguage.JAVA
            "c++", "cpp" -> TraceLanguage.CPP
            else -> TraceLanguage.KOTLIN
        }
        val highlightedCode = remember(snippet.code, traceLang) {
            SyntaxHighlighter.highlight(snippet.code, traceLang)
        }

        Text(
            text = highlightedCode,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = JetBrainsMono,
            color = TextPrimary,
            fontSize = AlgoType.microSize,
            lineHeight = AlgoType.leadingMicroRelaxed,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(AlgoTokens.space4)
        )
    }
}

@Composable
private fun ThinkingIndicatorBubble(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        Icon(
            imageVector = AlgoGlyphs.Spark,
            contentDescription = null,
            tint = SecondaryPurple,
            modifier = Modifier.size(AlgoTokens.inlineIconSm)
        )

        // Single quiet line of intent instead of a bordered status chip —
        // the transient "thinking" cue doesn't deserve the same chrome as
        // persistent structured blocks.
        Text(
            text = "Thinking…",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ChatInputDock(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isThinking: Boolean,
    isEditing: Boolean = false,
    onCancelEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val canSend = inputText.isNotBlank() && !isThinking

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackgroundElevated)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(PrimaryCyan.copy(alpha = 0.12f))
                    .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusSm))
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.EditPencil,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Editing message",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.clickable { onCancelEdit() }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            // Text Input Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 46.dp)
                    .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                    .background(CanvasBackground)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusMd))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = "Ask about an algorithm…",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontFamily = JetBrainsMono,
                        fontSize = AlgoType.labelSize
                    )
                }

                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontFamily = JetBrainsMono,
                        fontSize = AlgoType.labelSize
                    ),
                    cursorBrush = SolidColor(PrimaryCyan),
                    singleLine = false,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() })
                )
            }

            // Send Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(AlgoTokens.radiusMd))
                    .background(if (canSend) PrimaryCyan else CardBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        if (canSend) PrimaryCyan else BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusMd)
                    )
                    .clickable(enabled = canSend) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Send,
                    contentDescription = "Send",
                    tint = if (canSend) DarkBackground else TextDark,
                    modifier = Modifier.size(AlgoTokens.inlineIconMd)
                )
            }
        }
    }
}
