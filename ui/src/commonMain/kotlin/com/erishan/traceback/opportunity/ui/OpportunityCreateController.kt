package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OpportunityCreateController(
    private val scope: CoroutineScope,
    private val repository: OpportunityRepository,
) {
    private val _uiState = MutableStateFlow(OpportunityCreateUiState())
    val uiState: StateFlow<OpportunityCreateUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onSourceChange(source: OpportunitySource) {
        _uiState.update {
            it.copy(
                source = source,
                sourceLabel = if (source == OpportunitySource.OTHER) it.sourceLabel else null,
            )
        }
    }

    fun onSourceLabelChange(value: String) {
        if (_uiState.value.source == OpportunitySource.OTHER) {
            _uiState.update { it.copy(sourceLabel = value) }
        }
    }

    fun onPipelineStageChange(stage: PipelineStage) {
        _uiState.update { it.copy(pipelineStage = stage) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onSave() {
        scope.launch {
            val draft = _uiState.value
            var saved = false
            var failed = false
            _uiState.update { it.copy(isSaving = true, isSaved = false, hasError = false) }
            try {
                repository.save(
                    Opportunity(
                        id = Uuid.random().toString(),
                        title = draft.title,
                        description = draft.description?.takeIf { it.isNotBlank() },
                        source = draft.source,
                        sourceLabel = draft.sourceLabel?.takeIf { draft.source == OpportunitySource.OTHER },
                        pipelineStage = draft.pipelineStage,
                        createdAt = Clock.System.now(),
                        notes = emptyList(),
                        appliedMessage = null,
                    )
                )
                saved = true
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                failed = true
            } finally {
                _uiState.update { it.copy(isSaving = false, isSaved = saved, hasError = failed) }
            }
        }
    }

    fun reset() {
        _uiState.value = OpportunityCreateUiState()
    }
}
