package com.erishan.traceback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.FabShape
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme

private const val BloomMidStop = 0.55f

fun Modifier.bloom(
    color: Color,
    reach: Dp,
    centerAlpha: Float,
    midAlpha: Float,
    drop: Float,
): Modifier = drawBehind {
    drawBloom(color, reach.toPx(), centerAlpha, midAlpha, drop)
}

internal fun DrawScope.drawBloom(
    color: Color,
    reach: Float,
    centerAlpha: Float,
    midAlpha: Float,
    drop: Float,
) {
    if (size.width <= 0f || size.height <= 0f) return
    val radius = size.width / 2f + reach
    val origin = Offset(center.x, center.y + size.height * drop)
    scale(scaleX = 1f, scaleY = size.height / size.width, pivot = origin) {
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to color.copy(alpha = centerAlpha),
                    BloomMidStop to color.copy(alpha = midAlpha),
                    1f to Color.Transparent,
                ),
                center = origin,
                radius = radius,
            ),
            radius = radius,
            center = origin,
        )
    }
}

private val PreviewFrameHeight = 200.dp
private val PreviewButtonWidth = 150.dp

private const val PreviewCenterAlpha = 0.42f
private const val PreviewMidAlpha = 0.14f
private const val PreviewDrop = 0.22f

@Composable
private fun BloomPreviewContent() {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXl)) {
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceXl)) {
            Box(
                Modifier
                    .bloom(
                        color = colors.accent,
                        reach = dimens.fabGlow,
                        centerAlpha = PreviewCenterAlpha,
                        midAlpha = PreviewMidAlpha,
                        drop = PreviewDrop,
                    )
                    .size(dimens.fabSize)
                    .clip(FabShape)
                    .background(colors.accent)
            )
            Box(
                Modifier
                    .bloom(
                        color = colors.stageHired,
                        reach = dimens.rodGlow,
                        centerAlpha = PreviewCenterAlpha,
                        midAlpha = PreviewMidAlpha,
                        drop = PreviewDrop,
                    )
                    .size(dimens.fabSize)
                    .clip(FabShape)
                    .background(colors.stageHired)
            )
        }
        Box(
            Modifier
                .bloom(
                    color = colors.accent,
                    reach = dimens.fabGlow,
                    centerAlpha = PreviewCenterAlpha,
                    midAlpha = PreviewMidAlpha,
                    drop = PreviewDrop,
                )
                .width(PreviewButtonWidth)
                .height(MinTouchTarget)
                .clip(ButtonShape)
                .background(colors.accent)
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.labelLarge,
                color = colors.onAccent,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview(name = "dark")
@Composable
private fun BloomDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { BloomPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun BloomLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { BloomPreviewContent() }
}
