package com.erishan.traceback.ui.theme

import androidx.compose.ui.graphics.Color

private val White = Color(0xFFFFFFFF)
private val Ink = Color(0xFF0A0E19)

// Ground

internal val GroundDark = Color(0xFF07080B)
internal val GroundLight = Color(0xFFEEF1F7)

// Glass

internal val GlassDark = White.copy(alpha = 0.045f)
internal val GlassStrongDark = White.copy(alpha = 0.07f)
internal val EdgeDark = White.copy(alpha = 0.10f)
internal val EdgeHighlightDark = White.copy(alpha = 0.16f)
internal val TrackDark = White.copy(alpha = 0.09f)

internal val GlassLight = White.copy(alpha = 0.72f)
internal val GlassStrongLight = White.copy(alpha = 0.86f)
internal val EdgeLight = Ink.copy(alpha = 0.14f)
internal val EdgeHighlightLight = White.copy(alpha = 0.90f)
internal val TrackLight = Ink.copy(alpha = 0.08f)

// Text

internal val TextHighDark = Color(0xFFF4F7FC)
internal val TextDimDark = Color(0xFFB5BCC9)
internal val TextFaintDark = Color(0xFF9099AB)

internal val TextHighLight = Color(0xFF0A0D14)
internal val TextDimLight = Color(0xFF3B424F)
internal val TextFaintLight = Color(0xFF535C6D)

// Accent

internal val AccentDark = Color(0xFFFF6B3D)
internal val AccentTextDark = AccentDark
internal val OnAccentDark = Color(0xFF1B0700)
internal val AccentDimDark = AccentDark.copy(alpha = 0.14f)

internal val AccentLight = Color(0xFFCB450B)
internal val AccentTextLight = Color(0xFFA03609)
internal val OnAccentLight = White
internal val AccentDimLight = AccentLight.copy(alpha = 0.12f)

// Aurora

private val Indigo = Color(0xFF3B2ED0)
private val Teal = Color(0xFF12B5A8)

internal val AuroraIndigoDark = Indigo.copy(alpha = 0.14f)
internal val AuroraTealDark = Teal.copy(alpha = 0.10f)
internal val AuroraWarmDark = AccentDark.copy(alpha = 0.07f)

internal val AuroraIndigoLight = Indigo.copy(alpha = 0.11f)
internal val AuroraTealLight = Teal.copy(alpha = 0.10f)
internal val AuroraWarmLight = AccentLight.copy(alpha = 0.09f)

// Stage

internal val StageDraftDark = Color(0xFF8D97AA)
internal val StageAppliedDark = Color(0xFF4D9BFF)
internal val StageInConversationDark = Color(0xFFFFB020)
internal val StageInterviewDark = Color(0xFFAA7EFF)
internal val StageHiredDark = Color(0xFF2FE39B)
internal val StageDeliveredDark = Color(0xFF2BD9D0)
internal val StageClosedDark = Color(0xFF8D97AA)
internal val StageLostDark = Color(0xFFFF5E7B)

internal val StageDraftLight = Color(0xFF656E81)
internal val StageAppliedLight = Color(0xFF1769DC)
internal val StageInConversationLight = Color(0xFF9A6100)
internal val StageInterviewLight = Color(0xFF7B45D6)
internal val StageHiredLight = Color(0xFF007D5B)
internal val StageDeliveredLight = Color(0xFF0C7B76)
internal val StageClosedLight = Color(0xFF656E81)
internal val StageLostLight = Color(0xFFD0284B)
