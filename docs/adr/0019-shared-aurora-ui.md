# ADR-0019 — Shared Aurora UI

Status: Accepted · 2026-08-29

## Context

ADR-0018 landed `:shared` and an iOS smoke shell. The list screen and the
components had moved to `:ui`, but detail, create, Me, strings and presentation
logic still lived in `:app`. iOS could browse the pipeline but could not open,
edit or brief an opportunity.

## Decision

- `:ui` owns every aurora surface in common code: theme, components, list,
  detail, create and Me.
- User facing copy lives in Compose Multiplatform resources inside `:ui`, with
  label helpers in `TracebackStrings.kt`.
- Presentation logic sits in `:ui` as plain controllers with `StateFlow`. There
  is no Android `ViewModel` in common code. The `:app` ViewModels are thin
  wrappers for navigation and lifecycle.
- Platform seams in `:ui` are date formatting per target and previews on
  Android only.
- `:app` keeps Navigation 3, routes and Android previews. `:iosCompose` runs a
  back stack written by hand and wires the same controllers.
- The iOS host lets Compose apply the safe area once, instead of UIKit and
  Compose both applying it.

## Consequences

- Android and iOS draw the same opportunity and Me screens, so brief parity
  follows on its own.
- The Android string file is reduced to the app name.
- iOS has no Navigation 3 yet, so its back stack is written by hand.

## Reverse cost

Moderate. Going back means moving screens to `:app` and restoring Android
string lookups. Controllers can stay or fold into ViewModels mechanically.
