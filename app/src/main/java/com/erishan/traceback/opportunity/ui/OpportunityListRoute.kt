package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OpportunityListRoute(
    modifier: Modifier = Modifier,
    viewModel: OpportunityListViewModel = viewModel(
        factory = OpportunityListViewModel.Factory
    )
) {
    val uiState: OpportunityListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    OpportunityListScreen(uiState, modifier)
}