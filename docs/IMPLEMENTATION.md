# IMPLEMENTATION.md — Restore AVIA's Visual Identity and Simplify Chat & Profile

## 1. What Was Changed

### 1.1 Dual-Typeface Contract & Bundled JetBrains Mono (`Type.kt`)
- **Restored bundled `R.font.jetbrains_mono_variable` (`JetBrainsMono`):** Replaced `FontFamily.Monospace` fallback with `FontFamily(Font(R.font.jetbrains_mono_variable, ...))` across weights `400 Normal`, `500 Medium`, `600 SemiBold`, and `700 Bold`.
- **Proportional UI & prose (`AlgoSans`):** Mapped Material 3 `AlgoLensTypography` (`display*`, `headline*`, `title*`, `body*`, `label*`) to `AlgoSans` (`FontFamily.SansSerif`) with a strict `11.sp` label floor (`labelSmall` / `microSize = 11.sp`) and restrained tracking (`trackSection = 0.1.sp`, `trackHeader = 0.2.sp`) instead of wide `0.8sp` all-caps tracking.
- **Dedicated monospace instrument & telemetry styles (`AlgoType`):** Preserved and exposed `AlgoType.codeTrace`, `AlgoType.telemetryMono`, and `AlgoType.micro` in `JetBrainsMono` with `fontFeatureSettings = "tnum"` for source code lines, complexity expressions (`O(...)`), step counters (`01/65`), speed chips (`1.0x`), and live variable/state readouts (`AlgoCard`, `ComplexityCard`, `CodeListing`, `StateDeckPage`, `PlaybackRail`, `VisualizerHeader`).

### 1.2 Compact Visible Controls Inside 44dp Touch Targets (`WorkspaceControls.kt` & Call Sites)
- Added `CompactIconButton` in [`WorkspaceControls.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/WorkspaceControls.kt), wrapping a compact `32dp` (`AlgoTokens.iconButtonMd`) visual button and `14dp` (`AlgoTokens.inlineIconMd`) `AlgoGlyphs` icon inside an outer `44dp` (`AlgoTokens.Spacing.minTouchTarget`) hit area with `Modifier.pressPhysics`.
- Replaced oversized `44dp × 44dp` boxed header buttons across `SettingsScreen` (Back button), `ChatScreen` (`ChatHeader` history drawer toggle, new conversation button, and overflow action button), and `ProfileScreen` (Settings gear and Edit Profile pencil buttons) with `CompactIconButton`.

### 1.3 Explore Root Tab Navigation Cleanup (`PracticeScreen.kt` & `AlgoLensApp.kt`)
- Removed the redundant `onBack` callback and `44dp` `Back` button from `PracticeScreen` when displayed as the root `NavTab.EXPLORE` destination.
- Updated the `PracticeScreen` header to present `Explore` (`Practice Mode · Complexity & Step Drills`) as a peer root destination alongside `Home`, `Chat`, and `Profile`, while keeping system Back navigation handled cleanly by `AlgoLensApp.kt`.

### 1.4 Chat Screen Redesign, Starter Collapse & Single-Owner Window Insets (`ChatScreen.kt`)
- **Simplified `ChatHeader`:** Replaced the two-tier action bar (`+ NEW` + `CLEAR` + `HISTORY`) with a single clean header row: a compact `CompactIconButton(AlgoGlyphs.List)` drawer toggle on the left, the active conversation title + honest `"Offline catalog & project advisor"` subtitle in the center, and a restrained `CompactIconButton(AlgoGlyphs.Plus)` + `CompactIconButton(AlgoGlyphs.More)` overflow menu (`Reset current thread` with confirmation dialog) on the right.
- **Collapsible starter prompts (`EmptyConversationStarters`):** Moved starter prompt cards and the `"Plan an algorithm for my project"` CTA out of a permanent sticky bar and into the `LazyColumn` so they appear only when a conversation has no user messages (`messages.none { it.isFromUser }`), collapsing automatically once the conversation starts.
- **Editorial message stream (`UserMessageBubble` & `AssistantMessageBubble`):**
  - Removed the outer `DoubleBezelShell` frame around ordinary assistant prose so markdown explanations read cleanly with proportional `AlgoType.readingBody` (`AlgoSans`, `13.5sp`, `20sp` leading).
  - Kept structured attachments (`ComplexitySnapshot` cards, `CodeSnippetAttachment` blocks with `JetBrainsMono`, `ProjectRecommendationCard` cards, and `ProjectIntakeFormCard`) inside `DoubleBezelShell` instrument surfaces with direct `"Open in Visualizer"` CTAs.
- **Multiline `ChatInputDock` & single-owner IME insets:**
  - Upgraded the composer `BasicTextField` from `singleLine = true` monospace to a multiline proportional `AlgoSans` input (`minLines = 1`, `maxLines = 5`) with an accessible `44dp` hit target around the send button.
  - Eliminated duplicate bottom inset ownership by applying `.windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))` once on `ChatScreen` and removing duplicate `navigationBarsPadding()` from `ChatInputDock`.

### 1.5 Separated Edit Profile Sheet & Honest Account Surface (`AppSettings.kt`, `AuthRepository.kt`, `AccountTemplateCard.kt`, `ProfileScreen.kt`)
- **Local profile persistence (`AppSettings.kt` & `AuthRepository.kt`):**
  - Persisted `guestDisplayName`, `guestHandle`, `guestRoleTitle`, and `guestAvatarUri` in `AppSettings` so local profile customizations survive app restarts in offline mode.
  - Added `ProfileValidator` (validating display name `2..32` chars, `@handle` `[a-zA-Z0-9_.]`, and role/status `max 48` chars) and `AuthProviderType.GOOGLE_FIREBASE` (`signInWithGoogleIdToken`) for future Google Sign-In via Firebase Authentication.
- **Real avatar rendering (`ProfileAvatar`):**
  - Decodes persisted `content://` URIs via `ContentResolver.openInputStream` + `BitmapFactory.decodeStream` and requests `takePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION)` on selection from the Android System Photo Picker (`ActivityResultContracts.PickVisualMedia`), falling back cleanly to a 2-letter monogram when no avatar URI is set or readable.
  - Rendered `ProfileAvatar` both in `ProfileScreen`'s hero header and inside `EditProfileSheet`.
- **Separated `EditProfileSheet` vs `AccountStatusCard`:**
  - Replaced `EditProfileAndAccountSheet` with `EditProfileSheet` (`ProfileEditorDraft` state holder with validation, Save/Cancel actions, and unsaved-change confirmation dialog) containing **only** profile identity fields (avatar picker/remove, display name, `@handle`, and role/study focus).
  - Added a separate, concise `AccountStatusCard` on `ProfileScreen` that clearly states that bookmarks, settings, and profile data are stored locally on-device and that optional Google Sign-In via Firebase Auth is not enabled in this offline build — without collecting email/password credentials or presenting fake sign-in forms.

---

## 2. Files Modified / Created / Deleted

### Modified:
- [`app/src/main/java/com/example/algolens/ui/theme/Type.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/theme/Type.kt)
- [`app/src/main/java/com/example/algolens/ui/components/WorkspaceControls.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/WorkspaceControls.kt)
- [`app/src/main/java/com/example/algolens/ui/components/AlgoCard.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/AlgoCard.kt)
- [`app/src/main/java/com/example/algolens/ui/components/CommonComponents.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/components/CommonComponents.kt)
- [`app/src/main/java/com/example/algolens/ui/settings/SettingsScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/settings/SettingsScreen.kt)
- [`app/src/main/java/com/example/algolens/ui/practice/PracticeScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/practice/PracticeScreen.kt)
- [`app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/AlgoLensApp.kt)
- [`app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/chat/ChatScreen.kt)
- [`app/src/main/java/com/example/algolens/data/AppSettings.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/AppSettings.kt)
- [`app/src/main/java/com/example/algolens/data/auth/AuthRepository.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/data/auth/AuthRepository.kt)
- [`app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/profile/AccountTemplateCard.kt)
- [`app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/main/java/com/example/algolens/ui/profile/ProfileScreen.kt)
- [`app/src/test/java/com/example/algolens/FeatureEnhancementsTest.kt`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/app/src/test/java/com/example/algolens/FeatureEnhancementsTest.kt)
- [`docs/DESIGN.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/DESIGN.md)
- [`docs/ARCHITECTURE.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/ARCHITECTURE.md)
- [`docs/PROJECT.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/PROJECT.md)
- [`docs/IMPLEMENTATION.md`](file:///c:/Users/Quacky/Documents/Coding/Android%20Studio/Projects/AlgoLens/docs/IMPLEMENTATION.md)

### Created / Deleted:
- None (all changes were implemented cleanly within the existing modular structure).

---

## 3. Important Implementation Decisions

1. **Tokenized Custom Lint Compliance (`:lint`):** All new UI components (`CompactIconButton`, `ProfileAvatar`, `AccountStatusCard`, `EditProfileSheet`, `EmptyConversationStarters`, `ChatHeader`) strictly use `AlgoTokens.space1`–`space8`, `AlgoTokens.radius*`, `AlgoTokens.iconButton*`, and `AlgoTokens.inlineIcon*` with zero hardcoded `Color(0xFF...)` or raw `.dp` spacing/radius literals.
2. **Testable Profile Draft State (`ProfileEditorDraft`):** Extracted profile editing state, validation (`ProfileValidator`), handle normalization (`@handle`), and unsaved-change tracking (`hasUnsavedChanges`) into `ProfileEditorDraft` so Save/Cancel and avatar state transitions are deterministically tested in JUnit unit tests.
3. **Single-Owner Keyboard Insets on Chat:** `AppShell` owns `BottomNavBar` (`navigationBarsPadding()`), while `ChatScreen` applies `.windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))` once so the composer lifts smoothly above the software keyboard without double-counting the navigation bar height.

---

## 4. Tests & Checks Performed

- **`.\gradlew.bat testDebugUnitTest`:** Passed all unit tests in `AlgorithmStepRepositoryTest`, `VisualizerScreenStateTest`, and `FeatureEnhancementsTest` (including new tests `profileEditorDraft_validatesPersistsAndRestoresAvatarAndIdentityFields` and `authRepository_googleSignInContractReportsHonestUnavailableAndSupportsFakeProvider`).
- **`.\gradlew.bat lintDebug`:** Passed with zero custom or Android lint errors (`AlgolensHardcodedHexColor`, `AlgolensRoundedCornerShapeLiteral`, `AlgolensRawDpSpacing`, `AlgolensVisualizerScreenMutation`).
- **`.\gradlew.bat assembleDebug`:** Built `:app:assembleDebug` cleanly (`BUILD SUCCESSFUL`).
- **`graphify update .`:** Updated the project knowledge graph in `graphify-out/` after all code edits.

---

## 5. Deviations from TASK.md

- None. All requirements in `docs/TASK.md` were implemented as specified.

---

## 6. Known Issues or Limitations

- No connected Android emulator or physical device was attached (`adb devices` empty) during this session, so verification was performed via unit tests (`testDebugUnitTest`), static/custom lint analysis (`lintDebug`), and full debug APK compilation (`assembleDebug`).
