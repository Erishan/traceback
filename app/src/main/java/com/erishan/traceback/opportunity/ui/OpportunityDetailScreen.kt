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
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.minTouchTarget
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
    onBrief: () -> Unit,
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
                onClick = onBack,
                modifier = Modifier.size(MinTouchTarget),
                colors = IconButtonDefaults.iconButtonColors(
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
                val briefCd = stringResource(R.string.cd_brief)
                TextButton(
                    onClick = onBrief,
                    enabled = content.briefActionEnabled,
                    modifier = Modifier
                        .heightIn(min = MinTouchTarget)
                        .semantics { contentDescription = briefCd },
                ) {
                    Text(stringResource(R.string.action_brief))
                }
                IconButton(
                    onClick = { showConfirm = true },
                    enabled = !content.isBusy,
                    modifier = Modifier.size(MinTouchTarget),
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
                    onBrief = onBrief,
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
) {
    var sourceOpen by remember { mutableStateOf(false) }
    var stageOpen by remember { mutableStateOf(false) }
    val editEnabled = !content.isBusy

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

        BriefSection(
            content = content,
            onBrief = onBrief,
            onUseProposalAsAppliedMessage = onAppliedMessageChange,
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
            Text(
                text = formatTimestampOrUnknown(content.createdAt),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = TracebackTheme.colors.textFaint,
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
private fun BriefSection(
    content: OpportunityDetailUiState.Content,
    onBrief: () -> Unit,
    onUseProposalAsAppliedMessage: (String) -> Unit,
    enabled: Boolean,
) {
    val briefEnabled = content.briefActionEnabled
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = onBrief,
                enabled = briefEnabled,
                modifier = Modifier.heightIn(min = MinTouchTarget),
            ) {
                Text(stringResource(R.string.action_brief))
            }
            if (content.briefInFlight) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (content.briefFailed != null) {
            ErrorBanner(text = stringResource(briefFailureRes(content.briefFailed)))
        }
        if (!content.canBrief && content.briefGateReason != null) {
            Text(
                text = stringResource(briefGateReasonRes(content.briefGateReason)),
                style = MaterialTheme.typography.bodyMedium,
                color = TracebackTheme.colors.textFaint,
            )
        }
        val brief = content.aiBrief
        if (brief == null) {
            Text(
                text = stringResource(R.string.brief_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = TracebackTheme.colors.textFaint,
            )
        } else {
            FitCard(brief)
            ProposalCard(
                proposal = brief.proposal,
                onUseAsAppliedMessage = onUseProposalAsAppliedMessage,
                enabled = enabled,
            )
            PriceCard(brief)
            DurationCard(brief)
            ApproachCard(brief)
        }
    }
}

@Composable
private fun FitCard(brief: JobBrief) {
    ReadOnlyCard(label = stringResource(R.string.field_fit)) {
        Text(
            text = stringResource(fitVerdictRes(brief.fit.verdict)),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = brief.fit.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ProposalCard(
    proposal: String,
    onUseAsAppliedMessage: (String) -> Unit,
    enabled: Boolean,
) {
    ReadOnlyCard(label = stringResource(R.string.field_proposal)) {
        Text(
            text = proposal,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        TextButton(
            onClick = { onUseAsAppliedMessage(proposal) },
            enabled = enabled,
            modifier = Modifier.heightIn(min = MinTouchTarget),
        ) {
            Text(stringResource(R.string.brief_use_as_applied))
        }
    }
}

@Composable
private fun PriceCard(brief: JobBrief) {
    ReadOnlyCard(label = stringResource(R.string.field_price)) {
        Text(
            text = stringResource(R.string.brief_price_range, brief.price.low, brief.price.high),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = brief.price.rationale,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DurationCard(brief: JobBrief) {
    ReadOnlyCard(label = stringResource(R.string.field_duration)) {
        Text(
            text = brief.duration.range,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.brief_duration_hours, brief.duration.hours),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(durationBasisRes(brief.duration.basis)),
            style = MaterialTheme.typography.bodySmall,
            color = TracebackTheme.colors.textFaint,
        )
    }
}

@Composable
private fun ApproachCard(brief: JobBrief) {
    ReadOnlyCard(label = stringResource(R.string.field_approach)) {
        Text(
            text = brief.approach.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (brief.approach.technologies.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = brief.approach.technologies.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReadOnlyCard(
    label: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        CardLabel(label)
        Spacer(Modifier.height(9.dp))
        content()
    }
}

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
    "yes" -> R.string.brief_verdict_yes
    "no" -> R.string.brief_verdict_no
    else -> R.string.brief_verdict_stretch
}

private fun durationBasisRes(basis: String): Int =
    if (basis == "profile") R.string.brief_basis_profile else R.string.brief_basis_typical

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
                .minTouchTarget()
                .clickable(role = Role.Button) {
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
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
        modifier = Modifier.minTouchTarget(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
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

    val body: @Composable () -> Unit = {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
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

    if (editing || !enabled) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            content = body,
        )
    } else {
        Surface(
            onClick = {
                buffer = value.orEmpty()
                editing = true
            },
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            content = body,
        )
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
            val sortedNotes = notes.sortedByDescending {
                it.createdAt?.toEpochMilliseconds() ?: Long.MIN_VALUE
            }
            sortedNotes.forEachIndexed { index, note ->
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
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(MinTouchTarget),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(TracebackTheme.colors.accentDim),
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
                text = formatTimestampOrUnknown(note.createdAt),
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
        IconButton(
            onClick = onDelete,
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_delete),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
            modifier = Modifier.size(MinTouchTarget),
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

@Composable
private fun formatTimestampOrUnknown(instant: Instant?): String =
    instant?.let(::formatNoteTimestamp) ?: stringResource(R.string.date_unknown)

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
        IconButton(
            onClick = onCancel,
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_cancel),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(MinTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_cancel),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
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
            onBrief = {},
            deleteFailed = false,
            onDeleteErrorDismiss = {},
        )
    }
}
