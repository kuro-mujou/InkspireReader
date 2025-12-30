package com.inkspire.ebookreader.ui.home.explore.search

import com.inkspire.ebookreader.ui.home.explore.common.SupportedWebsite

sealed interface SearchAction {
    data class ChangeSelectedWebsite(val selectedWebsite: SupportedWebsite) : SearchAction
    data class PerformSearchCategory(val category: String) : SearchAction
    data class PerformSearchQuery(val query: String) : SearchAction
    data class PerformSearchBookDetail(val bookUrl: String) : SearchAction
    data object NextPage : SearchAction
    data object PreviousPage : SearchAction
    data object ClearResult : SearchAction
}