package com.inkspire.ebookreader.ui.home.explore.truyenfull

sealed interface TruyenFullAction {
    data class PerformSearchCategory(val category: String) : TruyenFullAction
    data class PerformSearchQuery(val query: String) : TruyenFullAction
}