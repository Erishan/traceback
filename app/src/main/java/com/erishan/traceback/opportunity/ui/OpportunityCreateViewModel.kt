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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class OpportunityCreateViewModel(
    private val repository: OpportunityRepository
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityCreateViewModel(app.container.opportunityRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(OpportunityCreateUiState())
    val uiState: StateFlow<OpportunityCreateUiState> = _uiState.asStateFlow()

    // Bumps on reset() so an in-flight save cannot mark a fresh sheet as already saved.
    private var saveGeneration = 0

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
                sourceLabel = if (source == OpportunitySource.OTHER) it.sourceLabel else null
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

    fun onSave() {
        val snapshot = _uiState.value
        if (snapshot.isSaving || snapshot.isSaved) return
        val title = snapshot.title.trim()
        if (title.isEmpty()) return
        val generation = ++saveGeneration
        _uiState.update { it.copy(isSaving = true, hasError = false) }
        viewModelScope.launch {
            try {
                repository.save(
                    Opportunity(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = snapshot.description?.takeIf { it.isNotBlank() },
                        source = snapshot.source,
                        sourceLabel = snapshot.sourceLabel?.takeIf { snapshot.source == OpportunitySource.OTHER },
                        pipelineStage = snapshot.pipelineStage,
                        notes = null,
                        appliedMessage = null
                    )
                )
                if (generation != saveGeneration) return@launch
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (generation != saveGeneration) return@launch
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        hasError = true
                    )
                }
            }
        }
    }

    fun reset() {
        saveGeneration++
        _uiState.value = OpportunityCreateUiState()
    }
}
