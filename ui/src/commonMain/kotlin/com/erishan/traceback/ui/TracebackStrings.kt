package com.erishan.traceback.ui

import androidx.compose.runtime.Composable
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.ui.BriefFailureKind
import com.erishan.traceback.opportunity.ui.BriefGateReason
import com.erishan.traceback.opportunity.ui.OpportunityFilter
import com.erishan.traceback.settings.domain.ThemeMode
import com.erishan.traceback.ui.theme.Res
import com.erishan.traceback.ui.theme.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PipelineStage.label(): String = stringResource(
    when (this) {
        PipelineStage.DRAFT -> Res.string.stage_draft
        PipelineStage.APPLIED -> Res.string.stage_applied
        PipelineStage.IN_CONVERSATION -> Res.string.stage_in_conversation
        PipelineStage.INTERVIEW -> Res.string.stage_interview
        PipelineStage.HIRED -> Res.string.stage_hired
        PipelineStage.DELIVERED -> Res.string.stage_delivered
        PipelineStage.CLOSED -> Res.string.stage_closed
        PipelineStage.LOST -> Res.string.stage_lost
    }
)

@Composable
fun OpportunitySource.label(): String = stringResource(
    when (this) {
        OpportunitySource.UPWORK -> Res.string.source_upwork
        OpportunitySource.LINKEDIN -> Res.string.source_linkedin
        OpportunitySource.REFERRAL -> Res.string.source_referral
        OpportunitySource.OTHER -> Res.string.source_other
    }
)

@Composable
fun OpportunityFilter.label(): String = stringResource(
    when (this) {
        OpportunityFilter.All -> Res.string.filter_all
        OpportunityFilter.Active -> Res.string.filter_active
        OpportunityFilter.Won -> Res.string.filter_won
        OpportunityFilter.Lost -> Res.string.filter_lost
    }
)

@Composable
fun ThemeMode.label(): String = stringResource(themeModeLabelRes(this))

fun themeModeLabelRes(mode: ThemeMode): StringResource = when (mode) {
    ThemeMode.SYSTEM -> Res.string.theme_system
    ThemeMode.LIGHT -> Res.string.theme_light
    ThemeMode.DARK -> Res.string.theme_dark
}

@Composable
fun briefFailureText(kind: BriefFailureKind): String = stringResource(briefFailureRes(kind))

fun briefFailureRes(kind: BriefFailureKind): StringResource = when (kind) {
    BriefFailureKind.BadKey -> Res.string.brief_failed_bad_key
    BriefFailureKind.RateLimited -> Res.string.brief_failed_rate_limit
    BriefFailureKind.InvalidResponse -> Res.string.brief_failed_invalid
    BriefFailureKind.Network -> Res.string.brief_failed_network
}

@Composable
fun briefGateReasonText(reason: BriefGateReason): String = stringResource(briefGateReasonRes(reason))

fun briefGateReasonRes(reason: BriefGateReason): StringResource = when (reason) {
    BriefGateReason.MissingAbout -> Res.string.brief_disabled_no_about
    BriefGateReason.MissingKey -> Res.string.brief_disabled_no_key
    BriefGateReason.MissingAboutAndKey -> Res.string.brief_disabled_no_about_or_key
}

@Composable
fun fitVerdictText(verdict: String): String = stringResource(fitVerdictRes(verdict))

fun fitVerdictRes(verdict: String): StringResource = when (verdict) {
    BriefVerdictYes -> Res.string.brief_verdict_yes
    BriefVerdictNo -> Res.string.brief_verdict_no
    else -> Res.string.brief_verdict_stretch
}

@Composable
fun durationBasisText(basis: String): String = stringResource(durationBasisRes(basis))

fun durationBasisRes(basis: String): StringResource =
    if (basis == BriefBasisProfile) Res.string.brief_basis_profile else Res.string.brief_basis_typical

const val BriefVerdictYes = "yes"
const val BriefVerdictNo = "no"
const val BriefBasisProfile = "profile"
