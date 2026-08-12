package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun FieldLabel(
    text: String,
    trailing: String? = null,
    spacer: Boolean = true,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
            ),
            color = TracebackTheme.colors.textFaint,
        )
        if (trailing != null) {
            Text(
                text = " · $trailing",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = TracebackTheme.colors.textFaint,
            )
        }
    }
    if (spacer) Spacer(Modifier.height(7.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D)
@Composable
private fun FieldLabelPreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp)) {
                FieldLabel("Title")
                FieldLabel("Description", trailing = "optional")
            }
        }
    }
}
