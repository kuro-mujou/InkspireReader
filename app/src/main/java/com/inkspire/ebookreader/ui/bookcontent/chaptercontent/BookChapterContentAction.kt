package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

sealed interface BookChapterContentAction {
    data class InitFromDatabase(val chapter: Int, val paragraph: Int) : BookChapterContentAction
    data class UpdateCurrentChapterIndex(val index: Int) : BookChapterContentAction
    data class UpdateFirstVisibleItemIndex(val index: Int) : BookChapterContentAction
    data class UpdateLastVisibleItemIndex(val index: Int) : BookChapterContentAction
    data class UpdateScreenHeight(val screenHeight: Int) : BookChapterContentAction
    data class UpdateEnableUndoButton(val enable: Boolean) : BookChapterContentAction
    data class UpdateEnablePagerScroll(val enable: Boolean) : BookChapterContentAction
    data class RequestScrollToChapter(val index: Int) : BookChapterContentAction
    data class RequestAnimatedScrollToChapter(val index: Int) : BookChapterContentAction
    data class RequestScrollToParagraph(val chapterIndex: Int, val paragraphIndex: Int) : BookChapterContentAction
}