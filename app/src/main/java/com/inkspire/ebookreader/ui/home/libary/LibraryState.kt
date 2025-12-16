package com.inkspire.ebookreader.ui.home.libary

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Category

data class LibraryState(
    val isSortedByFavorite: Boolean = false,
    val listViewType: Int = -1,
    val isOnDeletingBooks: Boolean = false,

    val bookList: UiState<List<Book>> = UiState.Loading,
    val selectedBookList: List<Book> = emptyList(),
    val categories: List<Category> = emptyList(),
)