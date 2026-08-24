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

## What you get

- Add a lead in a few seconds: title, where it came from, what stage it is at.
- Open it later to see how far it got, edit anything inline, and drop dated
  notes as the conversation goes on.
- Filter the list down to what is active, won, or lost.
- Everything stays on your phone. No account, no sync, no network.

## Under the hood

Kotlin and Jetpack Compose, Room for storage, Navigation 3. Domain and data
layers are kept free of Android so they can move to Kotlin Multiplatform later.
The reasoning behind each choice lives in [docs/adr](docs/adr).

## Run it

Open it in Android Studio and hit Run

```bash
./gradlew installDebug
```

## Next

Payment tracking, then an iOS build.
