# ADR-0008 — Opportunity Detail Is the Single Editing Surface

Status: Accepted · 2026-08-13

## Context

An opportunity must be readable and editable after creation. The create sheet
captures only title, description, source and stage, leaving notes and the
applied message unreachable. ADR-0006 assumed a separate edit destination
reached from the list. ADR-0003 makes the database the single source of truth.

## Decision

- One destination per opportunity: detail. There is no edit destination and no
  edit mode.
- Every field is edited in place. Enum fields commit on selection; text fields
  open an inline field and commit on confirm.
- The detail ViewModel does not own content. An intent writes through the
  repository and the screen re-renders from the observed row; the ViewModel
  never writes content into its own state. Only transient status — save failed,
  delete failed — is ViewModel state.
- Creation stays a separate composable and ViewModel, not an id-less mode of the
  same form.

## Consequences

- No draft-versus-saved divergence to reconcile: an uncommitted buffer is local
  to the composable that owns it and dies with it.
- Rejecting a shared form keyed on a nullable id keeps invalid states
  unrepresentable — create and edit already differ in delete, stage transitions
  and presentation, and will diverge further.
- A field that commits on every keystroke writes a row per keystroke; the source
  label does this today and needs buffering if it grows.
- Deletion navigates through a one-shot event, not state, because popping the
  route cancels the scope that performs the delete.

## Reverse cost

Cheap: adding a dedicated edit destination later means a new key and route, not
a change to how detail reads or writes.
