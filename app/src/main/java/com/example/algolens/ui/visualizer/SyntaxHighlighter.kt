package com.example.algolens.ui.visualizer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.algolens.data.TraceLanguage
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary

/**
 * Syntax highlighting parser for code trace snippets.
 * Stays in `ui.visualizer` because it returns Compose [AnnotatedString] and
 * reads theme colour tokens; the multi-language code data lives in
 * `data/AlgorithmCodeRegistry.kt`.
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
                    pushStyle(SpanStyle(color = TextMuted, fontStyle = FontStyle.Italic))
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
                            pushStyle(SpanStyle(color = TextPrimary))
                            append(word)
                            pop()
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
                        pushStyle(SpanStyle(color = TextPrimary))
                        append(code[i].toString())
                        pop()
                    }
                }
                i++
            }
        }
    }
}
