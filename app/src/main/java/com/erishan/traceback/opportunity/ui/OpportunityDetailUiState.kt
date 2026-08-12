package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage

sealed interface OpportunityDetailUiState {
    data object Loading : OpportunityDetailUiState
    data object NotFound : OpportunityDetailUiState
    data class Content(
        val title: String,
        val description: String?,
        val source: OpportunitySource,
        val sourceLabel: String?,
        val pipelineStage: PipelineStage,
        val appliedMessage: String?,
        val notes: String?,
        val isSaving: Boolean = false,
        val saveFailed: Boolean = false,
    ) : OpportunityDetailUiState
}
