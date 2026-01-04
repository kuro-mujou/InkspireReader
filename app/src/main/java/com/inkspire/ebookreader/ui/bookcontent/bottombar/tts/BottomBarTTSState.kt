package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

import androidx.compose.runtime.Immutable

@Immutable
data class BottomBarTTSState(
    val ttsMusicMenuVisibility: Boolean = false,
    val ttsVoiceMenuVisibility: Boolean = false,
)
