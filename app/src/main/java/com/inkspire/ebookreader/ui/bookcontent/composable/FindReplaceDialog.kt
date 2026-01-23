package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun FindReplaceDialog(
    stylingState: StylingState,
    initialFindText: String = "",
    onDismiss: () -> Unit,
    onFind: (String, Boolean) -> Unit,
    onReplace: (String, String, Boolean) -> Unit
) {
    var findText by remember { mutableStateOf(initialFindText) }
    var replaceText by remember { mutableStateOf("") }
    var isReplaceMode by remember { mutableStateOf(false) }
    var isCaseSensitive by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(targetValue = if (isReplaceMode) 180f else 0f)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = stylingState.stylePreferences.backgroundColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isReplaceMode) "Find & Replace" else "Find in Book",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = stylingState.stylePreferences.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = findText,
                            onValueChange = { findText = it },
                            label = { Text("Find") },
                            textStyle = TextStyle(color = stylingState.stylePreferences.textColor),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = stylingState.stylePreferences.textColor,
                                unfocusedBorderColor = stylingState.stylePreferences.textColor,
                                focusedLabelColor = stylingState.stylePreferences.textColor,
                                unfocusedLabelColor = stylingState.stylePreferences.textColor
                            )
                        )

                        IconButton(
                            onClick = { isReplaceMode = !isReplaceMode },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_down),
                                contentDescription = "Toggle Replace",
                                tint = stylingState.stylePreferences.textColor,
                                modifier = Modifier.rotate(rotationState)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isReplaceMode,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = replaceText,
                            onValueChange = { replaceText = it },
                            label = { Text("Replace with") },
                            textStyle = TextStyle(color = stylingState.stylePreferences.textColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = stylingState.stylePreferences.textColor,
                                unfocusedBorderColor = stylingState.stylePreferences.textColor,
                                focusedLabelColor = stylingState.stylePreferences.textColor,
                                unfocusedLabelColor = stylingState.stylePreferences.textColor
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Checkbox(
                            checked = isCaseSensitive,
                            onCheckedChange = { isCaseSensitive = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = stylingState.stylePreferences.textColor,
                                uncheckedColor = stylingState.stylePreferences.textColor,
                                checkmarkColor = stylingState.stylePreferences.backgroundColor
                            )
                        )
                        Text(
                            text = "Match Case",
                            style = TextStyle(color = stylingState.stylePreferences.textColor),
                            modifier = Modifier.clickable { isCaseSensitive = !isCaseSensitive }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = stylingState.stylePreferences.textColor)
                    }
                    if (findText.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (isReplaceMode) {
                                    onReplace(findText, replaceText, isCaseSensitive)
                                } else {
                                    onFind(findText, isCaseSensitive)
                                }
                            },
                            enabled = findText.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = stylingState.stylePreferences.textColor)
                        ) {
                            Text(
                                text = if (isReplaceMode) "Replace All" else "Find",
                                color = stylingState.stylePreferences.backgroundColor
                            )
                        }
                    }
                }
            }
        }
    }
}