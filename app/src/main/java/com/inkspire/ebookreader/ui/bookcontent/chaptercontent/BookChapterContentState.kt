package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class BookChapterContentState(
    val enablePagerScroll: Boolean = true,
    val enableUndoButton: Boolean = false,
    val currentChapterIndex: Int = -1,
    val firstVisibleItemIndex: Int = -1,
    val lastVisibleItemIndex: Int = -1,
    val screenHeight: Int = 0,
    val globalMagnifierCenter: Offset = Offset.Unspecified,
    val activeSelectionIndex: Int? = null
)