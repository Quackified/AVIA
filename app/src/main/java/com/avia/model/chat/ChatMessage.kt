package com.avia.model.chat

import androidx.compose.runtime.Immutable
import com.avia.model.AlgorithmId
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
 * Structured constraints for recommending catalog algorithms for a user's project idea.
 */
@Immutable
data class ProjectConstraintBrief(
    val projectGoal: String,
    val dataScale: DataScale = DataScale.UNSPECIFIED,
    val inputOrder: InputOrder = InputOrder.UNSPECIFIED,
    val requiresStability: Boolean = false,
    val strictMemoryInPlace: Boolean = false,
    val frequentUpdates: Boolean = false,
    val graphGoal: GraphGoal = GraphGoal.NONE
) {
    enum class DataScale(val label: String) {
        UNSPECIFIED("Any / Unspecified"),
        SMALL_UNDER_64("Small (N < 64)"),
        MEDIUM_1K("Medium (100 – 10K)"),
        LARGE_100K_PLUS("Large (100K+)")
    }

    enum class InputOrder(val label: String) {
        UNSPECIFIED("General / Unspecified"),
        NEARLY_SORTED("Nearly Sorted"),
        ALREADY_SORTED("Already Sorted"),
        RANDOM_UNSORTED("Random / Unsorted"),
        LIFO_FIFO_STREAM("Stream / Undo / Queue"),
        TREE_OR_GRAPH("Hierarchy / Graph Network")
    }

    enum class GraphGoal(val label: String) {
        NONE("Not a Graph Problem"),
        SHORTEST_UNWEIGHTED_PATH("Shortest Unweighted Path"),
        EXHAUSTIVE_OR_CYCLE("Cycle Detection / Deep Search"),
        WEIGHTED_SHORTEST_PATH("Weighted Shortest Path (e.g. GPS)")
    }
}

/**
 * Verified candidate algorithm from the 14-algorithm catalog with registry-backed properties.
 */
@Immutable
data class RecommendedAlgorithmCandidate(
    val algorithmId: AlgorithmId,
    val displayName: String,
    val fitReason: String,
    val bestCase: String,
    val averageCase: String,
    val worstCase: String,
    val spaceComplexity: String,
    val isStable: Boolean,
    val isInPlace: Boolean,
    val tradeOffs: String
)

/**
 * Structured recommendation result rendered inside a chat card.
 */
@Immutable
data class ProjectRecommendationPayload(
    val projectSummary: String,
    val candidates: List<RecommendedAlgorithmCandidate>,
    val assumptions: List<String>,
    val alternatives: List<String>,
    val outOfCatalogNotice: String? = null
)

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
    val projectRecommendation: ProjectRecommendationPayload? = null,
    val actions: List<ChatAction> = emptyList(),
    val suggestedFollowUps: List<String> = emptyList()
)

/**
 * Persistent conversation thread in the local chat history store.
 */
@Immutable
data class ChatConversation(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "guest",
    val title: String = "New Conversation",
    val preview: String = "Offline catalog & step intelligence",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList(),
    val draftText: String = "",
    val isPinned: Boolean = false
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
