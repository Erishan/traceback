#!/usr/bin/env bash
# Regenerates the README screenshots from ScreenshotTest. Needs one running emulator or
# device. The pictures are rendered from the same showcase composables the previews use,
# so they cannot fall behind the design - re-run this after any visual change.
#
# Frame size follows the device's content area, so use a phone-shaped AVD (Pixel class).
#
# No adb needed: the test writes into the directory AGP passes as additionalTestOutputDir,
# and Gradle copies it back to the host on its own. Reading the app's own folder off the
# device with adb does not work on modern Android - scoped storage hides Android/data from
# the shell, so `adb pull` reports the directory as missing even while the files are there.
set -euo pipefail

cd "$(dirname "$0")/.."
names=(list-dark list-light detail-dark detail-light create-dark create-light)
collected="app/build/outputs/connected_android_test_additional_output"

rm -rf "$collected"

./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.erishan.traceback.ScreenshotTest

mkdir -p docs/images
missing=()
for name in "${names[@]}"; do
  src=$(find "$collected" -name "$name.png" -print -quit 2>/dev/null || true)
  if [ -z "$src" ]; then
    missing+=("$name.png")
  else
    cp "$src" "docs/images/$name.png"
  fi
done

if [ ${#missing[@]} -ne 0 ]; then
  echo "Gradle did not bring these back: ${missing[*]}" >&2
  echo "Look under $collected to see what the run produced." >&2
  exit 1
fi

# A phone frame lands around 1080px wide; the README shows it at 240. Resample to 720 -
# 3x, so it stays crisp on a retina screen - rather than committing several megabytes of
# pixels nobody sees. sips ships with macOS; without it the full-size files are kept.
if command -v sips >/dev/null 2>&1; then
  for name in "${names[@]}"; do
    sips --resampleWidth 720 "docs/images/$name.png" >/dev/null
  done
fi

echo
echo "Wrote:"
for name in "${names[@]}"; do ls -lh "docs/images/$name.png"; done
