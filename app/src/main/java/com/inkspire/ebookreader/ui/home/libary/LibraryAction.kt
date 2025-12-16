package com.inkspire.ebookreader.ui.home.libary

import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Category

sealed interface LibraryAction {
    data class ChangeChipState(val categoryChip: Category): LibraryAction
    data class UpdateBookFavoriteState(val book: Book): LibraryAction
    data class AddSelectedBook(val book: Book): LibraryAction
    data class RemoveSelectedBook(val book: Book): LibraryAction
    data class DeleteSelectedBooks(val book: Book): LibraryAction
    data object ResetChipState: LibraryAction
    data object UpdateBookListType: LibraryAction
    data object UpdateSortState: LibraryAction
    data object UpdateDeletingState: LibraryAction
    data object ConfirmDeleteBooks: LibraryAction
}