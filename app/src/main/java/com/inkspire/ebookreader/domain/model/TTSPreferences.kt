package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable
import java.util.Locale

@Immutable
data class TTSPreferences (
    val speed: Float = 1f,
    val pitch: Float = 1f,
    val locale: String = Locale.getDefault().displayName,
    val voice: String = "",
)