# ADR-0018 — Compose Multiplatform Foundation

Status: Accepted · 2026-08-26

## Context

ADR-0001 and ADR-0002 deferred a multiplatform extract until domain and data
were proven Android-free. That extract is now due: Room 3, Ktor, and package
boundaries already pointed at shared source sets. The hard CMP failures are
platform seams (database path, secrets, HTTP engine, composition root), not
aurora screens. Shipping those seams first keeps Android green while proving
iOS can open the same graph.

## Decision

- Gradle modules:
  - `:shared` — KMP library (`commonMain` + `androidMain` + `iosMain`) holding
    domain, Room data, AI use case / Ktor client, and `SharedContainer`.
  - `:app` — Android application; Navigation 3 routes and thin ViewModels;
    depends on `:shared` and `:ui`.
  - `:ui` — KMP Compose library (`commonMain` + `androidMain` + `iosMain`) for
    aurora theme, components, opportunity/Me screens, controllers, and compose
    resources. See ADR-0019.
  - `:iosCompose` — iOS Compose Multiplatform shell (list, detail, create, Me);
    exports `:shared` and `:ui` as the `IosCompose` framework.
  - `iosApp/` — thin Xcode host that embeds the framework via
    `embedAndSignAppleFrameworkForXcode`.
- Android KMP library plugin is `com.android.kotlin.multiplatform.library`
  (AGP 9); not `com.android.library`.
- Platform seams:
  - Room: `BundledSQLiteDriver` + path builders (Context / NSDocumentDirectory).
  - Secrets: `SecretStore` — EncryptedSharedPreferences on Android, Keychain on
    iOS.
  - Appearance: SharedPreferences / NSUserDefaults.
  - HTTP: Ktor CIO on Android, Darwin on iOS.
- Manual DI stays (`SharedContainer`); Koin remains deferred (ADR-0005).
- Aurora UI migration to `:ui` is tracked in ADR-0019 (not part of this
  foundation slice).

## Consequences

- Android and iOS share one Room schema (v1–4 history under `shared/schemas`).
- UI share is a separate move into `:ui` (ADR-0019), not another untangling of
  domain/data.
- Full iOS simulator launch needs a full Xcode install (not Command Line Tools
  alone); `:shared:compileKotlinIosSimulatorArm64` validates the shared graph
  without linking Compose.

## Reverse cost

Moderate: collapsing back to a single Android module is mechanical but touches
every consumer of `SharedContainer`. Expensive to change the Room driver or
secret-store interfaces after both platforms ship data.
