package com.example.algolens.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.boot.BootController
import com.example.algolens.ui.boot.BootControllerEffect
import com.example.algolens.ui.boot.BootOverlay
import com.example.algolens.ui.components.BottomNavBar
import com.example.algolens.ui.components.NavTab
import com.example.algolens.ui.dashboard.DashboardScreen
import com.example.algolens.ui.practice.PracticeScreen
import com.example.algolens.ui.profile.ProfileScreen
import com.example.algolens.ui.settings.SettingsScreen
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.visualizer.VisualizerScreen

@Composable
fun AlgoLensApp(
    modifier: Modifier = Modifier,
    bootController: BootController = remember { BootController() },
) {
    // Drive the boot countdown from the moment the root composition mounts.
    BootControllerEffect(bootController)

    var activeTab by remember { mutableStateOf(NavTab.HOME) }
    var selectedAlgorithm by remember { mutableStateOf<Algorithm?>(null) }

    // System back press handling
    BackHandler(enabled = selectedAlgorithm != null) {
        selectedAlgorithm = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // Crossfade between the boot overlay and the real app shell. The
        // overlay fades out using AlgoTokens.panelSpring (600ms ceiling) once
        // BootController.ready flips, so the dashboard mounts into the same
        // dark workspace the OS splash revealed.
        AnimatedContent(
            targetState = bootController.ready,
            transitionSpec = {
                (fadeIn(animationSpec = AlgoTokens.panelFadeSpring) togetherWith
                    fadeOut(animationSpec = AlgoTokens.panelFadeSpring))
            },
            label = "BootToApp",
        ) { isReady ->
            if (!isReady) {
                BootOverlay(controller = bootController)
            } else {
                AppShell(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it },
                    selectedAlgorithm = selectedAlgorithm,
                    onAlgorithmSelected = { selectedAlgorithm = it },
                    onAlgorithmCleared = { selectedAlgorithm = null },
                )
            }
        }
    }
}

/**
 * Pure presentational shell extracted from [AlgoLensApp] so the boot-overlay
 * branch doesn't have to repeat the dashboard / visualizer wiring.
 */
@Composable
private fun AppShell(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    selectedAlgorithm: Algorithm?,
    onAlgorithmSelected: (Algorithm) -> Unit,
    onAlgorithmCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(CanvasBackground)) {
        if (selectedAlgorithm != null) {
            VisualizerScreen(
                algorithm = selectedAlgorithm,
                onBack = onAlgorithmCleared,
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    when (activeTab) {
                        NavTab.HOME -> {
                            DashboardScreen(
                                onAlgorithmClick = onAlgorithmSelected,
                            )
                        }
                        NavTab.EXPLORE -> {
                            PracticeScreen(
                                onBack = { onTabSelected(NavTab.HOME) },
                            )
                        }
                        NavTab.PROFILE -> {
                            ProfileScreen(
                                onAlgorithmClick = onAlgorithmSelected,
                            )
                        }
                        NavTab.SETTINGS -> {
                            SettingsScreen(
                                onBack = { onTabSelected(NavTab.HOME) },
                            )
                        }
                    }
                }

                // Bottom Navigation Bar
                BottomNavBar(
                    activeTab = activeTab,
                    onTabSelected = onTabSelected,
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun AlgoLensAppPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        AlgoLensApp()
    }
}
