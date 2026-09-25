package com.example.algolens.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.ui.components.AlgoCard
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.SectionLabel
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.components.pressPhysics

@Composable
fun ProfileScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    onSettingsClick: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    authRepository: com.example.algolens.data.auth.AuthRepository = remember {
        com.example.algolens.data.auth.UnavailableFirebaseAuthRepository()
    },
    modifier: Modifier = Modifier
) {
    val allAlgorithms = remember { SampleData.algorithms }
    val totalCount = remember(allAlgorithms) { allAlgorithms.size }
    val sortingCount = remember(allAlgorithms) {
        allAlgorithms.count {
            it.id in listOf(
                AlgorithmId.BUBBLE_SORT,
                AlgorithmId.SELECTION_SORT,
                AlgorithmId.INSERTION_SORT,
                AlgorithmId.MERGE_SORT,
                AlgorithmId.QUICK_SORT
            )
        }
    }
    val searchingCount = remember(allAlgorithms) {
        allAlgorithms.count {
            it.id in listOf(AlgorithmId.LINEAR_SEARCH, AlgorithmId.BINARY_SEARCH)
        }
    }
    val structureCount = remember(allAlgorithms) {
        allAlgorithms.count {
            it.id in listOf(
                AlgorithmId.STACK,
                AlgorithmId.QUEUE,
                AlgorithmId.BINARY_SEARCH_TREE,
                AlgorithmId.HEAP
            )
        }
    }
    val graphCount = remember(allAlgorithms) {
        allAlgorithms.count {
            it.id in listOf(AlgorithmId.BFS, AlgorithmId.DFS)
        }
    }
    val codeTraceCount = remember(totalCount) { totalCount * 4 }
    val savedIds = com.example.algolens.data.AppSettings.bookmarkedAlgorithmIds
    val bookmarked = remember(allAlgorithms, savedIds) {
        allAlgorithms.filter { it.id.name in savedIds }
    }

    val profile = authRepository.currentProfile
    var showEditProfileSheet by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
        ) {
            // ── 0. Top Header with Settings Gear Action ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AlgoTokens.space1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.space2)
                            .clip(CircleShape)
                            .background(PrimaryCyan)
                    )
                    Text(
                        text = "Operator Profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                com.example.algolens.ui.components.CompactIconButton(
                    icon = AlgoGlyphs.SettingsGear,
                    contentDescription = "Settings",
                    onClick = onSettingsClick
                )
            }

            // ── 1. Profile Hero + Right-Side Edit Profile Pencil Button ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AlgoTokens.space1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                // Avatar (photo or monogram fallback)
                Box(
                    modifier = Modifier.size(AlgoTokens.avatarHeroSize)
                ) {
                    ProfileAvatar(
                        avatarUri = profile.avatarUri,
                        displayName = profile.displayName,
                        size = AlgoTokens.avatarHeroSize,
                        shape = RoundedCornerShape(AlgoTokens.radiusLg),
                        onClick = { showEditProfileSheet = true }
                    )

                    // Active dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(AlgoTokens.inlineIconSm)
                            .clip(CircleShape)
                            .background(AccentGreen)
                            .border(AlgoTokens.strokeActive, CanvasBackground, CircleShape)
                    )
                }

                // Info Column (Nickname - Handle - Role)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                ) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${profile.handle} · ${profile.roleTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Row(
                        modifier = Modifier.padding(top = AlgoTokens.space1),
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyanSubtle)
                                .border(AlgoTokens.strokeThin, PrimaryCyan.copy(alpha = 0.3f), CircleShape)
                                .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                        ) {
                            Text(
                                text = if (profile.isGuest) "Offline mode" else "Signed in · ${profile.email}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Compact Pencil Icon Button on Right Side (Opens Edit Profile Sheet)
                com.example.algolens.ui.components.CompactIconButton(
                    icon = AlgoGlyphs.EditPencil,
                    contentDescription = "Edit Profile",
                    onClick = { showEditProfileSheet = true },
                    tint = PrimaryCyan,
                    borderColor = BorderCyan.copy(alpha = 0.45f)
                )
            }

            // ── 1b. Concise Separate Account Surface ──
            AccountStatusCard(
                authRepository = authRepository
            )

            // ── 2. Real Architecture & Catalogue Specification ──
            DoubleBezelShell(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space5)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Bars,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                            Text(
                                text = "Catalogue Specification",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$totalCount algorithms verified",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextNavy
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                        CatalogueRow(label = "Sorting algorithms", count = "$sortingCount verified", accent = PrimaryCyan)
                        CatalogueRow(label = "Search algorithms", count = "$searchingCount verified", accent = PrimaryCyan)
                        CatalogueRow(label = "Data structure algorithms", count = "$structureCount verified", accent = SecondaryPurple)
                        CatalogueRow(label = "Graph traversal algorithms", count = "$graphCount verified", accent = SecondaryPurple)
                        CatalogueRow(label = "Multi-language code traces", count = "$codeTraceCount listings (4 langs)", accent = PrimaryCyan)
                    }
                }
            }

            // ── 3. Bookmarked Section ──
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        SectionLabel(
                            icon = AlgoGlyphs.Bookmark,
                            text = "Bookmarked"
                        )
                    }
                    Text(
                        text = "${bookmarked.size} saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (bookmarked.isEmpty()) {
                    DoubleBezelShell(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space5)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            Icon(
                                imageVector = AlgoGlyphs.Bookmark,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(AlgoTokens.inlineIconLg)
                            )
                            Text(
                                text = "No bookmarked algorithms",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bookmark any algorithm from the Visualizer header to pin it here for rapid recall.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = AlgoTokens.space1)
                                    .height(AlgoTokens.Spacing.minTouchTarget)
                                    .pressPhysics()
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CyanSubtle)
                                    .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                                    .clickable { onNavigateToCatalog() }
                                    .padding(horizontal = AlgoTokens.space5),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Browse Catalog",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    bookmarked.forEach { algo ->
                        AlgoCard(
                            algo = algo,
                            onClick = { onAlgorithmClick(algo) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AlgoTokens.space4))
        }

        // Focused Edit Profile Sheet (separate from Account / Auth)
        androidx.compose.animation.AnimatedVisibility(
            visible = showEditProfileSheet,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut()
        ) {
            EditProfileSheet(
                authRepository = authRepository,
                onDismiss = { showEditProfileSheet = false }
            )
        }
    }
}

@Composable
private fun CatalogueRow(
    label: String,
    count: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
            .background(CardBackgroundElevated)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = AlgoType.labelSize
        )
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.SemiBold,
            fontSize = AlgoType.microSize
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun ProfileScreenPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        ProfileScreen(onAlgorithmClick = {})
    }
}
