package com.example.algolens

import com.example.algolens.data.chat.AiChatEngine
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatSender
import com.example.algolens.ui.chat.ChatSessionManager
import com.example.algolens.ui.chat.FlowchartShape
import com.example.algolens.ui.chat.TableAlignment
import com.example.algolens.ui.chat.parseFlowchart
import com.example.algolens.ui.chat.parseMarkdownInline
import com.example.algolens.ui.chat.parseMarkdownTable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Comprehensive test suite for the AI Chatbox backend:
 *  - [AiChatEngine] intent parsing, algorithm recognition, Big-O derivation, comparisons, code retrieval, and actions.
 *  - [ChatSessionManager] dialogue state machine, asynchronous synthesis, prompt starters, and action execution.
 */
class AiChatBackendTest {

    // ── 1. AiChatEngine Algorithm Recognition & Intent Parsing ──────────

    @Test
    fun engine_detectsAlgorithmsFromInformalQueries() {
        val testQueries = listOf(
            "How does quick sort work?" to AlgorithmId.QUICK_SORT,
            "Explain bubble sort" to AlgorithmId.BUBBLE_SORT,
            "Why is merge sort stable?" to AlgorithmId.MERGE_SORT,
            "Insertion sort step walkthrough" to AlgorithmId.INSERTION_SORT,
            "Selection sort minimum search" to AlgorithmId.SELECTION_SORT,
            "Binary search tree in-order" to AlgorithmId.BINARY_SEARCH_TREE,
            "How fast is binary search?" to AlgorithmId.BINARY_SEARCH,
            "Explain linear search" to AlgorithmId.LINEAR_SEARCH,
            "LIFO discipline in stack" to AlgorithmId.STACK,
            "Queue FIFO operations" to AlgorithmId.QUEUE,
            "Heap sift down mechanics" to AlgorithmId.HEAP,
            "BFS shortest path" to AlgorithmId.BFS,
            "DFS recursive traversal" to AlgorithmId.DFS
        )

        testQueries.forEach { (query, expectedAlgo) ->
            val response = AiChatEngine.processQuery(query)
            assertEquals("Failed algorithm recognition for '$query'", expectedAlgo, response.referencedAlgorithm)
            assertTrue("Assistant response must not be blank for '$query'", response.content.isNotBlank())
        }
    }

    @Test
    fun engine_resolvesComplexityQueriesWithStructuredMatrix() {
        val response = AiChatEngine.processQuery("What is the time complexity of quick sort?")

        assertEquals(AlgorithmId.QUICK_SORT, response.referencedAlgorithm)
        assertNotNull("Complexity matrix must be populated for complexity query", response.complexity)
        val matrix = response.complexity!!
        assertEquals("O(n log n)", matrix.averageCase)
        assertTrue("Worst case must contain O(n²)", matrix.worstCase.contains("O(n²)"))
        assertEquals("O(log n) Call Stack", matrix.spaceComplexity)
        assertFalse("Quick sort must not be marked stable", matrix.isStable ?: true)

        // Verifies visualizer deep-link action attached
        assertTrue("Must include LaunchVisualizer action", response.actions.any { it is ChatAction.LaunchVisualizer })
        val launchAction = response.actions.first { it is ChatAction.LaunchVisualizer } as ChatAction.LaunchVisualizer
        assertEquals(AlgorithmId.QUICK_SORT, launchAction.algorithmId)
    }

    @Test
    fun engine_resolvesComparativeQueriesWithSideBySideAnalysis() {
        val response = AiChatEngine.processQuery("Compare Quick Sort vs Merge Sort in time complexity and space")

        assertTrue("Content must contain comparison header", response.content.contains("COMPARATIVE ANALYSIS"))
        assertTrue("Content must mention Quick Sort", response.content.contains("Quick Sort"))
        assertTrue("Content must mention Merge Sort", response.content.contains("Merge Sort"))
        assertTrue("Content must explain O(log n) vs O(n) auxiliary space", response.content.contains("O(n)"))

        // Should attach deep-link actions for both algorithms
        val launchActions = response.actions.filterIsInstance<ChatAction.LaunchVisualizer>()
        assertEquals("Must offer deep links to both compared algorithms", 2, launchActions.size)
        assertTrue(launchActions.any { it.algorithmId == AlgorithmId.QUICK_SORT })
        assertTrue(launchActions.any { it.algorithmId == AlgorithmId.MERGE_SORT })
    }

    @Test
    fun engine_resolvesBfsVsDfsComparison() {
        val response = AiChatEngine.processQuery("BFS vs DFS: which is better for finding shortest path?")

        assertTrue("Content must explain BFS shortest path guarantee", response.content.contains("BFS") && response.content.contains("shortest"))
        val launchActions = response.actions.filterIsInstance<ChatAction.LaunchVisualizer>()
        assertEquals(2, launchActions.size)
        assertTrue(launchActions.any { it.algorithmId == AlgorithmId.BFS })
        assertTrue(launchActions.any { it.algorithmId == AlgorithmId.DFS })
    }

    @Test
    fun engine_resolvesCodeSnippetQueries() {
        val kotlinResponse = AiChatEngine.processQuery("Show me kotlin code for binary search")
        assertNotNull("Code snippet must be attached", kotlinResponse.codeSnippet)
        assertEquals("Kotlin", kotlinResponse.codeSnippet!!.language)
        assertTrue("Code snippet must contain binarySearch function", kotlinResponse.codeSnippet!!.code.contains("fun binarySearch"))

        val pythonResponse = AiChatEngine.processQuery("Show me python code for merge sort")
        assertNotNull("Python code snippet must be attached", pythonResponse.codeSnippet)
        assertEquals("Python", pythonResponse.codeSnippet!!.language)
        assertTrue("Python code snippet must contain def merge_sort", pythonResponse.codeSnippet!!.code.contains("def merge_sort"))
    }

    @Test
    fun engine_promptStartersAreCuratedAndComplete() {
        val starters = AiChatEngine.promptStarters
        assertTrue("Must provide at least 5 prompt starters", starters.size >= 5)

        starters.forEach { starter ->
            assertTrue("Starter id must be present", starter.id.isNotBlank())
            assertTrue("Starter title must be present", starter.title.isNotBlank())
            assertTrue("Starter prompt must not be blank", starter.prompt.isNotBlank())

            // Every starter query should yield a valid assistant response
            val response = AiChatEngine.processQuery(starter.prompt)
            assertEquals(ChatSender.ASSISTANT, response.sender)
            assertTrue(response.content.isNotBlank())
        }
    }

    @Test
    fun engine_handlesStabilityGeneralQuery() {
        val response = AiChatEngine.processQuery("What is sorting stability?")
        assertTrue("Response must explain stability definition", response.content.contains("if and only if"))
        assertTrue("Response must mention stable and unstable sorts", response.content.contains("Merge Sort") && response.content.contains("Quick Sort"))
    }

    @Test
    fun engine_handlesCatalogueAndHelpQueries() {
        val catalogueResponse = AiChatEngine.processQuery("What algorithms are in the catalogue?")
        assertTrue("Must report 13 verified algorithms", catalogueResponse.content.contains("13 fully verified offline algorithms"))

        val helpResponse = AiChatEngine.processQuery("Help me, what can you do?")
        assertTrue("Help response must emphasize offline capabilities", helpResponse.content.contains("100% offline"))
    }

    // ── 2. ChatSessionManager State Machine & Action Execution ─────────

    @Test
    fun sessionManager_initialStateIsCorrect() {
        runBlocking {
            val manager = ChatSessionManager(scope = this)
            assertEquals("Initial dialogue starts with 1 greeting message", 1, manager.messages.size)
            assertEquals(ChatSender.ASSISTANT, manager.messages.first().sender)
            assertFalse("Initial thinking state must be false", manager.isThinking)
            assertEquals("", manager.inputText)
            assertTrue("Prompt starters must be available", manager.starters.isNotEmpty())
        }
    }

    @Test
    fun sessionManager_messageSendingAppendsUserAndAssistantTurn() {
        runBlocking {
            val manager = ChatSessionManager(scope = this)

            manager.onInputChange("Why is Binary Search O(log n)?")
            assertEquals("Why is Binary Search O(log n)?", manager.inputText)

            manager.sendMessage()

            // After sending, input is cleared
            assertEquals("", manager.inputText)

            // User message was appended
            assertTrue("Messages must have at least user and assistant", manager.messages.size >= 2)
            val userMsg = manager.messages[1]
            assertEquals(ChatSender.USER, userMsg.sender)
            assertEquals("Why is Binary Search O(log n)?", userMsg.content)

            // Wait for assistant async delay
            kotlinx.coroutines.delay(450)

            assertEquals("Dialogue must have 3 messages (Greeting + User + Assistant)", 3, manager.messages.size)
            val assistantMsg = manager.messages[2]
            assertEquals(ChatSender.ASSISTANT, assistantMsg.sender)
            assertEquals(AlgorithmId.BINARY_SEARCH, assistantMsg.referencedAlgorithm)
            assertFalse("Thinking must be complete", manager.isThinking)
        }
    }

    @Test
    fun sessionManager_executesLaunchVisualizerAction() {
        runBlocking {
            val manager = ChatSessionManager(scope = this)
            var launchedAlgoId: AlgorithmId? = null

            val action = ChatAction.LaunchVisualizer(AlgorithmId.QUICK_SORT, "Quick Sort")
            manager.executeAction(action) { algoId ->
                launchedAlgoId = algoId
            }

            assertEquals(AlgorithmId.QUICK_SORT, launchedAlgoId)
        }
    }

    @Test
    fun sessionManager_clearChatResetsToGreeting() {
        runBlocking {
            val manager = ChatSessionManager(scope = this)
            manager.sendMessage("Hello")
            kotlinx.coroutines.delay(400)
            assertTrue("Messages should have more than 1", manager.messages.size > 1)

            manager.clearChat()
            assertEquals("Reset to single greeting message", 1, manager.messages.size)
            assertEquals(ChatSender.ASSISTANT, manager.messages.first().sender)
            assertEquals("", manager.inputText)
            assertFalse(manager.isThinking)
        }
    }

    // ── 3. Markdown Rich Text Parser ───────────────────────────────────

    @Test
    fun markdown_parsesBoldInlineCodeAndItalics() {
        val raw = "Comparing **Quick Sort** with `O(n log n)` and *in-place* property."
        val annotated = parseMarkdownInline(raw)

        assertEquals("Comparing Quick Sort with O(n log n) and in-place property.", annotated.text)
        assertTrue("Must contain span styles for bold, code, and italics", annotated.spanStyles.size >= 3)
    }

    @Test
    fun markdown_parsesTablesWithAlignmentAndPadding() {
        val tableLines = listOf(
            "| Algorithm | Time (Avg) | Space | Stability |",
            "| :--- | :---: | ---: | :--- |",
            "| Merge Sort | O(n log n) | O(n) | Stable |",
            "| Quick Sort | O(n log n) | O(log n) |"
        )
        val table = parseMarkdownTable(tableLines)
        assertNotNull("Table must be parsed", table)
        assertEquals(listOf("Algorithm", "Time (Avg)", "Space", "Stability"), table!!.headers)
        assertEquals(4, table.alignments.size)
        assertEquals(TableAlignment.LEFT, table.alignments[0])
        assertEquals(TableAlignment.CENTER, table.alignments[1])
        assertEquals(TableAlignment.RIGHT, table.alignments[2])
        assertEquals(TableAlignment.LEFT, table.alignments[3])

        assertEquals(2, table.rows.size)
        assertEquals(listOf("Merge Sort", "O(n log n)", "O(n)", "Stable"), table.rows[0])
        // Verify padding of 4th cell in 2nd row
        assertEquals(listOf("Quick Sort", "O(n log n)", "O(log n)", ""), table.rows[1])
    }

    @Test
    fun markdown_parsesFlowchartWithNodesAndEdges() {
        val flowchartText = """
            flowchart TD
            Start(Input Array) --> Pivot[Select Pivot]
            Pivot --> Decision{Partition?}
            Decision -->|Yes| Left[Recurse Left]
            Decision -->|No| Done(End)
        """.trimIndent()

        val flowchart = parseFlowchart(flowchartText)
        assertNotNull("Flowchart must be parsed", flowchart)
        val nodes = flowchart!!.nodes
        assertEquals(5, nodes.size)

        val startNode = nodes.firstOrNull { it.id == "Start" }
        assertNotNull(startNode)
        assertEquals(FlowchartShape.TERMINAL, startNode!!.shape)
        assertEquals("Input Array", startNode.label)

        val decNode = nodes.firstOrNull { it.id == "Decision" }
        assertNotNull(decNode)
        assertEquals(FlowchartShape.DECISION, decNode!!.shape)
        assertEquals("Partition?", decNode.label)

        val doneNode = nodes.firstOrNull { it.id == "Done" }
        assertNotNull(doneNode)
        assertEquals(FlowchartShape.TERMINAL, doneNode!!.shape)
        assertEquals("End", doneNode.label)

        assertEquals(4, flowchart.edges.size)
        val yesEdge = flowchart.edges.firstOrNull { it.fromId == "Decision" && it.toId == "Left" }
        assertNotNull(yesEdge)
        assertEquals("Yes", yesEdge!!.label)
    }

    @Test
    fun engine_generatesMarkdownTableForComparativeQueries() {
        val response = AiChatEngine.processQuery("Compare Quick Sort vs Merge Sort")
        assertTrue("Content must contain markdown table header", response.content.contains("| Metric | Quick Sort | Merge Sort |"))
        assertTrue("Content must contain table delimiter", response.content.contains("| :--- | :---: | :---: |"))
        assertTrue("Content must include TIP callout", response.content.contains("> [!TIP]"))
    }

    @Test
    fun engine_generatesFlowchartAndCalloutsForAlgorithmExplanation() {
        val response = AiChatEngine.processQuery("How does binary search work?")
        assertTrue("Content must include flowchart block", response.content.contains("```flowchart"))
        assertTrue("Flowchart must include binary search logic", response.content.contains("Compute mid") && response.content.contains("target == arr[mid]"))
        assertTrue("Content must include TIP callout", response.content.contains("> [!TIP]"))
    }

    @Test
    fun engine_generatesTableAndCalloutForComplexityQuery() {
        val response = AiChatEngine.processQuery("What is the time complexity of merge sort?")
        assertTrue("Content must contain markdown table for metrics", response.content.contains("| Metric | Bound / Property |"))
        assertTrue("Content must contain Best Case row", response.content.contains("| Best Case | `O(n log n)` |"))
        assertTrue("Content must include TIP callout", response.content.contains("> [!TIP]"))
    }
}
