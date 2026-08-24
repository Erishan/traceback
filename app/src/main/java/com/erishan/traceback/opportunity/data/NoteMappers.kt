package com.erishan.traceback.opportunity.data

import com.erishan.traceback.opportunity.domain.Note
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


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
        createdAt = createdAtEpochMillis.toKnownInstantOrNull(),
        text = text,
    )

private fun Note.toDto(): NoteDto =
    NoteDto(
        id = id,
        createdAtEpochMillis = createdAt.toStoredEpochMillis(),
        text = text,
    )

internal fun String?.toNotes(fallbackCreatedAtMillis: Long): List<Note> {
    if (isNullOrBlank()) return emptyList()
    return try {
        notesJson.decodeFromString<List<NoteDto>>(this).map { it.toDomain() }
    } catch (e: Exception) {
        listOf(
            Note(
                id = "legacy-$fallbackCreatedAtMillis",
                createdAt = fallbackCreatedAtMillis.toKnownInstantOrNull(),
                text = this,
            )
        )
    }
}

internal fun List<Note>.toNotesColumn(): String? =
    if (isEmpty()) null else notesJson.encodeToString(map { it.toDto() })
