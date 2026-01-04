package com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.BookmarkSettingDatastoreUseCase
import com.inkspire.ebookreader.domain.usecase.TableOfContentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val tableOfContentUseCase: TableOfContentUseCase,
    private val datastoreUseCase: BookmarkSettingDatastoreUseCase
): ViewModel(){
    private val _state = MutableStateFlow(BookmarkListState())
    val state: StateFlow<BookmarkListState> = _state
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

    fun onAction(action: BookmarkListAction) {
        when(action) {
            is BookmarkListAction.DeleteBookmark -> {
                viewModelScope.launch {
                    tableOfContentUseCase.updateChapterFavoriteState(
                        bookId = action.bookId,
                        chapterIndex = action.index,
                        isFavorite = false
                    )
                }
                _state.update {
                    it.copy(
                        enableUndoDeleteBookmark = true,
                        undoBookmarkList = _state.value.undoBookmarkList + action.index
                    )
                }

            }
            is BookmarkListAction.UndoDeleteBookmark -> {
                viewModelScope.launch {
                    _state.value.undoBookmarkList.forEach {
                        tableOfContentUseCase.updateChapterFavoriteState(
                            bookId = action.bookId,
                            chapterIndex = it,
                            isFavorite = true
                        )
                    }
                    _state.update {
                        it.copy(
                            enableUndoDeleteBookmark = false,
                            undoBookmarkList = emptyList()
                        )
                    }
                }
            }
            BookmarkListAction.UpdateBookmarkThemeSettingVisibility -> {
                _state.update { it.copy(bookmarkThemeSettingVisibility = !_state.value.bookmarkThemeSettingVisibility) }
            }
        }
    }
}