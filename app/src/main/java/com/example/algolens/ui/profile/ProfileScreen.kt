package com.example.algolens.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.algolens.data.AppSettings
import com.example.algolens.data.SampleData
import com.example.algolens.data.TraceLanguage
import com.example.algolens.data.auth.AuthRepository
import com.example.algolens.data.auth.UnavailableFirebaseAuthRepository
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.AlgoCard
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.*

@Composable
fun ProfileScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    authRepository: AuthRepository = remember { UnavailableFirebaseAuthRepository() },
    onEditProfileClick: () -> Unit = {},
    onNavigateToPractice: () -> Unit = {},
    onNavigateToChat: () -> Unit = {}
) = ProfileTheme {
    var showingSaved by rememberSaveable { mutableStateOf(false) }
    val algorithms = remember { SampleData.algorithms }
    val savedIds = AppSettings.bookmarkedAlgorithmIds
    val saved = remember(algorithms, savedIds) { algorithms.filter { it.id.name in savedIds } }
    val profile = authRepository.currentProfile
    val profileScroll = rememberScrollState()
    val savedScroll = rememberScrollState()
    BackHandler(enabled = showingSaved) { showingSaved = false }

    Column(
        modifier.fillMaxSize().background(CanvasBackground).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileTopBar(
            title = if (showingSaved) "Saved algorithms" else "My Profile",
            onBack = if (showingSaved) ({ showingSaved = false }) else null,
            trailing = {
                if (!showingSaved) {
                    IconButton(onClick = onSettingsClick) {
                        Icon(AlgoGlyphs.SettingsGear, "Settings", tint = TextPrimary)
                    }
                }
            }
        )
        Column(
            Modifier.widthIn(max = AlgoTokens.profileContentMaxWidth).fillMaxWidth()
                .weight(1f).verticalScroll(if (showingSaved) savedScroll else profileScroll)
                .padding(horizontal = AlgoTokens.space7, vertical = AlgoTokens.space6),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space7)
        ) {
            if (showingSaved) {
                if (saved.isEmpty()) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = AlgoTokens.space8),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space6)
                    ) {
                        Icon(AlgoGlyphs.Bookmark, null, Modifier.size(AlgoTokens.iconButtonLg), PrimaryCyan)
                        Text("Keep your next algorithm close", style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary, textAlign = TextAlign.Center)
                        Text("Tap the bookmark in any algorithm to save it here.",
                            style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                            textAlign = TextAlign.Center)
                        Button(onClick = onNavigateToCatalog, shape = RoundedCornerShape(AlgoTokens.radiusMd)) {
                            Text("Browse algorithms")
                        }
                    }
                } else {
                    Text("${saved.size} saved on this device", style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary)
                    saved.forEach { algorithm ->
                        AlgoCard(algo = algorithm, onClick = { onAlgorithmClick(algorithm) })
                    }
                }
            } else {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val stack = maxWidth < AlgoTokens.profileHeroStackWidth || LocalDensity.current.fontScale > 1.3f
                    val avatar: @Composable () -> Unit = {
                        ProfileAvatar(
                            avatarUri = profile.avatarUri,
                            displayName = profile.displayName,
                            size = AlgoTokens.profileAvatarSize,
                            shape = CircleShape,
                            onClick = onEditProfileClick,
                            actionLabel = "Edit profile photo"
                        )
                    }
                    val identity: @Composable () -> Unit = {
                        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                            Text(profile.displayName, style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(profile.handle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            if (profile.roleTitle.isNotBlank()) {
                                Text(profile.roleTitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Button(
                                onClick = onEditProfileClick,
                                modifier = Modifier.padding(top = AlgoTokens.space4),
                                shape = RoundedCornerShape(AlgoTokens.radiusMd),
                                contentPadding = PaddingValues(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space5)
                            ) { Text("Edit profile", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                    if (stack) {
                        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space6)) {
                            avatar()
                            identity()
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space6)) {
                            avatar()
                            Box(Modifier.weight(1f)) { identity() }
                        }
                    }
                }
                Column {
                    ProfileDestination(AlgoGlyphs.Bookmark, "Saved algorithms",
                        if (saved.isEmpty()) "Your personal collection" else "${saved.size} saved",
                        onClick = { showingSaved = true })
                    ProfileDestination(AlgoGlyphs.Compass, "Practice drills", "Put your understanding to work",
                        onClick = onNavigateToPractice)
                    ProfileDestination(AlgoGlyphs.Book, "Algorithm library",
                        "${algorithms.size} algorithms · ${TraceLanguage.entries.size} code languages",
                        onClick = onNavigateToCatalog)
                    HorizontalDivider(Modifier.padding(vertical = AlgoTokens.space4), color = BorderSubtle)
                    ProfileDestination(AlgoGlyphs.Tune, "Study preferences", "Code language, playback & accessibility",
                        onClick = onSettingsClick)
                    ProfileDestination(AlgoGlyphs.Chat, "Your tutor", "Continue a conversation or ask a question",
                        onClick = onNavigateToChat)
                }
                AccountStatusCard(authRepository)
                Spacer(Modifier.height(AlgoTokens.space4))
            }
        }
    }
}

@Composable
internal fun ProfileTheme(content: @Composable () -> Unit) {
    MaterialTheme(typography = ProfileTypography, content = content)
}

@Composable
internal fun ProfileTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        Modifier.widthIn(max = AlgoTokens.profileContentMaxWidth).fillMaxWidth()
            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(AlgoTokens.avatarHeroSize), contentAlignment = Alignment.CenterStart) {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(AlgoGlyphs.Back, "Back", tint = TextPrimary) }
            }
        }
        Text(title, Modifier.weight(1f).semantics { heading() },
            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold,
            color = TextPrimary, textAlign = TextAlign.Center)
        Box(Modifier.widthIn(min = AlgoTokens.avatarHeroSize), contentAlignment = Alignment.CenterEnd) { trailing() }
    }
}

@Composable
private fun ProfileDestination(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = AlgoTokens.profileRowMinHeight)
            .clip(RoundedCornerShape(AlgoTokens.radiusMd)).pressPhysics()
            .clickable(onClick = onClick)
            .padding(vertical = AlgoTokens.space6, horizontal = AlgoTokens.space2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space6)
    ) {
        Icon(icon, null, Modifier.size(AlgoTokens.space7), tint = TextPrimary)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary,
                fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(AlgoGlyphs.ChevronRight, null, Modifier.size(AlgoTokens.inlineIconLg), tint = TextSecondary)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AlgoLensTheme { ProfileScreen(onAlgorithmClick = {}) }
}
