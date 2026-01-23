package com.inkspire.ebookreader.ui.bookcontent.root

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.model.HighlightResult
import com.inkspire.ebookreader.domain.model.SearchResult
import com.inkspire.ebookreader.domain.model.TableOfContent

@Immutable
data class BookContentDataState(
    val bookState: UiState<Book> = UiState.None,
    val tableOfContentState: UiState<List<TableOfContent>> = UiState.None,
    val chapterStates: Map<Int, UiState<Chapter>> = emptyMap(),
    val searchResults: List<SearchResult> = emptyList(),
    val allHighlights: List<HighlightResult> = emptyList()
)