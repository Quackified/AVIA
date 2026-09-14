package com.example.algolens.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

/**
 * Red-line enforcement for the AlgoLens design system (Phase 6 / task 4.3.3).
 * Four detectors, one per architectural red line:
 *  1. HardcodedHexColorDetector      - no Color(0xFF...) outside ui/theme/Color.kt.
 *  2. RoundedCornerShapeLiteralDetector - no RoundedCornerShape(*.dp) literals.
 *  3. RawDpSpacingDetector           - no raw .dp inside padding / spacedBy / size.
 *  4. VisualizerScreenMutationDetector - no mutableStateOf inside VisualizerScreen.kt.
 *
 * All detectors scan via the UAST handler API (getApplicableUastTypes +
 * createUastHandler), classifying every UCallExpression by method name.
 * This is the non-deprecated scanning path in lint 9 / AGP 9 and, unlike
 * getApplicableConstructorTypes, it resolves reliably against library AAR
 * classes (which is what broke RoundedCornerShape against real Compose).
 *
 * Pre-existing violations are grandfathered through the app's lint baseline;
 * any NEW violation fails the build.
 */
class AlgoLensIssueRegistry : IssueRegistry() {
    override val vendor: Vendor = Vendor("AlgoLens", "com.example.algolens")

    override val issues: List<Issue> = listOf(
        HardcodedHexColorDetector.ISSUE,
        RoundedCornerShapeLiteralDetector.ISSUE,
        RawDpSpacingDetector.ISSUE,
        VisualizerScreenMutationDetector.ISSUE,
    )
}

private fun JavaContext.isAppFile(relative: String): Boolean =
    file.path.replace('\\', '/').endsWith("com/example/algolens/$relative")

private fun JavaContext.normalizedPath(): String = file.path.replace('\\', '/')

// Intended regexes: \b\d+(\.\d+)?\.dp\b  and  0x[0-9a-fA-F]{6,8}\b
private val DP_LITERAL_REGEX = Regex("\\b\\d+(\\.\\d+)?\\.dp\\b")
private val HEX_REGEX = Regex("0x[0-9a-fA-F]{6,8}\\b")

/** True when the call text contains a raw *.dp literal (e.g. 12.dp). */
private fun UCallExpression.hasDpLiteralArg(): Boolean =
    DP_LITERAL_REGEX.containsMatchIn(asSourceString())

/** True when the first argument is a 6-8 digit hex literal (e.g. 0xFF112233). */
private fun UCallExpression.hasHexLiteralArg(): Boolean {
    val arg = valueArguments.firstOrNull() ?: return false
    return HEX_REGEX.containsMatchIn(arg.asSourceString())
}

/**
 * Robust simple name of the invoked function / constructor. Plain and
 * extension calls populate [methodName]; constructor invocations sometimes
 * leave it null in newer UAST, so fall back to the resolved FQN's last segment
 * (e.g. androidx.compose.foundation.shape.RoundedCornerShape -> RoundedCornerShape).
 */
private fun UCallExpression.callName(): String? {
    methodName?.takeIf { it.isNotBlank() }?.let { return it }
    resolve()?.name?.let { return it }
    // Fallback for the lint-test harness where constructors / receiver-extension
    // calls don't resolve against stub sources: infer the name from the call text.
    val src = asSourceString()
    val open = src.indexOf('(')
    if (open > 0) {
        val name = src.substring(0, open).substringAfterLast('.').trim()
        if (name.isNotEmpty() && name.all { it.isLetterOrDigit() || it == '_' }) return name
    }
    return null
}
/** Red line 1: hex color literals only in ui/theme/Color.kt. */
class HardcodedHexColorDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if (node.callName() != "Color") return
                if (!node.hasHexLiteralArg()) return
                if (context.normalizedPath().endsWith("ui/theme/Color.kt")) return
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Hard-coded Color(0x...) literal: add a named token in ui/theme/Color.kt and reference it instead.",
                )
            }
        }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "AlgolensHardcodedHexColor",
            briefDescription = "Hard-coded hex color outside the theme",
            explanation = "AlgoLens tokens every color in ui/theme/Color.kt. A raw Color(0xFF...) literal bypasses the functional-accent rule and breaks dark-theme consistency. Add a named token and use it.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                HardcodedHexColorDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )
    }
}

/** Red line 2 (shapes): no RoundedCornerShape dp literals; use AlgoTokens.radius*. */
class RoundedCornerShapeLiteralDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if (node.callName() != "RoundedCornerShape") return
                if (!node.hasDpLiteralArg()) return
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "RoundedCornerShape(*.dp) literal: use an AlgoTokens.radius* token (radiusXxs/xs/sm/md/lg/xl) instead.",
                )
            }
        }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "AlgolensRoundedCornerShapeLiteral",
            briefDescription = "Raw dp radius instead of an AlgoTokens radius token",
            explanation = "Corner radii must come from AlgoTokens.radius* so the workspace language stays consistent. Replace the raw dp literal with the closest radius token.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                RoundedCornerShapeLiteralDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )
    }
}

/**
 * Red line 3 (spacing): no raw `.dp` inside Modifier.padding / spacedBy / size.
 * Kotlin extension calls (Modifier.size(...), Arrangement.spacedBy(...)) are
 * delivered to the handler as UCallExpressions regardless of the receiver.
 */
class RawDpSpacingDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if (node.callName() !in TARGET_METHODS) return
                if (!node.hasDpLiteralArg()) return
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Raw .dp literal inside ${node.methodName}(...): use an AlgoTokens.space* / size token so spacing stays on the 4dp grid.",
                )
            }
        }

    companion object {
        private val TARGET_METHODS = setOf("padding", "spacedBy", "size")

        val ISSUE: Issue = Issue.create(
            id = "AlgolensRawDpSpacing",
            briefDescription = "Raw dp padding / spacing / size instead of a token",
            explanation = "Layout metrics in Modifier.padding, spacedBy and size must come from AlgoTokens (space1-space8, iconButton*, inlineIcon*, minTouchTarget, ...) so the layout honors the 4dp grid.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                RawDpSpacingDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )
    }
}

/** Red line 4: VisualizerScreen.kt must not own mutableStateOf. */
class VisualizerScreenMutationDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if (node.callName() !in TARGET_METHODS) return
                if (!context.isAppFile("ui/visualizer/VisualizerScreen.kt")) return
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "${node.methodName}(...) inside VisualizerScreen.kt: hoist this state into VisualizerScreenState - the screen is a thin shell.",
                )
            }
        }

    companion object {
        private val TARGET_METHODS = setOf(
            "mutableStateOf",
            "mutableIntStateOf",
            "mutableFloatStateOf",
            "mutableLongStateOf",
            "mutableStateListOf",
            "mutableStateMapOf",
        )

        val ISSUE: Issue = Issue.create(
            id = "AlgolensVisualizerScreenMutation",
            briefDescription = "State created directly inside VisualizerScreen.kt",
            explanation = "VisualizerScreen.kt is a thin shell; all playback state is owned by VisualizerScreenState. Creating mutableStateOf (or a sibling) directly in the screen breaks the state-hoisting contract.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                VisualizerScreenMutationDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )
    }
}
