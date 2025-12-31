package com.inkspire.ebookreader.ui.home.explore.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.TruyenFullScraper
import com.inkspire.ebookreader.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    bookUrl: String
) : ViewModel() {
    private val _state = MutableStateFlow(DetailState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        searchBookDetail(bookUrl)
    }

    private fun searchBookDetail(bookUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(searchResultDetail = UiState.Loading) }
            try {
                val result = TruyenFullScraper.fetchBookDetails(bookUrl)
                _state.update { it.copy(searchResultDetail = UiState.Success(result)) }
            } catch (e: Exception) {
                _state.update { it.copy(searchResultDetail = UiState.Error(Throwable("Failed: ${e.message}"))) }
            }
        }
    }
}