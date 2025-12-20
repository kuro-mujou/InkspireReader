package com.inkspire.ebookreader.ui.bookcontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.common.UiState.Error
import com.inkspire.ebookreader.common.UiState.Success
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.usecase.BookContentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookContentViewModel(
    private val bookId: String,
    private val bookContentUseCase: BookContentUseCase
): ViewModel() {
    private val _bookContentState = MutableStateFlow(BookContentState())
    val bookContentState: StateFlow<BookContentState> = _bookContentState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _bookContentState.value
    )
    private val _contentState = MutableStateFlow(ContentState())
    val contentState: StateFlow<ContentState> = _contentState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _contentState.value
    )

    init {
        bookContentUseCase.getBookAsFlow(bookId)
            .map { book ->
                if (book == null) {
                    _bookContentState.update { it.copy(bookState = UiState.Empty) }
                } else {
                    _bookContentState.update { it.copy(bookState = UiState.Success(book)) }
                }
            }
            .onStart {
                _bookContentState.update { it.copy(bookState = UiState.Loading) }
            }
            .catch { exception ->
                _bookContentState.update { it.copy(bookState = UiState.Error(exception)) }
            }
            .distinctUntilChanged()
            .launchIn(viewModelScope)
    }

    fun onAction(action: BookContentAction) {
        when(action) {
            is BookContentAction.LoadChapter -> {
                if (_bookContentState.value.chapterStates[action.index] is UiState.Success) return
                bookContentUseCase.getChapterContentFlow(bookId, action.index)
                    .map { chapter ->
                        if (chapter == null) UiState.Empty else Success(chapter)
                    }
                    .onStart { updateChapterState(action.index, UiState.Loading) }
                    .catch { updateChapterState(action.index, Error(it)) }
                    .onEach { state -> updateChapterState(action.index, state) }
                    .launchIn(viewModelScope)
            }
            is BookContentAction.UpdateRecentChapterToDB -> {
                viewModelScope.launch {
                    bookContentUseCase.saveBookInfoChapterIndex(bookId, action.chapterIndex)
                }
            }
            is BookContentAction.UpdateRecentParagraphToDB -> {
                viewModelScope.launch {
                    bookContentUseCase.saveBookInfoParagraphIndex(bookId, action.paragraphIndex)
                }
            }

            is BookContentAction.UpdateCurrentChapter -> {
                _contentState.update { it.copy(currentChapterIndex = action.index) }
            }
            is BookContentAction.UpdateFirstVisibleItemIndex -> {
                _contentState.update { it.copy(firstVisibleItemIndex = action.index) }
            }
            is BookContentAction.UpdateLastVisibleItemIndex -> {
                _contentState.update { it.copy(lastVisibleItemIndex = action.index) }
            }
        }
    }

    private fun updateChapterState(index: Int, state: UiState<Chapter>) {
        _bookContentState.update { currentState ->
            currentState.copy(chapterStates = currentState.chapterStates + (index to state))
        }
    }
}