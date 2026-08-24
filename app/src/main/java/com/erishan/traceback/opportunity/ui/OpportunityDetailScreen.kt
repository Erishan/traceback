package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.PlayForWork
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.theme.TracebackTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

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
    onAddNote: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAppliedMessageChange: (String) -> Unit,
    deleteFailed: Boolean,
    onDeleteErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirm by remember { mutableStateOf(false) }
    val content = uiState as? OpportunityDetailUiState.Content

    val deleteFailedText = stringResource(R.string.opportunity_could_not_delete)
    LaunchedEffect(deleteFailed) {
        if (deleteFailed) {
            snackbarHostState.showSnackbar(deleteFailedText)
            onDeleteErrorDismiss()
        }
    }

    TbScaffold(
        modifier = modifier.fillMaxSize(),
        title = null,
        navigationIcon = {
            IconButton(
                onClick = onBack, colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                )
            }
        },
        actions = {
            if (content != null) {
                IconButton(
                    onClick = { showConfirm = true },
                    enabled = !content.isSaving,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = stringResource(R.string.cd_delete),
                    )
                }
            }
        },
        snackbarHostState =snackbarHostState,
    ) { innerPadding ->
        when (uiState) {
            OpportunityDetailUiState.Loading ->
                Box(Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) { LoadingState() }

            OpportunityDetailUiState.NotFound ->
                Box(Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                    EmptyState(
                        title = stringResource(R.string.detail_not_found_title),
                        message = stringResource(R.string.detail_not_found_message),
                    )
                }

            is OpportunityDetailUiState.Content ->
                DetailContent(
                    content = uiState,
                    contentPadding = innerPadding,
                    onStageChange = onStageChange,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onSourceChange = onSourceChange,
                    onSourceLabelChange = onSourceLabelChange,
                    onAddNote = onAddNote,
                    onDeleteNote = onDeleteNote,
                    onAppliedMessageChange = onAppliedMessageChange,
                )
        }
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
private fun DetailContent(
    content: OpportunityDetailUiState.Content,
    contentPadding: PaddingValues,
    onStageChange: (PipelineStage) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    onAddNote: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAppliedMessageChange: (String) -> Unit,
) {
    var sourceOpen by remember { mutableStateOf(false) }
    var stageOpen by remember { mutableStateOf(false) }
    val editEnabled = !content.isSaving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 18.dp),
    ) {
        if (content.saveFailed) {
            Spacer(Modifier.height(12.dp))
            ErrorBanner(text = stringResource(R.string.opportunity_could_not_save))
        }

        InlineTitle(value = content.title, onCommit = onTitleChange, enabled = editEnabled)
        Spacer(Modifier.height(20.dp))

        StagePipeline(
            stage = content.pipelineStage,
            pickerOpen = stageOpen,
            onOpenPicker = {
                stageOpen = !stageOpen
                sourceOpen = false
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = editEnabled,
        )

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
                        enabled = editEnabled,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        EditableCard(
            label = stringResource(R.string.field_description),
            value = content.description,
            placeholder = stringResource(R.string.detail_description_hint),
            emptyText = stringResource(R.string.detail_description_empty),
            onCommit = onDescriptionChange,
            enabled = editEnabled,
        )
        Spacer(Modifier.height(12.dp))

        EditableCard(
            label = stringResource(R.string.field_applied_message),
            value = content.appliedMessage,
            placeholder = stringResource(R.string.applied_message_hint),
            emptyText = stringResource(R.string.applied_message_empty),
            onCommit = onAppliedMessageChange,
            enabled = editEnabled,
        )
        Spacer(Modifier.height(12.dp))

        NotesSection(
            notes = content.notes,
            onAdd = onAddNote,
            onDelete = onDeleteNote,
            enabled = editEnabled,
        )
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
                enabled = editEnabled,
            )
            Spacer(Modifier.width(12.dp))
            Text(text = formatNoteTimestamp(content.createdAt), style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp), color = TracebackTheme.colors.textFaint)
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
                            enabled = editEnabled,
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
                            enabled = editEnabled,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InlineTitle(
    value: String,
    onCommit: (String) -> Unit,
    enabled: Boolean,
) {
    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }

    if (editing) {
        Column {
            TbTextField(
                value = buffer,
                onValueChange = { buffer = it },
                placeholder = stringResource(R.string.detail_title_hint),
                imeAction = ImeAction.Done,
                enabled = enabled,
            )
            EditActions(
                onCancel = { editing = false },
                onConfirm = {
                    if (buffer.isNotBlank()) onCommit(buffer)
                    editing = false
                },
                enabled = enabled,
            )
        }
    } else {
        val modifier = if (enabled) {
            Modifier
                .fillMaxWidth()
                .clickable {
                    buffer = value
                    editing = true
                }
        } else {
            Modifier.fillMaxWidth()
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier,
        )
    }
}

@Composable
private fun SourcePill(
    source: OpportunitySource,
    sourceLabel: String?,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(color = Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayForWork,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colorScheme.primary,
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
    enabled: Boolean,
) {
    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }

    val base = Modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.surface)
    val container = if (editing || !enabled) base else base.clickable {
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
                enabled = enabled,
            )
            EditActions(
                onCancel = { editing = false },
                onConfirm = {
                    onCommit(buffer)
                    editing = false
                },
                enabled = enabled,
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
private fun NotesSection(
    notes: List<Note>,
    onAdd: (String) -> Unit,
    onDelete: (String) -> Unit,
    enabled: Boolean,
) {
    var composing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CardLabel(stringResource(R.string.field_notes))
            AddNoteToggle(
                expanded = composing,
                onClick = { composing = !composing },
                enabled = enabled,
            )
        }

        AnimatedVisibility(visible = composing) {
            Column {
                Spacer(Modifier.height(14.dp))
                NoteComposer(
                    onSubmit = { text ->
                        onAdd(text)
                        composing = false
                    },
                    enabled = enabled,
                )
            }
        }

        if (notes.isEmpty()) {
            if (!composing) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.notes_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TracebackTheme.colors.textFaint,
                )
            }
        } else {
            Spacer(Modifier.height(14.dp))
            // Newest first
            notes.sortedByDescending { it.createdAt }.forEachIndexed { index, note ->
                if (index > 0) Spacer(Modifier.height(12.dp))
                NoteRow(note = note, onDelete = { onDelete(note.id) }, enabled = enabled)
            }
        }
    }
}

@Composable
private fun AddNoteToggle(expanded: Boolean, onClick: () -> Unit, enabled: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "addNoteRotation",
    )
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(TracebackTheme.colors.accentDim)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(
                if (expanded) R.string.cd_cancel else R.string.notes_add
            ),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(16.dp)
                .rotate(rotation),
        )
    }
}

@Composable
private fun NoteRow(note: Note, onDelete: () -> Unit, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatNoteTimestamp(note.createdAt),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.sp,
                ),
                color = TracebackTheme.colors.textFaint,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.cd_delete),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(16.dp)
                .clickable(enabled = enabled, onClick = onDelete),
        )
    }
}

@Composable
private fun NoteComposer(onSubmit: (String) -> Unit, enabled: Boolean) {
    var buffer by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            TbTextField(
                value = buffer,
                onValueChange = { buffer = it },
                placeholder = stringResource(R.string.notes_hint),
                modifier = Modifier.focusRequester(focusRequester),
                singleLine = false,
                minLines = 1,
                maxLines = 4,
                enabled = enabled,
            )
        }
        IconButton(
            onClick = {
                val text = buffer.trim()
                if (text.isNotBlank()) onSubmit(text)
            },
            enabled = enabled,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.cd_confirm),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private val noteDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.ENGLISH)

private fun formatNoteTimestamp(instant: Instant): String {
    val platformInstant = java.time.Instant.ofEpochMilli(instant.toEpochMilliseconds())
    val local = java.time.LocalDateTime.ofInstant(platformInstant, ZoneId.systemDefault())
    return noteDateFormatter.format(local)
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
private fun EditActions(onCancel: () -> Unit, onConfirm: () -> Unit, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onCancel, enabled = enabled) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_cancel),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onConfirm, enabled = enabled) {
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
        OpportunityDetailScreen(
            uiState = OpportunityDetailUiState.Content(
                title = "SaaS onboarding flow redesign",
                description = "Rework the multi-step signup, reduce mobile drop-off across the trial funnel.",
                source = OpportunitySource.UPWORK,
                sourceLabel = null,
                pipelineStage = PipelineStage.APPLIED,
                createdAt = Instant.fromEpochMilliseconds(1_723_600_000_000L),
                appliedMessage = null,
                notes = listOf(
                    Note(
                        id = "n1",
                        createdAt = Instant.fromEpochMilliseconds(1_723_600_000_000L),
                        text = "Client wants a Loom walkthrough before the call.",
                    ),
                    Note(
                        id = "n2",
                        createdAt = Instant.fromEpochMilliseconds(1_723_700_000_000L),
                        text = "Followed up Monday, no reply yet.",
                    ),
                ),
            ),
            onBack = {},
            onDelete = {},
            onStageChange = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onSourceChange = {},
            onSourceLabelChange = {},
            onAddNote = {},
            onDeleteNote = {},
            onAppliedMessageChange = {},
            deleteFailed = false,
            onDeleteErrorDismiss = {},
        )
    }
}
