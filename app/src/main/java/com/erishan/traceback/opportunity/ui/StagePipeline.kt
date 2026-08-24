package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.TracebackTheme

private val SegmentHeight = 6.dp
private val SegmentGap = 6.dp
private const val CompletedAlpha = 0.5f
private const val ExitedAlpha = 0.5f
private const val BadgeFillAlpha = 0.14f
private const val BadgeBorderAlpha = 0.24f

@Composable
fun StagePipeline(
    stage: PipelineStage,
    pickerOpen: Boolean,
    onOpenPicker: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val color by animateColorAsState(targetValue = stageColor(stage), label = "stageColor")
    val pipeAlpha by animateFloatAsState(
        targetValue = if (stage.isTerminal) ExitedAlpha else 1f,
        label = "pipeAlpha",
    )
    val caret by animateFloatAsState(
        targetValue = if (pickerOpen) 180f else 0f,
        label = "caret",
    )

    Column(modifier = modifier) {
        Pipe(trackIndex = stage.trackIndex, color = color, alpha = pipeAlpha)
        Spacer(Modifier.height(if (stage.isTerminal) 12.dp else 6.dp))
        if (stage.isTerminal) {
            TerminalBadge(
                stage = stage,
                color = color,
                caret = caret,
                onClick = onOpenPicker,
                enabled = enabled,
            )
        } else {
            TrackLabel(
                stage = stage,
                color = color,
                caret = caret,
                onClick = onOpenPicker,
                enabled = enabled,
            )
        }
    }
}

@Composable
private fun Pipe(trackIndex: Int?, color: Color, alpha: Float) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalArrangement = Arrangement.spacedBy(SegmentGap),
    ) {
        PipelineStage.track.forEachIndexed { index, _ ->
            val target = if (trackIndex == null) {
                color
            } else {
                when {
                    index > trackIndex -> trackColor
                    index == trackIndex -> color
                    else -> color.copy(alpha = CompletedAlpha)
                }
            }

            val segment by animateColorAsState(targetValue = target, label = "segment$index")
            Box(
                Modifier
                    .weight(1f)
                    .height(SegmentHeight)
                    .clip(CircleShape)
                    .background(segment)
            )
        }
    }
}

@Composable
private fun TrackLabel(
    stage: PipelineStage,
    color: Color,
    caret: Float,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .alpha(if (enabled) 1f else 0.55f)
            .padding(vertical = 8.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(stageLabelRes(stage)),
            style = MaterialTheme.typography.titleSmall,
            color = color,
        )
        Caret(color = color, rotation = caret)
    }
}

@Composable
private fun TerminalBadge(
    stage: PipelineStage,
    color: Color,
    caret: Float,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val shape = MaterialTheme.shapes.small
    Row(
        modifier = Modifier
            .clip(shape)
            .background(color.copy(alpha = BadgeFillAlpha))
            .border(width = 1.dp, color = color.copy(alpha = BadgeBorderAlpha), shape = shape)
            .clickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .alpha(if (enabled) 1f else 0.55f)
            .padding(start = 10.dp, end = 6.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = stringResource(stageLabelRes(stage)),
            style = MaterialTheme.typography.titleSmall,
            color = color,
        )
        Caret(color = color, rotation = caret)
    }
}

@Composable
private fun Caret(color: Color, rotation: Float) {
    Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = stringResource(R.string.cd_change_stage),
        tint = color,
        modifier = Modifier
            .size(18.dp)
            .rotate(rotation),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D, widthDp = 320)
@Composable
private fun StagePipelinePreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                StagePipeline(PipelineStage.APPLIED, pickerOpen = false, onOpenPicker = {})
                StagePipeline(PipelineStage.INTERVIEW, pickerOpen = true, onOpenPicker = {})
                StagePipeline(PipelineStage.LOST, pickerOpen = false, onOpenPicker = {})
            }
        }
    }
}
