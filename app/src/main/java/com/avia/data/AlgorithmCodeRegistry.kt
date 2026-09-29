package com.avia.data

import com.avia.model.AlgorithmId
import com.avia.model.BstMode
import com.avia.model.QueueVariant

/**
 * Supported programming languages for the code trace inspector.
 * Lives in `data/` because it is part of the multi-language data model and
 * is consumed by [AlgorithmCodeRegistry] (pure data, no Compose deps).
 */
enum class TraceLanguage(val label: String, val extension: String) {
    KOTLIN("Kotlin", ".kt"),
    JAVA("Java", ".java"),
    PYTHON("Python", ".py"),
    CPP("C++", ".cpp")
}

/**
 * Multi-Language Code Snippets and Line Mappings Repository.
 *
 * Pure data — no Compose, no Android dependencies. One [MultiLangCode] per
 * (algorithm, language) pair. The `lineMapping` keys are 1-indexed Python
 * line numbers, values are 1-indexed lines in the target language. This is
 * the contract `step.activeCodeLines` is keyed against.
 */
object AlgorithmCodeRegistry {

    data class MultiLangCode(
        val lines: List<String>,
        // Mapping from Python 1-indexed line number to this language's 1-indexed lines
        val lineMapping: Map<Int, List<Int>>
    )

    fun getCode(
        id: AlgorithmId,
        language: TraceLanguage,
        bstMode: BstMode = BstMode.SEARCH,
        queueVariant: QueueVariant = QueueVariant.LINEAR_FIFO
    ): MultiLangCode {
        return when (id) {
            AlgorithmId.BUBBLE_SORT -> getBubbleSortCode(language)
            AlgorithmId.QUICK_SORT -> getQuickSortCode(language)
            AlgorithmId.INSERTION_SORT -> getInsertionSortCode(language)
            AlgorithmId.SELECTION_SORT -> getSelectionSortCode(language)
            AlgorithmId.MERGE_SORT -> getMergeSortCode(language)
            AlgorithmId.BINARY_SEARCH -> getBinarySearchCode(language)
            AlgorithmId.LINEAR_SEARCH -> getLinearSearchCode(language)
            AlgorithmId.STACK -> getStackCode(language)
            AlgorithmId.QUEUE -> {
                if (queueVariant == QueueVariant.CIRCULAR_RING) {
                    getCircularQueueCode(language)
                } else {
                    getQueueCode(language)
                }
            }
            AlgorithmId.BINARY_SEARCH_TREE -> {
                when (bstMode) {
                    BstMode.SEARCH -> getBstCode(language)
                    BstMode.IN_ORDER -> getBstInOrderCode(language)
                    BstMode.PRE_ORDER -> getBstPreOrderCode(language)
                    BstMode.POST_ORDER -> getBstPostOrderCode(language)
                }
            }
            AlgorithmId.HEAP -> getHeapCode(language)
            AlgorithmId.BFS -> getBfsCode(language)
            AlgorithmId.DFS -> getDfsCode(language)
            AlgorithmId.DIJKSTRA -> getDijkstraCode(language)
        }
    }

    fun getCode(
        algorithmName: String,
        language: TraceLanguage,
        bstMode: BstMode = BstMode.SEARCH,
        queueVariant: QueueVariant = QueueVariant.LINEAR_FIFO
    ): MultiLangCode {
        val normalized = algorithmName.lowercase().trim()
        val id = AlgorithmId.fromDisplayName(normalized) ?: when (normalized) {
            "bst", "binary search tree (bst)" -> AlgorithmId.BINARY_SEARCH_TREE
            "bfs", "breadth-first search" -> AlgorithmId.BFS
            "dfs", "depth-first search" -> AlgorithmId.DFS
            "dijkstra", "dijkstra's shortest path", "dijkstra shortest path" -> AlgorithmId.DIJKSTRA
            "max-heap", "min-heap", "binary heap" -> AlgorithmId.HEAP
            else -> AlgorithmId.entries.firstOrNull { it.name.equals(normalized, ignoreCase = true) }
        } ?: throw IllegalArgumentException("Unsupported algorithm for source trace: '$algorithmName'")
        return getCode(id, language, bstMode, queueVariant)
    }

    private fun getBubbleSortCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun bubbleSort(arr: IntArray) {",
                    "    val n = arr.size",
                    "    for (i in 0 until n - 1) {",
                    "        for (j in 0 until n - i - 1) {",
                    "            if (arr[j] > arr[j + 1]) {",
                    "                val temp = arr[j]",
                    "                arr[j] = arr[j + 1]",
                    "                arr[j + 1] = temp",
                    "            }",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6, 7, 8),
                    7 to listOf(11, 12)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public void bubbleSort(int[] arr) {",
                    "    int n = arr.length;",
                    "    for (int i = 0; i < n - 1; i++) {",
                    "        for (int j = 0; j < n - i - 1; j++) {",
                    "            if (arr[j] > arr[j + 1]) {",
                    "                int temp = arr[j];",
                    "                arr[j] = arr[j + 1];",
                    "                arr[j + 1] = temp;",
                    "            }",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6, 7, 8),
                    7 to listOf(11, 12)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def bubble_sort(arr):",
                    "    n = len(arr)",
                    "    for i in range(n - 1):",
                    "        for j in range(n - i - 1):",
                    "            if arr[j] > arr[j + 1]:",
                    "                arr[j], arr[j+1] = arr[j+1], arr[j]",
                    "    return arr"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7)
                )
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void bubbleSort(vector<int>& arr) {",
                    "    int n = arr.size();",
                    "    for (int i = 0; i < n - 1; i++) {",
                    "        for (int j = 0; j < n - i - 1; j++) {",
                    "            if (arr[j] > arr[j + 1]) {",
                    "                std::swap(arr[j], arr[j + 1]);",
                    "            }",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(9, 10)
                )
            )
        }
    }

    private fun getQuickSortCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun quickSort(arr: IntArray, low: Int, high: Int) {",
                    "    if (low < high) {",
                    "        val pi = partition(arr, low, high)",
                    "        quickSort(arr, low, pi - 1)",
                    "        quickSort(arr, pi + 1, high)",
                    "    }",
                    "}",
                    "fun partition(arr: IntArray, low: Int, high: Int): Int {",
                    "    val pivot = arr[high]",
                    "    var i = low - 1",
                    "    for (j in low until high) {",
                    "        if (arr[j] <= pivot) {",
                    "            i++",
                    "            arr.swap(i, j)",
                    "        }",
                    "    }",
                    "    arr.swap(i + 1, high)",
                    "    return i + 1",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    7 to listOf(8),
                    8 to listOf(9),
                    9 to listOf(10),
                    10 to listOf(11),
                    11 to listOf(12),
                    12 to listOf(13, 14),
                    13 to listOf(17, 18)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public void quickSort(int[] arr, int low, int high) {",
                    "    if (low < high) {",
                    "        int pi = partition(arr, low, high);",
                    "        quickSort(arr, low, pi - 1);",
                    "        quickSort(arr, pi + 1, high);",
                    "    }",
                    "}",
                    "private int partition(int[] arr, int low, int high) {",
                    "    int pivot = arr[high];",
                    "    int i = low - 1;",
                    "    for (int j = low; j < high; j++) {",
                    "        if (arr[j] <= pivot) {",
                    "            i++;",
                    "            int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;",
                    "        }",
                    "    }",
                    "    int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;",
                    "    return i + 1;",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    7 to listOf(8),
                    8 to listOf(9),
                    9 to listOf(10),
                    10 to listOf(11),
                    11 to listOf(12),
                    12 to listOf(13, 14),
                    13 to listOf(17, 18)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
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
                    "            a[i], a[j] = a[j], a[i]",
                    "    a[i + 1], a[high] = a[high], a[i + 1]",
                    "    return i + 1"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    7 to listOf(7),
                    8 to listOf(8),
                    9 to listOf(9),
                    10 to listOf(10),
                    11 to listOf(11),
                    12 to listOf(12, 13),
                    13 to listOf(14, 15)
                )
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void quickSort(vector<int>& arr, int low, int high) {",
                    "    if (low < high) {",
                    "        int pi = partition(arr, low, high);",
                    "        quickSort(arr, low, pi - 1);",
                    "        quickSort(arr, pi + 1, high);",
                    "    }",
                    "}",
                    "int partition(vector<int>& arr, int low, int high) {",
                    "    int pivot = arr[high];",
                    "    int i = low - 1;",
                    "    for (int j = low; j < high; j++) {",
                    "        if (arr[j] <= pivot) {",
                    "            i++;",
                    "            swap(arr[i], arr[j]);",
                    "        }",
                    "    }",
                    "    swap(arr[i + 1], arr[high]);",
                    "    return i + 1;",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    7 to listOf(8),
                    8 to listOf(9),
                    9 to listOf(10),
                    10 to listOf(11),
                    11 to listOf(12),
                    12 to listOf(13, 14),
                    13 to listOf(17, 18)
                )
            )
        }
    }

    private fun getBinarySearchCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun binarySearch(arr: IntArray, target: Int): Int {",
                    "    var low = 0",
                    "    var high = arr.size - 1",
                    "    while (low <= high) {",
                    "        val mid = (low + high) / 2",
                    "        if (arr[mid] == target) return mid",
                    "        else if (arr[mid] < target) low = mid + 1",
                    "        else high = mid - 1",
                    "    }",
                    "    return -1",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3),
                    3 to listOf(4),
                    4 to listOf(5),
                    5 to listOf(6),
                    7 to listOf(7),
                    9 to listOf(8),
                    11 to listOf(10)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public int binarySearch(int[] arr, int target) {",
                    "    int low = 0, high = arr.length - 1;",
                    "    while (low <= high) {",
                    "        int mid = (low + high) / 2;",
                    "        if (arr[mid] == target) return mid;",
                    "        else if (arr[mid] < target) low = mid + 1;",
                    "        else high = mid - 1;",
                    "    }",
                    "    return -1;",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    7 to listOf(6),
                    9 to listOf(7),
                    11 to listOf(9)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
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
                ),
                lineMapping = (1..11).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "int binarySearch(const vector<int>& arr, int target) {",
                    "    int low = 0, high = arr.size() - 1;",
                    "    while (low <= high) {",
                    "        int mid = (low + high) / 2;",
                    "        if (arr[mid] == target) return mid;",
                    "        else if (arr[mid] < target) low = mid + 1;",
                    "        else high = mid - 1;",
                    "    }",
                    "    return -1;",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    7 to listOf(6),
                    9 to listOf(7),
                    11 to listOf(9)
                )
            )
        }
    }

    private fun getInsertionSortCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun insertionSort(arr: IntArray) {",
                    "    for (i in 1 until arr.size) {",
                    "        val key = arr[i]",
                    "        var j = i - 1",
                    "        while (j >= 0 && arr[j] > key) {",
                    "            arr[j + 1] = arr[j]",
                    "            j--",
                    "        }",
                    "        arr[j + 1] = key",
                    "    }",
                    "}"
                ),
                lineMapping = (1..10).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public void insertionSort(int[] arr) {",
                    "    for (int i = 1; i < arr.length; i++) {",
                    "        int key = arr[i];",
                    "        int j = i - 1;",
                    "        while (j >= 0 && arr[j] > key) {",
                    "            arr[j + 1] = arr[j];",
                    "            j--;",
                    "        }",
                    "        arr[j + 1] = key;",
                    "    }",
                    "}"
                ),
                lineMapping = (1..10).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def insertion_sort(arr):",
                    "    for i in range(1, len(arr)):",
                    "        key = arr[i]",
                    "        j = i - 1",
                    "        while j >= 0 and arr[j] > key:",
                    "            arr[j + 1] = arr[j]",
                    "            j -= 1",
                    "        arr[j + 1] = key"
                ),
                lineMapping = (1..8).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void insertionSort(vector<int>& arr) {",
                    "    for (size_t i = 1; i < arr.size(); i++) {",
                    "        int key = arr[i];",
                    "        int j = i - 1;",
                    "        while (j >= 0 && arr[j] > key) {",
                    "            arr[j + 1] = arr[j];",
                    "            j--;",
                    "        }",
                    "        arr[j + 1] = key;",
                    "    }",
                    "}"
                ),
                lineMapping = (1..10).associateWith { listOf(it) }
            )
        }
    }

    private fun getSelectionSortCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun selectionSort(arr: IntArray) {",
                    "    val n = arr.size",
                    "    for (i in 0 until n - 1) {",
                    "        var minIdx = i",
                    "        for (j in i + 1 until n) {",
                    "            if (arr[j] < arr[minIdx]) minIdx = j",
                    "        }",
                    "        arr.swap(i, minIdx)",
                    "    }",
                    "}"
                ),
                lineMapping = (1..9).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public void selectionSort(int[] arr) {",
                    "    int n = arr.length;",
                    "    for (int i = 0; i < n - 1; i++) {",
                    "        int minIdx = i;",
                    "        for (int j = i + 1; j < n; j++) {",
                    "            if (arr[j] < arr[minIdx]) minIdx = j;",
                    "        }",
                    "        int temp = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = temp;",
                    "    }",
                    "}"
                ),
                lineMapping = (1..9).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def selection_sort(arr):",
                    "    n = len(arr)",
                    "    for i in range(n - 1):",
                    "        min_idx = i",
                    "        for j in range(i + 1, n):",
                    "            if arr[j] < arr[min_idx]:",
                    "                min_idx = j",
                    "        arr[i], arr[min_idx] = arr[min_idx], arr[i]"
                ),
                lineMapping = (1..8).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void selectionSort(vector<int>& arr) {",
                    "    int n = arr.size();",
                    "    for (int i = 0; i < n - 1; i++) {",
                    "        int minIdx = i;",
                    "        for (int j = i + 1; j < n; j++) {",
                    "            if (arr[j] < arr[minIdx]) minIdx = j;",
                    "        }",
                    "        swap(arr[i], arr[minIdx]);",
                    "    }",
                    "}"
                ),
                lineMapping = (1..9).associateWith { listOf(it) }
            )
        }
    }

    private fun getMergeSortCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun mergeSort(arr: IntArray, l: Int, r: Int) {",
                    "    if (l < r) {",
                    "        val m = (l + r) / 2",
                    "        mergeSort(arr, l, m)",
                    "        mergeSort(arr, m + 1, r)",
                    "        merge(arr, l, m, r)",
                    "    }",
                    "}"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public void mergeSort(int[] arr, int l, int r) {",
                    "    if (l < r) {",
                    "        int m = (l + r) / 2;",
                    "        mergeSort(arr, l, m);",
                    "        mergeSort(arr, m + 1, r);",
                    "        merge(arr, l, m, r);",
                    "    }",
                    "}"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def merge_sort(arr, l, r):",
                    "    if l < r:",
                    "        m = (l + r) // 2",
                    "        merge_sort(arr, l, m)",
                    "        merge_sort(arr, m + 1, r)",
                    "        merge(arr, l, m, r)"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void mergeSort(vector<int>& arr, int l, int r) {",
                    "    if (l < r) {",
                    "        int m = (l + r) / 2;",
                    "        mergeSort(arr, l, m);",
                    "        mergeSort(arr, m + 1, r);",
                    "        merge(arr, l, m, r);",
                    "    }",
                    "}"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
        }
    }

    private fun getLinearSearchCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun linearSearch(arr: IntArray, target: Int): Int {",
                    "    for (i in arr.indices) {",
                    "        if (arr[i] == target) return i",
                    "    }",
                    "    return -1",
                    "}"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public int linearSearch(int[] arr, int target) {",
                    "    for (int i = 0; i < arr.length; i++) {",
                    "            if (arr[i] == target) return i;",
                    "    }",
                    "    return -1;",
                    "}"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def linear_search(arr, target):",
                    "    for i in range(len(arr)):",
                    "        if arr[i] == target:",
                    "            return i",
                    "    return -1"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "int linearSearch(const vector<int>& arr, int target) {",
                    "    for (size_t i = 0; i < arr.size(); i++) {",
                    "        if (arr[i] == target) return i;",
                    "    }",
                    "    return -1;",
                    "}"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
        }
    }

    private fun getStackCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "class LifoStack<T>(private val cap: Int = 8) {",
                    "    private val items = ArrayDeque<T>()",
                    "    fun push(value: T) { if (items.size < cap) items.addLast(value) }",
                    "    fun pop(): T? {",
                    "        if (items.isEmpty()) return null",
                    "        return items.removeLast()",
                    "    }",
                    "    fun peek(): T? = items.lastOrNull()",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(3),
                    3 to listOf(4, 5),
                    4 to listOf(5, 6),
                    5 to listOf(6),
                    6 to listOf(8)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public class LifoStack<T> {",
                    "    private final Deque<T> items = new ArrayDeque<>();",
                    "    public void push(T value) { items.addLast(value); }",
                    "    public T pop() {",
                    "        if (items.isEmpty()) return null;",
                    "        return items.removeLast();",
                    "    }",
                    "    public T peek() { return items.peekLast(); }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(3),
                    3 to listOf(4, 5),
                    4 to listOf(5, 6),
                    5 to listOf(6),
                    6 to listOf(8)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "class LifoStack:",
                    "    def push(self, value): self.items.append(value)",
                    "    def pop(self):",
                    "        return self.items.pop() if self.items else None",
                    "    def is_empty(self): return len(self.items) == 0",
                    "    def peek(self): return self.items[-1] if self.items else None"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "template <typename T> class LifoStack {",
                    "    vector<T> items;",
                    "public:",
                    "    void push(T value) { items.push_back(value); }",
                    "    T pop() {",
                    "        if (items.empty()) return T{};",
                    "        T top = items.back(); items.pop_back(); return top;",
                    "    }",
                    "    T peek() const { return items.empty() ? T{} : items.back(); }",
                    "};"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(4),
                    3 to listOf(5, 6),
                    4 to listOf(6, 7),
                    5 to listOf(7),
                    6 to listOf(9)
                )
            )
        }
    }

    private fun getQueueCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "class FifoQueue<T>(private val cap: Int = 8) {",
                    "    private val items = ArrayDeque<T>()",
                    "    fun enqueue(value: T) { if (items.size < cap) items.addLast(value) }",
                    "    fun dequeue(): T? {",
                    "        if (items.isEmpty()) return null",
                    "        return items.removeFirst()",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(3),
                    3 to listOf(4, 5),
                    4 to listOf(5, 6)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public class FifoQueue<T> {",
                    "    private final Deque<T> items = new ArrayDeque<>();",
                    "    public void enqueue(T value) { items.addLast(value); }",
                    "    public T dequeue() {",
                    "        if (items.isEmpty()) return null;",
                    "        return items.removeFirst();",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(3),
                    3 to listOf(4, 5),
                    4 to listOf(5, 6)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "class FifoQueue:",
                    "    def enqueue(self, value): self.items.append(value)",
                    "    def dequeue(self):",
                    "        return self.items.popleft() if self.items else None"
                ),
                lineMapping = (1..4).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "template <typename T> class FifoQueue {",
                    "    deque<T> items;",
                    "public:",
                    "    void enqueue(T value) { items.push_back(value); }",
                    "    T dequeue() {",
                    "        if (items.empty()) return T{};",
                    "        T front = items.front(); items.pop_front(); return front;",
                    "    }",
                    "};"
                ),
                lineMapping = mapOf(
                    1 to listOf(1, 2),
                    2 to listOf(4),
                    3 to listOf(5, 6),
                    4 to listOf(6, 7)
                )
            )
        }
    }

    private fun getBstCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun insertBst(root: TreeNode?, v: Int): TreeNode =",
                    "    if (root == null) TreeNode(v) else if (v < root.key) root.apply { left = insertBst(left, v) } else root.apply { right = insertBst(right, v) }",
                    "fun searchBst(node: TreeNode?, key: Int): TreeNode? {",
                    "    if (node == null) return null",
                    "    if (node.key == key) return node",
                    "    return if (key < node.key) searchBst(node.left, key)",
                    "    else searchBst(node.right, key)",
                    "}"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "TreeNode insertBst(TreeNode root, int v) {",
                    "    if (root == null) return new TreeNode(v); if (v < root.key) root.left = insertBst(root.left, v); else root.right = insertBst(root.right, v); return root; }",
                    "TreeNode searchBst(TreeNode node, int key) {",
                    "    if (node == null) return null;",
                    "    if (node.key == key) return node;",
                    "    return (key < node.key) ? searchBst(node.left, key)",
                    "                            : searchBst(node.right, key);",
                    "}"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def insert_bst(root, v):",
                    "    if not root: return Node(v)",
                    "def search_bst(node, key):",
                    "    if node is None: return None",
                    "    if node.key == key: return node",
                    "    if key < node.key: return search_bst(node.left, key)",
                    "    return search_bst(node.right, key)"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "TreeNode* insertBst(TreeNode* root, int v) {",
                    "    if (!root) return new TreeNode(v); if (v < root->key) root->left = insertBst(root->left, v); else root->right = insertBst(root->right, v); return root; }",
                    "TreeNode* searchBst(TreeNode* node, int key) {",
                    "    if (!node) return nullptr;",
                    "    if (node->key == key) return node;",
                    "    return (key < node->key) ? searchBst(node->left, key)",
                    "                             : searchBst(node->right, key);",
                    "}"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
        }
    }

    private fun getCircularQueueCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "class CircularQueue(val cap: Int = 8) {",
                    "    fun enqueue(v: Int) {",
                    "        if ((rear + 1) % cap == front) return // full",
                    "        if (front == -1) front = 0",
                    "        rear = (rear + 1) % cap; arr[rear] = v",
                    "    }",
                    "    fun dequeue(): Int? {",
                    "        if (front == -1) return null // empty",
                    "        val v = arr[front]",
                    "        if (front == rear) { front = -1; rear = -1 }",
                    "        else front = (front + 1) % cap; return v",
                    "    }",
                    "    fun peek(): Int? = if (front != -1) arr[front] else null",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3, 4, 5),
                    3 to listOf(7, 8, 9),
                    4 to listOf(10, 11),
                    5 to listOf(13)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public class CircularQueue {",
                    "    public void enqueue(int v) {",
                    "        if ((rear + 1) % cap == front) return;",
                    "        if (front == -1) front = 0;",
                    "        rear = (rear + 1) % cap; arr[rear] = v;",
                    "    }",
                    "    public Integer dequeue() {",
                    "        if (front == -1) return null;",
                    "        int v = arr[front];",
                    "        if (front == rear) { front = -1; rear = -1; }",
                    "        else front = (front + 1) % cap; return v;",
                    "    }",
                    "    public Integer peek() { return front != -1 ? arr[front] : null; }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3, 4, 5),
                    3 to listOf(7, 8, 9),
                    4 to listOf(10, 11),
                    5 to listOf(13)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "class CircularQueue:",
                    "    def enqueue(self, v):",
                    "        if (self.rear + 1) % self.cap == self.front: return",
                    "        if self.front == -1: self.front = 0",
                    "        self.rear = (self.rear + 1) % self.cap; self.arr[self.rear] = v",
                    "    def dequeue(self):",
                    "        if self.front == -1: return None",
                    "        v = self.arr[self.front]",
                    "        if self.front == self.rear: self.front = -1; self.rear = -1",
                    "        else: self.front = (self.front + 1) % self.cap; return v",
                    "    def peek(self): return self.arr[self.front] if self.front != -1 else None"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3, 4, 5),
                    3 to listOf(6, 7, 8),
                    4 to listOf(9, 10),
                    5 to listOf(11)
                )
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "class CircularQueue {",
                    "    void enqueue(int v) {",
                    "        if ((rear + 1) % cap == front) return;",
                    "        if (front == -1) front = 0;",
                    "        rear = (rear + 1) % cap; arr[rear] = v;",
                    "    }",
                    "    int dequeue() {",
                    "        if (front == -1) return -1;",
                    "        int v = arr[front];",
                    "        if (front == rear) { front = -1; rear = -1; }",
                    "        else front = (front + 1) % cap; return v;",
                    "    }",
                    "    int peek() { return front != -1 ? arr[front] : -1; }",
                    "};"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3, 4, 5),
                    3 to listOf(7, 8, 9),
                    4 to listOf(10, 11),
                    5 to listOf(13)
                )
            )
        }
    }

    private fun getBstInOrderCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun inOrder(node: TreeNode?) {",
                    "    if (node == null) return",
                    "    inOrder(node.left)",
                    "    visit(node.value)",
                    "    inOrder(node.right)",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "void inOrder(TreeNode node) {",
                    "    if (node == null) return;",
                    "    inOrder(node.left);",
                    "    visit(node.value);",
                    "    inOrder(node.right);",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def in_order(node):",
                    "    if not node: return",
                    "    in_order(node.left)",
                    "    visit(node.value)",
                    "    in_order(node.right)"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void inOrder(TreeNode* node) {",
                    "    if (!node) return;",
                    "    inOrder(node->left);",
                    "    visit(node->value);",
                    "    inOrder(node->right);",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
        }
    }

    private fun getBstPreOrderCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun preOrder(node: TreeNode?) {",
                    "    if (node == null) return",
                    "    visit(node.value)",
                    "    preOrder(node.left)",
                    "    preOrder(node.right)",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "void preOrder(TreeNode node) {",
                    "    if (node == null) return;",
                    "    visit(node.value);",
                    "    preOrder(node.left);",
                    "    preOrder(node.right);",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def pre_order(node):",
                    "    if not node: return",
                    "    visit(node.value)",
                    "    pre_order(node.left)",
                    "    pre_order(node.right)"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void preOrder(TreeNode* node) {",
                    "    if (!node) return;",
                    "    visit(node->value);",
                    "    preOrder(node->left);",
                    "    preOrder(node->right);",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
        }
    }

    private fun getBstPostOrderCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun postOrder(node: TreeNode?) {",
                    "    if (node == null) return",
                    "    postOrder(node.left)",
                    "    postOrder(node.right)",
                    "    visit(node.value)",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "void postOrder(TreeNode node) {",
                    "    if (node == null) return;",
                    "    postOrder(node.left);",
                    "    postOrder(node.right);",
                    "    visit(node.value);",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def post_order(node):",
                    "    if not node: return",
                    "    post_order(node.left)",
                    "    post_order(node.right)",
                    "    visit(node.value)"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void postOrder(TreeNode* node) {",
                    "    if (!node) return;",
                    "    postOrder(node->left);",
                    "    postOrder(node->right);",
                    "    visit(node->value);",
                    "}"
                ),
                lineMapping = (1..6).associateWith { listOf(it) }
            )
        }
    }

    private fun getHeapCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun buildAndExtractHeap(arr: IntArray) {",
                    "    for (i in arr.size / 2 - 1 downTo 0) siftDown(arr, i, arr.size)",
                    "    // siftDown: compare parent with left(2i+1) & right(2i+2)",
                    "    val left = 2 * i + 1; val right = 2 * i + 2",
                    "    val target = selectDominantChild(arr, i, left, right, heapBound)",
                    "    if (target != i) {",
                    "        arr.swap(i, target); siftDown(arr, target, heapBound)",
                    "    }",
                    "    arr.swap(0, arr.lastIndex); siftDown(arr, 0, arr.size - 1)",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7),
                    8 to listOf(9)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "void buildAndExtractHeap(int[] arr) {",
                    "    for (int i = arr.length / 2 - 1; i >= 0; i--) siftDown(arr, i, arr.length);",
                    "    // siftDown: compare parent with left(2i+1) & right(2i+2)",
                    "    int left = 2 * i + 1, right = 2 * i + 2;",
                    "    int target = selectDominantChild(arr, i, left, right, heapBound);",
                    "    if (target != i) {",
                    "        swap(arr, i, target); siftDown(arr, target, heapBound);",
                    "    }",
                    "    swap(arr, 0, arr.length - 1); siftDown(arr, 0, arr.length - 1);",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7),
                    8 to listOf(9)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def build_and_extract_heap(arr):",
                    "    for i in range(len(arr) // 2 - 1, -1, -1): sift_down(arr, i, len(arr))",
                    "def sift_down(arr, i, heap_bound):",
                    "    left, right = 2 * i + 1, 2 * i + 2",
                    "    target = select_dominant_child(arr, i, left, right, heap_bound)",
                    "    if target != i:",
                    "        arr[i], arr[target] = arr[target], arr[i]; sift_down(arr, target, heap_bound)",
                    "def extract_root(arr): arr[0], arr[-1] = arr[-1], arr[0]; sift_down(arr, 0, len(arr) - 1)"
                ),
                lineMapping = (1..8).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void buildAndExtractHeap(vector<int>& arr) {",
                    "    for (int i = (int)arr.size() / 2 - 1; i >= 0; --i) siftDown(arr, i, arr.size());",
                    "    // siftDown: compare parent with left(2i+1) & right(2i+2)",
                    "    int left = 2 * i + 1, right = 2 * i + 2;",
                    "    int target = selectDominantChild(arr, i, left, right, heapBound);",
                    "    if (target != i) {",
                    "        swap(arr[i], arr[target]); siftDown(arr, target, heapBound);",
                    "    }",
                    "    swap(arr[0], arr.back()); siftDown(arr, 0, arr.size() - 1);",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7),
                    8 to listOf(9)
                )
            )
        }
    }

    private fun getBfsCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun bfs(adj: Map<String, List<Edge>>, start: String) {",
                    "    val visited = linkedSetOf(start); val queue = ArrayDeque(listOf(start))",
                    "    while (queue.isNotEmpty()) {",
                    "        val curr = queue.removeFirst()",
                    "        for ((next, w) in adj[curr].orEmpty()) {",
                    "            if (visited.add(next)) {",
                    "                queue.addLast(next)",
                    "            }",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "void bfs(Map<String, List<Edge>> adj, String start) {",
                    "    Set<String> visited = new LinkedHashSet<>(List.of(start)); Queue<String> queue = new ArrayDeque<>(List.of(start));",
                    "    while (!queue.isEmpty()) {",
                    "        String curr = queue.remove();",
                    "        for (Edge e : adj.getOrDefault(curr, List.of())) {",
                    "            if (visited.add(e.to)) {",
                    "                queue.add(e.to);",
                    "            }",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def bfs(adj, start):",
                    "    visited, queue = {start}, deque([start])",
                    "    while queue:",
                    "        curr = queue.popleft()",
                    "        for nxt, weight in adj.get(curr, []):",
                    "            if nxt not in visited:",
                    "                visited.add(nxt); queue.append(nxt)"
                ),
                lineMapping = (1..7).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void bfs(const map<string, vector<Edge>>& adj, const string& start) {",
                    "    set<string> visited{start}; deque<string> queue{start};",
                    "    while (!queue.empty()) {",
                    "        string curr = queue.front(); queue.pop_front();",
                    "        for (const auto& e : adj.at(curr)) {",
                    "            if (visited.insert(e.to).second) {",
                    "                queue.push_back(e.to);",
                    "            }",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5),
                    6 to listOf(6),
                    7 to listOf(7)
                )
            )
        }
    }

    private fun getDfsCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun dfs(u: String, adj: Map<String, List<Edge>>, visited: MutableSet<String>) {",
                    "    visited.add(u)",
                    "    for ((v, weight) in adj[u].orEmpty()) {",
                    "        if (v !in visited) {",
                    "            dfs(v, adj, visited)",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5, 7)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "void dfs(String u, Map<String, List<Edge>> adj, Set<String> visited) {",
                    "    visited.add(u);",
                    "    for (Edge e : adj.getOrDefault(u, List.of())) {",
                    "        if (!visited.contains(e.to)) {",
                    "            dfs(e.to, adj, visited);",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5, 7)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def dfs(u, adj, visited):",
                    "    visited.add(u)",
                    "    for v, weight in adj.get(u, []):",
                    "        if v not in visited:",
                    "            dfs(v, adj, visited)"
                ),
                lineMapping = (1..5).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "void dfs(const string& u, const map<string, vector<Edge>>& adj, set<string>& visited) {",
                    "    visited.insert(u);",
                    "    for (const auto& e : adj.at(u)) {",
                    "        if (!visited.count(e.to)) {",
                    "            dfs(e.to, adj, visited);",
                    "        }",
                    "    }",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3),
                    4 to listOf(4),
                    5 to listOf(5, 7)
                )
            )
        }
    }

    private fun getDijkstraCode(language: TraceLanguage): MultiLangCode {
        return when (language) {
            TraceLanguage.KOTLIN -> MultiLangCode(
                lines = listOf(
                    "fun dijkstra(graph: Map<String, List<Pair<String, Int>>>, start: String): Map<String, Int> {",
                    "    val dist = mutableMapOf(start to 0)",
                    "    val pq = PriorityQueue<Pair<String, Int>>(compareBy { it.second })",
                    "    pq.add(start to 0)",
                    "    while (pq.isNotEmpty()) {",
                    "        val (u, d) = pq.poll()",
                    "        if (d > (dist[u] ?: Int.MAX_VALUE)) continue",
                    "        for ((v, weight) in graph[u].orEmpty()) {",
                    "            if (d + weight < (dist[v] ?: Int.MAX_VALUE)) {",
                    "                dist[v] = d + weight",
                    "                pq.add(v to d + weight)",
                    "            }",
                    "        }",
                    "    }",
                    "    return dist",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2),
                    3 to listOf(3, 4),
                    4 to listOf(5),
                    5 to listOf(6),
                    6 to listOf(7),
                    7 to listOf(7),
                    8 to listOf(8),
                    9 to listOf(9),
                    10 to listOf(10),
                    11 to listOf(11)
                )
            )
            TraceLanguage.JAVA -> MultiLangCode(
                lines = listOf(
                    "public Map<String, Integer> dijkstra(Map<String, List<Edge>> graph, String start) {",
                    "    Map<String, Integer> dist = new HashMap<>();",
                    "    dist.put(start, 0);",
                    "    PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));",
                    "    pq.add(new NodeDist(start, 0));",
                    "    while (!pq.isEmpty()) {",
                    "        NodeDist curr = pq.poll();",
                    "        if (curr.dist > dist.getOrDefault(curr.node, Integer.MAX_VALUE)) continue;",
                    "        for (Edge e : graph.getOrDefault(curr.node, List.of())) {",
                    "            if (curr.dist + e.weight < dist.getOrDefault(e.to, Integer.MAX_VALUE)) {",
                    "                dist.put(e.to, curr.dist + e.weight);",
                    "                pq.add(new NodeDist(e.to, curr.dist + e.weight));",
                    "            }",
                    "        }",
                    "    }",
                    "    return dist;",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3),
                    3 to listOf(4, 5),
                    4 to listOf(6),
                    5 to listOf(7),
                    6 to listOf(8),
                    7 to listOf(8),
                    8 to listOf(9),
                    9 to listOf(10),
                    10 to listOf(11),
                    11 to listOf(12)
                )
            )
            TraceLanguage.PYTHON -> MultiLangCode(
                lines = listOf(
                    "def dijkstra(graph, start):",
                    "    dist = {start: 0}",
                    "    pq = [(0, start)]",
                    "    while pq:",
                    "        d, u = heapq.heappop(pq)",
                    "        if d > dist.get(u, float('inf')):",
                    "            continue",
                    "        for v, weight in graph.get(u, []):",
                    "            if d + weight < dist.get(v, float('inf')):",
                    "                dist[v] = d + weight",
                    "                heapq.heappush(pq, (dist[v], v))",
                    "    return dist"
                ),
                lineMapping = (1..11).associateWith { listOf(it) }
            )
            TraceLanguage.CPP -> MultiLangCode(
                lines = listOf(
                    "map<string, int> dijkstra(const map<string, vector<pair<string, int>>>& graph, const string& start) {",
                    "    map<string, int> dist;",
                    "    dist[start] = 0;",
                    "    priority_queue<pair<int, string>, vector<pair<int, string>>, greater<>> pq;",
                    "    pq.push({0, start});",
                    "    while (!pq.empty()) {",
                    "        auto [d, u] = pq.top(); pq.pop();",
                    "        if (dist.count(u) && d > dist[u]) continue;",
                    "        for (const auto& [v, weight] : graph.at(u)) {",
                    "            if (!dist.count(v) || d + weight < dist[v]) {",
                    "                dist[v] = d + weight;",
                    "                pq.push({d + weight, v});",
                    "            }",
                    "        }",
                    "    }",
                    "    return dist;",
                    "}"
                ),
                lineMapping = mapOf(
                    1 to listOf(1),
                    2 to listOf(2, 3),
                    3 to listOf(4, 5),
                    4 to listOf(6),
                    5 to listOf(7),
                    6 to listOf(8),
                    7 to listOf(8),
                    8 to listOf(9),
                    9 to listOf(10),
                    10 to listOf(11),
                    11 to listOf(12)
                )
            )
        }
    }
}
