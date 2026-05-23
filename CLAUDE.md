# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & common commands

```bash
# Assemble debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.example.myapplication.ExampleUnitTest"

# Run instrumented tests (requires running emulator or device)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

## Architecture

### Navigation — two layers

**Outer** (`ui/navigation/NavGraph.kt`): Compose Navigation with three routes — `login`, `register`, `main`. Start destination is decided at runtime based on `AuthStorage.isLoggedIn()`. Register → Login passes a success message via `savedStateHandle`.

**Inner** (`ui/main/MainScreen.kt`): Once logged in, all screens live inside a single `Scaffold` with a `PrimaryTabRow` (4 tabs: Shorten, Detail, Analytics, Settings). Tab content is swapped with `AnimatedContent`. `MainViewModel` (Compose `mutableStateOf`) owns `selectedTab` and `selectedLink` and is the only cross-tab shared state.

### Dependency injection — manual via Application class

`LilUrlApp` creates and holds `authStorage`, `authRepository`, and `urlRepository` as lazy singletons. Screens retrieve them via `LocalContext.current.applicationContext as LilUrlApp`. No Hilt/Dagger.

### State flow

Repositories return `util.Result<T>` (Success/Error). ViewModels map this to `util.UiState<T>` (Idle/Loading/Success/Error) exposed as `StateFlow` or Compose `mutableStateOf`. Screens collect with `collectAsStateWithLifecycle()` or read directly.

`LaunchedEffect` on state values is the main side-effect pattern (navigate on success, show snackbar on error, clear token on 401).

### Backend

Single Retrofit `ApiService` against `https://lil-url-production.up.railway.app/api/`. `AuthInterceptor` injects the JWT from `AuthStorage` into every request. The server's `shortUrl` field returns an internal hostname — repositories rewrite it to `ROOT_URL/s/{shortCode}` before surfacing it to the UI.

JWT is stored in `EncryptedSharedPreferences` (AES-256-GCM).

### Mock data boundary

The **Shorten tab** (`ShortenScreen` + `ShortenViewModel`) and **Links screen** (`LinksScreen`) hit the real API. The **Detail**, **Analytics**, and **Settings** tabs are fully mocked — they read from `data/mock/MockData.kt` and display `WipBanner` components to mark unconnected sections. Connecting them to the backend means wiring `mainVm.selectedLink` (currently `MockLink`) to real API models and removing the `WipBanner` calls.

### Canvas components

`QrCodeDisplay` encodes with ZXing and rasterizes to `ImageBitmap` on `Dispatchers.Default` via `produceState` — do not move this work back to composition. `SparklineChart`, `LineChart`, `BarChart`, and `HeatmapGrid` are pure Canvas draw components that take pre-computed data lists.
