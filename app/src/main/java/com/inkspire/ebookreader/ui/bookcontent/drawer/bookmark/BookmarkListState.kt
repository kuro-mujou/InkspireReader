package com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark

import com.inkspire.ebookreader.common.BookmarkStyle

data class BookmarkListState(
    val bookmarkThemeSettingVisibility: Boolean = false,
    val enableUndoDeleteBookmark: Boolean = false,
    val undoBookmarkList: List<Int> = emptyList(),
    val selectedBookmarkStyle: BookmarkStyle = BookmarkStyle.WAVE_WITH_BIRDS,
)
