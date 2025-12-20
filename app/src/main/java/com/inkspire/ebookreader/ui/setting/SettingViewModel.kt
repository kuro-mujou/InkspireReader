package com.inkspire.ebookreader.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.SettingDatastoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingDatastoreUseCase: SettingDatastoreUseCase,
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
                _state.update { it.copy(openSpecialCodeDialog = action.open) }
            }

            is SettingAction.KeepScreenOn -> {
                viewModelScope.launch {
                    settingDatastoreUseCase.setKeepScreenOn(action.keepScreenOn)
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
        }
    }

    init {
        viewModelScope.launch {
            settingDatastoreUseCase.getKeepScreenOn().collectLatest { keepScreenOn ->
                _state.update { it.copy(keepScreenOn = keepScreenOn) }
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
    }
}