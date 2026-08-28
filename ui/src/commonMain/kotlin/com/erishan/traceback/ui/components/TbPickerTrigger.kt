package com.erishan.traceback.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme

private val ChevronSize = 18.dp

private const val DisabledAlpha = 0.55f
private const val HalfTurn = 180f

/**
 * The app's one way of saying "this is the current value, and there are others".
 *
 * A value plus a chevron that turns over when the choices are showing. It names the
 * current answer rather than the question, so the row reads as an answer already given -
 * which is what a setting with a default is.
 *
 * It does not own the choices. The caller decides what opens below it and keeps [open],
 * because the two pickers in the app expand into different things.
 *
 * @param color the value's own colour, when it has one. Stage pickers burn in the stage's
 *   colour; a picker whose value has no colour of its own leaves this alone.
 * @param onClickLabel what the tap does, for screen readers.
 */
@Composable
fun TbPickerTrigger(
    label: String,
    open: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = TracebackTheme.colors.textHigh,
    onClickLabel: String? = null,
    enabled: Boolean = true,
) {
    val motion = TracebackTheme.motion
    val dimens = TracebackTheme.dimens

    val chevron by animateFloatAsState(
        targetValue = if (open) HalfTurn else 0f,
        animationSpec = tween(motion.fast, easing = motion.standardEasing),
        label = "pickerChevron",
    )

    Box(
        modifier = modifier
            .sizeIn(minWidth = MinTouchTarget, minHeight = MinTouchTarget)
            .clip(MaterialTheme.shapes.small)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick,
            )
            .alpha(if (enabled) 1f else DisabledAlpha),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimens.spaceXxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXxs),
        ) {
            Text(text = label, style = MaterialTheme.typography.titleSmall, color = color)
            TbPickerChevron(color = color, rotation = chevron)
        }
    }
}

/** The chevron on its own, for a trigger that needs its own container. */
@Composable
fun TbPickerChevron(color: Color, rotation: Float) {
    Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = null,
        tint = color,
        modifier = Modifier
            .size(ChevronSize)
            .rotate(rotation),
    )
}
