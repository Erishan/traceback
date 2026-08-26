package com.erishan.traceback.settings.domain

/**
 * Which theme the app paints in.
 *
 * [SYSTEM] is a real choice, not the absence of one: a person who has never opened the
 * setting still gets the theme their phone is in, and a person who picks it deliberately
 * gets a light desk in the morning and a dark one at night without touching the app.
 */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}
