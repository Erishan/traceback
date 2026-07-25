# ADR-0004 — Technology Choices

Status: Accepted
Date: 2026-07-25

## Context

Traceback needs a baseline technology stack fixed before implementation
begins. Most choices here are current standard Android defaults; they are
recorded not because they are contested but so the reasoning is explicit and
the stack is not silently inherited from a project wizard. Two areas with real
architectural weight — dependency injection and the async/reactive layer — are
deliberately excluded and handled in their own ADR (0005).

## Decision

- **Language: Kotlin.** The standard and only sensible choice for new Android
  work; also the path toward a later Kotlin Multiplatform move.
- **UI: Jetpack Compose.** Declarative UI is the current standard; the whole
  UI layer (ADR-0002) is built on it.
- **Build DSL: Kotlin (`.kts`).** Type-safe build scripts, consistent with a
  Kotlin codebase.
- **Local database: Room.** Follows directly from ADR-0003 — Room is the
  single source of truth. Room is the standard local persistence layer and
  integrates with Flow for the observation model.
- **Navigation: Compose Navigation.** Screen-to-screen navigation within the
  single-module UI layer.
- **minSdk: 26 (Android 8.0).** In practice the floor of modern Android:
  device loss above it is negligible for this project, while staying above it
  avoids backward-compatibility code for behaviors that settled at 26 —
  including the background and foreground-service restrictions that are central
  to this project's later stability scenarios.
- **targetSdk: 36 (Android 16).** The current required target for new apps on
  Google Play as of Aug 31, 2026. targetSdk is the platform whose rules the
  app opts into; keeping it current means the app runs under the latest
  behavior changes rather than legacy compatibility modes.

## Options considered

- **minSdk lower (e.g. 24)** → REJECTED. Wider device reach is irrelevant for
  a personal/portfolio tool, and it would force compatibility shims for APIs
  and behaviors that are clean from 26 onward.
- **minSdk higher (e.g. 33, matching the dev device)** → REJECTED. It would
  exclude common devices and emulators for no benefit, including anyone
  cloning the repo to run it.

## Consequences

- minSdk 26 / targetSdk 36 gives a clean modern baseline: the platform
  behaviors relevant to later stability work are available and consistent,
  without legacy branches.
- Dependency injection and the async/reactive layer are intentionally unfixed
  here; they carry enough weight to warrant their own decision and are
  deferred to ADR-0005.

## Cost to reverse

- Cheap: raising minSdk later (drops old devices, no code cost) or raising
  targetSdk (requires testing against new behaviors, but is expected annual
  maintenance).
- Moderate: lowering minSdk later would reintroduce compatibility handling for
  everything relied on above the old floor.