package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.ComponentPreview
import com.erishan.traceback.ui.theme.TracebackTheme

private const val StageChipFillAlpha = 0.16f

@Composable
fun StageChip(
    stage: PipelineStage,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val color = stageColor(stage)
    ChoiceChip(
        label = stringResource(stageLabelRes(stage)),
        selected = selected,
        selectedBg = color.copy(alpha = StageChipFillAlpha),
        selectedFg = color,
        onClick = onClick,
        leadingDot = color,
        enabled = enabled,
    )
}

@Composable
private fun StageChipPreviewContent() {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs),
    ) {
        PipelineStage.entries.forEach { stage ->
            StageChip(
                stage = stage,
                selected = stage == PipelineStage.INTERVIEW,
                onClick = {},
            )
        }
    }
}

@Preview(name = "dark")
@Composable
private fun StageChipDarkPreview() {
    ComponentPreview(darkTheme = true, height = 120.dp) { StageChipPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun StageChipLightPreview() {
    ComponentPreview(darkTheme = false, height = 120.dp) { StageChipPreviewContent() }
}
