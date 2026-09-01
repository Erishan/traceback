package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.DurationEstimate
import com.erishan.traceback.opportunity.domain.Fit
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.opportunity.domain.Price
import com.erishan.traceback.ui.BriefBasisProfile
import com.erishan.traceback.ui.BriefVerdictYes
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlin.time.Instant

private val PreviewBrief = JobBrief(
    generatedAtEpochMillis = 1_723_600_000_000L,
    model = "gpt-4o",
    fit = Fit(
        verdict = BriefVerdictYes,
        summary = "Compose work with a clear funnel goal - squarely your stack.",
    ),
    proposal = "I rebuilt a five-step signup into two screens for a B2B trial last quarter and " +
        "cut drop-off by a third. I would start by instrumenting the current funnel so we " +
        "argue from numbers, then ship the new flow behind a flag and compare cohorts. " +
        "Two weeks of build, one week watching it, and you keep the measurement harness " +
        "either way. Happy to walk the current flow with you before we scope it.",
    price = Price(
        low = "$3.2k",
        high = "$4.5k",
        rationale = "Two weeks at your mid band, plus a week of measurement.",
    ),
    duration = DurationEstimate(
        range = "40-56 hours",
        hours = "48",
        basis = BriefBasisProfile,
    ),
    approach = Approach(
        summary = "Instrument the funnel, then rebuild signup as two screens behind a flag.",
        technologies = listOf("Compose", "Firebase", "Figma"),
    ),
)
private val PreviewCreatedAt = Instant.fromEpochMilliseconds(1_723_600_000_000L)

private fun previewContent(
    stage: PipelineStage,
    description: String? = "Rework the multi-step signup and cut mobile drop-off across the trial funnel.",
    appliedMessage: String? = null,
) = OpportunityDetailUiState.Content(
    title = "SaaS onboarding flow redesign",
    description = description,
    source = OpportunitySource.UPWORK,
    sourceLabel = null,
    pipelineStage = stage,
    createdAt = PreviewCreatedAt,
    appliedMessage = appliedMessage,
    notes = listOf(
        Note(
            id = "n1",
            createdAt = PreviewCreatedAt,
            text = "Client wants a Loom walkthrough before the call.",
        ),
        Note(
            id = "n2",
            createdAt = Instant.fromEpochMilliseconds(1_723_700_000_000L),
            text = "Followed up Monday, no reply yet.",
        ),
    ),
)

@Composable
internal fun DetailScreenShowcase(darkTheme: Boolean) {
    TracebackTheme(darkTheme = darkTheme, reducedMotion = true) {
        OpportunityDetailScreen(
            uiState = previewContent(
                stage = PipelineStage.INTERVIEW,
                appliedMessage = "Sent a two-paragraph note with the Loom link and a rate band.",
            ).copy(aiBrief = PreviewBrief, canBrief = true),
            onBack = {},
            onDelete = {},
            onStageChange = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onSourceChange = {},
            onSourceLabelChange = {},
            onAddNote = {},
            onDeleteNote = {},
            onAppliedMessageChange = {},
            onBrief = {},
            onOpenMe = {},
            deleteFailed = false,
            onDeleteErrorDismiss = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
