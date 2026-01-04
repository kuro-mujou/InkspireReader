package com.inkspire.ebookreader.ui.home.recentbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.usecase.RecentBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecentBookViewModel(
    private val recentBookUseCase: RecentBookUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RecentBookState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        viewModelScope.launch {
            recentBookUseCase.getRecentBookList()
                .map { books ->
                    if (books.isEmpty()) {
                        _state.update { it.copy(recentBookState = UiState.Empty) }
                    } else {
                        _state.update { it.copy(recentBookState = UiState.Success { books }) }
                    }
                }
                .onStart {
                    _state.update { it.copy(recentBookState = UiState.Loading) }
                }
                .catch { exception ->
                    _state.update { it.copy(recentBookState = UiState.Error(exception)) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun updateRecentRead(bookId: String) {
        viewModelScope.launch {
            recentBookUseCase.updateRecentRead(bookId)
        }
    }
}