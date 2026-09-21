package com.example.algolens.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.algolens.data.chat.AiChatEngine
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatMessage
import com.example.algolens.model.chat.ChatPromptStarter
import com.example.algolens.model.chat.ChatSender
import com.example.algolens.model.chat.ChatStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * State manager owning reactive dialogue state, message synthesis, and actions for the AI Chatbox.
 */
@Stable
class ChatSessionManager(
    private val scope: CoroutineScope
) {
    val starters: List<ChatPromptStarter> = AiChatEngine.promptStarters

    var messages by mutableStateOf<List<ChatMessage>>(listOf(createInitialGreeting()))
        private set

    var inputText by mutableStateOf("")
        private set

    var isThinking by mutableStateOf(false)
        private set

    fun onInputChange(newText: String) {
        inputText = newText
    }

    fun sendMessage(text: String = inputText) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || isThinking) return

        val userMessage = ChatMessage(
            sender = ChatSender.USER,
            content = trimmed,
            status = ChatStatus.COMPLETE
        )

        messages = messages + userMessage
        inputText = ""
        isThinking = true

        scope.launch {
            // Realistic offline terminal synthesis pacing
            delay(350)
            val assistantResponse = AiChatEngine.processQuery(trimmed)
            messages = messages + assistantResponse
            isThinking = false
        }
    }

    fun sendStarter(starter: ChatPromptStarter) {
        sendMessage(starter.prompt)
    }

    fun executeAction(action: ChatAction, onLaunchVisualizer: (AlgorithmId) -> Unit) {
        when (action) {
            is ChatAction.LaunchVisualizer -> {
                onLaunchVisualizer(action.algorithmId)
            }
            is ChatAction.QueryFollowUp -> {
                sendMessage(action.prompt)
            }
        }
    }

    fun clearChat() {
        messages = listOf(createInitialGreeting())
        inputText = ""
        isThinking = false
    }

    companion object {
        fun createInitialGreeting(): ChatMessage {
            return ChatMessage(
                sender = ChatSender.ASSISTANT,
                content = "Greetings, Operator. I am AVIA's embedded Neural Reasoning Engine. I operate 100% offline with zero network latency.\n\n" +
                    "I can analyze Big-O complexity matrices, compare sorting and graph algorithms, generate multi-language code traces (Kotlin, Python, Java, C++), and deep-link directly into interactive visualizers.\n\n" +
                    "Select a prompt starter below or query me directly.",
                suggestedFollowUps = listOf(
                    "Compare Quick Sort vs Merge Sort",
                    "Why is Binary Search O(log n)?",
                    "When to use BFS vs DFS?"
                )
            )
        }
    }
}

@Composable
fun rememberChatSessionManager(
    scope: CoroutineScope = rememberCoroutineScope()
): ChatSessionManager {
    return remember(scope) {
        ChatSessionManager(scope = scope)
    }
}
