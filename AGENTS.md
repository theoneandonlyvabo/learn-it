# Learn-it — AI Agent Guidelines

## Scope

Backend layer is complete (data, domain, DI, ViewModels). Frontend Compose screens
are **not yet implemented** — do not generate UI code unless explicitly asked.

## Before touching any file

1. Read `CLAUDE.md` for architecture and stack context.
2. Run `./gradlew :app:testDebugUnitTest` to confirm baseline is green before changes.
3. Check `gradle/libs.versions.toml` before adding any dependency — it may already be there.

## Layer rules

| Layer | Rule |
|---|---|
| `ui/viewmodel/` | Inject repositories only. Never inject DAOs or ApiService directly. |
| `data/repository/` | Single source of truth. Impls go here; interfaces are in the same package. |
| `data/remote/` | Groq API only. Auth header built by repo, not by the service interface. |
| `data/local/` | Room only. No business logic in DAOs. |
| `domain/model/` | Plain data classes and pure objects. No Android imports. |
| `di/` | `NetworkModule` = network + Firebase singletons. `RepositoryModule` = `@Binds` only. |

## Serialization

Use `@Serializable` + `kotlinx.serialization`. No Gson. No Moshi. No Jackson.

## DI

Use `@Inject constructor` + Hilt annotations. No manual DI. KSP processes Room and Hilt.

## Secrets

Never hardcode. Read from `BuildConfig.GROQ_API_KEY` (populated from `.env` at build time).

## Naming

- New remote DTOs: prefix `Groq` (e.g. `GroqRequest`).
- New domain models: plain noun (e.g. `Flashcard`, `LeaderboardEntry`).
- New repositories: `<Domain>Repository` interface + `<Domain>RepositoryImpl`.

## Do not

- Rename `MyApplication` — it is referenced by `android:name` in `AndroidManifest.xml`.
- Add `-ktx` Firebase artifacts — they are deprecated in BOM 34.x.
- Add kapt — project uses KSP exclusively.
- Write usecases — not part of this project's architecture.
- Use `android.os.CountDownTimer` — timer is a coroutine loop in `StudySessionViewModel`.
