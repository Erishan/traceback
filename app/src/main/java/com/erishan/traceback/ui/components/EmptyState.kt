package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme

private val CardMaxWidth = 300.dp

@Composable
fun EmptyState(modifier: Modifier = Modifier, title: String, message: String? = null) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TbGlassSurface(modifier = Modifier.widthIn(max = CardMaxWidth)) {
            Column(
                modifier = Modifier.padding(
                    horizontal = dimens.spaceXl,
                    vertical = dimens.spaceL,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textHigh,
                    textAlign = TextAlign.Center,
                )
                if (message != null) {
                    Spacer(Modifier.height(dimens.spaceXs))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textDim,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(name = "dark")
@Composable
private fun EmptyStateDarkPreview() {
    ComponentPreview(darkTheme = true) {
        EmptyState(title = "No opportunities yet", message = "Add the first one to start a trace.")
    }
}

@Preview(name = "light")
@Composable
private fun EmptyStateLightPreview() {
    ComponentPreview(darkTheme = false) {
        EmptyState(title = "No opportunities yet", message = "Add the first one to start a trace.")
    }
}
