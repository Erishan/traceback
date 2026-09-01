# ADR-0018 — Compose Multiplatform Foundation

Status: Accepted · 2026-08-26

## Context

ADR-0001 and ADR-0002 delayed the shared extract until domain and data were
free of Android. That point is reached. The hard failures in a shared build are
the platform seams, such as database path, secrets and HTTP engine, not the
screens. Shipping the seams first keeps Android green while it is proven that
iOS can open the same graph.

## Decision

- `:shared` is a Kotlin Multiplatform library holding domain, Room data, the AI
  use case and the container.
- `:app` is the Android app with Navigation 3 and thin ViewModels.
- `:ui` is a shared Compose library for theme, components and screens.
  See ADR-0019.
- `:iosCompose` is the iOS Compose shell and `iosApp/` is the Xcode host that
  embeds the framework.
- The Android side of a shared library uses the multiplatform library plugin.
- Platform seams: Room uses a bundled SQLite driver with a path builder per
  platform, `SecretStore` uses encrypted preferences on Android and Keychain on
  iOS, and HTTP uses Ktor CIO on Android and Darwin on iOS.
- Manual injection stays. Koin is still deferred.

## Consequences

- Android and iOS share one Room schema and its version history.
- Moving the UI into `:ui` is a separate step and not another untangling of
  domain and data.
- A full iOS simulator run needs a full Xcode install.

## Reverse cost

Moderate. Going back to one Android module is mechanical but touches every user
of the container. Expensive to change the Room driver or the secret store
interface after both platforms ship data.
