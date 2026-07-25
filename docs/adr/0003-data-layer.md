# ADR-0003 — Data Layer: Offline-First, Single Source of Truth

Status: Accepted
Date: 2026-07-24

## Context

Traceback stores opportunities locally (Room) and, later, syncs them to a
remote backend (Firebase). This raises a question the UI cannot answer on its
own: when both a local and a remote copy exist, which one does the screen
show, and what happens to writes made while offline? The data layer needs a
single, explicit rule for where truth lives and how data flows.

## Decision

- **Room is the single source of truth.** The UI observes Room, never the
  network directly.
- **Flow:** UI reads from Room. Firebase feeds Room when reachable; it never
  feeds the UI. Writes hit Room first and are observed immediately; Firebase
  sync runs afterward in the background.
- **Domain models and persistence entities are separate classes**, mapped in
  the data layer. `Opportunity` is shaped by business rules, `OpportunityEntity`
  by persistence; they evolve independently so persistence changes cannot force
  a change in business rules.

## Options considered

- **UI observes Firebase directly** → REJECTED. Every data operation would
  require a live connection; offline the app would be inefficient or unusable,
  and a write made while offline could fail and be lost. Making Room the
  source of truth lets writes complete locally and instantly, with sync
  deferred and invisible to the user.
- **A single shared class for both domain and persistence** → REJECTED. It
  couples the business rules to Room; a persistence-driven change (a new
  technical column, a Room constraint) would leak into the domain. Separate
  classes keep the domain clean; the small duplication is deliberate
  decoupling, not redundancy.

## Consequences

- Writes are optimistic: they land in Room and appear in the UI without
  waiting for the network. Sync reconciles with Firebase later.
- The UI and domain are unaware of whether a remote source exists at all; they
  only ever see Room-backed data through the repository interface.
- Entities never cross into the domain. The mapping boundary in the data
  layer is what prevents a leaky abstraction.

## Cost to reverse

- Cheap: adding or removing Firebase. The domain depends only on a repository
  interface (ADR-0002), so Firebase attaches as a data-layer source that feeds
  Room; therefore domain and UI untouched.
- The rejected design (Firebase as SSOT, wired into the UI) would instead
  ripple through every screen on removal.