package com.inkspire.ebookreader.ui.bookcontent.bottombar

sealed interface BookContentBottomBarAction {
    data object ChangeBottomBarVisibility : BookContentBottomBarAction
    data object AutoScrollIconClicked : BookContentBottomBarAction
    data object SettingIconClicked : BookContentBottomBarAction
    data object ThemeIconClicked : BookContentBottomBarAction
    data object TtsIconClicked : BookContentBottomBarAction
    data object ResetBottomBarMode : BookContentBottomBarAction
}