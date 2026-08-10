package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import com.erishan.traceback.ui.theme.FabShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.theme.TracebackTheme


@Composable
fun OpportunityListScreen(
    uiState: OpportunityListUiState,
    onAddClick: () -> Unit,
    onFilterSelected: (OpportunityFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ListHeader(total = uiState.total)
            FilterRow(selected = uiState.selectedFilter, onSelect = onFilterSelected)

            when {
                uiState.isLoading -> LoadingState()
                uiState.opportunities.isEmpty() -> EmptyState(
                    title = stringResource(R.string.empty_opportunities_list_title),
                    message = stringResource(R.string.empty_opportunities_list_message)
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    items(items = uiState.opportunities, key = { it.id }) { OpportunityCard(it) }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = FabShape,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_opportunity))
        }
    }
}

@Composable
private fun ListHeader(total: Int) {
    Column(Modifier.padding(start = 22.dp, end = 22.dp, top = 14.dp, bottom = 12.dp)) {
        Text(
            text = stringResource(R.string.pipeline_overline).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.opportunities_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.opportunities_count, total),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FilterRow(selected: OpportunityFilter, onSelect: (OpportunityFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OpportunityFilter.entries.forEach { f ->
            val on = f == selected
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (on) TracebackTheme.colors.accentDim else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onSelect(f) }
                    .padding(horizontal = 13.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(f.labelRes),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                    color = if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OpportunityCard(o: Opportunity) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = o.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                StagePill(o.pipelineStage)
            }

            if (o.description != null) {
                Spacer(Modifier.height(7.dp))
                Text(
                    text = o.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))
            SourceChip(o)
        }
    }
}

@Composable
private fun StagePill(stage: PipelineStage) {
    val c = stageColor(stage)
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(c.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(stageLabelRes(stage)),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.2.sp),
            color = c
        )
    }
}

@Composable
private fun SourceChip(o: Opportunity) {
    val text = o.sourceLabel?.takeIf { o.source == OpportunitySource.OTHER }
        ?: stringResource(sourceLabelRes(o.source))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(TracebackTheme.colors.sourceChipBg)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Box(Modifier.size(5.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        Spacer(Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D)
@Composable
private fun OpportunityListScreenPreview() {
    TracebackTheme {
        OpportunityListScreen(
            uiState = OpportunityListUiState(
                opportunities = listOf(
                    Opportunity(
                        id = "1",
                        title = "SaaS onboarding flow redesign",
                        description = "Rework the multi-step signup, reduce mobile drop-off.",
                        source = OpportunitySource.UPWORK,
                        sourceLabel = null,
                        pipelineStage = PipelineStage.APPLIED,
                        notes = null,
                        appliedMessage = null,
                    ),
                    Opportunity(
                        id = "2",
                        title = "Fintech dashboard, Compose migration",
                        description = "Move a legacy XML dashboard to Jetpack Compose.",
                        source = OpportunitySource.LINKEDIN,
                        sourceLabel = null,
                        pipelineStage = PipelineStage.INTERVIEW,
                        notes = null,
                        appliedMessage = null,
                    ),
                    Opportunity(
                        id = "3",
                        title = "Crash triage for release build",
                        description = "R8-only NPE on startup. Needs mapping-file analysis.",
                        source = OpportunitySource.REFERRAL,
                        sourceLabel = null,
                        pipelineStage = PipelineStage.IN_CONVERSATION,
                        notes = null,
                        appliedMessage = null,
                    ),
                ),
                isLoading = false,
            ),
            onAddClick = {},
            onFilterSelected = {},
        )
    }
}