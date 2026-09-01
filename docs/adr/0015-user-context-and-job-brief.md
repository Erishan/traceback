# ADR-0015 — User Context and Job Brief

Status: Accepted · 2026-08-25

## Context

A brief that does not know the freelancer is a generic chatbot. Profiles held
in other tools cannot be imported through an official API, so the profile has
to live in Traceback. Detail is already the only editing surface, and notes
already prove that a JSON column works.

## Decision

- One profile row in Room, `UserContext`. `about` is required and holds stack,
  limits and voice in one text. `rateBand` and `pace` are optional. There is no
  settings destination.
- A brief is one structured OpenAI call. The system prompt is the profile. The
  user prompt is title, description, source and the current applied message.
  The answer has five fields: fit, proposal, price, duration, approach.
- The answer is stored as `aiBrief` TEXT on the opportunity row. A parse
  failure degrades to "no brief" and not to a broken row.
- Detail still owns editing. Brief is a command there. It is disabled when
  `about` is empty or no key is stored. Success writes Room and failure is
  short lived ViewModel status.
- "Use as applied message" copies the proposal through the existing write. The
  brief is not the applied message.

## Consequences

- Running a brief again overwrites the old one. There is no brief history.
- Price and duration are suggestions and not payment tracking.
- `UserContext`, `JobBrief` and the OpenAI client move to shared code as they
  are. The secret store stays platform code.

## Reverse cost

Cheap to add a field to `about`. Moderate to split the profile into many
columns or to add a briefs table, because both are migrations with no claim
today.
