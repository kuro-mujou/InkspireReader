package com.inkspire.ebookreader.ui.bookcontent.topbar

sealed interface BookContentTopBarAction {
    data object ChangeTopBarVisibility : BookContentTopBarAction
}