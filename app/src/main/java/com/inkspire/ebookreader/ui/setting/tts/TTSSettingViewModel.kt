package com.inkspire.ebookreader.ui.setting.tts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.TTSSettingDataStoreUseCase
import com.inkspire.ebookreader.service.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class TTSSettingViewModel(
    private val ttsManager: TTSManager,
    private val dataStoreUseCase: TTSSettingDataStoreUseCase
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
            combine(
                dataStoreUseCase.getTtsSpeed(),
                dataStoreUseCase.getTtsPitch(),
                dataStoreUseCase.getTtsLocale(),
                dataStoreUseCase.getTtsVoice()
            ) { speed, pitch, localeName, voiceName ->
                val selectedLocale = _state.value.languages.find { it.displayName == localeName }
                val selectedVoice = _state.value.voices.find {
                    it.name == voiceName && it.locale == selectedLocale
                } ?: _state.value.voices.firstOrNull { it.locale == selectedLocale }

                _state.update { it.copy(
                    currentSpeed = speed,
                    currentPitch = pitch,
                    currentLanguage = selectedLocale,
                    currentVoice = selectedVoice
                )}
            }.collect()
        }
    }

    fun onAction(action: TTSSettingAction) {
        when (action) {
            is TTSSettingAction.FixNullVoice -> {
                viewModelScope.launch {
                    var selectedVoice = action.tts.voices?.find {
                        it.locale == _state.value.currentLanguage
                    }
                    if (selectedVoice == null) {
                        selectedVoice = action.tts.voices?.firstOrNull {
                            it.locale == _state.value.currentLanguage
                        } ?: action.tts.defaultVoice
                    }
                    dataStoreUseCase.setTTSVoice(selectedVoice.name)
                    ttsManager.updateVoice(selectedVoice)
                    _state.update {
                        it.copy(currentVoice = selectedVoice)
                    }
                }
            }
            is TTSSettingAction.UpdateLanguage -> {
                viewModelScope.launch {
                    dataStoreUseCase.setTTSLocale(action.language?.displayName.toString())
                    dataStoreUseCase.setTTSVoice("")
                    ttsManager.updateLanguage(action.language ?: Locale.getDefault())
                    _state.update {
                        it.copy(currentLanguage = action.language)
                    }
                }
            }
            is TTSSettingAction.UpdatePitch -> {
                viewModelScope.launch {
                    dataStoreUseCase.setTTSPitch(action.pitch)
                    ttsManager.updatePitch(action.pitch)
                }
            }
            is TTSSettingAction.UpdateSpeed -> {
                viewModelScope.launch {
                    dataStoreUseCase.setTTSSpeed(action.speed)
                    ttsManager.updateSpeed(action.speed)
                }
            }
            is TTSSettingAction.UpdateVoice -> {
                viewModelScope.launch {
                    if (action.voice != null) {
                        dataStoreUseCase.setTTSVoice(action.voice.name)
                        ttsManager.updateVoice(action.voice)
                    }
                    _state.update {
                        it.copy(currentVoice = action.voice)
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