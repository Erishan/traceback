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
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme


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
