package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.PipelineStage

enum class OpportunityFilter {
    All,
    Active,
    Won,
    Lost,
    ;

    val label: String
        get() = when (this) {
            All -> "All"
            Active -> "Active"
            Won -> "Won"
            Lost -> "Lost"
        }
}

fun OpportunityFilter.matches(stage: PipelineStage): Boolean = when (this) {
    OpportunityFilter.All -> true
    OpportunityFilter.Active -> stage in listOf(
        PipelineStage.DRAFT, PipelineStage.APPLIED, PipelineStage.IN_CONVERSATION, PipelineStage.INTERVIEW
    )
    OpportunityFilter.Won -> stage in listOf(PipelineStage.HIRED, PipelineStage.DELIVERED)
    OpportunityFilter.Lost -> stage in listOf(PipelineStage.CLOSED, PipelineStage.LOST)
}
