# ADR-0017 — Theme Mode Is a Preference

Status: Accepted · 2026-08-26

## Context

ADR-0016 picked the scheme from the phone setting, so the phone was the only
voice. People run a phone in one mode and want one app in the other. Storing a
choice needs a preference that outlives the process, and the first frame has to
be painted in the chosen theme instead of corrected after it.

## Decision

- `ThemeMode` is `SYSTEM`, `LIGHT` or `DARK`. `SYSTEM` is a stored value like
  the others, not the absence of a choice.
- `AppearanceStore` lives in `settings/domain`. It observes a `Flow` to read
  and takes a `suspend` call to write. It also exposes a direct `current()`,
  which exists only so the first composition can pick a scheme.
- The Android implementation is `SharedPreferences` and not DataStore. It is
  one enum, read at start and written on a tap. The interface carries the cost
  of changing that later.
- The control on the Me screen is the picker the stages already use. Three
  answers do not fit the two positions of a switch, and a row of chips would
  spend the screen forever on a setting that changes once.
- Secrets keep their own encrypted store. This file holds no secret.

## Consequences

- The theme is app state now, so the activity reads it before composing and the
  tree still takes it as a parameter. Screens and previews did not change.
- Switching applies on the next frame with no restart, because the scheme is a
  `CompositionLocal` and not a resource qualifier.
- Two preference stores now exist, encrypted and plain, split by whether the
  value is a secret and not by convenience.

## Reverse cost

Cheap. Going back to the phone setting touches four files and leaves one unused
preference file on the device.
