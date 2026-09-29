package com.avia.data.firebase

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.avia.data.AppSettings
import com.avia.data.auth.AuthAccountState
import com.avia.data.auth.AuthProviderType
import com.avia.data.auth.AuthRepository
import com.avia.data.auth.CredentialValidator
import com.avia.data.auth.GuestDataMigrationPolicy
import com.avia.data.auth.ProfileValidator
import com.avia.data.auth.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Production Firebase Authentication repository implementing [AuthRepository].
 *
 * Supports:
 *  - Google Sign-In with ID token exchange
 *  - Anonymous-to-Google account linking (preserving guest data without collisions)
 *  - Email & password fallback authentication
 *  - Offline guest profile customization backed by [AppSettings]
 *  - Automated cloud data synchronization via [FirestoreSyncEngine]
 */
@Stable
class FirebaseAuthRepository(
    private val authProvider: () -> FirebaseAuth? = {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseAuthRepository", "Firebase Auth uninitialized; running offline", e)
            null
        }
    },
    val syncEngine: FirestoreSyncEngine = FirestoreSyncEngine(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
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
        AuthAccountState.SignedOut(initialGuestProfile)
    )
        private set

    override val currentProfile: UserProfile
        get() = when (val s = state) {
            is AuthAccountState.SignedIn -> s.profile
            is AuthAccountState.SignedOut -> s.guestProfile
            is AuthAccountState.Error -> s.previousProfile
            is AuthAccountState.Unavailable -> s.guestProfile
            is AuthAccountState.Loading -> guestProfile
        }

    init {
        val auth = authProvider()
        if (auth == null) {
            state = AuthAccountState.Unavailable(
                reason = "Firebase Auth uninitialized; local guest mode active.",
                guestProfile = initialGuestProfile
            )
        } else {
            try {
                auth.addAuthStateListener { fbAuth ->
                    val user = fbAuth.currentUser
                    if (user != null && !user.isAnonymous) {
                        val profile = UserProfile(
                            uid = user.uid,
                            displayName = user.displayName?.ifBlank { null } ?: guestProfile.displayName,
                            handle = ProfileValidator.normalizeHandle(user.email?.substringBefore("@") ?: guestProfile.handle),
                            roleTitle = guestProfile.roleTitle,
                            email = user.email ?: "",
                            avatarUri = user.photoUrl?.toString() ?: guestProfile.avatarUri,
                            isGuest = false,
                            provider = AuthProviderType.GOOGLE_FIREBASE
                        )
                        state = AuthAccountState.SignedIn(profile)
                    } else {
                        state = AuthAccountState.SignedOut(guestProfile)
                    }
                }
            } catch (e: Exception) {
                Log.w("FirebaseAuthRepository", "Unable to attach auth state listener", e)
            }
        }

        AppSettings.onBookmarksChanged = { ownerId, bookmarks ->
            syncEngine.syncBookmarksAsync(ownerId, bookmarks)
        }
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

        val auth = authProvider()
        if (auth == null) {
            state = AuthAccountState.Unavailable(
                reason = "Firebase Auth backend is not available.",
                guestProfile = guestProfile
            )
            return
        }

        state = AuthAccountState.Loading("Signing in with Google...")

        scope.launch(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val currentUser = auth.currentUser

                val authResult = if (currentUser != null && currentUser.isAnonymous) {
                    try {
                        currentUser.linkWithCredential(credential).await()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Log.i("FirebaseAuthRepository", "Anonymous account collision; signing into existing Google account", e)
                        auth.signInWithCredential(credential).await()
                    }
                } else {
                    auth.signInWithCredential(credential).await()
                }

                val user = authResult.user ?: throw IllegalStateException("Missing Firebase User")
                val resolvedName = (user.displayName?.ifBlank { null }
                    ?: displayName?.ifBlank { null }
                    ?: user.email?.substringBefore("@")
                    ?: guestProfile.displayName).take(36)

                val resolvedEmail = user.email ?: email ?: ""
                val resolvedHandle = ProfileValidator.normalizeHandle(resolvedEmail.substringBefore("@").ifBlank { guestProfile.handle })
                val resolvedAvatar = user.photoUrl?.toString() ?: guestProfile.avatarUri

                val signedInProfile = UserProfile(
                    uid = user.uid,
                    displayName = resolvedName,
                    handle = resolvedHandle,
                    roleTitle = guestProfile.roleTitle,
                    email = resolvedEmail,
                    avatarUri = resolvedAvatar,
                    isGuest = false,
                    provider = AuthProviderType.GOOGLE_FIREBASE
                )

                AppSettings.switchOwner(
                    user.uid,
                    migrateGuestBookmarks = (migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT)
                )

                // Trigger Firestore cloud sync
                syncEngine.performInitialSync(user.uid, signedInProfile, migrationPolicy)

                launch(Dispatchers.Main) {
                    state = AuthAccountState.SignedIn(signedInProfile)
                }
            } catch (e: Exception) {
                Log.e("FirebaseAuthRepository", "Google sign-in failed", e)
                launch(Dispatchers.Main) {
                    state = AuthAccountState.Error(
                        e.localizedMessage ?: "Google sign-in failed.",
                        guestProfile
                    )
                }
            }
        }
    }

    override fun signIn(email: String, password: String) {
        val emailErr = CredentialValidator.validateEmail(email)
        val passErr = CredentialValidator.validatePassword(password)
        val error = emailErr ?: passErr
        if (error != null) {
            state = AuthAccountState.Error(error, guestProfile)
            return
        }

        val auth = authProvider()
        if (auth == null) {
            state = AuthAccountState.Unavailable(
                "Firebase Auth is unavailable offline.",
                guestProfile
            )
            return
        }

        state = AuthAccountState.Loading("Signing in...")
        scope.launch(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
                val user = result.user ?: throw IllegalStateException("Missing Firebase User")
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = user.displayName?.ifBlank { null } ?: user.email?.substringBefore("@") ?: "Operator",
                    handle = ProfileValidator.normalizeHandle(user.email?.substringBefore("@") ?: "@operator"),
                    roleTitle = guestProfile.roleTitle,
                    email = user.email ?: email.trim(),
                    avatarUri = user.photoUrl?.toString() ?: guestProfile.avatarUri,
                    isGuest = false,
                    provider = AuthProviderType.EMAIL_PASSWORD
                )
                AppSettings.switchOwner(
                    user.uid,
                    migrateGuestBookmarks = (migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT)
                )
                syncEngine.performInitialSync(user.uid, profile, migrationPolicy)
                launch(Dispatchers.Main) {
                    state = AuthAccountState.SignedIn(profile)
                }
            } catch (e: Exception) {
                Log.e("FirebaseAuthRepository", "Email sign-in failed", e)
                launch(Dispatchers.Main) {
                    state = AuthAccountState.Error(e.localizedMessage ?: "Sign-in failed.", guestProfile)
                }
            }
        }
    }

    override fun signUp(displayName: String, email: String, password: String) {
        if (displayName.isBlank()) {
            state = AuthAccountState.Error("Display name is required.", guestProfile)
            return
        }
        val emailErr = CredentialValidator.validateEmail(email)
        val passErr = CredentialValidator.validatePassword(password)
        val error = emailErr ?: passErr
        if (error != null) {
            state = AuthAccountState.Error(error, guestProfile)
            return
        }

        val auth = authProvider()
        if (auth == null) {
            state = AuthAccountState.Unavailable(
                "Firebase Auth is unavailable offline.",
                guestProfile
            )
            return
        }

        state = AuthAccountState.Loading("Creating account...")
        scope.launch(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val user = result.user ?: throw IllegalStateException("Missing Firebase User")
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = displayName.trim().take(36),
                    handle = ProfileValidator.normalizeHandle(email.trim().substringBefore("@")),
                    roleTitle = guestProfile.roleTitle,
                    email = email.trim().lowercase(),
                    avatarUri = guestProfile.avatarUri,
                    isGuest = false,
                    provider = AuthProviderType.EMAIL_PASSWORD
                )
                AppSettings.switchOwner(
                    user.uid,
                    migrateGuestBookmarks = (migrationPolicy == GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT)
                )
                syncEngine.performInitialSync(user.uid, profile, migrationPolicy)
                launch(Dispatchers.Main) {
                    state = AuthAccountState.SignedIn(profile)
                }
            } catch (e: Exception) {
                Log.e("FirebaseAuthRepository", "Account creation failed", e)
                launch(Dispatchers.Main) {
                    state = AuthAccountState.Error(e.localizedMessage ?: "Sign-up failed.", guestProfile)
                }
            }
        }
    }

    override fun signOut() {
        val auth = authProvider()
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w("FirebaseAuthRepository", "Error during sign out", e)
        }
        AppSettings.switchOwner("guest", false)
        state = AuthAccountState.SignedOut(guestProfile)
    }

    override fun updateAvatar(avatarUri: String?) {
        when (val s = state) {
            is AuthAccountState.SignedIn -> {
                val updated = s.profile.copy(avatarUri = avatarUri)
                state = AuthAccountState.SignedIn(updated)
                scope.launch(Dispatchers.IO) {
                    syncEngine.syncProfile(updated.uid, updated)
                }
            }
            else -> {
                AppSettings.guestAvatarUri = avatarUri
                guestProfile = guestProfile.copy(avatarUri = avatarUri)
                if (state !is AuthAccountState.Loading) {
                    state = AuthAccountState.SignedOut(guestProfile)
                }
            }
        }
    }

    override fun updateDisplayName(displayName: String) {
        val trimmed = displayName.trim()
        if (trimmed.isEmpty()) return
        val safeName = trimmed.take(36)
        when (val s = state) {
            is AuthAccountState.SignedIn -> {
                val updated = s.profile.copy(displayName = safeName)
                state = AuthAccountState.SignedIn(updated)
                scope.launch(Dispatchers.IO) {
                    syncEngine.syncProfile(updated.uid, updated)
                }
            }
            else -> {
                AppSettings.guestDisplayName = safeName
                guestProfile = guestProfile.copy(displayName = safeName)
                if (state !is AuthAccountState.Loading) {
                    state = AuthAccountState.SignedOut(guestProfile)
                }
            }
        }
    }

    override fun updateProfile(displayName: String, handle: String, roleTitle: String) {
        val safeName = displayName.trim().ifEmpty { currentProfile.displayName }.take(36)
        val safeHandle = ProfileValidator.normalizeHandle(handle.ifEmpty { currentProfile.handle })
        val safeRole = roleTitle.trim().take(48)
        when (val s = state) {
            is AuthAccountState.SignedIn -> {
                val updated = s.profile.copy(
                    displayName = safeName,
                    handle = safeHandle,
                    roleTitle = safeRole
                )
                state = AuthAccountState.SignedIn(updated)
                scope.launch(Dispatchers.IO) {
                    syncEngine.syncProfile(updated.uid, updated)
                }
            }
            else -> {
                AppSettings.guestDisplayName = safeName
                AppSettings.guestHandle = safeHandle
                AppSettings.guestRoleTitle = safeRole
                guestProfile = guestProfile.copy(
                    displayName = safeName,
                    handle = safeHandle,
                    roleTitle = safeRole
                )
                if (state !is AuthAccountState.Loading) {
                    state = AuthAccountState.SignedOut(guestProfile)
                }
            }
        }
    }

    override fun changeEmail(currentPassword: String, newEmail: String) {
        state = AuthAccountState.Error("Email change requires re-authentication.", currentProfile)
    }

    override fun changePassword(currentPassword: String, newPassword: String) {
        state = AuthAccountState.Error("Password change requires re-authentication.", currentProfile)
    }

    override fun clearNotice() {
        if (state is AuthAccountState.Error) {
            state = AuthAccountState.SignedOut(guestProfile)
        }
    }
}
