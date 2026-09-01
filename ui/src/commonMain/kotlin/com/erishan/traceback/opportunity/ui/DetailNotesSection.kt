package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.Res
import com.erishan.traceback.ui.theme.*
import com.erishan.traceback.ui.theme.TracebackTheme
import org.jetbrains.compose.resources.stringResource

private val AddNoteToggleSize = 30.dp
private val AddNoteGlyph = 16.dp

private const val PlusToCloseRotation = 45f

@Composable
internal fun NotesSection(
    notes: List<Note>,
    onAdd: (String) -> Unit,
    onDelete: (String) -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    var composing by remember { mutableStateOf(false) }

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FieldLabel(stringResource(Res.string.field_notes), spacer = false)
                AddNoteToggle(
                    expanded = composing,
                    onClick = { composing = !composing },
                    enabled = enabled,
                )
            }

            AnimatedVisibility(visible = composing) {
                Column {
                    Spacer(Modifier.height(dimens.spaceXs))
                    NoteComposer(
                        onSubmit = { text ->
                            onAdd(text)
                            composing = false
                        },
                        enabled = enabled,
                    )
                }
            }

            if (notes.isEmpty()) {
                if (!composing) {
                    Spacer(Modifier.height(dimens.spaceXs))
                    Text(
                        text = stringResource(Res.string.notes_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textFaint,
                    )
                }
            } else {
                val sorted = notes.sortedByDescending {
                    it.createdAt?.toEpochMilliseconds() ?: Long.MIN_VALUE
                }
                sorted.forEachIndexed { index, note ->
                    if (index == 0) {
                        Spacer(Modifier.height(dimens.spaceS))
                    } else {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = dimens.spaceS),
                            thickness = dimens.hairline,
                            color = colors.edge,
                        )
                    }
                    NoteRow(note = note, onDelete = { onDelete(note.id) }, enabled = enabled)
                }
            }
        }
    }
}

@Composable
private fun AddNoteToggle(expanded: Boolean, onClick: () -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    val motion = TracebackTheme.motion
    val rotation by animateFloatAsState(
        targetValue = if (expanded) PlusToCloseRotation else 0f,
        animationSpec = tween(motion.fast, easing = motion.standardEasing),
        label = "addNoteRotation",
    )
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(MinTouchTarget),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = colors.textDim,
            containerColor = Color.Transparent,
        ),
    ) {
        TbGlassSurface(
            modifier = Modifier.size(AddNoteToggleSize),
            shape = CircleShape,
            strong = true,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(
                    if (expanded) Res.string.cd_cancel else Res.string.notes_add
                ),
                tint = colors.textDim,
                modifier = Modifier
                    .size(AddNoteGlyph)
                    .align(Alignment.Center)
                    .rotate(rotation),
            )
        }
    }
}

@Composable
private fun NoteRow(note: Note, onDelete: () -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatTimestampOrUnknown(note.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textFaint,
            )
            Spacer(Modifier.height(dimens.spaceXxs))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textHigh,
            )
        }
        Spacer(Modifier.width(dimens.spaceXs))
        IconButton(
            onClick = onDelete,
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.cd_delete),
                tint = colors.textDim,
                modifier = Modifier.size(InlineGlyph),
            )
        }
    }
}

@Composable
private fun NoteComposer(onSubmit: (String) -> Unit, enabled: Boolean) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    var buffer by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            TbTextField(
                value = buffer,
                onValueChange = { buffer = it },
                placeholder = stringResource(Res.string.notes_hint),
                modifier = Modifier.focusRequester(focusRequester),
                singleLine = false,
                minLines = 1,
                maxLines = 4,
                enabled = enabled,
            )
        }
        IconButton(
            onClick = {
                val text = buffer.trim()
                if (text.isNotBlank()) onSubmit(text)
            },
            enabled = enabled,
            modifier = Modifier.size(MinTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.cd_confirm),
                tint = colors.accent,
                modifier = Modifier.size(InlineGlyph),
            )
        }
    }
}
