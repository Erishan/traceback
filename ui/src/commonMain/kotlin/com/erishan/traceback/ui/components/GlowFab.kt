package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.FabShape
import com.erishan.traceback.ui.theme.TracebackTheme

private val NoElevation = 0.dp

private const val FabBloomCenterAlpha = 0.42f
private const val FabBloomMidAlpha = 0.14f
private const val FabBloomDrop = 0.22f

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
            .bloom(
                color = accent,
                reach = dimens.fabGlow,
                centerAlpha = FabBloomCenterAlpha,
                midAlpha = FabBloomMidAlpha,
                drop = FabBloomDrop,
            )
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
