# ADR-0001 — Scope & Domain Model

Status: Accepted
Date: 2026-07-24

## Context

Traceback is a personal tool for tracking a freelance opportunity from
proposal to payment. Because the tool models a real, messy pipeline (many
opportunities die, payment doesn't move in lockstep with delivery, work
arrives from several channels), the domain must be defined carefully up
front. Scope is kept deliberately narrow: a small, correct core is more
valuable than a broad, shallow one, and leaving features out creates clear,
well-defined room to add them later.

## Decision

### v1 core actions
- Add an opportunity (manual entry)
- Change an opportunity's pipeline stage (including the LOST terminal state)
- List opportunities and filter by stage / source
- Delete an opportunity

### Domain concepts
- **Opportunity** — the central domain model.
- **Pipeline stage** — where an opportunity currently sits. Values:
  DRAFT → APPLIED → IN_CONVERSATION → INTERVIEW (optional/skippable)
  → HIRED → DELIVERED → CLOSED, plus LOST, reachable from any stage
  (terminal). The model is designed to be configurable; v1 ships no
  editing UI for stages.
- **Source** — the channel an opportunity came from. Enum: UPWORK,
  LINKEDIN, REFERRAL, OTHER. When OTHER is selected, a separate free-text
  label field (sourceLabel) is filled. The enum is extended only by adding
  values in code (compile-time); users cannot add sources at runtime.
- **Payment status** — an axis independent of the pipeline. Not in v1
  (see more on Out of scope).

### Out of scope (explicitly NOT built in v1)
- Payment axis (NONE / DEPOSIT_PAID / FULLY_PAID) → v1.1, planned as the
  first add-a-feature increment (requires a schema migration).
- Statistics / win-rate screen → v1.2.
- Automatic opportunity collection (Upwork / LinkedIn / mail integration)
  → separate project. All entry in v1 is manual.
- Reminders / notifications.
- Interview notes, message history.
- Stage-editing UI (model is ready for it; the interface comes later).

## Options considered

- **Collapsing pipeline and payment into a single chain** → REJECTED. In
  reality a deposit may be taken before, after, or never relative to
  delivery, and some jobs are paid in installments. A single axis cannot
  represent this, so payment is modeled as its own independent axis.
- **A forward-only pipeline with no terminal failure state** → REJECTED.
  Most opportunities die (no reply to a proposal, not selected, ghosted).
  Without LOST there is no way to compute win rate, and the app degrades
  into a record of won work only rather than a true pipeline.
- **Free-form string for source** → REJECTED. Variants like "Upwork" /
  "upwork" / "UpWork" fragment the data and break any later aggregation.
  A closed enum with an OTHER escape hatch was chosen instead.

## Consequences

- **Enums are persisted as strings, never as ordinal/int.** Ordinal
  storage silently corrupts existing rows when a value is later inserted
  mid-enum, because stored positions shift meaning. String storage is
  immune to reordering and to adding new values. This is a deliberate
  correctness decision, not merely a preference.
- A narrow v1 turns payment and statistics into clean, self-contained
  later increments. Leaving these gaps is intentional.
- Configurable stages and a code-extensible source enum keep the model
  flexible without adding UI cost in v1.

## Cost to reverse

- Cheap: adding a value to the source enum; adding a new pipeline stage.
- Expensive: undoing the decision to model payment as a separate axis and
  collapsing it back into one; changing the enum persistence format
  between string and ordinal (both require a data migration).