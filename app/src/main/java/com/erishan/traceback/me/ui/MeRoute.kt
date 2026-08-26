package com.erishan.traceback.me.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MeRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeViewModel = viewModel(factory = MeViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MeScreen(
        uiState = uiState,
        onBack = onBack,
        onSaveProfile = viewModel::onSaveProfile,
        onSaveKey = viewModel::onSaveKey,
        onClearKey = viewModel::onClearKey,
        onThemeModeChange = viewModel::onThemeModeChange,
        modifier = modifier,
    )
}
