package com.avia.ui

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.avia.data.UserPreferences
import com.avia.model.Algorithm
import com.avia.ui.boot.BootController
import com.avia.ui.boot.BootControllerEffect
import com.avia.ui.boot.BootOverlay
import com.avia.ui.chat.ChatScreen
import com.avia.ui.components.BottomNavBar
import com.avia.ui.components.NavTab
import com.avia.ui.dashboard.DashboardScreen
import com.avia.ui.onboarding.OnboardingScreen
import com.avia.ui.practice.ExploreCatalogScreen
import com.avia.ui.practice.PracticeScreen
import com.avia.ui.profile.ProfileScreen
import com.avia.ui.settings.SettingsScreen
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.visualizer.VisualizerScreen

private enum class RootStage {
    BOOT,
    ONBOARDING,
    APP
}

@Composable
fun AviaApp(
    modifier: Modifier = Modifier,
    bootController: BootController = remember { BootController() },
) {
    // Drive the boot countdown from the moment the root composition mounts.
    BootControllerEffect(bootController)

    val context = LocalContext.current
    var replayingOnboarding by remember { mutableStateOf(false) }
    val showOnboarding = !UserPreferences.hasCompletedOnboarding || replayingOnboarding

    var activeTab by rememberSaveable { mutableStateOf(NavTab.HOME) }
    var selectedAlgorithm by remember { mutableStateOf<Algorithm?>(null) }
    var showingSettings by remember { mutableStateOf(false) }
    var activePracticeCategory by rememberSaveable { mutableStateOf<String?>(null) }

    // System back press handling: child routes first, then non-Home root tabs -> HOME
    BackHandler(
        enabled = replayingOnboarding || selectedAlgorithm != null || showingSettings || activePracticeCategory != null || activeTab != NavTab.HOME
    ) {
        when {
            replayingOnboarding -> replayingOnboarding = false
            selectedAlgorithm != null -> selectedAlgorithm = null
            showingSettings -> showingSettings = false
            activePracticeCategory != null -> activePracticeCategory = null
            activeTab != NavTab.HOME -> activeTab = NavTab.HOME
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
                        activePracticeCategory = activePracticeCategory,
                        onStartPracticeCategory = { activePracticeCategory = it },
                        onPracticeCategoryCleared = { activePracticeCategory = null },
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
    activePracticeCategory: String?,
    onStartPracticeCategory: (String) -> Unit,
    onPracticeCategoryCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val chatManager = com.avia.ui.chat.rememberChatSessionManager()
    val authRepository = remember {
        try {
            com.avia.data.firebase.FirebaseAuthRepository()
        } catch (_: Exception) {
            com.avia.data.auth.UnavailableFirebaseAuthRepository()
        }
    }
    val syncEngine = (authRepository as? com.avia.data.firebase.FirebaseAuthRepository)?.syncEngine

    // Wire real-time chat persistence callbacks to Firestore sync engine
    androidx.compose.runtime.DisposableEffect(chatManager, syncEngine) {
        if (syncEngine != null) {
            chatManager.repository.onConversationSaved = { ownerId, conversation ->
                if (ownerId != com.avia.data.chat.ChatHistoryRepository.DEFAULT_GUEST_OWNER) {
                    syncEngine.syncChatConversationAsync(ownerId, conversation)
                }
            }
            chatManager.repository.onConversationDeleted = { ownerId, conversationId ->
                if (ownerId != com.avia.data.chat.ChatHistoryRepository.DEFAULT_GUEST_OWNER) {
                    syncEngine.deleteChatConversationAsync(ownerId, conversationId)
                }
            }
        }
        onDispose {
            chatManager.repository.onConversationSaved = null
            chatManager.repository.onConversationDeleted = null
        }
    }

    // Keep chat owner aligned with Firebase Auth state and merge remote cloud data
    androidx.compose.runtime.LaunchedEffect(authRepository.state) {
        when (val s = authRepository.state) {
            is com.avia.data.auth.AuthAccountState.SignedIn -> {
                val uid = s.profile.uid
                val migrate = authRepository.migrationPolicy == com.avia.data.auth.GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT
                chatManager.switchOwner(uid, migrateGuestData = migrate)

                if (syncEngine != null) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val remoteConvsResult = syncEngine.fetchChatConversations(uid)
                        val remoteConvs = remoteConvsResult.getOrDefault(emptyList())
                        if (remoteConvs.isNotEmpty()) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                chatManager.syncWithRemote(remoteConvs)
                            }
                        }
                        // Sync up any local conversations that might not be on remote yet
                        val localConvs = chatManager.conversations
                        for (conv in localConvs) {
                            if (conv.messages.any { it.sender == com.avia.model.chat.ChatSender.USER }) {
                                syncEngine.syncChatConversation(uid, conv)
                            }
                        }
                    }
                }
            }
            is com.avia.data.auth.AuthAccountState.SignedOut -> {
                chatManager.switchOwner(com.avia.data.chat.ChatHistoryRepository.DEFAULT_GUEST_OWNER, migrateGuestData = false)
            }
            else -> Unit
        }
    }

    var editingProfile by rememberSaveable { mutableStateOf(false) }
    val profileState = rememberSaveableStateHolder()
    var visualizerReturnContext by remember { mutableStateOf<com.avia.model.VisualizerReturnContext?>(null) }
    var initialVisualizerStepIdx by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(CanvasBackground)) {
        if (editingProfile) {
            com.avia.ui.profile.EditProfileSheet(
                authRepository = authRepository,
                onDismiss = { editingProfile = false },
            )
        } else if (selectedAlgorithm != null) {
            VisualizerScreen(
                algorithm = selectedAlgorithm,
                initialStepIdx = initialVisualizerStepIdx,
                onBack = {
                    initialVisualizerStepIdx = 0
                    onAlgorithmCleared()
                },
                onOpenFullChat = { algo, stepIdx, prompt ->
                    visualizerReturnContext = com.avia.model.VisualizerReturnContext(algo, stepIdx)
                    chatManager.createNewChat()
                    chatManager.sendMessage(prompt)
                    initialVisualizerStepIdx = 0
                    onAlgorithmCleared()
                    onTabSelected(NavTab.CHAT)
                }
            )
        } else if (showingSettings) {
            SettingsScreen(
                onBack = onCloseSettings,
                onReplayOnboarding = onReplayOnboarding,
            )
        } else if (activePracticeCategory != null) {
            PracticeScreen(
                initialCategory = activePracticeCategory,
                onBack = onPracticeCategoryCleared,
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
                            ExploreCatalogScreen(
                                onStartPractice = onStartPracticeCategory,
                            )
                        }
                        NavTab.CHAT -> {
                            ChatScreen(
                                onAlgorithmClick = onAlgorithmSelected,
                                manager = chatManager,
                                visualizerReturnContext = visualizerReturnContext,
                                onReturnToVisualizer = {
                                    val target = visualizerReturnContext ?: return@ChatScreen
                                    visualizerReturnContext = null
                                    initialVisualizerStepIdx = target.stepIndex
                                    onAlgorithmSelected(target.algorithm)
                                }
                            )
                        }
                        NavTab.PROFILE -> {
                            profileState.SaveableStateProvider("profile") {
                            ProfileScreen(
                                onAlgorithmClick = onAlgorithmSelected,
                                onSettingsClick = onOpenSettings,
                                onNavigateToCatalog = { onTabSelected(NavTab.HOME) },
                                authRepository = authRepository,
                                onEditProfileClick = { editingProfile = true },
                                onNavigateToPractice = { onTabSelected(NavTab.EXPLORE) },
                                onNavigateToChat = { onTabSelected(NavTab.CHAT) },
                            )
                            }
                        }
                    }
                }

                // Bottom Navigation Bar - hidden when keyboard is open to avoid pushing the bottom bar or leaving dead space
                val isImeOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
                if (!isImeOpen) {
                    BottomNavBar(
                        activeTab = activeTab,
                        onTabSelected = onTabSelected,
                    )
                }
            }
        }
    }
}

@Composable
fun AlgoLensApp(
    modifier: Modifier = Modifier,
    bootController: BootController = remember { BootController() },
) {
    AviaApp(modifier = modifier, bootController = bootController)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun AviaAppPreview() {
    com.avia.ui.theme.AviaTheme {
        AviaApp()
    }
}
