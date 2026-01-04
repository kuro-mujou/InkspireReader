package com.inkspire.ebookreader.ui.bookcontent.autoscroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.AutoScrollDatastoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AutoScrollViewModel(
    private val datastoreUseCase: AutoScrollDatastoreUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AutoScrollState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AutoScrollState()
        )

    init {
        viewModelScope.launch {
            datastoreUseCase.autoScrollPreferences.collectLatest { prefs ->
                _state.update { it.copy(autoScrollPreferences = prefs) }
            }
        }
    }

    fun onAction(action: AutoScrollAction) {
        when(action) {
            is AutoScrollAction.UpdateIsActivated -> {
                _state.update { it.copy(isActivated = action.isActivated) }
            }
            is AutoScrollAction.UpdateIsAnimationRunning -> {
                _state.update { it.copy(isAnimationRunning = action.isAnimationRunning) }
            }
            is AutoScrollAction.UpdateIsPaused -> {
                _state.update { it.copy(isPaused = action.isPaused) }
            }
            is AutoScrollAction.UpdateIsScrolledToEnd -> {
                _state.update { it.copy(isScrolledToEnd = action.isScrolledToEnd) }
            }
        }
    }
}