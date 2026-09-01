# ADR-0012 — Schema Changes and Migration Tests

Status: Accepted · 2026-08-14

## Context

The database is the one source of truth and holds data older than any release,
so every schema change has to carry existing rows forward. Room can derive some
changes and not others, and a wrong migration is only found on a device that
already has data.

## Decision

- Schema JSON is exported for every version and committed.
- A change that only adds uses `@AutoMigration`. A hand written `Migration` is
  used only where the change cannot be derived, such as a rename, a split, a
  type change or a backfill.
- A new NOT NULL column takes the value for old rows from
  `@ColumnInfo(defaultValue = ...)`. A Kotlin default does not reach SQL and
  fails schema validation.
- Every version step has an instrumented test that seeds the old version, runs
  the migration and checks the result against the exported schema.

## Consequences

- A migration that loses data fails in CI and not on a phone, and the exported
  JSON makes the change readable in the diff.
- Migration tests need a device or an emulator, so unit tests alone cannot
  verify the data layer.
- Destructive fallback is off. A missing migration fails at build or test time
  and never loses data in silence.

## Reverse cost

Expensive once released. Dropping the exported schemas or the test gate removes
the only proof that databases already shipped can still be opened.
