#!/usr/bin/env python3
"""Measures the design's contrast and fails if anything drops below WCAG AA.

The token values are read out of ui/theme/Color.kt and the aurora geometry out of
ui/components/AuroraBackground.kt, so this cannot drift from the app: change a colour and
re-run it. Glass is translucent, so every text token is measured against the lightest AND
the darkest backdrop the aurora can put behind it, on three surfaces (glass, raised glass,
bare ground), in both themes.

    python3 tools/contrast_audit.py            # table, exit 1 on any failure
    python3 tools/contrast_audit.py --verbose  # every check, not just the worst per role

The table it prints is the one in docs/private/design/design-identity.md.
"""
from __future__ import annotations

import argparse
import math
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
THEME = ROOT / "ui/src/commonMain/kotlin/com/erishan/traceback/ui/theme/Color.kt"
AURORA = ROOT / "ui/src/commonMain/kotlin/com/erishan/traceback/ui/components/AuroraBackground.kt"

BODY, LARGE, NON_TEXT = 4.5, 3.0, 3.0

# A field may not move the ground by more than this share of the channel range at any
# point on screen. Above it the ground reads as a poster - and in dark it lifts the
# backdrop far enough to drag text and stage colours under AA.
AURORA_BUDGET = 0.14

# Component alphas that are design decisions rather than tokens. Kept here so a change to
# one of them is a deliberate edit in two places, not a silent drift in one.
STAGE_PILL_FILL = 0.14
CHIP_FILL = 0.16
CHIP_BLOOM = 0.18
# The selected chip's edge is deliberately soft, so it is decorative rather than the state
# indicator. What identifies the state at full strength is the dot the chip grows when selected -
# that is the check below, and it is the one that owes 3:1.
CHIP_EDGE = 0.42
BRIEF_PILL_FILL = 0.10

CANVAS = (440, 956)
DRIFT_PHASES = (0.0, math.pi / 2, math.pi, 3 * math.pi / 2)
GRID = 160

Rgb = tuple[float, float, float]


# --- reading the source of truth ------------------------------------------------------

def parse_colors(text: str) -> dict[str, Rgb]:
    """Resolves `Color(0xAARRGGBB)` literals and one level of `X.copy(alpha = y)`."""
    literal = re.compile(r"val (\w+) = Color\(0x([0-9A-Fa-f]{8})\)")
    copied = re.compile(r"val (\w+) = (\w+)\.copy\(alpha = ([0-9.]+)f\)")
    aliased = re.compile(r"val (\w+) = (\w+)\s*$", re.MULTILINE)

    out: dict[str, Rgb] = {}
    alpha: dict[str, float] = {}
    for line in text.splitlines():
        if m := literal.search(line):
            argb = m.group(2)
            out[m.group(1)] = tuple(int(argb[i:i + 2], 16) for i in (2, 4, 6))
            alpha[m.group(1)] = int(argb[0:2], 16) / 255
        elif m := copied.search(line):
            out[m.group(1)] = out[m.group(2)]
            alpha[m.group(1)] = float(m.group(3))
        elif m := aliased.search(line):
            if m.group(2) in out:
                out[m.group(1)] = out[m.group(2)]
                alpha[m.group(1)] = alpha[m.group(2)]
    return out, alpha


def parse_aurora(text: str) -> dict[str, float]:
    numbers = {}
    for name, value in re.findall(r"val (\w+) = (-?[0-9.]+)f", text):
        numbers[name] = float(value)
    for name, x, y in re.findall(r"val (\w+) = Offset\((-?[0-9.]+)f, (-?[0-9.]+)f\)", text):
        numbers[name] = (float(x), float(y))
    return numbers


# --- colour maths ---------------------------------------------------------------------

def over(top: Rgb, alpha: float, bottom: Rgb) -> Rgb:
    return tuple(t * alpha + b * (1 - alpha) for t, b in zip(top, bottom))


def luminance(rgb: Rgb) -> float:
    def channel(v: float) -> float:
        v /= 255
        return v / 12.92 if v <= 0.04045 else ((v + 0.055) / 1.055) ** 2.4
    r, g, b = (channel(v) for v in rgb)
    return 0.2126 * r + 0.7152 * g + 0.0722 * b


def ratio(a: Rgb, b: Rgb) -> float:
    la, lb = luminance(a), luminance(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)


def field_alpha(px: float, py: float, centre, radius_fraction, drift, cfg) -> float:
    """One radial field, exactly as AuroraBackground draws it."""
    width, height = CANVAS
    ox = (centre[0] + drift[0]) * width
    oy = (centre[1] + drift[1]) * height
    radius = max(width, height) * radius_fraction
    d = min(1.0, math.hypot(px - ox, py - oy) / radius)
    mid, scale = cfg["FieldMidStop"], cfg["FieldMidAlphaScale"]
    if d <= mid:
        return 1.0 + (scale - 1.0) * (d / mid)
    return scale * (1 - (d - mid) / (1 - mid))


def backdrops(ground: Rgb, fields, cfg):
    """The lightest and darkest pixel the ground can be, over the whole drift cycle.

    Also returns how far the fields move the ground at their loudest - measured per
    channel over every sampled pixel, not just at the two luminance extremes, because a
    hue shift that barely touches luminance is still the ground moving.
    """
    width, height = CANVAS
    amp, skew = cfg["DriftAmplitude"], cfg["DriftSkew"]
    turn = 2 * math.pi
    darkest = lightest = None
    movement = 0.0
    for phase in DRIFT_PHASES:
        drifts = [
            (math.sin(phase + off) * amp, math.cos(phase * skew + off) * amp)
            for _, _, _, _, off in fields
        ]
        for iy in range(GRID):
            py = height * iy / (GRID - 1)
            for ix in range(GRID):
                px = width * ix / (GRID - 1)
                colour = ground
                for (rgb, alpha, centre, rf, _), drift in zip(fields, drifts):
                    a = field_alpha(px, py, centre, rf, drift, cfg) * alpha
                    colour = over(rgb, a, colour)
                lum = luminance(colour)
                if darkest is None or lum < darkest[0]:
                    darkest = (lum, colour)
                if lightest is None or lum > lightest[0]:
                    lightest = (lum, colour)
                movement = max(movement, max(abs(x - y) for x, y in zip(colour, ground)))
    return darkest[1], lightest[1], movement / 255


# --- the audit ------------------------------------------------------------------------

STAGES = ("Draft", "Applied", "InConversation", "Interview", "Hired", "Delivered",
          "Closed", "Lost")


def theme(name: str, colours, alphas, cfg):
    def c(token: str) -> Rgb:
        return colours[f"{token}{name}"]

    def a(token: str) -> float:
        return alphas[f"{token}{name}"]

    ground = c("Ground")
    fields = [
        (c("AuroraIndigo"), a("AuroraIndigo"), cfg["IndigoCenter"], cfg["IndigoRadius"], 0.0),
        (c("AuroraTeal"), a("AuroraTeal"), cfg["TealCenter"], cfg["TealRadius"],
         2 * math.pi / 3),
        (c("AuroraWarm"), a("AuroraWarm"), cfg["WarmCenter"], cfg["WarmRadius"],
         4 * math.pi / 3),
    ]
    dark_bd, light_bd, movement = backdrops(ground, fields, cfg)

    checks = []

    def check(role, need, fg, bg, note=""):
        checks.append((role, need, ratio(fg, bg), note))

    for label, bd in (("darkest", dark_bd), ("lightest", light_bd)):
        glass = over(c("Glass"), a("Glass"), bd)
        raised = over(c("GlassStrong"), a("GlassStrong"), bd)
        surfaces = ((glass, "glass"), (raised, "glassStrong"), (bd, "ground"))

        for token in ("TextHigh", "TextDim", "TextFaint", "AccentText"):
            for surface, _ in surfaces:
                check(token[0].lower() + token[1:], BODY, c(token), surface)

        check("textHigh", BODY, c("TextHigh"),
              over(c("Accent"), BRIEF_PILL_FILL, glass))
        check("accentText on accent %d%% fill" % (BRIEF_PILL_FILL * 100), BODY,
              c("AccentText"), over(c("Accent"), BRIEF_PILL_FILL, glass))
        check("onAccent on accent fill", BODY, c("OnAccent"), c("Accent"))

        check("accent fill vs ground", NON_TEXT, c("Accent"), bd, "non-text")
        check("accent fill vs ground", NON_TEXT, c("Accent"), glass, "non-text")
        check("accent edge vs glassStrong (focus)", NON_TEXT, c("Accent"), raised, "non-text")
        check("edge vs glass (decorative)", 0.0, over(c("Edge"), a("Edge"), glass), glass,
              "decorative")

        track = over(c("Track"), a("Track"), glass)
        for stage in STAGES:
            colour = c(f"Stage{stage}")
            check("stage label on glass", BODY, colour, glass)
            check("stage value 20sp/700 on glass", LARGE, colour, glass, "large")
            check("stage label on its own %d%% fill" % (STAGE_PILL_FILL * 100), BODY,
                  colour, over(colour, STAGE_PILL_FILL, glass))
            check("textHigh", BODY, c("TextHigh"),
                  over(colour, CHIP_FILL, over(colour, CHIP_BLOOM, bd)))
            check("stage rod vs glass", NON_TEXT, colour, glass, "non-text")
            check("stage vs track (conduit)", NON_TEXT, colour, track, "non-text")
            check("selection dot vs glass", NON_TEXT, colour, glass, "non-text")
            check("selection dot vs ground", NON_TEXT, colour, bd, "non-text")
            check("selected chip edge (decorative)", 0.0,
                  over(colour, CHIP_EDGE, bd), bd, "decorative")
        check("selection dot vs glass", NON_TEXT, c("Accent"), glass, "non-text")
        check("selection dot vs ground", NON_TEXT, c("Accent"), bd, "non-text")
        check("selection dot vs glass", NON_TEXT, c("TextHigh"), glass, "non-text")

    return movement, dark_bd, light_bd, checks


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--verbose", action="store_true")
    args = parser.parse_args()

    colours, alphas = parse_colors(THEME.read_text())
    cfg = parse_aurora(AURORA.read_text())

    results = {}
    failures = []
    for name in ("Dark", "Light"):
        movement, dark_bd, light_bd, checks = theme(name, colours, alphas, cfg)
        results[name] = (movement, dark_bd, light_bd, checks)
        failures += [(name, *ch) for ch in checks if ch[2] < ch[1]]

    for name in ("Dark", "Light"):
        movement, dark_bd, light_bd, _ = results[name]
        hexed = lambda c: "#%02X%02X%02X" % tuple(int(round(v)) for v in c)
        flag = "OK" if movement <= AURORA_BUDGET else "OVER BUDGET"
        if movement > AURORA_BUDGET:
            failures.append((name, "aurora ground movement", AURORA_BUDGET, movement, "budget"))
        print(f"{name:5}  aurora moves the ground {movement * 100:4.1f}%  [{flag}]"
              f"   backdrops {hexed(dark_bd)} .. {hexed(light_bd)}")
    print()

    worst: dict[tuple[str, str], tuple[float, float, str]] = {}
    for name in ("Dark", "Light"):
        for role, need, got, note in results[name][3]:
            key = (name, role)
            if key not in worst or got < worst[key][0]:
                worst[key] = (got, need, note)

    roles = sorted({role for _, role in worst}, key=str.lower)
    print(f"{'role':40} {'need':>5} {'dark':>6} {'light':>6}")
    for role in roles:
        d = worst.get(("Dark", role))
        l = worst.get(("Light", role))
        need = (d or l)[1]
        threshold = f"{need:.1f}" if need else "-"
        print(f"{role:40} {threshold:>5} {d[0]:6.2f} {l[0]:6.2f}")

    if args.verbose:
        print()
        for name in ("Dark", "Light"):
            for role, need, got, note in results[name][3]:
                print(f"  {name:5} {role:44} {got:6.2f} need {need} {note}")

    total = sum(len(results[n][3]) for n in results)
    print()
    if failures:
        print(f"{total} checks, {len(failures)} BELOW THRESHOLD:")
        for name, role, need, got, note in failures:
            print(f"  {name:5} {role:44} {got:.2f} < {need} {note}")
        return 1
    print(f"{total} checks, 0 below threshold.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
