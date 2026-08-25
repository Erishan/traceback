package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.components.ComponentPreview
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme

private val PreviewFrameHeight = 260.dp

internal val MinStagePickerSize = MinTouchTarget

private const val BadgeFillAlpha = 0.14f
private const val BadgeEdgeAlpha = 0.24f
private const val DisabledTriggerAlpha = 0.55f
private const val HalfTurn = 180f

private val TerminalGlyph = 15.dp
private val ChevronSize = 18.dp

@Composable
fun StageTrigger(
    stage: PipelineStage,
    open: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val motion = TracebackTheme.motion
    val shape = MaterialTheme.shapes.small

    val color by animateColorAsState(
        targetValue = stageColor(stage),
        animationSpec = tween(motion.slow, easing = motion.standardEasing),
        label = "triggerColor",
    )
    val chevron by animateFloatAsState(
        targetValue = if (open) HalfTurn else 0f,
        animationSpec = tween(motion.fast, easing = motion.standardEasing),
        label = "triggerChevron",
    )

    Box(
        modifier = modifier
            .sizeIn(minWidth = MinStagePickerSize, minHeight = MinStagePickerSize)
            .clip(shape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = stringResource(R.string.cd_change_stage),
                onClick = onClick,
            )
            .alpha(if (enabled) 1f else DisabledTriggerAlpha),
        contentAlignment = Alignment.CenterStart,
    ) {
        val label = stringResource(stageLabelRes(stage))
        if (stage.isTerminal) {
            TerminalBadge(label = label, color = color, chevron = chevron, shape = shape)
        } else {
            TrackLabel(label = label, color = color, chevron = chevron)
        }
    }
}

@Composable
private fun TrackLabel(label: String, color: Color, chevron: Float) {
    val dimens = TracebackTheme.dimens
    Row(
        modifier = Modifier.padding(horizontal = dimens.spaceXxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceXxs),
    ) {
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = color)
        Chevron(color = color, rotation = chevron)
    }
}

@Composable
private fun TerminalBadge(
    label: String,
    color: Color,
    chevron: Float,
    shape: Shape,
) {
    val dimens = TracebackTheme.dimens
    Row(
        modifier = Modifier
            .clip(shape)
            .background(color.copy(alpha = BadgeFillAlpha))
            .border(dimens.hairline, color.copy(alpha = BadgeEdgeAlpha), shape)
            .padding(
                start = dimens.spaceXs,
                end = dimens.spaceXxs,
                top = dimens.spaceXs,
                bottom = dimens.spaceXs,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceXxs),
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(TerminalGlyph),
        )
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = color)
        Chevron(color = color, rotation = chevron)
    }
}

@Composable
private fun Chevron(color: Color, rotation: Float) {
    Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = null,
        tint = color,
        modifier = Modifier
            .size(ChevronSize)
            .rotate(rotation),
    )
}

@Composable
private fun StageTriggerPreviewContent() {
    Column(verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs)) {
        StageTrigger(stage = PipelineStage.APPLIED, open = false, onClick = {})
        StageTrigger(stage = PipelineStage.INTERVIEW, open = true, onClick = {})
        StageTrigger(stage = PipelineStage.LOST, open = false, onClick = {})
        StageTrigger(stage = PipelineStage.CLOSED, open = false, onClick = {}, enabled = false)
    }
}

@Preview(name = "dark")
@Composable
private fun StageTriggerDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { StageTriggerPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun StageTriggerLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { StageTriggerPreviewContent() }
}
