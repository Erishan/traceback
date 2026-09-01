# ADR-0011 — Notes Are Stored in One Column

Status: Accepted · 2026-08-14

## Context

An opportunity carries an ordered list of notes with timestamps. Notes are only
ever read together with the opportunity that owns them. Nothing queries, sorts
or filters notes on their own, and nothing points at a note from elsewhere.

## Decision

- Notes are stored as a JSON array in one TEXT column on the opportunity row.
  There is no notes table and no foreign key.
- The domain exposes `List<Note>`. Serialisation lives in the data layer
  mappers, at the same seam that maps entity to domain.
- A column that fails to parse becomes one note holding the raw text, so a bad
  value degrades the row instead of failing the read.

## Consequences

- Adding, editing and deleting a note is one write of the owning row, and the
  list arrives in order with its opportunity.
- Notes are invisible to SQL. There is no query for one note, no search across
  opportunities and no index. The whole list is written again on every change.
- A table was rejected as cost with no claim on it. The day a note has to be
  found without its opportunity, this becomes a table and a migration.

## Reverse cost

Moderate. The domain type stays, but a table means a new schema version, a
migration that parses every existing column, and a second DAO.
