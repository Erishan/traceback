package com.erishan.traceback.opportunity.domain

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage

data class Opportunity(
    val id: String,
    val title: String,
    val description: String?,
    val source: OpportunitySource,
    val sourceLabel: String?, // only is going to fill when OpportunitySource is OTHER
    val pipelineStage: PipelineStage,
    val notes: String?,
    val appliedMessage: String?,
)
