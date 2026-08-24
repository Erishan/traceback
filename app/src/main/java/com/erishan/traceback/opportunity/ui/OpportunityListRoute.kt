package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
        onAddClick = { showCreate = true },
        onFilterSelected = viewModel::onFilterSelected,
        modifier
    )

    if (showCreate) {
        val createViewModel: OpportunityCreateViewModel =
            viewModel(factory = OpportunityCreateViewModel.Factory)
        val createState by createViewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(createState.isSaved) {
            if (createState.isSaved) {
                showCreate = false
                createViewModel.reset()
            }
        }

        OpportunityCreateDialog(
            createState,
            onTitleChange = createViewModel::onTitleChange,
            onDescriptionChange = createViewModel::onDescriptionChange,
            onSourceChange = createViewModel::onSourceChange,
            onSourceLabelChange = createViewModel::onSourceLabelChange,
            onStageChange = createViewModel::onPipelineStageChange,
            onSave = createViewModel::onSave,
            onDismiss = {
                createViewModel.reset()
                showCreate = false
            }
        )
    }
}
