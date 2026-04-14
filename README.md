# VideoFeed

A TikTok-style short video feed app built as a KMP portfolio project to showcase senior-level Android and Kotlin Multiplatform skills.

## Features

- **Auth** — JWT-based login and registration
- **Feed** — vertical swipe video feed with ExoPlayer, prefetch, and thumbnail loading
- **Upload** — direct-to-R2 video upload with presigned URLs and real-time progress
- **Backend** — Ktor REST API with PostgreSQL, Cloudflare R2 storage, and FFmpeg thumbnail generation

## Tech Stack

| Layer | Technology |
|---|---|
| Android UI | Compose + Material 3 |
| Video Playback | Media3 / ExoPlayer |
| KMP Shared | MVI ViewModels (Kotlin coroutines + StateFlow) |
| Swift Interop | SKIE |
| DI | Koin |
| Networking | Ktor HttpClient |
| Token Storage | DataStore (Android) |
| Backend | Ktor + Exposed + PostgreSQL |
| Object Storage | Cloudflare R2 (presigned URLs) |
| Auth | JWT + bcrypt |
| Thumbnails | FFmpeg (server-side, async) |
| Error Handling | `Result<T, E>` + `DataError` |

## Module Structure

```
VideoFeed/
├── composeApp/   # Android app — Compose UI, navigation, Koin Android
├── shared/       # KMP shared — domain, data, presenter (as packages)
└── server/       # Ktor backend
```

Layers (`domain`, `data`, `presenter`) are **packages inside `:shared`**, not separate Gradle modules.

## Architecture

Each feature follows a strict MVI pattern:

```
shared/src/commonMain/.../
└── <feature>/
    ├── domain/       # interfaces + error types
    ├── data/         # Ktor data sources + DTOs
    └── presenter/    # ViewModel + Contract (Action/Event) + UI models
```

Android-specific UI lives in `composeApp/src/androidMain/`.

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17+
- A running instance of the backend (or update `BASE_URL` in `local.properties`)

### Run Android App

```shell
# macOS/Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Run Server

```shell
# macOS/Linux
./gradlew :server:run

# Windows
.\gradlew.bat :server:run
```
