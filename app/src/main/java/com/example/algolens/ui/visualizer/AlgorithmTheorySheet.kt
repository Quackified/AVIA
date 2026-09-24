package com.example.algolens.ui.visualizer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.components.AlgoGlyphs

/**
 * Data structure holding deep theory information for algorithms.
 */
data class AlgorithmTheoryData(
    val name: String,
    val overview: String,
    val howItWorks: List<String>,
    val whenToUse: List<String>,
    val bestCase: String,
    val averageCase: String,
    val worstCase: String,
    val spaceComplexity: String,
    val isStable: Boolean,
    val isInPlace: Boolean,
    val commonPitfalls: List<String>,
    val proTips: String
)

object AlgorithmTheoryRepository {
    fun getTheory(algorithmName: String): AlgorithmTheoryData {
        return when (algorithmName.lowercase().trim()) {
            "bubble sort" -> AlgorithmTheoryData(
                name = "Bubble Sort",
                overview = "A simple comparison-based sorting algorithm that repeatedly steps through the input list, compares adjacent elements, and swaps them if they are in the wrong order.",
                howItWorks = listOf(
                    "Iterate through the array from index 0 to n-1.",
                    "In each pass, compare adjacent pairs (arr[j] and arr[j+1]).",
                    "If arr[j] > arr[j+1], swap them so the larger element bubbles up towards the right.",
                    "Repeat for n-1 passes or stop early if no swaps occurred during a full pass."
                ),
                whenToUse = listOf(
                    "Educational purposes to understand basic sorting concepts and loops.",
                    "Small or nearly-sorted datasets where O(n) early-termination is advantageous.",
                    "Situations where code simplicity is prioritized over computational speed."
                ),
                bestCase = "O(n) - Already sorted with early-exit flag",
                averageCase = "O(n²)",
                worstCase = "O(n²) - Reverse sorted array",
                spaceComplexity = "O(1) Auxiliary",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Forgetting the swapped flag leads to O(n²) even when the array is already sorted.",
                    "Extremely slow on large inputs (millions of elements)."
                ),
                proTips = "Adding a 'swapped' boolean flag allows Bubble Sort to complete in a single O(n) scan when the array is already ordered."
            )
            "quick sort" -> AlgorithmTheoryData(
                name = "Quick Sort",
                overview = "A highly efficient Divide-and-Conquer sorting algorithm that selects a 'pivot' element and partitions the other elements into two sub-arrays according to whether they are less than or greater than the pivot.",
                howItWorks = listOf(
                    "Choose a pivot element from the array (e.g., last element in Lomuto partition).",
                    "Partition: Reorder elements so that all items smaller than the pivot precede it, and larger items follow it.",
                    "Recursively apply the algorithm to the sub-array of smaller elements and the sub-array of larger elements.",
                    "Base case is reached when sub-arrays have 0 or 1 element."
                ),
                whenToUse = listOf(
                    "General-purpose in-memory sorting of large datasets.",
                    "Systems requiring high average-case performance with minimal memory overhead.",
                    "Used in language runtimes (e.g. C qsort, Java Dual-Pivot Quicksort for primitives)."
                ),
                bestCase = "O(n log n) - Balanced partitions",
                averageCase = "O(n log n)",
                worstCase = "O(n²) - Unbalanced partitions (e.g. sorted array with bad pivot)",
                spaceComplexity = "O(log n) Call Stack",
                isStable = false,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Choosing the first or last element as pivot on already-sorted input degrades to O(n²).",
                    "Deep recursive call stacks on malicious inputs can cause stack overflow if not using tail-call optimization or Median-of-3."
                ),
                proTips = "Use randomized pivot selection or 'Median-of-Three' (first, middle, last) to virtually eliminate the O(n²) worst-case."
            )
            "merge sort" -> AlgorithmTheoryData(
                name = "Merge Sort",
                overview = "A reliable Divide-and-Conquer algorithm that divides the input array into two halves, recursively sorts each half, and then merges the two sorted halves into a single sorted array.",
                howItWorks = listOf(
                    "Divide: Find the midpoint m = (l + r) / 2 and split the array into left and right halves.",
                    "Conquer: Recursively call mergeSort on left and right sub-arrays.",
                    "Combine: Merge the two sorted halves into a temporary buffer and copy back to the original array."
                ),
                whenToUse = listOf(
                    "When guaranteed O(n log n) worst-case time complexity is required.",
                    "Sorting linked lists (where random access is not available and merging requires O(1) space).",
                    "External sorting of datasets that exceed RAM capacity."
                ),
                bestCase = "O(n log n)",
                averageCase = "O(n log n)",
                worstCase = "O(n log n) Guaranteed",
                spaceComplexity = "O(n) Auxiliary Buffer",
                isStable = true,
                isInPlace = false,
                commonPitfalls = listOf(
                    "High auxiliary memory overhead (O(n)) compared to in-place sorts like Quick Sort or Heap Sort.",
                    "Allocating new buffers repeatedly in recursion rather than reusing a single temporary array."
                ),
                proTips = "Merge Sort is naturally stable and forms the foundation for Timsort (used in Python and Java collections)."
            )
            "insertion sort" -> AlgorithmTheoryData(
                name = "Insertion Sort",
                overview = "A simple, intuitive sorting algorithm that builds the final sorted array one item at a time by repeatedly taking the next element and inserting it into its correct position among previously sorted elements.",
                howItWorks = listOf(
                    "Start with the first element considered sorted.",
                    "Pick the next element ('key') from the unsorted section.",
                    "Compare key with elements in the sorted portion from right to left.",
                    "Shift elements greater than key one position to the right.",
                    "Insert key into the newly vacated position."
                ),
                whenToUse = listOf(
                    "Small arrays (typically N < 16 to 32 elements) due to minimal constant factor overhead.",
                    "Nearly sorted or continuously streaming real-time data.",
                    "As the base-case fallback for hybrid algorithms like Timsort and Introsort."
                ),
                bestCase = "O(n) - Already sorted input",
                averageCase = "O(n²)",
                worstCase = "O(n²) - Reverse sorted input",
                spaceComplexity = "O(1) Auxiliary",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Excessive element shifts on large reverse-sorted arrays.",
                    "Off-by-one errors when managing the boundary between sorted and unsorted segments."
                ),
                proTips = "Because of cache locality and zero recursion overhead, Insertion Sort outperforms Quick Sort and Merge Sort on arrays under 20 elements."
            )
            "selection sort" -> AlgorithmTheoryData(
                name = "Selection Sort",
                overview = "An in-place comparison sort that divides the list into a sorted and unsorted sublist. In each pass, it finds the smallest element from the unsorted sublist and swaps it with the first unsorted element.",
                howItWorks = listOf(
                    "Initialize the boundary of the sorted array at index 0.",
                    "Find the minimum element in the remaining unsorted subarray [i..n-1].",
                    "Swap the found minimum element with the element at index i.",
                    "Advance the boundary by one and repeat until the array is fully sorted."
                ),
                whenToUse = listOf(
                    "When memory writes are significantly more expensive than reads (e.g. Flash memory or EEPROM), as it performs at most O(n) swaps.",
                    "Small embedded systems with strict deterministic cycle constraints."
                ),
                bestCase = "O(n²)",
                averageCase = "O(n²)",
                worstCase = "O(n²)",
                spaceComplexity = "O(1) Auxiliary",
                isStable = false,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Performs O(n²) comparisons even if the input array is already completely sorted.",
                    "Not stable by default (long-distance swaps can reorder identical keys)."
                ),
                proTips = "Selection Sort strictly makes at most (n - 1) swaps, making it ideal when writing to persistent storage has a high penalty."
            )
            "binary search" -> AlgorithmTheoryData(
                name = "Binary Search",
                overview = "An ultra-fast search algorithm that finds the position of a target value within a sorted array by repeatedly dividing the search space in half.",
                howItWorks = listOf(
                    "Maintain two pointers: low = 0 and high = array.length - 1.",
                    "Calculate the midpoint: mid = low + (high - low) / 2.",
                    "If arr[mid] == target, return mid.",
                    "If arr[mid] < target, narrow the search space to the right half (low = mid + 1).",
                    "If arr[mid] > target, narrow the search space to the left half (high = mid - 1).",
                    "Return -1 if low exceeds high without finding target."
                ),
                whenToUse = listOf(
                    "Searching in large sorted datasets, dictionaries, or database indices.",
                    "Finding boundary conditions / monotone predicates (Binary Search on Answer).",
                    "High-throughput lookup tables."
                ),
                bestCase = "O(1) - Target is at exact midpoint",
                averageCase = "O(log n)",
                worstCase = "O(log n)",
                spaceComplexity = "O(1) Iterative / O(log n) Recursive",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Applying binary search on unsorted arrays yields incorrect or undefined results.",
                    "Integer overflow when calculating (low + high) / 2 on large arrays. Use low + (high - low) / 2 instead."
                ),
                proTips = "For a dataset of 1 billion items, Binary Search finds any element in at most 30 comparisons!"
            )
            "linear search" -> AlgorithmTheoryData(
                name = "Linear Search",
                overview = "The simplest search algorithm that checks every element in a list sequentially from start to end until a match is found or the entire list has been traversed.",
                howItWorks = listOf(
                    "Start at the first element (index 0).",
                    "Compare each element with the target value.",
                    "If a match is found, return the current index immediately.",
                    "If the end of the array is reached without matching, return -1."
                ),
                whenToUse = listOf(
                    "Unordered / unsorted datasets where sorting overhead is not justified.",
                    "Small collections (N < 20) or single-lookup operations.",
                    "Linked lists where random access indexing is unavailable."
                ),
                bestCase = "O(1) - Target is at index 0",
                averageCase = "O(n)",
                worstCase = "O(n) - Target is last or not present",
                spaceComplexity = "O(1) Auxiliary",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Inefficient for repeated lookups on large datasets. Use hash tables or sort once and use binary search."
                ),
                proTips = "Sentinel linear search can eliminate boundary checks on each iteration by placing the target at the end of the array."
            )
            "breadth-first search (bfs)", "bfs" -> AlgorithmTheoryData(
                name = "Breadth-First Search (BFS)",
                overview = "A graph and tree traversal algorithm that explores all neighbor nodes at the present depth level before moving on to nodes at the next depth level using a Queue (FIFO).",
                howItWorks = listOf(
                    "Enqueue the starting node and mark it as visited.",
                    "While the queue is not empty: dequeue the front node.",
                    "For each unvisited neighbor of the current node, mark as visited and enqueue it.",
                    "Repeat until the queue is empty or the target node is found."
                ),
                whenToUse = listOf(
                    "Finding the shortest path in unweighted graphs / grids.",
                    "Level-order traversal of hierarchical trees.",
                    "Social network degree-of-separation calculations (e.g. LinkedIn connections)."
                ),
                bestCase = "O(1) - Starting node is target",
                averageCase = "O(V + E) - Vertices + Edges",
                worstCase = "O(V + E)",
                spaceComplexity = "O(V) Queue & Visited Set",
                isStable = true,
                isInPlace = false,
                commonPitfalls = listOf(
                    "Forgetting the visited set causes infinite loops on cyclic graphs.",
                    "Memory explosion when exploring graphs with high branching factors."
                ),
                proTips = "BFS is guaranteed to find the shortest path in terms of edge count for unweighted graphs."
            )
            "depth-first search (dfs)", "dfs" -> AlgorithmTheoryData(
                name = "Depth-First Search (DFS)",
                overview = "A graph and tree traversal algorithm that explores as far as possible along each branch before backtracking using recursion or an explicit Stack (LIFO).",
                howItWorks = listOf(
                    "Mark the current node as visited.",
                    "For each unvisited adjacent neighbor, recursively invoke DFS.",
                    "Backtrack once a node has no remaining unvisited neighbors.",
                    "Can be implemented iteratively using an explicit Stack."
                ),
                whenToUse = listOf(
                    "Cycle detection in directed/undirected graphs.",
                    "Topological sorting for dependency resolution (e.g. Gradle / npm build tasks).",
                    "Maze solving, game theory decision trees, and connected component finding."
                ),
                bestCase = "O(1)",
                averageCase = "O(V + E)",
                worstCase = "O(V + E)",
                spaceComplexity = "O(V) Recursion Stack",
                isStable = true,
                isInPlace = false,
                commonPitfalls = listOf(
                    "StackOverflowError on extremely deep or degenerate graphs.",
                    "DFS does not guarantee the shortest path to a target node."
                ),
                proTips = "Combining DFS with discovery/finish timestamps enables Tarjan's and Kosaraju's strongly connected components algorithms."
            )
            "stack" -> AlgorithmTheoryData(
                name = "Stack (LIFO)",
                overview = "A linear data structure following the Last-In, First-Out (LIFO) principle. Elements can only be added (pushed) or removed (popped) from the top.",
                howItWorks = listOf(
                    "push(x): Adds element x to the top of the stack.",
                    "pop(): Removes and returns the top element.",
                    "peek(): Returns the top element without removing it.",
                    "isEmpty(): Checks if the stack has zero elements."
                ),
                whenToUse = listOf(
                    "Function call stack management and recursion frames.",
                    "Undo / Redo buffers in text editors.",
                    "Expression evaluation and matching balanced parentheses (e.g. compiler ASTs)."
                ),
                bestCase = "O(1) push / pop / peek",
                averageCase = "O(1)",
                worstCase = "O(1) per operation",
                spaceComplexity = "O(n) for n stored elements",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Stack Underflow when popping from an empty stack.",
                    "Stack Overflow when exceeding fixed array capacity."
                ),
                proTips = "Stacks can be implemented efficiently via dynamic arrays (`ArrayList`) or singly linked lists with O(1) head insertion."
            )
            "queue" -> AlgorithmTheoryData(
                name = "Queue (FIFO)",
                overview = "A linear data structure following the First-In, First-Out (FIFO) principle. Elements are inserted at the rear (enqueue) and removed from the front (dequeue).",
                howItWorks = listOf(
                    "enqueue(x): Appends element x to the rear of the queue.",
                    "dequeue(): Removes and returns the front element.",
                    "peek(): Inspects the front element without removing it."
                ),
                whenToUse = listOf(
                    "CPU task scheduling and asynchronous message queues (RabbitMQ, Kafka).",
                    "Print spoolers and IO buffer management.",
                    "Breadth-First Search (BFS) graph traversals."
                ),
                bestCase = "O(1) enqueue / dequeue",
                averageCase = "O(1)",
                worstCase = "O(1)",
                spaceComplexity = "O(n)",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Using a plain array without circular indices causes O(n) shifts on dequeue. Use circular buffer or linked list."
                ),
                proTips = "Circular queues reuse previously dequeued slots using modulo arithmetic `(rear + 1) % capacity`."
            )
            "binary search tree" -> AlgorithmTheoryData(
                name = "Binary Search Tree (BST)",
                overview = "A node-based binary tree data structure where each node has at most two children. The left subtree contains only nodes with keys less than the node's key, and the right subtree contains only nodes with keys greater than the node's key.",
                howItWorks = listOf(
                    "Search: Compare target with root. If target < root, recurse left; if target > root, recurse right.",
                    "Insert: Traverse to the appropriate leaf position adhering to BST property and attach new node.",
                    "Delete: Handle three cases (0 children: remove leaf, 1 child: promote child, 2 children: replace with in-order successor)."
                ),
                whenToUse = listOf(
                    "Dynamic search keys where frequent insertions and deletions occur.",
                    "In-order traversals yield keys in sorted ascending order in O(n) time.",
                    "Implementing associative sets and map dictionaries."
                ),
                bestCase = "O(log n) Search / Insert / Delete",
                averageCase = "O(log n)",
                worstCase = "O(n) - Degenerates to linked list on sorted input",
                spaceComplexity = "O(n)",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Unbalanced BSTs degrade to O(n) line structures when keys are inserted in sorted order.",
                    "Use Self-Balancing BSTs (AVL, Red-Black Trees) to maintain strict O(log n) height."
                ),
                proTips = "An In-order traversal (Left -> Node -> Right) of a BST always visits nodes in ascending sorted order!"
            )
            "heap" -> AlgorithmTheoryData(
                name = "Heap (Binary Min/Max)",
                overview = "A complete binary tree that satisfies the heap property: in a Min-Heap, the key at root is the minimum among all keys; in a Max-Heap, the key at root is the maximum.",
                howItWorks = listOf(
                    "Array Representation: Node at index i has left child at 2i+1, right child at 2i+2, and parent at (i-1)/2.",
                    "Insert: Append to end of array and 'bubble up' (sift-up) to restore heap property.",
                    "Extract Min/Max: Replace root with last element, pop last, and 'sift-down' from root."
                ),
                whenToUse = listOf(
                    "Priority Queues and event-driven simulations.",
                    "Heap Sort algorithm for O(n log n) in-place sorting.",
                    "Top-K elements, streaming median (two heaps), and Dijkstra's Shortest Path."
                ),
                bestCase = "O(1) peek",
                averageCase = "O(log n) insert / extract",
                worstCase = "O(log n)",
                spaceComplexity = "O(n) Compact Array",
                isStable = false,
                isInPlace = true,
                commonPitfalls = listOf(
                    "Heaps only guarantee root is min/max; sibling nodes are not ordered relative to each other.",
                    "Arbitrary element lookup is O(n), not O(log n)."
                ),
                proTips = "Building a heap from an unordered array of N elements takes O(n) linear time via bottom-up heapify!"
            )
            else -> AlgorithmTheoryData(
                name = algorithmName,
                overview = "Algorithmic computational procedure with defined inputs and outputs.",
                howItWorks = listOf("Executes sequential logical operations according to its computational paradigm."),
                whenToUse = listOf("Specific domain computational problem solving."),
                bestCase = "O(1)",
                averageCase = "O(n)",
                worstCase = "O(n)",
                spaceComplexity = "O(1)",
                isStable = true,
                isInPlace = true,
                commonPitfalls = listOf("Improper handling of edge cases or out-of-bounds indices."),
                proTips = "Inspect the live step trace to visualize internal data flow."
            )
        }
    }
}

/**
 * Redesigned Algorithm Theory & Deep Dive modal bottom sheet.
 * Slides up from the bottom edge over a dimmed scrim with a pinned header,
 * 2x2 weighted complexity bento grid (separating Big-O notation from condition notes),
 * property badges, step-by-step execution cards, and engineering callouts.
 */
@Composable
fun AlgorithmTheorySheet(
    algorithm: Algorithm,
    isVisible: Boolean = true,
    onDismiss: () -> Unit
) {
    val theory = remember(algorithm.id) {
        AlgorithmTheoryRepository.getTheory(algorithm.name)
    }

    BackHandler(enabled = isVisible) { onDismiss() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrim backdrop (tap to dismiss)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(240)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.62f))
                    .clickable(onClick = onDismiss)
            )
        }

        // Bottom modal sheet
        AnimatedVisibility(
            visible = isVisible,
            enter = androidx.compose.animation.slideInVertically { it } + fadeIn(tween(260)),
            exit = androidx.compose.animation.slideOutVertically { it } + fadeOut(tween(220)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.86f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(CardBackground)
                    .border(
                        AlgoTokens.strokeThin,
                        BorderCyan.copy(alpha = 0.35f),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .navigationBarsPadding()
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(BorderSubtle)
                    )
                }

                // Pinned Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanSubtle)
                                .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Book,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = theory.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PropertyBadge(
                                    label = if (theory.isStable) "Stable" else "Unstable",
                                    color = if (theory.isStable) AccentGreen else AccentOrange
                                )
                                PropertyBadge(
                                    label = if (theory.isInPlace) "In-Place" else "Out-of-Place",
                                    color = if (theory.isInPlace) PrimaryCyan else SecondaryPurple
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, BorderSubtle, CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Subtle Header Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderSubtle.copy(alpha = 0.6f))
                )

                // Scrollable Sheet Body
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Overview
                    Text(
                        text = theory.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    // ── 2x2 Weighted Bento Complexity Matrix ──
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "COMPLEXITY MATRIX",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ComplexityBentoCard(
                                label = "BEST TIME",
                                rawValue = theory.bestCase,
                                color = AccentGreen,
                                modifier = Modifier.weight(1f)
                            )
                            ComplexityBentoCard(
                                label = "AVERAGE TIME",
                                rawValue = theory.averageCase,
                                color = AccentYellow,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ComplexityBentoCard(
                                label = "WORST TIME",
                                rawValue = theory.worstCase,
                                color = AccentRed,
                                modifier = Modifier.weight(1f)
                            )
                            ComplexityBentoCard(
                                label = "SPACE MEMORY",
                                rawValue = theory.spaceComplexity,
                                color = PurpleGlow,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // ── How It Works (Step-by-Step Cards) ──
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "HOW IT WORKS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )

                        for ((index, step) in theory.howItWorks.withIndex()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CardBackgroundElevated)
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyanSubtle)
                                        .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimaryCyan,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ── When to Use ──
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, AccentGreen.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "WHEN TO USE",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen,
                            fontSize = 10.sp,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Bold
                        )

                        for (useCase in theory.whenToUse) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = AlgoGlyphs.Check,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(14.dp)
                                )
                                Text(
                                    text = useCase,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ── Common Pitfalls ──
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, AccentPink.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "COMMON PITFALLS & EDGE CASES",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentPink,
                            fontSize = 10.sp,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Bold
                        )

                        for (pitfall in theory.commonPitfalls) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = AlgoGlyphs.Warning,
                                    contentDescription = null,
                                    tint = AccentPink,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(14.dp)
                                )
                                Text(
                                    text = pitfall,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ── Engineering Pro-Tip Callout ──
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanSubtle)
                            .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Spark,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "ENGINEERING PRO-TIP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = theory.proTips,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun PropertyBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(color.copy(alpha = 0.14f))
            .border(AlgoTokens.strokeThin, color.copy(alpha = 0.35f), RoundedCornerShape(5.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ComplexityBentoCard(
    label: String,
    rawValue: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val parts = remember(rawValue) { rawValue.split(" - ", limit = 2) }
    val bigO = parts.firstOrNull()?.trim().orEmpty()
    val detail = parts.getOrNull(1)?.trim()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, color.copy(alpha = 0.30f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
        Text(
            text = bigO,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
        )
        if (!detail.isNullOrEmpty()) {
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun AlgorithmTheorySheetPreview() {
    AlgoLensTheme {
        AlgorithmTheorySheet(
            algorithm = Algorithm(id = com.example.algolens.model.AlgorithmId.QUICK_SORT),
            onDismiss = {}
        )
    }
}
