# ADR-0002 — Project Structure: Single Module, Package by Feature

Status: Accepted · 2026-07-25

## Context

v1 is small: a few screens, one local data source, one feature (opportunity
management). v1.1 adds a `me` profile feature and an `ai` package for a local
OpenAI key and job brief. Payment, statistics, and a Compose Multiplatform
extract are later. Two structural questions precede code: modules vs packages,
and organizing by layer vs by feature.

## Decision

Single Gradle module (`:app`), organized by feature. Each feature owns its
layers as packages:

- `opportunity.domain` — pure Kotlin: models, enums, repository interfaces.
- `opportunity.data` — Room, entities, mapping, repository implementations.
- `opportunity.ui` — Compose screens, ViewModels.

`data` and `ui` depend on `domain`; `domain` depends on neither. Shared code,
when it appears, goes in `core/`.

## Consequences

- Dependency direction is held by review, not the compiler: a stray framework
  import in `domain` would still compile. Accepted at this size.
- Package by feature keeps each feature self-contained instead of scattering it
  across three growing layer folders.
- A single module avoids drawing module boundaries before they are proven;
  moving a wrong boundary is expensive, and build times and team size do not yet
  justify compiler-enforced separation.
- Layers are package-separated from day one, so extracting modules later — and
  moving `domain`/`data` toward Kotlin Multiplatform — is a wrapping job, not an
  untangling one.

## Reverse cost

Cheap → moderate to go multi-module (layers already separated). Cheap to switch
feature/layer packaging now; costlier once several features exist.
