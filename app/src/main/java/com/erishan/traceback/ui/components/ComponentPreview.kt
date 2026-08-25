package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme

internal val PreviewHeight: Dp = 220.dp

@Composable
internal fun ComponentPreview(
    darkTheme: Boolean,
    height: Dp = PreviewHeight,
    content: @Composable BoxScope.() -> Unit,
) {
    TracebackTheme(darkTheme = darkTheme) {
        Box(modifier = Modifier.fillMaxWidth().height(height)) {
            AuroraBackground()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TracebackTheme.dimens.screenPadding)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}
