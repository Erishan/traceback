package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.TracebackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunityCreateDialog(
    uiState: OpportunityCreateUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    onStageChange: (PipelineStage) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetMaxWidth = Dp.Unspecified,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        CreateSheetContent(
            uiState = uiState,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onSourceChange = onSourceChange,
            onSourceLabelChange = onSourceLabelChange,
            onStageChange = onStageChange,
            onSave = onSave,
            onDismiss = onDismiss,
        )
    }
}

/** Stateless sheet body - @Preview */
@Composable
private fun CreateSheetContent(
    uiState: OpportunityCreateUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    onStageChange: (PipelineStage) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.create_opportunity_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.create_opportunity_lead),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(18.dp))

        // title - input area
        FieldLabel(stringResource(R.string.field_title))
        TbTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            placeholder = stringResource(R.string.create_title_hint),
            imeAction = ImeAction.Next,
        )
        Spacer(Modifier.height(14.dp))

        // description - input area
        FieldLabel(
            text = stringResource(R.string.field_description),
            trailing = stringResource(R.string.field_optional),
        )
        TbTextField(
            value = uiState.description.orEmpty(),
            onValueChange = onDescriptionChange,
            placeholder = stringResource(R.string.create_description_hint),
            singleLine = false,
            minLines = 1,
            maxLines = 6,
        )
        Spacer(Modifier.height(14.dp))

        // Source
        FieldLabel(stringResource(R.string.field_source))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OpportunitySource.entries.forEach { s ->
                ChoiceChip(
                    label = stringResource(sourceLabelRes(s)),
                    selected = uiState.source == s,
                    selectedBg = TracebackTheme.colors.accentDim,
                    selectedFg = MaterialTheme.colorScheme.primary,
                    onClick = { onSourceChange(s) },
                )
            }
        }

        // Source label
        AnimatedVisibility(visible = uiState.source == OpportunitySource.OTHER) {
            Column {
                Spacer(Modifier.height(14.dp))
                FieldLabel(stringResource(R.string.field_source_label))
                TbTextField(
                    value = uiState.sourceLabel.orEmpty(),
                    onValueChange = onSourceLabelChange,
                    placeholder = stringResource(R.string.create_source_label_hint),
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        // Stage - pipeline
        var stageOpen by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FieldLabel(text = stringResource(R.string.field_stage), spacer = false)
            StageTrigger(
                stage = uiState.pipelineStage,
                open = stageOpen,
                onClick = { stageOpen = !stageOpen },
            )
        }
        AnimatedVisibility(visible = stageOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 11.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PipelineStage.entries.forEach { st ->
                    StageChip(
                        stage = st,
                        selected = uiState.pipelineStage == st,
                        onClick = {
                            onStageChange(st)
                            stageOpen = false
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        AnimatedVisibility(visible = uiState.hasError) {
            Column {
                Row(
                    Modifier
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
                        text = stringResource(R.string.opportunity_could_not_save),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        // Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = ButtonShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text(stringResource(R.string.action_cancel))
            }
            Button(
                onClick = onSave,
                enabled = uiState.title.isNotBlank() && !uiState.isSaving && !uiState.isSaved,
                modifier = Modifier.weight(1f),
                shape = ButtonShape,
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
private fun FieldLabel(
    text: String,
    trailing: String? = null,
    spacer: Boolean = true,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
            ),
            color = TracebackTheme.colors.textFaint,
        )
        if (trailing != null) {
            Text(
                text = " · $trailing",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = TracebackTheme.colors.textFaint,
            )
        }
    }
    if (spacer) Spacer(Modifier.height(7.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    imeAction: ImeAction = ImeAction.Done,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TracebackTheme.colors.textFaint) },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = imeAction,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    selectedBg: Color,
    selectedFg: Color,
    onClick: () -> Unit,
    leadingDot: Color? = null,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(if (selected) selectedBg else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (leadingDot != null) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(leadingDot)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.sp),
            color = if (selected) selectedFg else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StageChip(stage: PipelineStage, selected: Boolean, onClick: () -> Unit) {
    val c = stageColor(stage)
    ChoiceChip(
        label = stringResource(stageLabelRes(stage)),
        selected = selected,
        selectedBg = c.copy(alpha = 0.16f),
        selectedFg = c,
        onClick = onClick,
        leadingDot = c,
    )
}

@Composable
private fun StageTrigger(stage: PipelineStage, open: Boolean, onClick: () -> Unit) {
    val c = stageColor(stage)
    val caret by animateFloatAsState(if (open) 180f else 0f, label = "caret")
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(c.copy(alpha = 0.16f))
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(c)
        )
        Text(
            text = stringResource(stageLabelRes(stage)),
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.sp),
            color = c,
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = c,
            modifier = Modifier.rotate(caret),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D)
@Composable
private fun CreateSheetContentPreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            CreateSheetContent(
                uiState = OpportunityCreateUiState(
                    title = "SaaS onboarding flow redesign",
                    description = "Rework the multi-step signup, reduce mobile drop-off.",
                    source = OpportunitySource.OTHER,
                    pipelineStage = PipelineStage.APPLIED,
                    sourceLabel = null,
                    hasError = false,
                ),
                onTitleChange = {},
                onDescriptionChange = {},
                onSourceChange = {},
                onSourceLabelChange = {},
                onStageChange = {},
                onSave = {},
                onDismiss = {},
            )
        }
    }
}
