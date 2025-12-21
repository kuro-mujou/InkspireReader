package com.inkspire.ebookreader.ui.setting.tts

import android.speech.tts.Voice
import com.inkspire.ebookreader.ui.setting.tts.common.TTSSettingScreenType
import java.util.Locale

data class TTSSettingState(
    val currentPitch: Float = 1f,
    val currentSpeed: Float = 1f,
    val currentLanguage: Locale? = null,
    val currentVoice: Voice? = null,
    val languages: List<Locale> = emptyList(),
    val voices: List<Voice> = emptyList(),
    val selectedScreenType: TTSSettingScreenType = TTSSettingScreenType.NORMAL_SETTING
)
