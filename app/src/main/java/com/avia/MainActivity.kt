package com.avia

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.avia.ui.AlgoLensApp
import com.avia.ui.boot.BootController
import com.avia.ui.theme.AlgoLensTheme

/**
 * Main Activity hosting the AlgoLens Jetpack Compose application.
 *
 * Cold-start path:
 *  1. installSplashScreen() applies the Theme.AlgoLens.Splash configured in
 *     AndroidManifest.xml (dark workspace background + cyan geometric boot
 *     mark). The library polyfills the API 31+ SplashScreen contract down to
 *     minSdk 24.
 *  2. setKeepOnScreenCondition holds the splash on screen until [BootController]
 *     reports `ready == true`. That flips after the in-Compose boot overlay
 *     settles (LaunchedEffect-driven delay of `BootController.DEFAULT_HOLD_MS`).
 *  3. enableEdgeToEdge + setContent swap the activity onto Theme.AlgoLens
 *     via the splash library's postSplashScreenTheme handoff.
 *  4. AlgoLensTheme + AlgoLensApp render the in-Compose BootOverlay which
 *     crossfades out as the dashboard mounts.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // MUST run before super.onCreate() per the core-splashscreen contract.
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Route hardware volume keys directly to STREAM_MUSIC so media volume
        // controls algorithm audio synthesis
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Initialize persistent user preferences and workspace settings
        com.avia.data.UserPreferences.init(applicationContext)
        com.avia.data.AppSettings.init(applicationContext)

        val bootController = BootController()
        // Dismiss the blank OS starting window immediately so only the
        // in-Compose "AVIA Logo and Text Splash" (BootOverlay) frame is shown.
        splash.setKeepOnScreenCondition { false }
        splash.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }

        enableEdgeToEdge()

        setContent {
            com.avia.ui.theme.AviaTheme {
                com.avia.ui.AviaApp(bootController = bootController)
            }
        }
    }
}