package com.inkspire.ebookreader.ui.setting

import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.model.SettingState
import com.inkspire.ebookreader.domain.repository.AppPreferencesRepository
import com.inkspire.ebookreader.domain.repository.BookRepository
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
    private val bookRepository: BookRepository,
    private val appPreferencesRepository: AppPreferencesRepository,
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
                _state.update {
                    it.copy(openTTSVoiceMenu = action.open)
                }
            }

            is SettingAction.OpenAutoScrollMenu -> {
                _state.update {
                    it.copy(openAutoScrollMenu = action.open)
                }
            }

            is SettingAction.KeepScreenOn -> {
                viewModelScope.launch {
                    appPreferencesRepository.setKeepScreenOn(action.keepScreenOn)
                }
            }

            is SettingAction.UpdateVoice -> {
                viewModelScope.launch {
                    if (action.voice != null) {
                        appPreferencesRepository.setTTSVoice(action.voice.name)
                        ttsManager.updateVoice(action.voice)
                    }
                    _state.update {
                        it.copy(currentVoice = action.voice)
                    }
                }
            }

            is SettingAction.UpdateLanguage -> {
                viewModelScope.launch {
                    appPreferencesRepository.setTTSLocale(action.language?.displayName.toString())
                    ttsManager.updateLanguage(action.language ?: Locale.getDefault())
                    _state.update {
                        it.copy(currentLanguage = action.language)
                    }
                }
            }

            is SettingAction.UpdateSpeed -> {
                viewModelScope.launch {
                    appPreferencesRepository.setTTSSpeed(action.speed)
                    ttsManager.updateSpeed(action.speed)
                }
            }

            is SettingAction.UpdatePitch -> {
                viewModelScope.launch {
                    appPreferencesRepository.setTTSPitch(action.pitch)
                    ttsManager.updatePitch(action.pitch)
                }
            }

            is SettingAction.FixNullVoice -> {
                fixNullVoice(action.tts)
            }

            is SettingAction.UpdateSelectedBookmarkStyle -> {
                viewModelScope.launch {
                    appPreferencesRepository.setBookmarkStyle(action.style)
                }
            }

            is SettingAction.OnEnableBackgroundMusicChange -> {
                viewModelScope.launch {
                    appPreferencesRepository.setEnableBackgroundMusic(action.enable)
                }
            }

            is SettingAction.OnPlayerVolumeChange -> {
                viewModelScope.launch {
                    appPreferencesRepository.setPlayerVolume(action.volume)
                }
            }

            is SettingAction.UpdateAutoResumeScrollMode -> {
                viewModelScope.launch {
                    appPreferencesRepository.setAutoScrollResumeMode(action.autoResume)
                }
            }

            is SettingAction.UpdateDelayAtEnd -> {
                viewModelScope.launch {
                    appPreferencesRepository.setDelayTimeAtEnd(action.delay)
                }
            }

            is SettingAction.UpdateDelayAtStart -> {
                viewModelScope.launch {
                    appPreferencesRepository.setDelayTimeAtStart(action.delay)
                }
            }

            is SettingAction.UpdateDelayResumeMode -> {
                viewModelScope.launch {
                    appPreferencesRepository.setAutoScrollResumeDelayTime(action.delay)
                }
            }

            is SettingAction.UpdateScrollSpeed -> {
                viewModelScope.launch {
                    appPreferencesRepository.setAutoScrollSpeed(action.speed)
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
                    bookRepository.insertCategory(action.category)
                }
            }

            is SettingAction.DeleteCategory -> {
                viewModelScope.launch {
                    bookRepository.deleteCategory(
                        _state.value.bookCategories.filter {
                            it.isSelected
                        }
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
                    appPreferencesRepository.setUnlockSpecialCodeStatus(true)
                }
            }

            is SettingAction.UpdateEnableSpecialArt -> {
                viewModelScope.launch {
                    appPreferencesRepository.setEnableSpecialArt(action.enable)
                }
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
            appPreferencesRepository.setTTSVoice(selectedVoice.name)
            ttsManager.updateVoice(selectedVoice)
            _state.update {
                it.copy(currentVoice = selectedVoice)
            }
        }
    }

    init {
        viewModelScope.launch {
            appPreferencesRepository.getKeepScreenOn().collectLatest { keepScreenOn ->
                _state.update {
                    it.copy(keepScreenOn = keepScreenOn)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getTtsPitch().collectLatest { ttsPitch ->
                _state.update {
                    it.copy(currentPitch = ttsPitch)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getTtsSpeed().collectLatest { ttsSpeed ->
                _state.update {
                    it.copy(currentSpeed = ttsSpeed)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getBookmarkStyle().collectLatest { bookmarkStyle ->
                _state.update {
                    it.copy(selectedBookmarkStyle = bookmarkStyle)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getEnableBackgroundMusic().collectLatest { enableBackgroundMusic ->
                _state.update {
                    it.copy(enableBackgroundMusic = enableBackgroundMusic)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getAutoScrollSpeed().collectLatest { speed ->
                _state.update {
                    it.copy(currentScrollSpeed = speed)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getAutoScrollResumeMode().collectLatest { autoScrollResumeMode ->
                _state.update {
                    it.copy(isAutoResumeScrollMode = autoScrollResumeMode)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getAutoScrollResumeDelayTime().collectLatest { delay ->
                _state.update {
                    it.copy(delayResumeMode = delay)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getDelayTimeAtEnd().collectLatest { delay ->
                _state.update {
                    it.copy(delayAtEnd = delay)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getDelayTimeAtStart().collectLatest { delay ->
                _state.update {
                    it.copy(delayAtStart = delay)
                }
            }
        }
        viewModelScope.launch {
            bookRepository.getBookCategory().collectLatest { categories ->
                _state.update {
                    it.copy(bookCategories = categories)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getUnlockSpecialCodeStatus().collectLatest { status->
                _state.update {
                    it.copy(unlockSpecialCodeStatus = status)
                }
            }
        }
        viewModelScope.launch {
            appPreferencesRepository.getEnableSpecialArt().collectLatest {enable->
                _state.update {
                    it.copy(enableSpecialArt = enable)
                }
            }
        }
        viewModelScope.launch {
            val selectedLocale = ttsManager.getTTS()?.availableLanguages?.find {
                it.displayName == appPreferencesRepository.getTtsLocale().first()
            }
            ttsManager.updateLanguage(selectedLocale ?: Locale.getDefault())
            val selectedVoice = ttsManager.getTTS()?.voices?.find {
                it.name == appPreferencesRepository.getTtsVoice().first() && it.locale == selectedLocale
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