package com.inkspire.ebookreader.ui.bookcontent.common

interface CombineActions {
    fun navigateToChapter(chapterIndex: Int)
    fun navigateToParagraph(chapterIndex: Int, paragraphIndex: Int)

    fun updateSystemBarVisibility()

    fun onBackClicked()

    fun onTTSActivated(firstVisibleItemIndex: Int)
    fun onPreviousChapterClicked()
    fun onNextChapterClicked()
    fun onPreviousParagraphClicked()
    fun onNextParagraphClicked()
    fun onToggleTTS()
    fun onStopTTS()

    fun onAutoScrollActivated()
    fun onStopAutoScroll()
    fun onToggleAutoScroll(isPaused: Boolean)
}