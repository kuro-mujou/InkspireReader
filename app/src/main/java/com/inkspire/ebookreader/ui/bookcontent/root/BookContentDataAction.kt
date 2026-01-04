package com.inkspire.ebookreader.ui.bookcontent.root

sealed interface BookContentDataAction {
    data class LoadChapter(val index: Int) : BookContentDataAction

    data class UpdateRecentChapterToDB(val chapterIndex: Int) : BookContentDataAction
    data class UpdateRecentParagraphToDB(val paragraphIndex: Int) : BookContentDataAction
}