# Traceback

[![CI](https://github.com/Erishan/traceback/actions/workflows/ci.yml/badge.svg)](https://github.com/Erishan/traceback/actions/workflows/ci.yml)

*A pipeline for freelance leads. Android, on your phone, no account.*

This app is intentionally small. One pipeline, local storage, one optional OpenAI call.
All additional details are in [Under the hood](#under-the-hood).

![just-give-me-the-file.png](docs/images/just-give-me-the-file.png)

Closing a deal on your freelance work is not an easy job.
You gotta see what they really want if they want anything,
you send your proposal and wait...
Sometimes they go quiet.
Sometimes they are too hyped and they just jump to a new project.
Sometimes you do not hear from them for three whole weeks and all of a sudden a new opportunity pops up.

Traceback keeps all of it in one place, so you always know what is still alive.

Every opportunity in this big bang dust that we call life has its own stage.

In Traceback every lead gets a stage such as Draft, Applied, In conversation, Interview, Hired,
Delivered. And it moves along as things happen. When one dies, it goes to
Closed or Lost instead of quietly disappearing, which can help you find out what
your real win rate is.

It is an Android app and everything stays on your phone. No account, no
Traceback server, nothing to sign up for. There is an iOS build too, and a
[section further down](#ios) telling you exactly how much to trust it.

-> fyi the source is only here, there is no app on any store.

## What it looks like

<p align="center">
  <img src="docs/images/list-dark.png" width="240" alt="The pipeline: every lead on a stage, with a strip showing where they sit">
  <img src="docs/images/detail-dark.png" width="240" alt="One opportunity, its stage lit along the conduit, with notes and a job brief">
  <img src="docs/images/create-dark.png" width="240" alt="Adding a lead: title, where it came from, what stage it is at">
</p>

<details>
<summary>Light theme</summary>
<p align="center">
  <img src="docs/images/list-light.png" width="240" alt="The pipeline in the light theme">
  <img src="docs/images/detail-light.png" width="240" alt="One opportunity in the light theme">
  <img src="docs/images/create-light.png" width="240" alt="Adding a lead in the light theme">
</p>
</details>

## What you get

- Add a lead in a few seconds: title, where it came from, what stage it is at.
- Open it later to see how far it got, edit anything inline, and drop dated
  notes as the conversation goes on.
- Filter the list down to what is active, won, or lost.
- Ask for a job brief and it goes to OpenAI with the API key you typed in.
  That is the only network call the app ever makes, and the key never leaves
  the device except as an Authorization header to `api.openai.com`.

## Under the hood

As previously mentioned Traceback is relatively small.
The module table is where most of the work went.
Kotlin and Compose Multiplatform. Four modules:

| Module | What is in it | Builds for |
|---|---|---|
| `:shared` | Domain, Room data, the OpenAI client, the manual DI container | Android, iOS |
| `:ui` | Aurora theme, components, the list, detail, create and Me screens, and the controllers that back them | Android, iOS |
| `:app` | The Android app: Navigation 3 routes, thin ViewModels, Android previews | Android |
| `:iosCompose` + `iosApp/` | The iOS shell: a hand written back stack and the Xcode host that embeds the framework | iOS |

The reasoning behind each choice is in [docs/adr](docs/adr).

## Contrast is measured, not eyeballed
tools/contrast_audit.py reads the colours out of the theme and fails on any text or surface pair below WCAG AA, in both themes.

## iOS

The shared core runs on iOS. `:shared` and `:ui` are Kotlin Multiplatform, so
iOS gets the same Room schema, the same OpenAI client and the same opportunity
and Me screens as Android, from the same source.

What it does not get is a native feel! 
The screens are Compose drawn into a UIKit host, so text editing behaviour, selection handles 
and scroll momentum come from Compose and not from UIKit. 
As iOS also has no Navigation 3: the back stack and the back swipe are written by hand.
One shared UI is the standing decision and this is the price it charges. 

-> Yes, it's better to read it here than to find it in the build.

## Run it

### Android

Open it in Android Studio and hit Run.

```bash
./gradlew installDebug
```

### iOS

Requires a full **Xcode** install (Command Line Tools alone are not enough).
Open [`iosApp/iosApp.xcodeproj`](iosApp/iosApp.xcodeproj), pick a simulator,
and Run. The Xcode build embeds `:iosCompose`, which exports `:shared` and
`:ui`.

```bash
./gradlew :ui:compileKotlinIosSimulatorArm64 :iosCompose:compileKotlinIosSimulatorArm64
```

## License

The application code is licensed under [Apache 2.0](LICENSE); the bundled
Manrope font is licensed separately under the SIL Open Font License 1.1
([docs/licenses/Manrope-OFL.txt](docs/licenses/Manrope-OFL.txt), summarised in
[NOTICE](NOTICE)).
