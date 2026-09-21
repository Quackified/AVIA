package com.example.algolens.model.chat

import androidx.compose.runtime.Immutable
import com.example.algolens.model.AlgorithmId
import java.util.UUID

/**
 * Sender identity for an AI Chatbox dialogue turn.
 */
enum class ChatSender {
    USER,
    ASSISTANT,
    SYSTEM
}

/**
 * Lifecycle state of a chat message in the conversation stream.
 */
enum class ChatStatus {
    SENT,
    THINKING,
    COMPLETE,
    ERROR
}

/**
 * Algorithmic complexity matrix snapshot rendered as structured pills inside a chat card.
 */
@Immutable
data class ChatComplexitySnapshot(
    val bestCase: String,
    val averageCase: String,
    val worstCase: String,
    val spaceComplexity: String,
    val isStable: Boolean? = null,
    val isInPlace: Boolean? = null
)

/**
 * Code snippet container for formatted multi-language previews in the chat stream.
 */
@Immutable
data class ChatCodeSnippet(
    val language: String,
    val code: String
)

/**
 * Deep-link actions attached to assistant messages.
 */
sealed interface ChatAction {
    /** Launch the full-screen visualizer directly into the specified algorithm. */
    data class LaunchVisualizer(
        val algorithmId: AlgorithmId,
        val displayName: String
    ) : ChatAction

    /** Send an instant follow-up query to the assistant. */
    data class QueryFollowUp(
        val prompt: String
    ) : ChatAction
}

/**
 * Core entity representing a message bubble in the AI Chatbox stream.
 */
@Immutable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: ChatSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: ChatStatus = ChatStatus.COMPLETE,
    val referencedAlgorithm: AlgorithmId? = null,
    val complexity: ChatComplexitySnapshot? = null,
    val codeSnippet: ChatCodeSnippet? = null,
    val actions: List<ChatAction> = emptyList(),
    val suggestedFollowUps: List<String> = emptyList()
)

/**
 * Pre-configured prompt starter card shown on initial / idle chat states.
 */
@Immutable
data class ChatPromptStarter(
    val id: String,
    val title: String,
    val subtitle: String,
    val prompt: String,
    val algorithmId: AlgorithmId? = null
)
