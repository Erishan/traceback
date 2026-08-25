package com.erishan.traceback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.minTouchTarget

private val PreviewFrameHeight = 120.dp

private const val SelectedFillAlpha = 0.16f
private const val SelectedBloomAlpha = 0.22f
private const val SelectedBloomMidAlpha = 0.11f
private const val SelectedBloomDrop = 0f

private val DotSize = 8.dp

@Composable
fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectionColor: Color = TracebackTheme.colors.accent,
    selectedFill: Color = selectionColor.copy(alpha = SelectedFillAlpha),
    leadingDot: Color? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val contentColor = if (selected) colors.textHigh else colors.textDim

    TbGlassSurface(
        modifier = Modifier
            .minTouchTarget()
            .then(
                if (selected) {
                    Modifier.bloom(
                        color = selectionColor,
                        reach = dimens.rodGlow,
                        centerAlpha = SelectedBloomAlpha,
                        midAlpha = SelectedBloomMidAlpha,
                        drop = SelectedBloomDrop,
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected },
        shape = shape,
        fill = if (selected) selectedFill else null,
        edge = if (selected) selectionColor else null,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceXs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            if (leadingDot != null) {
                Box(Modifier.size(DotSize).clip(CircleShape).background(leadingDot))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
            )
        }
    }
}

@Composable
private fun ChoiceChipPreviewContent() {
    Row(horizontalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs)) {
        ChoiceChip(label = "Upwork", selected = true, onClick = {})
        ChoiceChip(label = "LinkedIn", selected = false, onClick = {})
        ChoiceChip(
            label = "Hired",
            selected = false,
            onClick = {},
            leadingDot = TracebackTheme.colors.stageHired,
        )
    }
}

@Preview(name = "dark")
@Composable
private fun ChoiceChipDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { ChoiceChipPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun ChoiceChipLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { ChoiceChipPreviewContent() }
}
