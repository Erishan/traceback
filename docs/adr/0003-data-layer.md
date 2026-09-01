# ADR-0003 — Data Layer: Offline First, Single Source of Truth

Status: Accepted · 2026-07-24

## Context

Opportunities are stored locally in Room and may later sync to a remote
backend. When a local copy and a remote copy both exist, the data layer needs
one rule for which one is true and what happens to writes made offline.

## Decision

- Room is the one source of truth. The UI observes Room and never the network.
- Writes go to Room first and are seen at once. Sync runs later in the
  background and only ever feeds Room.
- Domain models and database entities are separate classes. The data layer maps
  between them.

## Consequences

- The app works offline. A write finishes locally and sync stays invisible.
- Domain and UI never learn whether a remote source exists.
- OpenAI is not a source of truth. A brief is a user command. The client calls
  the API and writes the result to Room, and the UI observes the row.
- Entities never reach the domain, so storage and business rules can change on
  their own.

## Reverse cost

Cheap to add or drop a remote source, because `domain` only knows the
repository interface. A remote source of truth wired straight into the UI was
rejected, because removing it would then touch every screen.
