package com.example.algolens.data

import com.example.algolens.model.Algorithm
import com.example.algolens.ui.visualizer.BufferItem
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState
import com.example.algolens.ui.visualizer.VisualizerRenderMode
import com.example.algolens.ui.visualizer.VisualizerStep

/**
 * Repository containing step generators and code templates for all 13 algorithms.
 */
object AlgorithmStepRepository {

    fun generateStepsForAlgorithm(
        algorithm: Algorithm,
        inputArray: List<Int> = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)
    ): List<VisualizerStep> {
        return when (algorithm.name.lowercase().trim()) {
            "bubble sort" -> generateBubbleSort(inputArray)
            "selection sort" -> generateSelectionSort(inputArray)
            "insertion sort" -> generateInsertionSort(inputArray)
            "merge sort" -> generateMergeSort(inputArray)
            "quick sort" -> generateQuickSort(inputArray)
            "linear search" -> generateLinearSearch(inputArray, target = 6)
            "binary search" -> generateBinarySearch(inputArray.sorted(), target = 6)
            "stack" -> generateStackSteps()
            "queue" -> generateQueueSteps()
            "binary search tree" -> generateBSTSteps()
            "heap" -> generateHeapSteps(inputArray.take(7))
            "breadth-first search (bfs)", "bfs" -> generateBFSSteps()
            "depth-first search (dfs)", "dfs" -> generateDFSSteps()
            else -> generateBubbleSort(inputArray)
        }
    }

    fun getCodeLinesForAlgorithm(algorithm: Algorithm): List<String> {
        return when (algorithm.name.lowercase().trim()) {
            "quick sort" -> listOf(
                "def quick_sort(a, low, high):",
                "    if low < high:",
                "        pi = partition(a, low, high)",
                "        quick_sort(a, low, pi - 1)",
                "        quick_sort(a, pi + 1, high)",
                "",
                "def partition(a, low, high):",
                "    pivot = a[high]",
                "    i = low - 1",
                "    for j in range(low, high):",
                "        if a[j] <= pivot:",
                "            i += 1",
                "            a[i], a[j] = a[j], a[i]"
            )
            "bubble sort" -> listOf(
                "def bubble_sort(arr):",
                "    n = len(arr)",
                "    for i in range(n - 1):",
                "        for j in range(n - i - 1):",
                "            if arr[j] > arr[j + 1]:",
                "                arr[j], arr[j+1] = arr[j+1], arr[j]",
                "    return arr"
            )
            "selection sort" -> listOf(
                "def selection_sort(arr):",
                "    n = len(arr)",
                "    for i in range(n - 1):",
                "        min_idx = i",
                "        for j in range(i + 1, n):",
                "            if arr[j] < arr[min_idx]:",
                "                min_idx = j",
                "        arr[i], arr[min_idx] = arr[min_idx], arr[i]"
            )
            "insertion sort" -> listOf(
                "def insertion_sort(arr):",
                "    for i in range(1, len(arr)):",
                "        key = arr[i]",
                "        j = i - 1",
                "        while j >= 0 and arr[j] > key:",
                "            arr[j + 1] = arr[j]",
                "            j -= 1",
                "        arr[j + 1] = key"
            )
            "merge sort" -> listOf(
                "def merge_sort(arr, l, r):",
                "    if l < r:",
                "        m = (l + r) // 2",
                "        merge_sort(arr, l, m)",
                "        merge_sort(arr, m + 1, r)",
                "        merge(arr, l, m, r)"
            )
            "linear search" -> listOf(
                "def linear_search(arr, target):",
                "    for i in range(len(arr)):",
                "        if arr[i] == target:",
                "            return i",
                "    return -1"
            )
            "binary search" -> listOf(
                "def binary_search(arr, target):",
                "    low, high = 0, len(arr) - 1",
                "    while low <= high:",
                "        mid = (low + high) // 2",
                "        if arr[mid] == target:",
                "            return mid",
                "        elif arr[mid] < target:",
                "            low = mid + 1",
                "        else:",
                "            high = mid - 1",
                "    return -1"
            )
            "stack" -> listOf(
                "class Stack:",
                "    def push(self, val):",
                "        self.items.append(val)",
                "    def pop(self):",
                "        return self.items.pop()",
                "    def peek(self):",
                "        return self.items[-1]"
            )
            "queue" -> listOf(
                "class Queue:",
                "    def enqueue(self, val):",
                "        self.items.append(val)",
                "    def dequeue(self):",
                "        return self.items.pop(0)",
                "    def peek(self):",
                "        return self.items[0]"
            )
            "binary search tree" -> listOf(
                "def insert(root, key):",
                "    if root is None:",
                "        return Node(key)",
                "    if key < root.val:",
                "        root.left = insert(root.left, key)",
                "    else:",
                "        root.right = insert(root.right, key)"
            )
            "heap" -> listOf(
                "def heapify(arr, n, i):",
                "    largest = i",
                "    l, r = 2 * i + 1, 2 * i + 2",
                "    if l < n and arr[l] > arr[largest]: largest = l",
                "    if r < n and arr[r] > arr[largest]: largest = r",
                "    if largest != i:",
                "        arr[i], arr[largest] = arr[largest], arr[i]",
                "        heapify(arr, n, largest)"
            )
            "breadth-first search (bfs)", "bfs" -> listOf(
                "def bfs(graph, start):",
                "    visited, queue = set([start]), [start]",
                "    while queue:",
                "        node = queue.pop(0)",
                "        for neighbor in graph[node]:",
                "            if neighbor not in visited:",
                "                visited.add(neighbor)",
                "                queue.append(neighbor)"
            )
            "depth-first search (dfs)", "dfs" -> listOf(
                "def dfs(graph, node, visited=set()):",
                "    visited.add(node)",
                "    for neighbor in graph[node]:",
                "        if neighbor not in visited:",
                "            dfs(graph, neighbor, visited)"
            )
            else -> emptyList()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 1. Bubble Sort
    // ─────────────────────────────────────────────────────────────
    private fun generateBubbleSort(input: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        val n = arr.size
        var sIdx = 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Bubble Sort on ${arr.size} elements",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until n,
                elementStates = emptyMap(),
                activeCodeLines = listOf(1, 2)
            )
        )

        for (i in 0 until n - 1) {
            val unsortedRange = 0..(n - i - 1)
            for (j in 0 until n - i - 1) {
                val compExpr = "COMPARE: ${arr[j]} > ${arr[j + 1]}?"
                val sortedTail = (n - i until n).associateWith { ElementState.SORTED }
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Comparing arr[$j]=${arr[j]} and arr[${j + 1}]=${arr[j + 1]}",
                        comparisonExpr = compExpr,
                        phaseLabel = "COMPARING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = unsortedRange,
                        sortedBoundary = n - i,
                        leftPointer = j,
                        rightPointer = j + 1,
                        elementStates = sortedTail + mapOf(
                            j to ElementState.COMPARING,
                            (j + 1) to ElementState.COMPARING
                        ),
                        bottomPointers = mapOf("j" to j, "j+1" to j + 1),
                        activeCodeLines = listOf(4, 5)
                    )
                )

                if (arr[j] > arr[j + 1]) {
                    val tmp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = tmp

                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "Swapping ${arr[j + 1]} and ${arr[j]}",
                            comparisonExpr = "SWAP: ${arr[j + 1]} <-> ${arr[j]}",
                            phaseLabel = "SWAPPING",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = arr.toList(),
                            activeRange = unsortedRange,
                            sortedBoundary = n - i,
                            leftPointer = j,
                            rightPointer = j + 1,
                            swappedIndices = Pair(j, j + 1),
                            elementStates = sortedTail + mapOf(
                                j to ElementState.SWAPPING,
                                (j + 1) to ElementState.SWAPPING
                            ),
                            bottomPointers = mapOf("j" to j, "j+1" to j + 1),
                            activeCodeLines = listOf(6)
                        )
                    )
                }
            }
            // Mark end as sorted
            val sortedMap = (n - i - 1 until n).associateWith { ElementState.SORTED }
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Element ${arr[n - i - 1]} is locked into sorted tail position",
                    comparisonExpr = "LOCKED: arr[${n - i - 1}]=${arr[n - i - 1]}",
                    phaseLabel = "LOCKED IN TAIL",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = 0 until (n - i - 1),
                    sortedBoundary = n - i - 1,
                    elementStates = sortedMap,
                    activeCodeLines = listOf(3)
                )
            )
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Bubble Sort Complete! Array is fully sorted.",
                comparisonExpr = "SORT COMPLETE",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until n,
                sortedBoundary = 0,
                elementStates = (0 until n).associateWith { ElementState.SORTED },
                activeCodeLines = listOf(7)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Selection Sort
    // ─────────────────────────────────────────────────────────────
    private fun generateSelectionSort(input: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        val n = arr.size
        var sIdx = 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Selection Sort",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until n,
                sortedBoundary = 0,
                activeCodeLines = listOf(1, 2)
            )
        )

        for (i in 0 until n - 1) {
            var minIdx = i
            val sortedLeft = (0 until i).associateWith { ElementState.SORTED }

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Pass $i: Setting minimum candidate at index $i (${arr[i]})",
                    comparisonExpr = "INITIAL MIN: arr[$i]=${arr[i]}",
                    phaseLabel = "MIN SEARCH",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = i until n,
                    sortedBoundary = i,
                    minIndex = minIdx,
                    elementStates = sortedLeft + mapOf(i to ElementState.ACTIVE),
                    topPointers = mapOf("min" to minIdx),
                    bottomPointers = mapOf("i" to i),
                    activeCodeLines = listOf(3, 4)
                )
            )

            for (j in i + 1 until n) {
                val isSmaller = arr[j] < arr[minIdx]
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Comparing arr[$j]=${arr[j]} with current min arr[$minIdx]=${arr[minIdx]}",
                        comparisonExpr = "COMPARE: ${arr[j]} < ${arr[minIdx]}?",
                        phaseLabel = "MIN SEARCH",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = i until n,
                        sortedBoundary = i,
                        minIndex = minIdx,
                        leftPointer = minIdx,
                        rightPointer = j,
                        elementStates = sortedLeft + mapOf(
                            minIdx to ElementState.ACTIVE,
                            j to ElementState.COMPARING
                        ),
                        topPointers = mapOf("min" to minIdx),
                        bottomPointers = mapOf("i" to i, "j" to j),
                        activeCodeLines = listOf(5, 6)
                    )
                )

                if (isSmaller) {
                    minIdx = j
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "Found new smaller minimum: arr[$minIdx]=${arr[minIdx]}",
                            comparisonExpr = "NEW MIN: arr[$minIdx]=${arr[minIdx]}",
                            phaseLabel = "NEW MIN FOUND",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = arr.toList(),
                            activeRange = i until n,
                            sortedBoundary = i,
                            minIndex = minIdx,
                            elementStates = sortedLeft + mapOf(minIdx to ElementState.FOUND),
                            topPointers = mapOf("min" to minIdx),
                            bottomPointers = mapOf("i" to i, "j" to j),
                            activeCodeLines = listOf(7)
                        )
                    )
                }
            }

            if (minIdx != i) {
                val tmp = arr[i]
                arr[i] = arr[minIdx]
                arr[minIdx] = tmp

                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Swapping minimum ${arr[i]} into sorted slot $i",
                        comparisonExpr = "SWAP: arr[$i] <-> arr[$minIdx]",
                        phaseLabel = "SWAPPING MIN",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = i until n,
                        sortedBoundary = i,
                        minIndex = minIdx,
                        swappedIndices = Pair(i, minIdx),
                        elementStates = sortedLeft + mapOf(
                            i to ElementState.SWAPPING,
                            minIdx to ElementState.SWAPPING
                        ),
                        topPointers = mapOf("min" to minIdx),
                        bottomPointers = mapOf("i" to i),
                        activeCodeLines = listOf(8)
                    )
                )
            }

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Position $i locked into sorted region (${arr[i]})",
                    comparisonExpr = "SORTED: [0..$i]",
                    phaseLabel = "PASS COMPLETE",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = (i + 1) until n,
                    sortedBoundary = i + 1,
                    elementStates = (0..i).associateWith { ElementState.SORTED },
                    bottomPointers = mapOf("i" to i),
                    activeCodeLines = listOf(8)
                )
            )
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Selection Sort Complete! Array sorted.",
                comparisonExpr = "SORT COMPLETE",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until n,
                sortedBoundary = n,
                elementStates = (0 until n).associateWith { ElementState.SORTED },
                activeCodeLines = listOf(8)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Insertion Sort
    // ─────────────────────────────────────────────────────────────
    private fun generateInsertionSort(input: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        val n = arr.size
        var sIdx = 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Insertion Sort",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                sortedBoundary = 1,
                elementStates = mapOf(0 to ElementState.SORTED),
                activeCodeLines = listOf(1)
            )
        )

        for (i in 1 until n) {
            val key = arr[i]
            var j = i - 1
            val sortedPrefix = (0 until i).associateWith { ElementState.SORTED }

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Elevating key arr[$i]=$key to inspect insertion into sorted prefix [0..${i - 1}]",
                    comparisonExpr = "ELEVATED KEY: $key at [$i]",
                    phaseLabel = "KEY ELEVATED",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    sortedBoundary = i,
                    floatingElement = Pair(key, i),
                    elementStates = sortedPrefix + mapOf(i to ElementState.TARGET),
                    topPointers = mapOf("key" to i),
                    bottomPointers = mapOf("i" to i, "j" to j),
                    activeCodeLines = listOf(2, 3, 4)
                )
            )

            while (j >= 0 && arr[j] > key) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "arr[$j]=${arr[j]} > key=$key. Shifting ${arr[j]} one position right.",
                        comparisonExpr = "SHIFT: ${arr[j]} > $key",
                        phaseLabel = "SHIFTING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        sortedBoundary = i,
                        floatingElement = Pair(key, i),
                        elementStates = sortedPrefix + mapOf(
                            j to ElementState.SWAPPING,
                            (j + 1) to ElementState.COMPARING
                        ),
                        topPointers = mapOf("key" to (j + 1)),
                        bottomPointers = mapOf("j" to j),
                        activeCodeLines = listOf(5, 6, 7)
                    )
                )
                arr[j + 1] = arr[j]
                j--
            }
            arr[j + 1] = key

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Dropped key $key into cleared position ${j + 1}",
                    comparisonExpr = "PLACED: key $key at [${j + 1}]",
                    phaseLabel = "KEY INSERTED",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    sortedBoundary = i + 1,
                    floatingElement = null,
                    elementStates = (0..i).associateWith { ElementState.SORTED },
                    bottomPointers = mapOf("i" to i),
                    activeCodeLines = listOf(8)
                )
            )
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Insertion Sort Complete! All elements sorted.",
                comparisonExpr = "SORT COMPLETE",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                sortedBoundary = n,
                elementStates = (0 until n).associateWith { ElementState.SORTED },
                activeCodeLines = listOf(8)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Merge Sort (2-Tier Split & Merge View)
    // ─────────────────────────────────────────────────────────────
    private fun generateMergeSort(input: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        var sIdx = 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Divide & Conquer Merge Sort",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until arr.size,
                mergeBlocks = listOf(0 until arr.size),
                recursionDepth = 0,
                activeCodeLines = listOf(1, 2)
            )
        )

        fun merge(l: Int, m: Int, r: Int, depth: Int) {
            val left = arr.subList(l, m + 1).toList()
            val right = arr.subList(m + 1, r + 1).toList()
            var i = 0
            var j = 0
            var k = l

            val aux = left + right
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Merging blocks [$l..$m] (${left}) and [${m + 1}..$r] (${right})",
                    comparisonExpr = "MERGE RANGE: [$l..$r]",
                    phaseLabel = "MERGING",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = l..r,
                    recursionDepth = depth,
                    mergeBlocks = listOf(l..m, (m + 1)..r),
                    auxiliaryArray = aux,
                    auxiliaryIndices = mapOf("i" to 0, "j" to left.size, "k" to l),
                    elementStates = (l..r).associateWith { ElementState.COMPARING },
                    topPointers = mapOf("L" to l, "M" to m, "R" to r),
                    bottomPointers = mapOf("k" to k),
                    activeCodeLines = listOf(6)
                )
            )

            val mergedBuffer = mutableListOf<Int>()
            while (i < left.size && j < right.size) {
                val compVal = if (left[i] <= right[j]) left[i] else right[j]
                val desc = if (left[i] <= right[j]) {
                    "Left element arr[${l + i}]=${left[i]} <= Right element arr[${m + 1 + j}]=${right[j]}"
                } else {
                    "Right element arr[${m + 1 + j}]=${right[j]} < Left element arr[${l + i}]=${left[i]}"
                }

                if (left[i] <= right[j]) {
                    arr[k] = left[i]
                    mergedBuffer.add(left[i])
                    i++
                } else {
                    arr[k] = right[j]
                    mergedBuffer.add(right[j])
                    j++
                }

                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "$desc -> Merged $compVal into slot $k",
                        comparisonExpr = "MERGED: arr[$k] = $compVal",
                        phaseLabel = "MERGING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = l..r,
                        recursionDepth = depth,
                        mergeBlocks = listOf(l..m, (m + 1)..r),
                        auxiliaryArray = aux,
                        auxiliaryIndices = mapOf("i" to i, "j" to (left.size + j), "k" to k),
                        elementStates = (l..k).associateWith { ElementState.ACTIVE } + ((k + 1)..r).associateWith { ElementState.COMPARING },
                        bottomPointers = mapOf("k" to k),
                        activeCodeLines = listOf(6)
                    )
                )
                k++
            }

            while (i < left.size) {
                arr[k] = left[i]
                mergedBuffer.add(left[i])
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Flushing remaining left element ${left[i]} to index $k",
                        comparisonExpr = "FLUSH LEFT: arr[$k] = ${left[i]}",
                        phaseLabel = "MERGING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = l..r,
                        recursionDepth = depth,
                        mergeBlocks = listOf(l..m, (m + 1)..r),
                        auxiliaryArray = aux,
                        auxiliaryIndices = mapOf("i" to i, "k" to k),
                        elementStates = (l..k).associateWith { ElementState.ACTIVE },
                        bottomPointers = mapOf("k" to k),
                        activeCodeLines = listOf(6)
                    )
                )
                i++
                k++
            }
            while (j < right.size) {
                arr[k] = right[j]
                mergedBuffer.add(right[j])
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Flushing remaining right element ${right[j]} to index $k",
                        comparisonExpr = "FLUSH RIGHT: arr[$k] = ${right[j]}",
                        phaseLabel = "MERGING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = l..r,
                        recursionDepth = depth,
                        mergeBlocks = listOf(l..m, (m + 1)..r),
                        auxiliaryArray = aux,
                        auxiliaryIndices = mapOf("j" to (left.size + j), "k" to k),
                        elementStates = (l..k).associateWith { ElementState.ACTIVE },
                        bottomPointers = mapOf("k" to k),
                        activeCodeLines = listOf(6)
                    )
                )
                j++
                k++
            }

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Completed merge for range [$l..$r]",
                    comparisonExpr = "MERGE COMPLETE: [$l..$r]",
                    phaseLabel = "MERGE COMPLETE",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = l..r,
                    recursionDepth = depth,
                    mergeBlocks = listOf(l..r),
                    elementStates = (l..r).associateWith { ElementState.ACTIVE },
                    activeCodeLines = listOf(6)
                )
            )
        }

        fun sort(l: Int, r: Int, depth: Int) {
            if (l < r) {
                val m = (l + r) / 2
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Dividing subarray [$l..$r] at midpoint $m into blocks [$l..$m] and [${m + 1}..$r]",
                        comparisonExpr = "SPLIT: mid=$m",
                        phaseLabel = "DIVIDING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = l..r,
                        recursionDepth = depth,
                        mergeBlocks = listOf(l..m, (m + 1)..r),
                        topPointers = mapOf("L" to l, "M" to m, "R" to r),
                        activeCodeLines = listOf(3, 4, 5)
                    )
                )
                sort(l, m, depth + 1)
                sort(m + 1, r, depth + 1)
                merge(l, m, r, depth)
            }
        }

        sort(0, arr.size - 1, 0)

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Merge Sort Complete! All sub-arrays merged and sorted.",
                comparisonExpr = "SORT COMPLETE",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until arr.size,
                elementStates = (0 until arr.size).associateWith { ElementState.SORTED },
                activeCodeLines = listOf(6)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 5. Quick Sort (In-Place Partitioning View)
    // ─────────────────────────────────────────────────────────────
    private fun generateQuickSort(input: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        var sIdx = 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting QuickSort on array of ${arr.size} elements",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until arr.size,
                activeCodeLines = listOf(1, 2)
            )
        )

        fun partition(low: Int, high: Int): Int {
            val pivot = arr[high]
            var i = low - 1

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Partition [$low..$high]: Selected pivot = $pivot at index $high",
                    comparisonExpr = "PIVOT: $pivot at index $high",
                    phaseLabel = "PARTITIONING",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = low..high,
                    pivotIndex = high,
                    leftPointer = low,
                    rightPointer = high,
                    elementStates = mapOf(high to ElementState.PIVOT),
                    topPointers = mapOf("L" to low, "pivot" to high),
                    bottomPointers = mapOf("i" to low.coerceAtLeast(0)),
                    activeCodeLines = listOf(8, 9)
                )
            )

            for (j in low until high) {
                val isSmallerOrEqual = arr[j] <= pivot
                val expr = "COMPARE: ${arr[j]} <= $pivot?"

                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Comparing arr[$j]=${arr[j]} with pivot $pivot",
                        comparisonExpr = expr,
                        phaseLabel = "SCANNING",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = arr.toList(),
                        activeRange = low..high,
                        pivotIndex = high,
                        leftPointer = i.coerceAtLeast(0),
                        rightPointer = j,
                        elementStates = mapOf(
                            j to ElementState.COMPARING,
                            high to ElementState.PIVOT
                        ) + (if (i >= 0) mapOf(i to ElementState.ACTIVE) else emptyMap()),
                        topPointers = mapOf("L" to low, "pivot" to high),
                        bottomPointers = mapOf(
                            "i" to i.coerceAtLeast(0),
                            "j" to j
                        ),
                        activeCodeLines = listOf(10, 11)
                    )
                )

                if (isSmallerOrEqual) {
                    i++
                    val tmp = arr[i]
                    arr[i] = arr[j]
                    arr[j] = tmp

                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$j] <= pivot. Swapped arr[$i] (${arr[i]}) with arr[$j] (${arr[j]})",
                            comparisonExpr = "SWAP: arr[$i] <-> arr[$j]",
                            phaseLabel = "SWAPPING",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = arr.toList(),
                            activeRange = low..high,
                            pivotIndex = high,
                            leftPointer = i,
                            rightPointer = j,
                            swappedIndices = Pair(i, j),
                            elementStates = mapOf(
                                i to ElementState.SWAPPING,
                                j to ElementState.SWAPPING,
                                high to ElementState.PIVOT
                            ),
                            topPointers = mapOf("L" to low, "pivot" to high),
                            bottomPointers = mapOf("i" to i, "j" to j),
                            activeCodeLines = listOf(12, 13)
                        )
                    )
                }
            }

            // Place pivot in correct slot
            val tmp = arr[i + 1]
            arr[i + 1] = arr[high]
            arr[high] = tmp

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Placed pivot $pivot into final sorted slot ${i + 1}",
                    comparisonExpr = "PIVOT PLACED: slot ${i + 1}",
                    phaseLabel = "PIVOT PLACED",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = low..high,
                    pivotIndex = i + 1,
                    swappedIndices = Pair(i + 1, high),
                    elementStates = mapOf((i + 1) to ElementState.SORTED),
                    topPointers = mapOf("pivot" to (i + 1)),
                    bottomPointers = mapOf("i+1" to (i + 1)),
                    activeCodeLines = listOf(13)
                )
            )

            return i + 1
        }

        fun quickSort(low: Int, high: Int) {
            if (low < high) {
                val pi = partition(low, high)
                quickSort(low, pi - 1)
                quickSort(pi + 1, high)
            }
        }

        quickSort(0, arr.size - 1)

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "QuickSort Complete! All elements partitioned and sorted.",
                comparisonExpr = "SORT COMPLETE",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until arr.size,
                elementStates = (0 until arr.size).associateWith { ElementState.SORTED },
                activeCodeLines = listOf(1, 2)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 6. Linear Search
    // ─────────────────────────────────────────────────────────────
    private fun generateLinearSearch(input: List<Int>, target: Int): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Linear Search for target = $target",
                comparisonExpr = "TARGET: $target",
                renderMode = VisualizerRenderMode.CELLS,
                array = input,
                topPointers = mapOf("target" to 0),
                activeCodeLines = listOf(1, 2)
            )
        )

        for (i in input.indices) {
            val isFound = input[i] == target
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Checking index $i: arr[$i]=${input[i]} == $target?",
                    comparisonExpr = "COMPARE: ${input[i]} == $target?",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = input,
                    elementStates = mapOf(i to if (isFound) ElementState.FOUND else ElementState.COMPARING),
                    bottomPointers = mapOf("i" to i),
                    activeCodeLines = listOf(3)
                )
            )

            if (isFound) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Target $target FOUND at index $i!",
                        comparisonExpr = "FOUND: index $i",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = input,
                        elementStates = mapOf(i to ElementState.FOUND),
                        bottomPointers = mapOf("found" to i),
                        activeCodeLines = listOf(4)
                    )
                )
                return steps
            }
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Target $target not found in array.",
                renderMode = VisualizerRenderMode.CELLS,
                array = input,
                activeCodeLines = listOf(5)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 7. Binary Search
    // ─────────────────────────────────────────────────────────────
    private fun generateBinarySearch(sortedInput: List<Int>, target: Int): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        var low = 0
        var high = sortedInput.size - 1

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Binary Search on sorted array for target = $target",
                comparisonExpr = "TARGET: $target",
                renderMode = VisualizerRenderMode.CELLS,
                array = sortedInput,
                topPointers = mapOf("L" to low, "H" to high),
                activeCodeLines = listOf(1, 2)
            )
        )

        while (low <= high) {
            val mid = (low + high) / 2
            val midVal = sortedInput[mid]

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Range [$low..$high]: Mid index $mid (value $midVal)",
                    comparisonExpr = "MID: arr[$mid] = $midVal",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = sortedInput,
                    elementStates = mapOf(mid to ElementState.COMPARING),
                    topPointers = mapOf("L" to low, "H" to high),
                    bottomPointers = mapOf("mid" to mid),
                    activeCodeLines = listOf(3, 4, 5)
                )
            )

            if (midVal == target) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Target $target FOUND at index $mid!",
                        comparisonExpr = "FOUND: index $mid ($target)",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = sortedInput,
                        elementStates = mapOf(mid to ElementState.FOUND),
                        topPointers = mapOf("L" to low, "H" to high),
                        bottomPointers = mapOf("mid" to mid),
                        activeCodeLines = listOf(6)
                    )
                )
                return steps
            } else if (midVal < target) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "arr[$mid]=$midVal < target=$target. Eliminating left half. New low = ${mid + 1}",
                        comparisonExpr = "$midVal < $target -> low = ${mid + 1}",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = sortedInput,
                        topPointers = mapOf("L" to (mid + 1).coerceAtMost(sortedInput.size - 1), "H" to high),
                        activeCodeLines = listOf(7, 8)
                    )
                )
                low = mid + 1
            } else {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "arr[$mid]=$midVal > target=$target. Eliminating right half. New high = ${mid - 1}",
                        comparisonExpr = "$midVal > $target -> high = ${mid - 1}",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = sortedInput,
                        topPointers = mapOf("L" to low, "H" to (mid - 1).coerceAtLeast(0)),
                        activeCodeLines = listOf(9, 10)
                    )
                )
                high = mid - 1
            }
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Target $target not found in sorted array.",
                renderMode = VisualizerRenderMode.CELLS,
                array = sortedInput,
                activeCodeLines = listOf(11)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 8. Stack
    // ─────────────────────────────────────────────────────────────
    private fun generateStackSteps(): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val items = mutableListOf<BufferItem>()

        fun addStep(desc: String, codeLine: Int) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = items.toList(),
                    bufferLabel = "Stack · LIFO (Last In First Out)",
                    activeCodeLines = listOf(codeLine)
                )
            )
        }

        addStep("Created empty Stack", 1)

        items.add(BufferItem("1", "10", ElementState.ACTIVE))
        addStep("push(10) -> Pushed 10 onto stack top", 2)
        items[0] = items[0].copy(state = ElementState.IDLE)

        items.add(BufferItem("2", "25", ElementState.ACTIVE))
        addStep("push(25) -> Pushed 25 onto stack top", 2)
        items[1] = items[1].copy(state = ElementState.IDLE)

        items.add(BufferItem("3", "42", ElementState.ACTIVE))
        addStep("push(42) -> Pushed 42 onto stack top", 2)
        items[2] = items[2].copy(state = ElementState.IDLE)

        items[2] = items[2].copy(state = ElementState.FOUND)
        addStep("peek() -> Top element is 42", 6)
        items[2] = items[2].copy(state = ElementState.IDLE)

        items.removeAt(items.size - 1)
        addStep("pop() -> Popped 42 from stack top. New top is 25", 4)

        items.add(BufferItem("4", "88", ElementState.ACTIVE))
        addStep("push(88) -> Pushed 88 onto stack top", 2)

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 9. Queue
    // ─────────────────────────────────────────────────────────────
    private fun generateQueueSteps(): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val items = mutableListOf<BufferItem>()

        fun addStep(desc: String, codeLine: Int) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = items.toList(),
                    bufferLabel = "Queue · FIFO (First In First Out)",
                    activeCodeLines = listOf(codeLine)
                )
            )
        }

        addStep("Created empty Queue", 1)

        items.add(BufferItem("1", "15", ElementState.ACTIVE))
        addStep("enqueue(15) -> Added 15 to rear of queue", 2)
        items[0] = items[0].copy(state = ElementState.IDLE)

        items.add(BufferItem("2", "30", ElementState.ACTIVE))
        addStep("enqueue(30) -> Added 30 to rear of queue", 2)
        items[1] = items[1].copy(state = ElementState.IDLE)

        items.add(BufferItem("3", "45", ElementState.ACTIVE))
        addStep("enqueue(45) -> Added 45 to rear of queue", 2)
        items[2] = items[2].copy(state = ElementState.IDLE)

        items[0] = items[0].copy(state = ElementState.FOUND)
        addStep("peek() -> Front element is 15", 6)
        items[0] = items[0].copy(state = ElementState.IDLE)

        items.removeAt(0)
        addStep("dequeue() -> Removed 15 from front. New front is 30", 4)

        items.add(BufferItem("4", "60", ElementState.ACTIVE))
        addStep("enqueue(60) -> Added 60 to rear of queue", 2)

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 10. Binary Search Tree (BST)
    // ─────────────────────────────────────────────────────────────
    private fun generateBSTSteps(): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        val nodes = listOf(
            GraphNodeState("50", "50", 100f, 15f),
            GraphNodeState("30", "30", 50f, 50f),
            GraphNodeState("70", "70", 150f, 50f),
            GraphNodeState("20", "20", 25f, 90f),
            GraphNodeState("40", "40", 75f, 90f),
            GraphNodeState("60", "60", 125f, 90f),
            GraphNodeState("80", "80", 175f, 90f)
        )

        val edges = listOf(
            GraphEdgeState("50", "30", isDirected = true),
            GraphEdgeState("50", "70", isDirected = true),
            GraphEdgeState("30", "20", isDirected = true),
            GraphEdgeState("30", "40", isDirected = true),
            GraphEdgeState("70", "60", isDirected = true),
            GraphEdgeState("70", "80", isDirected = true)
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Binary Search Tree: Searching for key = 40",
                comparisonExpr = "SEARCH: target = 40",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes,
                edges = edges,
                activeCodeLines = listOf(1, 2)
            )
        )

        // Step 1: Compare with Root 50
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "40 < root(50) -> Traverse LEFT subtree to node 30",
                comparisonExpr = "40 < 50 -> LEFT",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { if (it.id == "50") it.copy(state = ElementState.COMPARING) else it },
                edges = edges.map { if (it.from == "50" && it.to == "30") it.copy(isHighlighted = true) else it },
                activeNodeId = "50",
                activeCodeLines = listOf(4, 5)
            )
        )

        // Step 2: Compare with 30
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "40 > node(30) -> Traverse RIGHT subtree to node 40",
                comparisonExpr = "40 > 30 -> RIGHT",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "50" -> it.copy(state = ElementState.VISITED)
                        "30" -> it.copy(state = ElementState.COMPARING)
                        else -> it
                    }
                },
                edges = edges.map {
                    when {
                        it.from == "50" && it.to == "30" -> it.copy(isHighlighted = true)
                        it.from == "30" && it.to == "40" -> it.copy(isHighlighted = true)
                        else -> it
                    }
                },
                activeNodeId = "30",
                activeCodeLines = listOf(6, 7)
            )
        )

        // Step 3: Match 40
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Found key 40 in BST!",
                comparisonExpr = "FOUND: Node 40",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "40" -> it.copy(state = ElementState.FOUND)
                        "50", "30" -> it.copy(state = ElementState.VISITED)
                        else -> it
                    }
                },
                edges = edges.map {
                    when {
                        it.from == "50" && it.to == "30" -> it.copy(isHighlighted = true)
                        it.from == "30" && it.to == "40" -> it.copy(isHighlighted = true)
                        else -> it
                    }
                },
                activeNodeId = "40",
                activeCodeLines = listOf(2, 3)
            )
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 11. Heap (Max-Heap)
    // ─────────────────────────────────────────────────────────────
    private fun generateHeapSteps(input: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        val nodes = listOf(
            GraphNodeState("0", "90", 100f, 15f),
            GraphNodeState("1", "80", 50f, 50f),
            GraphNodeState("2", "70", 150f, 50f),
            GraphNodeState("3", "40", 25f, 90f),
            GraphNodeState("4", "50", 75f, 90f),
            GraphNodeState("5", "30", 125f, 90f),
            GraphNodeState("6", "60", 175f, 90f)
        )

        val edges = listOf(
            GraphEdgeState("0", "1", isDirected = true),
            GraphEdgeState("0", "2", isDirected = true),
            GraphEdgeState("1", "3", isDirected = true),
            GraphEdgeState("1", "4", isDirected = true),
            GraphEdgeState("2", "5", isDirected = true),
            GraphEdgeState("2", "6", isDirected = true)
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Max-Heap: Root element (90) satisfies parent >= children property",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes,
                edges = edges,
                activeCodeLines = listOf(1, 2)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Comparing left child 80 and right child 70 with root 90",
                comparisonExpr = "HEAPIFY: 90 > max(80, 70)",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "0" -> it.copy(state = ElementState.ACTIVE)
                        "1", "2" -> it.copy(state = ElementState.COMPARING)
                        else -> it
                    }
                },
                edges = edges.map { if (it.from == "0") it.copy(isHighlighted = true) else it },
                activeNodeId = "0",
                activeCodeLines = listOf(3, 4, 5)
            )
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 12. Breadth-First Search (BFS)
    // ─────────────────────────────────────────────────────────────
    private fun generateBFSSteps(): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        val nodes = listOf(
            GraphNodeState("A", "A", 40f, 25f),
            GraphNodeState("B", "B", 110f, 15f),
            GraphNodeState("C", "C", 40f, 85f),
            GraphNodeState("D", "D", 110f, 85f),
            GraphNodeState("E", "E", 180f, 50f)
        )

        val edges = listOf(
            GraphEdgeState("A", "B", isDirected = false),
            GraphEdgeState("A", "C", isDirected = false),
            GraphEdgeState("B", "D", isDirected = false),
            GraphEdgeState("C", "D", isDirected = false),
            GraphEdgeState("D", "E", isDirected = false)
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS: Starting at node A. Enqueued A.",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { if (it.id == "A") it.copy(state = ElementState.ACTIVE) else it },
                edges = edges,
                activeNodeId = "A",
                visitedNodeIds = setOf("A"),
                activeCodeLines = listOf(1, 2)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS: Dequeued A. Visiting unvisited neighbors B and C. Queue = [B, C]",
                comparisonExpr = "QUEUE: [B, C]",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "A" -> it.copy(state = ElementState.VISITED)
                        "B", "C" -> it.copy(state = ElementState.COMPARING)
                        else -> it
                    }
                },
                edges = edges.map { if (it.from == "A") it.copy(isHighlighted = true) else it },
                activeNodeId = "A",
                visitedNodeIds = setOf("A", "B", "C"),
                activeCodeLines = listOf(3, 4, 5, 6, 7)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS: Dequeued B. Visiting neighbor D. Queue = [C, D]",
                comparisonExpr = "QUEUE: [C, D]",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "B" -> it.copy(state = ElementState.ACTIVE)
                        "D" -> it.copy(state = ElementState.COMPARING)
                        "A", "C" -> it.copy(state = ElementState.VISITED)
                        else -> it
                    }
                },
                edges = edges.map {
                    when {
                        it.from == "A" || (it.from == "B" && it.to == "D") -> it.copy(isHighlighted = true)
                        else -> it
                    }
                },
                activeNodeId = "B",
                visitedNodeIds = setOf("A", "B", "C", "D"),
                activeCodeLines = listOf(3, 4, 5, 6, 7)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS: Dequeued D. Visiting neighbor E. Queue = [E]",
                comparisonExpr = "QUEUE: [E]",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "D" -> it.copy(state = ElementState.ACTIVE)
                        "E" -> it.copy(state = ElementState.FOUND)
                        else -> it.copy(state = ElementState.VISITED)
                    }
                },
                edges = edges.map { it.copy(isHighlighted = true) },
                activeNodeId = "D",
                visitedNodeIds = setOf("A", "B", "C", "D", "E"),
                activeCodeLines = listOf(3, 4, 5, 6, 7)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS Traversal Complete! All nodes visited level by level.",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { it.copy(state = ElementState.VISITED) },
                edges = edges.map { it.copy(isHighlighted = true) },
                visitedNodeIds = setOf("A", "B", "C", "D", "E"),
                activeCodeLines = listOf(3)
            )
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 13. Depth-First Search (DFS)
    // ─────────────────────────────────────────────────────────────
    private fun generateDFSSteps(): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        val nodes = listOf(
            GraphNodeState("A", "A", 40f, 25f),
            GraphNodeState("B", "B", 110f, 15f),
            GraphNodeState("C", "C", 40f, 85f),
            GraphNodeState("D", "D", 110f, 85f),
            GraphNodeState("E", "E", 180f, 50f)
        )

        val edges = listOf(
            GraphEdgeState("A", "B", isDirected = false),
            GraphEdgeState("A", "C", isDirected = false),
            GraphEdgeState("B", "D", isDirected = false),
            GraphEdgeState("C", "D", isDirected = false),
            GraphEdgeState("D", "E", isDirected = false)
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "DFS: Starting at root node A. Exploring depth branch A -> B",
                comparisonExpr = "CALL STACK: [dfs(A)]",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { if (it.id == "A") it.copy(state = ElementState.ACTIVE) else it },
                edges = edges,
                activeNodeId = "A",
                visitedNodeIds = setOf("A"),
                activeCodeLines = listOf(1, 2)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "DFS: Reached node B. Exploring deeper to node D.",
                comparisonExpr = "CALL STACK: [dfs(A), dfs(B)]",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "A" -> it.copy(state = ElementState.VISITED)
                        "B" -> it.copy(state = ElementState.ACTIVE)
                        else -> it
                    }
                },
                edges = edges.map { if (it.from == "A" && it.to == "B") it.copy(isHighlighted = true) else it },
                activeNodeId = "B",
                visitedNodeIds = setOf("A", "B"),
                activeCodeLines = listOf(3, 4, 5)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "DFS: Reached node D. Exploring deeper to node E.",
                comparisonExpr = "CALL STACK: [dfs(A), dfs(B), dfs(D)]",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "A", "B" -> it.copy(state = ElementState.VISITED)
                        "D" -> it.copy(state = ElementState.ACTIVE)
                        else -> it
                    }
                },
                edges = edges.map {
                    when {
                        it.from == "A" && it.to == "B" -> it.copy(isHighlighted = true)
                        it.from == "B" && it.to == "D" -> it.copy(isHighlighted = true)
                        else -> it
                    }
                },
                activeNodeId = "D",
                visitedNodeIds = setOf("A", "B", "D"),
                activeCodeLines = listOf(3, 4, 5)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "DFS: Reached leaf node E. Backtracking.",
                comparisonExpr = "BACKTRACK: dfs(E) returns",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map {
                    when (it.id) {
                        "E" -> it.copy(state = ElementState.FOUND)
                        else -> it.copy(state = ElementState.VISITED)
                    }
                },
                edges = edges.map { it.copy(isHighlighted = true) },
                activeNodeId = "E",
                visitedNodeIds = setOf("A", "B", "D", "E"),
                activeCodeLines = listOf(5)
            )
        )

        return steps
    }
}
