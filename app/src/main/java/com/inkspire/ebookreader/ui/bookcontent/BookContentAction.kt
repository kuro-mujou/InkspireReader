package com.inkspire.ebookreader.ui.bookcontent

sealed interface BookContentAction {
    data class LoadChapter(val index: Int) : BookContentAction

    data class UpdateCurrentChapter(val index: Int) : BookContentAction
    data class UpdateFirstVisibleItemIndex(val index: Int) : BookContentAction
    data class UpdateLastVisibleItemIndex(val index: Int) : BookContentAction

    data class UpdateRecentChapterToDB(val chapterIndex: Int) : BookContentAction
    data class UpdateRecentParagraphToDB(val paragraphIndex: Int) : BookContentAction
}