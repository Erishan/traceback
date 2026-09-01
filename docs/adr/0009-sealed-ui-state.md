# ADR-0009 — Sealed UI State for Preloaded Screens

Status: Accepted · 2026-08-13

## Context

Some screens have an honest value before data arrives and some do not. A list
has the empty list. A create form has the blank form. A detail screen loaded by
id has nothing until the row arrives, and the id may resolve to nothing.

## Decision

- A screen whose content can be missing models its state as a sealed interface
  with one variant per case: `Loading`, `NotFound`, `Content`. The content
  variant carries fields that are never null.
- A screen that always has an honest default keeps one data class with flags.
- The test is whether an honest value exists, not whether screens look alike.

## Consequences

- Detail has no nullable content fields and no loading flag. The `when` over
  the state is exhaustive, so a new case cannot be missed at the call site.
- A data class with flags was rejected here, because it forces either invented
  content or nullable fields that leak into every reader.
- Two shapes of UI state live in the code. That is fine only while the rule
  above is applied and the pattern is not copied.

## Reverse cost

Cheap. UI state is one file per screen, and its readers are that screen route
and composable.
