# ADR-0016 — Design Token Contract

Status: Accepted · 2026-08-25

## Context

The visual language was defined once in a document and then typed again at
every call site as hex, dp, sp and durations, so changing one decision meant
finding every copy. The theme also had a dark scheme only.

## Decision

- The contract is tokenised on four axes: colour, type, shape, and measure with
  motion. Those axes are the only source of visual values.
- Screens and components read tokens. They hold no raw colour, size, corner,
  duration or easing. A single measurement is a named private value in the file
  that owns it.
- Tokens that Material has a slot for live in `ColorScheme`, `Typography` and
  `Shapes`. The rest is carried by `CompositionLocal` and read as
  `TracebackTheme.colors`, `.dimens` and `.motion`.
- The theme is two complete schemes, light and dark. Neither falls back to a
  Material default.
- The type scale is closed and uses tabular figures everywhere, so numbers do
  not shift the text around them.
- `TokenSheet` draws every token in both schemes. A value that cannot be seen
  there is not a token.

## Consequences

- One visual decision has one edit site, which is what the redesign buys.
- The theme layer is pure Compose apart from the font seam, so it moves to
  shared code as it is.
- Dynamic colour is rejected. The palette is the identity of the product and
  not the wallpaper of the phone, and a generated scheme cannot express glass.

## Reverse cost

Cheap. The tokens are values with names, so removing them means putting them
back at the call sites in one mechanical pass.
