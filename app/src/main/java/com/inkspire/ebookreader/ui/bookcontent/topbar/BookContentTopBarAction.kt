package com.inkspire.ebookreader.ui.bookcontent.topbar

sealed interface BookContentTopBarAction {
    data object ChangeTopBarVisibility : BookContentTopBarAction
    data object BackIconClicked : BookContentTopBarAction
    data object DrawerIconClicked : BookContentTopBarAction
    data object BookmarkIconClicked : BookContentTopBarAction
}