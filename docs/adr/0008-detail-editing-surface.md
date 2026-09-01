# ADR-0008 — Detail Is the Only Editing Surface

Status: Accepted · 2026-08-13

## Context

An opportunity has to be readable and editable after it is created. The create
sheet captures only title, description, source and stage, so notes and the
applied message cannot be reached. ADR-0006 assumed a separate edit destination
reached from the list.

## Decision

- One destination per opportunity, which is detail. There is no edit
  destination and no edit mode.
- Every field is edited in place. Enum fields save on selection. Text fields
  open inline and save on confirm.
- The detail ViewModel does not hold content. An intent writes through the
  repository and the screen redraws from the observed row. Only short lived
  status, such as a failed save or a failed delete, is ViewModel state.
- Creation stays a separate composable and ViewModel.

## Consequences

- There is no draft against saved copy to reconcile, because an uncommitted
  buffer belongs to the composable and dies with it.
- One shared form keyed on a nullable id was rejected, because create and edit
  already differ in delete, stage moves and layout, and will differ more.
- A field that saves on every keystroke writes a row per keystroke. The source
  label does this today and needs buffering if it grows.
- Deletion navigates through a single event and not through state, because
  popping the route cancels the scope that does the delete.

## Reverse cost

Cheap. Adding an edit destination later is a new key and a new route, not a
change to how detail reads or writes.
