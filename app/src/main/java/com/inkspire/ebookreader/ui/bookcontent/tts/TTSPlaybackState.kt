package com.inkspire.ebookreader.ui.bookcontent.tts

import android.speech.tts.Voice
import androidx.compose.ui.text.TextRange
import java.util.Locale

data class TTSPlaybackState(
    //tts info
    val currentSpeed : Float = 1f,
    val currentPitch : Float = 1f,
    val currentLanguage: Locale? = null,
    val currentVoice: Voice? = null,
    val chapterIndex: Int = -1,
    val paragraphIndex: Int = -1,
    val currentWordRange: TextRange = TextRange.Zero,

    //content state
    val currentParagraphText: String = "",
    val chapterText: List<String> = emptyList(),

    //playback state
    val isActivated: Boolean = false,
    val isPaused: Boolean = false,
)