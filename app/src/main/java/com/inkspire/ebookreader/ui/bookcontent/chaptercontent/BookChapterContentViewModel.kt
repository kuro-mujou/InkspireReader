package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BookChapterContentViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(BookChapterContentState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: BookChapterContentAction){
        when(action) {
            is BookChapterContentAction.UpdateCurrentChapter -> {
                _state.update { it.copy(currentChapterIndex = action.index) }
            }
            is BookChapterContentAction.UpdateFirstVisibleItemIndex -> {
                _state.update { it.copy(firstVisibleItemIndex = action.index) }
            }
            is BookChapterContentAction.UpdateLastVisibleItemIndex -> {
                _state.update { it.copy(lastVisibleItemIndex = action.index) }
            }
            is BookChapterContentAction.UpdateScreenHeight -> {
                _state.update { it.copy(screenHeight = action.screenHeight) }
            }
            is BookChapterContentAction.UpdateScreenWidth -> {
                _state.update { it.copy(screenWidth = action.screenWidth) }
            }
            is BookChapterContentAction.UpdateEnableUndoButton -> {
                _state.update { it.copy(enableUndoButton = action.enable) }
            }
            is BookChapterContentAction.UpdateEnablePagerScroll -> {
                _state.update { it.copy(enablePagerScroll = action.enable) }
            }
            else -> {}
        }
    }
}