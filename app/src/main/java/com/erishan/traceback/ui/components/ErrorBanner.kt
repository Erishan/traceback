package com.erishan.traceback.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme

private val PreviewFrameHeight = 220.dp

private val ErrorGlyph = 16.dp

private const val ErrorFillAlpha = 0.12f
private const val ErrorEdgeAlpha = 0.36f

@Composable
fun ErrorBanner(
    text: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val dimens = TracebackTheme.dimens
    val error = MaterialTheme.colorScheme.error

    TbGlassSurface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        fill = error.copy(alpha = ErrorFillAlpha),
        edge = error.copy(alpha = ErrorEdgeAlpha),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimens.spaceS, vertical = dimens.spaceXs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = error,
                modifier = Modifier.size(ErrorGlyph),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = error,
                modifier = Modifier.weight(1f),
            )
            if (actionText != null && onAction != null) {
                TextAction(text = actionText, color = error, onClick = onAction)
            }
        }
    }
}

/** The rank below a filled button: text alone, still a full pointer target. */
@Composable
fun TextAction(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Box(
        modifier = modifier
            .heightIn(min = MinTouchTarget)
            .clip(ButtonShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) color else colors.textFaint,
            modifier = Modifier.padding(horizontal = dimens.spaceXs),
        )
    }
}

@Composable
private fun ErrorBannerPreviewContent() {
    Column(verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs)) {
        ErrorBanner(text = "Opportunity couldn't save")
        ErrorBanner(
            text = "The model didn't answer",
            actionText = "Try again",
            onAction = {},
        )
        TextAction(text = "Use as applied message", color = TracebackTheme.colors.textDim, onClick = {})
    }
}

@Preview(name = "dark")
@Composable
private fun ErrorBannerDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { ErrorBannerPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun ErrorBannerLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { ErrorBannerPreviewContent() }
}
