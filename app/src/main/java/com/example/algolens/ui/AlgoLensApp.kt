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
import androidx.compose.ui.platform.LocalContext
import com.example.algolens.data.UserPreferences
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.boot.BootController
import com.example.algolens.ui.boot.BootControllerEffect
import com.example.algolens.ui.boot.BootOverlay
import com.example.algolens.ui.chat.ChatScreen
import com.example.algolens.ui.components.BottomNavBar
import com.example.algolens.ui.components.NavTab
import com.example.algolens.ui.dashboard.DashboardScreen
import com.example.algolens.ui.onboarding.OnboardingScreen
import com.example.algolens.ui.practice.PracticeScreen
import com.example.algolens.ui.profile.ProfileScreen
import com.example.algolens.ui.settings.SettingsScreen
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.visualizer.VisualizerScreen

private enum class RootStage {
    BOOT,
    ONBOARDING,
    APP
}

@Composable
fun AlgoLensApp(
    modifier: Modifier = Modifier,
    bootController: BootController = remember { BootController() },
) {
    // Drive the boot countdown from the moment the root composition mounts.
    BootControllerEffect(bootController)

    val context = LocalContext.current
    var replayingOnboarding by remember { mutableStateOf(false) }
    val showOnboarding = !UserPreferences.hasCompletedOnboarding || replayingOnboarding

    var activeTab by remember { mutableStateOf(NavTab.HOME) }
    var selectedAlgorithm by remember { mutableStateOf<Algorithm?>(null) }
    var showingSettings by remember { mutableStateOf(false) }

    // System back press handling
    BackHandler(enabled = replayingOnboarding || selectedAlgorithm != null || showingSettings) {
        if (replayingOnboarding) {
            replayingOnboarding = false
        } else if (selectedAlgorithm != null) {
            selectedAlgorithm = null
        } else if (showingSettings) {
            showingSettings = false
        }
    }

    val rootStage = when {
        !bootController.ready -> RootStage.BOOT
        showOnboarding -> RootStage.ONBOARDING
        else -> RootStage.APP
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        // Crossfade between the boot overlay, onboarding tour, and real app shell.
        AnimatedContent(
            targetState = rootStage,
            transitionSpec = {
                (fadeIn(animationSpec = AlgoTokens.panelFadeSpring) togetherWith
                    fadeOut(animationSpec = AlgoTokens.panelFadeSpring))
            },
            label = "RootStageTransition",
        ) { stage ->
            when (stage) {
                RootStage.BOOT -> {
                    BootOverlay(controller = bootController)
                }
                RootStage.ONBOARDING -> {
                    OnboardingScreen(
                        onComplete = {
                            UserPreferences.setOnboardingCompleted(context, true)
                            replayingOnboarding = false
                        }
                    )
                }
                RootStage.APP -> {
                    AppShell(
                        activeTab = activeTab,
                        onTabSelected = { activeTab = it },
                        selectedAlgorithm = selectedAlgorithm,
                        onAlgorithmSelected = { selectedAlgorithm = it },
                        onAlgorithmCleared = { selectedAlgorithm = null },
                        showingSettings = showingSettings,
                        onOpenSettings = { showingSettings = true },
                        onCloseSettings = { showingSettings = false },
                        onReplayOnboarding = { replayingOnboarding = true },
                    )
                }
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
    showingSettings: Boolean,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onReplayOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(CanvasBackground)) {
        if (selectedAlgorithm != null) {
            VisualizerScreen(
                algorithm = selectedAlgorithm,
                onBack = onAlgorithmCleared,
            )
        } else if (showingSettings) {
            SettingsScreen(
                onBack = onCloseSettings,
                onReplayOnboarding = onReplayOnboarding,
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
                        NavTab.CHAT -> {
                            ChatScreen(
                                onAlgorithmClick = onAlgorithmSelected,
                            )
                        }
                        NavTab.PROFILE -> {
                            ProfileScreen(
                                onAlgorithmClick = onAlgorithmSelected,
                                onSettingsClick = onOpenSettings,
                                onNavigateToCatalog = { onTabSelected(NavTab.HOME) },
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
