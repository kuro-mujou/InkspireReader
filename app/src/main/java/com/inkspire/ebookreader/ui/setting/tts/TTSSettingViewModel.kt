package com.inkspire.ebookreader.ui.setting.tts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.TTSSettingDataStoreUseCase
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class TTSSettingViewModel(
    private val ttsManager: TTSManager,
    private val datastoreUseCase: TTSSettingDataStoreUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TTSSettingState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    init {
        viewModelScope.launch {
            val (languages, voices) = ttsManager.getAvailableVoicesAndLanguages()
            _state.update {
                it.copy(
                    languages = languages,
                    voices = voices
                )
            }
        }
    }

    fun onAction(action: TTSSettingAction) {
        when (action) {
            is TTSSettingAction.UpdateLanguage -> {
                viewModelScope.launch {
                    datastoreUseCase.setTTSLocale(action.language?.displayName.toString())
                    val selectedVoice = action.tts.voices?.find {
                        it.locale == action.language
                    } ?: action.tts.defaultVoice
                    datastoreUseCase.setTTSVoice(selectedVoice.name)
                    ttsManager.updateLanguage(action.language ?: Locale.getDefault())
                }
            }
            is TTSSettingAction.UpdatePitch -> {
                viewModelScope.launch {
                    datastoreUseCase.setTTSPitch(action.pitch)
                    ttsManager.updatePitch(action.pitch)
                }
            }
            is TTSSettingAction.UpdateSpeed -> {
                viewModelScope.launch {
                    datastoreUseCase.setTTSSpeed(action.speed)
                    ttsManager.updateSpeed(action.speed)
                }
            }
            is TTSSettingAction.UpdateVoice -> {
                viewModelScope.launch {
                    if (action.voice != null) {
                        datastoreUseCase.setTTSVoice(action.voice.name)
                        ttsManager.updateVoice(action.voice)
                    }
                }
            }

            is TTSSettingAction.UpdateScreenType -> {
                _state.update {
                    it.copy(selectedScreenType = action.screenType)
                }
            }
        }
    }
}