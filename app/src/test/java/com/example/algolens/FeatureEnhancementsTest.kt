package com.example.algolens

import androidx.compose.ui.geometry.Offset
import com.example.algolens.data.AlgorithmCodeRegistry
import com.example.algolens.data.AlgorithmRegistry
import com.example.algolens.data.AlgorithmStepRepository
import com.example.algolens.data.AppSettings
import com.example.algolens.data.SampleData
import com.example.algolens.data.TraceLanguage
import com.example.algolens.data.auth.AuthAccountState
import com.example.algolens.data.auth.FakeAuthRepository
import com.example.algolens.data.auth.GuestDataMigrationPolicy
import com.example.algolens.data.auth.UnavailableFirebaseAuthRepository
import com.example.algolens.data.chat.ChatHistoryRepository
import com.example.algolens.data.chat.ProjectAlgorithmRecommender
import com.example.algolens.model.Algorithm
import com.example.algolens.model.AlgorithmId
import com.example.algolens.model.chat.ProjectConstraintBrief
import com.example.algolens.ui.chat.ChatSessionManager
import com.example.algolens.ui.visualizer.AlgorithmTheoryRepository
import com.example.algolens.ui.visualizer.ChallengeState
import com.example.algolens.ui.visualizer.ElementState
import com.example.algolens.ui.visualizer.GraphCanvasGeometry
import com.example.algolens.ui.visualizer.GraphEdgeState
import com.example.algolens.ui.visualizer.GraphNodeState
import com.example.algolens.ui.visualizer.PRESET_OPTIONS
import com.example.algolens.ui.visualizer.SyntaxHighlighter
import com.example.algolens.ui.visualizer.TOUR_STEPS
import com.example.algolens.ui.visualizer.VisualizerStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureEnhancementsTest {

    @Test
    fun edgeCasePresets_containAllRequiredOptions() {
        val presetIds = PRESET_OPTIONS.map { it.id }
        assertTrue("Presets should contain 'Random'", presetIds.contains("Random"))
        assertTrue("Presets should contain 'Already Sorted'", presetIds.contains("Already Sorted"))
        assertTrue("Presets should contain 'Reverse Sorted'", presetIds.contains("Reverse Sorted"))
        assertTrue("Presets should contain 'All Equal'", presetIds.contains("All Equal"))
    }

    @Test
    fun multiLanguageRegistry_supportsAll14AlgorithmsAnd4LanguagesWithValidLineMappings() {
        val languages = TraceLanguage.entries
        val bubbleKotlinLines = AlgorithmCodeRegistry.getCode(
            AlgorithmId.BUBBLE_SORT,
            TraceLanguage.KOTLIN
        ).lines

        for (algoId in AlgorithmId.entries) {
            val steps = AlgorithmStepRepository.generateStepsForAlgorithm(
                Algorithm(id = algoId)
            )
            assertTrue("Steps for ${algoId.name} should not be empty", steps.isNotEmpty())

            for (lang in languages) {
                val codeData = AlgorithmCodeRegistry.getCode(algoId, lang)
                assertTrue("Code for ${algoId.name} in ${lang.label} should not be empty", codeData.lines.isNotEmpty())
                assertTrue("Line mapping for ${algoId.name} in ${lang.label} should exist", codeData.lineMapping.isNotEmpty())

                if (algoId != AlgorithmId.BUBBLE_SORT && lang == TraceLanguage.KOTLIN) {
                    assertFalse(
                        "${algoId.name} should not fall back to Bubble Sort code",
                        codeData.lines == bubbleKotlinLines
                    )
                }

                for (step in steps) {
                    for (stepKey in step.activeCodeLines) {
                        val mappedLines: List<Int> = codeData.lineMapping[stepKey] ?: listOf(stepKey)
                        assertTrue("Mapped lines for key $stepKey in ${algoId.name} (${lang.label}) should not be empty", mappedLines.isNotEmpty())
                        for (lineNum in mappedLines) {
                            assertTrue(
                                "Mapped line $lineNum (from key $stepKey) for ${algoId.name} in ${lang.label} out of range 1..${codeData.lines.size}",
                                lineNum in 1..codeData.lines.size
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun multiLanguageRegistry_semanticStepAlignmentAcrossFamilies() {
        // 1. LINEAR_1D: Binary Search mid calculation / comparison steps
        val binarySteps = AlgorithmStepRepository.generateStepsForAlgorithm(Algorithm(id = AlgorithmId.BINARY_SEARCH))
        val binaryCode = AlgorithmCodeRegistry.getCode(AlgorithmId.BINARY_SEARCH, TraceLanguage.KOTLIN)
        val resolvedBinaryLines = binarySteps
            .flatMap { it.activeCodeLines }
            .flatMap { key -> binaryCode.lineMapping[key] ?: listOf(key) }
            .distinct()
            .map { lineIdx -> binaryCode.lines[lineIdx - 1] }
            .joinToString("\n")
        assertTrue(
            "Binary Search active steps should highlight code referencing mid/low/high/target, got: $resolvedBinaryLines",
            resolvedBinaryLines.contains("mid") && (resolvedBinaryLines.contains("low") || resolvedBinaryLines.contains("high"))
        )

        // 2. BUFFER: Stack push/pop operations
        val stackSteps = AlgorithmStepRepository.generateStepsForAlgorithm(Algorithm(id = AlgorithmId.STACK))
        val stackCode = AlgorithmCodeRegistry.getCode(AlgorithmId.STACK, TraceLanguage.KOTLIN)
        val resolvedStackLines = stackSteps
            .flatMap { it.activeCodeLines }
            .flatMap { key -> stackCode.lineMapping[key] ?: listOf(key) }
            .distinct()
            .map { lineIdx -> stackCode.lines[lineIdx - 1] }
            .joinToString("\n")
        assertTrue(
            "Stack active steps should highlight code referencing push/pop/stack, got: $resolvedStackLines",
            resolvedStackLines.contains("push", ignoreCase = true) ||
                resolvedStackLines.contains("add", ignoreCase = true) ||
                resolvedStackLines.contains("stack", ignoreCase = true)
        )

        // 3. GRAPH_2D: BFS queue dequeue / neighbor exploration
        val bfsSteps = AlgorithmStepRepository.generateStepsForAlgorithm(Algorithm(id = AlgorithmId.BFS))
        val bfsCode = AlgorithmCodeRegistry.getCode(AlgorithmId.BFS, TraceLanguage.KOTLIN)
        val resolvedBfsLines = bfsSteps
            .flatMap { it.activeCodeLines }
            .flatMap { key -> bfsCode.lineMapping[key] ?: listOf(key) }
            .distinct()
            .map { lineIdx -> bfsCode.lines[lineIdx - 1] }
            .joinToString("\n")
        assertTrue(
            "BFS active steps should highlight code referencing queue/visited/adj, got: $resolvedBfsLines",
            resolvedBfsLines.contains("queue", ignoreCase = true) &&
                resolvedBfsLines.contains("visited", ignoreCase = true)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun multiLanguageRegistry_unknownAlgorithmThrowsIllegalArgumentException() {
        AlgorithmCodeRegistry.getCode("Unknown Algorithm Name", TraceLanguage.KOTLIN)
    }

    @Test
    fun graphCanvasGeometry_preservesLegibleNodeScaleAcrossDockExpansionAndRoundTripsPan() {
        val sampleNodes = listOf(
            GraphNodeState("A", "10", 20f, 15f, ElementState.ACTIVE),
            GraphNodeState("B", "20", 80f, 85f, ElementState.IDLE),
            GraphNodeState("C", "30", 50f, 50f, ElementState.VISITED)
        )
        // Compact phone portrait viewport when dock is peeking (height = 1100px) vs expanded (height = 460px)
        val peekGeometry = GraphCanvasGeometry.from(
            nodes = sampleNodes,
            drawSize = androidx.compose.ui.geometry.Size(980f, 1100f),
            referenceHeight = 1100f
        )
        val expandedDockGeometry = GraphCanvasGeometry.from(
            nodes = sampleNodes,
            drawSize = androidx.compose.ui.geometry.Size(980f, 460f),
            panOffset = Offset(120f, -85f),
            referenceHeight = 1100f
        )

        assertTrue("Peek nodeRadius must be >= 16f", peekGeometry.nodeRadius >= 16f)
        assertTrue("Expanded dock nodeRadius must remain >= 16f", expandedDockGeometry.nodeRadius >= 16f)
        assertEquals(
            "Dock expansion must not shrink world scale when referenceHeight is anchored",
            peekGeometry.nodeRadius,
            expandedDockGeometry.nodeRadius,
            0.01f
        )

        // Verify panOffset coordinate round-trip between renderer (toCanvasOffset) and hit-testing (toWorldCoords)
        val originalWorld = Pair(72.5f, 84.0f)
        val screenPos = expandedDockGeometry.toCanvasOffset(originalWorld.first, originalWorld.second)
        val roundTrippedWorld = expandedDockGeometry.toWorldCoords(screenPos)
        assertEquals(originalWorld.first, roundTrippedWorld.x, 0.05f)
        assertEquals(originalWorld.second, roundTrippedWorld.y, 0.05f)
    }

    @Test
    fun chatSessionManager_persistsMultiConversationHistoryAndCancelsStaleResponsesOnDelete() {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val repository = ChatHistoryRepository(context = null)

        val manager = ChatSessionManager(
            scope = scope,
            repository = repository,
            responseDelayMs = 5_000L
        )

        val firstThreadId = manager.activeConversationId
        manager.sendMessage("Compare Quick Sort vs Merge Sort")
        assertTrue("Conversation should enter thinking state while response is in flight", manager.isThinking)

        // Delete the thread while the delayed response is still in flight
        manager.deleteConversation(firstThreadId)

        // Ensure the deleted thread was not resurrected and its response did not leak into the new active thread
        assertFalse(
            "Deleted conversation must not reappear after deletion",
            manager.conversations.any { it.id == firstThreadId }
        )
        assertEquals(
            "New replacement thread should only contain initial greeting",
            1,
            manager.messages.size
        )

        // Now send a synchronous message (responseDelayMs = 0L) and verify persistence across reload
        val immediateManager = ChatSessionManager(
            scope = scope,
            repository = repository,
            responseDelayMs = 0L
        )
        immediateManager.sendMessage("Why is Binary Search O(log n)?")
        assertEquals(3, immediateManager.messages.size)

        val reloadedManager = ChatSessionManager(
            scope = scope,
            repository = repository,
            responseDelayMs = 0L
        )
        assertEquals(immediateManager.activeConversation.title, reloadedManager.activeConversation.title)
        assertEquals(3, reloadedManager.messages.size)
    }

    @Test
    fun projectAlgorithmRecommender_groundsAllCandidatesInRegistryAndSurfacesOutOfCatalogNotice() {
        // 1. In-catalog sorting project
        val sortBrief = ProjectConstraintBrief(
            projectGoal = "Sort 100,000 customer invoice records with stable ordering",
            dataScale = ProjectConstraintBrief.DataScale.LARGE_100K_PLUS,
            inputOrder = ProjectConstraintBrief.InputOrder.RANDOM_UNSORTED,
            requiresStability = true
        )
        val sortPayload = ProjectAlgorithmRecommender.recommend(sortBrief)
        assertTrue("Should return 1..3 candidates", sortPayload.candidates.size in 1..3)
        assertEquals("Merge Sort should rank #1 for large stable sorting", AlgorithmId.MERGE_SORT, sortPayload.candidates.first().algorithmId)

        for (candidate in sortPayload.candidates) {
            val spec = AlgorithmRegistry.specFor(candidate.algorithmId)
            assertNotNull("Candidate ${candidate.algorithmId} must exist in AlgorithmRegistry", spec)
            val theory = AlgorithmTheoryRepository.getTheory(spec!!.id.displayName)
            assertEquals(spec.id.displayName, candidate.displayName)
            assertEquals(theory.averageCase, candidate.averageCase)
            assertEquals(theory.worstCase, candidate.worstCase)
            assertEquals(theory.spaceComplexity, candidate.spaceComplexity)
        }

        // 2. In-catalog Dijkstra recommendation vs out-of-catalog Bellman-Ford notice
        val dijkstraPayload = ProjectAlgorithmRecommender.recommendFromNaturalLanguage(
            "I need a project algorithm for weighted GPS road network shortest path routing with Dijkstra"
        )
        assertTrue(
            "Dijkstra must be recommended as an in-catalog candidate",
            dijkstraPayload.candidates.any { it.algorithmId == AlgorithmId.DIJKSTRA }
        )

        val outOfCatalogPayload = ProjectAlgorithmRecommender.recommendFromNaturalLanguage(
            "I need Bellman-Ford algorithm for negative edge cycle detection"
        )
        assertNotNull(
            "Out-of-catalog notice must be explicitly populated for Bellman-Ford",
            outOfCatalogPayload.outOfCatalogNotice
        )
        assertTrue(outOfCatalogPayload.outOfCatalogNotice!!.contains("Bellman-Ford"))
    }

    @Test
    fun authRepository_enforcesValidationHonestUnavailableStateAndCrossAccountIsolation() {
        // 1. UnavailableFirebaseAuthRepository never fakes sign-in when Firebase is absent
        val unavailableRepo = UnavailableFirebaseAuthRepository()
        assertTrue(unavailableRepo.state is AuthAccountState.Unavailable)

        unavailableRepo.signIn("invalid-email", "123")
        assertTrue("Invalid credentials should report Error", unavailableRepo.state is AuthAccountState.Error)

        unavailableRepo.signIn("operator@algolens.dev", "ValidPass123")
        assertTrue(
            "When Firebase is unconfigured, valid credentials must transition to Unavailable rather than fake SignedIn",
            unavailableRepo.state is AuthAccountState.Unavailable
        )

        // 2. FakeAuthRepository verifies guest-to-account migration & cross-account isolation
        AppSettings.clearAllSavedData()
        val chatRepo = ChatHistoryRepository(context = null)
        val fakeAuth = FakeAuthRepository { newOwnerId, migrateGuest ->
            AppSettings.switchOwner(newOwnerId, migrateGuest)
            if (migrateGuest) {
                chatRepo.migrateGuestConversationsToAccount(newOwnerId)
            }
        }

        // Guest bookmarks Bubble Sort
        AppSettings.toggleBookmark(AlgorithmId.BUBBLE_SORT.name)
        assertTrue(AppSettings.isBookmarked(AlgorithmId.BUBBLE_SORT.name))

        // Sign in as User A with KEEP_SEPARATE policy -> User A starts with empty bookmarks
        fakeAuth.setGuestMigrationPolicy(GuestDataMigrationPolicy.KEEP_SEPARATE)
        fakeAuth.signIn("userA@algolens.dev", "SecurePass1")
        assertTrue(fakeAuth.state is AuthAccountState.SignedIn)
        assertFalse("User A should not see Guest bookmarks when KEEP_SEPARATE is selected", AppSettings.isBookmarked(AlgorithmId.BUBBLE_SORT.name))

        // User A bookmarks Quick Sort
        AppSettings.toggleBookmark(AlgorithmId.QUICK_SORT.name)
        assertTrue(AppSettings.isBookmarked(AlgorithmId.QUICK_SORT.name))

        // Sign out back to Guest -> Guest still has Bubble Sort, not User A's Quick Sort
        fakeAuth.signOut()
        assertTrue(fakeAuth.state is AuthAccountState.SignedOut)
        assertTrue("Guest bookmark should remain intact", AppSettings.isBookmarked(AlgorithmId.BUBBLE_SORT.name))
        assertFalse("User A bookmark must not leak into Guest after sign-out", AppSettings.isBookmarked(AlgorithmId.QUICK_SORT.name))

        // Sign in as User B with MERGE_GUEST_TO_ACCOUNT -> User B receives Guest's Bubble Sort but not User A's Quick Sort
        fakeAuth.setGuestMigrationPolicy(GuestDataMigrationPolicy.MERGE_GUEST_TO_ACCOUNT)
        fakeAuth.signIn("userB@algolens.dev", "SecurePass2")
        assertTrue(AppSettings.isBookmarked(AlgorithmId.BUBBLE_SORT.name))
        assertFalse("User A bookmark must not leak into User B", AppSettings.isBookmarked(AlgorithmId.QUICK_SORT.name))
    }

    @Test
    fun syntaxHighlighter_producesFormattedAnnotatedStrings() {
        val snippet = "fun bubbleSort(arr: IntArray) { val n = 10 }"
        val highlighted = SyntaxHighlighter.highlight(snippet, TraceLanguage.KOTLIN)
        assertEquals(snippet, highlighted.text)
        assertTrue("Syntax spans should be applied", highlighted.spanStyles.isNotEmpty())
    }

    @Test
    fun graphTreeVisualizer_stateCalculations() {
        val step = VisualizerStep(
            nodes = listOf(
                GraphNodeState("A", "A", 10f, 20f, ElementState.ACTIVE),
                GraphNodeState("B", "B", 80f, 60f, ElementState.IDLE)
            ),
            edges = listOf(
                GraphEdgeState(from = "A", to = "B", isHighlighted = true)
            )
        )

        assertEquals(2, step.nodes.size)
        assertEquals(1, step.edges.size)
        assertTrue(step.edges.first().isHighlighted)
    }

    @Test
    fun algorithmTheoryRepository_containsDataForAll14Algorithms() {
        for (algo in SampleData.algorithms) {
            val theory = AlgorithmTheoryRepository.getTheory(algo.name)
            assertNotNull("Theory for ${algo.name} should not be null", theory)
            assertTrue("Theory overview for ${algo.name} should not be blank", theory.overview.isNotBlank())
            assertTrue("Theory howItWorks for ${algo.name} should not be empty", theory.howItWorks.isNotEmpty())
            assertTrue("Theory whenToUse for ${algo.name} should not be empty", theory.whenToUse.isNotEmpty())
            assertTrue("Theory bestCase for ${algo.name} should not be blank", theory.bestCase.isNotBlank())
            assertTrue("Theory worstCase for ${algo.name} should not be blank", theory.worstCase.isNotBlank())
        }
    }

    @Test
    fun guidedTour_containsAll6InteractiveSteps() {
        assertEquals(6, TOUR_STEPS.size)
        assertTrue(TOUR_STEPS.any { it.title.contains("Visualizer Canvas") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Multi-Language Code Trace") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Variable State") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Playback Controls") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Predict Next Step") })
        assertTrue(TOUR_STEPS.any { it.title.contains("Theory Deep Dive") })
    }

    @Test
    fun challengeState_initializesAndUpdatesCorrectly() {
        var state = ChallengeState(isActive = true, score = 100, streak = 2)
        assertEquals(100, state.score)
        assertEquals(2, state.streak)
        assertTrue(state.isActive)

        state = state.copy(score = state.score + 140, streak = state.streak + 1)
        assertEquals(240, state.score)
        assertEquals(3, state.streak)
    }

    @Test
    fun offscreenPointerTarget_correctlyFlagsDirection() {
        val targetRight = com.example.algolens.ui.visualizer.OffscreenPointerTarget(
            index = 8,
            value = 7,
            label = "pivot",
            state = ElementState.PIVOT,
            badgeBg = androidx.compose.ui.graphics.Color.Red,
            badgeTextColor = androidx.compose.ui.graphics.Color.White,
            isRight = true
        )
        assertTrue(targetRight.isRight)
        assertEquals("pivot", targetRight.label)
        assertEquals(7, targetRight.value)
        assertEquals(8, targetRight.index)

        val targetLeft = com.example.algolens.ui.visualizer.OffscreenPointerTarget(
            index = 0,
            value = 3,
            label = "i",
            state = ElementState.ACTIVE,
            badgeBg = androidx.compose.ui.graphics.Color.Cyan,
            badgeTextColor = androidx.compose.ui.graphics.Color.Black,
            isRight = false
        )
        assertFalse(targetLeft.isRight)
        assertEquals("i", targetLeft.label)
    }

    @Test
    fun profileEditorDraft_validatesPersistsAndRestoresAvatarAndIdentityFields() {
        AppSettings.clearAllSavedData()
        val repo = UnavailableFirebaseAuthRepository()

        val initialDraft = com.example.algolens.ui.profile.ProfileEditorDraft.from(repo.currentProfile)
        assertFalse("Fresh draft should have no unsaved changes", initialDraft.hasUnsavedChanges)
        assertTrue("Default guest profile should validate cleanly", initialDraft.isValid)

        // Blank display name fails validation
        val invalidNameDraft = initialDraft.copy(displayName = " ")
        assertFalse(invalidNameDraft.isValid)
        assertNotNull(invalidNameDraft.displayNameError)

        // Invalid handle characters fail validation
        val invalidHandleDraft = initialDraft.copy(handle = "@bad handle!")
        assertFalse(invalidHandleDraft.isValid)
        assertNotNull(invalidHandleDraft.handleError)

        // Valid edits including avatar URI
        val editedDraft = initialDraft.copy(
            displayName = "Ada Lovelace",
            handle = "ada_lovelace",
            roleTitle = "Systems Architect",
            avatarUri = "content://media/picker/0/com.android.providers.media.photopicker/media/42"
        )
        assertTrue("Edited draft must flag unsaved changes", editedDraft.hasUnsavedChanges)
        assertTrue("Edited draft must be valid", editedDraft.isValid)

        // Commit save to repository and verify AppSettings persistence
        val saved = editedDraft.commitTo(repo)
        assertTrue(saved)
        assertEquals("Ada Lovelace", repo.currentProfile.displayName)
        assertEquals("@ada_lovelace", repo.currentProfile.handle)
        assertEquals("Systems Architect", repo.currentProfile.roleTitle)
        assertEquals(
            "content://media/picker/0/com.android.providers.media.photopicker/media/42",
            repo.currentProfile.avatarUri
        )
        assertEquals("Ada Lovelace", AppSettings.guestDisplayName)
        assertEquals("@ada_lovelace", AppSettings.guestHandle)

        // Clearing avatar falls back to null (monogram fallback)
        val clearedAvatarDraft = com.example.algolens.ui.profile.ProfileEditorDraft
            .from(repo.currentProfile)
            .copy(avatarUri = null)
        assertTrue(clearedAvatarDraft.hasUnsavedChanges)
        assertTrue(clearedAvatarDraft.commitTo(repo))
        assertEquals(null, repo.currentProfile.avatarUri)
    }

    @Test
    fun authRepository_googleSignInContractReportsHonestUnavailableAndSupportsFakeProvider() {
        val unavailableRepo = UnavailableFirebaseAuthRepository()
        unavailableRepo.signInWithGoogleIdToken("sample.jwt.token", "ada@algolens.dev", "Ada Lovelace")
        assertTrue(
            "Unconfigured Firebase repository must stay in Unavailable state for Google Sign-In",
            unavailableRepo.state is AuthAccountState.Unavailable
        )

        AppSettings.clearAllSavedData()
        val fakeRepo = FakeAuthRepository { newOwnerId, migrateGuest ->
            AppSettings.switchOwner(newOwnerId, migrateGuest)
        }
        fakeRepo.signInWithGoogleIdToken(
            idToken = "google.id.token.123",
            email = "ada@algolens.dev",
            displayName = "Ada Lovelace"
        )
        fakeRepo.updateAvatar("content://avatar/ada")
        assertTrue(fakeRepo.state is AuthAccountState.SignedIn)
        assertEquals(
            com.example.algolens.data.auth.AuthProviderType.GOOGLE_FIREBASE,
            fakeRepo.currentProfile.provider
        )
        assertEquals("ada@algolens.dev", fakeRepo.currentProfile.email)
        assertEquals("Ada Lovelace", fakeRepo.currentProfile.displayName)
        assertEquals("content://avatar/ada", fakeRepo.currentProfile.avatarUri)
    }
}
