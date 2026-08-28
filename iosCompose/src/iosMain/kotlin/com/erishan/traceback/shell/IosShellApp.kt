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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Traceback", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "iOS foundation shell — Room + key smoke",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "${opportunities.size} opportunities",
                    style = MaterialTheme.typography.titleMedium,
                )
                OpportunityList(opportunities)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (keyPresence.hasKey) {
                        "Key on device · last four ${keyPresence.lastFour}"
                    } else {
                        "No OpenAI key stored"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedTextField(
                    value = keyDraft,
                    onValueChange = { keyDraft = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("OpenAI API key") },
                    singleLine = true,
                )
                Button(
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
                ) {
                    Text("Save key")
                }
                if (keyPresence.hasKey) {
                    Button(
                        onClick = {
                            scope.launch {
                                container.secretStore.clearOpenAiKey()
                                status = "Key cleared"
                            }
                        },
                    ) {
                        Text("Clear key")
                    }
                }
                status?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun OpportunityList(opportunities: List<Opportunity>) {
    if (opportunities.isEmpty()) {
        Text("No leads yet — add some on Android or seed later.")
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(opportunities, key = { it.id }) { opportunity ->
            Text("• ${opportunity.title}")
        }
    }
}
