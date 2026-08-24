package com.erishan.traceback.opportunity.data

import androidx.room3.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.erishan.traceback.core.db.AppDatabase
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.opportunity.domain.Opportunity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Instant

@RunWith(AndroidJUnit4::class)
class OpportunityRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: OpportunityRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()
        repository = OpportunityRepositoryImpl(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun concurrentUpdatesAreAppliedToTheLatestStoredOpportunity() = runBlocking {
        val opportunity = opportunity(
            pipelineStage = PipelineStage.DRAFT,
            notes = listOf(note(id = "n1", text = "Existing note")),
        )
        repository.save(opportunity)

        val start = CompletableDeferred<Unit>()
        coroutineScope {
            val addNote = async {
                start.await()
                repository.update(opportunity.id) {
                    it.copy(notes = it.notes + note(id = "n2", text = "New note"))
                }
            }
            val changeStage = async {
                start.await()
                repository.update(opportunity.id) {
                    it.copy(pipelineStage = PipelineStage.HIRED)
                }
            }

            start.complete(Unit)

            assertEquals(listOf(true, true), awaitAll(addNote, changeStage))
        }

        val saved = database.opportunityDao().getById(opportunity.id)?.toDomain()

        assertNotNull(saved)
        assertEquals(PipelineStage.HIRED, saved?.pipelineStage)
        assertEquals(2, saved?.notes?.size)
        assertTrue(saved?.notes.orEmpty().any { it.id == "n2" })
    }

    private fun opportunity(
        pipelineStage: PipelineStage,
        notes: List<Note>,
    ) = Opportunity(
        id = "op-1",
        title = "Opportunity",
        description = null,
        source = OpportunitySource.UPWORK,
        sourceLabel = null,
        pipelineStage = pipelineStage,
        createdAt = Instant.fromEpochMilliseconds(1_723_600_000_000L),
        notes = notes,
        appliedMessage = null,
    )

    private fun note(id: String, text: String) =
        Note(
            id = id,
            createdAt = Instant.fromEpochMilliseconds(1_723_600_000_000L),
            text = text,
        )
}
