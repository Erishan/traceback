package com.erishan.traceback.opportunity.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
import com.erishan.traceback.ai.domain.BriefJobUseCase
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import com.erishan.traceback.opportunity.ui.DetailEvent
import com.erishan.traceback.opportunity.ui.OpportunityDetailController
import com.erishan.traceback.opportunity.ui.OpportunityDetailUiState
import kotlinx.coroutines.flow.StateFlow

class OpportunityDetailViewModel(
    id: String,
    repository: OpportunityRepository,
    userContextRepository: UserContextRepository,
    secretStore: SecretStore,
    briefJobUseCase: BriefJobUseCase,
) : ViewModel() {
    companion object {
        fun provideFactory(id: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityDetailViewModel(
                    id = id,
                    repository = app.container.opportunityRepository,
                    userContextRepository = app.container.userContextRepository,
                    secretStore = app.container.secretStore,
                    briefJobUseCase = app.container.briefJobUseCase,
                )
            }
        }
    }

    private val controller = OpportunityDetailController(
        scope = viewModelScope,
        id = id,
        repository = repository,
        userContextRepository = userContextRepository,
        secretStore = secretStore,
        briefJobUseCase = briefJobUseCase,
    )

    val events = controller.events

    val uiState: StateFlow<OpportunityDetailUiState> = controller.uiState

    fun onStageChange(stage: PipelineStage) = controller.onStageChange(stage)

    fun onTitleChange(title: String) = controller.onTitleChange(title)

    fun onDescriptionChange(description: String) = controller.onDescriptionChange(description)

    fun onSourceChange(source: OpportunitySource) = controller.onSourceChange(source)

    fun onSourceLabelChange(label: String) = controller.onSourceLabelChange(label)

    fun onAddNote(text: String) = controller.onAddNote(text)

    fun onDeleteNote(noteId: String) = controller.onDeleteNote(noteId)

    fun onAppliedMessageChange(message: String) = controller.onAppliedMessageChange(message)

    fun onBrief() = controller.onBrief()

    fun delete() = controller.delete()
}
