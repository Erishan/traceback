# ADR-0019 — Shared Aurora UI

Status: Accepted · 2026-08-29

## Context

ADR-0018 landed `:shared` and an iOS smoke shell. The list screen and aurora
components moved to `:ui` in follow-up commits, but detail, create, Me, strings,
and presentation logic still lived in `:app`. iOS could browse the pipeline but
could not open, edit, or brief an opportunity.

## Decision

- `:ui` owns all aurora surfaces in `commonMain`:
  - Theme, components, opportunity list/detail/create, Me screen.
  - Compose Multiplatform Resources for copy (`composeResources/values/strings.xml`,
    `Res` in `com.erishan.traceback.ui.theme`, `publicResClass = true`).
  - Label helpers in `TracebackStrings.kt` (stages, sources, filters, brief).
- Presentation logic sits in `:ui` as plain **controllers** (`*Controller.kt`)
  with `StateFlow` — no Android `ViewModel` in common code. `:app` ViewModels
  are thin wrappers over controllers for Nav3 + lifecycle.
- Platform seams in `:ui`:
  - `formatListDate` / `formatDetailTimestamp` — expect/actual per target.
  - `ComponentPreview` — `androidMain` only.
- `:app` keeps Navigation 3, routes, and Android-only previews.
- `:iosCompose` hosts a manual back stack (List → Detail → Me) and wires the
  same controllers. Create opens as a shared bottom sheet.
- iOS shell chrome:
  - SwiftUI `ComposeView` uses `.ignoresSafeArea(.all)` so safe area is applied
    once inside Compose (Material `Scaffold` / `TopAppBar`), not twice by UIKit
    and Compose.
  - Bootstrap drops the white Material `Surface` wrapper.

## Consequences

- Android and iOS render the same opportunity and Me UI; brief parity follows
  automatically because detail is shared.
- Android `strings.xml` in `:app` is reduced to `app_name` only; user-facing
  copy lives in `:ui` compose resources.
- Cross-package access to generated `Res.string.*` requires
  `import com.erishan.traceback.ui.theme.*` outside the theme package.
- iOS has no Navigation 3 yet; back stack is hand-rolled in `IosShellApp`.
- Unit tests for `DetailMutationGate` move to `:ui` `commonTest`.

## Reverse cost

Moderate: reverting means moving screens back to `:app` and restoring Android
`R.string` usage. Controllers can stay or fold back into ViewModels mechanically.
