# ADR-0005 — Dependency Injection and Async Layer

Status: Accepted · 2026-07-28

## Context

Objects are built and connected in one place instead of inside the classes that
use them, and data reads and writes run off the main thread. The graph is small
today, and the domain and data layers will move to shared Kotlin code where
Android only tooling cannot run.

## Decision

- Manual injection. One composition root, `AppContainer`, builds the graph, and
  `Application` holds it for the life of the process.
- Dependencies arrive through constructors. No class builds its own. The
  container exposes `OpportunityRepository`, not the implementation.
- Coroutines for async. Reads return `Flow` and single writes are `suspend`.
  The dispatcher stays out of the domain interface.

## Consequences

- One instance rests on discipline, not on the compiler. There is one
  construction site with safe lazy setup.
- The graph carries no framework, so it moves to shared code. Hilt was rejected
  because it is Android only. Koin was rejected as too early and will be looked
  at again at the multiplatform step.
- Domain and UI know only the repository interface, never how it is built.

## Reverse cost

Cheap. The graph is one file, so moving to Hilt or Koin later touches that file
and the constructors, not the call sites.
