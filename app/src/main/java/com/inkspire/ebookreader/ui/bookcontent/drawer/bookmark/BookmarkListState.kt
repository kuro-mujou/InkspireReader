package com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.ReaderSettingPreferences

@Immutable
data class BookmarkListState(
    val readerSettings: ReaderSettingPreferences = ReaderSettingPreferences(),

    val bookmarkThemeSettingVisibility: Boolean = false,
    val enableUndoDeleteBookmark: Boolean = false,
    val undoBookmarkList: List<Int> = emptyList(),
)
