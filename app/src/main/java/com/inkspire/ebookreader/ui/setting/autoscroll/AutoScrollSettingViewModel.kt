package com.inkspire.ebookreader.ui.setting.autoscroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.AutoScrollSettingDatastoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AutoScrollSettingViewModel(
    private val datastoreUseCase: AutoScrollSettingDatastoreUseCase
): ViewModel() {
    private val _state = MutableStateFlow(AutoScrollSettingState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    init {
        viewModelScope.launch {
            datastoreUseCase.getAutoScrollSpeed().collectLatest { data ->
                _state.update { it.copy(currentScrollSpeed = data) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getDelayTimeAtStart().collectLatest { data ->
                _state.update { it.copy(delayAtStart = data) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getDelayTimeAtEnd().collectLatest { data ->
                _state.update { it.copy(delayAtEnd = data) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getAutoScrollResumeDelayTime().collectLatest { data ->
                _state.update { it.copy(delayResumeMode = data) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getAutoScrollResumeMode().collectLatest { data ->
                _state.update { it.copy(isAutoResumeScrollMode = data) }
            }
        }
    }

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