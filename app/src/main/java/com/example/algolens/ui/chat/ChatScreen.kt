package com.example.algolens.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.algolens.ui.theme.JetBrainsMono
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.example.algolens.data.SampleData
import com.example.algolens.data.TraceLanguage
import com.example.algolens.model.Algorithm
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatCodeSnippet
import com.example.algolens.model.chat.ChatComplexitySnapshot
import com.example.algolens.model.chat.ChatMessage
import com.example.algolens.model.chat.ChatPromptStarter
import com.example.algolens.model.chat.ChatSender
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.AlgoHairline
import com.example.algolens.ui.components.CompactIconButton
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
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
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.visualizer.SyntaxHighlighter

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
    manager: ChatSessionManager = rememberChatSessionManager()
) {
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    var showResetConfirm by remember { mutableStateOf(false) }

    val hasUserMessages = remember(manager.messages) {
        manager.messages.any { it.sender == ChatSender.USER }
    }

    BackHandler(enabled = manager.isSidebarOpen || manager.showProjectPlanner || showResetConfirm) {
        when {
            manager.showProjectPlanner -> manager.toggleProjectPlanner(false)
            manager.isSidebarOpen -> manager.toggleSidebar(false)
            showResetConfirm -> showResetConfirm = false
        }
    }

    // Auto-scroll to latest response on message emission or thinking status change
    LaunchedEffect(manager.activeConversationId, manager.messages.size, manager.isThinking) {
        if (manager.messages.isNotEmpty()) {
            listState.animateScrollToItem(manager.messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // BottomNavBar sits directly below ChatScreen in AppShell and already applies
        // navigationBarsPadding(). Excluding navigationBars from IME insets prevents a
        // duplicate blank band above the bottom bar while lifting cleanly when the keyboard opens.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
        ) {
            // ── 1. Compact Conversation Toolbar ──
            ChatHeader(
                conversationTitle = manager.activeConversation.title,
                providerLabel = manager.providerLabel,
                onOpenHistory = {
                    focusManager.clearFocus()
                    manager.toggleSidebar(true)
                },
                onNewChat = {
                    showResetConfirm = false
                    manager.createNewChat()
                },
                onOpenPlanner = {
                    focusManager.clearFocus()
                    manager.toggleProjectPlanner(true)
                },
                onRequestReset = { showResetConfirm = true }
            )

            AlgoHairline()

            // ── Optional Confirmation Banner for Resetting Current Thread ──
            AnimatedVisibility(visible = showResetConfirm) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RedSubtle)
                        .border(AlgoTokens.strokeThin, AccentRed.copy(alpha = 0.45f))
                        .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space3),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reset this conversation thread?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(CardBackground)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                                .clickable { showResetConfirm = false }
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(AccentRed)
                                .clickable {
                                    showResetConfirm = false
                                    manager.clearChat()
                                }
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
            ) {
                items(
                    items = manager.messages,
                    key = { it.id }
                ) { msg ->
                    when (msg.sender) {
                        ChatSender.USER -> UserMessageBubble(message = msg)
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
                isThinking = manager.isThinking
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
                onSearchQueryChange = { manager.onSearchQueryChange(it) },
                onSelectConversation = { manager.selectConversation(it) },
                onNewChat = { manager.createNewChat() },
                onOpenPlanner = {
                    manager.toggleSidebar(false)
                    manager.toggleProjectPlanner(true)
                },
                onRenameConversation = { id, title -> manager.renameConversation(id, title) },
                onDeleteConversation = { manager.deleteConversation(it) },
                onClearAllHistory = { manager.clearAllHistory() },
                onQuickPrompt = { prompt ->
                    manager.toggleSidebar(false)
                    manager.sendMessage(prompt)
                },
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
    }
}

@Composable
private fun ChatHeader(
    conversationTitle: String,
    providerLabel: String,
    onOpenHistory: () -> Unit,
    onNewChat: () -> Unit,
    onOpenPlanner: () -> Unit,
    onRequestReset: () -> Unit,
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
                Text(
                    text = conversationTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                    modifier = Modifier.background(CardBackgroundElevated)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Plan algorithm for project",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = AlgoGlyphs.Spark,
                                contentDescription = null,
                                tint = SecondaryPurple,
                                modifier = Modifier.size(AlgoTokens.inlineIconMd)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenPlanner()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Reset current thread…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AccentRed
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = AlgoGlyphs.Trash,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier.size(AlgoTokens.inlineIconMd)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onRequestReset()
                        }
                    )
                }
            }
        }
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
                        text = "Match data scale, ordering, and memory constraints against the 13-algorithm catalog",
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

@Composable
private fun UserMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Text(
            text = "OPERATOR",
            style = MaterialTheme.typography.labelSmall,
            color = TextDark,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = AlgoType.trackSection
        )

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = AlgoTokens.radiusMd,
                        topEnd = AlgoTokens.radiusXs,
                        bottomStart = AlgoTokens.radiusMd,
                        bottomEnd = AlgoTokens.radiusMd
                    )
                )
                .background(CardBackgroundElevated)
                .border(
                    AlgoTokens.strokeThin,
                    BorderCyan.copy(alpha = 0.45f),
                    RoundedCornerShape(
                        topStart = AlgoTokens.radiusMd,
                        topEnd = AlgoTokens.radiusXs,
                        bottomStart = AlgoTokens.radiusMd,
                        bottomEnd = AlgoTokens.radiusMd
                    )
                )
                .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3)
        ) {
            Text(
                text = parseMarkdownInline(message.content),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontSize = AlgoType.bodySize,
                lineHeight = AlgoType.leadingBody
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
        ) {
            Icon(
                imageVector = AlgoGlyphs.Spark,
                contentDescription = null,
                tint = SecondaryPurple,
                modifier = Modifier.size(AlgoTokens.inlineIconSm)
            )
            Text(
                text = "AVIA CORE",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryPurple,
                fontSize = AlgoType.microSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = AlgoType.trackSection
            )
        }

        DoubleBezelShell(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(AlgoTokens.space5)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
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
                        }
                    )
                }

                // Complexity Matrix (if present)
                message.complexity?.let { matrix ->
                    ComplexityMatrixBlock(matrix = matrix)
                }

                // Code Snippet (if present)
                message.codeSnippet?.let { snippet ->
                    CodeSnippetBlock(snippet = snippet)
                }

                // Action Buttons (Launch Visualizer)
                if (message.actions.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
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
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
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
        Text(
            text = "COMPLEXITY TELEMETRY",
            style = MaterialTheme.typography.labelSmall,
            color = TextDark,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = AlgoType.trackSection
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ComplexityPill(label = "BEST", value = matrix.bestCase, accent = AccentGreen)
            ComplexityPill(label = "AVG", value = matrix.averageCase, accent = AccentYellow)
            ComplexityPill(label = "WORST", value = matrix.worstCase, accent = AccentRed)
            ComplexityPill(label = "SPACE", value = matrix.spaceComplexity, accent = SecondaryPurple)
        }
    }
}

@Composable
private fun ComplexityPill(
    label: String,
    value: String,
    accent: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
            .background(accent.copy(alpha = 0.10f))
            .border(AlgoTokens.strokeThin, accent.copy(alpha = 0.28f), RoundedCornerShape(AlgoTokens.radiusXxs))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = AlgoType.microSize
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
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
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
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
        Box(
            modifier = Modifier
                .size(AlgoTokens.iconButtonSm)
                .clip(CircleShape)
                .background(CyanSubtle)
                .border(AlgoTokens.strokeThin, BorderCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AlgoGlyphs.Spark,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(AlgoTokens.inlineIconSm)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, BorderCyan.copy(alpha = 0.3f), RoundedCornerShape(AlgoTokens.radiusSm))
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                Text(
                    text = "REASONING MATRIX RUNTIME",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection
                )
                Text(
                    text = "...",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ChatInputDock(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isThinking: Boolean,
    modifier: Modifier = Modifier
) {
    val canSend = inputText.isNotBlank() && !isThinking

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle)
            .navigationBarsPadding()
            .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space3),
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            // Text Input Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CanvasBackground)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = "Query complexity, code, or compare...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark,
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() })
                )
            }

            // Send Button
            Box(
                modifier = Modifier
                    .size(AlgoTokens.iconButtonMd)
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(if (canSend) PrimaryCyan else CardBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        if (canSend) PrimaryCyan else BorderSubtle,
                        RoundedCornerShape(AlgoTokens.radiusSm)
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
