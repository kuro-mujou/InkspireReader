package com.inkspire.ebookreader.ui.home.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun CustomPopupNoDefaultMenu() {
    // 1. Controls for our custom popup
    var popupRect by remember { mutableStateOf<Rect?>(null) }
    var isPopupVisible by remember { mutableStateOf(false) }

    // 2. The magic part: A toolbar that does NOTHING system-side
    val customTextToolbar = remember {
        object : TextToolbar {
            override val status: TextToolbarStatus
                get() = if (isPopupVisible) TextToolbarStatus.Shown else TextToolbarStatus.Hidden

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                // Instead of calling the system, we just update our own state
                popupRect = rect
                isPopupVisible = true
            }

            override fun hide() {
                isPopupVisible = false
            }
        }
    }

    CompositionLocalProvider(LocalTextToolbar provides customTextToolbar) {
        // 3. Your text content
        Box(modifier = Modifier.fillMaxSize().padding(40.dp)) {
            SelectionContainer {
                Text(
                    text = "Select me! The default Android Copy/Paste menu is strictly forbidden here. Only the custom box will appear.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // 4. Your Custom Popup
            if (isPopupVisible && popupRect != null) {
                Popup(
                    // Align the popup near the selection
                    offset = IntOffset(
                        x = popupRect!!.left.toInt(),
                        y = popupRect!!.top.toInt() - 100 // Move it slightly above selection
                    ),
                    onDismissRequest = { isPopupVisible = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    // This is the ONLY thing that will show up
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.DarkGray,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text("My Custom Action", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}