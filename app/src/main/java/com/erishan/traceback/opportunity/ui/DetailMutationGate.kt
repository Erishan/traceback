package com.erishan.traceback.opportunity.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

internal class DetailMutationGate(
    initial: EditStatus = EditStatus(),
) {
    private val _state = MutableStateFlow(initial)
    val state: StateFlow<EditStatus> = _state.asStateFlow()

    fun tryClaimBrief(): Boolean {
        while (true) {
            val current = _state.value
            if (current.briefInFlight) return false
            val next = current.copy(briefInFlight = true, briefFailed = null)
            if (_state.compareAndSet(current, next)) return true
        }
    }

    suspend fun awaitNoPendingSaves() {
        _state.first { it.pendingSaves == 0 }
    }

    fun releaseBrief() {
        _state.update { it.copy(briefInFlight = false) }
    }

    fun tryBeginSave(): Boolean {
        while (true) {
            val current = _state.value
            if (current.briefInFlight) return false
            val next = current.copy(
                pendingSaves = current.pendingSaves + 1,
                saveFailed = false,
            )
            if (_state.compareAndSet(current, next)) return true
        }
    }

    fun endSave(failed: Boolean) {
        _state.update {
            it.copy(
                pendingSaves = (it.pendingSaves - 1).coerceAtLeast(0),
                saveFailed = it.saveFailed || failed,
            )
        }
    }

    fun markBriefFailed(kind: BriefFailureKind) {
        _state.update { it.copy(briefFailed = kind) }
    }
}

internal data class EditStatus(
    val pendingSaves: Int = 0,
    val saveFailed: Boolean = false,
    val briefInFlight: Boolean = false,
    val briefFailed: BriefFailureKind? = null,
) {
    val isSaving: Boolean
        get() = pendingSaves > 0
}
