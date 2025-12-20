package com.inkspire.ebookreader.ui.bookcontent

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Chapter

data class BookContentState(
    val bookState: UiState<Book> = UiState.None,
    val chapterStates: Map<Int, UiState<Chapter>> = emptyMap()
)