package com.avia.data.firebase

import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication state representation in AVIA.
 */
sealed class AuthState {
    data object Guest : AuthState()
    data class Anonymous(val uid: String) : AuthState()
    data class Authenticated(val uid: String, val email: String?, val displayName: String?) : AuthState()
}

/**
 * Core authentication repository contract for cloud sync and profile management.
 */
interface AuthRepository {
    val authState: StateFlow<AuthState>
    val currentUserId: String?

    suspend fun signInAnonymously(): Result<String>
    suspend fun signOut(): Result<Unit>
}
