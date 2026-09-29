package com.avia.data

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.avia.model.Algorithm
import com.avia.model.AlgorithmId
import com.avia.model.BstMode
import com.avia.model.BufferOp
import com.avia.model.QueueOp
import com.avia.model.QueueVariant
import com.avia.model.SortOrder
import com.avia.ui.visualizer.BufferItem
import com.avia.ui.visualizer.ElementState
import com.avia.ui.visualizer.GraphEdgeState
import com.avia.ui.visualizer.GraphNodeState
import com.avia.ui.visualizer.VisualizerRenderMode
import com.avia.ui.visualizer.VisualizerStep

/**
 * Repository containing step generators for every algorithm registered in
 * [AlgorithmRegistry]. The previous `when (name)`-style dispatcher has been
 * replaced by a single typed lookup against [AlgorithmId] — adding a new
 * algorithm now only requires an entry in [AlgorithmRegistry], never a
 * change to this class.
 *
 * NOTE: the legacy `getCodeLinesForAlgorithm(...)` method has been removed.
 * Code listings are owned by [com.avia.ui.visualizer.AlgorithmCodeRegistry]
 * in `CodeTracePane.kt` and the [com.avia.FeatureEnhancementsTest]
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
     * Canonical 7-element default input for [AlgorithmId.HEAP], shared by
     * [AlgorithmRegistry], [CustomizeGraphSheet], and [generateHeapSteps].
     */
    val DEFAULT_HEAP_INPUT: List<Int> = listOf(40, 80, 70, 90, 50, 30, 60)

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
        inputArray: List<Int> = emptyList(),
        sortOrder: SortOrder = SortOrder.ASC,
        searchTarget: Int? = null,
        bufferOps: List<BufferOp> = defaultStackOps(),
        queueOps: List<QueueOp> = defaultQueueOps(),
        bstValues: List<Int> = defaultBstValues,
        bstSearchKey: Int = defaultBstSearchKey,
        bstMode: BstMode = BstMode.SEARCH,
        queueVariant: QueueVariant = QueueVariant.LINEAR_FIFO,
        circularQueueOps: List<QueueOp> = defaultCircularQueueOps(),
        traversalStartNodeId: String = "A",
        targetNodeId: String? = null,
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null,
        customCoordinates: Map<String, Offset>? = null,
    ): List<VisualizerStep> {
        val spec = AlgorithmRegistry.specFor(algorithm.id)
        if (spec == null) {
            Log.w(TAG, "No spec registered for ${algorithm.id} -- returning empty steps.")
            return emptyList()
        }
        val effectiveArray = inputArray.ifEmpty {
            spec.defaultInput.ifEmpty { DEFAULT_INPUT }
        }

        return when (algorithm.id) {
            AlgorithmId.BUBBLE_SORT -> generateBubbleSort(effectiveArray, sortOrder)
            AlgorithmId.SELECTION_SORT -> generateSelectionSort(effectiveArray, sortOrder)
            AlgorithmId.INSERTION_SORT -> generateInsertionSort(effectiveArray, sortOrder)
            AlgorithmId.MERGE_SORT -> generateMergeSort(effectiveArray, sortOrder)
            AlgorithmId.QUICK_SORT -> generateQuickSort(effectiveArray, sortOrder)
            AlgorithmId.LINEAR_SEARCH -> generateLinearSearch(effectiveArray, target = searchTarget ?: 6, sortOrder)
            AlgorithmId.BINARY_SEARCH -> generateBinarySearch(
                sortedInput = if (sortOrder == SortOrder.ASC) effectiveArray.sorted() else effectiveArray.sortedDescending(),
                target = searchTarget ?: 6,
                sortOrder = sortOrder
            )
            AlgorithmId.STACK -> generateStackSteps(bufferOps.ifEmpty { defaultStackOps() })
            AlgorithmId.QUEUE -> {
                if (queueVariant == QueueVariant.CIRCULAR_RING) {
                    generateCircularQueueSteps(circularQueueOps.ifEmpty { defaultCircularQueueOps() })
                } else {
                    generateQueueSteps(queueOps.ifEmpty { defaultQueueOps() })
                }
            }
            AlgorithmId.BINARY_SEARCH_TREE -> generateBSTSteps(
                values = bstValues.ifEmpty { defaultBstValues },
                searchKey = bstSearchKey,
                mode = bstMode,
                customGraph = customGraph,
                customCoordinates = customCoordinates
            )
            AlgorithmId.HEAP -> generateHeapSteps(
                input = effectiveArray,
                sortOrder = sortOrder,
                customPositions = customCoordinates
            )
            AlgorithmId.BFS -> generateBFSSteps(traversalStartNodeId, customGraph, targetNodeId)
            AlgorithmId.DFS -> generateDFSSteps(traversalStartNodeId, customGraph, targetNodeId)
            AlgorithmId.DIJKSTRA -> generateDijkstraSteps(traversalStartNodeId, customGraph, targetNodeId)
        }
    }

    /**
     * Generate steps keyed directly by [AlgorithmId], bypassing the
     * `Algorithm` UI handle. Prefer this in tests and in code paths that
     * already have a stable identifier.
     */
    fun generateStepsForId(
        id: AlgorithmId,
        inputArray: List<Int> = emptyList(),
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
                variables = mapOf("n" to "$n"),
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
                        variables = mapOf("i" to "$i", "j" to "$j", "arr[j]" to "${arr[j]}", "arr[j+1]" to "${arr[j + 1]}"),
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
                            variables = mapOf("i" to "$i", "j" to "$j", "arr[j]" to "${arr[j]}", "arr[j+1]" to "${arr[j + 1]}", "swapped" to "true"),
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
                    variables = mapOf("i" to "$i", "tailIndex" to "${n - i - 1}", "tailVal" to "${arr[n - i - 1]}"),
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
                variables = mapOf("n" to "$n", "sorted" to "true"),
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
                variables = mapOf("n" to "$n"),
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
                    variables = mapOf("i" to "$i", "$extremeLabel" to "${arr[minIdx]}", "minIdx" to "$minIdx"),
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
                        variables = mapOf("i" to "$i", "j" to "$j", "arr[j]" to "${arr[j]}", "$extremeLabel" to "${arr[minIdx]}", "minIdx" to "$minIdx"),
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
                            variables = mapOf("i" to "$i", "j" to "$j", "new$extremeLabel" to "${arr[minIdx]}", "minIdx" to "$minIdx"),
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
                        variables = mapOf("i" to "$i", "swapped" to "arr[$i] <-> arr[$minIdx]"),
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
                    variables = mapOf("i" to "$i", "sortedBoundary" to "${i + 1}"),
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
                variables = mapOf("n" to "$n", "sorted" to "true"),
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
                variables = mapOf("n" to "$n"),
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
                    variables = mapOf("i" to "$i", "j" to "$j", "key" to "$key"),
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
                        variables = mapOf("i" to "$i", "j" to "$j", "key" to "$key", "arr[j]" to "${arr[j]}"),
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
                    elementStates = (0..i).associateWith { idx ->
                        if (i == n - 1 && idx == j + 1) ElementState.ACTIVE else ElementState.SORTED
                    },
                    bottomPointers = mapOf("i" to i),
                    variables = mapOf("i" to "$i", "insertedAt" to "${j + 1}", "key" to "$key"),
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
                variables = mapOf("n" to "$n", "sorted" to "true"),
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
                variables = mapOf("n" to "${arr.size}"),
                callStack = listOf("mergeSort(0, ${arr.size - 1})"),
                activeCodeLines = listOf(1, 2)
            )
        )

        fun merge(l: Int, m: Int, r: Int, depth: Int, path: List<IntRange>) {
            val left = arr.subList(l, m + 1).toList()
            val right = arr.subList(m + 1, r + 1).toList()
            var i = 0
            var j = 0
            var k = l
            val frames = path.map { "mergeSort(${it.first}, ${it.last})" }

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
                    variables = mapOf("l" to "$l", "m" to "$m", "r" to "$r", "leftSize" to "${left.size}", "rightSize" to "${right.size}"),
                    callStack = frames,
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
                        variables = mapOf("l" to "$l", "m" to "$m", "r" to "$r", "k" to "$k", "i" to "$i", "j" to "$j", "merged" to "$compVal"),
                        callStack = frames,
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
                        variables = mapOf("l" to "$l", "r" to "$r", "k" to "$k", "flushed" to "${left[i]}"),
                        callStack = frames,
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
                        variables = mapOf("l" to "$l", "r" to "$r", "k" to "$k", "flushed" to "${right[j]}"),
                        callStack = frames,
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
                    variables = mapOf("mergedRange" to "[$l..$r]"),
                    callStack = frames,
                    activeCodeLines = listOf(6)
                )
            )
        }

        fun sort(l: Int, r: Int, depth: Int, path: List<IntRange>) {
            if (l < r) {
                val m = (l + r) / 2
                val currentPath = path + listOf(l..r)
                val frames = currentPath.map { "mergeSort(${it.first}, ${it.last})" }
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
                        variables = mapOf("l" to "$l", "m" to "$m", "r" to "$r", "depth" to "$depth"),
                        callStack = frames,
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
                variables = mapOf("n" to "${arr.size}", "sorted" to "true"),
                callStack = listOf("mergeSort(0, ${arr.size - 1})"),
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
                variables = mapOf("n" to "${arr.size}"),
                callStack = listOf("quickSort(0, ${arr.size - 1})"),
                activeCodeLines = listOf(1, 2)
            )
        )

        val callFrames = mutableListOf<String>()

        fun partition(low: Int, high: Int, frames: List<String>): Int {
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
                    variables = mapOf("low" to "$low", "high" to "$high", "pivot" to "$pivot"),
                    callStack = frames,
                    recursionDepth = frames.size,
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
                        variables = mapOf("low" to "$low", "high" to "$high", "pivot" to "$pivot", "i" to "$i", "j" to "$j", "arr[j]" to "${arr[j]}"),
                        callStack = frames,
                        recursionDepth = frames.size,
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
                            variables = mapOf("low" to "$low", "high" to "$high", "pivot" to "$pivot", "i" to "$i", "j" to "$j", "swapped" to "arr[$i] <-> arr[$j]"),
                            callStack = frames,
                            recursionDepth = frames.size,
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
                    variables = mapOf("low" to "$low", "high" to "$high", "pivot" to "$pivot", "placedSlot" to "${i + 1}"),
                    callStack = frames,
                    recursionDepth = frames.size,
                    activeCodeLines = listOf(13)
                )
            )

            return i + 1
        }

        fun quickSort(low: Int, high: Int) {
            if (low < high) {
                val frame = "quickSort($low, $high)"
                callFrames.add(frame)
                val pi = partition(low, high, callFrames.toList())
                quickSort(low, pi - 1)
                quickSort(pi + 1, high)
                callFrames.removeAt(callFrames.lastIndex)
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
                variables = mapOf("n" to "${arr.size}", "sorted" to "true"),
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

        val targetIdx = input.indexOf(target)
        val initialTopPointers = if (targetIdx >= 0) mapOf("target" to targetIdx) else emptyMap()

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Starting Linear Search for target = $target",
                comparisonExpr = "TARGET: $target",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = input,
                topPointers = initialTopPointers,
                variables = mapOf("target" to "$target"),
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
                    phaseLabel = if (isFound) "TARGET FOUND" else "COMPARING",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = input,
                    elementStates = mapOf(i to if (isFound) ElementState.FOUND else ElementState.COMPARING),
                    bottomPointers = mapOf("i" to i),
                    variables = mapOf("i" to "$i", "target" to "$target", "arr[i]" to "${input[i]}", "match" to "$isFound"),
                    activeCodeLines = listOf(3)
                )
            )

            if (isFound) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Target $target FOUND at index $i!",
                        comparisonExpr = "FOUND: index $i",
                        phaseLabel = "TARGET FOUND",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = input,
                        elementStates = mapOf(i to ElementState.FOUND),
                        bottomPointers = mapOf("found" to i),
                        variables = mapOf("target" to "$target", "foundIndex" to "$i"),
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
                comparisonExpr = "NOT FOUND: target $target",
                phaseLabel = "NOT FOUND",
                renderMode = VisualizerRenderMode.CELLS,
                array = input,
                variables = mapOf("target" to "$target", "found" to "false"),
                activeCodeLines = listOf(5)
            )
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 7. Binary Search
    // ─────────────────────────────────────────────────────────────
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
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.CELLS,
                array = sortedInput,
                activeRange = 0 until sortedInput.size,
                topPointers = mapOf("L" to low, "H" to high),
                variables = mapOf("target" to "$target", "low" to "$low", "high" to "$high"),
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
                    phaseLabel = "COMPARING",
                    renderMode = VisualizerRenderMode.CELLS,
                    array = sortedInput,
                    activeRange = low..high,
                    elementStates = mapOf(mid to ElementState.COMPARING),
                    topPointers = mapOf("L" to low, "H" to high),
                    bottomPointers = mapOf("mid" to mid),
                    variables = mapOf("low" to "$low", "high" to "$high", "mid" to "$mid", "midVal" to "$midVal", "target" to "$target"),
                    activeCodeLines = listOf(3, 4, 5)
                )
            )

            if (midVal == target) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Target $target FOUND at index $mid!",
                        comparisonExpr = "FOUND: index $mid ($target)",
                        phaseLabel = "TARGET FOUND",
                        renderMode = VisualizerRenderMode.CELLS,
                        array = sortedInput,
                        activeRange = low..high,
                        elementStates = mapOf(mid to ElementState.FOUND),
                        topPointers = mapOf("L" to low, "H" to high),
                        bottomPointers = mapOf("mid" to mid),
                        variables = mapOf("target" to "$target", "foundIndex" to "$mid"),
                        activeCodeLines = listOf(6)
                    )
                )
                return steps
            } else if (midVal < target) {
                // ASC: target is right of mid; DESC: target is left of mid.
                if (isDesc) {
                    val newHigh = mid - 1
                    val nextRange = if (low <= newHigh) low..newHigh else low..high
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal $op target=$target. Eliminating right half. New high = ${mid - 1}",
                            comparisonExpr = "$midVal $op $target -> high = ${mid - 1}",
                            phaseLabel = "ELIMINATING HALF",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = sortedInput,
                            activeRange = nextRange,
                            topPointers = mapOf("L" to low, "H" to (mid - 1).coerceAtLeast(0)),
                            variables = mapOf("low" to "$low", "high" to "$high", "mid" to "$mid", "target" to "$target"),
                            activeCodeLines = listOf(9, 10)
                        )
                    )
                    high = mid - 1
                } else {
                    val newLow = mid + 1
                    val nextRange = if (newLow <= high) newLow..high else low..high
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal $op target=$target. Eliminating left half. New low = ${mid + 1}",
                            comparisonExpr = "$midVal $op $target -> low = ${mid + 1}",
                            phaseLabel = "ELIMINATING HALF",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = sortedInput,
                            activeRange = nextRange,
                            topPointers = mapOf("L" to (mid + 1).coerceAtMost(sortedInput.size - 1), "H" to high),
                            variables = mapOf("low" to "$low", "high" to "$high", "mid" to "$mid", "target" to "$target"),
                            activeCodeLines = listOf(7, 8)
                        )
                    )
                    low = mid + 1
                }
            } else {
                // The other branch.
                if (isDesc) {
                    val newLow = mid + 1
                    val nextRange = if (newLow <= high) newLow..high else low..high
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal > target=$target (descending). Eliminating left half. New low = ${mid + 1}",
                            comparisonExpr = "$midVal > $target -> low = ${mid + 1}",
                            phaseLabel = "ELIMINATING HALF",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = sortedInput,
                            activeRange = nextRange,
                            topPointers = mapOf("L" to (mid + 1).coerceAtMost(sortedInput.size - 1), "H" to high),
                            variables = mapOf("low" to "$low", "high" to "$high", "mid" to "$mid", "target" to "$target"),
                            activeCodeLines = listOf(7, 8)
                        )
                    )
                    low = mid + 1
                } else {
                    val newHigh = mid - 1
                    val nextRange = if (low <= newHigh) low..newHigh else low..high
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "arr[$mid]=$midVal > target=$target. Eliminating right half. New high = ${mid - 1}",
                            comparisonExpr = "$midVal > $target -> high = ${mid - 1}",
                            phaseLabel = "ELIMINATING HALF",
                            renderMode = VisualizerRenderMode.CELLS,
                            array = sortedInput,
                            activeRange = nextRange,
                            topPointers = mapOf("L" to low, "H" to (mid - 1).coerceAtLeast(0)),
                            variables = mapOf("low" to "$low", "high" to "$high", "mid" to "$mid", "target" to "$target"),
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
                comparisonExpr = "NOT FOUND: target $target",
                phaseLabel = "NOT FOUND",
                renderMode = VisualizerRenderMode.CELLS,
                array = sortedInput,
                variables = mapOf("target" to "$target", "found" to "false"),
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

    fun defaultCircularQueueOps(): List<QueueOp> = listOf(
        QueueOp.Enqueue(10),
        QueueOp.Enqueue(20),
        QueueOp.Enqueue(30),
        QueueOp.Dequeue,
        QueueOp.Dequeue,
        QueueOp.Enqueue(40),
        QueueOp.Enqueue(50),
        QueueOp.Enqueue(60),
        QueueOp.Enqueue(70),
        QueueOp.Enqueue(80),
        QueueOp.Enqueue(90),
        QueueOp.Peek,
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
            callFrame: String = "Stack.main()",
            opCount: Int = 0
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
                        "topIdx" to (items.size - 1).toString(),
                        "opCount" to opCount.toString()
                    ),
                    callStack = listOf("Stack.main()", callFrame).distinct()
                )
            )
        }

        addStep("Created empty Stack (LIFO capacity ready)", 1, "INITIALIZING", "STACK READY (size=0)", opCount = 0)

        ops.forEachIndexed { opIdx, op ->
            val countOp = opIdx + 1
            when (op) {
                is BufferOp.Push -> {
                    if (items.size < 8) {
                        val id = (nextId++).toString()
                        items.add(BufferItem(id, op.value.toString(), ElementState.ACTIVE))
                        addStep(
                            desc = "push(${op.value}) -> Pushed ${op.value} onto stack top (index ${items.size - 1})",
                            codeLine = 2,
                            label = "PUSH",
                            expr = "PUSH(${op.value}) -> top = ${op.value}",
                            callFrame = "push(${op.value})",
                            opCount = countOp
                        )
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                    } else {
                        addStep("push(${op.value}) -> Stack overflow (capacity 8 reached). No-op.", 2, "OVERFLOW", "PUSH: overflow (size=8)", "push(${op.value})", opCount = countOp)
                    }
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
                            callFrame = "pop()",
                            opCount = countOp
                        )
                        val popped = items.removeAt(items.size - 1)
                        if (items.isNotEmpty()) {
                            items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                        }
                        addStep(
                            desc = "pop() -> Popped ${popped.value} from stack top. " +
                                if (items.isNotEmpty()) "New top is ${items.last().value}"
                                else "Stack is now empty",
                            codeLine = 4,
                            label = "POP",
                            expr = "POPPED: ${popped.value} -> new top = ${items.lastOrNull()?.value ?: "∅"}",
                            callFrame = "pop() -> ${popped.value}",
                            opCount = countOp
                        )
                    } else {
                        addStep("pop() -> Stack underflow (empty). No-op.", 4, "UNDERFLOW", "POP: underflow (size=0)", "pop()", opCount = countOp)
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
                            callFrame = "peek() -> ${items.last().value}",
                            opCount = countOp
                        )
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                    } else {
                        addStep("peek() -> Stack is empty. Returns null.", 6, "PEEK", "PEEK() == null", "peek()", opCount = countOp)
                    }
                }
            }
        }

        addStep("Stack sequence complete (${ops.size} operations).", 1, "DONE", "COMPLETE (${items.size} items)", opCount = ops.size)
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
            callFrame: String = "Queue.main()",
            opCount: Int = 0
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
                        "rear" to rearVal,
                        "opCount" to opCount.toString()
                    ),
                    callStack = listOf("Queue.main()", callFrame).distinct()
                )
            )
        }

        addStep("Created empty Queue (FIFO capacity ready)", 1, "INITIALIZING", "QUEUE READY (size=0)", opCount = 0)

        ops.forEachIndexed { opIdx, op ->
            val countOp = opIdx + 1
            when (op) {
                is QueueOp.Enqueue -> {
                    if (items.size < 8) {
                        val id = (nextId++).toString()
                        items.add(BufferItem(id, op.value.toString(), ElementState.ACTIVE))
                        addStep(
                            desc = "enqueue(${op.value}) -> Added ${op.value} to rear of queue (index ${items.size - 1})",
                            codeLine = 2,
                            label = "ENQUEUE",
                            expr = "ENQUEUE(${op.value}) -> rear = ${op.value}",
                            callFrame = "enqueue(${op.value})",
                            opCount = countOp
                        )
                        items[items.size - 1] = items[items.size - 1].copy(state = ElementState.IDLE)
                    } else {
                        addStep("enqueue(${op.value}) -> Queue overflow (capacity 8 reached). No-op.", 2, "OVERFLOW", "ENQUEUE: overflow (size=8)", "enqueue(${op.value})", opCount = countOp)
                    }
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
                            callFrame = "dequeue()",
                            opCount = countOp
                        )
                        val removed = items.removeAt(0)
                        if (items.isNotEmpty()) {
                            items[0] = items[0].copy(state = ElementState.IDLE)
                        }
                        addStep(
                            desc = "dequeue() -> Removed ${removed.value} from front. " +
                                if (items.isNotEmpty()) "New front is ${items[0].value}"
                                else "Queue is now empty",
                            codeLine = 4,
                            label = "DEQUEUE",
                            expr = "DEQUEUED: ${removed.value} -> new front = ${items.firstOrNull()?.value ?: "∅"}",
                            callFrame = "dequeue() -> ${removed.value}",
                            opCount = countOp
                        )
                    } else {
                        addStep("dequeue() -> Queue underflow (empty). No-op.", 4, "UNDERFLOW", "DEQUEUE: underflow (size=0)", "dequeue()", opCount = countOp)
                    }
                }
                QueueOp.Peek -> {
                    if (items.isNotEmpty()) {
                        val frontItem = items.first()
                        items[0] = frontItem.copy(state = ElementState.FOUND)
                        addStep(
                            desc = "peek() -> Inspected front element ${frontItem.value} at front of queue without removing",
                            codeLine = 5,
                            label = "PEEK",
                            expr = "PEEK() == ${frontItem.value}",
                            callFrame = "peek() -> ${frontItem.value}",
                            opCount = countOp
                        )
                        items[0] = frontItem.copy(state = ElementState.IDLE)
                    } else {
                        addStep("peek() -> Queue underflow (empty). No-op.", 5, "UNDERFLOW", "PEEK: underflow (size=0)", "peek()", opCount = countOp)
                    }
                }
            }
        }

        addStep("Queue sequence complete (${ops.size} operations).", 1, "DONE", "COMPLETE (${items.size} items)", opCount = ops.size)
        return steps
    }

    /**
     * Radial Ring Buffer step generator for Circular Queue (fixed capacity N).
     * Models modular arithmetic:
     *   enqueue: rear = (rear + 1) % capacity
     *   dequeue: front = (front + 1) % capacity
     */
    fun generateCircularQueueSteps(
        operations: List<QueueOp> = defaultCircularQueueOps(),
        capacity: Int = 8
    ): List<VisualizerStep> {
        val ops = operations.ifEmpty { defaultCircularQueueOps() }
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val buffer = Array(capacity) { "—" }
        var front = -1
        var rear = -1
        var count = 0

        fun currentBufferItems(activeIdx: Int? = null, activeState: ElementState = ElementState.ACTIVE): List<BufferItem> {
            return (0 until capacity).map { i ->
                val state = when {
                    i == activeIdx -> activeState
                    buffer[i] != "—" -> ElementState.IDLE
                    else -> ElementState.IDLE
                }
                BufferItem(id = "cq_$i", value = buffer[i], state = state)
            }
        }

        fun addStep(
            desc: String,
            codeLine: Int,
            label: String,
            expr: String,
            activeIdx: Int? = null,
            activeState: ElementState = ElementState.ACTIVE,
            callFrame: String = "CircularQueue.main()",
            opCount: Int = 0
        ) {
            val formula = if (rear >= 0) "(rear + 1) % $capacity = ${(rear + 1) % capacity}" else "front=0, rear=0"
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    comparisonExpr = expr,
                    phaseLabel = label,
                    renderMode = VisualizerRenderMode.BUFFER,
                    buffer = currentBufferItems(activeIdx, activeState),
                    bufferCapacity = capacity,
                    bufferLabel = "CIRCULAR RING BUFFER (N=$capacity)",
                    topPointers = mapOf(
                        "F" to front,
                        "R" to rear
                    ),
                    activeCodeLines = listOf(codeLine),
                    variables = mapOf(
                        "front" to if (front >= 0) "[$front]" else "NONE",
                        "rear" to if (rear >= 0) "[$rear]" else "NONE",
                        "size" to "$count / $capacity",
                        "formula" to formula,
                        "queueVariant" to QueueVariant.CIRCULAR_RING.name,
                        "opCount" to opCount.toString()
                    ),
                    callStack = listOf("CircularQueue", callFrame).distinct()
                )
            )
        }

        addStep(
            desc = "Initialized 8-slot Circular Ring Buffer (empty, front=-1, rear=-1).",
            codeLine = 1,
            label = "INITIALIZING",
            expr = "INIT: N=$capacity",
            opCount = 0
        )

        ops.forEachIndexed { opIdx, op ->
            val countOp = opIdx + 1
            when (op) {
                is QueueOp.Enqueue -> {
                    if (count < capacity) {
                        val prevRear = rear
                        if (front == -1) {
                            front = 0
                            rear = 0
                        } else {
                            rear = (rear + 1) % capacity
                        }
                        buffer[rear] = op.value.toString()
                        count++
                        addStep(
                            desc = "enqueue(${op.value}) ➔ Stored at slot [$rear]. Index calculated as ($prevRear + 1) % $capacity = $rear.",
                            codeLine = 2,
                            label = "ENQUEUE",
                            expr = "ENQUEUE(${op.value}) ➔ slot[$rear]",
                            activeIdx = rear,
                            activeState = ElementState.ACTIVE,
                            callFrame = "enqueue(${op.value})",
                            opCount = countOp
                        )
                    } else {
                        addStep(
                            desc = "enqueue(${op.value}) ➔ Queue Overflow! Capacity $capacity reached. No-op.",
                            codeLine = 2,
                            label = "OVERFLOW",
                            expr = "OVERFLOW (size=$count/$capacity)",
                            callFrame = "enqueue(${op.value})",
                            opCount = countOp
                        )
                    }
                }
                QueueOp.Dequeue -> {
                    if (count > 0 && front != -1) {
                        val prevFront = front
                        val removedVal = buffer[front]
                        addStep(
                            desc = "dequeue() ➔ Removing front element '$removedVal' at slot [$prevFront].",
                            codeLine = 3,
                            label = "DEQUEUING",
                            expr = "DEQUEUE: slot[$prevFront] = $removedVal",
                            activeIdx = prevFront,
                            activeState = ElementState.SWAPPING,
                            callFrame = "dequeue()",
                            opCount = countOp
                        )
                        buffer[prevFront] = "—"
                        count--
                        if (count == 0) {
                            front = -1
                            rear = -1
                        } else {
                            front = (prevFront + 1) % capacity
                        }
                        addStep(
                            desc = "dequeue() complete ➔ Removed '$removedVal'. Next front advances to [${if (front >= 0) front else "NONE"}] via ($prevFront + 1) % $capacity.",
                            codeLine = 4,
                            label = "DEQUEUE",
                            expr = "DEQUEUED: $removedVal ➔ new front=[$front]",
                            callFrame = "dequeue() -> $removedVal",
                            opCount = countOp
                        )
                    } else {
                        addStep(
                            desc = "dequeue() ➔ Queue Underflow! Buffer is empty (front=-1, rear=-1). No-op.",
                            codeLine = 4,
                            label = "UNDERFLOW",
                            expr = "UNDERFLOW (size=0)",
                            callFrame = "dequeue()",
                            opCount = countOp
                        )
                    }
                }
                QueueOp.Peek -> {
                    if (count > 0 && front != -1) {
                        val peekVal = buffer[front]
                        addStep(
                            desc = "peek() ➔ Inspected front element '$peekVal' at slot [$front] without advancing pointers.",
                            codeLine = 5,
                            label = "PEEK",
                            expr = "PEEK() == $peekVal at [$front]",
                            activeIdx = front,
                            activeState = ElementState.FOUND,
                            callFrame = "peek() -> $peekVal",
                            opCount = countOp
                        )
                    } else {
                        addStep(
                            desc = "peek() ➔ Queue Underflow! Buffer is empty.",
                            codeLine = 5,
                            label = "UNDERFLOW",
                            expr = "PEEK: underflow (size=0)",
                            callFrame = "peek()",
                            opCount = countOp
                        )
                    }
                }
            }
        }

        addStep(
            desc = "Circular Queue sequence complete (${ops.size} operations processed).",
            codeLine = 1,
            label = "DONE",
            expr = "COMPLETE ($count / $capacity occupied)",
            opCount = ops.size
        )
        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 10. Binary Search Tree (BST)
    // ─────────────────────────────────────────────────────────────
    val defaultBstValues = listOf(50, 30, 70, 20, 40, 60, 80)
    val defaultBstSearchKey = 40

    /**
     * Build a BST imperatively from [values], ordering the returned node list
     * strictly by insertion position (`0 until values.size`) so prefix frame `i`
     * always highlights the `i`-th inserted node (`nodes[i]`), including duplicates.
     */
    private fun buildBstFromValues(values: List<Int>): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
        if (values.isEmpty()) return emptyList<GraphNodeState>() to emptyList()

        data class Node(val value: Int, val id: String, var left: Node? = null, var right: Node? = null)

        val root = Node(values[0], values[0].toString())
        val seen = mutableSetOf(root.id)
        val insertionOrderIds = mutableListOf(root.id)
        for (i in 1 until values.size) {
            val v = values[i]
            var cur: Node = root
            val baseId = v.toString()
            var id = baseId
            var k = 1
            while (id in seen) { id = "$baseId#$k"; k++ }
            seen.add(id)
            insertionOrderIds.add(id)
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
        val nodesById = inOrderList.associateBy { it.id }

        val spanX = maxOf(160f, (total - 1) * 28f)
        val spanY = maxOf(86f, maxDepth * 26f)
        val minX = 130f - spanX / 2f
        val minY = 75f - spanY / 2f

        // Preserve insertion order so nodes[i] is always the i-th inserted element
        val nodes = insertionOrderIds.mapNotNull { id ->
            val n = nodesById[id] ?: return@mapNotNull null
            val rank = rankMap[id] ?: 0
            val depth = depths[id] ?: 0
            val x = if (total <= 1) 130f else minX + (rank.toFloat() / (total - 1).toFloat()) * spanX
            val y = if (maxDepth == 0) 75f else minY + (depth.toFloat() / maxDepth.toFloat()) * spanY
            GraphNodeState(n.id, n.value.toString(), x, y)
        }

        return nodes to edges
    }

    fun generateBSTSteps(
        values: List<Int> = defaultBstValues,
        searchKey: Int = defaultBstSearchKey,
        mode: BstMode = BstMode.SEARCH,
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null,
        customCoordinates: Map<String, Offset>? = null
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0

        val (rawNodes, rawEdges) = if (customGraph != null) {
            if (customGraph.first.isEmpty()) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Empty BST. Tap Add Node to insert a key.",
                        comparisonExpr = "EMPTY BST",
                        phaseLabel = "INITIALIZING",
                        renderMode = VisualizerRenderMode.GRAPH_TREE,
                        nodes = emptyList(),
                        edges = emptyList(),
                        activeCodeLines = listOf(3, 4)
                    )
                )
                return steps
            }
            customGraph
        } else {
            val cleanValues = values.take(15)
            if (cleanValues.isEmpty()) {
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Empty BST. Tap Add Node to insert a key.",
                        comparisonExpr = "EMPTY BST",
                        phaseLabel = "INITIALIZING",
                        renderMode = VisualizerRenderMode.GRAPH_TREE,
                        nodes = emptyList(),
                        edges = emptyList(),
                        activeCodeLines = listOf(3, 4)
                    )
                )
                return steps
            }
            if (cleanValues == defaultBstValues) {
                listOf(
                    GraphNodeState("50", "50", 130f, 30f),
                    GraphNodeState("30", "30", 80f, 65f),
                    GraphNodeState("70", "70", 180f, 65f),
                    GraphNodeState("20", "20", 55f, 105f),
                    GraphNodeState("40", "40", 105f, 105f),
                    GraphNodeState("60", "60", 155f, 105f),
                    GraphNodeState("80", "80", 205f, 105f)
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
        }

        val nodes = if (customCoordinates != null) {
            rawNodes.map { node ->
                customCoordinates[node.id]?.let { node.copy(x = it.x, y = it.y) } ?: node
            }
        } else {
            rawNodes
        }
        val edges = rawEdges

        val byId = nodes.associateBy { it.id }
        val childrenById = edges.groupBy { it.from }.mapValues { (_, es) -> es.map { it.to } }

        val rootId = nodes.firstOrNull()?.id
        if (rootId == null) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Empty BST. Nothing to traverse.",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes,
                    edges = edges,
                    activeCodeLines = listOf(3, 4)
                )
            )
            return steps
        }

        if (mode != BstMode.SEARCH) {
            return generateBstTraversalSteps(
                nodes = nodes,
                edges = edges,
                byId = byId,
                childrenById = childrenById,
                rootId = rootId,
                mode = mode
            )
        }

        // Step 0: Show fully pre-built tree — skip incremental construction phase.
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BST ready (${nodes.size} nodes). Starting search from root ${byId[rootId]?.label} for target key = $searchKey.",
                comparisonExpr = "SEARCH: target = $searchKey",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { if (it.id == rootId) it.copy(state = ElementState.ACTIVE) else it },
                edges = edges,
                buffer = listOf(BufferItem("path_0", byId[rootId]?.label ?: rootId, ElementState.ACTIVE, nodeId = rootId)),
                bufferLabel = "BST SEARCH PATH",
                activeNodeId = rootId,
                visitedNodeIds = setOf(rootId),
                activeCodeLines = listOf(3),
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
                BufferItem(
                    id = "p_$idx",
                    value = byId[id]?.label ?: id,
                    state = if (id == current) ElementState.ACTIVE else ElementState.IDLE,
                    nodeId = id
                )
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
                        buffer = pathBuffer.map { if (it.nodeId == current) it.copy(state = ElementState.FOUND) else it },
                        bufferLabel = "BST SEARCH PATH",
                        activeNodeId = current,
                        visitedNodeIds = visited.toSet(),
                        activeCodeLines = listOf(5),
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
                    activeCodeLines = if (searchKey < nodeVal) listOf(6) else listOf(7),
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
                        BufferItem("p_$idx", byId[id]?.label ?: id, ElementState.VISITED, nodeId = id)
                    },
                    bufferLabel = "BST SEARCH PATH",
                    activeNodeId = visited.lastOrNull(),
                    visitedNodeIds = visited.toSet(),
                    activeCodeLines = listOf(4),
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

    /**
     * Recursive tree traversal step generator for Binary Search Tree.
     * Supports IN_ORDER, PRE_ORDER, and POST_ORDER.
     */
    private fun generateBstTraversalSteps(
        nodes: List<GraphNodeState>,
        edges: List<GraphEdgeState>,
        byId: Map<String, GraphNodeState>,
        childrenById: Map<String, List<String>>,
        rootId: String,
        mode: BstMode
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val visitedOrder = mutableListOf<String>()
        val callStack = mutableListOf<String>()

        fun getLeft(id: String): String? {
            val children = childrenById[id] ?: return null
            val node = byId[id] ?: return null
            return children.firstOrNull {
                val c = byId[it] ?: return@firstOrNull false
                c.x < node.x || (c.label.toIntOrNull() ?: 0) < (node.label.toIntOrNull() ?: 0)
            }
        }

        fun getRight(id: String): String? {
            val children = childrenById[id] ?: return null
            val node = byId[id] ?: return null
            return children.firstOrNull {
                val c = byId[it] ?: return@firstOrNull false
                c.x > node.x || (c.label.toIntOrNull() ?: 0) >= (node.label.toIntOrNull() ?: 0)
            }
        }

        fun currentBuffer(): List<BufferItem> = visitedOrder.mapIndexed { idx, id ->
            val label = byId[id]?.label ?: id
            BufferItem(
                id = "t_$idx",
                value = label,
                state = if (idx == visitedOrder.lastIndex) ElementState.FOUND else ElementState.VISITED,
                nodeId = id
            )
        }

        fun addStep(
            desc: String,
            codeLine: Int,
            phase: String,
            expr: String,
            activeId: String?,
            visitingId: String? = null
        ) {
            val activePath = callStack.mapNotNull { frame ->
                val label = frame.substringAfter("(").substringBefore(")")
                byId.values.firstOrNull { it.label == label }?.id
            }.toSet()

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = desc,
                    comparisonExpr = expr,
                    phaseLabel = phase,
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = nodes.map { node ->
                        when {
                            node.id == visitingId -> node.copy(state = ElementState.FOUND)
                            node.id == activeId -> node.copy(state = ElementState.ACTIVE)
                            node.id in visitedOrder -> node.copy(state = ElementState.VISITED)
                            node.id in activePath -> node.copy(state = ElementState.COMPARING)
                            else -> node.copy(state = ElementState.IDLE)
                        }
                    },
                    edges = edges.map { e ->
                        if (e.from in activePath && (e.to in activePath || e.to == activeId)) {
                            e.copy(isHighlighted = true)
                        } else {
                            e.copy(isHighlighted = false)
                        }
                    },
                    buffer = currentBuffer(),
                    bufferLabel = "TRAVERSAL OUTPUT (${mode.displayName.uppercase()})",
                    activeNodeId = activeId,
                    visitedNodeIds = visitedOrder.toSet(),
                    activeCodeLines = listOf(codeLine),
                    variables = mapOf(
                        "mode" to mode.displayName,
                        "formula" to mode.formula,
                        "current" to (activeId?.let { byId[it]?.label } ?: "null"),
                        "emitted" to visitedOrder.size.toString(),
                        "bstMode" to mode.name
                    ),
                    callStack = callStack.toList()
                )
            )
        }

        // Initializing Step
        addStep(
            desc = "Starting ${mode.displayName} Traversal (${mode.formula}) on root node ${byId[rootId]?.label}.",
            codeLine = 1,
            phase = "INITIALIZING",
            expr = mode.formula,
            activeId = rootId
        )

        fun traverse(currId: String?) {
            val methodName = when (mode) {
                BstMode.IN_ORDER -> "inOrder"
                BstMode.PRE_ORDER -> "preOrder"
                BstMode.POST_ORDER -> "postOrder"
                BstMode.SEARCH -> "search"
            }
            if (currId == null) {
                callStack.add("$methodName(null)")
                addStep(
                    desc = "Reached null leaf branch. Returning from recursive call.",
                    codeLine = 2,
                    phase = "BACKTRACKING",
                    expr = "node == null ➔ return",
                    activeId = null
                )
                callStack.removeAt(callStack.lastIndex)
                return
            }

            val nodeLabel = byId[currId]?.label ?: currId
            callStack.add("$methodName($nodeLabel)")

            when (mode) {
                BstMode.IN_ORDER -> {
                    val left = getLeft(currId)
                    val right = getRight(currId)

                    // 1. Traverse Left
                    addStep(
                        desc = "inOrder($nodeLabel) ➔ Recurse left child (${left?.let { byId[it]?.label } ?: "null"}).",
                        codeLine = 3,
                        phase = "TRAVERSING",
                        expr = "inOrder(node.left)",
                        activeId = currId
                    )
                    traverse(left)

                    // 2. Visit Node (Emit)
                    visitedOrder.add(currId)
                    addStep(
                        desc = "inOrder($nodeLabel) ➔ Visit node $nodeLabel. Emitted to output tray: [${visitedOrder.map { byId[it]?.label }.joinToString(", ")}].",
                        codeLine = 4,
                        phase = "VISIT_NODE",
                        expr = "VISIT: $nodeLabel",
                        activeId = currId,
                        visitingId = currId
                    )

                    // 3. Traverse Right
                    addStep(
                        desc = "inOrder($nodeLabel) ➔ Recurse right child (${right?.let { byId[it]?.label } ?: "null"}).",
                        codeLine = 5,
                        phase = "TRAVERSING",
                        expr = "inOrder(node.right)",
                        activeId = currId
                    )
                    traverse(right)
                }

                BstMode.PRE_ORDER -> {
                    val left = getLeft(currId)
                    val right = getRight(currId)

                    // 1. Visit Node (Emit)
                    visitedOrder.add(currId)
                    addStep(
                        desc = "preOrder($nodeLabel) ➔ Visit root $nodeLabel before children. Emitted to output tray: [${visitedOrder.map { byId[it]?.label }.joinToString(", ")}].",
                        codeLine = 3,
                        phase = "VISIT_NODE",
                        expr = "VISIT: $nodeLabel",
                        activeId = currId,
                        visitingId = currId
                    )

                    // 2. Traverse Left
                    addStep(
                        desc = "preOrder($nodeLabel) ➔ Recurse left child (${left?.let { byId[it]?.label } ?: "null"}).",
                        codeLine = 4,
                        phase = "TRAVERSING",
                        expr = "preOrder(node.left)",
                        activeId = currId
                    )
                    traverse(left)

                    // 3. Traverse Right
                    addStep(
                        desc = "preOrder($nodeLabel) ➔ Recurse right child (${right?.let { byId[it]?.label } ?: "null"}).",
                        codeLine = 5,
                        phase = "TRAVERSING",
                        expr = "preOrder(node.right)",
                        activeId = currId
                    )
                    traverse(right)
                }

                BstMode.POST_ORDER -> {
                    val left = getLeft(currId)
                    val right = getRight(currId)

                    // 1. Traverse Left
                    addStep(
                        desc = "postOrder($nodeLabel) ➔ Recurse left child (${left?.let { byId[it]?.label } ?: "null"}).",
                        codeLine = 3,
                        phase = "TRAVERSING",
                        expr = "postOrder(node.left)",
                        activeId = currId
                    )
                    traverse(left)

                    // 2. Traverse Right
                    addStep(
                        desc = "postOrder($nodeLabel) ➔ Recurse right child (${right?.let { byId[it]?.label } ?: "null"}).",
                        codeLine = 4,
                        phase = "TRAVERSING",
                        expr = "postOrder(node.right)",
                        activeId = currId
                    )
                    traverse(right)

                    // 3. Visit Node (Emit)
                    visitedOrder.add(currId)
                    addStep(
                        desc = "postOrder($nodeLabel) ➔ Visit root $nodeLabel after both children. Emitted to output tray: [${visitedOrder.map { byId[it]?.label }.joinToString(", ")}].",
                        codeLine = 5,
                        phase = "VISIT_NODE",
                        expr = "VISIT: $nodeLabel",
                        activeId = currId,
                        visitingId = currId
                    )
                }

                BstMode.SEARCH -> Unit
            }

            callStack.removeAt(callStack.lastIndex)
        }

        traverse(rootId)

        // Final Completed Step
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Completed ${mode.displayName} Traversal (${visitedOrder.size} nodes). Final Sequence: [${visitedOrder.map { byId[it]?.label }.joinToString(", ")}].",
                comparisonExpr = "COMPLETE: [${visitedOrder.map { byId[it]?.label }.joinToString(", ")}]",
                phaseLabel = "DONE",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = nodes.map { it.copy(state = ElementState.VISITED) },
                edges = edges,
                buffer = currentBuffer(),
                bufferLabel = "TRAVERSAL OUTPUT (${mode.displayName.uppercase()})",
                activeNodeId = null,
                visitedNodeIds = visitedOrder.toSet(),
                activeCodeLines = listOf(1),
                variables = mapOf(
                    "mode" to mode.displayName,
                    "formula" to mode.formula,
                    "totalNodes" to visitedOrder.size.toString(),
                    "sequence" to visitedOrder.map { byId[it]?.label }.joinToString(", "),
                    "bstMode" to mode.name
                ),
                callStack = emptyList()
            )
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 11. Heap (Max-Heap / Min-Heap with Dual Tree + Array State)
    // ─────────────────────────────────────────────────────────────
    fun generateHeapSteps(
        input: List<Int> = DEFAULT_HEAP_INPUT,
        sortOrder: SortOrder = SortOrder.ASC,
        customPositions: Map<String, Offset>? = null
    ): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val isDesc = sortOrder == SortOrder.DESC
        val heapKind = if (isDesc) "Min-Heap" else "Max-Heap"
        val op = if (isDesc) "<" else ">"

        if (input.isEmpty()) {
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "Empty Heap. Tap Add Node to push values.",
                    comparisonExpr = "EMPTY HEAP",
                    phaseLabel = "INITIALIZING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    array = emptyList(),
                    nodes = emptyList(),
                    edges = emptyList(),
                    activeCodeLines = listOf(1, 2)
                )
            )
            return steps
        }

        val initial = input.take(15)
        val arr = initial.toMutableList()
        val n = arr.size

        fun buildTreeNodes(
            values: List<Int>,
            states: Map<Int, ElementState>,
            heapBound: Int = values.size
        ): Pair<List<GraphNodeState>, List<GraphEdgeState>> {
            val maxLevel = if (values.size <= 1) 0 else 31 - Integer.numberOfLeadingZeros(values.size)
            val treeNodes = values.mapIndexed { idx, v ->
                val slotId = idx.toString()
                val level = 31 - Integer.numberOfLeadingZeros(idx + 1)
                val indexInLevel = (idx + 1) - (1 shl level)
                val nodesInLevel = 1 shl level
                val spanX = 170f
                val minX = 130f - spanX / 2f
                val spanY = 82f
                val minY = 75f - spanY / 2f
                val defaultX = minX + ((indexInLevel + 0.5f) / nodesInLevel.toFloat()) * spanX
                val defaultY = if (maxLevel == 0) 75f else minY + (level.toFloat() / maxLevel.toFloat()) * spanY
                val custom = customPositions?.get(slotId)
                val x = custom?.x ?: defaultX
                val y = custom?.y ?: defaultY
                val st = states[idx] ?: if (idx >= heapBound) ElementState.SORTED else ElementState.IDLE
                GraphNodeState(slotId, v.toString(), x, y, st)
            }
            val treeEdges = mutableListOf<GraphEdgeState>()
            for (i in 0 until values.size) {
                val left = 2 * i + 1
                val right = 2 * i + 2
                if (left < values.size) {
                    val hi = (states[i] == ElementState.ACTIVE || states[i] == ElementState.SWAPPING) &&
                        (states[left] == ElementState.COMPARING || states[left] == ElementState.SWAPPING)
                    treeEdges.add(GraphEdgeState(i.toString(), left.toString(), weight = null, isDirected = true, isHighlighted = hi))
                }
                if (right < values.size) {
                    val hi = (states[i] == ElementState.ACTIVE || states[i] == ElementState.SWAPPING) &&
                        (states[right] == ElementState.COMPARING || states[right] == ElementState.SWAPPING)
                    treeEdges.add(GraphEdgeState(i.toString(), right.toString(), weight = null, isDirected = true, isHighlighted = hi))
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
            if (activeIdx != null && activeIdx in 0 until heapBound) {
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
                        if (activeIdx != null && activeIdx in 0 until heapBound) {
                            put("parentIdx", activeIdx.toString())
                            put("parentVal", arr[activeIdx].toString())
                        }
                    },
                    callStack = listOf("Heap.main()", callFrame).distinct()
                )
            )
        }

        emitHeapStep(
            desc = "$heapKind: Initial array $arr (${n} elements) mapped to Complete Binary Tree. For any index i, Left child is at 2i+1, Right child at 2i+2. Starting bottom-up buildHeap from last parent index ${(n / 2) - 1}.",
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
                }
                val rightStr = if (right < endExclusive) ", R[$right]=${arr[right]}" else ""
                emitHeapStep(
                    desc = "siftDown(i=$root): Checking $heapKind condition for parent P[$root]=${arr[root]} with child L[$left]=${arr[left]}$rightStr",
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
                    }
                    emitHeapStep(
                        desc = "Child arr[$target] ($childVal) $op Parent arr[$root] ($parentVal) violates $heapKind property -> Swapping index $root ↔ $target to elevate $childVal.",
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
            desc = "$heapKind fully established! All parent nodes satisfy the heap property (Parent ${if (isDesc) "≤" else "≥"} Children). Root arr[0]=${arr[0]} is the guaranteed ${if (isDesc) "minimum" else "maximum"} element.",
            expr = "HEAP READY: root = ${arr[0]}",
            phase = "HEAPIFIED",
            states = (0 until n).associateWith { idx -> if (idx == 0) ElementState.FOUND else ElementState.IDLE },
            activeIdx = 0,
            codeLines = listOf(2),
            callFrame = "buildHeap() -> complete"
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
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null,
        targetNodeId: String? = null
    ): List<VisualizerStep> {
        if (customGraph != null && customGraph.first.isEmpty()) {
            return listOf(
                VisualizerStep(
                    stepIndex = 0,
                    description = "Empty graph. Tap Add Node to plant a node.",
                    comparisonExpr = "EMPTY GRAPH",
                    phaseLabel = "STANDBY",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = emptyList(),
                    edges = emptyList(),
                    activeCodeLines = listOf(1)
                )
            )
        }
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val (rawNodes, rawEdges) = customGraph ?: canonicalWeightedGraph()
        val baseNodes = rawNodes.map { it.copy(state = ElementState.IDLE) }
        val baseEdges = rawEdges.map { it.copy(isHighlighted = false) }
        val validIds = baseNodes.map { it.id }.toSet()
        val start = if (startNodeId.uppercase() in validIds) startNodeId.uppercase() else (baseNodes.firstOrNull()?.id ?: "A")
        val target = targetNodeId?.uppercase()?.takeIf { it in validIds && it != start }

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
        val parentMap = mutableMapOf<String, String>()
        var foundTarget = false

        visited.add(start)
        queue.addLast(start)
        distMap[start] = 0

        fun queueBufferItems(highlightId: String? = null): List<BufferItem> =
            queue.mapIndexed { idx, id ->
                BufferItem(
                    id = "q_${idx}_$id",
                    value = "$id(d=${distMap[id] ?: 0})",
                    state = if (id == highlightId) ElementState.ACTIVE else ElementState.IDLE,
                    nodeId = id
                )
            }

        fun isTreeEdge(e: GraphEdgeState): Boolean =
            (e.from to e.to) in treeEdges || (e.to to e.from) in treeEdges

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "BFS: Initialized source node $start (dist=0). Enqueued $start into frontier." +
                    if (target != null) " Goal: reach node $target." else "",
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

        while (queue.isNotEmpty() && !foundTarget) {
            val curr = queue.removeFirst()
            val currDist = distMap[curr] ?: 0
            val neighbors = adj[curr].orEmpty()

            // 1. DEQUEUE phase: curr is removed from frontier and set as active inspection focal
            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "BFS: Dequeued $curr (dist=$currDist) from the frontier. Inspecting adjacent edges.",
                    comparisonExpr = "DEQUEUE: $curr | QUEUE: [${queue.joinToString(", ")}]",
                    phaseLabel = "DEQUEUE",
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
                    activeCodeLines = listOf(3, 4),
                    variables = mapOf(
                        "curr" to curr,
                        "dist[$curr]" to currDist.toString(),
                        "queue" to "[${queue.joinToString(",")}]",
                        "visited" to visited.joinToString("→")
                    ),
                    callStack = listOf("bfs(start=$start)", "expand(curr=$curr)")
                )
            )

            for ((nextId, weight) in neighbors) {
                if (nextId !in visited) {
                    visited.add(nextId)
                    distMap[nextId] = currDist + weight
                    parentMap[nextId] = curr
                    treeEdges.add(curr to nextId)

                    // 2. EXPLORE phase: traverse edge from curr to nextId (photon wave travels across the edge)
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "BFS: Traversed edge $curr → $nextId (weight $weight). Discovered unvisited neighbor $nextId.",
                            comparisonExpr = "EXPLORE: $curr → $nextId",
                            phaseLabel = "EXPLORE",
                            renderMode = VisualizerRenderMode.GRAPH_TREE,
                            nodes = baseNodes.map { node ->
                                when {
                                    node.id == nextId -> node.copy(state = ElementState.ACTIVE)
                                    node.id == curr -> node.copy(state = ElementState.VISITED)
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
                            activeNodeId = nextId,
                            visitedNodeIds = visited.toSet(),
                            activeCodeLines = listOf(5, 6),
                            variables = mapOf(
                                "curr" to curr,
                                "next" to nextId,
                                "dist[$nextId]" to (distMap[nextId] ?: 0).toString(),
                                "queue" to "[${queue.joinToString(",")}]",
                                "visited" to visited.joinToString("→")
                            ),
                            callStack = listOf("bfs(start=$start)", "explore(from=$curr, to=$nextId)")
                        )
                    )

                    // 3. ENQUEUE phase: push nextId into the frontier queue
                    queue.addLast(nextId)
                    val isTarget = (nextId == target)
                    if (isTarget) foundTarget = true

                    val enqDesc = if (isTarget) {
                        "BFS: Enqueued TARGET $nextId (dist=${distMap[nextId]}). Goal reached — shortest hop path locked!"
                    } else {
                        "BFS: Enqueued $nextId (dist=${distMap[nextId]}) into frontier queue. Queue = [${queue.joinToString(", ")}]"
                    }

                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = enqDesc,
                            comparisonExpr = "ENQUEUE: $nextId | QUEUE: [${queue.joinToString(", ")}]",
                            phaseLabel = if (isTarget) "FOUND" else "ENQUEUED",
                            renderMode = VisualizerRenderMode.GRAPH_TREE,
                            nodes = baseNodes.map { node ->
                                when {
                                    node.id == nextId && isTarget -> node.copy(state = ElementState.FOUND)
                                    node.id in queue -> node.copy(state = ElementState.COMPARING)
                                    node.id in visited -> node.copy(state = ElementState.VISITED)
                                    else -> node
                                }
                            },
                            edges = baseEdges.map { e ->
                                if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                            },
                            buffer = queueBufferItems(highlightId = nextId),
                            bufferLabel = "BFS FRONTIER QUEUE",
                            activeNodeId = curr,
                            visitedNodeIds = visited.toSet(),
                            activeCodeLines = listOf(6, 7),
                            variables = mapOf(
                                "enqueued" to nextId,
                                "dist[$nextId]" to (distMap[nextId] ?: 0).toString(),
                                "queue" to "[${queue.joinToString(",")}]",
                                "visited" to visited.joinToString("→")
                            ),
                            callStack = listOf("bfs(start=$start)", "enqueue(node=$nextId)")
                        )
                    )

                    if (foundTarget) break
                }
            }
        }

        // Path reconstruction via parentMap when the search was goal-directed.
        val goalPath = if (target != null && foundTarget) {
            val rev = mutableListOf(target)
            var cur: String = target
            while (parentMap.containsKey(cur)) {
                cur = parentMap.getValue(cur)
                rev.add(0, cur)
            }
            rev
        } else null
        val goalPathEdges: Set<Pair<String, String>> =
            goalPath?.zipWithNext()?.map { it.first to it.second }?.toSet() ?: emptySet()

        fun isGoalEdge(e: GraphEdgeState): Boolean =
            (e.from to e.to) in goalPathEdges || (e.to to e.from) in goalPathEdges

        val lastVisited = visited.lastOrNull() ?: start
        val terminalFocal = if (target != null && foundTarget) target else lastVisited
        val terminalDesc = when {
            target != null && foundTarget ->
                "TARGET REACHED: ${goalPath?.joinToString(" → ")} (${(goalPath?.size ?: 1) - 1} hops) after ${visited.size} expansions."
            target != null ->
                "TARGET UNREACHABLE: $target was never discovered. Explored ${visited.size} nodes: ${visited.joinToString(" → ")}."
            else ->
                "BFS Traversal Complete! Order: ${visited.joinToString(" → ")}."
        }
        val terminalPhase = when {
            target != null && foundTarget -> "FOUND"
            target != null -> "NOT_FOUND"
            else -> "SORTED"
        }
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = terminalDesc,
                comparisonExpr = if (target != null && foundTarget) {
                    "PATH: ${goalPath?.joinToString("→")}"
                } else {
                    "COMPLETE: ${visited.size} nodes visited"
                },
                phaseLabel = terminalPhase,
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = baseNodes.map {
                    when {
                        it.id == terminalFocal -> it.copy(state = ElementState.FOUND)
                        it.id in visited -> it.copy(state = ElementState.VISITED)
                        else -> it
                    }
                },
                edges = baseEdges.map { e ->
                    when {
                        isGoalEdge(e) -> e.copy(isHighlighted = true)
                        isTreeEdge(e) -> e.copy(isHighlighted = true)
                        else -> e
                    }
                },
                buffer = emptyList(),
                bufferLabel = "BFS FRONTIER QUEUE",
                activeNodeId = terminalFocal,
                visitedNodeIds = visited.toSet(),
                activeCodeLines = listOf(3),
                variables = buildMap {
                    put("source", start)
                    put("visitedOrder", visited.joinToString("→"))
                    put("totalVisited", visited.size.toString())
                    if (target != null) {
                        put("target", target)
                        put("targetFound", foundTarget.toString())
                        if (goalPath != null) put("path", goalPath.joinToString("→"))
                    }
                },
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
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null,
        targetNodeId: String? = null
    ): List<VisualizerStep> {
        if (customGraph != null && customGraph.first.isEmpty()) {
            return listOf(
                VisualizerStep(
                    stepIndex = 0,
                    description = "Empty graph. Tap Add Node to plant a node.",
                    comparisonExpr = "EMPTY GRAPH",
                    phaseLabel = "STANDBY",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = emptyList(),
                    edges = emptyList(),
                    activeCodeLines = listOf(1)
                )
            )
        }
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val (rawNodes, rawEdges) = customGraph ?: canonicalWeightedGraph()
        val baseNodes = rawNodes.map { it.copy(state = ElementState.IDLE) }
        val baseEdges = rawEdges.map { it.copy(isHighlighted = false) }
        val validIds = baseNodes.map { it.id }.toSet()
        val start = if (startNodeId.uppercase() in validIds) startNodeId.uppercase() else (baseNodes.firstOrNull()?.id ?: "A")
        val target = targetNodeId?.uppercase()?.takeIf { it in validIds && it != start }

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
        val parentMap = mutableMapOf<String, String>()
        var foundTarget = false

        fun isTreeEdge(e: GraphEdgeState): Boolean =
            (e.from to e.to) in treeEdges || (e.to to e.from) in treeEdges

        fun stackBufferItems(): List<BufferItem> =
            callStack.mapIndexed { idx, id ->
                BufferItem(
                    id = "dfs_${idx}_$id",
                    value = "dfs($id)",
                    state = if (idx == callStack.lastIndex) ElementState.ACTIVE else ElementState.IDLE,
                    nodeId = id
                )
            }

        fun dfs(u: String, incomingWeight: Int?) {
            if (foundTarget) return
            visited.add(u)
            callStack.add(u)
            val weightInfo = if (incomingWeight != null) " via edge (w=$incomingWeight)" else ""
            val hitTarget = u == target
            if (hitTarget) foundTarget = true

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = "DFS: Visiting node $u$weightInfo. Call Stack = [${callStack.joinToString(" → ")}]" +
                        if (hitTarget) " TARGET REACHED — recursion stops here." else "",
                    comparisonExpr = "CALL STACK: [${callStack.joinToString(", ") { "dfs($it)" }}]",
                    phaseLabel = if (callStack.size == 1) "INITIALIZING" else "VISITING",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = baseNodes.map { node ->
                        when {
                            node.id == u -> node.copy(state = if (hitTarget) ElementState.FOUND else ElementState.ACTIVE)
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

            if (hitTarget) return

            for ((v, w) in adj[u].orEmpty()) {
                if (v !in visited) {
                    treeEdges.add(u to v)
                    parentMap[v] = u
                    dfs(v, w)
                    if (foundTarget) return
                }
            }

            if (foundTarget) return
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

        val goalPath: List<String> = if (foundTarget && target != null) {
            val p = mutableListOf(target)
            var cur: String = target
            while (parentMap.containsKey(cur)) {
                cur = parentMap.getValue(cur)
                p.add(0, cur)
            }
            p
        } else emptyList()
        val goalEdgePairs: Set<Pair<String, String>> = goalPath.zipWithNext().map { it.first to it.second }.toSet()

        val lastNode = if (foundTarget) target ?: start else (visited.lastOrNull() ?: start)
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = when {
                    foundTarget -> "TARGET REACHED: ${goalPath.joinToString(" → ")} (${(goalPath.size - 1).coerceAtLeast(0)} hops) after ${visited.size} visits. The call stack held the whole route."
                    target != null -> "TARGET UNREACHABLE: $target was never discovered. Visited ${visited.size} nodes: ${visited.joinToString(" → ")}."
                    else -> "DFS Complete! Visited order: ${visited.joinToString(" → ")}."
                },
                comparisonExpr = if (foundTarget) "PATH: ${goalPath.joinToString(" -> ")}" else "COMPLETE: ${visited.size} nodes visited",
                phaseLabel = "FOUND",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = baseNodes.map {
                    if (it.id == lastNode) it.copy(state = ElementState.FOUND)
                    else if (it.id in visited) it.copy(state = ElementState.VISITED)
                    else it
                },
                edges = baseEdges.map { e ->
                    if (goalEdgePairs.isNotEmpty()) {
                        if ((e.from to e.to) in goalEdgePairs || (e.to to e.from) in goalEdgePairs) e.copy(isHighlighted = true) else e
                    } else if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                },
                buffer = emptyList(),
                bufferLabel = "DFS CALL STACK",
                activeNodeId = lastNode,
                visitedNodeIds = visited.toSet(),
                activeCodeLines = listOf(5),
                variables = buildMap {
                    put("source", start)
                    put("visitedOrder", visited.joinToString("→"))
                    put("totalVisited", visited.size.toString())
                    if (target != null) put("target", target)
                    if (foundTarget) put("path", goalPath.joinToString("→"))
                },
                callStack = listOf("dfs(start=$start) -> complete")
            )
        )

        return steps
    }

    // ─────────────────────────────────────────────────────────────
    // 14. Dijkstra's Shortest Path Algorithm
    // ─────────────────────────────────────────────────────────────
    fun generateDijkstraSteps(
        startNodeId: String = "A",
        customGraph: Pair<List<GraphNodeState>, List<GraphEdgeState>>? = null,
        targetNodeId: String? = null
    ): List<VisualizerStep> {
        if (customGraph != null && customGraph.first.isEmpty()) {
            return listOf(
                VisualizerStep(
                    stepIndex = 0,
                    description = "Empty graph. Tap Add Node to plant a node.",
                    comparisonExpr = "EMPTY GRAPH",
                    phaseLabel = "STANDBY",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = emptyList(),
                    edges = emptyList(),
                    activeCodeLines = listOf(1)
                )
            )
        }
        val steps = mutableListOf<VisualizerStep>()
        var sIdx = 0
        val (rawNodes, rawEdges) = customGraph ?: canonicalWeightedGraph()
        val baseNodes = rawNodes.map { it.copy(state = ElementState.IDLE) }
        val baseEdges = rawEdges.map { it.copy(isHighlighted = false) }
        val validIds = baseNodes.map { it.id }.toSet()
        val start = if (startNodeId.uppercase() in validIds) startNodeId.uppercase() else (baseNodes.firstOrNull()?.id ?: "A")
        val target = targetNodeId?.uppercase()?.takeIf { it in validIds && it != start }

        val adj = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        baseNodes.forEach { adj[it.id] = mutableListOf() }
        baseEdges.forEach { e ->
            val w = e.weight ?: 1
            adj[e.from]?.add(e.to to w)
            if (!e.isDirected) {
                adj[e.to]?.add(e.from to w)
            }
        }
        adj.values.forEach { it.sortBy { n -> n.first } }

        val dist = mutableMapOf<String, Int>().apply {
            baseNodes.forEach { put(it.id, Int.MAX_VALUE) }
        }
        dist[start] = 0
        val prev = mutableMapOf<String, String>()
        val settled = mutableSetOf<String>()
        val treeEdges = mutableSetOf<Pair<String, String>>()

        // Min-Priority Queue ordered by distance, tie-breaking on node ID
        val pq = java.util.PriorityQueue<Pair<String, Int>>(
            compareBy<Pair<String, Int>> { it.second }.thenBy { it.first }
        )
        pq.add(start to 0)

        fun pqBufferItems(activeId: String? = null): List<BufferItem> {
            val list = pq.toList().sortedWith(compareBy<Pair<String, Int>> { it.second }.thenBy { it.first })
            return list.mapIndexed { idx, pair ->
                BufferItem(
                    id = "pq_${idx}_${pair.first}_${pair.second}",
                    value = "${pair.first}(d=${pair.second})",
                    state = if (pair.first == activeId) ElementState.ACTIVE else ElementState.IDLE,
                    nodeId = pair.first
                )
            }
        }

        fun isTreeEdge(e: GraphEdgeState): Boolean =
            (e.from to e.to) in treeEdges || (e.to to e.from) in treeEdges

        fun currentDistancesMap(): Map<String, String> = buildMap {
            dist.forEach { (nodeId, distance) ->
                put("dist[$nodeId]", if (distance == Int.MAX_VALUE) "∞" else distance.toString())
            }
        }

        fun mapNodesWithDistances(
            activeId: String? = null,
            comparingId: String? = null,
            foundId: String? = null
        ): List<GraphNodeState> = baseNodes.map { node ->
            val currentD = dist[node.id]
            val distLabel = if (currentD == null || currentD == Int.MAX_VALUE) "∞" else currentD.toString()
            val state = when {
                node.id == foundId -> ElementState.FOUND
                node.id == activeId -> ElementState.ACTIVE
                node.id == comparingId -> ElementState.COMPARING
                node.id in settled -> ElementState.VISITED
                else -> ElementState.IDLE
            }
            node.copy(state = state, value = "d=$distLabel")
        }

        // 1. Initial Step: INITIALIZING
        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = "Dijkstra: Initialized dist[$start]=0, all other nodes dist=∞. Added ($start, d=0) to Priority Queue.",
                comparisonExpr = "INITIALIZE: dist[$start] = 0",
                phaseLabel = "INITIALIZING",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = mapNodesWithDistances(activeId = start),
                edges = baseEdges,
                buffer = pqBufferItems(start),
                bufferLabel = "PRIORITY QUEUE",
                activeNodeId = start,
                visitedNodeIds = emptySet(),
                activeCodeLines = listOf(1, 2, 3),
                variables = buildMap {
                    put("source", start)
                    put("dist[$start]", "0")
                    if (target != null) put("target", target)
                    putAll(currentDistancesMap())
                },
                callStack = listOf("dijkstra(start=$start)")
            )
        )

        var foundTarget = false

        while (pq.isNotEmpty() && !foundTarget) {
            val (u, d) = pq.poll() ?: break

            // Skip obsolete priority queue entry if we already found a shorter path
            if (d > (dist[u] ?: Int.MAX_VALUE)) {
                continue
            }
            if (u in settled) {
                continue
            }

            settled.add(u)
            if (prev.containsKey(u)) {
                treeEdges.add(prev.getValue(u) to u)
            }

            // Step: EXTRACT_MIN / SETTLED
            val isGoal = (u == target)
            if (isGoal) {
                foundTarget = true
            }

            steps.add(
                VisualizerStep(
                    stepIndex = sIdx++,
                    description = if (isGoal) {
                        "Target $target extracted from Priority Queue with confirmed shortest distance $d."
                    } else {
                        "Settled node $u with optimal distance dist[$u]=$d. Expanding adjacent edges."
                    },
                    comparisonExpr = "EXTRACT-MIN: $u (dist = $d)",
                    phaseLabel = if (isGoal) "FOUND" else "SETTLED",
                    renderMode = VisualizerRenderMode.GRAPH_TREE,
                    nodes = mapNodesWithDistances(
                        activeId = if (isGoal) null else u,
                        foundId = if (isGoal) u else null
                    ),
                    edges = baseEdges.map { e ->
                        if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                    },
                    buffer = pqBufferItems(),
                    bufferLabel = "PRIORITY QUEUE",
                    activeNodeId = u,
                    visitedNodeIds = settled.toSet(),
                    activeCodeLines = listOf(5),
                    variables = buildMap {
                        put("settledNode", u)
                        put("dist[$u]", d.toString())
                        put("settledCount", settled.size.toString())
                        put("pqSize", pq.size.toString())
                        putAll(currentDistancesMap())
                    },
                    callStack = listOf("settled: $u (cost=$d)")
                )
            )

            if (isGoal) {
                break
            }

            // Inspect and relax outgoing edges
            for ((v, weight) in adj[u].orEmpty()) {
                val currentDistV = dist[v] ?: Int.MAX_VALUE
                val candidateDist = d + weight

                if (v in settled) {
                    continue
                }

                // Step: RELAXING / COMPARING
                steps.add(
                    VisualizerStep(
                        stepIndex = sIdx++,
                        description = "Inspecting edge ($u → $v, weight $weight): candidate dist = $d + $weight = $candidateDist vs current dist[$v] = ${if (currentDistV == Int.MAX_VALUE) "∞" else currentDistV.toString()}.",
                        comparisonExpr = "RELAX: $d + $weight ${if (candidateDist < currentDistV) "<" else "≥"} ${if (currentDistV == Int.MAX_VALUE) "∞" else currentDistV}",
                        phaseLabel = "RELAXING",
                        renderMode = VisualizerRenderMode.GRAPH_TREE,
                        nodes = mapNodesWithDistances(activeId = u, comparingId = v),
                        edges = baseEdges.map { e ->
                            val isCurrentEdge = (e.from == u && e.to == v) || (e.to == u && e.from == v)
                            if (isCurrentEdge || isTreeEdge(e)) e.copy(isHighlighted = true) else e
                        },
                        buffer = pqBufferItems(),
                        bufferLabel = "PRIORITY QUEUE",
                        activeNodeId = v,
                        visitedNodeIds = settled.toSet(),
                        activeCodeLines = listOf(8, 9),
                        variables = buildMap {
                            put("u", u)
                            put("v", v)
                            put("edgeWeight", weight.toString())
                            put("newDist", candidateDist.toString())
                            put("oldDist", if (currentDistV == Int.MAX_VALUE) "∞" else currentDistV.toString())
                            putAll(currentDistancesMap())
                        },
                        callStack = listOf("relax(edge=$u->$v, w=$weight)")
                    )
                )

                if (candidateDist < currentDistV) {
                    dist[v] = candidateDist
                    prev[v] = u
                    pq.add(v to candidateDist)

                    // Step: UPDATING
                    steps.add(
                        VisualizerStep(
                            stepIndex = sIdx++,
                            description = "Shorter path to $v found via $u! Updated dist[$v] = $candidateDist and enqueued ($v, d=$candidateDist).",
                            comparisonExpr = "UPDATE: dist[$v] = $candidateDist",
                            phaseLabel = "UPDATING",
                            renderMode = VisualizerRenderMode.GRAPH_TREE,
                            nodes = mapNodesWithDistances(activeId = v),
                            edges = baseEdges.map { e ->
                                val isCurrentEdge = (e.from == u && e.to == v) || (e.to == u && e.from == v)
                                if (isCurrentEdge || isTreeEdge(e)) e.copy(isHighlighted = true) else e
                            },
                            buffer = pqBufferItems(v),
                            bufferLabel = "PRIORITY QUEUE",
                            activeNodeId = v,
                            visitedNodeIds = settled.toSet(),
                            activeCodeLines = listOf(10, 11),
                            variables = buildMap {
                                put("u", u)
                                put("v", v)
                                put("relaxedNode", v)
                                put("dist[$v]", candidateDist.toString())
                                put("parent[$v]", u)
                                putAll(currentDistancesMap())
                            },
                            callStack = listOf("dist[$v] := $candidateDist")
                        )
                    )
                }
            }
        }

        // Reconstruct path
        val goalPath = if (foundTarget && target != null) {
            val p = mutableListOf(target)
            var cur: String = target
            while (prev.containsKey(cur)) {
                cur = prev.getValue(cur)
                p.add(0, cur)
            }
            p
        } else emptyList()
        val goalEdgePairs: Set<Pair<String, String>> = goalPath.zipWithNext().map { it.first to it.second }.toSet()

        val lastNode = if (foundTarget) target ?: start else (settled.lastOrNull() ?: start)
        val finalCost = if (foundTarget && target != null) dist[target] ?: 0 else 0

        steps.add(
            VisualizerStep(
                stepIndex = sIdx++,
                description = when {
                    foundTarget -> "TARGET REACHED: Shortest path is ${goalPath.joinToString(" → ")} (total cost: $finalCost) after settling ${settled.size} nodes."
                    target != null -> "TARGET UNREACHABLE: Target node $target is not reachable from source $start. Settled ${settled.size} nodes: ${settled.joinToString(" → ")}."
                    else -> "Dijkstra Complete! Settled all reachable nodes from $start: ${settled.joinToString(" → ")}."
                },
                comparisonExpr = if (foundTarget) "SHORTEST PATH: ${goalPath.joinToString(" → ")} (cost = $finalCost)" else "COMPLETE: ${settled.size} settled",
                phaseLabel = if (foundTarget) "FOUND" else if (target != null) "NOT_FOUND" else "SORTED",
                renderMode = VisualizerRenderMode.GRAPH_TREE,
                nodes = baseNodes.map { node ->
                    val state = when {
                        node.id == lastNode -> if (foundTarget) ElementState.FOUND else ElementState.VISITED
                        node.id in goalPath -> ElementState.FOUND
                        node.id in settled -> ElementState.VISITED
                        else -> node.state
                    }
                    val currentD = dist[node.id]
                    val distLabel = if (currentD == null || currentD == Int.MAX_VALUE) "∞" else currentD.toString()
                    node.copy(state = state, value = "d=$distLabel")
                },
                edges = baseEdges.map { e ->
                    if (goalEdgePairs.isNotEmpty()) {
                        if ((e.from to e.to) in goalEdgePairs || (e.to to e.from) in goalEdgePairs) e.copy(isHighlighted = true) else e
                    } else if (isTreeEdge(e)) e.copy(isHighlighted = true) else e
                },
                buffer = emptyList(),
                bufferLabel = "PRIORITY QUEUE",
                activeNodeId = lastNode,
                visitedNodeIds = settled.toSet(),
                activeCodeLines = listOf(11),
                variables = buildMap {
                    put("source", start)
                    put("settledCount", settled.size.toString())
                    if (target != null) put("target", target)
                    if (foundTarget) {
                        put("path", goalPath.joinToString(" → "))
                        put("cost", finalCost.toString())
                    }
                    putAll(currentDistancesMap())
                },
                callStack = listOf("dijkstra(start=$start) -> complete")
            )
        )

        return steps
    }
}

/**
 * Shared pure graph-search engine used by the BFS/DFS step generators, the
 * node-graph editor's live path preview, and unit tests.
 *
 * When [target] is `null` the searches run exhaustive traversals (visiting
 * everything reachable from the source — the classic textbook behaviour).
 * When a [target] is given the search is goal-directed: BFS stops the moment
 * the target is *discovered* (fewest-hops guarantee) and DFS stops when its
 * walk *reaches* the target (a valid path, not necessarily the shortest).
 */
object GraphSearch {

    /** Immutability marker: order and parents are owned by the receiver. */
    class Result(
        val order: List<String>,
        val parents: Map<String, String>,
        val targetFound: Boolean,
        val target: String?
    ) {
        /** Reconstructs source→target hop list from the parent map, or empty. */
        fun pathToTarget(): List<String> {
            if (!targetFound || target == null) return emptyList()
            val path = mutableListOf(target)
            var cur: String = target
            while (parents.containsKey(cur)) {
                cur = parents.getValue(cur)
                path.add(0, cur)
            }
            return path
        }

        /** Parent-map edges of the source→target route, order-independent pairs. */
        fun pathEdgePairs(): Set<Pair<String, String>> {
            val p = pathToTarget()
            return if (p.size < 2) emptySet()
            else p.zipWithNext().map { it.first to it.second }.toSet()
        }
    }

    /** Adjacency with edge weights; undirected edges expand both ways. */
    fun buildAdjacency(edges: List<GraphEdgeState>): Map<String, MutableList<Pair<String, Int>>> {
        val adj = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        edges.forEach { e ->
            val w = e.weight ?: 1
            adj.getOrPut(e.from) { mutableListOf() }.add(e.to to w)
            if (!e.isDirected) {
                adj.getOrPut(e.to) { mutableListOf() }.add(e.from to w)
            }
        }
        adj.values.forEach { it.sortBy { n -> n.first } }
        return adj
    }

    fun bfs(adj: Map<String, List<Pair<String, Int>>>, start: String, target: String? = null): Result {
        val order = mutableListOf(start)
        val parents = mutableMapOf<String, String>()
        val visited = mutableSetOf(start)
        val queue = ArrayDeque<String>()
        queue.addLast(start)
        var found = target != null && start == target
        while (queue.isNotEmpty() && !found) {
            val curr = queue.removeFirst()
            for ((v, _) in adj[curr].orEmpty()) {
                if (v !in visited) {
                    visited.add(v)
                    parents[v] = curr
                    order.add(v)
                    if (v == target) {
                        found = true
                        break
                    }
                    queue.addLast(v)
                }
            }
        }
        return Result(order, parents, found, target)
    }

    /**
     * Iterative DFS mirroring the recursive generator's visit order (neighbors
     * in label order, deepest-first). Stops early when [target] is reached.
     */
    fun dfs(adj: Map<String, List<Pair<String, Int>>>, start: String, target: String? = null): Result {
        val order = mutableListOf<String>()
        val parents = mutableMapOf<String, String>()
        val visited = mutableSetOf<String>()
        val stack = ArrayDeque<String>()
        stack.addLast(start)
        var found = false
        while (stack.isNotEmpty() && !found) {
            val u = stack.removeLast()
            if (u in visited) continue
            visited.add(u)
            order.add(u)
            if (u == target) {
                found = true
                break
            }
            for ((v, _) in adj[u].orEmpty().asReversed()) {
                if (v !in visited) {
                    parents.putIfAbsent(v, u)
                    stack.addLast(v)
                }
            }
        }
        return Result(order, parents, found, target)
    }

    /** Shortest path (fewest hops) between two nodes via BFS, or empty list. */
    fun shortestPath(adj: Map<String, List<Pair<String, Int>>>, start: String, end: String): List<String> =
        bfs(adj, start, end).pathToTarget()

    /** Least-cost shortest path between two nodes using Dijkstra's algorithm, with exact path cost. */
    fun dijkstraShortestPath(
        adj: Map<String, List<Pair<String, Int>>>,
        start: String,
        end: String
    ): Pair<List<String>, Int> {
        if (start == end) return listOf(start) to 0
        val dist = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        val prev = mutableMapOf<String, String>()
        val pq = java.util.PriorityQueue<Pair<String, Int>>(
            compareBy<Pair<String, Int>> { it.second }.thenBy { it.first }
        )
        dist[start] = 0
        pq.add(start to 0)
        val settled = mutableSetOf<String>()

        while (pq.isNotEmpty()) {
            val (u, d) = pq.poll() ?: break
            if (d > dist.getValue(u)) continue
            if (u in settled) continue
            settled.add(u)
            if (u == end) break

            for ((v, w) in adj[u].orEmpty()) {
                val weight = if (w > 0) w else 1
                val newDist = d + weight
                if (newDist < dist.getValue(v)) {
                    dist[v] = newDist
                    prev[v] = u
                    pq.add(v to newDist)
                }
            }
        }

        if (dist.getValue(end) == Int.MAX_VALUE) return emptyList<String>() to -1
        val path = mutableListOf<String>()
        var cur: String? = end
        while (cur != null) {
            path.add(0, cur)
            cur = prev[cur]
        }
        return path to dist.getValue(end)
    }
}
