package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage

data class OpportunityCreateUiState(
    val title: String = "",
    val description: String? = null,
    val source: OpportunitySource = OpportunitySource.UPWORK,
    val sourceLabel: String? = null,
    val pipelineStage: PipelineStage = PipelineStage.DRAFT,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val hasError: Boolean = false
)
