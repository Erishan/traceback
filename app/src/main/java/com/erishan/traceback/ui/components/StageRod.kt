package com.erishan.traceback.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme

private val PreviewFrameHeight = 320.dp

private const val BloomAlpha = 0.7f

private val PreviewCardHeight = 76.dp

@Composable
fun StageRod(color: Color, modifier: Modifier = Modifier) {
    val dimens = TracebackTheme.dimens
    Canvas(modifier = modifier.width(dimens.rodWidth).fillMaxHeight()) {
        val inset = dimens.spaceS.toPx()
        val glow = dimens.rodGlow.toPx()
        val top = inset.coerceAtMost(size.height / 2f)
        val bottom = size.height - top

        drawRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0f to color.copy(alpha = color.alpha * BloomAlpha),
                    1f to Color.Transparent,
                ),
                startX = 0f,
                endX = size.width + glow,
            ),
            topLeft = Offset(0f, top),
            size = Size(size.width + glow, bottom - top),
        )
        drawLine(
            color = color,
            start = Offset(size.width / 2f, top),
            end = Offset(size.width / 2f, bottom),
            strokeWidth = size.width,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun StageRodPreviewContent() {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceS)) {
        listOf(
            "In conversation" to colors.stageInConversation,
            "Interview" to colors.stageInterview,
            "Hired" to colors.stageHired,
        ).forEach { (label, color) ->
            TbGlassSurface(modifier = Modifier.height(PreviewCardHeight)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StageRod(color = color)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textHigh,
                        modifier = Modifier.padding(horizontal = dimens.spaceM),
                    )
                }
            }
        }
    }
}

@Preview(name = "dark")
@Composable
private fun StageRodDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { StageRodPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun StageRodLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { StageRodPreviewContent() }
}
