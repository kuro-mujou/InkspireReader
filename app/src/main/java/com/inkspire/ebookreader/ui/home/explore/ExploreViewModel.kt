package com.inkspire.ebookreader.ui.home.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.ui.home.explore.common.supportedWebsites
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ExploreViewModel : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    init {
        _state.update { it.copy(selectedWebsite = supportedWebsites.first()) }
    }

    fun onAction(action: ExploreAction) {
        when (action) {
            is ExploreAction.ChangeSelectedWebsite -> {
                _state.update { it.copy(selectedWebsite = action.selectedWebsite) }
            }
        }
    }
}