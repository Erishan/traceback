# ADR-0017 — Theme Mode Is a Preference, Not the System Setting

Status: Accepted · 2026-08-26

## Context

ADR-0016 defines two complete schemes and selects between them with `isSystemInDarkTheme()`.
That makes the phone the only voice in the decision. People run a phone in one mode and want a
single app in the other, and a design whose light theme was drawn deliberately is worth being
able to look at on a dark phone. Storing a choice means the app needs a preference that outlives
the process, and the first frame has to be painted in the chosen theme rather than corrected
after it.

## Decision

- `ThemeMode` is `SYSTEM`, `LIGHT`, or `DARK`. `SYSTEM` is a stored value like the others, not the
  absence of a choice.
- `AppearanceStore` lives in `settings/domain`: `observe(): Flow<ThemeMode>` to read,
  `suspend setThemeMode` to write, per ADR-0005. It also exposes a synchronous `current()`, which
  exists solely so the first composition can pick a scheme; a Flow that has not emitted yet would
  paint one frame of the wrong theme.
- The Android implementation is `SharedPreferences`, not DataStore: one enum, read at startup,
  written on a tap. The interface is the seam that carries the cost of changing that.
- The control on the Me screen is the picker the pipeline stages already use — the current
  answer with a chevron, the alternatives only once asked for — not a switch and not a row of
  chips. Three answers do not fit a switch's two positions, and a permanent row of chips spends
  the screen forever on a setting that changes once. `TbPickerTrigger` is promoted to
  `ui/components` so both pickers are the same control rather than two that resemble each other.
- Secrets keep their own encrypted store. This file holds nothing worth encrypting.

## Consequences

The theme is now app state, so `MainActivity` reads it before composing and the whole tree still
takes it as the `darkTheme` parameter — screens and previews are untouched. Switching applies on
the next frame with no restart, because the scheme is a `CompositionLocal`, not a resource
qualifier. Two preference mechanisms now exist, encrypted and plain, split by whether the value
is a secret rather than by convenience. `SharedPreferences`, not DataStore, because the migration
cost is one file and the dependency is not; when this layer moves to `commonMain` that file is
where a multiplatform store lands.

## Reverse cost

Cheap. Deleting the store and passing `isSystemInDarkTheme()` again touches four files and leaves
an orphaned preference file on device.
