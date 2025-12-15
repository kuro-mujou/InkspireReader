package com.inkspire.ebookreader.ui.bookcontent

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book

data class BookContentState(
    val bookState: UiState<Book> = UiState.Loading
)
