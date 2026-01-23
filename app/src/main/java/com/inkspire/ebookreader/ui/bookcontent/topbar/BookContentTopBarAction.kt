package com.inkspire.ebookreader.ui.bookcontent.topbar

sealed interface BookContentTopBarAction {
    data object ChangeTopBarVisibility : BookContentTopBarAction
    data class ShowFindAndReplace(val show: Boolean) : BookContentTopBarAction
    data class ShowHighlightList(val show: Boolean) : BookContentTopBarAction
    data class ShowSearchResultsSheet(val show: Boolean) : BookContentTopBarAction
}