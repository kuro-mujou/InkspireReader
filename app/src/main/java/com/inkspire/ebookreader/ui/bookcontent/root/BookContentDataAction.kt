package com.inkspire.ebookreader.ui.bookcontent.root

import com.inkspire.ebookreader.domain.model.HighlightToInsert

sealed interface BookContentDataAction {
    data class LoadChapter(val index: Int) : BookContentDataAction

    data class UpdateRecentChapterToDB(val chapterIndex: Int) : BookContentDataAction
    data class UpdateRecentParagraphToDB(val paragraphIndex: Int) : BookContentDataAction
    data class AddHighlightForParagraph(val highlightInfo: HighlightToInsert) : BookContentDataAction
    data class DeleteHighlightRange(val tocId: Int, val paragraphIndex: Int, val start: Int, val end: Int) : BookContentDataAction
    data class EditParagraphContent(
        val tocId: Int,
        val paragraphIndex: Int,
        val selectionStart: Int,
        val selectionEnd: Int,
        val replacementText: String
    ) : BookContentDataAction
    data class AddHiddenText(val text: String) : BookContentDataAction
}