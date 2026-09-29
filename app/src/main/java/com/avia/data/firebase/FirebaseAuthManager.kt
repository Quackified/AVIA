package com.avia.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Production Firebase Authentication manager with resilient offline/unconfigured fallback.
 */
class FirebaseAuthManager(
    private val authProvider: () -> FirebaseAuth? = {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Firebase Auth uninitialized; using guest mode", e)
            null
        }
    }
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Guest)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override val currentUserId: String?
        get() = try {
            authProvider()?.currentUser?.uid
        } catch (_: Exception) {
            null
        }

    init {
        try {
            val auth = authProvider()
            if (auth != null) {
                auth.addAuthStateListener { fbAuth ->
                    val user = fbAuth.currentUser
                    _authState.value = when {
                        user == null -> AuthState.Guest
                        user.isAnonymous -> AuthState.Anonymous(user.uid)
                        else -> AuthState.Authenticated(user.uid, user.email, user.displayName)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Unable to attach AuthStateListener", e)
        }
    }

    override suspend fun signInAnonymously(): Result<String> {
        val auth = authProvider() ?: return Result.failure(IllegalStateException("Firebase Auth unavailable"))
        return try {
            val result = auth.signInAnonymously().await()
            val uid = result.user?.uid ?: throw IllegalStateException("Missing user UID")
            _authState.value = AuthState.Anonymous(uid)
            Result.success(uid)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Anonymous sign-in failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        val auth = authProvider() ?: return Result.success(Unit)
        return try {
            auth.signOut()
            _authState.value = AuthState.Guest
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
