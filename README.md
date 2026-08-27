Yeah. 
![just-give-me-the-file.png](docs/images/just-give-me-the-file.png)

However, you may just like it. 

# Traceback

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
- Everything stays on your phone. No account, no Traceback server, no sync.
  The only network call is a job brief you start, sent to OpenAI with the
  API key you typed in. The key never leaves the device except as an
  Authorization header to `api.openai.com`.

## Under the hood

Kotlin and Jetpack Compose on Android; domain, Room, and the OpenAI client live
in a shared KMP module (`:shared`) so iOS can reuse them. Navigation 3 and the
aurora UI stay in `:app` for now. The reasoning behind each choice lives in
[docs/adr](docs/adr).

## Run it

### Android

Open it in Android Studio and hit Run

```bash
./gradlew installDebug
```

### iOS (foundation shell)

Requires a full **Xcode** install (Command Line Tools alone are not enough).
Open [`iosApp/iosApp.xcodeproj`](iosApp/iosApp.xcodeproj), pick a simulator,
and Run. The Xcode build embeds `:iosCompose` (which exports `:shared`).

```bash
./gradlew :shared:compileKotlinIosSimulatorArm64
```

## Next

The OpenAI brief is in: a **Me** screen (profile + key) and five boxes on an
opportunity (fit, proposal, price, duration, approach). The decision record
is [docs/adr/0015](docs/adr/0015-user-context-and-job-brief.md).

CMP foundation is in ([ADR-0018](docs/adr/0018-compose-multiplatform-foundation.md)):
shared domain/data/AI, Android intact, iOS Compose smoke shell. Next is moving
aurora UI into shared Compose Multiplatform. Claude, Gemini, and payment
tracking come after that ([ADR-0001](docs/adr/0001-scope-and-domain.md)).
