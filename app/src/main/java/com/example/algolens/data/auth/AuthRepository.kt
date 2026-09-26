package com.example.algolens.data.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.algolens.data.AppSettings
import com.example.algolens.data.chat.ChatHistoryRepository

/**
 * Explicit account state machine for AlgoLens identity and Firebase Auth template integration.
 */
sealed interface AuthAccountState {
    data class SignedOut(val guestProfile: UserProfile) : AuthAccountState
    data class Loading(val operation: String) : AuthAccountState
    data class SignedIn(val profile: UserProfile) : AuthAccountState
    data class Error(
        val message: String,
        val previousProfile: UserProfile
    ) : AuthAccountState
    data class Unavailable(
        val reason: String,
        val guestProfile: UserProfile
    ) : AuthAccountState
}

/**
 * Supported authentication provider types for future Firebase Authentication integration.
 */
enum class AuthProviderType(val displayName: String) {
    GOOGLE_FIREBASE("Google Sign-In (Firebase Auth)"),
    EMAIL_PASSWORD("Email & Password")
}

/**
 * User identity profile model. Never stores passwords or sensitive credentials.
 */
data class UserProfile(
    val uid: String = ChatHistoryRepository.DEFAULT_GUEST_OWNER,
    val displayName: String = "Duke Ducky",
    val handle: String = "@quacky",
    val roleTitle: String = "CS Student · AVIA Workspace",
    val email: String = "guest@local.algolens",
    val avatarUri: String? = null,
    val isGuest: Boolean = true,
    val provider: AuthProviderType? = null
)

/**
 * Validation helper for Edit Profile inputs (`displayName`, `handle`, `roleTitle`).
 */
object ProfileValidator {
    private val HANDLE_BODY_REGEX = Regex("^[A-Za-z0-9_.]{2,23}$")

    fun validateDisplayName(displayName: String): String? {
        val trimmed = displayName.trim()
        return when {
            trimmed.isEmpty() -> "Display name is required."
            trimmed.length > 36 -> "Display name must be 36 characters or fewer."
            else -> null
        }
    }

    fun validateHandle(handle: String): String? {
        val body = handle.trim().removePrefix("@")
        return when {
            body.isEmpty() -> "Handle is required (e.g., @quacky)."
            !HANDLE_BODY_REGEX.matches(body) -> "Handle may use 2–23 letters, numbers, underscores, or dots."
            else -> null
        }
    }

    fun normalizeHandle(handle: String): String {
        val body = handle.trim().removePrefix("@").take(23)
        return if (body.isEmpty()) "@guest" else "@$body"
    }

    fun validateRoleTitle(roleTitle: String): String? {
        val trimmed = roleTitle.trim()
        return if (trimmed.length > 48) {
            "Role or bio must be 48 characters or fewer."
        } else {
            null
        }
    }
}

/**
 * Policy governing what happens to local Guest bookmarks and chat history when signing in.
 */
enum class GuestDataMigrationPolicy(val label: String, val description: String) {
    MERGE_GUEST_TO_ACCOUNT(
        label = "Merge Guest Data into Account",
        description = "Copy local Guest bookmarks and chat threads into the signed-in account."
    ),
    KEEP_SEPARATE(
        label = "Keep Guest & Account Separate",
        description = "Start with a clean account workspace while preserving Guest data for sign-out."
    )
}

/**
 * Strict client-side credential validator. Never persists passwords.
 */
object CredentialValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "Email address is required."
            !EMAIL_REGEX.matches(trimmed) -> "Enter a valid email address (e.g., operator@domain.com)."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters long."
            !password.any { it.isDigit() } -> "Password must include at least one digit."
            !password.any { it.isLetter() } -> "Password must include at least one letter."
            else -> null
        }
    }

    fun validateReauthPassword(currentPassword: String): String? {
        return if (currentPassword.isBlank()) {
            "Current password is required to re-authenticate before changing email or password."
        } else {
            null
        }
    }
}

/**
 * Provider-neutral contract for authentication and profile operations.
 * Designed for future Firebase Authentication (including Google Sign-In via ID token)
 * while keeping local Guest profile editing fully offline.
 */
interface AuthRepository {
    val state: AuthAccountState
    val currentProfile: UserProfile
    val migrationPolicy: GuestDataMigrationPolicy

    fun setGuestMigrationPolicy(policy: GuestDataMigrationPolicy)
    fun signInWithGoogleIdToken(
        idToken: String,
        email: String? = null,
        displayName: String? = null
    )
    fun signIn(email: String, password: String)
    fun signUp(displayName: String, email: String, password: String)
    fun signOut()
    fun updateAvatar(avatarUri: String?)
    fun updateDisplayName(displayName: String)
    fun updateProfile(displayName: String, handle: String, roleTitle: String)
    fun changeEmail(currentPassword: String, newEmail: String)
    fun changePassword(currentPassword: String, newPassword: String)
    fun clearNotice()
}

/**
 * Default repository used when Firebase (`google-services.json` / Firebase Auth SDK) is not configured.
 *
 * Guarantees:
 *  - Never stores passwords locally or in SharedPreferences.
 *  - Never claims authentication succeeded when Firebase is absent.
 *  - Supports local Guest profile customization (display name, handle, role/bio, and photo-picker avatar URI)
 *    persisted locally through [AppSettings].
 */
@Stable
class UnavailableFirebaseAuthRepository(
    initialGuestProfile: UserProfile = UserProfile(
        displayName = AppSettings.guestDisplayName,
        handle = AppSettings.guestHandle,
        roleTitle = AppSettings.guestRoleTitle,
        avatarUri = AppSettings.guestAvatarUri
    )
) : AuthRepository {

    private var guestProfile by mutableStateOf(initialGuestProfile)

    override var migrationPolicy by mutableStateOf(GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT)
        private set

    override var state by mutableStateOf<AuthAccountState>(
        AuthAccountState.Unavailable(
            reason = UNAVAILABLE_REASON,
            guestProfile = initialGuestProfile
        )
    )
        private set

    override val currentProfile: UserProfile
        get() = guestProfile

    override fun setGuestMigrationPolicy(policy: GuestDataMigrationPolicy) {
        migrationPolicy = policy
    }

    override fun signInWithGoogleIdToken(
        idToken: String,
        email: String?,
        displayName: String?
    ) {
        if (idToken.isBlank()) {
            state = AuthAccountState.Error("Google ID token is required.", guestProfile)
            return
        }
        state = AuthAccountState.Unavailable(
            reason = "Google Sign-In via Firebase Authentication is not enabled in this offline build; continuing as Local Guest.",
            guestProfile = guestProfile
        )
    }

    override fun signIn(email: String, password: String) {
        val emailErr = CredentialValidator.validateEmail(email)
        val passErr = CredentialValidator.validatePassword(password)
        val error = emailErr ?: passErr
        if (error != null) {
            state = AuthAccountState.Error(error, guestProfile)
            return
        }
        state = AuthAccountState.Unavailable(
            reason = "Firebase Auth backend is not configured in this build. Sign-in for '${email.trim()}' cannot complete offline; continuing as Local Guest.",
            guestProfile = guestProfile
        )
    }

    override fun signUp(displayName: String, email: String, password: String) {
        if (displayName.isBlank()) {
            state = AuthAccountState.Error("Display name is required for account creation.", guestProfile)
            return
        }
        val emailErr = CredentialValidator.validateEmail(email)
        val passErr = CredentialValidator.validatePassword(password)
        val error = emailErr ?: passErr
        if (error != null) {
            state = AuthAccountState.Error(error, guestProfile)
            return
        }
        state = AuthAccountState.Unavailable(
            reason = "Firebase Auth backend is not configured in this build. Account registration is unavailable offline; continuing as Local Guest.",
            guestProfile = guestProfile
        )
    }

    override fun signOut() {
        state = AuthAccountState.SignedOut(guestProfile)
    }

    override fun updateAvatar(avatarUri: String?) {
        AppSettings.guestAvatarUri = avatarUri
        guestProfile = guestProfile.copy(avatarUri = avatarUri)
        state = AuthAccountState.Unavailable(UNAVAILABLE_REASON, guestProfile)
    }

    override fun updateDisplayName(displayName: String) {
        val trimmed = displayName.trim()
        if (trimmed.isEmpty()) return
        val safeName = trimmed.take(36)
        AppSettings.guestDisplayName = safeName
        guestProfile = guestProfile.copy(displayName = safeName)
        state = AuthAccountState.Unavailable(UNAVAILABLE_REASON, guestProfile)
    }

    override fun updateProfile(displayName: String, handle: String, roleTitle: String) {
        val safeName = displayName.trim().ifEmpty { guestProfile.displayName }.take(36)
        val safeHandle = ProfileValidator.normalizeHandle(handle.ifEmpty { guestProfile.handle })
        val safeRole = roleTitle.trim().take(48)
        AppSettings.guestDisplayName = safeName
        AppSettings.guestHandle = safeHandle
        AppSettings.guestRoleTitle = safeRole
        guestProfile = guestProfile.copy(
            displayName = safeName,
            handle = safeHandle,
            roleTitle = safeRole
        )
        state = AuthAccountState.Unavailable(UNAVAILABLE_REASON, guestProfile)
    }

    override fun changeEmail(currentPassword: String, newEmail: String) {
        val reauthErr = CredentialValidator.validateReauthPassword(currentPassword)
        val emailErr = CredentialValidator.validateEmail(newEmail)
        val error = reauthErr ?: emailErr
        if (error != null) {
            state = AuthAccountState.Error(error, guestProfile)
            return
        }
        state = AuthAccountState.Unavailable(
            reason = "Email change requires Firebase Auth re-authentication, which is not configured in this offline build.",
            guestProfile = guestProfile
        )
    }

    override fun changePassword(currentPassword: String, newPassword: String) {
        val reauthErr = CredentialValidator.validateReauthPassword(currentPassword)
        val passErr = CredentialValidator.validatePassword(newPassword)
        val error = reauthErr ?: passErr
        if (error != null) {
            state = AuthAccountState.Error(error, guestProfile)
            return
        }
        state = AuthAccountState.Unavailable(
            reason = "Password change requires Firebase Auth re-authentication, which is not configured in this offline build.",
            guestProfile = guestProfile
        )
    }

    override fun clearNotice() {
        state = AuthAccountState.Unavailable(UNAVAILABLE_REASON, guestProfile)
    }

    companion object {
        const val UNAVAILABLE_REASON =
            "Account sign-in (Google Sign-In via Firebase Auth) is not enabled in this offline build. " +
                "Your profile, avatar, bookmarks, and chat history are saved locally on this device."
    }
}

/**
 * Deterministic in-memory implementation of [AuthRepository] for testing all state transitions
 * (`SignedOut`, `SignedIn`, `Error`), re-authentication validation, guest-to-account migration,
 * and cross-account isolation (`userA` vs `userB` vs `guest`).
 */
@Stable
class FakeAuthRepository(
    private val onOwnerChanged: (newOwnerId: String, migrateGuestData: Boolean) -> Unit = { ownerId, migrate ->
        AppSettings.switchOwner(ownerId, migrate)
    }
) : AuthRepository {

    private var guestProfile = UserProfile()
    private val registeredUsers = mutableMapOf<String, UserProfile>()

    override var migrationPolicy by mutableStateOf(GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT)
        private set

    override var state by mutableStateOf<AuthAccountState>(AuthAccountState.SignedOut(guestProfile))
        private set

    override val currentProfile: UserProfile
        get() = when (val s = state) {
            is AuthAccountState.SignedIn -> s.profile
            is AuthAccountState.SignedOut -> s.guestProfile
            is AuthAccountState.Error -> s.previousProfile
            is AuthAccountState.Unavailable -> s.guestProfile
            is AuthAccountState.Loading -> guestProfile
        }

    override fun setGuestMigrationPolicy(policy: GuestDataMigrationPolicy) {
        migrationPolicy = policy
    }

    override fun signInWithGoogleIdToken(
        idToken: String,
        email: String?,
        displayName: String?
    ) {
        if (idToken.isBlank()) {
            state = AuthAccountState.Error("Google ID token is required.", currentProfile)
            return
        }
        val resolvedEmail = (email?.trim()?.ifEmpty { null } ?: "google_${idToken.take(8)}@gmail.com").lowercase()
        val uid = "uid_google_${resolvedEmail.replace(Regex("[^a-z0-9]"), "_")}"
        val resolvedName = displayName?.trim()?.ifEmpty { null } ?: resolvedEmail.substringBefore("@")
        val profile = registeredUsers.getOrPut(resolvedEmail) {
            UserProfile(
                uid = uid,
                displayName = resolvedName,
                handle = ProfileValidator.normalizeHandle(resolvedEmail.substringBefore("@")),
                roleTitle = guestProfile.roleTitle,
                email = resolvedEmail,
                avatarUri = guestProfile.avatarUri,
                isGuest = false,
                provider = AuthProviderType.GOOGLE_FIREBASE
            )
        }
        val shouldMigrate = migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT
        onOwnerChanged(profile.uid, shouldMigrate)
        state = AuthAccountState.SignedIn(profile)
    }

    override fun signUp(displayName: String, email: String, password: String) {
        val emailErr = CredentialValidator.validateEmail(email)
        val passErr = CredentialValidator.validatePassword(password)
        if (displayName.isBlank() || emailErr != null || passErr != null) {
            state = AuthAccountState.Error(
                message = emailErr ?: passErr ?: "Display name is required.",
                previousProfile = currentProfile
            )
            return
        }
        val uid = "uid_${email.trim().lowercase().replace(Regex("[^a-z0-9]"), "_")}"
        val profile = UserProfile(
            uid = uid,
            displayName = displayName.trim(),
            handle = "@${email.trim().substringBefore("@").lowercase()}",
            roleTitle = guestProfile.roleTitle,
            email = email.trim().lowercase(),
            avatarUri = guestProfile.avatarUri,
            isGuest = false
        )
        registeredUsers[email.trim().lowercase()] = profile
        val shouldMigrate = migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT
        onOwnerChanged(uid, shouldMigrate)
        state = AuthAccountState.SignedIn(profile)
    }

    override fun signIn(email: String, password: String) {
        val emailErr = CredentialValidator.validateEmail(email)
        val passErr = CredentialValidator.validatePassword(password)
        if (emailErr != null || passErr != null) {
            state = AuthAccountState.Error(emailErr ?: passErr!!, currentProfile)
            return
        }
        val normalizedEmail = email.trim().lowercase()
        val uid = "uid_${normalizedEmail.replace(Regex("[^a-z0-9]"), "_")}"
        val profile = registeredUsers.getOrPut(normalizedEmail) {
            UserProfile(
                uid = uid,
                displayName = normalizedEmail.substringBefore("@"),
                handle = "@${normalizedEmail.substringBefore("@")}",
                roleTitle = guestProfile.roleTitle,
                email = normalizedEmail,
                isGuest = false
            )
        }
        val shouldMigrate = migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT
        onOwnerChanged(profile.uid, shouldMigrate)
        state = AuthAccountState.SignedIn(profile)
    }

    override fun signOut() {
        onOwnerChanged(ChatHistoryRepository.DEFAULT_GUEST_OWNER, false)
        state = AuthAccountState.SignedOut(guestProfile)
    }

    override fun updateAvatar(avatarUri: String?) {
        when (val s = state) {
            is AuthAccountState.SignedIn -> {
                val updated = s.profile.copy(avatarUri = avatarUri)
                registeredUsers[updated.email] = updated
                state = AuthAccountState.SignedIn(updated)
            }
            else -> {
                guestProfile = guestProfile.copy(avatarUri = avatarUri)
                state = AuthAccountState.SignedOut(guestProfile)
            }
        }
    }

    override fun updateDisplayName(displayName: String) {
        val trimmed = displayName.trim()
        if (trimmed.isEmpty()) return
        when (val s = state) {
            is AuthAccountState.SignedIn -> {
                val updated = s.profile.copy(displayName = trimmed)
                registeredUsers[updated.email] = updated
                state = AuthAccountState.SignedIn(updated)
            }
            else -> {
                guestProfile = guestProfile.copy(displayName = trimmed)
                state = AuthAccountState.SignedOut(guestProfile)
            }
        }
    }

    override fun updateProfile(displayName: String, handle: String, roleTitle: String) {
        val safeName = displayName.trim().ifEmpty { currentProfile.displayName }.take(36)
        val rawHandle = handle.trim().ifEmpty { currentProfile.handle }.take(24)
        val safeHandle = if (rawHandle.startsWith("@")) rawHandle else "@$rawHandle"
        val safeRole = roleTitle.trim().take(48)
        when (val s = state) {
            is AuthAccountState.SignedIn -> {
                val updated = s.profile.copy(
                    displayName = safeName,
                    handle = safeHandle,
                    roleTitle = safeRole
                )
                registeredUsers[updated.email] = updated
                state = AuthAccountState.SignedIn(updated)
            }
            else -> {
                guestProfile = guestProfile.copy(
                    displayName = safeName,
                    handle = safeHandle,
                    roleTitle = safeRole
                )
                state = AuthAccountState.SignedOut(guestProfile)
            }
        }
    }

    override fun changeEmail(currentPassword: String, newEmail: String) {
        val reauthErr = CredentialValidator.validateReauthPassword(currentPassword)
        val emailErr = CredentialValidator.validateEmail(newEmail)
        if (reauthErr != null || emailErr != null) {
            state = AuthAccountState.Error(reauthErr ?: emailErr!!, currentProfile)
            return
        }
        val s = state as? AuthAccountState.SignedIn ?: run {
            state = AuthAccountState.Error("Sign in first to change account email.", currentProfile)
            return
        }
        val updated = s.profile.copy(email = newEmail.trim().lowercase())
        state = AuthAccountState.SignedIn(updated)
    }

    override fun changePassword(currentPassword: String, newPassword: String) {
        val reauthErr = CredentialValidator.validateReauthPassword(currentPassword)
        val passErr = CredentialValidator.validatePassword(newPassword)
        if (reauthErr != null || passErr != null) {
            state = AuthAccountState.Error(reauthErr ?: passErr!!, currentProfile)
            return
        }
    }

    override fun clearNotice() {
        if (state is AuthAccountState.Error) {
            state = AuthAccountState.SignedOut(guestProfile)
        }
    }
}
