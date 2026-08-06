package com.erishan.traceback.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary          = Emerald,
    onPrimary        = EmeraldOn,
    background       = Ink,
    onBackground     = TextHigh,
    surface          = Surface1,
    onSurface        = TextHigh,
    surfaceVariant   = Surface2,
    onSurfaceVariant = TextDim,
    surfaceContainerLowest  = Ink,
    surfaceContainerLow     = Surface1,
    surfaceContainer        = Surface1,
    surfaceContainerHigh    = Surface2,
    surfaceContainerHighest = Surface3,
    outline          = OutlineStrong,
    outlineVariant   = OutlineSubtle,
    error            = StageLost,
    onError          = Ink,
)

// Material'da rol karşılığı olmayan token'lar.
private val DarkExtras = TracebackColors(
    accentDim           = AccentDim,
    textFaint           = TextFaint,
    hairlineTop         = HairlineTop,
    sourceChipBg        = Surface2,
    stageDraft          = StageDraft,
    stageApplied        = StageApplied,
    stageInConversation = StageInConversation,
    stageInterview      = StageInterview,
    stageHired          = StageHired,
    stageDelivered      = StageDelivered,
    stageClosed         = StageClosed,
    stageLost           = StageLost,
)

@Composable
fun TracebackTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTracebackColors provides DarkExtras) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
