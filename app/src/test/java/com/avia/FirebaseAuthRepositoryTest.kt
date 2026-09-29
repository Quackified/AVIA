package com.avia

import com.avia.data.AppSettings
import com.avia.data.auth.AuthAccountState
import com.avia.data.auth.GuestDataMigrationPolicy
import com.avia.data.auth.UserProfile
import com.avia.data.firebase.FirebaseAuthRepository
import com.avia.data.firebase.FirestoreSyncEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirebaseAuthRepositoryTest {

    private lateinit var repository: FirebaseAuthRepository

    @Before
    fun setUp() {
        AppSettings.clearAllSavedData()
        repository = FirebaseAuthRepository(
            authProvider = { null },
            syncEngine = FirestoreSyncEngine(firestoreProvider = { null }),
            initialGuestProfile = UserProfile(
                displayName = "Test Duck",
                handle = "@testduck",
                roleTitle = "CS Learner",
                avatarUri = null
            )
        )
    }

    @Test
    fun uninitializedAuthSetsUnavailableState() {
        assertTrue(
            "State should be Unavailable when FirebaseAuth is null",
            repository.state is AuthAccountState.Unavailable
        )
        assertEquals("Test Duck", repository.currentProfile.displayName)
        assertEquals("@testduck", repository.currentProfile.handle)
    }

    @Test
    fun emptyGoogleIdTokenYieldsError() {
        repository.signInWithGoogleIdToken("")
        val state = repository.state
        assertTrue("State should be Error when ID token is empty", state is AuthAccountState.Error)
        assertEquals("Google ID token is required.", (state as AuthAccountState.Error).message)
    }

    @Test
    fun guestMigrationPolicyCanBeUpdated() {
        assertEquals(GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT, repository.migrationPolicy)
        repository.setGuestMigrationPolicy(GuestDataMigrationPolicy.KEEP_SEPARATE)
        assertEquals(GuestDataMigrationPolicy.KEEP_SEPARATE, repository.migrationPolicy)
    }

    @Test
    fun localProfileUpdatesPropagateToGuestProfile() {
        repository.updateDisplayName("Algo Master")
        assertEquals("Algo Master", repository.currentProfile.displayName)
        assertEquals("Algo Master", AppSettings.guestDisplayName)

        repository.updateProfile("Algo Master", "@algomaster", "Software Engineer")
        assertEquals("Algo Master", repository.currentProfile.displayName)
        assertEquals("@algomaster", repository.currentProfile.handle)
        assertEquals("Software Engineer", repository.currentProfile.roleTitle)
    }

    @Test
    fun signOutReturnsToGuestMode() {
        repository.signOut()
        assertTrue(repository.state is AuthAccountState.SignedOut)
        assertEquals("Test Duck", repository.currentProfile.displayName)
    }
}
