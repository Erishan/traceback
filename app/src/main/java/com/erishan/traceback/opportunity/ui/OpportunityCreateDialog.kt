package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.AuroraBackground
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.ErrorBanner
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.PrimaryButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.components.TextAction
import com.erishan.traceback.ui.theme.SheetShape
import com.erishan.traceback.ui.theme.TracebackTheme

private const val DescriptionMaxLines = 6

private const val ScrimAlpha = 0.55f

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
        shape = SheetShape,
        containerColor = sheetSurface(),
        contentColor = TracebackTheme.colors.textHigh,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = ScrimAlpha),
        dragHandle = { SheetHandle() },
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
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

@Composable
private fun sheetSurface(): Color =
    TracebackTheme.colors.glassStrong.compositeOver(TracebackTheme.colors.ground)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetHandle() {
    val colors = TracebackTheme.colors
    TbGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = SheetShape,
        fill = Color.Transparent,
        edge = Color.Transparent,
    ) {
        BottomSheetDefaults.DragHandle(
            modifier = Modifier.align(Alignment.Center),
            color = colors.textFaint,
        )
    }
}

/** Stateless sheet body - the windowed composable above cannot be previewed, this can. */
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
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val editable = !uiState.isSaving

    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding)
            .padding(bottom = dimens.spaceXl),
    ) {
        Text(
            text = stringResource(R.string.create_opportunity_title),
            style = MaterialTheme.typography.titleMedium,
            color = colors.textHigh,
        )
        Spacer(Modifier.height(dimens.spaceXxs))
        Text(
            text = stringResource(R.string.create_opportunity_lead),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textDim,
        )
        Spacer(Modifier.height(dimens.spaceL))

        FieldLabel(stringResource(R.string.field_title))
        TbTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            placeholder = stringResource(R.string.create_title_hint),
            imeAction = ImeAction.Next,
            enabled = editable,
        )
        Spacer(Modifier.height(dimens.spaceM))

        FieldLabel(
            text = stringResource(R.string.field_description),
            trailing = stringResource(R.string.field_optional),
        )
        TbTextField(
            value = uiState.description.orEmpty(),
            onValueChange = onDescriptionChange,
            placeholder = stringResource(R.string.create_description_hint),
            singleLine = false,
            maxLines = DescriptionMaxLines,
            enabled = editable,
        )
        Spacer(Modifier.height(dimens.spaceM))

        FieldLabel(stringResource(R.string.field_source))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            OpportunitySource.entries.forEach { source ->
                ChoiceChip(
                    label = stringResource(sourceLabelRes(source)),
                    selected = uiState.source == source,
                    selectedBg = colors.glassStrong,
                    selectedFg = colors.textHigh,
                    onClick = { onSourceChange(source) },
                    enabled = editable,
                )
            }
        }

        AnimatedVisibility(visible = uiState.source == OpportunitySource.OTHER) {
            Column {
                Spacer(Modifier.height(dimens.spaceM))
                FieldLabel(stringResource(R.string.field_source_label))
                TbTextField(
                    value = uiState.sourceLabel.orEmpty(),
                    onValueChange = onSourceLabelChange,
                    placeholder = stringResource(R.string.create_source_label_hint),
                    enabled = editable,
                )
            }
        }
        Spacer(Modifier.height(dimens.spaceM))

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
                enabled = editable,
            )
        }
        AnimatedVisibility(visible = stageOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.spaceS)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
            ) {
                PipelineStage.entries.forEach { stage ->
                    StageChip(
                        stage = stage,
                        selected = uiState.pipelineStage == stage,
                        onClick = {
                            onStageChange(stage)
                            stageOpen = false
                        },
                        enabled = editable,
                    )
                }
            }
        }

        Spacer(Modifier.height(dimens.spaceL))

        AnimatedVisibility(visible = uiState.hasError) {
            Column {
                ErrorBanner(text = stringResource(R.string.opportunity_could_not_save))
                Spacer(Modifier.height(dimens.spaceS))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceS)) {
            TextAction(
                text = stringResource(R.string.action_cancel),
                color = colors.textDim,
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                text = stringResource(R.string.action_save),
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = uiState.title.isNotBlank() && !uiState.isSaving,
                busy = uiState.isSaving,
            )
        }
    }
}

private val EmptyDraft = OpportunityCreateUiState()

private val OtherSourceDraft = OpportunityCreateUiState(
    title = "SaaS onboarding flow redesign",
    description = "Rework the multi-step signup, reduce mobile drop-off.",
    source = OpportunitySource.OTHER,
    sourceLabel = "Twitter DM",
    pipelineStage = PipelineStage.APPLIED,
)

private val SavingDraft = OtherSourceDraft.copy(
    source = OpportunitySource.UPWORK,
    sourceLabel = null,
    isSaving = true,
)

@Composable
private fun CreateSheetPreview(darkTheme: Boolean, uiState: OpportunityCreateUiState) {
    TracebackTheme(darkTheme = darkTheme) {
        Box(Modifier.fillMaxSize()) {
            AuroraBackground()
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(SheetShape)
                    .background(sheetSurface()),
            ) {
                SheetHandle()
                CreateSheetContent(
                    uiState = uiState,
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
}

@Preview(name = "empty - dark", widthDp = 360, heightDp = 640)
@Composable
private fun CreateSheetEmptyDarkPreview() = CreateSheetPreview(true, EmptyDraft)

@Preview(name = "empty - light", widthDp = 360, heightDp = 640)
@Composable
private fun CreateSheetEmptyLightPreview() = CreateSheetPreview(false, EmptyDraft)

@Preview(name = "other source - dark", widthDp = 360, heightDp = 640)
@Composable
private fun CreateSheetOtherSourceDarkPreview() = CreateSheetPreview(true, OtherSourceDraft)

@Preview(name = "other source - light", widthDp = 360, heightDp = 640)
@Composable
private fun CreateSheetOtherSourceLightPreview() = CreateSheetPreview(false, OtherSourceDraft)

@Preview(name = "saving - dark", widthDp = 360, heightDp = 640)
@Composable
private fun CreateSheetSavingDarkPreview() = CreateSheetPreview(true, SavingDraft)

@Preview(name = "saving - light", widthDp = 360, heightDp = 640)
@Composable
private fun CreateSheetSavingLightPreview() = CreateSheetPreview(false, SavingDraft)
