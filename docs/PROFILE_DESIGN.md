# Profile and Edit Profile

Mode: Operate. Help an AVIA learner personalize their local identity and return to their saved algorithms, practice, conversations, and study preferences.

## Direction contract

THESIS: Personal identity followed by useful destinations. Replace catalogue statistics dominating the profile with actions grounded in existing app capabilities.

OWN-WORLD: Inherit AVIA's dark canvas, cyan actions, proportional UI type, AlgoGlyphs and spacing tokens. Use quiet dividers and filled input wells; avoid nested framed cards.

STORY: Recognize your profile, edit it, or return to your learning workspace. Local storage is explained in plain language.

FIRST VIEWPORT: Centered page title and settings action. Large circular avatar on the left, name and handle with a labeled Edit profile button on the right. Full-width icon-and-chevron destination rows below. Editor has back/title/save, a centered avatar and vertically stacked labeled fields.

FORM: User-pinned reference composition adapted to the established app. Code-led; no concept seed required for this specified layout. Stack the identity block at narrow widths or large font scale; constrain content on wider windows.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance

## Scope and constraints

Keep guest data, bookmarks, photo picking and validation. Full-screen editing protects drafts from accidental tab navigation. No fabricated progress or unsupported authentication. Existing global visual identity remains in docs/DESIGN.md.

## Feature opportunities

| Priority | Feature | Benefit and existing support | Delivery |
| --- | --- | --- | --- |
| P0 | Clear identity and profile editor | Existing profile repository, photo picker and local preferences | This change |
| P0 | Saved algorithms destination | Existing bookmark IDs and algorithm cards | This change |
| P0 | Reliable drafts and cancel handling | Existing draft model needs restoration and repeated-back protection | This change |
| P1 | Study preferences shortcut | Existing trace language, playback and accessibility settings | This change |
| P1 | Practice and tutor shortcuts | Existing Explore and Chat destinations | This change |
| P2 | Recent learning activity | Requires persisted algorithm visits and timestamps | Later |
| P2 | Practice progress | Requires durable attempt and completion records; catalogue counts are not progress | Later |
| P2 | Profile/data export and import | Local stores exist; needs versioned format and conflict handling | Later |
| P3 | Optional account and device sync | Provider contract exists; production authentication and sync are absent | Later |
