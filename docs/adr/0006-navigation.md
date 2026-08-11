# ADR-0006 — Navigation

Status: Accepted · 2026-08-11 (supersedes the navigation choice in ADR-0004)

## Context

ADR-0004 fixed navigation generically as Compose Navigation, before any
multi-screen wiring existed. The app now needs a second destination (edit)
reached from the list with a typed argument, and the domain and data layers
target Kotlin Multiplatform, where an Android-centric navigation library is a
poor fit. The model is chosen now, before screens multiply and a switch grows
expensive.

## Decision

- Navigation 3 (`androidx.navigation3`) for screen navigation.
- The back stack is app-owned state: a `NavBackStack` of `@Serializable`
  `NavKey`s held at the composition root (`App()`), rendered by `NavDisplay`.
  Navigation is `add` / `removeLastOrNull` on that list.
- Destinations are typed keys carrying arguments as fields; the list→edit
  argument is a key field, not a parsed string route.
- Each feature owns its keys and registers its destinations through an
  `EntryProviderScope` extension; the root composes them and wires only
  navigation intents.
- Per-entry `ViewModelStore` and `rememberSaveable` state via `NavDisplay`
  entry decorators.

## Consequences

- Navigation state is single-source-of-truth and reactive, consistent with the
  UDF already used elsewhere; a screen's ViewModel is cleared when its entry is
  popped, removing manual state reset.
- Portable to the shared multiplatform targets — Compose Navigation rejected as
  less aligned with a KMP target and as hiding the back stack inside the library.
- Only navigation events cross the graph boundary; per-screen actions stay in the
  route and ViewModel, so entry wiring stays thin as screens grow.
- `lifecycle` raised to 2.11.0 and `compileSdk` to 37 as the floor these
  libraries require; targetSdk 36 and minSdk 26 are unchanged.

## Reverse cost

Moderate: the graph lives at one root and each feature exposes its own keys, so a
switch touches `App()` and the per-feature entry extensions, not call sites — but
keys, decorators, and back-stack ownership all change shape.
