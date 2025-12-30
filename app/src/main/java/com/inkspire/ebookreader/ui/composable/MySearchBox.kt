package com.inkspire.ebookreader.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun MySearchBox(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    hint: @Composable () -> Unit,
    decorationAlwaysVisible: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    cursorColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    textStyle: TextStyle = TextStyle(
        color = textColor
    ),
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActionHandler? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val focusRequester = remember { FocusRequester() }

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (decorationAlwaysVisible || isFocused || textFieldState.text.isNotEmpty()) 1f else 0f,
        label = "bgAlpha"
    )

    val underlineAlpha by animateFloatAsState(
        targetValue = if (decorationAlwaysVisible || isFocused || textFieldState.text.isNotEmpty()) 1f else 0f,
        label = "lineAlpha"
    )

    val underlineColor by animateColorAsState(
        targetValue = if (isFocused) cursorColor else textColor
    )

    val verticalPadding by animateDpAsState(
        targetValue = if (decorationAlwaysVisible || isFocused || textFieldState.text.isNotEmpty()) 12.dp else 0.dp,
        label = "padding"
    )

    BasicTextField(
        state = textFieldState,
        lineLimits = TextFieldLineLimits.SingleLine,
        interactionSource = interactionSource,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .fillMaxWidth(),
        textStyle = textStyle,
        cursorBrush = SolidColor(cursorColor),
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor.copy(
                            alpha = backgroundAlpha
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = verticalPadding),
                contentAlignment = Alignment.CenterStart
            ) {
                if (textFieldState.text.isEmpty() && !isFocused) {
                    hint()
                }
                Row {
                    leadingIcon()
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        innerTextField()
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(underlineColor.copy(alpha = underlineAlpha))
                        )
                    }
                    trailingIcon()
                }
            }
        },
        keyboardOptions = keyboardOptions,
        onKeyboardAction = keyboardActions
    )
}