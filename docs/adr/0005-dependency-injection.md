# ADR-0005 — Dependency Injection and Async Layer

Status: Accepted · 2026-07-28

## Context

Objects must be constructed and connected in one place rather than scattered
across the classes that use them, and the data layer's reads and writes run off
the main thread. ADR-0004 deferred both the injection strategy and the
concurrency/reactive contract here. The object graph is currently small,
and the domain and data layers are meant to move to shared Kotlin
Multiplatform code, where Android-only tooling cannot run.

## Decision

- Manual injection: one composition root (`AppContainer`) builds the graph;
  `Application` holds it for the process lifetime.
- Dependencies are constructor-injected; no class builds its own. The container
  exposes `OpportunityRepository`, not the implementation.
- Coroutines for async: reads return `Flow`, one-shot writes are `suspend`. The
  dispatcher stays out of the domain interface.

## Consequences

- Single-instance rests on discipline, not the compiler: one construction site,
  thread-safe lazy initialization.
- Framework-free and portable to the shared layer — Hilt rejected as Android-only
  (dead in shared code); Koin rejected as premature, revisited at the
  multiplatform step.
- Domain and UI depend only on the repository interface, never on construction.

## Reverse cost

Cheap: the graph lives in one file, so adopting Hilt or Koin later touches the
composition root and constructors, not call sites.
