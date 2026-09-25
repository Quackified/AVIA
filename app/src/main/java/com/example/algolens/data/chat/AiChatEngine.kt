package com.example.algolens.data.chat

import com.example.algolens.data.AlgorithmCodeRegistry
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.TraceLanguage
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatCodeSnippet
import com.example.algolens.model.chat.ChatComplexitySnapshot
import com.example.algolens.model.chat.ChatMessage
import com.example.algolens.model.chat.ChatPromptStarter
import com.example.algolens.model.chat.ChatSender
import com.example.algolens.model.chat.ChatStatus
import com.example.algolens.ui.visualizer.AlgorithmTheoryRepository

/**
 * Embedded offline algorithmic intelligence reasoning engine.
 *
 * Provides domain-specific synthesis, Big-O complexity analysis, comparative
 * breakdowns, code listings, and deep-links directly into interactive visualizers
 * without making network requests.
 */
object AiChatEngine {

    val promptStarters: List<ChatPromptStarter> = listOf(
        ChatPromptStarter(
            id = "starter_quick_merge",
            title = "Quick vs Merge Sort",
            subtitle = "Compare divide-and-conquer strategies & cache locality",
            prompt = "Compare Quick Sort vs Merge Sort in time complexity, space, and stability.",
            algorithmId = AlgorithmId.QUICK_SORT
        ),
        ChatPromptStarter(
            id = "starter_binary_search",
            title = "Binary Search O(log n)",
            subtitle = "Why halving the search space yields logarithmic steps",
            prompt = "Why is Binary Search O(log n) time complexity?",
            algorithmId = AlgorithmId.BINARY_SEARCH
        ),
        ChatPromptStarter(
            id = "starter_bfs_dfs",
            title = "BFS vs DFS Traversal",
            subtitle = "Queue vs Stack and shortest paths on graphs",
            prompt = "When should I use BFS instead of DFS on a graph?",
            algorithmId = AlgorithmId.BFS
        ),
        ChatPromptStarter(
            id = "starter_bst_invariants",
            title = "BST Invariant",
            subtitle = "Subtree ordering rules & degenerate branch edge cases",
            prompt = "Explain the Binary Search Tree invariant and what happens when it degenerates.",
            algorithmId = AlgorithmId.BINARY_SEARCH_TREE
        ),
        ChatPromptStarter(
            id = "starter_insertion_small",
            title = "Insertion Sort Efficiency",
            subtitle = "Why production hybrid sorts (Timsort) use it for N < 32",
            prompt = "Why is Insertion Sort faster than O(n log n) sorts on small arrays?",
            algorithmId = AlgorithmId.INSERTION_SORT
        ),
        ChatPromptStarter(
            id = "starter_stability",
            title = "Sorting Stability",
            subtitle = "Preserving relative order of identical keys",
            prompt = "What does stability mean in sorting algorithms and which ones are stable?",
            algorithmId = null
        )
    )

    /**
     * Synthesizes an offline assistant response for a given user query.
     */
    fun processQuery(userText: String): ChatMessage {
        val query = userText.trim()
        val queryLower = query.lowercase()

        // 0. Check for project-idea algorithm recommendation requests
        if (ProjectAlgorithmRecommender.isProjectAdviceQuery(queryLower)) {
            val payload = ProjectAlgorithmRecommender.recommendFromNaturalLanguage(query)
            return ProjectAlgorithmRecommender.toChatMessage(payload)
        }

        // 1. Check for comparative queries (e.g. "quick sort vs merge sort")
        val comparisonPair = detectComparison(queryLower)
        if (comparisonPair != null) {
            return generateComparisonResponse(comparisonPair.first, comparisonPair.second)
        }

        // 2. Detect target algorithm
        val detectedAlgo = detectAlgorithm(queryLower)

        // 3. Detect intent
        val isCodeQuery = queryLower.contains("code") ||
            queryLower.contains("implementation") ||
            queryLower.contains("syntax") ||
            queryLower.contains("kotlin") ||
            queryLower.contains("python") ||
            queryLower.contains("java") ||
            queryLower.contains("c++") ||
            queryLower.contains("cpp")

        val isComplexityQuery = queryLower.contains("complexity") ||
            queryLower.contains("big o") ||
            queryLower.contains("big-o") ||
            queryLower.contains("time") ||
            queryLower.contains("space") ||
            queryLower.contains("worst") ||
            queryLower.contains("best") ||
            queryLower.contains("average")

        val isStabilityQuery = queryLower.contains("stable") || queryLower.contains("stability")

        return when {
            detectedAlgo != null && isCodeQuery -> {
                generateCodeResponse(detectedAlgo, queryLower)
            }
            detectedAlgo != null && isComplexityQuery -> {
                generateComplexityResponse(detectedAlgo)
            }
            detectedAlgo != null -> {
                generateAlgorithmExplanation(detectedAlgo, queryLower)
            }
            isStabilityQuery -> {
                generateGeneralStabilityResponse()
            }
            queryLower.contains("what algorithms") || queryLower.contains("catalogue") || queryLower.contains("list") -> {
                generateCatalogueResponse()
            }
            queryLower.contains("help") || queryLower.contains("who are you") || queryLower.contains("what can you do") -> {
                generateHelpResponse()
            }
            else -> {
                generateFallbackResponse(query)
            }
        }
    }

    private fun detectAlgorithm(query: String): AlgorithmId? {
        return when {
            query.contains("bubble") -> AlgorithmId.BUBBLE_SORT
            query.contains("selection") -> AlgorithmId.SELECTION_SORT
            query.contains("insertion") -> AlgorithmId.INSERTION_SORT
            query.contains("merge") -> AlgorithmId.MERGE_SORT
            query.contains("quick") -> AlgorithmId.QUICK_SORT
            query.contains("binary search tree") || query.contains("bst") -> AlgorithmId.BINARY_SEARCH_TREE
            query.contains("binary search") -> AlgorithmId.BINARY_SEARCH
            query.contains("linear search") || query.contains("linear") -> AlgorithmId.LINEAR_SEARCH
            query.contains("stack") -> AlgorithmId.STACK
            query.contains("queue") -> AlgorithmId.QUEUE
            query.contains("heap") || query.contains("priority queue") -> AlgorithmId.HEAP
            query.contains("bfs") || query.contains("breadth") -> AlgorithmId.BFS
            query.contains("dfs") || query.contains("depth") -> AlgorithmId.DFS
            else -> null
        }
    }

    private fun detectComparison(query: String): Pair<AlgorithmId, AlgorithmId>? {
        val hasVs = query.contains(" vs ") || query.contains(" versus ") || query.contains(" or ") || query.contains("compare")
        if (!hasVs) return null

        val algoMatches = mutableListOf<Pair<Int, AlgorithmId>>()
        fun recordMatch(needle: String, id: AlgorithmId) {
            val idx = query.indexOf(needle)
            if (idx != -1) {
                algoMatches.add(idx to id)
            }
        }

        recordMatch("bubble", AlgorithmId.BUBBLE_SORT)
        recordMatch("selection", AlgorithmId.SELECTION_SORT)
        recordMatch("insertion", AlgorithmId.INSERTION_SORT)
        recordMatch("merge", AlgorithmId.MERGE_SORT)
        recordMatch("quick", AlgorithmId.QUICK_SORT)
        if (query.contains("binary search tree") || query.contains("bst")) {
            val idx = if (query.contains("binary search tree")) query.indexOf("binary search tree") else query.indexOf("bst")
            algoMatches.add(idx to AlgorithmId.BINARY_SEARCH_TREE)
        } else if (query.contains("binary search")) {
            algoMatches.add(query.indexOf("binary search") to AlgorithmId.BINARY_SEARCH)
        }
        recordMatch("linear", AlgorithmId.LINEAR_SEARCH)
        recordMatch("stack", AlgorithmId.STACK)
        recordMatch("queue", AlgorithmId.QUEUE)
        recordMatch("heap", AlgorithmId.HEAP)
        recordMatch("bfs", AlgorithmId.BFS)
        recordMatch("dfs", AlgorithmId.DFS)

        val sortedAlgos = algoMatches.sortedBy { it.first }.map { it.second }.distinct()
        return if (sortedAlgos.size >= 2) Pair(sortedAlgos[0], sortedAlgos[1]) else null
    }

    private fun generateComparisonResponse(algoA: AlgorithmId, algoB: AlgorithmId): ChatMessage {
        val theoryA = AlgorithmTheoryRepository.getTheory(algoA.displayName)
        val theoryB = AlgorithmTheoryRepository.getTheory(algoB.displayName)

        val content = buildString {
            append("### COMPARATIVE ANALYSIS: ${algoA.displayName} vs ${algoB.displayName}\n\n")

            append("| Metric | ${algoA.displayName} | ${algoB.displayName} |\n")
            append("| :--- | :---: | :---: |\n")
            append("| Best Case | `${theoryA.bestCase}` | `${theoryB.bestCase}` |\n")
            append("| Average Case | `${theoryA.averageCase}` | `${theoryB.averageCase}` |\n")
            append("| Worst Case | `${theoryA.worstCase}` | `${theoryB.worstCase}` |\n")
            append("| Auxiliary Space | `${theoryA.spaceComplexity}` | `${theoryB.spaceComplexity}` |\n")
            append("| Stability | ${if (theoryA.isStable) "Stable" else "Unstable"} | ${if (theoryB.isStable) "Stable" else "Unstable"} |\n")
            append("| In-Place | ${if (theoryA.isInPlace) "Yes" else "No"} | ${if (theoryB.isInPlace) "Yes" else "No"} |\n\n")

            append("**Architectural Trade-offs & When to Pick:**\n")
            if (algoA == AlgorithmId.QUICK_SORT && algoB == AlgorithmId.MERGE_SORT ||
                algoA == AlgorithmId.MERGE_SORT && algoB == AlgorithmId.QUICK_SORT
            ) {
                append("• **Quick Sort** provides superior average-case wall-clock performance due to contiguous memory access and CPU cache locality, requiring only O(log n) auxiliary stack frames.\n")
                append("• **Merge Sort** guarantees O(n log n) in all cases (immune to pathological input) and provides guaranteed stability, but requires O(n) auxiliary heap buffer allocation.\n\n")
                append("> [!TIP]\n")
                append("> Choose Quick Sort for generic in-memory arrays when memory is constrained and raw throughput is critical. Choose Merge Sort when external sorting or identical-key stability is mandatory.")
            } else if (algoA == AlgorithmId.BFS && algoB == AlgorithmId.DFS ||
                algoA == AlgorithmId.DFS && algoB == AlgorithmId.BFS
            ) {
                append("• **BFS** explores level-by-level using a FIFO Queue; it is mathematically guaranteed to find the shortest unweighted path.\n")
                append("• **DFS** explores deep branches using a LIFO Stack / recursion; ideal for topological ordering, cycle detection, and maze solving with lower peak memory footprint on thin trees.\n\n")
                append("> [!NOTE]\n")
                append("> BFS requires O(V) queue capacity in wide graphs, while DFS only tracks O(h) recursion depth along the current branch.")
            } else {
                append("• **${algoA.displayName}**: Best suited for ${theoryA.whenToUse.firstOrNull() ?: "its canonical domain"}.\n")
                append("• **${algoB.displayName}**: Best suited for ${theoryB.whenToUse.firstOrNull() ?: "its canonical domain"}.\n\n")
                append("> [!TIP]\n")
                append("> ${theoryA.proTips}")
            }
        }

        val actions = listOf(
            ChatAction.LaunchVisualizer(algoA, algoA.displayName),
            ChatAction.LaunchVisualizer(algoB, algoB.displayName)
        )

        val followUps = listOf(
            "Show code for ${algoA.displayName}",
            "Show code for ${algoB.displayName}",
            "How does Lomuto partitioning work?"
        )

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            referencedAlgorithm = algoA,
            actions = actions,
            suggestedFollowUps = followUps
        )
    }

    private fun generateComplexityResponse(algo: AlgorithmId): ChatMessage {
        val theory = AlgorithmTheoryRepository.getTheory(algo.displayName)

        val complexity = ChatComplexitySnapshot(
            bestCase = theory.bestCase,
            averageCase = theory.averageCase,
            worstCase = theory.worstCase,
            spaceComplexity = theory.spaceComplexity,
            isStable = theory.isStable,
            isInPlace = theory.isInPlace
        )

        val content = buildString {
            append("### COMPLEXITY MATRIX: ${algo.displayName}\n\n")

            append("| Metric | Bound / Property |\n")
            append("| :--- | :--- |\n")
            append("| Best Case | `${theory.bestCase}` |\n")
            append("| Average Case | `${theory.averageCase}` |\n")
            append("| Worst Case | `${theory.worstCase}` |\n")
            append("| Auxiliary Space | `${theory.spaceComplexity}` |\n")
            append("| Stability | **${if (theory.isStable) "Stable" else "Unstable"}** |\n")
            append("| In-Place | **${if (theory.isInPlace) "Yes" else "No"}** |\n\n")

            append("**Theoretical Derivation:**\n")
            append("${theory.overview}\n\n")
            append("> [!TIP]\n")
            append("> ${theory.proTips}")
        }

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            referencedAlgorithm = algo,
            complexity = complexity,
            actions = listOf(ChatAction.LaunchVisualizer(algo, algo.displayName)),
            suggestedFollowUps = listOf(
                "Show implementation code for ${algo.displayName}",
                "What are the edge cases for ${algo.displayName}?",
                "Step-by-step walkthrough"
            )
        )
    }

    private fun generateCodeResponse(algo: AlgorithmId, query: String): ChatMessage {
        val language = when {
            query.contains("python") -> TraceLanguage.PYTHON
            query.contains("java") -> TraceLanguage.JAVA
            query.contains("cpp") || query.contains("c++") -> TraceLanguage.CPP
            else -> TraceLanguage.KOTLIN
        }

        val codeData = AlgorithmCodeRegistry.getCode(algo.displayName, language)
        val snippetText = codeData.lines.joinToString("\n")

        val codeSnippet = ChatCodeSnippet(
            language = language.label,
            code = snippetText
        )

        val content = buildString {
            append("Here is the canonical **${language.label}** implementation of **${algo.displayName}** verified for offline step tracing:")
        }

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            referencedAlgorithm = algo,
            codeSnippet = codeSnippet,
            actions = listOf(ChatAction.LaunchVisualizer(algo, algo.displayName)),
            suggestedFollowUps = listOf(
                "Explain time complexity of ${algo.displayName}",
                "Show in Python",
                "Show in Kotlin"
            )
        )
    }

    private fun generateAlgorithmExplanation(algo: AlgorithmId, query: String): ChatMessage {
        val theory = AlgorithmTheoryRepository.getTheory(algo.displayName)
        val flowchart = getAlgorithmFlowchart(algo)

        val content = buildString {
            append("### ${theory.name}\n\n")
            append("${theory.overview}\n\n")

            append("**Algorithm Control Flow:**\n")
            append(flowchart)
            append("\n\n")

            append("**Execution Mechanics:**\n")
            theory.howItWorks.take(4).forEachIndexed { index, step ->
                append("${index + 1}. $step\n")
            }

            if (theory.whenToUse.isNotEmpty()) {
                append("\n**Optimal Use Cases:**\n")
                theory.whenToUse.take(2).forEach {
                    append("• $it\n")
                }
            }

            append("\n> [!TIP]\n")
            append("> ${theory.proTips}")
        }

        val complexity = ChatComplexitySnapshot(
            bestCase = theory.bestCase,
            averageCase = theory.averageCase,
            worstCase = theory.worstCase,
            spaceComplexity = theory.spaceComplexity,
            isStable = theory.isStable,
            isInPlace = theory.isInPlace
        )

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            referencedAlgorithm = algo,
            complexity = complexity,
            actions = listOf(ChatAction.LaunchVisualizer(algo, algo.displayName)),
            suggestedFollowUps = listOf(
                "Show ${algo.displayName} code",
                "Why is ${algo.displayName} ${theory.worstCase} in worst case?",
                "Compare with other algorithms"
            )
        )
    }

    private fun generateGeneralStabilityResponse(): ChatMessage {
        val content = buildString {
            append("### ALGORITHMIC STABILITY IN SORTING\n\n")
            append("A sorting algorithm is **stable** if and only if two objects with equal keys appear in the same relative order in the sorted output as they were in the unsorted input.\n\n")

            append("| Algorithm | Average Time | Space | Stability |\n")
            append("| :--- | :---: | :---: | :---: |\n")
            append("| Bubble Sort | `O(n²)` | `O(1)` | Stable |\n")
            append("| Insertion Sort | `O(n²)` | `O(1)` | Stable |\n")
            append("| Merge Sort | `O(n log n)` | `O(n)` | Stable |\n")
            append("| Quick Sort | `O(n log n)` | `O(log n)` | Unstable |\n")
            append("| Selection Sort | `O(n²)` | `O(1)` | Unstable |\n\n")

            append("**Why Stability Matters:**\n")
            append("Essential when performing multi-key sorting (e.g. sorting by First Name, then sorting by Last Name). An unstable sort would scramble the first name ordering.\n\n")

            append("> [!IMPORTANT]\n")
            append("> In-place algorithms with long-distance swaps (such as Quick Sort with Lomuto partition, or Selection Sort) disrupt the relative order of duplicate elements and are inherently unstable.")
        }

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            actions = listOf(
                ChatAction.LaunchVisualizer(AlgorithmId.MERGE_SORT, "Merge Sort (Stable)"),
                ChatAction.LaunchVisualizer(AlgorithmId.QUICK_SORT, "Quick Sort (Unstable)")
            ),
            suggestedFollowUps = listOf(
                "Compare Quick Sort vs Merge Sort",
                "Why is Selection Sort unstable?",
                "Explain Insertion Sort"
            )
        )
    }

    private fun getAlgorithmFlowchart(algo: AlgorithmId): String {
        return when (algo) {
            AlgorithmId.BINARY_SEARCH -> """
                ```flowchart
                Start(Sorted Array & Target) --> Mid[Compute mid = (low + high) / 2]
                Mid --> Check{target == arr[mid]?}
                Check -->|Match| Found(Return index mid)
                Check -->|Less| Left[high = mid - 1]
                Check -->|Greater| Right[low = mid + 1]
                Left --> Loop{low <= high?}
                Right --> Loop
                Loop -->|Yes| Mid
                Loop -->|No| Exhausted(Target not found)
                ```
            """.trimIndent()

            AlgorithmId.QUICK_SORT -> """
                ```flowchart
                Start(Subarray low..high) --> Base{low < high?}
                Base -->|Yes| Pivot[Select Pivot Element]
                Pivot --> Partition[Partition array around pivot]
                Partition --> RecurseL[QuickSort Left Subarray]
                RecurseL --> RecurseR[QuickSort Right Subarray]
                RecurseR --> Done(Partition Sorted)
                Base -->|No| Done
                ```
            """.trimIndent()

            AlgorithmId.MERGE_SORT -> """
                ```flowchart
                Start(Input Array) --> Base{Length > 1?}
                Base -->|Yes| Split[Split at Midpoint into Left & Right]
                Split --> RecurseL[MergeSort Left Half]
                RecurseL --> RecurseR[MergeSort Right Half]
                RecurseR --> Merge[Merge sorted halves into buffer]
                Merge --> Done(Array Sorted)
                Base -->|No| Done
                ```
            """.trimIndent()

            AlgorithmId.BUBBLE_SORT -> """
                ```flowchart
                Start(Input Array) --> Outer[Outer Loop pass i = 0..n-1]
                Outer --> Inner[Inner Loop compare arr[j], arr[j+1]]
                Inner --> SwapCheck{arr[j] > arr[j+1]?}
                SwapCheck -->|Yes| Swap[Swap arr[j] and arr[j+1]]
                SwapCheck -->|No| Advance[Advance to next pair]
                Swap --> Advance
                Advance --> LoopCheck{More pairs in pass?}
                LoopCheck -->|Yes| Inner
                LoopCheck -->|No| PassDone{Any swaps made?}
                PassDone -->|Yes| Outer
                PassDone -->|No| Done(Array Fully Sorted)
                ```
            """.trimIndent()

            AlgorithmId.INSERTION_SORT -> """
                ```flowchart
                Start(Input Array) --> Next[Select next unsorted key arr[i]]
                Next --> ShiftCheck{key < arr[j] and j >= 0?}
                ShiftCheck -->|Yes| Shift[Shift arr[j] to arr[j+1]]
                Shift --> Decr[Decrement j]
                Decr --> ShiftCheck
                ShiftCheck -->|No| Insert[Insert key at arr[j+1]]
                Insert --> ArrayCheck{More unsorted keys?}
                ArrayCheck -->|Yes| Next
                ArrayCheck -->|No| Done(Array Fully Sorted)
                ```
            """.trimIndent()

            AlgorithmId.SELECTION_SORT -> """
                ```flowchart
                Start(Input Array) --> Suffix[Scan unsorted suffix i..n-1]
                Suffix --> FindMin[Identify index of minimum element]
                FindMin --> Swap[Swap min element with slot i]
                Swap --> Check{i < n - 1?}
                Check -->|Yes| Suffix
                Check -->|No| Done(Array Fully Sorted)
                ```
            """.trimIndent()

            AlgorithmId.LINEAR_SEARCH -> """
                ```flowchart
                Start(Input Array & Target) --> Init[Set index i = 0]
                Init --> Check{arr[i] == target?}
                Check -->|Yes| Match(Return index i)
                Check -->|No| Inc[Increment i = i + 1]
                Inc --> Bounds{i < length?}
                Bounds -->|Yes| Check
                Bounds -->|No| Exhausted(Return -1)
                ```
            """.trimIndent()

            AlgorithmId.BFS -> """
                ```flowchart
                Start(Root Vertex) --> Init[Enqueue root & mark visited]
                Init --> Loop{Queue is empty?}
                Loop -->|No| Dequeue[Dequeue current vertex]
                Dequeue --> Process[Process / Record vertex]
                Process --> Neighbors[Enqueue unvisited neighbors]
                Neighbors --> Loop
                Loop -->|Yes| Done(Level-Order Traversal Complete)
                ```
            """.trimIndent()

            AlgorithmId.DFS -> """
                ```flowchart
                Start(Root Vertex) --> Init[Push root to Stack & mark visited]
                Init --> Loop{Stack is empty?}
                Loop -->|No| Pop[Pop current vertex]
                Pop --> Process[Process / Record vertex]
                Process --> Neighbors[Push unvisited neighbors to Stack]
                Neighbors --> Loop
                Loop -->|Yes| Done(Exhaustive Traversal Complete)
                ```
            """.trimIndent()

            AlgorithmId.BINARY_SEARCH_TREE -> """
                ```flowchart
                Start(Key Operation) --> Current{curr == null?}
                Current -->|Yes| Leaf[Target Slot Reached]
                Current -->|No| Comp{Compare Key vs curr.val}
                Comp -->|Key < curr| Left[curr = curr.left]
                Comp -->|Key > curr| Right[curr = curr.right]
                Comp -->|Key == curr| Match(Key Found in BST)
                Left --> Current
                Right --> Current
                ```
            """.trimIndent()

            AlgorithmId.HEAP -> """
                ```flowchart
                Start(Heap Operation) --> Root[Access / Replace Root]
                Root --> Check{Child violates Heap Property?}
                Check -->|Yes| Sift[Swap with Dominant Child]
                Sift --> Check
                Check -->|No| Done(Heap Invariant Preserved)
                ```
            """.trimIndent()

            AlgorithmId.STACK -> """
                ```flowchart
                Start(LIFO Operation) --> Op{Push or Pop?}
                Op -->|Push| Full{top >= Capacity - 1?}
                Full -->|Yes| Overflow(Stack Overflow Error)
                Full -->|No| PushVal[arr[++top] = element]
                Op -->|Pop| Empty{top < 0?}
                Empty -->|Yes| Underflow(Stack Underflow Error)
                Empty -->|No| PopVal[return arr[top--]]
                PushVal --> Done(Operation Complete)
                PopVal --> Done
                ```
            """.trimIndent()

            AlgorithmId.QUEUE -> """
                ```flowchart
                Start(FIFO Operation) --> Op{Enqueue or Dequeue?}
                Op -->|Enqueue| Full{size >= Capacity?}
                Full -->|Yes| Overflow(Queue Full Error)
                Full -->|No| AddRear[arr[rear] = element; rear = (rear+1)%cap]
                Op -->|Dequeue| Empty{size == 0?}
                Empty -->|Yes| Underflow(Queue Empty Error)
                Empty -->|No| RemFront[return arr[front]; front = (front+1)%cap]
                AddRear --> Done(Operation Complete)
                RemFront --> Done
                ```
            """.trimIndent()
        }
    }

    private fun generateCatalogueResponse(): ChatMessage {
        val allSpecs = AlgorithmRegistry.allSpecs()
        val content = buildString {
            append("### AVIA VERIFIED ALGORITHM CATALOGUE\n\n")
            append("AVIA currently features **${allSpecs.size} fully verified offline algorithms** across 4 computational paradigms:\n\n")
            append("1. **Sorting (5)**: Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort\n")
            append("2. **Searching (2)**: Linear Search, Binary Search\n")
            append("3. **Data Structures (4)**: Stack (LIFO), Queue (FIFO), Binary Search Tree (BST), Heap\n")
            append("4. **Graph Traversal (2)**: Breadth-First Search (BFS), Depth-First Search (DFS)\n\n")
            append("Tap any algorithm below to launch its visualizer or ask for complexity proofs.")
        }

        val actions = listOf(
            ChatAction.LaunchVisualizer(AlgorithmId.QUICK_SORT, "Quick Sort"),
            ChatAction.LaunchVisualizer(AlgorithmId.BINARY_SEARCH, "Binary Search"),
            ChatAction.LaunchVisualizer(AlgorithmId.BINARY_SEARCH_TREE, "BST")
        )

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            actions = actions,
            suggestedFollowUps = listOf(
                "Compare Quick Sort vs Merge Sort",
                "Explain BFS vs DFS",
                "How does Heap sort work?"
            )
        )
    }

    private fun generateHelpResponse(): ChatMessage {
        val content = buildString {
            append("### AVIA OFFLINE REASONING ENGINE\n\n")
            append("I am AVIA's embedded AI Copilot. I run **100% offline** directly on your device with zero network overhead.\n\n")
            append("**How I Can Assist You:**\n")
            append("• **Complexity Analysis**: Instant Big-O derivations for best, average, and worst cases.\n")
            append("• **Comparative Synthesis**: Side-by-side trade-offs (e.g. `Merge vs Quick`, `BFS vs DFS`).\n")
            append("• **Code Snippets**: Syntactically verified implementations in Kotlin, Python, Java, and C++.\n")
            append("• **Interactive Deep-Links**: Launch visualizers directly from our dialogue stream.\n")
            append("• **Invariants & Edge Cases**: Explanations of pivot selection, stack frames, and tree degradation.")
        }

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            suggestedFollowUps = listOf(
                "Compare Quick Sort vs Merge Sort",
                "Why is Binary Search O(log n)?",
                "When to use BFS vs DFS?"
            )
        )
    }

    private fun generateFallbackResponse(query: String): ChatMessage {
        val content = buildString {
            append("### AVIA COGNITIVE RESOLUTION\n\n")
            append("Analyzing query: *\"$query\"*\n\n")
            append("I specialize in algorithmic analysis, time & space complexities, data structure mechanics, and multi-language implementations for the 13 algorithms in our verified catalogue.\n\n")
            append("Try asking:\n")
            append("• *\"Compare Quick Sort and Merge Sort\"*\n")
            append("• *\"Why is Binary Search O(log n)?\"*\n")
            append("• *\"Show Kotlin code for Insertion Sort\"*\n")
            append("• *\"Explain BST invariant\"*")
        }

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            suggestedFollowUps = listOf(
                "Quick Sort vs Merge Sort",
                "BFS vs DFS",
                "What algorithms are available?"
            )
        )
    }
}
