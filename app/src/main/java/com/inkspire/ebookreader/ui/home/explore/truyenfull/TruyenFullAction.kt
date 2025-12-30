package com.inkspire.ebookreader.ui.home.explore.truyenfull

sealed interface TruyenFullAction {
    data class PerformSearchCategory(val category: String) : TruyenFullAction
    data class PerformSearchQuery(val query: String) : TruyenFullAction

    data object NextPage : TruyenFullAction
    data object PreviousPage : TruyenFullAction
    data object ClearResult : TruyenFullAction
}