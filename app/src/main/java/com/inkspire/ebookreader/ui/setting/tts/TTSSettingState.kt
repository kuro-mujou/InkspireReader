package com.inkspire.ebookreader.ui.setting.tts

import android.speech.tts.Voice
import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.ui.setting.tts.common.TTSSettingScreenType
import java.util.Locale

@Immutable
data class TTSSettingState(
    val languages: List<Locale> = emptyList(),
    val voices: List<Voice> = emptyList(),
    val selectedScreenType: TTSSettingScreenType = TTSSettingScreenType.NORMAL_SETTING
)
