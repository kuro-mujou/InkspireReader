package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class MusicPreferences(
    val enable: Boolean = false,
    val onlyRunWithTTS: Boolean = true,
    val volume: Float = 1f,
)
