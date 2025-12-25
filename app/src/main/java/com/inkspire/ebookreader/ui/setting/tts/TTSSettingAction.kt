package com.inkspire.ebookreader.ui.setting.tts

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import com.inkspire.ebookreader.ui.setting.tts.common.TTSSettingScreenType
import java.util.Locale

sealed interface TTSSettingAction {
    data class UpdateLanguage(val language: Locale?, val tts: TextToSpeech) : TTSSettingAction
    data class UpdateVoice(val voice: Voice?) : TTSSettingAction
    data class UpdateSpeed(val speed: Float) : TTSSettingAction
    data class UpdatePitch(val pitch: Float) : TTSSettingAction
    data class UpdateScreenType(val screenType: TTSSettingScreenType) : TTSSettingAction
}