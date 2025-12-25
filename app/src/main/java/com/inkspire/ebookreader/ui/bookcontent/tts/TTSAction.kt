package com.inkspire.ebookreader.ui.bookcontent.tts

import com.inkspire.ebookreader.domain.model.Book

sealed interface TTSAction {
    data class SetBookInfo(val bookInfo: Book) : TTSAction
    data class UpdateCurrentChapterData(val chapterIndexToLoadData: Int, val realCurrentChapterIndex: Int) : TTSAction

    data class StartTTS(val paragraphIndex: Int) : TTSAction
    data object OnPlayPreviousChapterClick : TTSAction
    data object OnPlayNextChapterClick : TTSAction
    data object OnPlayPreviousParagraphClick : TTSAction
    data object OnPlayNextParagraphClick : TTSAction
    data object OnPlayPauseClick : TTSAction
    data object OnStopClick : TTSAction
}