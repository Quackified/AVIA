package com.example.algolens.data.chat

import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ChatAction
import com.example.algolens.model.chat.ChatMessage
import com.example.algolens.model.chat.ChatSender
import com.example.algolens.model.chat.ProjectConstraintBrief
import com.example.algolens.model.chat.ProjectRecommendationPayload
import com.example.algolens.model.chat.RecommendedAlgorithmCandidate
import com.example.algolens.ui.visualizer.AlgorithmTheoryRepository

/**
 * Deterministic, catalog-grounded algorithm recommender for user project ideas.
 *
 * Every candidate returned by [recommend] or [validateAndGroundCandidate] is
 * verified against [AlgorithmRegistry] and populated with factual complexity,
 * stability, and in-place properties from [AlgorithmTheoryRepository].
 * When a user's project requires an algorithm outside AVIA's 13-algorithm
 * catalog (such as Dijkstra/A* weighted shortest path, Trie/KMP string search,
 * or Dynamic Programming), an explicit [ProjectRecommendationPayload.outOfCatalogNotice]
 * is produced instead of inventing unsupported algorithms.
 */
object ProjectAlgorithmRecommender {

    /**
     * Validates a candidate against [AlgorithmRegistry] and overwrites its
     * complexity/stability/in-place properties with the canonical truth from
     * [AlgorithmTheoryRepository]. Returns `null` if the [AlgorithmId] is not
     * registered in [AlgorithmRegistry].
     */
    fun validateAndGroundCandidate(
        id: AlgorithmId,
        fitReason: String,
        tradeOffs: String
    ): RecommendedAlgorithmCandidate? {
        val spec = AlgorithmRegistry.specFor(id) ?: return null
        val theory = AlgorithmTheoryRepository.getTheory(spec.id.displayName)
        return RecommendedAlgorithmCandidate(
            algorithmId = spec.id,
            displayName = spec.id.displayName,
            fitReason = fitReason,
            bestCase = theory.bestCase,
            averageCase = theory.averageCase,
            worstCase = theory.worstCase,
            spaceComplexity = theory.spaceComplexity,
            isStable = theory.isStable,
            isInPlace = theory.isInPlace,
            tradeOffs = tradeOffs
        )
    }

    /**
     * Detects whether a natural-language chat query is asking for project/use-case
     * algorithm advice.
     */
    fun isProjectAdviceQuery(query: String): Boolean {
        val lower = query.lowercase()
        return lower.contains("my project") ||
            lower.contains("recommend") ||
            lower.contains("which algorithm should i use") ||
            lower.contains("what algorithm for") ||
            lower.contains("building a ") ||
            lower.contains("building an ") ||
            lower.contains("plan an algorithm") ||
            lower.contains("best algorithm for") ||
            lower.contains("suitable algorithm")
    }

    /**
     * Infers a [ProjectConstraintBrief] from a free-form natural-language query
     * and generates a grounded [ProjectRecommendationPayload].
     */
    fun recommendFromNaturalLanguage(rawQuery: String): ProjectRecommendationPayload {
        val lower = rawQuery.lowercase()
        val dataScale = when {
            lower.contains("million") || lower.contains("100k") || lower.contains("large") || lower.contains("huge") ->
                ProjectConstraintBrief.DataScale.LARGE_100K_PLUS
            lower.contains("small") || lower.contains("tiny") || lower.contains("few") || lower.contains("under 50") ->
                ProjectConstraintBrief.DataScale.SMALL_UNDER_64
            lower.contains("thousand") || lower.contains("medium") ->
                ProjectConstraintBrief.DataScale.MEDIUM_1K
            else -> ProjectConstraintBrief.DataScale.UNSPECIFIED
        }

        val inputOrder = when {
            lower.contains("nearly sorted") || lower.contains("almost sorted") ->
                ProjectConstraintBrief.InputOrder.NEARLY_SORTED
            lower.contains("already sorted") || lower.contains("sorted array") ->
                ProjectConstraintBrief.InputOrder.ALREADY_SORTED
            lower.contains("undo") || lower.contains("redo") || lower.contains("lifo") ||
                lower.contains("fifo") || lower.contains("task queue") || lower.contains("job queue") ->
                ProjectConstraintBrief.InputOrder.LIFO_FIFO_STREAM
            lower.contains("graph") || lower.contains("network") || lower.contains("grid") ||
                lower.contains("maze") || lower.contains("map") || lower.contains("route") || lower.contains("path") ->
                ProjectConstraintBrief.InputOrder.TREE_OR_GRAPH
            else -> ProjectConstraintBrief.InputOrder.UNSPECIFIED
        }

        val graphGoal = when {
            lower.contains("weighted") || lower.contains("dijkstra") || lower.contains("bellman") || lower.contains("floyd") || lower.contains("a*") ||
                lower.contains("gps") || lower.contains("traffic") || lower.contains("road cost") ->
                ProjectConstraintBrief.GraphGoal.WEIGHTED_SHORTEST_PATH
            lower.contains("shortest") || lower.contains("minimum hops") || lower.contains("unweighted") ||
                lower.contains("degrees of separation") ->
                ProjectConstraintBrief.GraphGoal.SHORTEST_UNWEIGHTED_PATH
            lower.contains("cycle") || lower.contains("maze") || lower.contains("topological") ||
                lower.contains("dependency") || lower.contains("backtrack") ->
                ProjectConstraintBrief.GraphGoal.EXHAUSTIVE_OR_CYCLE
            else -> ProjectConstraintBrief.GraphGoal.NONE
        }

        val brief = ProjectConstraintBrief(
            projectGoal = rawQuery.trim(),
            dataScale = dataScale,
            inputOrder = inputOrder,
            requiresStability = lower.contains("stable") || lower.contains("preserve order") || lower.contains("multi-key"),
            strictMemoryInPlace = lower.contains("in-place") || lower.contains("low memory") || lower.contains("embedded") || lower.contains("o(1) space"),
            frequentUpdates = lower.contains("dynamic") || lower.contains("insert") || lower.contains("live leaderboard") || lower.contains("priority"),
            graphGoal = graphGoal
        )

        return recommend(brief)
    }

    /**
     * Produces a structured, catalog-validated [ProjectRecommendationPayload] from [brief].
     */
    fun recommend(brief: ProjectConstraintBrief): ProjectRecommendationPayload {
        val goalLower = brief.projectGoal.lowercase()
        val candidates = mutableListOf<RecommendedAlgorithmCandidate>()
        val assumptions = mutableListOf<String>()
        val alternatives = mutableListOf<String>()
        var outOfCatalogNotice: String? = null

        // 1. Detect Out-of-Catalog domains first for strict honesty
        val needsWeightedShortestPath = brief.graphGoal == ProjectConstraintBrief.GraphGoal.WEIGHTED_SHORTEST_PATH ||
            goalLower.contains("dijkstra") || goalLower.contains("bellman") || goalLower.contains("floyd") || goalLower.contains("a*") ||
            (goalLower.contains("weighted") && (goalLower.contains("shortest") || goalLower.contains("gps") || goalLower.contains("route")))
        val needsStringTrieOrKmp = goalLower.contains("autocomplete") || goalLower.contains("trie") ||
            goalLower.contains("regex") || goalLower.contains("substring") || goalLower.contains("kmp")
        val needsDynamicProgramming = goalLower.contains("knapsack") || goalLower.contains("dynamic programming") ||
            goalLower.contains("edit distance") || goalLower.contains("longest common")
        val needsCryptoOrMl = goalLower.contains("neural network") || goalLower.contains("machine learning") ||
            goalLower.contains("encryption") || goalLower.contains("hash table") || goalLower.contains("cryptography")

        if (needsWeightedShortestPath) {
            val isOutOfCatalogSpecific = goalLower.contains("bellman") || goalLower.contains("floyd")
            if (isOutOfCatalogSpecific) {
                outOfCatalogNotice =
                    "Out-of-Catalog Notice: Negative-weight or all-pairs algorithms (such as Bellman-Ford or Floyd-Warshall) are outside AVIA's 14-algorithm catalog. " +
                        "We recommend Dijkstra's Shortest Path as the foundational non-negative single-source shortest path engine in our verified catalog."
            }
            validateAndGroundCandidate(
                id = AlgorithmId.DIJKSTRA,
                fitReason = "Solves single-source shortest paths on non-negative weighted graphs in O((V + E) log V) time using Min-Priority Queue relaxation.",
                tradeOffs = "Requires non-negative edge weights (w >= 0). For negative weights, use Bellman-Ford."
            )?.let { candidates.add(it) }
            validateAndGroundCandidate(
                id = AlgorithmId.BFS,
                fitReason = "Alternative when all edge weights are uniform (unweighted hop count) in O(V + E) time.",
                tradeOffs = "Does not respect non-uniform edge weights."
            )?.let { candidates.add(it) }
            assumptions.add("Your graph has non-negative edge weights (w >= 0).")
            alternatives.add("For heuristic-guided point-to-point pathfinding, upgrade to A* search with Euclidean/Manhattan distance.")
        } else if (needsStringTrieOrKmp) {
            outOfCatalogNotice =
                "Out-of-Catalog Notice: Specialized string/prefix structures (Trie, KMP, Suffix Array) are outside AVIA's 14-algorithm catalog. " +
                    "We recommend Binary Search (over a sorted dictionary array) or Binary Search Tree (for dynamic ordered keys) as the closest catalog foundations."
            validateAndGroundCandidate(
                id = AlgorithmId.BINARY_SEARCH,
                fitReason = "Supports fast O(log n) prefix/range bounds over a static sorted word list.",
                tradeOffs = "Requires a pre-sorted array; inserting new words takes O(n) array shifting."
            )?.let { candidates.add(it) }
            validateAndGroundCandidate(
                id = AlgorithmId.BINARY_SEARCH_TREE,
                fitReason = "Maintains ordered keys dynamically with hierarchical lookup and insertion.",
                tradeOffs = "Unbalanced insertion order can degrade height toward O(n)."
            )?.let { candidates.add(it) }
            assumptions.add("Lexicographical ordering can substitute for character-by-character prefix nodes.")
            alternatives.add("For high-throughput prefix autocomplete, pair a Trie (prefix tree) with a Heap for top-K ranking.")
        } else if (needsDynamicProgramming || needsCryptoOrMl) {
            outOfCatalogNotice =
                "Out-of-Catalog Notice: This domain (${if (needsDynamicProgramming) "Dynamic Programming / Memoization" else "Machine Learning / Cryptography / Hashing"}) is outside AVIA's 14-algorithm catalog, which focuses on canonical sorting, searching, linear buffers, trees, and graph traversals."
            validateAndGroundCandidate(
                id = AlgorithmId.DFS,
                fitReason = "Demonstrates recursive state-space exploration and call-stack backtracking.",
                tradeOffs = "Without memoization or pruning, exhaustive branch exploration can be exponential."
            )?.let { candidates.add(it) }
            validateAndGroundCandidate(
                id = AlgorithmId.HEAP,
                fitReason = "Useful for tracking top-K candidate states or priority-bounded search.",
                tradeOffs = "Maintains partial order (extremum at root) rather than full key lookup."
            )?.let { candidates.add(it) }
            assumptions.add("Showing the closest state-traversal and priority primitives available in the 14-algorithm catalog.")
            alternatives.add("Consider dedicated tabular DP or hash-indexed structures in your target language standard library.")
        }

        // 2. Graph & Tree problems (when not already handled by out-of-catalog branch)
        if (candidates.isEmpty() && (
                brief.inputOrder == ProjectConstraintBrief.InputOrder.TREE_OR_GRAPH ||
                    brief.graphGoal != ProjectConstraintBrief.GraphGoal.NONE ||
                    goalLower.contains("graph") || goalLower.contains("tree") ||
                    goalLower.contains("bfs") || goalLower.contains("dfs") ||
                    goalLower.contains("maze") || goalLower.contains("dependency")
                )
        ) {
            if (brief.graphGoal == ProjectConstraintBrief.GraphGoal.EXHAUSTIVE_OR_CYCLE ||
                goalLower.contains("cycle") || goalLower.contains("maze") || goalLower.contains("dependency")
            ) {
                validateAndGroundCandidate(
                    id = AlgorithmId.DFS,
                    fitReason = "Explores deep paths and call-stack back-edges directly, making cycle detection, maze solving, and dependency walks straightforward.",
                    tradeOffs = "Does not guarantee the shortest path between two vertices."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.BFS,
                    fitReason = "Alternative level-order traversal if you also need minimum-hop distance from the source.",
                    tradeOffs = "Higher peak queue memory on wide branching graphs."
                )?.let { candidates.add(it) }
            } else {
                validateAndGroundCandidate(
                    id = AlgorithmId.BFS,
                    fitReason = "Explores vertices layer-by-layer via a FIFO Queue, guaranteeing the shortest path on unweighted graphs.",
                    tradeOffs = "Frontier queue can hold O(V) vertices on dense or wide graphs."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.DFS,
                    fitReason = "Lower memory footprint on deep, narrow graphs and naturally supports recursive backtracking.",
                    tradeOffs = "First path found is not guaranteed to be shortest."
                )?.let { candidates.add(it) }
            }
            assumptions.add("Graph edges are unweighted (or uniform cost) and represented as an adjacency list.")
            alternatives.add("If hierarchical key ordering is needed instead of general graph edges, use Binary Search Tree (BST).")
        }

        // 3. LIFO / FIFO / Priority / Dynamic Updates
        if (candidates.isEmpty() && (
                brief.inputOrder == ProjectConstraintBrief.InputOrder.LIFO_FIFO_STREAM ||
                    goalLower.contains("undo") || goalLower.contains("stack") ||
                    goalLower.contains("queue") || goalLower.contains("buffer") ||
                    goalLower.contains("scheduler") || goalLower.contains("leaderboard") ||
                    goalLower.contains("top k") || goalLower.contains("priority")
                )
        ) {
            if (goalLower.contains("undo") || goalLower.contains("back button") || goalLower.contains("lifo") || goalLower.contains("parser")) {
                validateAndGroundCandidate(
                    id = AlgorithmId.STACK,
                    fitReason = "LIFO (Last-In, First-Out) container gives O(1) push/pop for undo history, navigation back-stacks, and expression evaluation.",
                    tradeOffs = "Only the top element is immediately accessible; no random key lookup."
                )?.let { candidates.add(it) }
            }
            if (goalLower.contains("queue") || goalLower.contains("fifo") || goalLower.contains("producer") || goalLower.contains("message") || candidates.isEmpty()) {
                validateAndGroundCandidate(
                    id = AlgorithmId.QUEUE,
                    fitReason = "FIFO (First-In, First-Out) buffer preserves arrival fairness with O(1) enqueue and dequeue.",
                    tradeOffs = "Cannot prioritize urgent items ahead of earlier arrivals."
                )?.let { candidates.add(it) }
            }
            if (goalLower.contains("priority") || goalLower.contains("scheduler") || goalLower.contains("leaderboard") || goalLower.contains("top") || brief.frequentUpdates) {
                validateAndGroundCandidate(
                    id = AlgorithmId.HEAP,
                    fitReason = "Maintains the max/min priority item at index 0 in O(1) peek and O(log n) insert/extract-max without re-sorting the full dataset.",
                    tradeOffs = "Searching for an arbitrary non-root element still takes O(n)."
                )?.let { candidates.add(it) }
            }
            assumptions.add("Operations arrive incrementally as a live stream rather than a single batch array.")
            alternatives.add("Use Binary Search Tree (BST) if you need both dynamic insertions and full sorted range traversal.")
        }

        // 4. Lookup / Search problems
        if (candidates.isEmpty() && (
                goalLower.contains("search") || goalLower.contains("lookup") ||
                    goalLower.contains("find") || brief.inputOrder == ProjectConstraintBrief.InputOrder.ALREADY_SORTED
                ) && !goalLower.contains("sort")
        ) {
            if (brief.frequentUpdates) {
                validateAndGroundCandidate(
                    id = AlgorithmId.BINARY_SEARCH_TREE,
                    fitReason = "Supports average O(log n) lookup while allowing dynamic key insertions without shifting contiguous array elements.",
                    tradeOffs = "Can degrade to O(n) if keys are inserted in strictly sorted order without balancing."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.BINARY_SEARCH,
                    fitReason = "Zero pointer overhead and optimal cache locality when updates are infrequent.",
                    tradeOffs = "Requires maintaining a sorted array."
                )?.let { candidates.add(it) }
            } else if (brief.dataScale == ProjectConstraintBrief.DataScale.SMALL_UNDER_64 &&
                brief.inputOrder == ProjectConstraintBrief.InputOrder.RANDOM_UNSORTED
            ) {
                validateAndGroundCandidate(
                    id = AlgorithmId.LINEAR_SEARCH,
                    fitReason = "Scans unsorted small arrays directly in O(n) without paying the O(n log n) upfront cost of sorting.",
                    tradeOffs = "Scales poorly once dataset size grows or repeated queries are issued."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.BINARY_SEARCH,
                    fitReason = "Once sorted, reduces repeated lookups to O(log n) comparisons.",
                    tradeOffs = "Requires the input array to be sorted first."
                )?.let { candidates.add(it) }
            } else {
                validateAndGroundCandidate(
                    id = AlgorithmId.BINARY_SEARCH,
                    fitReason = "Halves the search interval at every step for O(log n) lookup time and O(1) auxiliary space.",
                    tradeOffs = "Requires random-access memory (contiguous array) sorted by the search key."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.BINARY_SEARCH_TREE,
                    fitReason = "Strong alternative when keys are inserted dynamically between lookups.",
                    tradeOffs = "Higher per-node memory overhead than a flat array."
                )?.let { candidates.add(it) }
            }
            assumptions.add("Keys are comparable with a total ordering.")
            alternatives.add("If the dataset is unsorted and queried only once, Linear Search avoids sorting overhead.")
        }

        // 5. Sorting & General Default (handles constraints: stability, memory, scale, nearly-sorted)
        if (candidates.isEmpty()) {
            if (brief.inputOrder == ProjectConstraintBrief.InputOrder.NEARLY_SORTED ||
                brief.dataScale == ProjectConstraintBrief.DataScale.SMALL_UNDER_64
            ) {
                validateAndGroundCandidate(
                    id = AlgorithmId.INSERTION_SORT,
                    fitReason = "Runs in near-linear O(n) time on nearly-sorted inputs, is stable, and operates in-place with O(1) extra memory and minimal overhead on small arrays.",
                    tradeOffs = "Degrades to O(n²) on large reverse-ordered or random datasets."
                )?.let { candidates.add(it) }
            }

            if (brief.requiresStability) {
                validateAndGroundCandidate(
                    id = AlgorithmId.MERGE_SORT,
                    fitReason = "Guarantees O(n log n) worst-case time complexity and preserves the relative order of equal keys (stable).",
                    tradeOffs = "Allocates O(n) auxiliary merge buffer space."
                )?.let { candidates.add(it) }
                if (candidates.none { it.algorithmId == AlgorithmId.INSERTION_SORT }) {
                    validateAndGroundCandidate(
                        id = AlgorithmId.INSERTION_SORT,
                        fitReason = "In-place O(1) stable alternative when the array is small (N < 64) or nearly sorted.",
                        tradeOffs = "O(n²) average/worst time on large random inputs."
                    )?.let { candidates.add(it) }
                }
            } else if (brief.strictMemoryInPlace) {
                validateAndGroundCandidate(
                    id = AlgorithmId.QUICK_SORT,
                    fitReason = "Delivers fast average O(n log n) throughput with high cache locality and in-place partitioning (O(log n) call stack).",
                    tradeOffs = "Unstable and can hit O(n²) worst-case on adversarial pivots without randomized/median-of-three selection."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.HEAP,
                    fitReason = "Guarantees O(n log n) worst-case time with O(1) auxiliary space via in-place max-heap extraction.",
                    tradeOffs = "Unstable and exhibits poorer CPU cache locality than Quick Sort."
                )?.let { candidates.add(it) }
            } else {
                validateAndGroundCandidate(
                    id = AlgorithmId.QUICK_SORT,
                    fitReason = "General-purpose high-throughput O(n log n) sort with contiguous cache-friendly memory access.",
                    tradeOffs = "Unstable; use Merge Sort if equal-key order must be preserved."
                )?.let { candidates.add(it) }
                validateAndGroundCandidate(
                    id = AlgorithmId.MERGE_SORT,
                    fitReason = "Predictable O(n log n) worst-case bound and guaranteed stability for multi-key records.",
                    tradeOffs = "Requires O(n) auxiliary buffer memory."
                )?.let { candidates.add(it) }
            }

            if (brief.dataScale == ProjectConstraintBrief.DataScale.UNSPECIFIED) {
                assumptions.add("Dataset size was not specified; prioritized O(n log n) algorithms that scale safely from hundreds to millions of items.")
            }
            if (!brief.requiresStability) {
                assumptions.add("Stability was not marked mandatory; included both in-place Quick Sort and stable Merge Sort so you can weigh memory vs stability.")
            }
            alternatives.add("Use Insertion Sort for small batches (N < 64) or nearly-sorted streams, or Heap when only the top-K items are needed.")
        }

        val summary = if (brief.projectGoal.isBlank()) {
            "Project Algorithm Recommendation (General Purpose)"
        } else {
            "Project Brief: \"${brief.projectGoal.take(90)}\""
        }

        return ProjectRecommendationPayload(
            projectSummary = summary,
            candidates = candidates.distinctBy { it.algorithmId }.take(3),
            assumptions = assumptions,
            alternatives = alternatives,
            outOfCatalogNotice = outOfCatalogNotice
        )
    }

    /**
     * Converts a [ProjectRecommendationPayload] into an interactive [ChatMessage]
     * with verified visualizer links and follow-up suggestions.
     */
    fun toChatMessage(payload: ProjectRecommendationPayload): ChatMessage {
        val content = buildString {
            append("### PROJECT ALGORITHM RECOMMENDATION\n\n")
            append("**${payload.projectSummary}**\n\n")
            if (payload.outOfCatalogNotice != null) {
                append("> [!WARNING]\n")
                append("> ${payload.outOfCatalogNotice}\n\n")
            }
            append("Evaluated against AVIA's 14-algorithm verified registry. See the structured candidates, complexity trade-offs, and assumptions below.")
        }

        val actions = payload.candidates.map { candidate ->
            ChatAction.LaunchVisualizer(
                algorithmId = candidate.algorithmId,
                displayName = candidate.displayName
            )
        }

        val followUps = payload.candidates.take(2).map { "Show Kotlin code for ${it.displayName}" } +
            listOf("Compare ${payload.candidates.firstOrNull()?.displayName ?: "Quick Sort"} vs ${payload.candidates.getOrNull(1)?.displayName ?: "Merge Sort"}")

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            content = content,
            referencedAlgorithm = payload.candidates.firstOrNull()?.algorithmId,
            projectRecommendation = payload,
            actions = actions,
            suggestedFollowUps = followUps
        )
    }
}
