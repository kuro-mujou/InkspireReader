package com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BottomBarAutoScrollViewModel: ViewModel() {
    private val _state = MutableStateFlow(BottomBarAutoScrollState())
    val state: StateFlow<BottomBarAutoScrollState>
        get() = _state
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = _state.value
            )

    fun onAction(action: BottomBarAutoScrollAction) {
        when (action) {
            BottomBarAutoScrollAction.ChangeSettingVisibility -> {
                _state.update { it.copy(settingVisibility = !it.settingVisibility) }
            }
            else -> {}
        }
    }
}