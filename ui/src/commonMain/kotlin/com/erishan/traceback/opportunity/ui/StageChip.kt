package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.label

@Composable
fun StageChip(
    stage: PipelineStage,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val color = stageColor(stage)
    ChoiceChip(
        label = stage.label().uppercase(),
        selected = selected,
        selectionColor = color,
        onClick = onClick,
        leadingDot = color,
        enabled = enabled,
    )
}
