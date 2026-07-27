package com.erishan.traceback.opportunity.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val source: OpportunitySource,
    val sourceLabel: String?,
    val pipelineStage: PipelineStage,
    val notes: String?,
    val appliedMessage: String?,
)
