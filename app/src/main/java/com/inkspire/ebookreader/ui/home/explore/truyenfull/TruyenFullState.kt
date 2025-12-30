package com.inkspire.ebookreader.ui.home.explore.truyenfull

import com.inkspire.ebookreader.common.ScrapedSearchResult
import com.inkspire.ebookreader.common.UiState

data class TruyenFullState(
    val searchResult: UiState<List<ScrapedSearchResult>> = UiState.None,
    val currentPage: Int = 1,
    val maxPage: Int = 1,
    val currentQuery: String? = null,
    val currentCategory: String? = null
)
