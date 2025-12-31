package com.inkspire.ebookreader.ui.bookcontent.tts

sealed interface TTSEvent {
    data object CheckPlayPreviousChapter : TTSEvent
    data object CheckPlayNextChapter : TTSEvent
    data object CheckPlayNextParagraph: TTSEvent
    data object CheckPauseReading : TTSEvent
    data object CheckResumeReading : TTSEvent
    data object StopReading : TTSEvent
    data class OnRangeStart(val startOffset: Int, val endOffset: Int) : TTSEvent
    data class OnReadOffset(val offset: Int) : TTSEvent
}