package com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark

sealed interface BookmarkListAction {
    data class DeleteBookmark(val bookId: String, val index: Int) : BookmarkListAction
    data class UndoDeleteBookmark(val bookId: String) : BookmarkListAction
    data object UpdateBookmarkThemeSettingVisibility : BookmarkListAction
}