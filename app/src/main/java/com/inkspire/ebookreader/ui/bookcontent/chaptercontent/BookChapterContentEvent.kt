package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

sealed interface BookChapterContentEvent {
    data class ScrollToChapter(val page: Int) : BookChapterContentEvent
    data class AnimatedScrollToChapter(val page: Int) : BookChapterContentEvent
    data class ScrollToParagraph(val page: Int, val paragraphIndex: Int) : BookChapterContentEvent
}