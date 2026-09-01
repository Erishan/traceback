# Architecture decision records

These files capture why Traceback is built the way it is.

- Numbered in chronological order. A later ADR can amend or supersede an earlier one.
The best approach is read forward when two entries touch the same topic.

| # | ADR | Decision | Status |
|---|---|---|---|
| 0001 | [Scope and domain model](0001-scope-and-domain.md) | v1 has one `Opportunity` entity on stage and source axes; actions are manual; payment, statistics and automatic collection are out of scope | Accepted |
| 0002 | [Project structure](0002-project-structure.md) | One Gradle module `:app` organised by feature, with `domain` / `data` / `ui` packages per feature and shared code in `core/` | Accepted · amended 2026-08-26 by [ADR-0018](0018-compose-multiplatform-foundation.md) |
| 0003 | [Data layer](0003-data-layer.md) | Room is the single source of truth; the UI observes Room and never the network; domain models and entities stay separate | Accepted |
| 0004 | [Technology choices](0004-technology-choices.md) | Kotlin, Jetpack Compose, `.kts` builds, Room, Compose Navigation, minSdk 26, targetSdk 36 | Accepted · navigation superseded by [ADR-0006](0006-navigation.md); persistence library superseded by [ADR-0007](0007-room3.md) |
| 0005 | [Dependency injection and async](0005-dependency-injection.md) | Manual injection through one `AppContainer`; constructor injection; reads return `Flow`, writes are `suspend` | Accepted |
| 0006 | [Navigation](0006-navigation.md) | Navigation 3 with an app-owned back stack of `@Serializable` `NavKey` values and typed destination arguments | Accepted · supersedes navigation in [ADR-0004](0004-technology-choices.md) |
| 0007 | [Room 3](0007-room3.md) | Persistence is Room 3 via KSP; DAOs speak entity types only; saves use `@Upsert`; enum columns use built-in converters | Accepted · supersedes persistence library in [ADR-0004](0004-technology-choices.md) |
| 0008 | [Detail editing surface](0008-detail-editing-surface.md) | One detail destination with in-place editing and no edit mode; the ViewModel holds only short-lived status, not content | Accepted |
| 0009 | [Sealed UI state](0009-sealed-ui-state.md) | Screens without an honest default use a sealed `Loading` / `NotFound` / `Content` interface; screens with a default keep one data class | Accepted |
| 0010 | [Navigation chrome](0010-navigation-chrome.md) | Each screen builds its own `Scaffold`; `TbTopAppBar` is shared with slot-based navigation, title and actions; titles stay centred | Accepted |
| 0011 | [Notes storage](0011-notes-storage.md) | Notes are a JSON array in one TEXT column on the opportunity row; there is no notes table | Accepted |
| 0012 | [Schema evolution](0012-schema-evolution.md) | Schema JSON is exported per version; `@AutoMigration` when possible; new NOT NULL columns use `@ColumnInfo(defaultValue)`; every step has an instrumented migration test | Accepted |
| 0013 | [Pipeline track and exits](0013-pipeline-track-and-exits.md) | `PipelineStage.isTerminal` is exhaustive; the forward track is non-terminal stages in declaration order; stage history is not recorded | Accepted |
| 0014 | [Local OpenAI key](0014-local-openai-key.md) | The user stores an OpenAI API key locally; there is no Traceback server; only `https://api.openai.com` is called, with `gpt-4o` fixed in code | Accepted |
| 0015 | [User context and job brief](0015-user-context-and-job-brief.md) | One `UserContext` profile row drives a structured brief call; the five-field answer is stored as `aiBrief` TEXT on the opportunity row | Accepted |
| 0016 | [Design token contract](0016-design-token-contract.md) | Visual values come from four token axes only; screens hold no raw colour or measure; light and dark are two complete schemes audited in `TokenSheet` | Accepted |
| 0017 | [Theme mode preference](0017-theme-mode-preference.md) | `ThemeMode` is `SYSTEM`, `LIGHT` or `DARK`; `AppearanceStore` in settings/domain uses `SharedPreferences`; the Me screen uses the same picker pattern as stages | Accepted |
| 0018 | [Compose Multiplatform foundation](0018-compose-multiplatform-foundation.md) | `:shared` holds domain, Room data and AI; `:ui` is shared Compose; `:app` is Android; `:iosCompose` plus `iosApp/` is the iOS shell with platform seams for storage and HTTP | Accepted |
| 0019 | [Shared Aurora UI](0019-shared-aurora-ui.md) | `:ui` owns theme, components and all screens; presentation logic uses plain controllers with `StateFlow`; `:app` ViewModels are thin wrappers; iOS runs a hand-written back stack | Accepted |
| 0020 | [Test placement](0020-test-placement.md) | Tests live in the module that owns the code; common tests use `kotlin.test`; device-only work stays in `androidTest`; CI runs `check`, `assembleDebug` and `connectedCheck` | Accepted |
| 0021 | [Accessibility floors](0021-accessibility-floors.md) | One `MinTouchTarget` token for all controls; WCAG AA contrast is enforced by `tools/contrast_audit.py` across both themes | Accepted |