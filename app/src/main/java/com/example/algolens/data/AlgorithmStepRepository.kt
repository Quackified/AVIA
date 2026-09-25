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
        bstValues: List<Int> = defaultBstValues,
        bstSearchKey: Int = defaultBstSearchKey,
        traversalStartNodeId: String = "A",
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null,
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
            AlgorithmId.STACK -> generateStackSteps(bufferOps.ifEmpty { defaultStackOps() })
            AlgorithmId.QUEUE -> generateQueueSteps(queueOps.ifEmpty { defaultQueueOps() })
            AlgorithmId.BINARY_SEARCH_TREE -> generateBSTSteps(bstValues.ifEmpty { defaultBstValues }, bstSearchKey)
            AlgorithmId.HEAP -> generateHeapSteps(inputArray, sortOrder)
            AlgorithmId.BFS -> generateBFSSteps(traversalStartNodeId, customGraph)
            AlgorithmId.DFS -> generateDFSSteps(traversalStartNodeId, customGraph)
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
    fun defaultStackOps(): List<BufferOp> = listOf(
        BufferOp.Push(10),
        BufferOp.Push(25),
        BufferOp.Push(42),
        BufferOp.Peek,
        BufferOp.Pop,
        BufferOp.Push(88),
    )

    fun defaultQueueOps(): List<QueueOp> = listOf(
        QueueOp.Enqueue(15),
        QueueOp.Enqueue(30),
        QueueOp.Enqueue(45),
        QueueOp.Dequeue,
        QueueOp.Enqueue(60),
    )

    private fun generateStackSteps(operations: List<BufferOp> = defaultStackOps()): List<VisualizerStep> {
        val ops = operations.ifEmpty { defaultStackOps() }
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val items = mutableListOf<BufferItem>()
        var nextId = 1

        fun addStep(
            desc: String,
            codeLine: Int,
            label: String = "PROCESSING",
            expr: String? = null,
            callFrame: String = "Stack.main()"
        ) {
            val topVal = items.lastOrNull()?.value ?: "∅"
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    comparisonExpr = expr ?: "TOP: $topVal · SIZE: ${items.size}",
                    phaseLabel = label,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = items.toList(),
                    bufferLabel = "STACK - LIFO (Last In, First Out)",
                    activeCodeLines = listOf(codeLine),
                    variables = mapOf(
                        "size" to items.size.toString(),
                        "top" to topVal,
                        "topIdx" to (items.size - 1).toString()
                    ),
                    callStack = listOf("Stack.main()", callFrame).distinct()
                )
            )
        }

        addStep("Created empty Stack (LIFO capacity ready)", 1, "INITIALIZING", "STACK READY (size=0)")

        ops.forEach { op ->
            when (op) {
                is BufferOp.Push -> {
                    val id = (nextId++).toString()
                    items.add(BufferItem(id, op.value.toString(), ElementState.ACTIVE))
                    addStep(
                        desc = "push(${op.value}) -> Pushed ${op.value} onto stack top (index ${items.size - 1})",
                        codeLine = 2,
                        label = "PUSH",
                        expr = "PUSH(${op.value}) -> top = ${op.value}",
                        callFrame = "push(${op.value})"
                    )
                    items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                }
                BufferOp.Pop -> {
                    if (items.isNotEmpty()) {
                        val topItem = items.last()
                        items[items.size - 1] = topItem.copy(state = ElementState.SWAPPING)
                        addStep(
                            desc = "pop() -> Targeting top element ${topItem.value} at index ${items.size - 1} for removal",
                            codeLine = 3,
                            label = "POPPING",
                            expr = "POP: targeting top = ${topItem.value}",
                            callFrame = "pop()"
                        )
                        val popped = items.removeAt(items.size - 1)
                        if (items.isNotEmpty()) {
                            items[items.size - 1] = items[items.size - 1].copy(state = ElementState.ACTIVE)
                        }
                        addStep(
                            desc = "pop() -> Popped ${popped.value} from stack top. " +
                                if (items.isNotEmpty()) "New top is ${items.last().value}"
                                else "Stack is now empty",
                            codeLine = 4,
                            label = "POP",
                            expr = "POPPED: ${popped.value} -> new top = ${items.lastOrNull()?.value ?: "∅"}",
                            callFrame = "pop() -> ${popped.value}"
                        )
                        if (items.isNotEmpty()) {
                            items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                        }
                    } else {
                        addStep("pop() -> Stack underflow (empty). No-op.", 4, "UNDERFLOW", "POP: underflow (size=0)", "pop()")
                    }
                }
                BufferOp.Peek -> {
                    if (items.isNotEmpty()) {
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.FOUND)
                        addStep(
                            desc = "peek() -> Inspected top element ${items.last().value} without removing",
                            codeLine = 6,
                            label = "PEEK",
                            expr = "PEEK() == ${items.last().value}",
                            callFrame = "peek() -> ${items.last().value}"
                        )
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                    } else {
                        addStep("peek() -> Stack is empty. Returns null.", 6, "PEEK", "PEEK() == null", "peek()")
                    }
                }
            }
        }

        addStep("Stack sequence complete (${ops.size} operations).", 1, "DONE", "COMPLETE (${items.size} items)")
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 9. Queue
    // ─────────────────────────────────────────────────────────────
    private fun generateQueueSteps(operations: List<QueueOp> = defaultQueueOps()): List<VisualizerStep> {
        val ops = operations.ifEmpty { defaultQueueOps() }
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val items = mutableListOf<BufferItem>()
        var nextId = 1

        fun addStep(
            desc: String,
            codeLine: Int,
            label: String = "PROCESSING",
            expr: String? = null,
            callFrame: String = "Queue.main()"
        ) {
            val frontVal = items.firstOrNull()?.value ?: "∅"
            val rearVal = items.lastOrNull()?.value ?: "∅"
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    comparisonExpr = expr ?: "FRONT: $frontVal · REAR: $rearVal · SIZE: ${items.size}",
                    phaseLabel = label,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = items.toList(),
                    bufferLabel = "QUEUE - FIFO (First In, First Out)",
                    activeCodeLines = listOf(codeLine),
                    variables = mapOf(
                        "size" to items.size.toString(),
                        "front" to frontVal,
                        "rear" to rearVal
                    ),
                    callStack = listOf("Queue.main()", callFrame).distinct()
                )
            )
        }

        addStep("Created empty Queue (FIFO capacity ready)", 1, "INITIALIZING", "QUEUE READY (size=0)")

        ops.forEach { op ->
            when (op) {
                is QueueOp.Enqueue -> {
                    val id = (nextId++).toString()
                    items.add(BufferItem(id, op.value.toString(), ElementState.ACTIVE))
                    addStep(
                        desc = "enqueue(${op.value}) -> Added ${op.value} to rear of queue (index ${items.size - 1})",
                        codeLine = 2,
                        label = "ENQUEUE",
                        expr = "ENQUEUE(${op.value}) -> rear = ${op.value}",
                        callFrame = "enqueue(${op.value})"
                    )
                    items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                }
                QueueOp.Dequeue -> {
                    if (items.isNotEmpty()) {
                        val frontItem = items.first()
                        items[0] = frontItem.copy(state = ElementState.SWAPPING)
                        addStep(
                            desc = "dequeue() -> Targeting front element ${frontItem.value} at head of queue",
                            codeLine = 3,
                            label = "DEQUEUING",
                            expr = "DEQUEUE: targeting front = ${frontItem.value}",
                            callFrame = "dequeue()"
                        )
                        val removed = items.removeAt(0)
                        if (items.isNotEmpty()) {
                            items[0] = items[0].copy(state = ElementState.ACTIVE)
                        }
                        addStep(
                            desc = "dequeue() -> Removed ${removed.value} from front. " +
                                if (items.isNotEmpty()) "New front is ${items[0].value}"
                                else "Queue is now empty",
                            codeLine = 4,
                            label = "DEQUEUE",
                            expr = "DEQUEUED: ${removed.value} -> new front = ${items.firstOrNull()?.value ?: "∅"}",
                            callFrame = "dequeue() -> ${removed.value}"
                        )
                        if (items.isNotEmpty()) {
                            items[0] = items[0].copy(state = ElementState.IDLE)
                        }
                    } else {
                        addStep("dequeue() -> Queue underflow (empty). No-op.", 4, "UNDERFLOW", "DEQUEUE: underflow (size=0)", "dequeue()")
                    }
                }
            }
        }

        addStep("Queue sequence complete (${ops.size} operations).", 1, "DONE", "COMPLETE (${items.size} items)")
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 10. Binary Search Tree (BST)
    // ─────────────────────────────────────────────────────────────
    val defaultBstValues = listOf(50, 30, 70, 20, 40, 60, 80)
    val defaultBstSearchKey = 40

    /**
     * Build a BST imperatively from [values], laying the resulting tree
     * out with normalized in-order X rank (`20f..180f`) and depth Y (`18f..108f`)
     * so custom or skewed trees never clip or overlap nodes.
     */
    private fun buildBstFromValues(values: List<Int>): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        if (values.isEmpty()) return emptyList<GraphNodeState>() to emptyList()

        data class Node(val value: Int, val id: String, var left: Node? = null, var right: Node? = null)

        val root = Node(values[0], values[0].toString())
        val seen = mutableSetOf(root.id)
        for (i in 1 until values.size) {
            val v = values[i]
            var cur: Node = root
            val baseId = v.toString()
            var id = baseId
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
        }

        val depths = mutableMapOf<String, Int>()
        val inOrderList = mutableListOf<Node>()
        val edges = mutableListOf<GraphEdgeState>()

        fun traverse(n: Node?, depth: Int) {
            if (n == null) return
            depths[n.id] = depth
            traverse(n.left, depth + 1)
            inOrderList.add(n)
            traverse(n.right, depth + 1)
            n.left?.let { edges.add(GraphEdgeState(n.id, it.id, isDirected = true)) }
            n.right?.let { edges.add(GraphEdgeState(n.id, it.id, isDirected = true)) }
        }
        traverse(root, 0)

        val maxDepth = (depths.values.maxOrNull() ?: 0).coerceAtLeast(1)
        val total = inOrderList.size
        val rankMap = inOrderList.mapIndexed { idx, node -> node.id to idx }.toMap()

        // Preserve canonical order (insertion order) for nodes list
        val nodesById = inOrderList.associateBy { it.id }
        val orderedIds = mutableListOf<String>()
        fun collectPreOrder(n: Node?) {
            if (n == null) return
            orderedIds.add(n.id)
            collectPreOrder(n.left)
            collectPreOrder(n.right)
        }
        collectPreOrder(root)

        val nodes = orderedIds.mapNotNull { id ->
            val n = nodesById[id] ?: return@mapNotNull null
            val rank = rankMap[id] ?: 0
            val depth = depths[id] ?: 0
            val x = if (total <= 1) 100f else 20f + (rank.toFloat() / (total - 1).toFloat()) * 160f
            val y = if (maxDepth == 0) 50f else 18f + (depth.toFloat() / maxDepth.toFloat()) * 86f
            GraphNodeState(n.id, n.value.toString(), x, y)
        }

        return nodes to edges
    }

    private fun generateBSTSteps(
        values: List<Int> = defaultBstValues,
        searchKey: Int = defaultBstSearchKey,
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val cleanValues = values.ifEmpty { defaultBstValues }

        val (nodes, edges) = if (cleanValues == defaultBstValues) {
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
            buildBstFromValues(cleanValues)
        }

        val byId = nodes.associateBy { it.id }
        val childrenById = edges.groupBy { it.from }.mapValues { (_, es) -> es.map { it.to } }

        // Emit incremental BST insertion steps so the user sees how the BST is constructed
        val insertedIds = linkedSetOf<String>()
        for (i in cleanValues.indices) {
            val subValues = cleanValues.subList(0, i + 1)
            val (subNodes, _) = if (cleanValues == defaultBstValues) {
                nodes.take(i + 1) to edges
            } else {
                buildBstFromValues(subValues)
            }
            val latestId = subNodes.lastOrNull()?.id ?: cleanValues[i].toString()
            insertedIds.add(latestId)
            val visibleNodes = nodes.filter { it.id in insertedIds }.map {
                if (it.id == latestId) it.copy(state = ElementState.ACTIVE)
                else it.copy(state = ElementState.VISITED)
            }
            val visibleEdges = edges.filter { it.from in insertedIds && it.to in insertedIds }.map {
                if (it.to == latestId) it.copy(isHighlighted = true) else it
            }
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = if (i == 0) "BST Insert: Placed root node ${cleanValues[i]}."
                    else "BST Insert: Inserted ${cleanValues[i]} into binary search tree (${insertedIds.size}/${cleanValues.size} nodes).",
                    comparisonExpr = "INSERT(${cleanValues[i]}) · target = $searchKey",
                    phaseLabel = "INSERTING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = visibleNodes,
                    edges = visibleEdges,
                    buffer = insertedIds.mapIndexed { idx, id ->
                        BufferItem("ins_$idx", byId[id]?.label ?: id, if (id == latestId) ElementState.ACTIVE else ElementState.IDLE)
                    },
                    bufferLabel = "BST INSERTION ORDER",
                    activeNodeId = latestId,
                    visitedNodeIds = insertedIds.toSet(),
                    activeCodeLines = listOf(1, 2),
                    variables = mapOf(
                        "inserted" to cleanValues[i].toString(),
                        "treeSize" to insertedIds.size.toString(),
                        "searchKey" to searchKey.toString()
                    ),
                    callStack = listOf("BST.build()", "insert(${cleanValues[i]})")
                )
            )
        }

        val rootId = nodes.firstOrNull()?.id
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

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BST constructed (${nodes.size} nodes). Starting search from root ${byId[rootId]?.label} for target key = $searchKey.",
                comparisonExpr = "SEARCH: target = $searchKey",
                phaseLabel = "SEARCHING",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { if (it.id == rootId) it.copy(state = ElementState.ACTIVE) else it },
                edges = edges,
                buffer = listOf(BufferItem("path_0", byId[rootId]?.label ?: rootId, ElementState.ACTIVE)),
                bufferLabel = "BST SEARCH PATH",
                activeNodeId = rootId,
                visitedNodeIds = setOf(rootId),
                activeCodeLines = listOf(1, 2),
                variables = mapOf(
                    "root" to (byId[rootId]?.label ?: rootId),
                    "searchKey" to searchKey.toString()
                ),
                callStack = listOf("BST.search($searchKey)")
            )
        )

        var current: String? = rootId
        val visited = linkedSetOf<String>()
        val searchStack = mutableListOf("BST.search($searchKey)")
        var found = false
        while (current != null) {
            val nodeVal = byId[current]?.label?.toIntOrNull() ?: break
            visited.add(current)
            searchStack.add("search(node=$nodeVal, key=$searchKey)")
            val pathBuffer = visited.mapIndexed { idx, id ->
                BufferItem("p_$idx", byId[id]?.label ?: id, if (id == current) ElementState.ACTIVE else ElementState.IDLE)
            }
            if (nodeVal == searchKey) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Found target key $searchKey at node $nodeVal! Search path: ${visited.joinToString(" → ") { byId[it]?.label ?: it }}",
                        comparisonExpr = "FOUND: key $searchKey == node($nodeVal)",
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
                            if (e.from in visited && e.to in visited) e.copy(isHighlighted = true) else e
                        },
                        buffer = pathBuffer.map { if (it.value == nodeVal.toString()) it.copy(state = ElementState.FOUND) else it },
                        bufferLabel = "BST SEARCH PATH",
                        activeNodeId = current,
                        visitedNodeIds = visited.toSet(),
                        activeCodeLines = listOf(2, 3),
                        variables = mapOf(
                            "current" to nodeVal.toString(),
                            "searchKey" to searchKey.toString(),
                            "depth" to (visited.size - 1).toString(),
                            "status" to "FOUND"
                        ),
                        callStack = searchStack.toList()
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
                children.firstOrNull { byId[it]?.label?.toIntOrNull()?.let { v -> v >= nodeVal } == true }
            }
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "searchKey=$searchKey $op node($nodeVal) -> Traverse $direction subtree" +
                        (if (next != null) " to node ${byId[next]?.label}" else " (null child)"),
                    comparisonExpr = "$searchKey $op $nodeVal -> $direction",
                    phaseLabel = direction,
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map {
                        when (it.id) {
                            current -> it.copy(state = ElementState.COMPARING)
                            in visited -> it.copy(state = ElementState.VISITED)
                            else -> it
                        }
                    },
                    edges = edges.map { e ->
                        if ((e.from == current && e.to == next) || (e.from in visited && e.to in visited))
                            e.copy(isHighlighted = true) else e
                    },
                    buffer = pathBuffer,
                    bufferLabel = "BST SEARCH PATH",
                    activeNodeId = current,
                    visitedNodeIds = visited.toSet(),
                    activeCodeLines = listOf(4, 5, 6, 7),
                    variables = mapOf(
                        "current" to nodeVal.toString(),
                        "searchKey" to searchKey.toString(),
                        "branch" to direction,
                        "next" to (next?.let { byId[it]?.label } ?: "null")
                    ),
                    callStack = searchStack.toList()
                )
            )
            current = next
        }
        if (!found) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Key $searchKey not found in BST (reached null leaf after ${visited.size} comparisons).",
                    comparisonExpr = "NOT FOUND: key $searchKey",
                    phaseLabel = "NOT FOUND",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map {
                        if (it.id in visited) it.copy(state = ElementState.VISITED) else it
                    },
                    edges = edges.map { e ->
                        if (e.from in visited && e.to in visited) e.copy(isHighlighted = true) else e
                    },
                    buffer = visited.mapIndexed { idx, id ->
                        BufferItem("p_$idx", byId[id]?.label ?: id, ElementState.VISITED)
                    },
                    bufferLabel = "BST SEARCH PATH",
                    activeNodeId = visited.lastOrNull(),
                    visitedNodeIds = visited.toSet(),
                    activeCodeLines = listOf(2, 3),
                    variables = mapOf(
                        "searchKey" to searchKey.toString(),
                        "comparisons" to visited.size.toString(),
                        "status" to "NOT_FOUND"
                    ),
                    callStack = searchStack.toList()
                )
            )
        }
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 11. Heap (Max-Heap / Min-Heap with Dual Tree + Array State)
    // ─────────────────────────────────────────────────────────────
    private fun generateHeapSteps(input: List<Int>, sortOrder: SortOrder = SortOrder.ASC): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        val heapKind = if (isDesc) "Min-Heap" else "Max-Heap"
        val op = if (isDesc) "<" else ">"

        val initial = if (input.isNotEmpty() && input != DEFAULT_INPUT) {
            input.take(15)
        } else {
            listOf(40, 80, 70, 90, 50, 30, 60)
        }
        val arr = initial.toMutableList()
        val n = arr.size

        fun buildTreeNodes(
            values: List<Int>,
            states: Map<Int, ElementState>,
            heapBound: Int = values.size
        ): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
            val maxLevel = if (values.size <= 1) 0 else 31 - Integer.numberOfLeadingZeros(values.size)
            val treeNodes = values.mapIndexed { idx, v ->
                val level = 31 - Integer.numberOfLeadingZeros(idx + 1)
                val indexInLevel = (idx + 1) - (1 shl level)
                val nodesInLevel = 1 shl level
                val x = 15f + ((indexInLevel + 0.5f) / nodesInLevel.toFloat()) * 170f
                val y = if (maxLevel == 0) 54f else 18f + (level.toFloat() / maxLevel.toFloat()) * 82f
                val st = states[idx] ?: if (idx >= heapBound) ElementState.SORTED else ElementState.IDLE
                GraphNodeState(idx.toString(), v.toString(), x, y, st)
            }
            val treeEdges = mutableListOf<GraphEdgeState>()
            for (i in 0 until values.size) {
                val left = 2 * i + 1
                val right = 2 * i + 2
                if (left < values.size) {
                    val hi = (states[i] == ElementState.ACTIVE || states[i] == ElementState.SWAPPING) &&
                        (states[left] == ElementState.COMPARING || states[left] == ElementState.SWAPPING)
                    treeEdges.add(GraphEdgeState(i.toString(), left.toString(), weight = left, isDirected = true, isHighlighted = hi))
                }
                if (right < values.size) {
                    val hi = (states[i] == ElementState.ACTIVE || states[i] == ElementState.SWAPPING) &&
                        (states[right] == ElementState.COMPARING || states[right] == ElementState.SWAPPING)
                    treeEdges.add(GraphEdgeState(i.toString(), right.toString(), weight = right, isDirected = true, isHighlighted = hi))
                }
            }
            return treeNodes to treeEdges
        }

        fun emitHeapStep(
            desc: String,
            expr: String?,
            phase: String,
            states: Map<Int, ElementState>,
            activeIdx: Int?,
            codeLines: List<Int>,
            heapBound: Int = n,
            callFrame: String = "buildHeap()"
        ) {
            val (nodes, edges) = buildTreeNodes(arr, states, heapBound)
            val topPtrs = mutableMapOf<String, Int>()
            if (activeIdx != null && activeIdx in 0 until n) {
                topPtrs["P"] = activeIdx
                val l = 2 * activeIdx + 1
                val r = 2 * activeIdx + 2
                if (l < heapBound) topPtrs["L"] = l
                if (r < heapBound) topPtrs["R"] = r
            }
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    comparisonExpr = expr,
                    phaseLabel = phase,
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    array = arr.toList(),
                    elementStates = states,
                    topPointers = topPtrs,
                    nodes = nodes,
                    edges = edges,
                    activeNodeId = activeIdx?.toString(),
                    visitedNodeIds = states.filter { it.value == ElementState.SORTED || it.value == ElementState.VISITED }.keys.map { it.toString() }.toSet(),
                    activeCodeLines = codeLines,
                    variables = buildMap {
                        put("heapType", heapKind)
                        put("heapSize", heapBound.toString())
                        put("rootVal", (arr.firstOrNull() ?: 0).toString())
                        if (activeIdx != null && activeIdx in 0 until n) {
                            put("parentIdx", activeIdx.toString())
                            put("parentVal", arr[activeIdx].toString())
                        }
                    },
                    callStack = listOf("Heap.main()", callFrame).distinct()
                )
            )
        }

        emitHeapStep(
            desc = "$heapKind: Initial array $arr (${n} elements). Starting bottom-up buildHeap from last parent index ${(n / 2) - 1}.",
            expr = "BUILD $heapKind (n=$n)",
            phase = "INITIALIZING",
            states = mapOf(0 to ElementState.ACTIVE),
            activeIdx = ((n / 2) - 1).coerceAtLeast(0),
            codeLines = listOf(1, 2),
            callFrame = "buildHeap(n=$n)"
        )

        fun siftDown(startIdx: Int, endExclusive: Int) {
            var root = startIdx
            while (2 * root + 1 < endExclusive) {
                val left = 2 * root + 1
                val right = 2 * root + 2
                var target = root

                if (isDesc) {
                    if (arr[left] < arr[target]) target = left
                    if (right < endExclusive && arr[right] < arr[target]) target = right
                } else {
                    if (arr[left] > arr[target]) target = left
                    if (right < endExclusive && arr[right] > arr[target]) target = right
                }

                val compareStates = buildMap {
                    put(root, ElementState.ACTIVE)
                    put(left, ElementState.COMPARING)
                    if (right < endExclusive) put(right, ElementState.COMPARING)
                    for (k in endExclusive until n) put(k, ElementState.SORTED)
                }
                val rightStr = if (right < endExclusive) ", R[$right]=${arr[right]}" else ""
                emitHeapStep(
                    desc = "siftDown(i=$root): Comparing parent P[$root]=${arr[root]} with child L[$left]=${arr[left]}$rightStr",
                    expr = "COMPARE: arr[$root] (${arr[root]}) vs arr[$target] (${arr[target]})",
                    phase = "HEAPIFY",
                    states = compareStates,
                    activeIdx = root,
                    codeLines = listOf(3, 4, 5),
                    heapBound = endExclusive,
                    callFrame = "siftDown(i=$root, size=$endExclusive)"
                )

                if (target != root) {
                    val parentVal = arr[root]
                    val childVal = arr[target]
                    arr[root] = childVal
                    arr[target] = parentVal

                    val swapStates = buildMap {
                        put(root, ElementState.SWAPPING)
                        put(target, ElementState.SWAPPING)
                        for (k in endExclusive until n) put(k, ElementState.SORTED)
                    }
                    emitHeapStep(
                        desc = "Child $childVal $op Parent $parentVal -> Swapped index $root ↔ $target",
                        expr = "ELEVATE: $childVal at [$root]",
                        phase = "SWAPPING",
                        states = swapStates,
                        activeIdx = target,
                        codeLines = listOf(6, 7),
                        heapBound = endExclusive,
                        callFrame = "swap($root, $target)"
                    )
                    root = target
                } else {
                    break
                }
            }
        }

        for (i in (n / 2) - 1 downTo 0) {
            siftDown(i, n)
        }

        emitHeapStep(
            desc = "$heapKind property established! Root arr[0]=${arr[0]} is the ${if (isDesc) "minimum" else "maximum"} element.",
            expr = "HEAP READY: root = ${arr[0]}",
            phase = "HEAPIFIED",
            states = mapOf(0 to ElementState.FOUND),
            activeIdx = 0,
            codeLines = listOf(2),
            callFrame = "buildHeap() -> ready"
        )

        // Demonstrate extracting the root element and re-heapifying
        if (n > 1) {
            val extracted = arr[0]
            val lastVal = arr[n - 1]
            arr[0] = lastVal
            arr[n - 1] = extracted
            emitHeapStep(
                desc = "extractRoot(): Swapped root $extracted with last leaf $lastVal (index ${n - 1}) and locked $extracted.",
                expr = "EXTRACT: $extracted -> slot [${n - 1}]",
                phase = "EXTRACTING",
                states = mapOf(0 to ElementState.ACTIVE, (n - 1) to ElementState.SORTED),
                activeIdx = 0,
                codeLines = listOf(6, 7),
                heapBound = n - 1,
                callFrame = "extractRoot() -> $extracted"
            )
            siftDown(0, n - 1)
        }

        emitHeapStep(
            desc = "$heapKind operations complete! Current root is ${arr[0]}.",
            expr = "COMPLETE: root = ${arr[0]}",
            phase = "SORTED",
            states = (0 until n).associateWith { if (it == 0) ElementState.FOUND else ElementState.VISITED },
            activeIdx = 0,
            codeLines = listOf(8),
            callFrame = "Heap.done()"
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // Canonical 6-Node Weighted Graph (`figma-make-ref` Parity)
    // ─────────────────────────────────────────────────────────────
    fun canonicalWeightedGraph(): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        val nodes = listOf(
            GraphNodeState("A", "A", 45f, 35f),
            GraphNodeState("B", "B", 125f, 25f),
            GraphNodeState("C", "C", 205f, 40f),
            GraphNodeState("D", "D", 50f, 110f),
            GraphNodeState("E", "E", 130f, 120f),
            GraphNodeState("F", "F", 210f, 105f)
        )
        val edges = listOf(
            GraphEdgeState("A", "B", weight = 4, isDirected = false),
            GraphEdgeState("A", "D", weight = 2, isDirected = false),
            GraphEdgeState("B", "C", weight = 5, isDirected = false),
            GraphEdgeState("B", "E", weight = 1, isDirected = false),
            GraphEdgeState("D", "E", weight = 3, isDirected = false),
            GraphEdgeState("E", "C", weight = 2, isDirected = false),
            GraphEdgeState("E", "F", weight = 4, isDirected = false),
            GraphEdgeState("C", "F", weight = 3, isDirected = false)
        )
        return nodes to edges
    }

    // ─────────────────────────────────────────────────────────────
    // 12. Breadth-First Search (BFS — Weighted Graph + Live Queue)
    // ─────────────────────────────────────────────────────────────
    private fun generateBFSSteps(
        startNodeId: String = "A",
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val (rawNodes, rawEdges) = customGraph?.takeIf { it.first.isNotEmpty() } ?: canonicalWeightedGraph()
        val baseNodes = rawNodes.map { it.copy(state = ElementState.IDLE) }
        val baseEdges = rawEdges.map { it.copy(isHighlighted = false) }
        val validIds = baseNodes.map { it.id }.toSet()
        val start = if (startNodeId.uppercase() in validIds) startNodeId.uppercase() else (baseNodes.firstOrNull()?.id ?: "A")

        val adj = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        baseNodes.forEach { adj[it.id] = mutableListOf() }
        baseEdges.forEach { e ->
            val w = e.weight ?: 1
            adj[e.from]?.add(e.to to w)
            if (!e.isDirected) {
                adj[e.to]?.add(e.from to w)
            }
        }
        adj.values.forEach { list -> list.sortBy { it.first } }

        val visited = linkedSetOf<String>()
        val queue = ArrayDeque<String>()
        val treeEdges = mutableSetOf<Pair<String, String>>()
        val distMap = mutableMapOf<String, Int>()

        visited.add(start)
        queue.addLast(start)
        distMap[start] = 0

        fun queueBufferItems(highlightId: String? = null): List<BufferItem> =
            queue.mapIndexed { idx, id ->
                BufferItem(
                    id = "q_${idx}_$id",
                    value = "$id(d=${distMap[id] ?: 0})",
                    state = if (id == highlightId) ElementState.ACTIVE else ElementState.IDLE
                )
            }

        fun isTreeEdge(e: GraphEdgeState): Boolean =
            (e.from to e.to) in treeEdges || (e.to to e.from) in treeEdges

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS: Initialized source node $start (dist=0). Enqueued $start into frontier.",
                comparisonExpr = "QUEUE: [$start]",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = baseNodes.map { if (it.id == start) it.copy(state = ElementState.ACTIVE) else it },
                edges = baseEdges,
                buffer = queueBufferItems(start),
                bufferLabel = "BFS FRONTIER QUEUE",
                activeNodeId = start,
                visitedNodeIds = visited.toSet(),
                activeCodeLines = listOf(1, 2),
                variables = mapOf(
                    "source" to start,
                    "queueSize" to queue.size.toString(),
                    "visitedCount" to visited.size.toString()
                ),
                callStack = listOf("bfs(start=$start)")
            )
        )

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            val currDist = distMap[curr] ?: 0
            val neighbors = adj[curr].orEmpty()
            val newlyDiscovered = mutableListOf<String>()

            for ((nextId, weight) in neighbors) {
                if (nextId !in visited) {
                    visited.add(nextId)
                    distMap[nextId] = currDist + weight
                    treeEdges.add(curr to nextId)
                    queue.addLast(nextId)
                    newlyDiscovered.add("$nextId(w=$weight)")
                }
            }

            val desc = if (newlyDiscovered.isNotEmpty()) {
                "BFS: Dequeued $curr (dist=$currDist). Discovered ${newlyDiscovered.joinToString(", ")}. Queue = [${queue.joinToString(", ")}]"
            } else {
                "BFS: Dequeued $curr (dist=$currDist). All neighbors already visited. Queue = [${queue.joinToString(", ")}]"
            }

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    comparisonExpr = "QUEUE: [${queue.joinToString(", ")}]",
                    phaseLabel = if (newlyDiscovered.isNotEmpty()) "ENQUEUED" else "VISITING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = baseNodes.map { node ->
                        when {
                            node.id == curr -> node.copy(state = ElementState.ACTIVE)
                            node.id in queue -> node.copy(state = ElementState.COMPARING)
                            node.id in visited -> node.copy(state = ElementState.VISITED)
                            else -> node
                        }
                    },
                    edges = baseEdges.map { e ->
                        if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                    },
                    buffer = queueBufferItems(),
                    bufferLabel = "BFS FRONTIER QUEUE",
                    activeNodeId = curr,
                    visitedNodeIds = visited.toSet(),
                    activeCodeLines = listOf(3, 4, 5, 6, 7),
                    variables = mapOf(
                        "curr" to curr,
                        "dist[$curr]" to currDist.toString(),
                        "queue" to "[${queue.joinToString(",")}]",
                        "visited" to visited.joinToString("→")
                    ),
                    callStack = listOf("bfs(start=$start)", "expand(curr=$curr)")
                )
            )
        }

        val lastVisited = visited.lastOrNull() ?: start
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS Traversal Complete! Order: ${visited.joinToString(" → ")}.",
                comparisonExpr = "COMPLETE: ${visited.size} nodes visited",
                phaseLabel = "SORTED",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = baseNodes.map {
                    if (it.id == lastVisited) it.copy(state = ElementState.FOUND)
                    else if (it.id in visited) it.copy(state = ElementState.VISITED)
                    else it
                },
                edges = baseEdges.map { e ->
                    if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                },
                buffer = emptyList(),
                bufferLabel = "BFS FRONTIER QUEUE",
                activeNodeId = lastVisited,
                visitedNodeIds = visited.toSet(),
                activeCodeLines = listOf(3),
                variables = mapOf(
                    "source" to start,
                    "visitedOrder" to visited.joinToString("→"),
                    "totalVisited" to visited.size.toString()
                ),
                callStack = listOf("bfs(start=$start) -> complete")
            )
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 13. Depth-First Search (DFS — Weighted Graph + Call Stack)
    // ─────────────────────────────────────────────────────────────
    private fun generateDFSSteps(
        startNodeId: String = "A",
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val (rawNodes, rawEdges) = customGraph?.takeIf { it.first.isNotEmpty() } ?: canonicalWeightedGraph()
        val baseNodes = rawNodes.map { it.copy(state = ElementState.IDLE) }
        val baseEdges = rawEdges.map { it.copy(isHighlighted = false) }
        val validIds = baseNodes.map { it.id }.toSet()
        val start = if (startNodeId.uppercase() in validIds) startNodeId.uppercase() else (baseNodes.firstOrNull()?.id ?: "A")

        val adj = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        baseNodes.forEach { adj[it.id] = mutableListOf() }
        baseEdges.forEach { e ->
            val w = e.weight ?: 1
            adj[e.from]?.add(e.to to w)
            if (!e.isDirected) {
                adj[e.to]?.add(e.from to w)
            }
        }
        adj.values.forEach { list -> list.sortBy { it.first } }

        val visited = linkedSetOf<String>()
        val callStack = mutableListOf<String>()
        val treeEdges = mutableSetOf<Pair<String, String>>()

        fun isTreeEdge(e: GraphEdgeState): Boolean =
            (e.from to e.to) in treeEdges || (e.to to e.from) in treeEdges

        fun stackBufferItems(): List<BufferItem> =
            callStack.mapIndexed { idx, id ->
                BufferItem(
                    id = "dfs_${idx}_$id",
                    value = "dfs($id)",
                    state = if (idx == callStack.lastIndex) ElementState.ACTIVE else ElementState.IDLE
                )
            }

        fun dfs(u: String, incomingWeight: Int?) {
            visited.add(u)
            callStack.add(u)
            val weightInfo = if (incomingWeight != null) " via edge (w=$incomingWeight)" else ""

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "DFS: Visiting node $u$weightInfo. Call Stack = [${callStack.joinToString(" → ")}]",
                    comparisonExpr = "CALL STACK: [${callStack.joinToString(", ") { "dfs($it)" }}]",
                    phaseLabel = if (callStack.size == 1) "INITIALIZING" else "VISITING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = baseNodes.map { node ->
                        when {
                            node.id == u -> node.copy(state = ElementState.ACTIVE)
                            node.id in callStack -> node.copy(state = ElementState.COMPARING)
                            node.id in visited -> node.copy(state = ElementState.VISITED)
                            else -> node
                        }
                    },
                    edges = baseEdges.map { e ->
                        if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                    },
                    buffer = stackBufferItems(),
                    bufferLabel = "DFS CALL STACK",
                    activeNodeId = u,
                    visitedNodeIds = visited.toSet(),
                    activeCodeLines = if (callStack.size == 1) listOf(1, 2) else listOf(3, 4, 5),
                    variables = mapOf(
                        "u" to u,
                        "depth" to callStack.size.toString(),
                        "visited" to visited.joinToString("→")
                    ),
                    callStack = callStack.map { "dfs($it)" }
                )
            )

            for ((v, w) in adj[u].orEmpty()) {
                if (v !in visited) {
                    treeEdges.add(u to v)
                    dfs(v, w)
                }
            }

            callStack.removeAt(callStack.lastIndex)
            if (callStack.isNotEmpty()) {
                val parent = callStack.last()
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "DFS: Backtracking from $u to parent $parent.",
                        comparisonExpr = "BACKTRACK: dfs($u) -> $parent",
                        phaseLabel = "BACKTRACKING",
                        renderMode = VisualizerRenderMode.GRAPH_TREE,
                        nodes = baseNodes.map { node ->
                            when {
                                node.id == parent -> node.copy(state = ElementState.ACTIVE)
                                node.id == u -> node.copy(state = ElementState.FOUND)
                                node.id in visited -> node.copy(state = ElementState.VISITED)
                                else -> node
                            }
                        },
                        edges = baseEdges.map { e ->
                            if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                        },
                        buffer = stackBufferItems(),
                        bufferLabel = "DFS CALL STACK",
                        activeNodeId = parent,
                        visitedNodeIds = visited.toSet(),
                        activeCodeLines = listOf(5),
                        variables = mapOf(
                            "backtrackFrom" to u,
                            "parent" to parent,
                            "depth" to callStack.size.toString()
                        ),
                        callStack = callStack.map { "dfs($it)" }
                    )
                )
            }
        }

        dfs(start, null)

        val lastNode = visited.lastOrNull() ?: start
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "DFS Complete! Visited order: ${visited.joinToString(" → ")}.",
                comparisonExpr = "COMPLETE: ${visited.size} nodes visited",
                phaseLabel = "FOUND",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = baseNodes.map {
                    if (it.id == lastNode) it.copy(state = ElementState.FOUND)
                    else if (it.id in visited) it.copy(state = ElementState.VISITED)
                    else it
                },
                edges = baseEdges.map { e ->
                    if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                },
                buffer = emptyList(),
                bufferLabel = "DFS CALL STACK",
                activeNodeId = lastNode,
                visitedNodeIds = visited.toSet(),
                activeCodeLines = listOf(5),
                variables = mapOf(
                    "source" to start,
                    "visitedOrder" to visited.joinToString("→"),
                    "totalVisited" to visited.size.toString()
                ),
                callStack = listOf("dfs(start=$start) -> complete")
            )
        )

        return steps
    }
}
