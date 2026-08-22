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
import com.example.algolens.ui.components.BottomNavBar
import com.example.algolens.ui.components.NavTab
import com.example.algolens.ui.dashboard.DashboardScreen
import com.example.algolens.ui.practice.PracticeScreen
import com.example.algolens.ui.profile.ProfileScreen
import com.example.algolens.ui.settings.SettingsScreen
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.visualizer.VisualizerScreen

@Composable
fun AlgoLensApp(
    modifier: Modifier = Modifier
) {
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
        if (selectedAlgorithm != null) {
            VisualizerScreen(
                algorithm = selectedAlgorithm!!,
                onBack = { selectedAlgorithm = null }
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
                                onAlgorithmClick = { selectedAlgorithm = it }
                            )
                        }
                        NavTab.EXPLORE -> {
                            PracticeScreen(
                                onBack = { activeTab = NavTab.HOME }
                            )
                        }
                        NavTab.PROFILE -> {
                            ProfileScreen(
                                onAlgorithmClick = { selectedAlgorithm = it }
                            )
                        }
                        NavTab.SETTINGS -> {
                            SettingsScreen(
                                onBack = { activeTab = NavTab.HOME }
                            )
                        }
                    }
                }

                // Bottom Navigation Bar
                BottomNavBar(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it }
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
