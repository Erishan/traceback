package com.erishan.traceback.opportunity.data

import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.DurationEstimate
import com.erishan.traceback.opportunity.domain.Fit
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.opportunity.domain.Price
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.time.Instant

class JobBriefMappersTest {

    @Test
    fun briefSurvivesARoundTripThroughTheColumn() {
        val brief = JobBrief(
            generatedAtEpochMillis = 1_723_600_000_000L,
            model = "gpt-4o",
            fit = Fit(verdict = "stretch", summary = "Close, missing a library."),
            proposal = "Happy to pair on the gap.",
            price = Price(low = "3k", high = "5k", rationale = "Above the floor"),
            duration = DurationEstimate(range = "3 weeks", hours = "50", basis = "profile"),
            approach = Approach(summary = "Spike, then ship.", technologies = listOf("Room", "Compose")),
        )

        assertEquals(brief, brief.toJobBriefColumn().toJobBrief())
    }

    @Test
    fun garbageStringBecomesNull() {
        assertNull("not json".toJobBrief())
        assertNull("{".toJobBrief())
        assertNull("""{"proposal":1}""".toJobBrief())
    }

    @Test
    fun emptyOrNullColumnIsNull() {
        assertNull(null.toJobBrief())
        assertNull("".toJobBrief())
        assertNull("   ".toJobBrief())
    }

    @Test
    fun notesMappingStillWorksAlongsideABrief() {
        val notes = listOf(
            Note(
                id = "n1",
                createdAt = Instant.fromEpochMilliseconds(1_723_600_000_000L),
                text = "Client wants a walkthrough first",
            )
        )

        assertEquals(notes, notes.toNotesColumn().toNotes(fallbackCreatedAtMillis = 0L))
    }
}
