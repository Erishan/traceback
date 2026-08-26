package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.minTouchClickable

private val PreviewFrameHeight = 120.dp

private val BarButtonSize = 38.dp
private val BarButtonGlyph = 18.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TbTopAppBar(
    title: String?,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val colors = TracebackTheme.colors
    CenterAlignedTopAppBar(
        title = {
            if (title != null) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textDim,
                )
            }
        },
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions ?: {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = colors.textDim,
            navigationIconContentColor = colors.textDim,
            actionIconContentColor = colors.textDim,
        ),
    )
}

@Composable
fun TbBarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = TracebackTheme.colors
    Box(
        modifier = modifier.minTouchClickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        TbGlassSurface(
            modifier = Modifier.size(BarButtonSize),
            shape = MaterialTheme.shapes.extraSmall,
            strong = true,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) colors.textDim else colors.textFaint,
                modifier = Modifier.size(BarButtonGlyph).align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun TopAppBarPreviewContent() {
    TbTopAppBar(
        title = "Opportunity",
        navigationIcon = {
            TbBarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = {},
            )
        },
        actions = {
            TbBarIconButton(
                icon = Icons.Outlined.DeleteOutline,
                contentDescription = "Delete",
                onClick = {},
            )
        },
    )
}

@Preview(name = "dark")
@Composable
private fun TbTopAppBarDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { TopAppBarPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun TbTopAppBarLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { TopAppBarPreviewContent() }
}
