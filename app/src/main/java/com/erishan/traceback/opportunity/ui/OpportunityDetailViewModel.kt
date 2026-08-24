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
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.time.Clock

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

    private val _events = Channel<DetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _status = MutableStateFlow(EditStatus())
    private val editMutex = Mutex()

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
                        createdAt = opp.createdAt,
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
        var failed = false
        _status.update { it.copy(pendingSaves = it.pendingSaves + 1, saveFailed = false) }
        try {
            editMutex.withLock {
                repository.update(id, transform)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failed = true
        } finally {
            _status.update {
                it.copy(
                    pendingSaves = (it.pendingSaves - 1).coerceAtLeast(0),
                    saveFailed = it.saveFailed || failed
                )
            }
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

    fun onAddNote(text: String) = saveEdit { opp ->
        opp.copy(
            notes = opp.notes + Note(
                id = UUID.randomUUID().toString(),
                createdAt = Clock.System.now(),
                text = text,
            )
        )
    }

    fun onDeleteNote(noteId: String) = saveEdit { opp ->
        opp.copy(notes = opp.notes.filterNot { it.id == noteId })
    }

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
    val pendingSaves: Int = 0,
    val saveFailed: Boolean = false,
) {
    val isSaving: Boolean
        get() = pendingSaves > 0
}

enum class DetailEvent { Deleted, DeleteFailed }
