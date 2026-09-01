package com.erishan.traceback.ai.domain

import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.DurationEstimate
import com.erishan.traceback.opportunity.domain.Fit
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Price
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JobBriefJsonTest {

    @Test
    fun roundTrip_preservesACompleteBrief() {
        val brief = sampleBrief()

        val decoded = decodeJobBriefJson(encodeJobBriefJson(brief))

        assertEquals(brief, decoded)
    }

    @Test
    fun garbageString_returnsNull() {
        assertNull(decodeJobBriefJson("not json"))
        assertNull(decodeJobBriefJson("{"))
        assertNull(decodeJobBriefJson("""{"fit":"nope"}"""))
    }

    @Test
    fun blank_returnsNull() {
        assertNull(decodeJobBriefJson(""))
        assertNull(decodeJobBriefJson("   "))
    }

    @Test
    fun unknownVerdict_mapsToStretch() {
        val json = modelJson(verdict = "maybe")

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 42L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals(VERDICT_STRETCH, brief?.fit?.verdict)
    }

    @Test
    fun pacePresent_setsBasisToProfile() {
        val json = modelJson(basis = "typical")

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 42L,
            model = OPENAI_MODEL,
            pace = "one client at a time",
        )

        assertEquals(BASIS_PROFILE, brief?.duration?.basis)
        assertEquals(42L, brief?.generatedAtEpochMillis)
        assertEquals(OPENAI_MODEL, brief?.model)
    }

    @Test
    fun paceBlank_setsBasisToTypical() {
        val json = modelJson(basis = "profile")

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 7L,
            model = OPENAI_MODEL,
            pace = "  ",
        )

        assertEquals(BASIS_TYPICAL, brief?.duration?.basis)
    }

    @Test
    fun technologies_areCappedAtFive() {
        val json = modelJson(
            technologies = listOf("A", "B", "C", "D", "E", "F"),
        )

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 1L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals(listOf("A", "B", "C", "D", "E"), brief?.approach?.technologies)
    }

    @Test
    fun missingProposal_returnsNull() {
        val json = """
            {
              "fit": {"verdict": "yes", "summary": "Good match"},
              "price": {"low": "2k", "high": "4k", "rationale": "Fits the band"},
              "duration": {"range": "2-3 weeks", "hours": "40", "basis": "typical"},
              "approach": {"summary": "Ship a slice.", "technologies": ["Compose"]}
            }
        """.trimIndent()

        assertNull(
            jobBriefFromModelJson(
                json = json,
                generatedAtEpochMillis = 1L,
                model = OPENAI_MODEL,
                pace = null,
            )
        )
    }

    @Test
    fun unknownKeys_areIgnored() {
        val json = modelJson().replace(
            """"proposal": "I can take this."""",
            """"proposal": "I can take this.", "extra": "ignore me"""",
        )

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 9L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals("I can take this.", brief?.proposal)
    }

    @Test
    fun numericPriceAndHours_areCoercedToStrings() {
        val json = """
            {
              "fit": {"verdict": "yes", "summary": "Stack matches."},
              "proposal": "I can take this.",
              "price": {"low": 2000, "high": 4000, "rationale": "Fits the band"},
              "duration": {"range": "2-3 weeks", "hours": 40, "basis": "typical"},
              "approach": {"summary": "Ship a slice.", "technologies": ["Compose"]}
            }
        """.trimIndent()

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 1L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals("2000", brief?.price?.low)
        assertEquals("4000", brief?.price?.high)
        assertEquals("40", brief?.duration?.hours)
    }

    @Test
    fun dottedKeys_mapToNestedFields() {
        val json = """
            {
              "fit.verdict": "yes",
              "fit.summary": "Stack matches.",
              "proposal": "I can take this.",
              "price.low": "2k",
              "price.high": "4k",
              "price.rationale": "Fits the band",
              "duration.range": "2-3 weeks",
              "duration.hours": "40",
              "duration.basis": "typical",
              "approach.summary": "Ship a slice.",
              "approach.technologies": ["Compose"]
            }
        """.trimIndent()

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 3L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals(VERDICT_YES, brief?.fit?.verdict)
        assertEquals("I can take this.", brief?.proposal)
        assertEquals("2k", brief?.price?.low)
        assertEquals(listOf("Compose"), brief?.approach?.technologies)
    }

    @Test
    fun markdownFence_isStripped() {
        val json = """
            ```json
            ${modelJson()}
            ```
        """.trimIndent()

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 4L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals("I can take this.", brief?.proposal)
    }

    @Test
    fun technologiesAsCommaString_splits() {
        val json = modelJson().replace(
            """"technologies": ["Compose"]""",
            """"technologies": "Compose, Room, Ktor"""",
        )

        val brief = jobBriefFromModelJson(
            json = json,
            generatedAtEpochMillis = 5L,
            model = OPENAI_MODEL,
            pace = null,
        )

        assertEquals(listOf("Compose", "Room", "Ktor"), brief?.approach?.technologies)
    }

    @Test
    fun assistantMessageContent_readsStringContent() {
        val body = """
            {"choices":[{"message":{"role":"assistant","content":"{\"proposal\":\"hi\"}"}}]}
        """.trimIndent()

        assertEquals("""{"proposal":"hi"}""", assistantMessageContent(body))
    }

    @Test
    fun assistantMessageContent_readsTextPartsArray() {
        val body = """
            {"choices":[{"message":{"content":[{"type":"text","text":"{\"a\":1}"}]}}]}
        """.trimIndent()

        assertEquals("""{"a":1}""", assistantMessageContent(body))
    }

    @Test
    fun normalizeVerdict_mapsKnownValues() {
        assertEquals(VERDICT_YES, normalizeVerdict("YES"))
        assertEquals(VERDICT_NO, normalizeVerdict(" No "))
        assertEquals(VERDICT_STRETCH, normalizeVerdict("stretch"))
        assertEquals(VERDICT_STRETCH, normalizeVerdict("???"))
        assertEquals(VERDICT_STRETCH, normalizeVerdict(null))
    }

    private fun sampleBrief() = JobBrief(
        generatedAtEpochMillis = 1_723_600_000_000L,
        model = OPENAI_MODEL,
        fit = Fit(verdict = VERDICT_YES, summary = "Stack matches."),
        proposal = "I can take this.",
        price = Price(low = "2k", high = "4k", rationale = "Fits the band"),
        duration = DurationEstimate(range = "2-3 weeks", hours = "40", basis = BASIS_TYPICAL),
        approach = Approach(summary = "Ship a slice.", technologies = listOf("Compose")),
    )

    private fun modelJson(
        verdict: String = "yes",
        basis: String = "typical",
        technologies: List<String> = listOf("Compose"),
    ): String {
        val tech = technologies.joinToString(",") { "\"$it\"" }
        return """
            {
              "fit": {"verdict": "$verdict", "summary": "Stack matches."},
              "proposal": "I can take this.",
              "price": {"low": "2k", "high": "4k", "rationale": "Fits the band"},
              "duration": {"range": "2-3 weeks", "hours": "40", "basis": "$basis"},
              "approach": {"summary": "Ship a slice.", "technologies": [$tech]}
            }
        """.trimIndent()
    }
}
