package com.erishan.traceback.shell

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.erishan.traceback.ai.domain.KeyPresence
import com.erishan.traceback.core.di.SharedContainer
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.ui.OpportunityFilter
import com.erishan.traceback.opportunity.ui.OpportunityListScreen
import com.erishan.traceback.opportunity.ui.OpportunityListUiState
import com.erishan.traceback.opportunity.ui.listUiState
import com.erishan.traceback.settings.domain.ThemeMode
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.PrimaryButton
import com.erishan.traceback.ui.components.TbBarIconButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.components.TextAction
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private enum class IosDestination { List, Me }

@Composable
fun IosShellApp(container: SharedContainer) {
    val mode by container.appearanceStore.observe()
        .collectAsState(initial = container.appearanceStore.current())
    val darkTheme = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    var destination by remember { mutableStateOf(IosDestination.List) }
    var filter by remember { mutableStateOf(OpportunityFilter.All) }
    val opportunities: List<Opportunity>? by container.opportunityRepository.observeAll()
        .map<List<Opportunity>, List<Opportunity>?> { it }
        .collectAsState(initial = null)

    TracebackTheme(darkTheme = darkTheme) {
        when (destination) {
            IosDestination.List -> OpportunityListScreen(
                uiState = opportunities?.let { listUiState(it, filter) } ?: OpportunityListUiState(),
                onAddClick = {},
                onFilterSelected = { filter = it },
                onOpenOpportunity = {},
                onOpenMe = { destination = IosDestination.Me },
            )
            IosDestination.Me -> IosKeyPane(
                container = container,
                onBack = { destination = IosDestination.List },
            )
        }
    }
}

@Composable
private fun IosKeyPane(container: SharedContainer, onBack: () -> Unit) {
    val keyPresence by container.secretStore.observe()
        .collectAsState(initial = KeyPresence(hasKey = false, lastFour = null))
    var keyDraft by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    TbScaffold(
        title = "Me",
        navigationIcon = {
            TbBarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(TracebackTheme.dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceS),
        ) {
            TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(TracebackTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceS),
                ) {
                    Text(
                        if (keyPresence.hasKey) {
                            "Key on device · last four ${keyPresence.lastFour}"
                        } else {
                            "No OpenAI key stored"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TracebackTheme.colors.textDim,
                    )
                    FieldLabel("OpenAI API key", spacer = false)
                    TbTextField(
                        value = keyDraft,
                        onValueChange = { keyDraft = it },
                        placeholder = "sk-…",
                    )
                    PrimaryButton(
                        text = "Save key",
                        onClick = {
                            scope.launch {
                                runCatching {
                                    container.secretStore.setOpenAiKey(keyDraft)
                                }.onSuccess {
                                    keyDraft = ""
                                    status = "Key saved"
                                }.onFailure {
                                    status = it.message ?: "Could not save key"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (keyPresence.hasKey) {
                        TextAction(
                            text = "Clear key",
                            color = TracebackTheme.colors.textDim,
                            onClick = {
                                scope.launch {
                                    container.secretStore.clearOpenAiKey()
                                    status = "Key cleared"
                                }
                            },
                        )
                    }
                    status?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = TracebackTheme.colors.textDim,
                        )
                    }
                }
            }
        }
    }
}
