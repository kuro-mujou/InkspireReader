package com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll

sealed interface BottomBarAutoScrollAction {
    data object StopIconClicked : BottomBarAutoScrollAction
    data class PlayPauseIconClicked(val isPaused: Boolean) : BottomBarAutoScrollAction
    data object ChangeSettingVisibility : BottomBarAutoScrollAction
}