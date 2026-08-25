package com.erishan.traceback.opportunity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class OpportunityListViewModel(
    repository: OpportunityRepository
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityListViewModel(app.container.opportunityRepository)
            }
        }
    }

    private val _filter = MutableStateFlow(OpportunityFilter.All)
    val uiState: StateFlow<OpportunityListUiState> =
        combine(repository.observeAll(), _filter) { list, filter ->
            OpportunityListUiState(
                opportunities = list.filter { filter.matches(it.pipelineStage) },
                selectedFilter = filter,
                distribution = StageDistribution(list.groupingBy { it.pipelineStage }.eachCount()),
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OpportunityListUiState()
        )

    fun onFilterSelected(filter: OpportunityFilter) { _filter.value = filter }
}
