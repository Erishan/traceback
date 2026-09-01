# ADR-0006 — Navigation

Status: Accepted · 2026-08-11 (supersedes the navigation choice in ADR-0004)

## Context

ADR-0004 named Compose Navigation before any screen wiring existed. The app now
needs a second destination reached from the list with a typed argument, and the
domain and data layers target Kotlin Multiplatform, where a library built for
Android alone fits poorly.

## Decision

- Navigation 3 for screens.
- The back stack is state the app owns. A list of `@Serializable` `NavKey`
  values lives at the composition root and `NavDisplay` draws it. Navigation is
  `add` and `removeLastOrNull` on that list.
- Destinations are typed keys that carry arguments as fields, not string routes
  that have to be parsed.
- Each feature owns its keys and registers its own destinations. The root wires
  navigation intents only.
- Each entry gets its own `ViewModelStore` and saved state through decorators.

## Consequences

- Navigation state is one source of truth and reactive, like the rest of the
  app. A ViewModel is cleared when its entry is popped, so no manual reset.
- The model moves to the shared targets. Compose Navigation was rejected
  because it fits a shared target less well and hides the back stack inside the
  library.
- Only navigation events cross the graph border, so entry wiring stays thin as
  screens grow.
- `lifecycle` moved to 2.11.0 and `compileSdk` to 37. targetSdk and minSdk did
  not change.

## Reverse cost

Moderate. The graph is at one root and each feature exposes its own keys, but
keys, decorators and back stack ownership all change shape.
