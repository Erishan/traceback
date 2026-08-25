package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme

private val PreviewFrameHeight = 140.dp

@Composable
fun FieldLabel(
    text: String,
    trailing: String? = null,
    spacer: Boolean = true,
) {
    val colors = TracebackTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textFaint,
        )
        if (trailing != null) {
            Text(
                text = " · ${trailing.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textFaint,
            )
        }
    }
    if (spacer) Spacer(Modifier.height(TracebackTheme.dimens.spaceXs))
}

@Composable
private fun FieldLabelPreviewContent() {
    Column {
        FieldLabel("Title")
        FieldLabel("Description", trailing = "optional")
        FieldLabel("Source", spacer = false)
    }
}

@Preview(name = "dark")
@Composable
private fun FieldLabelDarkPreview() {
    ComponentPreview(darkTheme = true, height = PreviewFrameHeight) { FieldLabelPreviewContent() }
}

@Preview(name = "light")
@Composable
private fun FieldLabelLightPreview() {
    ComponentPreview(darkTheme = false, height = PreviewFrameHeight) { FieldLabelPreviewContent() }
}
