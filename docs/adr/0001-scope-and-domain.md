# ADR-0001 — Scope and Domain Model

Status: Accepted · 2026-07-24

## Context

Traceback follows a freelance opportunity from proposal to payment. Most
opportunities die, payment does not follow delivery, and work arrives from
several channels. The domain is fixed before code and the scope stays small.

## Decision

- v1 has one entity, `Opportunity`, on two independent axes.
- Stage: DRAFT, APPLIED, IN_CONVERSATION, INTERVIEW, HIRED, DELIVERED, CLOSED
  in that order. LOST is terminal and can be reached from any stage.
- Source: UPWORK, LINKEDIN, REFERRAL, OTHER. OTHER carries a free text label.
- v1 actions: add, change stage, list, filter, delete. All input is manual.
- Enums are stored as strings, not as ordinals.
- Payment, statistics and automatic collection are out of scope.

## Consequences

- Stage and payment stay apart, because a deposit can come before delivery,
  after it, or never. Payment is a later step.
- LOST keeps the app a pipeline instead of a log of won work, so win rate can
  be computed.
- A closed source enum stops "Upwork", "upwork" and "UpWork" from all
  appearing. Strings, not ordinals, as ordinals change the meaning of old
  rows when the enum is reordered.

## Reverse cost

Cheap to add a stage or a source. Expensive to merge the two axes or to change
enum storage, because both need a data migration.

## Amendment 2026-08-25

v1.1 adds a local OpenAI job brief driven by a profile stored on the device
(ADR-0014, ADR-0015). The v1 pipeline does not change.

## Amendment 2026-08-26

The shared extract is done (ADR-0018). `:shared` holds domain, data and AI.
`:app` holds the aurora UI.
