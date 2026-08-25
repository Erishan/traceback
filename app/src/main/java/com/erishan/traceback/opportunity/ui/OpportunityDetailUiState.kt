package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Note
import kotlin.time.Instant

sealed interface OpportunityDetailUiState {
    data object Loading : OpportunityDetailUiState
    data object NotFound : OpportunityDetailUiState
    data class Content(
        val title: String,
        val description: String?,
        val source: OpportunitySource,
        val sourceLabel: String?,
        val pipelineStage: PipelineStage,
        val createdAt: Instant?,
        val appliedMessage: String?,
        val notes: List<Note>,
        val aiBrief: JobBrief? = null,
        val canBrief: Boolean = false,
        val briefInFlight: Boolean = false,
        val briefFailed: BriefFailureKind? = null,
        val briefGateReason: BriefGateReason? = null,
        val isSaving: Boolean = false,
        val saveFailed: Boolean = false,
    ) : OpportunityDetailUiState {
        val isBusy: Boolean
            get() = isSaving || briefInFlight
        val briefActionEnabled: Boolean
            get() = canBrief && !isBusy
    }
}

enum class BriefFailureKind {
    BadKey,
    RateLimited,
    InvalidResponse,
    Network,
}

enum class BriefGateReason {
    MissingAbout,
    MissingKey,
    MissingAboutAndKey,
}
