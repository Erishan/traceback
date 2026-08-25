package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.FabShape
import com.erishan.traceback.ui.theme.TracebackTheme

private val NoElevation = 0.dp

private const val BloomDrop = 0.22f
private const val BloomCenterAlpha = 0.42f
private const val BloomMidStop = 0.55f
private const val BloomMidAlpha = 0.14f

@Composable
fun GlowFab(
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val accent = colors.accent

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .drawBehind {
                val radius = size.minDimension / 2f + dimens.fabGlow.toPx()
                val origin = Offset(center.x, center.y + size.height * BloomDrop)
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to accent.copy(alpha = BloomCenterAlpha),
                            BloomMidStop to accent.copy(alpha = BloomMidAlpha),
                            1f to Color.Transparent,
                        ),
                        center = origin,
                        radius = radius,
                    ),
                    radius = radius,
                    center = origin,
                )
            }
            .size(dimens.fabSize),
        shape = FabShape,
        containerColor = accent,
        contentColor = colors.onAccent,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = NoElevation,
            pressedElevation = NoElevation,
            focusedElevation = NoElevation,
            hoveredElevation = NoElevation,
        ),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Preview(name = "dark")
@Composable
private fun GlowFabDarkPreview() {
    ComponentPreview(darkTheme = true) {
        GlowFab(onClick = {}, contentDescription = "Add opportunity", icon = Icons.Default.Add)
    }
}

@Preview(name = "light")
@Composable
private fun GlowFabLightPreview() {
    ComponentPreview(darkTheme = false) {
        GlowFab(onClick = {}, contentDescription = "Add opportunity", icon = Icons.Default.Add)
    }
}
