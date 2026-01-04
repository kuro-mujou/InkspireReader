package com.inkspire.ebookreader.ui.home.libary

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.BookWithCategoriesModel
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.model.LibrarySettingPreferences

@Immutable
data class LibraryState(
    val librarySettings: LibrarySettingPreferences = LibrarySettingPreferences(),

    val isOnDeletingBooks: Boolean = false,
    val bookList: UiState<List<BookWithCategoriesModel>> = UiState.None,
    val selectedBookList: List<Book> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val bottomSheetVisibility: Boolean = false,
    val fabVisibility: Boolean = true,
    val fabExpanded: Boolean = false,
)