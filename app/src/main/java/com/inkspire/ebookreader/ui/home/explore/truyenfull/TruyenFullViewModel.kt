package com.inkspire.ebookreader.ui.home.explore.truyenfull

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

class TruyenFullViewModel : ViewModel() {
    private val _state = MutableStateFlow(TruyenFullState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private val baseUrl = "https://truyenfull.vision"

    fun onAction(action: TruyenFullAction) {
        when (action) {
            is TruyenFullAction.PerformSearchQuery -> {
                if (action.query.isBlank()) return
                searchByQuery(action.query, 1)
            }
            is TruyenFullAction.PerformSearchCategory -> {
                searchByCategory(action.category, 1)
            }
            is TruyenFullAction.NextPage -> {
                val s = _state.value
                if (s.currentPage < s.maxPage) {
                    loadPage(s.currentPage + 1)
                }
            }
            is TruyenFullAction.PreviousPage -> {
                val s = _state.value
                if (s.currentPage > 1) {
                    loadPage(s.currentPage - 1)
                }
            }
            is TruyenFullAction.ClearResult -> {
                _state.update { TruyenFullState() }
            }
        }
    }

    private fun loadPage(page: Int) {
        val s = _state.value
        if (s.currentQuery != null) {
            searchByQuery(s.currentQuery, page)
        } else if (s.currentCategory != null) {
            searchByCategory(s.currentCategory, page)
        }
    }

    private fun searchByQuery(query: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(searchResult = UiState.Loading) }
            try {
                val result = TruyenFullScraper.search(baseUrl, query, page)

                if (result.data.isEmpty()) {
                    _state.update { it.copy(searchResult = UiState.Empty) }
                } else {
                    _state.update {
                        it.copy(
                            searchResult = UiState.Success(result.data),
                            currentPage = page,
                            maxPage = result.totalPages,
                            currentQuery = query,
                            currentCategory = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(searchResult = UiState.Error(Throwable("Failed: ${e.message}"))) }
            }
        }
    }

    private fun searchByCategory(categorySlug: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(searchResult = UiState.Loading) }
            try {
                val result = TruyenFullScraper.fetchCategoryBooks(baseUrl, categorySlug, page)

                if (result.data.isEmpty()) {
                    _state.update { it.copy(searchResult = UiState.Empty) }
                } else {
                    _state.update {
                        it.copy(
                            searchResult = UiState.Success(result.data),
                            currentPage = page,
                            maxPage = result.totalPages,
                            currentCategory = categorySlug,
                            currentQuery = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(searchResult = UiState.Error(Throwable("Failed: ${e.message}"))) }
            }
        }
    }
}