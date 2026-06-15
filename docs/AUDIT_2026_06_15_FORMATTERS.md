# Formatter / Dependency Audit — 2026-06-15

## Issue

GitHub Actions failed in `core-ui/src/main/java/ai/arena/aura/core/ui/Components.kt`:

```text
Unresolved reference 'common'
Unresolved reference 'AuraFormatters'
```

## Root cause

`AuraFormatters` already existed in:

```text
core-common/src/main/java/ai/arena/aura/core/common/Formatters.kt
```

with package:

```kotlin
package ai.arena.aura.core.common
```

`core-ui` imported it correctly:

```kotlin
import ai.arena.aura.core.common.AuraFormatters
```

but `core-ui/build.gradle.kts` did not declare a dependency on `core-common`.

## Decision

`AuraFormatters` belongs in `core-common`, not `core-ui`, because it is a pure shared formatting utility used by both UI and player feature code.

## References found

```text
core-common/src/main/java/ai/arena/aura/core/common/Formatters.kt
core-common/src/test/java/ai/arena/aura/core/common/AuraFormattersTest.kt
core-ui/src/main/java/ai/arena/aura/core/ui/Components.kt
feature-player/src/main/java/ai/arena/aura/feature/player/PlayerRoute.kt
```

## Changes

1. Added `implementation(project(":core-common"))` to `core-ui/build.gradle.kts`.
2. Rewrote `Components.kt` imports explicitly.
3. Removed unused `LazyRow` / `items` imports from `Components.kt`.
4. Verified package declarations match folder structure by static audit.
5. Verified project dependency graph by static audit.

## Validation

Static dependency audit result:

```text
dependency_audit= OK
package_audit= OK
```

Local Gradle execution could not be completed in the sandbox because Gradle is not installed:

```text
gradle: command not found
```

The GitHub Actions workflow remains the authoritative compile validation environment for `:app:assembleDebug`.
