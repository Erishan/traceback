# ADR-0003 — Data Layer: Offline-First, Single Source of Truth

Status: Accepted · 2026-07-24

## Context

Opportunities are stored locally (Room) and later synced to a remote backend
(Firebase). When a local and a remote copy both exist, the data layer needs one
rule for which is truth and what happens to writes made offline.

## Decision

- Room is the single source of truth; the UI observes Room, never the network.
- Writes hit Room first (optimistic, observed immediately). Firebase sync runs
  afterward in the background and only ever feeds Room, never the UI.
- Domain models and persistence entities are separate classes, mapped in the
  data layer.

## Consequences

- The app works offline: writes complete locally and instantly, sync is deferred
  and invisible.
- Domain and UI never learn whether a remote source exists; they see Room-backed
  data through the repository interface.
- OpenAI (ADR-0014) is not a source of truth. A brief is a user-initiated
  command: the client calls `api.openai.com`, then writes the result to Room.
  The UI observes the row, never the HTTP response.
- Entities never cross into the domain — the mapping boundary keeps the
  abstraction from leaking, and lets persistence and business rules evolve
  independently.

## Reverse cost

Cheap to add or remove Firebase: `domain` depends only on the repository
interface (ADR-0002), so Firebase attaches as a data-layer source feeding Room.
The rejected design -Firebase as source of truth wired into the UI- would
instead ripple through every screen on removal.
