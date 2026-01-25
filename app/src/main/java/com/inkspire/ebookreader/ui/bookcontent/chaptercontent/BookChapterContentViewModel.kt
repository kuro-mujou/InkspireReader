package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookChapterContentViewModel: ViewModel() {
    private val _state = MutableStateFlow(BookChapterContentState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private val _event = Channel<BookChapterContentEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun onAction(action: BookChapterContentAction){
        when(action) {
            is BookChapterContentAction.InitFromDatabase -> {
                if (_state.value.currentChapterIndex == -1) {
                    _state.update { it.copy(
                        currentChapterIndex = action.chapter,
                        firstVisibleItemIndex = action.paragraph
                    ) }
                }
            }
            is BookChapterContentAction.UpdateCurrentChapterIndex -> {
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
            is BookChapterContentAction.UpdateEnableUndoButton -> {
                _state.update { it.copy(enableUndoButton = action.enable) }
            }
            is BookChapterContentAction.UpdateEnablePagerScroll -> {
                _state.update { it.copy(enablePagerScroll = action.enable) }
            }
            is BookChapterContentAction.RequestScrollToChapter -> {
                viewModelScope.launch {
                    _event.send(BookChapterContentEvent.ScrollToChapter(action.index))
                    _state.update { it.copy(firstVisibleItemIndex = 0) }
                }
            }
            is BookChapterContentAction.RequestScrollToParagraph -> {
                viewModelScope.launch {
                    _event.send(BookChapterContentEvent.ScrollToParagraph(action.chapterIndex, action.paragraphIndex))
                }
            }
            is BookChapterContentAction.RequestAnimatedScrollToChapter -> {
                viewModelScope.launch {
                    _event.send(BookChapterContentEvent.AnimatedScrollToChapter(action.index))
                    _state.update { it.copy(firstVisibleItemIndex = 0) }
                }
            }
            is BookChapterContentAction.UpdateGlobalMagnifierCenter -> {
                _state.update { it.copy(globalMagnifierCenter = action.offset) }
            }
            is BookChapterContentAction.SetActiveSelectionIndex -> {
                _state.update { it.copy(activeSelectionIndex = action.index) }
            }
        }
    }
}