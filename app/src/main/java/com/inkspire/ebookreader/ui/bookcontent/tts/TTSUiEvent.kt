package com.inkspire.ebookreader.ui.bookcontent.tts

sealed interface TTSUiEvent {
    data object StopReading : TTSUiEvent
}