package com.inkspire.ebookreader.ui.bookcontent.topbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BookContentTopBarViewModel() : ViewModel() {
    private val _state = MutableStateFlow(BookContentTopBarState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: BookContentTopBarAction) {
        when (action) {
            is BookContentTopBarAction.ChangeTopBarVisibility -> {
                _state.update { it.copy(topBarVisibility = !it.topBarVisibility) }
            }
            is BookContentTopBarAction.ShowFindAndReplace -> {
                _state.update { it.copy(showFindAndReplace = action.show) }
            }
            is BookContentTopBarAction.ShowHighlightList -> {
                _state.update { it.copy(showHighlightList = action.show) }
            }
            is BookContentTopBarAction.ShowSearchResultsSheet -> {
                _state.update { it.copy(showSearchResultsSheet = action.show) }
            }
        }
    }
}