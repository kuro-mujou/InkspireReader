package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun EditContentDialog(
    originalText: String,
    stylingState: StylingState,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var newContent by remember { mutableStateOf(originalText) }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = stylingState.stylePreferences.backgroundColor
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Content",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = stylingState.stylePreferences.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                    )
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Original:",
                        style = TextStyle(color = stylingState.stylePreferences.textColor.copy(alpha = 0.7f))
                    )
                    Text(
                        text = originalText,
                        maxLines = 3,
                        style = TextStyle(
                            fontStyle = FontStyle.Italic,
                            color = stylingState.stylePreferences.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                        )
                    )
                }

                HorizontalDivider(color = stylingState.stylePreferences.textColor)

                OutlinedTextField(
                    value = newContent,
                    onValueChange = { newContent = it },
                    textStyle = TextStyle(
                        color = stylingState.stylePreferences.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                    ),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    label = { Text("Corrected Text") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = stylingState.stylePreferences.textColor,
                        unfocusedTextColor = stylingState.stylePreferences.textColor,
                        cursorColor = stylingState.stylePreferences.textColor,
                        focusedBorderColor = stylingState.stylePreferences.textColor,
                        unfocusedBorderColor = stylingState.stylePreferences.textColor,
                        focusedLabelColor = stylingState.stylePreferences.textColor,
                        unfocusedLabelColor = stylingState.stylePreferences.textColor,
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = stylingState.stylePreferences.textColor)
                    ) {
                        Text("Cancel", color = stylingState.stylePreferences.backgroundColor)
                    }
                    Button(
                        onClick = { onSubmit(newContent) },
                        colors = ButtonDefaults.buttonColors(containerColor = stylingState.stylePreferences.textColor)
                    ) {
                        Text("Save Change", color = stylingState.stylePreferences.backgroundColor)
                    }
                }
            }
        }
    }
}