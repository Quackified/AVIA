# .processQuery() / AiChatBackendTest

> 13 nodes

## Key Concepts

- **.processQuery()** (24 connections) — `app/src/main/java/com/example/algolens/data/chat/AiChatEngine.kt`
- **AiChatBackendTest** (19 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_detectsAlgorithmsFromInformalQueries()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_generatesFlowchartAndCalloutsForAlgorithmExplanation()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_generatesMarkdownTableForComparativeQueries()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_generatesTableAndCalloutForComplexityQuery()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_handlesCatalogueAndHelpQueries()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_handlesStabilityGeneralQuery()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_promptStartersAreCuratedAndComplete()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_resolvesBfsVsDfsComparison()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_resolvesCodeSnippetQueries()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_resolvesComparativeQueriesWithSideBySideAnalysis()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.engine_resolvesComplexityQueriesWithStructuredMatrix()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`

## Relationships

- [AiChatEngine / ChatMessage](AiChatEngine_-_ChatMessage.md) (11 shared connections)
- [ChatSessionManager / .sendMessage()](ChatSessionManager_-_.sendMessage.md) (5 shared connections)
- [ChatMarkdownMessage() / parseMarkdownInline()](ChatMarkdownMessage_-_parseMarkdownInline.md) (3 shared connections)
- [AiChatBackendTest / FeatureEnhancementsTest](AiChatBackendTest_-_FeatureEnhancementsTest.md) (1 shared connections)
- [ChatSessionState / AiChatEngine](ChatSessionState_-_AiChatEngine.md) (1 shared connections)

## Source Files

- `app/src/main/java/com/example/algolens/data/chat/AiChatEngine.kt`
- `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`

## Audit Trail

- EXTRACTED: 43 (100%)
- INFERRED: 0 (0%)
- AMBIGUOUS: 0 (0%)

---

*Part of the graphify knowledge wiki. See [index](index.md) to navigate.*