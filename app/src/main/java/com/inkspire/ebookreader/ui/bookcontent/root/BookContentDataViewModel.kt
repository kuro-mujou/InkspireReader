package com.inkspire.ebookreader.ui.bookcontent.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.usecase.BookContentUseCase
import com.inkspire.ebookreader.util.HighlightUtil
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

class BookContentDataViewModel(
    private val bookId: String,
    private val bookContentUseCase: BookContentUseCase
): ViewModel() {
    private val _state = MutableStateFlow(BookContentDataState())
    val state: StateFlow<BookContentDataState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _state.value
    )

    init {
        bookContentUseCase.getBookAsFlow(bookId)
            .map { book ->
                if (book == null) {
                    _state.update { it.copy(bookState = UiState.Empty) }
                } else {
                    _state.update { it.copy(bookState = UiState.Success { book }) }
                }
            }
            .onStart {
                _state.update { it.copy(bookState = UiState.Loading) }
            }
            .catch { exception ->
                _state.update { it.copy(bookState = UiState.Error(exception)) }
            }
            .distinctUntilChanged()
            .launchIn(viewModelScope)

        bookContentUseCase.getTableOfContentAsFlow(bookId)
            .map { tableOfContents ->
                if (tableOfContents.isEmpty()) {
                    _state.update { it.copy(tableOfContentState = UiState.Empty) }
                } else {
                    _state.update { it.copy(tableOfContentState = UiState.Success { tableOfContents }) }
                }
            }
            .onStart {
                _state.update { it.copy(tableOfContentState = UiState.Loading) }
            }
            .catch { exception ->
                _state.update { it.copy(tableOfContentState = UiState.Error(exception)) }
            }
            .distinctUntilChanged()
            .launchIn(viewModelScope)
    }

    fun onAction(action: BookContentDataAction) {
        when(action) {
            is BookContentDataAction.LoadChapter -> {
                if (_state.value.chapterStates[action.index] is UiState.Success) return
                bookContentUseCase.getChapterContentFlow(bookId, action.index)
                    .map { chapter ->
                        if (chapter == null) UiState.Empty else UiState.Success { chapter }
                    }
                    .onStart { updateChapterState(action.index, UiState.Loading) }
                    .catch { updateChapterState(action.index, UiState.Error(it)) }
                    .onEach { state -> updateChapterState(action.index, state) }
                    .launchIn(viewModelScope)
            }
            is BookContentDataAction.UpdateRecentChapterToDB -> {
                viewModelScope.launch {
                    bookContentUseCase.saveBookInfoChapterIndex(bookId, action.chapterIndex)
                }
            }
            is BookContentDataAction.UpdateRecentParagraphToDB -> {
                viewModelScope.launch {
                    bookContentUseCase.saveBookInfoParagraphIndex(bookId, action.paragraphIndex)
                }
            }
            is BookContentDataAction.AddHighlightForParagraph -> {
                viewModelScope.launch {
                    val newHighlight = action.highlightInfo

                    val current = bookContentUseCase.getHighlightsForParagraph(
                        bookId, newHighlight.tocId, newHighlight.paragraphIndex
                    )

                    val resultList = HighlightUtil.addHighlight(newHighlight, current)

                    bookContentUseCase.replaceHighlightsForParagraph(
                        bookId, newHighlight.tocId, newHighlight.paragraphIndex, resultList
                    )
                }
            }
            is BookContentDataAction.DeleteHighlightRange -> {

                viewModelScope.launch {
                    val current = bookContentUseCase.getHighlightsForParagraph(
                        bookId, action.tocId, action.paragraphIndex
                    )

                    val resultList = HighlightUtil.removeRange(
                        action.start, action.end, current
                    )

                    bookContentUseCase.replaceHighlightsForParagraph(
                        bookId, action.tocId, action.paragraphIndex, resultList
                    )
                }
            }
        }
    }

    private fun updateChapterState(index: Int, state: UiState<Chapter>) {
        _state.update { currentState ->
            currentState.copy(chapterStates = currentState.chapterStates + (index to state))
        }
    }
}