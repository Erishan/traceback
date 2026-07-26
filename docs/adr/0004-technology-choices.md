# ADR-0004 — Technology Choices

Status: Accepted · 2026-07-25

## Context

The baseline stack is fixed before implementation and recorded so it is
explicit rather than inherited from a project wizard. Most choices are current
Android defaults. Dependency injection and the async/reactive layer carry more
weight and are deferred to ADR-0005.

## Decision

- Kotlin, Jetpack Compose, Kotlin build DSL (`.kts`).
- Room for local persistence (follows ADR-0003; integrates with Flow).
- Compose Navigation for screen navigation.
- minSdk 26 (Android 8.0), targetSdk 36 (Android 16).

## Consequences

- minSdk 26 is the practical floor of modern Android: device loss below it is
  negligible, and it sits above the background and foreground-service behavior
  changes this project's later stability work depends on - avoiding
  compatibility shims for behaviors that settled at 26.
- targetSdk 36 is the required Play target for new apps from Aug 31, 2026;
  keeping it current runs the app under the latest platform behavior rather than
  legacy compatibility modes.
- DI and the async layer are intentionally unfixed here (ADR-0005).

## Reverse cost

Cheap to raise minSdk (drops old devices) or targetSdk (annual maintenance,
needs testing against new behaviors). Moderate to lower minSdk later -
reintroduces compatibility handling for everything relied on above the floor.
