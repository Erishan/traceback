# ADR-0020 — Test Placement Follows Module Ownership

Status: Accepted · 2026-09-01

## Context

Domain, data and AI moved to `:shared` and the UI moved to `:ui`, but their
tests stayed in the Android unit test folder of `:app`, so shared code was
checked on one platform only. The gate also has to decide what is worth an
emulator on every pull request.

## Decision

- A test lives in the module that owns the code it runs. `shared/src/commonTest`
  for domain, data and AI. `ui/src/commonTest` for UI logic that does not
  compose.
- Common tests use the `kotlin.test` API, so they run on every target the
  module declares and not on Android alone.
- `app/src/androidTest` holds only what needs a device: Room migration,
  repository transactions, touch target semantics and screenshot capture.
- The merge gate runs `check assembleDebug` for every module and then
  `connectedCheck` on an emulator, where the migration tests of ADR-0012 run.

## Consequences

- Shared logic now fails at compile time on iOS instead of after an iOS
  release, so the multiplatform claim is measured and not asserted.
- Compose screens have no behaviour tests. Screen rules are pulled into plain
  functions and tested there, because driving them through the semantics tree
  measures layout and animation more than it measures the rule.
- Screenshots are saved as files and not compared to a baseline, because over
  moving glass a baseline is a flake and not a gate. A person reads them.
- Every pull request pays for an emulator boot, which ADR-0012 requires.

## Reverse cost

Cheap to move a test between modules, because the API is the same. Expensive to
drop the device step, because migration coverage goes with it.
