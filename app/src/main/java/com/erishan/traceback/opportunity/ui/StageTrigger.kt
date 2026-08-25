package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.minTouchTarget

@Composable
fun StageTrigger(stage: PipelineStage, open: Boolean, onClick: () -> Unit) {
    val c = stageColor(stage)
    val caret by animateFloatAsState(if (open) 180f else 0f, label = "caret")
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = c.copy(alpha = 0.16f),
        modifier = Modifier.minTouchTarget(),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(c)
            )
            Text(
                text = stringResource(stageLabelRes(stage)),
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.sp),
                color = c,
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = c,
                modifier = Modifier.rotate(caret),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D)
@Composable
private fun StageTriggerPreview() {
    TracebackTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Box(Modifier.padding(16.dp)) {
                StageTrigger(stage = PipelineStage.APPLIED, open = false, onClick = {})
            }
        }
    }
}
