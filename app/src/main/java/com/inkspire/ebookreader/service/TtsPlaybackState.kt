package com.inkspire.ebookreader.service

data class TtsPlaybackState(
    val chapterIndex: Int = -1,
    val paragraphIndex: Int = -1,
    val charOffset: Int = 0,
    val isSpeaking: Boolean = false,
    val isPaused: Boolean = false,
    val isLoading: Boolean = true,
    val currentParagraphText: String = ""
)
