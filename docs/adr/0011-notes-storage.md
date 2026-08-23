# ADR-0011 — Notes Are Stored as a Serialized Column

Status: Accepted · 2026-08-14

## Context

An opportunity carries an ordered list of timestamped notes. Notes are only
ever read alongside the opportunity that owns them; nothing queries, sorts or
filters notes on their own, and nothing references a note from elsewhere.
ADR-0003 makes the database the single source of truth.

## Decision

- Notes are stored as a JSON array in one TEXT column on the opportunity row.
  There is no notes table and no foreign key.
- The domain exposes `List<Note>`; serialization lives in the data layer's
  mappers, at the same seam that maps entity to domain.
- A column that fails to parse becomes a single note carrying the raw text
  rather than an error, so an unreadable value degrades the row instead of
  failing the read.

## Consequences

- Adding, editing and deleting a note is one upsert of the owning row, and the
  list arrives already ordered with its opportunity.
- Notes are invisible to SQL: no per-note query, no cross-opportunity note
  search, no index. The entire list is re-serialized on every write.
- A relation was rejected as cost without a claim on it — the moment a note
  needs to be found without its opportunity, this becomes a table and a
  migration.

## Reverse cost

Moderate: the domain type does not change, but moving to a table means a new
schema version, a data migration that parses every existing column, and a
second DAO.
