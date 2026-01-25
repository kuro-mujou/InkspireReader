package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

class SelectionPopupPositionProvider(
    private val selectionBoundsInWindow: Rect,
    private val margin: Int = 20
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val selectionCenterX = selectionBoundsInWindow.center.x.toInt()
        var popupX = selectionCenterX - (popupContentSize.width / 2)

        if (popupX < 0) popupX = 10
        if (popupX + popupContentSize.width > windowSize.width) {
            popupX = windowSize.width - popupContentSize.width - 10
        }

        val selectionTop = selectionBoundsInWindow.top.toInt()
        val selectionBottom = selectionBoundsInWindow.bottom.toInt()

        val potentialTopY = selectionTop - popupContentSize.height - margin
        val popupY = if (potentialTopY >= 0) {
            potentialTopY
        } else {
            selectionBottom + margin
        }

        return IntOffset(popupX, popupY)
    }
}