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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.minTouchTarget

private const val SelectedFillAlpha = 0.16f
private const val SelectedEdgeAlpha = 0.42f
private const val SelectedBloomAlpha = 0.22f
private const val SelectedBloomSpread = 0.85f

private val DotSize = 8.dp

@Composable
fun ChoiceChip(
    label: String,
    selected: Boolean,
    selectedBg: Color = TracebackTheme.colors.accent.copy(alpha = SelectedFillAlpha),
    selectedFg: Color = TracebackTheme.colors.accent,
    onClick: () -> Unit,
    leadingDot: Color? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val contentColor = if (selected) selectedFg else colors.textDim

    TbGlassSurface(
        modifier = Modifier
            .minTouchTarget()
            .then(if (selected) Modifier.drawBehind { bloom(selectedFg) } else Modifier)
            .clip(shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected },
        shape = shape,
        fill = if (selected) selectedBg else null,
        edge = if (selected) selectedFg.copy(alpha = SelectedEdgeAlpha) else null,
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

private fun DrawScope.bloom(color: Color) {
    val radius = size.maxDimension * SelectedBloomSpread
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to color.copy(alpha = SelectedBloomAlpha),
                1f to Color.Transparent,
            ),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
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
    ComponentPreview(darkTheme = true, height = 120.dp) { ChoiceChipPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun ChoiceChipLightPreview() {
    ComponentPreview(darkTheme = false, height = 120.dp) { ChoiceChipPreviewContent() }
}
