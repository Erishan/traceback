package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun OpportunityDetailScreen(
    uiState: OpportunityDetailUiState,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onStageChange: (PipelineStage) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAppliedMessageChange: (String) -> Unit,
    deleteFailed: Boolean,
    onDeleteErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        OpportunityDetailUiState.Loading -> LoadingState()
        OpportunityDetailUiState.NotFound -> Column(modifier.fillMaxSize()) {
            DetailTopBar(onBack = onBack, onDeleteClick = null)
            EmptyState(
                title = stringResource(R.string.detail_not_found_title),
                message = stringResource(R.string.detail_not_found_message),
            )
        }

        is OpportunityDetailUiState.Content -> DetailContent(
            content = uiState,
            onBack = onBack,
            onDelete = onDelete,
            onStageChange = onStageChange,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onSourceChange = onSourceChange,
            onSourceLabelChange = onSourceLabelChange,
            onNotesChange = onNotesChange,
            onAppliedMessageChange = onAppliedMessageChange,
            deleteFailed = deleteFailed,
            onDeleteErrorDismiss = onDeleteErrorDismiss,
            modifier = modifier,
        )
    }
}

@Composable
private fun DetailContent(
    content: OpportunityDetailUiState.Content,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onStageChange: (PipelineStage) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAppliedMessageChange: (String) -> Unit,
    deleteFailed: Boolean,
    onDeleteErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showConfirm by remember { mutableStateOf(false) }
    var sourceOpen by remember { mutableStateOf(false) }
    var stageOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 18.dp),
    ) {
        DetailTopBar(onBack = onBack, onDeleteClick = { showConfirm = true })

        if (deleteFailed) {
            ErrorBanner(
                text = stringResource(R.string.opportunity_could_not_delete),
                onDismiss = onDeleteErrorDismiss,
            )
            Spacer(Modifier.height(12.dp))
        }
        if (content.saveFailed) {
            ErrorBanner(text = stringResource(R.string.opportunity_could_not_save))
            Spacer(Modifier.height(12.dp))
        }

        InlineTitle(value = content.title, onCommit = onTitleChange)
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SourcePill(
                source = content.source,
                sourceLabel = content.sourceLabel,
                onClick = {
                    sourceOpen = !sourceOpen
                    stageOpen = false
                },
            )
            StageTrigger(
                stage = content.pipelineStage,
                open = stageOpen,
                onClick = {
                    stageOpen = !stageOpen
                    sourceOpen = false
                },
            )
        }

        AnimatedVisibility(visible = sourceOpen) {
            Column {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OpportunitySource.entries.forEach { s ->
                        ChoiceChip(
                            label = stringResource(sourceLabelRes(s)),
                            selected = content.source == s,
                            selectedBg = TracebackTheme.colors.accentDim,
                            selectedFg = MaterialTheme.colorScheme.primary,
                            onClick = { onSourceChange(s) },
                        )
                    }
                }
                AnimatedVisibility(visible = content.source == OpportunitySource.OTHER) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        FieldLabel(stringResource(R.string.field_source_label))
                        TbTextField(
                            value = content.sourceLabel.orEmpty(),
                            onValueChange = onSourceLabelChange,
                            placeholder = stringResource(R.string.create_source_label_hint),
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = stageOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PipelineStage.entries.forEach { st ->
                    StageChip(
                        stage = st,
                        selected = content.pipelineStage == st,
                        onClick = {
                            onStageChange(st)
                            stageOpen = false
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        EditableCard(
            label = stringResource(R.string.field_description),
            value = content.description,
            placeholder = stringResource(R.string.detail_description_hint),
            emptyText = stringResource(R.string.detail_description_empty),
            onCommit = onDescriptionChange,
        )
        Spacer(Modifier.height(12.dp))

        EditableCard(
            label = stringResource(R.string.field_applied_message),
            value = content.appliedMessage,
            placeholder = stringResource(R.string.applied_message_hint),
            emptyText = stringResource(R.string.applied_message_empty),
            onCommit = onAppliedMessageChange,
        )
        Spacer(Modifier.height(12.dp))

        EditableCard(
            label = stringResource(R.string.field_notes),
            value = content.notes,
            placeholder = stringResource(R.string.notes_hint),
            emptyText = stringResource(R.string.notes_empty),
            onCommit = onNotesChange,
        )
        Spacer(Modifier.height(24.dp))
    }

    if (showConfirm) {
        DeleteConfirmDialog(
            onConfirm = {
                showConfirm = false
                onDelete()
            },
            onDismiss = { showConfirm = false },
        )
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit, onDeleteClick: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (onDeleteClick != null) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InlineTitle(value: String, onCommit: (String) -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }

    if (editing) {
        Column {
            TbTextField(
                value = buffer,
                onValueChange = { buffer = it },
                placeholder = stringResource(R.string.detail_title_hint),
                imeAction = ImeAction.Done,
            )
            EditActions(
                onCancel = { editing = false },
                onConfirm = {
                    if (buffer.isNotBlank()) onCommit(buffer)
                    editing = false
                },
            )
        }
    } else {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    buffer = value
                    editing = true
                },
        )
    }
}

@Composable
private fun SourcePill(
    source: OpportunitySource,
    sourceLabel: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = sourceLabel?.takeIf { source == OpportunitySource.OTHER && it.isNotBlank() }
                ?: stringResource(sourceLabelRes(source)),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EditableCard(
    label: String,
    value: String?,
    placeholder: String,
    emptyText: String,
    onCommit: (String) -> Unit,
) {
    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }

    val base = Modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.surface)
    val container = if (editing) base else base.clickable {
        buffer = value.orEmpty()
        editing = true
    }

    Column(modifier = container.padding(horizontal = 16.dp, vertical = 14.dp)) {
        CardLabel(label)
        Spacer(Modifier.height(9.dp))
        if (editing) {
            TbTextField(
                value = buffer,
                onValueChange = { buffer = it },
                placeholder = placeholder,
                singleLine = false,
                minLines = 2,
                maxLines = 8,
            )
            EditActions(
                onCancel = { editing = false },
                onConfirm = {
                    onCommit(buffer)
                    editing = false
                },
            )
        } else {
            Text(
                text = value ?: emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (value == null) TracebackTheme.colors.textFaint
                else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun CardLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun EditActions(onCancel: () -> Unit, onConfirm: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_cancel),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onConfirm) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.cd_confirm),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ErrorBanner(text: String, onDismiss: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
        )
        if (onDismiss != null) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_cancel),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onDismiss),
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.delete_confirm_title)) },
        text = { Text(stringResource(R.string.delete_confirm_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.action_delete),
                    color = TracebackTheme.colors.stageLost,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D, widthDp = 360)
@Composable
private fun OpportunityDetailScreenPreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            OpportunityDetailScreen(
                uiState = OpportunityDetailUiState.Content(
                    title = "SaaS onboarding flow redesign",
                    description = "Rework the multi-step signup, reduce mobile drop-off across the trial funnel.",
                    source = OpportunitySource.UPWORK,
                    sourceLabel = null,
                    pipelineStage = PipelineStage.APPLIED,
                    appliedMessage = null,
                    notes = "Client wants a Loom walkthrough before the call. Follow up Monday if no reply.",
                ),
                onBack = {},
                onDelete = {},
                onStageChange = {},
                onTitleChange = {},
                onDescriptionChange = {},
                onSourceChange = {},
                onSourceLabelChange = {},
                onNotesChange = {},
                onAppliedMessageChange = {},
                deleteFailed = false,
                onDeleteErrorDismiss = {},
            )
        }
    }
}
