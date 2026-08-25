package com.erishan.traceback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme

private const val BusyFillAlpha = 0.55f

private const val ButtonBloomCenterAlpha = 0.34f
private const val ButtonBloomMidAlpha = 0.12f
private const val ButtonBloomDrop = 0.14f

private const val BloomMidStop = 0.55f

private val IndicatorSize = 18.dp
private val IndicatorStroke = 2.dp

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    busy: Boolean = false,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val accent = colors.accent
    val lit = enabled || busy

    val fill = when {
        enabled -> accent
        busy -> accent.copy(alpha = BusyFillAlpha)
        else -> colors.glassStrong
    }
    val content = if (lit) colors.onAccent else colors.textFaint

    Box(
        modifier = modifier
            .heightIn(min = MinTouchTarget)
            .then(
                if (lit) {
                    Modifier.drawBehind {
                        accentBloom(
                            color = accent,
                            reach = dimens.fabGlow.toPx(),
                            centerAlpha = ButtonBloomCenterAlpha,
                            midAlpha = ButtonBloomMidAlpha,
                            drop = ButtonBloomDrop,
                        )
                    }
                } else {
                    Modifier
                }
            )
            .clip(ButtonShape)
            .background(fill)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (busy) {
            CircularProgressIndicator(
                modifier = Modifier.size(IndicatorSize),
                color = content,
                strokeWidth = IndicatorStroke,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = content,
            )
        }
    }
}

internal fun DrawScope.accentBloom(
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

@Composable
private fun PrimaryButtonPreviewContent() {
    Column(verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceS)) {
        PrimaryButton(text = "Save profile", onClick = {}, modifier = Modifier.fillMaxWidth())
        PrimaryButton(
            text = "Save profile",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            busy = true,
        )
        PrimaryButton(
            text = "Save profile",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
        )
    }
}

@Preview(name = "dark")
@Composable
private fun PrimaryButtonDarkPreview() {
    ComponentPreview(darkTheme = true, height = 220.dp) { PrimaryButtonPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun PrimaryButtonLightPreview() {
    ComponentPreview(darkTheme = false, height = 220.dp) { PrimaryButtonPreviewContent() }
}
