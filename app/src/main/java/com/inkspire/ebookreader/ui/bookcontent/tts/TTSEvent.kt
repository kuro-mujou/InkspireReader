package com.inkspire.ebookreader.ui.bookcontent.tts

sealed interface TTSEvent {
    data object CheckPlayPreviousChapter : TTSEvent
    data object CheckPlayNextChapter : TTSEvent
    data object CheckPlayNextParagraph: TTSEvent
    data object StopReading : TTSEvent
    data class OnRangeStart(val charOffset: Int) : TTSEvent
}