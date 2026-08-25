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
internal val EdgeLight = Ink.copy(alpha = 0.09f)
internal val EdgeHighlightLight = White.copy(alpha = 0.90f)
internal val TrackLight = Ink.copy(alpha = 0.08f)

// Text

internal val TextHighDark = Color(0xFFF4F7FC)
internal val TextDimDark = Color(0xFF98A1B4)
internal val TextFaintDark = Color(0xFF5B6478)

internal val TextHighLight = Color(0xFF0A0D14)
internal val TextDimLight = Color(0xFF5A6478)
internal val TextFaintLight = Color(0xFF8B94A6)

// Accent

internal val AccentDark = Color(0xFFFF6B3D)
internal val OnAccentDark = Color(0xFF1B0700)
internal val AccentDimDark = AccentDark.copy(alpha = 0.14f)

internal val AccentLight = Color(0xFFF2530F)
internal val OnAccentLight = White
internal val AccentDimLight = AccentLight.copy(alpha = 0.12f)

// Aurora

private val Indigo = Color(0xFF3B2ED0)
private val Teal = Color(0xFF12B5A8)

internal val AuroraIndigoDark = Indigo.copy(alpha = 0.42f)
internal val AuroraTealDark = Teal.copy(alpha = 0.30f)
internal val AuroraWarmDark = AccentDark.copy(alpha = 0.20f)

internal val AuroraIndigoLight = Indigo.copy(alpha = 0.13f)
internal val AuroraTealLight = Teal.copy(alpha = 0.12f)
internal val AuroraWarmLight = AccentLight.copy(alpha = 0.11f)

// Stage

internal val StageDraftDark = Color(0xFF8892A6)
internal val StageAppliedDark = Color(0xFF4D9BFF)
internal val StageInConversationDark = Color(0xFFFFB020)
internal val StageInterviewDark = Color(0xFFA97CFF)
internal val StageHiredDark = Color(0xFF2FE39B)
internal val StageDeliveredDark = Color(0xFF2BD9D0)
internal val StageClosedDark = Color(0xFF8892A6)
internal val StageLostDark = Color(0xFFFF4D6D)

internal val StageDraftLight = Color(0xFF6B7488)
internal val StageAppliedLight = Color(0xFF1F73E8)
internal val StageInConversationLight = Color(0xFFB87400)
internal val StageInterviewLight = Color(0xFF7B45D6)
internal val StageHiredLight = Color(0xFF00926A)
internal val StageDeliveredLight = Color(0xFF0E8F8A)
internal val StageClosedLight = Color(0xFF6B7488)
internal val StageLostLight = Color(0xFFD62B4E)
