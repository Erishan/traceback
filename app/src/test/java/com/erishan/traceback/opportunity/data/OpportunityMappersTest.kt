package com.erishan.traceback.opportunity.data

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.DurationEstimate
import com.erishan.traceback.opportunity.domain.Fit
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.Price
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

    @Test
    fun `aiBrief round trips through the entity`() {
        val brief = JobBrief(
            generatedAtEpochMillis = 1_723_600_000_000L,
            model = "gpt-4o",
            fit = Fit(verdict = "yes", summary = "Stack matches."),
            proposal = "I can take this.",
            price = Price(low = "2k", high = "4k", rationale = "Fits the band"),
            duration = DurationEstimate(range = "2-3 weeks", hours = "40", basis = "typical"),
            approach = Approach(summary = "Ship a slice.", technologies = listOf("Compose")),
        )

        val domain = opportunity(createdAt = Instant.fromEpochMilliseconds(1L), aiBrief = brief)
            .toEntity()
            .toDomain()

        assertEquals(brief, domain.aiBrief)
    }

    @Test
    fun `unparseable aiBrief column becomes null`() {
        val domain = entity(notes = null, createdAt = 1L, aiBrief = "not json").toDomain()

        assertNull(domain.aiBrief)
    }

    @Test
    fun `null aiBrief column stays null`() {
        assertNull(entity(notes = null, createdAt = 1L, aiBrief = null).toDomain().aiBrief)
    }

    private fun entity(
        notes: String?,
        createdAt: Long,
        aiBrief: String? = null,
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
        aiBrief = aiBrief,
    )

    private fun opportunity(createdAt: Instant?, aiBrief: JobBrief? = null) = Opportunity(
        id = "op-1",
        title = "Opportunity",
        description = null,
        source = OpportunitySource.UPWORK,
        sourceLabel = null,
        pipelineStage = PipelineStage.DRAFT,
        createdAt = createdAt,
        notes = emptyList(),
        appliedMessage = null,
        aiBrief = aiBrief,
    )
}
