package com.avia.data.practice

import com.avia.model.AlgorithmId
import com.avia.model.practice.PracticeDifficulty
import com.avia.model.practice.PracticeGraphEdge
import com.avia.model.practice.PracticeGraphNode
import com.avia.model.practice.PracticeOption
import com.avia.model.practice.PracticeQuestion
import com.avia.model.practice.PracticeSnapshot
import com.avia.ui.visualizer.ElementState

/**
 * Repository containing curated practice challenges and quiz questions for AlgoLens.
 *
 * Questions cover all 14 algorithms grouped into 4 core categories:
 *  - Sorting (Bubble, Selection, Insertion, Merge, Quick)
 *  - Searching (Linear, Binary)
 *  - Data Structures (Stack, Queue, BST, Heap)
 *  - Graph Traversal & Shortest Path (BFS, DFS, Dijkstra)
 *
 * Each question features:
 *  - Difficulty tier: Easy (fundamentals, complexities, properties), Medium (step calculations,
 *    partitions, traversals), Hard (edge cases, invariants, stability).
 *  - Authentic visual snapshot of the data structure (1D array, buffer, or 2D graph).
 *  - Educational explanations for instant feedback.
 */
object PracticeQuestionRepository {

    val categories: List<String> = listOf(
        "All",
        "Sorting",
        "Searching",
        "Data Structures",
        "Graph Traversal"
    )

    private val questions: List<PracticeQuestion> = listOf(
        // ====================================================================
        //  SORTING — BUBBLE SORT
        // ====================================================================
        PracticeQuestion(
            id = "sort_bubble_1",
            algorithmId = AlgorithmId.BUBBLE_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "COMPLEXITY · BUBBLE SORT",
            promptText = "What is the worst-case and average-case time complexity of standard Bubble Sort?",
            explanation = "Bubble Sort repeatedly steps through the list, comparing adjacent pairs. In the worst and average cases, it performs n*(n-1)/2 comparisons, resulting in O(n²) time complexity with O(1) auxiliary space.",
            options = listOf(
                PracticeOption(0, "O(n log n)", false),
                PracticeOption(1, "O(n²)", true),
                PracticeOption(2, "O(n)", false),
                PracticeOption(3, "O(log n)", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(64, 34, 25, 12, 22, 11, 90),
                elementStates = mapOf(0 to ElementState.COMPARING, 1 to ElementState.COMPARING),
                topPointers = mapOf("L" to 0, "R" to 1)
            )
        ),
        PracticeQuestion(
            id = "sort_bubble_2",
            algorithmId = AlgorithmId.BUBBLE_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "STEP 1 · BUBBLE SORT PASS",
            promptText = "Comparing index 0 (64) and index 1 (34): what action is taken in the next step?",
            explanation = "Because 64 > 34, the elements are out of order for ascending sort. Bubble Sort swaps them, bringing the smaller element (34) forward and bubbling 64 upward.",
            options = listOf(
                PracticeOption(0, "Swap 64 and 34", true),
                PracticeOption(1, "Leave them as-is and increment pointer", false),
                PracticeOption(2, "Terminate the algorithm", false),
                PracticeOption(3, "Shift all remaining elements left", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(64, 34, 25, 12, 22, 11, 90),
                elementStates = mapOf(0 to ElementState.COMPARING, 1 to ElementState.COMPARING),
                bottomPointers = mapOf("i" to 0, "j" to 1)
            )
        ),
        PracticeQuestion(
            id = "sort_bubble_3",
            algorithmId = AlgorithmId.BUBBLE_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.HARD,
            stepTitle = "OPTIMIZATION · BUBBLE SORT",
            promptText = "Which optimization allows Bubble Sort to achieve O(n) best-case time complexity?",
            explanation = "By introducing a boolean flag ('isSwapped') that tracks whether any swaps occurred during a pass, the algorithm can terminate early after just 1 pass (n-1 comparisons) if the array is already sorted.",
            options = listOf(
                PracticeOption(0, "Using binary search to find insertion points", false),
                PracticeOption(1, "Tracking if any swaps occurred during a pass", true),
                PracticeOption(2, "Sorting from both ends simultaneously", false),
                PracticeOption(3, "Allocating an auxiliary array of size n", false)
            )
        ),

        // ====================================================================
        //  SORTING — SELECTION SORT
        // ====================================================================
        PracticeQuestion(
            id = "sort_selection_1",
            algorithmId = AlgorithmId.SELECTION_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "COMPLEXITY · SELECTION SORT",
            promptText = "What is the auxiliary space complexity of Selection Sort?",
            explanation = "Selection Sort performs in-place sorting by repeatedly selecting the minimum element and swapping it into the prefix. It only uses a constant number of pointer variables, making space complexity O(1).",
            options = listOf(
                PracticeOption(0, "O(1) Auxiliary Space", true),
                PracticeOption(1, "O(n) Auxiliary Space", false),
                PracticeOption(2, "O(log n) Auxiliary Space", false),
                PracticeOption(3, "O(n²) Auxiliary Space", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(11, 12, 22, 64, 25),
                elementStates = mapOf(0 to ElementState.SORTED, 1 to ElementState.SORTED),
                topPointers = mapOf("SORTED" to 1)
            )
        ),
        PracticeQuestion(
            id = "sort_selection_2",
            algorithmId = AlgorithmId.SELECTION_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "STEP 3 · SELECTION MINIMUM SCAN",
            promptText = "In the unsorted range [64, 25, 34], which value is selected to swap with index 2 (64)?",
            explanation = "Selection Sort scans the entire unsorted region [64, 25, 34] and finds 25 as the smallest element. It then swaps 25 into slot 2, expanding the sorted prefix.",
            options = listOf(
                PracticeOption(0, "25 (Index 3)", true),
                PracticeOption(1, "34 (Index 4)", false),
                PracticeOption(2, "64 remains in place", false),
                PracticeOption(3, "11 (Index 0)", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(11, 12, 64, 25, 34),
                elementStates = mapOf(
                    0 to ElementState.SORTED,
                    1 to ElementState.SORTED,
                    2 to ElementState.ACTIVE,
                    3 to ElementState.TARGET
                ),
                topPointers = mapOf("MIN" to 3),
                bottomPointers = mapOf("target" to 2)
            )
        ),
        PracticeQuestion(
            id = "sort_selection_3",
            algorithmId = AlgorithmId.SELECTION_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.HARD,
            stepTitle = "STABILITY · SELECTION SORT",
            promptText = "Why is standard array-based Selection Sort NOT a stable sort?",
            explanation = "Selection Sort swaps the minimum element across long distances. For example, in [4a, 4b, 2], swapping 2 with 4a moves 4a past 4b, yielding [2, 4b, 4a] and reversing the relative order of identical elements.",
            options = listOf(
                PracticeOption(0, "Long-distance swaps can reorder identical elements", true),
                PracticeOption(1, "It uses a divide-and-conquer partition", false),
                PracticeOption(2, "It requires non-contiguous memory access", false),
                PracticeOption(3, "The time complexity is non-deterministic", false)
            )
        ),

        // ====================================================================
        //  SORTING — INSERTION SORT
        // ====================================================================
        PracticeQuestion(
            id = "sort_insertion_1",
            algorithmId = AlgorithmId.INSERTION_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "BEST CASE · INSERTION SORT",
            promptText = "What is the time complexity of Insertion Sort when the input array is ALREADY sorted?",
            explanation = "When the array is already sorted, each element only needs 1 comparison against its predecessor (with zero shifts), resulting in an optimal O(n) linear run-time.",
            options = listOf(
                PracticeOption(0, "O(n)", true),
                PracticeOption(1, "O(n log n)", false),
                PracticeOption(2, "O(n²)", false),
                PracticeOption(3, "O(1)", false)
            )
        ),
        PracticeQuestion(
            id = "sort_insertion_2",
            algorithmId = AlgorithmId.INSERTION_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "STEP 4 · INSERTION KEY SHIFT",
            promptText = "With sorted prefix [12, 34, 45, 89] and elevated key 23, how many elements must shift right?",
            explanation = "Key 23 is compared from right to left against the sorted prefix: 89 > 23 (shift), 45 > 23 (shift), 34 > 23 (shift), but 12 < 23 (stop). Exactly 3 elements shift right to open slot 1.",
            options = listOf(
                PracticeOption(0, "1 element (89)", false),
                PracticeOption(1, "2 elements (89, 45)", false),
                PracticeOption(2, "3 elements (89, 45, 34)", true),
                PracticeOption(3, "4 elements (all of them)", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(12, 34, 45, 89, 23),
                elementStates = mapOf(
                    0 to ElementState.SORTED,
                    1 to ElementState.SORTED,
                    2 to ElementState.SORTED,
                    3 to ElementState.SORTED,
                    4 to ElementState.ACTIVE
                ),
                topPointers = mapOf("KEY" to 4)
            )
        ),

        // ====================================================================
        //  SORTING — MERGE SORT
        // ====================================================================
        PracticeQuestion(
            id = "sort_merge_1",
            algorithmId = AlgorithmId.MERGE_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "COMPLEXITY · MERGE SORT",
            promptText = "What is the guaranteed worst-case time complexity of Merge Sort?",
            explanation = "Merge Sort divides the array into halves recursively (depth log n) and merges them in linear O(n) time at each level. Its worst, average, and best-case time complexities are all strictly O(n log n).",
            options = listOf(
                PracticeOption(0, "O(n log n)", true),
                PracticeOption(1, "O(n²)", false),
                PracticeOption(2, "O(n)", false),
                PracticeOption(3, "O(log n)", false)
            )
        ),
        PracticeQuestion(
            id = "sort_merge_2",
            algorithmId = AlgorithmId.MERGE_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "MERGE STEP · TWO POINTERS",
            promptText = "Merging [12, 35] and [18, 42]: which element is placed into the buffer first?",
            explanation = "The merge step compares the heads of the two sorted halves: 12 vs 18. Since 12 <= 18, 12 is placed into the output buffer first, and the left pointer advances.",
            options = listOf(
                PracticeOption(0, "12 (from left half)", true),
                PracticeOption(1, "18 (from right half)", false),
                PracticeOption(2, "35 (from left half)", false),
                PracticeOption(3, "42 (from right half)", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(12, 35, 18, 42),
                elementStates = mapOf(0 to ElementState.COMPARING, 2 to ElementState.COMPARING),
                topPointers = mapOf("L" to 0, "R" to 2)
            )
        ),

        // ====================================================================
        //  SORTING — QUICK SORT
        // ====================================================================
        PracticeQuestion(
            id = "sort_quick_1",
            algorithmId = AlgorithmId.QUICK_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "WORST CASE · QUICK SORT",
            promptText = "Under what condition does standard QuickSort degrade to its worst-case O(n²) time complexity?",
            explanation = "When the pivot chosen is consistently the smallest or largest element (such as choosing the last element on an already sorted or reverse-sorted array without randomisation), the partition splits into sizes 0 and n-1, leading to O(n²) time.",
            options = listOf(
                PracticeOption(0, "When the pivot is consistently extreme (smallest/largest)", true),
                PracticeOption(1, "When all elements are distinct integers", false),
                PracticeOption(2, "When the array size is a power of 2", false),
                PracticeOption(3, "When running on a multi-core processor", false)
            )
        ),
        PracticeQuestion(
            id = "sort_quick_2",
            algorithmId = AlgorithmId.QUICK_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "STEP 4 · QUICKSORT PARTITION",
            promptText = "With array [12, 45, 23, 89, 34] and pivot 89: which element is swapped with pivot (89) to place it?",
            explanation = "Index 4 (value 34) is the final element in the scan. Swapping 34 with pivot 89 places 89 into its correct sorted partition boundary.",
            options = listOf(
                PracticeOption(0, "Index 1 (Value: 45)", false),
                PracticeOption(1, "Index 4 (Value: 34)", true),
                PracticeOption(2, "Index 2 (Value: 23)", false),
                PracticeOption(3, "Index 0 (Value: 12)", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(12, 45, 23, 89, 34),
                elementStates = mapOf(
                    1 to ElementState.COMPARING,
                    3 to ElementState.PIVOT,
                    4 to ElementState.COMPARING
                ),
                topPointers = mapOf("PIVOT" to 3),
                bottomPointers = mapOf("L" to 1, "R" to 4)
            )
        ),
        PracticeQuestion(
            id = "sort_quick_3",
            algorithmId = AlgorithmId.QUICK_SORT,
            category = "Sorting",
            difficulty = PracticeDifficulty.HARD,
            stepTitle = "INVARIANT · QUICK SORT",
            promptText = "Where is the pivot element guaranteed to be located once its partition step completes?",
            explanation = "The fundamental QuickSort invariant guarantees that after partition, the pivot sits at its final, permanent sorted index. All elements to its left are <= pivot and all elements to its right are >= pivot.",
            options = listOf(
                PracticeOption(0, "In its final, permanent sorted position", true),
                PracticeOption(1, "Always at index 0", false),
                PracticeOption(2, "Always at the rightmost index (n-1)", false),
                PracticeOption(3, "In a temporary auxiliary memory buffer", false)
            )
        ),

        // ====================================================================
        //  SEARCHING — LINEAR SEARCH
        // ====================================================================
        PracticeQuestion(
            id = "search_linear_1",
            algorithmId = AlgorithmId.LINEAR_SEARCH,
            category = "Searching",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "COMPLEXITY · LINEAR SEARCH",
            promptText = "What is the worst-case number of comparisons for Linear Search on an array of size n?",
            explanation = "If the target element is at the very last index or not present at all, Linear Search must inspect every single element, making exactly n comparisons (O(n) time).",
            options = listOf(
                PracticeOption(0, "n comparisons", true),
                PracticeOption(1, "log₂ n comparisons", false),
                PracticeOption(2, "n² comparisons", false),
                PracticeOption(3, "1 comparison", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(14, 28, 42, 56, 70),
                elementStates = mapOf(0 to ElementState.ACTIVE),
                bottomPointers = mapOf("i" to 0)
            )
        ),
        PracticeQuestion(
            id = "search_linear_2",
            algorithmId = AlgorithmId.LINEAR_SEARCH,
            category = "Searching",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "STEP 3 · LINEAR TARGET MATCH",
            promptText = "Searching for target 42 in [14, 28, 42, 56, 70]: what happens at index 2?",
            explanation = "At index 2, array[2] == 42 matches the search target. The algorithm immediately flags the element as FOUND and terminates with index 2.",
            options = listOf(
                PracticeOption(0, "Match found: algorithm terminates returning index 2", true),
                PracticeOption(1, "Mismatch: continues scanning index 3", false),
                PracticeOption(2, "Divides array into two halves", false),
                PracticeOption(3, "Swaps 42 with index 0", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(14, 28, 42, 56, 70),
                elementStates = mapOf(
                    0 to ElementState.IDLE,
                    1 to ElementState.IDLE,
                    2 to ElementState.FOUND
                ),
                topPointers = mapOf("TARGET" to 2),
                bottomPointers = mapOf("i" to 2)
            )
        ),

        // ====================================================================
        //  SEARCHING — BINARY SEARCH
        // ====================================================================
        PracticeQuestion(
            id = "search_binary_1",
            algorithmId = AlgorithmId.BINARY_SEARCH,
            category = "Searching",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "PREREQUISITE · BINARY SEARCH",
            promptText = "What essential condition MUST an array satisfy before Binary Search can be applied?",
            explanation = "Binary Search relies on the property that elements are in sorted order. Without sorted order, discarding half of the search space based on midpoint comparison is invalid.",
            options = listOf(
                PracticeOption(0, "The array elements must be sorted", true),
                PracticeOption(1, "The array size must be an even number", false),
                PracticeOption(2, "All values must be positive integers", false),
                PracticeOption(3, "The array must have no duplicate values", false)
            )
        ),
        PracticeQuestion(
            id = "search_binary_2",
            algorithmId = AlgorithmId.BINARY_SEARCH,
            category = "Searching",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "STEP 1 · MIDPOINT CALCULATION",
            promptText = "In sorted array [10, 20, 30, 40, 50, 60, 70] with low=0 and high=6: what is the first mid index checked?",
            explanation = "mid = low + (high - low) / 2 = 0 + (6 - 0) / 2 = 3. The algorithm evaluates index 3 (value 40).",
            options = listOf(
                PracticeOption(0, "Index 3 (Value 40)", true),
                PracticeOption(1, "Index 2 (Value 30)", false),
                PracticeOption(2, "Index 4 (Value 50)", false),
                PracticeOption(3, "Index 0 (Value 10)", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(10, 20, 30, 40, 50, 60, 70),
                elementStates = mapOf(3 to ElementState.COMPARING),
                topPointers = mapOf("mid" to 3),
                bottomPointers = mapOf("low" to 0, "high" to 6)
            )
        ),
        PracticeQuestion(
            id = "search_binary_3",
            algorithmId = AlgorithmId.BINARY_SEARCH,
            category = "Searching",
            difficulty = PracticeDifficulty.HARD,
            stepTitle = "BOUND REDUCTION · BINARY SEARCH",
            promptText = "Searching for target 55: if arr[mid] (40) < 55, how are search bounds updated?",
            explanation = "Since the target (55) is greater than arr[mid] (40), the target must reside in the right subarray if present. The low pointer is updated to mid + 1 (index 4), eliminating indices 0..3.",
            options = listOf(
                PracticeOption(0, "low = mid + 1 (eliminates left half)", true),
                PracticeOption(1, "high = mid - 1 (eliminates right half)", false),
                PracticeOption(2, "low = mid (keeps mid in search space)", false),
                PracticeOption(3, "Terminate and return -1", false)
            ),
            snapshot = PracticeSnapshot.LinearSnapshot(
                array = listOf(10, 20, 30, 40, 50, 60, 70),
                elementStates = mapOf(
                    0 to ElementState.IDLE,
                    1 to ElementState.IDLE,
                    2 to ElementState.IDLE,
                    3 to ElementState.COMPARING
                ),
                activeRange = 4..6,
                topPointers = mapOf("mid" to 3),
                bottomPointers = mapOf("low" to 0, "high" to 6)
            )
        ),

        // ====================================================================
        //  DATA STRUCTURES — STACK
        // ====================================================================
        PracticeQuestion(
            id = "ds_stack_1",
            algorithmId = AlgorithmId.STACK,
            category = "Data Structures",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "PRINCIPLE · STACK",
            promptText = "Which operational access discipline defines a Stack data structure?",
            explanation = "A Stack follows LIFO (Last-In, First-Out): the most recently pushed element is the first one removed by a pop operation.",
            options = listOf(
                PracticeOption(0, "LIFO (Last-In, First-Out)", true),
                PracticeOption(1, "FIFO (First-In, First-Out)", false),
                PracticeOption(2, "Priority-based ordering", false),
                PracticeOption(3, "Random access via index", false)
            ),
            snapshot = PracticeSnapshot.BufferSnapshot(
                items = listOf(10, 20, 30),
                isStack = true,
                highlightIndex = 2,
                operationLabel = "TOP"
            )
        ),
        PracticeQuestion(
            id = "ds_stack_2",
            algorithmId = AlgorithmId.STACK,
            category = "Data Structures",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "OPERATIONS · STACK POP",
            promptText = "Starting with empty stack: Push(15), Push(25), Push(35), then Pop(). Which value is returned?",
            explanation = "Elements are pushed in order: [15] -> [15, 25] -> [15, 25, 35]. Pop() removes and returns the top element (35).",
            options = listOf(
                PracticeOption(0, "35", true),
                PracticeOption(1, "15", false),
                PracticeOption(2, "25", false),
                PracticeOption(3, "Empty", false)
            ),
            snapshot = PracticeSnapshot.BufferSnapshot(
                items = listOf(15, 25, 35),
                isStack = true,
                highlightIndex = 2,
                operationLabel = "POP"
            )
        ),

        // ====================================================================
        //  DATA STRUCTURES — QUEUE
        // ====================================================================
        PracticeQuestion(
            id = "ds_queue_1",
            algorithmId = AlgorithmId.QUEUE,
            category = "Data Structures",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "PRINCIPLE · QUEUE",
            promptText = "Which operational access discipline defines a Queue data structure?",
            explanation = "A Queue follows FIFO (First-In, First-Out): elements are added at the rear and removed from the front, mirroring a real-world checkout line.",
            options = listOf(
                PracticeOption(0, "FIFO (First-In, First-Out)", true),
                PracticeOption(1, "LIFO (Last-In, First-Out)", false),
                PracticeOption(2, "Key-value hash lookup", false),
                PracticeOption(3, "Sorted binary partition", false)
            ),
            snapshot = PracticeSnapshot.BufferSnapshot(
                items = listOf(10, 20, 30),
                isStack = false,
                highlightIndex = 0,
                operationLabel = "FRONT"
            )
        ),
        PracticeQuestion(
            id = "ds_queue_2",
            algorithmId = AlgorithmId.QUEUE,
            category = "Data Structures",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "OPERATIONS · QUEUE DEQUEUE",
            promptText = "Starting with empty queue: Enqueue(8), Enqueue(16), Enqueue(24), then Dequeue(). Which value is removed?",
            explanation = "Enqueue appends to the back: [8] -> [8, 16] -> [8, 16, 24]. Dequeue removes from the front, returning 8.",
            options = listOf(
                PracticeOption(0, "8", true),
                PracticeOption(1, "24", false),
                PracticeOption(2, "16", false),
                PracticeOption(3, "None (Queue is full)", false)
            ),
            snapshot = PracticeSnapshot.BufferSnapshot(
                items = listOf(8, 16, 24),
                isStack = false,
                highlightIndex = 0,
                operationLabel = "DEQUEUE"
            )
        ),

        // ====================================================================
        //  DATA STRUCTURES — BINARY SEARCH TREE (BST)
        // ====================================================================
        PracticeQuestion(
            id = "ds_bst_1",
            algorithmId = AlgorithmId.BINARY_SEARCH_TREE,
            category = "Data Structures",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "PROPERTY · BINARY SEARCH TREE",
            promptText = "Which tree traversal of a valid BST visits keys in strictly ASCENDING sorted order?",
            explanation = "In-Order traversal recursively visits Left Subtree -> Node -> Right Subtree. Due to the BST invariant (Left < Node < Right), this yields sorted order.",
            options = listOf(
                PracticeOption(0, "In-Order Traversal", true),
                PracticeOption(1, "Pre-Order Traversal", false),
                PracticeOption(2, "Post-Order Traversal", false),
                PracticeOption(3, "Level-Order Traversal", false)
            )
        ),
        PracticeQuestion(
            id = "ds_bst_2",
            algorithmId = AlgorithmId.BINARY_SEARCH_TREE,
            category = "Data Structures",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "INSERTION · BST BRANCHING",
            promptText = "Inserting 25 into a BST with Root=40 and LeftChild=20: where is 25 placed?",
            explanation = "Comparing 25 with Root (40): 25 < 40, go left to node 20. Comparing 25 with 20: 25 > 20, go right. Since 20's right child is null, 25 becomes the right child of 20.",
            options = listOf(
                PracticeOption(0, "Right child of node 20", true),
                PracticeOption(1, "Left child of node 20", false),
                PracticeOption(2, "Right child of root 40", false),
                PracticeOption(3, "It replaces root 40", false)
            ),
            snapshot = PracticeSnapshot.GraphSnapshot(
                nodes = listOf(
                    PracticeGraphNode("root", "40", ElementState.IDLE, 0.5f, 0.2f),
                    PracticeGraphNode("left", "20", ElementState.ACTIVE, 0.25f, 0.6f),
                    PracticeGraphNode("right", "60", ElementState.IDLE, 0.75f, 0.6f)
                ),
                edges = listOf(
                    PracticeGraphEdge("root", "left", isHighlighted = true),
                    PracticeGraphEdge("root", "right")
                ),
                activeNodeId = "left"
            )
        ),

        // ====================================================================
        //  DATA STRUCTURES — HEAP
        // ====================================================================
        PracticeQuestion(
            id = "ds_heap_1",
            algorithmId = AlgorithmId.HEAP,
            category = "Data Structures",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "COMPLEXITY · HEAP INSERTION",
            promptText = "What is the worst-case time complexity of inserting an element into a binary heap of size n?",
            explanation = "Inserting into a binary heap appends the element at the next available leaf and sifts it up to restore the heap property. The height of a complete binary tree is ⌊log₂ n⌋, giving O(log n) time.",
            options = listOf(
                PracticeOption(0, "O(log n)", true),
                PracticeOption(1, "O(1)", false),
                PracticeOption(2, "O(n)", false),
                PracticeOption(3, "O(n log n)", false)
            )
        ),
        PracticeQuestion(
            id = "ds_heap_2",
            algorithmId = AlgorithmId.HEAP,
            category = "Data Structures",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "INDEXING · BINARY HEAP",
            promptText = "In a 0-indexed array representation of a binary heap, what is the parent index of node at index i?",
            explanation = "In a standard 0-indexed complete binary tree, the left child is at 2i + 1, the right child is at 2i + 2, and the parent is at ⌊(i - 1) / 2⌋.",
            options = listOf(
                PracticeOption(0, "(i - 1) / 2", true),
                PracticeOption(1, "i / 2", false),
                PracticeOption(2, "2 * i + 1", false),
                PracticeOption(3, "(i + 1) / 2", false)
            )
        ),

        // ====================================================================
        //  GRAPH TRAVERSAL — BFS
        // ====================================================================
        PracticeQuestion(
            id = "graph_bfs_1",
            algorithmId = AlgorithmId.BFS,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "QUEUE · BREADTH-FIRST SEARCH",
            promptText = "Which data structure is fundamental to implementing Breadth-First Search (BFS)?",
            explanation = "BFS explores neighbor nodes level by level. A Queue (FIFO) is required to ensure that nodes discovered first at the current depth level are visited before nodes at deeper levels.",
            options = listOf(
                PracticeOption(0, "Queue (FIFO)", true),
                PracticeOption(1, "Stack (LIFO)", false),
                PracticeOption(2, "Hash Set", false),
                PracticeOption(3, "Binary Heap", false)
            ),
            snapshot = PracticeSnapshot.GraphSnapshot(
                nodes = listOf(
                    PracticeGraphNode("A", "A", ElementState.SORTED, 0.5f, 0.2f),
                    PracticeGraphNode("B", "B", ElementState.ACTIVE, 0.25f, 0.6f),
                    PracticeGraphNode("C", "C", ElementState.ACTIVE, 0.75f, 0.6f)
                ),
                edges = listOf(
                    PracticeGraphEdge("A", "B", isHighlighted = true),
                    PracticeGraphEdge("A", "C", isHighlighted = true)
                ),
                activeNodeId = "A"
            )
        ),
        PracticeQuestion(
            id = "graph_bfs_2",
            algorithmId = AlgorithmId.BFS,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "SHORTEST PATH · BFS",
            promptText = "On what type of graph does BFS guarantee finding the SHORTEST path in terms of edge count?",
            explanation = "Because BFS explores nodes radially level by level, the first time it reaches a destination node in an unweighted graph, it has found the path with the minimum number of edges.",
            options = listOf(
                PracticeOption(0, "Unweighted graph", true),
                PracticeOption(1, "Graph with negative weights", false),
                PracticeOption(2, "Directed Acyclic Graph with weighted edges", false),
                PracticeOption(3, "Complete graph with arbitrary weights", false)
            )
        ),

        // ====================================================================
        //  GRAPH TRAVERSAL — DFS
        // ====================================================================
        PracticeQuestion(
            id = "graph_dfs_1",
            algorithmId = AlgorithmId.DFS,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "MECHANISM · DEPTH-FIRST SEARCH",
            promptText = "Which operational mechanism does standard Depth-First Search (DFS) employ to explore paths?",
            explanation = "DFS explores as far as possible along each branch before backtracking, naturally implemented using recursion (the system call stack) or an explicit Stack (LIFO).",
            options = listOf(
                PracticeOption(0, "Call Stack / Recursion (LIFO)", true),
                PracticeOption(1, "Queue (FIFO)", false),
                PracticeOption(2, "Priority Queue", false),
                PracticeOption(3, "B-Tree indexing", false)
            ),
            snapshot = PracticeSnapshot.GraphSnapshot(
                nodes = listOf(
                    PracticeGraphNode("A", "A", ElementState.SORTED, 0.5f, 0.2f),
                    PracticeGraphNode("B", "B", ElementState.SORTED, 0.3f, 0.55f),
                    PracticeGraphNode("D", "D", ElementState.ACTIVE, 0.2f, 0.85f)
                ),
                edges = listOf(
                    PracticeGraphEdge("A", "B", isHighlighted = true),
                    PracticeGraphEdge("B", "D", isHighlighted = true)
                ),
                activeNodeId = "D"
            )
        ),
        PracticeQuestion(
            id = "graph_dfs_2",
            algorithmId = AlgorithmId.DFS,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "DEAD END · DFS BACKTRACKING",
            promptText = "When DFS reaches a node where all adjacent neighbors have already been visited, what action does it take?",
            explanation = "Upon reaching a dead end, DFS backtracks to the previous node along the search path and checks for any remaining unvisited branches.",
            options = listOf(
                PracticeOption(0, "Backtracks to the previous node with unvisited neighbors", true),
                PracticeOption(1, "Clears the visited set and restarts from root", false),
                PracticeOption(2, "Switches to breadth-first queue order", false),
                PracticeOption(3, "Terminates the program immediately", false)
            )
        ),

        // ====================================================================
        //  GRAPH TRAVERSAL — DIJKSTRA'S SHORTEST PATH
        // ====================================================================
        PracticeQuestion(
            id = "graph_dijkstra_1",
            algorithmId = AlgorithmId.DIJKSTRA,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.EASY,
            stepTitle = "COMPLEXITY · DIJKSTRA",
            promptText = "What is the time complexity of Dijkstra's algorithm implemented with a Min-Binary Heap on a graph with V vertices and E edges?",
            explanation = "Each vertex is inserted and extracted from the priority queue once (V * O(log V)), and each edge can trigger a distance relaxation (E * O(log V)), resulting in total time complexity O((V + E) log V).",
            options = listOf(
                PracticeOption(0, "O((V + E) log V)", true),
                PracticeOption(1, "O(V²)", false),
                PracticeOption(2, "O(V + E)", false),
                PracticeOption(3, "O(V log E)", false)
            ),
            snapshot = PracticeSnapshot.GraphSnapshot(
                nodes = listOf(
                    PracticeGraphNode("S", "S", ElementState.VISITED, 0.2f, 0.5f),
                    PracticeGraphNode("A", "A", ElementState.ACTIVE, 0.5f, 0.2f),
                    PracticeGraphNode("B", "B", ElementState.ACTIVE, 0.5f, 0.8f),
                    PracticeGraphNode("C", "C", ElementState.IDLE, 0.8f, 0.5f)
                ),
                edges = listOf(
                    PracticeGraphEdge("S", "A", weight = 3),
                    PracticeGraphEdge("S", "B", weight = 1),
                    PracticeGraphEdge("B", "C", weight = 4),
                    PracticeGraphEdge("A", "C", weight = 2)
                )
            )
        ),
        PracticeQuestion(
            id = "graph_dijkstra_2",
            algorithmId = AlgorithmId.DIJKSTRA,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.MEDIUM,
            stepTitle = "INVARIANT · NEGATIVE WEIGHTS",
            promptText = "Why does standard Dijkstra's algorithm fail to guarantee optimal shortest paths on graphs with negative edge weights?",
            explanation = "Dijkstra is a greedy algorithm based on the non-decreasing distance invariant: once a vertex is settled (extracted from the priority queue), its shortest distance is finalized and never relaxed again. Negative edges can provide a cheaper detour later, breaking this greedy invariant. Use Bellman-Ford for negative weights.",
            options = listOf(
                PracticeOption(0, "Once a node is settled, Dijkstra assumes its shortest distance is finalized and never re-relaxes it", true),
                PracticeOption(1, "Priority Queues cannot store negative numbers", false),
                PracticeOption(2, "Dijkstra only works on tree topologies without cycles", false),
                PracticeOption(3, "Negative edge weights cause the graph to become disconnected", false)
            ),
            snapshot = PracticeSnapshot.GraphSnapshot(
                nodes = listOf(
                    PracticeGraphNode("A", "A", ElementState.VISITED, 0.2f, 0.5f),
                    PracticeGraphNode("B", "B", ElementState.ACTIVE, 0.5f, 0.3f),
                    PracticeGraphNode("C", "C", ElementState.IDLE, 0.8f, 0.5f)
                ),
                edges = listOf(
                    PracticeGraphEdge("A", "B", weight = 5),
                    PracticeGraphEdge("A", "C", weight = 2),
                    PracticeGraphEdge("B", "C", weight = -4)
                )
            )
        ),
        PracticeQuestion(
            id = "graph_dijkstra_3",
            algorithmId = AlgorithmId.DIJKSTRA,
            category = "Graph Traversal",
            difficulty = PracticeDifficulty.HARD,
            stepTitle = "RELAXATION · SHORTEST PATH TRACE",
            promptText = "In a graph with source S (dist=0) and edges (S->A, weight 4), (S->B, weight 1), (B->A, weight 2), which vertex is settled first after S, and what is the final shortest distance to A?",
            explanation = "From S, distances are dist[B]=1, dist[A]=4. Vertex B has the minimum tentative distance (1), so B is settled next. Relaxing edge (B->A, weight 2) produces candidate 1 + 2 = 3 < 4, updating dist[A] to 3.",
            options = listOf(
                PracticeOption(0, "B is settled first; dist[A] = 3", true),
                PracticeOption(1, "A is settled first; dist[A] = 4", false),
                PracticeOption(2, "B is settled first; dist[A] = 4", false),
                PracticeOption(3, "A and B are settled simultaneously", false)
            ),
            snapshot = PracticeSnapshot.GraphSnapshot(
                nodes = listOf(
                    PracticeGraphNode("S", "S", ElementState.VISITED, 0.2f, 0.5f),
                    PracticeGraphNode("B", "B", ElementState.ACTIVE, 0.5f, 0.8f),
                    PracticeGraphNode("A", "A", ElementState.COMPARING, 0.7f, 0.3f)
                ),
                edges = listOf(
                    PracticeGraphEdge("S", "A", weight = 4),
                    PracticeGraphEdge("S", "B", weight = 1, isHighlighted = true),
                    PracticeGraphEdge("B", "A", weight = 2)
                )
            )
        )
    )

    /**
     * Retrieve filtered practice questions by category and optional difficulty tier.
     */
    fun getQuestions(
        category: String = "All",
        difficulty: PracticeDifficulty? = null
    ): List<PracticeQuestion> {
        return questions.filter { q ->
            val matchesCategory = category.equals("All", ignoreCase = true) ||
                q.category.equals(category, ignoreCase = true)
            val matchesDifficulty = difficulty == null || q.difficulty == difficulty
            matchesCategory && matchesDifficulty
        }
    }

    /**
     * Retrieve question by ID.
     */
    fun getQuestionById(id: String): PracticeQuestion? =
        questions.find { it.id == id }

    /**
     * Retrieve all questions for a specific algorithm.
     */
    fun getQuestionsForAlgorithm(algorithmId: AlgorithmId): List<PracticeQuestion> =
        questions.filter { it.algorithmId == algorithmId }

    /** Total count of all questions currently in catalogue. */
    val totalCount: Int get() = questions.size
}
