package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.DurationEstimate
import com.erishan.traceback.opportunity.domain.Fit
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.opportunity.domain.Price
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.ComponentPreview
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.ErrorBanner
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.TbBarIconButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.components.TextAction
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.PillShape
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.TracebackTheme.colors
import com.erishan.traceback.ui.theme.minTouchTarget
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

private val AddNoteToggleSize = 30.dp
private val AddNoteGlyph = 16.dp
private val InlineGlyph = 20.dp
private val BriefSpinnerSize = 16.dp
private val BriefSpinnerStroke = 2.dp
private val ScrollBottomInset = 40.dp

private val SkeletonKeyHeight = 8.dp
private val SkeletonValueHeight = 15.dp
private val SkeletonSupportHeight = 9.dp

private const val PlusToCloseRotation = 45f

private const val BriefActionFillAlpha = 0.10f
private const val BriefActionEdgeAlpha = 0.38f

private const val SkeletonKeyWidthFraction = 0.34f
private const val SkeletonValueWidthFraction = 0.62f
private const val SkeletonSupportWidthFraction = 0.88f

private const val BoxSupportMaxLines = 2
private const val ApproachSummaryMaxLines = 2
private const val ProposalCollapsedMaxLines = 6
private const val SkeletonRows = 2

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

    val deleteFailedText = stringResource(R.string.opportunity_could_not_delete)
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
        title = stringResource(R.string.detail_opportunity),
        auroraTint = auroraTint,
        navigationIcon = {
            TbBarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                onClick = onBack,
            )
        },
        actions = {
            if (content != null) {
                TbBarIconButton(
                    icon = Icons.Outlined.DeleteOutline,
                    contentDescription = stringResource(R.string.cd_delete),
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
            ErrorBanner(text = stringResource(R.string.opportunity_could_not_save))
            Spacer(Modifier.height(dimens.spaceS))
        }

        InlineTitle(value = content.title, onCommit = onTitleChange, enabled = editEnabled)
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
            label = stringResource(R.string.field_description),
            value = content.description,
            placeholder = stringResource(R.string.detail_description_hint),
            emptyText = stringResource(R.string.detail_description_empty),
            onCommit = onDescriptionChange,
            enabled = editEnabled,
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
            label = stringResource(R.string.field_applied_message),
            value = content.appliedMessage,
            placeholder = stringResource(R.string.applied_message_hint),
            emptyText = stringResource(R.string.applied_message_empty),
            onCommit = onAppliedMessageChange,
            enabled = editEnabled,
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
        val editLabel = stringResource(R.string.cd_edit)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = MinTouchTarget)
                .then(
                    if (enabled) {
                        Modifier.clickable(role = Role.Button, onClickLabel = editLabel) {
                            buffer = value
                            editing = true
                        }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = value,
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
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val shape = MaterialTheme.shapes.medium

    var editing by remember { mutableStateOf(false) }
    var buffer by remember { mutableStateOf("") }
    val tappable = enabled && !editing
    val editLabel = stringResource(R.string.cd_edit)

    TbGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (tappable) {
                    Modifier.clickable(role = Role.Button, onClickLabel = editLabel) {
                        buffer = value.orEmpty()
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
                        onCommit(buffer)
                        editing = false
                    },
                    enabled = enabled,
                )
            } else {
                Text(
                    text = value ?: emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (value == null) colors.textFaint else colors.textHigh,
                )
            }
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
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    var composing by remember { mutableStateOf(false) }

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FieldLabel(stringResource(R.string.field_notes), spacer = false)
                AddNoteToggle(
                    expanded = composing,
                    onClick = { composing = !composing },
                    enabled = enabled,
                )
            }

            AnimatedVisibility(visible = composing) {
                Column {
                    Spacer(Modifier.height(dimens.spaceXs))
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
                    Spacer(Modifier.height(dimens.spaceXs))
                    Text(
                        text = stringResource(R.string.notes_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textFaint,
                    )
                }
            } else {
                val sorted = notes.sortedByDescending {
                    it.createdAt?.toEpochMilliseconds() ?: Long.MIN_VALUE
                }
                sorted.forEachIndexed { index, note ->
                    if (index == 0) {
                        Spacer(Modifier.height(dimens.spaceS))
                    } else {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = dimens.spaceS),
                            thickness = dimens.hairline,
                            color = colors.edge,
                        )
                    }
                    NoteRow(note = note, onDelete = { onDelete(note.id) }, enabled = enabled)
                }
            }
        }
    }
}

@Composable
private fun AddNoteToggle(expanded: Boolean, onClick: () -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    val motion = TracebackTheme.motion
    val rotation by animateFloatAsState(
        targetValue = if (expanded) PlusToCloseRotation else 0f,
        animationSpec = tween(motion.fast, easing = motion.standardEasing),
        label = "addNoteRotation",
    )
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(MinTouchTarget),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = colors.textDim,
            containerColor = Color.Transparent,
        ),
    ) {
        TbGlassSurface(
            modifier = Modifier.size(AddNoteToggleSize),
            shape = CircleShape,
            strong = true,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(
                    if (expanded) R.string.cd_cancel else R.string.notes_add
                ),
                tint = colors.textDim,
                modifier = Modifier
                    .size(AddNoteGlyph)
                    .align(Alignment.Center)
                    .rotate(rotation),
            )
        }
    }
}

@Composable
private fun NoteRow(note: Note, onDelete: () -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatTimestampOrUnknown(note.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textFaint,
            )
            Spacer(Modifier.height(dimens.spaceXxs))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textHigh,
            )
        }
        Spacer(Modifier.width(dimens.spaceXs))
        IconButton(
            onClick = onDelete,
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_delete),
                tint = colors.textDim,
                modifier = Modifier.size(InlineGlyph),
            )
        }
    }
}

@Composable
private fun NoteComposer(onSubmit: (String) -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    var buffer by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
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
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.cd_confirm),
                tint = colors.accent,
                modifier = Modifier.size(InlineGlyph),
            )
        }
    }
}

/**
 * Where the record came from, and when it arrived.
 *
 * Both are nameplate, not control: they sit below the editing surface, behind a rule, in the
 * quietest rank on the screen. Stage is the live control and reads that way; source is the label on
 * the box, and only turns into a picker if you ask it to.
 */
@Composable
private fun MetaFooter(
    source: OpportunitySource,
    sourceLabel: String?,
    createdAt: Instant?,
    onSourceClick: () -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider(thickness = dimens.hairline, color = colors.edge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.spaceXxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SourcePill(
                source = source,
                sourceLabel = sourceLabel,
                onClick = onSourceClick,
                enabled = enabled,
            )
            Spacer(Modifier.width(dimens.spaceS))
            Text(
                text = formatTimestampOrUnknown(createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textFaint,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SourcePill(
    source: OpportunitySource,
    sourceLabel: String?,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val label = sourceLabel?.takeIf { source == OpportunitySource.OTHER && it.isNotBlank() }
        ?: stringResource(sourceLabelRes(source))
    val changeSource = stringResource(R.string.cd_change_source)

    Box(modifier = Modifier.minTouchTarget(), contentAlignment = Alignment.CenterStart) {
        TbGlassSurface(
            modifier = Modifier
                .clip(PillShape)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClickLabel = changeSource,
                    onClick = onClick,
                ),
            shape = PillShape,
            strong = true,
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    horizontal = dimens.spaceXs,
                    vertical = dimens.spaceXxs,
                ),
            )
        }
    }
}

@Composable
private fun SourcePicker(
    source: OpportunitySource,
    sourceLabel: String?,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    enabled: Boolean,
) {
    val dimens = TracebackTheme.dimens
    Column {
        Spacer(Modifier.height(dimens.spaceS))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            OpportunitySource.entries.forEach { entry ->
                ChoiceChip(
                    label = stringResource(sourceLabelRes(entry)).uppercase(),
                    selected = source == entry,
                    onClick = { onSourceChange(entry) },
                    selectionColor = colors.textHigh,
                    selectedFill = colors.glassStrong,
                    enabled = enabled,
                )
            }
        }
        AnimatedVisibility(visible = source == OpportunitySource.OTHER) {
            Column {
                Spacer(Modifier.height(dimens.spaceS))
                FieldLabel(stringResource(R.string.field_source_label))
                TbTextField(
                    value = sourceLabel.orEmpty(),
                    onValueChange = onSourceLabelChange,
                    placeholder = stringResource(R.string.create_source_label_hint),
                    enabled = enabled,
                )
            }
        }
    }
}

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
                contentDescription = stringResource(R.string.cd_cancel),
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
                contentDescription = stringResource(R.string.cd_confirm),
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
                    text = stringResource(R.string.delete_confirm_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textHigh,
                )
                Spacer(Modifier.height(dimens.spaceXs))
                Text(
                    text = stringResource(R.string.delete_confirm_message),
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
                            text = stringResource(R.string.action_cancel),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textDim,
                        )
                    }
                    TextButton(onClick = onConfirm) {
                        Text(
                            text = stringResource(R.string.action_delete),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

// brief

@Composable
private fun BriefSection(
    content: OpportunityDetailUiState.Content,
    onBrief: () -> Unit,
    onUseProposalAsAppliedMessage: (String) -> Unit,
    onOpenMe: () -> Unit,
    enabled: Boolean,
) {
    val dimens = TracebackTheme.dimens
    val brief = content.aiBrief

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceXs),
    ) {
        BriefHeader(
            hasBrief = brief != null,
            inFlight = content.briefInFlight,
            actionEnabled = content.briefActionEnabled,
            onBrief = onBrief,
        )

        if (content.briefFailed != null) {
            ErrorBanner(
                text = stringResource(briefFailureRes(content.briefFailed)),
                actionText = stringResource(R.string.action_try_again),
                onAction = onBrief.takeIf { content.briefActionEnabled },
            )
        }

        if (content.briefGateReason != null) {
            BriefGateCard(
                reason = content.briefGateReason,
                onOpenMe = onOpenMe,
                enabled = enabled,
            )
        }

        when {
            content.briefInFlight -> BriefSkeleton()

            brief != null -> BriefBoxes(
                brief = brief,
                onUseProposalAsAppliedMessage = onUseProposalAsAppliedMessage,
                enabled = enabled,
            )

            // Gated and empty: the gate card above already says why there is nothing here.
            content.briefGateReason == null -> BriefEmptyCard()
        }
    }
}

@Composable
private fun BriefHeader(
    hasBrief: Boolean,
    inFlight: Boolean,
    actionEnabled: Boolean,
    onBrief: () -> Unit,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FieldLabel(text = stringResource(R.string.field_brief), spacer = false)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            if (inFlight) {
                CircularProgressIndicator(
                    modifier = Modifier.size(BriefSpinnerSize),
                    strokeWidth = BriefSpinnerStroke,
                    color = colors.accent,
                    trackColor = Color.Transparent,
                )
            }
            BriefActionButton(
                text = stringResource(
                    if (hasBrief) R.string.action_brief_rerun else R.string.action_brief
                ),
                enabled = actionEnabled,
                onClick = onBrief,
            )
        }
    }
}

@Composable
private fun BriefActionButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(
        modifier = Modifier
            .minTouchTarget()
            .clip(ButtonShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = ButtonShape,
        fill = if (enabled) colors.accent.copy(alpha = BriefActionFillAlpha) else null,
        edge = if (enabled) colors.accent.copy(alpha = BriefActionEdgeAlpha) else null,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) colors.accentText else colors.textFaint,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = dimens.spaceM),
        )
    }
}

@Composable
private fun BriefEmptyCard() {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.brief_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textFaint,
            modifier = Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS),
        )
    }
}

@Composable
private fun BriefGateCard(
    reason: BriefGateReason,
    onOpenMe: () -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = dimens.spaceXs, vertical = dimens.spaceXs),
        ) {
            Text(
                text = stringResource(briefGateReasonRes(reason)),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textFaint,
                modifier = Modifier.padding(horizontal = dimens.spaceXs),
            )
            TextAction(
                text = stringResource(R.string.brief_open_me),
                color = colors.accentText,
                onClick = onOpenMe,
                enabled = enabled,
            )
        }
    }
}

// brief · boxes

@Composable
private fun BriefBoxes(
    brief: JobBrief,
    onUseProposalAsAppliedMessage: (String) -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
        BriefRow {
            BriefBox(
                label = stringResource(R.string.field_fit),
                value = stringResource(fitVerdictRes(brief.fit.verdict)),
                valueColor = fitVerdictColor(brief.fit.verdict),
                support = brief.fit.summary,
                modifier = Modifier.weight(1f),
            )
            BriefBox(
                label = stringResource(R.string.field_price),
                value = stringResource(
                    R.string.brief_price_range,
                    brief.price.low,
                    brief.price.high,
                ),
                valueColor = colors.textHigh,
                support = brief.price.rationale,
                modifier = Modifier.weight(1f),
            )
        }
        BriefRow {
            BriefBox(
                label = stringResource(R.string.field_duration),
                value = stringResource(R.string.brief_duration_hours, brief.duration.hours),
                valueColor = colors.textHigh,
                support = stringResource(
                    R.string.brief_duration_support,
                    brief.duration.range,
                    stringResource(durationBasisRes(brief.duration.basis)),
                ),
                modifier = Modifier.weight(1f),
            )
            ApproachBox(approach = brief.approach, modifier = Modifier.weight(1f))
        }
        ProposalCard(
            proposal = brief.proposal,
            onUseAsAppliedMessage = onUseProposalAsAppliedMessage,
            enabled = enabled,
        )
    }
}

@Composable
private fun BriefRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs),
        content = content,
    )
}

@Composable
private fun BriefBox(
    label: String,
    value: String,
    valueColor: Color,
    support: String?,
    modifier: Modifier = Modifier,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceS),
        ) {
            FieldLabel(label)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                maxLines = BoxSupportMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (support != null) {
                Spacer(Modifier.height(dimens.spaceXxs))
                Text(
                    text = support,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textDim,
                    maxLines = BoxSupportMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ApproachBox(approach: Approach, modifier: Modifier = Modifier) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceS),
        ) {
            FieldLabel(stringResource(R.string.field_approach))
            Text(
                text = approach.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textHigh,
                maxLines = ApproachSummaryMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (approach.technologies.isNotEmpty()) {
                Spacer(Modifier.height(dimens.spaceXxs))
                Text(
                    text = approach.technologies.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textDim,
                    maxLines = BoxSupportMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: String,
    onUseAsAppliedMessage: (String) -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    var expanded by remember { mutableStateOf(false) }
    var clipped by remember { mutableStateOf(false) }

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceXs, vertical = dimens.spaceXs),
        ) {
            Column(modifier = Modifier.padding(horizontal = dimens.spaceXs)) {
                FieldLabel(stringResource(R.string.field_proposal))
                Text(
                    text = proposal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textHigh,
                    maxLines = if (expanded) Int.MAX_VALUE else ProposalCollapsedMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    // Only the collapsed pass can tell us there is more to read.
                    onTextLayout = { if (!expanded) clipped = it.hasVisualOverflow },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextAction(
                    text = stringResource(R.string.brief_use_as_applied),
                    color = colors.textDim,
                    onClick = { onUseAsAppliedMessage(proposal) },
                    enabled = enabled,
                )
                if (clipped) {
                    TextAction(
                        text = stringResource(
                            if (expanded) R.string.action_show_less else R.string.action_show_more
                        ),
                        color = colors.textDim,
                        onClick = { expanded = !expanded },
                    )
                }
            }
        }
    }
}

// brief · loading

@Composable
private fun BriefSkeleton() {
    val dimens = TracebackTheme.dimens

    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
        repeat(SkeletonRows) {
            BriefRow {
                SkeletonBox(Modifier.weight(1f))
                SkeletonBox(Modifier.weight(1f))
            }
        }
        SkeletonBox(Modifier.fillMaxWidth())
    }
}

// Every skeleton box holds the same fixed bars, so they line up without filling the row -
// and filling it under the screen's scrolling column would ask for an infinite height.
@Composable
private fun SkeletonBox(modifier: Modifier = Modifier) {
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceS),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            SkeletonBar(widthFraction = SkeletonKeyWidthFraction, height = SkeletonKeyHeight)
            SkeletonBar(widthFraction = SkeletonValueWidthFraction, height = SkeletonValueHeight)
            SkeletonBar(
                widthFraction = SkeletonSupportWidthFraction,
                height = SkeletonSupportHeight,
            )
        }
    }
}

@Composable
private fun SkeletonBar(widthFraction: Float, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(PillShape)
            .background(TracebackTheme.colors.track)
    )
}

// brief · mapping

private fun briefFailureRes(kind: BriefFailureKind): Int = when (kind) {
    BriefFailureKind.BadKey -> R.string.brief_failed_bad_key
    BriefFailureKind.RateLimited -> R.string.brief_failed_rate_limit
    BriefFailureKind.InvalidResponse -> R.string.brief_failed_invalid
    BriefFailureKind.Network -> R.string.brief_failed_network
}

private fun briefGateReasonRes(reason: BriefGateReason): Int = when (reason) {
    BriefGateReason.MissingAbout -> R.string.brief_disabled_no_about
    BriefGateReason.MissingKey -> R.string.brief_disabled_no_key
    BriefGateReason.MissingAboutAndKey -> R.string.brief_disabled_no_about_or_key
}

private fun fitVerdictRes(verdict: String): Int = when (verdict) {
    VerdictYes -> R.string.brief_verdict_yes
    VerdictNo -> R.string.brief_verdict_no
    else -> R.string.brief_verdict_stretch
}

@Composable
private fun fitVerdictColor(verdict: String): Color = with(TracebackTheme.colors) {
    when (verdict) {
        VerdictYes -> stageHired
        VerdictNo -> stageLost
        else -> stageInConversation
    }
}

private fun durationBasisRes(basis: String): Int =
    if (basis == BasisProfile) R.string.brief_basis_profile else R.string.brief_basis_typical

private const val VerdictYes = "yes"
private const val VerdictNo = "no"
private const val BasisProfile = "profile"

// brief · previews

private val BriefPreviewShortHeight = 200.dp
private val BriefPreviewTallHeight = 520.dp

private val PreviewBrief = JobBrief(
    generatedAtEpochMillis = 1_723_600_000_000L,
    model = "gpt-4o",
    fit = Fit(
        verdict = VerdictYes,
        summary = "Compose work with a clear funnel goal - squarely your stack.",
    ),
    proposal = "I rebuilt a five-step signup into two screens for a B2B trial last quarter and " +
        "cut drop-off by a third. I would start by instrumenting the current funnel so we " +
        "argue from numbers, then ship the new flow behind a flag and compare cohorts. " +
        "Two weeks of build, one week watching it, and you keep the measurement harness " +
        "either way. Happy to walk the current flow with you before we scope it.",
    price = Price(
        low = "$3.2k",
        high = "$4.5k",
        rationale = "Two weeks at your mid band, plus a week of measurement.",
    ),
    duration = DurationEstimate(
        range = "40-56 hours",
        hours = "48",
        basis = BasisProfile,
    ),
    approach = Approach(
        summary = "Instrument the funnel, then rebuild signup as two screens behind a flag.",
        technologies = listOf("Compose", "Firebase", "Figma"),
    ),
)

private fun previewBriefState(
    aiBrief: JobBrief? = null,
    canBrief: Boolean = true,
    briefInFlight: Boolean = false,
    briefFailed: BriefFailureKind? = null,
    briefGateReason: BriefGateReason? = null,
) = previewContent(PipelineStage.APPLIED).copy(
    aiBrief = aiBrief,
    canBrief = canBrief,
    briefInFlight = briefInFlight,
    briefFailed = briefFailed,
    briefGateReason = briefGateReason,
)

@Composable
private fun BriefSectionPreview(
    darkTheme: Boolean,
    content: OpportunityDetailUiState.Content,
    height: Dp,
) {
    ComponentPreview(darkTheme = darkTheme, height = height) {
        BriefSection(
            content = content,
            onBrief = {},
            onUseProposalAsAppliedMessage = {},
            onOpenMe = {},
            enabled = true,
        )
    }
}

@Preview(name = "brief · empty · dark", widthDp = 360)
@Composable
private fun BriefEmptyDarkPreview() {
    BriefSectionPreview(true, previewBriefState(), BriefPreviewShortHeight)
}

@Preview(name = "brief · empty · light", widthDp = 360)
@Composable
private fun BriefEmptyLightPreview() {
    BriefSectionPreview(false, previewBriefState(), BriefPreviewShortHeight)
}

@Preview(name = "brief · gated · dark", widthDp = 360)
@Composable
private fun BriefGatedDarkPreview() {
    BriefSectionPreview(
        darkTheme = true,
        content = previewBriefState(
            canBrief = false,
            briefGateReason = BriefGateReason.MissingAboutAndKey,
        ),
        height = BriefPreviewShortHeight,
    )
}

@Preview(name = "brief · gated · light", widthDp = 360)
@Composable
private fun BriefGatedLightPreview() {
    BriefSectionPreview(
        darkTheme = false,
        content = previewBriefState(
            canBrief = false,
            briefGateReason = BriefGateReason.MissingAboutAndKey,
        ),
        height = BriefPreviewShortHeight,
    )
}

@Preview(name = "brief · loading · dark", widthDp = 360)
@Composable
private fun BriefLoadingDarkPreview() {
    BriefSectionPreview(
        darkTheme = true,
        content = previewBriefState(briefInFlight = true),
        height = BriefPreviewTallHeight,
    )
}

@Preview(name = "brief · loading · light", widthDp = 360)
@Composable
private fun BriefLoadingLightPreview() {
    BriefSectionPreview(
        darkTheme = false,
        content = previewBriefState(briefInFlight = true),
        height = BriefPreviewTallHeight,
    )
}

@Preview(name = "brief · failed · dark", widthDp = 360)
@Composable
private fun BriefFailedDarkPreview() {
    BriefSectionPreview(
        darkTheme = true,
        content = previewBriefState(briefFailed = BriefFailureKind.BadKey),
        height = BriefPreviewShortHeight,
    )
}

@Preview(name = "brief · failed · light", widthDp = 360)
@Composable
private fun BriefFailedLightPreview() {
    BriefSectionPreview(
        darkTheme = false,
        content = previewBriefState(briefFailed = BriefFailureKind.BadKey),
        height = BriefPreviewShortHeight,
    )
}

@Preview(name = "brief · full · dark", widthDp = 360)
@Composable
private fun BriefFullDarkPreview() {
    BriefSectionPreview(true, previewBriefState(aiBrief = PreviewBrief), BriefPreviewTallHeight)
}

@Preview(name = "brief · full · light", widthDp = 360)
@Composable
private fun BriefFullLightPreview() {
    BriefSectionPreview(false, previewBriefState(aiBrief = PreviewBrief), BriefPreviewTallHeight)
}

// timestamps

private val DetailDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.ENGLISH)

@Composable
private fun formatTimestampOrUnknown(instant: Instant?): String =
    instant?.let(::formatDetailTimestamp) ?: stringResource(R.string.date_unknown)

private fun formatDetailTimestamp(instant: Instant): String {
    val platformInstant = java.time.Instant.ofEpochMilli(instant.toEpochMilliseconds())
    val local = java.time.LocalDateTime.ofInstant(platformInstant, ZoneId.systemDefault())
    return DetailDateFormatter.format(local)
}

// previews

private val PreviewCreatedAt = Instant.fromEpochMilliseconds(1_723_600_000_000L)

private fun previewContent(
    stage: PipelineStage,
    description: String? = "Rework the multi-step signup and cut mobile drop-off across the trial funnel.",
    appliedMessage: String? = null,
) = OpportunityDetailUiState.Content(
    title = "SaaS onboarding flow redesign",
    description = description,
    source = OpportunitySource.UPWORK,
    sourceLabel = null,
    pipelineStage = stage,
    createdAt = PreviewCreatedAt,
    appliedMessage = appliedMessage,
    notes = listOf(
        Note(
            id = "n1",
            createdAt = PreviewCreatedAt,
            text = "Client wants a Loom walkthrough before the call.",
        ),
        Note(
            id = "n2",
            createdAt = Instant.fromEpochMilliseconds(1_723_700_000_000L),
            text = "Followed up Monday, no reply yet.",
        ),
    ),
)

@Composable
private fun DetailPreview(darkTheme: Boolean, uiState: OpportunityDetailUiState) {
    TracebackTheme(darkTheme = darkTheme) {
        OpportunityDetailScreen(
            uiState = uiState,
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
            onBrief = {},
            onOpenMe = {},
            deleteFailed = false,
            onDeleteErrorDismiss = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
internal fun DetailScreenShowcase(darkTheme: Boolean) {
    TracebackTheme(darkTheme = darkTheme, reducedMotion = true) {
        OpportunityDetailScreen(
            uiState = previewContent(
                stage = PipelineStage.INTERVIEW,
                appliedMessage = "Sent a two-paragraph note with the Loom link and a rate band.",
            ).copy(aiBrief = PreviewBrief, canBrief = true),
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
            onBrief = {},
            onOpenMe = {},
            deleteFailed = false,
            onDeleteErrorDismiss = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "active · dark", widthDp = 360, heightDp = 860)
@Composable
private fun DetailActiveDarkPreview() = DetailScreenShowcase(darkTheme = true)

@Preview(name = "active · light", widthDp = 360, heightDp = 860)
@Composable
private fun DetailActiveLightPreview() = DetailScreenShowcase(darkTheme = false)

@Preview(name = "lost · dark", widthDp = 360, heightDp = 860)
@Composable
private fun DetailLostDarkPreview() {
    DetailPreview(
        darkTheme = true,
        uiState = previewContent(PipelineStage.LOST, description = null),
    )
}

@Preview(name = "lost · light", widthDp = 360, heightDp = 860)
@Composable
private fun DetailLostLightPreview() {
    DetailPreview(
        darkTheme = false,
        uiState = previewContent(PipelineStage.LOST, description = null),
    )
}

@Preview(name = "not found · dark", widthDp = 360, heightDp = 420)
@Composable
private fun DetailNotFoundDarkPreview() {
    DetailPreview(darkTheme = true, uiState = OpportunityDetailUiState.NotFound)
}

@Preview(name = "not found · light", widthDp = 360, heightDp = 420)
@Composable
private fun DetailNotFoundLightPreview() {
    DetailPreview(darkTheme = false, uiState = OpportunityDetailUiState.NotFound)
}
