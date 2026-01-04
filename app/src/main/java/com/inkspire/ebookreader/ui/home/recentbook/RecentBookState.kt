package com.inkspire.ebookreader.ui.home.recentbook

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book

@Immutable
data class RecentBookState(
    val recentBookState: UiState<List<Book>> = UiState.None
)
