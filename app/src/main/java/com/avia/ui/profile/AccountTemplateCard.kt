package com.avia.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avia.data.auth.*
import com.avia.ui.components.AlgoGlyphs
import com.avia.ui.components.DoubleBezelShell
import com.avia.ui.components.pressPhysics
import com.avia.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** A draft stays separate from persisted identity until Save is explicitly chosen. */
@Stable
class ProfileEditorDraft(private val initialProfile: UserProfile) {
    var displayName by mutableStateOf(initialProfile.displayName)
    var handle by mutableStateOf(initialProfile.handle)
    var roleTitle by mutableStateOf(initialProfile.roleTitle)
    var avatarUri by mutableStateOf(initialProfile.avatarUri)
    var showDiscardConfirm by mutableStateOf(false)

    val displayNameError: String? get() = ProfileValidator.validateDisplayName(displayName)
    val handleError: String? get() = ProfileValidator.validateHandle(handle)
    val roleTitleError: String? get() = ProfileValidator.validateRoleTitle(roleTitle)
    val isValid: Boolean get() = displayNameError == null && handleError == null && roleTitleError == null
    val hasUnsavedChanges: Boolean
        get() = displayName.trim() != initialProfile.displayName.trim() ||
            ProfileValidator.normalizeHandle(handle) != ProfileValidator.normalizeHandle(initialProfile.handle) ||
            roleTitle.trim() != initialProfile.roleTitle.trim() || avatarUri != initialProfile.avatarUri

    fun copy(
        displayName: String = this.displayName,
        handle: String = this.handle,
        roleTitle: String = this.roleTitle,
        avatarUri: String? = this.avatarUri
    ): ProfileEditorDraft = ProfileEditorDraft(initialProfile).also {
        it.displayName = displayName
        it.handle = handle
        it.roleTitle = roleTitle
        it.avatarUri = avatarUri
    }

    fun saveTo(authRepository: AuthRepository): Boolean {
        if (!isValid) return false
        authRepository.updateProfile(displayName.trim(), ProfileValidator.normalizeHandle(handle), roleTitle.trim())
        authRepository.updateAvatar(avatarUri)
        showDiscardConfirm = false
        return true
    }

    fun commitTo(authRepository: AuthRepository): Boolean = saveTo(authRepository)

    fun requestCancel(): Boolean {
        if (!hasUnsavedChanges) return true
        showDiscardConfirm = true
        return false
    }

    companion object {
        fun from(profile: UserProfile): ProfileEditorDraft = ProfileEditorDraft(profile)

        fun saver(profile: UserProfile) = listSaver<ProfileEditorDraft, Any>(
            save = { listOf(it.displayName, it.handle, it.roleTitle, it.avatarUri.orEmpty(), it.showDiscardConfirm) },
            restore = { values ->
                ProfileEditorDraft(profile).also {
                    it.displayName = values[0] as String
                    it.handle = values[1] as String
                    it.roleTitle = values[2] as String
                    it.avatarUri = (values[3] as String).ifEmpty { null }
                    it.showDiscardConfirm = values[4] as Boolean
                }
            }
        )
    }
}

@Composable
fun ProfileAvatar(
    avatarUri: String?,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = AlgoTokens.avatarHeroSize,
    shape: Shape = CircleShape,
    onClick: (() -> Unit)? = null,
    actionLabel: String = "Change profile photo"
) {
    val context = LocalContext.current
    // Photo-picker images can be very large. Decode a sampled thumbnail off the UI thread.
    val decoded by produceState<ImageBitmap?>(null, avatarUri) {
        value = null
        value = withContext(Dispatchers.IO) {
            if (avatarUri.isNullOrBlank()) null else runCatching {
                val uri = avatarUri.toUri()
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
    val initials = remember(displayName) {
        displayName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            .take(2).joinToString("") { it.take(1) }.uppercase().ifEmpty { "AV" }
    }
    Box(
        modifier.size(size).clip(shape).background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderCyan, shape)
            .then(if (onClick != null) Modifier.pressPhysics(shape = shape)
                .clickable(role = Role.Button, onClickLabel = actionLabel, onClick = onClick)
                .semantics { contentDescription = actionLabel } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        val bitmap = decoded
        if (bitmap != null) {
            Image(bitmap, if (onClick == null) "Profile photo for $displayName" else null,
                Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Text(initials, style = MaterialTheme.typography.headlineMedium,
                color = PrimaryCyan, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AccountStatusCard(
    authRepository: AuthRepository,
    modifier: Modifier = Modifier,
    onNotice: ((String) -> Unit)? = null
) {
    val accountState = authRepository.state
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSigningIn by remember { mutableStateOf(false) }

    DoubleBezelShell(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AlgoTokens.space4)
    ) {
        when (accountState) {
            is AuthAccountState.Loading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AlgoTokens.space2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = PrimaryCyan,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = accountState.operation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
            is AuthAccountState.SignedIn -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                )
                                Text(
                                    text = "SIGNED IN · ${accountState.profile.provider?.displayName ?: "Google Account"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = AlgoType.trackSection
                                )
                            }
                            Text(
                                text = accountState.profile.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                                .background(RedSubtle)
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

                    // Cloud Sync Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(DarkBackground)
                            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = AlgoGlyphs.Cloud,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Cloud Sync Active · Firestore Auto-Sync Enabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                ) {
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
                                text = "Local Account",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Bookmarks and study activity are saved on this device. Sign in with Google to sync across devices.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    // Sign In With Google Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CyanSubtle)
                            .border(AlgoTokens.strokeThin, BorderCyan, RoundedCornerShape(AlgoTokens.radiusSm))
                            .clickable(enabled = !isSigningIn) {
                                isSigningIn = true
                                coroutineScope.launch {
                                    val result = GoogleSignInHelper.launchGoogleSignIn(context)
                                    isSigningIn = false
                                    when (result) {
                                        is GoogleSignInResult.Success -> {
                                            authRepository.signInWithGoogleIdToken(
                                                idToken = result.credentials.idToken,
                                                email = result.credentials.email,
                                                displayName = result.credentials.displayName
                                            )
                                        }
                                        is GoogleSignInResult.Failure -> {
                                            onNotice?.invoke(result.message)
                                        }
                                        is GoogleSignInResult.Cancelled -> {
                                            // User dismissed prompt
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
                        ) {
                            if (isSigningIn) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = PrimaryCyan,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Connecting to Google...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = AlgoGlyphs.Globe,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Sign in with Google",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Full-screen editor. Kept under the existing name for call-site compatibility. */
@Composable
fun EditProfileSheet(authRepository: AuthRepository, onDismiss: () -> Unit, modifier: Modifier = Modifier) = ProfileTheme {
    val context = LocalContext.current
    val focus = LocalFocusManager.current
    val profile = authRepository.currentProfile
    val draft = rememberSaveable(profile.uid, saver = ProfileEditorDraft.saver(profile)) { ProfileEditorDraft(profile) }
    var photoNotice by rememberSaveable { mutableStateOf<String?>(null) }
    var saveError by rememberSaveable { mutableStateOf<String?>(null) }
    val canSave = draft.isValid && draft.hasUnsavedChanges && authRepository.state !is AuthAccountState.Loading
    val save: () -> Unit = {
        if (canSave) {
            runCatching { draft.saveTo(authRepository) }.onSuccess { saved ->
                if (saved) { focus.clearFocus(); onDismiss() }
            }.onFailure { saveError = "Your changes could not be saved. Please try again." }
        }
    }
    val cancel: () -> Unit = { if (draft.requestCancel()) onDismiss() }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val permission = runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(stream, null, bounds)
                    check(bounds.outWidth > 0 && bounds.outHeight > 0)
                } ?: error("Unreadable photo")
            }
            if (permission.isSuccess) {
                draft.avatarUri = uri.toString()
                photoNotice = null
            } else {
                photoNotice = "This photo could not be opened. Please choose another image."
            }
        }
    }
    val choosePhoto: () -> Unit = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    BackHandler(onBack = cancel)

    Column(
        modifier.fillMaxSize().background(CanvasBackground).statusBarsPadding()
            .navigationBarsPadding().imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileTopBar("Edit Profile", onBack = cancel, trailing = {
            TextButton(onClick = save, enabled = canSave, contentPadding = PaddingValues(AlgoTokens.space2)) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }
        })
        Column(
            Modifier.widthIn(max = AlgoTokens.profileContentMaxWidth).fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AlgoTokens.space7, vertical = AlgoTokens.space6),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space7)
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    ProfileAvatar(draft.avatarUri, draft.displayName, size = AlgoTokens.profileAvatarSize,
                        onClick = choosePhoto)
                    Icon(AlgoGlyphs.EditPencil, null,
                        Modifier.align(Alignment.BottomEnd).clip(CircleShape).background(PrimaryCyan)
                            .padding(AlgoTokens.space3).size(AlgoTokens.inlineIconLg), tint = CanvasBackground)
                }
                TextButton(onClick = choosePhoto) { Text(if (draft.avatarUri == null) "Choose photo" else "Change photo") }
                if (draft.avatarUri != null) {
                    TextButton(onClick = { draft.avatarUri = null; photoNotice = null }) {
                        Text("Remove photo", color = TextSecondary)
                    }
                }
                photoNotice?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = AccentRed) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space6)) {
                ProfileField("Display name", draft.displayName, "Your name", draft.displayNameError,
                    hint = "Up to 36 characters", capitalization = KeyboardCapitalization.Words,
                    onNext = { focus.moveFocus(FocusDirection.Down) }) { draft.displayName = it }
                ProfileField("Username", draft.handle, "@username", draft.handleError,
                    hint = "2–23 letters, numbers, dots or underscores",
                    onNext = { focus.moveFocus(FocusDirection.Down) }) { draft.handle = it }
                ProfileField("Study focus", draft.roleTitle, "What are you learning?", draft.roleTitleError,
                    hint = "Optional · up to 48 characters", capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done, onNext = { focus.clearFocus(); save() }) { draft.roleTitle = it }
            }
            Text(if (profile.isGuest) "This is your local AVIA profile. Changes are saved on this device."
                else "Your profile details are managed by your account.",
                style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            saveError?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = AccentRed) }
        }
    }
    if (draft.showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { draft.showDiscardConfirm = false },
            containerColor = CardBackground,
            titleContentColor = TextPrimary,
            textContentColor = TextPrimary,
            title = { Text("Discard changes?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Your profile has unsaved changes.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Discard", color = AccentRed, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { draft.showDiscardConfirm = false }) { Text("Keep editing", color = TextSecondary) } }
        )
    }
}

@Composable
fun EditProfileAndAccountSheet(authRepository: AuthRepository, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    EditProfileSheet(authRepository, onDismiss, modifier)
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    placeholder: String,
    errorText: String?,
    hint: String,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = TextPrimary)
        TextField(
            value = value, onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
            placeholder = { Text(placeholder, color = TextSecondary) },
            singleLine = true, isError = errorText != null,
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(AlgoTokens.radiusMd),
            keyboardOptions = KeyboardOptions(capitalization = capitalization, autoCorrectEnabled = false, imeAction = imeAction),
            keyboardActions = KeyboardActions(onNext = { onNext() }, onDone = { onNext() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardBackground, unfocusedContainerColor = CardBackground,
                errorContainerColor = CardBackground,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = PrimaryCyan, unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryCyan
            )
        )
        Text(errorText ?: hint, style = MaterialTheme.typography.bodySmall,
            color = if (errorText != null) AccentRed else TextSecondary)
    }
}
