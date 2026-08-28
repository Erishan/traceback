package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun stageColor(stage: PipelineStage): Color = with(TracebackTheme.colors) {
    when (stage) {
        PipelineStage.DRAFT -> stageDraft
        PipelineStage.APPLIED -> stageApplied
        PipelineStage.IN_CONVERSATION -> stageInConversation
        PipelineStage.INTERVIEW -> stageInterview
        PipelineStage.HIRED -> stageHired
        PipelineStage.DELIVERED -> stageDelivered
        PipelineStage.CLOSED -> stageClosed
        PipelineStage.LOST -> stageLost
    }
}

fun PipelineStage.label(): String = when (this) {
    PipelineStage.DRAFT -> "Draft"
    PipelineStage.APPLIED -> "Applied"
    PipelineStage.IN_CONVERSATION -> "In conversation"
    PipelineStage.INTERVIEW -> "Interview"
    PipelineStage.HIRED -> "Hired"
    PipelineStage.DELIVERED -> "Delivered"
    PipelineStage.CLOSED -> "Closed"
    PipelineStage.LOST -> "Lost"
}

fun OpportunitySource.label(): String = when (this) {
    OpportunitySource.UPWORK -> "Upwork"
    OpportunitySource.LINKEDIN -> "LinkedIn"
    OpportunitySource.REFERRAL -> "Referral"
    OpportunitySource.OTHER -> "Other"
}
