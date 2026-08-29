package com.erishan.traceback.shell

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.erishan.traceback.core.di.SharedContainer
import com.erishan.traceback.me.ui.MeController
import com.erishan.traceback.me.ui.MeScreen
import com.erishan.traceback.opportunity.ui.DetailEvent
import com.erishan.traceback.opportunity.ui.OpportunityCreateController
import com.erishan.traceback.opportunity.ui.OpportunityCreateDialog
import com.erishan.traceback.opportunity.ui.OpportunityDetailController
import com.erishan.traceback.opportunity.ui.OpportunityDetailScreen
import com.erishan.traceback.opportunity.ui.OpportunityListController
import com.erishan.traceback.opportunity.ui.OpportunityListScreen
import com.erishan.traceback.settings.domain.ThemeMode
import com.erishan.traceback.ui.theme.TracebackTheme

private sealed interface IosDestination {
    data object List : IosDestination
    data class Detail(val id: String) : IosDestination
    data object Me : IosDestination
}

@Composable
fun IosShellApp(container: SharedContainer) {
    val mode by container.appearanceStore.observe()
        .collectAsState(initial = container.appearanceStore.current())
    val darkTheme = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val scope = rememberCoroutineScope()
    val backStack = remember { mutableStateListOf<IosDestination>(IosDestination.List) }
    var showCreate by remember { mutableStateOf(false) }

    val listController = remember(container) {
        OpportunityListController(scope, container.opportunityRepository)
    }
    val createController = remember(container) {
        OpportunityCreateController(scope, container.opportunityRepository)
    }
    val meController = remember(container) {
        MeController(
            scope = scope,
            userContextRepository = container.userContextRepository,
            secretStore = container.secretStore,
            appearanceStore = container.appearanceStore,
        )
    }

    val listState by listController.uiState.collectAsState()
    val createState by createController.uiState.collectAsState()
    val meState by meController.uiState.collectAsState()

    LaunchedEffect(createState.isSaved) {
        if (createState.isSaved) {
            showCreate = false
            createController.reset()
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    TracebackTheme(darkTheme = darkTheme) {
        when (val destination = backStack.last()) {
            IosDestination.List -> {
                OpportunityListScreen(
                    uiState = listState,
                    onAddClick = { showCreate = true },
                    onFilterSelected = listController::onFilterSelected,
                    onOpenOpportunity = { id -> backStack.add(IosDestination.Detail(id)) },
                    onOpenMe = { backStack.add(IosDestination.Me) },
                )
            }

            is IosDestination.Detail -> {
                val detailController = remember(destination.id, container) {
                    OpportunityDetailController(
                        scope = scope,
                        id = destination.id,
                        repository = container.opportunityRepository,
                        userContextRepository = container.userContextRepository,
                        secretStore = container.secretStore,
                        briefJobUseCase = container.briefJobUseCase,
                    )
                }
                val detailState by detailController.uiState.collectAsState()
                var deleteFailed by remember(destination.id) { mutableStateOf(false) }

                LaunchedEffect(destination.id) {
                    detailController.events.collect { event ->
                        when (event) {
                            DetailEvent.Deleted -> navigateBack()
                            DetailEvent.DeleteFailed -> deleteFailed = true
                        }
                    }
                }

                OpportunityDetailScreen(
                    uiState = detailState,
                    onBack = ::navigateBack,
                    onDelete = detailController::delete,
                    onStageChange = detailController::onStageChange,
                    onTitleChange = detailController::onTitleChange,
                    onDescriptionChange = detailController::onDescriptionChange,
                    onSourceChange = detailController::onSourceChange,
                    onSourceLabelChange = detailController::onSourceLabelChange,
                    onAddNote = detailController::onAddNote,
                    onDeleteNote = detailController::onDeleteNote,
                    onAppliedMessageChange = detailController::onAppliedMessageChange,
                    onBrief = detailController::onBrief,
                    onOpenMe = { backStack.add(IosDestination.Me) },
                    deleteFailed = deleteFailed,
                    onDeleteErrorDismiss = { deleteFailed = false },
                )
            }

            IosDestination.Me -> {
                MeScreen(
                    uiState = meState,
                    onBack = ::navigateBack,
                    onSaveProfile = meController::onSaveProfile,
                    onSaveKey = meController::onSaveKey,
                    onClearKey = meController::onClearKey,
                    onThemeModeChange = meController::onThemeModeChange,
                )
            }
        }

        if (showCreate) {
            OpportunityCreateDialog(
                uiState = createState,
                onTitleChange = createController::onTitleChange,
                onDescriptionChange = createController::onDescriptionChange,
                onSourceChange = createController::onSourceChange,
                onSourceLabelChange = createController::onSourceLabelChange,
                onStageChange = createController::onPipelineStageChange,
                onSave = createController::onSave,
                onDismiss = { showCreate = false },
            )
        }
    }
}
