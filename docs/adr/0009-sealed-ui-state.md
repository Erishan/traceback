# ADR-0009 — Sealed UI State for Preloaded Screens

Status: Accepted · 2026-08-13

## Context

Screens differ in whether their content has an honest value before data
arrives. A list has one — the empty list. A creation form has one — the blank
form. A detail screen loaded by id has none: until the row arrives, and if the
id resolves to nothing, there is no title, stage or source to show.

## Decision

- A screen whose content can be absent models its UI state as a sealed
  interface with one variant per structural case (`Loading`, `NotFound`,
  `Content`). The content variant carries non-null fields.
- A screen whose content always has an honest default keeps a single data class
  with flags.
- The discriminator is whether an honest value exists, not consistency across
  screens.

## Consequences

- Detail carries no nullable content fields and no `isLoading` flag; the
  `when` over the state is exhaustive, so a new case cannot be forgotten at the
  call site.
- A flagged data class was rejected for this shape because it forces either
  fabricated content or nullable fields that leak into every consumer.
- Two shapes of UI state coexist in the codebase, which is only defensible
  while the rule above is applied rather than the pattern copied.

## Reverse cost

Cheap: UI state is one file per screen and its consumers are that screen's
route and composable.
