# ADR-0010 — Navigation Chrome Ownership

Status: Accepted · 2026-08-14

## Context

ADR-0006 places the back stack at the composition root. A single `Scaffold`
there had to serve every screen's chrome, and the screens disagree: the list
shows a title and a count, detail shows a back button and a delete action, and
future screens will show neither.

## Decision

- Each screen builds its own `Scaffold`, including its own snackbar host.
- The shared unit is the bar, not the scaffold: `TbTopAppBar`, a slot-based
  component taking `navigationIcon`, `title` and `actions` as composables.
- The title is always centred. Layout does not change with which slots are
  filled; a null title means the text is not drawn, not that the layout shifts.
- Window insets are consumed by the per-screen bar, since the root no longer
  has a scaffold to consume them.

## Consequences

- The composition root stays a bare `NavDisplay` that knows no feature, holding
  the isolation ADR-0002 requires — an app-level scaffold driven by the current
  key would import every feature and flicker across transitions.
- Slots take two actions, a badge, or none; a data parameter of icon and
  callback takes only what was foreseen.
- Chrome is duplicated per screen by construction. The bar absorbs the parts
  worth sharing; the rest is layout that legitimately differs.

## Reverse cost

Cheap: the bar is one component and each screen wires it in one place.
