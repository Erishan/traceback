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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme

private val PreviewFrameHeight = 220.dp

private const val BusyFillAlpha = 0.55f

private const val ButtonBloomCenterAlpha = 0.34f
private const val ButtonBloomMidAlpha = 0.12f
private const val ButtonBloomDrop = 0.14f

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
                    Modifier.bloom(
                        color = accent,
                        reach = dimens.fabGlow,
                        centerAlpha = ButtonBloomCenterAlpha,
                        midAlpha = ButtonBloomMidAlpha,
                        drop = ButtonBloomDrop,
                    )
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
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { PrimaryButtonPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun PrimaryButtonLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { PrimaryButtonPreviewContent() }
}
