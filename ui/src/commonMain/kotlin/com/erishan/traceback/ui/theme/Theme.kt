package com.erishan.traceback.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalInspectionMode

private val DarkColorScheme = darkColorScheme(
    primary = AccentDark,
    onPrimary = OnAccentDark,
    primaryContainer = AccentDimDark,
    onPrimaryContainer = AccentDark,
    secondary = TextDimDark,
    onSecondary = GroundDark,
    secondaryContainer = GlassStrongDark,
    onSecondaryContainer = TextHighDark,
    tertiary = AccentDark,
    onTertiary = OnAccentDark,
    tertiaryContainer = AccentDimDark,
    onTertiaryContainer = AccentDark,
    background = GroundDark,
    onBackground = TextHighDark,
    surface = GlassDark,
    onSurface = TextHighDark,
    surfaceVariant = GlassStrongDark,
    onSurfaceVariant = TextDimDark,
    surfaceTint = AccentDark,
    surfaceContainerLowest = GroundDark,
    surfaceContainerLow = GlassDark,
    surfaceContainer = GlassDark,
    surfaceContainerHigh = GlassStrongDark,
    surfaceContainerHighest = TrackDark,
    inverseSurface = TextHighDark,
    inverseOnSurface = GroundDark,
    inversePrimary = AccentDark,
    outline = EdgeDark,
    outlineVariant = EdgeHighlightDark,
    error = StageLostDark,
    onError = GroundDark,
    errorContainer = GlassStrongDark,
    onErrorContainer = StageLostDark,
)

private val LightColorScheme = lightColorScheme(
    primary = AccentLight,
    onPrimary = OnAccentLight,
    primaryContainer = AccentDimLight,
    onPrimaryContainer = AccentLight,
    secondary = TextDimLight,
    onSecondary = GroundLight,
    secondaryContainer = GlassStrongLight,
    onSecondaryContainer = TextHighLight,
    tertiary = AccentLight,
    onTertiary = OnAccentLight,
    tertiaryContainer = AccentDimLight,
    onTertiaryContainer = AccentLight,
    background = GroundLight,
    onBackground = TextHighLight,
    surface = GlassLight,
    onSurface = TextHighLight,
    surfaceVariant = GlassStrongLight,
    onSurfaceVariant = TextDimLight,
    surfaceTint = AccentLight,
    surfaceContainerLowest = GroundLight,
    surfaceContainerLow = GlassLight,
    surfaceContainer = GlassLight,
    surfaceContainerHigh = GlassStrongLight,
    surfaceContainerHighest = TrackLight,
    inverseSurface = TextHighLight,
    inverseOnSurface = GroundLight,
    inversePrimary = AccentLight,
    outline = EdgeLight,
    outlineVariant = EdgeHighlightLight,
    error = StageLostLight,
    onError = OnAccentLight,
    errorContainer = GlassStrongLight,
    onErrorContainer = StageLostLight,
)

@Composable
fun TracebackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reducedMotion: Boolean = LocalInspectionMode.current,
    content: @Composable () -> Unit,
) {
    val typography = rememberTracebackTypography()
    CompositionLocalProvider(
        LocalTracebackColors provides if (darkTheme) DarkExtras else LightExtras,
        LocalTracebackDimens provides DefaultDimens,
        LocalTracebackMotion provides if (reducedMotion) StillMotion else DefaultMotion,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = typography,
            shapes = TracebackShapes,
            content = content,
        )
    }
}

/** The half of the contract Material has no slot for. */
object TracebackTheme {
    val colors: TracebackColors
        @Composable @ReadOnlyComposable
        get() = LocalTracebackColors.current

    val dimens: TracebackDimens
        @Composable @ReadOnlyComposable
        get() = LocalTracebackDimens.current

    val motion: TracebackMotion
        @Composable @ReadOnlyComposable
        get() = LocalTracebackMotion.current
}
