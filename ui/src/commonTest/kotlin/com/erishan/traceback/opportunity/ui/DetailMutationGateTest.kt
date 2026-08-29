package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class DetailMutationGateTest {

    @Test
    fun secondClaimLoses_soTwoTapsCannotBothEnterBrief() {
        val gate = DetailMutationGate()

        assertTrue(gate.tryClaimBrief())
        assertTrue(gate.state.value.briefInFlight)
        assertFalse(gate.tryClaimBrief())
    }

    @Test
    fun concurrentClaims_onlyOneWins() = runBlocking {
        val gate = DetailMutationGate()
        val wins = AtomicInteger(0)

        coroutineScope {
            repeat(64) {
                launch(Dispatchers.Default) {
                    if (gate.tryClaimBrief()) wins.incrementAndGet()
                }
            }
        }

        assertEquals(1, wins.get())
        assertTrue(gate.state.value.briefInFlight)
    }

    @Test
    fun beginSaveRejectedWhileBriefInFlight() {
        val gate = DetailMutationGate()
        assertTrue(gate.tryClaimBrief())

        assertFalse(gate.tryBeginSave())
        assertEquals(0, gate.state.value.pendingSaves)
    }

    @Test
    fun awaitBeginSaveWaitsUntilBriefReleasesThenClaims() = runBlocking {
        val gate = DetailMutationGate()
        assertTrue(gate.tryClaimBrief())

        val waiter = launch { gate.awaitBeginSave() }
        yield()
        assertTrue(waiter.isActive)
        assertEquals(0, gate.state.value.pendingSaves)

        gate.releaseBrief()
        waiter.join()

        assertEquals(1, gate.state.value.pendingSaves)
        assertFalse(gate.state.value.briefInFlight)
    }

    @Test
    fun briefCanClaimWhileSavePending_thenAwaitUnblocksAfterSaveEnds() = runBlocking {
        val gate = DetailMutationGate()
        assertTrue(gate.tryBeginSave())
        assertTrue(gate.tryClaimBrief())

        val waiter = launch { gate.awaitNoPendingSaves() }
        yield()
        assertTrue(waiter.isActive)

        gate.endSave(failed = false)
        waiter.join()

        assertEquals(0, gate.state.value.pendingSaves)
        assertTrue(gate.state.value.briefInFlight)
    }

    @Test
    fun concurrentSaveAndClaim_neverOverlapInFlightWork() = runBlocking {
        repeat(32) {
            val gate = DetailMutationGate()
            var saveAccepted = false
            var briefAccepted = false

            coroutineScope {
                launch(Dispatchers.Default) { saveAccepted = gate.tryBeginSave() }
                launch(Dispatchers.Default) { briefAccepted = gate.tryClaimBrief() }
            }

            val status = gate.state.value
            if (briefAccepted) {
                assertTrue(status.briefInFlight)
                if (saveAccepted) {
                    assertEquals(1, status.pendingSaves)
                    assertFalse(gate.tryBeginSave())
                } else {
                    assertEquals(0, status.pendingSaves)
                    assertFalse(gate.tryBeginSave())
                }
            } else {
                assertTrue(saveAccepted)
                assertFalse(status.briefInFlight)
                assertEquals(1, status.pendingSaves)
            }
        }
    }

    @Test
    fun releaseBriefAllowsANewClaimAndSave() {
        val gate = DetailMutationGate()
        assertTrue(gate.tryClaimBrief())
        gate.markBriefFailed(BriefFailureKind.Network)
        gate.releaseBrief()

        assertFalse(gate.state.value.briefInFlight)
        assertEquals(BriefFailureKind.Network, gate.state.value.briefFailed)
        assertTrue(gate.tryBeginSave())
        gate.endSave(failed = false)
        assertTrue(gate.tryClaimBrief())
        assertEquals(null, gate.state.value.briefFailed)
    }
}

class OpportunityDetailUiStateTest {

    @Test
    fun briefActionDisabledWhileSavingOrInFlight() {
        val ready = content(canBrief = true)
        assertTrue(ready.briefActionEnabled)
        assertFalse(ready.isBusy)

        assertFalse(ready.copy(isSaving = true).briefActionEnabled)
        assertFalse(ready.copy(briefInFlight = true).briefActionEnabled)
        assertFalse(ready.copy(canBrief = false).briefActionEnabled)
        assertTrue(ready.copy(isSaving = true).isBusy)
        assertTrue(ready.copy(briefInFlight = true).isBusy)
    }

    private fun content(
        canBrief: Boolean = false,
        briefInFlight: Boolean = false,
        isSaving: Boolean = false,
    ) = OpportunityDetailUiState.Content(
        title = "Job",
        description = null,
        source = OpportunitySource.UPWORK,
        sourceLabel = null,
        pipelineStage = PipelineStage.APPLIED,
        createdAt = null,
        appliedMessage = null,
        notes = emptyList(),
        canBrief = canBrief,
        briefInFlight = briefInFlight,
        isSaving = isSaving,
    )
}
