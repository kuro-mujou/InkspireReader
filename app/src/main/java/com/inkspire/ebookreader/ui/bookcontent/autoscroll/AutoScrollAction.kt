package com.inkspire.ebookreader.ui.bookcontent.autoscroll

sealed interface AutoScrollAction {
    data class UpdateIsPaused(val isPaused: Boolean) : AutoScrollAction
    data class UpdateIsActivated(val isActivated: Boolean) : AutoScrollAction
    data class UpdateIsScrolledToEnd(val isScrolledToEnd: Boolean) : AutoScrollAction
    data class UpdateIsAnimationRunning(val isAnimationRunning: Boolean) : AutoScrollAction
}