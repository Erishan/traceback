package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showCreate by remember { mutableStateOf(false) }
    OpportunityListScreen(
        uiState,
        onAddClick = { showCreate },
        onFilterSelected = viewModel::onFilterSelected,
        modifier
    )

    if(showCreate) {
        val editViewModel: OpportunityEditViewModel = viewModel(factory = OpportunityEditViewModel.Factory)
        val editState by editViewModel.uiState.collectAsStateWithLifecycle()

        if(editState.isSaved) {
            showCreate = false
        }

        OpportunityCreateDialog(
            editState,
            onTitleChange = editViewModel::onTitleChange,
            onDescriptionChange = editViewModel::onDescriptionChange,
            onSourceChange = editViewModel::onSourceChange,
            onSourceLabelChange = editViewModel::onSourceLabelChange,
            onSave = editViewModel::onSave,
            onDismiss = { showCreate = false }
        )
    }
}