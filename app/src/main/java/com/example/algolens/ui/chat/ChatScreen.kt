package com.example.algolens.ui.chat

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.example.algolens.data.SampleData
import com.example.algolens.data.TraceLanguage
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.visualizer.SyntaxHighlighter
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatCodeSnippet
import com.example.algolens.model.chat.ChatComplexitySnapshot
import com.example.algolens.model.chat.ChatMessage
import com.example.algolens.model.chat.ChatPromptStarter
import com.example.algolens.model.chat.ChatSender
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
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.JetBrainsMono
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

/**
 * AVIA AI Chatbox Screen.
 *
 * Implements an offline-first algorithmic conversation UI framed with the
 * tech-noir aesthetic:
 *  - Top header with offline status indicator and clear chat action.
 *  - Quick prompt starters horizontal carousel.
 *  - Interactive message stream with complexity matrices, code previews, and visualizer deep-links.
 *  - Bottom terminal dock with monospace input and cyan send trigger.
 */
@Composable
fun ChatScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    modifier: Modifier = Modifier,
    manager: ChatSessionManager = rememberChatSessionManager()
) {
    val listState = rememberLazyListState()

    // Auto-scroll to latest response on message emission or thinking status change
    LaunchedEffect(manager.messages.size, manager.isThinking) {
        if (manager.messages.isNotEmpty()) {
            listState.animateScrollToItem(manager.messages.size)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        // ── 1. Top Instrument Header ──
        ChatHeader(
            onClear = { manager.clearChat() }
        )

        // ── 2. Quick Prompt Starters Carousel ──
        PromptStartersBar(
            starters = manager.starters,
            onStarterClick = { manager.sendStarter(it) }
        )

        // ── 3. Conversation Stream ──
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = AlgoTokens.space5,
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
                        onFollowUp = { prompt -> manager.sendMessage(prompt) }
                    )
                }
            }

            if (manager.isThinking) {
                item(key = "thinking_indicator") {
                    ThinkingIndicatorBubble()
                }
            }
        }

        // ── 4. Bottom Terminal Input Dock ──
        ChatInputDock(
            inputText = manager.inputText,
            onInputChange = { manager.onInputChange(it) },
            onSend = { manager.sendMessage() },
            isThinking = manager.isThinking
        )
    }
}

@Composable
private fun ChatHeader(
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            Box(
                modifier = Modifier
                    .size(AlgoTokens.iconButtonMd)
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(PurpleSubtle)
                    .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.35f), RoundedCornerShape(AlgoTokens.radiusSm)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AlgoGlyphs.Spark,
                    contentDescription = null,
                    tint = PurpleGlow,
                    modifier = Modifier.size(AlgoTokens.inlineIconMd)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                Text(
                    text = "AVIA AI COPILOT",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space2)
                            .clip(CircleShape)
                            .background(AccentGreen)
                    )
                    Text(
                        text = "OFFLINE STEP INTELLIGENCE",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackTight
                    )
                }
            }
        }

        // Clear Chat Action
        Box(
            modifier = Modifier
                .size(AlgoTokens.iconButtonSm)
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackground)
                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                .clickable { onClear() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AlgoGlyphs.Trash,
                contentDescription = "Clear Chat",
                tint = TextSecondary,
                modifier = Modifier.size(AlgoTokens.inlineIconMd)
            )
        }
    }
}

@Composable
private fun PromptStartersBar(
    starters: List<ChatPromptStarter>,
    onStarterClick: (ChatPromptStarter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
    ) {
        starters.forEach { starter ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                    .background(CardBackgroundElevated)
                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                    .clickable { onStarterClick(starter) }
                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Spark,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    Text(
                        text = starter.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = AlgoType.labelSize
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
                ChatMarkdownMessage(content = message.content)

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
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                            .background(CyanSubtle)
                                            .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                                            .clickable { onAction(action) }
                                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
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
                                }
                                is ChatAction.QueryFollowUp -> {
                                    // Handled in follow-ups row
                                }
                            }
                        }
                    }
                }

                // Dynamic Suggested Follow-ups
                if (message.suggestedFollowUps.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                        Text(
                            text = "SUGGESTED EXPLORATION:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = AlgoType.trackSection
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            message.suggestedFollowUps.forEach { followUp ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                        .background(PurpleSubtle)
                                        .border(AlgoTokens.strokeThin, SecondaryPurple.copy(alpha = 0.3f), RoundedCornerShape(AlgoTokens.radiusXs))
                                        .clickable { onFollowUp(followUp) }
                                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                                ) {
                                    Text(
                                        text = followUp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PurpleGlow,
                                        fontSize = AlgoType.microSize,
                                        fontWeight = FontWeight.SemiBold
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

@Composable
private fun ComplexityMatrixBlock(
    matrix: ChatComplexitySnapshot,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
            .padding(AlgoTokens.space4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
            Text(
                text = "COMPLEXITY MATRIX",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontSize = AlgoType.microSize,
                letterSpacing = AlgoType.trackSection,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ComplexityPill(label = "Best", value = matrix.bestCase, accent = AccentGreen)
                ComplexityPill(label = "Avg", value = matrix.averageCase, accent = AccentYellow)
                ComplexityPill(label = "Worst", value = matrix.worstCase, accent = AccentRed)
                ComplexityPill(label = "Space", value = matrix.spaceComplexity, accent = SecondaryPurple)
            }
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
            .background(accent.copy(alpha = 0.12f))
            .border(AlgoTokens.strokeThin, accent.copy(alpha = 0.3f), RoundedCornerShape(AlgoTokens.radiusXxs))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = AlgoType.microSize,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.labelSize
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
        // Snippet Header
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
                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
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

        // Code Lines (Syntax Highlighted)
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
            modifier = Modifier.padding(AlgoTokens.space4)
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
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
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
