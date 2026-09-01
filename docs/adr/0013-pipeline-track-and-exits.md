# ADR-0013 — Pipeline Track and Terminal Stages

Status: Accepted · 2026-08-23

## Context

`PipelineStage` holds two different things. Some stages are steps forward and
some are endings. Showing progress needs to know which is which and in what
order. An opportunity stores only its current stage and keeps no history.

## Decision

- `PipelineStage` owns the difference. `isTerminal` is an exhaustive `when`, so
  a new stage cannot be added without classifying it. The forward track is the
  entries that are not terminal, in declaration order, and the track index is
  null for a terminal stage.
- Progress comes from the current stage alone. A terminal stage has no place on
  the track and is drawn as an exit from it.
- Stage history is not recorded. Progress reached before a terminal stage is
  not shown, because it is not known.

## Consequences

- Order and terminality are facts about the pipeline and not about the screen,
  so they move to shared code untouched. Only the drawing stays in the UI.
- Deriving the track from `isTerminal` removes a second list that would drift
  away from the enum.
- A terminal opportunity shows no partial progress. A `lastActiveStage` field
  was rejected, because once history is wanted it is wanted with times and
  reasons, which is a transitions table.

## Reverse cost

Cheap for the drawing and moderate for the data. A transitions table is a new
schema version and a write on every stage change, but nothing that exists today
changes shape.
