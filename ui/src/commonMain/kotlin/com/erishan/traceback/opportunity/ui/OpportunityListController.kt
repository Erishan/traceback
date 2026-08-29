package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class OpportunityListController(
    scope: CoroutineScope,
    repository: OpportunityRepository,
) {
    private val filter = MutableStateFlow(OpportunityFilter.All)

    val uiState: StateFlow<OpportunityListUiState> =
        combine(repository.observeAll(), filter) { list, selected ->
            listUiState(all = list, filter = selected)
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OpportunityListUiState(),
        )

    fun onFilterSelected(selected: OpportunityFilter) {
        filter.value = selected
    }
}
