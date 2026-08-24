package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
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
        val isSaving: Boolean = false,
        val saveFailed: Boolean = false,
    ) : OpportunityDetailUiState
}
