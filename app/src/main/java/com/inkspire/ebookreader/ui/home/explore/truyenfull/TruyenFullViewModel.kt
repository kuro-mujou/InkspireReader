package com.inkspire.ebookreader.ui.home.explore.truyenfull

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TruyenFullViewModel : ViewModel() {
    private val _state = MutableStateFlow(TruyenFullState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )
    fun onAction(action: TruyenFullAction) {
        when (action) {
            is TruyenFullAction.PerformSearchQuery -> {

            }
            is TruyenFullAction.PerformSearchCategory -> {

            }
        }
    }
}