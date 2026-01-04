package com.inkspire.ebookreader.ui.setting.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.BookmarkSettingDatastoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarkSettingViewModel(
    private val datastoreUseCase: BookmarkSettingDatastoreUseCase
): ViewModel() {
    private val _state = MutableStateFlow(BookmarkSettingState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    init {
        viewModelScope.launch {
            datastoreUseCase.readerSettings.collectLatest { settings ->
                _state.update { it.copy(readerSettings = settings) }
            }
        }
    }

    fun onAction(action: BookmarkSettingAction) {
        when (action) {
            is BookmarkSettingAction.UpdateSelectedBookmarkStyle -> {
                viewModelScope.launch {
                    datastoreUseCase.setBookmarkStyle(action.bookmarkStyle)
                }
            }
        }
    }
}