package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.TracebackTheme

private val ConduitBloomReach = 9.dp

private const val ConduitBloomRest = 0.34f
private const val ConduitBloomPeak = 0.90f

internal const val CompletedSegmentAlpha = 0.45f

internal const val ExitedRailAlpha = 0.30f

internal fun pipeRailAlpha(isTerminal: Boolean): Float =
    if (isTerminal) ExitedRailAlpha else 1f

internal enum class PipeSegmentTone {
    Completed,
    Current,
    Empty,
    Exited,
}

internal fun pipeSegmentTone(trackIndex: Int?, segmentIndex: Int): PipeSegmentTone =
    when {
        trackIndex == null -> PipeSegmentTone.Exited
        segmentIndex < trackIndex -> PipeSegmentTone.Completed
        segmentIndex == trackIndex -> PipeSegmentTone.Current
        else -> PipeSegmentTone.Empty
    }

internal fun pipeSegmentColor(tone: PipeSegmentTone, stageColor: Color, trackColor: Color): Color =
    when (tone) {
        PipeSegmentTone.Completed -> stageColor.copy(alpha = CompletedSegmentAlpha)
        PipeSegmentTone.Current -> stageColor
        PipeSegmentTone.Empty -> trackColor
        PipeSegmentTone.Exited -> trackColor
    }

@Composable
fun StagePipeline(stage: PipelineStage, modifier: Modifier = Modifier) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val motion = TracebackTheme.motion

    val color by animateColorAsState(
        targetValue = stageColor(stage),
        animationSpec = tween(motion.slow, easing = motion.standardEasing),
        label = "conduitColor",
    )
    val railAlpha by animateFloatAsState(
        targetValue = pipeRailAlpha(stage.isTerminal),
        animationSpec = tween(motion.slow, easing = motion.standardEasing),
        label = "conduitRail",
    )

    val still = motion.stilled
    val bloom = remember { Animatable(ConduitBloomRest) }
    LaunchedEffect(stage, still) {
        if (still) {
            bloom.snapTo(ConduitBloomRest)
            return@LaunchedEffect
        }
        val half = motion.stageBloom / 2
        bloom.animateTo(ConduitBloomPeak, tween(half, easing = motion.standardEasing))
        bloom.animateTo(ConduitBloomRest, tween(half, easing = motion.standardEasing))
    }

    val trackColor = colors.track
    val trackIndex = stage.trackIndex
    val bloomAlpha = bloom.value

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.conduitHeight + ConduitBloomReach * 2)
    ) {
        val count = PipelineStage.track.size
        val thickness = dimens.conduitHeight.toPx()
        val gap = dimens.conduitGap.toPx()
        val centerY = size.height / 2f
        val segment = ((size.width - gap * (count - 1)) / count).coerceAtLeast(thickness)
        val radius = thickness / 2f
        val reach = ConduitBloomReach.toPx()

        repeat(count) { index ->
            val left = index * (segment + gap)
            val tone = pipeSegmentTone(trackIndex, index)
            if (tone == PipeSegmentTone.Current) {
                drawSegmentBloom(color, left, segment, centerY, reach, bloomAlpha)
            }
            drawSegment(
                color = pipeSegmentColor(tone, color, trackColor),
                left = left,
                width = segment,
                centerY = centerY,
                thickness = thickness,
                radius = radius,
                alpha = railAlpha,
            )
        }
    }
}

private fun DrawScope.drawSegment(
    color: Color,
    left: Float,
    width: Float,
    centerY: Float,
    thickness: Float,
    radius: Float,
    alpha: Float,
) {
    drawLine(
        color = color,
        start = Offset(left + radius, centerY),
        end = Offset(left + width - radius, centerY),
        strokeWidth = thickness,
        cap = StrokeCap.Round,
        alpha = alpha,
    )
}

private fun DrawScope.drawSegmentBloom(
    color: Color,
    left: Float,
    width: Float,
    centerY: Float,
    reach: Float,
    alpha: Float,
) {
    drawRect(
        brush = Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                0.5f to color.copy(alpha = alpha),
                1f to Color.Transparent,
            ),
            startY = centerY - reach,
            endY = centerY + reach,
        ),
        topLeft = Offset(left, centerY - reach),
        size = Size(width, reach * 2f),
    )
}
