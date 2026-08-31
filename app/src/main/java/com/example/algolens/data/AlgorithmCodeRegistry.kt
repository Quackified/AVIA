package com.example.algolens.data

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

    fun getCode(algorithmName: String, language: TraceLanguage): MultiLangCode {
        return when (algorithmName.lowercase().trim()) {
            "bubble sort" -> getBubbleSortCode(language)
            "quick sort" -> getQuickSortCode(language)
            "insertion sort" -> getInsertionSortCode(language)
            "selection sort" -> getSelectionSortCode(language)
            "merge sort" -> getMergeSortCode(language)
            "binary search" -> getBinarySearchCode(language)
            "linear search" -> getLinearSearchCode(language)
            else -> getBubbleSortCode(language)
        }
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
}
