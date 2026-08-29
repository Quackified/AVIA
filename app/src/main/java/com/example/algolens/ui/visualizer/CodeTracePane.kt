package com.example.algolens.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.smoothPanelExpansion
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanBright
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

/**
 * Supported programming languages for the code trace inspector.
 */
enum class TraceLanguage(val label: String, val extension: String) {
    KOTLIN("Kotlin", ".kt"),
    JAVA("Java", ".java"),
    PYTHON("Python", ".py"),
    CPP("C++", ".cpp")
}

/**
 * Syntax highlighting parser for code trace snippets.
 */
object SyntaxHighlighter {
    private val KEYWORDS = setOf(
        "fun", "val", "var", "def", "class", "public", "private", "protected",
        "void", "int", "return", "if", "else", "while", "for", "in", "range",
        "len", "vector", "swap", "new", "this", "self", "import", "package",
        "include", "template", "null", "None", "true", "false", "True", "False",
        "nullptr", "auto", "const", "size_t", "typedef", "struct"
    )

    private val TYPES = setOf(
        "Int", "IntArray", "List", "String", "Boolean", "Node", "Queue", "Stack",
        "Integer", "ArrayList", "int[]", "vector<int>", "set", "Set"
    )

    fun highlight(code: String, language: TraceLanguage): AnnotatedString {
        return buildAnnotatedString {
            var i = 0
            val n = code.length

            while (i < n) {
                // Comments
                if ((code.startsWith("//", i)) || (language == TraceLanguage.PYTHON && code[i] == '#')) {
                    val commentEnd = code.indexOf('\n', i).let { if (it == -1) n else it }
                    pushStyle(SpanStyle(color = Color(0xFF64748B), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                    append(code.substring(i, commentEnd))
                    pop()
                    i = commentEnd
                    continue
                }

                // Strings
                if (code[i] == '"' || code[i] == '\'') {
                    val quote = code[i]
                    var endQuote = i + 1
                    while (endQuote < n && code[endQuote] != quote) {
                        if (code[endQuote] == '\\' && endQuote + 1 < n) endQuote++
                        endQuote++
                    }
                    if (endQuote < n) endQuote++
                    pushStyle(SpanStyle(color = AccentGreen))
                    append(code.substring(i, endQuote))
                    pop()
                    i = endQuote
                    continue
                }

                // Numbers
                if (code[i].isDigit()) {
                    var numEnd = i
                    while (numEnd < n && (code[numEnd].isDigit() || code[numEnd] == '.')) {
                        numEnd++
                    }
                    pushStyle(SpanStyle(color = AccentOrange, fontWeight = FontWeight.SemiBold))
                    append(code.substring(i, numEnd))
                    pop()
                    i = numEnd
                    continue
                }

                // Words (Identifiers, Keywords, Types)
                if (code[i].isLetter() || code[i] == '_') {
                    var wordEnd = i
                    while (wordEnd < n && (code[wordEnd].isLetterOrDigit() || code[wordEnd] == '_')) {
                        wordEnd++
                    }
                    val word = code.substring(i, wordEnd)
                    when {
                        KEYWORDS.contains(word) -> {
                            pushStyle(SpanStyle(color = SecondaryPurple, fontWeight = FontWeight.Bold))
                            append(word)
                            pop()
                        }
                        TYPES.contains(word) -> {
                            pushStyle(SpanStyle(color = PrimaryCyan, fontWeight = FontWeight.SemiBold))
                            append(word)
                            pop()
                        }
                        // Function call detection
                        wordEnd < n && code[wordEnd] == '(' -> {
                            pushStyle(SpanStyle(color = CyanGlow, fontWeight = FontWeight.Medium))
                            append(word)
                            pop()
                        }
                        else -> {
                            append(word)
                        }
                    }
                    i = wordEnd
                    continue
                }

                // Operators and punctuation
                when (code[i]) {
                    '+', '-', '*', '/', '%', '=', '<', '>', '!', '&', '|', '^', '~' -> {
                        pushStyle(SpanStyle(color = AccentPink))
                        append(code[i].toString())
                        pop()
                    }
                    else -> {
                        append(code[i].toString())
                    }
                }
                i++
            }
        }
    }
}

/**
 * Multi-Language Code Snippets and Line Mappings Repository
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
                    "        if (arr[i] == target) return i;",
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

/**
 * Enhanced Multi-Language Code Trace Pane with syntax highlighting,
 * segmented tab row, dynamic active-line mapping, and scroll state preservation.
 */
@Composable
fun CodeTracePane(
    step: VisualizerStep,
    algorithmName: String = "Bubble Sort",
    syncPulse: State<Float> = mutableStateOf(0f),
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf(TraceLanguage.KOTLIN) }
    var varsExpanded by remember { mutableStateOf(false) }
    var stackExpanded by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    val codeData = remember(algorithmName, selectedLanguage) {
        AlgorithmCodeRegistry.getCode(algorithmName, selectedLanguage)
    }

    // Map active code lines from VisualizerStep to the current selected language
    val activeLinesInCurrentLang = remember(step.activeCodeLines, codeData) {
        if (step.activeCodeLines.isEmpty()) {
            emptyList()
        } else {
            step.activeCodeLines.flatMap { pyLine ->
                codeData.lineMapping[pyLine] ?: listOf(pyLine.coerceIn(1, codeData.lines.size.coerceAtLeast(1)))
            }.distinct()
        }
    }

    val highlightedLines = remember(codeData, selectedLanguage) {
        codeData.lines.map { line -> SyntaxHighlighter.highlight(line, selectedLanguage) }
    }

    // ── Sliding Active-Line Pill (bouncy vertical tracker) ──
    var pillTargetY by remember { mutableStateOf(0f) }
    var pillTargetH by remember { mutableStateOf(0f) }
    val pillPaddingPx = with(LocalDensity.current) { 4.dp.toPx() }

    // The pill takes the semantic accent of the currently executed line family.
    val pillAccent = when {
        activeLinesInCurrentLang.any {
            codeData.lines.getOrNull(it - 1)?.contains("swap", ignoreCase = true) == true
        } -> AlgoTokens.accentPink
        activeLinesInCurrentLang.any {
            codeData.lines.getOrNull(it - 1)?.contains("pivot", ignoreCase = true) == true
        } -> AlgoTokens.accentYellow
        else -> AlgoTokens.accentCyan
    }

    LaunchedEffect(activeLinesInCurrentLang, lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                (it.index + 1) in activeLinesInCurrentLang
            }
        }.collect { info ->
            if (info != null) {
                pillTargetY = info.offset.toFloat()
                pillTargetH = info.size.toFloat()
            }
        }
    }

    val animatedPillY by animateFloatAsState(
        targetValue = pillTargetY,
        animationSpec = AlgoTokens.lineTrackSpring,
        label = "activeLinePillY"
    )
    val animatedPillH by animateFloatAsState(
        targetValue = pillTargetH,
        animationSpec = tween(160),
        label = "activeLinePillH"
    )

    // Smoothly keep the active lines in view only when out of viewport
    LaunchedEffect(activeLinesInCurrentLang) {
        val firstActiveLine = activeLinesInCurrentLang.minOrNull()
        if (firstActiveLine != null && codeData.lines.isNotEmpty()) {
            val targetIdx = (firstActiveLine - 1).coerceIn(0, codeData.lines.size - 1)
            val visibleIndices = lazyListState.layoutInfo.visibleItemsInfo.map { it.index }
            if (targetIdx !in visibleIndices) {
                lazyListState.animateScrollToItem(targetIdx)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Top: Code Container ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackgroundElevated)
                .border(1.dp, SecondaryPurple.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Multi-Language Segmented Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = PurpleGlow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Source Code Trace",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }

                    // ── Sleek Segmented Language Tabs (Kotlin, Java, Python, C++) ──
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CanvasBackground)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        TraceLanguage.entries.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            val animatedBg by animateColorAsState(
                                targetValue = if (isSelected) SecondaryPurple else Color.Transparent,
                                animationSpec = tween(120),
                                label = "langTabBg_${lang.name}"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(animatedBg)
                                    .clickable { selectedLanguage = lang }
                                    .padding(horizontal = 7.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 8.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // ── Syntax Highlighted Code Listing (Optimized Cached Lines) ──
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Sliding active-line indicator pill (bouncy spring tracker,
                    // driven purely by graphicsLayer — no layout re-measures).
                    if (animatedPillH > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(with(LocalDensity.current) { animatedPillH.toDp() })
                                .graphicsLayer { translationY = animatedPillY + pillPaddingPx }
                                .clip(RoundedCornerShape(4.dp))
                                .drawBehind {
                                    // Pulse read in the render phase — no recomposition.
                                    drawRect(pillAccent.copy(alpha = 0.10f + 0.12f * syncPulse.value))
                                }
                                .border(1.dp, pillAccent.copy(alpha = 0.28f), RoundedCornerShape(4.dp))
                        )
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CanvasBackground)
                            .padding(vertical = 4.dp)
                    ) {
                    itemsIndexed(highlightedLines) { index, lineAnnotated ->
                        val lineNum = index + 1
                        val isActive = lineNum in activeLinesInCurrentLang
                        val rawLine = codeData.lines.getOrElse(index) { "" }

                        // ── Semantic accent mirroring: swap lines flash pink,
                        //    pivot lines yellow, sorted checks green, else cyan ──
                        val lineAccent = when {
                            rawLine.contains("swap", ignoreCase = true) -> AlgoTokens.accentPink
                            rawLine.contains("pivot", ignoreCase = true) -> AlgoTokens.accentYellow
                            rawLine.contains("sorted", ignoreCase = true) -> AlgoTokens.accentGreen
                            else -> AlgoTokens.accentCyan
                        }

                        // Inline variable chips: only variables referenced by this line
                        val lineVars = remember(rawLine, step.variables) {
                            if (!isActive || step.variables.isEmpty()) {
                                emptyMap()
                            } else {
                                step.variables.filterKeys { key ->
                                    Regex("(?<![A-Za-z0-9_])${Regex.escape(key)}(?![A-Za-z0-9_])")
                                        .containsMatchIn(rawLine)
                                }.entries.take(4).associate { it.key to it.value }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // ── Inline Variable Chips (mirrored inspector, above line) ──
                            if (lineVars.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 35.dp, top = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    lineVars.forEach { (label, value) ->
                                        InlineVarChip(
                                            label = label,
                                            value = value,
                                            accent = lineAccent
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 2.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Active-line semantic rail (glows with the sync pulse).
                                // Pulse is read in the render phase — no recomposition.
                                Box(
                                    modifier = Modifier
                                        .width(2.5.dp)
                                        .height(13.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .then(
                                            if (isActive) {
                                                Modifier
                                                    .drawBehind {
                                                        drawRoundRect(
                                                            color = lineAccent,
                                                            cornerRadius = CornerRadius(2.dp.toPx())
                                                        )
                                                    }
                                                    .graphicsLayer {
                                                        alpha = 0.55f + 0.45f * syncPulse.value
                                                    }
                                            } else {
                                                Modifier
                                            }
                                        )
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                Text(
                                    text = lineNum.toString().padStart(2, ' '),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isActive) lineAccent else TextDark,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(22.dp)
                                )

                                Text(
                                    text = lineAnnotated,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                }
            }
        }

        // ── Bottom: Collapsible Inspector Disclosure Buttons ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            InspectorDisclosure(
                title = "LIVE VARIABLE INSPECTOR",
                icon = Icons.Default.Code,
                accent = PrimaryCyan,
                summary = if (step.variables.isNotEmpty()) "${step.variables.size} live" else "pointers",
                expanded = varsExpanded,
                onToggle = { varsExpanded = !varsExpanded }
            ) {
                VariableInspectorContent(step = step)
            }

            InspectorDisclosure(
                title = "MEMORY CALL STACK",
                icon = Icons.Default.Terminal,
                accent = PurpleGlow,
                summary = "depth ${step.recursionDepth + 1}",
                expanded = stackExpanded,
                onToggle = { stackExpanded = !stackExpanded }
            ) {
                MemoryCallStackContent(
                    algorithmName = algorithmName,
                    step = step,
                    activeLines = activeLinesInCurrentLang
                )
            }
        }
    }
}

/**
 * Collapsible disclosure button used for the Variable Inspector & Memory Call
 * Stack. Collapsed by default to give the code listing maximum room; expanding
 * uses the shared low-stiffness panel spring so it never feels snappy.
 */
@Composable
private fun InspectorDisclosure(
    title: String,
    icon: ImageVector,
    accent: Color,
    summary: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "inspectorChevron_$title"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackground)
            .border(
                1.dp,
                accent.copy(alpha = if (expanded) 0.45f else 0.2f),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onToggle)
            .smoothPanelExpansion()
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        // ── Disclosure header button ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontSize = 8.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = summary,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = TextMuted,
                modifier = Modifier
                    .size(14.dp)
                    .graphicsLayer { rotationZ = chevronRotation }
            )
        }

        // ── Collapsible content ──
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(tween(160)),
            exit = shrinkVertically(
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeOut(tween(120))
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Content of the collapsible Live Variable Inspector disclosure.
 */
@Composable
private fun VariableInspectorContent(step: VisualizerStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (step.variables.isNotEmpty()) {
            step.variables.forEach { (k, v) ->
                VarBadge(label = k, value = v)
            }
        } else {
            // Fallback from pointers
            step.bottomPointers.forEach { (label, index) ->
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            step.topPointers.forEach { (label, index) ->
                val arrVal = step.array.getOrNull(index)?.toString() ?: index.toString()
                VarBadge(label = label, value = "$index ($arrVal)")
            }
            if (step.bottomPointers.isEmpty() && step.topPointers.isEmpty()) {
                VarBadge(label = "step", value = "${step.stepIndex + 1}")
            }
        }
    }
}

/**
 * Content of the collapsible Memory Call Stack disclosure.
 */
@Composable
private fun MemoryCallStackContent(
    algorithmName: String,
    step: VisualizerStep,
    activeLines: List<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(PurpleSubtle)
            .border(1.dp, SecondaryPurple.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$algorithmName(size=${step.array.size})",
                style = MaterialTheme.typography.labelSmall,
                color = PurpleGlow,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp
            )
            Text(
                text = "mode: ${step.renderMode.name.lowercase()}, active line: ${activeLines.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 7.5.sp
            )
        }
    }
}

@Composable
private fun VarBadge(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(CyanSubtle)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label = $value",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp
        )
    }
}

/**
 * Inline variable chip badge mirrored directly above active code lines
 * (e.g. `i = 0`, `pivot = 7`). Uses the line's semantic accent so the
 * chip, rail and canvas glow all speak the same functional color.
 */
@Composable
private fun InlineVarChip(
    label: String,
    value: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = 7.5.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 7.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun CodeTracePanePreview() {
    AlgoLensTheme {
        Box(modifier = Modifier.height(450.dp).padding(16.dp)) {
            CodeTracePane(
                step = VisualizerStep(
                    stepIndex = 3,
                    description = "Comparing elements at index 1 and 2",
                    array = listOf(3, 8, 9, 2, 6, 1, 5, 4, 7),
                    activeCodeLines = listOf(5),
                    variables = mapOf("i" to "0", "j" to "1", "arr[j]" to "8", "arr[j+1]" to "9")
                ),
                algorithmName = "Bubble Sort"
            )
        }
    }
}

