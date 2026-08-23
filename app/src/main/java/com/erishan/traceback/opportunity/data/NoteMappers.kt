package com.erishan.traceback.opportunity.data

import com.erishan.traceback.opportunity.domain.Note
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant


@Serializable
private data class NoteDto(
    val id: String,
    val createdAtEpochMillis: Long,
    val text: String,
)

private val notesJson = Json { ignoreUnknownKeys = true }

private fun NoteDto.toDomain(): Note =
    Note(
        id = id,
        createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
        text = text,
    )

private fun Note.toDto(): NoteDto =
    NoteDto(
        id = id,
        createdAtEpochMillis = createdAt.toEpochMilliseconds(),
        text = text,
    )

// column -> domain. Rows written before notes became a list held a single free-text string
// (or null); if the column doesn't parse as a JSON array, treat it as one legacy note.
internal fun String?.toNotes(fallbackCreatedAtMillis: Long): List<Note> {
    if (isNullOrBlank()) return emptyList()
    return try {
        notesJson.decodeFromString<List<NoteDto>>(this).map { it.toDomain() }
    } catch (e: Exception) {
        listOf(
            Note(
                id = "legacy-$fallbackCreatedAtMillis",
                createdAt = Instant.fromEpochMilliseconds(fallbackCreatedAtMillis),
                text = this,
            )
        )
    }
}

// domain -> column. Empty list is stored as NULL (not "[]") to keep the column tidy.
internal fun List<Note>.toNotesColumn(): String? =
    if (isEmpty()) null else notesJson.encodeToString(map { it.toDto() })
