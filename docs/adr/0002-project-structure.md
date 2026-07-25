# ADR-0002 — Project Structure: Single Module, Layered

Status: Accepted
Date: 2026-07-24

## Context

Traceback's v1 is small: a handful of screens and a single local data
source. The eventual size and boundaries of the project are unknown. A clean
architecture (clear separation between domain, data, and UI) is required, but
that separation can be achieved either physically (separate Gradle modules,
enforced by the compiler) or conceptually (packages within one module,
enforced by discipline).

## Decision

Ship v1 as a **single Gradle module** (`:app`) with three layers expressed
as packages:

- **domain/** — pure Kotlin. Opportunity model, pipeline stage, source enum,
  and the repository interfaces. Depends on nothing outside itself.
- **data/** — repository implementations, persistence (Room), entities, and
  mapping between entities and domain models. Depends on domain.
- **ui/** — Compose screens and ViewModels. Depends on domain.

Dependencies point inward toward domain; domain depends on neither data nor
ui. This is the same dependency direction a multi-module setup would enforce,
here maintained by discipline rather than by the compiler.

## Options considered

- **Multi-module from the start** (separate `:domain`, `:data`, `:ui`
  modules) → REJECTED for v1. For a project this small, with boundaries not
  yet known, splitting up front draws walls in places that may turn out
  wrong, and re-drawing a wrong module boundary is expensive. This is
  premature modularization and over-engineering at this size. Multi-module
  becomes worth its cost as the project grows and build times or team
  boundaries justify compiler-enforced separation.

## Consequences

- Dependency direction is not compiler-enforced in v1. A stray framework
  import in domain will compile and run; discipline and code review are the
  only guardrails. This is an accepted trade-off at this size.
- Because the three layers are cleanly separated by package from day one,
  extracting them into separate modules later is cheap. The code does not need
  to be untangled, only wrapped in module boundaries with declared
  dependencies. The expensive path (mixing layers and separating later) is
  avoided.
- The clean domain/data split also eases a later move of domain and data
  toward Kotlin Multiplatform.

## Cost to reverse

- Cheap → moderate: moving from single module to multi-module. Since layers
  are already package-separated, the work is creating module files and
  re-declaring dependencies, not restructuring code. The discipline invested
  now (keeping domain import-clean) is what keeps this cost low.