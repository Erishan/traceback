package com.erishan.traceback.opportunity.data

import com.erishan.traceback.opportunity.domain.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Instant

class NoteMappersTest {

    @Test
    fun `notes survive a round trip through the column`() {
        val notes = listOf(
            note("n1", 1_723_600_000_000L, "Client wants a walkthrough first"),
            note("n2", 1_723_700_000_000L, "Followed up, no reply"),
        )

        val column = notes.toNotesColumn()

        assertEquals(notes, column.toNotes(fallbackCreatedAtMillis = 0L))
    }

    @Test
    fun `an empty list is stored as null rather than an empty array`() {
        assertNull(emptyList<Note>().toNotesColumn())
    }

    @Test
    fun `a null or blank column reads as no notes`() {
        assertTrue(null.toNotes(fallbackCreatedAtMillis = 0L).isEmpty())
        assertTrue("   ".toNotes(fallbackCreatedAtMillis = 0L).isEmpty())
    }

    @Test
    fun `a column that is not a json array becomes one legacy note`() {
        val legacy = "a note written before notes became a list"

        val notes = legacy.toNotes(fallbackCreatedAtMillis = 42L)

        assertEquals(1, notes.size)
        assertEquals(legacy, notes.single().text)
        assertEquals(Instant.fromEpochMilliseconds(42L), notes.single().createdAt)
    }

    @Test
    fun `a legacy note with an unknown fallback date keeps the date unknown`() {
        val legacy = "a note written before opportunity createdAt existed"

        val notes = legacy.toNotes(fallbackCreatedAtMillis = 0L)

        assertEquals(1, notes.size)
        assertEquals(legacy, notes.single().text)
        assertNull(notes.single().createdAt)
    }

    @Test
    fun `a serialized note with a zero timestamp keeps the date unknown`() {
        val notes = """[{"id":"n1","createdAtEpochMillis":0,"text":"Legacy note"}]"""
            .toNotes(fallbackCreatedAtMillis = 42L)

        assertEquals(1, notes.size)
        assertEquals("Legacy note", notes.single().text)
        assertNull(notes.single().createdAt)
    }

    @Test
    fun `a malformed json array degrades instead of failing the read`() {
        val notes = """[{"id":"n1"}]""".toNotes(fallbackCreatedAtMillis = 7L)

        assertEquals(1, notes.size)
        assertEquals("""[{"id":"n1"}]""", notes.single().text)
    }

    private fun note(id: String, millis: Long, text: String) =
        Note(id = id, createdAt = Instant.fromEpochMilliseconds(millis), text = text)
}
