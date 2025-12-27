package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

data class BookChapterContentState(
    val enablePagerScroll: Boolean = true,
    val enableUndoButton: Boolean = false,
    val currentChapterIndex: Int = -1,
    val firstVisibleItemIndex: Int = -1,
    val lastVisibleItemIndex: Int = -1,
    val screenHeight: Int = 0,
)