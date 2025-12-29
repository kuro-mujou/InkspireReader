package com.inkspire.ebookreader.ui.home.explore.truyenfull

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book

data class TruyenFullState(
    val searchResult: UiState<List<Book>> = UiState.None
)
