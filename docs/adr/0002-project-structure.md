# ADR-0002 — Project Structure: Single Module, Package by Feature

Status: Accepted
Date: 2026-07-25

## Context

Traceback's v1 is small: a handful of screens and a single local data source,
with essentially one feature (opportunity management). Payment (v1.1) and
statistics (v1.2) are already on the roadmap. Two structural questions must be
settled before code: how to separate concerns physically (modules) vs
conceptually (packages), and whether to organize the code by layer or by
feature.

## Decision

Ship v1 as a **single Gradle module** (`:app`), organized **by feature**. Each
feature owns its three layers as packages:

 - .opportunity.domain — pure Kotlin: models, pipeline
stage, source enum, repository
interfaces. Depends on nothing.
 - .opportunity.data — repository implementations,
Room persistence, entities,
mapping. Depends on domain. 
 - .opportunity.ui — Compose screens, ViewModels.
Depends on domain.

Future features (payment, statistics) become sibling packages with the same
internal layering. Dependencies point inward toward each feature's domain;
domain depends on neither data nor ui, maintained by discipline rather than by
the compiler.

## Options considered

- **Package by layer** (top-level `domain/`, `data/`, `ui/`, each holding all
  features) → REJECTED. As feature count grows, each layer package accumulates
  every feature's files, scattering the code for any one feature across three
  swelling folders. Package by feature keeps each feature self-contained; the
  number of feature packages grows but each stays independently workable.
- **Multi-module from the start** (separate `:domain`, `:data`, `:ui` or
  per-feature modules) → REJECTED for v1. At this size, with boundaries not yet
  proven, splitting up front draws walls that may turn out wrong, and moving a
  wrong module boundary is expensive. Multi-module earns its cost once build
  times or team boundaries justify compiler-enforced separation.

## Consequences

- Dependency direction is not compiler-enforced in v1. A stray framework import
  in a domain package will compile and run; discipline and code review are the
  only guardrails. Accepted at this size.
- A new feature is added as a new package with its own layers, without touching
  existing features.
- Cross-feature shared code (e.g. an enum used by both opportunity and
  statistics) is not yet needed; when it appears it will go in a dedicated
  `core/` package rather than being duplicated or reached across features.
- Because layers are cleanly separated by package from day one, extracting them
  into modules later is cheap — code is wrapped in module boundaries, not
  untangled. This also eases a later move of domain and data toward Kotlin
  Multiplatform.

## Cost to reverse

- Cheap → moderate: single module to multi-module, since layers are already
  package-separated (create module files, re-declare dependencies).
- Cheap: switching between package-by-feature and package-by-layer is a package
  move, not a code rewrite — but doing it after several features exist means
  moving many files, so settling it now avoids that.