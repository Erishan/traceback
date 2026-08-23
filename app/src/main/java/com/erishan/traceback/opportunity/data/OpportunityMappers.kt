package com.erishan.traceback.opportunity.data

import com.erishan.traceback.opportunity.domain.Opportunity
import kotlin.time.Instant

fun OpportunityEntity.toDomain(): Opportunity {
    return Opportunity(
        id = this.id,
        title = this.title,
        description = this.description,
        source = this.source,
        sourceLabel = this.sourceLabel,
        pipelineStage = this.pipelineStage,
        createdAt = Instant.fromEpochMilliseconds(this.createdAt),
        notes = this.notes.toNotes(this.createdAt),
        appliedMessage = this.appliedMessage,
    )
}

fun List<OpportunityEntity>.toDomainList(): List<Opportunity> {
    return this.map { it.toDomain() }
}

fun Opportunity.toEntity(): OpportunityEntity {
    return OpportunityEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        source = this.source,
        sourceLabel = this.sourceLabel,
        pipelineStage = this.pipelineStage,
        notes = this.notes.toNotesColumn(),
        createdAt = this.createdAt.toEpochMilliseconds(),
        appliedMessage = this.appliedMessage,
    )
}
