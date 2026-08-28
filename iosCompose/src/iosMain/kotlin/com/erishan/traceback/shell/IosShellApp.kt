package com.erishan.traceback.shell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ai.domain.KeyPresence
import com.erishan.traceback.core.di.SharedContainer
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.PrimaryButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.components.TextAction
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlinx.coroutines.launch

@Composable
fun IosShellApp(container: SharedContainer) {
    val opportunities by container.opportunityRepository.observeAll()
        .collectAsState(initial = emptyList())
    val keyPresence by container.secretStore.observe()
        .collectAsState(initial = KeyPresence(hasKey = false, lastFour = null))
    var keyDraft by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    TracebackTheme {
        TbScaffold(title = "Traceback") { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(TracebackTheme.dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceS),
            ) {
                Text(
                    "iOS foundation shell — shared aurora UI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TracebackTheme.colors.textDim,
                )
                Text(
                    "${opportunities.size} opportunities",
                    style = MaterialTheme.typography.titleMedium,
                )
                OpportunityList(opportunities)
                Spacer(Modifier.height(TracebackTheme.dimens.spaceXs))
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
}

@Composable
private fun OpportunityList(opportunities: List<Opportunity>) {
    if (opportunities.isEmpty()) {
        EmptyState(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            title = "No leads yet",
            message = "Add some on Android or seed later.",
        )
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXxs),
    ) {
        items(opportunities, key = { it.id }) { opportunity ->
            TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = opportunity.title,
                    modifier = Modifier.padding(TracebackTheme.dimens.spaceS),
                    style = MaterialTheme.typography.titleSmall,
                    color = TracebackTheme.colors.textHigh,
                )
            }
        }
    }
}
