package com.erishan.traceback.opportunity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OpportunityDetailViewModel(
    private val id: String,
    private val repository: OpportunityRepository
) : ViewModel() {
    companion object {
        fun provideFactory(id: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityDetailViewModel(id, app.container.opportunityRepository)
            }
        }
    }

    private fun currentOpportunity(): Opportunity? =
        (uiState.value as? OpportunityDetailUiState.Content)?.let { c ->
            Opportunity(
                id = id,
                title = c.title,
                description = c.description,
                source = c.source,
                sourceLabel = c.sourceLabel,
                pipelineStage = c.pipelineStage,
                notes = c.notes,
                appliedMessage = c.appliedMessage,
            )
        }

    private val _events = Channel<DetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _status = MutableStateFlow(EditStatus())

    val uiState: StateFlow<OpportunityDetailUiState> =
        combine(repository.observeById(id), _status) { opp, status ->
            when {
                opp == null -> OpportunityDetailUiState.NotFound
                else -> {
                    OpportunityDetailUiState.Content(
                        title = opp.title,
                        description = opp.description,
                        source = opp.source,
                        sourceLabel = opp.sourceLabel,
                        pipelineStage = opp.pipelineStage,
                        appliedMessage = opp.appliedMessage,
                        notes = opp.notes,
                        isSaving = status.isSaving,
                        saveFailed = status.saveFailed
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OpportunityDetailUiState.Loading
        )

    private fun saveEdit(transform: (Opportunity) -> Opportunity) = viewModelScope.launch {
        val current = currentOpportunity() ?: return@launch
        _status.update { it.copy(isSaving = true, saveFailed = false) }
        try {
            repository.save(transform(current))
            _status.update { it.copy(isSaving = false) }
        } catch (e: Exception) {
            _status.update { it.copy(isSaving = false, saveFailed = true) }
        }
    }

    fun onStageChange(stage: PipelineStage) = saveEdit { it.copy(pipelineStage = stage) }

    fun onTitleChange(title: String) = saveEdit { it.copy(title = title) }

    fun onDescriptionChange(description: String) = saveEdit { it.copy(description = description) }

    fun onSourceChange(source: OpportunitySource) = saveEdit {
        it.copy(
            source = source,
            sourceLabel = if (source == OpportunitySource.OTHER) it.sourceLabel else null
        )
    }

    fun onSourceLabelChange(label: String) = saveEdit {
        it.copy(
            sourceLabel = if (it.source == OpportunitySource.OTHER) label else null
        )
    }

    fun onNotesChange(notes: String) = saveEdit { it.copy(notes = notes) }

    fun onAppliedMessageChange(message: String) = saveEdit { it.copy(appliedMessage = message) }

    fun delete() {
        viewModelScope.launch {
            try {
                repository.delete(id)
                _events.send(DetailEvent.Deleted)
            } catch (e: Exception) {
                _events.send(DetailEvent.DeleteFailed)
            }
        }
    }
}

private data class EditStatus(
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false,
)

enum class DetailEvent { Deleted, DeleteFailed }