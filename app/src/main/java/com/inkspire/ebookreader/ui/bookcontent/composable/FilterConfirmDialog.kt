package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun FilterConfirmDialog(
    selectedText: String,
    stylingState: StylingState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = stylingState.stylePreferences.backgroundColor,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Hide this text?",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = stylingState.stylePreferences.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"$selectedText\"",
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = stylingState.stylePreferences.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This text will be removed from all chapters in this book.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = stylingState.stylePreferences.textColor.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = stylingState.stylePreferences.textColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = stylingState.stylePreferences.textColor)
                    ) {
                        Text("Confirm", color = stylingState.stylePreferences.backgroundColor)
                    }
                }
            }
        }
    }
}