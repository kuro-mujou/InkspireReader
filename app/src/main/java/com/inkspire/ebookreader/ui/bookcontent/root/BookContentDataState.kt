package com.inkspire.ebookreader.ui.bookcontent.root

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.model.TableOfContent

data class BookContentDataState(
    val bookState: UiState<Book> = UiState.None,
    val tableOfContentState: UiState<List<TableOfContent>> = UiState.None,
    val chapterStates: Map<Int, UiState<Chapter>> = emptyMap(),
)