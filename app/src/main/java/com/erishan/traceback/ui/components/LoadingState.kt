package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme

private val IndicatorSize = 26.dp
private val IndicatorStroke = 2.dp

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(IndicatorSize),
            color = TracebackTheme.colors.accent,
            strokeWidth = IndicatorStroke,
            trackColor = Color.Transparent,
        )
    }
}

@Preview(name = "dark")
@Composable
private fun LoadingStateDarkPreview() {
    ComponentPreview(darkTheme = true) { LoadingState() }
}

@Preview(name = "light")
@Composable
private fun LoadingStateLightPreview() {
    ComponentPreview(darkTheme = false) { LoadingState() }
}
