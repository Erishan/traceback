package com.erishan.traceback.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme
import com.erishan.traceback.ui.theme.minTouchTarget


private const val SelectedFillAlpha = 0.16f
private const val SelectedEdgeAlpha = 0.42f
private const val SelectedBloomAlpha = 0.22f
private const val SelectedBloomMidAlpha = 0.11f
private const val SelectedBloomDrop = 0f

private val CapsuleHeight = 30.dp
private const val PressedScale = 0.96f

private val DotSize = 8.dp

@Composable
fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectionColor: Color = TracebackTheme.colors.accent,
    selectedFill: Color = selectionColor.copy(alpha = SelectedFillAlpha),
    leadingDot: Color? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val motion = TracebackTheme.motion
    val contentColor = if (selected) colors.textHigh else colors.textDim

    val dot: Color? = when {
        leadingDot != null -> leadingDot
        selected -> selectionColor
        else -> null
    }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) PressedScale else 1f,
        animationSpec = motion.pressSpring,
        label = "chipPress",
    )

    Box(
        modifier = Modifier
            .minTouchTarget()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { this.selected = selected },
        contentAlignment = Alignment.Center,
    ) {
        TbGlassSurface(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .height(CapsuleHeight)
                .then(
                    if (selected) {
                        Modifier.bloom(
                            color = selectionColor,
                            reach = dimens.rodGlow,
                            centerAlpha = SelectedBloomAlpha,
                            midAlpha = SelectedBloomMidAlpha,
                            drop = SelectedBloomDrop,
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(shape),
            shape = shape,
            fill = if (selected) selectedFill else null,
            edge = if (selected) selectionColor.copy(alpha = SelectedEdgeAlpha) else null,
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = dimens.spaceS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spaceXxs),
            ) {
                if (dot != null) {
                    Box(Modifier.size(DotSize).clip(CircleShape).background(dot))
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                )
            }
        }
    }
}
