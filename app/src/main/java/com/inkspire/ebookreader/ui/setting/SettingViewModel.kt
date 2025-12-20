package com.inkspire.ebookreader.ui.setting

import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.model.SettingState
import com.inkspire.ebookreader.domain.usecase.SettingDatastoreUseCase
import com.inkspire.ebookreader.domain.usecase.SettingUseCase
import com.inkspire.ebookreader.service.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class SettingViewModel(
    private val settingUseCase: SettingUseCase,
    private val settingDatastoreUseCase: SettingDatastoreUseCase,
    private val ttsManager: TTSManager
) : ViewModel() {
    private val _state = MutableStateFlow(SettingState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: SettingAction) {
        when (action) {
            is SettingAction.OpenTTSVoiceMenu -> {
                _state.update { it.copy(openTTSVoiceMenu = action.open) }
            }

            is SettingAction.OpenAutoScrollMenu -> {
                _state.update { it.copy(openAutoScrollMenu = action.open) }
            }

            is SettingAction.KeepScreenOn -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setKeepScreenOn(action.keepScreenOn)
                }
            }

            is SettingAction.UpdateVoice -> {
                viewModelScope.launch {
                    if (action.voice != null) {
                        settingDatastoreUseCase.setTTSVoice(action.voice.name)
                        ttsManager.updateVoice(action.voice)
                    }
                    _state.update {
                        it.copy(currentVoice = action.voice)
                    }
                }
            }

            is SettingAction.UpdateLanguage -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setTTSLocale(action.language?.displayName.toString())
                    ttsManager.updateLanguage(action.language ?: Locale.getDefault())
                    _state.update {
                        it.copy(currentLanguage = action.language)
                    }
                }
            }

            is SettingAction.UpdateSpeed -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setTTSSpeed(action.speed)
                    ttsManager.updateSpeed(action.speed)
                }
            }

            is SettingAction.UpdatePitch -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setTTSPitch(action.pitch)
                    ttsManager.updatePitch(action.pitch)
                }
            }

            is SettingAction.FixNullVoice -> {
                fixNullVoice(action.tts)
            }

            is SettingAction.UpdateSelectedBookmarkStyle -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setBookmarkStyle(action.style)
                }
            }

            is SettingAction.OnEnableBackgroundMusicChange -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setEnableBackgroundMusic(action.enable)
                }
            }

            is SettingAction.OnPlayerVolumeChange -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setPlayerVolume(action.volume)
                }
            }

            is SettingAction.UpdateAutoResumeScrollMode -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setAutoScrollResumeMode(action.autoResume)
                }
            }

            is SettingAction.UpdateDelayAtEnd -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setDelayTimeAtEnd(action.delay)
                }
            }

            is SettingAction.UpdateDelayAtStart -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setDelayTimeAtStart(action.delay)
                }
            }

            is SettingAction.UpdateDelayResumeMode -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setAutoScrollResumeDelayTime(action.delay)
                }
            }

            is SettingAction.UpdateScrollSpeed -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setAutoScrollSpeed(action.speed)
                }
            }

            is SettingAction.ChangeChipState -> {
                _state.update {
                    it.copy(
                        bookCategories = it.bookCategories.map { chip ->
                            if (chip.id == action.chip.id) {
                                chip.copy(isSelected = !chip.isSelected)
                            } else {
                                chip
                            }
                        }
                    )
                }
            }

            is SettingAction.AddCategory -> {
                viewModelScope.launch {
                    settingUseCase.insertCategory(action.category)
                }
            }

            is SettingAction.DeleteCategory -> {
                viewModelScope.launch {
                    settingUseCase.deleteCategory(
                        _state.value.bookCategories.filter { it.isSelected }
                    )
                }
            }

            is SettingAction.ResetChipState -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            bookCategories = it.bookCategories.map { category ->
                                category.copy(isSelected = false)
                            }
                        )
                    }
                }
            }

            is SettingAction.OpenSpecialCodeSuccess -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setUnlockSpecialCodeStatus(true)
                }
            }

            is SettingAction.UpdateEnableSpecialArt -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setEnableSpecialArt(action.enable)
                }
            }

            is SettingAction.OpenBackgroundMusicMenu -> {
                _state.update { it.copy(openBackgroundMusicMenu = action.open) }
            }
            is SettingAction.OpenBookmarkThemeMenu -> {
                _state.update { it.copy(openBookmarkThemeMenu = action.open) }
            }
            is SettingAction.OpenCategoryMenu -> {
                _state.update { it.copy(openCategoryMenu = action.open) }
            }
            is SettingAction.OpenSpecialCodeDialog -> {
                _state.update { it.copy(specialCodeDialog = action.open) }
            }
        }
    }

    private fun fixNullVoice(textToSpeech: TextToSpeech) {
        viewModelScope.launch {
            var selectedVoice = textToSpeech.voices?.find {
                it.locale == _state.value.currentLanguage
            }
            if (selectedVoice == null) {
                selectedVoice = textToSpeech.voices?.firstOrNull {
                    it.locale == _state.value.currentLanguage
                } ?: textToSpeech.defaultVoice
            }
            settingDatastoreUseCase.setTTSVoice(selectedVoice.name)
            ttsManager.updateVoice(selectedVoice)
            _state.update {
                it.copy(currentVoice = selectedVoice)
            }
        }
    }

    init {
        viewModelScope.launch {
            settingDatastoreUseCase.getKeepScreenOn().collectLatest { keepScreenOn ->
                _state.update { it.copy(keepScreenOn = keepScreenOn) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getTtsPitch().collectLatest { ttsPitch ->
                _state.update { it.copy(currentPitch = ttsPitch) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getTtsSpeed().collectLatest { ttsSpeed ->
                _state.update { it.copy(currentSpeed = ttsSpeed) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getBookmarkStyle().collectLatest { bookmarkStyle ->
                _state.update { it.copy(selectedBookmarkStyle = bookmarkStyle) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getEnableBackgroundMusic().collectLatest { enableBackgroundMusic ->
                _state.update { it.copy(enableBackgroundMusic = enableBackgroundMusic) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getAutoScrollSpeed().collectLatest { speed ->
                _state.update { it.copy(currentScrollSpeed = speed) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getAutoScrollResumeMode().collectLatest { autoScrollResumeMode ->
                _state.update { it.copy(isAutoResumeScrollMode = autoScrollResumeMode) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getAutoScrollResumeDelayTime().collectLatest { delay ->
                _state.update { it.copy(delayResumeMode = delay) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getDelayTimeAtEnd().collectLatest { delay ->
                _state.update { it.copy(delayAtEnd = delay) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getDelayTimeAtStart().collectLatest { delay ->
                _state.update { it.copy(delayAtStart = delay) }
            }
        }
        viewModelScope.launch {
            settingUseCase.getBookCategoryFlow().collectLatest { categories ->
                _state.update { it.copy(bookCategories = categories) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getUnlockSpecialCodeStatus().collectLatest { status->
                _state.update { it.copy(unlockSpecialCodeStatus = status) }
            }
        }
        viewModelScope.launch {
            settingDatastoreUseCase.getEnableSpecialArt().collectLatest {enable->
                _state.update {
                    it.copy(enableSpecialArt = enable)
                }
            }
        }
        viewModelScope.launch {
            val selectedLocale = ttsManager.getTTS()?.availableLanguages?.find {
                it.displayName == settingDatastoreUseCase.getTtsLocale().first()
            }
            ttsManager.updateLanguage(selectedLocale ?: Locale.getDefault())
            val selectedVoice = ttsManager.getTTS()?.voices?.find {
                it.name == settingDatastoreUseCase.getTtsVoice().first() && it.locale == selectedLocale
            } ?: ttsManager.getTTS()?.voices?.firstOrNull {
                it.locale == selectedLocale
            }
            ttsManager.getTTS()?.let {
                ttsManager.updateVoice(selectedVoice?: it.defaultVoice)
            }
            _state.update {
                it.copy(
                    currentLanguage = selectedLocale,
                    currentVoice = selectedVoice
                )
            }
        }
    }
}