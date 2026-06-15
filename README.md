# Aura Music Native Android

Aura Music Native is a 100% native Android reconstruction of the Aura Music product specification from `aura.html`.

## Stack

- Kotlin 2.x
- Jetpack Compose
- Material 3
- Compose Navigation
- Hilt
- Coroutines / Flow / StateFlow
- Room
- Media3 ExoPlayer / MediaSession
- Ktor
- Coil
- Timber
- JUnit / MockK

## Modules

- `app`
- `core`
- `core-ui`
- `core-design`
- `core-domain`
- `core-database`
- `core-media`
- `core-network`
- `core-common`
- `feature-home`
- `feature-library`
- `feature-search`
- `feature-player`
- `feature-playlists`
- `feature-downloads`
- `feature-settings`
- `feature-lyrics`
- `sync`
- `analytics`

## Current native capabilities

- Runtime audio permission flow
- MediaStore audio scan into Room
- Offline library browsing
- Recently added / recently played / favorites surfaces
- Instant local search across songs, albums, artists, playlists, and lyrics table
- Media3 playback controller
- MediaSessionService declaration
- Mini player and full player
- Compose Material 3 Aura theme
- Lyrics storage surface
- Playlists creation and mutation repository
- Settings persistence with Kotlinx Serialization
- Replay analytics repository from local playback history

## Build

Open this directory in Android Studio and sync Gradle.

```bash
./gradlew :app:assembleDebug
```
