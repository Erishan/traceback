# ADR-0012 — Schema Evolution and Migration Tests

Status: Accepted · 2026-08-14

## Context

The database is the single source of truth (ADR-0003) and holds data that
predates any given release, so every schema change must carry existing rows
forward. Room can derive some changes and not others, and a migration that is
wrong is only discovered on a device that already has data.

## Decision

- Schema JSON is exported for every version and committed.
- Additive changes use `@AutoMigration`. A hand-written `Migration` is used only
  where the change cannot be derived — a rename, a split, a type change, a
  backfill.
- A new NOT NULL column takes its value for existing rows from
  `@ColumnInfo(defaultValue = ...)`. A Kotlin default on the entity property
  does not reach SQL and fails schema validation.
- Every version step is covered by an instrumented test that seeds the previous
  version, runs the migration and validates the result against the exported
  schema. The exported schemas are wired in as test assets.

## Consequences

- A schema change that does not carry data forward fails in CI rather than on a
  user's device, and the exported JSON makes the change reviewable in the diff.
- Migration tests require a device or emulator, so the data layer cannot be
  fully verified by unit tests alone.
- Destructive fallback is not configured; a missing migration is a build-time
  or test-time failure, never silent data loss.

## Reverse cost

Expensive to abandon once released: dropping exported schemas or the test gate
removes the only evidence that shipped databases still open.
