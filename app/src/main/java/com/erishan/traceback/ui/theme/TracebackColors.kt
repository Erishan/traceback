package com.erishan.traceback.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class TracebackColors(
    /** Page behind everything. Also `colorScheme.background`. */
    val ground: Color,
    /** Translucent card fill. Also `colorScheme.surface`. */
    val glass: Color,
    /** Raised glass: sheets, inputs, the surface that sits on another surface. */
    val glassStrong: Color,
    /** Hairline around glass. Also `colorScheme.outline`. */
    val edge: Color,
    /** Brighter inner edge along the top of a glass surface - light passing through. */
    val edgeHighlight: Color,
    /** Unfilled part of a progress conduit. */
    val track: Color,
    val textHigh: Color,
    val textDim: Color,
    /** Third rank: placeholders and empty-state text only. */
    val textFaint: Color,
    val accent: Color,
    val onAccent: Color,
    /** Accent as a wash, for the one selected or primary surface. */
    val accentDim: Color,
    val auroraIndigo: Color,
    val auroraTeal: Color,
    val auroraWarm: Color,
    val stageDraft: Color,
    val stageApplied: Color,
    val stageInConversation: Color,
    val stageInterview: Color,
    val stageHired: Color,
    val stageDelivered: Color,
    val stageClosed: Color,
    val stageLost: Color,
) {
    /** Chip that names where an opportunity came from. Reads as raised glass. */
    val sourceChipBg: Color get() = glassStrong
}

internal val DarkExtras = TracebackColors(
    ground = GroundDark,
    glass = GlassDark,
    glassStrong = GlassStrongDark,
    edge = EdgeDark,
    edgeHighlight = EdgeHighlightDark,
    track = TrackDark,
    textHigh = TextHighDark,
    textDim = TextDimDark,
    textFaint = TextFaintDark,
    accent = AccentDark,
    onAccent = OnAccentDark,
    accentDim = AccentDimDark,
    auroraIndigo = AuroraIndigoDark,
    auroraTeal = AuroraTealDark,
    auroraWarm = AuroraWarmDark,
    stageDraft = StageDraftDark,
    stageApplied = StageAppliedDark,
    stageInConversation = StageInConversationDark,
    stageInterview = StageInterviewDark,
    stageHired = StageHiredDark,
    stageDelivered = StageDeliveredDark,
    stageClosed = StageClosedDark,
    stageLost = StageLostDark,
)

internal val LightExtras = TracebackColors(
    ground = GroundLight,
    glass = GlassLight,
    glassStrong = GlassStrongLight,
    edge = EdgeLight,
    edgeHighlight = EdgeHighlightLight,
    track = TrackLight,
    textHigh = TextHighLight,
    textDim = TextDimLight,
    textFaint = TextFaintLight,
    accent = AccentLight,
    onAccent = OnAccentLight,
    accentDim = AccentDimLight,
    auroraIndigo = AuroraIndigoLight,
    auroraTeal = AuroraTealLight,
    auroraWarm = AuroraWarmLight,
    stageDraft = StageDraftLight,
    stageApplied = StageAppliedLight,
    stageInConversation = StageInConversationLight,
    stageInterview = StageInterviewLight,
    stageHired = StageHiredLight,
    stageDelivered = StageDeliveredLight,
    stageClosed = StageClosedLight,
    stageLost = StageLostLight,
)

val LocalTracebackColors = staticCompositionLocalOf<TracebackColors> {
    error("TracebackColors not provided - wrap the content in TracebackTheme")
}
