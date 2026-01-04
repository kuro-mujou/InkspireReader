package com.inkspire.ebookreader.ui.bookcontent.tts

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.MusicPreferences
import com.inkspire.ebookreader.domain.model.TTSPreferences

@Immutable
data class TTSPlaybackState(
    val ttsPreferences: TTSPreferences = TTSPreferences(),
    val musicPreferences: MusicPreferences = MusicPreferences(),
    val currentVoiceQuality: String = "",

    val chapterIndex: Int = -1,
    val paragraphIndex: Int = -1,
    val currentParagraphText: String = "",
    val chapterText: List<String> = emptyList(),
    val isActivated: Boolean = false,
    val isPaused: Boolean = false,
)