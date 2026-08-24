package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun StageChip(
    stage: PipelineStage,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val c = stageColor(stage)
    ChoiceChip(
        label = stringResource(stageLabelRes(stage)),
        selected = selected,
        selectedBg = c.copy(alpha = 0.16f),
        selectedFg = c,
        onClick = onClick,
        leadingDot = c,
        enabled = enabled,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D)
@Composable
private fun StageChipPreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StageChip(stage = PipelineStage.APPLIED, selected = true, onClick = {})
                StageChip(stage = PipelineStage.DRAFT, selected = false, onClick = {})
            }
        }
    }
}
