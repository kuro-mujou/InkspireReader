package com.inkspire.ebookreader.ui.setting.autoscroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.AutoScrollSettingDatastoreUseCase
import kotlinx.coroutines.launch

class AutoScrollSettingViewModel(
    private val datastoreUseCase: AutoScrollSettingDatastoreUseCase
): ViewModel() {
    fun onAction(action: AutoScrollSettingAction) {
        when (action) {
            is AutoScrollSettingAction.UpdateAutoResumeScrollMode -> {
                viewModelScope.launch {
                    datastoreUseCase.setAutoScrollResumeMode(action.autoResume)
                }
            }
            is AutoScrollSettingAction.UpdateDelayAtEnd -> {
                viewModelScope.launch {
                    datastoreUseCase.setDelayTimeAtEnd(action.delay)
                }
            }
            is AutoScrollSettingAction.UpdateDelayAtStart -> {
                viewModelScope.launch {
                    datastoreUseCase.setDelayTimeAtStart(action.delay)
                }
            }
            is AutoScrollSettingAction.UpdateDelayResumeMode -> {
                viewModelScope.launch {
                    datastoreUseCase.setAutoScrollResumeDelayTime(action.delay)
                }
            }
            is AutoScrollSettingAction.UpdateScrollSpeed -> {
                viewModelScope.launch {
                    datastoreUseCase.setAutoScrollSpeed(action.speed)
                }
            }
        }
    }
}