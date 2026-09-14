package com.example.algolens.data

import android.util.Log
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.BufferOp
import com.example.algolens.model.QueueOp
import com.example.algolens.model.SortOrder
import com.example.algolens.ui.visualizer.BufferItem
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState
import com.example.algolens.ui.visualizer.VisualizerRenderMode
import com.example.algolens.ui.visualizer.VisualizerStep

/**
 * Repository containing step generators for every algorithm registered in
 * [AlgorithmRegistry]. The previous `when (name)`-style dispatcher has been
 * replaced by a single typed lookup against [AlgorithmId] — adding a new
 * algorithm now only requires an entry in [AlgorithmRegistry], never a
 * change to this class.
 *
 * NOTE: the legacy `getCodeLinesForAlgorithm(...)` method has been removed.
 * Code listings are owned by [com.example.algolens.ui.visualizer.AlgorithmCodeRegistry]
 * in `CodeTracePane.kt` and the [com.example.algolens.FeatureEnhancementsTest]
 * already tests that path. Keeping a parallel Python-only registry here
 * produced drift.
 */
object AlgorithmStepRepository {

    private const val TAG = "AlgorithmStepRepository"

    /**
     * Default input used when an algorithm's [AlgorithmSpec.defaultInput] is
     * not overridden. Kept as a single source of truth so the test suite
     * can predict what the visualizer will see.
     */
    val DEFAULT_INPUT: List<Int> = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7)

    /**
     * Generate the step stream for an algorithm.
     *
     * Falls back to an empty list (and logs a warning) for unknown
     * algorithms instead of silently routing to Bubble Sort. This was the
     * previous behaviour and masked any new algorithm that was added to
     * the dashboard but not to the dispatcher.
     */
    fun generateStepsForAlgorithm(
        algorithm: Algorithm,
        inputArray: List<Int> = DEFAULT_INPUT,
        sortOrder: SortOrder = SortOrder.ASC,
        searchTarget: Int? = null,
        bufferOps: List<BufferOp> = defaultStackOps(),
        queueOps: List<QueueOp> = defaultQueueOps(),
        bstValues: List<Int> = listOf(50, 30, 70, 20, 40, 60, 80),
        bstSearchKey: Int = 40,
        traversalStartNodeId: String = "A",
    ): List<VisualizerStep> {
        if (AlgorithmRegistry.specFor(algorithm.id) == null) {
            Log.w(TAG, "No spec registered for ${algorithm.id} -- returning empty steps.")
            return emptyList()
        }

        return when (algorithm.id) {
            AlgorithmId.BUBBLE_SORT -> generateBubbleSort(inputArray, sortOrder)
            AlgorithmId.SELECTION_SORT -> generateSelectionSort(inputArray, sortOrder)
            AlgorithmId.INSERTION_SORT -> generateInsertionSort(inputArray, sortOrder)
            AlgorithmId.MERGE_SORT -> generateMergeSort(inputArray, sortOrder)
            AlgorithmId.QUICK_SORT -> generateQuickSort(inputArray, sortOrder)
            AlgorithmId.LINEAR_SEARCH -> generateLinearSearch(inputArray, target = searchTarget ?: 6, sortOrder)
            AlgorithmId.BINARY_SEARCH -> generateBinarySearch(
                sortedInput = if (sortOrder == SortOrder.ASC) inputArray.sorted() else inputArray.sortedDescending(),
                target = searchTarget ?: 6,
                sortOrder = sortOrder
            )
            AlgorithmId.STACK -> generateStackSteps(bufferOps)
            AlgorithmId.QUEUE -> generateQueueSteps(queueOps)
            AlgorithmId.BINARY_SEARCH_TREE -> generateBSTSteps(bstValues, bstSearchKey)
            AlgorithmId.HEAP -> generateHeapSteps(inputArray.take(7), sortOrder)
            AlgorithmId.BFS -> generateBFSSteps(traversalStartNodeId)
            AlgorithmId.DFS -> generateDFSSteps(traversalStartNodeId)
        }
    }

    /**
     * Generate steps keyed directly by [AlgorithmId], bypassing the
     * `Algorithm` UI handle. Prefer this in tests and in code paths that
     * already have a stable identifier.
     */
    fun generateStepsForId(
        id: AlgorithmId,
        inputArray: List<Int> = DEFAULT_INPUT,
        sortOrder: SortOrder = SortOrder.ASC,
    ): List<VisualizerStep> = generateStepsForAlgorithm(Algorithm(id = id), inputArray, sortOrder)

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 1. Bubble Sort
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateBubbleSort(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        val n = arr.size
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: largest bubbles right (sorted tail = right). DESC: smallest
        // bubbles right (sorted tail = right), so the operator flips.
        val op = if (isDesc) "<" else ">"
        val outOfOrder = { a: Int, b: Int -> if (isDesc) a < b else a > b }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Bubble Sort on ${arr.size} elements (${sortOrder.name})",
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
                val compExpr = "COMPARE: ${arr[j]} $op ${arr[j + 1]}?"
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

                if (outOfOrder(arr[j], arr[j + 1])) {
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
                description = "Bubble Sort Complete! Array is fully sorted (${sortOrder.name}).",
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 2. Selection Sort
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateSelectionSort(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        val n = arr.size
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: track the smallest (min). DESC: track the largest (max).
        val op = if (isDesc) ">" else "<"
        val extremeLabel = if (isDesc) "max" else "min"
        val extremeFoundLabel = if (isDesc) "NEW MAX FOUND" else "NEW MIN FOUND"
        val extremePhase = if (isDesc) "MAX SEARCH" else "MIN SEARCH"
        val isBetter = { candidate: Int, current: Int -> if (isDesc) candidate > current else candidate < current }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Selection Sort (${sortOrder.name})",
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
                    description = "Pass $i: Setting $extremeLabel candidate at index $i (${arr[i]})",
                    comparisonExpr = "INITIAL $extremeLabel.uppercase(): arr[$i]=${arr[i]}",
                    phaseLabel = extremePhase,
                    renderMode = VisualizerRenderMode.CELLS,
                    array = arr.toList(),
                    activeRange = i until n,
                    sortedBoundary = i,
                    minIndex = minIdx,
                    elementStates = sortedLeft + mapOf(i to ElementState.ACTIVE),
                    topPointers = mapOf(extremeLabel to minIdx),
                    bottomPointers = mapOf("i" to i),
                    activeCodeLines = listOf(3, 4)
                )
            )

            for (j in i + 1 until n) {
                val isBetterCandidate = isBetter(arr[j], arr[minIdx])
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Comparing arr[$j]=${arr[j]} with current $extremeLabel arr[$minIdx]=${arr[minIdx]}",
                        comparisonExpr = "COMPARE: ${arr[j]} $op ${arr[minIdx]}?",
                        phaseLabel = extremePhase,
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
                        topPointers = mapOf(extremeLabel to minIdx),
                        bottomPointers = mapOf("i" to i, "j" to j),
                        activeCodeLines = listOf(5, 6)
                    )
                )

                if (isBetterCandidate) {
                    minIdx = j
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "Found new $extremeLabel candidate: arr[$minIdx]=${arr[minIdx]}",
                            comparisonExpr = "NEW ${extremeLabel.uppercase()}: arr[$minIdx]=${arr[minIdx]}",
                            phaseLabel = extremeFoundLabel,
                            renderMode = VisualizerRenderMode.CELLS,
                            array = arr.toList(),
                            activeRange = i until n,
                            sortedBoundary = i,
                            minIndex = minIdx,
                            elementStates = sortedLeft + mapOf(minIdx to ElementState.FOUND),
                            topPointers = mapOf(extremeLabel to minIdx),
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
                        description = "Swapping $extremeLabel ${arr[i]} into sorted slot $i",
                        comparisonExpr = "SWAP: arr[$i] <-> arr[$minIdx]",
                        phaseLabel = if (isDesc) "SWAPPING MAX" else "SWAPPING MIN",
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
                        topPointers = mapOf(extremeLabel to minIdx),
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
                description = "Selection Sort Complete! Array sorted (${sortOrder.name}).",
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 3. Insertion Sort
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateInsertionSort(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        val n = arr.size
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: shift while arr[j] > key (move smaller to the right).
        // DESC: shift while arr[j] < key (move larger to the right).
        val op = if (isDesc) "<" else ">"
        val shouldShift = { a: Int, key: Int -> if (isDesc) a < key else a > key }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Insertion Sort (${sortOrder.name})",
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

            while (j >= 0 && shouldShift(arr[j], key)) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "arr[$j]=${arr[j]} $op key=$key. Shifting ${arr[j]} one position right.",
                        comparisonExpr = "SHIFT: ${arr[j]} $op $key",
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
                description = "Insertion Sort Complete! All elements sorted (${sortOrder.name}).",
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 4. Merge Sort (2-Tier Split & Merge View)
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateMergeSort(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        var sIdx = 0
        val rootRange = 0 until arr.size
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: pick smaller of left[i] and right[j] next. DESC: pick larger.
        val op = if (isDesc) ">=" else "<="

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Divide & Conquer Merge Sort (${sortOrder.name})",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = rootRange,
                mergeBlocks = listOf(rootRange),
                recursionDepth = 0,
                ancestorRanges = listOf(rootRange),
                activeCodeLines = listOf(1, 2)
            )
        )

        fun merge(l: Int, m: Int, r: Int, depth: Int, path: List<IntRange>) {
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
                    ancestorRanges = path,
                    mergeBlocks = listOf(l..m, (m + 1)..r),
                    auxiliaryArray = aux,
                    auxiliaryIndices = mapOf("i" to 0, "j" to left.size, "k" to l),
                    elementStates = (l..r).associateWith { ElementState.COMPARING },
                    topPointers = mapOf("L" to l, "M" to m, "R" to r),
                    bottomPointers = mapOf("k" to k),
                    activeCodeLines = listOf(6)
                )
            )

            while (i < left.size && j < right.size) {
                val compVal = if (isDesc) {
                    if (left[i] >= right[j]) left[i] else right[j]
                } else {
                    if (left[i] <= right[j]) left[i] else right[j]
                }
                val pickLeft = if (isDesc) left[i] >= right[j] else left[i] <= right[j]
                val desc = if (pickLeft) {
                    "Left element arr[${l + i}]=${left[i]} $op Right element arr[${m + 1 + j}]=${right[j]}"
                } else {
                    val otherOp = if (isDesc) "<" else ">"
                    "Right element arr[${m + 1 + j}]=${right[j]} $otherOp Left element arr[${l + i}]=${left[i]}"
                }

                if (pickLeft) {
                    arr[k] = left[i]
                    i++
                } else {
                    arr[k] = right[j]
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
                        ancestorRanges = path,
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
                        ancestorRanges = path,
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
                        ancestorRanges = path,
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
                    ancestorRanges = path,
                    mergeBlocks = listOf(l..r),
                    elementStates = (l..r).associateWith { ElementState.ACTIVE },
                    activeCodeLines = listOf(6)
                )
            )
        }

        fun sort(l: Int, r: Int, depth: Int, path: List<IntRange>) {
            if (l < r) {
                val m = (l + r) / 2
                val currentPath = path + listOf(l..r)
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
                        ancestorRanges = currentPath,
                        mergeBlocks = listOf(l..m, (m + 1)..r),
                        topPointers = mapOf("L" to l, "M" to m, "R" to r),
                        activeCodeLines = listOf(3, 4, 5)
                    )
                )
                sort(l, m, depth + 1, currentPath)
                sort(m + 1, r, depth + 1, currentPath)
                merge(l, m, r, depth, currentPath)
            }
        }

        sort(0, arr.size - 1, 0, listOf(rootRange))

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Merge Sort Complete! All sub-arrays merged and sorted (${sortOrder.name}).",
                comparisonExpr = "SORT COMPLETE",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.CELLS,
                array = arr.toList(),
                activeRange = 0 until arr.size,
                ancestorRanges = listOf(rootRange),
                elementStates = (0 until arr.size).associateWith { ElementState.SORTED },
                activeCodeLines = listOf(6)
            )
        )
        return steps
    }

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 5. Quick Sort (In-Place Partitioning View)
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateQuickSort(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = input.toMutableList()
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: send smaller-or-equal to the left. DESC: send larger-or-equal to the left.
        val op = if (isDesc) ">=" else "<="
        val onCorrectSide = { a: Int, pivot: Int -> if (isDesc) a >= pivot else a <= pivot }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting QuickSort on array of ${arr.size} elements (${sortOrder.name})",
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
                val expr = "COMPARE: ${arr[j]} $op $pivot?"
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

                if (onCorrectSide(arr[j], pivot)) {
                    i++
                    val tmp = arr[i]
                    arr[i] = arr[j]
                    arr[j] = tmp

                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$j] $op pivot. Swapped arr[$i] (${arr[i]}) with arr[$j] (${arr[j]})",
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
                description = "QuickSort Complete! All elements partitioned and sorted (${sortOrder.name}).",
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 6. Linear Search
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateLinearSearch(input: List<Int>, target: Int, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 7. Binary Search
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateBinarySearch(sortedInput: List<Int>, target: Int, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        var low = 0
        var high = sortedInput.size - 1
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: midVal < target means target is in the right half.
        // DESC: midVal > target means target is in the right half.
        val op = if (isDesc) ">" else "<"

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Binary Search on ${if (isDesc) "descending" else "ascending"} array for target = $target",
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
                // ASC: target is right of mid; DESC: target is left of mid.
                if (isDesc) {
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal $op target=$target. Eliminating right half. New high = ${mid - 1}",
                            comparisonExpr = "$midVal $op $target -> high = ${mid - 1}",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = sortedInput,
                            topPointers = mapOf("L" to low, "H" to (mid - 1).coerceAtLeast(0)),
                            activeCodeLines = listOf(9, 10)
                        )
                    )
                    high = mid - 1
                } else {
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal $op target=$target. Eliminating left half. New low = ${mid + 1}",
                            comparisonExpr = "$midVal $op $target -> low = ${mid + 1}",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = sortedInput,
                            topPointers = mapOf("L" to (mid + 1).coerceAtMost(sortedInput.size - 1), "H" to high),
                            activeCodeLines = listOf(7, 8)
                        )
                    )
                    low = mid + 1
                }
            } else {
                // The other branch.
                if (isDesc) {
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal > target=$target (descending). Eliminating left half. New low = ${mid + 1}",
                            comparisonExpr = "$midVal > $target -> low = ${mid + 1}",
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 8. Stack
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun defaultStackOps(): List<BufferOp> = listOf(
        BufferOp.Push(10),
        BufferOp.Push(25),
        BufferOp.Push(42),
        BufferOp.Peek,
        BufferOp.Pop,
        BufferOp.Push(88),
    )

    private fun defaultQueueOps(): List<QueueOp> = listOf(
        QueueOp.Enqueue(15),
        QueueOp.Enqueue(30),
        QueueOp.Enqueue(45),
        QueueOp.Dequeue,
        QueueOp.Enqueue(60),
    )

    private fun generateStackSteps(operations: List<BufferOp> = defaultStackOps()): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val items = mutableListOf<BufferItem>()
        var nextId = 1

        fun addStep(desc: String, codeLine: Int, label: String = "PROCESSING") {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    phaseLabel = label,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = items.toList(),
                    bufferLabel = "STACK - LIFO (Last In, First Out)",
                    activeCodeLines = listOf(codeLine)
                )
            )
        }

        addStep("Created empty Stack", 1, "INITIALIZING")

        operations.forEach { op ->
            when (op) {
                is BufferOp.Push -> {
                    val id = (nextId++).toString()
                    items.add(BufferItem(id, op.value.toString(), ElementState.ACTIVE))
                    addStep("push(${op.value}) -> Pushed ${op.value} onto stack top", 2, "PUSH")
                    items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                }
                BufferOp.Pop -> {
                    if (items.isNotEmpty()) {
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.SWAPPING)
                        val popped = items.removeAt(items.size - 1)
                        addStep("pop() -> Popped ${popped.value} from stack top. " +
                            if (items.isNotEmpty()) "New top is ${items[items.size - 1].value}"
                            else "Stack is now empty", 4, "POP")
                    } else {
                        addStep("pop() -> Stack is empty. No-op.", 4, "POP")
                    }
                }
                BufferOp.Peek -> {
                    if (items.isNotEmpty()) {
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.FOUND)
                        addStep("peek() -> Top element is ${items[items.size - 1].value}", 6, "PEEK")
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                    } else {
                        addStep("peek() -> Stack is empty. No-op.", 6, "PEEK")
                    }
                }
            }
        }

        addStep("Stack sequence complete (${operations.size} operations).", 1, "DONE")
        return steps
    }

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 9. Queue
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateQueueSteps(operations: List<QueueOp> = defaultQueueOps()): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val items = mutableListOf<BufferItem>()
        var nextId = 1

        fun addStep(desc: String, codeLine: Int, label: String = "PROCESSING") {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    phaseLabel = label,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = items.toList(),
                    bufferLabel = "QUEUE - FIFO (First In, First Out)",
                    activeCodeLines = listOf(codeLine)
                )
            )
        }

        addStep("Created empty Queue", 1, "INITIALIZING")

        operations.forEach { op ->
            when (op) {
                is QueueOp.Enqueue -> {
                    val id = (nextId++).toString()
                    items.add(BufferItem(id, op.value.toString(), ElementState.ACTIVE))
                    addStep("enqueue(${op.value}) -> Added ${op.value} to rear of queue", 2, "ENQUEUE")
                    items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                }
                QueueOp.Dequeue -> {
                    if (items.isNotEmpty()) {
                        items[0] = items[0].copy(state = ElementState.SWAPPING)
                        val removed = items.removeAt(0)
                        addStep("dequeue() -> Removed ${removed.value} from front. " +
                            if (items.isNotEmpty()) "New front is ${items[0].value}"
                            else "Queue is now empty", 4, "DEQUEUE")
                    } else {
                        addStep("dequeue() -> Queue is empty. No-op.", 4, "DEQUEUE")
                    }
                }
            }
        }

        addStep("Queue sequence complete (${operations.size} operations).", 1, "DONE")
        return steps
    }

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 10. Binary Search Tree (BST)
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private val defaultBstValues = listOf(50, 30, 70, 20, 40, 60, 80)
    private val defaultBstSearchKey = 40

    /**
     * Build a BST imperatively from [values], laying the resulting tree
     * out with a simple BFS x/y scheme. Used when the user provides a
     * custom list — gives acceptable visual fidelity for a teaching app
     * without re-deriving the hand-tuned default layout.
     */
    private fun buildBstFromValues(values: List<Int>): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        if (values.isEmpty()) return emptyList<GraphNodeState>() to emptyList()

        data class Node(val value: Int, val id: String, var left: Node? = null, var right: Node? = null)

        val root = Node(values[0], values[0].toString())
        var counter = 1
        for (i in 1 until values.size) {
            val v = values[i]
            var cur: Node = root
            // Unique id per value: "value" if first, else "value#k".
            val baseId = v.toString()
            var id = baseId
            val existing = mutableSetOf(root.id)
            var n: Node? = root
            val seen = mutableSetOf<String>()
            fun collectIds(n: Node?) {
                if (n == null) return
                seen.add(n.id)
                collectIds(n.left); collectIds(n.right)
            }
            collectIds(root)
            var k = 1
            while (id in seen) { id = "$baseId#$k"; k++ }
            seen.add(id)
            val newNode = Node(v, id)
            while (true) {
                if (v < cur.value) {
                    if (cur.left == null) { cur.left = newNode; break } else cur = cur.left!!
                } else {
                    if (cur.right == null) { cur.right = newNode; break } else cur = cur.right!!
                }
            }
            counter++
        }

        // BFS layout: root at (100, 15); each level y += 35; x's evenly spaced.
        val nodes = mutableListOf<GraphNodeState>()
        val edges = mutableListOf<GraphEdgeState>()
        val rowWidths = mutableMapOf<Int, Int>()
        // Count nodes per level
        fun countLevel(n: Node?, level: Int) {
            if (n == null) return
            rowWidths[level] = (rowWidths[level] ?: 0) + 1
            countLevel(n.left, level + 1)
            countLevel(n.right, level + 1)
        }
        countLevel(root, 0)
        val totalLevels = rowWidths.size.coerceAtLeast(1)
        // x positions per level: even spacing in [25, 175]
        val levelStepX = if (totalLevels > 0) 150f / (1 shl (totalLevels - 1)) else 75f
        val slotByLevel = mutableMapOf<Pair<Int, Int>, Int>()  // (level, index in level) → x slot
        fun assignSlots(n: Node?, level: Int, slotLeft: Int, slotRight: Int) {
            if (n == null) return
            val mid = (slotLeft + slotRight) / 2
            slotByLevel[level to (mid)] = mid
            // BFS position tracking is simpler with an index list
            assignSlots(n.left, level + 1, slotLeft, (slotLeft + slotRight) / 2 - 1)
            assignSlots(n.right, level + 1, (slotLeft + slotRight) / 2 + 1, slotRight)
        }
        val maxSlots = (1 shl totalLevels)
        assignSlots(root, 0, 0, maxSlots - 1)

        val placedX = mutableMapOf<String, Float>()
        val placedY = mutableMapOf<String, Float>()
        val levelCounters = mutableMapOf<Int, Int>()
        fun place(n: Node?, level: Int, slotLeft: Int, slotRight: Int) {
            if (n == null) return
            val mid = (slotLeft + slotRight) / 2
            val y = 15f + level * 35f
            val x = 25f + (mid.toFloat() / maxSlots.coerceAtLeast(1)) * 150f
            placedX[n.id] = x
            placedY[n.id] = y
            nodes.add(GraphNodeState(n.id, n.value.toString(), x, y))
            place(n.left, level + 1, slotLeft, mid - 1)
            place(n.right, level + 1, mid + 1, slotRight)
            n.left?.let { edges.add(GraphEdgeState(n.id, it.id, isDirected = true)) }
            n.right?.let { edges.add(GraphEdgeState(n.id, it.id, isDirected = true)) }
        }
        place(root, 0, 0, maxSlots - 1)
        return nodes to edges
    }

    /**
     * Search [searchKey] in a BST. Returns the sequence of visited node ids
     * (each compared node) plus the final found id (or null if not present).
     * Pure data — does not emit any [VisualizerStep].
     */

    private fun generateBSTSteps(
        values: List<Int> = defaultBstValues,
        searchKey: Int = defaultBstSearchKey,
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        // Use hand-tuned layout for the canonical default; BFS layout otherwise.
        val (nodes, edges) = if (values == defaultBstValues) {
            listOf(
                GraphNodeState("50", "50", 100f, 15f),
                GraphNodeState("30", "30", 50f, 50f),
                GraphNodeState("70", "70", 150f, 50f),
                GraphNodeState("20", "20", 25f, 90f),
                GraphNodeState("40", "40", 75f, 90f),
                GraphNodeState("60", "60", 125f, 90f),
                GraphNodeState("80", "80", 175f, 90f)
            ) to listOf(
                GraphEdgeState("50", "30", isDirected = true),
                GraphEdgeState("50", "70", isDirected = true),
                GraphEdgeState("30", "20", isDirected = true),
                GraphEdgeState("30", "40", isDirected = true),
                GraphEdgeState("70", "60", isDirected = true),
                GraphEdgeState("70", "80", isDirected = true)
            )
        } else {
            buildBstFromValues(values)
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Binary Search Tree: Searching for key = $searchKey",
                comparisonExpr = "SEARCH: target = $searchKey",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes,
                edges = edges,
                activeCodeLines = listOf(1, 2)
            )
        )

        // Walk the BST to find the search key. Each comparison emits a step.
        // We rely on the first value as the root (matches the visualization
        // tree built by buildBstFromValues) — for the hand-tuned default
        // the root is 50.
        val byId = nodes.associateBy { it.id }
        val childrenById = edges.groupBy { it.from }.mapValues { (_, es) -> es.map { it.to } }
        val rootId = values.firstOrNull()?.toString()?.let { id ->
            // If the default demo, prefer the known root.
            if (values == defaultBstValues) "50" else id
        }
        if (rootId == null) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Empty BST. Nothing to search.",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes,
                    edges = edges,
                    activeCodeLines = listOf(1)
                )
            )
            return steps
        }

        var current: String? = rootId
        val visited = mutableSetOf<String>()
        var found = false
        while (current != null) {
            val nodeVal = byId[current]?.label?.toIntOrNull() ?: break
            visited.add(current)
            if (nodeVal == searchKey) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Found key $searchKey in BST!",
                        comparisonExpr = "FOUND: Node $searchKey",
                        phaseLabel = "FOUND",
                        renderMode = VisualizerRenderMode.GRAPH_TREE,
                        nodes = nodes.map {
                            when (it.id) {
                                current -> it.copy(state = ElementState.FOUND)
                                in visited -> it.copy(state = ElementState.VISITED)
                                else -> it
                            }
                        },
                        edges = edges.map { e ->
                            if (e.from in visited || e.to in visited) e.copy(isHighlighted = true) else e
                        },
                        activeNodeId = current,
                        activeCodeLines = listOf(2, 3)
                    )
                )
                found = true
                break
            }
            val direction = if (searchKey < nodeVal) "LEFT" else "RIGHT"
            val op = if (searchKey < nodeVal) "<" else ">"
            val children = childrenById[current] ?: emptyList()
            val next = if (searchKey < nodeVal) {
                children.firstOrNull { byId[it]?.label?.toIntOrNull()?.let { v -> v < nodeVal } == true }
            } else {
                children.firstOrNull { byId[it]?.label?.toIntOrNull()?.let { v -> v > nodeVal } == true }
            }
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "searchKey=$searchKey $op node($nodeVal) -> Traverse $direction subtree",
                    comparisonExpr = "$searchKey $op $nodeVal -> $direction",
                    phaseLabel = if (searchKey < nodeVal) "LEFT" else "RIGHT",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map {
                        when (it.id) {
                            current -> it.copy(state = ElementState.COMPARING)
                            in visited -> it.copy(state = ElementState.VISITED)
                            else -> it
                        }
                    },
                    edges = edges.map { e ->
                        if ((e.from == current && e.to == next) || e.from in visited)
                            e.copy(isHighlighted = true) else e
                    },
                    activeNodeId = current,
                    activeCodeLines = listOf(4, 5, 6, 7)
                )
            )
            current = next
        }
        if (!found) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Key $searchKey not found in BST.",
                    phaseLabel = "NOT FOUND",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map {
                        if (it.id in visited) it.copy(state = ElementState.VISITED) else it
                    },
                    edges = edges,
                    activeCodeLines = listOf(2, 3)
                )
            )
        }
        return steps
    }

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 11. Heap (Max-Heap)
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateHeapSteps(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        // ASC: max-heap (parent >= children). DESC: min-heap (parent <= children).
        val heapKind = if (isDesc) "Min-Heap" else "Max-Heap"
        val op = if (isDesc) "<=" else ">="

        // For now we use the canonical 7-node demo layout and only flip
        // the labels / comparisons. A future change can re-build the tree
        // layout from the user-supplied `input` values.
        val values = listOf(90, 80, 70, 40, 50, 30, 60)
        val nodes = values.mapIndexed { idx, v ->
            val x = when (idx) {
                0 -> 100f
                1 -> 50f
                2 -> 150f
                3 -> 25f
                4 -> 75f
                5 -> 125f
                else -> 175f
            }
            val y = if (idx == 0) 15f else if (idx < 3) 50f else 90f
            GraphNodeState(idx.toString(), v.toString(), x, y)
        }

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
                description = "$heapKind: Root element (${values[0]}) satisfies parent $op children property",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes,
                edges = edges,
                activeCodeLines = listOf(1, 2)
            )
        )

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Comparing left child ${values[1]} and right child ${values[2]} with root ${values[0]}",
                comparisonExpr = if (isDesc) "HEAPIFY: ${values[0]} < min(${values[1]}, ${values[2]})"
                                 else "HEAPIFY: ${values[0]} > max(${values[1]}, ${values[2]})",
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

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 12. Breadth-First Search (BFS)
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateBFSSteps(startNodeId: String = "A"): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val validStart = startNodeId.uppercase() in setOf("A", "B", "C", "D", "E")
        val start = if (validStart) startNodeId.uppercase() else "A"

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
                description = "BFS: Starting at node $start. Enqueued $start.",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { if (it.id == start) it.copy(state = ElementState.ACTIVE) else it },
                edges = edges,
                activeNodeId = start,
                visitedNodeIds = setOf(start),
                activeCodeLines = listOf(1, 2)
            )
        )

        // For non-default start, emit a short synthetic sequence noting the
        // start. The full BFS-from-arbitrary-start implementation is a
        // follow-up; this keeps the existing demo intact.
        if (start == "A") {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "BFS: Dequeued A. Visiting unvisited neighbors B and C. Queue = [B, C]",
                    comparisonExpr = "QUEUE: [B, C]",
                    phaseLabel = "ENQUEUED",
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
                    phaseLabel = "ENQUEUED",
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
                    phaseLabel = "ENQUEUED",
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
        } else {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "BFS from $start: walking the existing A/B/C/D/E topology (full BFS-from-arbitrary-start coming soon).",
                    comparisonExpr = "START: $start",
                    phaseLabel = "VISITING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map { it.copy(state = ElementState.VISITED) },
                    edges = edges.map { it.copy(isHighlighted = true) },
                    visitedNodeIds = setOf("A", "B", "C", "D", "E"),
                    activeCodeLines = listOf(1)
                )
            )
        }

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS Traversal Complete! All nodes visited level by level.",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { it.copy(state = ElementState.VISITED) },
                edges = edges.map { it.copy(isHighlighted = true) },
                visitedNodeIds = setOf("A", "B", "C", "D", "E"),
                activeCodeLines = listOf(3)
            )
        )

        return steps
    }

    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    // 13. Depth-First Search (DFS)
    // ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
    private fun generateDFSSteps(startNodeId: String = "A"): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val validStart = startNodeId.uppercase() in setOf("A", "B", "C", "D", "E")
        val start = if (validStart) startNodeId.uppercase() else "A"

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

        if (start == "A") {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "DFS: Starting at root node A. Exploring depth branch A -> B",
                    comparisonExpr = "CALL STACK: [dfs(A)]",
                    phaseLabel = "INITIALIZING",
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
                    phaseLabel = "VISITING",
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
                phaseLabel = "VISITING",
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
                phaseLabel = "FOUND",
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
        } else {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "DFS from $start: walking the existing A/B/C/D/E topology (full DFS-from-arbitrary-start coming soon).",
                    comparisonExpr = "START: $start",
                    phaseLabel = "VISITING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map { it.copy(state = ElementState.VISITED) },
                    edges = edges.map { it.copy(isHighlighted = true) },
                    visitedNodeIds = setOf("A", "B", "C", "D", "E"),
                    activeCodeLines = listOf(1)
                )
            )
        }

        return steps
    }

    // ---------------------------------------------------------------------
}
