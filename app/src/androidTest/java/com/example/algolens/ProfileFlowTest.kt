package com.example.algolens

import android.graphics.Bitmap
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.example.algolens.data.auth.FakeAuthRepository
import com.example.algolens.ui.profile.EditProfileSheet
import com.example.algolens.ui.profile.ProfileScreen
import com.example.algolens.ui.theme.AlgoLensTheme
import com.example.algolens.ui.theme.CanvasBackground
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.Before

class ProfileFlowTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Before fun keepTestActivityAwake() {
        compose.runOnUiThread {
            compose.activity.enableEdgeToEdge()
            compose.activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            compose.activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    @Test
    fun editorRestoresDraftAndRequiresExplicitDiscard() {
        val repo = FakeAuthRepository { _, _ -> }
        var dismissed = false
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            AlgoLensTheme { EditProfileSheet(repo, { dismissed = true }) }
        }
        compose.onNodeWithText("Save").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Display name").performTextReplacement("Ada Lovelace")
        compose.onNodeWithText("Save").assertIsEnabled()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithContentDescription("Display name").assertTextContains("Ada Lovelace")
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Discard changes?").assertIsDisplayed()
        compose.onNodeWithText("Keep editing").performClick()
        compose.onNodeWithContentDescription("Display name").assertTextContains("Ada Lovelace")
        compose.runOnIdle { assertFalse(dismissed); assertEquals("Duke Ducky", repo.currentProfile.displayName) }
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Discard", useUnmergedTree = true).performClick()
        compose.runOnIdle { assertTrue(dismissed); assertEquals("Duke Ducky", repo.currentProfile.displayName) }
    }

    @Test
    fun validSaveUpdatesIdentityAndEmptyOptionalField() {
        val repo = FakeAuthRepository { _, _ -> }
        var dismissed = false
        compose.setContent { AlgoLensTheme { EditProfileSheet(repo, { dismissed = true }) } }
        capture("editor-phone")
        compose.onNodeWithContentDescription("Display name").performTextReplacement("")
        compose.onNodeWithText("Save").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Display name").performTextReplacement("Ada Lovelace")
        compose.onNodeWithContentDescription("Study focus").performScrollTo().performTextReplacement("")
        compose.onNodeWithText("Save").performClick()
        compose.runOnIdle {
            assertTrue(dismissed)
            assertEquals("Ada Lovelace", repo.currentProfile.displayName)
            assertEquals("", repo.currentProfile.roleTitle)
        }
    }

    @Test
    fun profileDestinationsAndEmptyCollectionWork() {
        val repo = FakeAuthRepository { _, _ -> }
        var edit = false
        var settings = false
        var practice = false
        var catalog = false
        var chat = false
        compose.setContent {
            AlgoLensTheme {
                ProfileScreen(onAlgorithmClick = {}, authRepository = repo,
                    onEditProfileClick = { edit = true }, onSettingsClick = { settings = true },
                    onNavigateToPractice = { practice = true }, onNavigateToChat = { chat = true },
                    onNavigateToCatalog = { catalog = true })
            }
        }
        capture("profile-phone")
        compose.onNodeWithText("Edit profile").performClick()
        compose.onNodeWithText("Practice drills").performScrollTo().performClick()
        compose.onNodeWithText("Study preferences").performScrollTo().performClick()
        compose.onNodeWithText("Your tutor").performScrollTo().performClick()
        compose.onNodeWithText("Algorithm library").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(edit && settings && practice && catalog && chat) }
        compose.onNodeWithText("Saved algorithms").performScrollTo().performClick()
        compose.onNodeWithText("Keep your next algorithm close").assertIsDisplayed()
        capture("saved-empty-phone")
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("My Profile").assertIsDisplayed()
    }

    @Test
    fun narrowLargeTextEditorRemainsUsable() {
        val repo = FakeAuthRepository { _, _ -> }
        compose.setContent {
            AlgoLensTheme {
                CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.5f)) {
                    Box(Modifier.fillMaxSize().background(CanvasBackground)) {
                        Box(Modifier.width(320.dp)) { EditProfileSheet(repo, {}) }
                    }
                }
            }
        }
        capture("editor-narrow-large-text")
        compose.onNodeWithContentDescription("Study focus").performScrollTo().performTextReplacement("Graph algorithms")
        capture("editor-narrow-keyboard")
        compose.onNodeWithText("Save").assertIsDisplayed().assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals("Graph algorithms", repo.currentProfile.roleTitle) }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        assertNotNull(bitmap)
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "profile-review").apply { mkdirs() }
        File(directory, "$name.png").outputStream().use { bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
