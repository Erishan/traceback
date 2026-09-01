package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OpportunityCreateControllerTest {

    @Test
    fun successfulSave_marksDraftSavedAndClearsTransientState() = runBlocking {
        val repository = FakeOpportunityRepository()
        val controller = OpportunityCreateController(this, repository)
        controller.onTitleChange("KMP UI migration")
        controller.onDescriptionChange("Move create flow state into common code.")

        controller.onSave()
        yield()

        val state = controller.uiState.value
        assertFalse(state.isSaving)
        assertTrue(state.isSaved)
        assertFalse(state.hasError)
        assertEquals("KMP UI migration", repository.saved.single().title)
        assertEquals("Move create flow state into common code.", repository.saved.single().description)
    }

    @Test
    fun failedSave_marksErrorAndStopsSaving() = runBlocking {
        val saveAttempted = CompletableDeferred<Unit>()
        val repository = FakeOpportunityRepository {
            saveAttempted.complete(Unit)
            throw IllegalStateException("database unavailable")
        }
        val controller = OpportunityCreateController(this, repository)
        controller.onTitleChange("Unstable backend")

        controller.onSave()
        saveAttempted.await()
        yield()

        val state = controller.uiState.value
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertTrue(state.hasError)
    }

    @Test
    fun cancelledSaveDoesNotProduceErrorState() = runBlocking {
        val saveAttempted = CompletableDeferred<Unit>()
        val repository = FakeOpportunityRepository {
            saveAttempted.complete(Unit)
            throw CancellationException("route disposed")
        }
        val controller = OpportunityCreateController(this, repository)
        controller.onTitleChange("Cancelled draft")

        controller.onSave()
        saveAttempted.await()
        yield()

        val state = controller.uiState.value
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertFalse(state.hasError)
    }

    private class FakeOpportunityRepository(
        private val saveBehavior: suspend (Opportunity) -> Unit = {},
    ) : OpportunityRepository {
        val saved = mutableListOf<Opportunity>()

        override suspend fun save(opportunity: Opportunity) {
            saved += opportunity
            saveBehavior(opportunity)
        }

        override suspend fun update(
            id: String,
            transform: (Opportunity) -> Opportunity,
        ): Boolean = false

        override suspend fun delete(id: String) = Unit

        override fun observeById(id: String): Flow<Opportunity?> = MutableStateFlow(null)

        override fun observeAll(): Flow<List<Opportunity>> = MutableStateFlow(emptyList())
    }
}
