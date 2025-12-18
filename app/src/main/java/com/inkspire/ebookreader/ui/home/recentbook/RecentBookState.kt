package com.inkspire.ebookreader.ui.home.recentbook

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book

data class RecentBookState(
    val recentBookState: UiState<List<Book>> = UiState.None
)
