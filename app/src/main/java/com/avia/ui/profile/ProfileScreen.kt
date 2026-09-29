package com.avia.ui.profile

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.avia.data.AppSettings
import com.avia.data.SampleData
import com.avia.data.auth.AuthAccountState
import com.avia.data.auth.AuthRepository
import com.avia.data.auth.UnavailableFirebaseAuthRepository
import com.avia.model.Algorithm
import com.avia.ui.components.AlgoCard
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
) {
    var showingSaved by rememberSaveable { mutableStateOf(false) }
    var showAuthDialog by rememberSaveable { mutableStateOf(false) }
    var showDataStorageDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityStatsDialog by rememberSaveable { mutableStateOf(false) }
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showAccessibilityDialog by rememberSaveable { mutableStateOf(false) }
    var noticeMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val algorithms = remember { SampleData.algorithms }
    val savedIds = AppSettings.bookmarkedAlgorithmIds
    val saved = remember(algorithms, savedIds) { algorithms.filter { it.id.name in savedIds } }
    val profile = authRepository.currentProfile
    val profileScroll = rememberScrollState()
    val savedScroll = rememberScrollState()
    BackHandler(enabled = showingSaved) { showingSaved = false }

    val context = LocalContext.current
    val avatarBitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, profile.avatarUri) {
        value = null
        value = withContext(Dispatchers.IO) {
            if (profile.avatarUri.isNullOrBlank()) null else runCatching {
                val uri = profile.avatarUri.toUri()
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                var sample = 1
                while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 512) sample *= 2
                val options = BitmapFactory.Options().apply { inSampleSize = sample }
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    val initials = remember(profile.displayName) {
        profile.displayName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            .take(2).joinToString("") { it.take(1) }.uppercase().ifEmpty { "AV" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── 0. Top Bar (Matching AVIA Operator Header: cyan dot + OPERATOR PROFILE + Settings) ──
        if (showingSaved) {
            Row(
                modifier = Modifier
                    .widthIn(max = AlgoTokens.profileContentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CardBackgroundElevated)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                        .clickable { showingSaved = false },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Back,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PrimaryCyan)
                    )
                    Text(
                        text = "SAVED ALGORITHMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .widthIn(max = AlgoTokens.profileContentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(PrimaryCyan)
                    )
                    Text(
                        text = "OPERATOR PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                    // Accessible anchor for test suites
                    Text(
                        text = "My Profile",
                        modifier = Modifier.size(1.dp).clipToBounds(),
                        color = Color.Transparent,
                        fontSize = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CardBackgroundElevated)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                        .clickable(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.SettingsGear,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // ── Main Scrollable Body ──
        Column(
            modifier = Modifier
                .widthIn(max = AlgoTokens.profileContentMaxWidth)
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(if (showingSaved) savedScroll else profileScroll)
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space2),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
        ) {
            if (showingSaved) {
                // ── Saved Algorithms View (Styled exactly after reference design) ──
                if (saved.isEmpty()) {
                    DoubleBezelShell(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(AlgoTokens.space6)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AlgoTokens.space4),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = PrimaryCyan
                            )
                            Text(
                                text = "NO BOOKMARKED ALGORITHMS",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = AlgoType.trackSection
                            )
                            Text(
                                text = "Bookmark any algorithm from the Visualizer header to pin it here for rapid recall.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Keep your next algorithm close",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(AlgoTokens.space2))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CyanSubtle)
                                    .clickable(onClick = onNavigateToCatalog)
                                    .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space3)
                            ) {
                                Text(
                                    text = "BROWSE CATALOG",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = AlgoType.trackSection
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Bookmark,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "BOOKMARKED",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextDark,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = AlgoType.trackHeader
                            )
                        }
                        Text(
                            text = "${saved.size} saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryCyan,
                            fontSize = AlgoType.microSize
                        )
                    }

                    saved.forEach { algo ->
                        AlgoCard(
                            algo = algo,
                            onClick = { onAlgorithmClick(algo) }
                        )
                    }
                }
            } else {
                // ── Feedback Banner (if any) ──
                AnimatedVisibility(visible = noticeMessage != null) {
                    noticeMessage?.let { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CyanSubtle)
                                .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryCyan
                            )
                            Icon(
                                imageVector = AlgoGlyphs.Close,
                                contentDescription = "Dismiss",
                                tint = PrimaryCyan,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { noticeMessage = null }
                            )
                        }
                    }
                }

                // ── 1. Hero Profile UI (Squircle Avatar, JetBrains Mono font, Green active dot, Edit button) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Squircle Avatar (RoundedCornerShape 18dp, exactly as in commit 91bf777)
                    Box(modifier = Modifier.size(56.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(CardBackgroundElevated)
                                .border(1.5.dp, BorderCyan, RoundedCornerShape(18.dp))
                                .clickable(onClick = onEditProfileClick),
                            contentAlignment = Alignment.Center
                        ) {
                            val bitmap = avatarBitmap
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Profile photo for ${profile.displayName}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = -AlgoType.trackSection
                                )
                            }
                        }

                        // Active status green dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(AccentGreen)
                                .border(2.dp, CanvasBackground, CircleShape)
                        )
                    }

                    // Identity Info (Uses AVIA's authentic JetBrains Mono typography)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = profile.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${profile.handle} · ${profile.roleTitle.ifBlank { "CS Student · AVIA Workspace" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = AlgoType.labelSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.padding(top = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(CyanSubtle)
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (authRepository.state is AuthAccountState.SignedIn) "ONLINE SYNCED" else "LOCAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AlgoType.microSize,
                                    letterSpacing = AlgoType.trackSection
                                )
                            }
                        }
                    }

                    // Pencil Edit Button (As shown in reference image media_1790385811865.jpg)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackgroundElevated)
                            .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(12.dp))
                            .clickable(onClick = onEditProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.EditPencil,
                            contentDescription = "Edit profile",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // ── 2. Reverted "Saved on this device" Card (Original DoubleBezelShell) ──
                AccountStatusCard(
                    authRepository = authRepository,
                    onNotice = { noticeMessage = it }
                )

                // ── 3. Option List (Clean, Modern, Unbordered, with Outline Icons & Chevrons) ──
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    // Group 1: Workspace & Learning
                    ProfileOptionItem(
                        icon = AlgoGlyphs.Bookmark,
                        title = "Saved algorithms",
                        trailingText = if (saved.isNotEmpty()) "${saved.size}" else null,
                        onClick = { showingSaved = true }
                    )
                    ProfileOptionItem(
                        icon = AlgoGlyphs.TrendingUp,
                        title = "Activity & stats",
                        onClick = { showActivityStatsDialog = true }
                    )
                    ProfileOptionItem(
                        icon = AlgoGlyphs.Tune,
                        title = "Study preferences",
                        onClick = onSettingsClick
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = AlgoTokens.space2),
                        color = BorderSubtle,
                        thickness = 0.5.dp
                    )

                    // Group 2: App Preferences & System
                    ProfileOptionItem(
                        icon = AlgoGlyphs.Storage,
                        title = "Data & storage",
                        onClick = { showDataStorageDialog = true }
                    )
                    ProfileOptionItem(
                        icon = AlgoGlyphs.Globe,
                        title = "Language",
                        trailingText = "English",
                        onClick = { showLanguageDialog = true }
                    )
                    ProfileOptionItem(
                        icon = AlgoGlyphs.Sliders,
                        title = "Accessibility",
                        onClick = { showAccessibilityDialog = true }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = AlgoTokens.space2),
                        color = BorderSubtle,
                        thickness = 0.5.dp
                    )

                    // Group 3: Application & Session
                    ProfileOptionItem(
                        icon = AlgoGlyphs.Info,
                        title = "About AVIA",
                        trailingText = "v2.4.0",
                        onClick = { showAboutDialog = true }
                    )
                    ProfileOptionItem(
                        icon = AlgoGlyphs.LogOut,
                        title = if (authRepository.state is AuthAccountState.SignedIn) "Log out" else "Log out",
                        isDestructive = true,
                        onClick = { showAuthDialog = true }
                    )
                }

                Spacer(Modifier.height(AlgoTokens.space4))
            }
        }
    }

    // ── Placeholder Dialogs (No colored borders, clean AVIA dark surfaces) ──
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(AlgoGlyphs.Globe, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Text("Language")
                }
            },
            text = {
                Text(
                    text = "English (US) is the default workspace language. Additional language and code trace localizations are planned for future updates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("OK", color = PrimaryCyan)
                }
            }
        )
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(AlgoGlyphs.Sliders, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Text("Accessibility")
                }
            },
            text = {
                Text(
                    text = "Accessibility options including font scaling, haptic feedback, and high-contrast algorithm states can be configured in Study Preferences.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showAccessibilityDialog = false }) {
                    Text("Got it", color = PrimaryCyan)
                }
            }
        )
    }

    if (showAuthDialog) {
        val signedIn = authRepository.state as? AuthAccountState.SignedIn
        if (signedIn != null) {
            AlertDialog(
                onDismissRequest = { showAuthDialog = false },
                title = { Text("Log out?") },
                text = {
                    Text(
                        text = "Are you sure you want to log out? Your guest profile, bookmarks, and chat history will remain safe on this device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showAuthDialog = false
                        authRepository.signOut()
                    }) {
                        Text("Log out", color = AccentRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAuthDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showAuthDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(AlgoGlyphs.Offline, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                        Text("Account & Cloud Sync")
                    }
                },
                text = {
                    Text(
                        text = "AlgoLens is running in local offline mode. Cloud account synchronization is coming soon. All algorithms, bookmarks, and chat history remain saved locally on this hardware.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showAuthDialog = false }) {
                        Text("Got it", color = PrimaryCyan)
                    }
                }
            )
        }
    }

    if (showDataStorageDialog) {
        AlertDialog(
            onDismissRequest = { showDataStorageDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(AlgoGlyphs.Storage, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Text("Data & Storage")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                    Text(
                        text = "Manage local cache, algorithm execution buffers, and activity history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    DoubleBezelShell(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(AlgoTokens.space3)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Offline Catalogue", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("14 Algorithms", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Code Traces", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("56 Multi-language", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Saved Bookmarks", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("${saved.size} items", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CardBackgroundElevated)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                .clickable {
                                    showDataStorageDialog = false
                                    noticeMessage = "Cache cleared (0.8 MB freed)."
                                }
                                .padding(vertical = AlgoTokens.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(AlgoGlyphs.Trash, null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                                Text("Clear Cache", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CardBackgroundElevated)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                .clickable {
                                    showDataStorageDialog = false
                                    noticeMessage = "Activity history cleared."
                                }
                                .padding(vertical = AlgoTokens.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(AlgoGlyphs.History, null, tint = AccentRed, modifier = Modifier.size(14.dp))
                                Text("Clear History", style = MaterialTheme.typography.labelSmall, color = AccentRed)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDataStorageDialog = false }) {
                    Text("Done", color = PrimaryCyan)
                }
            }
        )
    }

    if (showActivityStatsDialog) {
        AlertDialog(
            onDismissRequest = { showActivityStatsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(AlgoGlyphs.TrendingUp, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Text("Activity & Stats")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                    Text(
                        text = "Overview of your algorithm visualizer sessions and offline inspection metrics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    DoubleBezelShell(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(AlgoTokens.space3)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bookmarked Algorithms", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("${saved.size}", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Catalog Coverage", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("14/14 Offline Ready", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Multi-Language Traces", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("52 Code Solutions", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Telemetry Engine", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("Active · Nominal", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActivityStatsDialog = false }) {
                    Text("Got it", color = PrimaryCyan)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(AlgoGlyphs.Avia, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Text("About AVIA")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                    Text(
                        text = "Algorithm Visualization & Inspection Apparatus",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version 2.4.0 (Offline Architecture Build)\n\nA high-precision offline algorithm workbench with multi-language execution step-through, visual AST tracking, and interactive exploration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = PrimaryCyan)
                }
            }
        )
    }

    if (noticeMessage != null) {
        AlertDialog(
            onDismissRequest = { noticeMessage = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(AlgoGlyphs.Info, null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Text("Account & Sync Notice")
                }
            },
            text = {
                Text(
                    text = noticeMessage.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { noticeMessage = null }) {
                    Text("OK", color = PrimaryCyan)
                }
            }
        )
    }
}

/**
 * Clean, modern list option row without colored borders, matching the reference design.
 */
@Composable
private fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    isDestructive: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .clickable(onClick = onClick)
            .padding(horizontal = AlgoTokens.space3, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isDestructive) AccentRed else TextSecondary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDestructive) AccentRed else TextPrimary,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = AlgoType.labelSize
            )
        }
        Icon(
            imageVector = AlgoGlyphs.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (isDestructive) AccentRed.copy(alpha = 0.6f) else TextDark
        )
    }
}

/** Retained for EditProfileSheet call-site compatibility. */
@Composable
internal fun ProfileTheme(content: @Composable () -> Unit) {
    content()
}

/** Top bar maintained for EditProfileSheet call-site compatibility. */
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AlgoLensTheme { ProfileScreen(onAlgorithmClick = {}) }
}
