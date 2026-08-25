# ADR-0016 — Design Token Contract

Status: Accepted · 2026-08-25

## Context

The visual language was defined once, in a document, and then re-entered by hand at every call
site: hex literals in previews, `dp` in layouts, `sp` in text styles, durations inside `tween`.
Changing one visual decision meant finding every place that had copied it. The theme also had a
single dark scheme, so a light one could not be expressed at all.

## Decision

- The design contract is tokenised on four axes — colour, type, shape, and measure + motion — and
  those axes are the only source of visual values.
- Screens and shared components read tokens. They do not contain raw colour, size, corner,
  duration, or easing values. A one-off measurement belongs to a named `private val` at the top of
  the file that owns it.
- Tokens Material has a slot for live in `ColorScheme`, `Typography`, and `Shapes`. Everything else
  — glass fills, edges, aurora fields, stage colours, the third text rank — is carried by
  `CompositionLocal` and read as `TracebackTheme.colors`, `.dimens`, `.motion`.
- The theme is two complete instances, light and dark, selected from the system setting. Both are
  fully specified; neither falls back to a Material default.
- The type scale is closed and applies tabular figures to every role, because numbers appear in
  every role and must not shift the text around them.
- `TokenSheet` renders every token in both schemes as a preview. A value that cannot be seen there
  is not a token.

## Consequences

- One visual decision has one edit site, which is the property the redesign is actually buying.
- The theme layer is pure Compose except for the font seam in `AppFonts.kt`, so it moves to shared
  multiplatform code as-is; the font resource lookup is the only thing migration rewrites.
- Dynamic colour is rejected: the palette is the product's identity, not the wallpaper's to pick,
  and a generated scheme cannot express glass or the aurora fields.
- Raw values at call sites are rejected even where they are read-only, not because they are wrong
  today but because they are the mechanism by which the contract drifts.
- `CompositionLocal` costs an indirection Material does not, but the alternative is either bending
  unrelated Material slots to carry glass and stage colours, or passing them as parameters through
  every component.

## Reverse cost

Cheap. The tokens are values with names; removing the extra locals means inlining them back into
the scheme or into call sites, one mechanical pass with no behaviour to preserve.
