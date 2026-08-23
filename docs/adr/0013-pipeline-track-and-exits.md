# ADR-0013 — Pipeline Track and Terminal Stages

Status: Accepted · 2026-08-23

## Context

`PipelineStage` mixes two different things: stages an opportunity advances
through, and stages it ends at. Presenting progress requires knowing which is
which and in what order. An opportunity stores only its current stage; no
transition history is kept.

## Decision

- `PipelineStage` owns the distinction. `isTerminal` is an exhaustive `when`, so
  a new stage cannot be added without classifying it. The forward `track` is
  derived as the non-terminal entries in declaration order, and `trackIndex` is
  null for terminal stages.
- Progress is a function of the current stage alone. Terminal stages have no
  position on the track and are presented as an exit from it, not a point on it.
- Stage history is not recorded. Progress reached before a terminal stage is not
  displayed, because it is not known.

## Consequences

- Ordering and terminality are facts about the pipeline, not about the screen,
  so they travel to shared multiplatform code untouched; only the drawing stays
  in the UI layer.
- Deriving the track from `isTerminal` removes the second list that would
  otherwise drift from the enum.
- A terminal opportunity shows no partial progress. A `lastActiveStage` field
  was rejected: once history is wanted it is wanted with timestamps and reasons,
  which is a transitions table, and a single column would fix the wrong shape in
  the schema.

## Reverse cost

Cheap for the presentation, moderate for the data: adding a transitions table is
a new schema version and a write on every stage change, but nothing that exists
today has to change shape.
