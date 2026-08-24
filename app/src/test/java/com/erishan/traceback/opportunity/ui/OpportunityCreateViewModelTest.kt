package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OpportunityCreateViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun onSave_doesNotPersistBlankOrWhitespaceTitle() = runTest(dispatcher) {
        val repo = FakeOpportunityRepository()
        val vm = OpportunityCreateViewModel(repo)

        vm.onTitleChange("   ")
        vm.onSave()
        advanceUntilIdle()

        assertTrue(repo.saved.isEmpty())
        assertFalse(vm.uiState.value.isSaving)
        assertFalse(vm.uiState.value.isSaved)
    }

    @Test
    fun onSave_secondTapDoesNotInsertAnotherRow() = runTest(dispatcher) {
        val repo = FakeOpportunityRepository()
        val vm = OpportunityCreateViewModel(repo)

        vm.onTitleChange("SaaS onboarding")
        vm.onSave()
        vm.onSave()
        advanceUntilIdle()

        assertEquals(1, repo.saved.size)
        assertEquals("SaaS onboarding", repo.saved.single().title)
        assertTrue(vm.uiState.value.isSaved)
    }

    @Test
    fun reset_duringInFlightSave_doesNotLeaveIsSavedTrue() = runTest(dispatcher) {
        val repo = FakeOpportunityRepository(holdSave = true)
        val vm = OpportunityCreateViewModel(repo)

        vm.onTitleChange("Held save")
        vm.onSave()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.isSaving)

        vm.reset()
        repo.releaseSave()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isSaved)
        assertFalse(vm.uiState.value.isSaving)
        assertEquals("", vm.uiState.value.title)
        assertEquals(1, repo.saved.size)
    }

    @Test
    fun onSave_afterSuccessfulSave_doesNotInsertAnotherRow() = runTest(dispatcher) {
        val repo = FakeOpportunityRepository()
        val vm = OpportunityCreateViewModel(repo)

        vm.onTitleChange("Once")
        vm.onSave()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.isSaved)

        vm.onSave()
        advanceUntilIdle()

        assertEquals(1, repo.saved.size)
    }

    private class FakeOpportunityRepository(
        private val holdSave: Boolean = false,
    ) : OpportunityRepository {
        val saved = mutableListOf<Opportunity>()
        private val proceed = CompletableDeferred<Unit>()

        override suspend fun save(opportunity: Opportunity) {
            if (holdSave) proceed.await()
            saved += opportunity
        }

        fun releaseSave() {
            proceed.complete(Unit)
        }

        override suspend fun delete(id: String) = Unit

        override fun observeById(id: String): Flow<Opportunity?> = emptyFlow()

        override fun observeAll(): Flow<List<Opportunity>> = emptyFlow()
    }
}
