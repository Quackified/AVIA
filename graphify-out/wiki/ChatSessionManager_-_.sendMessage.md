# ChatSessionManager / .sendMessage()

> 11 nodes

## Key Concepts

- **ChatSessionManager** (15 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- **.sendMessage()** (4 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- **.createInitialGreeting()** (3 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- **.executeAction()** (3 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- **.sendStarter()** (3 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- **.clearChat()** (2 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- **.sessionManager_clearChatResetsToGreeting()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.sessionManager_executesLaunchVisualizerAction()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.sessionManager_initialStateIsCorrect()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.sessionManager_messageSendingAppendsUserAndAssistantTurn()** (2 connections) — `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`
- **.onInputChange()** (1 connections) — `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`

## Relationships

- [ChatSessionState / AiChatEngine](ChatSessionState_-_AiChatEngine.md) (5 shared connections)
- [.processQuery() / AiChatBackendTest](processQuery_-_AiChatBackendTest.md) (5 shared connections)
- [AiChatBackendTest / FeatureEnhancementsTest](AiChatBackendTest_-_FeatureEnhancementsTest.md) (1 shared connections)
- [ChatScreen / ChatScreen()](ChatScreen_-_ChatScreen.md) (1 shared connections)
- [AiChatEngine / ChatMessage](AiChatEngine_-_ChatMessage.md) (1 shared connections)

## Source Files

- `app/src/main/java/com/example/algolens/ui/chat/ChatSessionState.kt`
- `app/src/test/java/com/example/algolens/AiChatBackendTest.kt`

## Audit Trail

- EXTRACTED: 26 (100%)
- INFERRED: 0 (0%)
- AMBIGUOUS: 0 (0%)

---

*Part of the graphify knowledge wiki. See [index](index.md) to navigate.*