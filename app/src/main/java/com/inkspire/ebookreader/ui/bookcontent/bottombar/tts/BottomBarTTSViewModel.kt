package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BottomBarTTSViewModel(

) : ViewModel() {
    private val _state = MutableStateFlow(BottomBarTTSState())
    val state: StateFlow<BottomBarTTSState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: BottomBarTTSAction) {
        when (action) {
            is BottomBarTTSAction.UpdateMusicMenuVisibility -> {
                _state.update { it.copy(ttsMusicMenuVisibility = !_state.value.ttsMusicMenuVisibility) }
            }

            is BottomBarTTSAction.UpdateVoiceMenuVisibility -> {
                _state.update { it.copy(ttsVoiceMenuVisibility = !_state.value.ttsVoiceMenuVisibility) }
            }

            else -> {}
        }
    }
}