package com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.TableOfContentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TableOfContentViewModel(
    private val tableOfContentUseCase: TableOfContentUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TableOfContentState())
    val state = _state
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _state.value
        )

    fun onAction(action: TableOfContentAction) {
        when (action) {
            is TableOfContentAction.ChangeFabVisibility -> {
                _state.update { it.copy(fabVisibility = action.visibility) }
            }
            is TableOfContentAction.UpdateFirstVisibleTocIndex -> {
                _state.update { it.copy(firstVisibleTocIndex = action.index) }
            }
            is TableOfContentAction.UpdateLastVisibleTocIndex -> {
                _state.update { it.copy(lastVisibleTocIndex = action.index) }
            }
            is TableOfContentAction.UpdateTargetSearchIndex -> {
                _state.update { it.copy(targetSearchIndex = action.index) }
            }
            is TableOfContentAction.UpdateSearchState -> {
                _state.update { it.copy(searchState = action.searchState) }
            }
            is TableOfContentAction.UpdateCurrentChapterFavoriteState -> {
                viewModelScope.launch {
                    tableOfContentUseCase.updateChapterFavoriteState(
                        bookId = action.bookId,
                        chapterIndex = action.chapterIndex,
                        isFavorite = action.isFavorite
                    )
                }
            }
            else -> {}
        }
    }
}