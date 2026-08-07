package com.erishan.traceback.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// design tokens
@Immutable
data class TracebackColors(
    val accentDim: Color,
    val textFaint: Color,
    val hairlineTop: Color,
    val sourceChipBg: Color,
    val stageDraft: Color,
    val stageApplied: Color,
    val stageInConversation: Color,
    val stageInterview: Color,
    val stageHired: Color,
    val stageDelivered: Color,
    val stageClosed: Color,
    val stageLost: Color,
)

val LocalTracebackColors = staticCompositionLocalOf<TracebackColors> {
    error("TracebackColors not provided")
}

object TracebackTheme {
    val colors: TracebackColors
        @Composable @ReadOnlyComposable
        get() = LocalTracebackColors.current
}
