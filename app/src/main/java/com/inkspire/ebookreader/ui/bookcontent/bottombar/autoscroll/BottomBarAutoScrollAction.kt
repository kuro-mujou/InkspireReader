package com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll

sealed interface BottomBarAutoScrollAction {
    data object ChangeSettingVisibility : BottomBarAutoScrollAction
}