package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun NoteDialog(
    note: String,
    noteInput: String = "",
    stylingState: StylingState,
    onDismiss: () -> Unit,
    onNoteChanged: (String) -> Unit
) {
    var noteContent by remember { mutableStateOf("") }
    LaunchedEffect(noteInput) {
        noteContent = noteInput
    }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = stylingState.backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (linkPattern.containsMatchIn(note)) {
                    AsyncImage(
                        model = note,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth()
                    ) {
                        VerticalDivider(
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                            thickness = 2.dp,
                            color = stylingState.textColor
                        )
                        Text(
                            text = note,
                            modifier = Modifier.padding(all = 8.dp),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontStyle = FontStyle.Italic,
                                color = stylingState.textColor,
                                textAlign = TextAlign.Justify,
                                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = noteContent,
                    onValueChange = {
                        noteContent = it
                    },
                    textStyle = TextStyle(
                        color = stylingState.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
                    ),
                    maxLines = 20,
                    label = {
                        Text(
                            text = "Enter your note",
                            style = TextStyle(
                                color = stylingState.textColor,
                                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = stylingState.textColor,
                        unfocusedLabelColor = stylingState.textColor,
                        focusedBorderColor = stylingState.textColor,
                        focusedLabelColor = stylingState.textColor,
                        cursorColor = stylingState.textColor,
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = stylingState.textColor,
                        )
                    ) {
                        Text(
                            text = "Close",
                            style = TextStyle(
                                color = stylingState.backgroundColor,
                                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
                            )
                        )
                    }
                    Button(
                        onClick = {
                            onNoteChanged(noteContent)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = stylingState.textColor,
                        )
                    ) {
                        Text(
                            text = "Submit",
                            style = TextStyle(
                                color = stylingState.backgroundColor,
                                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
                            )
                        )
                    }
                }
            }
        }
    }
}