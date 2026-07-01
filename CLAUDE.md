# Learn-it — Claude Code Context

## Project

Android study-companion app. Kotlin + Jetpack Compose + MVVM.
Single module: `app`. Min SDK 24, Target SDK 36.

## Stack (actual installed versions)

| Lib | Version |
|---|---|
| Kotlin | 2.3.21 |
| AGP | 9.2.1 |
| Compose BOM | 2026.05.01 |
| Hilt | 2.59.2 |
| Room | 2.7.0 |
| Retrofit | 3.0.0 |
| Coroutines | 1.11.0 |
| Firebase BOM | 34.15.0 |

Serialization: **kotlinx.serialization** (not Gson/Moshi). KSP (not kapt).

## Commands

```bash
./gradlew :app:assembleDebug          # build
./gradlew :app:testDebugUnitTest      # unit tests (JVM only, no emulator)
./gradlew :app:connectedDebugAndroidTest  # instrumented tests (needs device)
```

## Architecture

```
com.learnit.app/
  data/
    local/          # Room — entities, DAOs, LearnitDatabase
    remote/         # Groq API — GroqApiService, GroqResponseParser, DTOs
    repository/     # FlashcardRepository, AuthRepository, LeaderboardRepository
  domain/model/     # Flashcard, SessionUiState, ScoreCalculator, etc.
  di/               # DatabaseModule, NetworkModule, RepositoryModule
  ui/viewmodel/     # FlashcardViewModel, StudySessionViewModel
  presentation/     # App.kt (NavHost placeholder), theme
  util/             # ApiState, NetworkMonitor
```

- **No usecase layer** — ViewModels call repositories directly.
- **Single source of truth**: repositories. ViewModels never touch DAOs or ApiService.
- **StateFlow everywhere** — no LiveData.

## Secrets

Stored in `.env` at project root (gitignored). Gradle reads it at build time.

```
GROQ_API_KEY=gsk_...
```

Do NOT commit `.env`. Do NOT hardcode keys. Do NOT touch `local.properties` for secrets.

## Key decisions (do not reverse without discussion)

- `MyApplication.kt` is the Application class — do not rename (manifest references it).
- Timer in `StudySessionViewModel` is a coroutine `while` loop, not `CountDownTimer`.
- Scoring: `revealedCards * 10 + timeRemaining` (bonus only if ALL cards revealed).
- Firebase BOM 34.x — use `firebase-auth` / `firebase-firestore` (not `-ktx` variants, deprecated).
- Groq model: `llama-3.3-70b-versatile` (defined in `GroqApiService.MODEL`).

## What is NOT done yet (frontend scope)

`presentation/App.kt` is a placeholder `Text("Learn-it")`. Compose screens for flashcard list,
study session, auth, and leaderboard are not implemented.

## Tests

```
app/src/test/java/com/learnit/app/
  ScoreCalculatorTest.kt       # scoring formula
  GroqResponseParserTest.kt    # JSON parsing + fence stripping
  FakeFlashcardRepository.kt   # test double
  StudySessionViewModelTest.kt # session flow + score
```

All pure JVM. No Robolectric. ViewModel tests use `UnconfinedTestDispatcher`.
