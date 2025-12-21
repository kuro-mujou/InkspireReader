package com.inkspire.ebookreader.ui.bookcontent.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

@Composable
fun customPopupPositionProvider(): PopupPositionProvider {
    val tooltipAnchorSpacing = 0
    return remember(tooltipAnchorSpacing) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val x = anchorBounds.topRight.x
                val y =
                    if (anchorBounds.height > popupContentSize.height)
                        anchorBounds.topRight.y + popupContentSize.height / 2
                    else
                        anchorBounds.topRight.y
                return IntOffset(x, y)
            }
        }
    }
}