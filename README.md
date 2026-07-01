# Learn-it

AI-powered flashcard study app for Android. Generate flashcards from any topic via Groq LLM, study them with a timed session, and compete on a real-time leaderboard.

![Language](https://img.shields.io/badge/language-Kotlin-B125EA?logo=kotlin&style=flat-square)
![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&style=flat-square)
![Min SDK](https://img.shields.io/badge/minSdk-24-blue?style=flat-square)
![Architecture](https://img.shields.io/badge/architecture-MVVM-orange?style=flat-square)

---

## Features

- **Flashcard generation** — enter any topic, get 8–10 Q&A cards powered by Groq (`llama-3.3-70b-versatile`)
- **Study session** — timed card-flip session with live countdown
- **Scoring** — base score per card revealed + speed bonus for finishing early
- **Local storage** — cards and session history persisted via Room
- **Authentication** — email/password login via Firebase Auth
- **Leaderboard** — real-time top scores via Firebase Firestore

---

## Tech Stack

| | |
|---|---|
| Language | Kotlin 2.3.21 |
| UI | Jetpack Compose (BOM 2026.05.01) |
| Architecture | MVVM — data / domain / ui layers |
| DI | Dagger-Hilt 2.59.2 |
| Local DB | Room 2.7.0 (KSP) |
| Network | Retrofit 3.0.0 + kotlinx.serialization |
| AI API | Groq (`llama-3.3-70b-versatile`) |
| Backend | Firebase Auth + Firestore (BOM 34.15.0) |
| Async | Kotlin Coroutines + Flow 1.11.0 |

---

## Getting Started

### 1. Clone

```bash
git clone https://github.com/<your-org>/learn-it.git
cd learn-it
```

### 2. Add secrets

Create `.env` at the project root (gitignored):

```
GROQ_API_KEY=gsk_your_key_here
```

Get a free Groq API key at [console.groq.com](https://console.groq.com).

### 3. Firebase

Place your `google-services.json` inside `app/`. The file is gitignored — get it from the Firebase console under **Project Settings → Your apps → Android**.

### 4. Build

```bash
./gradlew :app:assembleDebug
```

---

## Project Structure

```
app/src/main/java/com/learnit/app/
├── data/
│   ├── local/              # Room — entities, DAOs, LearnitDatabase
│   ├── remote/             # Groq API — service, parser, DTOs, exceptions
│   └── repository/         # FlashcardRepository, AuthRepository, LeaderboardRepository
├── domain/model/           # Flashcard, SessionUiState, ScoreCalculator, LeaderboardEntry
├── di/                     # DatabaseModule, NetworkModule, RepositoryModule
├── ui/viewmodel/           # FlashcardViewModel, StudySessionViewModel
├── presentation/           # App.kt (NavHost), theme
└── util/                   # ApiState, NetworkMonitor
```

---

## Running Tests

```bash
./gradlew :app:testDebugUnitTest
```

Covers: `ScoreCalculatorTest`, `GroqResponseParserTest`, `StudySessionViewModelTest`.
All pure JVM — no emulator required.

---

## Contributing

See [`AGENTS.md`](AGENTS.md) for layer rules, naming conventions, and what not to change.
See [`CLAUDE.md`](CLAUDE.md) for AI assistant context (Claude Code, Copilot, etc.).
See [`api.yaml`](api.yaml) for the Groq API spec used by this app.

---

## License

MIT
