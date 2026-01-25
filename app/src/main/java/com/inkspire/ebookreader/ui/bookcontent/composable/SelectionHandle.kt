package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun SelectionHandle(
    position: Offset,
    isStartHandle: Boolean,
    color: Color,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val handleSize = 25.dp
    val density = LocalDensity.current

    val xOffset = with(density) {
        if (isStartHandle) {
            position.x.toDp() - handleSize
        } else {
            position.x.toDp()
        }
    }

    val yOffset = with(density) { position.y.toDp() }

    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(handleSize)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { currentOnDragStart() },
                    onDragEnd = { currentOnDragEnd() },
                    onDragCancel = { currentOnDragEnd() }
                ) { change, dragAmount ->
                    change.consume()
                    currentOnDrag(dragAmount)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                if (isStartHandle) {
                    moveTo(size.width, 0f)
                    lineTo(size.width, size.width / 2)
                    arcTo(
                        rect = Rect(0f, 0f, size.width, size.height),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 270f,
                        forceMoveTo = false
                    )
                    lineTo(size.width, 0f)
                } else {
                    moveTo(0f, 0f)
                    lineTo(size.width / 2, 0f)
                    arcTo(
                        rect = Rect(0f, 0f, size.width, size.height),
                        startAngleDegrees = -90f,
                        sweepAngleDegrees = 270f,
                        forceMoveTo = false
                    )
                    lineTo(0f, 0f)
                }
            }
            drawPath(path, color)
        }
    }
}