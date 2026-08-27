package com.erishan.traceback.opportunity.domain

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import kotlin.time.Instant

data class Opportunity(
    val id: String,
    val title: String,
    val description: String?,
    val source: OpportunitySource,
    val sourceLabel: String?,
    val pipelineStage: PipelineStage,
    val createdAt: Instant?,
    val notes: List<Note>,
    val appliedMessage: String?,
    val aiBrief: JobBrief? = null,
)
