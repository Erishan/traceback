package com.erishan.traceback.opportunity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.flow.StateFlow

class OpportunityListViewModel(
    repository: OpportunityRepository,
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityListViewModel(app.container.opportunityRepository)
            }
        }
    }

    private val controller = OpportunityListController(
        scope = viewModelScope,
        repository = repository,
    )

    val uiState: StateFlow<OpportunityListUiState> = controller.uiState

    fun onFilterSelected(filter: OpportunityFilter) = controller.onFilterSelected(filter)
}
