package com.avia.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Assert.assertTrue
import org.junit.Test

class AviaDetectorsTest : LintDetectorTest() {
typealias AlgoLensDetectorsTest = AviaDetectorsTest

    // Minimal Compose stubs so Kotlin type resolution succeeds.
    private val stubs = arrayOf(
        kotlin(
            "src/androidx/compose/ui/graphics/Color.kt",
            "package androidx.compose.ui.graphics\n" +
                "class Color private constructor(val value: Long)\n" +
                "fun Color(hex: Long): Color = Color(hex)\n",
        ),
        kotlin(
            "src/androidx/compose/ui/unit/Dp.kt",
            "package androidx.compose.ui.unit\n" +
                "class Dp\n" +
                "val Int.dp: Dp get() = Dp()\n" +
                "val Double.dp: Dp get() = Dp()\n",
        ),
        kotlin(
            "src/androidx/compose/ui/Modifier.kt",
            "package androidx.compose.ui\n" +
                "class Modifier\n" +
                "fun Modifier.padding(vararg values: Any): Modifier = this\n" +
                "fun Modifier.size(vararg values: Any): Modifier = this\n",
        ),
        kotlin(
            "src/androidx/compose/foundation/layout/Arrangement.kt",
            "package androidx.compose.foundation.layout\n" +
                "object Arrangement {\n" +
                "    fun spacedBy(vararg values: Any): Any = Any()\n" +
                "}\n",
        ),
        kotlin(
            "src/androidx/compose/foundation/shape/RoundedCornerShape.kt",
            "package androidx.compose.foundation.shape\n" +
                "class RoundedCornerShape(vararg radii: Any)\n",
        ),
        kotlin(
            "src/androidx/compose/runtime/MutableState.kt",
            "package androidx.compose.runtime\n" +
                "fun <T> mutableStateOf(value: T): Any = Any()\n",
        ),
    )

    private fun lintTask() =
        lint().allowMissingSdk().allowCompilationErrors().expectIdenticalTestModeOutput(false)

    override fun getDetector(): Detector = HardcodedHexColorDetector()

    override fun getIssues(): List<Issue> = listOf(
        HardcodedHexColorDetector.ISSUE,
        RoundedCornerShapeLiteralDetector.ISSUE,
        RawDpSpacingDetector.ISSUE,
        VisualizerScreenMutationDetector.ISSUE,
    )

    @Test
    fun testRegistryIsServiceLoaded() {
        val found = java.util.ServiceLoader.load(
            com.android.tools.lint.client.api.IssueRegistry::class.java,
            AviaIssueRegistry::class.java.classLoader,
        ).iterator()
        assertTrue(found.asSequence().any { it is AviaIssueRegistry })
    }


    @Test
    fun testMutableStateOfInVisualizerScreenIsFlagged() {
        lintTask()
            .files(*stubs, kotlin(
                "src/com/avia/ui/visualizer/VisualizerScreen.kt",
                "package com.avia.ui.visualizer\n" +
                    "import androidx.compose.runtime.mutableStateOf\n" +
                    "val X = mutableStateOf(false)\n",
            ))
            .run()
            .expect(
                """
                |src/com/avia/ui/visualizer/VisualizerScreen.kt:3: Error: mutableStateOf(...) inside VisualizerScreen.kt: hoist this state into VisualizerScreenState - the screen is a thin shell. [AlgolensVisualizerScreenMutation]
                |val X = mutableStateOf(false)
                |        ~~~~~~~~~~~~~~~~~~~~~
                |1 errors, 0 warnings
                """.trimMargin()
            )
    }

    @Test
    fun testMutableStateOfInOtherFilesIsAllowed() {
        lintTask()
            .files(*stubs, kotlin(
                "src/com/avia/ui/visualizer/Other.kt",
                "package com.avia.ui.visualizer\n" +
                    "import androidx.compose.runtime.mutableStateOf\n" +
                    "val X = mutableStateOf(false)\n",
            ))
            .run()
            .expectClean()
    }

    @Test
    fun testHexColorInsideThemeColorKtIsAllowed() {
        lintTask()
            .files(*stubs, kotlin(
                "src/com/avia/ui/theme/Color.kt",
                "package com.avia.ui.theme\n" +
                    "import androidx.compose.ui.graphics.Color\n" +
                    "val X = Color(0xFF123456)\n",
            ))
            .run()
            .expectClean()
    }

    @Test
    fun testTokenPaddingIsAllowed() {
        lintTask()
            .files(*stubs, kotlin(
                "src/com/avia/ui/Foo.kt",
                "package com.avia.ui\n" +
                    "import androidx.compose.ui.Modifier\n" +
                    "val space2 = 8\n" +
                    "val X = Modifier.padding(space2)\n",
            ))
            .run()
            .expectClean()
    }
@Test
    fun testHexColorOutsideThemeIsFlagged() {
        lintTask()
            .files(*stubs, kotlin(
                "src/com/avia/ui/Foo.kt",
                "package com.avia.ui\n" +
                    "import androidx.compose.ui.graphics.Color\n" +
                    "val X = Color(0xFF123456)\n",
            ))
            .run()
            .expectContains("[AlgolensHardcodedHexColor]")
    }

    @Test
    fun testRawDpSizeIsFlagged() {
        lintTask()
            .files(*stubs, kotlin(
                "src/com/avia/ui/Foo.kt",
                "package com.avia.ui\n" +
                    "import androidx.compose.ui.Modifier\n" +
                    "import androidx.compose.ui.unit.dp\n" +
                    "val X = Modifier.size(12.dp)\n",
            ))
            .run()
            .expectErrorCount(1)
    }

    }