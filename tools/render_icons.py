#!/usr/bin/env python3
"""Renders every launcher and launch-screen bitmap Traceback ships.

The vector drawables in app/src/main/res/drawable are the mark; this script
carries the same 108dp geometry so the bitmaps cannot drift from them. Re-run
it after changing the geometry or a colour token:

    python3 tools/render_icons.py

Requires Pillow and numpy. Colours are the ui/theme/Color.kt tokens: ground,
track, stage.delivered (cool) and accent (warm), in both schemes.
"""

import os
import sys

import numpy as np
from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
GRID = 108.0          # adaptive-icon canvas
VISIBLE = 72.0        # the part every mask shows
MAX_SUPERSAMPLE = 8

# Geometry, identical to ic_launcher_foreground.xml.
ROD_W, ROD_R = 9.0, 4.5
RODS = [  # x, track y, track height, fill y, fill height, role
    (37.0, 41.0, 38.0, 61.0, 18.0, "cool"),
    (49.5, 29.0, 50.0, 41.0, 38.0, "warm"),
    (62.0, 47.0, 32.0, 67.0, 12.0, "cool"),
]
BLOOM = (54.0, 56.0, 27.0)  # centre x, centre y, radius

THEMES = {
    "dark": dict(
        ground=(7, 8, 11),
        aurora=[((33, 29, 52), (59, 46, 208), 0.55),
                ((80, 86, 48), (18, 181, 168), 0.34)],
        track=((255, 255, 255), 0.15),
        cool=(43, 217, 208),
        warm=(255, 107, 61),
        bloom=0.30,
        cool_alpha=1.0,
    ),
    "light": dict(
        ground=(238, 241, 247),
        aurora=[((33, 29, 52), (59, 46, 208), 0.20),
                ((80, 86, 48), (18, 181, 168), 0.16)],
        track=((10, 14, 25), 0.16),
        cool=(11, 112, 107),
        warm=(203, 69, 11),
        bloom=0.12,
        cool_alpha=1.0,
    ),
    # One colour, alpha carries the hierarchy: iOS tinted icons and Android
    # themed icons both recolour the image and keep only its alpha.
    "mono": dict(
        ground=None,
        aurora=[],
        track=((255, 255, 255), 0.42),
        cool=(255, 255, 255),
        warm=(255, 255, 255),
        bloom=0.0,
        cool_alpha=0.72,
    ),
}


def _supersample(px):
    return max(1, min(MAX_SUPERSAMPLE, 3072 // px))


def _radial(size, cx, cy, radius, scale):
    ys, xs = np.mgrid[0:size, 0:size]
    d = np.hypot(xs - cx * scale, ys - cy * scale) / (radius * scale)
    return np.clip(1.0 - d, 0.0, 1.0) ** 1.7


def canvas(px, theme, background=True):
    """The full 108dp canvas rendered at px by px."""
    t = THEMES[theme]
    size = px * _supersample(px)
    scale = size / GRID

    base = np.zeros((size, size, 4), dtype=np.float64)
    if background and t["ground"] is not None:
        base[..., :3] = np.array(t["ground"], dtype=np.float64)
        base[..., 3] = 255.0
        for (cx, cy, radius), rgb, alpha in t["aurora"]:
            a = (_radial(size, cx, cy, radius, scale) * alpha)[..., None]
            base[..., :3] = base[..., :3] * (1 - a) + np.array(rgb, dtype=np.float64) * a
    img = Image.fromarray(base.round().astype(np.uint8), "RGBA")

    if t["bloom"] > 0:
        cx, cy, radius = BLOOM
        glow = np.zeros((size, size, 4), dtype=np.uint8)
        glow[..., :3] = np.array(t["warm"], dtype=np.uint8)
        glow[..., 3] = (_radial(size, cx, cy, radius, scale) * t["bloom"] * 255).round()
        img = Image.alpha_composite(img, Image.fromarray(glow, "RGBA"))

    layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    track_rgb, track_alpha = t["track"]
    for x, track_y, track_h, fill_y, fill_h, role in RODS:
        def box(y, h):
            return [x * scale, y * scale, (x + ROD_W) * scale - 1, (y + h) * scale - 1]
        draw.rounded_rectangle(box(track_y, track_h), radius=ROD_R * scale,
                               fill=track_rgb + (round(track_alpha * 255),))
        warm = role == "warm"
        draw.rounded_rectangle(
            box(fill_y, fill_h), radius=ROD_R * scale,
            fill=(t["warm"] if warm else t["cool"])
                 + (255 if warm else round(t["cool_alpha"] * 255),))
    img = Image.alpha_composite(img, layer)

    return img.resize((px, px), Image.LANCZOS)


def visible(px, theme, background=True):
    """The central 72dp of the canvas, which is what a mask leaves on screen."""
    n = round(px * GRID / VISIBLE)
    lo, hi = round(n * 18 / GRID), round(n * 90 / GRID)
    return canvas(n, theme, background).crop((lo, lo, hi, hi)).resize((px, px), Image.LANCZOS)


def masked(px, theme, shape):
    """A legacy launcher bitmap: pre-masked, because API 25 and below have no mask."""
    img = visible(px, theme)
    ss = _supersample(px)
    mask = Image.new("L", (px * ss, px * ss), 0)
    draw = ImageDraw.Draw(mask)
    edge = px * ss - 1
    if shape == "circle":
        draw.ellipse([0, 0, edge, edge], fill=255)
    else:
        draw.rounded_rectangle([0, 0, edge, edge], radius=px * ss * 0.2, fill=255)
    img.putalpha(mask.resize((px, px), Image.LANCZOS))
    return img


def write(img, *parts, fmt="PNG"):
    path = os.path.join(ROOT, *parts)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    if fmt == "WEBP":
        img.save(path, "WEBP", lossless=True, quality=100)
    else:
        img.save(path, "PNG")
    print(f"{os.path.getsize(path):>8}  {os.path.relpath(path, ROOT)}")


def main():
    res = ("app", "src", "main", "res")
    for bucket, px in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96),
                       ("xxhdpi", 144), ("xxxhdpi", 192)]:
        write(masked(px, "dark", "squircle"), *res, f"mipmap-{bucket}",
              "ic_launcher.webp", fmt="WEBP")
        write(masked(px, "dark", "circle"), *res, f"mipmap-{bucket}",
              "ic_launcher_round.webp", fmt="WEBP")

    icon = ("iosApp", "iosApp", "Assets.xcassets", "AppIcon.appiconset")
    write(visible(1024, "dark").convert("RGB"), *icon, "icon-1024.png")
    write(visible(1024, "dark", background=False), *icon, "icon-1024-dark.png")
    write(visible(1024, "mono", background=False), *icon, "icon-1024-tinted.png")

    logo = ("iosApp", "iosApp", "Assets.xcassets", "LaunchLogo.imageset")
    for suffix, px in [("", 120), ("@2x", 240), ("@3x", 360)]:
        write(visible(px, "light", background=False), *logo, f"launch-logo{suffix}.png")
        write(visible(px, "dark", background=False), *logo, f"launch-logo-dark{suffix}.png")

    return 0


if __name__ == "__main__":
    sys.exit(main())
