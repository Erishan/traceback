# ADR-0001 — Scope & Domain Model

Status: Accepted · 2026-07-24

## Context

Traceback tracks a freelance opportunity from proposal to payment: a pipeline
where most opportunities die, payment does not track delivery, and work arrives
from several channels. The domain is fixed before implementation, and scope is
kept narrow.

## Decision

v1 models one entity, `Opportunity`, on two independent axes:

- **Pipeline stage** — DRAFT → APPLIED → IN_CONVERSATION → INTERVIEW → HIRED →
  DELIVERED → CLOSED, plus LOST as a terminal state reachable from any stage.
- **Source** — enum UPWORK / LINKEDIN / REFERRAL / OTHER, with a free-text
  label when OTHER.

v1 actions: add, change stage, list/filter by stage and source, delete; all
entry is manual. Payment status, statistics, and automated collection are out
of scope. Enums persist as strings, not ordinals.

## Consequences

- Pipeline and payment are separate axes: a deposit can precede, follow, or
  never accompany delivery, which one chain cannot represent. Payment is a
  later increment (amendment 2026-08-26).
- LOST keeps the app a real pipeline rather than a log of won work, and makes
  win rate computable.
- A closed source enum prevents the "Upwork / upwork / UpWork" fragmentation a
  free string invites. String enum storage survives reordering and inserted
  values; ordinal storage would shift the meaning of existing rows.

## Reverse cost

Cheap to add an enum value or stage. Expensive to merge the two axes or change
enum storage format — both require a data migration.

## Amendment 2026-08-25

v1.1 is a local OpenAI job brief driven by an on-device profile (ADR-0014,
ADR-0015). The v1 opportunity pipeline is unchanged.

## Amendment 2026-08-26

The Android brief loop and aurora identity are in. The next product step is a
Compose Multiplatform iOS build that shares `domain`/`data` (and the OpenAI
client) already kept free of Android. Payment tracking, statistics, and other
model providers (Claude, Gemini) stay deferred until after that.
