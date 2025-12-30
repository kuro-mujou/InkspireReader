package com.inkspire.ebookreader.ui.home.explore.search

import com.inkspire.ebookreader.common.ScrapedSearchResult
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.ui.home.explore.common.SupportedWebsite

data class SearchState (
    val selectedWebsite: SupportedWebsite = SupportedWebsite.TRUYEN_FULL,
    val searchResult: UiState<List<ScrapedSearchResult>> = UiState.None,
    val currentPage: Int = 1,
    val maxPage: Int = 1,
    val currentQuery: String? = null,
    val currentCategory: String? = null
)