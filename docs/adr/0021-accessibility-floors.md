# ADR-0021 — Accessibility Floors Are Measured

Status: Accepted · 2026-09-01

## Context

The design is glass over a moving field, so a text colour that passes on one
screen can fail where the field is brightest, and no reviewer sees that by eye.
Touch targets drift the same way, because any call site can size a control
below the platform minimum.

## Decision

- The smallest touch target is one token, `MinTouchTarget` in `:ui`, and
  controls read it. There is no minimum per screen.
- `MinTouchTargetTest` pins the value of the token. `MinTouchTargetSemanticsTest`
  measures the drawn node on a device.
- WCAG AA is the floor: 4.5:1 for body text, 3:1 for everything else.
- `tools/contrast_audit.py` reads the token and aurora sources and measures
  every text role against the lightest and the darkest backdrop the field can
  make, on three surfaces, in both themes. It fails on any role below the floor.
- A role that fails is fixed. The threshold is not lowered, and a new surface
  is added to the audit instead of being excused from it.

## Consequences

- Contrast cannot drift in silence, because the audit reads the same files the
  app draws from, so a colour edit is measured again and not argued again.
- Glass edges and the rim of a selected chip are decorative and owe no ratio.
  The selection dot carries the same state at full strength and does owe 3:1.
- The audit is a script and not a Gradle task, so a colour change can still
  reach main without being measured.
- Alphas that are decisions rather than tokens are repeated in the audit, so
  changing one is a deliberate edit in two places.

## Reverse cost

Cheap to delete the audit. Expensive to rebuild the evidence it prints, which
is the contrast table in the design document.
