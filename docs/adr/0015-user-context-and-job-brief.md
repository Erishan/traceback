# ADR-0015 — User Context and Job Brief

Status: Accepted · 2026-08-25

## Context

A brief that does not know the freelancer is a generic chatbot. ChatGPT
memory, Claude Projects, and Gems cannot be imported through official APIs.
The profile has to live in Traceback. Opportunity detail is already the
single editing surface (ADR-0008). Notes already prove a JSON column on the
opportunity row (ADR-0011).

## Decision

- One profile row in Room (`UserContext`): required `about` (stack, won’t-do,
  voice in one text), optional `rateBand`, optional `pace`. No separate
  Settings destination. Me is list → `MeKey`.
- A brief is one structured OpenAI call. System prompt = profile. User prompt
  = title, description, source, existing applied message. Response JSON is
  five fields: fit, proposal, price, duration, approach.
- The JSON is stored as `aiBrief` TEXT on `opportunities` (nullable). The
  domain type is `JobBrief?`. Parse failure degrades to “no brief”, not a
  crashed row (same spirit as ADR-0011, without turning garbage into a note).
- Detail still owns editing. Brief is a command on that screen: disabled when
  `about` is blank or no key is stored; in flight it does not invent a second
  source of truth — success writes Room, failure is transient ViewModel
  status.
- “Use as applied message” copies `proposal` through the existing
  `appliedMessage` write. The brief is not the applied message.

## Consequences

- Regenerating a brief overwrites `aiBrief`. There is no brief history.
- Price and duration are suggestions, not payment tracking (ADR-0001).
- Compose Multiplatform later shares `UserContext`, `JobBrief`, and the
  OpenAI client. Me chrome and `EncryptedSharedPreferences` stay platform
  code.

## Reverse cost

Cheap to add a field to `about`. Moderate to split profile into many columns
or a briefs table — both are migrations without a claim today.
