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
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.flow.StateFlow

class OpportunityCreateViewModel(
    repository: OpportunityRepository,
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityCreateViewModel(app.container.opportunityRepository)
            }
        }
    }

    private val controller = OpportunityCreateController(
        scope = viewModelScope,
        repository = repository,
    )

    val uiState: StateFlow<OpportunityCreateUiState> = controller.uiState

    fun onTitleChange(value: String) = controller.onTitleChange(value)

    fun onDescriptionChange(value: String) = controller.onDescriptionChange(value)

    fun onSourceChange(source: OpportunitySource) = controller.onSourceChange(source)

    fun onSourceLabelChange(value: String) = controller.onSourceLabelChange(value)

    fun onPipelineStageChange(stage: PipelineStage) = controller.onPipelineStageChange(stage)

    fun onSave() = controller.onSave()

    fun reset() = controller.reset()
}
