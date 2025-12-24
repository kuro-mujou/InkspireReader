package com.inkspire.ebookreader.ui.bookcontent.tts

data class TTSPlaybackState(
    val chapterIndex: Int = -1,
    val paragraphIndex: Int = -1,
    val charOffset: Int = 0,
    val isSpeaking: Boolean = false,
    val isPaused: Boolean = false,
    val isLoading: Boolean = true,
    val currentParagraphText: String = ""
)