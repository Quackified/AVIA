package com.example.algolens.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.algolens.data.auth.AuthAccountState
import com.example.algolens.data.auth.AuthRepository
import com.example.algolens.data.auth.ProfileValidator
import com.example.algolens.data.auth.UserProfile
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentRed
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.RedSubtle
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle

/**
 * Testable draft state for the Edit Profile flow.
 * Tracks edits against the initial [UserProfile], validates fields, and detects unsaved changes.
 */
@Stable
class ProfileEditorDraft(
    private val initialProfile: UserProfile
) {
    var displayName by mutableStateOf(initialProfile.displayName)
    var handle by mutableStateOf(initialProfile.handle)
    var roleTitle by mutableStateOf(initialProfile.roleTitle)
    var avatarUri by mutableStateOf(initialProfile.avatarUri)
    var showDiscardConfirm by mutableStateOf(false)

    val displayNameError: String?
        get() = ProfileValidator.validateDisplayName(displayName)

    val handleError: String?
        get() = ProfileValidator.validateHandle(handle)

    val roleTitleError: String?
        get() = ProfileValidator.validateRoleTitle(roleTitle)

    val isValid: Boolean
        get() = displayNameError == null && handleError == null && roleTitleError == null

    val hasUnsavedChanges: Boolean
        get() = displayName.trim() != initialProfile.displayName.trim() ||
            ProfileValidator.normalizeHandle(handle) != ProfileValidator.normalizeHandle(initialProfile.handle) ||
            roleTitle.trim() != initialProfile.roleTitle.trim() ||
            avatarUri != initialProfile.avatarUri

    fun copy(
        displayName: String = this.displayName,
        handle: String = this.handle,
        roleTitle: String = this.roleTitle,
        avatarUri: String? = this.avatarUri
    ): ProfileEditorDraft {
        return ProfileEditorDraft(initialProfile).also { draft ->
            draft.displayName = displayName
            draft.handle = handle
            draft.roleTitle = roleTitle
            draft.avatarUri = avatarUri
        }
    }

    /**
     * Attempts to save validated changes into [authRepository].
     * Returns `true` if valid and saved, `false` otherwise.
     */
    fun saveTo(authRepository: AuthRepository): Boolean {
        if (!isValid) return false
        authRepository.updateAvatar(avatarUri)
        authRepository.updateProfile(
            displayName = displayName.trim(),
            handle = ProfileValidator.normalizeHandle(handle),
            roleTitle = roleTitle.trim().ifEmpty { "CS Student · AVIA Workspace" }
        )
        showDiscardConfirm = false
        return true
    }

    fun commitTo(authRepository: AuthRepository): Boolean = saveTo(authRepository)

    /**
     * Handles a Cancel/Back request. If there are unsaved changes, prompts for confirmation first.
     * Returns `true` if the sheet can dismiss immediately.
     */
    fun requestCancel(): Boolean {
        return if (hasUnsavedChanges && !showDiscardConfirm) {
            showDiscardConfirm = true
            false
        } else {
            showDiscardConfirm = false
            true
        }
    }

    companion object {
        fun from(profile: UserProfile): ProfileEditorDraft = ProfileEditorDraft(profile)
    }
}

/**
 * Renders the user's profile avatar.
 * If [avatarUri] points to a readable image via [android.content.ContentResolver],
 * decodes and displays the cropped bitmap; otherwise renders a clean 2-letter monogram placeholder.
 */
@Composable
fun ProfileAvatar(
    avatarUri: String?,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = AlgoTokens.avatarHeroSize,
    shape: Shape = RoundedCornerShape(AlgoTokens.radiusLg),
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val decodedBitmap: ImageBitmap? = remember(avatarUri) {
        if (avatarUri.isNullOrBlank()) {
            null
        } else {
            runCatching {
                val uri = Uri.parse(avatarUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    val initials = remember(displayName) {
        displayName.trim().take(2).uppercase().ifEmpty { "AV" }
    }

    val borderColor = if (avatarUri != null) PrimaryCyan else BorderCyan
    val clickMod = if (onClick != null) {
        Modifier
            .pressPhysics(shape = shape, accent = PrimaryCyan)
            .clickable { onClick() }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(if (avatarUri != null) CyanSubtle else CardBackgroundElevated)
            .border(AlgoTokens.strokeMedium, borderColor, shape)
            .then(clickMod),
        contentAlignment = Alignment.Center
    ) {
        if (decodedBitmap != null) {
            Image(
                bitmap = decodedBitmap,
                contentDescription = "Profile photo for $displayName",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Concise, honest Account & Local Storage status card displayed on [ProfileScreen].
 *
 * Makes clear that the workspace runs offline on-device and that optional account sign-in
 * (Google Sign-In via Firebase Authentication) is not enabled in this build, without collecting
 * any credentials or displaying non-functional sign-in forms.
 */
@Composable
fun AccountStatusCard(
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    val accountState = authRepository.state

    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AlgoTokens.space4)
    ) {
        when (accountState) {
            is AuthAccountState.SignedIn -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                    ) {
                        Text(
                            text = "Signed In · ${accountState.profile.provider?.displayName ?: "Account"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = accountState.profile.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(RedSubtle)
                            .border(AlgoTokens.strokeThin, AccentRed.copy(alpha = 0.45f), RoundedCornerShape(AlgoTokens.radiusSm))
                            .clickable { authRepository.signOut() }
                            .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Offline,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier
                            .padding(top = AlgoTokens.space1)
                            .size(AlgoTokens.inlineIconMd)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                    ) {
                        Text(
                            text = "Local Guest Workspace · On-Device Storage",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Your profile, bookmarks, settings, and chat threads are saved on this device. Optional Google Sign-In (Firebase Auth) is not enabled in this offline build.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

/**
 * Focused, native-feeling Edit Profile sheet (`EditProfileSheet`).
 *
 * Edits local profile identity (`displayName`, `handle`, `roleTitle`) and avatar photo
 * via the Android System Photo Picker (`PickVisualMedia`) with validation and unsaved-change
 * protection. Contains no speculative cloud-sync or credential collection forms.
 */
@Composable
fun EditProfileSheet(
    authRepository: AuthRepository,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile = authRepository.currentProfile
    val draft = remember(profile) { ProfileEditorDraft(profile) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            draft.avatarUri = uri.toString()
        }
    }

    BackHandler {
        if (draft.requestCancel()) {
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.92f))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasBackground)
                .verticalScroll(rememberScrollState())
                .padding(AlgoTokens.space5),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
        ) {
            // ── 1. Top Bar: Cancel | Edit Profile | Save ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(CardBackgroundElevated)
                        .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                        .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm))
                        .clickable {
                            if (draft.requestCancel()) {
                                onDismiss()
                            }
                        }
                        .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                val canSave = draft.isValid
                Box(
                    modifier = Modifier
                        .heightIn(min = AlgoTokens.Spacing.minTouchTarget)
                        .alpha(if (canSave) 1f else AlgoTokens.disabledAlpha)
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(if (canSave) PrimaryCyan else CardBackground)
                        .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), enabled = canSave)
                        .clickable(enabled = canSave) {
                            if (draft.saveTo(authRepository)) {
                                onDismiss()
                            }
                        }
                        .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (canSave) DarkBackground else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Unsaved Changes Confirmation Banner ──
            AnimatedVisibility(visible = draft.showDiscardConfirm) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(YellowSubtle)
                        .border(AlgoTokens.strokeThin, AccentYellow.copy(alpha = 0.55f), RoundedCornerShape(AlgoTokens.radiusSm))
                        .padding(AlgoTokens.space4),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discard unsaved profile changes?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(CardBackground)
                                .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
                                .clickable { draft.showDiscardConfirm = false }
                                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = "Keep Editing",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(AccentRed)
                                .clickable {
                                    draft.showDiscardConfirm = false
                                    onDismiss()
                                }
                                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = "Discard",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── 2. Avatar Preview & Photo Picker Trigger ──
            DoubleBezelShell(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(AlgoTokens.space5)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    ProfileAvatar(
                        avatarUri = draft.avatarUri,
                        displayName = draft.displayName,
                        size = AlgoTokens.avatarHeroSize,
                        shape = CircleShape,
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(CyanSubtle)
                                .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                                .pressPhysics(shape = RoundedCornerShape(AlgoTokens.radiusSm), accent = PrimaryCyan)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                        ) {
                            Text(
                                text = if (draft.avatarUri != null) "Change Photo" else "Choose Photo",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (draft.avatarUri != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                    .background(CardBackgroundElevated)
                                    .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                                    .clickable { draft.avatarUri = null }
                                    .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space2)
                            ) {
                                Text(
                                    text = "Remove Photo",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // ── 3. Profile Identity Fields ──
            DoubleBezelShell(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(AlgoTokens.space5)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
                    ProfileField(
                        label = "Display Name",
                        value = draft.displayName,
                        placeholder = "Duke Ducky",
                        errorText = draft.displayNameError,
                        onValueChange = { draft.displayName = it }
                    )

                    ProfileField(
                        label = "Username Handle",
                        value = draft.handle,
                        placeholder = "@quacky",
                        errorText = draft.handleError,
                        onValueChange = { draft.handle = it }
                    )

                    ProfileField(
                        label = "Role or Study Focus (Optional)",
                        value = draft.roleTitle,
                        placeholder = "CS Student · AVIA Workspace",
                        errorText = draft.roleTitleError,
                        onValueChange = { draft.roleTitle = it }
                    )
                }
            }
        }
    }
}

/**
 * Backwards-compatible alias delegating to [EditProfileSheet].
 */
@Composable
fun EditProfileAndAccountSheet(
    authRepository: AuthRepository,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    EditProfileSheet(
        authRepository = authRepository,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    placeholder: String,
    errorText: String? = null,
    onValueChange: (String) -> Unit
) {
    val borderColor = if (errorText != null) AccentRed else BorderSubtle
    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                .background(CardBackgroundElevated)
                .border(AlgoTokens.strokeThin, borderColor, RoundedCornerShape(AlgoTokens.radiusSm))
                .padding(horizontal = AlgoTokens.space4, vertical = AlgoTokens.space3)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(PrimaryCyan),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = AccentRed
            )
        }
    }
}
