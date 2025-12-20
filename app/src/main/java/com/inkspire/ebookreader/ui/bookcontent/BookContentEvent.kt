package com.inkspire.ebookreader.ui.bookcontent

sealed class UiEvent {
    data class FetchedChapterContent(val chapterIndex: Int, val paragraphIndex: Int)
}