package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.ErrorBanner
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.TbBarIconButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.Res
import com.erishan.traceback.ui.theme.*
import com.erishan.traceback.ui.theme.TracebackTheme
import org.jetbrains.compose.resources.stringResource

internal val InlineGlyph = 20.dp
private val ScrollBottomInset = 40.dp

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
    onBrief: () -> Unit,
    onOpenMe: () -> Unit,
    deleteFailed: Boolean,
    onDeleteErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TracebackTheme.colors
    val motion = TracebackTheme.motion
    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirm by remember { mutableStateOf(false) }
    val content = uiState as? OpportunityDetailUiState.Content

    val deleteFailedText = stringResource(Res.string.opportunity_could_not_delete)
    LaunchedEffect(deleteFailed) {
        if (deleteFailed) {
            snackbarHostState.showSnackbar(deleteFailedText)
            onDeleteErrorDismiss()
        }
    }

    val tintTarget = if (content != null) stageColor(content.pipelineStage) else colors.auroraWarm
    val auroraTint by animateColorAsState(
        targetValue = tintTarget,
        animationSpec = tween(motion.slow, easing = motion.standardEasing),
        label = "auroraTint",
    )

    TbScaffold(
        modifier = modifier,
        title = stringResource(Res.string.detail_opportunity),
        auroraTint = auroraTint,
        navigationIcon = {
            TbBarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.cd_back),
                onClick = onBack,
            )
        },
        actions = {
            if (content != null) {
                TbBarIconButton(
                    icon = Icons.Outlined.DeleteOutline,
                    contentDescription = stringResource(Res.string.cd_delete),
                    onClick = { showConfirm = true },
                    enabled = !content.isBusy,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        when (uiState) {
            OpportunityDetailUiState.Loading ->
                Box(Modifier.fillMaxSize().padding(innerPadding)) { LoadingState() }

            OpportunityDetailUiState.NotFound ->
                Box(Modifier.fillMaxSize().padding(innerPadding)) {
                    EmptyState(
                        title = stringResource(Res.string.detail_not_found_title),
                        message = stringResource(Res.string.detail_not_found_message),
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
                    onBrief = onBrief,
                    onOpenMe = onOpenMe,
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
    onBrief: () -> Unit,
    onOpenMe: () -> Unit,
) {
    val dimens = TracebackTheme.dimens
    var sourceOpen by remember { mutableStateOf(false) }
    var stageOpen by remember { mutableStateOf(false) }
    val editEnabled = !content.isBusy

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = dimens.screenPadding),
    ) {
        if (content.saveFailed) {
            ErrorBanner(text = stringResource(Res.string.opportunity_could_not_save))
            Spacer(Modifier.height(dimens.spaceS))
        }

        InlineTitle(
            value = content.title,
            onCommit = onTitleChange,
            enabled = editEnabled,
            saveFailed = content.saveFailed,
        )
        Spacer(Modifier.height(dimens.spaceL))

        StagePipeline(stage = content.pipelineStage)
        StageTrigger(
            stage = content.pipelineStage,
            open = stageOpen,
            onClick = {
                stageOpen = !stageOpen
                sourceOpen = false
            },
            enabled = editEnabled,
        )
        AnimatedVisibility(visible = stageOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.spaceXxs, bottom = dimens.spaceXs)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
            ) {
                PipelineStage.entries.forEach { stage ->
                    StageChip(
                        stage = stage,
                        selected = content.pipelineStage == stage,
                        onClick = {
                            onStageChange(stage)
                            stageOpen = false
                        },
                        enabled = editEnabled,
                    )
                }
            }
        }

        Spacer(Modifier.height(dimens.spaceM))

        EditableCard(
            label = stringResource(Res.string.field_description),
            value = content.description,
            placeholder = stringResource(Res.string.detail_description_hint),
            emptyText = stringResource(Res.string.detail_description_empty),
            onCommit = onDescriptionChange,
            enabled = editEnabled,
            saveFailed = content.saveFailed,
        )
        Spacer(Modifier.height(dimens.spaceS))

        BriefSection(
            content = content,
            onBrief = onBrief,
            onUseProposalAsAppliedMessage = onAppliedMessageChange,
            onOpenMe = onOpenMe,
            enabled = editEnabled,
        )
        Spacer(Modifier.height(dimens.spaceS))

        EditableCard(
            label = stringResource(Res.string.field_applied_message),
            value = content.appliedMessage,
            placeholder = stringResource(Res.string.applied_message_hint),
            emptyText = stringResource(Res.string.applied_message_empty),
            onCommit = onAppliedMessageChange,
            enabled = editEnabled,
            saveFailed = content.saveFailed,
        )
        Spacer(Modifier.height(dimens.spaceS))

        NotesSection(
            notes = content.notes,
            onAdd = onAddNote,
            onDelete = onDeleteNote,
            enabled = editEnabled,
        )

        Spacer(Modifier.height(dimens.spaceXl))
        MetaFooter(
            source = content.source,
            sourceLabel = content.sourceLabel,
            createdAt = content.createdAt,
            onSourceClick = {
                sourceOpen = !sourceOpen
                stageOpen = false
            },
            enabled = editEnabled,
        )
        AnimatedVisibility(visible = sourceOpen) {
            SourcePicker(
                source = content.source,
                sourceLabel = content.sourceLabel,
                onSourceChange = onSourceChange,
                onSourceLabelChange = onSourceLabelChange,
                enabled = editEnabled,
            )
        }
        Spacer(Modifier.height(ScrollBottomInset))
    }
}

@Composable
private fun InlineTitle(
    value: String,
    onCommit: (String) -> Unit,
    enabled: Boolean,
    saveFailed: Boolean,
) {
    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }
    val shown = rememberSaveEcho(upstream = value, saveFailed = saveFailed)

    if (editing) {
        Column {
            TbTextField(
                value = buffer,
                onValueChange = { buffer = it },
                placeholder = stringResource(Res.string.detail_title_hint),
                imeAction = ImeAction.Done,
                enabled = enabled,
            )
            EditActions(
                onCancel = { editing = false },
                onConfirm = {
                    if (buffer.isNotBlank()) {
                        shown.commit(buffer)
                        onCommit(buffer)
                    }
                    editing = false
                },
                enabled = enabled,
            )
        }
    } else {
        val editLabel = stringResource(Res.string.cd_edit)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = MinTouchTarget)
                .then(
                    if (enabled) {
                        Modifier.clickable(role = Role.Button, onClickLabel = editLabel) {
                            buffer = shown.value
                            editing = true
                        }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = shown.value,
                style = MaterialTheme.typography.titleMedium,
                color = TracebackTheme.colors.textHigh,
            )
        }
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
    saveFailed: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val shape = MaterialTheme.shapes.medium

    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }
    val shown = rememberSaveEcho(upstream = value.orEmpty(), saveFailed = saveFailed)
    val displayed = shown.value.ifEmpty { null }
    val tappable = enabled && !editing
    val editLabel = stringResource(Res.string.cd_edit)

    TbGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (tappable) {
                    Modifier.clickable(role = Role.Button, onClickLabel = editLabel) {
                        buffer = shown.value
                        editing = true
                    }
                } else {
                    Modifier
                }
            ),
        shape = shape,
    ) {
        Column(Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS)) {
            FieldLabel(label)
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
                        shown.commit(buffer)
                        onCommit(buffer)
                        editing = false
                    },
                    enabled = enabled,
                )
            } else {
                Text(
                    text = displayed ?: emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (displayed == null) colors.textFaint else colors.textHigh,
                )
            }
        }
    }
}

@Composable
private fun rememberSaveEcho(upstream: String, saveFailed: Boolean): SaveEcho {
    var pending by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(upstream, pending) {
        if (pending != null && upstream == pending) pending = null
    }
    LaunchedEffect(saveFailed) {
        if (saveFailed) pending = null
    }
    return SaveEcho(
        value = pending ?: upstream,
        commit = { pending = it },
    )
}

private data class SaveEcho(
    val value: String,
    val commit: (String) -> Unit,
)

@Composable
private fun EditActions(onCancel: () -> Unit, onConfirm: () -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onCancel,
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.cd_cancel),
                tint = colors.textDim,
                modifier = Modifier.size(InlineGlyph),
            )
        }
        IconButton(
            onClick = onConfirm,
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.cd_confirm),
                tint = colors.accent,
                modifier = Modifier.size(InlineGlyph),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    BasicAlertDialog(onDismissRequest = onDismiss) {
        TbGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            strong = true,
        ) {
            Column(Modifier.padding(dimens.spaceL)) {
                Text(
                    text = stringResource(Res.string.delete_confirm_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textHigh,
                )
                Spacer(Modifier.height(dimens.spaceXs))
                Text(
                    text = stringResource(Res.string.delete_confirm_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textDim,
                )
                Spacer(Modifier.height(dimens.spaceS))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(Res.string.action_cancel),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textDim,
                        )
                    }
                    TextButton(onClick = onConfirm) {
                        Text(
                            text = stringResource(Res.string.action_delete),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
