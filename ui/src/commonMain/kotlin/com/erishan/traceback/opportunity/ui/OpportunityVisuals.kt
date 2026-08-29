package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
