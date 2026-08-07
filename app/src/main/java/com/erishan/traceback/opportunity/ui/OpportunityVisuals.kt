package com.erishan.traceback.opportunity.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.erishan.traceback.R
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

@StringRes
fun stageLabelRes(stage: PipelineStage): Int = when (stage) {
    PipelineStage.DRAFT -> R.string.stage_draft
    PipelineStage.APPLIED -> R.string.stage_applied
    PipelineStage.IN_CONVERSATION -> R.string.stage_in_conversation
    PipelineStage.INTERVIEW -> R.string.stage_interview
    PipelineStage.HIRED -> R.string.stage_hired
    PipelineStage.DELIVERED -> R.string.stage_delivered
    PipelineStage.CLOSED -> R.string.stage_closed
    PipelineStage.LOST -> R.string.stage_lost
}

@StringRes
fun sourceLabelRes(source: OpportunitySource): Int = when (source) {
    OpportunitySource.UPWORK -> R.string.source_upwork
    OpportunitySource.LINKEDIN -> R.string.source_linkedin
    OpportunitySource.REFERRAL -> R.string.source_referral
    OpportunitySource.OTHER -> R.string.source_other
}
