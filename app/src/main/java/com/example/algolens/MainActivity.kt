package com.example.algolens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.algolens.ui.AlgoLensApp
import com.example.algolens.ui.boot.BootController
import com.example.algolens.ui.theme.AlgoLensTheme

/**
 * Main Activity hosting the AlgoLens Jetpack Compose application.
 *
 * Cold-start path:
 *  1. installSplashScreen() applies the Theme.AlgoLens.Splash configured in
 *     AndroidManifest.xml (dark workspace background + cyan geometric boot
 *     mark). The library polyfills the API 31+ SplashScreen contract down to
 *     minSdk 24.
 *  2. setKeepOnScreenCondition holds the splash on screen until [BootController]
 *     reports `ready == true`. That flips after the first composition settles
 *     (LaunchedEffect-driven delay, ~600ms — the panelSpring ceiling).
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

        val bootController = BootController()
        splash.setKeepOnScreenCondition { !bootController.ready }

        enableEdgeToEdge()

        setContent {
            AlgoLensTheme {
                AlgoLensApp(bootController = bootController)
            }
        }
    }
}