# ADR-0010 — Navigation Chrome Ownership

Status: Accepted · 2026-08-14

## Context

ADR-0006 puts the back stack at the composition root. One `Scaffold` there
would have to serve every screen, and the screens disagree. The list shows a
title and a count, detail shows a back button and a delete action, and later
screens will show neither.

## Decision

- Each screen builds its own `Scaffold`, including its own snackbar host.
- The shared piece is the bar and not the scaffold. `TbTopAppBar` takes
  `navigationIcon`, `title` and `actions` as slots.
- The title is always centred. The layout does not move when a slot is empty.
  An empty title means the text is not drawn, not that the layout shifts.
- The bar of each screen consumes the window insets, because the root no longer
  has a scaffold that could.

## Consequences

- The composition root stays a bare `NavDisplay` that knows no feature, which
  keeps the isolation ADR-0002 asks for. A root scaffold driven by the current
  key would import every feature and flicker between screens.
- Slots take two actions, a badge, or nothing. A data parameter of icon and
  callback would take only what was foreseen.
- Chrome is repeated per screen by design. The bar holds the part worth
  sharing, and the rest is layout that really differs.

## Reverse cost

Cheap. The bar is one component and each screen wires it in one place.
