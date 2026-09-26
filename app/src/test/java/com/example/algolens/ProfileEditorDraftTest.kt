package com.example.algolens

import com.example.algolens.data.AppSettings
import com.example.algolens.data.auth.FakeAuthRepository
import com.example.algolens.data.auth.UnavailableFirebaseAuthRepository
import com.example.algolens.data.auth.UserProfile
import com.example.algolens.ui.profile.ProfileEditorDraft
import org.junit.Assert.*
import org.junit.Test

class ProfileEditorDraftTest {
    @Test
    fun repeatedBackNeverDiscardsDirtyDraft() {
        val draft = ProfileEditorDraft(UserProfile())
        draft.displayName = "Ada Lovelace"
        repeat(3) {
            assertFalse(draft.requestCancel())
            assertTrue(draft.showDiscardConfirm)
        }
        draft.showDiscardConfirm = false
        assertEquals("Ada Lovelace", draft.displayName)
        assertFalse(draft.requestCancel())
    }

    @Test
    fun unchangedDraftCanCloseAndInvalidDraftCannotWrite() {
        val repository = FakeAuthRepository { _, _ -> }
        val initial = repository.currentProfile
        val draft = ProfileEditorDraft(initial)
        assertTrue(draft.requestCancel())
        draft.displayName = " "
        draft.avatarUri = "content://draft-only-photo"
        assertFalse(draft.saveTo(repository))
        assertEquals(initial, repository.currentProfile)
    }

    @Test
    fun optionalStudyFocusCanBeClearedAndRestoredAsEmpty() {
        AppSettings.clearAllSavedData()
        try {
            val repository = UnavailableFirebaseAuthRepository()
            val draft = ProfileEditorDraft(repository.currentProfile)
            draft.roleTitle = " "
            assertTrue(draft.hasUnsavedChanges)
            assertTrue(draft.saveTo(repository))
            assertEquals("", repository.currentProfile.roleTitle)
            assertEquals("", UnavailableFirebaseAuthRepository().currentProfile.roleTitle)
            val fake = FakeAuthRepository { _, _ -> }
            fake.updateProfile("Ada", "@ada", "")
            assertEquals("", fake.currentProfile.roleTitle)
        } finally {
            AppSettings.clearAllSavedData()
        }
    }
}
