package com.inkspire.ebookreader.ui.bookcontent.colorpicker

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import com.inkspire.ebookreader.ui.bookcontent.colorpicker.detectMotionEvents

fun Modifier.pointerMotionEvents(
    key1: Any? = Unit,
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L
) = this.then(
    Modifier.pointerInput(key1) {
        detectMotionEvents(onDown, onMove, onUp, delayAfterDownInMillis)
    }
)
