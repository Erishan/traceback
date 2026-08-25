package com.erishan.traceback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme

private const val HighlightFadeIn = 0.12f
private const val HighlightFadeOut = 0.88f

private val PreviewCardWidth = 260.dp

@Composable
fun TbGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    strong: Boolean = false,
    fill: Color? = null,
    edge: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = TracebackTheme.colors
    val hairline = TracebackTheme.dimens.hairline
    val highlight = colors.edgeHighlight

    Box(
        modifier = modifier
            .clip(shape)
            .background(fill ?: if (strong) colors.glassStrong else colors.glass)
            .drawWithContent {
                drawContent()
                val stroke = hairline.toPx()
                drawLine(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            HighlightFadeIn to highlight,
                            HighlightFadeOut to highlight,
                            1f to Color.Transparent,
                        ),
                    ),
                    start = Offset(0f, stroke / 2f),
                    end = Offset(size.width, stroke / 2f),
                    strokeWidth = stroke,
                )
            }
            .border(hairline, edge ?: colors.edge, shape),
        content = content,
    )
}

@Composable
private fun GlassSurfacePreviewContent() {
    val dimens = TracebackTheme.dimens
    Column {
        TbGlassSurface(modifier = Modifier.width(PreviewCardWidth)) {
            Column(Modifier.padding(dimens.spaceM)) {
                Text(
                    text = "Glass",
                    style = MaterialTheme.typography.titleSmall,
                    color = TracebackTheme.colors.textHigh,
                )
                Text(
                    text = "The default rank: a card on the ground",
                    style = MaterialTheme.typography.bodySmall,
                    color = TracebackTheme.colors.textDim,
                )
            }
        }
        TbGlassSurface(
            modifier = Modifier.padding(top = dimens.spaceS).width(PreviewCardWidth),
            strong = true,
        ) {
            Column(Modifier.padding(dimens.spaceM)) {
                Text(
                    text = "Glass, raised",
                    style = MaterialTheme.typography.titleSmall,
                    color = TracebackTheme.colors.textHigh,
                )
                Text(
                    text = "A sheet or an input, sitting on another surface",
                    style = MaterialTheme.typography.bodySmall,
                    color = TracebackTheme.colors.textDim,
                )
            }
        }
    }
}

@Preview(name = "dark")
@Composable
private fun TbGlassSurfaceDarkPreview() {
    ComponentPreview(darkTheme = true) { GlassSurfacePreviewContent() }
}

@Preview(name = "light")
@Composable
private fun TbGlassSurfaceLightPreview() {
    ComponentPreview(darkTheme = false) { GlassSurfacePreviewContent() }
}
