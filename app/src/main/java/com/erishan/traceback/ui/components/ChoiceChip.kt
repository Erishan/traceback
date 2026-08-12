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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun ChoiceChip(
    label: String,
    selected: Boolean,
    selectedBg: Color,
    selectedFg: Color,
    onClick: () -> Unit,
    leadingDot: Color? = null,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(if (selected) selectedBg else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (leadingDot != null) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(leadingDot)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.sp),
            color = if (selected) selectedFg else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D)
@Composable
private fun ChoiceChipPreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ChoiceChip(
                    label = "Upwork",
                    selected = true,
                    selectedBg = TracebackTheme.colors.accentDim,
                    selectedFg = MaterialTheme.colorScheme.primary,
                    onClick = {},
                )
                ChoiceChip(
                    label = "LinkedIn",
                    selected = false,
                    selectedBg = TracebackTheme.colors.accentDim,
                    selectedFg = MaterialTheme.colorScheme.primary,
                    onClick = {},
                )
            }
        }
    }
}
