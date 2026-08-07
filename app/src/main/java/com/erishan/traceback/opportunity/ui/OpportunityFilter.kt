package com.erishan.traceback.opportunity.ui

import androidx.annotation.StringRes
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.PipelineStage

enum class OpportunityFilter(@StringRes val labelRes: Int) {
    All(R.string.filter_all),
    Active(R.string.filter_active),
    Won(R.string.filter_won),
    Lost(R.string.filter_lost),
}

fun OpportunityFilter.matches(stage: PipelineStage): Boolean = when (this) {
    OpportunityFilter.All -> true
    OpportunityFilter.Active -> stage in listOf(
        PipelineStage.DRAFT, PipelineStage.APPLIED, PipelineStage.IN_CONVERSATION, PipelineStage.INTERVIEW
    )
    OpportunityFilter.Won -> stage in listOf(PipelineStage.HIRED, PipelineStage.DELIVERED)
    OpportunityFilter.Lost -> stage in listOf(PipelineStage.CLOSED, PipelineStage.LOST)
}