# ADR-0002 — Project Structure: One Module, Package by Feature

Status: Accepted · 2026-07-25 · Amended 2026-08-26

## Context

v1 is small: a few screens, one local data source, one feature. v1.1 adds a
`me` profile and an `ai` package. Two questions come before code. Modules or
packages, and organise by layer or by feature.

## Decision

- One Gradle module, `:app`, organised by feature.
- Each feature owns its layers as packages. `opportunity.domain` holds pure
  Kotlin models, enums and repository interfaces. `opportunity.data` holds
  Room, entities and mapping. `opportunity.ui` holds Compose and ViewModels.
- `data` and `ui` depend on `domain`. `domain` depends on nothing.
- Code shared by features goes in `core/`.

## Consequences

- Review holds the dependency direction, not the compiler. A framework import
  in `domain` would still compile. That is accepted at this size.
- Packaging by feature keeps a feature in one place instead of spreading it
  over three layer folders.
- One module avoids drawing module borders before they are proven, and a wrong
  border is expensive to move.
- The layers are already apart, so pulling out a module later is wrapping work
  and not untangling work.

## Reverse cost

Cheap to split into modules later.

## Amendment 2026-08-26

The shared extract happened (ADR-0018). Domain, data and AI live in `:shared`.
Packaging inside `:shared` does not change.
