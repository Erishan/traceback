package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlin.time.Instant

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
        Note(
            id = "n3",
            createdAt = Instant.fromEpochMilliseconds(1_723_800_000_000L),
            text = "They asked for a phased rollout behind a feature flag.",
        ),
    ),
)

@Composable
fun DetailScreenShowcase(darkTheme: Boolean) {
    TracebackTheme(darkTheme = darkTheme, reducedMotion = true) {
        OpportunityDetailScreen(
            uiState = previewContent(
                stage = PipelineStage.INTERVIEW,
                appliedMessage = "Sent a two-paragraph note with the Loom link and a rate band.",
            ).copy(canBrief = true),
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
