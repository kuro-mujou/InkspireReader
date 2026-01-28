package com.inkspire.ebookreader.ui.bookcontent.common

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

class TooltipPopupPositionProvider () : PopupPositionProvider {
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