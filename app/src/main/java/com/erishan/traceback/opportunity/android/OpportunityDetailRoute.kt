package com.erishan.traceback.opportunity.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erishan.traceback.opportunity.ui.DetailEvent
import com.erishan.traceback.opportunity.ui.OpportunityDetailScreen

@Composable
fun OpportunityDetailRoute(
    id: String,
    onBack: () -> Unit,
    onOpenMe: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OpportunityDetailViewModel = viewModel(
        factory = OpportunityDetailViewModel.provideFactory(id)
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DetailEvent.Deleted -> onBack()
                DetailEvent.DeleteFailed -> deleteFailed = true
            }
        }
    }

    OpportunityDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onDelete = viewModel::delete,
        onStageChange = viewModel::onStageChange,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSourceChange = viewModel::onSourceChange,
        onSourceLabelChange = viewModel::onSourceLabelChange,
        onAddNote = viewModel::onAddNote,
        onDeleteNote = viewModel::onDeleteNote,
        onAppliedMessageChange = viewModel::onAppliedMessageChange,
        onBrief = viewModel::onBrief,
        onOpenMe = onOpenMe,
        deleteFailed = deleteFailed,
        onDeleteErrorDismiss = { deleteFailed = false },
        modifier = modifier,
    )
}
