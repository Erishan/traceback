package com.erishan.traceback.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.TracebackTheme


private val RingWidth = 3.dp
private const val RingAlpha = 0.14f

@Composable
fun TbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    imeAction: ImeAction = ImeAction.Done,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val shape = MaterialTheme.shapes.small

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val textColor = if (enabled) colors.textHigh else colors.textDim

    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    SideEffect {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = RingWidth,
                color = if (focused) colors.accent.copy(alpha = RingAlpha) else Color.Transparent,
                shape = shape,
            )
            .padding(RingWidth)
            .then(modifier)
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { updated ->
                textFieldValue = updated
                if (updated.text != value) {
                    onValueChange(updated.text)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.accent),
        ) { field ->
            TbGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                strong = true,
                edge = if (focused) colors.accent else null,
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = dimens.spaceS,
                        vertical = dimens.spaceS,
                    )
                ) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textFaint,
                        )
                    }
                    field()
                }
            }
        }
    }
}
