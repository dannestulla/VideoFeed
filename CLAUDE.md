# VideoFeed — Claude Instructions

## Module Structure

This project has exactly 3 Gradle modules:
- `:composeApp` — Android app (Compose UI, Koin Android, Navigation)
- `:shared` — KMP shared code (domain, data, presenter layers as packages)
- `:server` — Ktor backend

Layers (domain, data, presenter) are **packages inside `:shared`**, never separate Gradle modules.

## MVI Screen Structure

Every screen has exactly **2 files**:

```
presenter/<feature>/<screen>/
  <Screen>Contract.kt   ← State + Action + Event (top-level declarations, no wrapping object)
  <Screen>ViewModel.kt
```

### Contract file pattern

```kotlin
// LoginContract.kt
data class LoginState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data object OnSubmit : LoginAction
}

sealed interface LoginEvent {
    data object NavigateToFeed : LoginEvent
}
```

Do **not** wrap in an object or companion. Top-level declarations only.

## Koin DI

- `koin-core` in `:shared` commonMain for data/domain modules
- `koin-android` in `:composeApp` only — `viewModelOf` DSL is Android-specific
- ViewModel registrations live in `composeApp/src/androidMain/.../di/AppModules.kt`
- `baseUrl` passed via `properties(mapOf("baseUrl" to BuildConfig.BASE_URL))` in Application

## Tests

- `:shared` commonTest uses Turbine + AssertK + MockEngine
- Fakes go in their own files (e.g. `FakeAuthDataSource.kt`), not inline in test files
- Use `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@BeforeTest`
