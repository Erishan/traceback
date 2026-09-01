# ADR-0004 — Technology Choices

Status: Accepted · 2026-07-25

## Context

The base stack is written down so that it is a choice and not a default from a
project wizard. Most of it is the current Android default. Injection and the
async layer carry more weight and move to ADR-0005.

## Decision

- Kotlin, Jetpack Compose, Kotlin build files (`.kts`).
- Room for local storage.
- Compose Navigation for screens.
- minSdk 26, targetSdk 36.

## Consequences

- minSdk 26 loses very few devices and sits above the background and service
  behaviour changes that the later stability work depends on, so no
  compatibility shims are needed for behaviour that settled at 26.
- targetSdk 36 is what Play requires for new apps from 31 August 2026, so the
  app runs under current platform behaviour instead of legacy modes.
- Injection and the async layer are left open here on purpose.

## Reverse cost

Cheap to raise minSdk or targetSdk.
