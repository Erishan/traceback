package com.erishan.traceback.opportunity.data

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Opportunity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.time.Instant

class OpportunityMappersTest {

    @Test
    fun `legacy zero createdAt maps to an unknown domain date`() {
        val domain = entity(
            notes = "a note written before notes became a list",
            createdAt = 0L,
        ).toDomain()

        assertNull(domain.createdAt)
        assertEquals(1, domain.notes.size)
        assertNull(domain.notes.single().createdAt)
        assertEquals("a note written before notes became a list", domain.notes.single().text)
    }

    @Test
    fun `known createdAt still maps to a real domain date`() {
        val createdAtMillis = 1_723_600_000_000L

        val domain = entity(
            notes = "a legacy note with a known opportunity date",
            createdAt = createdAtMillis,
        ).toDomain()

        val expected = Instant.fromEpochMilliseconds(createdAtMillis)
        assertEquals(expected, domain.createdAt)
        assertEquals(expected, domain.notes.single().createdAt)
    }

    @Test
    fun `unknown domain createdAt stores the zero sentinel`() {
        val entity = opportunity(createdAt = null).toEntity()

        assertEquals(0L, entity.createdAt)
    }

    private fun entity(
        notes: String?,
        createdAt: Long,
    ) = OpportunityEntity(
        id = "op-1",
        title = "Opportunity",
        description = null,
        source = OpportunitySource.UPWORK,
        sourceLabel = null,
        pipelineStage = PipelineStage.DRAFT,
        notes = notes,
        createdAt = createdAt,
        appliedMessage = null,
    )

    private fun opportunity(createdAt: Instant?) = Opportunity(
        id = "op-1",
        title = "Opportunity",
        description = null,
        source = OpportunitySource.UPWORK,
        sourceLabel = null,
        pipelineStage = PipelineStage.DRAFT,
        createdAt = createdAt,
        notes = emptyList(),
        appliedMessage = null,
    )
}
