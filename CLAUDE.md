# VideoFeed — Claude Instructions

## Communication

Always be concise. Short, direct responses. No preamble, no summaries, no filler.

## Module Structure (CRITICAL)

There are exactly **3 Gradle modules**: `:composeApp`, `:server`, `:shared`.

**Never** create new Gradle modules. No `:core`, no `:feature:*`, no `:core:domain`, no `:core:data`.
Layers are **package folders**, not modules.

All KMP business logic lives in `:shared/src/` organized as:
- `br.gohan.videofeed.domain/` — models, Result, DataError, repository interfaces
- `br.gohan.videofeed.data/` — Ktor client, repositories, TokenStorage implementations
- `br.gohan.videofeed.presenter/` — ViewModels, MVI state/action/event

Platform-specific code goes in `androidMain/` and `iosMain/` inside `:shared`.

## Stack

- **Android UI**: Jetpack Compose + Media3/ExoPlayer
- **iOS UI**: SwiftUI + AVPlayer
- **Shared KMP**: MVI ViewModels + SKIE for Swift interop
- **DI**: Koin
- **Backend**: Ktor + Exposed + PostgreSQL
- **Storage**: Cloudflare R2 (presigned URLs)
- **Auth**: JWT + bcrypt
- **Error handling**: `Result<T, E>` + `DataError`

## Do Not

- Create new Gradle modules
- Use Hilt or Dagger (use Koin)
- Use LiveData (use StateFlow/SharedFlow)
- Use ViewModel from `androidx.lifecycle` directly in `:shared` (keep it KMP-compatible)
